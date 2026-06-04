# Core V2.0 — 任务管理开发任务单

> **关联 PRD**：[任务管理.md](../../../1-prd/core/V2.0/任务管理.md)
> **关联 Milestone**：[MILESTONES/V2.0-小版本规划.md](../../../MILESTONES/V2.0-小版本规划.md)

## V2.0 任务管理任务概览

| 模块 | 任务 ID | 说明 |
|------|---------|------|
| EAV 自定义字段 | V20-CF-* | 25 种字段类型 + 上下文 + EAV 存储 |
| Issue Type | V20-IT-* | Issue Type 管理 + Issue Type Scheme |
| Screen / Scheme | V20-SCR-* | Screen + Screen Scheme + Issue Type Screen Scheme |
| Field Configuration | V20-FC-* | Field Configuration + Field Configuration Scheme |
| Workflow Engine | V20-WF-* | 工作流 CRUD + 流转校验 + 条件/校验/动作 |
| 看板增强 | V20-BOARD-* | 动态列配置 + Backlog 视图 |
| JQL 增强 | V20-JQL-* | 高级 JQL 函数解析器 |
| Issue Security | V20-SEC-* | 安全级别方案 |
| Issue Link | V20-IL-* | 任务关联（8 种关系类型）|
| 批量操作 | V20-BATCH-* | 批量编辑/移动/流转 |
| 项目模板 | V20-TPL-* | 项目模板 + 配置复制 |

## 数据库设计（V20-DB-*）

| 任务 ID | 说明 | 工作量 | 状态 |
|---------|------|--------|------|
| V20-DB-001 | `issue_type` 表 DDL | 0.5d | 🔴 |
| V20-DB-002 | `workflow_definition` 表 DDL | 0.5d | 🔴 |
| V20-DB-003 | `workflow_status` 表 DDL | 0.5d | 🔴 |
| V20-DB-004 | `workflow_transition` 表 DDL | 0.5d | 🔴 |
| V20-DB-005 | `workflow_condition` 表 DDL | 0.5d | 🔴 |
| V20-DB-006 | `workflow_validator` 表 DDL | 0.5d | 🔴 |
| V20-DB-007 | `workflow_post_function` 表 DDL | 0.5d | 🔴 |
| V20-DB-008 | `workflow_trigger` 表 DDL | 0.5d | 🔴 |
| V20-DB-009 | `screen` 表 DDL（含 layout JSON）| 0.5d | 🔴 |
| V20-DB-010 | `screen_field` 表 DDL | 0.5d | 🔴 |
| V20-DB-011 | `screen_scheme` + `screen_scheme_detail` 表 DDL | 0.5d | 🔴 |
| V20-DB-012 | `issue_type_screen_scheme` 表 DDL | 0.5d | 🔴 |
| V20-DB-013 | `custom_field_definition` 表 DDL（25 种字段类型）| 1d | 🔴 |
| V20-DB-014 | `custom_field_option` 表 DDL | 0.5d | 🔴 |
| V20-DB-015 | `custom_field_context` 表 DDL | 0.5d | 🔴 |
| V20-DB-016 | `custom_field_context_option` 表 DDL | 0.5d | 🔴 |
| V20-DB-017 | `custom_field_value` 表 DDL（EAV 存储）| 1d | 🔴 |
| V20-DB-018 | `field_config` 表 DDL | 0.5d | 🔴 |
| V20-DB-019 | `field_config_scheme` + `field_config_scheme_issue_type` + `field_config_scheme_project` 表 DDL | 0.5d | 🔴 |
| V20-DB-020 | `issue_type_scheme` + `issue_type_scheme_issue_type` 表 DDL | 0.5d | 🔴 |
| V20-DB-021 | `workflow_scheme` + `workflow_scheme_issue_type` + `workflow_scheme_project` 表 DDL | 0.5d | 🔴 |
| V20-DB-022 | `board` + `board_column` + `board_swimlane` 表 DDL | 1d | 🔴 |
| V20-DB-023 | `issue_link_type` + `issue_link` 表 DDL | 0.5d | 🔴 |
| V20-DB-024 | `issue_security_scheme` + `issue_security_level` 表 DDL | 0.5d | 🔴 |
| V20-DB-025 | `project_template` + `project_template_content` 表 DDL | 0.5d | 🔴 |
| V20-DB-026 | `issue` 表改造（issue_no / issue_type_id / severity / reporter_id / resolution / story_points）| 1d | 🔴 |
| V20-DB-027 | `issue_label` + `label` 表 DDL（V2.0 标签体系）| 0.5d | 🔴 |

