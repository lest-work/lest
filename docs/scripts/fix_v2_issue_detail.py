#!/usr/bin/env python3
"""Fix V2.0 PRD: Add missing Jira-aligned fields and pages."""

import re

filepath = '/Users/liuyue/code/lest-platform/docs/1-prd/core/V2.0/任务管理.md'
with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

changes = 0

# 1. Add Issue Link section BEFORE the migration section (line 1197)
marker = '## 7. 与 V1.0 的迁移策略\n'
issue_link_section = '''## 7. Issue Link（任务关联）← V2.0 新增

> **Jira 对应**：Issue Links。Issue Link 是 Jira Issue Detail 页面 Sidebar 的核心功能，允许任务之间建立多种关系（blocks、relates to、duplicates 等）。

### 7.1 功能说明

任务关联用于表达任务之间的关系，例如：
- **Blocks**（阻塞）：A 阻塞 B，A 完成前 B 不能完成
- **Relates to**（关联）：A 与 B 有关联关系
- **Duplicates**（重复）：A 重复 B，关闭 A
- **Is duplicated by**（被重复）：A 被 B 重复
- **Cloners**（克隆来源）：A 是 B 的克隆源
- **Is cloned by**（被克隆）：A 是 B 的克隆
- **Causes**（导致）：A 导致 B 发生
- **Caused by**（被导致）：A 被 B 导致

### 7.2 用户故事

#### US-IL-001: 查看任务关联列表
**作为** 团队成员，**我希望** 在任务详情页查看该任务的所有关联，**以便** 了解任务的依赖关系。

验收标准：
- [ ] Sidebar 显示关联任务数量
- [ ] 展开后显示所有关联（类型 + 任务 Key + 标题）
- [ ] 点击跳转到关联任务详情
- [ ] 支持按关联类型筛选

#### US-IL-002: 创建任务关联
**作为** 任务负责人，**我希望** 将当前任务与其他任务建立关联，**以便** 表达任务间的关系。

验收标准：
- [ ] 点击「创建关联」弹出选择器
- [ ] 选择关联类型（Blocks/Relates to 等 8 种）
- [ ] 搜索并选择目标任务
- [ ] 保存后自动更新双方任务的关联列表
- [ ] 关联操作记录到变更历史

#### US-IL-003: 删除任务关联
**作为** 任务负责人，**我希望** 删除不需要的关联，**以便** 清理错误的关系。

验收标准：
- [ ] 在关联列表中点击删除按钮
- [ ] 确认后删除关联（双向同步删除）
- [ ] 删除操作记录到变更历史

#### US-IL-004: 跨项目任务关联
**作为** 项目经理，**我希望** 在不同项目间建立任务关联，**以便** 表达跨项目的依赖。

验收标准：
- [ ] 关联选择器支持跨项目搜索
- [ ] 跨项目关联在双方均显示
- [ ] 无权限的项目不显示内部信息

### 7.3 数据库设计

#### 7.3.1 `issue_link_type` — 关联类型定义

|| 字段 | 类型 | 说明 |
||------|------|------|
| `link_type_id` | BIGINT PK | 主键 |
| `name` | VARCHAR(64) | 关联类型名称：Blocks |
| `inward` | VARCHAR(64) | 正向描述：is blocked by |
| `outward` | VARCHAR(64) | 反向描述：blocks |
| `is_system` | TINYINT | 1=系统内置 |
| `sort` | INT | 排序 |

> Jira 默认 8 种关联类型，均为 is_system=1。

#### 7.3.2 `issue_link` — 关联关系表

|| 字段 | 类型 | 说明 |
||------|------|------|
| `link_id` | BIGINT PK | 主键 |
| `source_issue_id` | BIGINT FK | 源任务 ID |
| `target_issue_id` | BIGINT FK | 目标任务 ID |
| `link_type_id` | BIGINT FK | 关联类型 ID |
| `created_by` | BIGINT | 创建人 |
| `created_at` | DATETIME | 创建时间 |

> **复合唯一索引**：`UNIQUE(source_issue_id, target_issue_id, link_type_id)`

### 7.4 API 设计

```
# 获取任务的关联列表
GET /issue/{issueId}/links
Authorization: Bearer {accessToken}

响应:
{
  "code": 200,
  "data": {
    "outwardLinks": [
      { "linkId": 1, "linkType": "blocks", "linkTypeInward": "is blocked by",
        "issue": { "key": "PROJ-5", "summary": "前端接口对接", "status": "todo" } }
    ],
    "inwardLinks": [
      { "linkId": 2, "linkType": "blocks", "linkTypeOutward": "blocks",
        "issue": { "key": "PROJ-3", "summary": "后端登录接口", "status": "done" } }
    ]
  }
}

# 创建关联
POST /issue/{issueId}/links
Authorization: Bearer {accessToken}
Content-Type: application/json
{
  "targetIssueId": 5,
  "linkTypeId": 1
}

# 删除关联
DELETE /issue/{issueId}/links/{linkId}
Authorization: Bearer {accessToken}
```

---

## 8. 任务详情页 Sidebar 增强字段（V2.0）

### 8.1 Epic Link（史诗链接）

| 字段 | 说明 | 字段配置 |
|------|------|---------|
| Epic Link | 将 Story/Bug 关联到 Epic | 自定义字段类型：epic_link |
| Epic Name | 显示关联 Epic 的名称（只读） | 自定义字段类型：epic_name |
| Epic Color | 显示关联 Epic 的颜色（只读） | 自定义字段类型：epic_color |

> **实现方式**：Epic Link 是特殊的自定义字段，类型为 `epic_link`，引用 `issue_type = epic` 的任务。

### 8.2 Story Points（故事点）

| 字段 | 说明 | 字段配置 |
|------|------|---------|
| Story Points | 数字输入（斐波那契数列：1,2,3,5,8,13,21） | 自定义字段类型：number |
| Total Points | Epic 下所有 Story 的故事点汇总（只读） | 自动计算 |

> **Jira 惯例**：大多数团队使用斐波那契数列（1, 2, 3, 5, 8, 13, 21），也有使用 T-Shirt Size（S/M/L）或纯数字。

### 8.3 Affects Version / Fix Version（影响版本 / 修复版本）

| 字段 | 说明 | 字段配置 |
|------|------|---------|
| Affects Version/s（影响的版本） | Bug 影响的版本（多选） | 自定义字段类型：version_multi |
| Fix Version/s（修复版本） | 计划修复的版本（多选） | 自定义字段类型：version_multi |

> **Jira 惯例**：Affects Version 仅 Bug 类型使用；Fix Version 所有类型均可用。版本来源于项目的版本管理。

---

## 9. 项目设置页 — Scheme 配置 UI（V2.0）

> **Jira 对应**：Project Settings 中的 Issue Types / Workflows / Screens / Fields 等 Tab。

### 9.1 Issue Type Scheme 配置页

```
┌────────────────────────────────────────────────────────────────────────────┐
│ 项目设置 > Issue Types                                                   │
│                                                                            │
│  [概览] [详情] [成员] [角色] [Issue Types*] [Workflows] [Screens]        │
│  [Fields] [Notifications] [Versions] [Components] [Sprints] [Labels]   │
├────────────────────────────────────────────────────────────────────────────┤
│                                                                            │
│  当前方案：Default Issue Type Scheme            [编辑方案]                 │
│                                                                            │
│  ─────────────────────────────────────────────────────────────────────────│
│  允许的 Issue Types                         [+ 创建 Issue Type]           │
│  ─────────────────────────────────────────────────────────────────────────│
│  ☑ Epic         🟣 史诗（大型功能）             [移除]                     │
│  ☑ Story        🟢 用户故事                     [移除]                     │
│  ☑ Task         🔵 任务                         [移除]                     │
│  ☑ Bug          🔴 缺陷                         [移除]                     │
│  ─────────────────────────────────────────────────────────────────────────│
│  [+ 添加 Issue Type]                                                      │
│  ─────────────────────────────────────────────────────────────────────────│
│  未分配的 Issue Types                                                    │
│  ☐ Improvement   🟡 优化                         [添加]                    │
│  ☐ Sub-task     ⚪ 子任务                       [添加]                    │
│                                                                            │
│  [保存更改]                              默认类型：[Story ▼]               │
└────────────────────────────────────────────────────────────────────────────┘
```

### 9.2 Workflow Scheme 配置页

```
┌────────────────────────────────────────────────────────────────────────────┐
│ 项目设置 > Workflows                                                      │
├────────────────────────────────────────────────────────────────────────────┤
│  当前方案：Default Workflow Scheme           [编辑方案]                    │
│                                                                            │
│  Issue Type 与 Workflow 映射                                            │
│  ─────────────────────────────────────────────────────────────────────────│
│  Epic         → [Dev Workflow (开发流程) ▼]     [编辑流程]  [查看图]     │
│  Story        → [Dev Workflow (开发流程) ▼]     [编辑流程]  [查看图]     │
│  Task         → [Dev Workflow (开发流程) ▼]     [编辑流程]  [查看图]     │
│  Bug          → [Bug Workflow (缺陷流程) ▼]     [编辑流程]  [查看图]     │
│  ─────────────────────────────────────────────────────────────────────────│
│  [+ 添加 Issue Type 映射]                                                 │
│  [+ 新建 Workflow]                                                        │
└────────────────────────────────────────────────────────────────────────────┘
```

### 9.3 Screen Scheme 配置页

```
┌────────────────────────────────────────────────────────────────────────────┐
│ 项目设置 > Screens                                                        │
├────────────────────────────────────────────────────────────────────────────┤
│  当前方案：Default Screen Scheme            [编辑方案]                     │
│                                                                            │
│  操作与 Screen 映射                                                       │
│  ─────────────────────────────────────────────────────────────────────────│
│  创建问题        → [Default Create Screen ▼]    [编辑字段]  [预览]         │
│  编辑问题        → [Default Edit Screen ▼]     [编辑字段]  [预览]         │
│  查看问题        → [Default View Screen ▼]     [编辑字段]  [预览]         │
│  创建子任务      → [Default Subtask Screen ▼]  [编辑字段]  [预览]         │
│  转换子任务      → [Convert Subtask Screen ▼]  [编辑字段]  [预览]         │
│  流转对话框      → [Transition Screen ▼]       [编辑字段]  [预览]         │
│  ─────────────────────────────────────────────────────────────────────────│
│  [+ 添加操作映射]                                                          │
└────────────────────────────────────────────────────────────────────────────┘
```

### 9.4 Field Configuration 配置页

```
┌────────────────────────────────────────────────────────────────────────────┐
│ 项目设置 > Fields                                                         │
├────────────────────────────────────────────────────────────────────────────┤
│  当前方案：Default Field Config Scheme     [编辑方案]                      │
│                                                                            │
│  字段行为配置                                                           │
│  ┌──────────────────────────────────────────────────────────────────────┐ │
│  │ [所有 Issue Types ▼]                                                  │ │
│  │                                                                        │ │
│  │ 字段                    │ 隐藏 │ 必填 │ 只读 │ 描述（自定义标签）       │ │
│  │ ────────────────────────┼──────┼──────┼──────┼─────────────           │ │
│  │ Assignee（负责人）       │  -   │  -   │  -   │                        │ │
│  │ Reporter（报告人）       │  -   │  -   │  -   │                        │ │
│  │ Sprint（迭代）           │  -   │  -   │  -   │                        │ │
│  │ Epic Link（史诗）        │  -   │  -   │  -   │                        │ │
│  │ Story Points（故事点）   │  -   │  -   │  -   │                        │ │
│  │ Due Date（截止日期）     │  -   │  -   │  ✓   │                        │ │
│  │ [自定义字段：严重程度]   │  -   │  ✓   │  -   │ Bug 必填               │ │
│  │ [自定义字段：客户名称]   │  ✓   │  -   │  -   │                        │ │
│  └──────────────────────────────────────────────────────────────────────┘ │
│                                                                            │
│  [+ 添加字段]  [保存更改]                                                  │
└────────────────────────────────────────────────────────────────────────────┘
```

---

## 10. 看板 Backlog 视图（V2.0）

> **Jira 对应**：Backlog View。在 Board 视图中切换到 Backlog Tab，显示未规划到 Sprint 的所有 Issue。

### 10.1 Backlog 视图布局

```
┌────────────────────────────────────────────────────────────────────────────┐
│  LEST Platform — Scrum Board                        [Board▼] [Backlog▼]    │
├────────────────────────────────────────────────────────────────────────────┤
│                                                                            │
│  Active Sprint          Sprint 2026-W23  (2026-06-01 ~ 2026-06-07)      │
│  ─────────────────────────────────────────────────────────────────────────│
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │
│  │ 待办 (2)   │  │ 进行中 (3)│  │ 代码审核 (1)│  │ 已完成 (5) │     │
│  │ [故事点:8] │  │ [故事点:13]│  │ [故事点:3] │  │ [故事点:21]│     │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘     │
│                                                                            │
│  Backlog                                                                 │
│  ─────────────────────────────────────────────────────────────────────────│
│  [🔵 PROJ-10] 实现用户登录页面                          [故事点: 3] [👤]  │
│  [🔵 PROJ-11] 添加注册功能                               [故事点: 5] [👤]  │
│  [🔵 PROJ-12] 忘记密码功能                               [故事点: 8] [👤]  │
│  [🔵 PROJ-13] 第三方登录集成                             [故事点:13] [👤]  │
│  ─────────────────────────────────────────────────────────────────────────│
│  故事点汇总: 29        [创建 Sprint]  [将问题分配到 Sprint]                │
└────────────────────────────────────────────────────────────────────────────┘
```

### 10.2 用户故事

#### US-BL-001: 查看 Backlog
**作为** 产品经理，**我希望** 查看 Backlog 中的所有未规划问题，**以便** 了解待办列表。

验收标准：
- [ ] Backlog 按 Story Points 倒序排列
- [ ] 显示每个 Issue 的 Key、Summary、Story Points、Assignee
- [ ] 支持拖拽调整排序
- [ ] 支持 Epic 分组展示

#### US-BL-002: 将 Issue 规划到 Sprint
**作为** 产品经理，**我希望** 将 Backlog 中的 Issue 拖拽到 Sprint，**以便** 规划迭代内容。

验收标准：
- [ ] 拖拽 Issue 到 Active Sprint 的列中
- [ ] 支持批量选择多个 Issue 一起拖拽
- [ ] 规划后自动更新 Sprint 的故事点汇总
- [ ] 支持键盘快捷键（E=Edit, S=Sprint, ←→=调整顺序）

---

## 11. 工作流设计增强（V2.0）

### 11.1 全局转换（Global Transition）

> **Jira 对应**：Global Transition。任何状态都可以有一个「全局」流转路径，允许在任意状态下直接转到目标状态。

**使用场景**：
- 「重新打开」：任何状态 → Open（除了已经是 Open 的）
- 「取消」：任意状态 → Cancelled
- 「挂起」：任意状态 → On Hold

**设计**：`workflow_transition` 表增加 `is_global TINYINT` 字段：
- `is_global = 1`：该流转是全局转换，不限制起始状态
- `is_global = 0`：该流转是普通流转，需要严格匹配 from_status

**校验逻辑**：
```java
// 工作流流转校验
public boolean canTransition(Issue issue, String targetStatus) {
    List<WorkflowTransition> transitions = getTransitions(issue.getStatusKey());
    // 全局转换：任何状态都可以
    for (WorkflowTransition t : transitions) {
        if (t.getToStatusKey().equals(targetStatus) && t.getIsGlobal() == 1) {
            return true;
        }
    }
    // 普通流转：必须 from_status 匹配
    for (WorkflowTransition t : transitions) {
        if (t.getToStatusKey().equals(targetStatus) && t.getFromStatusKey().equals(issue.getStatusKey())) {
            return true;
        }
    }
    return false;
}
```

### 11.2 工作流循环检测

**问题**：如果工作流配置了循环路径（如 A→B, B→A），会导致 Issue 在两个状态间无限流转。

**解决方案**：
1. **保存时检测**：发布工作流前，DFS 检测是否存在环
2. **运行时兜底**：即使有环，流转记录会防止无限循环（通过 transition_id 去重）

```java
public boolean hasCycle(WorkflowDefinition workflow) {
    Map<String, List<String>> graph = buildGraph(workflow);
    Set<String> visited = new HashSet<>();
    Set<String> recursion = new HashSet<>();
    for (String node : graph.keySet()) {
        if (dfs(node, graph, visited, recursion)) {
            return true;
        }
    }
    return false;
}

private boolean dfs(String node, Map<String, List<String>> graph,
                    Set<String> visited, Set<String> recursion) {
    if (recursion.contains(node)) return true;  // 找到环
    if (visited.contains(node)) return false;
    visited.add(node);
    recursion.add(node);
    for (String next : graph.getOrDefault(node, Collections.emptyList())) {
        if (dfs(next, graph, visited, recursion)) return true;
    }
    recursion.remove(node);
    return false;
}
```

### 11.3 工作流草稿 / 发布区分

**Jira 的 Draft/Published 机制**：
- 创建/编辑工作流时为 **草稿（Draft）** 状态
- 草稿可以反复修改
- 只有 **发布（Published）** 的工作流才会在 Issue 流转中被使用
- 发布前可预览影响范围（哪些项目/Issue Type 在使用）

**设计**：`workflow_definition` 表增加字段：

|| 字段 | 类型 | 说明 |
||------|------|------|
| `is_draft` | TINYINT | 1=草稿，0=已发布 |
| `draft_updated_at` | DATETIME | 草稿更新时间 |
| `published_at` | DATETIME | 发布时间 |

**API**：
```
# 发布草稿工作流
POST /workflow/{workflowId}/publish
响应: { "affectedProjects": 3, "affectedIssueTypes": 5 }

# 丢弃草稿
DELETE /workflow/{workflowId}/draft
```

---

## 7. 与 V1.0 的迁移策略
'''

if marker in content:
    content = content.replace(marker, issue_link_section)
    changes += 1
    print("1. Added Issue Link + Sidebar Fields + Project Settings UI + Backlog + Workflow enhancements sections")
else:
    print("ERROR: Migration marker not found")

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)

print(f"\n总计完成 {changes} 处修改")
print(f"文件总行数: {len(content.splitlines())}")
