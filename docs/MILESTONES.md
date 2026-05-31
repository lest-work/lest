# LEST Platform — 里程碑规划 / Milestone Roadmap

> 版本迭代按周推进，每个里程碑对应一个主功能模块的完整交付。
> Iterations advance weekly; each milestone corresponds to the full delivery of a major functional module.
>
> 状态图例 / Status Legend:
> `✅ 已完成 Done` | `🟡 进行中 In Progress` | `🔴 未开始 Not Started` | `⏸️ 暂缓 On Hold`

---

## 🏁 Milestone 1 — 基础框架 / Foundation

**目标 / Goal**: 完成后端微服务框架、认证、系统管理、Docker 环境搭建，前端脚手架可运行 / Complete backend microservice framework, auth, system mgmt, Docker setup, frontend scaffold running

**版本 / Version**: `v0.1.0` | **完成日期 / Completed**: 2026-05-29 (W22) | **状态**: ✅ 已完成

| 交付项 / Deliverable | 状态 |
| :--- | :--- |
| Spring Boot 4.x + Spring Cloud 微服务架构搭建 / Spring Boot 4.x + Spring Cloud microservice architecture | ✅ |
| Spring Cloud Gateway 统一路由（StripPrefix=1） / Spring Cloud Gateway unified routing (StripPrefix=1)| ✅ |
| lest-auth：登录/登出/验证码/刷新 Token / Login/logout/captcha/refresh token | ✅ |
| lest-system：用户/角色/菜单/部门/岗位/字典/参数/日志/公告/定时任务 / User/Role/Menu/Dept/Post/Dict/Config/Log/Notice/Job CRUD | ✅ |
| lest-project：项目/成员/迭代/里程碑 CRUD（后端） / Project/Member/Iteration/Milestone CRUD (backend)| ✅ |
| lest-task：任务/看板/甘特/工时/Webhook（后端） / Task/Kanban/Gantt/Worklog/Webhook (backend)| ✅ |
| Docker Compose 本地开发环境（14 容器） / Docker Compose local dev environment (14 containers)| ✅ |
| 数据库初始化 SQL（系统表 + 初始数据） / Database init SQL (system tables + seed data)| ✅ |
| 前端 Vue 3 + TypeScript 脚手架 + 仪表盘 / Frontend Vue 3 + TypeScript scaffold + dashboard | ✅ |
| 前端系统管理全套页面 / Full frontend system admin pages | ✅ |
| project/task DDL 加入 init.sql / Project/task DDL added to init.sql | ✅ |

---

## 🏁 Milestone 2 — 项目与任务前端 / Project & Task UI

**目标 / Goal**: 项目管理和任务管理的完整前端页面，实现完整的增删改查和可视化看板 / Complete project & task management frontend with full CRUD and visual kanban

**版本 / Version**: `v0.2.0-alpha.1` | **完成日期 / Completed**: 2026-05-31 (W22) | **状态**: ✅ 已完成

| 交付项 / Deliverable | 状态 |
| :--- | :--- |
| project/task 建表 SQL 加入 init.sql / Project/task DDL added to init.sql | ✅ |
| 项目列表页（卡片视图） / Project list page (card grid)| ✅ |
| 项目详情页（概况/成员/迭代/里程碑 Tab） / Project detail page (Overview/Members/Iteration/Milestone tabs)| ✅ |
| 任务列表页（多条件筛选） / Task list page (multi-filter)| ✅ |
| 任务看板视图（基础版） / Task kanban board (basic)| ✅ |
| 任务详情抽屉（子任务/工时/关联 commit） / Task detail drawer (subtask/worklog/linked commits)| ✅ |
| 甘特图视图（ECharts 横道图） / Gantt chart view (ECharts)| ✅ |
| 发布管理前端页面（列表/详情/产物/关联问题） / Release management frontend (list/detail/artifacts/linked issues)| ✅ |

---

## 🏁 Milestone 3 — 通知与消息 / Notification System

**目标 / Goal**: 站内通知、消息推送、@提及通知，WebSocket 实时推送 / WebSocket real-time push / In-app notifications, push messages, @mention alerts, WebSocket real-time delivery

**版本 / Version**: `v0.3.0` | **计划发布 / Target**: 2026-06-12 (W24) | **状态**: 🔴 未开始

| 交付项 / Deliverable | 状态 |
| :--- | :--- |
| lest-notification 服务启动 / lest-notification service startup | 🔴 |
| 站内通知 CRUD / In-app notification CRUD | 🔴 |
| WebSocket 实时推送 / WebSocket real-time push | 🔴 |
| 任务被指派/评论/@提及 触发通知 / Task assigned/commented/@mentioned triggers notification | 🔴 |
| 前端通知中心（铃铛下拉 + 消息列表页） / Frontend notification center (bell dropdown + message list)| 🔴 |

---

## 🏁 Milestone 4 — 敏捷会议 / Agile Meetings

**目标 / Goal**: 每日站会、迭代计划会、回顾会记录与管理 / Daily standup, sprint planning, retrospective records & management

