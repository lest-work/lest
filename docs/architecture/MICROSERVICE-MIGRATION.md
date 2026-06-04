# LEST Platform — 微服务架构调整方案

> **文档目的**：分析现有若依（RuoYi-Vue-Plus）架构与 Jira 对齐 PRD 之间的差距，提出具体的迁移调整方案，并给出详细的微服务架构图。
>
> **前置文档**：
> - [全栈架构设计](./BACKEND-FRONTEND-ARCHITECTURE.md)
> - [Core V1.0 认证系统](../1-prd/core/V1.0/认证系统与系统管理.md) — 明确删除 OA 功能
> - [Core V2.0 任务管理](../1-prd/core/V2.0/任务管理.md) — EAV + Scheme 体系
>
> **版本**: V1.1 | **状态**: 设计完成 | **最后更新**: 2026-06-03

---

## 1. 现有架构分析

### 1.1 当前模块清单

```
backend/
├── pom.xml                           # Maven 多模块（Spring Boot 4.0.6, Java 21）
├── lest-common/                     # 公共类库（9 个子模块）
│   ├── lest-common-core/            # 核心工具：JWT/MyBatis/PageHelper
│   ├── lest-common-security/        # 安全：鉴权/权限/Token
│   ├── lest-common-log/            # 日志
│   ├── lest-common-redis/          # Redis
│   ├── lest-common-swagger/        # Swagger
│   ├── lest-common-datasource/     # 动态数据源（Druid）
│   ├── lest-common-datascope/      # 数据权限范围过滤
│   ├── lest-common-sensitive/       # 数据脱敏
│   └── lest-common-seata/          # 分布式事务
├── lest-api/                        # Feign 客户端（1 个子模块）
│   └── lest-api-system/           # 调用 lest-system 的 Feign 接口
├── lest-gateway/                    # API 网关（SCG，无 DB）
├── lest-modules/                    # 业务微服务（13 个模块）
│   ├── lest-system/               # 系统管理（MySQL）⚠️ OA 风格
│   ├── lest-project/              # 项目管理（MySQL）
│   ├── lest-task/                 # 任务管理（MySQL + Kafka）
│   ├── lest-release/              # 发布管理（MySQL）
│   ├── lest-file/                 # 文件服务（MinIO）
│   ├── lest-job/                  # 定时任务（Quartz/MySQL）
│   ├── lest-notification/         # 通知（无 DB）⚠️
│   ├── lest-ai/                   # AI 服务（无 DB）⚠️
│   ├── lest-meeting/              # 会议管理（无 DB）⚠️
│   ├── lest-open/                 # 开放平台（无 DB）⚠️
│   ├── lest-performance/          # 绩效服务（无 DB）⚠️
│   ├── lest-plugin/               # 插件管理（无 DB）⚠️
│   └── lest-wakapi/               # WakaTime（无 DB）⚠️
└── lest-visual/                    # 监控（Spring Boot Admin）
```

### 1.2 与 PRD 的核心差距

| 问题 | 现有架构 | PRD 要求 | 严重度 |
|------|---------|---------|--------|
| **OA 遗留** | `lest-system` 包含用户 CRUD/部门/岗位/字典/菜单管理 | 删除全部 OA 功能，改为邀请制 + project_member | 🔴 Critical |
| **Auth 无 DB** | `lest-auth` 无数据库，只有 Token 管理 | 需要 `auth_db`（user + auth_token 表） | 🔴 Critical |
| **数据丢失** | 删除 `sys_menu`、`sys_dict` 后系统无法运行 | 迁移方案：固定菜单 + EAV 自定义字段 | 🔴 Critical |
| **服务无 DB** | 6 个插件服务无数据库 | 每个服务独立数据库（`xxx_db`） | 🔴 Critical |
| **模块名不一致** | `lest-open`/`lest-performance` | 应为 `lest-openapi`/`lest-report` | 🟡 Medium |
| **Kafka 仅 Task** | 仅 `lest-task` 使用 Kafka | 所有跨服务通信走 Kafka 事件总线 | 🟡 Medium |
| **前端单体** | `frontend-pc` 是传统 Vue 单体 | 需要插件化前端架构 | 🟡 Medium |
| **无事件总线** | 服务间同步 Feign 调用 | 改为事件驱动（Kafka） | 🟡 Medium |
| **无 Scheme 表** | task/project 无 Scheme 相关表 | 需要完整的 EAV + Scheme 表 | 🟡 Medium |
| **权限体系** | 基于 `sys_menu` 的全局菜单权限 | 改为项目级 Permission Scheme | 🟡 Medium |

