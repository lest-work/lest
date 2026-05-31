# LEST Platform — Milestone Roadmap

> Iterations advance weekly; each milestone corresponds to the full delivery of a major functional module.

> Status Legend:
> `✅ Done` | `🟡 In Progress` | `🔴 Not Started` | `⏸️ On Hold`

---

## 🏁 Milestone 1 — Foundation

**Goal**: Complete backend microservice framework, auth, system mgmt, Docker setup, frontend scaffold running

**Version**: `v0.1.0` | **Completed**: 2026-05-29 (W22) | **Status**: ✅ Done

| Deliverable | Status |
| :--- | :--- |
| Spring Boot 4.x + Spring Cloud microservice architecture | ✅ |
| Spring Cloud Gateway unified routing (StripPrefix=1) | ✅ |
| lest-auth: Login/logout/captcha/refresh token | ✅ |
| lest-system: User/Role/Menu/Dept/Post/Dict/Config/Log/Notice/Job CRUD | ✅ |
| lest-project: Project/Member/Iteration/Milestone CRUD (backend) | ✅ |
| lest-task: Task/Kanban/Gantt/Worklog/Webhook (backend) | ✅ |
| Docker Compose local dev environment (14 containers) | ✅ |
| Database init SQL (system tables + seed data) | ✅ |
| Frontend Vue 3 + TypeScript scaffold + dashboard | ✅ |
| Full frontend system admin pages | ✅ |
| Project/task DDL added to init.sql | ✅ |

---

## 🏁 Milestone 2 — Project & Task UI

**Goal**: Complete project & task management frontend with full CRUD and visual kanban

**Version**: `v0.2.0-alpha.1` | **Completed**: 2026-05-31 (W22) | **Status**: ✅ Done

| Deliverable | Status |
| :--- | :--- |
| Project/task DDL added to init.sql | ✅ |
| Project list page (card grid) | ✅ |
| Project detail page (Overview/Members/Iteration/Milestone tabs) | ✅ |
| Task list page (multi-filter) | ✅ |
| Task kanban board (basic) | ✅ |
| Task detail drawer (subtask/worklog/linked commits) | ✅ |
| Gantt chart view (ECharts) | ✅ |
| Release management frontend (list/detail/artifacts/linked issues) | ✅ |

---

## 🏁 Milestone 3 — Notification System

**Goal**: In-app notifications, push messages, @mention alerts, WebSocket real-time delivery

**Version**: `v0.3.0` | **Target**: 2026-06-12 (W24) | **Status**: 🔴 Not Started

| Deliverable | Status |
| :--- | :--- |
| lest-notification service startup | 🔴 |
| In-app notification CRUD | 🔴 |
| WebSocket real-time push | 🔴 |
| Task assigned/commented/@mentioned triggers notification | 🔴 |
| Frontend notification center (bell dropdown + message list) | 🔴 |

---

## 🏁 Milestone 4 — Agile Meetings

**Goal**: Daily standup, sprint planning, retrospective records & management

**Version**: `v0.4.0` | **Target**: 2026-06-19 (W25) | **Status**: 🔴 Not Started

| Deliverable | Status |
| :--- | :--- |
| lest-meeting service startup | 🔴 |
| Meeting record CRUD (type/time/participants/summary) | 🔴 |
| Action item tracking | 🔴 |
| Link to task/iteration | 🔴 |
| Frontend meeting calendar + meeting detail page | 🔴 |

---

## 🏁 Milestone 5 — Release Management

**Goal**: Release planning, artifact tracking, release-issue linking

**Version**: `v0.2.0-alpha.1` (DDL+frontend) → `v0.5.0` (full) | **Partially Done**: 2026-05-31 | **Status**: 🟡 In Progress (DDL + frontend pages completed)

| Deliverable | Status |
| :--- | :--- |
| lest-release DDL + frontend pages | ✅ |
| Release plan CRUD (version/status/target date) | ✅ |
| Release artifact management | ✅ |
| Link Issue/Task | ✅ |
| Frontend release list + detail page | ✅ |
| Release Gantt chart view | 🔴 |
| Release email/Slack notification | 🔴 |

---

## 🏁 Milestone 6 — CI Integration

**Goal**: Integrate GitLab CI / GitHub Actions, build status visualization

**Version**: `v0.6.0` | **Target**: 2026-07-03 (W27) | **Status**: 🔴 Not Started

| Deliverable | Status |
| :--- | :--- |
| lest-ci service (CI pipeline status receiver) | 🔴 |
| GitLab CI Webhook integration | 🔴 |
| GitHub Actions Webhook integration | 🔴 |
| Build status display (success/failed/in-progress) | 🔴 |
| Link to task/release | 🔴 |

---

## 🏁 Milestone 7 — WakaTime Integration

**Goal**: Integrate WakaTime / Wakapi, display team coding time statistics

**Version**: `v0.7.0` | **Target**: 2026-07-10 (W28) | **Status**: 🔴 Not Started

---

## 🏁 Milestone 8 — Team Performance

**Goal**: R&D performance dashboard (task completion rate, work hours, commit frequency)

**Version**: `v0.8.0` | **Target**: 2026-07-17 (W29) | **Status**: 🔴 Not Started

---

## 🏁 Milestone 9 — AI Assistant

**Goal**: AI-assisted task breakdown, requirement analysis, code review suggestions

**Version**: `v0.9.0` | **Target**: 2026-07-24 (W30) | **Status**: 🔴 Not Started

---

## 🏁 Milestone 10 — v1.0 General Release

**Goal**: Complete & stable core features, production-ready

**Version**: `v1.0.0` | **Target**: 2026-08-07 (W32) | **Status**: 🔴 Not Started

| Deliverable | Status |
| :--- | :--- |
| All M1-M9 features stable | 🔴 |
| Performance testing & optimization | 🔴 |
| Complete API docs (Swagger/Springdoc) | 🔴 |
| Open platform API key management | 🔴 |
| Plugin extension framework | 🔴 |
| Production Docker Compose / K8s Helm Chart | 🔴 |

---

> 📌 See full version history: [CHANGELOG.md](../CHANGELOG.md)
> 📌 See development tasks: [TASKS/README.md](./TASKS/README.md)
