# 仪表盘与日历视图（V3.0）— 开发任务单

> **关联 PRD**：[`docs/1-prd/core/V3.0/仪表盘与日历视图.md`](../../../1-prd/core/V3.0/仪表盘与日历视图.md)
>
> **小版本**：v3.3 ~ v3.4 | **开发周期**：2027-01-30 ~ 2027-02-12
>
> **状态**：🔴 待启动 | **任务 ID 前缀**：`V33-DB-`（仪表盘）/ `V33-CAL-`（日历）/ `V34-DB-`（暗黑模式/i18n）

## 功能模块分解

### 1. 数据库设计

|| 任务 ID | 说明 | 工作量 | 状态 |
||---------|------|--------|------|
|| V33-DB-DDL-001 | `dashboard` 表（id/type/owner_id/project_id/name/is_default/layout_config） | 0.5d | 🔴 |
|| V33-DB-DDL-002 | `dashboard_gadget` 表（id/dashboard_id/gadget_type/title/position_x/y/width/height/config/refresh_interval） | 0.5d | 🔴 |
|| V33-DB-DDL-003 | `calendar_event` 表（id/type/source_type/source_id/title/start_date/end_date/all_day/owner_id） | 0.5d | 🔴 |
|| V33-DB-DDL-004 | `dashboard_share` 表（id/dashboard_id/shared_user_id/shared_project_id） | 0.5d | 🔴 |

### 2. 后端 API

|| 任务 ID | 说明 | 工作量 | 状态 |
||---------|------|--------|------|
|| V33-DB-BE-001 | Dashboard CRUD API（创建/编辑/删除/复制/设为默认） | 1d | 🔴 |
|| V33-DB-BE-002 | Gadget 布局 API（添加/移除/调整位置和大小） | 1d | 🔴 |
|| V33-DB-BE-003 | Gadget 数据 API（15 种 Gadget 的数据查询接口） | 3d | 🔴 |
|| V33-DB-BE-004 | Dashboard 分享 API（分享给用户/项目） | 0.5d | 🔴 |
|| V33-DB-BE-005 | 仪表盘聚合 API（统计已用 Gadget 数/访问次数） | 0.5d | 🔴 |
|| V33-DB-BE-006 | 日历事件 API（CRUD + 与任务/会议联动） | 1.5d | 🔴 |
|| V33-DB-BE-007 | 日历视图数据 API（按月/周/日查询事件） | 1d | 🔴 |
|| V33-DB-BE-008 | 过滤器订阅 API（保存的 JQL 定期推送通知） | 1d | 🔴 |

### 3. 前端页面

|| 任务 ID | 说明 | 工作量 | 状态 |
||---------|------|--------|------|
|| V33-DB-FE-001 | 仪表盘首页（默认布局 + 拖拽 Gadget 网格） | 2d | 🔴 |
|| V33-DB-FE-002 | 添加 Gadget 弹窗（目录浏览 + 搜索 + 分类） | 1.5d | 🔴 |
|| V33-DB-FE-003 | Gadget 配置面板（各类型 Gadget 的参数配置） | 1.5d | 🔴 |
|| V33-DB-FE-004 | Gadget 渲染引擎（统一渲染 15 种 Gadget 类型） | 2d | 🔴 |
|| V33-DB-FE-005 | 仪表盘管理（创建/复制/删除/重命名） | 1d | 🔴 |
|| V33-DB-FE-006 | 仪表盘分享页面 | 0.5d | 🔴 |
|| V33-DB-FE-007 | 日历视图页面（月视图/周视图/日视图切换） | 2d | 🔴 |
|| V33-DB-FE-008 | 日历事件弹窗（创建/编辑事件，关联任务/会议） | 1.5d | 🔴 |
|| V33-DB-FE-009 | 过滤器订阅配置（保存 JQL + 选择推送频率和渠道） | 1d | 🔴 |
|| V34-DB-FE-010 | i18n 国际化（英文/简体中文/繁体中文支持） | 1.5d | 🔴 |
|| V34-DB-FE-011 | 暗黑模式（主题切换 + 跟随系统） | 1d | 🔴 |

### 4. 15 种 Gadget 实现

|| 任务 ID | 说明 | 工作量 | 状态 |
||---------|------|--------|------|
|| V33-DB-FE-G01 | 我的任务 Gadget（当前用户负责的任务列表） | 1d | 🔴 |
|| V33-DB-FE-G02 | 需要我处理 Gadget（待办/进行中任务） | 0.5d | 🔴 |
|| V33-DB-FE-G03 | 即将到期 Gadget（截止日期 7 天内） | 0.5d | 🔴 |
|| V33-DB-FE-G04 | 最近更新 Gadget（最近变更的任务） | 0.5d | 🔴 |
|| V33-DB-FE-G05 | Sprint 进度 Gadget（当前 Sprint 燃尽图） | 1d | 🔴 |
|| V33-DB-FE-G06 | 项目统计 Gadget（总任务数/完成率） | 0.5d | 🔴 |
|| V33-DB-FE-G07 | 团队活动 Gadget（团队最近操作动态） | 0.5d | 🔴 |
|| V33-DB-FE-G08 | 燃尽图 Gadget（Sprint 燃尽图） | 1d | 🔴 |
|| V33-DB-FE-G09 | 速度图 Gadget（最近 Sprint 速度） | 0.5d | 🔴 |
|| V33-DB-FE-G10 | 史诗进度 Gadget（Epic 完成百分比） | 0.5d | 🔴 |
|| V33-DB-FE-G11 | 代码提交 Gadget（最近 Git 提交） | 0.5d | 🔴 |
|| V33-DB-FE-G12 | CI 构建状态 Gadget（最近构建结果） | 0.5d | 🔴 |
|| V33-DB-FE-G13 | 快捷链接 Gadget（自定义链接入口） | 0.5d | 🔴 |
|| V33-DB-FE-G14 | 统计数字 Gadget（自定义数字指标） | 0.5d | 🔴 |
|| V33-DB-FE-G15 | 筛选结果 Gadget（嵌入保存的过滤器） | 0.5d | 🔴 |

### 5. 测试

|| 任务 ID | 说明 | 工作量 | 状态 |
||---------|------|--------|------|
|| V33-DB-TEST-001 | 仪表盘 CRUD + 布局拖拽测试 | 1d | 🔴 |
|| V33-DB-TEST-002 | 15 种 Gadget 数据准确性测试 | 1d | 🔴 |
|| V33-DB-TEST-003 | 日历视图（月/周/日）渲染测试 | 0.5d | 🔴 |
|| V33-DB-TEST-004 | 过滤器订阅通知测试 | 0.5d | 🔴 |

## 交付物

- [ ] 仪表盘首页（拖拽布局）
- [ ] 15 种 Gadget 完整实现
- [ ] 日历视图（月/周/日）
- [ ] 过滤器订阅
- [ ] i18n + 暗黑模式
