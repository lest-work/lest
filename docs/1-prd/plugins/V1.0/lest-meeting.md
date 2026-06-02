# 敏捷会议 PRD
> **📌 Jira 映射**：Jira Meetings + Atlassian Confluence Pages
>
> **功能定位**：站会、计划会、回顾会、评审会及会议纪要


## 文档信息

| 属性 | 内容 |
|------|------|
| 版本 | V1.0 |
| 状态 | 已完成 |
| 创建日期 | 2026-05-25 |
| 最后更新 | 2026-05-25 |

---

## 1. 概述

### 1.1 功能背景

敏捷会议是 LEST Platform 支持敏捷开发流程的重要组成部分。平台支持多种敏捷会议类型（每日站会、周会、回顾会、计划会），帮助团队保持透明沟通、持续改进，同时自动关联会议内容与任务、迭代。

### 1.2 功能目标

- 支持多种会议类型（每日站会、周会、Sprint 回顾会、Sprint 计划会）
- 提供会议模板和议程
- 支持会议预约和提醒
- 自动关联会议讨论的任务和迭代
- 提供会议纪要编辑和分享
- 支持会议决策跟踪
- 关联会议生成会议报告

### 1.3 目标用户

- **Scrum Master**：组织和管理敏捷会议
- **项目经理**：跟踪项目进度和决策
- **开发团队**：参与会议、记录讨论
- **管理层**：查看会议报告和团队状态

---

## 2. 用户故事

### 2.1 会议管理

#### US-001: 创建会议
**作为** Scrum Master，**我希望** 创建敏捷会议，**以便** 组织团队讨论。

验收标准：
- [ ] 选择会议类型（每日站会/周会/回顾会/计划会）
- [ ] 选择参与人员
- [ ] 设置会议时间（一次性/重复）
- [ ] 设置会议地点或链接（线上会议链接）
- [ ] 设置会议提醒时间
- [ ] 选择关联的迭代（可选）

#### US-002: 每日站会
**作为** 团队成员，**我希望** 参与每日站会，**以便** 同步团队进度。

验收标准：
- [ ] 填写"昨日完成"内容
- [ ] 填写"今日计划"内容
- [ ] 填写"遇到问题/阻塞"内容
- [ ] 关联相关任务
- [ ] 显示与会人员状态
- [ ] 站会记录自动汇总

#### US-003: Sprint 计划会
**作为** Scrum Master，**我希望** 组织 Sprint 计划会，**以便** 规划迭代任务。

验收标准：
- [ ] 显示待选任务列表
- [ ] 支持拖拽选择任务到迭代
- [ ] 显示团队容量（人天）
- [ ] 显示迭代时间范围
- [ ] 设置迭代目标和范围
- [ ] 自动创建迭代

#### US-004: Sprint 回顾会
**作为** Scrum Master，**我希望** 组织 Sprint 回顾会，**以便** 复盘迭代执行。

验收标准：
- [ ] 显示迭代完成情况统计
- [ ] 填写"做得好的地方"
- [ ] 填写"需要改进的地方"
- [ ] 填写"改进措施"
- [ ] 设置改进措施的责任人和截止日期
- [ ] 保存回顾记录

#### US-005: Sprint 评审会
**作为** 团队，**我希望** 展示迭代成果，**以便** 获得利益相关者反馈。

验收标准：
- [ ] 显示已完成的功能列表
- [ ] 支持功能演示（可上传演示材料）
- [ ] 收集反馈意见
- [ ] 记录评审结论（通过/需要修改）
- [ ] 评审通过后更新功能状态

### 2.2 会议纪要

#### US-006: 编辑会议纪要
**作为** 记录人，**我希望** 编辑会议纪要，**以便** 记录会议内容。

验收标准：
- [ ] 自动生成基本格式
- [ ] 支持富文本编辑
- [ ] 支持添加会议决议
- [ ] 支持添加行动项
- [ ] 支持 @提及相关人员
- [ ] 支持关联任务

#### US-007: 分享会议纪要
**作为** 记录人，**我希望** 分享会议纪要，**以便** 让相关人员了解会议内容。

验收标准：
- [ ] 站内通知参与者
- [ ] 支持导出 PDF/Markdown
- [ ] 支持生成分享链接
- [ ] 支持邮件发送