---

## 2. 调整方案：Core 层迁移

### 2.1 lest-system → lest-auth + lest-project 拆分

现有 `lest-system` 是最大的技术债。它混合了：

```
lest-system（当前，13000+ 行）
├── 用户管理（SysUser）      ← → lest-auth（user 表）
├── 角色管理（SysRole）      ← → 删除（改用 project_member）
├── 菜单管理（SysMenu）      ← → 删除（固定菜单）
├── 部门管理（SysDept）      ← → 删除（无机构层级）
├── 岗位管理（SysPost）      ← → 删除（无岗位概念）
├── 字典管理（SysDict*）    ← → 删除（改用 EAV 自定义字段）
├── 参数配置（SysConfig）   ← → lest-auth（平台配置）
├── 通知管理（SysNotice）   ← → lest-notification
├── 操作日志（SysOperLog）  ← → lest-auth（审计日志）
├── 登录日志（SysLogininfor）← → lest-auth
├── 在线用户（SysUserOnline）← → lest-auth（Redis 会话）
└── 个人中心（SysProfile）  ← → lest-auth
```

**迁移步骤**：

```
Phase 1: 新建 lest-auth（独立数据库 auth_db）
├── 迁移 user 表（从 sys_user → user）
├── 迁移 auth_token 表（或改为 stateless JWT + Redis 黑名单）
├── 迁移 sys_config 表（平台配置）
├── 迁移 sys_notice 表（站内通知）
├── 删除: sys_role/user_role/sys_menu/sys_org/sys_post/sys_dict*/sys_logininfor/sys_operlog
└── 废弃: DataScope 过滤器（基于部门的数据权限）

Phase 2: 重构 lest-project（project_member 表）
├── 新增 project_member 表
├── 删除 sys_role/sys_menu 的所有 Service/Controller/Mapper
├── 新增项目级角色 API（Admin/Dev/Viewer/Lead）
└── 迁移完成后的 lest-system 仅保留: SysConfigController + SysNoticeController

Phase 3: 清理 lest-common
├── 删除 lest-common-datascope（数据权限范围）
├── 删除 lest-common-sensitive（数据脱敏相关）
├── 重构 lest-common-security（移除 @DataScope 注解）
└── 新增 lest-common-mq（Kafka 事件封装）
```

### 2.2 新增 Core 表设计

```sql
-- auth_db（新建）
CREATE TABLE `user` (...);           -- 从 sys_user 迁移
CREATE TABLE `token_blacklist` (...); -- JWT 黑名单（Redis 同步）
CREATE TABLE `refresh_token` (...);    -- Refresh Token（用于续期）
-- 删除: sys_role, user_role, sys_menu, sys_menu_role, sys_org, sys_post, sys_dict*, sys_logininfor, sys_operlog

-- system_db（从 lest-system 保留）
CREATE TABLE `sys_config` (...);     -- 保留（平台配置）
CREATE TABLE `sys_notice` (...);     -- 保留（站内通知）
```

### 2.3 权限体系重构

现有基于 `sys_menu` 的全局菜单权限：

```java
// 现有（OA 风格）— 必须删除
@DataScope("dept_id")          // 基于部门的行级数据权限
@RequiresPermissions("system:user:list")
public class SysUserController { ... }
```

改为项目级 Permission Scheme：

```java
// 重构后（Jira 风格）
@RequireProjectRole(ProjectRole.ADMIN)   // 项目管理员角色
@RequireProjectRole(ProjectRole.DEV)      // 项目开发者角色
public class ProjectController { ... }
```

---

## 3. 调整方案：Plugin 层补全

### 3.1 现状：骨架服务，无实际功能

