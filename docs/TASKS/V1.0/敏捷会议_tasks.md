# 敏捷会议开发任务列表

## 模块上下文

- **微服务模块**：`lest-meeting`（端口: 8005）
- **主目录**：`backend/lest-modules/lest-meeting`
- **包路径**：`com.lest.meeting`
- **数据库 Schema**：`lest_meeting`（Flyway 目录：`src/main/resources/db/migration/meeting`）
- **前端 PC 目录**：`frontend-pc/src/views/meeting/`

---

## 1. 数据库迁移任务（DDL Tasks）

| 任务 ID | 类型 | 目标文件 | 详细实现细节与约束规范 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-MEET-DDL-101** | DDL | `V1.0.0__init_meeting_table.sql` | 创建 `meet_meeting` 表：`id`(BIGINT PK)、`project_id`(BIGINT NOT NULL)、`iteration_id`(BIGINT)、`type`(VARCHAR 32, daily/weekly/sprint_planning/sprint_retrospective/sprint_review/brainstorm/one_on_one)、`title`(VARCHAR 256)、`status`(VARCHAR 16, scheduled/in_progress/completed/cancelled)、`start_time`(DATETIME NOT NULL)、`duration`(INT DEFAULT 15, 分钟)、`location`(VARCHAR 512)、`organizer_id`(BIGINT NOT NULL)、`created_at`、`updated_at`。建立索引 `idx_project_id`、`idx_iteration_id`、`idx_start_time`。 | P0 | 待启动 | 未测试 |
| **TSK-MEET-DDL-102** | DDL | `V1.0.1__init_participant_minutes_tables.sql` | 创建 `meet_participant` 表：`id`(BIGINT PK)、`meeting_id`(BIGINT NOT NULL)、`user_id`(BIGINT NOT NULL)、`status`(VARCHAR 16 DEFAULT 'pending', pending/completed)、`report_content`(JSON, 站会发言内容)、`reported_at`(DATETIME)、`created_at`，建立索引 `idx_meeting_id`、唯一索引 `uk_meeting_user`(meeting_id, user_id)。创建 `meet_minutes` 表：`id`(BIGINT PK)、`meeting_id`(BIGINT UNIQUE)、`content`(TEXT)、`decisions`(JSON)、`action_items`(JSON)、`recorder_id`(BIGINT)、`created_at`、`updated_at`。 | P0 | 待启动 | 未测试 |
| **TSK-MEET-DDL-103** | DDL | `V1.0.2__init_action_item_table.sql` | 创建 `meet_action_item` 表：`id`(BIGINT PK)、`meeting_id`(BIGINT NOT NULL)、`task_id`(BIGINT NULL, 创建为任务后填充)、`assignee_id`(BIGINT)、`assignee_name`(VARCHAR 64)、`title`(VARCHAR 255 NOT NULL)、`description`(TEXT)、`due_date`(DATE)、`status`(VARCHAR 16 DEFAULT 'pending', pending/in_progress/completed)、`priority`(VARCHAR 16 DEFAULT 'medium')、`completed_at`(DATETIME)、`created_by`(BIGINT NOT NULL)、`created_at`、`updated_at`。建立索引 `idx_meeting_id`、`idx_task_id`、`idx_assignee_id`、`idx_status`。 | P0 | 待启动 | 未测试 |

---

## 2. 后端 API 任务

### 2.1 会议管理接口

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-MEET-API-101** | `MeetingController` | `POST /meeting` | Body: `{ type, title, projectId, iterationId?, startTime, duration, participantIds[], location?, reminderMinutes? }` | `{ id, type, title }` | 1. 校验 projectId 有效；2. 插入 `meet_meeting`；3. 批量插入 `meet_participant`；4. 发送 `MEETING_SCHEDULED` 通知事件（通过 Kafka 发送到通知模块）；5. 若 reminderMinutes 不为空，创建定时提醒任务（存入 Redis 延迟队列）。 | P0 | 待启动 | 未测试 |
| **TSK-MEET-API-102** | `MeetingController` | `GET /meeting/page` | Query: `projectId`(必填)、`type`(可选)、`status`(可选)、`date`(可选，YYYY-MM-DD)、`page`、`size` | `{ records: [{ id, type, title, status, startTime, duration, participantCount, iterationName }], total, page, size }` | 按 `start_time` 升序排列；`participantCount` 通过 COUNT 聚合；`iterationName` 通过 Feign 调用项目模块获取。 | P0 | 待启动 | 未测试 |
| **TSK-MEET-API-103** | `MeetingController` | `GET /meeting/{id}` | Path: `id` | `{ id, type, title, status, projectId, iterationId, startTime, duration, location, organizer: { userId, username }, participants: [{ userId, username, status, report }], minutes: { content, decisions[], actionItems[] }, linkedTasks[] }` | 查询会议详情；participants 通过 Feign 聚合用户信息；linkedTasks 从 `meet_action_item.task_id` 关联查询。 | P0 | 待启动 | 未测试 |
| **TSK-MEET-API-104** | `MeetingController` | `PUT /meeting/{id}/status` | Path: `id`；Body: `{ status: "in_progress" 或 "completed" 或 "cancelled" }` | `{ code: 200, data: null }` | 更新会议状态；status 变为 cancelled 时发送 `MEETING_CANCELLED` 通知；status 变为 completed 时发送 `SPRINT_ENDED` 通知（若为 sprint_review 类型）。 | P0 | 待启动 | 未测试 |