### 2.3 会议报告

#### US-008: 查看会议统计
**作为** 项目经理，**我希望** 查看会议统计，**以便** 了解会议效率。

验收标准：
- [ ] 显示会议频率统计
- [ ] 显示参会率统计
- [ ] 显示行动项完成率
- [ ] 显示迭代效率与会议关联

---

## 3. 功能详细设计

### 3.1 功能清单

| 序号 | 功能点 | 功能描述 | 优先级 |
|------|--------|---------|--------|
| 1 | 创建会议 | 创建各类敏捷会议 | P0 |
| 2 | 每日站会 | 站会发言、同步进度 | P0 |
| 3 | Sprint 计划会 | 任务规划、容量评估 | P0 |
| 4 | Sprint 回顾会 | 迭代复盘、改进措施 | P0 |
| 5 | Sprint 评审会 | 成果展示、反馈收集 | P0 |
| 6 | 会议纪要 | 编辑、分享、导出 | P0 |
| 7 | 会议提醒 | 提前提醒参与者 | P1 |
| 8 | 会议报告 | 会议效率统计 | P2 |
| 9 | 会议模板 | 自定义会议模板 | P2 |
| 10 | 会议-任务集成 | 行动项创建任务、任务关联会议 | P0 |
| 11 | 会议-绩效集成 | 会议参与度统计 | P1 |
| 12 | 会议-通知集成 | 完整的会议通知事件 | P0 |

### 3.2 会议-任务集成设计

#### US-009: 会议行动项创建任务
**作为** 记录人，**我希望** 将会议行动项直接创建为任务，**以便** 自动化工作安排。

验收标准：
- [ ] 会议纪要中的行动项支持一键创建任务
- [ ] 自动填充任务标题、描述、负责人、截止日期
- [ ] 行动项创建任务后，关联会议 ID 便于追溯

#### US-010: 站会任务汇报
**作为** 团队成员，**我希望** 在站会中汇报任务进展，**以便** 高效同步工作。

验收标准：
- [ ] 站会页面显示"我的任务"列表（今日完成 + 明日计划）
- [ ] 支持快速更新任务状态
- [ ] 站会汇报记录关联到会议

#### 跨模块数据关联

| 关联方向 | 关联方式 | 说明 |
|---------|---------|------|
| 会议 → 任务 | action_items.task_id | 行动项关联创建的任务 |
| 任务 → 会议 | task.source_meeting_id | 任务来源追溯到会议 |
| 会议 → 绩效 | participant.participation_record | 参会记录用于绩效统计 |

### 3.3 会议通知事件定义

| 事件编码 | 事件名称 | 触发时机 | 通知对象 | 优先级 |
|---------|---------|---------|---------|--------|
| MEETING_SCHEDULED | 会议安排 | 创建会议成功时 | 所有参与者 | P0 |
| MEETING_REMINDER | 会议提醒 | 会议开始前 N 分钟（可配置，默认 5 分钟） | 所有参与者 | P0 |
| MEETING_CANCELLED | 会议取消 | 会议被取消时 | 所有参与者 | P1 |
| MEETING_UPDATED | 会议变更 | 会议时间/内容变更时 | 所有参与者 | P1 |
| MEETING_MINUTES_SHARED | 纪要分享 | 会议纪要分享时 | 所有参与者 | P1 |
| SPRINT_STARTED | Sprint 启动 | 迭代状态变为进行中时 | 项目所有成员 | P0 |
| SPRINT_ENDED | Sprint 结束 | 迭代状态变为已完成时 | 项目所有成员 | P0 |
| SPRINT_REVIEW_COMPLETED | Sprint 评审完成 | Sprint 评审会结束时 | 项目所有成员 | P1 |
| MEETING_ACTION_ITEMS_CREATED | 行动项创建 | 行动项创建为任务时 | 负责人 | P2 |

### 3.4 会议类型定义

