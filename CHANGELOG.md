# CHANGELOG — LEST Platform

> 版本发布遵循 **语义化版本 Semantic Versioning (SemVer)**，按周节奏迭代。
> Releases follow **Semantic Versioning (SemVer)** with a weekly cadence.
>
> 版本格式 / Version format: `v{MAJOR}.{MINOR}.{PATCH}`
> - **MAJOR** — 不兼容的架构重大变更 / Incompatible architectural breaking changes
> - **MINOR** — 每周新功能迭代 / Weekly feature iterations
> - **PATCH** — 当周 Bug 修复补丁 / Bug fix patches within the same week
>
> 发版周期 / Release Cadence: 每周四发布 MINOR 版本 / Minor version every Thursday

---

## [v0.1.0] — 2026-05-29 (W22)

### 🎯 本周主题 / Sprint Theme
**基础框架搭建** — Foundation & Infrastructure Setup

### ✨ 新功能 / New Features

#### 后端 / Backend
- **认证服务 Auth Service** (`lest-auth`, port: 8096)
  - 图形验证码登录 / Captcha-based login (`POST /auth/login`)
  - JWT Token 签发与刷新 / JWT token issuance and refresh
  - 登出清除 Redis Session / Logout with Redis session cleanup
  - 获取当前用户信息+菜单权限 / Get current user info with menu permissions
- **系统管理服务 System Service** (`lest-system`, port: 8081)
  - 用户/角色/菜单/部门/岗位 CRUD / User, Role, Menu, Dept, Post management
  - 字典类型与字典数据管理 / Dictionary type and data management
  - 系统参数配置 / System parameter configuration
  - 操作日志与登录日志 / Operation log and login log
  - 在线用户管理与强退 / Online user management and force logout
  - 定时任务管理（Quartz）/ Scheduled job management (Quartz)
  - 公告管理 / Notice management
  - 仪表盘 API：最新动态 + 小组成员在线状态 / Dashboard API: activities + member online status
- **项目管理服务 Project Service** (`lest-project`, port: 8082)
  - 项目 CRUD + 归档 / Project CRUD + archive
  - 项目成员管理 / Project member management
  - 迭代管理（Iteration）/ Sprint/iteration management
  - 里程碑管理（Milestone）/ Milestone management
- **任务管理服务 Task Service** (`lest-task`, port: 8083)
  - 任务 CRUD + 状态流转 / Task CRUD + status lifecycle
  - 看板视图 / Kanban board view
  - 甘特图数据 / Gantt chart data
  - 子任务 / Subtask support
  - 工时记录 / Work log tracking
  - 关联代码提交 / Git commit association
  - GitLab/GitHub Webhook 接入 / GitLab/GitHub Webhook integration
  - 标签管理 / Label management
- **网关 Gateway** (`lest-gateway`, port: 8080)
  - 统一路由 StripPrefix=1 / Unified routing with StripPrefix=1
  - 验证码过滤器（WebFlux）/ Captcha filter (WebFlux reactive)
  - Token 鉴权过滤器 / Token authentication filter
  - 接口白名单配置 / Whitelist configuration

#### 前端 / Frontend (`frontend-pc`)
- Vue 3 + TypeScript + Element Plus + Vite 完整脚手架 / Full scaffold
- 登录页（验证码）/ Login page with captcha
- 首页仪表盘：最新动态/小组成员/项目进度/我的任务 卡片 / Home dashboard widgets
- 系统管理全套页面：用户/角色/菜单/部门/岗位/字典/参数/公告 / Full system admin pages
- 监控中心：操作日志/登录日志/在线用户/定时任务 / Monitor center pages
- API 层类型化（TypeScript model/index.ts）/ Typed API layer
- 响应格式对齐 RuoYi：`msg`/`rows`/`AjaxResult`/`TableDataInfo` / Response format aligned

#### 数据库 / Database
- 完整初始化 SQL（含初始数据）/ Complete init SQL with seed data
- 系统表：`sys_user/role/menu/dept/post/dict/config/log/notice/job` + 11张 Quartz 表

