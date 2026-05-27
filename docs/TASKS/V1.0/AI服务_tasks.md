# AI 服务开发任务列表

## 模块上下文

- **微服务模块**：`lest-ai`（端口: 8008）
- **主目录**：`backend/lest-modules/lest-ai`
- **包路径**：`com.lest.ai`
- **数据库 Schema**：`lest_ai`（Flyway 目录：`src/main/resources/db/migration/ai`）
- **前端 PC 目录**：`frontend-pc/src/views/ai/`

---

## 1. 数据库迁移任务（DDL Tasks）

| 任务 ID | 类型 | 目标文件 | 详细实现细节与约束规范 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-AI-DDL-101** | DDL | `V1.0.0__init_ai_model_config.sql` | 创建 `ai_model_config` 表：`id`(BIGINT PK)、`model_type`(VARCHAR 32, qwen/kimi/gpt4/gpt35/ollama)、`api_key`(VARCHAR 512, AES 加密存储)、`api_url`(VARCHAR 256)、`default_model`(VARCHAR 64)、`enabled`(TINYINT DEFAULT 1)、`priority`(INT DEFAULT 100)、`created_at`、`updated_at`。建立唯一索引 `uk_model_type`。 | P0 | 待启动 | 未测试 |
| **TSK-AI-DDL-102** | DDL | `V1.0.1__init_ai_conversation.sql` | 创建 `ai_conversation` 表：`id`(BIGINT PK)、`user_id`(BIGINT)、`scene`(VARCHAR 32, code_review/meeting/task/chat)、`model_type`(VARCHAR 32)、`model`(VARCHAR 64)、`messages`(JSON)、`request_tokens`(INT)、`response_tokens`(INT)、`cost`(DECIMAL 10,4)、`duration_ms`(INT)、`status`(VARCHAR 16)、`error_message`(VARCHAR 512)、`created_at`。建立索引 `idx_user_id`、`idx_scene`、`idx_created_at`。 | P0 | 待启动 | 未测试 |
| **TSK-AI-DDL-103** | DDL | `V1.0.2__init_ai_review_summary.sql` | 创建 `ai_code_review` 表：`id`、`mr_id`(BIGINT)、`repository_id`(BIGINT)、`score`(DECIMAL 3,1)、`summary`(TEXT)、`issues`(JSON)、`status`(VARCHAR 16, pending/completed/failed)、`created_at`、`updated_at`，建立索引 `idx_mr_id`。创建 `ai_meeting_summary` 表：`id`、`meeting_id`(BIGINT UNIQUE)、`overview`(JSON)、`key_points`(JSON)、`decisions`(JSON)、`action_items`(JSON)、`task_suggestions`(JSON)、`created_at`、`updated_at`，建立唯一索引 `uk_meeting_id`。 | P0 | 待启动 | 未测试 |
| **TSK-AI-DDL-104** | DDL | `V1.0.3__init_ai_prompt_template.sql` | 创建 `ai_prompt_template` 表：`id`、`code`(VARCHAR 64, UNIQUE)、`name`(VARCHAR 128)、`description`(VARCHAR 256)、`template`(TEXT)、`model_type`(VARCHAR 32)、`variables`(JSON)、`enabled`(TINYINT DEFAULT 1)、`created_at`、`updated_at`，建立唯一索引 `uk_code`。 | P1 | 待启动 | 未测试 |


---

## 2. 后端 API 任务

