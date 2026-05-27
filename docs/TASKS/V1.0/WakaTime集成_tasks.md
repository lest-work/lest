# WakaTime 集成与编码统计开发任务列表

## 模块上下文

- **微服务模块**：`lest-wakapi`（端口: 8009）
- **主目录**：`backend/lest-modules/lest-wakapi`
- **包路径**：`com.lest.wakapi`
- **数据库 Schema**：`lest_wakapi`（Flyway 目录：`src/main/resources/db/migration/wakapi`）
- **前端 PC 目录**：`frontend-pc/src/views/performance/`

---

## 1. 数据库迁移任务（DDL Tasks）

| 任务 ID | 类型 | 目标文件 | 详细实现细节与约束规范 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-WAKA-DDL-101** | DDL | `V1.0.0__init_heartbeat_table.sql` | 创建 `waka_heartbeat` 表：`id`(BIGINT PK)、`user_id`(BIGINT NOT NULL)、`entity`(VARCHAR 512, 文件路径)、`type`(VARCHAR 16, file/app/domain)、`project`(VARCHAR 128)、`branch`(VARCHAR 128)、`language`(VARCHAR 64)、`is_write`(TINYINT DEFAULT 0)、`time`(DECIMAL 16,6, Unix 时间戳)、`created_at`。建立索引 `idx_user_id`、`idx_project`、`idx_time`。 | P0 | 待启动 | 未测试 |
| **TSK-WAKA-DDL-102** | DDL | `V1.0.1__init_summary_tables.sql` | 创建 `waka_daily_summary` 表：`id`、`user_id`(BIGINT)、`summary_date`(DATE)、`total_seconds`(INT)、`projects`(JSON, 各项目时长)、`languages`(JSON, 各语言时长)、`created_at`、`updated_at`，建立唯一索引 `uk_user_date`(user_id, summary_date)。创建 `waka_api_key` 表：`id`、`user_id`(BIGINT UNIQUE)、`api_key`(VARCHAR 64, UNIQUE)、`created_at`、`updated_at`。 | P0 | 待启动 | 未测试 |
| **TSK-WAKA-DDL-103** | DDL | `V1.0.2__init_task_link_table.sql` | 创建 `waka_task_link` 表：`id`、`user_id`(BIGINT)、`task_id`(BIGINT)、`total_seconds`(INT DEFAULT 0)、`last_synced_at`(DATETIME)，建立唯一索引 `uk_user_task`(user_id, task_id)。用于记录每个用户在每个任务上的累计编码时长。 | P1 | 待启动 | 未测试 |

---

## 2. 后端 API 任务

### 2.1 WakaTime 兼容协议接口

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-WAKA-API-101** | `HeartbeatController` | `POST /api/v1/compat/wakatime/v1/users/current/heartbeats` | Header: `Authorization: Basic {base64(apiKey:)}`；Body: `[{ entity, type, project, branch, language, is_write, time }]` | `{ responses: [[null, 201]] }` | 兼容 WakaTime 官方 IDE 插件协议；从 Authorization Header 解析 API Key，查询 `waka_api_key` 表获取 userId；批量接收心跳数组（最多25条）；暂存 Redis（Key: `waka:heartbeat:{userId}`，TTL 10分钟）；返回 WakaTime 标准格式响应。 | P0 | 待启动 | 未测试 |
| **TSK-WAKA-API-102** | `HeartbeatController` | `GET /api/v1/compat/wakatime/v1/users/current/heartbeats` | Header: Authorization；Query: `date`(YYYY-MM-DD) | `{ data: [{ entity, type, project, branch, language, time }], total, page }` | 查询指定日期的心跳记录，用于 IDE 插件状态同步。 | P1 | 待启动 | 未测试 |

### 2.2 编码统计查询接口

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-WAKA-API-201** | `StatsController` | `GET /wakapi/stats/summary` | Header: Bearer Token；Query: `userId`(可选，默认当前用户)、`startDate`、`endDate` | `{ totalSeconds, humanReadableTotal, projects: [{ name, totalSeconds }], languages: [{ name, totalSeconds, percent }], dailyAverage }` | 聚合查询 `waka_daily_summary` 表；按日期范围汇总各项目和语言时长；`humanReadableTotal` 格式化为"X hrs Y mins"。 | P0 | 待启动 | 未测试 |
| **TSK-WAKA-API-202** | `StatsController` | `GET /wakapi/stats/heatmap` | Header: Bearer Token；Query: `userId`(可选)、`year`(默认当前年) | `{ data: [{ date: "YYYY-MM-DD", totalSeconds: number }] }` | 查询全年每天的编码时长，用于热力图渲染；数据来源 `waka_daily_summary`；无记录的日期返回 totalSeconds=0。 | P0 | 待启动 | 未测试 |
| **TSK-WAKA-API-203** | `StatsController` | `GET /wakapi/stats/task/{taskId}` | Header: Bearer Token；Path: `taskId` | `{ taskId, totalSeconds, humanReadableTotal, contributors: [{ userId, username, totalSeconds }] }` | 查询指定任务的累计编码时长；从 `waka_task_link` 聚合；contributors 通过 userId 跨服务查询用户信息。 | P1 | 待启动 | 未测试 |
| **TSK-WAKA-API-204** | `StatsController` | `GET /wakapi/stats/leaderboard` | Header: Bearer Token；Query: `projectId`(可选)、`startDate`、`endDate`、`limit`(默认10) | `{ data: [{ rank, userId, username, avatar, totalSeconds, humanReadableTotal }] }` | 查询编码时长排行榜；按 `waka_daily_summary` 聚合排序；支持按项目过滤。 | P2 | 待启动 | 未测试 |

