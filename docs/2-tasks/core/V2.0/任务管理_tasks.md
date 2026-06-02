# 📋 任务管理 V2.0 开发任务单

> **📌 Jira 映射**：Jira Issue Custom Fields + Workflow Engine + Screen Scheme
>
> **关联 PRD**：`../../1-prd/core/V2.0/任务管理.md`
>
> **整体状态**：🔴 待启动

---

## Phase 1 — 核心框架

### 1.1 Issue Type 体系

|| 任务 ID | 说明 | 优先级 | 状态 |
|| :--- | :--- | :--- | :--- |
|| TSK-V2-DDL-101 | 创建 `issue_type` 表（id/name/type_key/description/icon/color/default_workflow_id/default_screen_id/is_system/sort） | P0 | 🔴 待启动 |
|| TSK-V2-DDL-102 | 创建 `issue_type_scheme` 表 | P0 | 🔴 待启动 |
|| TSK-V2-DDL-103 | 创建 `issue_type_scheme_issue_type` 表 | P0 | 🔴 待启动 |
|| TSK-V2-BE-101 | IssueTypeController CRUD API | P0 | 🔴 待启动 |
|| TSK-V2-BE-102 | IssueTypeSchemeController CRUD API | P0 | 🔴 待启动 |
|| TSK-V2-BE-103 | 初始化内置 Issue Type 数据（epic/story/task/bug/tech_debt/subtask） | P0 | 🔴 待启动 |
|| TSK-V2-BE-104 | `issue` 表新增 `issue_type_id` 和 `issue_no` 字段，生成 `issue_no`（PROJECT_CODE-NNNN） | P0 | 🔴 待启动 |
|| TSK-V2-FE-101 | Issue Type 管理页面（列表 + 新增 + 编辑 + 禁用） | P0 | 🔴 待启动 |
|| TSK-V2-FE-102 | Issue Type Scheme 管理页面 | P1 | 🔴 待启动 |

### 1.2 自定义字段体系

|| 任务 ID | 说明 | 优先级 | 状态 |
|| :--- | :--- | :--- | :--- |
|| TSK-V2-DDL-201 | 创建 `custom_field_definition` 表（field_key/name/type/description/validation_rule/searcher_key/is_global/project_id） | P0 | 🔴 待启动 |
|| TSK-V2-DDL-202 | 创建 `custom_field_option` 表（field_id/parent_option_id/option_value/label/color/sort） | P0 | 🔴 待启动 |
|| TSK-V2-DDL-203 | 创建 `custom_field_context` 表（field_id/context_type/project_id） | P0 | 🔴 待启动 |
|| TSK-V2-DDL-204 | 创建 `custom_field_context_option` 表（上下文级选项覆盖） | P1 | 🔴 待启动 |
|| TSK-V2-DDL-205 | 创建 `custom_field_value` EAV 表（issue_id/field_id + text/number/date/option 多列） | P0 | 🔴 待启动 |
|| TSK-V2-BE-201 | CustomFieldDefinitionController CRUD | P0 | 🔴 待启动 |
|| TSK-V2-BE-202 | CustomFieldValueService 存储/读取（根据 field_type 路由到不同列） | P0 | 🔴 待启动 |
|| TSK-V2-BE-203 | CustomFieldValueService 列表查询（反查 + JQL 风格搜索） | P0 | 🔴 待启动 |
|| TSK-V2-BE-204 | 初始化内置自定义字段（severity/story_points/team） | P1 | 🔴 待启动 |
|| TSK-V2-FE-201 | 自定义字段管理页面（字段定义 + 选项管理 + 上下文配置） | P0 | 🔴 待启动 |
|| TSK-V2-FE-202 | 字段渲染器体系（FieldRenderer 组件，支持 25 种字段类型） | P0 | 🔴 待启动 |
|| TSK-V2-FE-203 | 动态表单渲染器（IssueForm，根据 Screen 配置渲染字段） | P0 | 🔴 待启动 |

### 1.3 Field Configuration 体系

|| 任务 ID | 说明 | 优先级 | 状态 |
|| :--- | :--- | :--- | :--- |
|| TSK-V2-DDL-301 | 创建 `field_config_scheme` 表 | P0 | 🔴 待启动 |
|| TSK-V2-DDL-302 | 创建 `field_config` 表（scheme_id/field_id/is_hidden/is_required/is_readonly/label/description/default_value） | P0 | 🔴 待启动 |
|| TSK-V2-DDL-303 | 创建 `field_config_scheme_issue_type` 和 `field_config_scheme_project` 关联表 | P0 | 🔴 待启动 |
|| TSK-V2-BE-301 | FieldConfigSchemeController CRUD | P0 | 🔴 待启动 |
|| TSK-V2-BE-302 | 动态表单加载器：根据 issue_type_id + context 加载完整 field config | P0 | 🔴 待启动 |
|| TSK-V2-FE-301 | Field Configuration Scheme 管理页面 | P1 | 🔴 待启动 |

