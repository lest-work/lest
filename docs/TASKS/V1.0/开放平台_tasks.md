# 开放平台开发任务列表

## 模块上下文

- **微服务模块**：`lest-open`（端口: 8013）
- **主目录**：`backend/lest-modules/lest-open`
- **包路径**：`com.lest.open`
- **数据库 Schema**：`lest_open`（Flyway 目录：`src/main/resources/db/migration/open`）
- **前端 PC 目录**：`frontend-pc/src/views/open/`


---

## 1. 数据库迁移任务（DDL Tasks）

| 任务 ID | 类型 | 目标文件 | 详细实现细节与约束规范 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-OPEN-DDL-101** | DDL | `V1.0.0__init_api_token_table.sql` | 创建 `open_api_token` 表：`id`(VARCHAR 32 PK)、`user_id`(BIGINT NOT NULL)、`name`(VARCHAR 128)、`token`(VARCHAR 64, UNIQUE, SHA256 hash 存储)、`scopes`(JSON)、`expires_at`(DATETIME)、`last_used_at`(DATETIME)、`created_at`。建立唯一索引 `uk_token`、普通索引 `idx_user_id`。 | P0 | 待启动 | 未测试 |
| **TSK-OPEN-DDL-102** | DDL | `V1.0.1__init_oauth_tables.sql` | 创建 `open_oauth_application` 表：`id`(VARCHAR 32 PK)、`user_id`(BIGINT NOT NULL)、`name`(VARCHAR 128)、`description`(VARCHAR 512)、`client_id`(VARCHAR 64, UNIQUE)、`client_secret`(VARCHAR 256, AES加密)、`redirect_uris`(JSON)、`scopes`(JSON)、`homepage`(VARCHAR 256)、`logo_url`(VARCHAR 256)、`status`(VARCHAR 16 DEFAULT 'active')、`created_at`、`updated_at`，建立唯一索引 `uk_client_id`、索引 `idx_user_id`。创建 `open_oauth_token` 表：`id`(VARCHAR 32 PK)、`application_id`(VARCHAR 32)、`user_id`(BIGINT)、`access_token`(VARCHAR 256, UNIQUE, hash存储)、`refresh_token`(VARCHAR 256, UNIQUE)、`scopes`(JSON)、`expires_at`(DATETIME)、`refresh_expires_at`(DATETIME)、`revoked`(TINYINT DEFAULT 0)、`created_at`。 | P0 | 待启动 | 未测试 |
| **TSK-OPEN-DDL-103** | DDL | `V1.0.2__init_webhook_tables.sql` | 创建 `open_webhook` 表：`id`(VARCHAR 32 PK)、`user_id`(BIGINT NOT NULL)、`application_id`(VARCHAR 32)、`name`(VARCHAR 128)、`url`(VARCHAR 512)、`secret`(VARCHAR 128)、`events`(JSON)、`headers`(JSON)、`ssl_verify`(TINYINT DEFAULT 1)、`active`(TINYINT DEFAULT 1)、`retry_policy`(JSON)、`created_at`、`updated_at`，建立索引 `idx_user_id`、`idx_active`。创建 `open_webhook_delivery` 表：`id`(VARCHAR 32 PK)、`webhook_id`(VARCHAR 32)、`event_id`(VARCHAR 64)、`event_type`(VARCHAR 64)、`payload`(JSON)、`status`(VARCHAR 16, pending/success/failed)、`status_code`(INT)、`response_body`(TEXT)、`response_time`(INT)、`attempt`(INT DEFAULT 1)、`error_message`(VARCHAR 512)、`next_retry_at`(DATETIME)、`created_at`、`completed_at`，建立索引 `idx_webhook_id`、`idx_status`、`idx_created_at`。 | P0 | 待启动 | 未测试 |
| **TSK-OPEN-DDL-104** | DDL | `V1.0.3__init_api_log_table.sql` | 创建 `open_api_log` 表：`id`(BIGINT PK)、`request_id`(VARCHAR 64, UNIQUE)、`user_id`(BIGINT)、`application_id`(VARCHAR 32)、`token_id`(VARCHAR 32)、`method`(VARCHAR 8)、`path`(VARCHAR 256)、`query_params`(JSON)、`request_body`(TEXT)、`response_status`(INT)、`response_time`(INT)、`ip_address`(VARCHAR 45)、`user_agent`(VARCHAR 256)、`created_at`，建立唯一索引 `uk_request_id`、索引 `idx_user_id`、`idx_created_at`。 | P1 | 待启动 | 未测试 |


