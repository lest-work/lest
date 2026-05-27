# LEST Platform AI 开发者低漂移高精度实现规范 (AI_DEVELOPER_IMPLEMENTATION_SPEC.md)

## 属性信息

| 属性 | 内容 |
| :--- | :--- |
| **制订人** | LEST Platform 联合架构委员会（资深产品总监 + 资深系统架构师 + 资深研发专家） |
| **版本** | V1.0.0 (生产级基线) |
| **目的** | 消除 AI 开发者与人工开发者在复杂微服务系统中的「认知漂移」，建立绝对具象、开箱即用的落地蓝图 |

---

## 1. 概述与设计理念

LEST Platform 在架构上是一个 **AI-Native 协同管理平台**。然而，当多模态 AI (如 Cascade 等) 接入项目进行自动化功能迭代时，由于传统 PRD 和高层架构文档 (High-Level Architecture) 存在语义模糊和实现留白，AI 往往会采用「打补丁」、「写死逻辑」或「破坏分层」的初级编码方式，导致系统迅速积累技术债务。

本规范以**产品总监的业务严密性**、**系统架构师的边界控制力**以及**资深开发的落地细节感**，对整个系统进行全方位的细节细化。所有参与本项目的 AI 实体必须无条件遵循此规范。

---

## 2. 👨‍💼 资深产品总监：核心协同业务流的绝对具象化

高层 PRD 中「任务流转」、「通知推送」往往只有文字性描述。为了消除开发漂移，以下定义核心业务细节。

### 2.1 任务生命周期流转状态机与强校验阻断机制

任务（Task）的生命周期状态采用枚举 `TaskStatus` 表达：`PLANNING` (计划中)、`TODO` (待办)、`IN_PROGRESS` (开发中)、`CODE_REVIEW` (代码审查)、`TESTING` (测试中)、`COMPLETED` (已完成)、`REJECTED` (已拒绝)。

AI 开发必须在 `TaskServiceImpl.updateStatus` 方法中，执行以下强校验，阻断非法的状态变更：

```
                ┌──────────────┐
                │   PLANNING   │
                └──────┬───────┘
                       │ 激活
                       ▼
                 ┌──────────┐ 拒绝
                 │   TODO   ├───────────────┐
                 └─────┬────┘               │
                       │ 开始               │
                       ▼                    │
                ┌──────────────┐            │
                │ IN_PROGRESS  │            │
                └──────┬───────┘            │
                       │ 提交 MR            │
                       ▼                    ▼
                ┌──────────────┐      ┌──────────┐
                │ CODE_REVIEW  │      │ REJECTED │
                └──────┬───────┘      └──────────┘
                       │ MR 合并            ▲
                       ▼                    │
                 ┌──────────┐               │
                 │ TESTING  ├───────────────┘
                 └─────┬────┘ 拒绝
                       │ 验证通过
                       ▼
                ┌──────────────┐
                │  COMPLETED   │
                └──────────────┘
```

#### 状态扭转的前置契约与阻断规则表

| 源状态 | 目标状态 | 前置准入校验规则（不满足直接抛 `BizException(400, "message")`） | 触发的后置级联动作（通过 Kafka 异步事件或 Feign 触发） |
| :--- | :--- | :--- | :--- |
| `PLANNING` | `TODO` | 1. 任务必须关联有效的项目 (`projectId` 存在且非软删除)<br>2. 预估工时 (`estimated_hours`) 必须大于 0。 | 无。 |
| `TODO` | `IN_PROGRESS` | 1. 必须指派具体的负责人 (`assignee_id` 不能为空)。 | 1. 记录活动日志：`User A started task`。<br>2. 发送 Kafka 异步事件 `lest-task.taskStarted`。 |
| `IN_PROGRESS` | `CODE_REVIEW` | 1. **核心关联校验**：任务必须在 `task_commit` 或 `task_merge_request` 中存在至少一条活跃的提交/MR 关联记录（除非任务类型为 `BUG` 或 `DOCUMENT`）。 | 1. 触发 AI 服务接口 `/api/ai/v1/code-review`，异步执行自动代码审查，并将初审意见作为评论写入任务详情。 |
| `CODE_REVIEW` | `TESTING` | 1. 关联的 Merge Request 必须在 Git 服务中标记为 `MERGED`。<br>2. AI 审查的严重缺陷（Blocker 级）必须全部被标记为已解决。 | 1. 自动指派任务负责人为该项目的默认测试人员（`tester_id`）。 |
| `TESTING` | `COMPLETED` | 1. 当前操作人必须为项目测试人员或管理员（非开发负责人本人，防自测自通）。<br>2. **工时完整性校验**：实际登记工时 (`actual_hours`) 必须大于 0，或者 WakaTime 统计的该任务关联编码时长大于 10 分钟。 | 1. 触发 AI 工时预估偏差分析 API。<br>2. 发送 Kafka 事件 `lest-task.taskCompleted` 驱动团队绩效更新。 |

