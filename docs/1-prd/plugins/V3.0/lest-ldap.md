# lest-ldap — LDAP 集成插件

> **插件 ID**: `lest-ldap`
>
> **版本**: V3.0
>
> **Jira 映射**: Atlassian Crowd / Jira LDAP Directory（商业功能）
>
> **许可**: 🔒 商业版
>
> **最后更新**: 2026-06-02

---

## 1. 产品概述

### 1.1 目标与愿景

LEST LDAP 插件帮助企业将现有的 Active Directory / OpenLDAP 目录服务与 LEST 无缝集成，实现用户和组织的自动同步、基于 LDAP 组的权限映射，以及 Single Sign-On（需配合 SSO 插件）。

### 1.2 与竞品对标

| 功能 | Jira Crowd | LEST LDAP | 说明 |
|------|-----------|----------|------|
| LDAP 连接配置 | ✅ | ✅ | 支持 AD / OpenLDAP |
| 用户同步 | ✅ | ✅ | 全量 + 增量同步 |
| 组织架构同步 | ✅ | ✅ | 部门/OU 树形结构 |
| 用户组映射 | ✅ | ✅ | LDAP Group → LEST 角色 |
| 密码策略 | ✅ | ✅ | LDAP 托管密码 |
| 同步调度 | ✅ | ✅ | Cron 表达式配置 |
| 同步日志 | ✅ | ✅ | 详细同步记录 |

---

## 2. 功能范围

### 2.1 核心功能

| 功能 | 说明 | 优先级 |
|------|------|--------|
| LDAP 连接配置 | AD / OpenLDAP 连接参数（Host/Port/SSL/Base DN/Bind DN） | P0 |
| 用户同步 | LDAP 用户 → LEST 用户同步（全量/增量） | P0 |
| 组织架构同步 | LDAP OU 结构 → LEST 部门同步 | P0 |
| 用户组映射 | LDAP Group → LEST 项目角色映射规则 | P0 |
| 同步调度 | Cron 表达式配置同步时间（默认每日凌晨） | P0 |
| 同步日志 | 每次同步记录：新增/更新/禁用用户列表 | P0 |
| 用户属性映射 | LDAP 属性（mail/title/department）→ LEST 用户字段 | P1 |
| 同步冲突处理 | LDAP 删除用户时的 LEST 处理策略（禁用/删除） | P1 |
| 实时同步 | LDAP Change Notifications（AD 主动推送变更） | P2 |

---

## 3. 数据库设计

### 3.1 表结构

```sql
-- LDAP 连接配置
CREATE TABLE pl_ldap_config (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_key     VARCHAR(64) NOT NULL UNIQUE DEFAULT 'default',
    ldap_type       VARCHAR(16) NOT NULL COMMENT 'ACTIVE_DIRECTORY/OPENLDAP',
    host            VARCHAR(128) NOT NULL,
    port            INT DEFAULT 389,
    use_ssl         BOOLEAN DEFAULT FALSE,
    bind_dn         VARCHAR(256) NOT NULL COMMENT '管理员 DN',
    bind_password   VARCHAR(256) NOT NULL COMMENT '加密存储',
    base_dn         VARCHAR(256) NOT NULL COMMENT '用户搜索根路径',
    user_filter     VARCHAR(512) DEFAULT '(objectClass=user)' COMMENT '用户过滤条件',
    group_filter    VARCHAR(512) DEFAULT '(objectClass=group)' COMMENT '组过滤条件',
    user_mapping    JSON NOT NULL COMMENT 'LDAP 属性 → LEST 用户字段映射',
    group_mapping   JSON NOT NULL COMMENT 'LDAP 组 → LEST 角色映射',
    is_enabled      BOOLEAN DEFAULT TRUE,
    created_by      BIGINT NOT NULL,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 同步日志
CREATE TABLE pl_ldap_sync_log (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    sync_type       VARCHAR(16) NOT NULL COMMENT 'FULL/INCREMENTAL/MANUAL',
    status          VARCHAR(16) NOT NULL COMMENT 'RUNNING/SUCCESS/FAILED',
    started_at      DATETIME NOT NULL,
    finished_at     DATETIME,
    users_added     INT DEFAULT 0,
    users_updated   INT DEFAULT 0,
    users_disabled  INT DEFAULT 0,
    groups_synced   INT DEFAULT 0,
    error_message   TEXT,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- LDAP 组 → LEST 角色映射规则
CREATE TABLE pl_ldap_group_mapping (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    ldap_group_dn   VARCHAR(256) NOT NULL COMMENT 'LDAP 组 DN',
    ldap_group_name  VARCHAR(128) NOT NULL,
    lest_role_key   VARCHAR(64) NOT NULL COMMENT 'LEST 角色标识',
    project_id      BIGINT DEFAULT NULL COMMENT '项目级角色映射（可选）',
    auto_assign     BOOLEAN DEFAULT TRUE COMMENT '新用户自动分配',
    FOREIGN KEY (project_id) REFERENCES project(id)
);

-- 用户 LDAP 关联记录
CREATE TABLE pl_ldap_user (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    lest_user_id    BIGINT NOT NULL UNIQUE,
    ldap_dn         VARCHAR(256) NOT NULL UNIQUE COMMENT 'LDAP DN',
    ldap_guid       VARCHAR(64) COMMENT 'AD ObjectGUID / LDAP UID',
    last_sync_at    DATETIME,
    sync_status     VARCHAR(16) DEFAULT 'ACTIVE' COMMENT 'ACTIVE/DISABLED/DELETED',
    FOREIGN KEY (lest_user_id) REFERENCES sys_user(user_id)
);

-- 部门映射（LDAP OU → LEST 部门）
CREATE TABLE pl_ldap_dept_mapping (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    ldap_ou_dn      VARCHAR(256) NOT NULL,
    lest_dept_id     BIGINT NOT NULL,
    parent_ldap_ou_dn VARCHAR(256) COMMENT '父 LDAP OU DN',
    FOREIGN KEY (lest_dept_id) REFERENCES sys_dept(dept_id)
);
```

