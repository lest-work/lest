# lest-sso — SSO 单点登录插件

> **插件 ID**: `lest-sso`
>
> **版本**: V3.0
>
> **Jira 映射**: Atlassian Access / SAML / OIDC（商业功能）
>
> **许可**: 🔒 商业版
>
> **最后更新**: 2026-06-02

---

## 1. 产品概述

### 1.1 目标与愿景

LEST SSO 插件为企业提供统一身份认证能力，支持 SAML 2.0、OIDC、OAuth2 等主流 SSO 协议。使用户通过企业身份提供商（IdP）一次登录即可访问 LEST 及所有集成的企业应用，实现真正的 Single Sign-On。

### 1.2 支持的协议

| 协议 | 说明 | 优先级 |
|------|------|--------|
| **SAML 2.0** | 适用于大多数企业 IdP（Okta/Azure AD/OneLogin） | P0 |
| **OIDC** | 现代标准，适合云原生应用 | P0 |
| **OAuth 2.0** | 适用于社交登录（可选） | P1 |
| **CAS** | 适用于教育/科研机构 | P2 |

---

## 2. 功能范围

### 2.1 核心功能

| 功能 | 说明 | 优先级 |
|------|------|--------|
| SAML 2.0 配置 | IdP 元数据上传，SP 配置生成 | P0 |
| OIDC 配置 | IdP 元数据/手动配置，客户端凭证管理 | P0 |
| 用户属性映射 | IdP 属性（email/name/group）→ LEST 用户字段 | P0 |
| SSO 登录流程 | SP-Initiated + IdP-Initiated SSO | P0 |
| 回退登录 | SSO 不可用时支持用户名密码登录 | P0 |
| 会话管理 | SSO 会话与 LEST 会话同步 | P0 |
| 审计日志 | SSO 登录事件记录 | P0 |
| 多 IdP 支持 | 同一 LEST 实例支持配置多个 IdP | P1 |
| JIT 用户创建 | IdP 用户首次登录时自动在 LEST 创建账户 | P1 |
| 强制 SSO | 禁止用户名密码登录，仅允许 SSO | P2 |

---

## 3. 数据库设计

### 3.1 表结构

```sql
-- SSO Provider 配置
CREATE TABLE pl_sso_provider (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    provider_type   VARCHAR(16) NOT NULL COMMENT 'SAML/OIDC/OAUTH2/CAS',
    provider_name   VARCHAR(128) NOT NULL COMMENT '如 Okta/Azure AD/Keycloak',
    is_enabled     BOOLEAN DEFAULT FALSE,
    is_default     BOOLEAN DEFAULT FALSE COMMENT '默认登录入口',
    config_data    JSON NOT NULL COMMENT '协议特定配置',
    attribute_mapping JSON NOT NULL COMMENT '用户属性映射',
    group_mapping   JSON COMMENT 'IdP 组 → LEST 角色映射',
    created_by      BIGINT NOT NULL,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- SAML 专用配置（存储在 config_data JSON 中）
-- {
--   "entityId": "https://lest.example.com",
--   "ssoUrl": "https://idp.example.com/sso",
--   "sloUrl": "https://idp.example.com/slo",
--   "certificate": "-----BEGIN CERTIFICATE-----...",
--   "metadataUrl": "https://idp.example.com/metadata"
-- }

-- OIDC 专用配置（存储在 config_data JSON 中）
-- {
--   "issuer": "https://idp.example.com",
--   "clientId": "lest-app",
--   "clientSecret": "加密存储",
--   "scopes": ["openid", "profile", "email", "groups"],
--   "endpoints": { "authorization": "...", "token": "...", "userinfo": "..." }
-- }

-- SSO 会话记录
CREATE TABLE pl_sso_session (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id         BIGINT NOT NULL,
    provider_id     BIGINT NOT NULL,
    session_index   VARCHAR(256) COMMENT 'SAML SessionIndex / OIDC sid',
    id_token_hash   VARCHAR(64) COMMENT 'ID Token 哈希',
    expires_at      DATETIME,
    last_activity  DATETIME DEFAULT CURRENT_TIMESTAMP,
    ip_address     VARCHAR(45),
    user_agent      VARCHAR(256),
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES sys_user(user_id),
    FOREIGN KEY (provider_id) REFERENCES pl_sso_provider(id)
);

-- SSO 登录日志
CREATE TABLE pl_sso_login_log (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id         BIGINT,
    provider_id     BIGINT,
    event_type      VARCHAR(32) NOT NULL COMMENT 'LOGIN_SUCCESS/LOGIN_FAIL/LOGOUT/SESSION_EXPIRED',
    ip_address     VARCHAR(45),
    user_agent      VARCHAR(256),
    error_message   VARCHAR(512),
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (provider_id) REFERENCES pl_sso_provider(id)
);
```