---

### 2.2 统一通知引擎事件与多渠道推送矩阵

平台具有统一的通知模块 `lest-notification`。任何业务子系统触发通知，严禁在自身模块直接调用邮件、WebSocket 或短信服务，必须通过 Kafka 投递统一的事件格式。

#### 高频通知事件路由规范

```json
// Kafka 统一通知事件 Payload 示例 (Topic: lest-prod-notification-dispatcher)
{
  "eventId": "evt_9a8b7c6d5e4f3a2b",
  "eventType": "TASK_ASSIGNED",
  "tenantId": "tenant_sh_01",
  "timestamp": 1779836400000,
  "senderId": 1001,
  "receiverIds": [2005],
  "templateCode": "tpl_task_assigned_default",
  "channels": ["EMAIL", "WEB_SOCKET"],
  "variables": {
    "projectName": "LEST Platform V1.0",
    "taskTitle": "设计微服务事件总线基座",
    "assignerName": "产品总监张三",
    "priority": "URGENT",
    "deadline": "2026-06-01"
  }
}
```

#### 推送通道容灾与降级策略 (通知服务内部实现)

```
                            ┌────────────────────────┐
                            │   收到通知事件 Payload   │
                            └───────────┬────────────┘
                                        │
                         ┌──────────────┴──────────────┐
                         ▼                             ▼
               ┌──────────────────┐          ┌──────────────────┐
               │    EMAIL 通道     │          │  WEB_SOCKET 通道  │
               └─────────┬────────┘          └─────────┬────────┘
                         │                             │
                  [发送失败/超时]                [用户不在线]
                         │                             │
                         ▼                             ▼
               ┌──────────────────┐          ┌──────────────────┐
               │  降级为站内信存储  │◀─────────┤ 自动转为离线消息  │
               │ (db:sys_message) │          │  (等待下次握手)   │
               └──────────────────┘          └──────────────────┘
```

---

## 3. 🏗️ 资深系统架构师：微服务边界与双部署模式抽象

微服务系统最怕「循环依赖」和「本地测试的高门槛」。

### 3.1 严格的单向依赖树与 Monorepo 模块隔离机制

在 `backend` 的 Monorepo 依赖管理中，所有微服务子模块 `lest-modules/*` 之间的 **Gradle 依赖必须是绝对孤立的**。

- **❌ 错误做法**：在 `lest-task/build.gradle` 中声明 `implementation project(':lest-modules:lest-auth')`。这会导致高度耦合，破坏微服务边界。
- **✅ 正确做法**：所有跨模块数据交互，同步必须通过 `lest-common-security` 包内的声明式 `OpenFeign` 客户端进行，异步必须通过 `LestEventPublisher` 进行。

---

### 3.2 本地单进程（Monolith）与微服务（Distributed）双部署无缝切换

为了让 AI 在开发时不依赖复杂的云端 K8s 环境，系统必须提供完美的「双模部署抽象」。

#### (1) 通信总线抽象

```java
// lest-common-security 模块中定义
package com.lest.common.core.event;

/**
 * 统一事件发布器，隔离底层传输载体
 */
public interface LestEventPublisher {
    /**
     * @param topic 目标主题
     * @param eventPayload 业务负载对象（将被序列化为 JSON）
     */
    void publish(String topic, Object eventPayload);
}
```

```java
// 分布式环境实现（基于 Kafka）
@Component
@ConditionalOnProperty(name = "lest.deployment.mode", havingValue = "distributed")
@RequiredArgsConstructor
@Slf4j
public class KafkaEventPublisher implements LestEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publish(String topic, Object eventPayload) {
        log.info("[Distributed Mode] 推送 Kafka 事件到 Topic: {}", topic);
        kafkaTemplate.send(topic, eventPayload);
    }
}
```