| 服务 | 状态 | 需要的 DB |
|------|------|----------|
| `lest-notification` | 骨架 | `notification_db` |
| `lest-ai` | 骨架 | `ai_db` |
| `lest-meeting` | 骨架 | `meeting_db` |
| `lest-open` | 骨架 | `openapi_db` |
| `lest-performance` | 骨架 | `report_db` |
| `lest-plugin` | 骨架 | `plugin_db` |
| `lest-wakapi` | 骨架 | `wakapi_db` |

每个服务需要补全：

```
示例：lest-notification
├── NotificationController
├── NotificationService
├── NotificationMapper
├── notification_db 数据库
│   ├── notification 表
│   ├── notification_subscription 表
│   └── notification_template 表
├── Kafka 事件消费者（订阅 task/project 等事件）
├── Email 发送（Spring Mail + SMTP）
└── WebSocket 推送（实时通知）
```

### 3.2 模块重命名

| 旧名 | 新名 | 理由 |
|------|------|------|
| `lest-open` | `lest-openapi` | 与 PRD 插件名一致 |
| `lest-performance` | `lest-report` | 与 PRD 插件名一致，且 V1.0 绩效 = V2.0 报表中心 |

---

## 4. 事件驱动架构改造

### 4.1 从同步调用改为 Kafka 事件总线

**现状**：服务间通过 Feign 同步调用。

```java
// 现有同步调用（紧耦合）
@Service
public class TaskServiceImpl {
    @Autowired private ProjectFeignClient projectClient;  // 同步调用

    public void assign(Long taskId, Long userId) {
        task.assign(userId);
        taskRepo.save(task);
        // 同步调用：阻塞、高耦合
        projectClient.recordActivity(task.getProjectId(), "task_assigned");
    }
}
```

**改造后**：Kafka 事件驱动（松耦合）。

```java
// 改造后（事件驱动）
@Service
public class TaskServiceImpl {
    @Autowired private EventPublisher publisher;

    public void assign(Long taskId, Long userId) {
        task.assign(userId);
        taskRepo.save(task);
        // 发布事件，解耦
        publisher.publish("task-assigned", new TaskAssignedEvent(taskId, userId, task.getProjectId()));
    }
}

// lest-project 消费（消费者组：lest-project）
@KafkaListener(topics = "task-assigned", groupId = "lest-project")
public void onTaskAssigned(TaskAssignedEvent event) {
    projectService.recordActivity(event.projectId(), "task_assigned");
}

// lest-notification 消费（消费者组：lest-notification）
@KafkaListener(topics = "task-assigned", groupId = "lest-notification")
public void onTaskAssigned(TaskAssignedEvent event) {
    notificationService.notifyUser(event.assigneeId(), "TASK_ASSIGNED");
}
```

### 4.2 Kafka 消费者组隔离说明

**关键原则**：
1. **同一 Topic 可被多个消费者组独立消费**：每个消费者组有自己的 offset，互不干扰
2. **Producer 和 Consumer 必须使用不同的 Topic**：避免同一服务内 Producer 产生的消息被自己的 Consumer 消费（自触发问题）
3. **消费者组命名规范**：`{service-name}`，确保每个服务独立消费

| Topic | 生产者 | 消费者组 | 说明 |
|-------|--------|----------|------|
| `task-created` | lest-task | lest-notification, lest-project, lest-ai | lest-task 生产，3 个消费者组各自独立处理 |
| `task-assigned` | lest-task | lest-notification, lest-project | lest-task 生产，lest-notification 和 lest-project 各自处理 |
| `task-updated` | lest-task | lest-notification | — |
| `task-deleted` | lest-task | lest-notification | — |
| `task-status-changed` | lest-task | lest-notification, lest-project | — |
| `project-created` | lest-project | lest-notification | — |
| `project-member-added` | lest-project | lest-notification | — |
| `sprint-started` | lest-project | lest-notification | — |
| `sprint-completed` | lest-project | lest-notification, lest-report | — |
| `release-published` | lest-release | lest-notification | — |
| `meeting-scheduled` | lest-meeting | lest-notification | — |

### 4.3 完整事件清单

