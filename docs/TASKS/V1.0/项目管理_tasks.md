# 📂 项目管理服务微观开发任务清单 (项目管理_tasks.md)

> **最后更新**：2026-05-29 | **整体状态**：🟡 后端基础完成，前端页面待开发

## 📌 模块技术上下文

- **微服务模块**：`lest-project`（端口: 8082）
- **主目录**：`backend/lest-modules/lest-project`
- **包路径**：`com.lest.modules.project`
- **初始化 SQL**：❌ 尚未加入 `01_lest_platform_init.sql`（待补充）
- **前端 API**：`frontend-pc/src/api/project/index.ts` + `model/index.ts`
- **前端 PC 页面**：`frontend-pc/src/views/project/`（待开发）

---

## 🛠️ 1. 数据库 DDL 任务

| 任务 ID | 表名 | 说明 | 优先级 | 状态 | 备注 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-PROJ-DDL-101** | `project` | 项目表（id/name/description/owner_id/status/template/start_date/end_date/create_by/create_time/update_by/update_time/del_flag） | P0 | ⚠️ 代码已有，SQL 未补 | Java 实体存在，Mapper XML 存在，需加入 init.sql |
| **TSK-PROJ-DDL-102** | `project_member` | 项目成员表（project_id/user_id/role/create_time） | P0 | ⚠️ 代码已有，SQL 未补 | |
| **TSK-PROJ-DDL-103** | `iteration` | 迭代表（id/project_id/name/goal/status/start_date/end_date/create_time/update_time） | P1 | ⚠️ 代码已有，SQL 未补 | |
| **TSK-PROJ-DDL-104** | `milestone` | 里程碑表（id/project_id/name/description/target_date/status/create_time） | P2 | ⚠️ 代码已有，SQL 未补 | |
| **TSK-PROJ-DDL-105** | `milestone_iteration` | 里程碑-迭代关联表 | P2 | ⚠️ 代码已有，SQL 未补 | |

> **⚡ 下一步**：将上述建表 SQL 追加到 `backend/docker/mysql/db/01_lest_platform_init.sql`

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

### 3.3 独立页面（待开发）

| 任务 ID | 页面路径 | 路由 | 功能 | 优先级 | 状态 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-PROJ-FE-310** | `views/project/index.vue` | `/project` | 项目列表页（卡片网格/表格切换、新建项目弹窗） | P0 | 🔴 待开发 |
| **TSK-PROJ-FE-311** | `views/project/detail/index.vue` | `/project/:id` | 项目详情页（概况/成员/迭代/里程碑 Tab） | P0 | 🔴 待开发 |
| **TSK-PROJ-FE-312** | 项目详情 - 成员 Tab | `/project/:id/member` | 成员管理（角色分配/添加/移除） | P1 | 🔴 待开发 |
| **TSK-PROJ-FE-313** | 项目详情 - 迭代 Tab | `/project/:id/iteration` | 迭代看板（激活/关闭/创建） | P1 | 🔴 待开发 |
| **TSK-PROJ-FE-314** | 项目详情 - 里程碑 Tab | `/project/:id/milestone` | 里程碑时间线 | P2 | 🔴 待开发 |
| **TSK-PROJ-FE-315** | `views/project/dashboard/index.vue` | `/project/:id/dashboard` | 项目燃尽图（ECharts）+ 任务统计 | P1 | 🔴 待开发 |

