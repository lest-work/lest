# 通知与消息服务开发任务列表

## 模块上下文

- **微服务模块**：`lest-notification`（端口: 8006）
- **主目录**：`backend/lest-modules/lest-notification`
- **包路径**：`com.lest.notification`
- **数据库 Schema**：`lest_notification`（Flyway 目录：`src/main/resources/db/migration/notification`）
- **前端 PC 目录**：`frontend-pc/src/views/notification/`


---

## 1. 数据库迁移任务（DDL Tasks）

| 任务 ID | 类型 | 目标文件 | 详细实现细节与约束规范 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-NOT-DDL-101** | DDL | `V1.0.0__init_message_table.sql` | 创建 `msg_message` 表：`id`(BIGINT PK)、`user_id`(BIGINT NOT NULL, 接收者)、`type`(VARCHAR 16, task/mr/build/system)、`title`(VARCHAR 256)、`content`(TEXT)、`related_type`(VARCHAR 32)、`related_id`(BIGINT)、`sender_id`(BIGINT)、`read_status`(VARCHAR 16 DEFAULT 'unread')、`read_at`(DATETIME)、`created_at`。建立索引 `idx_user_id`、`idx_read_status`、`idx_created_at`。 | P0 | 待启动 | 未测试 |
| **TSK-NOT-DDL-102** | DDL | `V1.0.1__init_config_template_tables.sql` | 创建 `msg_notification_config` 表：`id`、`scope`(VARCHAR 16, system/project/user)、`scope_id`(BIGINT)、`event_type`(VARCHAR 32)、`channels`(JSON)、`enabled`(TINYINT DEFAULT 1)、`created_at`、`updated_at`，建立索引 `idx_scope`。创建 `msg_template` 表：`id`、`event_type`(VARCHAR 32)、`channel`(VARCHAR 16)、`title_template`(VARCHAR 256)、`content_template`(TEXT)、`language`(VARCHAR 8 DEFAULT 'zh_CN')、`created_at`、`updated_at`，建立唯一索引 `uk_event_channel_lang`(event_type, channel, language)。 | P0 | 待启动 | 未测试 |
| **TSK-NOT-DDL-103** | DDL | `V1.0.2__init_user_settings_send_log.sql` | 创建 `msg_user_settings` 表：`id`、`user_id`(BIGINT UNIQUE)、`channels`(JSON)、`mute_enabled`(TINYINT DEFAULT 0)、`mute_start_time`(TIME)、`mute_end_time`(TIME)、`mute_days`(JSON)、`mute_event_types`(JSON)、`frequency`(VARCHAR 16 DEFAULT 'realtime')、`event_settings`(JSON)、`updated_at`。创建 `msg_send_log` 表：`id`、`message_id`(BIGINT)、`channel`(VARCHAR 16)、`recipient`(VARCHAR 128)、`status`(VARCHAR 16)、`error_message`(VARCHAR 512)、`sent_at`(DATETIME)、`created_at`，建立索引 `idx_message_id`、`idx_status`。 | P1 | 待启动 | 未测试 |


---

## 2. 后端 API 任务

### 2.1 站内消息接口

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-NOT-API-101** | `MessageController` | `GET /notification/message/page` | Header: Bearer Token；Query: `type`(可选)、`readStatus`(unread/read/all, 默认all)、`keyword`(可选模糊搜索标题)、`page`(默认1)、`size`(默认20) | `{ records: [{ id, type, title, content, readStatus, relatedType, relatedId, relatedTitle, sender: { userId, username, avatar }, createdAt }], total, unreadCount, page, size }` | 按当前登录用户 `user_id` 过滤；按 `created_at` 倒序；`unreadCount` 单独统计未读总数，用于顶部红点展示；`relatedTitle` 通过 relatedType+relatedId 跨服务 RPC 聚合（任务标题/MR 标题等）。 | P0 | 待启动 | 未测试 |
| **TSK-NOT-API-102** | `MessageController` | `PUT /notification/message/read` | Header: Bearer Token；Body: `{ messageIds: number[] }` | `{ code: 200, data: null }` | `messageIds` 为空数组时全部已读（更新当前用户所有未读消息）；否则批量更新指定 ID 的 `read_status='read'` 和 `read_at`；只能操作自己的消息，越权忽略。 | P0 | 待启动 | 未测试 |
| **TSK-NOT-API-103** | `MessageController` | `GET /notification/message/unread-count` | Header: Bearer Token | `{ total: number, byType: { task: number, mr: number, build: number, system: number } }` | 统计当前用户各类型未读消息数量；结果可缓存到 Redis（用户维度，TTL 60秒），消息已读时主动清除缓存。 | P0 | 待启动 | 未测试 |
| **TSK-NOT-API-104** | `MessageController` | `DELETE /notification/message/{id}` | Header: Bearer Token；Path: `id` | `{ code: 200, data: null }` | 软删除（或物理删除）当前用户的指定消息；校验消息归属，非本人消息返回 403。 | P1 | 待启动 | 未测试 |


