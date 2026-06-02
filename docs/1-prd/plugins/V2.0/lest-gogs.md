# Gogs 集成（lest-gogs）PRD — V2.0

> **插件 ID**: `lest-gogs`
>
> **版本**: V2.0
>
> **许可**: 🆓 开源基础版
>
> **最后更新**: 2026-06-02

---

## 1. 产品概述

支持 Gogs 仓库与任务关联（提交、PR）以及 Webhook 事件回写。功能与 lest-gitea 基本一致，使用 Gogs 专有 API。

---

## 2. 数据库设计

```sql
-- Gogs 连接配置
CREATE TABLE pl_gogs_connection (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    name            VARCHAR(128) NOT NULL,
    gogs_url        VARCHAR(256) NOT NULL,
    api_token       VARCHAR(256) NOT NULL COMMENT '加密存储',
    is_enabled      BOOLEAN DEFAULT TRUE,
    created_by      BIGINT NOT NULL,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 仓库绑定
CREATE TABLE pl_gogs_repo (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    connection_id   BIGINT NOT NULL,
    repo_owner      VARCHAR(128) NOT NULL,
    repo_name       VARCHAR(128) NOT NULL,
    lest_project_id BIGINT NOT NULL,
    auto_link       BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (connection_id) REFERENCES pl_gogs_connection(id),
    FOREIGN KEY (lest_project_id) REFERENCES project(id)
);

-- 提交关联
CREATE TABLE pl_gogs_commit_link (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    repo_id         BIGINT NOT NULL,
    commit_sha      VARCHAR(64) NOT NULL,
    commit_message  VARCHAR(512),
    author_name     VARCHAR(128),
    committed_at    DATETIME,
    author_id       BIGINT,
    FOREIGN KEY (repo_id) REFERENCES pl_gogs_repo(id)
);

-- PR 关联
CREATE TABLE pl_gogs_pr_link (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    repo_id         BIGINT NOT NULL,
    pr_number       INT NOT NULL,
    pr_title        VARCHAR(256),
    pr_state        VARCHAR(16),
    author_name     VARCHAR(128),
    merged_at       DATETIME,
    author_id       BIGINT,
    FOREIGN KEY (repo_id) REFERENCES pl_gogs_repo(id)
);
```

---

## 3. 用户故事

| ID | 用户故事 | 验收标准 |
|----|---------|---------|
| US-GOGS-001 | 作为开发者，我希望配置 Gogs 连接并绑定仓库到 LEST 项目 | 连接测试通过后绑定成功 |
| US-GOGS-002 | 作为开发者，我希望提交信息中的 `LEST-123` 自动关联任务 | 提交推送后任务详情页自动更新 |
| US-GOGS-003 | 作为开发者，我希望在 Gogs PR 中写入 `LEST-123`，系统自动关联 | PR 打开/合并时自动更新任务状态 |
| US-GOGS-004 | 作为系统管理员，我希望配置 Webhook 接收 Gogs 事件 | push/PR 事件自动更新任务动态 |

---

## 4. API 设计

| 接口 | 方法 | 说明 |
|------|------|------|
| `/gogs/connections` | GET/POST | 连接列表/创建 |
| `/gogs/connections/{id}/test` | POST | 测试连接 |
| `/gogs/repos` | GET/POST | 仓库列表/绑定 |
| `/gogs/repos/{id}/commits` | GET | 提交列表 |
| `/gogs/repos/{id}/pulls` | GET | PR 列表 |
| `/gogs/webhook/{repoId}` | POST | 接收 Webhook 事件 |

---

## 5. 错误码

| 模块 | 错误码范围 | 说明 |
|------|-----------|------|
| Gogs | 10800-10899 | 插件 Gogs 模块 |

## 6. 版本历史

| 版本 | 日期 | 修改内容 | 作者 |
|------|------|---------|------|
| V1.0 | 2026年底 | 初始版本 | - |