## EAV 自定义字段体系（V20-CF-*）

### 后端（V20-CF-BE-*）

| 任务 ID | 说明 | 工作量 | 状态 |
|---------|------|--------|------|
| V20-CF-BE-001 | 自定义字段定义 CRUD API | 2d | 🔴 |
| V20-CF-BE-002 | 自定义字段值存储引擎（EAV JSONB，多列存储）| 2d | 🔴 |
| V20-CF-BE-003 | 自定义字段值读取 API（GET /issue/{id}/fields）| 1d | 🔴 |
| V20-CF-BE-004 | 字段选项 CRUD API（custom_field_option）| 1d | 🔴 |
| V20-CF-BE-005 | 字段上下文 API（global / project / template 作用域）| 1.5d | 🔴 |
| V20-CF-BE-006 | 上下文选项覆盖 API | 1d | 🔴 |
| V20-CF-BE-007 | 字段校验规则引擎（JSON validation_rule）| 1d | 🔴 |
| V20-CF-BE-008 | 任务创建/编辑时字段校验逻辑 | 1d | 🔴 |
| V20-CF-BE-009 | 列表页 JQL 风格自定义字段过滤 | 1.5d | 🔴 |
| V20-CF-BE-010 | 自定义字段搜索器实现（textsearcher / exactnumber / datepicker / userpicker 等）| 2d | 🔴 |

### 前端（V20-CF-FE-*）

| 任务 ID | 说明 | 工作量 | 状态 |
|---------|------|--------|------|
| V20-CF-FE-001 | 自定义字段管理器页面（列表 + 创建/编辑弹窗）| 2d | 🔴 |
| V20-CF-FE-002 | 25 种字段类型渲染组件（TextField / Number / Date / Select / MultiSelect / UserPicker 等）| 3d | 🔴 |
| V20-CF-FE-003 | 动态表单渲染引擎（IssueForm，根据 Screen + Field Config 动态渲染）| 3d | 🔴 |
| V20-CF-FE-004 | 字段选项编辑器（Select / Radio / Checkbox 等选项管理）| 1.5d | 🔴 |
| V20-CF-FE-005 | 上下文配置页面（字段作用范围管理）| 1.5d | 🔴 |
| V20-CF-FE-006 | 字段校验规则编辑器（必填/正则/范围）| 1d | 🔴 |

## Issue Type 体系（V20-IT-*）

### 后端（V20-IT-BE-*）

| 任务 ID | 说明 | 工作量 | 状态 |
|---------|------|--------|------|
| V20-IT-BE-001 | Issue Type CRUD API | 1.5d | 🔴 |
| V20-IT-BE-002 | Issue Type Scheme CRUD API | 1.5d | 🔴 |
| V20-IT-BE-003 | Issue Type 与工作流/Screen/Field Config 关联配置 API | 1d | 🔴 |
| V20-IT-BE-004 | Issue 创建时类型校验（必须在 Scheme 允许范围内）| 0.5d | 🔴 |
| V20-IT-BE-005 | 内置 Issue Type 初始化数据（Epic/Story/Task/Bug/Improvement）| 0.5d | 🔴 |

### 前端（V20-IT-FE-*）