---

## 2. 后端 API 任务

### 2.1 API Token 管理接口

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-OPEN-API-101** | `ApiTokenController` | `GET /open/tokens` | Header: Bearer Token | `{ data: [{ id, name, scopes[], expiresAt, lastUsedAt, createdAt }] }` | 查询当前用户的所有 API Token；token 字段不返回（只在创建时返回一次）。 | P0 | 待启动 | 未测试 |
| **TSK-OPEN-API-102** | `ApiTokenController` | `POST /open/tokens` | Body: `{ name, scopes[], expiresIn? }` | `{ id, name, token: "lest_tok_xxx", scopes[], expiresAt, createdAt }` | 1. 生成随机 Token（格式：`lest_tok_` + 32位随机字符串）；2. SHA256 hash 后存入 `open_api_token.token`；3. 明文 Token 只在响应中返回一次，不存储；4. expiresIn 单位为天，为空则永不过期。 | P0 | 待启动 | 未测试 |
| **TSK-OPEN-API-103** | `ApiTokenController` | `DELETE /open/tokens/{tokenId}` | Path: `tokenId` | `{ code: 200, data: null }` | 删除 Token 记录；立即失效（Redis 黑名单或直接删除）。 | P0 | 待启动 | 未测试 |

### 2.2 OAuth2 接口

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-OPEN-API-201** | `OAuthController` | `GET /oauth/authorize` | Query: `client_id`、`redirect_uri`、`response_type=code`、`scope`、`state` | 302 重定向到授权确认页 | 1. 校验 client_id 存在且 status=active；2. 校验 redirect_uri 在应用配置的白名单内；3. 校验 scope 在应用允许范围内；4. 重定向到前端授权确认页（`/oauth/confirm?...`）。 | P0 | 待启动 | 未测试 |
| **TSK-OPEN-API-202** | `OAuthController` | `POST /oauth/authorize/confirm` | Body: `{ clientId, redirectUri, scope, state, approved: boolean }` | 302 重定向到 redirect_uri（带 code 或 error） | 1. 用户确认授权（approved=true）：生成 authorization_code（10分钟有效），存入 Redis；重定向到 `redirect_uri?code=xxx&state=xxx`；2. 用户拒绝：重定向到 `redirect_uri?error=access_denied&state=xxx`。 | P0 | 待启动 | 未测试 |
| **TSK-OPEN-API-203** | `OAuthController` | `POST /oauth/token` | Body: `{ grant_type, client_id, client_secret, code, redirect_uri }` | `{ access_token, token_type: "Bearer", expires_in: 3600, refresh_token, scope }` | 1. 校验 client_id/client_secret；2. 校验 code 有效性（从 Redis 取出，使用后立即删除）；3. 生成 Access Token（1小时）和 Refresh Token（30天）；4. hash 后存入 `open_oauth_token`；5. 返回明文 Token。 | P0 | 待启动 | 未测试 |
| **TSK-OPEN-API-204** | `OAuthController` | `POST /oauth/token/refresh` | Body: `{ grant_type: "refresh_token", client_id, client_secret, refresh_token }` | 同上 | 1. 校验 refresh_token 有效且未撤销；2. 撤销旧 Token；3. 生成新的 Access Token 和 Refresh Token。 | P0 | 待启动 | 未测试 |

