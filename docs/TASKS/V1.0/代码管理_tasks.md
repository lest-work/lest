# 代码管理开发任务列表

## 模块上下文

- **微服务模块**：`lest-code`（端口: 8004）
- **主目录**：`backend/lest-modules/lest-code`
- **包路径**：`com.lest.code`
- **数据库 Schema**：`lest_code`（Flyway 目录：`src/main/resources/db/migration/code`）
- **前端 PC 目录**：`frontend-pc/src/views/code/`

---

## 1. 数据库迁移任务（DDL Tasks）

| 任务 ID | 类型 | 目标文件 | 详细实现细节与约束规范 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-CODE-DDL-101** | DDL | `V1.0.0__init_repository_table.sql` | 创建 `code_repository` 表：`id`(BIGINT PK)、`project_id`(BIGINT NOT NULL)、`provider`(VARCHAR 16, github/gitlab)、`owner`(VARCHAR 128)、`repo`(VARCHAR 128)、`full_name`(VARCHAR 256)、`description`(VARCHAR 512)、`default_branch`(VARCHAR 64)、`language`(VARCHAR 32)、`stars`(INT DEFAULT 0)、`forks`(INT DEFAULT 0)、`access_token`(VARCHAR 512, AES加密)、`refresh_token`(VARCHAR 512, AES加密)、`token_expires_at`(DATETIME)、`last_sync_at`(DATETIME)、`created_at`、`updated_at`、`deleted`(TINYINT DEFAULT 0)。建立索引 `idx_project_id`、唯一索引 `uk_repo`(provider, owner, repo)。 | P0 | 待启动 | 未测试 |
| **TSK-CODE-DDL-102** | DDL | `V1.0.1__init_pull_request_table.sql` | 创建 `code_pull_request` 表：`id`(BIGINT PK)、`repository_id`(BIGINT NOT NULL)、`remote_id`(VARCHAR 64)、`number`(INT)、`title`(VARCHAR 512)、`description`(TEXT)、`status`(VARCHAR 16, open/closed/merged)、`source_branch`(VARCHAR 128)、`target_branch`(VARCHAR 128)、`author_username`(VARCHAR 64)、`author_avatar`(VARCHAR 512)、`additions`(INT DEFAULT 0)、`deletions`(INT DEFAULT 0)、`review_status`(VARCHAR 16)、`ci_status`(VARCHAR 16)、`merged_at`(DATETIME)、`closed_at`(DATETIME)、`created_at`、`updated_at`。建立索引 `idx_repository_id`、`idx_number`、`idx_status`。 | P0 | 待启动 | 未测试 |
| **TSK-CODE-DDL-103** | DDL | `V1.0.2__init_oauth_state_table.sql` | 创建 `code_oauth_state` 表：`id`(BIGINT PK)、`state`(VARCHAR 64, UNIQUE)、`user_id`(BIGINT)、`provider`(VARCHAR 16)、`project_id`(BIGINT)、`expires_at`(DATETIME)、`created_at`。用于 OAuth2 授权流程的 state 参数防 CSRF 校验，TTL 10分钟。 | P0 | 待启动 | 未测试 |

---

## 2. 后端 API 任务

### 2.1 仓库管理接口

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-CODE-API-101** | `RepositoryController` | `GET /code/repository/page` | Header: Bearer Token；Query: `projectId`(必填)、`provider`(可选)、`page`、`size` | `{ records: [{ id, provider, owner, repo, fullName, description, defaultBranch, language, stars, forks, lastCommitAt }], total, page, size }` | 查询项目关联的仓库列表；`lastCommitAt` 从 `last_sync_at` 取值。 | P0 | 待启动 | 未测试 |
| **TSK-CODE-API-102** | `RepositoryController` | `POST /code/repository` | Body: `{ provider, projectId, owner, repo }` | `{ id, provider, owner, repo, fullName }` | 1. 校验仓库未被关联，重复抛 `REPOSITORY_ALREADY_LINKED(10001)`；2. 调用对应 GitProvider 接口验证仓库存在且有访问权限；3. 插入 `code_repository`；4. 异步同步仓库基本信息（stars/forks/language）。 | P0 | 待启动 | 未测试 |
| **TSK-CODE-API-103** | `RepositoryController` | `DELETE /code/repository/{id}` | Path: `id` | `{ code: 200, data: null }` | 软删除仓库关联；不删除已同步的 MR 和提交记录（保留历史）。 | P1 | 待启动 | 未测试 |
| **TSK-CODE-API-104** | `OAuthController` | `GET /code/oauth/{provider}/authorize` | Path: `provider`(github/gitlab)；Query: `projectId` | 302 重定向到 GitHub/GitLab OAuth 授权页 | 生成随机 state 存入 `code_oauth_state`（TTL 10分钟）；构造 OAuth2 授权 URL 并重定向。 | P0 | 待启动 | 未测试 |
| **TSK-CODE-API-105** | `OAuthController` | `GET /code/oauth/{provider}/callback` | Path: `provider`；Query: `code`、`state` | 302 重定向到前端页面（带成功/失败参数） | 1. 校验 state 有效性，防 CSRF；2. 用 code 换取 access_token（调用 GitHub/GitLab Token 接口）；3. AES 加密后存入 `code_repository.access_token`；4. 重定向到前端。 | P0 | 待启动 | 未测试 |