| 任务 ID | 说明 | 工作量 | 状态 |
|---------|------|--------|------|
| V20-IT-FE-001 | Issue Type 管理页面（列表 + 创建/编辑）| 1.5d | 🔴 |
| V20-IT-FE-002 | Issue Type Scheme 配置页面 | 1.5d | 🔴 |
| V20-IT-FE-003 | Issue Type 选择器（创建任务时）| 1d | 🔴 |
| V20-IT-FE-004 | Issue Type 图标和颜色展示 | 0.5d | 🔴 |

## Screen / Screen Scheme 体系（V20-SCR-*）

### 后端（V20-SCR-BE-*）

| 任务 ID | 说明 | 工作量 | 状态 |
|---------|------|--------|------|
| V20-SCR-BE-001 | Screen CRUD API（含 layout JSON）| 1.5d | 🔴 |
| V20-SCR-BE-002 | Screen Field 配置 API（字段可见性/必填/只读）| 1d | 🔴 |
| V20-SCR-BE-003 | Screen Scheme CRUD API | 1d | 🔴 |
| V20-SCR-BE-004 | Issue Type Screen Scheme API | 1d | 🔴 |
| V20-SCR-BE-005 | 任务创建/编辑时 Screen 加载逻辑（根据 Issue Type + 操作类型）| 1.5d | 🔴 |
| V20-SCR-BE-006 | 动态表单字段过滤（根据 Screen + Field Config 过滤）| 1d | 🔴 |

### 前端（V20-SCR-FE-*）

| 任务 ID | 说明 | 工作量 | 状态 |
|---------|------|--------|------|
| V20-SCR-FE-001 | Screen 管理页面（列表 + 可视化布局编辑器）| 2d | 🔴 |
| V20-SCR-FE-002 | Screen 字段配置编辑器（Tab + 字段分组 + 拖拽排序）| 2d | 🔴 |
| V20-SCR-FE-003 | Screen Scheme 配置页面 | 1.5d | 🔴 |
| V20-SCR-FE-004 | Issue Type Screen Scheme 配置页面 | 1.5d | 🔴 |
| V20-SCR-FE-005 | Screen 预览功能 | 1d | 🔴 |

## Field Configuration 体系（V20-FC-*）

### 后端（V20-FC-BE-*）

| 任务 ID | 说明 | 工作量 | 状态 |
|---------|------|--------|------|
| V20-FC-BE-001 | Field Configuration CRUD API | 1.5d | 🔴 |
| V20-FC-BE-002 | Field Configuration Scheme CRUD API | 1d | 🔴 |
| V20-FC-BE-003 | Scheme 与 Issue Type/Project 关联 API | 1d | 🔴 |
| V20-FC-BE-004 | 字段行为加载逻辑（is_hidden / is_required / is_readonly / label）| 1d | 🔴 |
| V20-FC-BE-005 | 默认值填充逻辑（Screen 创建时）| 0.5d | 🔴 |

### 前端（V20-FC-FE-*）

| 任务 ID | 说明 | 工作量 | 状态 |
|---------|------|--------|------|
| V20-FC-FE-001 | Field Configuration 编辑页面（表格形式：字段/隐藏/必填/只读/标签）| 2d | 🔴 |
| V20-FC-FE-002 | Field Configuration Scheme 配置页面 | 1.5d | 🔴 |
| V20-FC-FE-003 | 字段默认值编辑器 | 1d | 🔴 |
| V20-FC-FE-004 | 表单渲染时字段行为应用（必填标记/只读/隐藏）| 1d | 🔴 |

## 工作流引擎（V20-WF-*）

### 后端（V20-WF-BE-*）