| 事件 Topic | 生产者 | 消费者 |
|-----------|--------|--------|
| `task-created` | lest-task | lest-notification, lest-project, lest-ai |
| `task-updated` | lest-task | lest-notification |
| `task-deleted` | lest-task | lest-notification |
| `task-assigned` | lest-task | lest-notification, lest-project |
| `task-status-changed` | lest-task | lest-notification, lest-project |
| `project-created` | lest-project | lest-notification |
| `project-member-added` | lest-project | lest-notification |
| `sprint-started` | lest-project | lest-notification |
| `sprint-completed` | lest-project | lest-notification, lest-report |
| `release-published` | lest-release | lest-notification |
| `meeting-scheduled` | lest-meeting | lest-notification |

---

## 5. 详细微服务架构图

### 5.1 若依原版架构（现状）

```
┌─────────────────────────────────────────────────────────────────────┐
│                           外部请求                                    │
└──────────────────────────────────┬──────────────────────────────────┘
                                   │ HTTP(S)
                    ┌──────────────▼──────────────┐
                    │      Nacos                 │
                    │   (服务注册与配置中心)        │
                    │   v3.0.2                  │
                    └──────────────┬──────────────┘
                                   │
┌──────────────────────────────────▼──────────────────────────────────┐
│                        Spring Cloud Gateway                          │
│                        (lest-gateway :8080)                         │
│   JWT 验证 · 路由转发 · 限流熔断                                     │
└──────────────────────────────────┬──────────────────────────────────┘
                                   │
        ┌───────────┬───────────────┼───────────────┬───────────────┐
        │           │               │               │               │
        ▼           ▼               ▼               ▼               ▼
┌────────────┐ ┌────────────┐ ┌────────────┐ ┌────────────┐ ┌────────────┐
│ lest-auth  │ │ lest-system│ │ lest-project│ │ lest-task  │ │ lest-file  │
│ :8096     │ │ :8081     │ │ :8082      │ │ :8083      │ │ :8084      │
│            │ │            │ │            │ │            │ │            │
│  Token    │ │ ⚠️ OA 功能 │ │  Project   │ │  Task      │ │  File      │
│  管理      │ │ 用户/角色/ │ │  迭代/里程碑│ │  看板/标签 │ │  MinIO     │
│  (无DB)   │ │ 部门/字典  │ │            │ │  +Kafka    │ │            │
│            │ │            │ │            │ │            │ │            │
│  MySQL    │ │  MySQL     │ │  MySQL     │ │  MySQL     │ │  MinIO     │
│ (无DB!!)  │ │  Druid     │ │            │ │            │ │            │
└────────────┘ └────────────┘ └────────────┘ └────────────┘ └────────────┘
                                   │
                    ┌───────────────┼───────────────┐
                    ▼               ▼               ▼
            ┌────────────┐ ┌────────────┐ ┌────────────┐
            │lest-release│ │lest-notification│ lest-ai │
            │ :8087     │ │ :8088       │ │ :8089     │
            │            │ │  ⚠️ 无DB    │ │  ⚠️ 无DB │
            └────────────┘ └────────────┘ └────────────┘
                    │
        ┌───────────┼───────────┬───────────┬───────────┐
        ▼           ▼           ▼           ▼           ▼
┌────────────┐ ┌────────────┐ ┌────────────┐ ┌────────────┐ ┌────────────┐
│lest-meeting│ │lest-open   │ │lest-performance│ │lest-plugin│ │lest-wakapi│
│ :8090     │ │ :8091     │ │ :8092     │ │ :8093     │ │ :8094     │
│ ⚠️ 无DB   │ │ ⚠️ 无DB   │ │ ⚠️ 无DB   │ │ ⚠️ 无DB   │ │ ⚠️ 无DB   │
└────────────┘ └────────────┘ └────────────┘ └────────────┘ └────────────┘

┌────────────────────────────────────────────────────────────────────┐
│                         lest-common（9 个子模块）                      │
│ lest-common-core / security / log / redis / swagger / datasource /  │
│ datascope / sensitive / seata                                       │
└────────────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────────────┐
│                    lest-api-system（Feign 客户端）                    │
│ RemoteUserService / RemoteFileService / RemoteLogService             │
└────────────────────────────────────────────────────────────────────┘
```