```java
// 本地单进程环境实现（基于 Spring Event，零中间件依赖）
@Component
@ConditionalOnProperty(name = "lest.deployment.mode", havingValue = "monolith", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class SpringLocalEventPublisher implements LestEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(String topic, Object eventPayload) {
        log.info("[Monolith Mode] 分发本地 Spring ApplicationEvent. Topic: {}", topic);
        applicationEventPublisher.publishEvent(new LocalEventEnvelope(topic, eventPayload));
    }
}
```

#### (2) Feign 客户端动态替换

在本地单进程（Monolith）启动时，为了避免不必要的 HTTP 调用开销和网关转发，需要将 Feign 客户端的代理 Bean 直接替换为本地的 Service 依赖。

```java
// 在 lest-task 模块中引用 lest-auth 服务的客户端声明
package com.lest.task.client;

import com.lest.common.core.domain.Result;
import com.lest.task.domain.vo.UserVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "lest-auth", path = "/api/auth/v1", contextId = "authClient")
public interface AuthClient {
    @GetMapping("/user/{id}")
    Result<UserVO> getUserById(@PathVariable("id") Long id);
}
```

```java
// 本地注入装配器配置类
package com.lest.task.config;

import com.lest.task.client.AuthClient;
import com.lest.auth.service.UserService; // 仅在本地 monolith 编译模式下通过 Gradle compileOnly 引入
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Configuration
public class MonolithClientAutoconfiguration {

    @Bean
    @ConditionalOnProperty(name = "lest.deployment.mode", havingValue = "monolith", matchIfMissing = true)
    public AuthClient monolithAuthClient(UserService userService) {
        // 使用 Lambda 直接适配 Service 接口到 FeignClient 契约，避免走 HTTP 协议栈
        return id -> {
            var userEntity = userService.getById(id);
            return Result.ok(new UserVO(userEntity.getId(), userEntity.getUsername(), userEntity.getRole()));
        };
    }
}
```

---

### 3.3 跨服务分布式数据聚合（严禁跨库 JOIN）

微服务架构下数据库物理隔离，`lest_task` 数据库中仅存储了任务信息和 `assignee_id`（负责人ID），但列表页面需要同时展示负责人的「用户名」和「头像」。

**AI 严禁采用以下低级手段**：在 `TaskMapper.xml` 中编写 `LEFT JOIN lest_auth.sys_user` 语句。这会在云端部署、跨容器数据库拆分时发生灾难性的连接失败！

#### 正确的 API 驱动内存聚合设计模式

```java
// 内存双向聚合方案 (InMemory Aggregation Pattern)
@Service
@RequiredArgsConstructor
public class TaskQueryServiceImpl implements TaskQueryService {
    private final TaskMapper taskMapper;
    private final AuthClient authClient; // Feign 客户端

    @Override
    public PageResult<TaskVO> queryTaskPage(TaskQueryDTO query) {
        // 1. 查询本模块 Task 数据 (物理隔离)
        Page<Task> page = taskMapper.selectPage(new Page<>(query.page(), query.size()), query.getWrapper());
        List<Task> records = page.getRecords();
        if (records.isEmpty()) {
            return PageResult.of(List.of(), page.getTotal(), page.getSize(), page.getCurrent());
        }

        // 2. 提取需要跨服务补充信息的负责人 IDs (去重，过滤空值)
        List<Long> assigneeIds = records.stream()
                .map(Task::getAssigneeId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        // 3. 批量同步调用 AuthClient 获取用户信息集合 (单次 RPC，严禁循环中调用 Feign！)
        Map<Long, UserVO> userMap = Map.of();
        if (!assigneeIds.isEmpty()) {
            Result<List<UserVO>> usersResult = authClient.getUsersByIds(assigneeIds);
            if (usersResult.isSuccess() && usersResult.getData() != null) {
                userMap = usersResult.getData().stream()
                        .collect(Collectors.toMap(UserVO::id, Function.identity()));
            }
        }

        // 4. 双指针高性能内存拼接 DTO -> VO
        final Map<Long, UserVO> finalUserMap = userMap;
        List<TaskVO> voList = records.stream().map(task -> {
            UserVO user = finalUserMap.get(task.getAssigneeId());
            return TaskVO.from(task, user); // 拼接头像和名称
        }).toList();

        return PageResult.of(voList, page.getTotal(), page.getSize(), page.getCurrent());
    }
}
```