| 任务 ID | 说明 | 工作量 | 状态 |
|---------|------|--------|------|
| V20-WF-BE-001 | Workflow Definition CRUD API | 1.5d | 🔴 |
| V20-WF-BE-002 | Workflow Status CRUD API | 1d | 🔴 |
| V20-WF-BE-003 | Workflow Transition CRUD API | 1d | 🔴 |
| V20-WF-BE-004 | 状态流转校验引擎（加载流转路径 + 条件评估）| 1.5d | 🔴 |
| V20-WF-BE-005 | 流转条件引擎（workflow_condition，AND/OR 逻辑）| 1.5d | 🔴 |
| V20-WF-BE-006 | 流转校验器引擎（workflow_validator）| 1d | 🔴 |
| V20-WF-BE-007 | 流转后置动作执行引擎（workflow_post_function）| 1.5d | 🔴 |
| V20-WF-BE-008 | 工作流执行入口 API（PUT /issue/{id}/transition）| 1d | 🔴 |
| V20-WF-BE-009 | 工作流草稿/发布区分（is_draft 字段）| 1d | 🔴 |
| V20-WF-BE-010 | 工作流循环检测（DFS 算法）| 1d | 🔴 |
| V20-WF-BE-011 | 全局转换支持（is_global 字段）| 1d | 🔴 |
| V20-WF-BE-012 | Workflow Scheme CRUD API | 1d | 🔴 |
| V20-WF-BE-013 | Issue Type 与 Workflow Scheme 映射 API | 1d | 🔴 |
| V20-WF-BE-014 | 状态变更时自动触发后置动作 | 1d | 🔴 |
| V20-WF-BE-015 | 内置 Post Function 实现（SET_FIELD / SEND_NOTIFICATION / CREATE_SUBTASK 等）| 2d | 🔴 |
| V20-WF-BE-016 | `AUTOMATION_CUSTOM_ACTION` 扩展点执行逻辑 | 1.5d | 🔴 |

### 前端（V20-WF-FE-*）

| 任务 ID | 说明 | 工作量 | 状态 |
|---------|------|--------|------|
| V20-WF-FE-001 | 工作流可视化编辑器（拖拽节点/连线）| 4d | 🔴 |
| V20-WF-FE-002 | 状态节点配置面板（名称/分类/颜色/图标）| 1d | 🔴 |
| V20-WF-FE-003 | 流转连接线配置（名称/条件/描述）| 1.5d | 🔴 |
| V20-WF-FE-004 | 条件编辑器（字段/操作符/值，AND/OR 组合）| 2d | 🔴 |
| V20-WF-FE-005 | 校验器配置面板 | 1d | 🔴 |
| V20-WF-FE-006 | Post Function 配置面板（类型/参数）| 1.5d | 🔴 |
| V20-WF-FE-007 | 工作流列表页面 | 1d | 🔴 |
| V20-WF-FE-008 | 工作流详情预览（只读视图）| 1d | 🔴 |
| V20-WF-FE-009 | 工作流发布确认（影响范围预览）| 1d | 🔴 |
| V20-WF-FE-010 | 流转对话框（状态变更时弹出，显示可用流转）| 1.5d | 🔴 |
| V20-WF-FE-011 | Workflow Scheme 配置页面 | 1.5d | 🔴 |

## 看板增强（V20-BOARD-*）

### 后端（V20-BOARD-BE-*）

| 任务 ID | 说明 | 工作量 | 状态 |
|---------|------|--------|------|
| V20-BOARD-BE-001 | Board CRUD API | 1d | 🔴 |
| V20-BOARD-BE-002 | Board Column CRUD API（status_key / wip_limit / color）| 1d | 🔴 |
| V20-BOARD-BE-003 | Board Swimlane API（泳道路由配置）| 1d | 🔴 |
| V20-BOARD-BE-004 | 动态列看板视图 API（根据 Board 配置加载列）| 1.5d | 🔴 |
| V20-BOARD-BE-005 | WIP 限制校验逻辑 | 0.5d | 🔴 |
| V20-BOARD-BE-006 | Backlog 视图 API（未规划到 Sprint 的 Issue）| 1.5d | 🔴 |
| V20-BOARD-BE-007 | Epic 分组查询 API | 1d | 🔴 |
| V20-BOARD-BE-008 | 快速规划 API（拖拽排序/分配迭代）| 1d | 🔴 |
| V20-BOARD-BE-009 | 故事点汇总计算（Epic 下所有 Story 的故事点之和）| 1d | 🔴 |

