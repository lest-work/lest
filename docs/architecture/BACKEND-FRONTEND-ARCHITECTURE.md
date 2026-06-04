# LEST Platform — 全栈架构设计

> **文档目的**：以架构师视角，基于 Core PRD（V1.0~V3.0）的功能需求，设计完整的后端和前端架构。
>
> **前置文档**：
> - [Core V1.0 认证系统](../1-prd/core/V1.0/认证系统与系统管理.md) — 权限模型、用户体系
> - [Core V1.0 任务管理](../1-prd/core/V1.0/任务管理.md) — 任务实体、视图、工时
> - [Core V1.0 项目管理](../1-prd/core/V1.0/项目管理.md) — 项目、迭代、里程碑
> - [Core V1.0 通知与消息](../1-prd/core/V1.0/通知与消息.md) — 事件总线、通知渠道
> - [Core V2.0 任务管理](../1-prd/core/V2.0/任务管理.md) — EAV 自定义字段、Screen/Scheme、工作流引擎
> - [Core V2.0 UI 插件化架构设计](../1-prd/core/V2.0/UI插件化架构设计.md) — 前端扩展点体系
> - [Core V3.0 任务管理](../1-prd/core/V3.0/任务管理.md) — Issue Link、附件、时间追踪、自动化引擎
>
> **版本**: V1.1 | **状态**: 设计完成 | **最后更新**: 2026-06-03

---

## 1. 架构设计原则

LEST Platform 的架构设计遵循以下核心原则：

1. **Jira 对齐，超越 Jira**：功能设计对标 Jira，架构设计追求更高的可维护性和可扩展性
2. **Core 最小化，Plugin 可插拔**：所有非核心功能通过插件提供，Core 代码不依赖任何插件
3. **事件驱动，松耦合**：微服务间通过 Kafka 事件总线通信，避免同步调用强耦合
4. **前后端分离，插件隔离**：前端插件通过 Extension Point 注入，后端插件通过 Spring Boot 自动装配隔离
5. **API First**：所有功能优先设计 API，API 是前后端和插件的唯一契约

---

## 2. 技术选型总览

### 2.1 后端技术栈

| 层级 | 技术 | 版本 | 说明 |
|------|------|------|------|
| **语言** | Java | 21 | JDK 21，LTS 版本 |
| **框架** | Spring Boot | 4.0.3 | 核心框架 |
| **微服务** | Spring Cloud | 2025.0.2 | 服务注册/发现、网关、负载均衡 |
| **ORM** | MyBatis-Plus | 3.5.16 | 原生 MyBatis + CRUD 增强 + 分页 + 代码生成 |
| **数据库** | MySQL | 8.4.9 | 主数据库 |
| **缓存** | Redis | 8.6.3 | 分布式缓存 + 会话存储 |
| **消息队列** | Apache Kafka | 4.3.0 | 服务间事件驱动通信 |
| **服务注册** | Nacos | 3.2.1 | 服务发现 + 配置中心 |
| **对象存储** | MinIO | RELEASE.2026-04-17 | S3 兼容的对象存储 |
| **API 网关** | Spring Cloud Gateway | 4.3.4 | JWT 鉴权、路由、限流、熔断 |
| **服务调用** | OpenFeign | 内置于 SC | 声明式 HTTP 客户端 |
| **熔断限流** | Resilience4j + Bucket4j | 2.4.0 / 8.18.0 | 函数式熔断 + 令牌桶限流 |
| **JWT** | jjwt | 0.13.0 | RFC 7519/7523 实现，支持 JWKS |
| **构建** | Maven | 3.9+ | 后端 Monorepo 构建 |
| **容器** | Docker + Kubernetes | 24.0+ / 1.28+ | 容器化 + 编排 |

### 2.2 前端技术栈

| 层级 | 技术 | 版本 | 说明 |
|------|------|------|------|
| **框架** | Vue | 3.5+ | Composition API + `<script setup>` |
| **类型** | TypeScript | 5.4+ | 严格模式 |
| **UI 库** | Element Plus | 2.9+ | PC 端组件库 |
| **构建** | Vite | 6.0+ | 极速开发服务器 + 构建 |
| **路由** | Vue Router | 4.5+ | 前端路由 |
| **状态** | Pinia | 2.3+ | Vue 3 原生状态管理 |
| **HTTP** | Axios + ky | — | API 请求封装 |
| **拖拽** | @vueuse/core + 手写 | — | 看板拖拽（无外部 DndKit 依赖）|
| **富文本** | Tiptap / Monaco Editor | — | 任务描述编辑器 |
| **图表** | Apache ECharts | 5.5+ | 燃尽图、甘特图、报表 |
| **国际化** | vue-i18n | 10+ | 多语言支持 |
| **测试** | Vitest + Playwright | — | 单元测试 + E2E |

---

## 3. 后端架构

### 3.1 微服务划分

基于 Core PRD 的功能边界，后端划分为 **11 个核心微服务**（按数据隔离强度分三组）：

#### 第一组：强隔离服务（独立数据库 Schema）

```
┌─────────────────────────────────────────────────────────┐
│                      lest-gateway                        │
│              (Spring Cloud Gateway)                      │
│         JWT 鉴权 · 路由转发 · 限流熔断                  │
└────────────────────────┬────────────────────────────────┘
                         │ HTTP / WebSocket
     ┌───────────────────┼───────────────────┐
     ▼                   ▼                   ▼
┌─────────┐      ┌────────────┐     ┌────────────┐
│ lest-auth│      │ lest-project│     │ lest-task   │
│ auth_db  │      │ project_db  │     │  task_db    │
└─────────┘      └────────────┘     └────────────┘
     │                   │                   │
     │         project_member 表         task 表
     │         由 lest-project 管理    由 lest-task 管理
     │                   │                   │
     └───────────────────┼───────────────────┘
                         │ Kafka 事件总线
                         ▼
              ┌──────────────────────┐
              │   lest-notification   │
              │    notification_db     │
              └──────────────────────┘
                         ▲
                         │ 订阅 task/project 事件
         ┌───────────────┼───────────────┐
         ▼               ▼               ▼
    ┌────────┐    ┌──────────┐    ┌──────────┐
    │lest-file│   │ lest-ai  │    │lest-plugin│
    │file_db  │   │   ai_db   │    │ plugin_db │
    └────────┘    └──────────┘    └──────────┘
```

#### 第二组：共享 lest-common（库级隔离）

| 服务 | 数据库 | 说明 |
|------|--------|------|
| `lest-auth` | `auth_db` | 认证服务：JWT 签发/验证、用户注册、邀请链接、会话管理 |
| `lest-project` | `project_db` | 项目服务：Project CRUD、项目成员（`project_member`）、迭代（Sprint）、里程碑（Version）、看板 |
| `lest-task` | `task_db` | 任务服务：Task CRUD、Issue Link、工时、附件、时间追踪、子任务、EAV 自定义字段、工作流 |
| `lest-notification` | `notification_db` | 通知服务：事件消费、通知聚合、站内信、邮件发送 |
| `lest-file` | `file_db` | 文件服务：MinIO 上传/下载、文件元数据管理 |
| `lest-ai` | `ai_db` | AI 服务：AI 网关路由、Provider 管理、Memory 管理 |
| `lest-plugin` | `plugin_db` | 插件服务：插件注册/安装/卸载、Extension Point 路由、manifest.json 验证 |

#### 第三组：插件服务（按需安装）