### 2.1 AI 网关与模型配置接口

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-AI-API-101** | `AIGatewayController` | `POST /ai/chat` | Header: Bearer Token；Body: `{ model?: string, messages: [{role, content}], temperature?: number, maxTokens?: number }` | `{ id, model, usage: { promptTokens, completionTokens, totalTokens }, choices: [{ message: { role, content } }] }` | 统一 AI 调用入口；按 `model` 参数路由到对应 Provider，未指定则按 `priority` 选最高优先级已启用的模型；调用失败自动降级到下一个 Provider；异步写入 `ai_conversation` 记录；支持流式响应（SSE）。 | P0 | 待启动 | 未测试 |
| **TSK-AI-API-102** | `AIModelConfigController` | `GET /ai/model/config` | Header: Bearer Token（需 admin 权限） | `{ records: [{ id, modelType, apiUrl, defaultModel, enabled, priority }] }` | 查询所有模型配置；`api_key` 字段脱敏（仅返回前4位+****）。 | P0 | 待启动 | 未测试 |
| **TSK-AI-API-103** | `AIModelConfigController` | `PUT /ai/model/config/{id}` | Path: `id`；Body: `{ apiKey?, apiUrl, defaultModel, enabled, priority }` | `{ code: 200, data: null }` | 更新模型配置；`apiKey` 非空时 AES 加密后存储；`apiKey` 为空时不更新该字段（保留原值）。 | P0 | 待启动 | 未测试 |
| **TSK-AI-API-104** | `AIModelConfigController` | `POST /ai/model/test` | Header: Bearer Token（需 admin 权限）；Body: `{ modelType }` | `{ success: boolean, latencyMs: number, message: string }` | 向指定模型发送测试 prompt，验证 API Key 和连通性；返回响应延迟。 | P0 | 待启动 | 未测试 |

### 2.2 AI 代码审查接口

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-AI-API-201** | `AICodeReviewController` | `POST /ai/code-review` | Header: Bearer Token；Body: `{ mrId: number, repositoryId: number }` | `{ reviewId: string, status: "pending" }` | 异步任务模式：立即返回 reviewId 和 pending 状态；后台拉取 MR diff → 构造 prompt → 调用 AI 网关 → 解析结果写入 `ai_code_review`；完成后通过 Kafka 发送 `AI_REVIEW_COMPLETED` 事件触发通知。 | P0 | 待启动 | 未测试 |
| **TSK-AI-API-202** | `AICodeReviewController` | `GET /ai/code-review/{reviewId}` | Path: `reviewId` | `{ reviewId, mrId, score, summary, issues: [{ file, line, severity, type, description, suggestion }], status, createdAt }` | 查询审查结果；status 为 pending 时前端轮询；status 为 completed 时返回完整结果。 | P0 | 待启动 | 未测试 |
| **TSK-AI-API-203** | `AICodeReviewController` | `GET /ai/code-review/mr/{mrId}` | Path: `mrId` | 同上 | 通过 mrId 查询最新一次审查结果，用于 MR 详情页展示。 | P0 | 待启动 | 未测试 |

### 2.3 AI 会议总结接口

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-AI-API-301** | `AIMeetingSummaryController` | `POST /ai/meeting-summary` | Header: Bearer Token；Body: `{ meetingId: number, minutes: string }` | `{ summaryId: string, status: "pending" }` | 异步任务模式；将会议原始记录传入 AI，提取关键讨论点、决议、行动项；完成后写入 `ai_meeting_summary`，发送 `AI_SUMMARY_GENERATED` 通知。 | P1 | 待启动 | 未测试 |
| **TSK-AI-API-302** | `AIMeetingSummaryController` | `GET /ai/meeting-summary/{meetingId}` | Path: `meetingId` | `{ summaryId, meetingId, overview, keyPoints[], decisions[], actionItems: [{ content, assignee, dueDate }], taskSuggestions[], createdAt }` | 查询指定会议的 AI 总结结果。 | P1 | 待启动 | 未测试 |
| **TSK-AI-API-303** | `AIMeetingSummaryController` | `POST /ai/meeting-summary/{meetingId}/create-tasks` | Path: `meetingId`；Body: `{ actionItemIndexes: number[] }` | `{ createdTaskIds: number[] }` | 将 AI 提取的指定行动项批量创建为任务；通过 Feign 调用 `lest-task` 服务的创建任务接口；返回创建成功的任务 ID 列表。 | P1 | 待启动 | 未测试 |

### 2.4 AI 使用统计接口

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-AI-API-401** | `AIUsageStatsController` | `GET /ai/usage-stats` | Header: Bearer Token（需 admin 权限）；Query: `startDate`、`endDate`、`userId`(可选) | `{ totalCalls, totalTokens, costEstimate, byModel: { [type]: { calls, tokens, cost } }, byUser: [{ userId, username, calls, tokens }], dailyTrend: [{ date, calls, tokens }] }` | 聚合查询 `ai_conversation` 表；按日期范围、用户过滤；成本按各模型单价估算。 | P1 | 待启动 | 未测试 |