### 1.4 Screen / Screen Scheme 体系

|| 任务 ID | 说明 | 优先级 | 状态 |
|| :--- | :--- | :--- | :--- |
|| TSK-V2-DDL-401 | 创建 `screen` 表（name/screen_type/layout JSON） | P0 | 🔴 待启动 |
|| TSK-V2-DDL-402 | 创建 `screen_field` 表（screen_id/field_key/is_required/is_readonly/is_hidden/sort） | P0 | 🔴 待启动 |
|| TSK-V2-DDL-403 | 创建 `screen_scheme` 表 | P0 | 🔴 待启动 |
|| TSK-V2-DDL-404 | 创建 `screen_scheme_detail` 表（scheme_id/operation/screen_id） | P0 | 🔴 待启动 |
|| TSK-V2-DDL-405 | 创建 `issue_type_screen_scheme` 表 | P0 | 🔴 待启动 |
|| TSK-V2-BE-401 | ScreenController CRUD | P0 | 🔴 待启动 |
|| TSK-V2-BE-402 | ScreenSchemeController CRUD | P0 | 🔴 待启动 |
|| TSK-V2-BE-403 | 动态 Screen 加载器：根据 issue_type_id + operation 解析 Screen | P0 | 🔴 待启动 |
|| TSK-V2-FE-401 | Screen 可视化编辑器（拖拽字段到 Tab，支持 Tab 增删改） | P0 | 🔴 待启动 |
|| TSK-V2-FE-402 | Screen Scheme 管理页面 | P1 | 🔴 待启动 |

---

## Phase 2 — 工作流引擎

### 2.1 工作流定义

|| 任务 ID | 说明 | 优先级 | 状态 |
|| :--- | :--- | :--- | :--- |
|| TSK-V2-DDL-501 | 创建 `workflow_definition` 表 | P0 | 🔴 待启动 |
|| TSK-V2-DDL-502 | 创建 `workflow_status` 表（workflow_id/status_key/name/category/color/is_initial/sort） | P0 | 🔴 待启动 |
|| TSK-V2-DDL-503 | 创建 `workflow_transition` 表（workflow_id/from_status_id/to_status_id/name/description/sort） | P0 | 🔴 待启动 |
|| TSK-V2-DDL-504 | 创建 `workflow_condition` 表（transition_id/field_key/operator/value/logic） | P0 | 🔴 待启动 |
|| TSK-V2-DDL-505 | 创建 `workflow_validator` 表（transition_id/field_key/operator/value/error_message） | P0 | 🔴 待启动 |
|| TSK-V2-DDL-506 | 创建 `workflow_post_function` 表（transition_id/function_type/function_config/execute_order） | P0 | 🔴 待启动 |
|| TSK-V2-DDL-507 | 创建 `workflow_trigger` 表（workflow_id/trigger_type/trigger_config/conditions） | P2 | 🔴 待启动 |
|| TSK-V2-BE-501 | WorkflowDefinitionController CRUD | P0 | 🔴 待启动 |
|| TSK-V2-BE-502 | 初始化内置工作流（基础状态工作流 / Bug 工作流 / 敏捷 Story 工作流） | P0 | 🔴 待启动 |

### 2.2 工作流引擎核心

|| 任务 ID | 说明 | 优先级 | 状态 |
|| :--- | :--- | :--- | :--- |
|| TSK-V2-BE-511 | WorkflowEngine.executeTransition() — 流转主入口 | P0 | 🔴 待启动 |
|| TSK-V2-BE-512 | 条件引擎 — evaluateConditions（AND/OR 逻辑组合，支持 12 种操作符） | P0 | 🔴 待启动 |
|| TSK-V2-BE-513 | 校验引擎 — evaluateValidators（前置数据校验） | P0 | 🔴 待启动 |
|| TSK-V2-BE-514 | 前置动作执行 — executePreFunctions | P1 | 🔴 待启动 |
|| TSK-V2-BE-515 | 后置动作执行 — executePostFunctions（含 9 种动作类型） | P0 | 🔴 待启动 |
|| TSK-V2-BE-516 | 状态变更 API（PUT /issue/{id}/transition，校验 + 执行 + 通知） | P0 | 🔴 待启动 |
|| TSK-V2-BE-517 | 获取可用流转列表 API（GET /issue/{id}/transitions，根据当前状态返回可用流转） | P0 | 🔴 待启动 |

