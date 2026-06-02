# Gitea 集成（lest-gitea）PRD — V2.0

> **插件 ID**: `lest-gitea`
>
> **版本**: V2.0
>
> **许可**: 🆓 开源基础版
>
> **最后更新**: 2026-06-02

---

## 1. 产品概述

支持 Gitea 仓库与任务的关联（提交、PR/MR、分支）以及 Webhook 事件回写。

---

## 2. 数据库设计

```sql
-- Gitea 连接配置
CREATE TABLE pl_gitea_connection (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    name            VARCHAR(128) NOT NULL,
    gitea_url       VARCHAR(256) NOT NULL,
    api_token       VARCHAR(256) NOT NULL COMMENT '加密存储',
    is_enabled      BOOLEAN DEFAULT TRUE,
    created_by      BIGINT NOT NULL,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 仓库绑定
CREATE TABLE pl_gitea_repo (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    connection_id   BIGINT NOT NULL,
    repo_owner      VARCHAR(128) NOT NULL,
    repo_name       VARCHAR(128) NOT NULL,
    lest_project_id BIGINT NOT NULL,
    auto_link       BOOLEAN DEFAULT TRUE COMMENT '自动解析提交信息关联任务',
    FOREIGN KEY (connection_id) REFERENCES pl_gitea_connection(id),
    FOREIGN KEY (lest_project_id) REFERENCES project(id)
);

-- 提交 → 任务关联
CREATE TABLE pl_gitea_commit_link (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    repo_id         BIGINT NOT NULL,
    commit_sha      VARCHAR(64) NOT NULL,
    commit_message  VARCHAR(512),
    author_name     VARCHAR(128),
    committed_at    DATETIME,
    author_id       BIGINT COMMENT '映射的 LEST 用户',
    FOREIGN KEY (repo_id) REFERENCES pl_gitea_repo(id)
);

-- MR/PR → 任务关联
CREATE TABLE pl_gitea_pr_link (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    repo_id         BIGINT NOT NULL,
    pr_number       INT NOT NULL,
    pr_title        VARCHAR(256),
    pr_state        VARCHAR(16),
    source_branch   VARCHAR(128),
    target_branch   VARCHAR(128),
    author_name     VARCHAR(128),
    merged_at       DATETIME,
    author_id       BIGINT COMMENT '映射的 LEST 用户',
    FOREIGN KEY (repo_id) REFERENCES pl_gitea_repo(id)
);
```

---

## 3. 用户故事

| ID | 用户故事 | 验收标准 |
|----|---------|---------|
| US-GIT-001 | 作为开发者，我希望在任务详情页查看关联的提交和 PR 列表 | 列表显示 SHA/标题/作者/时间 |
| US-GIT-002 | 作为开发者，我希望在提交信息中写入 `LEST-123`，系统自动关联该任务 | 提交推送后任务详情页自动更新 |
| US-GIT-003 | 作为开发者，我希望在 Gitea PR 中写入 `LEST-123`，系统自动将 PR 关联到任务 | PR 打开/合并时自动更新任务状态 |
| US-GIT-004 | 作为系统管理员，我希望配置 Webhook，实现 Gitea 事件实时回写到 LEST | push/PR/MR 事件自动更新任务动态 |
| US-GIT-005 | 作为开发者，我希望在 LEST 看板卡片上看到分支名和最新 CI 状态 | CI 状态（pending/success/failure）在卡片上展示 |

---

## 4. API 设计

| 接口 | 方法 | 说明 |
|------|------|------|
| `/gitea/connections` | GET/POST | 连接列表/创建 |
| `/gitea/connections/{id}/test` | POST | 测试连接 |
| `/gitea/repos` | GET/POST | 仓库列表/绑定 |
| `/gitea/repos/{id}/commits` | GET | 提交列表 |
| `/gitea/repos/{id}/pulls` | GET | PR/MR 列表 |
| `/gitea/webhook/{repoId}` | POST | 接收 Webhook 事件 |

---

## 5. 错误码

| 模块 | 错误码范围 | 说明 |
|------|-----------|------|
| Gitea | 10700-10799 | 插件 Gitea 模块 |

## 6. 版本历史

| 版本 | 日期 | 修改内容 | 作者 |
|------|------|---------|------|
| V1.0 | 2026年底 | 初始版本 | - |
