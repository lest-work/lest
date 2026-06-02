# 任务管理 V3.0 开发任务单

> **关联 PRD**：`../../1-prd/core/V3.0/任务管理.md`
>
> **整体状态**：🔴 待启动
>
> **前置依赖**：任务管理 V2.0 全部完成（EAV + Screen/Scheme + Workflow Engine）
>
> **定位**：V3.0 在 V2.0 Scheme 架构基础上，补全所有 Jira 核心功能缺失（Issue Link / Attachment / Time Tracking / Sub-task / Automation 执行引擎 / 回收站）

---

## Phase 1 — Issue Link（任务关联）

> 对应 PRD Section 2：8 种链接类型。

### 1.1 数据库

||| 任务 ID | 说明 | 优先级 | 状态 |
||| :--- | :--- | :--- | :--- |
||| TSK-V3-DDL-101 | 创建 `issue_link_type` 表（link_key/name/inward/outward/is_system） | P0 | 🔴 待启动 |
||| TSK-V3-DDL-102 | 创建 `issue_link` 表（source_issue_id/target_issue_id/link_type_id/created_by） | P0 | 🔴 待启动 |
||| TSK-V3-DDL-103 | 初始化内置链接类型数据（blocks/is_blocked_by/duplicates/is_duplicated_by/relates_to/causes/is_caused_by/clones） | P0 | 🔴 待启动 |

### 1.2 后端 API

||| 任务 ID | 说明 | 优先级 | 状态 |
||| :--- | :--- | :--- | :--- |
||| TSK-V3-BE-101 | IssueLinkController — 创建/删除/列表查询 API | P0 | 🔴 待启动 |
||| TSK-V3-BE-102 | 链接冲突检测（禁止循环依赖，如 A blocks B blocks C blocks A） | P0 | 🔴 待启动 |
||| TSK-V3-BE-103 | 链接变更通知（发送 Webhook 给被关联任务负责人） | P1 | 🔴 待启动 |

### 1.3 前端

||| 任务 ID | 说明 | 优先级 | 状态 |
||| :--- | :--- | :--- | :--- |
||| TSK-V3-FE-101 | 任务详情页「关联」Tab（链接列表 + 筛选 + 分组显示） | P0 | 🔴 待启动 |
||| TSK-V3-FE-102 | 创建链接抽屉（选择链接类型 + JQL 搜索目标任务） | P0 | 🔴 待启动 |
||| TSK-V3-FE-103 | 链接关系图可视化（任务网络图） | P2 | 🔴 待启动 |

---

## Phase 2 — Attachment（附件管理）

> 对应 PRD Section 3：完整附件体系。

### 2.1 数据库

||| 任务 ID | 说明 | 优先级 | 状态 |
||| :--- | :--- | :--- | :--- |
||| TSK-V3-DDL-201 | 创建 `attachment` 表（issue_id/file_name/file_size/file_path/file_type/thumbnail_path/uploaded_by/version/parent_id/is_deleted） | P0 | 🔴 待启动 |

### 2.2 后端 API

||| 任务 ID | 说明 | 优先级 | 状态 |
||| :--- | :--- | :--- | :--- |
||| TSK-V3-BE-201 | AttachmentController — 上传/下载/删除/列表 API | P0 | 🔴 待启动 |
||| TSK-V3-BE-202 | 文件预览 API（图片缩略图生成 / PDF 在线预览 URL） | P1 | 🔴 待启动 |
||| TSK-V3-BE-203 | 附件版本管理 API（上传新版本/查看历史版本/恢复旧版本） | P1 | 🔴 待启动 |
||| TSK-V3-BE-204 | 附件大小限制校验（单文件 100MB，总计 500MB） | P0 | 🔴 待启动 |

### 2.3 前端

