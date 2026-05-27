# CI/CD 持续集成开发任务列表

## 模块上下文

- **微服务模块**：`lest-ci`（端口: 8007）
- **主目录**：`backend/lest-modules/lest-ci`
- **包路径**：`com.lest.ci`
- **数据库 Schema**：`lest_ci`（Flyway 目录：`src/main/resources/db/migration/ci`）
- **前端 PC 目录**：`frontend-pc/src/views/ci/`

---

## 1. 数据库迁移任务（DDL Tasks）

| 任务 ID | 类型 | 目标文件 | 详细实现细节与约束规范 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-CI-DDL-101** | DDL | `V1.0.0__init_ci_provider_table.sql` | 创建 `ci_provider` 表：`id`(BIGINT PK)、`name`(VARCHAR 64)、`type`(VARCHAR 16, jenkins/gitlab/github)、`config`(JSON, 存储 URL/Token 等，Token AES 加密)、`enabled`(TINYINT DEFAULT 1)、`created_at`、`updated_at`。 | P0 | 待启动 | 未测试 |
| **TSK-CI-DDL-102** | DDL | `V1.0.1__init_ci_build_table.sql` | 创建 `ci_build` 表：`id`(BIGINT PK)、`repository_id`(BIGINT NOT NULL)、`provider_id`(BIGINT NOT NULL)、`job_name`(VARCHAR 128)、`build_number`(INT)、`status`(VARCHAR 16, queued/running/success/failed/cancelled)、`branch`(VARCHAR 128)、`commit_sha`(VARCHAR 64)、`commit_message`(VARCHAR 512)、`trigger_type`(VARCHAR 16, manual/push/schedule/mr)、`trigger_user_id`(BIGINT)、`duration`(INT, 秒)、`queued_duration`(INT)、`coverage`(DECIMAL 5,2)、`queued_at`(DATETIME)、`started_at`(DATETIME)、`finished_at`(DATETIME)、`created_at`。建立索引 `idx_repository_id`、`idx_status`、`idx_branch`、`idx_commit_sha`。 | P0 | 待启动 | 未测试 |
| **TSK-CI-DDL-103** | DDL | `V1.0.2__init_ci_deployment_table.sql` | 创建 `ci_deployment` 表：`id`(BIGINT PK)、`project_id`(BIGINT NOT NULL)、`build_id`(BIGINT NOT NULL)、`environment`(VARCHAR 16, dev/test/staging/production)、`strategy`(VARCHAR 16, rolling/blue_green/canary)、`status`(VARCHAR 16, pending/deploying/success/failed/rolled_back)、`deployer_id`(BIGINT)、`started_at`(DATETIME)、`finished_at`(DATETIME)、`created_at`。建立索引 `idx_project_id`、`idx_build_id`、`idx_status`。 | P1 | 待启动 | 未测试 |

---

## 2. 后端 API 任务

### 2.1 CI 提供商配置接口

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-CI-API-101** | `CIProviderController` | `GET /ci/provider` | Header: Bearer Token（需 admin 权限） | `{ data: [{ id, name, type, enabled, createdAt }] }` | 查询所有 CI 提供商配置；config 中的 Token/密码字段脱敏返回。 | P0 | 待启动 | 未测试 |
| **TSK-CI-API-102** | `CIProviderController` | `POST /ci/provider` | Body: `{ name, type, config: { url, apiToken, ... } }` | `{ id, name, type }` | config 中的 apiToken 字段 AES 加密存储；插入 `ci_provider`。 | P0 | 待启动 | 未测试 |
| **TSK-CI-API-103** | `CIProviderController` | `PUT /ci/provider/{id}` | Path: `id`；Body: `{ name, config, enabled }` | `{ code: 200, data: null }` | 更新提供商配置；apiToken 为空时不更新（保留原值）。 | P0 | 待启动 | 未测试 |
| **TSK-CI-API-104** | `CIProviderController` | `POST /ci/provider/{id}/test` | Path: `id` | `{ success: boolean, message: string, latencyMs: number }` | 调用对应 CIClient 的 `testConnection()` 方法；返回连通性和延迟。 | P0 | 待启动 | 未测试 |

### 2.2 构建管理接口

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-CI-API-201** | `BuildController` | `GET /ci/build/page` | Query: `repositoryId`(必填)、`branch`(可选)、`status`(可选)、`startDate`、`endDate`、`page`、`size` | `{ records: [{ id, buildNumber, status, provider, jobName, branch, commitSha, commitMessage, trigger: { type, userId, username }, duration, coverage, startedAt, finishedAt }], total, page, size }` | 查询 `ci_build` 表；按 `started_at` 倒序；trigger.username 通过 Feign 聚合用户信息。 | P0 | 待启动 | 未测试 |
| **TSK-CI-API-202** | `BuildController` | `GET /ci/build/{id}` | Path: `id` | `{ id, buildNumber, status, provider, jobName, branch, commitSha, commitMessage, trigger, duration, coverage, stages: [{ name, status, duration, steps[] }], artifacts: [{ name, size, downloadUrl }], startedAt, finishedAt }` | 查询构建详情；stages 和 artifacts 通过调用 CIClient 实时获取（Jenkins API / GitLab API）。 | P0 | 待启动 | 未测试 |
| **TSK-CI-API-203** | `BuildController` | `POST /ci/build/trigger` | Body: `{ repositoryId, branch, parameters: { key: value } }` | `{ buildNumber, queueId }` | 1. 查询仓库对应的 CI 提供商；2. 调用 CIClient.triggerBuild()；3. 插入 `ci_build` 记录（status=queued）；4. 返回构建编号。 | P1 | 待启动 | 未测试 |
| **TSK-CI-API-204** | `BuildController` | `POST /ci/build/{id}/cancel` | Path: `id` | `{ code: 200, data: null }` | 调用 CIClient.cancelBuild()；更新 `ci_build.status` 为 cancelled。 | P1 | 待启动 | 未测试 |

