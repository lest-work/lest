#!/usr/bin/env python3
"""Fix V2.0 项目管理 PRD: add Project Settings tab UI and fix section numbers."""

import re

filepath = '/Users/liuyue/code/lest-platform/docs/1-prd/core/V2.0/项目管理.md'
with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

changes = 0

# 1. Fix section number: ## 7. 版本历史 -> ## 14. 版本历史
content = content.replace(
    '\n## 7. 版本历史\n\n|| 版本 | 日期',
    '\n## 14. 版本历史\n\n|| 版本 | 日期'
)
changes += 1
print("1. Fixed section number for 版本历史")

# 2. Add Project Settings tab UI mockup after 12.2 功能开关
old = '''|| wiki | false | 是否启用项目 Wiki（V3.0）|
|| components | true | 是否启用组件/子模块 |
|| issue_links | true | 是否启用任务关联 |'''

new = '''|| wiki | false | 是否启用项目 Wiki（V3.0）|
|| components | true | 是否启用组件/子模块 |
|| issue_links | true | 是否启用任务关联 |

### 12.3 项目设置 Tab UI 原型（Jira 对齐）

```
┌────────────────────────────────────────────────────────────────────────────┐
│  LEST Platform / LEST-DEV / 项目设置                                        │
│                                                                            │
│  [概览] [详情] [成员*] [角色] [Issue Types*] [Workflows*] [Screens*]    │
│  [Fields*] [Notifications] [Versions] [Components] [Sprints] [Labels]  │
│  [自动化] [项目安全]                                                       │
├────────────────────────────────────────────────────────────────────────────┤
│                                                                            │
│  左侧 Tab 导航说明：                                                        │
│                                                                            │
│  Tab 名称          | Jira 对应              | LEST 状态                    │
│  ───────────────┼──────────────────────┼─────────────                    │
│  概览             | Summary               | ✅ 已有                        │
│  详情             | Details                | ✅ 已有                        │
│  成员             | Members                | ✅ 已有                        │
│  角色             | Roles                  | ✅ Permission Scheme           │
│  Issue Types      | Issue Types            | ✅ Issue Type Scheme          │
│  Workflows        | Workflows              | ✅ Workflow Scheme            │
│  Screens          | Screens                | ✅ Screen Scheme             │
│  Fields           | Fields                 | ✅ Field Config Scheme        │
│  Notifications    | Notifications          | ⚠️ 有事件，缺 UI              │
│  Versions         | Versions               | ✅ 已有                        │
│  Components       | Components             | ✅ 已有                        │
│  Sprints          | Sprints                | ✅ 已有                        │
│  Labels           | Labels                 | ⚠️ 缺设计                     │
│  自动化           | Automation             | ✅ 有规则，有 UI              │
│  项目安全          | Issue Security         | ✅ V2.0 新增                  │
│                                                                            │
└────────────────────────────────────────────────────────────────────────────┘
```'''

if old in content:
    content = content.replace(old, new)
    changes += 1
    print("2. Added Project Settings tab UI prototype")
else:
    print("2. WARNING: Project Settings tab UI marker not found")

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)

# Verify
with open(filepath, 'r', encoding='utf-8') as f:
    lines = f.readlines()
for l in lines:
    if re.match(r'^## \d+', l):
        print(l.rstrip())

print(f"\n总计 {changes} 处修改")
