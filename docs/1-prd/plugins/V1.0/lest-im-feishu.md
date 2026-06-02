# 飞书集成（lest-im-feishu）PRD — V1.0

> **插件 ID**: `lest-im-feishu`
>
> **版本**: V1.0
>
> **Jira 映射**: —（中国特色能力）
>
> **许可**: 🆓 开源基础版
>
> **最后更新**: 2026-06-02

---

## 1. 产品概述

为企业提供飞书渠道的消息通知，降低"在系统里看不到消息"导致的效率损失。V1.0 聚焦消息通知，V2.0+ 可扩展组织架构同步和会话内操作。

---

## 2. 数据库设计

```sql
-- 飞书连接配置
CREATE TABLE pl_feishu_config (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_name     VARCHAR(128) NOT NULL,
    app_id          VARCHAR(128) COMMENT '飞书应用 App ID',
    app_secret      VARCHAR(256) COMMENT '飞书应用 App Secret（加密存储）',
    webhook_url     VARCHAR(512) COMMENT '群机器人 Webhook URL',
    is_enabled      BOOLEAN DEFAULT TRUE,
    notify_project_ids JSON COMMENT '启用通知的项目 ID 列表，NULL 表示全项目',
    created_by      BIGINT NOT NULL,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 通知模板
CREATE TABLE pl_feishu_template (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    event_type      VARCHAR(64) NOT NULL COMMENT 'TASK_ASSIGNED/TASK_STATUS_CHANGED/COMMENT_ADDED/AT_MENTION',
    card_template   JSON NOT NULL COMMENT '消息卡片模板（飞书 Card JSON）',
    is_enabled      BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (config_id) REFERENCES pl_feishu_config(id)
);

-- 通知日志
CREATE TABLE pl_feishu_log (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_id       BIGINT NOT NULL,
    event_type      VARCHAR(64) NOT NULL,
    payload         JSON COMMENT '发送的 payload',
    status          VARCHAR(16) NOT NULL COMMENT 'SENT/FAILED',
    error_message   VARCHAR(512),
    sent_at         DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

---

## 3. 通知事件与消息卡片

| 事件 | 触发时机 | 消息内容 |
|------|---------|---------|
| `TASK_ASSIGNED` | 任务被指派 | 【任务指派】{issue_key} {title} 已指派给你 |
| `TASK_STATUS_CHANGED` | 任务状态变更 | 【状态变更】{issue_key} 已从 {from_status} 变更为 {to_status} |
| `COMMENT_ADDED` | 新增评论 | 【新评论】{issue_key} 有新评论 |
| `AT_MENTION` | @提及 | 【@{user_name} 在评论中@了你】{issue_key} |

**消息卡片示例**：

```json
{
  "msg_type": "interactive",
  "card": {
    "header": {
      "title": { "tag": "plain_text", "content": "【任务指派】LEST-123" },
      "template": "blue"
    },
    "elements": [
      { "tag": "div", "text": { "tag": "lark_md", "content": "**任务标题**：修复登录页面白屏问题\n**指派给**：@{at_user}\n**优先级**：P1\n**截止日期**：2026-06-10" }},
      { "tag": "action", "actions": [
        { "tag": "button", "text": { "tag": "plain_text", "content": "立即处理" }, "type": "primary", "url": "{issue_detail_url}" }
      ]}
    ]
  }
}
```

---

## 4. 用户故事

| ID | 用户故事 | 验收标准 |
|----|---------|---------|
| US-FEISHU-001 | 作为系统管理员，我希望配置飞书 Webhook（群机器人），系统发送测试消息验证连接 | 测试消息发送成功后显示 |
| US-FEISHU-002 | 作为系统管理员，我希望为每个事件类型配置消息卡片模板（标题/内容/按钮） | 模板保存后通知内容按模板生成 |
| US-FEISHU-003 | 作为员工，我希望在任务被指派时收到飞书消息卡片 | 卡片包含任务标题/指派人/优先级/截止日期 |
| US-FEISHU-004 | 作为员工，我希望在任务状态变更时收到飞书消息 | 消息显示从哪个状态变更为哪个状态 |
| US-FEISHU-005 | 作为员工，我希望在评论被@时收到飞书消息 | 消息包含评论摘要和回复链接 |
| US-FEISHU-006 | 作为系统管理员，我希望查看飞书通知日志，了解发送成功/失败情况 | 日志列表支持按时间/状态筛选 |

---

## 5. API 设计

| 接口 | 方法 | 说明 |
|------|------|------|
| `/feishu/config` | GET/POST | 飞书配置列表/创建 |
| `/feishu/config/{id}` | GET/PUT/DELETE | 配置详情/更新/删除 |
| `/feishu/config/{id}/test` | POST | 发送测试消息 |
| `/feishu/templates` | GET/POST/PUT | 通知模板列表/创建/更新 |
| `/feishu/logs` | GET | 通知日志列表 |

---


## 6. 前端设计

### 6.1 页面结构

> **说明**：前端设计细节在开发阶段细化。此处预留章节结构。

### 6.2 关键组件

| 组件 | 说明 |
|------|------|
| — | — |

### 6.3 状态管理

| 状态 | 说明 |
|------|------|
| — | — |

---


## 7. 与其他模块的集成

### 7.1 集成点

| 集成模块 | 集成方式 | 说明 |
|---------|---------|------|
| 任务管理 | LEST Core API | 与任务生命周期联动 |

### 7.2 事件订阅

| 事件 | 处理逻辑 |
|------|---------|
| — | — |

---

## 8. 错误码

| 模块 | 错误码范围 | 说明 |
|------|-----------|------|
| 飞书 | 10200-10299 | 插件 飞书 模块 |

## 9. 版本历史

| 版本 | 日期 | 修改内容 | 作者 |
|------|------|---------|------|
| V1.0 | 2026-08 | 初始版本 | - |
