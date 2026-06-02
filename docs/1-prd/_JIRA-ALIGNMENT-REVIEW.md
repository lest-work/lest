# LEST Platform — Jira 功能对齐审查报告

> **审查时间**：2026-06-01
> **审查视角**：Jira 产品经理 + Jira 设计师 + Jira 架构师
> **审查范围**：V1.0 / V2.0 / V3.0 全部 Core PRD + Plugin PRD

---

## 1. Jira 功能清单总表

以下为 LEST Platform 需要对标的完整 Jira 功能体系（按模块分组）：

### 1.1 Issue Management（任务管理）

| # | 功能 | Jira 标准行为 | 所在版本 | 对齐状态 | 备注 |
|---|------|-------------|---------|---------|------|
| IM-01 | Issue CRUD | 创建/编辑/删除/克隆 | V1.0 | ✅ 已实现 | 需核对表结构 |
| IM-02 | Issue Type | Bug/Story/Task/Epic，子任务类型 | V2.0 | ✅ Scheme 完整 | 缺 V1.0 用户故事 |
| IM-03 | 自定义字段 | 25 种字段类型，EAV 存储 | V2.0 | ✅ 完整 | — |
| IM-04 | 字段上下文 | 全局/项目/模板三级上下文 | V2.0 | ✅ 完整 | — |
| IM-05 | Screen / Screen Scheme | create/edit/view/subtask_create/transition | V2.0 | ✅ 完整 | — |
| IM-06 | Field Configuration | 隐藏/必填/只读/标签/默认值 | V2.0 | ✅ 完整 | — |
| IM-07 | 工作流引擎 | 状态流转 + 条件 + 校验 + 动作 | V2.0 | ✅ 完整 | — |
| IM-08 | Issue Link | 8 种链接类型（blocks/relates_to 等） | V3.0 | ✅ 已规划 | — |
| IM-09 | Attachment | 上传/下载/预览/版本管理 | V3.0 | ✅ 已规划 | — |
| IM-10 | Comment（评论） | 列表/新增/编辑/删除/富文本 | V1.0 | ✅ US-027~030 已补充 | — |
| IM-11 | Watch（关注） | 关注/取消关注/通知开关 | V1.0 | ✅ US-031~032 + API 已补充 | — |
| IM-12 | Vote（投票） | 点赞，支持/反对 | V3.0 | ✅ 已规划 | 缺用户故事 |
| IM-13 | Share（分享） | 分享链接，权限控制 | V3.0 | ⚠️ 仅表结构 | 缺用户故事 |
| IM-14 | Time Tracking | 预估/剩余/已记录工时 | V3.0 | ✅ 已规划 | — |
| IM-15 | Sub-task | 父子任务，子任务专用 Issue Type | V3.0 | ✅ 已规划 | — |
| IM-16 | Issue Security Level | 安全级别控制可见性 | V2.0 | ✅ Section 7 完整设计 | — |
| IM-17 | Issue Resolution | 解决结果（Fixed/Duplicate/Won't Fix） | V2.0 | ✅ 表中有字段 | 缺 UI 设计 |
| IM-18 | Epic Link | 故事关联史诗 | V2.0 | ✅ Section 10 字段设计 | — |
| IM-19 | Story Points | 故事点估算 | V2.0 | ✅ Section 10 字段设计 | — |
| IM-20 | Sprint Field | 迭代选择 | V2.0 | ✅ 表中有字段 | 缺字段设计 |
| IM-21 | JQL 查询 | 高级搜索，支持全部操作符 | V2.0 | ⚠️ 部分实现 | 缺完整操作符列表 |
| IM-22 | 回收站 | 软删除/恢复/彻底删除 | V3.0 | ✅ 已规划 | — |
| IM-23 | 快捷键 | 全局 + 上下文快捷键 | V3.0 | ⚠️ 仅标题提及 | 缺用户故事和 UI |
| IM-24 | 克隆 Issue | 复制完整 Issue（含子任务） | V1.0 | ✅ US-033 + API 4.1.12 已补充 | — |
| IM-25 | 批量操作 | 批量编辑/移动/删除 | V2.0 | ✅ Section 9 完整设计 | — |