### 2.3 API Key 管理接口

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-WAKA-API-301** | `ApiKeyController` | `GET /wakapi/api-key` | Header: Bearer Token | `{ apiKey: string, createdAt: string }` | 查询当前用户的 WakaTime API Key；不存在则自动生成（UUID 格式）并存入 `waka_api_key`。 | P0 | 待启动 | 未测试 |
| **TSK-WAKA-API-302** | `ApiKeyController` | `POST /wakapi/api-key/regenerate` | Header: Bearer Token | `{ apiKey: string }` | 重新生成 API Key；旧 Key 立即失效；更新 `waka_api_key` 表。 | P0 | 待启动 | 未测试 |

### 2.4 定时任务

| 任务 ID | 类路径 | 触发方式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-WAKA-JOB-401** | `HeartbeatPersistJob` | 每5分钟执行（`@Scheduled`） | 从 Redis 批量读取所有用户的心跳缓存 → 合并相同文件/分支的时段（滑动窗口降噪，间隔超过2分钟视为新时段）→ JDBC Batch Insert 写入 `waka_heartbeat` → 清除 Redis 缓存。 | P0 | 待启动 | 未测试 |
| **TSK-WAKA-JOB-402** | `DailySummaryJob` | 每天凌晨1点执行（`@Scheduled`） | 聚合前一天所有用户的 `waka_heartbeat` 数据 → 按用户、项目、语言汇总时长 → upsert `waka_daily_summary`。 | P0 | 待启动 | 未测试 |
| **TSK-WAKA-JOB-403** | `TaskLinkSyncJob` | 每30分钟执行（`@Scheduled`） | 扫描 `waka_heartbeat` 中 branch 字段，用正则匹配任务 ID（如 `TSK-TASK-123`、`task/123`、`feature/TSK-123`）→ 将对应时段累加到 `waka_task_link`。 | P1 | 待启动 | 未测试 |

---

## 3. 前端 PC 端任务

### 3.1 个人编码统计页

| 任务 ID | 页面/组件路径 | 路由路径 | 页面元素 | 交互与 API 绑定 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-WAKA-FE-101** | `@/views/performance/wakatime.vue` | `/performance/wakatime` | 顶部：日期范围选择器（快捷选项：今天/本周/本月/近30天）；数据卡片：总编码时长、日均时长；ECharts 图表：语言分布饼图、每日编码时长柱状图、年度贡献热力图（类 GitHub 格子）；项目时长排行表：项目名、时长、占比进度条 | 1. 页面加载调用 `GET /wakapi/stats/summary`（默认近30天）；2. 切换日期范围重新查询；3. 热力图调用 `GET /wakapi/stats/heatmap`（当前年）；4. ECharts 热力图使用 `calendar` 类型渲染全年格子。 | P0 | 待启动 | 未测试 |

### 3.2 API Key 配置页

| 任务 ID | 页面/组件路径 | 路由路径 | 页面元素 | 交互与 API 绑定 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-WAKA-FE-201** | `@/views/performance/wakatime-setup.vue` | `/performance/wakatime-setup` | API Key 展示区（带复制按钮、重新生成按钮）；IDE 插件安装引导（VSCode/JetBrains/Vim 安装步骤说明）；配置示例代码块（`~/.wakatime.cfg` 内容，自动填入当前 API Key 和服务地址） | 1. 页面加载调用 `GET /wakapi/api-key` 获取 API Key；2. 复制按钮使用 `navigator.clipboard.writeText` 复制；3. 重新生成按钮弹出确认框后调用 `POST /wakapi/api-key/regenerate`。 | P0 | 待启动 | 未测试 |

### 3.3 编码排行榜组件

| 任务 ID | 页面/组件路径 | 路由路径 | 页面元素 | 交互与 API 绑定 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-WAKA-FE-301** | `@/views/performance/leaderboard.vue` | `/performance/leaderboard` | 顶部：项目下拉筛选、日期范围选择器；排行榜列表：排名（前三名金银铜图标）、用户头像、用户名、编码时长、时长进度条 | 1. 页面加载调用 `GET /wakapi/stats/leaderboard`；2. 切换项目或日期范围重新查询；3. 任务卡片组件（`TaskCard.vue`）中展示该任务的 Waka 编码时长，数据来源 `GET /wakapi/stats/task/{taskId}`。 | P2 | 待启动 | 未测试 |