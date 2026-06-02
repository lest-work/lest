# Jira 双向同步（lest-jira-sync）PRD — V2.0

> **插件 ID**: `lest-jira-sync`
>
> **版本**: V2.0
>
> **Jira 映射**: Jira Integrations / External System Sync
>
> **许可**: 🔒 商业版
>
> **最后更新**: 2026-06-02

---

## 1. 产品概述

支持 LEST 与 Jira（Cloud / Data Center）的双向同步，实现企业从 Jira 到 LEST 的平滑迁移。

---

## 2. 数据库设计

```sql
-- Jira 连接配置
CREATE TABLE pl_jira_config (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_name     VARCHAR(128) NOT NULL,
    jira_url        VARCHAR(256) NOT NULL COMMENT 'Jira 实例 URL',
    auth_type       VARCHAR(16) NOT NULL COMMENT 'BASIC/OAUTH/PAT',
    auth_config     JSON NOT NULL COMMENT '认证信息（PAT/API Token/OAuth Config）',
    is_enabled      BOOLEAN DEFAULT TRUE,
    last_sync_at    DATETIME,
    created_by      BIGINT NOT NULL,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 项目映射
CREATE TABLE pl_jira_project_map (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_id       BIGINT NOT NULL,
    jira_project_key VARCHAR(32) NOT NULL,
    lest_project_id BIGINT NOT NULL,
    sync_direction  VARCHAR(16) DEFAULT 'BIDIRECTIONAL' COMMENT 'LEST_TO_JIRA/JIRA_TO_LEST/BIDIRECTIONAL',
    FOREIGN KEY (config_id) REFERENCES pl_jira_config(id),
    FOREIGN KEY (lest_project_id) REFERENCES project(id)
);

-- 字段映射规则
CREATE TABLE pl_jira_field_map (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_id       BIGINT NOT NULL,
    jira_field      VARCHAR(128) NOT NULL COMMENT 'Jira 字段名',
    lest_field      VARCHAR(128) NOT NULL COMMENT 'LEST 字段标识',
    transform_expr  VARCHAR(512) COMMENT '转换表达式',
    direction       VARCHAR(16) DEFAULT 'BOTH' COMMENT 'LEST_TO_JIRA/JIRA_TO_LEST/BOTH',
    FOREIGN KEY (config_id) REFERENCES pl_jira_config(id)
);

-- 状态映射
CREATE TABLE pl_jira_status_map (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_id       BIGINT NOT NULL,
    jira_status     VARCHAR(64) NOT NULL,
    lest_status_id  BIGINT NOT NULL,
    FOREIGN KEY (config_id) REFERENCES pl_jira_config(id),
    FOREIGN KEY (lest_status_id) REFERENCES workflow_status(id)
);

-- 同步任务记录
CREATE TABLE pl_jira_sync_log (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_id       BIGINT NOT NULL,
    sync_type       VARCHAR(16) NOT NULL COMMENT 'FULL/INCREMENTAL/MANUAL',
    status          VARCHAR(16) NOT NULL COMMENT 'RUNNING/SUCCESS/FAILED/PARTIAL',
    issues_synced   INT DEFAULT 0,
    issues_failed   INT DEFAULT 0,
    started_at      DATETIME,
    finished_at     DATETIME,
    error_detail    TEXT,
    FOREIGN KEY (config_id) REFERENCES pl_jira_config(id)
);

-- Issue 同步关联（用于追踪 LEST Issue ↔ Jira Issue 的对应关系）
CREATE TABLE pl_jira_issue_map (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_id       BIGINT NOT NULL,
    lest_issue_id   BIGINT NOT NULL,
    jira_issue_key  VARCHAR(64) NOT NULL,
    last_synced_at  DATETIME,
    FOREIGN KEY (config_id) REFERENCES pl_jira_config(id),
    FOREIGN KEY (lest_issue_id) REFERENCES issue(id)
);
```

---

## 3. 同步策略

| 策略 | 说明 |
|------|------|
| **单向 LEST→Jira** | Jira 作为只读下游，只同步 LEST 变更 |
| **单向 Jira→LEST** | 从 Jira 迁移场景，Jira 变更同步到 LEST |
| **双向同步** | 两者实时同步，支持冲突检测 |
| **定时同步** | Cron 表达式配置，默认每 5 分钟增量同步 |
| **手动同步** | 管理员手动触发全量/增量同步 |

**冲突解决策略**：
- `LEST_WINS`：以 LEST 数据为准
- `JIRA_WINS`：以 Jira 数据为准
- `LATEST_WINS`：以最新更新时间为准
- `MANUAL`：冲突时暂停，等待人工处理

---

## 4. 用户故事

| ID | 用户故事 | 验收标准 |
|----|---------|---------|
| US-JIRA-001 | 作为系统管理员，我希望配置 Jira 连接（URL + PAT），LEST 自动验证连接 | 连接测试显示成功/失败及错误原因 |
| US-JIRA-002 | 作为系统管理员，我希望配置项目映射（选择 Jira 项目 ↔ LEST 项目） | 映射保存后可配置字段/状态映射 |
| US-JIRA-003 | 作为系统管理员，我希望配置字段映射规则（哪些 Jira 字段同步到 LEST） | 支持系统字段和自定义字段映射 |
| US-JIRA-004 | 作为系统管理员，我希望配置状态映射（Jira Status → LEST Status） | 状态映射决定同步时的状态转换 |
| US-JIRA-005 | 作为系统管理员，我希望配置同步策略（方向/频率/冲突解决） | 策略配置后按计划自动执行 |
| US-JIRA-006 | 作为系统管理员，我希望手动触发全量同步或增量同步 | 同步进度实时显示，完成后显示统计 |
| US-JIRA-007 | 作为系统管理员，我希望查看同步日志，了解每次同步的详情（成功数/失败数/冲突数） | 日志支持按时间/状态筛选 |
| US-JIRA-008 | 作为系统管理员，我希望在 Jira Issue 和 LEST Issue 之间双向跳转 | 任务详情页显示"在 Jira 中查看"按钮 |

---

## 5. API 设计

| 接口 | 方法 | 说明 |
|------|------|------|
| `/jira-sync/config` | GET/PUT | 同步配置读取/更新 |
| `/jira-sync/config/test` | POST | 测试 Jira 连接 |
| `/jira-sync/projects` | GET/POST/DELETE | 项目映射列表/添加/删除 |
| `/jira-sync/fields` | GET/POST/DELETE | 字段映射列表/添加/删除 |
| `/jira-sync/statuses` | GET/POST/DELETE | 状态映射列表/添加/删除 |
| `/jira-sync/sync` | POST | 触发同步（FULL/INCREMENTAL） |
| `/jira-sync/logs` | GET | 同步日志列表 |
| `/jira-sync/logs/{id}` | GET | 同步日志详情 |
| `/jira-sync/jira-browse/{issueKey}` | GET | 获取 Jira Issue 详情并跳转 |

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
| Jira同步 | 10500-10599 | 插件 Jira同步 模块 |

## 9. 版本历史

| 版本 | 日期 | 修改内容 | 作者 |
|------|------|---------|------|
| V1.0 | 2026年底 | 初始版本 | - |
