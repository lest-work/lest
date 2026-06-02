# 钉钉集成（lest-im-dingtalk）PRD — V1.0

> **插件 ID**: `lest-im-dingtalk`
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

通过钉钉消息触达与协作入口，提升任务流转效率与及时性。V1.0 聚焦机器人 Webhook 通知。

---

## 2. 数据库设计

```sql
-- 钉钉连接配置
CREATE TABLE pl_dingtalk_config (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_name     VARCHAR(128) NOT NULL,
    agent_id        VARCHAR(32) COMMENT '钉钉微应用 AgentId',
    app_key         VARCHAR(128) COMMENT '钉钉应用 AppKey',
    app_secret      VARCHAR(256) COMMENT '钉钉应用 AppSecret（加密存储）',
    webhook_url     VARCHAR(512) COMMENT '群机器人 Webhook URL',
    is_enabled      BOOLEAN DEFAULT TRUE,
    notify_project_ids JSON COMMENT '启用通知的项目 ID 列表，NULL 表示全项目',
    created_by      BIGINT NOT NULL,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 通知模板
CREATE TABLE pl_dingtalk_template (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    event_type      VARCHAR(64) NOT NULL,
    msg_type        VARCHAR(16) DEFAULT 'markdown' COMMENT 'text/markdown/actionCard',
    content         TEXT NOT NULL COMMENT '消息内容，支持 Markdown',
    is_enabled      BOOLEAN DEFAULT TRUE
);

-- 通知日志
CREATE TABLE pl_dingtalk_log (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_id       BIGINT NOT NULL,
    event_type      VARCHAR(64) NOT NULL,
    payload         JSON,
    status          VARCHAR(16) NOT NULL COMMENT 'SENT/FAILED',
    error_message   VARCHAR(512),
    sent_at         DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

---

## 3. 通知事件与消息格式

| 事件 | 消息格式 | 内容示例 |
|------|---------|---------|
| `TASK_ASSIGNED` | Markdown | ### 【任务指派】\n**LEST-123** 修复登录页面白屏问题\n指派给你 \| P1 \| 截止 2026-06-10 |
| `TASK_STATUS_CHANGED` | Markdown | ### 【状态变更】\n**LEST-123** Open → In Progress |
| `COMMENT_ADDED` | Markdown | ### 【新评论】\n@{user_name} 在 **LEST-123** 中评论了你 |
| `AT_MENTION` | Markdown | ### 【@{user_name} @了你】\n{comment_preview} |

---

## 4. 用户故事

| ID | 用户故事 | 验收标准 |
|----|---------|---------|
| US-DING-001 | 作为系统管理员，我希望配置钉钉应用凭证（AppKey + AppSecret）和 Webhook URL | 配置保存后测试连接 |
| US-DING-002 | 作为系统管理员，我希望配置每种事件的通知模板（Markdown 格式） | 模板保存后按模板发送 |
| US-DING-003 | 作为员工，我希望在任务被指派时收到钉钉消息 | 消息包含任务标题/指派人/优先级/截止日期 |
| US-DING-004 | 作为员工，我希望点击消息中的按钮跳转到 LEST 任务详情页 | 跳转链接正确且在钉钉内打开 |
| US-DING-005 | 作为系统管理员，我希望查看钉钉通知日志 | 日志列表支持按时间/状态筛选 |

---

## 5. API 设计

| 接口 | 方法 | 说明 |
|------|------|------|
| `/dingtalk/config` | GET/POST | 钉钉配置列表/创建 |
| `/dingtalk/config/{id}` | GET/PUT/DELETE | 配置详情/更新/删除 |
| `/dingtalk/config/{id}/test` | POST | 发送测试消息 |
| `/dingtalk/templates` | GET/POST/PUT | 通知模板列表/创建/更新 |
| `/dingtalk/logs` | GET | 通知日志列表 |

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
| 钉钉 | 10300-10399 | 插件 钉钉 模块 |

## 9. 版本历史

| 版本 | 日期 | 修改内容 | 作者 |
|------|------|---------|------|
| V1.0 | 2026-08 | 初始版本 | - |
