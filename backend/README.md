<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">Lest Platform</h1>
<h4 align="center">基于 Spring Boot 4.0.6 / Spring Cloud 2025.1.0 / Spring Cloud Alibaba 2025.1.0.0 的研发项目管理微服务平台</h4>

## 平台简介

Lest Platform 是一套研发项目管理平台，基于 RuoYi-Cloud 微服务架构开发。

* 后端采用 Spring Boot 4.0.6、Spring Cloud 2025.1.0、Spring Cloud Alibaba 2025.1.0.0。
* 注册中心、配置中心选型 Nacos，权限认证与会话使用 Redis。
* 流量治理采用 Resilience4j + Bucket4j（熔断/限流）。
* 插件体系采用声明式 manifest.json + 扩展点机制，支持热插拔。

## 系统模块

```text
lest-platform
├── lest-gateway                          // API 网关模块 [8080]
├── lest-auth                             // 认证授权中心 [8096]
├── lest-api                              // 接口模块（Feign）
│   └── lest-api-system                  // 系统接口
├── lest-common                           // 通用模块
│   ├── lest-common-core                 // 核心模块（异常/上下文/工具类）
│   ├── lest-common-datasource           // 多数据源
│   ├── lest-common-log                 // 日志记录（AOP 拦截）
│   ├── lest-common-mq                  // 消息队列（Kafka）
│   ├── lest-common-plugin               // 插件机制（manifest/扩展点/注册表）
│   ├── lest-common-redis               // 缓存服务
│   ├── lest-common-security             // 安全模块（JWT/权限/鉴权）
│   ├── lest-common-swagger             // 接口文档（SpringDoc）
│   └── lest-common-sensitive           // 敏感数据脱敏
├── lest-apps                             // Core 服务（V1.0 必需）
│   ├── lest-system                     // 系统管理 [8081]（用户/字典/审计）
│   ├── lest-project                    // 项目管理 [8082]（项目/迭代/里程碑）
│   ├── lest-task                       // 任务管理 [8083]（任务/看板/工时）
│   ├── lest-file                       // 文件服务 [8091]（MinIO/本地存储）
│   ├── lest-notification               // 通知服务 [8084]（Kafka/WebSocket）
│   └── lest-job                        // 定时任务 [9203]（Quartz）
├── lest-apps (extensions profile)        // `-Dinclude.extensions=true`
│   ├── ai                              // AI 服务 [8090]（多模型路由）
│   └── plugin-manager                  // 插件管理 [8088]
├── lest-plugins                          // 官方插件（激活 `-Dinclude.plugins=true` 时编译）
│   ├── lest-release                    // 发布管理 [8087]
│   ├── lest-meeting                   // 会议管理 [8085]
│   └── lest-openapi                    // 开放平台 [8086]
└── docker
    └── mysql/db/01_lest_platform_init.sql  // 数据库初始化脚本
```

## 核心架构

### 插件矩阵架构

Lest Platform 采用**插件矩阵架构**，核心功能通过扩展点机制支持插件扩展：

* **Core 层**（`lest-common-*` + `lest-apps/*`）：平台必须的基础功能，仅这些模块参与 V1.0 发布
* **Extension 层**（`lest-apps` 中通过 `-Dinclude.extensions=true` 激活的服务）：V1.x 预留扩展服务，可在 V1.1+ 按需启用
* **Plugin 层**（`lest-plugins/*`，需 `-Dinclude.plugins=true` 激活）：可选功能扩展，可按需安装/卸载/启用/禁用（V4.0 起交付）
* **ExtensionPoints**：预定义扩展点常量（`ExtensionPoints.java`），插件通过 `manifest.json` 声明对接

| 扩展点 | 说明 | 前端 UI 位置 |
| --- | --- | --- |
| `task.detail.tab` | 任务详情页 Tab | TaskDetailDrawer.vue |
| `project.sidebar.menu` | 项目侧边栏菜单 | AppLayout.vue |

### 项目级权限体系

采用 `@RequireProjectRole` 注解 + `ProjectRoleChecker` 接口的**项目级权限方案**：

* 平台管理员（userId=1）自动绕过所有项目权限检查
* 普通用户通过 `project_member` 表中的角色字段进行项目级鉴权
* 已移除若依遗留的 `@RequiresPermissions` 注解体系

## V1.0 (1.1) 稳定性自检清单

| 项目 | 核心检查点 |
| --- | --- |
| 编译 | `backend` 聚合工程执行 `mvn clean verify`（默认 Core-only），所有 Core 服务均可独立启动 |
| 扩展 | 若需验证 AI/插件管理模块，执行 `mvn clean verify -Dinclude.extensions=true`，确认可独立启动 |
| 前端 | `frontend-pc`、`frontend-h5` 构建通过，API 代理配置与 Gateway 路径一致 |
| API 契约 | Gateway 配置、Controller 路径与 `.devin/rules/05-api-conventions.md` 规范一致；OpenAPI 文档对齐真实返回结构 |
| Schema | `docker/mysql/db/01_lest_platform_init.sql` 与各服务 `domain/mapper` 字段匹配，无缺列/错类型 |
| 初始化数据 | 初始化脚本包含管理员、示例项目、任务基础状态，可一次执行完成 |
| E2E 冒烟 | 登录 → 创建项目/成员 → 创建任务 → 看板拖拽 → 评论 → 通知推送 全链路通过 |

> 官方插件构建需执行 `mvn clean verify -pl backend/plugins -am -Dinclude.plugins=true`。
> 若自检失败，请在对应模块的 `docs/tasks` 中登记阻塞项并标记所属里程碑（V10-STAB-00X）。

## 内置功能

1. 用户管理：用户是系统操作者（平台级）。
2. 角色管理：平台角色（管理员/普通用户）。
3. 字典管理：对系统中经常使用的一些较为固定的数据进行维护。
4. 审计日志：记录所有写操作（操作人/时间/IP/操作类型/资源）。
5. 项目管理：管理研发项目、迭代、里程碑。
6. 任务管理：管理项目任务、看板（拖拽）、甘特图、子任务、依赖关系。
7. 发布管理：管理发布计划、制品、关联问题（版本变更记录）。
8. 定时任务：在线（添加、修改、删除）任务调度包含执行结果日志。
9. 文件管理：MinIO 对象存储或本地文件系统。
10. 通知系统：Kafka 事件驱动 + WebSocket 实时推送。
11. AI 服务：多模型统一网关（OpenAI/Claude/本地模型）。

## 快速开始

### 环境依赖

* JDK 21+
* Maven 3.9+
* MySQL 8.0+
* Redis 7+
* Nacos 3.x（可选，用于服务注册与配置中心）

### 启动步骤

1. 创建数据库 `lest_platform`，运行 `docker/mysql/db/01_lest_platform_init.sql` 初始化脚本。
2. 修改各模块 `application.yml` 中的数据库、Redis、Nacos 连接信息。
3. 在 IDE 中运行各模块 Application 主类，或使用 Maven 启动：

```bash
# 编译整个项目
cd backend && mvn clean package -DskipTests

# 启动顺序：Gateway → Auth → Apps → Plugins
```

### 环境变量（可选）

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `NACOS_ENABLED` | `false` | 是否启用 Nacos 服务注册 |
| `REDIS_HOST` | `127.0.0.1` | Redis 主机 |
| `REDIS_PORT` | `6379` | Redis 端口 |