---

## 4. 用户故事

| ID | 用户故事 | 验收标准 |
|----|---------|---------|
| US-SSO-001 | 作为系统管理员，我希望上传 SAML IdP 元数据文件，自动解析 Entity ID/SSO URL/证书 | 元数据上传后自动填充配置 |
| US-SSO-002 | 作为系统管理员，我希望配置用户属性映射规则（IdP email → LEST email，IdP groups → LEST roles） | 属性映射在用户登录时生效 |
| US-SSO-003 | 作为系统管理员，我希望配置 OIDC IdP（手动输入或元数据 URL） | OIDC 配置后 SSO 登录可用 |
| US-SSO-004 | 作为员工，我希望在登录页选择"使用企业账号登录"，跳转到 IdP 进行身份验证后自动登录 LEST | SSO 登录后跳转回 LEST，无需再次输入密码 |
| US-SSO-005 | 作为员工，我希望在 IdP 中注销后，LEST 会话也自动失效 | 单点注销（SLO）生效 |
| US-SSO-006 | 作为系统管理员，我希望查看 SSO 登录日志，了解登录成功/失败情况 | 日志包含时间/IP/浏览器/错误原因 |
| US-SSO-007 | 作为系统管理员，我希望配置"强制 SSO"模式，禁止用户名密码登录 | 开启后登录页隐藏密码登录入口 |
| US-SSO-008 | 作为员工，我希望首次 SSO 登录时自动在 LEST 创建账户，无需管理员手动创建 | JIT 创建后用户可直接使用 |
| US-SSO-009 | 作为系统管理员，我希望在 SSO 不可用时保留用户名密码登录作为回退方案 | 回退登录不受 SSO 配置影响 |
| US-SSO-010 | 作为系统管理员，我希望配置多 IdP 支持（如同时配置 Azure AD 和 Keycloak） | 登录页显示多个 SSO 入口按钮 |

---

## 5. API 设计

| 接口 | 方法 | 说明 |
|------|------|------|
| `/sso/providers` | GET/POST | SSO Provider 列表/创建 |
| `/sso/providers/{id}` | GET/PUT/DELETE | Provider 详情/更新/删除 |
| `/sso/providers/{id}/test` | POST | 测试 SSO 配置 |
| `/sso/providers/{id}/metadata` | GET | 获取 SP 元数据 |
| `/sso/login/{providerId}` | GET | 发起 SSO 登录（重定向到 IdP） |
| `/sso/callback/{providerId}` | GET | SSO 回调（处理 IdP 响应） |
| `/sso/logout` | POST | SSO 登出（SLO） |
| `/sso/sessions` | GET | 当前用户 SSO 会话列表 |
| `/sso/sessions/{id}` | DELETE | 强制失效会话（管理员） |
| `/sso/login-logs` | GET | SSO 登录日志 |
| `/sso/config/enforce` | PUT | 强制 SSO 模式配置 |

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
| SSO | 11700-11799 | 插件 SSO 模块 |

## 9. 版本历史

| 版本 | 日期 | 修改内容 | 作者 |
|------|------|---------|------|
| V1.0 | 2027年中 | 初始版本 | - |
