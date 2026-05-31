# CHANGELOG — LEST Platform
|
|> 版本发布遵循 **语义化版本 Semantic Versioning (SemVer)**，按周节奏迭代。
|> Releases follow **Semantic Versioning (SemVer)** with a weekly cadence.
|
|> 版本格式 / Version format: `v{MAJOR}.{MINOR}.{PATCH}`
|> - **MAJOR** — 不兼容的架构重大变更 / Incompatible architectural breaking changes
|> - **MINOR** — 每周新功能迭代 / Weekly feature iterations
|> - **PATCH** — 当周 Bug 修复补丁 / Bug fix patches within the same week
|
|> 发版周期 / Release Cadence: 每周四发布 MINOR 版本 / Minor version every Thursday
|
---

## [Unreleased]

### 🛠️ 基础设施 / Infrastructure

- **新增域名规划 / New domain planning** — 注册 `lest.work`（产品平台）和 `lest.top`（官网），建立子域名架构。详见 [docs/DOMAIN_PLAN.md](./docs/DOMAIN_PLAN.md) / Registered `lest.work` (product platform) and `lest.top` (marketing site), establishing subdomain architecture. See [docs/DOMAIN_PLAN.md](./docs/DOMAIN_PLAN.md)
- **新增 `docs/DOMAIN_PLAN.md` / New `docs/DOMAIN_PLAN.md`** — 域名规划文档，含生产环境架构、DNS 配置、Nginx 示例 / Domain planning document with production architecture, DNS config, and Nginx examples
- **更新 API 域名 / API domain update** — staging/production 环境 API 地址统一指向 `https://api.lest.work` / Staging/production API URL unified to `https://api.lest.work`
- **更新 CORS 白名单 / CORS whitelist update** — Gateway 允许 `app.lest.work` 和 `lest.top` 跨域请求 / Gateway allows cross-origin requests from `app.lest.work` and `lest.top`

---

## [v0.2.1] — 2026-05-31 (W22)

### 🐛 Bug 修复 / Bug Fixes

- **关键运行时崩溃修复 / Critical runtime crash fixes**:
  - `ProjectMemberMapper`: 将软删除 (`SET deleted=1`) 改为硬删除 (`DELETE`)，因表无 `deleted` 列 / Change soft-delete (`SET deleted=1`) to hard `DELETE` — table has no `deleted` column
  - `TaskCommentMapper`: 同上修复 / Same fix for `task_comment` table
- **后端逻辑修复 / Backend logic fixes**:
  - `IterationMapper`: 所有 SELECT 查询增加 `deleted=0` 过滤，`deleteById` 改为软删除，resultMap/SELECT 增加 `deleted` 列 / Add `deleted=0` filter to all SELECT queries, change `deleteById` to soft delete, add `deleted` column to resultMap/SELECT
  - `IterationMapper.countDateConflicts`: 排除已删除迭代的日期冲突检查 / Exclude deleted iterations from date conflict checks
  - `IterationMapper.countByProjectId`: 仅统计未删除迭代 / Only count non-deleted iterations
  - `TaskMapper.selectByParentId`: 增加 `deleted=0` 过滤 / Add `deleted=0` filter
  - `ReleasePlanMapper`: 从 resultMap 移除重复的 `createdBy`/`updatedBy`（与 `BaseEntity` 字段冲突），resultMap/SELECT 增加 `deleted` 列 / Remove duplicate `createdBy`/`updatedBy` from resultMap (conflicts with `BaseEntity`), add `deleted` to resultMap/SELECT
  - `ReleasePlan.java`: 移除重复的 `createdBy`/`updatedBy` 字段（类型冲突：`BaseEntity` 为 `String`，此处为 `Long`）/ Remove duplicate `createdBy`/`updatedBy` fields (type conflict: `BaseEntity.String` vs `Long`)
- **前后端 API 接线修复 / Frontend/backend API wiring fixes**:
  - `updateIteration`: 修复 `data.id` → `data.iterationId`（原值为 undefined）/ Fix `data.id` → `data.iterationId` (was undefined)
  - `listWorklogs`: 修复 `/{id}/worklog` → `/{id}/worklog/list`
  - `addWorklog`: 修复 `/worklog` → `/{id}/worklog`（taskId 置于路径）
  - `listComments`: 修复 `/{id}/comment` → `/{id}/comment/list`
  - `addComment`/`removeComment`: 修复 URL 路径，补充 taskId 参数 / Fix URL paths, add taskId parameter
  - `listLabels`/`addLabel`: 修复 `/label/*` → `/project/{id}/label/*`