```java
public enum MeetingType {
    DAILY_STANDUP("每日站会", "daily"),
    WEEKLY_REVIEW("周会", "weekly"),
    SPRINT_PLANNING("Sprint 计划会", "sprint_planning"),
    SPRINT_RETROSPECTIVE("Sprint 回顾会", "sprint_retrospective"),
    SPRINT_REVIEW("Sprint 评审会", "sprint_review"),
    BRAINSTORM("头脑风暴", "brainstorm"),
    ONE_ON_ONE("一对一", "one_on_one");

    private final String name;
    private final String code;
}

public enum MeetingStatus {
    SCHEDULED,   // 已安排
    IN_PROGRESS,  // 进行中
    COMPLETED,    // 已完成
    CANCELLED     // 已取消
}
```

### 3.5 页面原型

#### 3.5.1 每日站会页

```
┌────────────────────────────────────────────────────────────────────────────┐
│ 每日站会 · 2026-05-25                              [结束站会] [历史记录]   │
├────────────────────────────────────────────────────────────────────────────┤
│ 当前迭代: Sprint 12  (2026-05-20 ~ 2026-06-02)                           │
│ 目标: 完成用户认证模块开发                                                 │
├────────────────────────────────────────────────────────────────────────────┤
│ 参与人员                                                                    │
│ ┌───────────────┬───────────────┬───────────────┬───────────────┐       │
│ │ 张三          │ 李四          │ 王五          │ 赵六          │       │
│ │ ✓ 已发言      │ ⏳ 发言中     │ ○ 待发言      │ ○ 待发言      │       │
│ └───────────────┴───────────────┴───────────────┴───────────────┘       │
├────────────────────────────────────────────────────────────────────────────┤
│ 当前发言人: 李四                                                           │
│ ┌──────────────────────────────────────────────────────────────────────────┐│
│ │ 昨日完成:                                                              ││
│ │ • 完成登录接口开发 #123                                                 ││
│ │ • 提交了 MR #45                                                        ││
│ │                                                                          ││
│ │ 今日计划:                                                              ││
│ │ • 实现注册功能 #124                                                     ││
│ │ • 编写单元测试                                                          ││
│ │                                                                          ││
│ │ 遇到问题:                                                              ││
│ │ • 缺少第三方短信验证码接口文档                                          ││
│ └──────────────────────────────────────────────────────────────────────────┘│
│                                                                          │
│ 关联任务: #123 登录接口  #124 注册功能                                     │
└────────────────────────────────────────────────────────────────────────────┘
```

#### 3.5.2 会议列表页

```
┌────────────────────────────────────────────────────────────────────────────┐
│ 会议                                                          [+ 创建会议] │
├────────────────────────────────────────────────────────────────────────────┤
│ [全部] [今日] [本周] [已结束]                                           │
├────────────────────────────────────────────────────────────────────────────┤
│ 05-25 14:00  Sprint 计划会                         Sprint 13  已安排  3人 │
│ 05-26 09:30  每日站会                               Sprint 12  已安排  5人 │
│ 05-27 09:30  每日站会                               Sprint 12  已安排  5人 │
│ 05-28 09:30  每日站会                               Sprint 12  已安排  5人 │
│ 05-29 14:00  Sprint 回顾会                         Sprint 12  已安排  6人 │
│ 05-30 10:00  Sprint 评审会                         Sprint 12  已安排  8人 │
└────────────────────────────────────────────────────────────────────────────┘
```

---



## 3.X 会议增强（V2.0/V3.0）

### 3.X.1 会议计时器（V2.0）

会议计时器帮助团队控制每个议题的时间：

| 功能 | 说明 |
|------|------|
| 议题计时 | 每个议题独立计时，超时提醒 |
| 总会议计时 | 整个会议计时 |
| 站立会计时 | 每人 2 分钟倒计时 |
| 演讲模式 | 自动切换到下一个议题 |

### 3.X.2 会议模板库（V2.0）

预设会议模板，快速创建会议：

| 模板 | 包含内容 |
|------|---------|
| 每日站会 | 3 个固定问题 + 每人 2 分钟 |
| Sprint 计划会 | 议程：Backlog 评审 → 估算 → 承诺 |
| Sprint 回顾会 | 议程：做得好的 → 待改进的 → 行动计划 |
| Sprint 评审会 | 议程：演示 → 反馈 → 验收 |
| 需求评审会 | 议程：需求介绍 → 澄清 → 估算 |
| 故障复盘会 | 议程：时间线 → 根因 → 改进措施 |

