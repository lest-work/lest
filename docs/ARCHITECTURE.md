# LEST Platform 架构文档

## 1. 架构概述

### 1.1 整体架构图

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                                    客户端层                                          │
│  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐                │
│  │   Web 前端 (Vue)  │  │   H5 移动端       │  │   第三方系统      │                │
│  │   端口: 3000      │  │   响应式设计      │  │   HTTP API       │                │
│  └────────┬──────────┘  └────────┬──────────┘  └────────┬──────────┘                │
└───────────┼──────────────────────┼──────────────────────┼─────────────────────────┘
            │                      │                      │
            └──────────────────────┼──────────────────────┘
                                   │
┌──────────────────────────────────▼────────────────────────────────────────────────┐
│                                 网关层                                              │
│  ┌─────────────────────────────────────────────────────────────────────────────┐   │
│  │                         Apache APISIX (K8s Ingress)                        │   │
│  │                                                                              │   │
│  │  • 路由转发 (lb://lest-xxx)                                                │   │
│  │  •  JWT 认证                                                                │   │
│  │  •  限流熔断                                                                │   │
│  │  •  请求日志                                                                │   │
│  └─────────────────────────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────┬─────────────────────────────────────────────┘
                                       │
     ┌─────────────────────────────────┼─────────────────────────────────┐
     │                                 │                                 │
┌────▼──────────────┐     ┌───────────▼───────────────┐     ┌───────────▼───────────┐
│     lest-auth      │     │       lest-task           │     │    lest-project       │
│   用户认证服务      │     │     任务管理服务           │     │   项目管理服务         │
│   端口: 8096      │     │     端口: 8083           │     │   端口: 8082         │
│                    │     │                         │     │                       │
│   核心功能:        │     │   核心功能:              │     │   核心功能:           │
│   • JWT Token     │     │   • 任务 CRUD            │     │   • 项目 CRUD         │
│   • 用户管理       │     │   • 任务分配             │     │   • 成员管理          │
│   • 角色权限       │     │   • 任务评论             │     │   • 迭代管理          │
│   • 菜单管理       │     │   • 跨项目依赖           │     │   • 里程碑管理        │
│   • 字典数据       │     │   • Webhook 处理         │     │                       │
│   • 机构管理       │     │   • Kafka 事件消费       │     │                       │
└──────────┬─────────┘     └───────────┬─────────────┘     └───────────┬─────────────┘
           │                          │                          │
     ┌─────▼──────────────┐    ┌─────▼──────────────────┐    ┌─────▼──────────────┐
     │   lest-release       │    │  消息队列 (Kafka)      │    │  基础设施层         │
     │   发布管理服务        │    │                        │    │                     │
     │   端口: 8087        │    │  • lest-task 任务事件   │    │  • MySQL 8.4.9      │
     │                     │    │  • lest-release 发布事件│    │  • Redis 8.6.3       │
     │   核心功能:          │    │                        │    │  • Kafka 4.3.0     │
     │   • 发布计划        │    └────────────────────────┘    │  • Nacos 3.2.1      │
     │   • 发布审批        │                                   │  • pgsty/minio      │
     │   • 发布部署/回滚   │                                   │                     │
     └─────────────────────┘                                   └───────────────────┘

┌─────────────────────────────────────────────────────────────────────────────────────┐
│                               基础设施层 (成熟开源中间件)                              │
│                                                                                     │
│  ┌────────────────┐  ┌────────────────┐  ┌────────────────┐  ┌────────────────┐  │
│  │  MySQL 8.4.9  │  │  Redis 8.6.3  │  │  Kafka 4.3.0   │  │  Nacos 3.2.1  │  │
│  │  端口: 3306    │  │  端口: 6379    │  │  端口: 9092    │  │  端口: 8848   │  │
│  │                │  │  • 会话缓存     │  │  • 事件驱动    │  │  • 服务注册   │  │
│  │  4 个 Database │  │  • 分布式锁     │  │  • 异步消息    │  │  • 配置中心   │  │
│  │  auth_db       │  │  • Redisson    │  │  • KRaft 模式  │  │  • AI Registry │  │
│  │  project_db    │  │  (Redisson)   │  │                │  │               │  │
│  │  task_db       │  │                │  │                │  │               │  │
│  │  release_db    │  │                │  │                │  │               │  │
│  └────────────────┘  └────────────────┘  └────────────────┘  └────────────────┘  │
│                                                                                     │
│                          ┌────────────────┐  ┌────────────────────────────────────┐ │
│                          │  pgsty/minio   │  │  Spring Cloud 2025.0.2 (Northfields) │ │
│                          │  端口: 9000    │  │  SCG 4.3.4 / OpenFeign 4.3.2       │ │
│                          │  S3 兼容存储    │  │  Java 17+ / JDK 25 友好             │ │
│                          └────────────────┘  └────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────────────────┘
```
                                                        └──────────────────────────────┘
### 1.2 项目目录结构

本文档描述的项目为 **Java 微服务架构**，所有代码均为 Java/Gradle，无 Python/FastAPI 代码。

```
lest-platform/                         # 项目根目录
│
├── backend/                      # 后端 Java 微服务（Gradle Monorepo）
│   ├── build.gradle                  # 根构建配置
│   ├── settings.gradle                # 模块列表
│   ├── gradle.properties              # 版本统一管理
│   │
│   ├── lest-common/                  # 公共模块（所有服务共享）
│   │   ├── lest-common-base/        # 基础工具（Result/PageResult/异常封装/错误码）
│   │   ├── lest-common-log/         # 日志增强（AOP 链路追踪 + JSON 输出）
│   │   ├── lest-common-redis/       # Redis 工具（StringRedisTemplate + Redisson 分布式锁）
│   │   └── lest-common-security/    # 安全工具（统一 JWT 工具类 JwtUtils）
│   │
│   └── lest-modules/                 # 微服务模块（仅已有代码的 4 个）
│       ├── lest-auth/                # 用户认证服务（含用户/角色/菜单/字典/机构管理）
│       ├── lest-project/             # 项目管理服务（含项目/迭代/里程碑管理）
│       ├── lest-task/               # 任务管理服务（含任务/标签/评论/工时管理）
│       └── lest-release/            # 发布管理服务（含发布计划/审批/部署/回滚管理）
│
├── frontend/                          # 前端（Vue 3 + Vite）
│   ├── src/                          # 源代码
│   │   ├── api/                     # API 调用封装
│   │   ├── components/               # 公共组件
│   │   ├── views/                    # 页面视图
│   │   ├── router/                   # 路由配置
│   │   ├── store/                    # Pinia 状态管理
│   │   ├── utils/                    # 工具函数
│   │   └── i18n/                    # 国际化
│   ├── public/                       # 静态资源
│   ├── vite.config.ts                # Vite 配置
│   └── package.json
│
├── docs/                             # 文档目录
│   ├── PRD/                         # 产品需求文档
│   │   ├── V1.0/                   # V1.0 功能集
│   │   └── V2.0/                   # V2.0 AI-Native 重构
│   ├── ARCHITECTURE.md              # 本架构文档
│   ├── DATABASE.md                  # 数据库设计文档
│   ├── API.md                       # API 接口文档
│   ├── DEVELOPMENT.md               # 开发指南
│   └── DEPLOYMENT.md               # 部署指南
│
├── k8s/                              # Kubernetes 部署清单
│   ├── config/                      # ConfigMap / Secret
│   ├── infrastructure/               # 基础设施（MySQL/Redis/Kafka/Nacos/MinIO）
│   └── apps/                       # 应用服务（lest-auth / lest-project / lest-task / lest-release）
│
├── sql/                              # SQL 脚本目录
├── docker-compose.yml                # 本地开发环境编排（MySQL + Redis + Kafka + Nacos + MinIO）
├── start-dev.sh                      # 开发环境启动脚本
└── stop-dev.sh                      # 开发环境停止脚本
```

> **说明**：以下目录为参考项目，**不在 LEST Platform 当前开发范围内**：
> - `Old-旧项目用来参考-里面内容不需要开发和编辑/`（旧参考项目）
> - `backend-java-不要修改我的代码/`（WakaTime 兼容参考代码）
> - `ele-admin-plus-ts 当前前端参考的模版/`（仅作 UI 样式参考，非代码引用）

---

## 1.5 服务注册与发现

### 1.5.1 问题背景

LEST Platform 为开源私有化部署项目，存在以下场景：

| 场景 | 客户端 | 问题 |
|------|--------|------|
| SaaS 托管 | Web (固定域名) | 无问题，域名写死在前端 |
| 私有化部署（内网）| Web | 运维部署时配置 Nginx 域名，浏览器访问无感知 |
| **私有化部署（开源分发）** | **APP / H5** | **App 里不知道该连谁的后端地址** |
| 多实例多租户 | APP | 用户切换租户时需动态切换后端 |

因此，**APP 和 H5 无法像 Web 一样靠运维人员配置 Nginx 来解决连接问题**，必须在应用层实现「客户端发现后端地址」的能力。

### 1.5.2 解决方案：实例注册 + 客户端拉取

采用 **Nacos** 作为服务注册中心，各微服务启动时自动注册到 Nacos，客户端通过 Nacos 发现后端地址：

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           部署实例 A（企业内网）                            │
│  ┌────────────┐     ┌─────────────┐     ┌──────────────────────────┐    │
│  │ lest-auth  │────▶│   Nacos     │◀────│  K8s / 启动脚本        │    │
│  │ (注册自身)  │     │  (nacos)    │     │  自动注册服务实例        │    │
│  └────────────┘     │  端口: 8848 │     └──────────────────────────┘    │
│                     └──────┬──────┘                                      │
└────────────────────────────┼────────────────────────────────────────────┘
                             │
            ┌────────────────┴────────────────┐
            ▼                                 ▼
    ┌───────────────┐                 ┌───────────────┐
    │   APP (用户)  │                 │  H5 (用户)   │
    │               │                 │               │
    │ 1. 打开 App   │                 │ 1. 打开 H5   │
    │ 2. 输入/扫码   │                 │ 2. 输入域名  │
    │   或选择实例   │                 │   或扫码      │
    │ 3. App 向    │                 │ 3. H5 向    │
    │   Nacos 查询  │                 │   Nacos 查询 │
    │   服务列表    │                 │   服务列表   │
    │ 4. 连接后端   │                 │ 4. 连接后端  │
    └───────────────┘                 └───────────────┘
```

### 1.5.3 核心设计

#### (1) Nacos 服务注册

各微服务通过 Spring Cloud Alibaba Nacos Discovery 启动时自动注册到 Nacos：

```json
// 注册信息结构
{
  "instanceId": "inst-prod-001",
  "tenantId": "default",
  "region": "华东-上海",
  "services": {
    "auth":      "http://192.168.1.10:8001",
    "task":      "http://192.168.1.10:8002",
    "system":    "http://192.168.1.10:8004",
    "ai":        "http://192.168.1.10:8008",
    "gateway":   "http://192.168.1.10:8001",
    "websocket": "ws://192.168.1.10:8001/ws"
  },
  "features": {
    "aiEnabled": true,
    "ssoEnabled": false,
    "version": "1.1.0"
  },
  "registeredAt": "2026-05-25T10:00:00Z",
  "heartbeatAt": "2026-05-25T10:05:00Z"
}
```

#### (2) 客户端拉取协议

APP/H5 首次打开时：

**方式一：扫码绑定（推荐，零配置上手）**
- 管理员在 Web 管理后台 → 系统设置 → 生成「实例绑定码」（含 instanceId 和 token）
- 用户打开 APP → 扫码 → APP 直接获取该实例的完整服务地址列表，连接后端

**方式二：手动输入域名**
- 用户输入部署实例的 Gateway 域名
- APP 请求 `{domain}/api/public/config` 获取实例服务地址列表
- 无需注册中心，适合单实例部署场景

**方式三：实例选择器（多租户/多实例场景）**
- APP 内置一个默认的公共查询服务地址（如 `https://registry.lest.com`），用于查询已注册实例列表
- 企业可自建公共查询服务（指向内部注册中心）
- 用户从列表中选择 → 连接选中的实例

#### (3) 客户端本地缓存与降级

- 首次成功连接后，APP 将服务地址缓存到本地（含版本号）
- 注册中心不可达时，使用本地缓存（保证离线可用性）
- 实例版本变更时，APP 检测到版本不匹配，提示用户更新或自动切换

### 1.5.4 实现要点

| 组件 | 技术选型 | 说明 |
|------|----------|------|
| 注册中心服务 | 内嵌轻量服务 | 使用 etcd / Consul SDK，Pod 启动时自动注册 |
| 健康检查 | 注册中心主动探活 | 超过 3 次心跳失败自动标记为不健康 |
| 安全 | HTTPS + Instance Token | APP 扫码时携带一次性 Token，防止恶意注册 |
| 多租户 | 每个实例注册时携带 `tenantId` | 客户端切换租户时重新拉取对应实例配置 |
| Web 前端 | 无需此机制 | 运维配置 Nginx 域名，浏览器通过 DNS 解析 |

---

## 2. 服务间通信

### 2.1 通信方式概览

LEST Platform 采用 **同步远程调用 + 异步消息通知** 的混合通信模式：

| 通信模式 | 技术选型 | 适用场景 |
|----------|----------|----------|
| 同步调用 | OpenFeign + LoadBalancer | 需要实时返回结果的查询、审批操作 |
| 异步通知 | Apache Kafka | 跨服务事件通知、日志异步处理 |
| 事件驱动 | Spring ApplicationEvent + Spring Cloud Stream | 内部模块解耦 |

### 2.2 OpenFeign 客户端示例

#### 2.2.1 定义 Feign Client 接口

```java
// lest-task 模块中定义，供其他服务调用
@FeignClient(name = "lest-task", path = "/api/task/v1", configuration = FeignConfig.class)
public interface TaskFeignClient {

    @GetMapping("/{taskId}")
    Result<TaskVO> getTaskById(@PathVariable Long taskId);

    @GetMapping("/{taskId}/coding-duration")
    Result<CodingDurationVO> getTaskCodingDuration(@PathVariable Long taskId,
                                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);

    @PostMapping("/{taskId}/associate")
    Result<Void> associateCodingHeartbeat(@PathVariable Long taskId, @RequestBody HeartbeatAssociateDTO dto);

    @GetMapping("/project/{projectId}")
    Result<List<TaskVO>> getTasksByProject(@PathVariable Long projectId);
}
```

#### 2.2.2 配置类

```java
@Configuration
@EnableConfigurationProperties
public class FeignConfig {

    @Bean
    public Retryer feignRetryer() {
        // 重试策略：失败后等待 100ms 重试，最多重试 3 次
        return new Retryer.Default(100, 500, 3);
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public RequestInterceptor tokenInterceptor() {
        return requestTemplate -> {
            // 从 Security Context 传递 JWT Token 到下游服务
            String token = SecurityContextHolder.getContext().getToken();
            if (StringUtils.hasText(token)) {
                requestTemplate.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            }
        };
    }
}
```

#### 2.2.3 调用示例（lest-performance 消费 lest-wakapi 编码时长数据）

```java
@Service
@RequiredArgsConstructor
public class CodingStatsService {

    private final WakatimeFeignClient wakatimeFeignClient;

    /**
     * 获取用户在指定时间范围内的编码时长
     */
    public CodingStatsVO getUserCodingStats(Long userId, LocalDate startDate, LocalDate endDate) {
        Result<WakatimeSummaryVO> result = wakatimeFeignClient.getUserSummary(userId, startDate, endDate);
        if (!result.isSuccess()) {
            throw new BizException("获取编码统计数据失败: " + result.getMessage());
        }
        WakatimeSummaryVO summary = result.getData();
        return CodingStatsVO.builder()
                .userId(userId)
                .totalSeconds(summary.getTotalSeconds())
                .dailyBreakdown(summary.getDailyBreakdown())
                .languageBreakdown(summary.getLanguageBreakdown())
                .projectBreakdown(summary.getProjectBreakdown())
                .build();
    }
}
```

### 2.3 Kafka 消息队列

#### 2.3.1 Kafka 主题命名规范

所有 Kafka 主题遵循以下命名规范：

```
{env}-{biz-domain}-{event-type}

示例:
  lest-dev-task-taskCreated        # 开发环境-task域-任务创建事件
  lest-prod-notification-email     # 生产环境-notification域-邮件通知事件
  lest-uat-code-mrOpened           # UAT环境-code域-MR打开事件
```

**主题前缀对照表：**

| 主题前缀 | 所属服务 | 用途 |
|----------|----------|------|
| `lest-{env}-task-*` | lest-task | 任务生命周期事件 |
| `lest-{env}-code-*` | lest-code | 代码/MR 生命周期事件 |
| `lest-{env}-ci-*` | lest-ci | CI 构建生命周期事件 |
| `lest-{env}-meeting-*` | lest-meeting | 会议生命周期事件 |
| `lest-{env}-release-*` | lest-release | 发布生命周期事件 |
| `lest-{env}-notification-*` | lest-notification | 统一通知分发 |
| `lest-{env}-wakapi-*` | lest-wakapi | WakaTime 编码心跳事件 |

#### 2.3.2 事件消息格式

所有事件消息使用统一的 CloudEvents + 业务扩展字段格式：

```json
{
  "specversion": "1.0",
  "type": "com.lest.task.taskCreated",
  "source": "lest-task",
  "subject": "task:10086",
  "id": "evt-a1b2c3d4-e5f6-7890",
  "time": "2026-05-25T10:30:00+08:00",
  "datacontenttype": "application/json",
  "data": {
    "taskId": 10086,
    "taskTitle": "实现用户登录功能",
    "projectId": 101,
    "assigneeId": 2001,
    "reporterId": 2002,
    "taskType": "STORY",
    "priority": "HIGH",
    "status": "IN_PROGRESS",
    "labels": ["backend", "auth"],
    "extra": {}
  },
  "lest": {
    "traceId": "trace-abc123",
    "spanId": "span-def456",
    "tenantId": "tenant-001",
    "env": "prod"
  }
}
```

#### 2.3.3 核心事件列表

**lest-task 服务发布的事件：**

| 事件类型 | 触发时机 | 消费者 |
|----------|----------|--------|
| `lest-task.taskCreated` | 新建任务时 | lest-notification, lest-wakapi |
| `lest-task.taskUpdated` | 任务状态/字段变更 | lest-notification |
| `lest-task.taskAssigned` | 任务分配变更 | lest-notification |
| `lest-task.taskCompleted` | 任务完成时 | lest-notification, lest-performance |
| `lest-task.taskDeleted` | 任务删除时 | lest-notification |
| `lest-task.heartbeatLinked` | 编码心跳关联任务 | lest-task (内部更新) |

**lest-code 服务发布的事件：**

| 事件类型 | 触发时机 | 消费者 |
|----------|----------|--------|
| `lest-code.mrOpened` | MR/PR 创建时 | lest-notification |
| `lest-code.mrMerged` | MR/PR 合并时 | lest-notification, lest-task (更新关联任务状态) |
| `lest-code.mrClosed` | MR/PR 关闭时 | lest-notification |
| `lest-code.commitPushed` | 提交推送时 | lest-notification |

**lest-ci 服务发布的事件：**

| 事件类型 | 触发时机 | 消费者 |
|----------|----------|--------|
| `lest-ci.buildStarted` | 构建开始时 | lest-notification |
| `lest-ci.buildCompleted` | 构建完成时 | lest-notification, lest-task (更新关联任务状态) |
| `lest-ci.buildFailed` | 构建失败时 | lest-notification, lest-task (更新关联任务) |
| `lest-ci.deploymentStarted` | 部署开始时 | lest-notification |
| `lest-ci.deploymentCompleted` | 部署完成时 | lest-notification |

**lest-meeting 服务发布的事件：**

| 事件类型 | 触发时机 | 消费者 |
|----------|----------|--------|
| `lest-meeting.meetingCreated` | 会议创建时 | lest-notification |
| `lest-meeting.actionItemCreated` | 行动项创建时 | lest-task (创建关联任务) |
| `lest-meeting.meetingEnded` | 会议结束时 | lest-notification |

**lest-release 服务发布的事件：**

| 事件类型 | 触发时机 | 消费者 |
|----------|----------|--------|
| `lest-release.releasePlanned` | 发布计划创建 | lest-notification |
| `lest-release.releaseApproved` | 发布审批通过 | lest-notification, lest-ci (触发部署) |
| `lest-release.releaseDeployed` | 发布部署完成 | lest-notification |
| `lest-release.releaseRolledBack` | 发布回滚 | lest-notification |

**lest-wakapi 服务发布的事件：**

| 事件类型 | 触发时机 | 消费者 |
|----------|----------|--------|
| `lest-wakapi.heartbeatReceived` | 收到编码心跳 | lest-task (关联编码时段到任务) |
| `lest-wakapi.dailySummary` | 每日编码汇总生成 | lest-notification (编码活跃度通知) |
| `lest-wakapi.lowActivityAlert` | 低活跃度告警 | lest-notification |

**AI 服务 (lest-ai) 事件 — 通过通知服务路由消费：**

| 事件类型 | 触发时机 | 处理方式 |
|----------|----------|----------|
| TASK_COMPLETED | 任务完成时 | 通知服务 → HTTP 调用 lest-ai → 工时预估准确度分析 → 推送结果 |
| MR_CREATED | MR 创建时 | 通知服务 → HTTP 调用 lest-ai → 代码审查 → 推送结果 |
| MEETING_ENDED | 会议结束时 | 通知服务 → HTTP 调用 lest-ai → 会议总结生成 → 推送结果 |
| DAILY_REPORT | 每日定时 | 通知服务 → HTTP 调用 lest-ai → 编码活跃度 + 个人发展建议 → 推送结果 |
| WEEKLY_REPORT | 每周定时 | 通知服务 → HTTP 调用 lest-ai → 团队状态报告 → 推送结果 |
| PROJECT_RISK_CHECK | 每日定时 | 通知服务 → HTTP 调用 lest-ai → 项目风险预警 → 推送结果 |

#### 2.3.4 Kafka 消费者配置示例

```java
@Configuration
@RequiredArgsConstructor
public class TaskEventConsumerConfig {

    private final TaskEventHandler taskEventHandler;
    private final ObjectMapper objectMapper;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            ConsumerFactory<String, String> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(3);
        factory.getContainerProperties().setPollTimeout(3000);
        return factory;
    }

    @KafkaListener(
            topics = {"${kafka.topic.task-events}"},
            groupId = "${kafka.group.task-consumer}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleTaskEvent(ConsumerRecord<String, String> record) {
        CloudEvent cloudEvent = objectMapper.readValue(record.value(), CloudEvent.class);
        log.info("收到任务事件: type={}, subject={}, id={}",
                cloudEvent.getType(), cloudEvent.getSubject(), cloudEvent.getId());

        switch (cloudEvent.getType()) {
            case "lest-task.taskCreated" -> taskEventHandler.onTaskCreated(cloudEvent);
            case "lest-task.taskCompleted" -> taskEventHandler.onTaskCompleted(cloudEvent);
            case "lest-task.taskAssigned" -> taskEventHandler.onTaskAssigned(cloudEvent);
            default -> log.warn("未知任务事件类型: {}", cloudEvent.getType());
        }
    }
}
```

### 2.4 服务通信矩阵

| 生产者服务 | 消费者服务 | 通信方式 | 说明 |
|------------|------------|----------|------|
| Gateway | 所有服务 | OpenFeign (lb://) | 路由转发，JWT Token 透传 |
| lest-task | lest-code | Kafka | 任务关联代码提交事件 |
| lest-task | lest-ci | Kafka | 任务关联构建事件 |
| lest-task | lest-notification | Kafka | 任务状态变更通知 |
| lest-task | lest-wakapi | OpenFeign | 查询任务编码时长 |
| lest-code | lest-task | Kafka | 提交关联任务更新 |
| lest-code | lest-notification | Kafka | MR 事件通知 |
| lest-ci | lest-task | Kafka | 构建完成通知，更新任务关联构建状态 |
| lest-ci | lest-notification | Kafka | 构建状态事件通知 |
| lest-meeting | lest-task | OpenFeign | 行动项自动创建任务 |
| lest-meeting | lest-notification | Kafka | 会议事件通知 |
| lest-release | lest-ci | OpenFeign | 获取构建信息和部署状态 |
| lest-release | lest-task | OpenFeign | 获取关联任务信息 |
| lest-release | lest-notification | Kafka | 发布事件通知 |
| lest-performance | lest-task | OpenFeign | 获取工时数据用于绩效统计 |
| lest-performance | lest-wakapi | OpenFeign | 获取编码时长数据用于绩效统计 |
| lest-performance | lest-code | OpenFeign | 获取代码量/MR 数据用于绩效统计 |
| lest-performance | lest-meeting | OpenFeign | 获取会议参与数据用于绩效统计 |
| lest-wakapi | lest-task | Kafka | 编码心跳关联任务事件 |
| lest-wakapi | lest-notification | Kafka | 编码活跃度通知（低活跃度告警等） |
| lest-ai | lest-task | OpenFeign | AI 任务分析结果回写 |
| lest-ai | lest-code | OpenFeign | AI 代码审查结果回写 |
| lest-open | 所有服务 | OpenFeign | Webhook 投递触发 |
| lest-plugin | 所有服务 | OpenFeign | 插件调用平台开放 API |

---

## 3. 数据模型

### 3.1 数据库隔离策略

LEST Platform 采用 **每个服务独立数据库** 的强隔离策略，确保服务间无直接数据库依赖。

#### 3.1.1 隔离原则

1. **Database 级别隔离**：每个服务拥有独立的 MySQL Database（`xxx_db`），确保故障隔离和数据隔离
2. **无跨库 JOIN**：服务间不允许直接跨库关联查询
3. **服务内聚合**：同一服务的多个聚合根可共享同一 Schema（如 task_db 中的 task 和 task_comment）
4. **外部 ID 引用**：跨服务关联通过业务 ID（如 taskId、userId）而非数据库主键

#### 3.1.2 Schema 规划总览

| Schema 名称 | 所属服务 | 表数量 | 核心表 |
|-------------|----------|--------|--------|
| `auth_db` | lest-auth | 7 | sys_user, sys_role, sys_menu, sys_org, sys_user_role, sys_role_menu, auth_token |
| `system_db` | lest-system | 5 | sys_config, sys_dict, sys_dict_data, sys_log, sys_backup |
| `project_db` | lest-project | 2 | project, project_member |
| `task_db` | lest-task | 6 | task, task_comment, task_worklog, task_commit, task_merge_request, task_pipeline |
| `code_db` | lest-code | 3 | code_repository, code_commit, code_merge_request |
| `notification_db` | lest-notification | 5 | notification, notification_channel, notification_template, notification_setting, notification_log |
|| `ci_db` | lest-ci | 3 | ci_provider, ci_pipeline, ci_build |
|| `meeting_db` | lest-meeting | 4 | meet_meeting, meet_participant, meet_minutes, meet_action_item |
|| `performance_db` | lest-performance | 6 | perf_work_log, perf_metric_config, perf_metric, perf_goal, perf_report, perf_metric_data |
|| `release_db` | lest-release | 4 | release_version, release_approval, release_change, release_deployment |
|| `ai_db` | lest-ai | 8 | ai_model_config, ai_prompt_template, ai_conversation, ai_code_review, ai_meeting_summary, ai_perf_insight, ai_estimate_accuracy, ai_project_risk |
|| `wakapi_db` | lest-wakapi | 4 | wakapi_heartbeat, wakapi_daily_summary, wakapi_task_link, wakapi_machine |
|| `plugin_db` | lest-plugin | 4 | plugin_registry, plugin_extension, plugin_version, plugin_permission |
|| `open_db` | lest-open | 6 | open_api_token, open_oauth_application, open_oauth_token, open_webhook, open_webhook_delivery, open_api_log |
|| `file_db` | lest-file | 1 | file_record |
#### 3.1.3 核心实体关系图

```
┌──────────────────────────────────────────────────────────────────────────────────┐
│                              跨服务实体关联图                                        │
├──────────────────────────────────────────────────────────────────────────────────┤
│                                                                                   │
│  ┌─────────────┐     1:N      ┌─────────────┐     1:N      ┌─────────────┐    │
│  │  project      │────────────│  task         │────────────│  task_worklog  │   │
│  │ (project_db) │              │ (task_db)    │              │  (task_db)   │    │
│  └──────┬───────┘              └──────┬───────┘              └──────────────┘    │
│         │  projectId (外键)           │ taskId (外键)                              │
│         │                             │                                             │
│  ┌──────▼───────┐              ┌──────▼───────┐      ┌─────────────┐              │
│  │ project_     │              │ task_        │      │ wakapi_heartbeat │        │
│  │ member        │              │ heartbeat    │◄────│ (wakapi_db) │              │
│  │ (project_db)  │              │ (task_db)    │      └──────┬──────┘              │
│  └───────────────┘              └──────┬───────┘             │ taskId               │
│                                        │                     │ (业务关联)            │
│  ┌─────────────┐              ┌────────▼────────┐      ┌──────▼──────────┐         │
│  │ code_repository│◄── repoId │ code_merge_request│  │ wakapi_daily_summary│ │
│  │ (code_db)   │              │ (code_db)      │      │ (wakapi_db)    │         │
│  └─────────────┘              └──────┬────────┘      └─────────────────┘         │
│                                        │                                          │
│  ┌─────────────┐               ┌───────▼────────┐                                │
│  │ ci_pipeline  │◄──pipelineId│ meet_action_item│◄──meetingId                    │
│  │  (ci_db)    │               │ (meeting_db)   │                                 │
│  └──────┬──────┘               └────────────────┘                                 │
│         │ pipelineId                                                                │
│  ┌──────▼─────────────┐      ┌─────────────────┐      ┌─────────────────┐          │
│  │ release_deployment│     │ notification     │     │ perf_personal_  │         │
│  │ (release_db)      │      │ (notification_db)│      │ report         │          │
│  └───────────────────┘      └─────────────────┘      │ (performance_db)│          │
│                                                        └─────────────────┘          │
└──────────────────────────────────────────────────────────────────────────────────────┘
```

### 3.2 跨服务关联机制

#### 3.2.1 关联策略

由于采用数据库隔离策略，跨服务关联通过以下机制实现：

| 关联类型 | 实现方式 | 示例 |
|----------|----------|------|
| 任务-代码 | 业务 ID 关联 + Kafka 事件同步 | taskId → code_merge_request.task_id |
| 任务-CI | 业务 ID 关联 + Kafka 事件同步 | taskId → ci_pipeline.task_id |
| 任务-WakaTime | 心跳关联表 + Kafka 事件 | wakapi_task_link |
| 会议-任务 | OpenFeign 调用创建 + Kafka 事件 | lest-meeting → lest-task |
| 绩效-各服务 | OpenFeign 聚合查询 | lest-performance 聚合多服务数据 |

#### 3.2.2 跨服务 ID 映射表

| 源服务 | 目标服务 | 关联字段 | 映射方式 |
|--------|----------|----------|----------|
| lest-task | lest-code | code_merge_request.task_id | Kafka 事件同步 |
| lest-task | lest-ci | ci_pipeline.task_id | Kafka 事件同步 |
| lest-task | lest-wakapi | wakapi_task_link.task_id | Kafka 事件同步 |
| lest-meeting | lest-task | m_action_item.related_task_id | OpenFeign 创建 |
| lest-performance | lest-task | 查询接口聚合 | OpenFeign 查询 |
| lest-performance | lest-wakapi | 查询接口聚合 | OpenFeign 查询 |
| lest-release | lest-ci | release_deployment.pipeline_id | OpenFeign 查询 |

### 3.3 ER 图（核心模块）

#### 3.3.1 任务管理模块 (task_db)

```
m_task
├── id (PK, BIGINT)
├── project_id (FK → project_db.project.id)
├── task_no (VARCHAR, 唯一编号，如 TASK-1234)
├── title (VARCHAR)
├── description (TEXT)
├── task_type (ENUM: STORY/TASK/BUG/EPIC)
├── priority (ENUM: LOW/MEDIUM/HIGH/CRITICAL)
├── status (ENUM: BACKLOG/TODO/IN_PROGRESS/IN_REVIEW/DONE/CLOSED)
├── assignee_id (FK → auth_db.sys_user.id)
├── reporter_id (FK → auth_db.sys_user.id)
├── iteration_id (FK → project_db.m_iteration.id)
├── story_points (INT)
├── estimated_hours (DECIMAL)
├── actual_hours (DECIMAL)
├── start_date (DATETIME)
├── due_date (DATETIME)
├── labels (JSON)
├── custom_fields (JSON)
├── parent_id (FK → task.id, 父任务)
├── sprint_id (FK → project_db.m_iteration.id)
├── position (INT, 看板排序)
├── linked_build_id (VARCHAR, lest-ci 构建ID)
├── linked_mr_id (BIGINT, lest-code MR ID)
├── coding_duration_seconds (BIGINT, 从 lest-wakapi 汇总)
├── created_at, updated_at, deleted_at, tenant_id
│
├──┬── task_comment (1:N)
│       ├── id, task_id (FK), content, author_id, parent_id (回复), created_at
│
├──┬── task_attachment (1:N)
│       ├── id, task_id (FK), file_id (FK → file_db), file_name, file_size
│
├──┬── task_worklog (1:N)
│       ├── id, task_id (FK), user_id, hours, description, logged_at
│
├──┬── task_label (N:N via join)
│       ├── id, task_id (FK), label_id, label_name, label_color
│
├──┬── task_heartbeat (1:N, 编码时段)
│       ├── id, task_id (FK), user_id, start_time, end_time, duration_seconds
│
└──┬── task_coding_stats (1:N, 日级汇总)
        ├── id, task_id (FK), user_id, stat_date, total_seconds, languages (JSON)
```

#### 3.3.2 WakaTime 集成模块 (wakapi_db)

```
wakapi_machine
├── id (PK)
├── user_id (FK → auth_db.sys_user.id, UNIQUE)
├── api_key (VARCHAR, 加密存储)
├── last_sync_at (DATETIME)
├── is_active (BOOLEAN)
└── created_at, updated_at, tenant_id

wakapi_heartbeat
├── id (PK)
├── user_id (FK → auth_db.sys_user.id)
├── entity (VARCHAR, 文件路径)
├── type (ENUM: file/folder/project)
├── project (VARCHAR, 项目名)
├── language (VARCHAR)
├── operating_system (VARCHAR)
├── editor (VARCHAR)
├── branch (VARCHAR)
├── machine (VARCHAR)
├── time (DATETIME, 心跳时间戳)
├── timestamp (BIGINT, Unix 秒)
├── is_write (BOOLEAN)
├── line_number (INT)
├── lines (INT)
├── created_at (DATETIME, 入库时间)
└── tenant_id

wakapi_task_link
├── id (PK)
├── heartbeat_id (FK → wakapi_heartbeat.id)
├── task_id (BIGINT, lest-task 业务ID，非外键)
├── user_id (FK → auth_db.sys_user.id)
├── confidence (DECIMAL, 0-1, 关联置信度)
├── auto_linked (BOOLEAN, 是否自动关联)
├── link_method (ENUM: TIMESTAMP/MANUAL/PATTERN)
├── created_at
└── tenant_id

wakapi_daily_summary
├── id (PK)
├── user_id (FK → auth_db.sys_user.id)
├── summary_date (DATE)
├── total_seconds (BIGINT)
├── total_editors (JSON)
├── total_languages (JSON)
├── total_projects (JSON)
├── daily_breakdown (JSON, 每日小时分布)
├── language_breakdown (JSON)
├── project_breakdown (JSON)
├── goal_seconds (BIGINT)
├── goal_percentage (DECIMAL)
├── created_at, updated_at
└── tenant_id (UNIQUE: user_id + summary_date)
```

---

## 4. 插件系统集成架构

### 4.1 架构概述

LEST Platform 插件系统采用 **SPI (Service Provider Interface)** 机制，允许第三方开发者通过标准接口扩展平台功能，无需修改核心代码。

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           插件系统架构                                       │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌──────────────┐         ┌──────────────┐         ┌──────────────┐       │
│  │   插件市场     │         │   lest-plugin │         │   开放 API   │       │
│  │  (前端商店)    │         │    服务 (8093) │         │   能力平台   │       │
│  └──────┬───────┘         └──────┬───────┘         └──────┬───────┘       │
│         │                       │                        │               │
│  ┌──────▼───────────────────────▼────────────────────────▼───────┐       │
│  │                      插件核心引擎                              │       │
│  │  • 插件生命周期管理（安装/卸载/启用/禁用）                         │       │
│  │  • 扩展点注册与发现（Extension Point Registry）                   │       │
│  │  • 插件隔离加载（类加载器隔离，每个插件独立 ClassLoader）          │       │
│  │  • SDK 版本管理（平台 API 与 SDK 版本对应）                      │       │
│  │  • 资源配额控制（CPU/内存/存储限制）                              │       │
│  └──────┬────────────────────────────────────────────┬───────────┘       │
│         │                                            │                   │
│  ┌──────▼───────┐  ┌──────▼───────┐  ┌──────▼───────┐  ┌──────▼───────┐  │
│  │  任务扩展点   │  │  代码扩展点   │  │  AI 扩展点   │  │ 通知扩展点   │  │
│  │ TaskExtension│  │CodeExtension │  │AIExtension   │  │NotifExtension│  │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘  │
│         │                  │                  │                  │         │
│  ┌──────▼───────┐  ┌──────▼───────┐  ┌──────▼───────┐  ┌──────▼───────┐  │
│  │   插件 A     │  │   插件 B      │  │   插件 C     │  │   插件 D     │  │
│  │ (Jira 同步)  │  │(SonarQube集成)│  │ (Copilot集成)│  │ (飞书通知)   │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  └──────────────┘  │
│                                                                             │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │                    平台 API 能力（通过 OpenFeign 调用）                │  │
│  │  lest-plugin → lest-task │ lest-code │ lest-notification │ lest-ai │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 4.2 扩展点定义

#### 4.2.1 核心扩展点接口

```java
// 任务扩展点
public interface TaskExtension {

    void onTaskCreated(TaskCreateEvent event);

    void onTaskUpdated(TaskUpdateEvent event);

    void onTaskStatusChanged(TaskStatusChangeEvent event);

    void onTaskAssigned(TaskAssignEvent event);

    void onTaskCompleted(TaskCompleteEvent event);

    TaskVO enrichTaskDetail(TaskVO task);

    List<CustomFieldDefinition> getCustomFields();

    Map<String, Object> getTaskMetrics(Long taskId);
}

// 代码扩展点
public interface CodeExtension {

    void onCommitPushed(CommitPushEvent event);

    void onMergeRequestOpened(MREvent event);

    void onMergeRequestMerged(MREvent event);

    List<CodeAnalysisResult> analyzeCode(Long mrId);
}

// AI 扩展点
public interface AIExtension {

    String getProviderName();

    AIChatResponse chat(AIChatRequest request);

    List<AICapability> getCapabilities();

    double getUsageCost(String model, int inputTokens, int outputTokens);
}

// 通知扩展点
public interface NotificationExtension {

    NotificationChannel getChannel();

    void send(Notification notification);

    NotificationTemplate parseTemplate(String templateContent);

    boolean verifyChannelConfig(Map<String, String> config);
}
```

### 4.3 插件生命周期

```
插件安装流程:
  上传插件 JAR/ZIP
       ↓
  验证插件签名和 SDK 版本兼容性
       ↓
  分配独立 ClassLoader
       ↓
  注册扩展点到 Extension Point Registry
       ↓
  执行插件初始化方法 (init)
       ↓
  插件启用 (ENABLED)

插件卸载流程:
  禁用插件 (DISABLED)
       ↓
  清理扩展点注册
       ↓
  关闭插件资源 (destroy)
       ↓
  释放 ClassLoader
       ↓
  删除插件文件
```

### 4.4 插件 SDK 与平台 API

插件通过 `lest-plugin-sdk` 与平台交互：

```java
// 插件中使用 SDK 调用平台能力
public class MyPluginIntegration {

    private final PluginTaskClient taskClient;
    private final PluginNotificationClient notificationClient;

    public void syncTaskToExternalSystem(Long taskId) {
        // 通过 SDK 获取任务详情（自动处理服务发现和认证）
        TaskVO task = taskClient.getTask(taskId);

        // 调用外部系统同步
        externalSystem.sync(task);

        // 通过 SDK 记录操作日志
        taskClient.addComment(taskId, "已同步到外部系统: " + externalSystem.getId());
    }
}
```

---

## 5. 开放平台集成架构

### 5.1 架构概述

LEST Platform 开放平台（lest-open）为第三方开发者提供标准化 API 能力，支持 OAuth2 和 API Key 两种认证方式，通过 Webhook 实现事件推送。

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                              开放平台架构                                          │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                  │
│  ┌────────────────────────────────────────────────────────────────────────────┐ │
│  │                         lest-open 服务 (8095)                               │ │
│  │                                                                            │ │
│  │  ┌────────────┐  ┌────────────┐  ┌────────────┐  ┌────────────┐           │ │
│  │  │  API Gateway │  │OAuth2 Server│  │ Webhook    │  │  Rate Limit │           │ │
│  │  │   路由分发   │  │  授权管理   │  │  投递服务   │  │   流量控制   │           │ │
│  │  └────────────┘  └────────────┘  └────────────┘  └────────────┘           │ │
│  └────────────────────────────────────────────────────────────────────────────┘ │
│                                    │                                             │
│         ┌──────────────────────────┼──────────────────────────┐               │
│         │                          │                          │               │
│  ┌──────▼──────┐           ┌───────▼──────┐          ┌───────▼──────┐        │
│  │  API Key    │           │  OAuth2      │          │  Webhook     │        │
│  │  认证       │           │  授权流程     │          │  投递        │        │
│  └─────────────┘           └──────────────┘          └──────────────┘        │
│         │                          │                          │               │
└─────────┼──────────────────────────┼──────────────────────────┼───────────────┘
          │                          │                          │
┌─────────▼──────────────────────────▼──────────────────────────▼───────────────┐
│                            第三方开发者生态                                      │
│  ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐                │
│  │  独立开发者 │    │  企业集成 │    │  移动 App │    │  CI/CD 系统 │                │
│  └──────────┘    └──────────┘    └──────────┘    └──────────┘                │
└────────────────────────────────────────────────────────────────────────────────┘
          │                          │                          │
          └──────────────────────────┼──────────────────────────┘
                                     │
                    ┌────────────────▼────────────────┐
                    │   OpenFeign → 各业务服务 API     │
                    │   lest-task / lest-code /        │
                    │   lest-task / lest-notification  │
                    └─────────────────────────────────┘
```

### 5.2 API Key 管理

| 功能 | 说明 |
|------|------|
| 创建 Key | 开发者可在开放平台控制台创建多个 API Key |
| 权限范围 | 每个 Key 可绑定不同的 API 权限范围（read/write/custom） |
| 有效期 | 支持设置 Key 有效期，支持续期 |
| 调用限制 | 每个 Key 有独立的 Rate Limit（默认 1000次/分钟） |
| 调用日志 | 完整记录每个 Key 的 API 调用日志 |

### 5.3 Webhook 配置

开发者可通过 Webhook 订阅平台事件，平台通过 HTTP POST 将事件推送到指定 URL：

```json
{
  "webhook_id": "wh-abc123",
  "app_id": "app-xyz789",
  "events": [
    "task.created",
    "task.completed",
    "code.mr.merged",
    "ci.build.completed"
  ],
  "url": "https://developer.example.com/webhook/lest",
  "secret": "whs_xxxxxxxxxxxxx",
  "headers": {
    "X-Lest-Signature": "sha256={signature}"
  }
}
```

### 5.4 OAuth2 授权流程

```
┌──────────────────────────────────────────────────────────────────┐
│                     OAuth2 授权码流程                              │
│                                                                  │
│  第三方 App              开放平台             用户                 │
│       │                     │                  │                 │
│       │  1. 请求授权         │                  │                 │
│       │─────────────────────►│                  │                 │
│       │                     │  2. 显示授权页      │                 │
│       │                     │──────────────────►│                 │
│       │                     │  3. 用户授权        │                 │
│       │                     │◄──────────────────│                 │
│       │  4. 授权码           │                  │                 │
│       │◄─────────────────────│                  │                 │
│       │  5. 用授权码换 Token  │                  │                 │
│       │─────────────────────►│                  │                 │
│       │  6. Access Token      │                  │                 │
│       │◄─────────────────────│                  │                 │
│       │  7. 调用 API (带Token)│                  │                 │
│       │═══════════════════════╪═══════════════════╪════════►       │
│       │  8. 业务数据响应       │                  │                 │
│       │◄═══════════════════════╪═══════════════════╪════════        │
└──────────────────────────────────────────────────────────────────┘
```

---

## 6. WakaTime 集成架构

### 6.1 架构概述

WakaTime 集成服务（lest-wakapi）负责接收开发者编码心跳数据，支持 WakaTime 官方 API 兼容协议，实现编码时长追踪、任务自动关联和编码活跃度统计。

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                          WakaTime 集成架构                                       │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                  │
│  ┌──────────────────────────────────────────────────────────────────────────┐  │
│  │                         lest-wakapi 服务 (8097)                           │  │
│  │                                                                          │  │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌────────────┐  │  │
│  │  │ WakaTime API │  │  心跳解析器   │  │  任务关联引擎 │  │  汇总计算  │  │  │
│  │  │  兼容层      │  │              │  │              │  │            │  │  │
│  │  │ /plugins/..  │  │  过滤脏数据  │  │  时间窗口匹配 │  │  每日汇总  │  │  │
│  │  │ /users/..    │  │  去重校验    │  │  项目名匹配   │  │  周/月报   │  │  │
│  │  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘  └─────┬──────┘  │  │
│  │         │                 │                  │                │         │  │
│  │         │          ┌──────▼──────────────────▼───────────────┐ │         │  │
│  │         │          │            wakapi_db                     │ │         │  │
│  │         │          │  wakapi_heartbeat │ wakapi_task_link   │ │         │  │
│  │         │          │  wakapi_daily_summary │ wakapi_machine │ │         │  │
│  │         │          └─────────────────────────────────────────┘ │         │  │
│  │         │                                                          │         │  │
│  │         └────────────────────┬──────────────────────────────────┘         │  │
│  └───────────────────────────────┼────────────────────────────────────────────┘  │
│                                  │                                               │
│  ┌───────────────────────────────▼────────────────────────────────────────────┐  │
│  │                          服务间通信                                         │  │
│  │                                                                            │  │
│  │   Kafka: lest-wakapi.heartbeatReceived  ──►  lest-task (任务编码关联)      │  │
│  │   Kafka: lest-wakapi.dailySummary       ──►  lest-notification (活跃度通知)│  │
│  │   OpenFeign: lest-performance ──► lest-wakapi.getUserSummary()            │  │
│  │   OpenFeign: lest-task ──► lest-wakapi.getTaskCodingDuration()           │  │
│  │   OpenFeign: lest-task ──► lest-wakapi.linkHeartbeatToTask()             │  │
│  └────────────────────────────────────────────────────────────────────────────┘  │
│                                                                                  │
│  ┌──────────────────────────────────────────────────────────────────────────┐  │
│  │                        开发者侧                                          │  │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐                   │  │
│  │  │  WakaTime    │  │  第三方 IDE  │  │  自定义心跳   │                   │  │
│  │  │  插件 (官方)  │  │  插件        │  │  上报工具     │                   │  │
│  │  │  API Key: xxx │  │  API Key: xxx│  │  HTTP POST   │                   │  │
│  │  │  base_url 配置│  │             │  │  /api/v1/... │                   │  │
│  │  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘                   │  │
│  └─────────┼─────────────────┼─────────────────┼────────────────────────────┘  │
│            └─────────────────┼─────────────────┘                              │
│                              │                                                 │
│                         HTTPS POST                                            │
│                    lest-wakapi:8097/plugins/..                                │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 6.2 心跳接收流程

#### 6.2.1 兼容 WakaTime 官方 API

lest-wakapi 实现与 WakaTime 官方 API 兼容的心跳接收端点，支持 WakaTime 官方 IDE 插件直接接入：

```
WakaTime 官方 API 端点:
  POST /api/v1/users/current/heartbeats
  Authorization: Basic base64(api_key:)

lest-wakapi 兼容端点:
  POST /api/v1/users/current/heartbeats (兼容官方协议)
  POST /api/v1/heartbeats (扩展协议，支持更多字段)
```

#### 6.2.2 心跳处理流程

```
开发者 IDE 心跳 (每 30s 自动发送)
        │
        ▼
┌─────────────────────────────────────────────────────┐
│  1. 认证与限流                                      │
│     • 验证 API Key / Basic Auth                    │
│     • 检查 Rate Limit (默认 30次/分钟/用户)        │
│     • 记录调用日志                                  │
└──────────────────────┬────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────┐
│  2. 心跳解析与标准化                                 │
│     • 解析 entity (文件路径)                        │
│     • 识别项目名 (从路径提取)                        │
│     • 识别语言 (从文件扩展名)                        │
│     • 识别分支 (从 git 信息或参数)                   │
│     • 过滤无效心跳 (间隔<30s/无效entity)            │
└──────────────────────┬────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────┐
│  3. 去重检查                                         │
│     • 基于 entity + timestamp + user_hash           │
│     • 5分钟内相同心跳只记录一次                       │
└──────────────────────┬────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────┐
│  4. 持久化入库                                      │
│     • 写入 wakapi_heartbeat 表                           │
│     • 异步触发任务关联流程                           │
│     • 触发 Kafka 事件 lest-wakapi.heartbeatReceived │
└──────────────────────┬────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────┐
│  5. 响应                                            │
│     • HTTP 200 OK                                   │
│     • 返回当前编码状态摘要                           │
└─────────────────────────────────────────────────────┘
```

#### 6.2.3 心跳请求格式

```json
// WakaTime 官方兼容格式
{
  "entity": "src/main/java/com/lest/modules/task/service/TaskService.java",
  "type": "file",
  "project": "lest-platform",
  "branch": "feature/new-task",
  "language": "Java",
  "is_write": true,
  "is_debugging": false,
  "is_coding": true,
  "lines": 250,
  "lineno": 45,
  "cursor_position": 120,
  "timestamp": 1748149200,
  "time": 1748149200.123,
  "operating_system": "macos",
  "machine": "MacBook-Pro-Liu",
  "user_agent": "vscode/1.87.0"
}

// lest-wakapi 扩展格式（支持任务关联）
{
  "entity": "/workspace/lest-platform/backend/...",
  "project": "lest-platform",
  "timestamp": 1748149200,
  "language": "Java",
  "task_id": 10086,
  "task_no": "TASK-1234",
  "linked": true
}
```

### 6.3 任务关联机制

#### 6.3.1 关联策略

| 关联方式 | 优先级 | 说明 |
|----------|--------|------|
| 手动指定 | 1 (最高) | 用户主动在 IDE 插件中指定任务 ID |
| 最近任务 | 2 | 基于心跳时间匹配最近一次操作过的任务 |
| 时间窗口 | 3 | 根据编码时段自动关联到时间重叠的任务 |
| 项目匹配 | 4 | 根据项目名自动关联同项目下的任务 |
| AI 推断 | 5 | 基于提交信息、文件名模式智能推断关联任务 |

#### 6.3.2 时间窗口关联算法

```java
public List<Long> matchTasksByTimeWindow(Heartbeat heartbeat) {
    // 1. 获取心跳时间前后的时间窗口
    Instant windowStart = heartbeat.getTimestamp().minusSeconds(300); // 前5分钟
    Instant windowEnd = heartbeat.getTimestamp().plusSeconds(300);   // 后5分钟

    // 2. 查询窗口内的任务操作记录
    List<TaskActivity> activities = taskActivityDao.findByTimeRange(
            heartbeat.getUserId(), windowStart, windowEnd);

    // 3. 过滤有效活动类型
    List<TaskActivity> validActivities = activities.stream()
            .filter(a -> a.getType() == TaskActivityType.EDIT
                    || a.getType() == TaskActivityType.VIEW
                    || a.getType() == TaskActivityType.ASSIGN)
            .toList();

    // 4. 按最近活动时间排序
    return validActivities.stream()
            .sorted(Comparator.comparing(
                    a -> Math.abs(a.getActivityAt().toEpochMilli()
                            - heartbeat.getTimestamp().toEpochMilli())))
            .map(TaskActivity::getTaskId)
            .distinct()
            .limit(3)
            .toList();
}
```

### 6.4 每日汇总计算

每天凌晨 02:00 (UTC+8) 执行定时任务，为所有用户生成编码汇总：

```
每日汇总计算流程:
  定时触发 (02:00 UTC+8)
       │
       ▼
  查询昨日所有有效心跳 (wakapi_heartbeat)
       │
       ▼
  按用户分组，聚合统计:
  • 总编码时长 (total_seconds)
  • 各语言时长 (language_breakdown)
  • 各项目时长 (project_breakdown)
  • 各编辑器时长 (editor_breakdown)
  • 每日小时分布 (hourly_breakdown)
       │
       ▼
  对比编码目标 (goal_seconds)
  计算目标达成率
       │
       ▼
  写入 wakapi_daily_summary 表
       │
       ▼
  检查低活跃度 (昨日<2小时):
  触发 Kafka lest-wakapi.lowActivityAlert 事件
       │
       ▼
  lest-notification 消费事件，发送低活跃度提醒
```

---

## 7. 部署架构

### 7.1 Kubernetes 部署概览

LEST Platform 所有服务均以容器化方式部署在 Kubernetes 集群中，采用 GitOps 工作流（ArgoCD）进行持续部署。

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                              Kubernetes 集群                                     │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                  │
│  ┌─────────────────────────────────────────────────────────────────────────┐   │
│  │                            Ingress Layer                                 │   │
│  │  ┌─────────────────────────────────────────────────────────────────────┐│   │
│  │  │              Apache APISIX (Ingress Controller)                     ││   │
│  │  │  • 路由规则 (Host-based / Path-based)                               ││   │
│  │  │  •  TLS Termination                                                 ││   │
│  │  │  •  WAF (Web Application Firewall)                                  ││   │
│  │  │  •  Rate Limiting                                                   ││   │
│  │  └─────────────────────────────────────────────────────────────────────┘│   │
│  └─────────────────────────────────────────────────────────────────────────┘   │
│                                    │                                              │
│  ┌─────────────────────────────────▼──────────────────────────────────────────┐ │
│  │                           CoreDNS + Service Mesh                           │ │
│  │  ┌────────────┐  ┌────────────┐  ┌────────────┐  ┌────────────┐          │ │
│  │  │  lest-gateway│ │ lest-gateway│ │ lest-gateway│ │ lest-gateway│          │ │
│  │  │  (Pod × 2)  │  │  (Pod × 2)  │  │  (Pod × 2)  │  │  (Pod × 2)  │          │ │
│  │  │  端口:8081  │  │             │  │             │  │             │          │ │
│  │  └────────────┘  └────────────┘  └────────────┘  └────────────┘          │ │
│  │                                                                             │ │
│  │  ┌────────────┐  ┌────────────┐  ┌────────────┐  ┌────────────┐          │ │
│  │  │ lest-auth   │  │ lest-task   │  │ lest-system│  │ lest-project│          │ │
│  │  │ (Pod × 2)   │  │ (Pod × 3)   │  │ (Pod × 2)  │  │ (Pod × 2)  │          │ │
│  │  │ 端口:8082  │  │ 端口:8083  │  │ 端口:8084  │  │ 端口:8086  │          │ │
│  │  └────────────┘  └────────────┘  └────────────┘  └────────────┘          │ │
│  │                                                                             │ │
│  │  ┌────────────┐  ┌────────────┐  ┌────────────┐  ┌────────────┐          │ │
│  │  │ lest-code  │  │ lest-notif │  │ lest-ci    │  │ lest-meeting│          │ │
│  │  │ (Pod × 2)  │  │ (Pod × 2)  │  │ (Pod × 2)  │  │ (Pod × 2)  │          │ │
│  │  │ 端口:8085  │  │ 端口:8087  │  │ 端口:8088  │  │ 端口:8089  │          │ │
│  │  └────────────┘  └────────────┘  └────────────┘  └────────────┘          │ │
│  │                                                                             │ │
│  │  ┌────────────┐  ┌────────────┐  ┌────────────┐  ┌────────────┐          │ │
│  │  │ lest-ai    │  │ lest-release│ │ lest-perf  │  │ lest-wakapi│          │ │
│  │  │ (Pod × 2)  │  │ (Pod × 2)  │  │ (Pod × 2)  │  │ (Pod × 2)  │          │ │
│  │  │ 端口:8090  │  │ 端口:8091  │  │ 端口:8092  │  │ 端口:8097  │          │ │
│  │  └────────────┘  └────────────┘  └────────────┘  └────────────┘          │ │
│  │                                                                             │ │
│  │  ┌────────────┐  ┌────────────┐  ┌────────────┐  ┌────────────┐          │ │
│  │  │ lest-plugin│  │ lest-sched │  │ lest-open  │  │ lest-file  │          │ │
│  │  │ (Pod × 2)  │  │ (Pod × 1)  │  │ (Pod × 2)  │  │ (Pod × 2)  │          │ │
│  │  │ 端口:8093  │  │ 端口:8094  │  │ 端口:8095  │  │ 端口:8096  │          │ │
│  │  └────────────┘  └────────────┘  └────────────┘  └────────────┘          │ │
│  └────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                  │
│  ┌───────────────────────────────┐  ┌────────────────┐  ┌─────────────────┐    │
│  │      MySQL 8.4 (LTS)      │  │  Redis Cluster  │  │  Kafka Cluster  │    │
│  │  18 个 Database               │  │  分片副本集      │  │  3 Broker 副本   │    │
│  │  每服务独立 Database            │  │  Cluster + Sentinel│ │  按 Topic 分区   │    │
│  └───────────────────────────────┘  └────────────────┘  └─────────────────┘    │
│                                                                                  │
│  ┌───────────────────────────────┐  ┌────────────────────────────────────────┐ │
│  │        pgsty/minio (对象存储)            │  │          ArgoCD (GitOps)              │ │
│  │  文件存储（备份到 S3）          │  │  自动同步 Git 部署清单到集群            │ │
│  └───────────────────────────────┘  └────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 7.2 Kubernetes 部署清单示例

#### 7.2.1 Deployment 模板

以下为 lest-task 服务的 Kubernetes 部署配置示例，其他服务结构类似：

```yaml
# lest-task-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: lest-task
  namespace: lest-platform
  labels:
    app: lest-task
    version: v1.0.0
    team: platform
spec:
  replicas: 3
  selector:
    matchLabels:
      app: lest-task
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  template:
    metadata:
      labels:
        app: lest-task
        version: v1.0.0
      annotations:
        prometheus.io/scrape: "true"
        prometheus.io/port: "8083"
        prometheus.io/path: "/actuator/prometheus"
    spec:
      serviceAccountName: lest-task
      securityContext:
        runAsNonRoot: true
        runAsUser: 1000
        fsGroup: 1000
      affinity:
        podAntiAffinity:
          preferredDuringSchedulingIgnoredDuringExecution:
            - weight: 100
              podAffinityTerm:
                labelSelector:
                  matchLabels:
                    app: lest-task
                topologyKey: kubernetes.io/hostname
        nodeAffinity:
          preferredDuringSchedulingIgnoredDuringExecution:
            - weight: 80
              preference:
                matchExpressions:
                  - key: node-type
                    operator: In
                    values:
                      - general-purpose
      containers:
        - name: lest-task
          image: registry.example.com/lest/lest-task:v1.0.0
          imagePullPolicy: Always
          ports:
            - name: http
              containerPort: 8083
              protocol: TCP
          env:
            - name: SPRING_PROFILES_ACTIVE
              value: "prod"
            - name: KAFKA_BOOTSTRAP_SERVERS
              valueFrom:
                configMapKeyRef:
                  name: lest-platform-config
                  key: kafka.servers
            - name: SPRING_DATASOURCE_URL
              valueFrom:
                secretKeyRef:
                  name: task-db-credentials
                  key: jdbc-url
            - name: SPRING_DATASOURCE_USERNAME
              valueFrom:
                secretKeyRef:
                  name: task-db-credentials
                  key: username
            - name: SPRING_DATASOURCE_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: task-db-credentials
                  key: password
            - name: REDIS_HOST
              valueFrom:
                configMapKeyRef:
                  name: lest-platform-config
                  key: redis.host
            - name: OPENTELEMETRY_SERVICE_NAME
              value: "lest-task"
          resources:
            requests:
              cpu: 250m
              memory: 512Mi
            limits:
              cpu: 1000m
              memory: 2Gi
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8083
            initialDelaySeconds: 60
            periodSeconds: 10
            failureThreshold: 3
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8083
            initialDelaySeconds: 30
            periodSeconds: 5
            failureThreshold: 3
          lifecycle:
            preStop:
              exec:
                command: ["/bin/sh", "-c", "sleep 10"]
      volumes:
        - name: timezone
          emptyDir: {}
```

#### 7.2.2 Service 配置

```yaml
# lest-task-service.yaml
apiVersion: v1
kind: Service
metadata:
  name: lest-task
  namespace: lest-platform
  labels:
    app: lest-task
  annotations:
    prometheus.io/scrape: "true"
spec:
  type: ClusterIP
  selector:
    app: lest-task
  ports:
    - name: http
      port: 8083
      targetPort: http
      protocol: TCP
```

#### 7.2.3 HPA（自动扩缩容）

```yaml
# lest-task-hpa.yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: lest-task-hpa
  namespace: lest-platform
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: lest-task
  minReplicas: 2
  maxReplicas: 10
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 70
    - type: Resource
      resource:
        name: memory
        target:
          type: Utilization
          averageUtilization: 80
  behavior:
    scaleDown:
      stabilizationWindowSeconds: 300
      policies:
        - type: Percent
          value: 10
          periodSeconds: 60
    scaleUp:
      stabilizationWindowSeconds: 0
      policies:
        - type: Percent
          value: 100
          periodSeconds: 15
```

### 7.3 ConfigMap 与 Secret

```yaml
# lest-platform-configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: lest-platform-config
  namespace: lest-platform
data:
  kafka.servers: "kafka-0.kafka-headless:9092,kafka-1.kafka-headless:9092,kafka-2.kafka-headless:9092"
  kafka.topic-prefix: "lest-prod"
  redis.host: "redis-cluster.redis.svc.cluster.local"
  redis.port: "6379"
  apisix.admin-key: "apisix-admin-key-xxx"
  minio.endpoint: "http://minio.minio.svc.cluster.local:9000"

---
# lest-platform-secrets.yaml
apiVersion: v1
kind: Secret
metadata:
  name: lest-platform-secrets
  namespace: lest-platform
type: Opaque
stringData:
  jwt.secret: "xxx"
  jwt.public-key: "xxx"
  db-master-password: "xxx"
  redis-password: "xxx"
  minio.access-key: "xxx"
  minio.secret-key: "xxx"
```

---

## 8. 监控与可观测性

### 8.1 可观测性架构

LEST Platform 遵循 **Metrics + Tracing + Logging (MTL)** 三大支柱构建可观测性体系：

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                          可观测性数据流                                           │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                  │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐       │
│  │  Micrometer │    │   Open      │    │   Logback   │    │   Skywalking │       │
│  │  (Metrics)  │    │  Telemetry  │    │  (Logging)  │    │  (Tracing)  │       │
│  │  / Micrometer│   │   (OTLP)    │    │             │    │              │       │
│  └──────┬──────┘    └──────┬──────┘    └──────┬──────┘    └──────┬───────┘       │
│         │                  │                  │                  │               │
│         │         ┌─────────▼──────────────────▼─────────┐        │               │
│         │         │         OTEL Collector               │        │               │
│         │         │  (Metrics/Traces/Logs 统一收集)       │        │               │
│         │         └────────────────┬────────────────────┘        │               │
│         │                          │                               │               │
│  ┌──────▼──────┐         ┌─────────▼─────────┐          ┌──────────▼──────────┐   │
│  │ Prometheus  │         │   Jaeger /         │          │   Loki             │   │
│  │             │         │   Tempo            │          │   (日志存储)       │   │
│  │ 指标存储      │         │   分布式追踪        │          │                    │   │
│  │ AlertManager│         │                   │          │                    │   │
│  └──────┬──────┘         └─────────┬─────────┘          └──────────┬─────────┘   │
│         │                           │                               │               │
│         └───────────────────────────┼───────────────────────────────┘               │
│                                     │                                               │
│                          ┌──────────▼──────────┐                                    │
│                          │   Grafana           │                                    │
│                          │   • Dashboard       │                                    │
│                          │   • Alert           │                                    │
│                          │   • Explore         │                                    │
│                          └─────────────────────┘                                    │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 8.2 核心监控指标

#### 8.2.1 业务指标

| 指标名称 | 类型 | 说明 | 告警阈值 |
|----------|------|------|----------|
| `task.created.total` | Counter | 累计创建任务数 | - |
| `task.completed.total` | Counter | 累计完成任务数 | - |
| `task.open.count` | Gauge | 当前开放任务数 | >10000 |
| `meeting.held.total` | Counter | 累计召开会议数 | - |
| `ci.build.total` | Counter | 累计构建次数 | - |
| `ci.build.duration.seconds` | Histogram | 构建耗时分布 | p99>30min |
| `release.deployed.total` | Counter | 累计发布次数 | - |
| `wakapi.heartbeat.received.total` | Counter | 累计收到心跳数 | - |
| `wakapi.heartbeat.processing.latency` | Histogram | 心跳处理延迟 | p99>500ms |
| `ai.chat.request.total` | Counter | AI 对话请求数 | - |
| `ai.chat.latency` | Histogram | AI 响应延迟 | p99>10s |
| `plugin.active.count` | Gauge | 活跃插件数 | - |

#### 8.2.2 系统指标

| 指标名称 | 采集方式 | 告警阈值 |
|----------|----------|----------|
| JVM 堆内存使用率 | Micrometer/JVM | >85% |
| JVM GC 频率/耗时 | Micrometer/JVM | Full GC >1次/小时 |
| HTTP 请求 QPS | Micrometer/Web | >5000/s |
| HTTP 请求延迟 P99 | Micrometer/Web | >2s |
| 数据库连接池使用率 | HikariCP | >80% |
| Kafka Consumer Lag | Kafka Exporter | >10000 |
| Redis 内存使用率 | Redis Exporter | >80% |
| Pod CPU/内存使用率 | kube-state-metrics | >80% |

### 8.3 分布式追踪

使用 OpenTelemetry + Skywalking 实现全链路追踪：

```java
// 链路追踪示例
@RestController
@RequestMapping("/api/task/v1")
@RequiredArgsConstructor
public class TaskController {

    @GetMapping("/{taskId}")
    public Result<TaskVO> getTask(@PathVariable Long taskId) {
        // 自动生成 traceId 和 spanId
        // 在 HTTP 响应头中返回 X-Trace-Id
        TaskVO task = taskService.getTaskById(taskId);
        return Result.success(task);
    }
}
```

追踪上下文在 OpenFeign 调用中自动传播：

```
追踪链路示例:
  Gateway → lest-task:8083 → lest-wakapi:8097
       │
       ├─ TraceId: trace-a1b2c3d4
       │   ├─ Span: [Gateway] APISIX 路由 (10ms)
       │   ├─ Span: [Gateway→Task] OpenFeign 调用 (50ms)
       │   │   ├─ Span: [Task] getTaskById (30ms)
       │   │   │   └─ Span: [Task→Wakapi] OpenFeign getTaskCodingDuration (20ms)
       │   │   │       └─ Span: [Wakapi] 查询编码时长 (15ms)
       │   │   └─ Span: [Task] 构建响应 (5ms)
       │   └─ Span: [Gateway] 序列化响应 (5ms)
```

---

## 9. 安全架构

### 9.1 安全架构概览

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                              安全架构层次                                         │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                  │
│  ┌───────────────────────────────────────────────────────────────────────────┐ │
│  │  网络层安全                                                               │ │
│  │  • VPC 网络隔离 (dev/staging/prod)                                        │ │
│  │  • Kubernetes NetworkPolicy (服务间白名单)                               │ │
│  │  • TLS 1.3 全链路加密                                                     │ │
│  │  • Apache APISIX WAF 模块                                                │ │
│  └───────────────────────────────────────────────────────────────────────────┘ │
│                                                                                  │
│  ┌───────────────────────────────────────────────────────────────────────────┐ │
│  │  认证与授权                                                               │ │
│  │  • OAuth2 + JWT (Access Token + Refresh Token)                           │ │
│  │  • LDAP/SSO 集成 (lest-auth)                                            │ │
│  │  • RBAC + ABAC 权限模型                                                  │ │
│  │  • API Key 认证 (开放平台)                                               │ │
│  │  • OAuth2 授权码流程 (第三方应用)                                         │ │
│  └───────────────────────────────────────────────────────────────────────────┘ │
│                                                                                  │
│  ┌───────────────────────────────────────────────────────────────────────────┐ │
│  │  数据安全                                                                 │ │
│  │  • 敏感数据加密存储 (AES-256)                                            │ │
│  │  • 数据库 Schema 级权限隔离                                               │ │
│  │  • 审计日志 (CRUD 操作全记录)                                            │ │
│  │  • 数据脱敏 (日志/响应中敏感字段屏蔽)                                      │ │
│  │  • 备份加密 (备份文件 AES 加密后存储)                                     │ │
│  └───────────────────────────────────────────────────────────────────────────┘ │
│                                                                                  │
│  ┌───────────────────────────────────────────────────────────────────────────┐ │
│  │  应用安全                                                                 │ │
│  │  • 输入验证 (JSR-303 + 自定义校验器)                                      │ │
│  │  • SQL 注入防护 (参数化查询)                                             │ │
│  │  • XSS 防护 (HTML 转义)                                                 │ │
│  │  • CSRF 防护 (Token 校验)                                                │ │
│  │  • Rate Limiting (接口限流)                                              │ │
│  │  • 依赖安全扫描 (Trivy + Snyk)                                           │ │
│  └───────────────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 9.2 认证与授权流程

#### 9.2.1 JWT Token 结构

```json
{
  "header": {
    "alg": "RS256",
    "typ": "JWT",
    "kid": "key-2026-001"
  },
  "payload": {
    "sub": "user-2001",
    "userId": 2001,
    "username": "liuyue",
    "roles": ["ROLE_DEVELOPER", "ROLE_TEAM_LEAD"],
    "tenantId": "tenant-001",
    "permissions": [
      "task:read",
      "task:write",
      "task:assign",
      "code:review",
      "ci:view",
      "performance:view"
    ],
    "scope": ["openid", "profile", "email"],
    "iat": 1748149200,
    "exp": 1748152800,
    "iss": "https://lest.example.com/auth",
    "aud": ["lest-platform", "lest-open"]
  },
  "signature": "RSASHA256(...)"
}
```

#### 9.2.2 多因素认证（MFA）

支持 TOTP（Google Authenticator）二次验证：

```
登录流程:
  用户名 + 密码 (factor 1)
       │
       ▼
  验证成功，检查用户 MFA 状态
       │
       ├── MFA 已启用 → 要求输入 TOTP Code
       │                    │
       │                    ▼
       │               验证 TOTP (30秒窗口)
       │                    │
       │                    ├── 验证通过 → 签发 JWT
       │                    └── 验证失败 → 拒绝登录
       │
       └── MFA 未启用 → 直接签发 JWT
```

### 9.3 审计日志

所有敏感操作均记录审计日志：

| 日志类型 | 记录内容 | 保留时间 |
|----------|----------|----------|
| 登录日志 | 用户/时间/IP/结果 | 1年 |
| 权限变更日志 | 操作用户/目标用户/变更内容/时间 | 永久 |
| 数据变更日志 | 实体类型/实体ID/变更前后/操作人 | 1年 |
| API 调用日志 | 调用方/接口/参数/响应/耗时 | 90天 |
| 文件操作日志 | 操作人/文件名/操作类型/时间 | 1年 |
| 插件操作日志 | 插件ID/操作类型/调用方/时间 | 1年 |

---

## 10. API 版本管理

### 10.1 版本管理策略

LEST Platform 采用 **URL 路径版本化 + Header 版本协商** 的双轨策略：

```
URL 路径版本 (主版本):
  /api/v1/task/...
  /api/v2/task/...

Header 版本协商 (次版本/兼容性):
  Accept: application/vnd.lest.api+json; version=1.2
```

### 10.2 版本演进规则

| 版本类型 | 变更内容 | 兼容性保证 |
|----------|----------|------------|
| **主版本 (v1 → v2)** | 破坏性变更（删除字段/重命名/改变类型） | 不兼容，需升级客户端 |
| **次版本 (v1.1 → v1.2)** | 新增字段/新增接口 | 向后兼容，老客户端可继续使用 |
| **补丁版本 (v1.1.0 → v1.1.1)** | Bug 修复/性能优化 | 完全兼容 |

### 10.3 版本生命周期

```
v1 版本生命周期:
  2024-01-01  v1.0.0 发布 (GA)
  2024-06-01  v1.1.0 新增功能 (向后兼容)
  2025-01-01  v1.2.0 新增功能 (向后兼容)
  2025-07-01  v2.0.0 发布，v1 进入维护模式
  2026-01-01  v1 停止维护，进入安全修复期（仅修复关键安全漏洞）
  2026-07-01  v1 正式停用，所有流量迁移到 v2
```

### 10.4 接口版本控制器

```java
@RestController
@RequestMapping("/api/{version}/task")
public class TaskController {

    @GetMapping("/{taskId}")
    @ApiVersion(group = ApiVersionConstant.V1_GROUP)
    public Result<TaskVO> getTaskV1(@PathVariable Long taskId) {
        return Result.success(taskService.getTaskById(taskId));
    }

    @GetMapping("/{taskId}")
    @ApiVersion(group = ApiVersionConstant.V2_GROUP)
    public Result<TaskVOV2> getTaskV2(@PathVariable Long taskId) {
        return Result.success(taskService.getTaskByIdV2(taskId));
    }
}
```

### 10.5 OpenAPI 规范

每个服务独立维护 OpenAPI 文档，通过 Swagger UI 提供交互式文档：

| 服务 | OpenAPI 文档地址 | Swagger UI |
|------|-----------------|------------|
| lest-auth | /swagger-ui.html | ✓ |
| lest-task | /swagger-ui.html | ✓ |
| lest-code | /swagger-ui.html | ✓ |
| lest-wakapi | /swagger-ui.html | ✓ |
| lest-open | /swagger-ui.html | ✓ |
| 其他服务 | /swagger-ui.html | ✓ |

---

## 附录

### A. 术语表

| 术语 | 说明 |
|------|------|
| MTL | Metrics + Tracing + Logging，可观测性三大支柱 |
| SPI | Service Provider Interface，插件扩展机制 |
| ABAC | Attribute-Based Access Control，基于属性的访问控制 |
| RBAC | Role-Based Access Control，基于角色的访问控制 |
| TOTP | Time-based One-time Password，基于时间的一次性密码 |
| MTTL | Mean Time To Recovery，平均故障恢复时间 |
| SLO | Service Level Objective，服务等级目标 |
| SLI | Service Level Indicator，服务等级指标 |
| HPA | Horizontal Pod Autoscaler，水平 Pod 自动扩缩容器 |

### B. 相关文档

| 文档 | 路径 |
|------|------|
| 项目需求文档 | `docs/PRD/V1.0/` |
| 数据库设计文档 | `docs/DATABASE.md` |
| API 接口文档 | `docs/API.md` |
| 部署指南 | `docs/DEPLOYMENT.md` |
| 开发规范 | `docs/DEVELOPMENT.md` |

### C. 成熟开源组件使用策略

LEST Platform 遵循「**优先使用成熟开源组件，避免重复造轮子**」原则。以下是技术选型说明：

#### C.1 中间件选型

| 用途 | 选用组件 | 版本 | 选型理由 |
|------|----------|------|----------|
| 服务注册与发现 | **Nacos** | 3.2.1 | 阿里开源，一站式服务发现+配置管理，Spring Cloud Alibaba 原生支持，3.x 支持 AI Registry（A2A Agent / Skill / Prompt） |
| 消息队列 | **Apache Kafka** | 4.3.0 | Apache 顶级项目，事件驱动架构首选，仅 KRaft 模式（ZooKeeper 已移除），支持 OAuth 客户端断言 |
| 数据库 | **MySQL** | 8.4.9 | 全球最流行开源关系数据库，成熟稳定 |
| 缓存 | **Redis** | 8.6.3 | NoSQL 缓存首选，支持丰富数据结构 |
| 对象存储 | **pgsty/minio** | RELEASE.2026-04-17 | 上游已归档（2026-04），社区 Fork 维护二进制 ⚠️ 单点维护风险，需监控上游活跃度 |
| API 网关 | **Spring Cloud Gateway** | 内置于 SC 2025.0.2 (SCG 4.3.4) | 响应式网关，与 Spring Cloud 深度集成 |
| 服务间调用 | **OpenFeign** | 内置于 SC | 声明式 HTTP 客户端，简洁易用 |
| 熔断限流 | **Resilience4j** + **Bucket4j** | 2.4.0 / 8.18.0 | 轻量级，函数式 API，无外部依赖 |
| ORM | **MyBatis-Plus** | 3.5.16 | 国产精品，CRUD 增强+分页+代码生成 |
| JWT | **jjwt** | 0.13.0 | Java JWT 标准库，RFC 7519/7523 实现，支持 JWKS 端点与零停机密钥轮换 |
| 定时任务 | **外部 POS + Kafka 事件驱动** | 外部定时触发器发送 Kafka 消息 |

#### C.2 自研组件说明

以下组件基于成熟开源库构建，业务逻辑为自研：

| 组件 | 自研范围 | 底层依赖 |
|------|----------|----------|
| lest-wakapi | 心跳解析、任务关联、编码统计汇总算法 | Spring Boot + Redis + Kafka |
| lest-ai | AI 网关路由、AI Provider 扩展点 | LangChain4j / Spring AI（待引入） |
| lest-notification | 通知渠道路由、模板引擎 | Spring Boot + Kafka |
| JWT 工具 | 统一 JWT 工具类 (`JwtUtils`) | jjwt 0.13.0 |

#### C.3 定时任务方案

LEST Platform 的定时任务通过**外部 POS 容器 + Kafka 消息驱动**实现，无需独立的调度服务：

- 外部定时触发器（cron / POS）按计划发送 Kafka 消息（如 `DAILY_REPORT`、`WEEKLY_REPORT`、`PROJECT_RISK_CHECK`）
- `lest-notification` 消费消息后，调用 `lest-ai` 处理业务逻辑
- 无需 XXL-Job Admin 管理后台，无额外运维负担
- 无需独立服务注册中心（各微服务已通过 Nacos 实现服务注册与发现）

#### C.4 技术演进路线

| 阶段 | 内容 | 说明 |
|------|------|------|
| 当前 | 外部 POS + Kafka 事件驱动定时任务 | 外部定时器触发 Kafka 消息 |
| 当前 | Nacos 服务注册与发现 | 所有微服务通过 Nacos 自动注册 |
| 当前 | pgsty/minio 对象存储 | 上游归档，社区 Fork，监控上游活跃度 |
| V2.0 | 引入 **LangChain4j** | 统一 AI Provider 管理 |
| V2.0 | 引入 **Spring Cloud Stream** | 统一消息处理 |
| V2.0 | 评估对象存储替代方案 | 如 pgsty/minio 维护者长期不活跃则迁移 |
| V3.0 | 考虑迁移部分服务到 **GraalVM Native Image** | 启动速度优化 |

#### C.5 技术风险登记册

| 风险 | 等级 | 当前缓解措施 |
|------|------|-------------|
| pgsty/minio 单点维护者风险 | **中** | 持续监控 Fork 活跃度（commits、releases 频率）；每月检查 GitHub stars 变化；提前制定 SeaweedFS 迁移预案 |
| Kafka 4.x 跨度升级风险 | **低** | 开发/测试环境先行验证；Kafka KRaft 模式已内置于 docker-compose |
| Spring Boot 4 迁移风险 | **低** | 已在开发环境验证；Spring Boot 3.5 作为桥接版本消除了大部分 breaking changes |
| Nacos 3.x 配置格式兼容 | **低** | 3.x 向前兼容 2.x 配置；无 bootstrap 依赖（已移除） |