- **新增后端接口 / New backend endpoints**:
  - `GET /{id}/comment/list` — 查询任务评论 / Query task comments
  - `POST /{id}/comment` — 添加任务评论 / Add task comment
  - `DELETE /{id}/comment/{commentId}` — 删除任务评论 / Delete task comment
- **代码规范 / Code style**:
  - `IterationServiceImpl`: 增加 `STATUS_PLANNING`/`IN_PROGRESS`/`COMPLETED` 状态常量 / Add `STATUS_PLANNING`/`IN_PROGRESS`/`COMPLETED` status constants
  - `ProjectServiceImpl`: 增加 `STATUS_ACTIVE`/`ARCHIVED` 状态常量 / Add `STATUS_ACTIVE`/`ARCHIVED` status constants
- **数据库迁移 / Database migration**: `migration_002_iteration_soft_delete.sql` — 为 `iteration` 表增加 `deleted` 列 / Add `deleted` column to `iteration` table

---

## [v0.1.0] — 2026-05-29 (W22)

### 🎯 本周主题 / Sprint Theme
**基础框架搭建 / Foundation & Infrastructure Setup**

### ✨ 新功能 / New Features

#### 后端 / Backend
- **认证服务 / Auth Service** (`lest-auth`, port: 8096)
  - 图形验证码登录 / Captcha-based login (`POST /auth/login`)
  - JWT Token 签发与刷新 / JWT token issuance and refresh
  - 登出清除 Redis Session / Logout with Redis session cleanup
  - 获取当前用户信息+菜单权限 / Get current user info with menu permissions
- **系统管理服务 / System Service** (`lest-system`, port: 8081)
  - 用户/角色/菜单/部门/岗位 CRUD / User, Role, Menu, Dept, Post management
  - 字典类型与字典数据管理 / Dictionary type and data management
  - 系统参数配置 / System parameter configuration
  - 操作日志与登录日志 / Operation log and login log
  - 在线用户管理与强退 / Online user management and force logout
  - 定时任务管理（Quartz）/ Scheduled job management (Quartz)
  - 公告管理 / Notice management
  - 仪表盘 API：最新动态 + 小组成员在线状态 / Dashboard API: activities + member online status
- **项目管理服务 / Project Service** (`lest-project`, port: 8082)
  - 项目 CRUD + 归档 / Project CRUD + archive
  - 项目成员管理 / Project member management
  - 迭代管理 / Sprint/iteration management
  - 里程碑管理 / Milestone management
- **任务管理服务 / Task Service** (`lest-task`, port: 8083)
  - 任务 CRUD + 状态流转 / Task CRUD + status lifecycle
  - 看板视图 / Kanban board view
  - 甘特图数据 / Gantt chart data
  - 子任务 / Subtask support
  - 工时记录 / Work log tracking
  - 关联代码提交 / Git commit association
  - GitLab/GitHub Webhook 接入 / GitLab/GitHub Webhook integration
  - 标签管理 / Label management
- **网关 / Gateway** (`lest-gateway`, port: 8080)
  - 统一路由 StripPrefix=1 / Unified routing with StripPrefix=1
  - 验证码过滤器（WebFlux）/ Captcha filter (WebFlux reactive)
  - Token 鉴权过滤器 / Token authentication filter
  - 接口白名单配置 / Whitelist configuration

#### 前端 / Frontend (`frontend-pc`)
- Vue 3 + TypeScript + Element Plus + Vite 完整脚手架 / Full scaffold
- 登录页（验证码）/ Login page with captcha
- 首页仪表盘：最新动态/小组成员/项目进度/我的任务 卡片 / Home dashboard: recent activities, team members, project progress, my tasks
- 系统管理全套页面：用户/角色/菜单/部门/岗位/字典/参数/公告 / Full system admin pages: user/role/menu/dept/post/dict/config/notice
- 监控中心：操作日志/登录日志/在线用户/定时任务 / Monitor center: operation logs, login logs, online users, scheduled jobs
- API 层类型化（TypeScript model/index.ts）/ Typed API layer
- 响应格式对齐 RuoYi / Response format aligned with RuoYi

#### 数据库 / Database
- 完整初始化 SQL（含初始数据）/ Complete init SQL with seed data
- 系统表：`sys_user/role/menu/dept/post/dict/config/log/notice/job` + 11张 Quartz 表 / System tables

#### 基础设施 / Infrastructure
- Docker Compose 本地开发环境（14 个容器）/ Local dev Docker Compose (14 containers)
  - MySQL 8.0, Redis 7, Nacos 2.x, Kafka, MinIO, Nginx Frontend
  - Gateway, Auth, System, Project, Task, Release, File, Job 服务 / Gateway, Auth, System, Project, Task, Release, File, Job services