### 3.X.3 视频会议集成（V3.0）

与主流视频会议平台集成：

| 平台 | 集成方式 |
|------|---------|
| 腾讯会议 | 创建会议 → 生成加入链接 |
| 钉钉会议 | 创建会议 → 生成加入链接 |
| 飞书会议 | 创建会议 → 生成加入链接 |
| Zoom | OAuth 授权 → 创建会议 |

**集成功能：**
- 点击"开始会议"自动创建视频会议并分享链接
- 会议结束后自动录制要点摘要
- 会议视频回放链接关联会议记录

### 3.X.4 会议协作编辑（V3.0）

会议纪要支持多人实时协作编辑：
- 基于 CRDT 算法实现无冲突编辑
- 显示在线编辑者头像
- 版本历史和回滚

## 4. 接口设计

### 4.1 会议接口

#### 4.1.1 创建会议
```
POST /meeting
Authorization: Bearer {accessToken}
Content-Type: application/json

请求:
{
  "type": "daily",
  "title": "每日站会 - 2026-05-25",
  "projectId": 1,
  "iterationId": 12,
  "startTime": "2026-05-25 09:30:00",
  "duration": 15,
  "participantIds": [1, 2, 3, 4, 5],
  "location": "https://meeting.example.com/123",
  "reminderMinutes": 5
}

响应:
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "type": "daily",
    "title": "每日站会 - 2026-05-25"
  }
}
```

#### 4.1.2 获取会议列表
```
GET /meeting?projectId=1&date=2026-05-25&type=daily&page=1&size=20
Authorization: Bearer {accessToken}

响应:
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "type": "daily",
        "title": "每日站会 - 2026-05-25",
        "status": "scheduled",
        "startTime": "2026-05-25 09:30:00",
        "duration": 15,
        "participantCount": 5,
        "iterationName": "Sprint 12"
      }
    ],
    "total": 20,
    "page": 1,
    "size": 20
  }
}
```

#### 4.1.3 获取会议详情
```
GET /meeting/{id}
Authorization: Bearer {accessToken}

响应:
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "type": "daily",
    "title": "每日站会 - 2026-05-25",
    "status": "in_progress",
    "projectId": 1,
    "iterationId": 12,
    "startTime": "2026-05-25 09:30:00",
    "duration": 15,
    "location": "https://meeting.example.com/123",
    "organizer": {
      "userId": 1,
      "username": "张三"
    },
    "participants": [
      {
        "userId": 1,
        "username": "张三",
        "status": "completed",
        "report": {
          "yesterdayDone": "完成登录接口开发 #123",
          "todayPlan": "实现注册功能 #124",
          "blockers": "缺少第三方短信验证码接口文档"
        }
      }
    ],
    "minutes": {
      "content": "会议纪要内容...",
      "decisions": [
        {
          "content": "决定使用阿里云短信服务",
          "decidedAt": "2026-05-25 09:45:00"
        }
      ],
      "actionItems": [
        {
          "content": "联系第三方获取 API 文档",
          "assigneeId": 2,
          "dueDate": "2026-05-26"
        }
      ]
    },
    "linkedTasks": [
      {
        "taskId": 123,
        "taskTitle": "实现登录接口"
      }
    ]
  }
}
```

#### 4.1.4 提交站会发言
```
POST /meeting/{id}/report
Authorization: Bearer {accessToken}
Content-Type: application/json

请求:
{
  "yesterdayDone": "完成登录接口开发 #123",
  "todayPlan": "实现注册功能 #124",
  "blockers": "缺少第三方短信验证码接口文档",
  "linkedTaskIds": [123, 124]
}

响应:
{
  "code": 200,
  "message": "success"
}
```

---

## 5. 数据库设计

### 5.1 表结构

#### meet_meeting 会议表

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| project_id | BIGINT | NOT NULL | 项目 ID |
| iteration_id | BIGINT | | 迭代 ID |
| type | VARCHAR(32) | NOT NULL | 会议类型 |
| title | VARCHAR(256) | NOT NULL | 会议标题 |
| status | VARCHAR(16) | NOT NULL | 状态 |
| start_time | DATETIME | NOT NULL | 开始时间 |
| duration | INT | DEFAULT 15 | 时长（分钟） |
| location | VARCHAR(512) | | 地点/链接 |
| organizer_id | BIGINT | NOT NULL | 组织者 ID |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| **索引** | | | |
| idx_project_id | | | 项目 ID 索引 |
| idx_iteration_id | | | 迭代 ID 索引 |
| idx_start_time | | | 开始时间索引 |

