# lest-multi-tenant — 多租户隔离插件

> **插件 ID**: `lest-multi-tenant`
>
> **版本**: V3.0
>
> **Jira 映射**: Jira Data Center Multi-Tenancy（商业功能）
>
> **许可**: 🔒 商业版
>
> **最后更新**: 2026-06-02

---

## 1. 产品概述

### 1.1 目标与愿景

LEST 多租户插件为 SaaS 服务商或大型集团提供多租户隔离能力。每个租户拥有独立的数据空间、独立的配置、独立的品牌（Logo/域名），租户之间完全隔离，平台管理员可以统一管理所有租户。

### 1.2 隔离策略

| 隔离维度 | 实现方式 | 说明 |
|---------|---------|------|
| 数据隔离 | Schema 隔离（每租户独立数据库 Schema） | 租户 A 无法访问租户 B 数据 |
| 认证隔离 | 租户专属 SSO/LDAP 配置 | 各租户独立身份认证 |
| 配置隔离 | 租户独立配置（Logo/域名/功能开关） | 配置互不影响 |
| 缓存隔离 | Redis key 添加租户前缀 | 租户缓存完全隔离 |
| 消息队列隔离 | Kafka topic 添加租户前缀 | 消息互不干扰 |
| 存储隔离 | MinIO bucket 按租户隔离 | 文件存储隔离 |

---

## 2. 功能范围

### 2.1 核心功能

| 功能 | 说明 | 优先级 |
|------|------|--------|
| 租户创建与管理 | 平台管理员创建/禁用/配置租户 | P0 |
| Schema 隔离 | 每租户独立数据库 Schema，租户间数据完全隔离 | P0 |
| 租户域名绑定 | 每个租户绑定独立域名（tenant.example.com） | P0 |
| 租户配置管理 | Logo/名称/功能开关/配额配置 | P0 |
| 租户配额管理 | 用户数/存储空间/API 调用量限制 | P0 |
| 租户健康检查 | 各租户可用性监控 | P0 |
| 租户数据导入/导出 | 租户数据迁移工具 | P1 |
| 租户监控仪表盘 | 活跃用户/存储/API 使用量趋势 | P1 |
| 跨租户数据迁移 | 租户间数据搬迁 | P2 |

---

## 3. 数据库设计

### 3.1 表结构

```sql
-- 租户（存储在 public schema）
CREATE TABLE pl_tenant (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_key     VARCHAR(64) NOT NULL UNIQUE COMMENT '租户唯一标识',
    tenant_name    VARCHAR(128) NOT NULL,
    schema_name    VARCHAR(64) NOT NULL UNIQUE COMMENT '数据库 Schema 名',
    domain         VARCHAR(256) COMMENT '租户绑定域名',
    logo_url       VARCHAR(512),
    contact_email  VARCHAR(128),
    contact_phone  VARCHAR(32),
    status         VARCHAR(16) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE/SUSPENDED/DELETED',
    created_at     DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 租户配置
CREATE TABLE pl_tenant_config (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_id       BIGINT NOT NULL UNIQUE,
    config_key      VARCHAR(64) NOT NULL,
    config_value    TEXT,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES pl_tenant(id)
);

-- 租户配额
CREATE TABLE pl_tenant_quota (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_id       BIGINT NOT NULL UNIQUE,
    max_users       INT DEFAULT 100 COMMENT '最大用户数，-1 表示无限制',
    max_storage_mb  BIGINT DEFAULT 10240 COMMENT '最大存储 MB，-1 表示无限制',
    max_api_calls   BIGINT DEFAULT 100000 COMMENT '每月最大 API 调用，-1 表示无限制',
    used_storage_mb BIGINT DEFAULT 0,
    used_api_calls  BIGINT DEFAULT 0,
    quota_period    DATE COMMENT '配额统计周期起始日期',
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES pl_tenant(id)
);

-- 租户配额使用日志
CREATE TABLE pl_tenant_usage_log (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_id       BIGINT NOT NULL,
    metric_type     VARCHAR(32) NOT NULL COMMENT 'STORAGE/API_CALLS/ACTIVE_USERS',
    metric_value    BIGINT NOT NULL,
    recorded_at     DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES pl_tenant(id)
);

-- 租户健康检查
CREATE TABLE pl_tenant_health (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_id       BIGINT NOT NULL,
    health_status   VARCHAR(16) NOT NULL COMMENT 'HEALTHY/DEGRADED/UNHEALTHY',
    response_time_ms INT COMMENT 'API 响应时间',
    error_rate      DECIMAL(5,2) COMMENT '错误率百分比',
    checked_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES pl_tenant(id)
);
```

---

## 4. 用户故事

| ID | 用户故事 | 验收标准 |
|----|---------|---------|
| US-TENANT-001 | 作为平台管理员，我希望创建新租户，设置租户名称和域名 | 租户创建后自动分配独立 Schema |
| US-TENANT-002 | 作为平台管理员，我希望为每个租户绑定独立域名 | 域名访问时自动路由到对应租户 |
| US-TENANT-003 | 作为平台管理员，我希望为每个租户设置配额（用户数/存储/API调用量） | 配额超限时自动限制操作 |
| US-TENANT-004 | 作为平台管理员，我希望查看租户监控仪表盘（活跃用户/存储使用/API 调用趋势） | 监控数据以图表展示 |
| US-TENANT-005 | 作为平台管理员，我希望禁用/启用租户 | 禁用后租户所有用户无法登录 |
| US-TENANT-006 | 作为租户管理员，我希望在租户内配置自己的 Logo 和名称 | 配置后在该租户内生效 |
| US-TENANT-007 | 作为租户管理员，我希望查看我的租户配额使用情况 | 配额使用量实时显示，超额预警 |
| US-TENANT-008 | 作为平台管理员，我希望查看各租户的健康状态（响应时间/错误率） | 健康异常时告警 |
| US-TENANT-009 | 作为平台管理员，我希望导出某个租户的全部数据（JSON/CSV） | 导出后可用于数据迁移或备份 |
| US-TENANT-010 | 作为平台管理员，我希望将数据从租户 A 迁移到租户 B | 迁移后目标租户拥有完整数据 |
| US-TENANT-011 | 作为平台管理员，我希望配置租户级别的功能开关（启用/禁用某些功能） | 功能开关在租户内即时生效 |

---

## 5. API 设计

| 接口 | 方法 | 说明 |
|------|------|------|
| `/tenant` | GET/POST | 租户列表/创建 |
| `/tenant/{id}` | GET/PUT/DELETE | 租户详情/更新/删除 |
| `/tenant/{id}/schema` | POST | 初始化租户 Schema |
| `/tenant/{id}/config` | GET/PUT | 租户配置读取/更新 |
| `/tenant/{id}/quota` | GET/PUT | 租户配额读取/更新 |
| `/tenant/{id}/health` | GET | 租户健康状态 |
| `/tenant/{id}/health/history` | GET | 租户健康历史 |
| `/tenant/{id}/usage` | GET | 租户配额使用情况 |
| `/tenant/{id}/export` | GET | 导出租户数据 |
| `/tenant/{id}/import` | POST | 导入租户数据 |
| `/tenant/{id}/suspend` | POST | 暂停租户 |
| `/tenant/{id}/activate` | POST | 激活租户 |
| `/tenant/dashboard` | GET | 平台租户总览仪表盘 |

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
| 多租户 | 11800-11899 | 插件 多租户 模块 |

## 9. 版本历史

| 版本 | 日期 | 修改内容 | 作者 |
|------|------|---------|------|
| V1.0 | 2027年中 | 初始版本 | - |