### 2.2 站会发言接口

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-MEET-API-201** | `MeetingReportController` | `POST /meeting/{id}/report` | Path: `id`；Body: `{ yesterdayDone, todayPlan, blockers, linkedTaskIds[] }` | `{ code: 200, data: null }` | 1. 校验当前用户是会议参与者；2. 更新 `meet_participant.report_content`（JSON 存储三段内容）和 `reported_at`；3. 更新 status 为 completed。 | P0 | 待启动 | 未测试 |

### 2.3 会议纪要接口

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-MEET-API-301** | `MinutesController` | `PUT /meeting/{id}/minutes` | Path: `id`；Body: `{ content, decisions: [{ content }], actionItems: [{ content, assigneeId, dueDate }] }` | `{ code: 200, data: null }` | upsert `meet_minutes`；同步更新 `meet_action_item` 表（先删后插）；记录人设为当前用户。 | P0 | 待启动 | 未测试 |
| **TSK-MEET-API-302** | `MinutesController` | `POST /meeting/{id}/minutes/share` | Path: `id` | `{ code: 200, data: null }` | 发送 `MEETING_MINUTES_SHARED` 通知事件，通知所有参与者；通知内容包含会议标题和纪要摘要。 | P1 | 待启动 | 未测试 |

### 2.4 行动项接口

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-MEET-API-401** | `ActionItemController` | `GET /meeting/{id}/action-items` | Path: `id` | `{ data: [{ id, title, description, assigneeId, assigneeName, dueDate, status, taskId, priority }] }` | 查询会议的行动项列表；`taskId` 不为空时表示已创建为任务。 | P0 | 待启动 | 未测试 |
| **TSK-MEET-API-402** | `ActionItemController` | `POST /meeting/{id}/action-items/create-tasks` | Path: `id`；Body: `{ actionItemIds: number[] }` | `{ createdTaskIds: number[] }` | 将指定行动项批量创建为任务；通过 Feign 调用 `lest-task` 的 `POST /task` 接口；创建成功后更新 `meet_action_item.task_id`；发送 `MEETING_ACTION_ITEMS_CREATED` 通知。 | P0 | 待启动 | 未测试 |
| **TSK-MEET-API-403** | `ActionItemController` | `PUT /meeting/action-items/{actionItemId}/status` | Path: `actionItemId`；Body: `{ status }` | `{ code: 200, data: null }` | 更新行动项状态；若已关联任务，同步更新任务状态（通过 Feign 调用）。 | P1 | 待启动 | 未测试 |

---

## 3. 前端 PC 端任务

### 3.1 会议列表页

| 任务 ID | 页面/组件路径 | 路由路径 | 页面元素 | 交互与 API 绑定 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-MEET-FE-101** | `@/views/meeting/index.vue` | `/meeting` | 顶部：类型 Tab（全部/今日/本周/已结束）、创建会议按钮；会议列表：日期时间、会议类型图标、会议标题、关联迭代标签、状态标签、参与人数、操作列（进入会议按钮、编辑按钮、取消按钮）；底部：分页组件 | 1. 页面加载调用 `GET /meeting/page`；2. 创建会议按钮打开 `MeetingCreateDialog`；3. 进入会议按钮跳转 `/meeting/{id}`；4. 取消按钮弹出确认框后调用 `PUT /meeting/{id}/status`（status=cancelled）。 | P0 | 待启动 | 未测试 |

### 3.2 每日站会页

| 任务 ID | 页面/组件路径 | 路由路径 | 页面元素 | 交互与 API 绑定 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-MEET-FE-201** | `@/views/meeting/standup.vue` | `/meeting/:id/standup` | 顶部：会议标题、当前迭代信息、结束站会按钮；参与人员状态卡片（头像+姓名+发言状态）；当前发言人区块：昨日完成文本域、今日计划文本域、遇到问题文本域、关联任务多选（从我的任务列表选择）、提交发言按钮；历史发言记录折叠展示 | 1. 页面加载调用 `GET /meeting/{id}` 获取会议和参与者信息；2. 提交发言调用 `POST /meeting/{id}/report`；3. 结束站会调用 `PUT /meeting/{id}/status`（status=completed）；4. 关联任务选择器调用 `GET /task/page`（筛选当前用户的任务）。 | P0 | 待启动 | 未测试 |

### 3.3 会议详情/纪要页

| 任务 ID | 页面/组件路径 | 路由路径 | 页面元素 | 交互与 API 绑定 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-MEET-FE-301** | `@/views/meeting/detail.vue` | `/meeting/:id` | 顶部：会议标题、类型标签、状态标签、时间/地点、参与人头像列表；Tab 页：会议纪要（富文本编辑器 + 决议列表 + 行动项列表）、参与者发言记录（站会专用）；行动项区块：行动项列表（含负责人/截止日期/状态/关联任务链接）、批量创建任务按钮、分享纪要按钮 | 1. 页面加载调用 `GET /meeting/{id}`；2. 纪要编辑保存调用 `PUT /meeting/{id}/minutes`；3. 分享纪要调用 `POST /meeting/{id}/minutes/share`；4. 批量创建任务：勾选行动项后调用 `POST /meeting/{id}/action-items/create-tasks`；5. AI 总结按钮（若会议已结束）调用 `POST /ai/meeting-summary`。 | P0 | 待启动 | 未测试 |