# LDAP 集成 — V5.0 PRD

> **模块**: Core V5.0 | **许可**: 🔒 商业版

## 1. 产品概述

### 1.1 目标

帮助企业将现有的 Active Directory / OpenLDAP 目录服务与 LEST 无缝集成，实现用户和组织的自动同步。

### 1.2 功能范围

|| 功能 | 说明 | 优先级 |
||------|------|--------|
| LDAP 连接配置 | 支持 AD / OpenLDAP | P0 |
| 用户同步 | 全量 + 增量同步 | P0 |
| 组织架构同步 | 部门/OU 树形结构 | P0 |
| 用户组映射 | LDAP Group → LEST 角色 | P0 |
| 同步调度 | Cron 表达式配置 | P1 |
| 密码策略 | LDAP 托管密码 | P2 |

## 2. 数据库设计

```sql
CREATE TABLE ldap_connection (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    name                VARCHAR(128) NOT NULL,
    host                VARCHAR(255) NOT NULL,
    port                INT DEFAULT 389,
    ssl                 BOOLEAN DEFAULT FALSE,
    base_dn             VARCHAR(255) NOT NULL,
    bind_dn             VARCHAR(255) NOT NULL,
    bind_password_enc   VARCHAR(512) NOT NULL COMMENT '加密存储',
    user_search_filter  VARCHAR(512) DEFAULT '(objectClass=user)',
    group_search_filter VARCHAR(512) DEFAULT '(objectClass=group)',
    sync_interval       INT DEFAULT 3600 COMMENT '秒',
    enabled             BOOLEAN DEFAULT TRUE,
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE ldap_sync_log (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    connection_id       BIGINT NOT NULL,
    sync_type          VARCHAR(16) COMMENT 'FULL/INCREMENTAL',
    sync_start         DATETIME NOT NULL,
    sync_end           DATETIME,
    status             VARCHAR(16) COMMENT 'RUNNING/SUCCESS/FAILED',
    users_synced       INT DEFAULT 0,
    errors             TEXT,
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ldap_user_mapping (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    connection_id   BIGINT NOT NULL,
    ldap_attr       VARCHAR(64) NOT NULL,
    lest_field      VARCHAR(64) NOT NULL,
    mapping_type    VARCHAR(16) COMMENT 'DIRECT/COMPUTED',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ldap_group_mapping (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    connection_id   BIGINT NOT NULL,
    ldap_group_dn  VARCHAR(255) NOT NULL,
    lest_role       VARCHAR(64) NOT NULL,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

## 3. 同步流程

```
定时任务触发（每小时/每天）
  ↓
连接 LDAP 服务器
  ↓
全量同步 / 增量同步（基于 lastSyncTime）
  ↓
用户映射处理（LDAP 属性 → LEST 用户字段）
  ↓
组映射处理（LDAP Group → LEST 角色）
  ↓
写入 LEST 数据库（创建/更新用户）
  ↓
记录同步日志
```