---

## 5. � 扩展：特定核心微服务深度落地设计 (AI 服务与插件系统)

为了防止 AI 开发者在最复杂的 `lest-ai`（AI服务）与 `lest-plugin`（插件服务）中写出高耦合、阻塞或不安全的代码，必须遵循以下高精度模型设计。

### 5.1 AI 服务 (lest-ai) 异步长轮询与事件/回调解耦机制

#### 5.1.1 核心痛点与架构红线
大模型 (LLM) 接口调用（如代码审查、绩效分析）通常需要消耗 10 ~ 45 秒。
- **❌ 错误做法**：在业务子模块（如 `lest-code`）中通过 OpenFeign 同步请求 `lest-ai` 并等待返回。这会导致主线程大量阻塞、线程池迅速耗尽并引发 HTTP 504 熔断。
- **✅ 正确做法**：强制执行「异步任务单 (Ticket) 模型」。业务模块发出异步任务创建请求，`lest-ai` 立即返回 Ticket 凭证，大模型计算完成后，`lest-ai` 通过 **Kafka 广播事件** 或 **HTTP 回调** 将结果写回。

#### 5.1.2 异步任务状态机

```
                      提交任务
                         │
                         ▼
                  ┌──────────────┐
                  │  SUBMITTED   │ (已提交凭证，立即返回业务端)
                  └──────┬───────┘
                         │ 线程池调度
                         ▼
                  ┌──────────────┐
                  │  PROCESSING  │ (大模型分析中...)
                  └──────┬───────┘
                         │
               ┌─────────┴─────────┐
               ▼                   ▼
        ┌──────────────┐    ┌──────────────┐
        │   SUCCESS    │    │    FAILED    │
        └──────┬───────┘    └──────┬───────┘
               │                   │
               ▼                   ▼
         广播 Kafka 事件      指数级退避重试 (最大3次)
```

#### 5.1.3 代码级抽象实现

```java
// 1. AI 异步分析控制器定义
package com.lest.ai.controller;

import com.lest.common.core.domain.Result;
import com.lest.ai.domain.dto.CodeReviewRequestDTO;
import com.lest.ai.domain.vo.TaskTicketVO;
import com.lest.ai.service.AiTaskService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai/task")
@RequiredArgsConstructor
public class AiTaskController {
    private final AiTaskService aiTaskService;

    @PostMapping("/code-review")
    public Result<TaskTicketVO> submitCodeReview(@RequestBody CodeReviewRequestDTO dto) {
        // 立即创建 Ticket 并提交到线程池，返回 TicketId (SUBMITTED 状态)
        TaskTicketVO ticket = aiTaskService.submitCodeReviewTask(dto);
        return Result.ok(ticket);
    }
}
```

```java
// 2. AI 任务异步处理器
package com.lest.ai.service.impl;

import com.lest.ai.domain.dto.CodeReviewRequestDTO;
import com.lest.ai.domain.vo.TaskTicketVO;
import com.lest.common.core.event.LestEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiTaskServiceImpl implements AiTaskService {
    private final AiTaskRepository aiTaskRepository;
    private final LestEventPublisher eventPublisher;
    private final LlmClient llmClient; // 对接 OpenAI/Claude/Qwen

    @Override
    public TaskTicketVO submitCodeReviewTask(CodeReviewRequestDTO dto) {
        // 保存初始任务单
        AiTask task = AiTask.create(dto.getMrId(), "CODE_REVIEW");
        aiTaskRepository.save(task);
        
        // 触发异步大模型计算 (使用异步线程池)
        this.executeLlmAnalysisAsync(task.getId(), dto);
        
        return new TaskTicketVO(task.getId(), "SUBMITTED", "/api/ai/v1/task/status/" + task.getId());
    }

    @Async("aiTaskExecutor") // 专属大模型高延迟线程池
    public void executeLlmAnalysisAsync(Long taskId, CodeReviewRequestDTO dto) {
        AiTask task = aiTaskRepository.findById(taskId).orElseThrow();
        task.setStatus("PROCESSING");
        aiTaskRepository.save(task);

        try {
            // 调用 LLM 服务 (耗时 15s)
            String reviewResult = llmClient.call(dto.getPrompt(), dto.getCodeDiff());
            
            task.setStatus("SUCCESS");
            task.setResult(reviewResult);
            aiTaskRepository.save(task);

            // 通过异步 Kafka 通知业务端 (代码服务/任务服务消费此事件更新 MR 评论)
            eventPublisher.publish("lest-prod-ai-analysisCompleted", new AiAnalysisCompletedEvent(taskId, dto.getMrId(), "SUCCESS", reviewResult));
        } catch (Exception e) {
            task.setStatus("FAILED");
            task.setErrorMsg(e.getMessage());
            aiTaskRepository.save(task);
            
            eventPublisher.publish("lest-prod-ai-analysisCompleted", new AiAnalysisCompletedEvent(taskId, dto.getMrId(), "FAILED", null));
        }
    }
}
```