### 1.2 Project Administration（项目管理）

| # | 功能 | Jira 标准行为 | 所在版本 | 对齐状态 | 备注 |
|---|------|-------------|---------|---------|------|
| PRJ-01 | Project CRUD | 创建/编辑/删除/归档 | V1.0 | ✅ 已实现 | — |
| PRJ-02 | Project Members | 成员管理，角色（admin/dev/viewer） | V1.0 | ✅ 已实现 | — |
| PRJ-03 | Project Scheme | Issue Type/Workflow/Screen/Field Config Scheme | V2.0 | ✅ 完整 | — |
| PRJ-04 | Permission Scheme | 细粒度权限控制 | V2.0 | ✅ 完整 | — |
| PRJ-05 | Notification Scheme | 通知规则配置 | V2.0 | ✅ 完整 | — |
| PRJ-06 | Priority Scheme | 优先级集合 | V2.0 | ✅ 完整 | — |
| PRJ-07 | Project Template | 从模板创建，复制 Scheme | V2.0 | ✅ 完整 | — |
| PRJ-08 | Project Category | 项目分类（部门维度） | V2.0 | ✅ 完整 | — |
| PRJ-09 | Project Component | 子模块管理 | V1.0 | ✅ 已实现 | — |
| PRJ-10 | Project Version | 版本/里程碑 | V1.0 | ✅ 已实现 | — |
| PRJ-11 | Project Iteration | Sprint 迭代 | V1.0 | ✅ 已实现 | — |
| PRJ-12 | Project Insights | 统计数据（创建趋势/完成率） | V2.0 | ✅ Section 12 完整设计 | — |
| PRJ-13 | Project Avatar/Color | 项目图标和颜色 | V2.0 | ✅ Section 12.1 功能开关中有 | — |
| PRJ-14 | Project Settings UI | Scheme 配置界面 | V2.0 | ✅ Section 11 Scheme UI 原型 | — |
| PRJ-15 | Project Workflow Override | 工作流覆盖特定 Issue Type | V2.0 | ✅ Scheme 有覆盖机制 | — |

### 1.3 Agile Board（敏捷看板）

| # | 功能 | Jira 标准行为 | 所在版本 | 对齐状态 | 备注 |
|---|------|-------------|---------|---------|------|
| BD-01 | 看板视图 | 按状态列显示 Issue | V1.0 | ✅ 已实现 | — |
| BD-02 | 动态列 | 基于工作流状态自动生成列 | V2.0 | ✅ 已规划 | — |
| BD-03 | 泳道（Swimlane） | 按负责人/史诗分组 | V3.0 | ✅ 已规划 | — |
| BD-04 | WIP 限制 | 列最大任务数限制 | V3.0 | ✅ 已规划 | — |
| BD-05 | 燃尽图 | Sprint Burndown Chart | V3.0 | ✅ 已规划 | — |
| BD-06 | 累积流图（CFD） | Cumulative Flow Diagram | V3.0 | ✅ 已规划 | — |
| BD-07 | 速度图 | Velocity Chart | V3.0 | ✅ 已规划 | — |
| BD-08 | 控制图 | Control Chart | V3.0 | ✅ 已规划 | — |
| BD-09 | 团队容量 | Sprint 容量规划 | V3.0 | ✅ 已规划 | — |
| BD-10 | 史诗进度 | Epic Progress Bar | V3.0 | ✅ 已规划 | — |
| BD-11 | 看板卡片配置 | 自定义卡片显示字段 | V2.0 | ⚠️ 缺 UI 设计 | — |
| BD-12 | 快速过滤 | 看板内置过滤器 | V1.0 | ⚠️ 基础筛选 | 缺高级过滤 |
| BD-13 | 估算故事点汇总 | 看板列头显示故事点总数 | V2.0 | ✅ Section 12 Backlog 视图中有 | — |

### 1.4 Automation（自动化）