### 5.2 目标架构（Jira 对齐，迁移后）

```
┌────────────────────────────────────────────────────────────────────────────┐
│                         CDN / WAF / APISIX                               │
│                      (HTTPS 终结 · 全局限流 · WAF)                       │
└────────────────────────────────────────┬───────────────────────────────┘
                                          │ :443
┌─────────────────────────────────────────▼───────────────────────────────┐
│                       Kubernetes Ingress / APISIX                        │
│             JWT 验证 · 路由转发 · 熔断限流 · 请求日志                      │
└────────────────────────────────────────┬───────────────────────────────┘
                                          │ :8080
         ┌────────────────────────────────┼────────────────────────────────┐
         │                                │                                │
         │                    ┌────────────▼───────────┐                  │
         │                    │     Nacos             │                  │
         │                    │ (服务注册 + 配置中心)  │                  │
         │                    │     v3.2.1            │                  │
         │                    └────────────┬───────────┘                  │
         │                                 │                               │
         │              ┌──────────────────┼──────────────────┐             │
         │              │                  │                  │             │
┌────────▼────────┐     │     ┌───────────▼───────────┐    │  ┌────────▼────────┐
│  lest-gateway   │     │     │    lest-auth           │    │  │   Sentinel       │
│  (SCG 4.3.4)   │     │     │    :8096              │    │  │   (限流熔断)     │
│  JWT · 路由     │     │     │                       │    │  │   :8858          │
│  限流 · 熔断    │     │     │  auth_db ────────────┐│    │  └─────────────────┘
└────────┬────────┘     │     │  user                 ││    │
         │                │     │  token_blacklist       ││    │
         ├────────────────┼─────┤  refresh_token         ││    │
         │                │     │  sys_config            ││    │
         │                │     │  sys_notice            ││    │
         │                │     │                       ││    │
         │                │     │  JWT 签发/验证        ││    │
         │                │     │  用户邀请链接          ││    │
         │                │     │  MFA / 2FA           ││    │
         │                │     │  审计日志              ││    │
         │                │     │  项目邀请（project_   ││    │
         │                │     │  member）              ││    │
         │                │     └───────────────────────┘│    │
         │                │                              │    │
         │    ┌───────────▼───────────────────────────┐│    │
         │    │           lest-project                 ││    │
         │    │           :8082                        ││    │
         │    │                                       ││    │
         │    │  project_db ────────────────────────┐││    │
         │    │  project                            │││    │
         │    │  project_member                     │││    │
         │    │  iteration                          │││    │
         │    │  project_version                     │││    │
         │    │  board / board_column / board_swim_lane││    │
         │    │  board_card_color_rule               │││    │
         │    │                                     │││    │
         │    │  Project CRUD · 成员管理            │││    │
         │    │  Sprint/迭代 · 里程碑/版本         │││    │
         │    │  敏捷看板（V3.0）                   │││    │
         │    └─────────────────────────────────────┘│    │
         │                                              │    │
         │    ┌───────────────────────────────────────▼──────┐    │
         │    │              lest-task                         │    │
         │    │              :8083                             │    │
         │    │                                                 │    │
         │    │  task_db ─────────────────────────────────────┐ │    │
         │    │  task                                        │ │    │
         │    │  task_link (blocks/relates_to/...)          │ │    │
         │    │  task_comment                                │ │    │
         │    │  task_attachment                            │ │    │
         │    │  task_change_history                        │ │    │
         │    │  task_worklog                               │ │    │
         │    │  label                                      │ │    │
         │    │  custom_field_definition (EAV)              │ │    │
         │    │  custom_field_value                         │ │    │
         │    │  workflow_definition / workflow_instance    │ │    │
         │    │  screen / field_configuration               │ │    │
         │    │  issue_type_scheme / issue_type_screen_scheme│ │    │
         │    │                                             │ │    │
         │    │  Task CRUD · 看板视图 · 甘特图              │ │    │
         │    │  EAV 自定义字段 · Screen/Scheme 渲染         │ │    │
         │    │  工作流引擎 · Issue Link · 批量操作          │ │    │
         │    │  Webhook · 变更历史                         │ │    │
         │    └─────────────────────────────────────────────┘ │    │
         │                                                        │
         │    ┌──────────────────────────────────────────────────▼───┐ │
         │    │              lest-notification                         │ │
         │    │              :8088                                    │ │
         │    │                                                        │ │
         │    │  notification_db ─────────────────────────────────┐  │ │
         │    │  notification                                        │  │ │
         │    │  notification_subscription                          │  │ │
         │    │  notification_template                              │  │ │
         │    │  notification_channel                               │  │ │
         │    │                                                        │  │ │
         │    │  📥 Kafka 事件消费者（订阅所有领域事件）                │  │ │
         │    │  📧 邮件发送（Spring Mail + SMTP）                    │  │ │
         │    │  🔔 WebSocket 推送（实时通知）                        │  │ │
         │    │  📱 IM 渠道扩展点（NOTIFICATION_CHANNEL 扩展点）     │  │ │
         │    └────────────────────────────────────────────────────────┘ │
         │                                                             │
         │    ┌──────────────────────────────────────────────────▼──────┐ │
         │    │              lest-file                                  │ │
         │    │              :8084                                      │ │
         │    │                                                        │ │
         │    │  file_db ───────────────────────────────────────────┐   │ │
         │    │  file_metadata                                      │   │ │
         │    │                                                        │   │ │
         │    │  MinIO / S3 对象存储                                  │   │ │
         │    └────────────────────────────────────────────────────────┘ │
         │                                                             │
         │    ┌──────────────────────────────────────────────────▼──────┐ │
         │    │              lest-ai                                     │ │
         │    │              :8089                                      │ │
         │    │                                                        │ │
         │    │  ai_db ─────────────────────────────────────────────┐  │ │
         │    │  ai_provider_config                                  │  │ │
         │    │  ai_conversation                                     │  │ │
         │    │  ai_prompt_template                                  │  │ │
         │    │                                                        │  │ │
         │    │  AI 网关路由（OpenAI / Claude / 通义 / DeepSeek）       │  │ │
         │    │  LangChain4j 集成 · Memory 管理 · RAG                  │  │ │
         │    └────────────────────────────────────────────────────────┘ │
         │                                                             │
         │    ┌──────────────────────────────────────────────────▼──────┐ │
         │    │              lest-plugin                                │ │
         │    │              :8093                                      │ │
         │    │                                                        │ │
         │    │  plugin_db ─────────────────────────────────────────┐  │ │
         │    │  plugin_package                                       │  │ │
         │    │  plugin_instance                                     │  │ │
         │    │  extension_point_registry                            │  │ │
         │    │                                                        │  │ │
         │    │  插件安装/卸载/启用/禁用                               │  │ │
         │    │  manifest.json 验证 · Spring 上下文加载               │  │ │
         │    │  Extension Point 路由（前端 Fragment 注册）            │  │ │
         │    └────────────────────────────────────────────────────────┘ │
         └─────────────────────────────────────────────────────────────┘
                                          │
                    ┌─────────────────────┼─────────────────────┐
                    │                     │                     │
                    ▼                     ▼                     ▼
         ┌──────────────────┐ ┌──────────────────┐ ┌──────────────────┐
         │ lest-code        │ │ lest-meeting     │ │ lest-release     │
         │ :8090            │ │ :8091            │ │ :8092            │
         │                  │ │                  │ │                  │
         │ code_db ────────┐ │ meeting_db ──────┐ │ release_db ──────┐ │
         │ code_repository │ │ meeting          │ │ release_plan     │ │
         │ code_commit      │ │ meeting_agenda   │ │ release_artifact │ │
         │ code_merge_req   │ │ meeting_summary  │ │ change_record    │ │
         │                  │ │                  │ │                  │
         │ Git 仓库集成      │ │ 会议管理          │ │ 发布计划/制品管理 │ │
         │ MR/PR 追踪       │ │ 会议纪要          │ │ 变更记录          │ │
         └──────────────────┘ └──────────────────┘ └──────────────────┘
                    │                     │                     │
                    ▼                     ▼                     ▼
         ┌──────────────────┐ ┌──────────────────┐ ┌──────────────────┐
         │ lest-report      │ │ lest-worklog     │ │ lest-openapi     │
         │ :8094            │ │ :8095            │ │ :8096            │
         │                  │ │                  │ │                  │
         │ report_db ────────┐ │ worklog_db ──────┐ │ openapi_db ──────┐ │
         │ report_definition │ │ worklog          │ │ api_key          │ │
         │ report_instance   │ │ worklog_approval │ │ oauth2_client    │ │
         │ dashboard         │ │                  │ │ api_log          │ │
         │                  │ │ 工时记录/统计/    │ │                  │
         │ Burndown/CFD/    │ │ 审批流            │ │ OAuth2 / API Key │ │
         │ Velocity Chart    │ │                  │ │ API 限流/计费    │ │
         └──────────────────┘ └──────────────────┘ └──────────────────┘

┌────────────────────────────────────────────────────────────────────────────┐
│                      Kafka 事件总线（3 节点 KRaft）                         │
│                                                                            │
│  task-created / task-updated / task-assigned / task-status-changed        │
│  project-created / project-member-added / sprint-started / sprint-completed │
│  release-published / meeting-scheduled                                     │
│                                                                            │
│  每个主题保留 7 天，支持任意 offset 回溯消费                                │
│  每个消费者组独立消费，互不干扰                                             │
└────────────────────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────────────────────┐
│                         lest-common（重构后的公共类库）                       │
│                                                                            │
│  lest-common-core/         # 核心：Result/PageResult/BusinessException     │
│  lest-common-redis/        # Redis：缓存、会话                             │
│  lest-common-security/     # Security：JWT/hasPermission/ProjectRole      │
│                              # 注解：@RequireProjectRole / @FieldPermission  │
│  lest-common-mq/           # Kafka：EventPublisher/@KafkaListener 基类      │
│  lest-common-plugin/       # 插件：Extension Point / Fragment 定义          │
│  lest-common-api/          # Feign：project-api / task-api / notify-api    │
│                                                                            │
│  ⚠️ 已删除：lest-common-datascope（OA 数据权限）/lest-common-sensitive       │
└────────────────────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────────────────────┐
│                         基础设施层（Docker / K8s）                           │
│                                                                            │
│  MySQL 8.4.9 (主库)     Redis 8.6.3 (缓存+会话)   Kafka 4.3.0 (KRaft)   │
│  Nacos 3.2.1 (注册+配置) MinIO (对象存储)         Sentinel (限流)          │
│                                                                            │
│  开发环境: docker-compose.local.yaml                                        │
│  生产环境: Kubernetes + HPA 自动扩缩容                                      │
└────────────────────────────────────────────────────────────────────────────┘
```