### 2.2 通知设置接口

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-NOT-API-201** | `NotificationSettingsController` | `GET /notification/settings` | Header: Bearer Token | `{ channels: { inApp, email, dingtalk, wecom, feishu }, muteSettings: { enabled, startTime, endTime, muteDays[], muteTypes[] }, frequency, eventSettings: { [eventType]: { channels[], enabled } } }` | 查询当前用户的 `msg_user_settings`；若不存在则返回系统默认配置（全渠道开启、实时推送、不免打扰）。 | P0 | 待启动 | 未测试 |
| **TSK-NOT-API-202** | `NotificationSettingsController` | `PUT /notification/settings` | Header: Bearer Token；Body: `{ channels, muteSettings, frequency, eventSettings }` | `{ code: 200, data: null }` | 存在则更新，不存在则插入（upsert）`msg_user_settings`；channels、muteSettings、eventSettings 均以 JSON 整体覆盖存储。 | P0 | 待启动 | 未测试 |

### 2.3 通知渠道配置接口（管理员）

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-NOT-API-301** | `ChannelConfigController` | `GET /notification/channel/config` | Header: Bearer Token（需 admin 权限） | `{ email: { host, port, username, fromName, enabled }, dingtalk: { webhook, keyword, enabled }, wecom: { webhook, enabled }, feishu: { webhook, enabled } }` | 查询系统级渠道配置；API Key / 密码字段脱敏返回（仅显示前4位+****）。 | P0 | 待启动 | 未测试 |
| **TSK-NOT-API-302** | `ChannelConfigController` | `PUT /notification/channel/config` | Header: Bearer Token（需 admin 权限）；Body: `{ channelType, config: {...} }` | `{ code: 200, data: null }` | 更新指定渠道配置；敏感字段加密存储（AES）；支持 channelType: email/dingtalk/wecom/feishu。 | P0 | 待启动 | 未测试 |
| **TSK-NOT-API-303** | `ChannelConfigController` | `POST /notification/channel/test` | Header: Bearer Token（需 admin 权限）；Body: `{ channelType, targetAddress }` | `{ success: boolean, message: string }` | 向 targetAddress 发送测试消息，验证渠道配置是否正确；邮件渠道发送测试邮件，Webhook 渠道发送测试消息。 | P0 | 待启动 | 未测试 |

### 2.4 通知模板接口（管理员）

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-NOT-API-401** | `TemplateController` | `GET /notification/template/page` | Query: `eventType`(可选)、`channel`(可选)、`page`、`size` | `{ records: [{ id, eventType, channel, titleTemplate, contentTemplate, language, updatedAt }], total, page, size }` | 分页查询通知模板列表。 | P1 | 待启动 | 未测试 |
| **TSK-NOT-API-402** | `TemplateController` | `PUT /notification/template/{id}` | Path: `id`；Body: `{ titleTemplate, contentTemplate }` | `{ code: 200, data: null }` | 更新模板内容；模板支持变量占位符如 `{username}`、`{taskTitle}`；不可修改 eventType、channel、language。 | P1 | 待启动 | 未测试 |
| **TSK-NOT-API-403** | `TemplateController` | `POST /notification/template/preview` | Body: `{ templateId, variables: { key: value } }` | `{ title: string, content: string }` | 用传入的变量渲染模板，返回预览结果；不实际发送。 | P1 | 待启动 | 未测试 |

### 2.5 事件推送接口（内部服务调用）

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-NOT-API-501** | `EventDispatchController` | `POST /notification/event` | Header: `X-Internal-Token`；Body: `{ event: string, data: object, timestamp: string }` | `{ code: 200, data: null }` | 仅供内部微服务调用，通过 `X-Internal-Token` 鉴权；接收事件后异步处理：查询订阅该事件的用户列表 → 过滤免打扰设置 → 渲染模板 → 写入 `msg_message` → 按渠道配置分发（站内信/邮件/Webhook）。 | P0 | 待启动 | 未测试 |

### 2.6 WebSocket 实时推送

