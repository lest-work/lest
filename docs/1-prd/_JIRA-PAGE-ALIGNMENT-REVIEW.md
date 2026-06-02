# LEST Platform — Jira 页面级功能对齐审查报告（第二轮·修复后）

> **审查时间**：2026-06-01
> **审查视角**：Jira 产品经理（逐页面、逐字段、逐 Tab）
> **审查范围**：V1.0 / V2.0 / V3.0 全部 Core PRD
> **本次修复**：新增 Issue Link、Sidebar 增强字段、Project Settings Tab UI、Backlog 视图、Workflow 增强

---

## 一、本次修复清单

### V1.0 PRD 修复（`docs/1-prd/core/V1.0/任务管理.md`）

| # | 修复项 | 位置 |
|---|--------|------|
| 1 | **Reporter 字段** | US-001 验收标准、API 请求/响应、详情页原型 |
| 2 | **Start Date 字段** | US-001 验收标准、API 请求/响应、详情页原型 |
| 3 | **Change History Tab** | 新增 Section 3.1b（详情页字段设计）、变更历史区域、API |
| 4 | **Watch/Unwatch 操作** | Watch 按钮、关注者列表、Sidebar 关注者指示器、API |
| 5 | **Clone 操作按钮** | 详情页操作栏、API |
| 6 | **功能清单扩展** | 新增 13b 变更历史、13c 任务关注 |
| 7 | **API 编号重排** | 4.1.8~4.1.12 顺序修正 |

### V2.0 PRD 修复（`docs/1-prd/core/V2.0/任务管理.md`）

| # | 修复项 | 位置 |
|---|--------|------|
| 1 | **Issue Link** | 新增 Section 8（用户故事×4、数据库设计×2、API×3）← 从 V3.0 提前 |
| 2 | **Sidebar 增强字段** | 新增 Section 9（Epic Link / Story Points / Affects+Fix Version） |
| 3 | **Project Settings Scheme UI** | 新增 Section 10（Issue Type Scheme / Workflow Scheme / Screen Scheme / Field Config UI 原型） |
| 4 | **Backlog 视图** | 新增 Section 11（视图布局 + 用户故事×2） |
| 5 | **Workflow 增强** | 新增 Section 12（全局转换 + 循环检测 + 草稿/发布区分） |
| 6 | **章节编号重排** | #1~#17 顺序修正 |

### V2.0 项目管理 PRD 修复（`docs/1-prd/core/V2.0/项目管理.md`）

| # | 修复项 | 位置 |
|---|--------|------|
| 1 | **Project Settings Tab UI** | 新增 Section 12.3（15 个 Tab 与 Jira 对应关系） |
| 2 | **章节编号重排** | #1~#13 顺序修正 |

---

## 二、修复后各页面对齐率评估

### 2.1 Issue Detail Page

| 区域 | 字段数 | 修复前 | 修复后 |
|------|--------|--------|--------|
| Header | 7 | 6/7 (86%) | 7/7 (100%) |
| Sidebar | 16 | 7/16 (44%) | 10/16 (63%) |
| Tabs | 6 | 1/6 (17%) | 2/6 (33%) |
| Actions | 10 | 4/10 (40%) | 6/10 (60%) |
| **合计** | **39** | **18/39 (46%)** | **25/39 (64%)** |

> **关键缺口**：Affects/Fix Version、Epic Link、Story Points、Activity Tab、Attachments Tab、Work Log Tab 完整 UI、Change History Tab 完整 UI、Votes、跨项目 Linked Issues

### 2.2 Board Page

| 区域 | 字段数 | 修复前 | 修复后 |
|------|--------|--------|--------|
| Header | 5 | 2/5 (40%) | 3/5 (60%) |
| 看板列 | 6 | 2/6 (33%) | 4/6 (67%) |
| 看板卡片 | 14 | 6/14 (43%) | 8/14 (57%) |
| **合计** | **25** | **10/25 (40%)** | **15/25 (60%)** |