---

### 5.2 插件系统 (lest-plugin) 动态类加载与安全沙箱隔离

#### 5.2.1 核心痛点与隔离架构
LEST Platform 通过动态上传 `.lpkg` (本质是 jar 包) 扩展系统。
- **安全红线**：插件不能污染主系统的 ClassPath，插件相互之间不能出现同包同名类冲突。插件的代码不能任意读取主机物理文件或执行 `System.exit()`。

#### 5.2.2 双亲委派破坏与 PluginClassLoader

```java
package com.lest.plugin.loader;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * 专属插件类加载器，破坏双亲委派，实现同名依赖物理隔离
 */
public class PluginClassLoader extends URLClassLoader {
    private final ClassLoader systemClassLoader;

    public PluginClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
        this.systemClassLoader = ClassLoader.getSystemClassLoader();
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            // 1. 检查类是否已经加载
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                // 2. 如果是系统级类 (如 java.lang.*、Spring 框架核心类)，必须委派给父类加载器
                if (name.startsWith("java.") || name.startsWith("org.springframework.") || name.startsWith("com.lest.common.")) {
                    c = super.loadClass(name, resolve);
                } else {
                    try {
                        // 3. 插件自身特有依赖，优先由插件类加载器在自身的 Jar 中寻找，避免使用宿主机的冲突类
                        c = findClass(name);
                    } catch (ClassNotFoundException e) {
                        c = super.loadClass(name, resolve);
                    }
                }
            }
            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
    }
}
```

#### 5.2.3 动态 API 路由沙箱注册
为了使宿主网关 (`lest-gateway`) 动态识别插件的 API：
1. 插件在自身的 Entry 类中通过注解 `@PluginController` 声明接口。
2. `lest-plugin` 加载 jar 时反射解析该注解，并在 Spring Boot 内置的 `RequestMappingHandlerMapping` 中动态注册路由。

```java
// 动态将插件 API 注册到 Spring Boot Web 容器中
package com.lest.plugin.manager;

import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import java.lang.reflect.Method;

@Component
@RequiredArgsConstructor
public class DynamicRouteRegistrar {
    private final RequestMappingHandlerMapping handlerMapping;

    public void registerPluginRoute(String pluginId, Object controllerInstance, Method method, String path) {
        // 构建独立于宿主系统的路由：/api/v1/plugins/{pluginId}/{path}
        RequestMappingInfo requestMappingInfo = RequestMappingInfo
                .paths("/api/v1/plugins/" + pluginId + "/" + path)
                .methods(RequestMethod.POST) // 强制插件数据变更均使用 POST
                .build();

        // 动态注入到 Spring 容器
        handlerMapping.registerMapping(requestMappingInfo, controllerInstance, method);
    }
}
```

---

## 6. 🚀 总结与 AI 执行机制

本规范已被注册在项目的最高审查层。当 AI（包括 Cascade 自身）在为 LEST Platform 开发任何新功能时：

1. **自动审查**：首先拉取本规范 `@/Users/liuyue/code/lest-platform/docs/AI_DEVELOPER_IMPLEMENTATION_SPEC.md`。
2. **严防漂移**：严格对照第三节的微服务数据聚合逻辑、双部署模式抽象配置代码，以及第五节的特定微服务架构设计，不可直接硬编码实现。
3. **交付质量**：每提交一个 PR，对应代码必须提供如第四节所示的 100% Mock 覆盖率单元测试。