### 2.5 AI 提示词模板接口

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-AI-API-501** | `AIPromptTemplateController` | `GET /ai/prompt-template/page` | Query: `code`(模糊)、`modelType`(可选)、`page`、`size` | `{ records: [{ id, code, name, description, modelType, enabled, updatedAt }], total, page, size }` | 分页查询提示词模板列表；不返回 template 字段（内容较大）。 | P1 | 待启动 | 未测试 |
| **TSK-AI-API-502** | `AIPromptTemplateController` | `PUT /ai/prompt-template/{id}` | Path: `id`；Body: `{ name, description, template, variables }` | `{ code: 200, data: null }` | 更新提示词模板；不可修改 code 字段。 | P1 | 待启动 | 未测试 |


---

## 3. 前端 PC 端任务

### 3.1 AI 代码审查结果页

| 任务 ID | 页面/组件路径 | 路由路径 | 页面元素 | 交互与 API 绑定 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-AI-FE-101** | `@/views/ai/code-review/index.vue` | `/ai/code-review/:mrId` | 顶部：综合评分（大字展示）、问题数量统计（高/中/低）；问题列表：按严重程度分组折叠面板，每条问题展示文件路径、行号、问题类型标签（安全/性能/风格/Bug）、问题描述、修改建议；底部：总体评价文本框 | 1. 页面加载调用 `GET /ai/code-review/mr/{mrId}`；2. 若 status 为 pending，每3秒轮询一次直到 completed；3. 展示 loading 骨架屏等待期间；4. 问题列表按 severity 分组（high/medium/low），默认展开 high 组。 | P0 | 待启动 | 未测试 |

### 3.2 AI 会议总结页

| 任务 ID | 页面/组件路径 | 路由路径 | 页面元素 | 交互与 API 绑定 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-AI-FE-201** | `@/views/ai/meeting-summary/index.vue` | `/ai/meeting-summary/:meetingId` | 顶部：生成时间、重新生成按钮、导出按钮；会议概要卡片（类型/参与人数/时长/目标）；关键讨论点列表；会议决议列表（带勾选图标）；行动项列表（每条含内容、负责人、截止日期、创建任务按钮）；迭代任务规划区块 | 1. 页面加载调用 `GET /ai/meeting-summary/{meetingId}`；2. 重新生成按钮调用 `POST /ai/meeting-summary`；3. 行动项"创建任务"按钮勾选后批量调用 `POST /ai/meeting-summary/{meetingId}/create-tasks`；4. 导出按钮将内容导出为 Markdown 文件。 | P1 | 待启动 | 未测试 |

### 3.3 AI 使用统计页（管理员）

| 任务 ID | 页面/组件路径 | 路由路径 | 页面元素 | 交互与 API 绑定 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-AI-FE-301** | `@/views/system/ai-stats.vue` | `/system/ai-stats` | 顶部：日期范围选择器、查询按钮；数据卡片：总调用次数、总 Token 消耗、估算费用；ECharts 图表：各模型调用占比饼图、每日调用趋势折线图；用户消耗排行表：用户名、调用次数、Token 消耗 | 1. 页面加载调用 `GET /ai/usage-stats`（默认近30天）；2. 切换日期范围重新查询；3. ECharts 图表数据绑定接口返回的 `byModel` 和 `dailyTrend`。 | P1 | 待启动 | 未测试 |

### 3.4 AI 模型配置页（管理员）

| 任务 ID | 页面/组件路径 | 路由路径 | 页面元素 | 交互与 API 绑定 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-AI-FE-401** | `@/views/system/ai-model-config.vue` | `/system/ai-model-config` | 模型卡片列表（通义千问/Kimi/GPT-4/GPT-3.5/Ollama）；每张卡片：模型名称、启用开关、优先级输入框、API 地址输入框、API Key 密码输入框（显示脱敏值）、默认模型选择、测试连接按钮、保存按钮 | 1. 页面加载调用 `GET /ai/model/config` 回显各模型配置；2. 保存按钮调用 `PUT /ai/model/config/{id}`；3. 测试连接按钮调用 `POST /ai/model/test`，展示延迟和成功/失败结果。 | P0 | 待启动 | 未测试 |