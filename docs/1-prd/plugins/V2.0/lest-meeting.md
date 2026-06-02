# 敏捷会议增强（lest-meeting）PRD — V2.0

> **插件 ID**: `lest-meeting`
>
> **版本**: V2.0
>
> **Jira 映射**: Jira + Confluence Meeting Notes
>
> **许可**: 🆓 开源基础版 / 🔒 商业增强版
>
> **最后更新**: 2026-06-02

---

## 1. 产品概述

### 1.1 目标

在 V1.0 会议基础能力上，提供会议模板库、计时器、会议纪要与行动项自动联动，以及与视频会议（飞书/钉钉/Zoom）的可选集成。

---

## 2. 数据库设计

```sql
-- 会议模板
CREATE TABLE pl_meeting_template (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    template_key    VARCHAR(64) NOT NULL UNIQUE,
    name            VARCHAR(128) NOT NULL COMMENT '如：Sprint Planning/Retrospective/Daily Standup',
    agenda_items    JSON NOT NULL COMMENT '[{"title": "...", "duration": 30, "type": "AGENDA/DISCUSSION/DECISION"}]',
    created_by      BIGINT NOT NULL,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 会议记录
CREATE TABLE pl_meeting_record (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    template_id     BIGINT,
    meeting_type    VARCHAR(32) NOT NULL,
    title           VARCHAR(256) NOT NULL,
    host_id         BIGINT NOT NULL,
    start_time      DATETIME NOT NULL,
    end_time        DATETIME,
    participants    JSON NOT NULL COMMENT '[user_id...]',
    notes           TEXT COMMENT '会议纪要（富文本）',
    action_items    JSON COMMENT '[{"description": "...", "assignee_id": 1, "due_date": "2026-06-15", "issue_id": null}]',
    recording_url   VARCHAR(512) COMMENT '会议录像 URL',
    video_provider  VARCHAR(32) COMMENT 'FEISHU/DINGTALK/ZOOM',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

---

## 3. 会议模板设计

| 模板 | 议程项 | 时长 |
|------|--------|------|
| Sprint Planning（计划会） | 1. Sprint Goal 确认 2. Backlog Grooming 3. 任务分配 4. 容量确认 | 60min |
| Daily Standup（每日站会） | 1. 昨日完成 2. 今日计划 3. 阻塞问题 | 15min |
| Sprint Retrospective（回顾会） | 1. What went well 2. What to improve 3. Action items | 60min |
| Sprint Review（评审会） | 1. Demo 演示 2. Feedback 收集 3. 验收确认 | 45min |

---

## 4. 用户故事

| ID | 用户故事 | 验收标准 |
|----|---------|---------|
| US-MTG-001 | 作为会议主持人，我希望选择会议模板并自动填充议程 | 模板填充后议程项可编辑 |
| US-MTG-002 | 作为会议主持人，我希望使用计时器控制每个议程的时间 | 超时前 1min 提醒，超时后自动跳转 |
| US-MTG-003 | 作为会议主持人，我希望在会议结束时将讨论的行动项一键创建为 LEST 任务 | 行动项自动关联到对应的迭代和 Epic |
| US-MTG-004 | 作为参会人，我希望在会议纪要中@提及某个任务，系统自动关联 | @任务后生成链接，点击跳转 |
| US-MTG-005 | 作为会议主持人，我希望连接视频会议（飞书/钉钉/Zoom），会议自动录制 | 录制完成后自动生成录像链接 |
| US-MTG-006 | 作为团队成员，我希望在会议结束后收到行动项通知 | 通知包含任务名称、负责人和截止日期 |

---

## 5. API 设计

| 接口 | 方法 | 说明 |
|------|------|------|
| `/meeting/templates` | GET/POST | 模板列表/创建 |
| `/meeting/templates/{id}` | GET/PUT/DELETE | 模板详情/更新/删除 |
| `/meeting` | GET/POST | 会议列表/创建 |
| `/meeting/{id}` | GET/PUT/DELETE | 会议详情/更新/删除 |
| `/meeting/{id}/action-items` | POST | 创建行动项（生成任务） |
| `/meeting/{id}/notes` | PUT | 更新会议纪要 |
| `/meeting/{id}/video/join` | GET | 获取视频会议加入链接 |
| `/meeting/timer/{templateId}` | GET | 获取计时器配置 |

## 6. 错误码

| 模块 | 错误码范围 | 说明 |
|------|-----------|------|
| 会议 | 11000-11099 | 插件 会议 模块 |

## 7. 版本历史

| 版本 | 日期 | 修改内容 | 作者 |
|------|------|---------|------|
| V1.0 | 2026年底 | 初始版本 | - |
