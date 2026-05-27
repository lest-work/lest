# 📂 项目管理服务微观开发任务清单 (项目管理_tasks.md)

## 📌 模块技术上下文
* **微服务模块**：`lest-project` (运行端口: 8003)
* **主目录**：`backend/lest-modules/lest-project`
* **包路径**：`com.lest.project`
* **数据库 Schema**：`lest_project` (Flyway 目录: `src/main/resources/db/migration/project`)
* **前端 PC 目录**：`frontend-pc/src/views/project/`

---

## 🛠️ 1. 数据库与初始化迁移任务 (Database DDL Tasks)

| 任务 ID | 类型 | 任务名称与目标文件 | 详细实现细节与约束规范 | 优先级 | 状态 | 测试结果 | 备注与关联提交 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-PROJ-DDL-101** | DDL 变更 | `V1.0.0__init_project.sql` | 1. 创建 `project_info` 表，含：`id`, `name`, `description`, `owner_id`, `status` (active/archived), `tenant_id`, `created_at`, `updated_at`, `deleted` (TINYINT, 0=未删除, 1=已删除)。<br>2. 建立联合唯一索引 `uk_tenant_project_name` 约束 `(tenant_id, name, deleted)`，防止同租户同名项目。 | P0 | 🔴 待启动 | 未测试 | |
| **TSK-PROJ-DDL-102** | DDL 变更 | `V1.0.1__init_project_member.sql` | 1. 创建 `project_member` 关联表，包含：`project_id` (BIGINT), `user_id` (BIGINT), `role` (VARCHAR, admin/developer/viewer), `created_at`。<br>2. 建立联合主键 `pk_project_member` 为 `(project_id, user_id)`。 | P0 | 🔴 待启动 | 未测试 | |
| **TSK-PROJ-DDL-103** | DDL 变更 | `V1.0.2__init_project_sprint.sql` | 1. 创建 `project_sprint` 迭代表，包含：`id`, `project_id`, `name`, `goal`, `status` (draft/active/closed), `start_date`, `end_date`, `created_at`, `updated_at`。<br>2. 建立索引 `idx_sprint_project_id` 优化针对特定项目的迭代查询。 | P1 | 🔴 待启动 | 未测试 | |
| **TSK-PROJ-DDL-104** | DDL 变更 | `V1.0.3__init_project_milestone.sql` | 创建 `project_milestone` 里程碑表，包含：`id`, `project_id`, `name`, `description`, `target_date`, `status` (todo/reached), `created_at`。 | P2 | 🔴 待启动 | 未测试 | |

---

## 💻 2. 后端 API 级精细任务 (Backend API Micro-Tasks)

### 2.1 项目基础维护接口

| 任务 ID | 类/接口路径 | 方法/Endpoint | 详细类签名、契约与逻辑约束 | 优先级 | 状态 | 测试结果 | 备注与关联提交 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-PROJ-API-201** | `ProjectController` | `POST /project` | **创建项目**：<br>1. 入参：Java 25 Record `CreateProjectRequest(String name, String description)`。<br>2. 逻辑：提取 Context 租户 `tenantId` 和用户 `ownerId`，校验唯一性，若冲突抛出 `BizException(400, "项目名已存在")`。<br>3. 事务：创建 `project_info` 的同时，向 `project_member` 插入一条记录，将 `ownerId` 标记为 `admin`。 | P0 | 🔴 待启动 | 未测试 | |
| **TSK-PROJ-API-202** | `ProjectController` | `GET /project/page` | **分页查询项目**：<br>1. 入参：`QueryProjectParam(String name, String status, Long page, Long size)`。<br>2. 逻辑：支持基于 `name` 模糊搜索，基于 `status` 精确搜索，必须带上 ThreadLocal 中的当前 `tenantId` 做多租户物理隔离。 | P0 | 🔴 待启动 | 未测试 | |
| **TSK-PROJ-API-203** | `ProjectController` | `GET /project/{id}` | **获取项目详情**：<br>1. 逻辑：校验项目 ID 是否属于当前 `tenantId`，不属于或已被软删除则抛 `BizException(404, "项目不存在")`。返回 `ProjectVO`。 | P0 | 🔴 待启动 | 未测试 | |
| **TSK-PROJ-API-204** | `ProjectController` | `PUT /project/{id}` | **更新项目**：<br>1. 入参：`UpdateProjectRequest(String name, String description)`。<br>2. 逻辑：越权校验（只有 `project_member` 中为 `admin` 的用户可修改）。更新项目基础信息。 | P1 | 🔴 待启动 | 未测试 | |
| **TSK-PROJ-API-205** | `ProjectController` | `DELETE /project/{id}` | **删除项目（软删除）**：<br>1. 逻辑：校验操作人角色为 `admin`。更新 `deleted = 1`。<br>2. 事件：发布 Kafka 消息 `lest-project.projectDeleted` 广播清除下游的任务关系。 | P1 | 🔴 待启动 | 未测试 | |
| **TSK-PROJ-API-206** | `ProjectController` | `PUT /project/{id}/archive` | **归档项目**：<br>1. 逻辑：更新 `status = 'archived'`。归档后该项目下的任务不可再被新建或更改。 | P2 | 🔴 待启动 | 未测试 | |

### 2.2 成员管理接口

