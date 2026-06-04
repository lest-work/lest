# SSO 单点登录 — V5.0 PRD

> **模块**: Core V5.0 | **许可**: 🔒 商业版

## 1. 产品概述

### 1.1 目标

为企业提供统一身份认证能力，支持 SAML 2.0、OIDC、OAuth2 等主流 SSO 协议。使用户通过企业 IdP 一次登录即可访问 LEST。

## 2. 核心功能

|| 功能 | 说明 | 优先级 |
||------|------|--------|
| SAML2.0 集成 | 适用于 Okta/Azure AD/OneLogin | P0 |
| OIDC 集成 | 现代标准，适合云原生应用 | P0 |
| OAuth2（社交登录） | 可选 | P1 |
| SSO 会话管理 | Token 签发 + 验证 + 刷新 | P0 |
| JIT 用户创建 | 首次 SSO 登录时自动创建 LEST 账号 | P0 |
| 审计日志 | SSO 登录记录 | P0 |

## 3. SSO 流程

```
用户 → LEST 登录页 → 选择 IdP（点击 SSO 按钮）
  ↓
IdP 登录页（Okta/Azure AD/OneLogin）
  ↓
IdP → LEST ACS 回调（携带 SAML Response）
  ↓
LEST 验证签名 → 提取用户信息 → 创建/更新用户 Session
  ↓
登录成功 → 重定向到 LEST 首页
```

## 4. 数据库设计

```sql
CREATE TABLE sso_provider (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    name            VARCHAR(128) NOT NULL,
    type            VARCHAR(16) NOT NULL COMMENT 'SAML/OIDC/OAUTH2',
    config          JSON COMMENT 'IdP 配置信息',
    metadata_xml    TEXT COMMENT 'SAML Metadata XML',
    enabled         BOOLEAN DEFAULT TRUE,
    is_default      BOOLEAN DEFAULT FALSE,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE sso_session (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id         BIGINT NOT NULL,
    provider_id     BIGINT,
    session_token   VARCHAR(255) UNIQUE,
    expires_at      DATETIME,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sso_user_mapping (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    provider_id         BIGINT NOT NULL,
    external_user_id    VARCHAR(255) NOT NULL,
    lest_user_id       BIGINT NOT NULL,
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(provider_id, external_user_id)
);
```

## 5. API 设计

|| 端点 | 说明 |
||------|------|
| GET /sso/providers | 获取已配置的 SSO 提供商列表 |
| POST /sso/saml/acs | SAML 断言消费服务（ACS） |
| POST /sso/oidc/callback | OIDC 回调 |
| POST /sso/session/refresh | 刷新 Session |
| POST /sso/logout | SSO 登出 |
