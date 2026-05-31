# 发布管理开发任务清单

> **最后更新**：2026-05-31 | **整体状态**：后端已完成，前端待完善 | **发布版本**：v0.1.0（后端）

## 模块技术上下文

- **微服务模块**：`lest-release`（端口: 8087）
- **主目录**：`backend/lest-modules/lest-release`
- **包路径**：`com.lest.modules.release`
- **数据库**：使用 `lest_platform` 数据库（与其他模块共用），DDL 已在 `backend/docker/mysql/db/01_lest_platform_init.sql`
- **前端 PC 目录**：`frontend-pc/src/views/release/`

---

## 1. 数据库 DDL 任务

| 任务 ID | 表名 | 说明 | 优先级 | 状态 |
|:---|:---|:---|:---:|:---:|
| **TSK-REL-DDL-101** | `release_plan` | 发布计划表 | P0 | 已完成 |
| **TSK-REL-DDL-102** | `release_issue` | 发布 Issue 关联表 | P0 | 已完成 |
| **TSK-REL-DDL-103** | `release_artifact` | 发布制品表 | P1 | 已完成 |

---

## 2. 后端 API 任务

| 任务 ID | Controller | Endpoint | 说明 | 优先级 | 状态 |
|:---|:---|:---|:---|:---:|:---:|
| **TSK-REL-API-101** | `ReleasePlanController` | `POST /release/plan` | 新增发布计划 | P0 | 已完成 |
| **TSK-REL-API-102** | `ReleasePlanController` | `GET /release/plan/list` | 发布计划列表 | P0 | 已完成 |
| **TSK-REL-API-103** | `ReleasePlanController` | `GET /release/plan/{id}` | 发布计划详情 | P0 | 已完成 |
| **TSK-REL-API-104** | `ReleasePlanController` | `PUT /release/plan/{id}` | 修改发布计划 | P0 | 已完成 |
| **TSK-REL-API-105** | `ReleasePlanController` | `DELETE /release/plan/{id}` | 删除发布计划 | P1 | 已完成 |
| **TSK-REL-API-201** | `ReleaseArtifactController` | `POST /release/artifact` | 上传制品 | P1 | 已完成 |
| **TSK-REL-API-202** | `ReleaseArtifactController` | `GET /release/artifact/list` | 制品列表 | P1 | 已完成 |
| **TSK-REL-API-203** | `ReleaseIssueController` | `POST /release/issue` | 关联 Issue | P1 | 已完成 |
| **TSK-REL-API-204** | `ReleaseIssueController` | `GET /release/issue/list` | Issue 列表 | P1 | 已完成 |

---

## 3. 前端任务

| 任务 ID | 页面路径 | 路由 | 功能 | 优先级 | 状态 |
|:---|:---|:---|:---|:---:|:---:|
| **TSK-REL-FE-101** | `views/release/index.vue` | `/release` | 发布列表页 | P0 | 已完成 |
| **TSK-REL-FE-102** | `views/release/detail.vue` | `/release/:id` | 发布详情页 | P0 | 已完成 |
| **TSK-REL-FE-103** | `views/release/plan-form.vue` | — | 发布计划表单（新增/编辑） | P0 | 已完成 |
| **TSK-REL-FE-201** | — | — | 制品上传管理页面 | P1 | 待开发（v0.4.0） |

---

## 4. 待开发项

| 任务 ID | 描述 | 版本 |
|:---|:---|:---|
| **TSK-REL-FE-201** | 制品上传管理页面 | v0.4.0 |
| **TSK-REL-FE-202** | 发布审批工作流（多人审批） | v0.4.0 |
| **TSK-REL-FE-203** | 发布计划甘特图视图 | v0.4.0 |
