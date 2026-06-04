# 看板快捷筛选增强（V3.0）— 开发任务单

> **关联 PRD**：[`docs/1-prd/core/V3.0/看板快捷筛选增强.md`](../../../1-prd/core/V3.0/看板快捷筛选增强.md)
>
> **小版本**：v3.4 | **开发周期**：2027-02-06 ~ 2027-02-12
>
> **状态**：🔴 待启动 | **任务 ID 前缀**：`V34-BOARD-`

## 功能模块分解

### 1. 数据库设计

|| 任务 ID | 说明 | 工作量 | 状态 |
||---------|------|--------|------|
|| V34-BOARD-DDL-001 | `board_quick_filter` 表（id/board_id/filter_type/filter_owner_id/name/jql/color/sort_order/is_enabled） | 0.5d | 🔴 |
|| V34-BOARD-DDL-002 | `board_card_config` 表（id/board_id/card_color_rule/column_collapse/wip_limit_visual） | 0.5d | 🔴 |

### 2. 后端 API

|| 任务 ID | 说明 | 工作量 | 状态 |
||---------|------|--------|------|
|| V34-BOARD-BE-001 | Quick Filter CRUD API（创建/编辑/删除/排序） | 1d | 🔴 |
|| V34-BOARD-BE-002 | Quick Filter 执行 API（JQL 查询 → 返回过滤结果） | 1d | 🔴 |
|| V34-BOARD-BE-003 | 卡片颜色规则 API（优先级/Epic/标签/负责人/日期着色） | 1d | 🔴 |
|| V34-BOARD-BE-004 | WIP 限制 API（列级 WIP 限制检查 + 告警） | 0.5d | 🔴 |
|| V34-BOARD-BE-005 | 泳道路由 API（基于泳道字段自动分流卡片） | 0.5d | 🔴 |
|| V34-BOARD-BE-006 | 列折叠状态 API（保存用户折叠偏好） | 0.5d | 🔴 |

### 3. 前端 — Quick Filter

|| 任务 ID | 说明 | 工作量 | 状态 |
||---------|------|--------|------|
|| V34-BOARD-FE-001 | Quick Filter 按钮栏组件（横排显示 + 激活高亮） | 1d | 🔴 |
|| V34-BOARD-FE-002 | Quick Filter 筛选执行（点击 → JQL 应用 → 卡片过滤） | 1d | 🔴 |
|| V34-BOARD-FE-003 | Quick Filter 叠加逻辑（多个筛选 AND 组合） | 0.5d | 🔴 |
|| V34-BOARD-FE-004 | Quick Filter 配置弹窗（添加/编辑/删除/排序） | 1.5d | 🔴 |
|| V34-BOARD-FE-005 | 我的 Quick Filter（个人筛选保存/管理） | 1d | 🔴 |
|| V34-BOARD-FE-006 | Quick Filter 颜色选择器（8 种预设颜色） | 0.5d | 🔴 |

### 4. 前端 — 卡片颜色

|| 任务 ID | 说明 | 工作量 | 状态 |
||---------|------|--------|------|
|| V34-BOARD-FE-007 | 卡片边框颜色（按优先级着色） | 0.5d | 🔴 |
|| V34-BOARD-FE-008 | 卡片顶边颜色（按 Epic 着色） | 0.5d | 🔴 |
|| V34-BOARD-FE-009 | 卡片边框颜色（按标签着色） | 0.5d | 🔴 |
|| V34-BOARD-FE-010 | 卡片背景微调（按负责人着色） | 0.5d | 🔴 |
|| V34-BOARD-FE-011 | 卡片日期变色（延期红色 + 临近橙色） | 0.5d | 🔴 |
|| V34-BOARD-FE-012 | 卡片颜色配置面板（选择着色规则） | 1d | 🔴 |

### 5. 前端 — 其他增强

|| 任务 ID | 说明 | 工作量 | 状态 |
||---------|------|--------|------|
|| V34-BOARD-FE-013 | 子任务折叠/展开（数量徽章 + 进度条） | 1d | 🔴 |
|| V34-BOARD-FE-014 | 列折叠（点击列头折叠 + 展开） | 0.5d | 🔴 |
|| V34-BOARD-FE-015 | WIP 限制可视化（列头显示 WIP 计数 + 超限警告） | 1d | 🔴 |
|| V34-BOARD-FE-016 | 泳道功能（按 Epic/负责人/版本分组泳道） | 2d | 🔴 |
|| V34-BOARD-FE-017 | 泳道路由（卡片自动进入对应泳道） | 1d | 🔴 |
|| V34-BOARD-FE-018 | 看板设置面板（Quick Filter / 卡片颜色 / WIP / 泳道 配置入口） | 1d | 🔴 |

### 6. 测试

|| 任务 ID | 说明 | 工作量 | 状态 |
||---------|------|--------|------|
|| V34-BOARD-TEST-001 | Quick Filter 筛选准确性测试（10 种 JQL 条件验证） | 0.5d | 🔴 |
|| V34-BOARD-TEST-002 | 卡片颜色准确性测试（5 种着色规则） | 0.5d | 🔴 |
|| V34-BOARD-TEST-003 | WIP 限制超限拦截测试 | 0.5d | 🔴 |
|| V34-BOARD-TEST-004 | 泳道路由准确性测试 | 0.5d | 🔴 |

## 交付物

- [ ] Quick Filter 快捷筛选（7 种默认 + 自定义）
- [ ] 卡片颜色（5 种着色规则）
- [ ] 子任务折叠/展开
- [ ] 列折叠
- [ ] WIP 限制可视化
- [ ] 泳道功能
