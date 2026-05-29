# 📂 项目管理服务微观开发任务清单 (项目管理_tasks.md)

> **最后更新**：2026-05-30 | **整体状态**：� 后端完成，前端核心页面已完成

## 📌 模块技术上下文

- **微服务模块**：`lest-project`（端口: 8082）
- **主目录**：`backend/lest-modules/lest-project`
- **包路径**：`com.lest.modules.project`
- **初始化 SQL**：✅ 已加入 `01_lest_platform_init.sql`（2026-05-30，project/project_member/iteration/milestone/milestone_iteration 建表 SQL）
- **前端 API**：`frontend-pc/src/api/project/index.ts` + `model/index.ts`（含 Iteration/Milestone/Member 补全）
- **前端 PC 页面**：`frontend-pc/src/views/project/`（✅ 核心页面已完成）

---

## 🛠️ 1. 数据库 DDL 任务

| 任务 ID | 表名 | 说明 | 优先级 | 状态 | 备注 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-PROJ-DDL-101** | `project` | 项目表（id/name/description/owner_id/status/template/start_date/end_date/create_by/create_time/update_by/update_time/deleted） | P0 | 🟢 已完成 | 2026-05-30 加入 init.sql |
| **TSK-PROJ-DDL-102** | `project_member` | 项目成员表（project_id/user_id/role/joined_at） | P0 | 🟢 已完成 | |
| **TSK-PROJ-DDL-103** | `iteration` | 迭代表（id/project_id/name/goal/status/start_date/end_date/completed_at/create_time/update_time） | P1 | 🟢 已完成 | |
| **TSK-PROJ-DDL-104** | `milestone` | 里程碑表（id/project_id/name/description/target_date/create_time） | P2 | 🟢 已完成 | |
| **TSK-PROJ-DDL-105** | `milestone_iteration` | 里程碑-迭代关联表 | P2 | 🟢 已完成 | |

---

## 💻 2. 后端 API 任务

### 2.1 项目 CRUD（ProjectController）

| 任务 ID | Endpoint | 说明 | 优先级 | 状态 | 备注 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-PROJ-API-201** | `GET /project/list` | 分页查询项目列表（PageHelper） | P0 | 🟢 已完成 | Cascade 2026-05-29，Controller+Service+Mapper 已实现 |
| **TSK-PROJ-API-202** | `GET /project/{id}` | 查询项目详情 | P0 | � 已完成 | |
| **TSK-PROJ-API-203** | `POST /project` | 新增项目 | P0 | � 已完成 | |
| **TSK-PROJ-API-204** | `PUT /project/{id}` | 修改项目 | P0 | � 已完成 | |
| **TSK-PROJ-API-205** | `DELETE /project/{id}` | 删除项目 | P1 | 🟢 已完成 | |
| **TSK-PROJ-API-206** | `PUT /project/archive/{id}` | 归档项目 | P2 | � 已完成 | |
| **TSK-PROJ-API-207** | `GET /project/member/{id}` | 获取项目成员列表 | P0 | � 已完成 | |

### 2.2 迭代与里程碑（IterationController / MilestoneController）

| 任务 ID | Endpoint | 说明 | 优先级 | 状态 |
| :--- | :--- | :--- | :--- | :--- |
| **TSK-PROJ-API-221** | `GET /iteration/list` | 查询迭代列表 | P1 | � 已完成 |
| **TSK-PROJ-API-222** | `POST /iteration` | 新增迭代 | P1 | � 已完成 |
| **TSK-PROJ-API-223** | `PUT /iteration/{id}` | 更新迭代状态 | P1 | 🟢 已完成 |
| **TSK-PROJ-API-231** | `GET /milestone/list` | 查询里程碑 | P2 | � 已完成 |
| **TSK-PROJ-API-232** | `POST /milestone` | 新增里程碑 | P2 | � 已完成 |

---

## 🎨 3. 前端任务

### 3.1 API 层

| 任务 ID | 文件 | 说明 | 状态 |
| :--- | :--- | :--- | :--- |
| **TSK-PROJ-FE-API-301** | `src/api/project/index.ts` | 完整项目 CRUD + 归档 + 成员 API 函数 | � 已完成，Cascade 2026-05-29 |
| **TSK-PROJ-FE-API-302** | `src/api/project/model/index.ts` | Project/ProjectMember/ProjectParam 类型定义 | � 已完成，Cascade 2026-05-29 |

### 3.2 首页仪表盘集成

| 任务 ID | 组件 | 说明 | 状态 |
| :--- | :--- | :--- | :--- |
| **TSK-PROJ-FE-303** | `views/index/components/project-card.vue` | 首页项目进度卡片，接入 `/project/list`，显示状态和进度条 | 🟢 已完成，Cascade 2026-05-29 |

### 3.3 独立页面

| 任务 ID | 页面路径 | 路由 | 功能 | 优先级 | 状态 | 备注 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-PROJ-FE-310** | `views/project/index.vue` | `/project` | 项目列表页（卡片网格、新建/编辑/归档/删除） | P0 | � 已完成 | 2026-05-30 |
| **TSK-PROJ-FE-311** | `views/project/detail/index.vue` | `/project/detail/:id` | 项目详情页（概况/成员/迭代/里程碑 Tab） | P0 | � 已完成 | 2026-05-30 |
| **TSK-PROJ-FE-312** | 项目详情 - 成员 Tab（已内置于 detail） | - | 成员列表 + 移除 | P1 | � 已完成 | 添加成员功能待完善 |
| **TSK-PROJ-FE-313** | 项目详情 - 迭代 Tab（已内置于 detail） | - | 迭代 CRUD（创建/编辑/删除） | P1 | � 已完成 | |
| **TSK-PROJ-FE-314** | 项目详情 - 里程碑 Tab（已内置于 detail） | - | 里程碑时间线（新建/删除） | P2 | � 已完成 | |
| **TSK-PROJ-FE-315** | `views/project/dashboard/index.vue` | `/project/:id/dashboard` | 项目燃尽图（ECharts）+ 任务统计 | P1 | 🔴 待开发 | 下一阶段 |

