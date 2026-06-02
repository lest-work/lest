# Core 核心 -- V3.0

> **Core V3.0** 在 V2.0 基础上完成 Jira 100% 功能对齐，补全 V2.0 遗留的所有核心功能。

## Core V3.0 定位

V2.0 完成了 Scheme 架构体系（约 90% Jira 功能对齐），但仍存在两类“收口缺口”：

1) **缺失能力**（缺用户故事/缺 API/缺 UI/缺实现）
- Issue Link（任务关联）完整用户故事
- Attachment（附件管理）完整用户故事
- Time Tracking（时间追踪）完整体系
- Automation 执行引擎
- 高级看板（泳道、WIP 限制、燃尽图）
- 史诗冲刺规划（Epic + Sprint 联动）
- 高级搜索与过滤器增强
- 回收站与操作历史

2) **已设计但未完全落地**（V2.0 PRD 已覆盖，V3.0 需实现并验收）
- 批量操作（Batch Operations）
- 投票/分享（Vote / Share）
- 键盘快捷键（Shortcuts）
- 自动化规则模板库（Automation Rule Templates）

## Core V3.0 模块

| 文档 | Jira 映射 | 说明 | Jira 对齐率 | 状态 |
|------|---------|------|-----------|------|
| [任务管理 V3.0](./任务管理.md) | Jira Issue + Automation | **Jira 100% 收口**：Issue Link、附件、时间追踪、子任务增强、自动化执行引擎、批量操作、投票/分享、快捷键、回收站 | ✅ PRD 已完成 |
| [项目管理 V3.0](./项目管理.md) | Jira Project Settings | **项目管理增强**：Issue Security Level 完善、跨项目标签、项目克隆、分组管理 | ✅ PRD 已完成 |
| [敏捷看板增强](./敏捷看板增强.md) | Jira Board + Reports | **高级看板**：泳道、WIP 限制、燃尽图、CFD、Velocity、Control Chart | ✅ PRD 已完成 |
| [仪表盘与日历视图](./仪表盘与日历视图.md) | Jira Dashboard + Calendar | **仪表盘与 Gadget**：个人仪表盘 + 16 种 Gadget、日历视图、过滤器订阅 | ✅ PRD 已完成 |
| [看板快捷筛选增强](./看板快捷筛选增强.md) | Jira Quick Filters | **快捷筛选**：泳道路由、WIP 可视化、卡片颜色、子任务折叠、列折叠 | ✅ PRD 已完成 |

## 开发任务单

| PRD 文档 | 开发任务单 | 状态 |
|---------|---------|------|
| [任务管理 V3.0](./任务管理.md) | [任务管理_tasks.md](../../../2-tasks/core/V3.0/任务管理_tasks.md) | ✅ PRD 已完成 |
| [敏捷看板增强](./敏捷看板增强.md) | [敏捷看板_tasks.md](../../../2-tasks/core/V3.0/敏捷看板_tasks.md) | ✅ PRD 已完成 |

| [仪表盘与日历视图](./仪表盘与日历视图.md) | ⚠️ 待创建 | ✅ PRD 已完成 |
| [看板快捷筛选增强](./看板快捷筛选增强.md) | ⚠️ 待创建 | ✅ PRD 已完成 |

> 部分新增 PRD 尚未拆分开发任务单，详见 [2-tasks/README.md](./../../../2-tasks/README.md)

## V2.0 vs V3.0 功能补全对比

| 功能维度 | V2.0 已实现 | V3.0 新增 | Jira 对齐率 |
|---------|-----------|---------|-----------|
| Issue Link | 数据结构，缺完整用户故事 | 完整 8 种链接类型 + 批量管理 | **100%** |
| Attachment | 无 | 完整上传/下载/预览/版本管理 | **100%** |
| Time Tracking | 无 | 预估/剩余/记录工时/进度计算 | **100%** |
| Sub-task | 基本父子关系 | 完整子任务体系 + subtask 专用 Issue Type | **100%** |
| Automation | 数据结构 | **执行引擎**（规则评估 + 动作执行 + 日志） | **100%** |
| Board | 基础看板 | 泳道、WIP 限制、燃尽图 | **100%** |
| Reports | 无 | Burndown/CFD/Velocity/Control Chart | **100%** |
| 回收站 | 无 | 软删除 + 恢复 + 彻底删除 | **100%** |

> **V3.0 整体 Jira 功能对齐率**：**100%（收口验收口径）**。V4.0/V5.0 聚焦 AI 原生与多租户等“超越 Jira”的增强能力。