### 2.3 部署管理接口

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-CI-API-301** | `DeploymentController` | `GET /ci/deployment/page` | Query: `projectId`(必填)、`environment`(可选)、`status`(可选)、`page`、`size` | `{ records: [{ id, buildId, buildNumber, environment, strategy, status, deployer: { userId, username }, startedAt, finishedAt }], total, page, size }` | 查询 `ci_deployment` 表；按 `created_at` 倒序。 | P1 | 待启动 | 未测试 |
| **TSK-CI-API-302** | `DeploymentController` | `POST /ci/deployment` | Body: `{ projectId, buildId, environment, strategy }` | `{ deploymentId, status: "deploying" }` | 1. 校验 buildId 对应构建 status 为 success；2. 插入 `ci_deployment`（status=deploying）；3. 异步执行部署逻辑；4. 发送 `DEPLOYMENT_SUCCESS` 或 `DEPLOYMENT_FAILED` 通知。 | P1 | 待启动 | 未测试 |

### 2.4 Webhook 接收接口

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-CI-API-401** | `CIWebhookController` | `POST /webhook/ci/jenkins` | Header: `X-Jenkins-Token`；Body: Jenkins Webhook Payload | `{ code: 200 }` | 1. 验证 Token；2. 解析 build.phase 和 build.status；3. upsert `ci_build` 记录；4. 构建完成时向任务模块发送 `POST /webhook/ci/build` 事件；5. 向通知模块发送 `BUILD_SUCCESS` 或 `BUILD_FAILED` 事件。 | P0 | 待启动 | 未测试 |
| **TSK-CI-API-402** | `CIWebhookController` | `POST /webhook/ci/gitlab` | Header: `X-Gitlab-Token`；Body: GitLab Pipeline Webhook Payload | `{ code: 200 }` | 1. 验证 Token；2. 解析 object_kind=pipeline 的 status；3. upsert `ci_build`；4. 同上发送跨模块事件。 | P0 | 待启动 | 未测试 |

---

## 3. 前端 PC 端任务

### 3.1 构建列表页

| 任务 ID | 页面/组件路径 | 路由路径 | 页面元素 | 交互与 API 绑定 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-CI-FE-101** | `@/views/ci/build/index.vue` | `/ci/build` | 顶部：仓库选择下拉、状态筛选（全部/成功/失败/运行中）、分支筛选输入框、触发构建按钮；构建列表：构建编号、状态图标（颜色区分）、仓库/分支、触发者头像+名称、触发时间、耗时、覆盖率（若有）、操作列（查看详情按钮、重新构建按钮）；底部：分页组件 | 1. 页面加载调用 `GET /ci/build/page`；2. 运行中的构建每10秒轮询刷新状态；3. 触发构建按钮打开 `BuildTriggerDialog`；4. 点击构建行跳转 `/ci/build/{id}`。 | P0 | 待启动 | 未测试 |

### 3.2 构建详情页

| 任务 ID | 页面/组件路径 | 路由路径 | 页面元素 | 交互与 API 绑定 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-CI-FE-201** | `@/views/ci/build/detail.vue` | `/ci/build/:id` | 顶部：构建编号、状态标签、重新构建按钮、取消按钮（运行中时显示）；信息摘要卡片：状态/耗时/触发者/分支/提交 SHA/队列等待时长；流水线阶段列表（可展开查看步骤日志）；Tab 页：控制台输出（虚拟滚动）、构建产物列表（文件名+大小+下载按钮）、测试报告（覆盖率+失败用例）、变更记录（关联提交列表） | 1. 页面加载调用 `GET /ci/build/{id}`；2. 运行中时每5秒轮询刷新；3. 下载产物按钮触发文件下载；4. 重新构建调用 `POST /ci/build/trigger`；5. 取消构建调用 `POST /ci/build/{id}/cancel`。 | P0 | 待启动 | 未测试 |

### 3.3 CI 提供商配置页（管理员）

| 任务 ID | 页面/组件路径 | 路由路径 | 页面元素 | 交互与 API 绑定 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-CI-FE-301** | `@/views/system/ci-provider.vue` | `/system/ci-provider` | 提供商卡片列表（Jenkins/GitLab CI/GitHub Actions）；每张卡片：提供商名称、启用开关、配置表单（URL/API Token 等）、测试连接按钮、保存按钮 | 1. 页面加载调用 `GET /ci/provider` 回显配置；2. 保存调用 `POST /ci/provider` 或 `PUT /ci/provider/{id}`；3. 测试连接调用 `POST /ci/provider/{id}/test`，展示延迟和结果。 | P0 | 待启动 | 未测试 |