| # | 功能 | Jira 标准行为 | 所在版本 | 对齐状态 | 备注 |
|---|------|-------------|---------|---------|------|
| AUTO-01 | Automation Rule 数据结构 | trigger/condition/action 配置 | V2.0 | ✅ 完整 | — |
| AUTO-02 | Automation 执行引擎 | 规则评估 + 动作执行 + 日志 | V3.0 | ✅ 已规划 | — |
| AUTO-03 | 规则模板库 | 预设规则模板 | V3.0 | ❌ **缺失** | — |
| AUTO-04 | 规则执行日志 | 每次执行记录 | V3.0 | ✅ 已规划 | — |
| AUTO-05 | 规则预览 | 显示规则会影响的 Issue | V3.0 | ✅ 已规划 | — |

---

## 2. 重大缺口详解

### 2.1 Comment（评论）— ⚠️ 表存在，缺用户故事

**现状**：
- V1.0 `task_comment` 表已存在（DDL 完成）
- V1.0 后端 API `TSK-TASK-API-210` 提到了评论查询
- **但是**：任务管理 PRD 没有任何 Comment 的用户故事、表结构说明、API 设计或 UI 设计

**Jira 标准行为**：
- 任务详情页有专门的「评论」Tab
- 支持富文本评论（Markdown/At 人）
- 支持评论编辑和删除
- 支持评论 @ 提及用户
- 评论变更触发通知

**修复方案**：
- V1.0 PRD 补充 Comment 用户故事（US-TASK-CMT-001 ~ 005）
- V3.0 PRD 补充评论增强（@ 提及、编辑历史、Markdown 支持）

### 2.2 Issue Clone（克隆）— ❌ 完全缺失

**Jira 标准行为**：点击「克隆」复制一个 Issue，包括：
- 标题/描述（可修改）
- 子任务（可选）
- 附件（可选）
- 自定义字段值

**修复方案**：V1.0 PRD 补充 Clone 用户故事

### 2.3 Issue Security Level（安全级别）— ❌ PRD 提及，代码缺失

**现状**：
- V2.0 任务管理 PRD 的 summary 提到了 Issue Security
- 但 Section 3 数据库设计中**没有** `issue_security_level` 表
- 没有 API 设计
- 没有 UI 设计

**Jira 标准行为**：
- 项目可配置 Issue Security Scheme
- 每个 Issue 可设置安全级别（Private / Project Role）
- 只有满足安全级别的用户可见 Issue

**修复方案**：V2.0 PRD 补充完整 Issue Security Level 设计

### 2.4 批量操作 — ❌ 完全缺失

**Jira 标准行为**：选中多个 Issue 后批量：
- 修改状态/负责人/优先级
- 移动到其他项目
- 添加标签
- 克隆

**修复方案**：V2.0 PRD 补充批量操作设计

### 2.5 Project Insights — ❌ 完全缺失

**Jira 标准行为**：
- 创建趋势图（每天/每周新增 Issue 数）
- 完成率统计
- 平均周期时间
- 吞吐量趋势

**修复方案**：V3.0 PRD 补充 Project Insights

### 2.6 Project Avatar/Color — ❌ 完全缺失

**Jira 标准行为**：每个项目有专属图标和背景色

**修复方案**：V2.0 PRD 补充

---

## 3. V1.0 PRD 缺口（与代码实现对照）

| 功能 | 代码状态 | PRD 状态 | 差距 |
|------|---------|---------|------|
| Comment | `task_comment` 表存在 | ❌ 无用户故事 | **需补充** |
| Watch | `task_watcher` 表存在 | ⚠️ 仅 DDL 提及 | **需补充** |
| Attachment | ❌ 表不存在 | ❌ 无设计 | V3.0 补充 |
| Issue Clone | ❌ 无实现 | ❌ 无设计 | V1.0 补充 |
| 批量操作 | ❌ 无实现 | ❌ 无设计 | V2.0 补充 |
| 看板故事点汇总 | ❌ 无实现 | ❌ 无设计 | V2.0 补充 |

---

## 4. 各版本 Jira 对齐率更新

### 更新后的对齐率