| 服务 | 数据库 | 说明 |
|------|--------|------|
| `lest-code` | `code_db` | Git 仓库、MR/PR、提交追踪 |
| `lest-meeting` | `meeting_db` | 会议管理、会议纪要 |
| `lest-release` | `release_db` | 发布计划、制品管理 |
| `lest-report` | `report_db` | 报表（Burndown/CFD/Velocity） |
| `lest-worklog` | `worklog_db` | 工时记录、统计、审批流 |
| `lest-wakapi` | `wakapi_db` | WakaTime 集成 |
| `lest-openapi` | `openapi_db` | 开放平台（OAuth2、API Key） |

### 3.2 lest-common 公共类库

所有微服务共享 `lest-common` 中的公共组件：

```
lest-common/
├── lest-common-core/              # 核心基础类
│   ├── domain/                    # 聚合根、实体、值对象
│   ├── dto/                       # 数据传输对象（请求/响应）
│   ├── vo/                        # 视图对象
│   ├── exception/                 # 统一异常体系（BusinessException + 错误码）
│   └── result/                   # 统一 API 响应包装（Result<T> / PageResult<T>）
├── lest-common-redis/             # Redis 封装
│   ├── config/                    # RedisTemplate 配置
│   ├── cache/                     # 缓存抽象（Cacheable 接口）
│   └── session/                   # 分布式会话
├── lest-common-security/          # 安全组件
│   ├── jwt/                       # JwtUtils（JWT 签发/验证/JWKS）
│   ├── auth/                      # 当前用户上下文（UserContextHolder）
│   ├── annotation/                # 自定义注解（@RequireProjectRole, @FieldPermission）
│   └── permission/               # hasPermission 注解 + 权限检查器
├── lest-common-mq/                # 消息队列封装
│   ├── config/                    # Kafka 配置
│   ├── producer/                 # 事件发布者（EventPublisher）
│   └── consumer/                 # 事件消费者基类
├── lest-common-plugin/           # 插件框架
│   ├── context/                  # PluginContext（插件加载上下文）
│   ├── extension/               # Extension Point 定义
│   ├── manifest/                # manifest.json 解析器
│   ├── config/                  # PluginAutoConfiguration（Spring Boot 自动装配）
│   ├── domain/                  # PluginDescriptor, PluginConfig
│   └── PluginManager             # 插件生命周期管理器（注册/启动/停止）
├── lest-common-mq/              # 消息队列封装
│   ├── config/                  # KafkaMqAutoConfiguration（Kafka 自动装配）
│   ├── constants/                # MqTopicConstants（所有业务 Topic 常量）
│   ├── message/                  # LestMessage（统一消息格式）
│   ├── core/                    # KafkaMessageSender（同步/异步发送）、LestEventListener（Spring Event → Kafka）
│   └── event/                   # LestPlatformEvent（事件基类 + 业务事件定义）
└── lest-common-api/              # 跨服务 API 声明
    ├── project-api/             # lest-project 暴露的 Feign 接口
    ├── task-api/                # lest-task 暴露的 Feign 接口
    └── notification-api/        # lest-notification 暴露的接口
```

### 3.3 数据库设计

#### 3.3.1 认证服务（auth_db）