---

## 4. 用户故事

| ID | 用户故事 | 验收标准 |
|----|---------|---------|
| US-LDAP-001 | 作为系统管理员，我希望配置 LDAP 连接参数（Host/Port/SSL/Bind DN）并测试连接 | 连接测试通过后保存配置 |
| US-LDAP-002 | 作为系统管理员，我希望设置用户属性映射规则（LDAP mail → LEST email） | 映射规则在同步时生效 |
| US-LDAP-003 | 作为系统管理员，我希望配置 LDAP Group → LEST 角色映射规则 | 映射后同步用户自动分配 LEST 角色 |
| US-LDAP-004 | 作为系统管理员，我希望手动触发全量同步或增量同步 | 同步进度实时显示，完成后显示统计 |
| US-LDAP-005 | 作为系统管理员，我希望配置同步调度（Cron 表达式） | 调度在后台自动执行 |
| US-LDAP-006 | 作为系统管理员，我希望查看同步日志，了解每次同步的详情（新增/更新/禁用的用户） | 日志列表支持时间范围筛选 |
| US-LDAP-007 | 作为系统管理员，我希望配置 LDAP 用户删除时的处理策略（禁用或删除） | 策略在同步时自动应用 |
| US-LDAP-008 | 作为系统管理员，我希望在同步前预览变更（将 LDAP 数据与 LEST 数据对比） | 预览显示新增/变更/删除的用户列表 |
| US-LDAP-009 | 作为系统，我希望在用户首次登录时自动从 LDAP 同步其信息 | 用户信息自动填充，无需手动创建 |
| US-LDAP-010 | 作为系统，我希望在 LDAP 用户被禁用时自动禁用其 LEST 账号 | 禁用用户的 LEST 登录权限自动移除 |

---

## 5. API 设计

| 接口 | 方法 | 说明 |
|------|------|------|
| `/ldap/config` | GET/PUT | LDAP 配置读取/更新 |
| `/ldap/config/test` | POST | 测试 LDAP 连接 |
| `/ldap/sync` | POST | 触发同步（FULL/INCREMENTAL） |
| `/ldap/sync/preview` | POST | 同步预览 |
| `/ldap/sync/logs` | GET | 同步日志列表 |
| `/ldap/sync/logs/{id}` | GET | 同步日志详情 |
| `/ldap/mappings/groups` | GET/POST/DELETE | Group → 角色映射 |
| `/ldap/mappings/depts` | GET/POST/DELETE | OU → 部门映射 |
| `/ldap/schedule` | GET/PUT | 同步调度配置 |
| `/ldap/stats` | GET | 同步统计（累计用户数/上次同步时间等） |

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
| LDAP | 11600-11699 | 插件 LDAP 模块 |

## 9. 版本历史

| 版本 | 日期 | 修改内容 | 作者 |
|------|------|---------|------|
| V1.0 | 2027年中 | 初始版本 | - |