| 版本 | 模块 | 原对齐率 | 更新后对齐率 | 变化原因 |
|------|------|---------|------------|---------|
| V1.0 | 任务管理 | ~40% | **~45%** | 补充 Comment + Watch + Clone 后可达 |
| V2.0 | 任务管理 | 90% | **~88%** | 去除 Issue Security + 批量操作后略有下降 |
| V2.0 | 项目管理 | 97% | **~93%** | 发现 Project Insights + Avatar 缺失 |
| V3.0 | 任务管理 | 95% | **100%（收口目标）** | V3.0 收口：补齐/落地 Vote/Share、快捷键、批量操作、Automation 模板库等缺口 |
| V3.0 | 敏捷看板 | 100% | **~100%** | 基本不变 |

### 各版本需要补充的功能

| 版本 | 需补充/修复的功能 | 优先级 |
|------|-----------------|--------|
| **V1.0** | Comment 用户故事 + API 设计 | P0 |
| **V1.0** | Watch 用户故事 + API 设计 | P1 |
| **V1.0** | Issue Clone 用户故事 | P1 |
| **V2.0** | Issue Security Level 完整设计（表+API+UI） | P1 |
| **V2.0** | 批量操作设计 | P2 |
| **V2.0** | 看板卡片配置 + 故事点汇总 | P2 |
| **V2.0** | Project Avatar/Color 设计 | P2 |
| **V2.0** | Project Insights 设计 | P2 |
| **V3.0** | Vote/Share 用户故事 + 实现验收 | P1 |
| **V3.0** | 快捷键完整设计 + 实现验收 | P2 |
| **V3.0** | Automation 规则模板库（模板 CRUD / 导入导出 / 分类） | P2 |
| **V3.0** | 批量操作完整落地（UI + API + 审计） | P2 |

---

## 5. 修复行动计划

### 5.1 V1.0 PRD 紧急补充

需在 V1.0 PRD 中补充以下用户故事（不影响 V2.0 架构，但需要代码实现）：

```
US-TASK-CMT-001: 查看评论列表
US-TASK-CMT-002: 新增评论
US-TASK-CMT-003: 编辑评论
US-TASK-CMT-004: 删除评论
US-TASK-CMT-005: @ 提及用户
---
US-TASK-WATCH-001: 关注任务
US-TASK-WATCH-002: 取消关注
US-TASK-WATCH-003: 查看关注列表
---
US-TASK-CLONE-001: 克隆任务
```

### 5.2 V2.0 PRD 补充设计

需补充以下章节到 V2.0 任务管理 PRD：
1. **Section XX: Issue Security Level** — 安全级别表 + API + UI
2. **Section YY: 批量操作** — 批量编辑/移动/删除

需补充以下章节到 V2.0 项目管理 PRD：
1. **Section ZZ: Project Insights** — 项目统计数据
2. **Section WW: Project Avatar/Color** — 项目图标和颜色

### 5.3 V3.0 PRD 补充设计

需补充：
1. Vote（投票）完整用户故事
2. Share（分享）完整用户故事
3. 快捷键完整设计
4. Automation 规则模板库（模板 + 导入/导出）
5. 批量操作完整链路（列表多选 → 预览 → 执行 → 审计）

---

## 6. 结论

LEST Platform 的 Jira 对齐工作整体方向正确，V2.0 的 Scheme 架构设计非常完整，达到了 Jira 架构的 90%+ 水平。但存在以下需要立即修复的问题：

1. **已修复**：V1.0 Comment/Watch/Clone 用户故事已补充（US-027~034），API 设计完成
2. **已修复**：V2.0 Issue Security Level 完整设计（Section 7），Issue Link 从 V3.0 提前至 V2.0（Section 8），Project Settings Tab UI 补充（Section 11）
3. **已修复**：Project Insights 完整设计（Section 12），Project Avatar/Color 在功能开关中体现，Backlog 视图（Section 12），Workflow 全局转换+循环检测+草稿发布（Section 13）
4. **V3.0 收口项**：Vote/Share、快捷键、批量操作、Automation 模板库等必须在 V3.0 完成并作为「Jira 功能完成度 100%」验收条件。

**目标**：V3.0 版本结束后，Core 侧 Jira 功能对齐达到 **100%**（以本报告的 Jira 功能清单为准）。

**建议**：按本文档的修复行动计划逐一补充，确保每个功能有完整的「用户故事 → 表结构 → API 设计 → UI 设计」四层设计。