---

## 6. 迁移实施计划

### Phase 1: Core 迁移（V2.0 开发期间）

> **⚠️ 风险提示**：Phase 1 涉及数据库 Schema 变更和数据迁移，属于高风险操作。必须按顺序执行，并在每一步完成后验证。

| 顺序 | 任务 | 工作量 | 风险 | 回滚策略 |
|------|------|--------|------|----------|
| 1 | 新建 `auth_db` 数据库，从 `sys_user` 迁移 user 数据 | 中 | 低 | 保留原 `sys_user` 数据，仅读不写 |
| 2 | 新建 `token_blacklist` 和 `refresh_token` 表 | 低 | 低 | 新建表不影响现有逻辑 |
| 3 | 重构 `lest-auth`：JWT/Token/MFA/邀请链接 + auth_db | 高 | 中 | 保留原 lest-auth 的 Token 管理作为 fallback |
| 4 | 新增 `project_member` 表（不影响现有系统） | 低 | 低 | 新建表，纯追加 |
| 5 | 重构 `lest-common-security`：添加 `@RequireProjectRole` 和 `@FieldPermission` 注解 | 中 | 中 | 注解向后兼容，不影响现有接口 |
| 6 | 删除 `lest-system` 中的 OA Controller（SysDept/SysMenu/SysDict/SysPost）| 中 | **高** | **必须先完成步骤 5 注解迁移**，否则旧接口可能无法访问 |
| 7 | 删除 `lest-common-datascope`（@DataScope 注解链路）| 中 | 高 | 确认无 Controller 使用 @DataScope 后再删除 |
| 8 | 为 `lest-notification/ai/meeting/openapi/report/worklog/plugin/wakapi` 各自创建独立数据库 | 高 | 中 | 新建 DB 不影响现有系统 |
| 9 | 引入 `lest-common-mq`：Kafka 事件发布/订阅基类 | 中 | 低 | 新增类，不影响现有代码 |
| 10 | 将所有 Feign 同步调用替换为 Kafka 事件发布 | 高 | 中 | 保留原有 Feign 调用作为 fallback，逐步迁移 |