### 前端（V20-BOARD-FE-*）

| 任务 ID | 说明 | 工作量 | 状态 |
|---------|------|--------|------|
| V20-BOARD-FE-001 | 动态列看板视图（根据配置渲染列 + WIP 限制可视化）| 2d | 🔴 |
| V20-BOARD-FE-002 | 看板配置页面（Board / Column / Swimlane 管理）| 2d | 🔴 |
| V20-BOARD-FE-003 | Backlog 视图 UI（Epic 分组 + 快速规划）| 2.5d | 🔴 |
| V20-BOARD-FE-004 | Epic 排序 + 故事点估算组件 | 1d | 🔴 |
| V20-BOARD-FE-005 | 泳道视图（按泳道字段分组显示）| 1.5d | 🔴 |
| V20-BOARD-FE-006 | 拖拽到 Sprint 规划 | 1.5d | 🔴 |
| V20-BOARD-FE-007 | 故事点汇总展示 | 0.5d | 🔴 |

## JQL 增强（V20-JQL-*）

### 后端（V20-JQL-BE-*）

| 任务 ID | 说明 | 工作量 | 状态 |
|---------|------|--------|------|
| V20-JQL-BE-001 | JQL 解析器增强（支持 earliestLag / aggregateExpression）| 2.5d | 🔴 |
| V20-JQL-BE-002 | JQL 函数注册表（membersOf / watchers / linkedIssues / sprint / 等）| 2d | 🔴 |
| V20-JQL-BE-003 | 自定义字段 JQL 过滤（解析 field_key 引用）| 1.5d | 🔴 |
| V20-JQL-BE-004 | JQL 自动补全数据 API | 1d | 🔴 |

### 前端（V20-JQL-FE-*）

| 任务 ID | 说明 | 工作量 | 状态 |
|---------|------|--------|------|
| V20-JQL-FE-001 | JQL 查询构建器 UI（可视化 + 文本模式切换）| 2.5d | 🔴 |
| V20-JQL-FE-002 | JQL 自动补全（下拉提示函数/字段/操作符）| 1.5d | 🔴 |
| V20-JQL-FE-003 | 高级筛选面板（支持自定义字段筛选）| 1.5d | 🔴 |

## Issue Security（V20-SEC-*）

### 后端（V20-SEC-BE-*）

| 任务 ID | 说明 | 工作量 | 状态 |
|---------|------|--------|------|
| V20-SEC-BE-001 | Issue Security Scheme CRUD API | 1d | 🔴 |
| V20-SEC-BE-002 | Issue Security Level CRUD API | 1d | 🔴 |
| V20-SEC-BE-003 | 任务安全级别设置 API（PUT /issue/{id}/security）| 0.5d | 🔴 |
| V20-SEC-BE-004 | 安全级别过滤逻辑（列表查询时自动过滤）| 1d | 🔴 |

### 前端（V20-SEC-FE-*）

| 任务 ID | 说明 | 工作量 | 状态 |
|---------|------|--------|------|
| V20-SEC-FE-001 | 安全方案配置页面 | 1.5d | 🔴 |
| V20-SEC-FE-002 | 任务详情页安全级别展示 + 修改 | 1d | 🔴 |

## Issue Link（V20-IL-*）

### 后端（V20-IL-BE-*）

| 任务 ID | 说明 | 工作量 | 状态 |
|---------|------|--------|------|
| V20-IL-BE-001 | Issue Link Type CRUD API（8 种内置类型）| 1d | 🔴 |
| V20-IL-BE-002 | Issue Link CRUD API | 1.5d | 🔴 |
| V20-IL-BE-003 | 链接冲突检测（循环依赖检测）| 1d | 🔴 |
| V20-IL-BE-004 | 链接变更通知（发送给被关联任务负责人）| 1d | 🔴 |

### 前端（V20-IL-FE-*）