### 2.2 MR/PR 接口

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-CODE-API-201** | `PullRequestController` | `GET /code/pull-request/page` | Query: `repositoryId`(必填)、`status`(open/closed/merged/all)、`authorUsername`(可选)、`keyword`(可选)、`page`、`size` | `{ records: [{ id, number, title, status, sourceBranch, targetBranch, author: { username, avatar }, additions, deletions, reviewStatus, ciStatus, createdAt, updatedAt }], total, page, size }` | 查询 `code_pull_request` 表；支持多条件过滤；按 `updated_at` 倒序。 | P0 | 待启动 | 未测试 |
| **TSK-CODE-API-202** | `PullRequestController` | `GET /code/pull-request/{id}` | Path: `id` | `{ id, number, title, description, status, sourceBranch, targetBranch, author, additions, deletions, reviewStatus, ciStatus, commits[], files[], reviews[], linkedTasks[], createdAt, updatedAt }` | 查询 MR 详情；commits/files/reviews 通过调用 GitProvider 接口实时获取（或缓存）；linkedTasks 通过 `task_commit`/`task_mr` 关联表查询。 | P0 | 待启动 | 未测试 |

### 2.3 提交记录接口

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-CODE-API-301** | `CommitController` | `GET /code/commit/page` | Query: `repositoryId`(必填)、`branch`(可选)、`authorUsername`(可选)、`startDate`、`endDate`、`page`、`size` | `{ records: [{ sha, shortSha, message, author: { username, avatar }, changedFiles, additions, deletions, committedAt, linkedTasks[] }], total, page, size }` | 调用 GitProvider 接口获取提交列表；`linkedTasks` 通过 `task_commit` 表关联查询。 | P0 | 待启动 | 未测试 |

### 2.4 Webhook 接收接口

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-CODE-API-401** | `WebhookController` | `POST /webhook/github` | Header: `X-Hub-Signature-256`、`X-GitHub-Event`；Body: GitHub Webhook Payload | `{ code: 200 }` | 1. 验证 HMAC-SHA256 签名，失败返回 401；2. 根据 `X-GitHub-Event` 分发处理：push → 解析 commit_message 自动关联任务；pull_request opened/merged/closed → 更新 `code_pull_request` 状态，向任务模块发送事件；3. 异步处理，立即返回 200。 | P0 | 待启动 | 未测试 |
| **TSK-CODE-API-402** | `WebhookController` | `POST /webhook/gitlab` | Header: `X-Gitlab-Token`；Body: GitLab Webhook Payload | `{ code: 200 }` | 1. 验证 Token；2. 根据 `object_kind` 分发处理：push → 自动关联任务；merge_request → 更新 MR 状态；3. 异步处理。 | P0 | 待启动 | 未测试 |

---

## 3. 前端 PC 端任务

### 3.1 仓库列表页

| 任务 ID | 页面/组件路径 | 路由路径 | 页面元素 | 交互与 API 绑定 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-CODE-FE-101** | `@/views/code/repository/index.vue` | `/code/repository` | 顶部：平台筛选下拉（全部/GitHub/GitLab）、搜索框、关联仓库按钮；列表：仓库名称（带平台图标）、描述、默认分支、主要语言标签、星标数、Fork 数、最后提交时间、操作列（查看 MR 按钮、取消关联按钮）；底部：分页组件 | 1. 页面加载调用 `GET /code/repository/page`；2. 关联仓库按钮打开 `RepositoryLinkDialog`；3. 查看 MR 按钮跳转 `/code/pull-request?repositoryId={id}`；4. 取消关联弹出确认框后调用 `DELETE /code/repository/{id}`。 | P0 | 待启动 | 未测试 |
| **TSK-CODE-FE-102** | `@/views/code/repository/components/RepositoryLinkDialog.vue` | — | 弹窗：平台选择（GitHub/GitLab 单选）、仓库拥有者输入框、仓库名称输入框、OAuth 授权按钮（跳转授权）、确认关联按钮 | 1. 点击 OAuth 授权按钮跳转 `GET /code/oauth/{provider}/authorize`；2. 授权回调后自动填充 owner/repo；3. 提交调用 `POST /code/repository`。 | P0 | 待启动 | 未测试 |

### 3.2 MR/PR 列表页

| 任务 ID | 页面/组件路径 | 路由路径 | 页面元素 | 交互与 API 绑定 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-CODE-FE-201** | `@/views/code/pull-request/index.vue` | `/code/pull-request` | 顶部：仓库选择下拉、状态 Tab（全部/开放/已合并/已关闭）、作者筛选、关键字搜索框；列表：MR 图标、编号、标题、状态标签（颜色区分）、源分支→目标分支、作者头像+名称、代码变更量（+绿/-红）、审查状态标签、CI 状态图标、创建时间；底部：分页组件 | 1. 页面加载调用 `GET /code/pull-request/page`；2. 切换 Tab 传入对应 status 参数；3. 点击 MR 行跳转 `/code/pull-request/{id}`。 | P0 | 待启动 | 未测试 |

### 3.3 MR/PR 详情页

| 任务 ID | 页面/组件路径 | 路由路径 | 页面元素 | 交互与 API 绑定 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-CODE-FE-301** | `@/views/code/pull-request/detail.vue` | `/code/pull-request/:id` | 顶部：MR 标题、状态标签、源分支→目标分支、作者信息；信息栏：代码变更量、审查状态、CI 状态；Tab 页：变更文件列表（文件名+变更行数）、提交记录列表（SHA+消息+作者+时间）、审查意见列表、关联任务列表；底部：AI 代码审查结果区块（若有） | 1. 页面加载调用 `GET /code/pull-request/{id}`；2. 关联任务列表中的任务可点击跳转任务详情；3. AI 审查结果调用 `GET /ai/code-review/mr/{mrId}`（若存在则展示）。 | P0 | 待启动 | 未测试 |