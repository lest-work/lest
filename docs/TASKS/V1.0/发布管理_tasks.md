# 发布管理开发任务列表

## 模块上下文

- **微服务模块**：`lest-release`（端口: 8010）
- **主目录**：`backend/lest-modules/lest-release`
- **包路径**：`com.lest.release`
- **数据库 Schema**：`lest_release`（Flyway 目录：`src/main/resources/db/migration/release`）
- **前端 PC 目录**：`frontend-pc/src/views/release/`

---

## 1. 数据库迁移任务（DDL Tasks）

| 任务 ID | 类型 | 目标文件 | 详细实现细节与约束规范 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-REL-DDL-101** | DDL | `V1.0.0__init_release_version_table.sql` | 创建 `release_version` 表：`id`(BIGINT PK)、`project_id`(BIGINT NOT NULL)、`version`(VARCHAR 32 NOT NULL)、`environment`(VARCHAR 16, dev/test/staging/production)、`status`(VARCHAR 16, planning/ready/pending_approval/approved/deploying/deployed/rolling_back/rolled_back/rejected/cancelled)、`description`(TEXT)、`release_date`(DATE)、`target_build_id`(BIGINT)、`created_at`、`updated_at`。建立索引 `idx_project_id`、唯一索引 `uk_project_version`(project_id, version)。 | P0 | 待启动 | 未测试 |
| **TSK-REL-DDL-102** | DDL | `V1.0.1__init_release_record_approval_tables.sql` | 创建 `release_release` 表：`id`(BIGINT PK)、`version_id`(BIGINT NOT NULL)、`build_id`(BIGINT NOT NULL)、`environment`(VARCHAR 16)、`status`(VARCHAR 16)、`description`(TEXT)、`strategy`(VARCHAR 16)、`deploy_started_at`(DATETIME)、`deploy_finished_at`(DATETIME)、`deploy_logs`(JSON)、`rollback_to_version`(VARCHAR 32)、`rollback_reason`(VARCHAR 512)、`released_by`(BIGINT NOT NULL)、`released_at`(DATETIME)、`created_at`、`updated_at`，建立索引 `idx_version_id`、`idx_status`。创建 `release_approval` 表：`id`(BIGINT PK)、`release_id`(BIGINT NOT NULL)、`approver_id`(BIGINT NOT NULL)、`status`(VARCHAR 16, pending/approved/rejected)、`comment`(VARCHAR 512)、`approved_at`(DATETIME)、`created_at`。 | P0 | 待启动 | 未测试 |
| **TSK-REL-DDL-103** | DDL | `V1.0.2__init_release_change_table.sql` | 创建 `release_change` 表：`id`(BIGINT PK)、`release_id`(BIGINT NOT NULL)、`change_type`(VARCHAR 16, task/mr/commit)、`change_id`(BIGINT NOT NULL)、`created_at`，建立索引 `idx_release_id`。 | P0 | 待启动 | 未测试 |

---

## 2. 后端 API 任务

### 2.1 版本规划接口

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-REL-API-101** | `ReleaseVersionController` | `POST /release/version` | Body: `{ projectId, version, environment, releaseDate?, description? }` | `{ id, version, status: "planning" }` | 1. 校验版本号格式（语义化版本，如 v1.0.0）；2. 校验同项目下版本号唯一，重复抛 `RELEASE_VERSION_CONFLICT(15008)`；3. 插入 `release_version`。 | P0 | 待启动 | 未测试 |
| **TSK-REL-API-102** | `ReleaseVersionController` | `GET /release/version/page` | Query: `projectId`(必填)、`environment`(可选)、`status`(可选)、`page`、`size` | `{ records: [{ id, version, environment, status, releaseDate, description, createdAt }], total, page, size }` | 分页查询版本列表；按 `created_at` 倒序。 | P0 | 待启动 | 未测试 |

