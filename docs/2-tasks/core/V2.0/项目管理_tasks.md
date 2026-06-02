# 📋 项目管理 V2.0 开发任务单

> **📌 Jira 映射**：Jira Project Administration + Project Configuration
>
> **关联 PRD**：`../../1-prd/core/V2.0/项目管理.md`
>
> **整体状态**：🔴 待启动
>
> **前置依赖**：任务管理 V2.0 Phase 1（Issue Type 体系）需先完成

---

## Phase 1 — Project Scheme 配置体系

### 1.1 Project 表改造

|| 任务 ID | 说明 | 优先级 | 状态 |
|| :--- | :--- | :--- | :--- |
|| PRJ-V2-DDL-101 | `project` 表新增字段（issue_type_scheme_id/workflow_scheme_id/screen_scheme_id/field_config_scheme_id/board_id/enabled_modules JSON/task_no_format） | P0 | 🔴 待启动 |
|| PRJ-V2-DDL-102 | 创建 `project_member` 表（project_id/user_id/role） | P0 | 🔴 待启动 |
|| PRJ-V2-DDL-103 | 创建 `project_module` 表（子模块，parent_module_id 支持树形） | P1 | 🔴 待启动 |
|| PRJ-V2-DDL-104 | 创建 `project_version` 表（版本/里程碑） | P1 | 🔴 待启动 |
|| PRJ-V2-DDL-105 | 创建 `project_iteration` 表（Sprint 迭代） | P1 | 🔴 待启动 |
|| PRJ-V2-BE-101 | Project 详情 API 改造（返回 Scheme 引用信息） | P0 | 🔴 待启动 |
|| PRJ-V2-BE-102 | Project 成员管理 API | P0 | 🔴 待启动 |
|| PRJ-V2-BE-103 | Project 子模块 CRUD | P1 | 🔴 待启动 |
|| PRJ-V2-BE-104 | Project 版本/里程碑 CRUD | P1 | 🔴 待启动 |
|| PRJ-V2-BE-105 | Project 迭代 CRUD | P1 | 🔴 待启动 |

### 1.2 Project Scheme 关联 API

|| 任务 ID | 说明 | 优先级 | 状态 |
|| :--- | :--- | :--- | :--- |
|| PRJ-V2-BE-201 | Project 设置页 API（获取/更新 Issue Type Scheme 关联） | P0 | 🔴 待启动 |
|| PRJ-V2-BE-202 | Project 设置页 API（获取/更新 Workflow Scheme 关联） | P0 | 🔴 待启动 |
|| PRJ-V2-BE-203 | Project 设置页 API（获取/更新 Screen Scheme 关联） | P0 | 🔴 待启动 |
|| PRJ-V2-BE-204 | Project 设置页 API（获取/更新 Field Configuration Scheme 关联） | P0 | 🔴 待启动 |
|| PRJ-V2-BE-205 | Project Scheme 冲突检测（Project 引用了不存在的 Scheme 时报错） | P0 | 🔴 待启动 |

### 1.3 Priority Scheme

|| 任务 ID | 说明 | 优先级 | 状态 |
|| :--- | :--- | :--- | :--- |
|| PRJ-V2-DDL-301 | 创建 `priority` 表 | P1 | 🔴 待启动 |
|| PRJ-V2-DDL-302 | 创建 `priority_scheme` 表 | P1 | 🔴 待启动 |
|| PRJ-V2-DDL-303 | 创建 `priority_scheme_priority` 关联表 | P1 | 🔴 待启动 |
|| PRJ-V2-BE-301 | PriorityController + PrioritySchemeController CRUD | P1 | 🔴 待启动 |
|| PRJ-V2-BE-302 | 初始化内置优先级数据（p0-紧急/p1-高/p2-中/p3-低） | P1 | 🔴 待启动 |

---

## Phase 2 — Project Template