### 2.3 工作流 Scheme

|| 任务 ID | 说明 | 优先级 | 状态 |
|| :--- | :--- | :--- | :--- |
|| TSK-V2-DDL-521 | 创建 `workflow_scheme` 表 | P0 | 🔴 待启动 |
|| TSK-V2-DDL-522 | 创建 `workflow_scheme_issue_type` 表（scheme_id/issue_type_id/workflow_id 覆盖） | P0 | 🔴 待启动 |
|| TSK-V2-DDL-523 | 创建 `workflow_scheme_project` 表 | P0 | 🔴 待启动 |
|| TSK-V2-BE-521 | WorkflowSchemeController CRUD | P0 | 🔴 待启动 |
|| TSK-V2-FE-501 | 工作流可视化编辑器（状态节点 + 流转箭头 + 条件/动作配置） | P0 | 🔴 待启动 |
|| TSK-V2-FE-502 | 工作流 Scheme 管理页面 | P1 | 🔴 待启动 |
|| TSK-V2-FE-503 | 状态变更抽屉（显示可用流转 + 条件说明 + 校验失败提示） | P0 | 🔴 待启动 |

---

## Phase 3 — 看板增强

|| 任务 ID | 说明 | 优先级 | 状态 |
|| :--- | :--- | :--- | :--- |
|| TSK-V2-DDL-601 | 创建 `board` 表（project_id/board_type/filter_jql/group_by_field） | P1 | 🔴 待启动 |
|| TSK-V2-DDL-602 | 创建 `board_column` 表（board_id/column_name/status_key/wip_limit/color/sort） | P1 | 🔴 待启动 |
|| TSK-V2-DDL-603 | 创建 `board_swimlane` 表 | P2 | 🔴 待启动 |
|| TSK-V2-BE-601 | BoardController CRUD + 看板数据查询（按列分组） | P1 | 🔴 待启动 |
|| TSK-V2-FE-601 | 看板视图（动态列 + WIP 限制 + 泳道） | P1 | 🔴 待启动 |

---

## Phase 4 — 项目模板

|| 任务 ID | 说明 | 优先级 | 状态 |
|| :--- | :--- | :--- | :--- |
|| TSK-V2-DDL-701 | 创建 `project_template` 表（name/description/category/icon/usage_count/is_official） | P2 | 🔴 待启动 |
|| TSK-V2-DDL-702 | 创建 `project_template_content` 表（template_id/content_type/content_data JSON） | P2 | 🔴 待启动 |
|| TSK-V2-BE-701 | ProjectTemplateService 从模板创建项目（序列化/反序列化 Scheme 配置） | P2 | 🔴 待启动 |
|| TSK-V2-BE-702 | 初始化官方模板数据（敏捷开发/Bug追踪/看板管理/空白项目） | P2 | 🔴 待启动 |
|| TSK-V2-FE-701 | 项目模板选择页面（模板预览 + 配置概览） | P2 | 🔴 待启动 |

---

## Phase 5 — 性能与迁移

|| 任务 ID | 说明 | 优先级 | 状态 |
|| :--- | :--- | :--- | :--- |
|| TSK-V2-OP-801 | EAV 查询优化（联合索引 + 反查表方案选型） | P2 | 🔴 待启动 |
|| TSK-V2-OP-802 | V1.0 → V2.0 数据迁移脚本（issue 表字段映射 + 状态映射） | P0 | 🔴 待启动 |
|| TSK-V2-OP-803 | 灰度发布策略（旧项目保持 V1.0，新项目默认 V2.0） | P1 | 🔴 待启动 |
|| TSK-V2-OP-804 | 字段搜索索引（Elasticsearch 或 MySQL 全文索引） | P2 | 🔴 待启动 |

---

## 状态变更规范

> 与 V1.0 TASKS 一致，AI 或人工开发时必须更新任务状态。

1. **领单启动** — `🔴 待启动` → `🟡 进行中`，备注追加承接人和日期
2. **提测完成** — 测试通过后，备注补充测试结果
3. **完成合并** — `🟡 进行中` → `✅ 已完成`，备注补充 commit hash 和 PR 编号
