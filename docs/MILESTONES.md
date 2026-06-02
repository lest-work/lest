> ⚠️ **DEPRECATED**: This file uses the outdated M1-M10 milestone structure. It has been replaced by the new V1.0-V5.0 version planning system.
> 
> **Please use the latest version**: [MILESTONES.zh-CN.md](./MILESTONES.zh-CN.md) and detailed small version plans in [MILESTONES/](./MILESTONES/) directory.

---
# LEST Platform — Milestone Roadmap (DEPRECATED)

> **Product Vision**: LEST is an AI-Native agile management platform for Chinese and global enterprises — matching Jira's flexibility, surpassing Jira's completeness.
>
> **Open Source Strategy**: V1.0-V4.0 fully open source (MIT License); V5.0 offers open source basic + commercial enterprise editions.
>
> **License Legend**: `✅ Open Source` = MIT License, free forever | `🔒 Commercial` = per-seat/annual subscription

> Status Legend:
> `✅ Done` | `🟡 In Progress` | `🔴 Not Started` | `⏸️ On Hold`

---

## 🚀 V1.0 — Open Source Foundation (August 2026)

**Goal**: Complete core features, open source and free (MIT), production-ready

**Version**: `v1.0.0` | **Planned Release**: 2026-08-28 (W35) | **Status**: 🔴 In Progress

> **Core Positioning**: Parity with Jira basic capabilities — project management, tasks, kanban, auth, notifications, meetings, releases, CI/CD, time tracking, performance, AI assistance. All open source, permanently free.

### M1 — Foundation Framework
**Version**: `v0.1.0` | **Completed**: 2026-05-29 | **Status**: ✅ Done

||| Deliverable | Status |
||| :--- | :--- |
||| Spring Boot 4.x + Spring Cloud microservice architecture | ✅ |
||| Spring Cloud Gateway unified routing | ✅ |
||| lest-auth: Login/logout/captcha/refresh token | ✅ |
||| lest-system: User/Role/Menu/Dept/Post/Dict/Config/Log/Notice/Job CRUD | ✅ |
||| lest-project: Project/Member/Iteration/Milestone CRUD | ✅ |
||| lest-task: Task/Kanban/Gantt/Worklog/Webhook | ✅ |
||| Docker Compose local dev environment (14 containers) | ✅ |
||| Database init SQL (system tables + seed data) | ✅ |
||| Frontend Vue 3 + TypeScript scaffold + dashboard | ✅ |

### M2 — Project & Task Frontend
**Version**: `v0.2.0` | **Completed**: 2026-05-31 | **Status**: ✅ Done

||| Deliverable | Status |
||| :--- | :--- |
||| Project list page (card grid) + detail page | ✅ |
||| Task list page (multi-filter) + detail drawer | ✅ |
||| Task kanban board (basic) | ✅ |
||| Gantt chart view (ECharts) | ✅ |
||| Release management frontend pages | ✅ |

### M3 — Notification Framework + Plugin System Framework + Open Platform Framework (v1.0)
**Version**: `v1.0` | **Planned Release**: 2026-06-20 (W25) | **Status**: 🔴 Not Started