|| 任务 ID | 说明 | 优先级 | 状态 |
|| :--- | :--- | :--- | :--- |
|| PRJ-V2-BE-401 | ProjectTemplateService 从模板创建项目（完整 Scheme 序列化） | P0 | 🔴 待启动 |
|| PRJ-V2-BE-402 | 模板内容序列化（Issue Type Scheme / Workflow Scheme / Screen Scheme / Field Config Scheme） → JSON | P0 | 🔴 待启动 |
|| PRJ-V2-BE-403 | 模板内容反序列化（JSON → 创建完整的 Scheme 配置） | P0 | 🔴 待启动 |
|| PRJ-V2-BE-404 | 初始化官方模板数据（敏捷开发/Bug追踪/看板管理/空白项目） | P0 | 🔴 待启动 |
|| PRJ-V2-FE-401 | 新建项目流程改造（选择模板 → 选择 Scheme → 填写基本信息 → 创建） | P0 | 🔴 待启动 |
|| PRJ-V2-FE-402 | 项目模板选择页面（模板卡片 + 配置概览） | P0 | 🔴 待启动 |
|| PRJ-V2-FE-403 | 项目设置页 Scheme 配置 UI（Issue Type / Workflow / Screen / Field Config） | P0 | 🔴 待启动 |

---

## Phase 3 — Board Scheme

> 对应 PRD Section 5.4：Board 看板配置。

|| 任务 ID | 说明 | 优先级 | 状态 |
|| :--- | :--- | :--- | :--- |
|| PRJ-V2-DDL-601 | 创建 `board_scheme` 表（name/description/is_default） | P1 | 🔴 待启动 |
|| PRJ-V2-DDL-602 | 创建 `board` 表（scheme_id/project_id/board_type/filter_jql/name） | P1 | 🔴 待启动 |
|| PRJ-V2-DDL-603 | 创建 `board_column` 表（board_id/status_key/column_name/wip_limit/color/sort） | P1 | 🔴 待启动 |
|| PRJ-V2-BE-601 | BoardSchemeController + BoardController CRUD | P1 | 🔴 待启动 |
|| PRJ-V2-BE-602 | Project 设置页 Board Scheme 关联 API | P1 | 🔴 待启动 |
|| PRJ-V2-FE-601 | Project 设置页 Board Scheme 配置 UI | P1 | 🔴 待启动 |

---

## Phase 4 — Project 增强功能

|| 任务 ID | 说明 | 优先级 | 状态 |
|| :--- | :--- | :--- | :--- |
|| PRJ-V2-BE-501 | Project 模块管理页面 API | P1 | 🔴 待启动 |
|| PRJ-V2-BE-502 | Project 版本管理页面 API | P1 | 🔴 待启动 |
|| PRJ-V2-BE-503 | Project 迭代管理页面 API | P1 | 🔴 待启动 |
|| PRJ-V2-BE-504 | Project 成员角色管理 API（admin/lead/developer/tester/viewer） | P0 | 🔴 待启动 |
|| PRJ-V2-FE-501 | Project 设置页（新增 Scheme 配置 Tab） | P0 | 🔴 待启动 |
|| PRJ-V2-FE-502 | Project 成员角色管理页面 | P0 | 🔴 待启动 |
|| PRJ-V2-FE-503 | Project 模块管理页面 | P1 | 🔴 待启动 |
|| PRJ-V2-FE-504 | Project 版本/里程碑管理页面 | P1 | 🔴 待启动 |
|| PRJ-V2-FE-505 | Project 迭代管理页面（Sprint 创建/启动/完成/关闭） | P1 | 🔴 待启动 |

---

## 状态变更规范

1. **领单启动** — `🔴 待启动` → `🟡 进行中`，备注追加承接人和日期
2. **提测完成** — 测试通过后，备注补充测试结果
3. **完成合并** — `🟡 进行中` → `✅ 已完成`，备注补充 commit hash 和 PR 编号
