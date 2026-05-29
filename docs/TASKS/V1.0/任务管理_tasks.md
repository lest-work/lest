# 📋 任务管理服务微观开发任务清单 (任务管理_tasks.md)

> **最后更新**：2026-05-29 | **整体状态**：🟡 后端基础完成，前端页面待开发

## 📌 模块技术上下文

- **微服务模块**：`lest-task`（端口: 8083）
- **主目录**：`backend/lest-modules/lest-task`
- **包路径**：`com.lest.modules.task`
- **初始化 SQL**：❌ 尚未加入 `01_lest_platform_init.sql`（待补充）
- **前端 API**：`frontend-pc/src/api/task/index.ts` + `model/index.ts`
- **前端 PC 页面**：`frontend-pc/src/views/task/`（待开发）

---

## 🛠️ 1. 数据库 DDL 任务

| 任务 ID | 表名 | 说明 | 优先级 | 状态 | 备注 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-TASK-DDL-101** | `task` | 任务主表（id/title/description/project_id/iteration_id/parent_id/task_type/priority/status/assignee_id/due_date/start_time/estimated_hours/actual_hours/sort/del_flag） | P0 | ⚠️ 代码已有，SQL 未补 | Java 实体 Task.java 存在，需加入 init.sql |
| **TSK-TASK-DDL-102** | `label` | 标签主表（id/name/color/project_id） | P1 | ⚠️ 代码已有，SQL 未补 | |
| **TSK-TASK-DDL-103** | `task_label` | 任务-标签关联（task_id/label_id） | P1 | ⚠️ 代码已有，SQL 未补 | |
| **TSK-TASK-DDL-104** | `task_watcher` | 任务关注人（task_id/user_id/create_time） | P2 | ⚠️ 代码已有，SQL 未补 | |
| **TSK-TASK-DDL-105** | `task_worklog` | 工时记录（id/task_id/user_id/hours/log_date/description/create_time） | P1 | ⚠️ 代码已有，SQL 未补 | |
| **TSK-TASK-DDL-106** | `task_comment` | 任务评论（id/task_id/user_id/content/parent_id/create_time） | P1 | ⚠️ 代码已有，SQL 未补 | |
| **TSK-TASK-DDL-107** | `task_commit` | 关联代码提交（id/task_id/repo/commit_hash/message/author/committed_at） | P2 | ⚠️ 代码已有，SQL 未补 | |
| **TSK-TASK-DDL-108** | `task_dependency` | 任务依赖关系（id/task_id/depends_on_id/type） | P2 | ⚠️ 代码已有，SQL 未补 | |

> **⚡ 下一步**：将上述建表 SQL 追加到 `backend/docker/mysql/db/01_lest_platform_init.sql`

---

## 💻 2. 后端 API 任务

### 2.1 任务 CRUD（TaskController，@RequestMapping("") 已修复）

| 任务 ID | Endpoint | 说明 | 优先级 | 状态 | 备注 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-TASK-API-201** | `GET /task/list` | 分页查询任务列表（支持 assignee_id/project_id/status/priority） | P0 | 🟢 已完成 | Cascade 2026-05-29，网关路由已修复 |
| **TSK-TASK-API-202** | `GET /task/{id}` | 查询任务详情 | P0 | 🟢 已完成 | |
| **TSK-TASK-API-203** | `POST /task` | 新增任务 | P0 | 🟢 已完成 | |
| **TSK-TASK-API-204** | `PUT /task/{id}` | 修改任务 | P0 | 🟢 已完成 | |
| **TSK-TASK-API-205** | `DELETE /task/{id}` | 删除任务 | P1 | 🟢 已完成 | |
| **TSK-TASK-API-206** | `PUT /task/status/{id}` | 更新状态（todo/in_progress/completed） | P0 | 🟢 已完成 | |
| **TSK-TASK-API-207** | `PUT /task/claim/{id}` | 认领任务 | P1 | 🟢 已完成 | |
| **TSK-TASK-API-208** | `GET /task/board` | 看板视图（按状态分组） | P1 | 🟢 已完成 | |
| **TSK-TASK-API-209** | `GET /task/gantt` | 甘特图数据 | P2 | 🟢 已完成 | |
| **TSK-TASK-API-210** | `GET /task/subtask/{id}` | 查询子任务列表 | P1 | 🟢 已完成 | |
| **TSK-TASK-API-211** | `GET /task/worklog/{id}` | 查询工时记录 | P1 | 🟢 已完成 | |
| **TSK-TASK-API-212** | `POST /task/worklog` | 新增工时记录 | P1 | 🟢 已完成 | |
| **TSK-TASK-API-213** | `GET /task/commit/{id}` | 查询关联代码提交 | P2 | 🟢 已完成 | |

### 2.2 标签（LabelController）

| 任务 ID | Endpoint | 说明 | 优先级 | 状态 |
| :--- | :--- | :--- | :--- | :--- |
| **TSK-TASK-API-221** | `GET /label/list` | 查询标签列表 | P1 | 🟢 已完成 |
| **TSK-TASK-API-222** | `POST /label` | 新增标签 | P1 | 🟢 已完成 |
| **TSK-TASK-API-223** | `DELETE /label/{id}` | 删除标签 | P1 | 🟢 已完成 |

### 2.3 Webhook（WebhookController）

| 任务 ID | Endpoint | 说明 | 优先级 | 状态 |
| :--- | :--- | :--- | :--- | :--- |
| **TSK-TASK-API-231** | `POST /webhook/gitlab` | 接收 GitLab Webhook，关联 commit | P2 | 🟢 已完成 |
| **TSK-TASK-API-232** | `POST /webhook/github` | 接收 GitHub Webhook | P2 | 🟢 已完成 |

---

## 🎨 3. 前端任务

### 3.1 API 层

| 任务 ID | 文件 | 说明 | 状态 |
| :--- | :--- | :--- | :--- |
| **TSK-TASK-FE-301** | `src/api/task/index.ts` | CRUD + 状态/认领/看板/甘特/工时 API | 🟢 已完成，Cascade 2026-05-29 |
| **TSK-TASK-FE-302** | `src/api/task/model/index.ts` | Task/TaskParam 类型定义 | 🟢 已完成，Cascade 2026-05-29 |

### 3.2 首页仪表盘集成

| 任务 ID | 组件 | 说明 | 状态 |
| :--- | :--- | :--- | :--- |
| **TSK-TASK-FE-303** | `views/index/components/task-card.vue` | 首页"我的任务"卡片，按当前用户过滤，显示优先级和状态 | 🟢 已完成，Cascade 2026-05-29 |

### 3.3 独立页面（待开发）

| 任务 ID | 页面路径 | 路由 | 功能 | 优先级 | 状态 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-TASK-FE-310** | `views/task/index.vue` | `/task` | 任务列表页（筛选：项目/迭代/状态/优先级/负责人） | P0 | 🔴 待开发 |
| **TSK-TASK-FE-311** | `views/task/board/index.vue` | `/task/board` | 看板视图（列：待办/进行中/已完成，拖拽更新状态） | P0 | 🔴 待开发 |
| **TSK-TASK-FE-312** | `views/task/gantt/index.vue` | `/task/gantt` | 甘特图（ECharts 横道图） | P1 | 🔴 待开发 |
| **TSK-TASK-FE-313** | 任务详情抽屉 | — | 标题/描述/子任务/评论/工时/关联 commit | P0 | 🔴 待开发 |
| **TSK-TASK-FE-314** | 工时日志组件 | — | 工时列表 + 新增工时弹窗 | P1 | 🔴 待开发 |
