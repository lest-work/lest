#!/usr/bin/env python3
"""Fix V1.0 PRD: Add missing Jira-aligned fields to Issue Detail Page."""

import re

filepath = '/Users/liuyue/code/lest-platform/docs/1-prd/core/V1.0/任务管理.md'
with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

changes = 0

# 1. Add Reporter + Start Date to US-001 (创建任务 acceptance criteria)
old = """验收标准：
- [ ] 输入任务标题（必填）
- [ ] 选择所属项目（必填）和迭代（可选）
- [ ] 选择任务类型（需求/任务/Bug/优化）
- [ ] 选择优先级（紧急/高/中/低）
- [ ] 选择负责人（可选，不选则未分配）
- [ ] 设置预估工时（可选）
- [ ] 设置截止日期（可选）
- [ ] 富文本描述（可选，支持 Markdown）"""
new = """验收标准：
- [ ] 输入任务标题（必填）
- [ ] 选择所属项目（必填）和迭代（可选）
- [ ] 选择任务类型（需求/任务/Bug/优化）
- [ ] 选择优先级（紧急/高/中/低）
- [ ] 选择负责人（可选，不选则未分配）
- [ ] 选择报告人（默认为当前用户，可修改）
- [ ] 设置开始日期（可选）
- [ ] 设置截止日期（可选）
- [ ] 设置预估工时（可选）
- [ ] 富文本描述（可选，支持 Markdown）"""
if old in content:
    content = content.replace(old, new)
    changes += 1
    print("1. Added Reporter + Start Date to US-001")

# 2. Add Reporter to US-002 (编辑任务)
old = """验收标准：
- [ ] 可编辑所有任务字段
- [ ] 编辑操作记录到操作日志
- [ ] 任务完成后不可再编辑（除非重新打开）"""
new = """验收标准：
- [ ] 可编辑所有任务字段（标题/描述/类型/优先级/负责人/报告人/迭代/开始日期/截止日期/工时/标签）
- [ ] 编辑操作记录到操作日志（变更历史）
- [ ] 任务完成后不可再编辑（除非重新打开）"""
if old in content:
    content = content.replace(old, new)
    changes += 1
    print("2. Added fields to US-002 edit acceptance criteria")

# 3. Add reporterId to API request (4.1.1)
old = '''  "assigneeId": 1,
  "estimatedHours": 8,'''
new = '''  "assigneeId": 1,
  "reporterId": 1,
  "startDate": "2026-05-22",
  "estimatedHours": 8,'''
if old in content:
    content = content.replace(old, new)
    changes += 1
    print("3. Added reporterId + startDate to API request body")

# 4. Add reporterId to API response (4.1.2)
old = '''        "assigneeId": 1,
        "assigneeName": "张三",
        "priority": "p1",'''
new = '''        "assigneeId": 1,
        "assigneeName": "张三",
        "reporterId": 1,
        "reporterName": "李四",
        "priority": "p1",'''
if old in content:
    content = content.replace(old, new)
    changes += 1
    print("4. Added reporter fields to API response")

# 5. Add reporter to detail page prototype
old = '''│  项目: LEST Platform    迭代: v1.0     类型: 任务      优先级: 🔴 P1      │
│  负责人: 👤 张三        状态: 进行中    标签: [后端] [登录]                │
│  创建时间: 2026-05-20    开始时间: 2026-05-22    截止日期: 2026-05-30  │'''
new = '''│  项目: LEST Platform    迭代: v1.0     类型: 任务      优先级: 🔴 P1      │
│  负责人: 👤 张三        报告人: 👤 李四    状态: 进行中    标签: [后端] [登录]│
│  开始时间: 2026-05-22    截止日期: 2026-05-30    创建时间: 2026-05-20    │'''
if old in content:
    content = content.replace(old, new)
    changes += 1
    print("5. Added Reporter + Start Date to detail page prototype")

