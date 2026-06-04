# LEST Platform Milestones

> **Status**: This English milestone file is intentionally kept as a short redirect to avoid maintaining two divergent roadmaps.
>
> **Current source of truth**:
>
> - [ROADMAP-CORE-FIRST.zh-CN.md](./ROADMAP-CORE-FIRST.zh-CN.md)
> - [MILESTONES.zh-CN.md](./MILESTONES.zh-CN.md)
> - [MILESTONES/](./MILESTONES/) detailed version plans

## Current Core-First Roadmap

| Version | Positioning | Delivery Focus |
|---------|-------------|----------------|
| 0.1 | Runnable skeleton | Frontend migration, backend build, core services boot |
| 0.2 | Core loop | Project/task/board/detail/permission/notification flow |
| 0.3 | Release candidate | API, schema, permission, E2E, deployment scripts |
| V1.0 | Jira Core Basic | Project, issue, board, Backlog/Sprint, notification, attachment, permission, OpenAPI basics |
| V2.0 | Scheme Configurable | Issue Type, Workflow, Custom Field, Screen, Field Config, project template |
| V3.0 | Jira Core Complete | Issue Link, time tracking, automation, agile reports, bulk, JQL, import/export |
| V4.0 | Extension Ecosystem and AI | Plugin runtime, official plugin pool, AI capabilities |
| V5.0 | Enterprise Commercial | Multi-tenant, SSO/LDAP, audit compliance, fine-grained permissions, HA, SLA |

## Core-First Rules

- V1.0/V2.0/V3.0 do not deliver official plugins.
- V1.0/V2.0 keep extension contracts, events, APIs, and UI slots lightweight.
- Full plugin runtime, SDK, marketplace, hot loading, and official plugins start in V4.0.
- Enterprise commercial capabilities are planned for V5.0.