**回滚策略**：

```
每一步迁移后的回滚方案：

步骤 3 回滚：关闭新 lest-auth，使用原 lest-auth（保留原 Token 管理逻辑）
步骤 6 回滚：将 OA Controller 代码从 Git 历史恢复，重启服务即可
步骤 7 回滚：从 Git 历史恢复 lest-common-datascope 相关代码
步骤 10 回滚：关闭 Kafka 消费者，重新启用原 Feign 调用

关键原则：
1. 所有删除操作（OA 表、Controller）先注释后删除，保留 Git 历史
2. 数据库删除操作放在最后一步，且提前 2 周做数据备份
3. 每次 Phase 1 部署前，验证旧功能是否正常
```

### Phase 2: 插件服务补全（V3.0 开发期间）

| 顺序 | 任务 | 优先级 |
|------|------|--------|
| 1 | lest-notification：Kafka 消费者 + 邮件 + WebSocket | P0 |
| 2 | lest-ai：LangChain4j 集成 + Provider 路由 | P0 |
| 3 | lest-meeting：会议 CRUD + 纪要 | P1 |
| 4 | lest-release：发布计划 + 制品管理 | P1 |
| 5 | lest-report：Burndown/CFD 报表 | P1 |
| 6 | lest-worklog：工时记录 + 审批流 | P1 |
| 7 | lest-openapi：OAuth2 + API Key | P2 |
| 8 | lest-code：Git 集成 | P2 |
| 9 | lest-plugin：插件安装/卸载/加载 | P0 |
| 10 | lest-wakapi：WakaTime 集成 | P3 |