| 任务 ID | 说明 | 工作量 | 状态 |
|---------|------|--------|------|
| V20-IL-FE-001 | 任务详情页「关联」Tab（链接列表 + 筛选 + 分组显示）| 2d | 🔴 |
| V20-IL-FE-002 | 创建链接抽屉（选择类型 + JQL 搜索目标任务）| 1.5d | 🔴 |
| V20-IL-FE-003 | 链接关系图可视化（任务网络图）| 2d | 🔴 |

## 批量操作（V20-BATCH-*）

### 后端（V20-BATCH-BE-*）

| 任务 ID | 说明 | 工作量 | 状态 |
|---------|------|--------|------|
| V20-BATCH-BE-001 | 批量编辑 API（字段更新 / 标签 / 优先级 / 迭代）| 1.5d | 🔴 |
| V20-BATCH-BE-002 | 批量移动 API（跨项目 / 跨 Issue Type）| 1.5d | 🔴 |
| V20-BATCH-BE-003 | 批量流转 API（按工作流校验可达状态）| 1.5d | 🔴 |
| V20-BATCH-BE-004 | 批量操作审计日志 | 1d | 🔴 |

### 前端（V20-BATCH-FE-*）

| 任务 ID | 说明 | 工作量 | 状态 |
|---------|------|--------|------|
| V20-BATCH-FE-001 | Issue 列表多选 + 批量操作栏 | 1.5d | 🔴 |
| V20-BATCH-FE-002 | 批量操作向导（分步：选择动作 → 配置 → 预览 → 执行）| 2d | 🔴 |
| V20-BATCH-FE-003 | 批量移动向导（目标项目选择 + 兼容性提示）| 1.5d | 🔴 |
| V20-BATCH-FE-004 | 批量流转对话框（可用状态 + 条件说明）| 1d | 🔴 |

## 项目模板（V20-TPL-*）

### 后端（V20-TPL-BE-*）

| 任务 ID | 说明 | 工作量 | 状态 |
|---------|------|--------|------|
| V20-TPL-BE-001 | Project Template CRUD API | 1.5d | 🔴 |
| V20-TPL-BE-002 | 模板配置序列化（Issue Type Scheme / Workflow Scheme / Screen Scheme / Field Config Scheme / Custom Field / Board）| 2d | 🔴 |
| V20-TPL-BE-003 | 从模板创建项目 API（反序列化配置 + 创建项目）| 2d | 🔴 |
| V20-TPL-BE-004 | 模板预览 API | 1d | 🔴 |

### 前端（V20-TPL-FE-*）

| 任务 ID | 说明 | 工作量 | 状态 |
|---------|------|--------|------|
| V20-TPL-FE-001 | 项目模板列表页面 | 1.5d | 🔴 |
| V20-TPL-FE-002 | 项目模板创建/编辑页面 | 2d | 🔴 |
| V20-TPL-FE-003 | 模板预览（配置项折叠展示）| 1.5d | 🔴 |
| V20-TPL-FE-004 | 从模板创建项目向导 | 1.5d | 🔴 |

## 项目设置页 — Scheme 配置 UI（V20-PROJ-FE-*）

| 任务 ID | 说明 | 工作量 | 状态 |
|---------|------|--------|------|
| V20-PROJ-FE-001 | Issue Type Scheme 配置页 | 1.5d | 🔴 |
| V20-PROJ-FE-002 | Workflow Scheme 配置页（类型 → 工作流映射）| 1.5d | 🔴 |
| V20-PROJ-FE-003 | Screen Scheme 配置页（操作 → Screen 映射）| 1.5d | 🔴 |
| V20-PROJ-FE-004 | Field Configuration Scheme 配置页 | 2d | 🔴 |
| V20-PROJ-FE-005 | Issue Security Scheme 配置页 | 1.5d | 🔴 |

## 状态变更规范

1. **领单启动** — `🔴 待启动` → `🟡 进行中`，备注追加承接人和日期
2. **提测完成** — 测试通过后，备注补充测试结果
3. **完成合并** — `🟡 进行中` → `✅ 已完成`，备注补充 commit hash 和 PR 编号