### 2.2 发布流程接口

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-REL-API-201** | `ReleaseController` | `GET /release/page` | Query: `projectId`(必填)、`environment`(可选)、`status`(可选)、`page`、`size` | `{ records: [{ id, version, projectName, environment, status, buildId, buildNumber, taskCount, mrCount, releaseBy: { userId, username }, releasedAt, createdAt }], total, page, size }` | 联表查询 `release_release` + `release_version`；taskCount/mrCount 通过 COUNT 聚合 `release_change`。 | P0 | 待启动 | 未测试 |
| **TSK-REL-API-202** | `ReleaseController` | `GET /release/{id}` | Path: `id` | `{ id, version, projectId, environment, status, description, build: { id, buildNumber, status, commitSha }, changes: { tasks[], mergeRequests[] }, approval: { approved, approvedBy, approvedAt, comment }, releaseBy, deployment: { strategy, startedAt, finishedAt, logs[] }, rollback: { available, previousVersion }, releasedAt, createdAt }` | 查询发布详情；changes 通过 `release_change` 关联查询，tasks 通过 Feign 调用任务模块，mergeRequests 通过 Feign 调用代码模块。 | P0 | 待启动 | 未测试 |
| **TSK-REL-API-203** | `ReleaseController` | `POST /release/apply` | Body: `{ versionId, buildId, description, approverId }` | `{ releaseId, status: "pending_approval" }` | 1. 校验 buildId 对应构建 status 为 success，否则抛 `BUILD_NOT_SUCCESS(15006)`；2. 插入 `release_release`（status=pending_approval）；3. 插入 `release_approval`；4. 自动收集关联的 MR 和任务（通过 commitSha 查询 `task_commit` 和 `code_pull_request`）插入 `release_change`；5. 发送 `RELEASE_CREATED` 通知给审批人。 | P0 | 待启动 | 未测试 |
| **TSK-REL-API-204** | `ReleaseController` | `POST /release/{id}/approve` | Path: `id`；Body: `{ approved: boolean, comment? }` | `{ code: 200, data: null }` | 1. 校验当前用户是审批人；2. 更新 `release_approval.status`；3. 若 approved=true，更新 `release_release.status` 为 approved，发送 `RELEASE_APPROVED` 通知；4. 若 approved=false，更新为 rejected，发送 `RELEASE_REJECTED` 通知。 | P0 | 待启动 | 未测试 |
| **TSK-REL-API-205** | `ReleaseController` | `POST /release/{id}/deploy` | Path: `id`；Body: `{ strategy }` | `{ deploymentId, status: "deploying" }` | 1. 校验 status 为 approved，否则抛 `RELEASE_PENDING_APPROVAL(15002)`；2. 更新 status 为 deploying；3. 发送 `RELEASE_DEPLOY_STARTED` 通知；4. 异步执行部署（调用 CI 模块）；5. 完成后更新 status 为 deployed，发送 `RELEASE_DEPLOY_COMPLETED` 通知。 | P0 | 待启动 | 未测试 |
| **TSK-REL-API-206** | `ReleaseController` | `POST /release/{id}/rollback` | Path: `id`；Body: `{ targetVersion, reason }` | `{ rollbackId, status: "rolling_back" }` | 1. 校验 status 为 deployed；2. 更新 status 为 rolling_back，记录 rollback_reason；3. 发送 `RELEASE_ROLLBACK_STARTED` 通知；4. 异步执行回滚；5. 完成后发送 `RELEASE_ROLLBACK_COMPLETED` 通知。 | P0 | 待启动 | 未测试 |

### 2.3 变更内容接口

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-REL-API-301** | `ReleaseChangeController` | `GET /release/{id}/tasks` | Path: `id` | `{ records: [{ taskId, taskTitle, taskType, status, assigneeId, assigneeName, completedAt }], total }` | 查询 `release_change`（change_type=task），通过 Feign 调用任务模块获取任务详情。 | P0 | 待启动 | 未测试 |
| **TSK-REL-API-302** | `ReleaseChangeController` | `GET /release/{id}/merge-requests` | Path: `id` | `{ records: [{ mrId, number, title, status, authorUsername }], total }` | 查询 `release_change`（change_type=mr），通过 Feign 调用代码模块获取 MR 详情。 | P0 | 待启动 | 未测试 |

---

## 3. 前端 PC 端任务

### 3.1 发布列表页

| 任务 ID | 页面/组件路径 | 路由路径 | 页面元素 | 交互与 API 绑定 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-REL-FE-101** | `@/views/release/index.vue` | `/release` | 顶部：环境筛选 Tab（全部/测试/预发/生产）、状态筛选下拉、版本搜索框、版本规划按钮、创建发布按钮；发布列表：版本号、环境标签、状态标签（颜色区分）、变更内容摘要（N任务 N MR）、发布人、发布时间、操作列（详情按钮、审批按钮（待审批时显示）、部署按钮（已审批时显示）、回滚按钮（已部署时显示））；底部：分页组件 | 1. 页面加载调用 `GET /release/page`；2. 审批按钮打开 `ApprovalDialog`；3. 部署按钮打开 `DeployDialog`；4. 回滚按钮打开 `RollbackDialog`；5. 详情按钮跳转 `/release/{id}`。 | P0 | 待启动 | 未测试 |

### 3.2 发布详情页

| 任务 ID | 页面/组件路径 | 路由路径 | 页面元素 | 交互与 API 绑定 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-REL-FE-201** | `@/views/release/detail.vue` | `/release/:id` | 顶部：版本号、环境标签、状态标签、回滚按钮（已部署时显示）；基本信息卡片：状态/发布人/发布时间/构建信息/部署策略；变更内容 Tab：任务列表（标题/类型/状态/负责人）、MR 列表（编号/标题/状态/作者）；审批记录区块：审批人/审批时间/审批意见；部署日志区块：时间线格式展示部署步骤和日志 | 1. 页面加载调用 `GET /release/{id}`；2. 任务列表调用 `GET /release/{id}/tasks`；3. MR 列表调用 `GET /release/{id}/merge-requests`；4. 部署日志实时轮询（部署中时每5秒刷新）。 | P0 | 待启动 | 未测试 |