> **Detailed tasks**: [MILESTONES/V1.0-小版本规划.md](./MILESTONES/V1.0-小版本规划.md#v10--notification-framework--plugin-system-framework--open-platform-framework-2026-06-20)

|||| Deliverable | Status |
|||| :--- | :--- |
|||| lest-notification: Kafka event + WebSocket push + in-app notifications | 🔴 |
|||| lest-plugin: Plugin lifecycle management + Extension Point definition + Backend SDK | 🔴 |
|||| lest-open: Open platform framework (API Key + Webhook + Rate limiting) | 🔴 |

### M4 — UI Plugin Runtime + System Management Simplification (v1.1)
**Version**: `v1.1` | **Planned Release**: 2026-07-04 (W27) | **Status**: 🔴 Not Started

> **Detailed tasks**: [MILESTONES/V1.0-小版本规划.md](./MILESTONES/V1.0-小版本规划.md#v11--ui-plugin-runtime--system-management-simplification-2026-07-04)

|||| Deliverable | Status |
|||| :--- | :--- |
|||| UI Plugin Runtime (Extension Point + ComponentRegistry + useExtensionResolver) | 🔴 |
|||| Plugin management page (install/enable/disable/configure) | 🔴 |
|||| System management simplification (keep mail/security/audit, remove OA features) | 🔴 |

### M5 — Agile Meetings + Release Management Frontend (v1.2)
**Version**: `v1.2` | **Planned Release**: 2026-07-18 (W29) | **Status**: 🔴 Not Started

> **Detailed tasks**: [MILESTONES/V1.0-小版本规划.md](./MILESTONES/V1.0-小版本规划.md#v12--agile-meetings-lest-meeting--release-management-frontend-2026-07-18)

|||| Deliverable | Status |
|||| :--- | :--- |
|||| lest-meeting: Meeting module (daily standup/sprint planning/retrospective/review + calendar + minutes) | 🔴 |
|||| lest-release: Backend integrated with frontend (release plan + Gantt chart + artifact management) | 🔴 |

### M6 — Worklog Management + WakaTime (v1.3)
**Version**: `v1.3` | **Planned Release**: 2026-08-01 (W31) | **Status**: 🔴 Not Started

> **Detailed tasks**: [MILESTONES/V1.0-小版本规划.md](./MILESTONES/V1.0-小版本规划.md#v13--worklog-lest-worklog--wakatime-lest-wakapi-2026-08-01)

|||| Deliverable | Status |
|||| :--- | :--- |
|||| lest-worklog: Worklog plugin (time tracking + statistics dashboard, integrated with UI pluginization) | 🔴 |
|||| lest-wakapi: WakaTime plugin (coding time tracking + heatmap + task linking) | 🔴 |

### M7 — CI/CD Integration + Team Performance (v1.4)
**Version**: `v1.4` | **Planned Release**: 2026-08-15 (W33) | **Status**: 🔴 Not Started

> **Detailed tasks**: [MILESTONES/V1.0-小版本规划.md](./MILESTONES/V1.0-小版本规划.md#v14--cicd-integration-lest-cicd--performance-module-lest-performance-2026-08-15)

|||| Deliverable | Status |
|||| :--- | :--- |
|||| lest-cicd: CI/CD integration plugin (GitLab/GitHub Webhook + build visualization) | 🔴 |
|||| lest-performance: Team performance plugin (worklog/code/meetings/tasks aggregation dashboard) | 🔴 |

### M8 — AI Service + IM Integration (v1.5)
**Version**: `v1.5` | **Planned Release**: 2026-08-28 (W35) | **Status**: 🔴 Not Started

> **Detailed tasks**: [MILESTONES/V1.0-小版本规划.md](./MILESTONES/V1.0-小版本规划.md#v15--ai-service-lest-ai--im-integration-dingtalk-wecom-feishu-2026-08-28)

|||| Deliverable | Status |
|||| :--- | :--- |
|||| lest-ai: AI gateway + multi-model routing + code review + task assistant + meeting summarization | 🔴 |
|||| IM integration plugins (DingTalk/WeCom/Feishu webhook message push) | 🔴 |

### M9 / M10 — V1.0 General Release
**Version**: `v1.0.0` | **Planned Release**: 2026-08-28 (W35) | **Status**: 🔴 Not Started

|||| Deliverable | Status |
|||| :--- | :--- |
|||| All v1.0 ~ v1.5 features stably integrated | 🔴 |
|||| Complete API documentation (OpenAPI 3.0) | 🔴 |
|||| Plugin SDK documentation (full Extension Point inventory) | 🔴 |
|||| Production Docker Compose + K8s Helm Chart | 🔴 |

> V1.0 open source version (MIT License) includes Core infrastructure + major plugins, permanently free.

---

## 🎯 V2.0 — Highly Customizable Edition (End of 2026)

**Goal**: Complete Jira-compatible scheme architecture — every team can customize their workflow

**Version**: `v2.0.0` | **Planned Release**: 2026-12-26 | **Status**: 🔴 Not Started

> **Core Positioning**: Adopt Jira Data Center's Scheme architecture to achieve EAV custom fields, Screen/Scheme multi-layer mapping, and workflow engine.

> **Detailed sub-version plan**: [MILESTONES/V2.0-小版本规划.md](./MILESTONES/V2.0-小版本规划.md)

### V2.0 Core Deliverables

||| Deliverable | Description | Status |
||| :--- | :--- | :--- |
||| EAV Custom Field System | 25 field types, EAV storage | 🔴 |
||| Screen + Screen Scheme | Form configuration: which fields on which tab, required/hidden | 🔴 |
||| Issue Type Scheme | Each type independently configured with Screen/Field/Workflow | 🔴 |
||| Workflow Engine | Condition + Validator + Post-function | 🔴 |
||| Project Templates | Create from template, fully replicate Scheme configuration | 🔴 |
||| Kanban Enhancement | Custom columns, WIP limits, swimlanes | 🔴 |

> Detailed design: [1-prd/core/V2.0/任务管理.md](./1-prd/core/V2.0/任务管理.md)

---

## 🏢 V3.0 — Enterprise Collaboration Enhancement (Mid 2027)

**Goal**: **100% Jira feature parity completion** + enterprise collaboration enhancements (IM/Approval/Portfolio/OKR)

**Version**: `v3.0.0` | **Planned Release**: 2027-04-03 | **Status**: 🔴 Not Started

> **Detailed sub-version plan**: [MILESTONES/V3.0-小版本规划.md](./MILESTONES/V3.0-小版本规划.md)

> **Core Positioning**: On one hand, complete all Jira Software/Data Center core capabilities and pass 100% verification; on the other hand, provide enterprise collaboration enhancements for Chinese companies (plugin-ready).

### V3.0 Core Deliverables

||| Deliverable | Description | Open Source |
||| :--- | :--- | :--- |
||| **Jira 100% Completion (Core)** | Use Jira feature checklist as acceptance criteria (all Issue/Board/Automation capabilities) | ✅ |
||| Issue Link + Attachment | Full task linking/attachment chain (UI/API/audit) | ✅ |
||| Time Tracking + Worklog | Estimate/remaining/logged time + worklog history | ✅ |
||| Automation Execution Engine + Template Library | Rule evaluation/action execution/execution log + rule template library | ✅ |
||| Advanced Kanban & Reports | Swimlane/WIP/Burndown/CFD/Velocity/Control Chart/Capacity/Epic progress | ✅ |
||| Batch Operations + Shortcuts + Vote/Share | List batch edit/move/delete/transition + shortcuts + vote/share | ✅ |
||| WeCom Deep Integration | Message push, approval flow, org structure sync | ✅ |
||| DingTalk Deep Integration | Message push, approval flow, org structure sync | ✅ |
||| Feishu Deep Integration | Message push, approval flow, org structure sync | ✅ |
||| Approval Flow Engine | Custom approval nodes, approvers, routing conditions | 🔒 |
||| Portfolio Multi-Project Governance | Cross-project progress, risk, resource overview | 🔒 |
||| OKR Goal Management | Alignment, KR tracking, scoring | 🔒 |
||| Domestic Database Optimization | DM/Kingbase/OceanBase adaptation | 🔒 |
||| Domestic OS Adaptation | Kylin/Linux/Uni UOS validation | 🔒 |

---

## 🤖 V4.0 — AI-Native Enhancement (2027-2028)

**Goal**: AI deeply embedded in every business process — becoming a true AI-Native platform

**Version**: `v4.0.0` | **Planned Release**: 2027-12-10 | **Status**: 🔴 Not Started

> **Core Positioning**: AI is no longer a supplementary feature, but the core engine embedded in every business process.

> **Detailed sub-version plan**: [MILESTONES/V4.0-小版本规划.md](./MILESTONES/V4.0-小版本规划.md)

### V4.0 Core Deliverables

||| Deliverable | Description | Open Source |
||| :--- | :--- | :--- |
||| AI Automatic Task Breakdown | Epic → Story → Task automatic decomposition | ✅ |
||| AI Burndown Chart Prediction | Predict completion time based on historical data | ✅ |
||| AI Risk Identification | Multi-dimensional risk early warning and smart suggestions | ✅ |
||| AI Intelligent Scheduling | Auto-scheduling based on team capability and dependencies | 🔒 |
||| AI Code Quality Analysis | Deep code smell detection and refactoring suggestions | ✅ |
||| AI Natural Language Query | Search tasks and projects using natural language | ✅ |
||| AI Code Generation Assistance | Generate code scaffolding from task descriptions | 🔒 |
||| AI Document Auto-Generation | Auto-generate weekly reports, review reports | 🔒 |

---

## 🏗️ V5.0 — Enterprise Flagship Edition (2028)

**Goal**: Ultra-large-scale enterprise deployment, meeting financial/government high compliance requirements

**Version**: `v5.0.0` | **Planned Release**: 2028-07-04 | **Status**: 🔴 Not Started

> **Core Positioning**: For ultra-large enterprises (financial, government, education, etc.) — high availability, high security, high compliance.

> **Detailed sub-version plan**: [MILESTONES/V5.0-小版本规划.md](./MILESTONES/V5.0-小版本规划.md)

### V5.0 Core Deliverables

||| Deliverable | Description | Open Source |
||| :--- | :--- | :--- |
||| Multi-Tenant Isolation | Fully isolated data, independent deployment support | 🔒 |
||| LDAP/OIDC/SSO | Enterprise unified identity authentication | 🔒 |
||| Audit Log Compliance | Meet Level Protection/ISO27001 requirements | 🔒 |
||| Fine-Grained Permissions | Row-level permissions, data masking | 🔒 |
||| Report Center | Enterprise BI reporting, advanced visualization | 🔒 |
||| SLA Guarantee | 99.9% availability, official technical support | 🔒 |

---

## 5-Version Overview

```
|Release  Aug 2026     End 2026         Mid 2027          2028.3            End 2028
|         │               │                 │                  │                 │
|      V1.0          V2.0              V3.0               V4.0              V5.0
|    ┌────────┐    ┌────────┐      ┌────────┐       ┌────────┐       ┌────────┐
|    │Open    │    │Highly  │      │Enterprise│     │AI-Native│      │Enterprise│
|    │Foundation│  │Customiz│      │Collab   │     │        │       │Flagship │
|    │        │    │        │      │        │       │        │       │        │
|    │Project │    │EAV     │      │IM Deep │       │AI Auto │       │Multi-  │
|    │Task    │    │Custom  │      │Integration│   │Task    │       │Tenant  │
|    │Kanban  │    │Fields  │      │Approval │      │Break-  │       │Isolati │
|    │Notify  │    │Screen/ │      │Portfolio│      │down    │       │on      │
|    │Meeting │    │Scheme  │      │OKR     │       │AI      │       │LDAP/   │
|    │CI/CD   │    │Workflow│      │Domesti │       │Intell  │       │OIDC    │
|    │Perform │    │Engine  │      │c Adapt │       │Schedul │       │Audit   │
|    │AI Base │    │Project │      │        │       │ing     │       │SLA     │
|    │        │    │Template│      │        │       │        │       │        │
|    └────────┘    └────────┘      └────────┘       └────────┘       └────────┘
|       MIT            MIT              MIT               MIT              🔒Comm.
|     Free&Forever   Free&Forever    Base Open         Base Open        Enterprise
|                                      Advanced          Advanced         Paid
|                                                   Commercial        Commercial
```

---

> 📌 See full version history: [CHANGELOG.md](../CHANGELOG.md)
> 📌 See development tasks: [2-tasks/README.md](./2-tasks/README.md)
> 📌 See product requirements: [1-prd/README.md](./1-prd/README.md)

---

## Sub-Version Release Plan Index

Each major version follows a **1 week development + 0.5 week testing = every 1.5 weeks release** cadence for independent weekly delivery.

|| Major Version | Sub-Version File | Description |
||--------|-----------|------|
|| **V1.0** | [MILESTONES/V1.0-小版本规划.md](./MILESTONES/V1.0-小版本规划.md) | v1.0 ~ v1.5 (6 sub-versions, 2026-06 ~ 2026-08) |
|| **V2.0** | [MILESTONES/V2.0-小版本规划.md](./MILESTONES/V2.0-小版本规划.md) | v2.0 ~ v2.5 (6 sub-versions, 2026-09 ~ 2026-11) |
|| **V3.0** | [MILESTONES/V3.0-小版本规划.md](./MILESTONES/V3.0-小版本规划.md) | v3.0 ~ v3.7 (8 sub-versions, 2026-12 ~ 2027-04) |
|| **V4.0** | [MILESTONES/V4.0-小版本规划.md](./MILESTONES/V4.0-小版本规划.md) | v4.0 ~ v4.6 (7 sub-versions, 2027-09 ~ 2027-12) |
|| **V5.0** | [MILESTONES/V5.0-小版本规划.md](./MILESTONES/V5.0-小版本规划.md) | v5.0 ~ v5.7 (8 sub-versions, 2028-03 ~ 2028-07) |

---

## All Sub-Version Timeline

```
2026
Jun   Jul   Jul   Jul   Aug   Aug
 8    20     4    18     1    15    28
  │     │     │     │     │     │     │
v1.0  v1.1  v1.2  v1.3  v1.4  v1.5
Notify+ UI plugin+ Meeting+ Worklog+ CI/CD+ AI+
Plugin+ Sys simplif+ Release  WakaTime  Perf   IM integ
Open platform
─────────────────────────────────────────── V1.0 (MIT Open Source)

Oct   Oct   Oct   Nov   Nov
 7    19     3    17    28
  │     │     │     │     │
v2.0  v2.1  v2.2  v2.3  v2.4  v2.5
EAV   Issue Screen Field  Workflow  Template+  GA
Field  Type  /Scheme Config Engine  Kanban enh  release
─────────────────────────────────────────── V2.0

2027
Jan   Jan  Feb  Feb  Mar  Mar  Apr
 9    23     6    20     6    20     3
  │     │     │     │     │     │     │
v3.0  v3.1  v3.2  v3.3  v3.4  v3.5  v3.6  v3.7
Issue   Time    Autom  Adv.Kanban BatchOp  IM Deep  OKR/PF  GA
Link+  Track+  ation+ +CFD   +Vote   Integr  +Domest  release
Attach   Recycle  Exec engine                   c Adapt
────────────────────────────────────────────────────── V3.0

Sep   Oct   Oct   Oct   Nov   Nov   Dec
 6    1    15    29    12    26    10
  │     │     │     │     │     │     │
v4.0 v4.0 v4.0 v4.0 v4.0 v4.0 v4.0
.0-α   .1    .2    .3    .4    .5    .6
AI    Burndown  Intelli  Code    NL query   GA
EngineV2 Predict+ g       quality        +doc  release
Task     +Risk   Schedul+  analysis
breakdown Identif ing(comm) +MR review
────────────────────────────────────────────────────── V4.0

2028
Mar   Apr   Apr   May   May   Jun   Jun   Jul
17    11    25     9    23     6    20     4
  │     │     │     │     │     │     │     │
v5.0 v5.0 v5.0 v5.0 v5.0 v5.0 v5.0 v5.0
.0-α   .1    .2    .3    .4    .5    .6    .7
Basic   Audit  Adv.    Report  HA       SLA    Data    GA
Multi-  log+   Multi-  Center  arch+    +monit +i18n   release
tenant  Fine-  tenant+ (comm)  perform
+SSO    grain        tenant
         perms   console         optim
────────────────────────────────────────────────────── V5.0
```
