# 团队绩效开发任务列表

## 模块上下文

- **微服务模块**：`lest-performance`（端口: 8012）
- **主目录**：`backend/lest-modules/lest-performance`
- **包路径**：`com.lest.performance`
- **数据库 Schema**：`lest_performance`（Flyway 目录：`src/main/resources/db/migration/performance`）
- **前端 PC 目录**：`frontend-pc/src/views/performance/`

---

## 1. 数据库迁移任务（DDL Tasks）

| 任务 ID | 类型 | 目标文件 | 详细实现细节与约束规范 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-PERF-DDL-101** | DDL | `V1.0.0__init_work_log_table.sql` | 创建 `perf_work_log` 表：`id`(BIGINT PK)、`user_id`(BIGINT NOT NULL)、`task_id`(BIGINT)、`date`(DATE NOT NULL)、`hours`(DECIMAL 4,1 NOT NULL)、`description`(VARCHAR 512)、`created_at`、`updated_at`。建立索引 `idx_user_id`、`idx_task_id`、`idx_date`、唯一索引 `uk_user_task_date`(user_id, task_id, date)。 | P0 | 待启动 | 未测试 |
| **TSK-PERF-DDL-102** | DDL | `V1.0.1__init_metric_goal_tables.sql` | 创建 `perf_metric_config` 表：`id`(BIGINT PK)、`metric_code`(VARCHAR 32 NOT NULL)、`metric_name`(VARCHAR 64)、`metric_type`(VARCHAR 16, task/code/review/work/meeting)、`unit`(VARCHAR 16)、`weight`(DECIMAL 3,2 DEFAULT 1.00)、`formula`(VARCHAR 256)、`target_value`(DECIMAL 10,2)、`enabled`(TINYINT DEFAULT 1)、`created_at`、`updated_at`。创建 `perf_goal` 表：`id`(BIGINT PK)、`user_id`(BIGINT NOT NULL)、`metric_code`(VARCHAR 32)、`target_value`(DECIMAL 10,2)、`start_date`(DATE)、`end_date`(DATE)、`status`(VARCHAR 16 DEFAULT 'in_progress')、`created_at`、`updated_at`，建立索引 `idx_user_id`、`idx_metric`。 | P2 | 待启动 | 未测试 |

---

## 2. 后端 API 任务

### 2.1 工作日志接口

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-PERF-API-101** | `WorkLogController` | `POST /performance/worklog` | Body: `{ taskId?, date, hours, description? }` | `{ id, taskId, date, hours }` | 1. 校验 hours 范围（0.5-24，精确到 0.5）；2. 校验同一用户同一任务同一日期唯一，重复抛 `WORK_LOG_DUPLICATE(14003)`；3. 插入 `perf_work_log`；4. 若 taskId 不为空，同步更新任务模块的 `actual_hours`（通过 Feign 调用）。 | P0 | 待启动 | 未测试 |
| **TSK-PERF-API-102** | `WorkLogController` | `GET /performance/worklog/page` | Query: `userId`(可选，默认当前用户)、`taskId`(可选)、`startDate`、`endDate`、`page`、`size` | `{ records: [{ id, userId, username, taskId, taskTitle, date, hours, description, createdAt }], total, page, size }` | 查询工作日志；taskTitle 通过 Feign 聚合任务信息；按 `date` 倒序。 | P0 | 待启动 | 未测试 |
| **TSK-PERF-API-103** | `WorkLogController` | `PUT /performance/worklog/{id}` | Path: `id`；Body: `{ hours, description }` | `{ code: 200, data: null }` | 更新工作日志；同步更新任务模块的 `actual_hours`（先减旧值再加新值）。 | P0 | 待启动 | 未测试 |
| **TSK-PERF-API-104** | `WorkLogController` | `DELETE /performance/worklog/{id}` | Path: `id` | `{ code: 200, data: null }` | 删除工作日志；同步减少任务模块的 `actual_hours`。 | P0 | 待启动 | 未测试 |