||| 任务 ID | 说明 | 优先级 | 状态 |
||| :--- | :--- | :--- | :--- |
||| TSK-V3-FE-201 | 任务详情页「附件」Tab（上传/预览/下载/删除） | P0 | 🔴 待启动 |
||| TSK-V3-FE-202 | 拖拽上传组件（支持多文件） | P0 | 🔴 待启动 |
||| TSK-V3-FE-203 | 附件版本历史面板 | P1 | 🔴 待启动 |

---

## Phase 3 — Time Tracking（时间追踪）

> 对应 PRD Section 4：预估工时/剩余工时/工时记录。

### 3.1 数据库

||| 任务 ID | 说明 | 优先级 | 状态 |
||| :--- | :--- | :--- | :--- |
||| TSK-V3-DDL-301 | `issue` 表新增字段（estimate_seconds/remaining_seconds/time_spent_seconds） | P0 | 🔴 待启动 |
||| TSK-V3-DDL-302 | 创建 `worklog` 表（issue_id/user_id/started_at/duration_seconds/description） | P0 | 🔴 待启动 |

### 3.2 后端 API

||| 任务 ID | 说明 | 优先级 | 状态 |
||| :--- | :--- | :--- | :--- |
||| TSK-V3-BE-301 | WorklogController — 创建/编辑/删除/列表 API | P0 | 🔴 待启动 |
||| TSK-V3-BE-302 | 预估工时/剩余工时更新逻辑（记录工时后自动重算 remaining） | P0 | 🔴 待启动 |
||| TSK-V3-BE-303 | 剩余工时为负警告逻辑 | P1 | 🔴 待启动 |

### 3.3 前端

||| 任务 ID | 说明 | 优先级 | 状态 |
||| :--- | :--- | :--- | :--- |
||| TSK-V3-FE-301 | 任务详情页「工时」Tab（预估/剩余/已记录工时 + 进度条） | P0 | 🔴 待启动 |
||| TSK-V3-FE-302 | 记录工时抽屉（工时 + 日期 + 说明） | P0 | 🔴 待启动 |
||| TSK-V3-FE-303 | 任务卡片和列表显示工时进度条 | P1 | 🔴 待启动 |

---

## Phase 4 — Sub-task 增强（子任务）

> 对应 PRD Section 5：完整子任务体系。

### 4.1 数据库

||| 任务 ID | 说明 | 优先级 | 状态 |
||| :--- | :--- | :--- | :--- |
||| TSK-V3-DDL-401 | `issue` 表新增字段（parent_id/is_subtask/root_id） + 索引 | P0 | 🔴 待启动 |

### 4.2 后端 API

||| 任务 ID | 说明 | 优先级 | 状态 |
||| :--- | :--- | :--- | :--- |
||| TSK-V3-BE-401 | 子任务列表 API（GET /issue/{id}/subtasks） | P0 | 🔴 待启动 |
||| TSK-V3-BE-402 | 子任务进度汇总 API（父任务自动计算子任务完成数/总工时） | P0 | 🔴 待启动 |
||| TSK-V3-BE-403 | 子任务 Issue Type 校验（子任务类型只能作为子任务创建） | P1 | 🔴 待启动 |

### 4.3 前端

||| 任务 ID | 说明 | 优先级 | 状态 |
||| :--- | :--- | :--- | :--- |
||| TSK-V3-FE-401 | 任务详情页「子任务」Tab（列表 + 新增 + 进度汇总） | P0 | 🔴 待启动 |
||| TSK-V3-FE-402 | 子任务 Issue Type 选择（限制为子任务专用类型） | P1 | 🔴 待启动 |

---

## Phase 5 — Automation 执行引擎

> 对应 PRD Section 6：规则评估 + 动作执行 + 日志。

### 5.1 数据库

||| 任务 ID | 说明 | 优先级 | 状态 |
||| :--- | :--- | :--- | :--- |
||| TSK-V3-DDL-501 | 创建 `automation_rule` 表（project_id/name/trigger_type/trigger_config/conditions/actions/is_enabled） | P0 | 🔴 待启动 |
||| TSK-V3-DDL-502 | 创建 `automation_execution_log` 表（rule_id/trigger_event/issue_id/actions_executed/status/error_message） | P0 | 🔴 待启动 |