**版本 / Version**: `v0.4.0` | **计划发布 / Target**: 2026-06-19 (W25) | **状态**: 🔴 未开始

| 交付项 / Deliverable | 状态 |
| :--- | :--- |
| lest-meeting 服务启动 / lest-meeting service startup | 🔴 |
| 会议记录 CRUD（类型/时间/参与人/摘要） / Meeting record CRUD (type/time/participants/summary)| 🔴 |
| 行动项（Action Item）跟踪 / Action item tracking | 🔴 |
| 与任务/迭代关联 / Link to task/iteration | 🔴 |
| 前端会议日历 + 会议详情页 / Frontend meeting calendar + meeting detail page | 🔴 |

---

## 🏁 Milestone 5 — 发布管理 / Release Management

**目标 / Goal**: 发布计划、制品追踪、发布与 Issue 的关联 / Release planning, artifact tracking, release-issue linking

**版本 / Version**: `v0.2.0-alpha.1` (DDL+前端) → `v0.5.0` (完整) | **部分完成 / Partially Done**: 2026-05-31 | **状态**: 🟡 进行中（DDL + 前端页面已完成）

| 交付项 / Deliverable | 状态 |
| :--- | :--- |
| lest-release DDL + 前端页面 / lest-release DDL + frontend pages | ✅ |
| 发布计划 CRUD（版本号/状态/目标日期） / Release plan CRUD (version/status/target date)| ✅ |
| 发布制品管理（Artifact） / Release artifact management| ✅ |
| 关联 Issue/Task / Link Issue/Task | ✅ |
| 前端发布列表 + 详情页 / Frontend release list + detail page | ✅ |
| 发布甘特图视图 / Release Gantt chart view | 🔴 |
| 发布邮件/Slack 通知 / Release email/Slack notification | 🔴 |

---

## 🏁 Milestone 6 — CI 持续集成 / CI Integration

**目标 / Goal**: 对接 GitLab CI / GitHub Actions，构建状态可视化 / Integrate GitLab CI / GitHub Actions, build status visualization

**版本 / Version**: `v0.6.0` | **计划发布 / Target**: 2026-07-03 (W27) | **状态**: 🔴 未开始

| 交付项 / Deliverable | 状态 |
| :--- | :--- |
| lest-ci 服务（CI 流水线状态接收） / lest-ci service (CI pipeline status receiver)| 🔴 |
| GitLab CI Webhook 接入 / GitLab CI Webhook integration | 🔴 |
| GitHub Actions Webhook 接入 / GitHub Actions Webhook integration | 🔴 |
| 构建状态展示（成功/失败/进行中） / Build status display (success/failed/in-progress)| 🔴 |
| 与任务/发布关联 / Link to task/release | 🔴 |

---

## 🏁 Milestone 7 — WakaTime 集成 / WakaTime Integration

**目标 / Goal**: 接入 WakaTime / Wakapi，展示团队编码时长统计 / Integrate WakaTime / Wakapi, display team coding time statistics

**版本 / Version**: `v0.7.0` | **计划发布 / Target**: 2026-07-10 (W28) | **状态**: 🔴 未开始

---

## 🏁 Milestone 8 — 团队绩效 / Team Performance

**目标 / Goal**: 研发效能数据看板（任务完成率、工时、提交频率）/ R&D performance dashboard (task completion rate, work hours, commit frequency)

**版本 / Version**: `v0.8.0` | **计划发布 / Target**: 2026-07-17 (W29) | **状态**: 🔴 未开始

---

## 🏁 Milestone 9 — AI 服务 / AI Assistant

**目标 / Goal**: AI 辅助任务拆解、需求分析、代码 Review 建议 / AI-assisted task breakdown, requirement analysis, code review suggestions

**版本 / Version**: `v0.9.0` | **计划发布 / Target**: 2026-07-24 (W30) | **状态**: 🔴 未开始

---

## 🏁 Milestone 10 — v1.0 正式发布 / v1.0 General Release

**目标 / Goal**: 核心功能完整、稳定，具备生产可用性 / Complete & stable core features, production-ready

**版本 / Version**: `v1.0.0` | **计划发布 / Target**: 2026-08-07 (W32) | **状态**: 🔴 未开始

| 交付项 / Deliverable | 状态 |
| :--- | :--- |
| 全部 M1-M9 功能稳定 / All M1-M9 features stable | 🔴 |
| 性能测试与优化 / Performance testing & optimization | 🔴 |
| 完整 API 文档（Swagger/Springdoc） / Complete API docs (Swagger/Springdoc)| 🔴 |
| 开放平台 API Key 管理 / Open platform API key management | 🔴 |
| 插件扩展框架 / Plugin extension framework | 🔴 |
| 生产 Docker Compose / K8s Helm Chart / Production Docker Compose / K8s Helm Chart | 🔴 |

---

> 📌 查看完整版本记录 / See full version history: [CHANGELOG.md](../CHANGELOG.md)
> 📌 查看开发任务 / See development tasks: [TASKS/README.md](./TASKS/README.md)