# 6. Add Watch/Unwatch detail after US-032
# First find the end of US-032
old_us032 = """#### US-032: 取消关注任务
**作为** 团队成员，**我希望** 取消关注任务，**以便** 停止接收该任务的后续通知。

验收标准：
- [ ] 在任务详情页的 Watchers 列表中移除当前用户
- [ ] 取消关注后不再接收该任务的通知
- [ ] 任务详情页的 Watch 按钮变为 Unwatch"""
if old_us032 in content:
    new_us032 = """#### US-032: 取消关注任务
**作为** 团队成员，**我希望** 取消关注任务，**以便** 停止接收该任务的后续通知。

验收标准：
- [ ] 在任务详情页的 Watchers 列表中移除当前用户
- [ ] 取消关注后不再接收该任务的通知
- [ ] 任务详情页的 Watch 按钮变为 Unwatch

#### US-032b: 任务关注者管理
**作为** 项目经理，**我希望** 查看和管理任务的关注者，**以便** 了解谁在关注该任务。

验收标准：
- [ ] 任务 Sidebar 显示 Watchers 数量
- [ ] 点击展开查看关注者列表（头像+用户名）
- [ ] 支持通过 @ 提及将用户加入 Watchers
- [ ] 任务负责人默认关注自己的任务
- [ ] 任务的创建者默认关注自己的任务"""
    content = content.replace(old_us032, new_us032)
    changes += 1
    print("6. Added Watcher management detail (US-032b)")

# 7. Add Watch button to Actions in detail page prototype
old_actions = """│ ← 返回  实现用户登录功能                          [编辑] [删除] [打开]    │"""
new_actions = """│ ← 返回  实现用户登录功能          [👁 关注] [克隆] [编辑] [删除] [打开]  │"""
if old_actions in content:
    content = content.replace(old_actions, new_actions)
    changes += 1
    print("7. Added Watch/Clone buttons to detail page prototype actions")

# 8. Add Change History section before Work Log in prototype
old_wl = """│  ─────────────────────────────────────────────────────────────────────────  │
│  工时记录 (5h)                                                        │"""
new_wl = """│  ─────────────────────────────────────────────────────────────────────────  │
│  变更历史                                                              │
│  ─────────────────────────────────────────────────────────────────────────  │
│  📝 张三  2026-05-22 14:30  将状态从「待办」改为「进行中」             │
│  📝 李四  2026-05-22 15:00  将负责人从「王五」改为「张三」             │
│  📝 张三  2026-05-23 10:00  将优先级从「P2」改为「P1」                 │
│                                                                            │
│  ─────────────────────────────────────────────────────────────────────────  │
│  工时记录 (5h)                                                        │"""
if old_wl in content:
    content = content.replace(old_wl, new_wl)
    changes += 1
    print("8. Added Change History to detail page prototype")

# 9. Add Watchers count in sidebar area of prototype
old_sb = """│  ─────────────────────────────────────────────────────────────────────────  │
│  依赖任务                                                              │"""
new_sb = """│  ─────────────────────────────────────────────────────────────────────────  │
│  👁 关注者 (3)    🔗 关联任务 (2)                                       │
│  👤 张三  👤 李四  👤 王五                                              │
│  ─────────────────────────────────────────────────────────────────────────  │
│  依赖任务                                                              │"""
if old_sb in content:
    content = content.replace(old_sb, new_sb)
    changes += 1
    print("9. Added Watchers + Linked Issues indicators to prototype")

# 10. Add "Change History" to feature list (3.1)
old_fl = """|| 13 | 任务评论 | 评论任务、@提及 | P2 |
|| 14 | 任务附件 | 上传、下载附件 | P2 |"""
new_fl = """|| 13 | 任务评论 | 评论任务、@提及 | P2 |
|| 13b | 变更历史 | 记录所有字段变更记录 | P2 |
|| 13c | 任务关注 | Watch/Unwatch，关注者列表 | P2 |
|| 14 | 任务附件 | 上传、下载附件 | P2 |"""
if old_fl in content:
    content = content.replace(old_fl, new_fl)
    changes += 1
    print("10. Added Change History + Watch to feature list")

# 11. Add Change History API to section 4
old_api = """#### 4.1.8 任务评论接口
```
GET /task/{taskId}/comments
Authorization: Bearer {accessToken}"""
new_api = """#### 4.1.7b 变更历史接口
```
GET /task/{taskId}/change-history
Authorization: Bearer {accessToken}

响应:
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "taskId": 1,
      "userId": 1,
      "userName": "张三",
      "fieldName": "status",
      "oldValue": "todo",
      "newValue": "in_progress",
      "createdAt": "2026-05-22T14:30:00Z"
    }
  ]
}
```

#### 4.1.8 任务评论接口
```
GET /task/{taskId}/comments
Authorization: Bearer {accessToken}"""
if old_api in content:
    content = content.replace(old_api, new_api)
    changes += 1
    print("11. Added Change History API")

# 12. Add Work Log Tab detail to page prototype
old_prototab = """│  评论 (2)"""
new_prototab = """│  评论 (2)"""
if old_prototab in content:
    # The prototype already has comments - add a note about Work Log tab
    pass