```sql
-- =============================================
-- 核心表（V2.0 迁移后保留）
-- =============================================

CREATE TABLE `user` (
    `id`            BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    `username`      VARCHAR(64) NOT NULL UNIQUE COMMENT '用户名/邮箱',
    `nickname`      VARCHAR(128) NOT NULL COMMENT '昵称',
    `password_hash` VARCHAR(255) NOT NULL COMMENT 'BCrypt 哈希',
    `avatar_url`    VARCHAR(512) COMMENT '头像 URL',
    `status`        TINYINT NOT NULL DEFAULT 1 COMMENT '1=正常 2=禁用',
    `email_verified` TINYINT NOT NULL DEFAULT 0 COMMENT '邮箱已验证',
    `mfa_enabled`   TINYINT NOT NULL DEFAULT 0 COMMENT 'MFA 已启用',
    `mfa_secret`    VARCHAR(255) COMMENT 'TOTP 密钥（加密存储）',
    `last_login_at` DATETIME COMMENT '最后登录时间',
    `created_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted_at`    DATETIME COMMENT '软删除',
    INDEX `idx_username` (`username`),
    INDEX `idx_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE `token_blacklist` (
    `id`            BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    `token_jti`     VARCHAR(64) NOT NULL UNIQUE COMMENT 'JWT ID (jti claim)',
    `user_id`       BIGINT UNSIGNED NOT NULL COMMENT '用户 ID',
    `blacklisted_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '拉黑时间',
    `expires_at`    DATETIME NOT NULL COMMENT 'Token 原始过期时间',
    INDEX `idx_token_jti` (`token_jti`),
    INDEX `idx_expires_at` (`expires_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='JWT Token 黑名单表（Redis 同步备份）';

CREATE TABLE `refresh_token` (
    `id`            BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    `user_id`       BIGINT UNSIGNED NOT NULL,
    `token_hash`    VARCHAR(255) NOT NULL COMMENT 'Refresh Token 哈希',
    `device_info`   VARCHAR(255) COMMENT '设备信息',
    `ip_address`    VARCHAR(45) COMMENT 'IP 地址',
    `expires_at`    DATETIME NOT NULL COMMENT 'Refresh Token 过期时间',
    `created_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `revoked_at`    DATETIME COMMENT '撤销时间（null=未撤销）',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_token_hash` (`token_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Refresh Token 表（用于 Token 续期）';

-- =============================================
-- ⚠️ DEPRECATED (V2.0): 以下 OA 风格表已废弃
-- 迁移说明见: ../1-prd/core/V1.0/认证系统与系统管理.md §2.4
-- =============================================

-- `sys_role`       — V2.0 删除，改用 project_member 表管理项目级角色
-- `user_role`      — V2.0 删除
-- `sys_menu`       — V2.0 删除，菜单固定化
-- `sys_role_menu`  — V2.0 删除
-- `sys_org`        — V2.0 删除，敏捷平台按项目而非部门组织
```

**JWT 设计说明**：
- 采用** stateless JWT** 方案：Token 无需数据库查询验证，通过签名即可确认有效性
- Token 过期时间：Access Token 15 分钟，Refresh Token 7 天
- **Token 黑名单**：用户登出时，将 Token JTI 加入 `token_blacklist` 表（同时同步到 Redis）
- 每次请求时，先检查 Redis 黑名单（O(1)），未命中则放行；也可只检查 Redis，数据库作为持久化备份
- Refresh Token 存储哈希，用于续期时验证（不存储在 JWT 中）

#### 3.3.2 项目服务（project_db）

```sql
CREATE TABLE `project` (
    `id`                    BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    `project_key`           VARCHAR(32) NOT NULL UNIQUE COMMENT '项目 Key（如 LEST）',
    `name`                 VARCHAR(128) NOT NULL,
    `description`           TEXT,
    `owner_id`             BIGINT UNSIGNED NOT NULL COMMENT '项目创建者',
    `project_type`         VARCHAR(32) NOT NULL DEFAULT 'agile' COMMENT 'agile/scrum/kanban',
    `default_view`         VARCHAR(32) NOT NULL DEFAULT 'board' COMMENT 'board/list/gantt',
    `issue_type_scheme_id` BIGINT UNSIGNED COMMENT 'Issue Type Scheme（V2.0）',
    `workflow_scheme_id`   BIGINT UNSIGNED COMMENT 'Workflow Scheme（V2.0）',
    `screen_scheme_id`     BIGINT UNSIGNED COMMENT 'Screen Scheme（V2.0）',
    `field_config_scheme_id` BIGINT UNSIGNED COMMENT 'Field Config Scheme（V2.0）',
    `status`               TINYINT NOT NULL DEFAULT 1 COMMENT '1=活跃 2=归档',
    `archived_at`         DATETIME COMMENT '归档时间',
    `start_date`           DATE COMMENT '项目开始日期',
    `end_date`             DATE COMMENT '项目结束日期',
    `created_at`           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_owner_id` (`owner_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目表';

CREATE TABLE `project_member` (
    `id`            BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    `project_id`   BIGINT UNSIGNED NOT NULL,
    `user_id`      BIGINT UNSIGNED NOT NULL,
    `role_type`    VARCHAR(32) NOT NULL COMMENT 'admin/dev/viewer/lead',
    `joined_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `invited_by`   BIGINT UNSIGNED COMMENT '邀请人',
    UNIQUE KEY `uk_project_user` (`project_id`, `user_id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目成员表（V2.0 替代 sys_role/user_role）';

CREATE TABLE `iteration` (
    `id`            BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    `project_id`    BIGINT UNSIGNED NOT NULL,
    `name`          VARCHAR(128) NOT NULL,
    `goal`          TEXT COMMENT 'Sprint 目标',
    `status`        VARCHAR(32) NOT NULL COMMENT 'planning/active/completed',
    `start_date`    DATE NOT NULL,
    `end_date`      DATE NOT NULL,
    `capacity`      INT COMMENT '团队容量（人天）',
    `created_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_project_id` (`project_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='迭代表（Sprint）';

CREATE TABLE `project_version` (
    `id`            BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    `project_id`    BIGINT UNSIGNED NOT NULL,
    `name`          VARCHAR(128) NOT NULL COMMENT '版本名（如 v1.0.0）',
    `description`   TEXT,
    `status`        VARCHAR(32) NOT NULL COMMENT 'released/unreleased/archived',
    `release_date`  DATE COMMENT '发布日期',
    `start_date`    DATE COMMENT '开始日期',
    `created_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_project_id` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='里程碑/版本表';

-- =============================================
-- V3.0 敏捷看板增强表
-- =============================================

CREATE TABLE `board` (
    `id`            BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    `project_id`    BIGINT UNSIGNED NOT NULL COMMENT '所属项目',
    `name`          VARCHAR(128) NOT NULL COMMENT '看板名称',
    `description`   TEXT,
    `is_default`    TINYINT NOT NULL DEFAULT 0 COMMENT '是否默认看板',
    `swim_lane_mode` VARCHAR(32) NOT NULL DEFAULT 'none' COMMENT '泳道模式: none/assignee/epic',
    `wip_limit_strategy` VARCHAR(32) NOT NULL DEFAULT 'column' COMMENT 'WIP 限制策略: none/column/swimlane',
    `created_by`    BIGINT UNSIGNED NOT NULL,
    `created_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_project_id` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='看板表';

CREATE TABLE `board_column` (
    `id`            BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    `board_id`      BIGINT UNSIGNED NOT NULL,
    `name`          VARCHAR(128) NOT NULL COMMENT '列名（如 待办/进行中/完成）',
    `status_mapping` VARCHAR(128) COMMENT '映射的系统状态（JSON 数组）',
    `color`         VARCHAR(32) COMMENT '列颜色',
    `wip_limit`     INT COMMENT 'WIP 限制数量（0 或 null=不限）',
    `min_limit`     INT COMMENT '最小任务数预警',
    `position`      INT NOT NULL DEFAULT 0 COMMENT '列顺序',
    `created_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_board_id` (`board_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='看板列定义';

CREATE TABLE `board_swim_lane` (
    `id`            BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    `board_id`      BIGINT UNSIGNED NOT NULL,
    `lane_key`      VARCHAR(64) NOT NULL COMMENT '泳道标识（user_id/epic_id/自定义值）',
    `lane_label`    VARCHAR(128) NOT NULL COMMENT '泳道显示名',
    `position`      INT NOT NULL DEFAULT 0 COMMENT '泳道顺序',
    `collapsed`     TINYINT NOT NULL DEFAULT 0 COMMENT '是否折叠',
    INDEX `idx_board_id` (`board_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='看板泳道表（V3.0）';

CREATE TABLE `board_card_color_rule` (
    `id`            BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    `board_id`      BIGINT UNSIGNED NOT NULL,
    `field`         VARCHAR(64) NOT NULL COMMENT '字段名（priority/assigneeId/label/...）',
    `operator`      VARCHAR(32) NOT NULL COMMENT '操作符: equals/contains/gt/lt',
    `value`         VARCHAR(128) NOT NULL COMMENT '匹配值',
    `color`         VARCHAR(32) NOT NULL COMMENT '卡片颜色',
    `position`      INT NOT NULL DEFAULT 0 COMMENT '规则优先级',
    INDEX `idx_board_id` (`board_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='看板卡片颜色规则（V3.0）';
```

#### 3.3.3 任务服务（task_db）

```sql
CREATE TABLE `task` (
    `id`              BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    `task_no`        VARCHAR(32) NOT NULL COMMENT '任务编号（如 LEST-123）',
    `project_id`     BIGINT UNSIGNED NOT NULL,
    `parent_id`      BIGINT UNSIGNED COMMENT '父任务 ID（子任务用）',
    `issue_type`      VARCHAR(32) NOT NULL COMMENT 'epic/story/task/bug',
    `title`          VARCHAR(512) NOT NULL,
    `description`    LONGTEXT COMMENT '富文本描述（JSON 结构化）',
    `status`         VARCHAR(64) NOT NULL COMMENT '待办/进行中/已完成（V1.0），可自定义（V2.0）',
    `priority`       VARCHAR(32) NOT NULL DEFAULT 'medium' COMMENT 'highest/high/medium/low/lowest',
    `assignee_id`   BIGINT UNSIGNED COMMENT '负责人',
    `reporter_id`   BIGINT UNSIGNED NOT NULL,
    `iteration_id`   BIGINT UNSIGNED COMMENT '所属 Sprint',
    `version_id`     BIGINT UNSIGNED COMMENT '目标版本',
    `story_points`   DECIMAL(5,1) COMMENT '故事点',
    `estimate_hours` DECIMAL(8,2) COMMENT '预估工时',
    `actual_hours`   DECIMAL(8,2) COMMENT '实际工时（自动汇总子任务）',
    `progress`        TINYINT DEFAULT 0 COMMENT '进度百分比 0-100',
    `start_date`     DATE COMMENT '开始日期',
    `due_date`       DATE COMMENT '截止日期',
    `labels`         JSON COMMENT '标签 ID 数组',
    `watchers`       JSON COMMENT '关注者用户 ID 数组',
    `security_level`  BIGINT UNSIGNED COMMENT '安全级别（V2.0）',
    `workflow_id`    BIGINT UNSIGNED COMMENT '当前工作流实例 ID（V2.0）',
    `position`       INT NOT NULL DEFAULT 0 COMMENT '看板内排序位置',
    `created_at`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted_at`     DATETIME COMMENT '软删除',
    UNIQUE KEY `uk_task_no` (`task_no`),
    INDEX `idx_project_id` (`project_id`),
    INDEX `idx_assignee_id` (`assignee_id`),
    INDEX `idx_iteration_id` (`iteration_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务表';

CREATE TABLE `task_link` (
    `id`           BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    `source_task_id` BIGINT UNSIGNED NOT NULL,
    `target_task_id` BIGINT UNSIGNED NOT NULL,
    `link_type`    VARCHAR(64) NOT NULL COMMENT 'blocks/blocked_by/relates_to/clones/is_cloned_by/subtask_of/parent_of',
    `created_at`   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_link` (`source_task_id`, `target_task_id`, `link_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务关联表（V3.0）';

CREATE TABLE `task_attachment` (
    `id`           BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    `task_id`      BIGINT UNSIGNED NOT NULL,
    `file_id`      BIGINT UNSIGNED NOT NULL COMMENT '引用 lest-file 服务',
    `file_name`    VARCHAR(255) NOT NULL,
    `file_size`    BIGINT NOT NULL COMMENT '字节',
    `mime_type`    VARCHAR(128) NOT NULL,
    `uploaded_by`  BIGINT UNSIGNED NOT NULL,
    `uploaded_at`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_task_id` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务附件表（V3.0）';

CREATE TABLE `task_change_history` (
    `id`           BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    `task_id`      BIGINT UNSIGNED NOT NULL,
    `field_name`   VARCHAR(64) NOT NULL COMMENT '变更字段名',
    `old_value`    TEXT COMMENT '变更前值',
    `new_value`    TEXT COMMENT '变更后值',
    `changed_by`   BIGINT UNSIGNED NOT NULL,
    `changed_at`   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_task_id` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务变更历史表';

-- =============================================
-- V2.0 EAV 自定义字段表
-- =============================================

CREATE TABLE `custom_field_definition` (
    `id`               BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    `name`             VARCHAR(128) NOT NULL COMMENT '字段显示名',
    `field_key`        VARCHAR(64) NOT NULL COMMENT '字段唯一标识（英文）',
    `field_type`       VARCHAR(32) NOT NULL COMMENT 'text/number/date/select/multi-select/user/project/version',
    `description`      TEXT COMMENT '字段描述',
    `default_value`    TEXT COMMENT '默认值',
    `options`          JSON COMMENT '选项列表（用于 select/multi-select）',
    `required`         TINYINT NOT NULL DEFAULT 0 COMMENT '是否必填',
    `searchable`       TINYINT NOT NULL DEFAULT 1 COMMENT '是否可搜索',
    `sortable`         TINYINT NOT NULL DEFAULT 1 COMMENT '是否可排序',
    `default_list_view` TINYINT NOT NULL DEFAULT 0 COMMENT '是否默认显示在列表',
    `default_detail_view` TINYINT NOT NULL DEFAULT 1 COMMENT '是否默认显示在详情',
    `validation_regex` VARCHAR(255) COMMENT '正则校验',
    `allowed_issue_types` JSON COMMENT '允许的 Issue Type（null=全部）',
    `created_at`       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_field_key` (`field_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自定义字段定义表（EAV）';

CREATE TABLE `custom_field_value` (
    `id`               BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    `task_id`          BIGINT UNSIGNED NOT NULL,
    `field_id`         BIGINT UNSIGNED NOT NULL,
    `value`            TEXT COMMENT '字段值（JSON 序列化）',
    `updated_at`       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_task_field` (`task_id`, `field_id`),
    INDEX `idx_field_id` (`field_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自定义字段值表（EAV）';

-- =============================================
-- V2.0 工作流引擎表
-- =============================================

CREATE TABLE `workflow_definition` (
    `id`               BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    `name`             VARCHAR(128) NOT NULL,
    `description`      TEXT,
    `status`           VARCHAR(32) NOT NULL DEFAULT 'draft' COMMENT 'draft/active/deprecated',
    `content`          JSON NOT NULL COMMENT '工作流定义 JSON（状态机）',
    `created_by`       BIGINT UNSIGNED NOT NULL,
    `created_at`       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工作流定义表';

CREATE TABLE `workflow_instance` (
    `id`               BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    `workflow_id`      BIGINT UNSIGNED NOT NULL,
    `task_id`          BIGINT UNSIGNED NOT NULL,
    `current_status`   VARCHAR(64) NOT NULL COMMENT '当前状态',
    `status_history`   JSON COMMENT '状态历史记录',
    `created_at`       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_task` (`task_id`),
    INDEX `idx_workflow_id` (`workflow_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工作流实例表（每个任务一个实例）';

-- =============================================
-- V2.0 Screen / Scheme 表
-- =============================================

CREATE TABLE `screen` (
    `id`               BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    `name`             VARCHAR(128) NOT NULL COMMENT 'Screen 名称（如 "Bug 创建屏"）',
    `description`      TEXT,
    `type`             VARCHAR(32) NOT NULL COMMENT 'create/edit/view/bulk_edit',
    `field_layout`     JSON NOT NULL COMMENT '字段布局配置（顺序、宽度、折叠组）',
    `created_at`       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Screen 表（字段布局配置）';

CREATE TABLE `field_configuration` (
    `id`               BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    `name`             VARCHAR(128) NOT NULL COMMENT 'Field Configuration 名称',
    `description`      TEXT,
    `field_settings`   JSON NOT NULL COMMENT '每个字段的设置（required/hidden/readonly）',
    `created_at`       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字段配置表';

CREATE TABLE `issue_type_scheme` (
    `id`               BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    `name`             VARCHAR(128) NOT NULL,
    `description`      TEXT,
    `is_default`       TINYINT NOT NULL DEFAULT 0 COMMENT '是否默认方案',
    `issue_types`      JSON NOT NULL COMMENT '包含的 Issue Type 列表及顺序',
    `created_at`       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Issue Type Scheme 表';

CREATE TABLE `issue_type_screen_scheme` (
    `id`               BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    `name`             VARCHAR(128) NOT NULL,
    `description`      TEXT,
    `is_default`       TINYINT NOT NULL DEFAULT 0,
    `issue_type_screens` JSON NOT NULL COMMENT '每个 Issue Type 对应的 Screen（JSON）',
    `created_at`       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Issue Type Screen Scheme 表';
```

#### 3.3.4 通知服务（notification_db）

```sql
CREATE TABLE `notification` (
    `id`           BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    `user_id`      BIGINT UNSIGNED NOT NULL COMMENT '通知接收者',
    `type`         VARCHAR(64) NOT NULL COMMENT 'task_created/task_assigned/comment_added/...',
    `title`        VARCHAR(255) NOT NULL,
    `content`      TEXT,
    `source_type`  VARCHAR(32) COMMENT 'task/project/meeting',
    `source_id`    BIGINT UNSIGNED COMMENT '关联实体 ID',
    `is_read`      TINYINT NOT NULL DEFAULT 0,
    `read_at`      DATETIME COMMENT '已读时间',
    `created_at`   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_user_unread` (`user_id`, `is_read`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

CREATE TABLE `notification_subscription` (
    `id`           BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    `user_id`      BIGINT UNSIGNED NOT NULL,
    `event_type`   VARCHAR(64) NOT NULL COMMENT 'task_assigned/task_created/comment_added/...',
    `scope_type`   VARCHAR(32) NOT NULL DEFAULT 'all' COMMENT 'all/project/filter',
    `scope_id`     BIGINT UNSIGNED COMMENT 'project_id 或 filter_id',
    `channel`      VARCHAR(32) NOT NULL COMMENT 'in_app/email/webhook/im',
    `enabled`      TINYINT NOT NULL DEFAULT 1,
    `created_at`   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_user_event_scope` (`user_id`, `event_type`, `scope_type`, `scope_id`, `channel`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知订阅表';

CREATE TABLE `notification_template` (
    `id`           BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    `event_type`   VARCHAR(64) NOT NULL UNIQUE COMMENT '对应 Kafka 事件类型',
    `channel`      VARCHAR(32) NOT NULL COMMENT 'in_app/email/webhook',
    `title_template` VARCHAR(512) NOT NULL COMMENT '标题模板（支持变量占位符）',
    `content_template` TEXT NOT NULL COMMENT '内容模板',
    `variables`    JSON COMMENT '可用变量列表（用于前端预览）',
    `enabled`      TINYINT NOT NULL DEFAULT 1,
    `created_at`   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知模板表';
```

### 3.4 事件驱动架构

微服务间通过 Kafka 事件总线解耦，避免同步 Feign 调用：

```java
// 事件定义（所有服务共享 lest-common-mq）
public interface DomainEvent {
    Long getOccurredOn();
}

// 任务领域事件
public record TaskCreatedEvent(
    Long taskId,
    String taskNo,
    Long projectId,
    Long reporterId,
    String title
) implements DomainEvent {
    public Long getOccurredOn() { return System.currentTimeMillis(); }
}

public record TaskUpdatedEvent(
    Long taskId,
    String taskNo,
    Long projectId,
    Map<String, Object> changes,
    Long changedBy
) implements DomainEvent {
    public Long getOccurredOn() { return System.currentTimeMillis(); }
}

public record TaskDeletedEvent(Long taskId, Long projectId) implements DomainEvent {
    public Long getOccurredOn() { return System.currentTimeMillis(); }
}

public record TaskAssignedEvent(
    Long taskId,
    String taskNo,
    Long assigneeId,
    Long projectId
) implements DomainEvent {
    public Long getOccurredOn() { return System.currentTimeMillis(); }
}

public record TaskCommentedEvent(
    Long taskId,
    Long commentId,
    Long authorId
) implements DomainEvent {
    public Long getOccurredOn() { return System.currentTimeMillis(); }
}

public record ProjectMemberAddedEvent(
    Long projectId,
    Long userId,
    String roleType
) implements DomainEvent {
    public Long getOccurredOn() { return System.currentTimeMillis(); }
}

public record ProjectMemberRemovedEvent(
    Long projectId,
    Long userId
) implements DomainEvent {
    public Long getOccurredOn() { return System.currentTimeMillis(); }
}

public record SprintStartedEvent(Long iterationId, Long projectId) implements DomainEvent {
    public Long getOccurredOn() { return System.currentTimeMillis(); }
}

public record SprintCompletedEvent(Long iterationId, Long projectId) implements DomainEvent {
    public Long getOccurredOn() { return System.currentTimeMillis(); }
}

// 事件发布（Task 服务）
@Service
public class TaskDomainService {
    private final EventPublisher publisher;

    public void assign(Long taskId, Long assigneeId) {
        task.assign(assigneeId);
        taskRepo.save(task);
        // 发布领域事件，通知其他服务
        publisher.publish(new TaskAssignedEvent(taskId, task.getTaskNo(), assigneeId, task.getProjectId()));
    }
}

// 事件消费（Notification 服务）
@KafkaListener(topics = "task-assigned", groupId = "lest-notification")
public void handle(TaskAssignedEvent event) {
    notificationService.send(
        event.assigneeId(),
        NotificationType.TASK_ASSIGNED,
        "任务 " + event.taskNo() + " 已分配给你"
    );
}

// 事件消费（Project 服务）
@KafkaListener(topics = "task-assigned", groupId = "lest-project")
public void handle(TaskAssignedEvent event) {
    // 更新项目的最后活动时间
    projectActivityService.record(event);
}
```

**Kafka 消费者组隔离说明**：
- 同一个 Topic 可以有多个消费者组，各组独立消费，互不干扰
- `lest-notification` 和 `lest-project` 使用不同的 `groupId`，确保各自独立处理 `task-assigned` 事件
- 同一个服务内的 Producer 和 Consumer 使用不同 Topic，避免自触发

### 3.5 权限检查架构

LEST 的权限分为三层，全部通过 AOP 注解驱动：

```java
// =============================================
// 自定义权限注解定义（lest-common-security）
// =============================================

/**
 * 项目级角色要求注解
 * 使用方式: @RequireProjectRole(ProjectRole.ADMIN)
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireProjectRole {
    ProjectRole[] value();
    
    Logical logical() default Logical.AND;  // AND=必须全部满足, OR=满足其一即可
}

/**
 * 字段级权限注解（V2.0）
 * 配合 FieldPermissionInterceptor 使用
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FieldPermission {
    String[] includes() default {};   // 只允许访问的字段
    String[] excludes() default {};  // 排除的字段（如 Reporter 不能改 securityLevel）
}

// 权限检查逻辑示例
@RequireProjectRole(ProjectRole.ADMIN)  // 项目管理员才能操作
public void updateProject(Long projectId, ProjectUpdateDTO dto) {
    // hasPermission(userId, projectId, "PROJECT_ADMIN") 检查
    projectRepo.update(projectId, dto);
}

@FieldPermission(excludes = {"securityLevel"})  // Reporter 角色不能修改安全级别
public void updateTaskSecurityLevel(Long taskId, SecurityLevel level) {}

// 数据级安全（Repository 层）
public List<Task> findAccessible(Long userId, String jql) {
    // 自动注入安全级别过滤（用户只能看到自己有权限的任务）
    return taskRepo.findByJQL(jql, SecurityFilter.of(userId));
}
```

### 3.6 API 分层与契约

```
┌─────────────────────────────────────────────────────────┐
│                    API Gateway (SCG)                     │
│         JWT 验证 → 路由 → 限流 → 熔断 → 日志           │
└────────────────────────┬────────────────────────────────┘
                         │ /api/v1/{service}/*
     ┌───────────────────┼───────────────────┐
     ▼                   ▼                   ▼
  /api/v1/auth      /api/v1/project    /api/v1/task
  lest-auth          lest-project       lest-task
```

#### 3.6.1 Core API 路由

| 前缀 | 服务 | 说明 |
|------|------|------|
| `/api/v1/auth` | lest-auth | 认证、用户、Token |
| `/api/v1/project` | lest-project | 项目、成员、迭代、版本 |
| `/api/v1/task` | lest-task | 任务、标签、评论 |
| `/api/v1/file` | lest-file | 文件上传/下载 |
| `/api/v1/notification` | lest-notification | 通知、订阅 |
| `/api/v1/ai` | lest-ai | AI 路由 |

#### 3.6.2 Plugin API 路由

插件 API 通过 `/api/plugin/{pluginId}/*` 前缀路由，由 Gateway 转发到对应插件服务：

| 前缀 | 说明 |
|------|------|
| `/api/plugin/lest-worklog/*` | 工时插件 API |
| `/api/plugin/lest-release/*` | 发布插件 API |
| `/api/plugin/lest-code/*` | 代码插件 API |
| ... | 其他插件 API |

```
┌──────────────────────────────────────────────────────────────┐
│                     API Gateway (SCG)                        │
│                                                              │
│  /api/v1/{service}/*  → 路由到 lest-{service}               │
│  /api/plugin/{pluginId}/* → 路由到 lest-plugin 再转发       │
│                                                              │
│  插件 API 路由通过 lest-plugin 服务的路由注册表动态转发       │
└──────────────────────────────────────────────────────────────┘
```

统一 API 响应格式（Java 21 Record）：

```java
// 统一成功响应
public record Result<T>(
    int code,           // HTTP 状态码（200=成功）
    String message,     // 描述信息
    T data,             // 泛型数据
    long timestamp      // 时间戳
) {
    public static <T> Result<T> ok(T data) {
        return new Result<>(200, "success", data, System.currentTimeMillis());
    }
    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message, null, System.currentTimeMillis());
    }
}

// 统一分页响应
public record PageResult<T>(
    List<T> records,
    long total,
    long page,
    long size
) {}

// 全局异常处理（@RestControllerAdvice）
// - BizException → Result.fail(code, message)
// - MethodArgumentNotValidException → Result.fail(400, msg)
// - Exception → Result.fail(500, "系统内部错误")
```

---

## 4. 前端架构

### 4.1 整体架构

LEST 前端采用**插件化单页应用**架构：

```
frontend-pc/
├── src/
│   ├── main.ts                   # 应用入口
│   ├── App.vue                   # 根组件
│   │
│   ├── core/                     # Core 核心（最小化，不可删除）
│   │   ├── router/              # 路由配置
│   │   ├── stores/              # Pinia 状态（全局）
│   │   ├── api/                 # API 请求封装
│   │   ├── components/          # 基础组件（不含业务逻辑）
│   │   │   ├── base/           # Button/Input/Dialog 等
│   │   │   └── layout/          # Sidebar/Layout/Header
│   │   ├── views/               # 页面骨架（插件容器）
│   │   │   ├── task/            # 任务页面（仅包含容器，不含具体字段）
│   │   │   ├── project/         # 项目页面
│   │   │   └── admin/           # 极简管理后台
│   │   └── plugin-host/         # 插件运行时（Extension Point 引擎）
│   │       ├── PluginEngine.ts  # 插件加载器
│   │       ├── ExtensionPoint.ts # 扩展点定义
│   │       └── FragmentRenderer.vue # Fragment 渲染器
│   │
│   ├── plugins/                  # 官方内置插件（编译时注入）
│   │   ├── lest-worklog/         # 工时插件
│   │   ├── lest-cicd/            # CI/CD 插件
│   │   └── ...
│   │
│   └── locales/                  # i18n 资源文件
└── vite.config.ts               # 构建配置（plugin host 注入）
```

### 4.2 插件化架构核心

前端插件化是 LEST 区别于传统后台系统的关键设计。参考 Jira Forge / VS Code Extension Host：

#### 4.2.1 Extension Point 定义

```typescript
// core/plugin-host/ExtensionPoint.ts
import type { ComponentType } from 'vue';

// 扩展点类型定义
export type ExtensionPointType =
  // Task 详情页
  | 'task-detail.header.actions'      // 头部操作按钮
  | 'task-detail.field.sidebar'     // 侧边字段区
  | 'task-detail.tabs'             // Tab 面板
  | 'task-detail.context.menu'      // 右键菜单
  | 'task-detail.quick.actions'     // 快捷操作
  // Task 列表页
  | 'task-list.toolbar'             // 列表工具栏
  | 'task-list.card.footer'         // 卡片底部
  // Project 详情页
  | 'project-detail.tabs'           // 项目 Tab
  | 'project-detail.sidebar'        // 项目侧边栏
  // 全局
  | 'global.toolbar'                // 全局工具栏
  | 'dashboard.gadget'             // 仪表盘 Gadget
  | 'notification.channel'          // 通知渠道
  | 'settings.panel';               // 设置面板

export interface ExtensionPoint<T = any> {
  id: ExtensionPointType;
  label: string;           // 扩展点名（如 "任务详情页 - 工时"）
  description?: string;
  maxItems?: number;       // 最大注册数量（-1=无限）
  render?: (props: T) => ComponentType;  // Vue 组件渲染函数
}

// Fragment 注册信息
export interface Fragment {
  id: string;
  pluginId: string;
  extensionPoint: ExtensionPointType;
  label: string;
  order: number;          // 排序优先级
  component: ComponentType; // Vue 组件
  configSchema?: JSONSchema; // 配置 Schema（可选）
}
```

**注意**：`render` 返回 `ComponentType`（Vue 组件类型），而非 JSX `JSX.Element`。Vue SFC 使用 `.vue` 文件，不使用 JSX。

#### 4.2.2 插件引擎

```typescript
// core/plugin-host/PluginEngine.ts
import type { ComponentType } from 'vue';

class PluginEngine {
  private fragments: Map<ExtensionPointType, Fragment[]> = new Map();
  private manifestCache = new Map<string, PluginManifest>();

  // 加载插件 manifest
  async loadPlugin(pluginId: string, manifestUrl: string) {
    const manifest = await fetch(manifestUrl).then(r => r.json());
    
    // 验证 manifest 签名
    if (!this.verifySignature(manifest, manifestUrl)) {
      throw new PluginLoadError('INVALID_SIGNATURE', pluginId);
    }

    // 注册所有 Fragment
    for (const contribution of manifest.contributes) {
      this.registerFragment({
        id: `${pluginId}:${contribution.id}`,
        pluginId,
        extensionPoint: contribution.extensionPoint,
        label: contribution.label,
        order: contribution.order ?? 0,
        component: this.lazyLoadComponent(contribution.entry) as ComponentType,
      });
    }

    this.manifestCache.set(pluginId, manifest);
  }

  // 按扩展点查询所有注册的 Fragment
  getFragments(point: ExtensionPointType): Fragment[] {
    return (this.fragments.get(point) ?? [])
      .sort((a, b) => a.order - b.order);
  }

  // 卸载插件
  async unloadPlugin(pluginId: string) {
    for (const [point, frags] of this.fragments) {
      this.fragments.set(point, frags.filter(f => f.pluginId !== pluginId));
    }
    this.manifestCache.delete(pluginId);
  }
}

export const pluginEngine = new PluginEngine();
```

#### 4.2.3 Fragment 渲染器

```vue
<!-- core/plugin-host/FragmentRenderer.vue -->
<script setup lang="ts">
import { pluginEngine } from '../PluginEngine';

const props = defineProps<{
  extensionPoint: ExtensionPointType;
  context: any;   // 上下文数据（如 taskId、projectId）
}>();

// 获取该扩展点的所有 Fragment
const fragments = computed(() => 
  pluginEngine.getFragments(props.extensionPoint)
);

// 控制默认插槽的渲染
const showDefaultSlot = computed(() => fragments.value.length === 0);
</script>

<template>
  <div class="extension-point-host" :data-point="extensionPoint">
    <template v-for="fragment in fragments" :key="fragment.id">
      <component
        :is="fragment.component"
        v-bind="context"
        :fragment="fragment"
      />
    </template>
    <!-- 无 Fragment 时渲染默认内容（使用 v-show 避免 v-if 的条件渲染问题）-->
    <div v-show="showDefaultSlot">
      <slot />
    </div>
  </div>
</template>
```

**注意**：使用 `v-show` 而非 `v-if` 来控制默认插槽的渲染。`v-if` 会导致 `<slot>` 在条件不满足时被销毁，后续即使有 Fragment 注册也无法正确渲染。`v-show` 只切换 `display` 样式，始终保留 `<slot>` 的内容结构。

#### 4.2.4 Core 页面 = 插件容器

```vue
<!-- views/task/TaskDetail.vue — 任务详情页 -->
<template>
  <div class="task-detail">
    <!-- 头部操作区：Core 按钮 + 插件注入的按钮 -->
    <div class="task-header-actions">
      <el-button>编辑</el-button>
      <el-button>删除</el-button>
      <!-- ✅ 插件扩展点：头部操作 -->
      <FragmentRenderer extension-point="task-detail.header.actions" :context="taskContext" />
    </div>

    <!-- 任务标题 + 描述 -->
    <h1>{{ task.title }}</h1>

    <!-- 侧边字段区：Core 字段 + 插件注入的字段 -->
    <div class="task-fields">
      <!-- ✅ Core 字段 -->
      <FieldItem label="状态">{{ task.status }}</FieldItem>
      <FieldItem label="负责人">{{ task.assignee }}</FieldItem>
      
      <!-- ✅ 插件扩展点：侧边字段 -->
      <FragmentRenderer extension-point="task-detail.field.sidebar" :context="taskContext" />
    </div>

    <!-- Tab 面板 -->
    <el-tabs>
      <el-tab-pane label="详情">...</el-tab-pane>
      <el-tab-pane label="评论">...</el-tab-pane>
      <!-- ✅ 插件扩展点：Tab -->
      <FragmentRenderer extension-point="task-detail.tabs" :context="taskContext" />
    </el-tabs>
  </div>
</template>
```

### 4.3 状态管理（Pinia）

```
stores/
├── user.ts           # 用户信息、认证状态
├── project.ts       # 当前项目上下文
├── task.ts           # 任务列表、筛选条件、分页
├── board.ts          # 看板列、拖拽状态
├── notification.ts    # 通知列表、未读数
└── plugin.ts         # 已加载插件列表、Fragment 注册表
```

每个 store 遵循「API 驱动」原则，数据仅从 Pinia 流出，页面组件不直接调用 axios：

```typescript
// stores/task.ts
export const useTaskStore = defineStore('task', () => {
  const tasks = ref<Task[]>([]);
  const loading = ref(false);
  const filter = reactive({ projectId: '', status: '', assigneeId: '' });

  // 分页
  const pagination = reactive({ page: 1, size: 20, total: 0 });

  async function fetchTasks() {
    loading.value = true;
    try {
      const { records, total } = await taskApi.list({ ...filter, ...pagination });
      tasks.value = records;
      pagination.total = total;
    } finally {
      loading.value = false;
    }
  }

  // 乐观更新 + 事件发布
  async function updateTask(id: number, patch: Partial<Task>) {
    const idx = tasks.value.findIndex(t => t.id === id);
    if (idx === -1) return;
    
    // 乐观更新
    const old = { ...tasks.value[idx] };
    tasks.value[idx] = { ...tasks.value[idx], ...patch };

    try {
      await taskApi.update(id, patch);
      // 发布领域事件（供其他服务消费）
      eventBus.emit('task:updated', { id, patch });
    } catch {
      // 回滚
      tasks.value[idx] = old;
    }
  }

  return { tasks, loading, filter, pagination, fetchTasks, updateTask };
});
```

### 4.4 看板架构

看板是 LEST 前端最复杂的交互模块：

```
board/
├── BoardView.vue           # 看板主视图
├── BoardColumn.vue         # 单列（列头 + 卡片列表 + 添加卡片）
├── TaskCard.vue            # 任务卡片（可拖拽）
├── SwimLane.vue            # 泳道（V3.0）
├── QuickFilterBar.vue      # 快捷筛选栏
├── CardColorRules.vue      # 卡片颜色规则（V3.0）
└── useBoardDrag.ts        # 拖拽逻辑（@vueuse/core + 手写）
```

看板使用**双层拖拽**：手写方案处理列内排序和列间拖拽：

```typescript
// useBoardDrag.ts
export function useBoardDrag() {
  const { el: boardRef } = useElementSize();
  const activeCard = ref<Task | null>(null);

  // 开始拖拽
  function onDragStart(task: Task) {
    activeCard.value = task;
    // 克隆拖拽预览
    setDragImage(createPreview(task), 0, 0);
  }

  // 跨列拖拽（放下）
  function onDrop(taskId: number, targetColumn: string, targetIndex: number) {
    const patch = { status: targetColumn, position: targetIndex };
    taskStore.updateTask(taskId, patch);
  }

  // WIP 限制检查（V3.0）
  function checkWipLimit(columnId: string): boolean {
    const column = boardStore.columns.find(c => c.id === columnId);
    if (column.wipLimit && column.tasks.length >= column.wipLimit) {
      ElMessage.warning(`列 "${column.name}" 已达 WIP 上限 (${column.wipLimit})`);
      return false;
    }
    return true;
  }

  return { onDragStart, onDrop, checkWipLimit };
}
```

---

## 5. 插件系统架构

### 5.1 前后端插件边界

```
插件 (Plugin)
├── 后端部分
│   ├── Spring Boot Starter（自动装配）
│   ├── 数据库 Schema（独立 DB 或表前缀）
│   ├── REST API（挂载在 /api/plugin/{pluginId}/*）
│   └── Kafka 事件消费者
│
└── 前端部分
    ├── Vue 组件（Fragment）
    ├── manifest.json（声明扩展点）
    └── 可选：前端路由、Pinia store
```

### 5.2 后端插件隔离

后端插件通过 Spring Boot 自动装配实现隔离：

```java
// lest-plugin/PluginManager.java
public class PluginManager {
    public void install(PluginPackage pkg) {
        // 1. 验证 manifest.json 签名
        verifySignature(pkg.getSignature());

        // 2. 解析 manifest.json
        PluginManifest manifest = parseManifest(pkg.getManifest());

        // 3. 加载 Spring 上下文（独立 ClassLoader）
        URL[] urls = pkg.getJarUrls().toArray(new URL[0]);
        ClassLoader pluginClassLoader = new URLClassLoader(urls, getParent());
        
        // 4. 注册 Spring 上下文（Bean 不污染主应用）
        GenericApplicationContext pluginContext = new GenericApplicationContext();
        pluginContext.setClassLoader(pluginClassLoader);
        // ... 加载 plugin 的 @Configuration 类
        
        // 5. 注册 API 路由
        apiGateway.registerRoutes(manifest.getRoutes());
        
        // 6. 注册 Extension Point（前端路由映射）
        extensionRegistry.register(manifest.getExtensionPoints());
        
        pluginContexts.put(manifest.getId(), pluginContext);
    }
}
```

### 5.3 插件 manifest.json 契约

```json
{
  "id": "lest-worklog",
  "version": "1.0.0",
  "name": "工时管理",
  "description": "任务工时记录、统计与审批流",
  "author": "LEST Team",
  "license": "MIT",
  "minAppVersion": "1.0.0",
  "permissions": ["task:read", "task:write"],
  "contributes": {
    "routes": [
      {
        "path": "/api/plugin/lest-worklog/worklog",
        "service": "lest-worklog",
        "methods": ["GET", "POST", "PUT", "DELETE"]
      }
    ],
    "extensionPoints": [
      {
        "id": "task-detail.field.sidebar",
        "label": "工时信息",
        "order": 10
      },
      {
        "id": "task-detail.tabs",
        "label": "工时记录",
        "order": 20
      }
    ],
    "events": [
      "task.created",
      "task.status.changed"
    ]
  },
  "dependencies": {
    "lest-core": ">=1.0.0"
  }
}
```

---

## 6. 数据流架构

### 6.1 任务创建数据流

```
用户点击"创建任务"
    │
    ▼
TaskDetailView.vue
  ├── 表单校验
  ├── taskStore.createTask(formData)
  │       │
  │       ▼
  │   POST /api/v1/task
  │   lest-task (POST /tasks)
  │       │
  │       ├── Validate (Issue Type Scheme 检查)
  │       ├── Save task → task_db.task
  │       │
  │       ▼ Kafka 事件
  │   "task-created" 事件
  │       │
    │       ├── lest-notification 消费
    │       │   ├── 发送站内通知（通知者、被分配者）
    │       │   └── 发送邮件（如用户订阅）
    │       │
    │       ├── lest-ai 消费
    │       │   └── AI 任务分类建议（异步）
    │       │
    │       └── lest-project 消费
    │           └── 更新项目最后活动时间
    │
    ▼ TaskCreatedEvent 返回
taskStore.tasks.unshift(newTask)
    │
    ▼
乐观更新 UI → WebSocket 推送确认
```

### 6.2 WebSocket 实时推送

LEST 使用 WebSocket 实现多人实时协作：

```java
// lest-task/WebSocketConfig.java
@Configuration
public class WebSocketConfig {
    @Bean
    public WebSocketHandler taskBroadcastHandler() {
        return new TaskBroadcastHandler();
    }
}

// TaskBroadcastHandler — 广播任务变更到所有订阅者
public class TaskBroadcastHandler extends TextWebSocketHandler {
    public void broadcastTaskUpdate(Long projectId, TaskUpdatedEvent event) {
        String message = Json.encode(new WSMessage(
            "TASK_UPDATED", projectId, event
        ));
        // 发送给所有订阅了该 projectId 的 WebSocket 连接
        sessions.values().stream()
            .filter(s -> s.projectId.equals(projectId))
            .forEach(s -> s.sendMessage(message));
    }
}
```

---

## 7. 部署架构

### 7.1 开发环境

> **V0.3 变更**：应用服务（Gateway / Auth / System / Project / Task / Release / File / Job）通过 **JAR 模式**启动，无需 Docker 镜像构建。基础设施（MySQL / Redis / Nacos / MinIO / Kafka）通过 `docker-compose.yml` 启动。
>
> 启动方式：
> - 基础设施：`cd backend/docker && docker-compose up -d`
> - 应用服务：`cd backend/docker && java -jar jar/lest-*.jar` 或在 IDE 中直接运行 Application 主类

```
docker-compose.yml
├── lest-mysql:8.0          # 主数据库
├── lest-redis:7-alpine    # 缓存
├── lest-kafka:3.7.0        # 消息队列（KRaft 单节点）
├── lest-nacos:v3.0.2       # 服务注册 + 配置中心
└── lest-minio:latest      # 对象存储
```

### 7.2 生产环境（Kubernetes）

```
┌────────────────────────────────────────────────────────────────────┐
│                          CDN / WAF                                 │
│                          (Cloudflare / 阿里云)                      │
└────────────────────────────────────┬─────────────────────────────┘
                                     │ :443 HTTPS
                    ┌────────────────▼────────────────┐
                    │       Kubernetes Ingress          │
                    │   (APISIX / Kong)                │
                    │   JWT 验证 · 限流 · WAF          │
                    └────────────────┬─────────────────┘
                                     │ :443
        ┌─────────────────────────────┼─────────────────────────────┐
        │                             │                             │
┌──────▼──────┐          ┌──────────▼──────────┐       ┌────────▼────────┐
│ lest-gateway│          │   lest-auth          │       │ lest-project    │
│  (2 副本)  │          │   (2 副本)           │       │  (2 副本)      │
│  SCG 4.3.4 │          │  auth_db (独立 PVC) │       │  project_db     │
└─────────────┘          └──────────────────────┘       └────────────────┘
        │                         │                             │
┌──────▼──────┐          ┌──────────▼──────────┐       ┌────────▼────────┐
│  lest-task   │          │  lest-notification   │       │   lest-file    │
│  (2 副本)  │          │   (2 副本)          │       │   (2 副本)    │
│  task_db     │          │  notification_db     │       │   file_db      │
└─────────────┘          └──────────────────────┘       └────────────────┘
        │                         ▲                             │
        └─────────────────────────┴─────────────────────────────┘
                                 │ Kafka
                         ┌───────▼───────┐
                         │ Kafka 集群     │
                         │ (3 节点 KRaft) │
                         └────────────────┘
```

### 7.3 插件安装部署

```
Plugin Package (ZIP)
├── manifest.json
├── backend.jar           # Spring Boot Starter
├── frontend/             # 前端资源
│   ├── manifest.json
│   ├── chunks/           # 代码分割
│   └── assets/
└── migrations/          # Flyway 迁移脚本（如需独立 DB）
```

插件上传 → lest-plugin 服务验证 → 解压到 `/data/plugins/{pluginId}/` → Spring 上下文加载 → Webpack chunks 注册到 Vite → 前端按需加载。

---

## 8. 关键设计决策（ADR）

### ADR-001: 为什么不用 MyBatis-Plus 增强（而用原生 MyBatis）

**决策**：使用原生 MyBatis XML + MyBatis-Plus 基础增强（分页、代码生成）。

**理由**：避免 MyBatis-Plus 的链路级连查询（如 ` IService.save()` 自动关联插入）引入隐藏耦合；所有跨表操作显式写 SQL，团队对查询性能有完全掌控。

---

### ADR-002: 为什么用 Kafka 而非 RabbitMQ

**决策**：使用 Apache Kafka 4.3.0（KRaft 模式）。

**理由**：Kafka 的 Topic 持久化和回溯消费能力（从任意 offset 消费）对审计和重放至关重要；与 Nacos 的服务注册一致性对齐；KRaft 模式消除了 ZooKeeper 依赖。

---

### ADR-003: 前端插件用 Webpack chunks 而非 iframe

**决策**：使用 Webpack 动态 import（code-split chunk）实现插件隔离。

**理由**：iframe 隔离过强（无法共享主题变量、无法跨插件拖拽）；code-split chunk 共享 Vue 运行时和 CSS 变量（Element Plus 主题），同时通过 ClassLoader 隔离插件 JS 命名空间；manifest.json 中的 `sandbox: true` 可启用更强的沙箱隔离。

---

### ADR-004: JWT 为什么用 stateless + 黑名单而非服务端存储 Token

**决策**：JWT 无状态验证，Token 黑名单存储在 Redis（数据库持久化备份）。

**理由**：
- 每次请求验证 Token 只需签名校验，O(1)，无需数据库查询
- Token 黑名单（用于登出）只写入 Redis，容量小（登出用户量有限），读写性能极高
- 数据库 `token_blacklist` 表作为 Redis 的持久化备份，Redis 宕机时可从 DB 恢复
- 相比 `auth_token` 表遍历匹配哈希的 O(n) 方案，性能提升显著

---

## 版本历史

| 版本 | 日期 | 修改内容 | 作者 |
|------|------|---------|------|
| V1.0 | 2026-06-03 | 初始版本 | 架构师 |
| V1.1 | 2026-06-03 | 修复 Java 25 → 21，sealed interface 移除；TypeScript JSX.Element → ComponentType；FragmentRenderer slot 修复；DndKit 引用移除；task_db/project_db/notification_db 补充缺失表；Gradle → Maven；JWT stateless + 黑名单方案；添加 @RequireProjectRole/@FieldPermission 注解定义；添加 Plugin API 路由说明；Kafka 消费者组隔离说明 | 架构师 |
| V1.2 | 2026-06-03 | lest-common-plugin 模块实现（Plugin 接口 + PluginManager + AutoConfiguration）；lest-common-mq 模块实现（KafkaMessageSender + LestMessage + Topic 常量 + EventListener）；@RequireProjectRole AOP 切面实现；ProjectRoleChecker 解耦接口避免循环依赖；DDL migration_003（is_platform_admin + task_no）；API 路径前缀规范（/api/v1）；分页接口补充；ReleasePlan.status 注释修复 | 架构师 |
| V1.3 | 2026-06-03 | 后端编译错误全面清理（修复 gateway RouterFunction、创建 lest-task 缺失 Domain 类如 IssueLink/Attachment/TaskVote/AutomationRule 等、补全 Mapper 接口方法、修复 lest-system StringUtils 冲突、创建 lest-api-system SysFile + RemoteFileService）；AutoConfiguration.imports 清理（移除已删除工厂引用）；OA 数据表（sys_dept/sys_role/sys_menu/sys_post 等 15 张）添加 ⚠️ DEPRECATED 注释；Docker 部署脚本全面修复（docker-compose.yml 重写为 JAR 模式 + MySQL/Redis/Nacos/MinIO/Kafka 容器；build-and-deploy.sh 重写为编译打包；bin/*.sh 所有脚本重写为 JAR 启动模式）；DataScopeAspect + @DataScope 添加 ⚠️ DEPRECATED 注释 | 架构师 |
| V1.4 | 2026-06-03 | API 路径全面统一（TaskController → /api/v1/task、ProjectController → /api/v1/project、SysUserController → /api/v1/user、SysProfileController → /api/v1/auth、SysDashboardController → /api/v1/dashboard、SysFileController → /api/v1、SysJobController → /api/v1/job、WebhookController → /api/v1/webhook、LabelController → /api/v1）；前端 API 全面对齐（board.ts 修复看板 /api/v1/task/board/{projectId}、auth.ts 修复 /api/v1/auth/profile 路径、新增 iteration.ts/milestone.ts/label.ts）；Gateway routes 重写为 /api/v1 直接转发（去 StripPrefix）；vite.config.ts proxy /api → localhost:8080 | 架构师 |