#### meet_participant 会议参与者表

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| meeting_id | BIGINT | NOT NULL | 会议 ID |
| user_id | BIGINT | NOT NULL | 用户 ID |
| status | VARCHAR(16) | DEFAULT 'pending' | 状态 |
| report_content | JSON | | 发言内容（站会专用） |
| reported_at | DATETIME | | 发言时间 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| **索引** | | | |
| idx_meeting_id | | | 会议 ID 索引 |

#### meet_minutes 会议纪要表

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| meeting_id | BIGINT | NOT NULL, UNIQUE | 会议 ID |
| content | TEXT | | 纪要内容 |
| decisions | JSON | | 会议决议 |
| action_items | JSON | | 行动项 |
| recorder_id | BIGINT | | 记录人 ID |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

#### meet_action_item 会议行动项表

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| meeting_id | BIGINT | NOT NULL | 会议 ID |
| task_id | BIGINT | NULL | 关联任务 ID（创建为任务后填充） |
| assignee_id | BIGINT | NULL | 负责人 ID |
| assignee_name | VARCHAR(64) | | 负责人姓名 |
| title | VARCHAR(255) | NOT NULL | 行动项标题 |
| description | TEXT | | 行动项描述 |
| due_date | DATE | NULL | 截止日期 |
| status | VARCHAR(16) | DEFAULT 'pending' | 状态：pending（待处理）、in_progress（进行中）、completed（已完成） |
| priority | VARCHAR(16) | DEFAULT 'medium' | 优先级：low、medium、high |
| completed_at | DATETIME | NULL | 完成时间 |
| created_by | BIGINT | NOT NULL | 创建人 ID |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| **索引** | | | |
| idx_meeting_id | | | 会议 ID 索引 |
| idx_task_id | | | 关联任务 ID 索引 |
| idx_assignee_id | | | 负责人 ID 索引 |
| idx_status | | | 状态索引 |

> **说明**：`meet_minutes.action_items` 字段（JSON 类型）用于快速存储轻量级行动项列表，`meet_action_item` 表用于需要独立追踪的正式行动项（可关联任务、设置截止日期、追踪状态）。

---

## 6. 验收标准

| 用例 | 验收标准 |
|------|---------|
| UC-001 | 可创建各类敏捷会议 |
| UC-002 | 每日站会显示发言状态和内容 |
| UC-003 | Sprint 计划会显示任务和容量 |
| UC-004 | Sprint 回顾会记录改进措施 |
| UC-005 | 会议纪要支持编辑和分享 |
| UC-006 | 会议关联任务和迭代 |
| UC-007 | 会议前发送提醒通知 |

---

## 7. 错误码

### 7.1 会议模块错误码 (13000-13999)

| 错误码 | 枚举常量 | HTTP 状态码 | 说明 |
|---------|----------|-------------|------|
| 13000 | `MEETING_NOT_FOUND` | 404 | 会议不存在 |
| 13001 | `MEETING_IN_PROGRESS` | 409 | 会议进行中 |
| 13002 | `MEETING_ALREADY_ENDED` | 409 | 会议已结束 |
| 13003 | `NOT_PARTICIPANT` | 403 | 非参与人员 |
| 13004 | `RECORD_NOT_ALLOWED` | 403 | 无记录权限 |
| 13005 | `ITERATION_NOT_FOUND` | 404 | 迭代不存在 |

---

## 8. 版本历史

| 版本 | 日期 | 修改内容 | 作者 |
|------|------|---------|------|
| V1.0 | 2026-05-25 | 初始版本 | - |
| V1.1 | 2026-05-25 | 补充会议-任务集成设计（行动项创建任务、站会汇报）、会议通知事件定义（9个事件）、会议-绩效关联说明、补充行动项表定义（meet_action_item）、修正章节编号（3.3→3.5）、补充用户故事编号（US-009, US-010） | - |
