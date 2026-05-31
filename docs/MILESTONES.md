# LEST Platform — 里程碑规划 / Milestone Roadmap

> 版本迭代按周推进，每个里程碑对应一个主功能模块的完整交付。
> Iterations advance weekly; each milestone corresponds to the full delivery of a major functional module.
>
> 状态图例 / Status Legend:
> `✅ 已完成 Done` | `🟡 进行中 In Progress` | `🔴 未开始 Not Started` | `⏸️ 暂缓 On Hold`

---

## 🏁 Milestone 1 — 基础框架 / Foundation

**目标 / Goal**: 完成后端微服务框架、认证、系统管理、Docker 环境搭建，前端脚手架可运行

**版本 / Version**: `v0.1.0` | **完成日期 / Completed**: 2026-05-29 (W22) | **状态**: ✅ 已完成

| 交付项 / Deliverable | 状态 |
| :--- | :--- |
| Spring Boot 4.x + Spring Cloud 微服务架构搭建 | ✅ |
| Spring Cloud Gateway 统一路由（StripPrefix=1）| ✅ |
| lest-auth：登录/登出/验证码/刷新 Token | ✅ |
| lest-system：用户/角色/菜单/部门/岗位/字典/参数/日志/公告/定时任务 | ✅ |
| lest-project：项目/成员/迭代/里程碑 CRUD（后端）| ✅ |
| lest-task：任务/看板/甘特/工时/Webhook（后端）| ✅ |
| Docker Compose 本地开发环境（14 容器）| ✅ |
| 数据库初始化 SQL（系统表 + 初始数据）| ✅ |
| 前端 Vue 3 + TypeScript 脚手架 + 仪表盘 | ✅ |
| 前端系统管理全套页面 | ✅ |
| project/task DDL 加入 init.sql | ✅ |

---

## 🏁 Milestone 2 — 项目与任务前端 / Project & Task UI

**目标 / Goal**: 项目管理和任务管理的完整前端页面，实现完整的增删改查和可视化看板

**版本 / Version**: `v0.2.0` | **完成日期 / Completed**: 2026-05-30 (W22) | **状态**: ✅ 已完成

| 交付项 / Deliverable | 状态 |
| :--- | :--- |
| project/task 建表 SQL 加入 init.sql | ✅ |
| 项目列表页（卡片视图）| ✅ |
| 项目详情页（概况/成员/迭代/里程碑 Tab）| ✅ |
| 任务列表页（多条件筛选）| ✅ |
| 任务看板视图（基础版）| ✅ |
| 任务详情抽屉（子任务/工时/关联 commit）| ✅ |
| 甘特图视图（ECharts 横道图）| ✅ |

---

## 🏁 Milestone 3 — 通知与消息 / Notification System

**目标 / Goal**: 站内通知、消息推送、@提及通知，WebSocket 实时推送

**版本 / Version**: `v0.3.0` | **计划发布 / Target**: 2026-06-12 (W24) | **状态**: 🔴 未开始

| 交付项 / Deliverable | 状态 |
| :--- | :--- |
| lest-notification 服务启动 | 🔴 |
| 站内通知 CRUD | 🔴 |
| WebSocket 实时推送 | 🔴 |
| 任务被指派/评论/@提及 触发通知 | 🔴 |
| 前端通知中心（铃铛下拉 + 消息列表页）| 🔴 |

---

## 🏁 Milestone 4 — 敏捷会议 / Agile Meetings

**目标 / Goal**: 每日站会、迭代计划会、回顾会记录与管理

**版本 / Version**: `v0.4.0` | **计划发布 / Target**: 2026-06-19 (W25) | **状态**: 🔴 未开始

| 交付项 / Deliverable | 状态 |
| :--- | :--- |
| lest-meeting 服务启动 | 🔴 |
| 会议记录 CRUD（类型/时间/参与人/摘要）| 🔴 |
| 行动项（Action Item）跟踪 | 🔴 |
| 与任务/迭代关联 | 🔴 |
| 前端会议日历 + 会议详情页 | 🔴 |

---

## 🏁 Milestone 5 — 发布管理 / Release Management

**目标 / Goal**: 发布计划、制品追踪、发布与 Issue 的关联

**版本 / Version**: `v0.5.0` | **计划发布 / Target**: 2026-06-26 (W26) | **状态**: 🔴 未开始

| 交付项 / Deliverable | 状态 |
| :--- | :--- |
| lest-release 服务完善（DDL + 前端）| 🔴 |
| 发布计划 CRUD（版本号/状态/目标日期）| 🔴 |
| 发布制品管理（Artifact）| 🔴 |
| 关联 Issue/Task | 🔴 |
| 前端发布列表 + 详情页 | 🔴 |

---

## 🏁 Milestone 6 — CI 持续集成 / CI Integration

**目标 / Goal**: 对接 GitLab CI / GitHub Actions，构建状态可视化

**版本 / Version**: `v0.6.0` | **计划发布 / Target**: 2026-07-03 (W27) | **状态**: 🔴 未开始

| 交付项 / Deliverable | 状态 |
| :--- | :--- |
| lest-ci 服务（CI 流水线状态接收）| 🔴 |
| GitLab CI Webhook 接入 | 🔴 |
| GitHub Actions Webhook 接入 | 🔴 |
| 构建状态展示（成功/失败/进行中）| 🔴 |
| 与任务/发布关联 | 🔴 |

---

## 🏁 Milestone 7 — WakaTime 集成 / WakaTime Integration

**目标 / Goal**: 接入 WakaTime / Wakapi，展示团队编码时长统计

**版本 / Version**: `v0.7.0` | **计划发布 / Target**: 2026-07-10 (W28) | **状态**: 🔴 未开始

---

## 🏁 Milestone 8 — 团队绩效 / Team Performance

**目标 / Goal**: 研发效能数据看板（任务完成率、工时、提交频率）

**版本 / Version**: `v0.8.0` | **计划发布 / Target**: 2026-07-17 (W29) | **状态**: 🔴 未开始

---

## 🏁 Milestone 9 — AI 服务 / AI Assistant

**目标 / Goal**: AI 辅助任务拆解、需求分析、代码 Review 建议

**版本 / Version**: `v0.9.0` | **计划发布 / Target**: 2026-07-24 (W30) | **状态**: 🔴 未开始

---

## 🏁 Milestone 10 — v1.0 正式发布 / v1.0 General Release

**目标 / Goal**: 核心功能完整、稳定，具备生产可用性

**版本 / Version**: `v1.0.0` | **计划发布 / Target**: 2026-08-07 (W32) | **状态**: 🔴 未开始

| 交付项 / Deliverable | 状态 |
| :--- | :--- |
| 全部 M1-M9 功能稳定 | 🔴 |
| 性能测试与优化 | 🔴 |
| 完整 API 文档（Swagger/Springdoc）| 🔴 |
| 开放平台 API Key 管理 | 🔴 |
| 插件扩展框架 | 🔴 |
| 生产 Docker Compose / K8s Helm Chart | 🔴 |

---

> 📌 查看完整版本记录 / See full version history: [CHANGELOG.md](../CHANGELOG.md)
> 📌 查看开发任务 / See development tasks: [TASKS/README.md](./TASKS/README.md)