# 13. Add Watch API endpoint
old_watchapi = """#### 4.1.14 任务通知接口
```
GET /notification"""
new_watchapi = """#### 4.1.13b 任务关注接口
```
# 关注任务
POST /task/{taskId}/watch
Authorization: Bearer {accessToken}

# 取消关注
DELETE /task/{taskId}/watch
Authorization: Bearer {accessToken}

# 获取关注者列表
GET /task/{taskId}/watchers
Authorization: Bearer {accessToken}

响应:
{
  "code": 200,
  "message": "success",
  "data": {
    "watching": true,
    "watchers": [
      { "userId": 1, "userName": "张三", "avatar": "..." },
      { "userId": 2, "userName": "李四", "avatar": "..." }
    ],
    "watcherCount": 2
  }
}
```

#### 4.1.14 任务通知接口
```
GET /notification"""
if old_watchapi in content:
    content = content.replace(old_watchapi, new_watchapi)
    changes += 1
    print("13. Added Watch API endpoints")

# 14. Update the 功能清单 table - add startDate column context
old_db = """### 3.2 数据库设计

#### 3.2.1 task 表"""
new_db = """### 3.1b 任务详情页字段设计（Jira 对齐）

#### 3.1b.1 Header 区域

| 字段 | 类型 | 说明 | 优先级 |
|------|------|------|--------|
| Issue Key | 自动生成 | PROJ-123 格式，项目前缀+数字 | 必填 |
| Summary | 文本 | 任务标题，1-255 字符 | 必填 |
| Issue Type | 选择器 | Epic / Story / Task / Bug | 必填 |
| Status | 状态标签 | 待办 / 进行中 / 已完成 | 自动 |
| Priority | 图标选择 | P0~P3 优先级 | 必填 |
| Labels | 标签选择 | 支持多选，颜色自定义 | 可选 |

#### 3.1b.2 Sidebar（右栏）

| 字段 | 类型 | 说明 | 优先级 |
|------|------|------|--------|
| Assignee（负责人） | 用户选择器 | 下拉选择，搜索用户 | 可选 |
| Reporter（报告人） | 用户选择器 | 默认当前用户 | 必填 |
| Labels | 标签选择 | 多选，颜色 chips | 可选 |
| Sprint（迭代） | 迭代选择器 | 当前项目中可选迭代 | 可选 |
| Due Date（截止日期） | 日期选择器 | 年-月-日 | 可选 |
| Start Date（开始日期） | 日期选择器 | 年-月-日 | 可选 |
| Estimated（预估工时） | 数字+单位 | 小时或天 | 可选 |
| Due Date Warning | 标签 | 超期前 3 天显示警告 | 自动 |

#### 3.1b.3 Tabs

| Tab 名称 | 功能描述 | 技术实现 |
|---------|---------|---------|
| Details（详情） | 任务描述，富文本编辑器 | Markdown 渲染 |
| Comments（评论） | 评论列表 + 富文本编辑器 | task_comment 表 |
| Change History（变更历史） | 所有字段变更记录 | task_history 表 |
| Work Log（工时日志） | 工时记录列表 + 添加按钮 | task_worklog 表 |

#### 3.1b.4 Actions 操作栏

| 操作 | 说明 | 权限 |
|------|------|------|
| Edit（编辑） | 打开编辑弹窗 | 负责人/创建者/管理员 |
| Clone（克隆） | 复制任务（含选项） | 有创建权限 |
| Watch（关注）| 关注/取消关注 | 登录用户 |
| Delete（删除） | 删除任务 | 管理员/创建者 |
| Create Subtask | 创建子任务 | 有创建权限 |

### 3.2 数据库设计

#### 3.2.1 task 表"""
if old_db in content:
    content = content.replace(old_db, new_db)
    changes += 1
    print("14. Added Issue Detail Page field design section (3.1b)")

# 15. Add Start Date to task table schema
old_schema = """  `description` text COMMENT '任务描述',
  `project_id` bigint NOT NULL COMMENT '所属项目ID',
  `iteration_id` bigint DEFAULT NULL COMMENT '所属迭代ID',"""
new_schema = """  `description` text COMMENT '任务描述',
  `project_id` bigint NOT NULL COMMENT '所属项目ID',
  `iteration_id` bigint DEFAULT NULL COMMENT '所属迭代ID',
  `start_date` date DEFAULT NULL COMMENT '开始日期',
  `due_date` date DEFAULT NULL COMMENT '截止日期',"""
if old_schema in content:
    content = content.replace(old_schema, new_schema)
    changes += 1
    print("15. Added startDate + dueDate to task table schema")

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)

print(f"\n总计完成 {changes} 处修改")