> **关键缺口**：WIP Limit 校验、列折叠、添加列、列头故事点汇总、Subtask 指示器、Epic Color Bar、Attachment/Comment 指示器、快速编辑

### 2.3 Project Settings

| Tab | 修复前 | 修复后 |
|-----|--------|--------|
| 所有 Scheme 配置页 | 缺 UI 原型 | ✅ 有 UI 原型 |
| Labels Tab | 缺设计 | ⚠️ 有 Tab，缺设计 |
| Notifications UI | ⚠️ 有事件，缺 UI | ⚠️ 有事件，缺 UI |
| Development Tab | 缺设计 | ⚠️ 有占位，缺设计 |
| **综合** | **~50%** | **~70%** |

### 2.4 Workflow Designer

| 元素 | 修复前 | 修复后 |
|------|--------|--------|
| 状态节点 | ✅ | ✅ |
| 流转箭头 | ✅ | ✅ |
| 条件/校验/动作编辑器 | ✅ | ✅ |
| 全局转换 | ❌ | ✅ |
| 循环检测 | ❌ | ✅ |
| 草稿/发布区分 | ❌ | ✅ |
| **综合** | **~60%** | **~90%** |

---

## 三、版本对齐率总览（修复后）

| 版本 | Issue Detail | Board | Project Settings | Workflow | Global Admin | **综合** |
|------|------------|-------|----------------|-----------|------------|---------|
| V1.0 | ~64% | ~60% | ~40% | N/A | ~70% | **~58%** |
| V2.0 | ~75% | ~70% | ~80% | ~90% | ~75% | **~78%** |
| V3.0 | ~100% | ~100% | ~100% | ~100% | ~100% | **~100%** |

> V2.0 目标：从 ~65% 提升至 **~78%**。距离 80% 目标还差 2 个百分点（Activity Tab、Attachment Tab 完整设计）

---

## 四、V3.0 收口缺口清单（按优先级）

> **说明**：以下缺口若在 V2.0 未完成，必须在 **V3.0** 收口并作为「Jira 功能完成度 100%」的验收条件。

### 🔴 P0（影响核心功能）

| # | 缺口 | 影响页面 | 建议版本 |
|---|------|---------|---------|
| 1 | **Activity Tab**（评论+活动合并流） | Issue Detail | V2.0 补充 |
| 2 | **Work Log Tab 完整 UI** | Issue Detail | V1.0 补充（已有表，缺 UI） |
| 3 | **Attachments Tab 完整 UI** | Issue Detail | V2.0 补充 |

### 🟡 P1（重要）

| # | 缺口 | 影响页面 | 建议版本 |
|---|------|---------|---------|
| 4 | **卡片 Subtask 指示器** | Board Card | V2.0 补充 |
| 5 | **卡片 Epic Color Bar** | Board Card | V2.0 补充 |
| 6 | **WIP Limit 看板列校验** | Board Column | V2.0 补充 |
| 7 | **Notifications 配置 UI** | Project Settings | V2.0 补充 |
| 8 | **Labels Tab 设计** | Project Settings | V2.0 补充 |
| 9 | **Sprint Backlog 键盘快捷键** | Board | V2.0 补充 |
| 10 | **自动化规则模板库** | Automation | V3.0 补充 |

### 🟢 P2（建议 V3.0 修复）

| # | 缺口 | 影响 |
|---|------|------|
| 11 | Automation 执行日志查看器 | V3.0 |
| 12 | Automation 规则预览（影响范围） | V3.0 |
| 13 | Development Tab（代码仓库/CI 集成） | V3.0 |
| 14 | Project Settings Wiki Tab | V3.0 |
| 15 | Automation 规则导入/导出 | V3.0 |

---

## 五、V2.0 达到 80% 目标路线图

以当前 78% 为基础，以下功能补齐后可达 80%+：

1. ✅ Activity Tab 设计（+1%）← V2.0
2. ✅ Work Log Tab 完整 UI（+1%）← V1.0/V2.0
3. ✅ Sprint Backlog 键盘快捷键（+1%）← V2.0

**达到 82%** → 满足 80% 目标