### 2.2 绩效统计接口

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-PERF-API-201** | `PerformanceController` | `GET /performance/personal` | Query: `userId`(可选，默认当前用户)、`startDate`、`endDate` | `{ userId, username, period, metrics: { tasksCompleted: { value, target, trend }, commitsCount, linesAdded, reviewsCount, workHours }, goals: [{ metric, target, current, progress, status }], activity: [{ date, commits, tasks, reviews }] }` | 聚合多数据源：任务数据通过 Feign 调用任务模块；代码数据通过 Feign 调用代码模块；工时数据查询本模块 `perf_work_log`；trend 为与上一周期的环比百分比。 | P0 | 待启动 | 未测试 |
| **TSK-PERF-API-202** | `PerformanceController` | `GET /performance/team` | Query: `projectId`(必填)、`startDate`、`endDate` | `{ projectId, period, teamMetrics: { tasksCompleted, commitsCount, linesAdded, reviewsCount, workHours }, memberRankings: [{ rank, userId, username, avatar, tasksCompleted, commitsCount, reviewsCount, score }], velocity: [{ sprintId, sprintName, planned, completed }] }` | 聚合项目所有成员的绩效数据；score 按各指标权重计算综合得分；velocity 通过 Feign 调用项目模块获取迭代数据。 | P0 | 待启动 | 未测试 |
| **TSK-PERF-API-203** | `PerformanceController` | `GET /performance/worklog-stats` | Query: `projectId`(可选)、`userId`(可选)、`startDate`、`endDate` | `{ totalHours, byUser: [{ userId, username, hours }], byTask: [{ taskId, taskTitle, hours }], dailyTrend: [{ date, hours }] }` | 聚合查询 `perf_work_log`；支持按项目或用户过滤；dailyTrend 按日期分组汇总。 | P0 | 待启动 | 未测试 |

---

## 3. 前端 PC 端任务

### 3.1 个人绩效看板

| 任务 ID | 页面/组件路径 | 路由路径 | 页面元素 | 交互与 API 绑定 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-PERF-FE-101** | `@/views/performance/personal.vue` | `/performance/personal` | 顶部：时间范围选择器（快捷选项：本周/本月/近30天/近90天）、导出报告按钮；关键指标卡片（完成任务/代码提交/评审次数/工作时长，含环比箭头）；目标达成进度条列表；ECharts 工作曲线图（每日活跃度柱状图）；工时统计表（按任务分组） | 1. 页面加载调用 `GET /performance/personal`；2. 切换时间范围重新查询；3. 导出报告按钮触发 PDF 导出（前端 html2canvas 或后端生成）。 | P0 | 待启动 | 未测试 |

### 3.2 团队绩效看板

| 任务 ID | 页面/组件路径 | 路由路径 | 页面元素 | 交互与 API 绑定 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-PERF-FE-201** | `@/views/performance/team.vue` | `/performance/team` | 顶部：项目选择下拉、时间范围选择器、导出报告按钮；团队概览卡片（4个指标+环比）；成员贡献排行表（排名/头像/姓名/完成任务/代码提交/评审次数/综合得分）；ECharts Sprint 速率折线图（计划 vs 完成）；工时分布饼图（按成员） | 1. 页面加载调用 `GET /performance/team`；2. 切换项目或时间范围重新查询；3. 点击成员行跳转该成员的个人绩效页。 | P0 | 待启动 | 未测试 |

### 3.3 工时登记组件

| 任务 ID | 页面/组件路径 | 路由路径 | 页面元素 | 交互与 API 绑定 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-PERF-FE-301** | `@/components/WorkLogDialog.vue` | — | 弹窗：关联任务选择器（可搜索）、工作日期选择器、工时输入框（步进 0.5h）、工作描述文本域、确认按钮、取消按钮 | 1. 任务选择器调用 `GET /task/page`（筛选当前用户的任务）；2. 提交调用 `POST /performance/worklog`；3. 此组件在任务详情页的工时 Tab 中嵌入使用。 | P0 | 待启动 | 未测试 |