### 🔧 技术栈 / Tech Stack
- Spring Boot `4.0.3` + Spring Cloud `2025.1.0` + Spring Cloud Alibaba `2025.1.0.0`
- 原生 MyBatis + PageHelper（非 MyBatis-Plus）/ Native MyBatis + PageHelper (not MyBatis-Plus)
- 无 Lombok，手写 getter/setter / No Lombok, explicit accessors
- Vue 3 + TypeScript + Element Plus + EleAdmin Pro

### ⚠️ 已知待完成 / Known Pending Items
- 项目燃尽图（ECharts）尚未开发 / Project burndown chart (ECharts) not yet built
- 任务详情页工时/评论功能待完善 / Task worklog & comment detail panel pending

---

## [v0.2.0-alpha.1] — 2026-05-31 (W22)

### 🎯 本周主题 / Sprint Theme
**项目与任务前端页面 / Project & Task Frontend Pages (Milestone 2)**

### ✨ 新功能 / New Features

#### 数据库 / Database
- `project`/`project_member`/`iteration`/`milestone`/`milestone_iteration` 建表 SQL 加入 `01_lest_platform_init.sql` / Project & iteration DDL added to unified init SQL
- `task`/`label`/`task_label`/`task_watcher`/`task_worklog`/`task_comment`/`task_commit`/`task_dependency` 建表 SQL / Task module DDL added to unified init SQL
- `sys_menu` 新增项目管理与任务管理菜单及按钮权限数据 / Added project & task menus with button permissions

#### 前端 API / Frontend API
- `api/project/index.ts` 补全：Iteration CRUD、Milestone CRUD、Member 添加/移除 / Extended: Iteration CRUD, Milestone CRUD, Member add/remove
- `api/project/model/index.ts` 补全：`Iteration`、`Milestone`、`IterationParam` 类型 / Extended models
- `api/task/index.ts` 补全：Worklog、Comment、Label、子任务、Gantt、看板（类型化）/ Extended: Worklog, Comment, Label, Subtask, Gantt, Kanban (typed)
- `api/task/model/index.ts` 补全：`TaskWorklog`、`TaskComment`、`Label`、`BoardColumn` 类型 / Extended models

#### 前端页面 / Frontend Pages
- `views/project/index.vue` — 项目列表（卡片网格、新建/编辑/归档/删除）/ Project list with card grid, CRUD, archive
- `views/project/detail/index.vue` — 项目详情（概况/成员/迭代/里程碑四 Tab）/ Project detail with 4-tab layout
- `views/task/index.vue` — 任务列表（搜索/分页/状态更改/详情抽屉）/ Task list with filters & detail drawer
- `views/task/board/index.vue` — 任务看板（三列看板、按项目/迭代过滤、快捷状态流转）/ Kanban board with project/iteration filter
- `views/release/index.vue` — 发布列表页（卡片视图）/ Release list with card grid
- `views/release/detail/index.vue` — 发布详情（产物/关联 Issue）/ Release detail (artifacts & linked issues)
- `views/task/gantt/index.vue` — 甘特图视图（ECharts，支持新建任务）/ Gantt chart view with ECharts, supports new task creation

#### 发布管理 / Release Management (`lest-release`)
- 发布计划 CRUD（版本号/状态/目标日期/Git 信息）/ Release plan CRUD
- 发布制品管理（Artifact 上传/下载/元数据）/ Release artifact management
- 发布关联 Issue/Task（支持按 category 分类）/ Release-issue linking
- `api/release/index.ts` + `api/release/model/index.ts` 前端 API / Frontend API

#### 后端 / Backend
- Gateway 修复 `lest-monitor` 路由端口（8081→9100）/ Fix `lest-monitor` route port (8081→9100)
- 禁用 7 个空模块路由（meeting/notification/ai/performance/open/plugin/wakapi）/ Disable routes for 7 unimplemented modules
- 统一所有模块 `NACOS_ENABLED=false` / Unify all modules with `NACOS_ENABLED=false`
- 修复 `ReleasePlanServiceImpl` 状态名称返回中文 / Fix `ReleasePlanServiceImpl` status names to return Chinese labels

### 🐛 Bug 修复 / Bug Fixes
- 修复 `task/gantt/index.vue` 模板语法错误 / Fix template syntax error in Gantt view

### ⚠️ 已知待完成 / Known Pending Items
- 项目燃尽图（ECharts）/ Project burndown chart (ECharts)
- 任务工时/评论详情面板 / Task worklog & comment detail panel
- 看板拖拽排序 / Kanban drag-and-drop sorting

---

> 📌 查看里程碑规划 / See milestone roadmap: [docs/MILESTONES.md](./docs/MILESTONES.md)
