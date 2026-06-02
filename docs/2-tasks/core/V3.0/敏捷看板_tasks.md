# 敏捷看板增强 V3.0 开发任务单

> **关联 PRD**：`../../1-prd/core/V3.0/敏捷看板增强.md`
>
> **整体状态**：🔴 待启动
>
> **前置依赖**：任务管理 V2.0 Phase 3（基础看板）完成
>
> **定位**：在 V2.0 基础看板之上，补全 Jira Software 的所有高级看板功能

---

## Phase 1 — 泳道（Swimlane）

> 对应 PRD Section 2：按泳道分组显示任务。

### 1.1 数据库

||| 任务 ID | 说明 | 优先级 | 状态 |
||| :--- | :--- | :--- | :--- |
||| BRD-V3-DDL-101 | `board_swimlane` 表新增字段（group_by/custom_field_id） | P0 | 🔴 待启动 |

### 1.2 后端 API

||| 任务 ID | 说明 | 优先级 | 状态 |
||| :--- | :--- | :--- | :--- |
||| BRD-V3-BE-101 | 泳道分组查询 API（GET /board/{id}/swimlane） | P0 | 🔴 待启动 |

### 1.3 前端

||| 任务 ID | 说明 | 优先级 | 状态 |
||| :--- | :--- | :--- | :--- |
||| BRD-V3-FE-101 | 泳道切换控件（支持 assignee/epic/label/version/custom_field） | P0 | 🔴 待启动 |
||| BRD-V3-FE-102 | 泳道行渲染（可折叠/展开泳道） | P0 | 🔴 待启动 |

---

## Phase 2 — WIP 限制

> 对应 PRD Section 3：限制每列最大任务数。

### 2.1 数据库

||| 任务 ID | 说明 | 优先级 | 状态 |
||| :--- | :--- | :--- | :--- |
||| BRD-V3-DDL-201 | `board_column_wip` 表（column_id/wip_limit） | P0 | 🔴 待启动 |

### 2.2 后端 API

||| 任务 ID | 说明 | 优先级 | 状态 |
||| :--- | :--- | :--- | :--- |
||| BRD-V3-BE-201 | WIP 限制配置 API | P0 | 🔴 待启动 |
||| BRD-V3-BE-202 | 拖拽时 WIP 限制校验 API（拒绝超限的拖拽） | P0 | 🔴 待启动 |

### 2.3 前端

||| 任务 ID | 说明 | 优先级 | 状态 |
||| :--- | :--- | :--- | :--- |
||| BRD-V3-FE-201 | 列头 WIP 警告样式（超限时列头变红） | P0 | 🔴 待启动 |
||| BRD-V3-FE-202 | 超限拖拽提示弹窗 | P0 | 🔴 待启动 |

---

## Phase 3 — 燃尽图（Burndown Chart）

> 对应 PRD Section 4：冲刺进度可视化。

### 3.1 数据库

||| 任务 ID | 说明 | 优先级 | 状态 |
||| :--- | :--- | :--- | :--- |
||| BRD-V3-DDL-301 | `sprint_burndown` 表（sprint_id/record_date/total_points/remaining_points/total_count/remaining_count） | P0 | 🔴 待启动 |

### 3.2 后端 API

||| 任务 ID | 说明 | 优先级 | 状态 |
||| :--- | :--- | :--- | :--- |
||| BRD-V3-BE-301 | 燃尽图数据 API（GET /sprint/{id}/burndown，按日期返回故事点/任务数） | P0 | 🔴 待启动 |
||| BRD-V3-BE-302 | 每日快照定时任务（每天凌晨计算并写入 sprint_burndown） | P0 | 🔴 待启动 |

### 3.3 前端

||| 任务 ID | 说明 | 优先级 | 状态 |
||| :--- | :--- | :--- | :--- |
||| BRD-V3-FE-301 | 燃尽图 ECharts 组件（理想线 + 实际线） | P0 | 🔴 待启动 |
||| BRD-V3-FE-302 | 冲刺详情页集成燃尽图 | P0 | 🔴 待启动 |
||| BRD-V3-FE-303 | 导出为图片功能 | P2 | 🔴 待启动 |

---

## Phase 4 — 累积流图（CFD）

> 对应 PRD Section 5：工作流吞吐可视化。

### 4.1 后端 API

||| 任务 ID | 说明 | 优先级 | 状态 |
||| :--- | :--- | :--- | :--- |
||| BRD-V3-BE-401 | CFD 数据 API（GET /sprint/{id}/cfd，按日期/状态返回在制品数量） | P0 | 🔴 待启动 |

### 4.2 前端

||| 任务 ID | 说明 | 优先级 | 状态 |
||| :--- | :--- | :--- | :--- |
||| BRD-V3-FE-401 | CFD ECharts 堆叠面积图 | P0 | 🔴 待启动 |
||| BRD-V3-FE-402 | 冲刺选择器和时间范围控件 | P0 | 🔴 待启动 |

---

## Phase 5 — 速度图 + 控制图 + 团队容量 + 史诗进度

> 对应 PRD Section 6~9。

### 5.1 数据库

||| 任务 ID | 说明 | 优先级 | 状态 |
||| :--- | :--- | :--- | :--- |
||| BRD-V3-DDL-501 | `team_capacity` 表（sprint_id/user_id/hours_per_day/working_days/total_hours） | P0 | 🔴 待启动 |

### 5.2 后端 API

||| 任务 ID | 说明 | 优先级 | 状态 |
||| :--- | :--- | :--- | :--- |
||| BRD-V3-BE-501 | 速度图数据 API（GET /project/{id}/velocity，按冲刺返回完成故事点） | P0 | 🔴 待启动 |
||| BRD-V3-BE-502 | 控制图数据 API（GET /project/{id}/control-chart，按完成日期返回周期时间） | P1 | 🔴 待启动 |
||| BRD-V3-BE-503 | 团队容量管理 API（GET/PUT /sprint/{id}/capacity） | P0 | 🔴 待启动 |
||| BRD-V3-BE-504 | 史诗进度 API（GET /epic/{id}/progress，返回子任务数/已完成数/总工时/已完成工时） | P0 | 🔴 待启动 |

### 5.3 前端

||| 任务 ID | 说明 | 优先级 | 状态 |
||| :--- | :--- | :--- | :--- |
||| BRD-V3-FE-501 | 速度图 ECharts 柱状图（已完成 vs 承诺） | P0 | 🔴 待启动 |
||| BRD-V3-FE-502 | 控制图 ECharts 散点图（平均线 + 标准差） | P1 | 🔴 待启动 |
||| BRD-V3-FE-503 | 团队容量配置面板 | P0 | 🔴 待启动 |
||| BRD-V3-FE-504 | 史诗详情页进度条组件 | P0 | 🔴 待启动 |

---

## 状态变更规范

1. **领单启动** — `🔴 待启动` → `🟡 进行中`，备注追加承接人和日期
2. **提测完成** — 测试通过后，备注补充测试结果
3. **完成合并** — `🟡 进行中` → `✅ 已完成`，备注补充 commit hash 和 PR 编号
