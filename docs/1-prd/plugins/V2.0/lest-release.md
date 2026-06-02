# 发布管理增强（lest-release）PRD — V2.0

> **插件 ID**: `lest-release`
>
> **版本**: V2.0
>
> **Jira 映射**: Jira Releases / Version Management
>
> **许可**: 🆓 开源基础版
>
> **最后更新**: 2026-06-02

---

## 1. 产品概述

在 V1.0 发布管理基础上增强：发布计划、制品管理、变更记录（Release Notes）与 CI/CD 自动化联动。

---

## 2. 数据库设计（V2.0 新增）

```sql
-- 发布计划（增强版）
CREATE TABLE pl_release_plan (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    release_key     VARCHAR(64) NOT NULL UNIQUE,
    name            VARCHAR(128) NOT NULL COMMENT '如：v2.5.0',
    version_type    VARCHAR(16) NOT NULL COMMENT 'MAJOR/MINOR/PATCH',
    target_date     DATE COMMENT '计划发布日期',
    actual_date     DATE COMMENT '实际发布日期',
    status          VARCHAR(16) NOT NULL DEFAULT 'PLANNING' COMMENT 'PLANNING/CODE_FREEZE/RELEASE/ARCHIVED',
    project_id      BIGINT NOT NULL,
    description     TEXT,
    created_by      BIGINT NOT NULL,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES project(id)
);

-- 发布变更记录（Release Notes）
CREATE TABLE pl_release_changelog (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    release_id      BIGINT NOT NULL,
    changelog_type  VARCHAR(16) NOT NULL COMMENT 'FEATURE/BUGFIX/IMPROVEMENT/SECURITY/REMOVED',
    issue_id        BIGINT COMMENT '关联的 Issue',
    change_summary  VARCHAR(256) NOT NULL,
    change_detail   TEXT,
    author_id       BIGINT NOT NULL,
    FOREIGN KEY (release_id) REFERENCES pl_release(id),
    FOREIGN KEY (issue_id) REFERENCES issue(id)
);

-- 制品关联（扩展 V1.0 lest-file）
CREATE TABLE pl_release_artifact (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    release_id      BIGINT NOT NULL,
    artifact_name   VARCHAR(256) NOT NULL,
    artifact_type   VARCHAR(32) NOT NULL COMMENT 'DOCKER_IMAGE/JAR/WAR/NPM/ZIP',
    artifact_url    VARCHAR(512),
    digest          VARCHAR(128) COMMENT '镜像 SHA256',
    build_id        VARCHAR(64) COMMENT 'CI 构建号',
    build_url       VARCHAR(512) COMMENT 'CI 构建链接',
    file_id         BIGINT COMMENT 'LEST 文件 ID',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (release_id) REFERENCES pl_release(id)
);

-- 发布审批记录
CREATE TABLE pl_release_approval (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    release_id      BIGINT NOT NULL,
    approval_type   VARCHAR(16) NOT NULL COMMENT 'CODE_FREEZE/RELEASE',
    approver_id     BIGINT NOT NULL,
    status          VARCHAR(16) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/APPROVED/REJECTED',
    comment         TEXT,
    decided_at      DATETIME,
    FOREIGN KEY (release_id) REFERENCES pl_release(id),
    FOREIGN KEY (approver_id) REFERENCES sys_user(user_id)
);
```

---

## 3. 用户故事

| ID | 用户故事 | 验收标准 |
|----|---------|---------|
| US-REL-001 | 作为发布经理，我希望在发布计划中聚合该版本的所有 Issue，形成变更清单 | 变更清单按 FEATURE/BUGFIX 分类 |
| US-REL-002 | 作为发布经理，我希望系统自动生成 Release Notes（按 Issue 类型汇总） | Release Notes 可编辑，支持导出 Markdown |
| US-REL-003 | 作为发布经理，我希望将 CI 构建的制品（镜像/JAR）关联到发布记录 | 制品列表显示名称/版本/Digest/构建链接 |
| US-REL-004 | 作为发布经理，我希望配置 Code Freeze 审批（需技术负责人审批后才能发布） | 审批通过后才能将状态变更为 RELEASE |
| US-REL-005 | 作为开发者，我希望在任务详情页看到该任务属于哪个发布版本 | 发布标签在任务卡片上显示 |
| US-REL-006 | 作为发布经理，我希望在发布完成后自动通知相关人员（邮件/IM） | 通知包含 Release Notes 链接 |

---

## 4. API 设计

| 接口 | 方法 | 说明 |
|------|------|------|
| `/release/plans` | GET/POST | 发布计划列表/创建 |
| `/release/plans/{id}` | GET/PUT/DELETE | 发布计划详情/更新/删除 |
| `/release/plans/{id}/issues` | GET/POST/DELETE | 发布关联的 Issue |
| `/release/plans/{id}/changelog` | GET/POST | 变更记录列表/添加 |
| `/release/plans/{id}/changelog/generate` | POST | 自动生成变更记录 |
| `/release/plans/{id}/artifacts` | GET/POST | 制品列表/添加 |
| `/release/plans/{id}/approval` | POST | 提交审批 |
| `/release/plans/{id}/approval/{approvalId}` | PUT | 审批通过/驳回 |
| `/release/plans/{id}/publish` | POST | 发布（变更状态为 RELEASED） |
| `/release/plans/{id}/export/{format}` | GET | 导出 Release Notes（Markdown/HTML/PDF） |

## 5. 错误码

| 模块 | 错误码范围 | 说明 |
|------|-----------|------|
| 发布 | 11200-11299 | 插件 发布 模块 |

## 6. 版本历史

| 版本 | 日期 | 修改内容 | 作者 |
|------|------|---------|------|
| V1.0 | 2026年底 | 初始版本 | - |