| 任务 ID | 类路径 | 说明 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-NOT-API-601** | `NotificationWebSocketHandler` | WebSocket 端点 `/ws/notification` | 握手时从 Query 参数提取 JWT Token 并验证；验证通过后将 userId 与 WebSocket Session 绑定存入内存 Map；收到新消息时通过 userId 查找 Session 实时推送；用户断开时清理 Session；离线消息已持久化到 `msg_message`，用户重连后通过 `GET /notification/message/page` 拉取未读。 | P0 | 待启动 | 未测试 |


---

## 3. 前端 PC 端任务

### 3.1 通知中心页面

| 任务 ID | 页面/组件路径 | 路由路径 | 页面元素 | 交互与 API 绑定 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-NOT-FE-101** | `@/views/notification/index.vue` | `/notification` | 顶部：类型 Tab（全部/任务/代码/构建/系统）、全部已读按钮、设置按钮；消息列表：未读消息蓝点标记、发送者头像、消息标题、消息摘要、关联对象链接、发送时间（相对时间）；底部：分页组件 | 1. 页面加载调用 `GET /notification/message/page`；2. 切换 Tab 传入对应 `type` 参数重新查询；3. 点击消息行标记已读（调用 `PUT /notification/message/read`）并跳转关联对象；4. 全部已读按钮调用 `PUT /notification/message/read`（传空数组）；5. 设置按钮跳转 `/notification/settings`。 | P0 | 待启动 | 未测试 |
| **TSK-NOT-FE-102** | `@/components/NotificationBell.vue` | — | 顶部导航栏铃铛图标、未读数量红点（超过99显示99+）、下拉面板（最近5条未读消息预览、查看全部按钮） | 1. 组件挂载时调用 `GET /notification/message/unread-count` 获取未读数；2. 建立 WebSocket 连接 `/ws/notification`，收到推送时更新未读数并弹出 `ElNotification` 提示；3. 点击铃铛展开下拉面板，加载最近5条未读消息；4. 点击"查看全部"跳转 `/notification`。 | P0 | 待启动 | 未测试 |

### 3.2 通知设置页面

| 任务 ID | 页面/组件路径 | 路由路径 | 页面元素 | 交互与 API 绑定 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-NOT-FE-201** | `@/views/notification/settings.vue` | `/notification/settings` | 通知渠道区块：站内消息/邮件/钉钉/企业微信/飞书开关；免打扰区块：启用开关、时间段选择器（开始/结束时间）、免打扰日期多选（周一至周日）、免打扰事件类型多选；通知频率区块：实时/每小时汇总/每日汇总单选；各事件类型细粒度设置折叠面板；保存按钮、取消按钮 | 1. 页面加载调用 `GET /notification/settings` 回显当前设置；2. 保存按钮调用 `PUT /notification/settings` 提交全量配置；3. 免打扰启用开关控制时间段和日期选择器的显示/禁用状态。 | P1 | 待启动 | 未测试 |

### 3.3 通知渠道配置页面（管理员）

| 任务 ID | 页面/组件路径 | 路由路径 | 页面元素 | 交互与 API 绑定 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-NOT-FE-301** | `@/views/system/notification-channel.vue` | `/system/notification-channel` | 渠道卡片列表（邮件/钉钉/企业微信/飞书）；每张卡片：渠道名称、启用开关、配置表单（SMTP/Webhook 等字段）、测试连接按钮、保存按钮 | 1. 页面加载调用 `GET /notification/channel/config` 回显各渠道配置；2. 保存按钮调用 `PUT /notification/channel/config`；3. 测试连接按钮弹出输入框填写测试地址后调用 `POST /notification/channel/test`，展示测试结果。 | P1 | 待启动 | 未测试 |

### 3.4 通知模板管理页面（管理员）

| 任务 ID | 页面/组件路径 | 路由路径 | 页面元素 | 交互与 API 绑定 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-NOT-FE-401** | `@/views/system/notification-template.vue` | `/system/notification-template` | 顶部：事件类型下拉筛选、渠道下拉筛选、搜索按钮；列表：事件类型、渠道、标题模板（截断显示）、更新时间、操作列（编辑按钮、预览按钮）；底部：分页组件 | 1. 页面加载调用 `GET /notification/template/page`；2. 编辑按钮打开 `TemplateEditDialog`，提交调用 `PUT /notification/template/{id}`；3. 预览按钮打开预览弹窗，填写变量后调用 `POST /notification/template/preview` 展示渲染结果。 | P1 | 待启动 | 未测试 |