### 2.3 OAuth2 应用管理接口

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-OPEN-API-301** | `OAuthApplicationController` | `GET /open/applications` | Header: Bearer Token | `{ data: [{ id, name, description, clientId, redirectUris[], scopes[], homepage, logoUrl, status, createdAt }] }` | 查询当前用户创建的 OAuth 应用；client_secret 不返回。 | P0 | 待启动 | 未测试 |
| **TSK-OPEN-API-302** | `OAuthApplicationController` | `POST /open/applications` | Body: `{ name, description, redirectUris[], scopes[], homepage?, logoUrl? }` | `{ id, name, clientId, clientSecret }` | 1. 生成 clientId（UUID）和 clientSecret（随机32位）；2. clientSecret AES 加密存储；3. 明文 clientSecret 只在创建时返回一次。 | P0 | 待启动 | 未测试 |
| **TSK-OPEN-API-303** | `OAuthApplicationController` | `POST /open/applications/{appId}/secrets` | Path: `appId` | `{ clientSecret: string }` | 重新生成 clientSecret；旧 Secret 立即失效；撤销该应用所有已颁发的 Token。 | P0 | 待启动 | 未测试 |

### 2.4 Webhook 管理接口

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-OPEN-API-401** | `WebhookController` | `GET /open/webhooks` | Header: Bearer Token | `{ data: [{ id, name, url, events[], active, createdAt }] }` | 查询当前用户的 Webhook 列表；secret 字段不返回。 | P0 | 待启动 | 未测试 |
| **TSK-OPEN-API-402** | `WebhookController` | `POST /open/webhooks` | Body: `{ name, url, events[], secret, active?, headers?, retryPolicy? }` | `{ id, name, url, events[], secret, active }` | 1. 校验 url 格式（必须 HTTPS）；2. 校验 events 在支持的事件类型列表内；3. 插入 `open_webhook`。 | P0 | 待启动 | 未测试 |
| **TSK-OPEN-API-403** | `WebhookController` | `PUT /open/webhooks/{webhookId}` | Path: `webhookId`；Body: `{ name, url, events[], active, headers? }` | `{ code: 200, data: null }` | 更新 Webhook 配置；不可修改 secret（需单独接口重置）。 | P0 | 待启动 | 未测试 |
| **TSK-OPEN-API-404** | `WebhookController` | `DELETE /open/webhooks/{webhookId}` | Path: `webhookId` | `{ code: 200, data: null }` | 删除 Webhook；同时删除相关投递记录。 | P0 | 待启动 | 未测试 |
| **TSK-OPEN-API-405** | `WebhookController` | `POST /open/webhooks/{webhookId}/test` | Path: `webhookId`；Body: `{ event }` | `{ deliveryId, status, statusCode, responseTime, responseBody }` | 1. 构造测试 payload；2. 计算 HMAC-SHA256 签名；3. 发送 HTTP POST 请求到 Webhook URL；4. 记录投递结果到 `open_webhook_delivery`；5. 返回投递结果。 | P0 | 待启动 | 未测试 |
| **TSK-OPEN-API-406** | `WebhookController` | `GET /open/webhooks/{webhookId}/deliveries` | Path: `webhookId`；Query: `page`、`size` | `{ records: [{ id, eventType, status, statusCode, responseTime, attempt, createdAt, completedAt }], total, page, size }` | 分页查询 Webhook 投递记录；按 `created_at` 倒序。 | P1 | 待启动 | 未测试 |

### 2.5 Webhook 事件分发（内部服务）

| 任务 ID | 类路径 | 说明 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-OPEN-API-501** | `WebhookDispatchService` | Kafka 消费者，监听所有业务事件 | 1. 监听 Kafka Topic `lest-webhook-events`；2. 根据 event_type 查询订阅该事件的所有 active Webhook；3. 构造标准 Webhook Payload（含 id/type/timestamp/data）；4. 计算 HMAC-SHA256 签名；5. 异步 HTTP POST 发送；6. 记录投递结果到 `open_webhook_delivery`；7. 失败时按 retryPolicy 重试（最多3次，指数退避）。 | P0 | 待启动 | 未测试 |

### 2.6 API 限流与鉴权（网关层）