#### 基础设施 / Infrastructure
- Docker Compose 本地开发环境（14 个容器）/ Local dev Docker Compose (14 containers)
  - MySQL 8.0, Redis 7, Nacos 2.x, Kafka, MinIO, Nginx Frontend
  - Gateway, Auth, System, Project, Task, Release, File, Job 服务

### 🔧 技术栈 / Tech Stack
- Spring Boot `4.0.3` + Spring Cloud `2025.1.0` + Spring Cloud Alibaba `2025.1.0.0`
- 原生 MyBatis + PageHelper（非 MyBatis-Plus）/ Native MyBatis + PageHelper
- 无 Lombok，手写 getter/setter / No Lombok, explicit accessors
- Vue 3 + TypeScript + Element Plus + EleAdmin Pro

### ⚠️ 已知待完成 / Known Pending Items
- 项目燃尽图（ECharts）尚未开发 / Project burndown chart not yet built
- 任务详情页工时/评论功能待完善 / Task worklog & comment UI pending

---

## [v0.2.0] — 2026-05-30 (W22)

### 🎯 本周主题 / Sprint Theme
**项目与任务前端页面** — Project & Task Frontend Pages (Milestone 2)

### ✨ 新功能 / New Features

#### 数据库 / Database
- `project`/`project_member`/`iteration`/`milestone`/`milestone_iteration` 建表 SQL 加入 `01_lest_platform_init.sql`
  / Project & iteration DDL added to unified init SQL
- `task`/`label`/`task_label`/`task_watcher`/`task_worklog`/`task_comment`/`task_commit`/`task_dependency` 建表 SQL
  / Task module DDL added to unified init SQL
- `sys_menu` 新增项目管理与任务管理菜单及按钮权限数据 / Added project & task menus with button permissions

#### 前端 API / Frontend API
- `api/project/index.ts` 补全：Iteration CRUD、Milestone CRUD、Member 添加/移除 / Extended project API
- `api/project/model/index.ts` 补全：`Iteration`、`Milestone`、`IterationParam` 类型 / Extended project models
- `api/task/index.ts` 补全：Worklog、Comment、Label、子任务、Gantt、看板（类型化）/ Extended task API
- `api/task/model/index.ts` 补全：`TaskWorklog`、`TaskComment`、`Label`、`BoardColumn` 类型 / Extended task models

#### 前端页面 / Frontend Pages
- `views/project/index.vue` — 项目列表（卡片网格、新建/编辑/归档/删除）/ Project list with card grid
- `views/project/detail/index.vue` — 项目详情（概况/成员/迭代/里程碑四 Tab）/ Project detail with 4-tab layout
- `views/task/index.vue` — 任务列表（搜索/分页/状态更改/详情抽屉）/ Task list with filters & drawer
- `views/task/board/index.vue` — 任务看板（三列看板、按项目/迭代过滤、快捷状态流转）/ Kanban board

### ⚠️ 已知待完成 / Known Pending Items
- 项目燃尽图（ECharts）/ Project burndown chart
- 任务工时/评论详情面板 / Task worklog & comment panel
- 看板拖拽排序 / Kanban drag-and-drop sorting

## [Unreleased] — upcoming v0.3.0 (W23, 2026-06-05)

### 🎯 计划主题 / Planned Theme
**项目燃尽图与任务增强** — Burndown Chart & Task Enhancement

### 📋 计划内容 / Planned Items
- [ ] 项目燃尽图（ECharts）/ Project burndown chart
- [ ] 任务详情面板（工时记录/评论区）/ Task detail panel with worklog & comments
- [ ] 看板拖拽排序（HTML5 DnD / vuedraggable）/ Kanban drag-and-drop
- [ ] 发布管理前端页面 / Release management UI pages

---

> 📌 查看里程碑规划 / See milestone roadmap: [docs/MILESTONES.md](./docs/MILESTONES.md)
