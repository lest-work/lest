# CHANGELOG — LEST Platform

> Releases follow **Semantic Versioning (SemVer)** with a weekly cadence.

> Version format: `v{MAJOR}.{MINOR}.{PATCH}`
> - **MAJOR** — Incompatible architectural breaking changes
> - **MINOR** — Weekly feature iterations
> - **PATCH** — Bug fix patches within the same week

> Release Cadence: Minor version every Thursday

---

## [Unreleased]

### Infrastructure

- **New domain planning** — Registered `lest.work` (product platform) and `lest.top` (marketing site), establishing subdomain architecture. See [docs/guide/DOMAIN_PLAN.md](./docs/guide/DOMAIN_PLAN.md)
- **New `docs/guide/DOMAIN_PLAN.md`** — Domain planning document with production architecture, DNS config, and Nginx examples
- **API domain update** — Staging/production API URL unified to `https://api.lest.work`
- **CORS whitelist update** — Gateway allows cross-origin requests from `app.lest.work` and `lest.top`

---

## [v0.2.1] — 2026-05-31 (W22)

### Bug Fixes

- **Critical runtime crash fixes**:
  - `ProjectMemberMapper`: Change soft-delete (`SET deleted=1`) to hard `DELETE` — table has no `deleted` column
  - `TaskCommentMapper`: Same fix for `task_comment` table
- **Backend logic fixes**:
  - `IterationMapper`: Add `deleted=0` filter to all SELECT queries, change `deleteById` to soft delete, add `deleted` column to resultMap/SELECT
  - `IterationMapper.countDateConflicts`: Exclude deleted iterations from date conflict checks
  - `IterationMapper.countByProjectId`: Only count non-deleted iterations
  - `TaskMapper.selectByParentId`: Add `deleted=0` filter
  - `ReleasePlanMapper`: Remove duplicate `createdBy`/`updatedBy` from resultMap (conflicts with `BaseEntity`), add `deleted` to resultMap/SELECT
  - `ReleasePlan.java`: Remove duplicate `createdBy`/`updatedBy` fields (type conflict: `BaseEntity.String` vs `Long`)
- **Frontend/backend API wiring fixes**:
  - `updateIteration`: Fix `data.id` → `data.iterationId` (was undefined)
  - `listWorklogs`: Fix `/{id}/worklog` → `/{id}/worklog/list`
  - `addWorklog`: Fix `/worklog` → `/{id}/worklog` (taskId in path)
  - `listComments`: Fix `/{id}/comment` → `/{id}/comment/list`
  - `addComment`/`removeComment`: Fix URL paths, add taskId parameter
  - `listLabels`/`addLabel`: Fix `/label/*` → `/project/{id}/label/*`
- **New backend endpoints**:
  - `GET /{id}/comment/list` — Query task comments
  - `POST /{id}/comment` — Add task comment
  - `DELETE /{id}/comment/{commentId}` — Delete task comment
- **Code style**:
  - `IterationServiceImpl`: Add `STATUS_PLANNING`/`IN_PROGRESS`/`COMPLETED` status constants
  - `ProjectServiceImpl`: Add `STATUS_ACTIVE`/`ARCHIVED` status constants
- **Database migration**: `migration_002_iteration_soft_delete.sql` — Add `deleted` column to `iteration` table

---

## [v0.2.0-alpha.1] — 2026-05-31 (W22)

### Sprint Theme
**Project & Task Frontend Pages (Milestone 2)**

### New Features

#### Database
- `project`/`project_member`/`iteration`/`milestone`/`milestone_iteration` DDL added to `01_lest_platform_init.sql`
- `task`/`label`/`task_label`/`task_watcher`/`task_worklog`/`task_comment`/`task_commit`/`task_dependency` DDL added to unified init SQL
- Added project & task menus with button permissions to `sys_menu`