| 任务 ID | 类路径 | 说明 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-OPEN-API-601** | `OpenApiAuthFilter` | Spring Security Filter | 1. 从 Header 提取 `X-API-Key` 或 `Authorization: Bearer`；2. API Key 模式：SHA256 hash 后查询 `open_api_token`，校验有效期和 scope；3. OAuth Token 模式：hash 后查询 `open_oauth_token`，校验有效期和 scope；4. 更新 `last_used_at`；5. 将 userId 和 scopes 注入 SecurityContext。 | P0 | 待启动 | 未测试 |
| **TSK-OPEN-API-602** | `RateLimitFilter` | 限流过滤器 | 1. 基于 Redis 令牌桶算法实现限流；2. 未认证 IP：60次/分钟；个人 Token：600次/分钟；OAuth 应用：1000次/分钟；3. 超限返回 429，响应头携带 `X-RateLimit-Limit`、`X-RateLimit-Remaining`、`X-RateLimit-Reset`；4. 写入 `open_api_log`（异步）。 | P1 | 待启动 | 未测试 |

---

## 3. 前端 PC 端任务

### 3.1 开放平台概览页

| 任务 ID | 页面/组件路径 | 路由路径 | 页面元素 | 交互与 API 绑定 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-OPEN-FE-101** | `@/views/open/index.vue` | `/open` | 顶部导航：API 文档、个人令牌、OAuth 应用、Webhook；概览卡片：本月 API 调用量、Webhook 投递量、应用数量；快速开始引导卡片（生成令牌/创建应用/配置 Webhook/查看文档）；API 调用趋势折线图（ECharts） | 1. 页面加载调用 `GET /ai/usage-stats` 获取 API 调用统计；2. ECharts 折线图展示近30天调用趋势；3. 快速开始卡片点击跳转对应子页面。 | P1 | 待启动 | 未测试 |

### 3.2 个人访问令牌管理页

| 任务 ID | 页面/组件路径 | 路由路径 | 页面元素 | 交互与 API 绑定 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-OPEN-FE-201** | `@/views/open/tokens.vue` | `/open/tokens` | 顶部：生成新令牌按钮；令牌列表：令牌名称、权限范围标签、创建时间、最后使用时间、操作列（撤销按钮）；生成令牌弹窗：名称输入框、权限范围多选（tasks:read/tasks:write/projects:read 等）、过期时间选择；令牌生成成功弹窗：展示明文 Token（带复制按钮，提示只显示一次） | 1. 页面加载调用 `GET /open/tokens`；2. 生成令牌调用 `POST /open/tokens`，成功后弹出展示明文 Token 的弹窗；3. 撤销按钮弹出确认框后调用 `DELETE /open/tokens/{tokenId}`。 | P0 | 待启动 | 未测试 |

### 3.3 Webhook 管理页

| 任务 ID | 页面/组件路径 | 路由路径 | 页面元素 | 交互与 API 绑定 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-OPEN-FE-301** | `@/views/open/webhooks.vue` | `/open/webhooks` | 顶部：创建 Webhook 按钮；Webhook 卡片列表：名称、URL、订阅事件标签、启用状态开关、投递成功率、最后投递时间、操作按钮（测试/编辑/查看日志/删除）；创建/编辑弹窗：名称/URL/事件多选/Secret/自定义 Header/重试策略 | 1. 页面加载调用 `GET /open/webhooks`；2. 启用开关调用 `PUT /open/webhooks/{id}`（更新 active 字段）；3. 测试按钮打开事件选择弹窗后调用 `POST /open/webhooks/{id}/test`；4. 查看日志按钮跳转 `/open/webhooks/{id}/deliveries`；5. 删除调用 `DELETE /open/webhooks/{id}`。 | P0 | 待启动 | 未测试 |

### 3.4 Webhook 投递记录页

| 任务 ID | 页面/组件路径 | 路由路径 | 页面元素 | 交互与 API 绑定 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-OPEN-FE-401** | `@/views/open/webhook-deliveries.vue` | `/open/webhooks/:id/deliveries` | 顶部：面包屑（Webhook 管理 > {name} 投递记录）；投递记录列表：事件类型、状态标签（成功/失败）、HTTP 状态码、响应时间、尝试次数、投递时间、操作列（详情按钮）；详情弹窗：请求 Header/Body、响应 Header/Body（JSON 格式化展示）；底部：分页组件 | 1. 页面加载调用 `GET /open/webhooks/{id}/deliveries`；2. 详情按钮展示完整的请求和响应内容。 | P1 | 待启动 | 未测试 |