### Phase 3: 前端插件化（V2.0 开发期间）

| 顺序 | 任务 | 优先级 |
|------|------|--------|
| 1 | 实现 `PluginEngine.ts`（插件加载 + Fragment 注册）| P0 |
| 2 | 定义所有 Extension Point 接口 | P0 |
| 3 | 改造 `TaskDetail.vue` 为插件容器 | P0 |
| 4 | 改造 `ProjectDetail.vue` 为插件容器 | P1 |
| 5 | 实现内置插件的 Fragment 注册 | P1 |
| 6 | 插件市场 UI（lest-plugin 前端）| P2 |

---

## 7. 技术指标对比

| 维度 | 现有架构 | 目标架构 |
|------|---------|---------|
| Java 版本 | 21 | 21 |
| Spring Boot | 4.0.6 | 4.0.3 |
| Spring Cloud | 2025.1.0 | 2025.0.2 |
| Nacos | 3.0.2 | 3.2.1 |
| 数据库数 | 5 个 | 14 个（每个服务独立）|
| Kafka 集成 | 仅 lest-task | 所有跨服务通信 |
| OA 遗留功能 | 7 类（部门/岗位/字典/菜单/角色/数据权限/数据脱敏）| **0** |
| Feign 同步调用 | 常见 | 仅必要的内部调用 |
| 前端插件化 | 无 | 完整 Extension Point 体系 |
| Scheme 表 | 无 | 完整 EAV + 6 类 Scheme |
| 构建工具 | Maven | Maven |

---

## 版本历史

| 版本 | 日期 | 修改内容 | 作者 |
|------|------|---------|------|
| V1.0 | 2026-06-03 | 初始版本 | 架构师 |
| V1.1 | 2026-06-03 | 修复 Gradle → Maven；Java 版本 25 → 21；添加 Kafka 消费者组隔离说明；添加 Phase 1 迁移回滚策略 | 架构师 |