#### Frontend API
- Extended `api/project/index.ts`: Iteration CRUD, Milestone CRUD, Member add/remove
- Extended `api/project/model/index.ts`: `Iteration`, `Milestone`, `IterationParam` types
- Extended `api/task/index.ts`: Worklog, Comment, Label, Subtask, Gantt, Kanban (typed)
- Extended `api/task/model/index.ts`: `TaskWorklog`, `TaskComment`, `Label`, `BoardColumn` types

#### Frontend Pages
- `views/project/index.vue` — Project list with card grid, CRUD, archive
- `views/project/detail/index.vue` — Project detail with 4-tab layout
- `views/task/index.vue` — Task list with filters & detail drawer
- `views/task/board/index.vue` — Kanban board with project/iteration filter
- `views/release/index.vue` — Release list with card grid
- `views/release/detail/index.vue` — Release detail (artifacts & linked issues)
- `views/task/gantt/index.vue` — Gantt chart view with ECharts, supports new task creation

#### Release Management (`lest-release`)
- Release plan CRUD (version, status, target date, Git info)
- Release artifact management (upload/download/metadata)
- Release-issue linking (by category)
- `api/release/index.ts` + `api/release/model/index.ts` frontend API

#### Backend
- Fix `lest-monitor` route port (8081→9100)
- Disable routes for 7 unimplemented modules (meeting/notification/ai/performance/open/plugin/wakapi)
- Unify all modules with `NACOS_ENABLED=false`
- Fix `ReleasePlanServiceImpl` status names to return labels

### Bug Fixes
- Fix template syntax error in `task/gantt/index.vue`

### Known Pending Items
- Project burndown chart (ECharts)
- Task worklog & comment detail panel
- Kanban drag-and-drop sorting

---


## [v0.2.0-alpha.1] — 2026-05-31 (W22)

### Sprint Theme
**Project & Task Frontend Pages (Milestone 2)**

### New Features

#### Database
- `project`/`project_member`/`iteration`/`milestone`/`milestone_iteration` DDL added to `01_lest_platform_init.sql`
- `task`/`label`/`task_label`/`task_watcher`/`task_worklog`/`task_comment`/`task_commit`/`task_dependency` DDL added to unified init SQL
- Added project & task menus with button permissions to `sys_menu`

#### Frontend API
- Extended `api/project/index.ts`: Iteration CRUD, Milestone CRUD, Member add/remove
- Extended `api/project/model/index.ts`: `Iteration`, `Milestone`, `IterationParam` types
- Extended `api/task/index.ts`: Worklog, Comment, Label, Subtask, Gantt, Kanban (typed)
- Extended `api/task/model/index.ts`: `TaskWorklog`, `TaskComment`, `Label`, `BoardColumn` types

#### Frontend Pages
- `views/project/index.vue` — Project list with card grid, CRUD, archive
- `views/project/detail/index.vue` — Project detail with 4-tab layout
- `views/task/index.vue` — Task list with filters & detail drawer
- `views/task/board/index.vue` — Kanban board with project/iteration filter
- `views/release/index.vue` — Release list with card grid
- `views/release/detail/index.vue` — Release detail (artifacts & linked issues)
- `views/task/gantt/index.vue` — Gantt chart view with ECharts, supports new task creation

#### Release Management (`lest-release`)
- Release plan CRUD (version, status, target date, Git info)
- Release artifact management (upload/download/metadata)
- Release-issue linking (by category)
- `api/release/index.ts` + `api/release/model/index.ts` frontend API

#### Backend
- Fix `lest-monitor` route port (8081→9100)
- Disable routes for 7 unimplemented modules (meeting/notification/ai/performance/open/plugin/wakapi)
- Unify all modules with `NACOS_ENABLED=false`
- Fix `ReleasePlanServiceImpl` status names to return labels

### Bug Fixes
- Fix template syntax error in `task/gantt/index.vue`

### Known Pending Items
- Project burndown chart (ECharts)
- Task worklog & comment detail panel
- Kanban drag-and-drop sorting

---

> 📌 See milestone roadmap: [docs/MILESTONES.md](./docs/MILESTONES.md)