### 5.2 后端 API

||| 任务 ID | 说明 | 优先级 | 状态 |
||| :--- | :--- | :--- | :--- |
||| TSK-V3-BE-501 | AutomationRuleController — CRUD + 启用/禁用 | P0 | 🔴 待启动 |
||| TSK-V3-BE-502 | AutomationExecutionLogController — 执行日志查询 | P1 | 🔴 待启动 |
||| TSK-V3-BE-503 | 触发器扫描服务（定时任务扫描所有启用的规则） | P0 | 🔴 待启动 |
||| TSK-V3-BE-504 | 条件评估引擎（evaluateConditions，AND/OR 逻辑树） | P0 | 🔴 待启动 |
||| TSK-V3-BE-505 | 动作执行引擎（executeActions，支持 SET_FIELD/SEND_NOTIFICATION/CREATE_SUBTASK/LINK_ISSUE/FIRE_WEBHOOK 等） | P0 | 🔴 待启动 |
||| TSK-V3-BE-506 | 规则预览 API（显示满足条件时会影响的 Issue 列表） | P1 | 🔴 待启动 |

### 5.3 前端

||| 任务 ID | 说明 | 优先级 | 状态 |
||| :--- | :--- | :--- | :--- |
||| TSK-V3-FE-501 | 自动化规则管理页面（列表 + 新增/编辑/启用/禁用） | P0 | 🔴 待启动 |
||| TSK-V3-FE-502 | 自动化规则可视化编辑器（trigger / conditions / actions 配置） | P0 | 🔴 待启动 |
||| TSK-V3-FE-503 | 规则执行日志查看器（列表 + 详情 + 错误信息） | P1 | 🔴 待启动 |

---

## Phase 6 — 回收站

> 对应 PRD Section 7：软删除 + 恢复 + 彻底删除。

### 6.1 数据库

||| 任务 ID | 说明 | 优先级 | 状态 |
||| :--- | :--- | :--- | :--- |
||| TSK-V3-DDL-601 | `issue` 表新增字段（is_deleted/deleted_at/deleted_by） | P0 | 🔴 待启动 |

### 6.2 后端 API

||| 任务 ID | 说明 | 优先级 | 状态 |
||| :--- | :--- | :--- | :--- |
||| TSK-V3-BE-601 | 软删除逻辑（DELETE → UPDATE is_deleted=1） | P0 | 🔴 待启动 |
||| TSK-V3-BE-602 | 回收站查询 API（GET /issue/trash，按 project/date 筛选） | P0 | 🔴 待启动 |
||| TSK-V3-BE-603 | 恢复 API（UPDATE is_deleted=0，含子任务） | P0 | 🔴 待启动 |
||| TSK-V3-BE-604 | 彻底删除 API（物理删除 + 清理关联数据） | P0 | 🔴 待启动 |
||| TSK-V3-BE-605 | 自动清理（回收站超过 30 天自动物理删除，定时任务） | P2 | 🔴 待启动 |

### 6.3 前端

||| 任务 ID | 说明 | 优先级 | 状态 |
||| :--- | :--- | :--- | :--- |
||| TSK-V3-FE-601 | 回收站页面（项目级 + 全局级） | P0 | 🔴 待启动 |
||| TSK-V3-FE-602 | 恢复/彻底删除操作（带确认对话框） | P0 | 🔴 待启动 |

---

## 状态变更规范

1. **领单启动** — `🔴 待启动` → `🟡 进行中`，备注追加承接人和日期
2. **提测完成** — 测试通过后，备注补充测试结果
3. **完成合并** — `🟡 进行中` → `✅ 已完成`，备注补充 commit hash 和 PR 编号