| 任务 ID | 类/接口路径 | 方法/Endpoint | 详细类签名、契约与逻辑约束 | 优先级 | 状态 | 测试结果 | 备注与关联提交 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-PROJ-API-211** | `ProjectMemberController` | `GET /project/{id}/member` | **获取项目成员列表**：<br>1. 返回：`List<ProjectMemberVO>` 包含关联用户 ID、用户名和当前项目角色。<br>2. 逻辑：在 Service 层通过内存聚合，一次性拉取 `sys_user` 详情（跨服务 RPC 聚合）。 | P0 | 🔴 待启动 | 未测试 | |
| **TSK-PROJ-API-212** | `ProjectMemberController` | `POST /project/{id}/member` | **添加项目成员**：<br>1. 入参：`AddMemberRequest(Long userId, String role)`。<br>2. 逻辑：校验 `userId` 是否为该租户的有效用户，校验 `role` 范围 (admin/developer/viewer)。联合主键防重。 | P1 | 🔴 待启动 | 未测试 | |
| **TSK-PROJ-API-213** | `ProjectMemberController` | `DELETE /project/{id}/member/{userId}` | **移除项目成员**：<br>1. 逻辑：只允许 `admin` 操作；不允许自我移除（如果当前用户是唯一的 admin）。<br>2. 级联事件：广播 Kafka 消息 `lest-project.memberRemoved`（Payload：`{projectId: 1, userId: 101}`）以释放指派。 | P1 | 🔴 待启动 | 未测试 | |
| **TSK-PROJ-API-214** | `ProjectMemberController` | `PUT /project/{id}/member/{userId}/role` | **修改成员角色**：<br>1. 入参：`ModifyRoleRequest(String role)`。<br>2. 逻辑：更新 `project_member` 的 `role` 字段。 | P2 | 🔴 待启动 | 未测试 | |

### 2.3 迭代与里程碑接口

| 任务 ID | 类/接口路径 | 方法/Endpoint | 详细类签名、契约与逻辑约束 | 优先级 | 状态 | 测试结果 | 备注与关联提交 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-PROJ-API-221** | `IterationController` | `POST /project/{id}/iteration` | **创建迭代**：<br>1. 入参：`CreateIterationRequest(String name, String goal, Date startDate, Date endDate)`。<br>2. 逻辑：检验开始时间早于结束时间。状态默认为 `draft`。 | P1 | 🔴 待启动 | 未测试 | |
| **TSK-PROJ-API-222** | `IterationController` | `GET /project/{id}/iteration/page` | **分页查询项目迭代**：<br>1. 支持按 `status` (draft/active/closed) 过滤，返回迭代列表及完成比例。 | P1 | 🔴 待启动 | 未测试 | |
| **TSK-PROJ-API-223** | `IterationController` | `PUT /project/iteration/{sprintId}/status` | **激活或关闭迭代**：<br>1. 状态跳转：`draft` -> `active` (强校验：**当前项目只允许存在一个 `active` 状态的迭代**，若有，强制抛 400)，`active` -> `closed`。 | P1 | 🔴 待启动 | 未测试 | |
| **TSK-PROJ-API-231** | `MilestoneController` | `POST /project/{id}/milestone` | **创建里程碑**：<br>1. 入参：`CreateMilestoneRequest(String name, Date targetDate)`。 | P2 | 🔴 待启动 | 未测试 | |

---

## 🎨 3. 前端 PC 端组件微观任务 (Frontend-PC Micro-Tasks)

| 任务 ID | 页面/组件路径 | 路由路径 | 详细组件结构、UI 交互与 API 绑定规则 | 优先级 | 状态 | 测试结果 | 备注与关联提交 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-PROJ-FE-301** | `@/views/project/list/index.vue` | `/project/list` | **项目列表主页**：<br>1. 交互：顶部为“新建项目”按钮（触发 `el-dialog` 弹窗调用 `TSK-PROJ-API-201`），中部为卡片网格列表，展示项目名、所有者、当前状态、快速“归档”按钮（调用 `TSK-PROJ-API-206`）。<br>2. 绑定：调用 `getProjectPage` 接口，支持分页组件无刷新查询。 | P0 | 🔴 待启动 | 未测试 | |
| **TSK-PROJ-FE-302** | `@/views/project/member/index.vue` | `/project/:id/member` | **项目成员管理页**：<br>1. 交互：展示当前项目下成员 Table (头像、名字、角色下拉框)。下拉框触发 `change` 动作动态调用 `TSK-PROJ-API-214` 修改角色；添加按钮弹出对话框，搜索全局用户并调用 `TSK-PROJ-API-212`。<br>2. 绑定：`getProjectMembers(projectId)` 接口。 | P1 | 🔴 待启动 | 未测试 | |
| **TSK-PROJ-FE-303** | `@/views/project/iteration/index.vue` | `/project/:id/iteration` | **迭代维护看板**：<br>1. 交互：上方显示进行中的迭代卡片（含激活/关闭按钮、时间日期选择器），下方以时间线格式渲染 `closed` 和 `draft` 迭代状态。<br>2. 绑定：点击“激活”按钮，发送 PUT 修改请求，捕获后端 400 重复激活异常，并给出 Toast 友好提示。 | P1 | 🔴 待启动 | 未测试 | |
| **TSK-PROJ-FE-304** | `@/views/project/dashboard/index.vue` | `/project/:id/dashboard` | **项目燃尽图仪表盘**：<br>1. 组件：引入 ECharts。根据后台工时历史接口，在 `onMounted` 钩子中绘制标准期望线（直线）与实际剩余工时曲线。数据实时联动。 | P1 | 🔴 待启动 | 未测试 | |

