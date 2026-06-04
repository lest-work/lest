# LEST Platform — 能力矩阵与扩展点总览

> **文档目的**：以资深产品经理和架构师视角，定义 LEST Platform 的完整**能力矩阵**，确保每一个需要扩展的维度都有对应的扩展点（Extension Point），并明确 Core 内置实现与插件实现的责任边界。
>
> **多端战略**：本文档覆盖 Web / 桌面端(Electron) / 移动 H5 / 原生 App(React Native) / 开放 API / IM Bot 共 6 个端。详见 [`MULTI-END-ARCHITECTURE.md`](../MULTI-END-ARCHITECTURE.md)。
>
> **维护原则**：Core 提供扩展点骨架和接口契约，插件提供具体实现。Core 不依赖任何第三方 SDK。所有第三方服务商（IM、存储、AI、短信等）通过插件接入。
>
> **最后更新**：2026-06-02

---

## 一、能力矩阵总览

### 1.1 能力分类框架

LEST Platform 的能力分为 **10 大域**（对应 Core 层和各端），每个域包含多个能力模块，每个模块有对应的扩展点：

| 域 | 英文 | 说明 | 覆盖端 |
|---|---|---|---|
| **A. 身份与认证** | Identity & Auth | 登录、MFA、SSO、LDAP、API Token | 所有端 |
| **B. 安全与合规** | Security | 验证码、审计日志、权限、敏感数据 | 所有端 |
| **C. 通知与消息** | Notification | 站内信、邮件、IM 渠道 | Web + 桌面端 + App + IM Bot |
| **D. 业务集成** | Integration | CI/CD、代码仓库、IM 平台、Webhook、API | Web + 桌面端 + 开放 API |
| **E. 数据与分析** | Data & Analytics | 报表、工时、编码时间、效能 | Web + 桌面端 |
| **F. 平台基础设施** | Platform | 存储、AI、搜索、导入导出、国际化 | Web + 桌面端 + App |
| **G. 桌面端 Electron** | Desktop | 离线存储、托盘、快捷键、IDE 集成 | 桌面端 |
| **H. 原生 App 端** | Mobile | 推送、生物识别、扫码、离线同步 | App |
| **I. 开放 API 端** | Open API | API 版本、限流、OAuth2 | 第三方系统 |
| **J. 跨端共享** | Cross-end | 实时推送、数据同步 | Web + 桌面端 + App |

---

### 1.2 能力矩阵全景图

#### A. 身份与认证（Identity & Auth）

| 能力 | Core 内置 | 扩展点 | 插件实现 |
|---|---|---|---|
| 用户名密码登录 | ✅ | `AUTH_MECHANISM` | — |
| 跨端统一认证（会话/生物识别/离线）| JWT | `AUTH_PROVIDER` | — |
| OAuth2 社交登录 | — | `IDENTITY_PROVIDER` | 企业微信、飞书、钉钉 |
| SAML 2.0 SSO | — | `IDENTITY_PROVIDER` | Okta、Azure AD、OneLogin |
| OIDC SSO | — | `IDENTITY_PROVIDER` | Keycloak、Auth0 |
| LDAP 用户同步 | — | `DIRECTORY_PROVIDER` | AD/OpenLDAP |
| 多租户隔离 | — | `TENANT_ISOLATION` | Schema 隔离 |
| API Token (PAT) | ✅ | — | — |
| 多因素认证 (MFA) | ✅ TOTP | `MFA_PROVIDER` | FIDO2/WebAuthn、短信 |
| 验证码防暴力 | ✅ 空实现 | `CAPTCHA_PROVIDER` | 腾讯防水墙、reCAPTCHA、网易易盾 |
| 会话管理 | ✅ JWT | `AUTH_PROVIDER` | — |

#### B. 安全与合规（Security）

| 能力 | Core 内置 | 扩展点 | 插件实现 |
|---|---|---|---|
| 审计日志 | ✅ 基础 | `AUDIT_HANDLER` | 等保合规、SIEM 集成 |
| 行级数据权限 | ✅ | `PERMISSION_RULE_ENGINE` | — |
| 字段级数据屏蔽 | — | `FIELD_MASK_PROVIDER` | 敏感字段屏蔽插件 |
| IP 白名单 | ✅ | — | — |
| 敏感数据加密 | ✅ | — | — |

#### C. 通知与消息（Notification）

| 能力 | Core 内置 | 扩展点 | 插件实现 |
|---|---|---|---|
| 站内信 | ✅ | — | — |
| 邮件发送 | ✅ SMTP | `MAIL_PROVIDER` | 云服务商邮件、SendGrid、Mailgun |
| IM 渠道通知 | — | `NOTIFICATION_CHANNEL_PROVIDER` | 钉钉、企业微信、飞书、Slack |
| 实时推送（WebSocket）| — | `REALTIME_PROVIDER` | — |
| 移动端推送（APNS/FCM）| — | `PUSH_NOTIFICATION_PROVIDER` | 极光、UniPush |
| 通知模板 | ✅ | — | — |
| 消息免打扰 | ✅ | — | — |

#### D. 业务集成（Integration）

| 能力 | Core 内置 | 扩展点 | 插件实现 |
|---|---|---|---|
| CI/CD 集成 | — | `CI_PROVIDER` | Jenkins、GitLab CI、GitHub Actions、CircleCI |
| 代码仓库集成 | — | `CODE_HOSTING_PROVIDER` | GitLab、GitHub、Gitea |
| IDE 侧栏集成 | — | `CODE_HOST_INTEGRATION` | VS Code 插件、GitHub Desktop 侧栏 |
| Webhook 发送 | ✅ 基础 | `OUTGOING_WEBHOOK` | 高级 Webhook + 签名 + 重试 |
| Webhook 接收 | — | `WEBHOOK_HANDLER` | CI Webhook、IM Webhook |
| 外部 API 导入 | — | `IMPORT_PROVIDER` | Jira、禅道、Linear |
| 外部 API 导出 | — | `EXPORT_PROVIDER` | CSV/Excel 导出 |
| 定时任务 | — | `SCHEDULED_JOB` | 组织架构同步、数据统计 |
| 会议平台集成 | — | `MEETING_PROVIDER` | 飞书会议、钉钉会议 |
| 日历同步 | — | `CALENDAR_PROVIDER` | Google Calendar、Outlook Calendar |
| IM 平台同步 | — | `IM_PROVIDER` | 钉钉组织同步、企微同步、飞书同步 |

#### E. 数据与分析（Data & Analytics）

| 能力 | Core 内置 | 扩展点 | 插件实现 |
|---|---|---|---|
| 燃尽图 | ✅ 基础 | `REPORT_RENDERER` | 高级燃尽图 |
| 速度图 | ✅ 基础 | `REPORT_RENDERER` | 高级报表 |
| 工时记录 | — | `WORKLOG_PROVIDER` | 工时插件 |
| 编码时间追踪 | — | `WAKATIME_PROVIDER` | WakaTime 兼容 |
| 效能分析 | — | `METRICS_ENGINE` | 团队绩效、AI 效能 |
| 仪表盘 Gadget | ✅ 基础 | `DASHBOARD_WIDGET` | 内置 Gadget + 插件扩展 |

#### F. 平台基础设施（Platform）

| 能力 | Core 内置 | 扩展点 | 插件实现 |
|---|---|---|---|
| 文件存储 | ✅ 本地 | `STORAGE_PROVIDER` | 阿里云 OSS、MinIO、S3、腾讯云 COS |
| AI 聊天/对话 | — | `AI_PROVIDER` | OpenAI、Claude、Gemini、通义千问 |
| AI 任务拆解 | — | `AI_TASK_DECOMPOSER` | AI 引擎 |
| AI 风险识别 | — | `AI_RISK_DETECTOR` | AI 引擎 |
| AI 效能预测 | — | `AI_FORECASTER` | AI 引擎 |
| 全文搜索 | ✅ PostgreSQL FTS | `SEARCH_PROVIDER` | Elasticsearch、Meilisearch |
| 国际化 | ✅ 中/英 | `I18N_PROVIDER` | 繁体中文、日文、韩文 |
| 开放 API | — | `API_VERSION_MANAGER` | REST v1/v2、GraphQL |
| API 限流 | ✅ 内存 | `RATE_LIMIT_PROVIDER` | Redis Token Bucket、Sliding Window |
| OAuth2 授权 | — | `OAUTH2_PROVIDER` | — |

---

## 二、扩展点完整清单

### 2.1 后端扩展点（按域分类）

#### A. 身份与认证（5 个扩展点）

| 扩展点 | 说明 | Core 内置 | 接口定义位置 |
|---|---|---|---|
| `AUTH_MECHANISM` | 登录方式（密码/OIDC/SAML）| 密码登录 | 本文档 §五 |
| `AUTH_PROVIDER` | 跨端统一认证（会话/生物识别/离线）| JWT | 本文档 §五 |
| `IDENTITY_PROVIDER` | 第三方身份源（OAuth2/SAML/OIDC）| — | [认证系统.md](./V1.0/认证系统与系统管理.md) |
| `DIRECTORY_PROVIDER` | 企业目录同步（LDAP/AD）| — | [LDAP集成.md](./V5.0/LDAP集成.md) |
| `MFA_PROVIDER` | 多因素认证（TOTP/短信/FIDO2）| TOTP | [认证系统.md](./V1.0/认证系统与系统管理.md) |
| `CAPTCHA_PROVIDER` | 验证码（防暴力破解）| 空实现 | [认证系统.md](./V1.0/认证系统与系统管理.md) |

#### B. 安全与合规（3 个扩展点）

| 扩展点 | 说明 | Core 内置 | 接口定义位置 |
|---|---|---|---|
| `AUDIT_HANDLER` | 审计事件处理器 | 基础日志 | [安全增强.md](./V1.0/安全增强.md) |
| `PERMISSION_RULE_ENGINE` | 精细化权限规则引擎 | 基础行级 | [精细化权限.md](./V5.0/精细化权限.md) |
| `FIELD_MASK_PROVIDER` | 字段级数据屏蔽 | — | 本文档 §五 |

#### C. 通知与消息（4 个扩展点）

| 扩展点 | 说明 | Core 内置 | 接口定义位置 |
|---|---|---|---|
| `MAIL_PROVIDER` | 邮件发送（SMTP/API）| SMTP | [通知与消息.md](./V1.0/通知与消息.md) |
| `NOTIFICATION_CHANNEL_PROVIDER` | IM 通知渠道 | 站内信 | [通知与消息.md](./V1.0/通知与消息.md) |
| `SMS_PROVIDER` | 短信发送（2FA/告警）| — | [安全增强.md](./V1.0/安全增强.md) |
| `REALTIME_PROVIDER` | 实时推送（WebSocket/SSE）| — | 本文档 §五（also in §J）|

#### D. 业务集成（11 个扩展点）

| 扩展点 | 说明 | Core 内置 | 接口定义位置 |
|---|---|---|---|
| `CI_PROVIDER` | CI/CD 服务接入 | — | [V4.0 插件规划](../plugins/V4.0/README.md) |
| `CODE_HOSTING_PROVIDER` | 代码仓库接入 | — | [V4.0 插件规划](../plugins/V4.0/README.md) |
| `CODE_HOST_INTEGRATION` | IDE 侧栏集成 | — | 本文档 §五 |
| `IM_PROVIDER` | IM 平台接入（组织同步）| — | [通知与消息.md](./V1.0/通知与消息.md) |
| `WEBHOOK_HANDLER` | 接收外部 Webhook | — | 本文档 §五 |
| `OUTGOING_WEBHOOK` | 发送 Webhook | 基础 | 本文档 §五 |
| `SCHEDULED_JOB` | 定时任务注册 | — | 本文档 §五 |
| `MEETING_PROVIDER` | 会议平台接入 | — | [V4.0 插件规划](../plugins/V4.0/README.md) |
| `CALENDAR_PROVIDER` | 日历平台同步 | — | 本文档 §五 |
| `IMPORT_PROVIDER` | 数据导入（外部系统）| — | 本文档 §五 |
| `EXPORT_PROVIDER` | 数据导出（外部格式）| — | 本文档 §五 |

#### E. 数据与分析（4 个扩展点）

| 扩展点 | 说明 | Core 内置 | 接口定义位置 |
|---|---|---|---|
| `WORKLOG_PROVIDER` | 工时记录与统计 | — | [V3.0 Core 规划](../../MILESTONES/V3.0-小版本规划.md) |
| `WAKATIME_PROVIDER` | 编码时间追踪 | — | [V4.0 插件规划](../plugins/V4.0/README.md) |
| `REPORT_RENDERER` | 报表渲染器 | 基础图表 | 本文档 §五 |
| `METRICS_ENGINE` | 效能指标计算引擎 | — | 本文档 §五 |

#### F. 平台基础设施（9 个扩展点）

| 扩展点 | 说明 | Core 内置 | 接口定义位置 |
|---|---|---|---|
| `STORAGE_PROVIDER` | 文件存储（本地/OSS）| 本地 | [系统设置.md](./V1.0/系统设置.md) |
| `AI_PROVIDER` | AI 模型接入 | — | [V4.0 插件规划](../plugins/V4.0/README.md) |
| `AI_TASK_DECOMPOSER` | AI 任务自动拆解 | — | [V4.0 插件规划](../plugins/V4.0/README.md) |
| `AI_RISK_DETECTOR` | AI 风险识别 | — | [V4.0 插件规划](../plugins/V4.0/README.md) |
| `AI_FORECASTER` | AI 效能预测 | — | [V4.0 插件规划](../plugins/V4.0/README.md) |
| `SEARCH_PROVIDER` | 全文搜索引擎 | PostgreSQL FTS | 本文档 §五 |
| `I18N_PROVIDER` | 国际化翻译服务 | 静态文件 | 本文档 §五 |
| `CONTENT_I18N_PROVIDER` | 多语言内容 | — | 本文档 §五 |
| `AUTOMATION_CUSTOM_ACTION` | 自动化自定义动作 | 基础动作 | [任务管理.md](./V2.0/任务管理.md) |

#### G. 桌面端 Electron（4 个扩展点）

| 扩展点 | 说明 | Core 内置 | 接口定义位置 |
|---|---|---|---|
| `OFFLINE_STORAGE_PROVIDER` | 离线存储（IndexedDB）| — | 本文档 §五 |
| `SYSTEM_TRAY_PROVIDER` | 系统托盘与原生通知 | — | 本文档 §五 |
| `GLOBAL_SHORTCUT_PROVIDER` | 全局快捷键 | — | 本文档 §五 |
| `CODE_HOST_INTEGRATION` | IDE 侧栏集成（VS Code/GitHub/GitLab）| — | 本文档 §五 |

#### H. 原生 App 端 React Native（4 个扩展点）

| 扩展点 | 说明 | Core 内置 | 接口定义位置 |
|---|---|---|---|
| `PUSH_NOTIFICATION_PROVIDER` | 推送通知（APNS/FCM）| — | 本文档 §五 |
| `BIOMETRIC_PROVIDER` | 生物识别（指纹/面容）| — | 本文档 §五 |
| `QR_SCAN_PROVIDER` | 二维码扫描 | — | 本文档 §五 |
| `OFFLINE_SYNC_PROVIDER` | 离线同步（SQLite）| — | 本文档 §五 |

#### I. 开放 API 端（3 个扩展点）

| 扩展点 | 说明 | Core 内置 | 接口定义位置 |
|---|---|---|---|
| `API_VERSION_MANAGER` | API 版本管理与废弃策略 | — | 本文档 §五 |
| `RATE_LIMIT_PROVIDER` | 限流（Token Bucket/Sliding Window）| 内存 | 本文档 §五 |
| `OAUTH2_PROVIDER` | OAuth2 授权服务 | — | 本文档 §五 |

#### J. 跨端共享（2 个扩展点）

| 扩展点 | 说明 | Core 内置 | 接口定义位置 |
|---|---|---|---|
| `REALTIME_PROVIDER` | 实时推送（WebSocket/SSE）| — | 本文档 §五 |
| `DATA_SYNC_PROVIDER` | 离线数据同步与冲突处理 | — | 本文档 §五 |

### 2.2 前端扩展点（UI Extension Points）

#### UI 扩展点清单

| 扩展点 | 说明 | 位置 | 支持多插件 |
|---|---|---|---|
| `DASHBOARD_WIDGET` | 仪表盘 Gadget | 仪表盘首页 | ✅ |
| `TASK_DETAIL_TAB` | 任务详情页 Tab | 任务详情 | ✅ |
| `TASK_DETAIL_ACTION` | 任务详情页操作按钮 | 任务详情顶部 | ✅ |
| `TASK_DETAIL_FIELD` | 任务详情页字段扩展 | 任务详情字段区 | ✅ |
| `TASK_DETAIL_CONTEXT_MENU` | 任务右键菜单 | 任务卡片 | ✅ |
| `PROJECT_TAB` | 项目详情页 Tab | 项目详情 | ✅ |
| `PROJECT_WIDGET` | 项目页侧边栏组件 | 项目详情 | ✅ |
| `PROJECT_HEADER_ACTION` | 项目页顶部操作按钮 | 项目页顶栏 | ✅ |
| `BOARD_COLUMN` | 看板列扩展 | 看板视图 | ✅ |
| `COMMAND_PALETTE` | 命令面板（Ctrl+K）| 全局 | ✅ |
| `GLOBAL_SIDEBAR` | 全局侧边栏扩展 | 左侧导航 | ✅ |
| `GLOBAL_HEADER` | 全局顶部栏扩展 | 页面顶栏 | ✅ |
| `BATCH_OPERATION` | 批量操作扩展 | 列表页 | ✅ |
| `MENU_ITEM` | 系统菜单项 | 左侧菜单 | ✅ |
| `ISSUE_TYPE_ICON` | Issue 类型图标扩展 | 任务列表/看板 | ✅ |
| `FILTER_RENDERER` | 筛选器渲染扩展 | 列表页 | ✅ |

---

## 三、接口契约规范

### 3.1 扩展点接口命名规范

```
{I能力}Provider   — 数据/服务提供者（如 STORAGE_PROVIDER、MAIL_PROVIDER）
{I能力}Handler    — 事件/请求处理器（如 WEBHOOK_HANDLER、AUDIT_HANDLER）
{I能力}Engine    — 规则/计算引擎（如 PERMISSION_RULE_ENGINE、METRICS_ENGINE）
{I能力}Renderer  — 渲染器（如 REPORT_RENDERER、FILTER_RENDERER）
{I能力}Action    — 可执行动作（如 AUTOMATION_CUSTOM_ACTION）
{I能力}Mechanism  — 认证机制（如 AUTH_MECHANISM）
{I能力}Integration — 集成扩展（如 CODE_HOST_INTEGRATION）
```

### 3.2 Core 内置 vs 插件实现的判定原则

| 判定维度 | Core 内置 | 插件实现 |
|---|---|---|
| **通用性** | 所有用户都需要（登录、通知）| 可选功能 |
| **稳定性** | 需求稳定不变（用户名密码）| 需求变化快（AI 模型、IM API）|
| **第三方依赖** | 无外部依赖 | 需要第三方 SDK |
| **商业敏感性** | 技术中立 | 商业绑定（钉钉、企微等）|
| **性能关键** | 热路径（认证、通知）| 可异步 |

### 3.3 必需 vs 可选扩展点

| 扩展点 | 必需？ | 缺失时的回退 |
|--------|--------|-------------|
| `AUTH_PROVIDER` | ✅ 是 | 系统不可用 |
| `STORAGE_PROVIDER` | ✅ 是 | 无法上传附件 |
| `SEARCH_PROVIDER` | ❌ 否 | 回退到 PostgreSQL FTS |
| `REALTIME_PROVIDER` | ❌ 否 | 无实时推送 |
| `MAIL_PROVIDER` | ❌ 否 | 无邮件通知 |
| `NOTIFICATION_CHANNEL_PROVIDER` | ❌ 否 | 无 IM 通知（站内信由 Core 提供）|
| `PUSH_NOTIFICATION_PROVIDER` | ❌ 否 | 无 App 推送 |
| `AI_PROVIDER` | ❌ 否 | 无 AI 功能 |

---

## 四、扩展点基础接口

所有 Provider 必须实现 `BaseProvider` 接口，包含生命周期方法：

```typescript
/** 所有 Provider 的基础接口（必须实现）*/
interface BaseProvider {
  /** 初始化（Spring @PostConstruct 等效）*/
  initialize?(): Promise<void>;

  /** 销毁（Spring @PreDestroy 等效）*/
  destroy?(): Promise<void>;

  /** 健康检查（Actuator 等效）*/
  healthCheck?(): Promise<{
    status: 'healthy' | 'degraded' | 'unhealthy';
    details?: unknown;
  }>;
}
```

> **说明**：所有 Provider 接口继承 `BaseProvider`。带 `?` 的方法是可选的，但建议实现以便于运维监控。
>
> **Spring Boot 实现注意**：Core 内置的 Provider 使用 `@Component` + `@PostConstruct` / `@PreDestroy`；插件提供的 Provider 在 V4.0 插件运行时中通过生命周期钩子注册。详见 [V4.0 插件规划](../plugins/V4.0/README.md)。

---

## 五、接口定义

> **类型约定**：本文档使用 TypeScript 语法定义接口。所有 `interface` 以 `BaseProvider` 开头表示继承。返回 `Promise` 的方法为异步，标注同步的方法如 `mask()` 为纯函数。
>
> **统一错误处理**：除特别说明外，所有 Promise 方法 reject 时抛出 `Error` 子类型。标准错误类型定义见 §五末。

### A. 身份与认证

**`AUTH_MECHANISM` 接口定义：**

```typescript
interface AuthMechanism extends BaseProvider {
  readonly id: string;
  readonly name: string;
  readonly protocol: 'form' | 'saml' | 'oidc' | 'ldap';

  authenticate(credential: Credential): Promise<AuthResult>;
  getLoginFormFields(): FormField[];
}

interface Credential {
  type: 'password' | 'saml' | 'oidc' | 'ldap';
  username?: string;
  password?: string;
  assertion?: string;   // SAML/OIDC assertion
  rawCredential?: unknown;
}

interface AuthResult {
  success: boolean;
  userId?: string;
  sessionId?: string;
  accessToken?: string;
  refreshToken?: string;
  expiresAt?: Date;
  error?: AuthError;
}

interface AuthError {
  code: 'INVALID_CREDENTIALS' | 'ACCOUNT_LOCKED' | 'MFA_REQUIRED' | 'SESSION_EXPIRED';
  message: string;
}

interface FormField {
  name: string;
  type: 'text' | 'password' | 'checkbox';
  label: string;
  placeholder?: string;
  required: boolean;
}
```

**`AUTH_PROVIDER` 接口定义（跨端统一认证）：**

```typescript
interface AuthProvider extends BaseProvider {
  readonly id: string;
  readonly name: string;

  /** 核心认证 */
  authenticate(credential: Credential): Promise<AuthResult>;
  refreshToken(refreshToken: string): Promise<AuthResult>;
  logout(userId: string): Promise<void>;

  /** 跨端会话管理 */
  validateSession(token: string, deviceId?: string): Promise<SessionInfo | null>;
  revokeAllSessions(userId: string): Promise<void>;
  getActiveSessions(userId: string): Promise<Session[]>;
  revokeSession(sessionId: string): Promise<void>;

  /** 社交登录 */
  getSocialLoginUrl(provider: string, state: string): string;
  handleSocialCallback(code: string): Promise<AuthResult>;

  /** 移动端生物识别 */
  authenticateWithBiometric(userId: string): Promise<AuthResult>;

  /** 桌面端离线认证 */
  authenticateOffline(credential: OfflineCredential): Promise<OfflineAuthResult>;
}

interface SessionInfo {
  userId: string;
  sessionId: string;
  deviceId?: string;
  deviceName?: string;
  createdAt: Date;
  expiresAt: Date;
  isValid: boolean;
}

interface Session {
  sessionId: string;
  deviceId: string;
  deviceName: string;
  ip: string;
  userAgent: string;
  createdAt: Date;
  lastActiveAt: Date;
}

interface OfflineCredential {
  type: 'offline';
  encryptedCredential: string;  // AES-256-GCM 加密的凭证
}

interface OfflineAuthResult extends AuthResult {
  offlineToken?: string;       // 离线访问令牌
  syncRequired: boolean;       // 是否需要联网同步
}
```

**`IdentityProvider` 接口定义：**

```typescript
interface IdentityProvider extends BaseProvider {
  readonly id: string;
  readonly name: string;
  readonly protocol: 'oauth2' | 'saml' | 'oidc';

  getAuthorizationUrl(state: string): Promise<string>;
  exchangeCode(code: string): Promise<ExternalUserInfo>;
  getAttributeMapping(): AttributeMapping[];

  /** PKCE 支持（OAuth2）*/
  generateCodeChallenge?(codeVerifier: string, method: 'S256'): string;
  exchangeCodeWithPKCE?(code: string, codeVerifier: string): Promise<ExternalUserInfo>;
}

interface ExternalUserInfo {
  provider: string;
  externalId: string;
  email: string;
  displayName: string;
  avatarUrl?: string;
  attributes: Record<string, string>;
}

interface AttributeMapping {
  externalAttr: string;
  localAttr: string;
  transform?: 'lowercase' | 'uppercase' | 'trim';
}
```

**`MFAProvider` 接口定义：**

```typescript
interface MFAProvider extends BaseProvider {
  readonly id: string;
  readonly name: string;
  readonly factorType: 'totp' | 'sms' | 'email' | 'hardware';

  enroll(user: User): Promise<EnrollResult>;
  verify(userId: string, credential: string): Promise<boolean>;
  listEnabledFactors(userId: string): Promise<MFAFactor[]>;
  disableFactor?(userId: string, factorId: string): Promise<void>;

  /** FIDO2/WebAuthn */
  enrollWebAuthn?(user: User): Promise<{ challenge: string; credentialOptions: unknown }>;
  verifyWebAuthnAssertion?(userId: string, assertion: unknown): Promise<boolean>;
}

interface User {
  id: string;
  username: string;
  email: string;
}

interface EnrollResult {
  success: boolean;
  factorId?: string;
  provisioningUri?: string;    // TOTP URI
  qrCode?: string;             // Base64 QR Code
  recoveryCodes?: string[];   // 一次性恢复码
}

interface MFAFactor {
  id: string;
  type: 'totp' | 'sms' | 'email' | 'hardware';
  name: string;
  enabled: boolean;
  createdAt: Date;
  lastUsedAt?: Date;
}
```

**`CaptchaProvider` 接口定义：**

```typescript
interface CaptchaProvider extends BaseProvider {
  challenge(): Promise<CaptchaChallenge>;
  verify(challengeId: string, userResponse: string): Promise<boolean>;
  getFrontendConfig(): Record<string, string>;
}

interface CaptchaChallenge {
  challengeId: string;
  data: unknown;  // 图片URL/滑块参数等
}
```

**`DirectoryProvider` 接口定义：**

```typescript
interface DirectoryProvider extends BaseProvider {
  readonly id: string;
  readonly name: string;
  readonly serverType: 'active_directory' | 'openldap' | 'freeipa';

  testConnection(config: DirectoryConfig): Promise<ConnectionResult>;
  syncUsers(lastSyncTime?: Date): Promise<UserSyncResult>;
  syncOrganization(): Promise<OrgSyncResult>;
  getGroupMappings(): GroupMapping[];
  searchUsers(query: string): Promise<ExternalUser[]>;
}

interface DirectoryConfig {
  host: string;
  port: number;
  ssl: boolean;
  bindDn: string;
  bindPassword: string;
  baseDn: string;
}

interface ConnectionResult {
  success: boolean;
  error?: string;
  serverVersion?: string;
}

interface UserSyncResult {
  usersCreated: number;
  usersUpdated: number;
  usersDeactivated: number;
  errors: string[];
}

interface OrgSyncResult {
  departmentsCreated: number;
  departmentsUpdated: number;
  errors: string[];
}

interface GroupMapping {
  localRole: string;
  externalGroup: string;
  scope: 'global' | 'project';
}
```

### B. 安全与合规

**`AUDIT_HANDLER` 接口定义：**

```typescript
interface AuditHandler extends BaseProvider {
  handle(event: AuditEvent): Promise<void>;
  supportsEventTypes(): string[];
  handleBatch?(events: AuditEvent[]): Promise<void>;
  filter?(event: AuditEvent): boolean;  // 判断是否处理该事件
}

interface AuditEvent {
  readonly id: string;
  readonly userId: string;
  readonly action: string;          // 'login' / 'task.create' / 'project.update'
  readonly resourceType: string;    // 'task' / 'project' / 'user'
  readonly resourceId: string;
  readonly ip: string;
  readonly userAgent: string;
  readonly payload: Record<string, unknown>;
  readonly timestamp: Date;
}
```

**`PERMISSION_RULE_ENGINE` 接口定义：**

```typescript
interface PermissionRuleEngine extends BaseProvider {
  evaluate(userId: string, action: string, resource: Resource): Promise<boolean>;
  getUserPermissions(userId: string, resourceType: string): Promise<Permission[]>;
}

interface Resource {
  type: string;
  id: string;
  projectId?: string;
  attributes?: Record<string, unknown>;
}

interface Permission {
  action: string;
  scope: 'global' | 'project' | 'own';
  conditions?: Record<string, unknown>;
}
```

**`FIELD_MASK_PROVIDER` 接口定义：**

```typescript
interface FieldMaskProvider extends BaseProvider {
  mask(fieldValue: unknown, context: MaskContext): unknown;
  shouldMask(field: string, user: User, resource: Resource): boolean;
}

interface MaskContext {
  reason: 'export' | 'api_response' | 'audit_log';
  requesterId?: string;
}
```

### C. 通知与消息

**`MailProvider` 接口定义：**

```typescript
interface MailProvider extends BaseProvider {
  readonly id: string;
  readonly name: string;
  send(mail: MailMessage): Promise<void>;
  validateConfig(config: MailConfig): Promise<boolean>;
}

interface MailMessage {
  to: string[];
  cc?: string[];
  bcc?: string[];
  subject: string;
  html: string;
  text?: string;
  replyTo?: string;
  attachments?: Attachment[];
}

interface Attachment {
  filename: string;
  contentType: string;
  content: Buffer | string;
}

interface MailConfig {
  host: string;
  port: number;
  username: string;
  password: string;
  from: string;
  ssl: boolean;
}
```

**`NotificationChannelProvider` 接口定义：**

```typescript
interface NotificationChannelProvider extends BaseProvider {
  readonly id: string;
  readonly name: string;
  enabled: boolean;
  supportedFormats: ('text' | 'markdown' | 'card')[];
  supportsMention: boolean;

  send(message: NotificationMessage, context: NotificationContext): Promise<SendResult>;
  validateConfig(config: ChannelConfig): Promise<ValidationResult>;
  getConfigFields(): ConfigField[];
}

interface NotificationMessage {
  title: string;
  body: string;
  format: 'text' | 'markdown' | 'card';
  mentionTargets?: string[];  // 被 @ 的用户 ID 列表
  actionUrl?: string;        // 点击跳转链接
  extraData?: Record<string, unknown>;
}

interface NotificationContext {
  userId: string;
  projectId?: string;
  taskId?: string;
  eventType: string;
}

interface SendResult {
  success: boolean;
  providerMessageId?: string;
  error?: string;
}

interface ChannelConfig extends Record<string, unknown> {}

interface ConfigField {
  key: string;
  type: 'text' | 'password' | 'checkbox' | 'select';
  label: string;
  required: boolean;
  options?: { value: string; label: string }[];
}

interface ValidationResult {
  valid: boolean;
  errors?: { field: string; message: string }[];
}
```

**`REALTIME_PROVIDER` 接口定义（跨端）：**

```typescript
interface RealtimeProvider extends BaseProvider {
  readonly id: string;  // 'websocket' / 'sse'
  readonly name: string;

  connect(options: ConnectionOptions): Promise<void>;
  disconnect(): void;

  subscribe(topic: string, handler: (event: RealtimeEvent) => void): Subscription;
  unsubscribe(subscriptionId: string): void;

  publish(topic: string, event: RealtimeEvent): Promise<void>;
  sendToUser(userId: string, message: RealtimeMessage): Promise<void>;

  onConnectionChange(handler: (status: ConnectionStatus) => void): void;
  reconnect(): void;
  ping(): void;
}

interface ConnectionOptions {
  token: string;           // JWT
  transport?: 'websocket' | 'sse';
  reconnectIntervalMs?: number;
  heartbeatIntervalMs?: number;
}

interface Subscription {
  readonly id: string;
  readonly topic: string;
  readonly createdAt: Date;
}

type RealtimeEvent = TaskCreatedEvent | TaskUpdatedEvent | CommentAddedEvent | NotificationEvent;

interface TaskCreatedEvent {
  type: 'task.created';
  taskId: string;
  projectId: string;
  creatorId: string;
  timestamp: number;
}

interface TaskUpdatedEvent {
  type: 'task.updated';
  taskId: string;
  projectId: string;
  updatedBy: string;
  changes: Record<string, { old: unknown; new: unknown }>;
  timestamp: number;
}

interface CommentAddedEvent {
  type: 'comment.created';
  commentId: string;
  taskId: string;
  authorId: string;
  content: string;
  timestamp: number;
}

interface NotificationEvent {
  type: 'notification';
  notificationId: string;
  userId: string;
  message: NotificationMessage;
  timestamp: number;
}

interface RealtimeMessage {
  id: string;
  topic: string;
  event: string;
  data: unknown;
  timestamp: number;
}

type ConnectionStatus = 'connecting' | 'connected' | 'disconnected' | 'error';
```

### D. 业务集成

**`CIProvider` 接口定义：**

```typescript
interface CIProvider extends BaseProvider {
  readonly id: string;  // 'jenkins' / 'gitlab-ci' / 'github-actions'
  readonly name: string;

  listBuilds(taskId: string): Promise<Build[]>;
  getBuild(buildId: string): Promise<BuildDetail>;
  getBuildLog(buildId: string): Promise<string>;
  triggerBuild?(projectId: string, params?: Record<string, unknown>): Promise<string>;
}

interface Build {
  id: string;
  buildNumber: number;
  status: 'pending' | 'running' | 'success' | 'failure' | 'cancelled';
  branch: string;
  commitHash: string;
  triggeredBy: string;
  triggeredAt: Date;
  durationSeconds?: number;
}

interface BuildDetail extends Build {
  taskId?: string;
  pipelineUrl: string;
  logUrl?: string;
}
```

**`CODE_HOSTING_PROVIDER` 接口定义：**

```typescript
interface CodeHostingProvider extends BaseProvider {
  readonly id: string;  // 'gitlab' / 'github' / 'gitea' / 'gogs'
  readonly name: string;

  getCommits(repositoryId: string, filePath: string): Promise<Commit[]>;
  getMergeRequest(mrId: string): Promise<MergeRequest>;
  getMergeRequestReviews(mrId: string): Promise<Review[]>;
  getCodeSnippet(repositoryId: string, commitSha: string, filePath: string, lineStart: number, lineEnd: number): Promise<string>;
  getBlame(repositoryId: string, filePath: string, revision: string): Promise<BlameEntry[]>;
}

interface Commit {
  sha: string;
  author: string;
  authorEmail: string;
  message: string;
  committedAt: Date;
  additions: number;
  deletions: number;
}

interface MergeRequest {
  id: string;
  title: string;
  sourceBranch: string;
  targetBranch: string;
  author: string;
  status: 'open' | 'merged' | 'closed';
  url: string;
  createdAt: Date;
  updatedAt: Date;
}

interface Review {
  reviewerId: string;
  state: 'approved' | 'changes_requested' | 'pending';
  reviewedAt?: Date;
}

interface BlameEntry {
  commit: string;
  author: string;
  lines: { start: number; end: number };
}
```

**`CODE_HOST_INTEGRATION` 接口定义（桌面端）：**

```typescript
interface CodeHostIntegration extends BaseProvider {
  readonly id: string;  // 'vscode-sidebar' / 'github-sidebar' / 'gitlab-sidebar'
  readonly name: string;
  readonly platform: 'vscode' | 'github-desktop' | 'jetbrains' | 'electron';

  renderSidebar(containerId: string, context: IntegrationContext): Promise<void>;
  renderLinkedTasks(files: string[]): Promise<SidebarTask[]>;
  renderPullRequestTasks(prId: string): Promise<TaskStatusSummary>;
  transitionTask(taskId: string, action: TaskAction): Promise<Task>;
  openTaskDetail(taskId: string): void;
  triggerBuild(taskId: string, config?: BuildConfig): Promise<Build>;
  onFileChange(handler: (files: string[]) => void): void;
}

interface IntegrationContext {
  repositoryId: string;
  branch: string;
  commitSha: string;
  currentFile?: string;
}

interface SidebarTask {
  id: string;
  key: string;
  title: string;
  status: string;
  priority: string;
  assignee?: string;
}

interface TaskStatusSummary {
  total: number;
  done: number;
  inProgress: number;
  pending: number;
}

interface TaskAction {
  type: 'transition';
  targetStatus: string;
  comment?: string;
}

interface BuildConfig {
  branch?: string;
  env?: Record<string, string>;
}
```

**`IMProvider` 接口定义：**

```typescript
interface IMProvider extends BaseProvider {
  readonly id: string;
  readonly name: string;
  readonly platform: 'dingtalk' | 'wecom' | 'feishu' | 'slack';

  getAuthorizationUrl(state: string): Promise<string>;
  exchangeCode(code: string): Promise<IMCredentials>;
  syncOrganization(): Promise<OrgSyncResult>;
  searchUsers(query: string): Promise<ExternalUser[]>;
}

interface IMCredentials {
  accessToken: string;
  refreshToken?: string;
  expiresAt: Date;
}
```

**`WebhookHandler` 接口定义：**

```typescript
interface WebhookHandler extends BaseProvider {
  readonly id: string;
  readonly name: string;
  readonly eventTypes: string[];

  handle(payload: WebhookPayload, headers: Record<string, string>): Promise<WebhookResult>;
  verifySignature?(payload: string, signature: string, secret: string): boolean;
}

interface WebhookPayload {
  event: string;
  timestamp: number;
  data: unknown;
}

interface WebhookResult {
  success: boolean;
  actionTaken?: string;
  error?: string;
}
```

**`OUTGOING_WEBHOOK` 接口定义：**

```typescript
interface OutgoingWebhook extends BaseProvider {
  readonly id: string;
  readonly name: string;

  send(payload: WebhookPayload, config: WebhookConfig): Promise<WebhookResult>;
  sign?(payload: string, secret: string): string;
  getDeliveryHistory(webhookId: string): Promise<DeliveryRecord[]>;
}

interface WebhookConfig {
  url: string;
  secret?: string;
  headers?: Record<string, string>;
  timeoutMs?: number;
  retryPolicy?: RetryPolicy;
}

interface RetryPolicy {
  maxRetries: number;
  backoffMs: number;
}

interface DeliveryRecord {
  id: string;
  webhookId: string;
  status: 'success' | 'failure' | 'pending';
  statusCode?: number;
  responseBody?: string;
  sentAt: Date;
}
```

**`CalendarProvider` 接口定义：**

```typescript
interface CalendarProvider extends BaseProvider {
  readonly id: string;  // 'google' / 'outlook' / 'apple'
  readonly name: string;

  getAuthorizationUrl(state: string): Promise<string>;
  exchangeCode(code: string): Promise<CalendarCredentials>;
  getCalendars(): Promise<Calendar[]>;
  createEvent(calendarId: string, event: CalendarEvent): Promise<string>;
  updateEvent(calendarId: string, eventId: string, event: CalendarEvent): Promise<void>;
  deleteEvent(calendarId: string, eventId: string): Promise<void>;
  fetchEvents(calendarId: string, timeRange: TimeRange): Promise<CalendarEvent[]>;
}

interface CalendarCredentials extends Record<string, unknown> {}

/** 外部用户（第三方身份源 / IM 平台同步）*/
interface ExternalUser {
  id: string;
  provider: string;           // 'dingtalk' | 'wecom' | 'feishu' | 'github' | 'gitlab' | 'oauth2' | 'saml'
  externalId: string;        // 第三方平台用户 ID
  email?: string;
  displayName?: string;
  avatarUrl?: string;
  department?: string;       // 部门名称
  phone?: string;
  isActive: boolean;
  syncedAt: Date;
}

/** 数据同步结果 */
interface PushResult {
  success: boolean;
  entityType: string;
  entityId: string;
  syncedAt?: Date;
  error?: string;
}

interface Calendar {
  id: string;
  name: string;
  primary: boolean;
}

interface CalendarEvent {
  title: string;
  description?: string;
  startAt: Date;
  endAt: Date;
  attendees?: string[];
  reminderMinutes?: number[];
  location?: string;
}

interface TimeRange {
  start: Date;
  end: Date;
}
```

**`IMPORT_PROVIDER` 接口定义：**

```typescript
interface ImportProvider extends BaseProvider {
  readonly id: string;  // 'jira' / 'zentao' / 'redmine' / 'linear'
  readonly name: string;
  readonly supportedEntities: ('issue' | 'project' | 'user' | 'attachment')[];

  preview(config: ImportConfig): Promise<ImportPreview>;
  execute(config: ImportConfig, options: ImportOptions): Promise<ImportResult>;
  getFieldMappings(): FieldMapping[];
  validate(data: unknown[]): Promise<ValidationResult[]>;
}

interface ImportConfig {
  sourceUrl: string;
  apiToken?: string;
  projectMapping?: Record<string, string>;  // 源项目ID → 目标项目ID
  fieldMappings?: FieldMapping[];
  dryRun: boolean;
}

interface ImportPreview {
  issuesFound: number;
  usersFound: number;
  estimatedDuration: number;  // 秒
  warnings: string[];
}

interface ImportOptions {
  skipAttachments: boolean;
  skipUsers: boolean;
  concurrency: number;
}

interface ImportResult {
  success: boolean;
  issuesImported: number;
  issuesFailed: number;
  usersImported: number;
  errors: ImportError[];
  durationMs: number;
}

interface ImportError {
  sourceId: string;
  reason: string;
  row?: unknown;
}

interface FieldMapping {
  sourceField: string;
  targetField: string;
  transform?: 'date_format' | 'user_lookup' | 'status_map';
  options?: Record<string, string>;
}

interface ValidationResult {
  row: number;
  field: string;
  valid: boolean;
  message?: string;
}
```

**`EXPORT_PROVIDER` 接口定义：**

```typescript
interface ExportProvider extends BaseProvider {
  readonly id: string;  // 'csv' / 'excel' / 'pdf' / 'json'
  readonly name: string;
  readonly supportedFormats: ExportFormat[];

  export(data: ExportData, format: ExportFormat): Promise<ExportResult>;
  getTemplate?(templateId: string): Promise<ExportTemplate>;
}

type ExportFormat = 'csv' | 'xlsx' | 'pdf' | 'json';

interface ExportData {
  type: 'tasks' | 'projects' | 'worklogs' | 'custom';
  filters?: Record<string, unknown>;
  fields: string[];
  projectId?: string;
}

interface ExportResult {
  success: boolean;
  fileId: string;
  filename: string;
  sizeBytes: number;
  downloadUrl: string;
  expiresAt: Date;
}

interface ExportTemplate {
  id: string;
  name: string;
  format: ExportFormat;
  columns: { field: string; header: string; width?: number }[];
}
```

### E. 数据与分析

**`REPORT_RENDERER` 接口定义：**

```typescript
interface ReportRenderer extends BaseProvider {
  readonly id: string;  // 'builtin' / 'echarts' / 'chartjs' / 'apexcharts'
  readonly name: string;

  render(config: ChartConfig): Promise<RenderResult>;
  export(config: ChartConfig, format: 'png' | 'pdf' | 'svg'): Promise<Buffer>;
  supportedChartTypes(): ChartType[];
  validateConfig?(config: ChartConfig): ValidationResult;
}

type ChartType = 'line' | 'bar' | 'pie' | 'area' | 'scatter' | 'heatmap'
  | 'burndown' | 'velocity' | 'cfd' | 'control';

interface ChartConfig {
  type: ChartType;
  title?: string;
  data: ChartData;
  width?: number;
  height?: number;
  options?: Record<string, unknown>;
}

interface ChartData {
  labels?: string[];
  datasets: { label: string; data: number[]; color?: string }[];
}

interface RenderResult {
  svg?: string;
  html?: string;
  chartType: ChartType;
}
```

**`METRICS_ENGINE` 接口定义：**

```typescript
interface MetricsEngine extends BaseProvider {
  readonly id: string;
  readonly name: string;

  calculate(metric: MetricType, params: MetricParams): Promise<MetricResult>;
  aggregate(metric: MetricType, range: TimeRange, granularity: 'day' | 'week' | 'month'): Promise<AggregatedResult>;
  getMetricDefinitions(): MetricDefinition[];
}

/** 工时记录提供者 — lest-worklog 插件实现 */
interface WorklogProvider extends BaseProvider {
  readonly id: 'lest-worklog';  // 'lest-worklog'
  readonly name: string;

  /** 记录工时 */
  log(params: {
    issueId: string;
    userId: string;
    startedAt: Date;
    endedAt: Date;
    description?: string;
    billable?: boolean;
  }): Promise<Worklog>;

  /** 获取任务的工时列表 */
  listByIssue(issueId: string): Promise<Worklog[]>;

  /** 获取用户在日期范围内的工时 */
  listByUser(userId: string, range: TimeRange): Promise<Worklog[]>;

  /** 汇总统计数据 */
  getSummary(projectId: string, range: TimeRange): Promise<WorklogSummary>;
}

interface Worklog {
  id: string;
  issueId: string;
  userId: string;
  startedAt: Date;
  endedAt: Date;
  durationMinutes: number;
  description?: string;
  billable: boolean;
  createdAt: Date;
}

interface WorklogSummary {
  totalMinutes: number;
  billableMinutes: number;
  nonBillableMinutes: number;
  byIssue: Record<string, number>;
  byUser: Record<string, number>;
}

/** 编码时间提供者 — lest-wakatime 插件实现 */
interface WakatimeProvider extends BaseProvider {
  readonly id: 'lest-wakatime';  // 'lest-wakatime'
  readonly name: string;
  readonly apiKey?: string;  // 用户配置的 WakaTime API Key

  /** 获取用户的编码时间统计（按天）*/
  getDailySummary(userId: string, range: TimeRange): Promise<WakatimeDailySummary[]>;

  /** 获取单个任务的编码时间 */
  getTimeByIssue(issueId: string, range: TimeRange): Promise<WakatimeTimeEntry[]>;

  /** 获取语言/项目分布 */
  getLanguageBreakdown(userId: string, range: TimeRange): Promise<LanguageStats[]>;

  /** 同步心跳数据 */
  syncHeartbeats(userId: string, from?: Date): Promise<SyncResult>;

  /** 获取热力图数据 */
  getHeatmap(userId: string, year: number): Promise<HeatmapData>;
}

interface WakatimeDailySummary {
  date: string;  // YYYY-MM-DD
  totalSeconds: number;
  languages: LanguageStats[];
  projects: ProjectStats[];
}

interface WakatimeTimeEntry {
  id: string;
  issueId?: string;
  project: string;
  language: string;
  startedAt: Date;
  endedAt: Date;
  durationSeconds: number;
}

interface LanguageStats {
  name: string;
  totalSeconds: number;
  percentage: number;
}

interface ProjectStats {
  name: string;
  totalSeconds: number;
  percentage: number;
}

interface HeatmapData {
  year: number;
  weeks: Array<Array<{ date: string; seconds: number }>>;
}

interface SyncResult {
  synced: number;
  skipped: number;
  errors: string[];
}

type MetricType = 'velocity' | 'burndown' | 'cycle_time' | 'lead_time' | 'cfd' | 'control_chart' | 'throughput';

interface MetricParams {
  projectId: string;
  sprintId?: string;
  startDate: Date;
  endDate: Date;
  teamMembers?: string[];
}

interface MetricResult {
  type: MetricType;
  value: number;
  unit: string;
  breakdown?: Record<string, number>;
  trend?: { direction: 'up' | 'down' | 'stable'; delta: number };
}

interface AggregatedResult {
  metric: MetricType;
  granularity: 'day' | 'week' | 'month';
  dataPoints: { date: string; value: number }[];
  summary: MetricResult;
}

interface MetricDefinition {
  type: MetricType;
  name: string;
  description: string;
  requiredParams: string[];
  supportedGranularities: ('day' | 'week' | 'month')[];
}
```

### F. 平台基础设施

**`StorageProvider` 接口定义：**

```typescript
interface StorageProvider extends BaseProvider {
  readonly id: string;
  readonly name: string;
  readonly isDefault: boolean;

  upload(file: File, path: string, options?: StorageOptions): Promise<StorageResult>;
  delete(path: string): Promise<void>;
  getUrl(path: string, options?: { expires?: number }): string;
  validateConfig(config: StorageConfig): Promise<boolean>;
  download?(path: string, destination: string): Promise<void>;
  copy?(src: string, dest: string): Promise<void>;
  move?(src: string, dest: string): Promise<void>;
  list?(prefix: string, options?: ListOptions): Promise<StorageFile[]>;
  multipartUploadInit?(file: File, path: string): Promise<string>;
  getPreviewUrl?(path: string): string;
}

interface File {
  name: string;
  size: number;
  contentType: string;
  stream?: ReadableStream;
  buffer?: Buffer;
}

interface StorageOptions {
  contentType?: string;
  expires?: number;
  metadata?: Record<string, string>;
}

interface StorageResult {
  path: string;
  size: number;
  contentType: string;
  url: string;
}

interface StorageConfig extends Record<string, unknown> {
  endpoint?: string;
  bucket?: string;
  accessKey?: string;
  secretKey?: string;
  region?: string;
}

interface ListOptions {
  limit?: number;
  marker?: string;
}

interface StorageFile {
  path: string;
  size: number;
  lastModified: Date;
  contentType: string;
}
```

**`AIProvider` 接口定义：**

```typescript
interface AIProvider extends BaseProvider {
  readonly id: string;  // 'openai' / 'claude' / 'gemini' / 'qwen'
  readonly name: string;
  readonly capabilities: ('chat' | 'embedding' | 'vision')[];

  chat(messages: AIMessage[]): Promise<AIResponse>;
  embeddings(texts: string[]): Promise<number[][]>;
  validateConfig(config: AIConfig): Promise<boolean>;
}

interface AIMessage {
  role: 'system' | 'user' | 'assistant';
  content: string;
  name?: string;
}

interface AIResponse {
  content: string;
  finishReason: 'stop' | 'length' | 'content_filter' | 'error';
  usage?: { promptTokens: number; completionTokens: number; totalTokens: number };
  error?: string;
}

interface AIConfig {
  apiKey: string;
  baseUrl?: string;
  model?: string;
  maxTokens?: number;
  temperature?: number;
}
```

**`SEARCH_PROVIDER` 接口定义：**

```typescript
interface SearchProvider extends BaseProvider {
  readonly id: string;  // 'postgres_fts' / 'elasticsearch' / 'meilisearch'
  readonly name: string;

  indexDocument(doc: SearchDocument): Promise<void>;
  indexBatch(docs: SearchDocument[]): Promise<void>;
  updateDocument(docId: string, doc: SearchDocument): Promise<void>;
  deleteDocument(docId: string): Promise<void>;
  search(query: SearchQuery): Promise<SearchResult>;
  rebuildIndex(): Promise<void>;
  getIndexStats?(): Promise<IndexStats>;
}

interface SearchDocument {
  id: string;
  type: string;
  title: string;
  content: string;
  metadata: Record<string, unknown>;
  projectId?: string;
  createdAt?: Date;
  updatedAt?: Date;
}

interface SearchQuery {
  query: string;
  filters?: SearchFilter[];
  page?: number;
  pageSize?: number;
  highlight?: boolean;
}

interface SearchFilter {
  field: string;
  operator: 'eq' | 'ne' | 'gt' | 'lt' | 'in' | 'range';
  value: unknown;
}

interface SearchResult {
  total: number;
  hits: SearchHit[];
  tookMs: number;
  page: number;
  pageSize: number;
}

interface SearchHit {
  id: string;
  type: string;
  title: string;
  highlights?: Record<string, string[]>;
  score: number;
}

interface IndexStats {
  documentCount: number;
  indexSizeBytes: number;
  lastIndexedAt?: Date;
}
```

**`I18NProvider` 接口定义：**

```typescript
interface I18NProvider extends BaseProvider {
  readonly id: string;
  readonly name: string;

  translate(text: string, from: Locale, to: Locale): Promise<string>;
  translateBatch(texts: string[], from: Locale, to: Locale): Promise<string[]>;
  getTranslations(locale: Locale, namespace?: string): Promise<Record<string, string>>;
}

type Locale = 'zh_CN' | 'zh_TW' | 'en_US' | 'ja_JP' | 'ko_KR' | 'fr_FR' | 'de_DE' | 'es_ES';
```

**`CONTENT_I18N_PROVIDER` 接口定义：**

```typescript
interface ContentI18NProvider extends BaseProvider {
  readonly id: string;
  readonly name: string;

  getContent(key: string, locale: Locale): Promise<string | null>;
  setContent(key: string, locale: Locale, value: string): Promise<void>;
  listLocales(): Locale[];
}
```

### G. 桌面端 Electron

**`OFFLINE_STORAGE_PROVIDER` 接口定义：**

```typescript
interface OfflineStorageProvider extends BaseProvider {
  readonly id: string;  // 'indexeddb' / 'electron-store'
  readonly name: string;

  saveTasks(tasks: Task[]): Promise<void>;
  getTasks(query?: TaskQuery): Promise<Task[]>;
  enqueueOperation(op: OfflineOperation): Promise<void>;
  getPendingOperations(): Promise<OfflineOperation[]>;
  markSynced(operationId: string): Promise<void>;
  resolveConflict(operationId: string, strategy: 'server-wins' | 'client-wins' | 'merge'): Promise<void>;
  getStorageStats(): Promise<{ usedBytes: number; taskCount: number; pendingOps: number }>;
  clear(): Promise<void>;
}

interface TaskQuery {
  projectId?: string;
  status?: string;
  assignee?: string;
  updatedSince?: Date;
  limit?: number;
}

interface OfflineOperation {
  id: string;
  type: 'CREATE' | 'UPDATE' | 'DELETE' | 'TRANSITION';
  entityType: 'task' | 'comment' | 'attachment';
  entityId?: string;
  payload: Record<string, unknown>;
  createdAt: Date;
  status: 'pending' | 'syncing' | 'synced' | 'conflict';
}
```

**`SYSTEM_TRAY_PROVIDER` 接口定义：**

```typescript
interface SystemTrayProvider extends BaseProvider {
  readonly id: string;
  readonly name: string;

  setIcon(iconPath: string, tooltip?: string): void;
  setContextMenu(menu: TrayMenuItem[]): void;
  showBalloon(title: string, content: string): void;
  onClick(handler: () => void): void;
  flash(intervalMs?: number): void;
  stopFlash(): void;
  toggleMainWindow(): void;
  quit(): void;
}

interface TrayMenuItem {
  id: string;
  label: string;
  icon?: string;
  enabled?: boolean;
  type?: 'normal' | 'separator' | 'checkbox' | 'submenu';
  submenu?: TrayMenuItem[];
}
```

**`GLOBAL_SHORTCUT_PROVIDER` 接口定义：**

```typescript
interface GlobalShortcutProvider extends BaseProvider {
  readonly id: string;
  readonly name: string;

  register(accelerator: string, handler: () => void): Promise<boolean>;
  registerBatch(shortcuts: Array<{ accelerator: string; handler: () => void }>): Promise<Record<string, boolean>>;
  unregister(accelerator: string): void;
  unregisterAll(): void;
  isRegistered(accelerator: string): boolean;
  getRegisteredShortcuts(): string[];
}
```

### H. 原生 App 端 React Native

**`PUSH_NOTIFICATION_PROVIDER` 接口定义：**

```typescript
interface PushNotificationProvider extends BaseProvider {
  readonly id: string;  // 'apns' / 'fcm' / 'jiguang' / 'unipush'
  readonly name: string;
  readonly platform: 'ios' | 'android' | 'both';

  getDeviceToken(): Promise<string>;
  registerToken(token: string, userId: string, metadata?: DeviceMetadata): Promise<void>;
  unregisterToken(token: string): Promise<void>;
  handleNotification(notification: RawNotification): NotificationResult;
  requestPermission(): Promise<PermissionStatus>;
  checkPermission(): Promise<PermissionStatus>;
  setBadgeNumber(number: number): Promise<void>;
  getDeliveredNotifications(): Promise<DeliveredNotification[]>;
}

interface DeviceMetadata {
  platform: 'ios' | 'android';
  osVersion: string;
  appVersion: string;
  deviceModel: string;
}

interface RawNotification {
  id: string;
  title: string;
  body: string;
  data?: Record<string, unknown>;
  foreground: boolean;
}

interface NotificationResult {
  handled: boolean;
  action?: string;  // 'open' / 'dismiss' / 'reply'
}

type PermissionStatus = 'granted' | 'denied' | 'notDetermined' | 'provisional';

interface DeliveredNotification {
  id: string;
  title: string;
  body: string;
  deliveredAt: Date;
  read: boolean;
}
```

**`BIOMETRIC_PROVIDER` 接口定义：**

```typescript
interface BiometricProvider extends BaseProvider {
  readonly id: string;  // 'face-id' / 'fingerprint' / 'biometric'
  readonly name: string;
  readonly supportedTypes: ('face' | 'fingerprint' | 'iris')[];

  isSupported(): Promise<{ supported: boolean; type: string }>;
  isEnrolled(): Promise<boolean>;
  authenticate(options: { reason: string; fallbackLabel?: string }): Promise<{ success: boolean; error?: string }>;
  enableBiometric(userId: string): Promise<void>;
  disableBiometric(userId: string): Promise<void>;
}
```

**`QR_SCAN_PROVIDER` 接口定义：**

```typescript
interface QRScanProvider extends BaseProvider {
  readonly id: string;
  readonly name: string;

  scanWithCamera(options?: { title?: string; flashOn?: boolean }): Promise<{ value: string; format: string }>;
  scanFromAlbum(): Promise<{ value: string; format: string }>;
  generate(data: string): Promise<string>;
}
```

**`OFFLINE_SYNC_PROVIDER` 接口定义：**

```typescript
interface OfflineSyncProvider extends BaseProvider {
  readonly id: string;  // 'watermelon-db' / 'realm' / 'sqlite'
  readonly name: string;

  initialize(): Promise<void>;
  saveOfflineData(entity: string, records: Record<string, unknown>[]): Promise<void>;
  getOfflineData(entity: string, query?: Record<string, unknown>): Promise<Record<string, unknown>[]>;
  enqueueOperation(op: SyncOperation): Promise<void>;
  getPendingOperations(): Promise<SyncOperation[]>;
  pullChanges(since: Date): Promise<ChangeSet[]>;
  pushChanges(operations: SyncOperation[]): Promise<SyncResult[]>;
  sync(): Promise<SyncReport>;
  onProgress(handler: (progress: SyncProgress) => void): void;
}

interface SyncOperation {
  id: string;
  type: 'CREATE' | 'UPDATE' | 'DELETE' | 'TRANSITION';
  entityType: string;
  entityId: string;
  payload: Record<string, unknown>;
  localVersion: number;
  createdAt: Date;
}

interface ChangeSet {
  entityType: string;
  changes: Array<{
    id: string;
    type: 'CREATED' | 'UPDATED' | 'DELETED';
    data: Record<string, unknown>;
    serverVersion: number;
  }>;
}

interface SyncResult {
  operationId: string;
  success: boolean;
  conflict?: boolean;
  serverVersion?: number;
  error?: string;
}

interface SyncReport {
  pulled: number;
  pushed: number;
  conflicts: number;
  durationMs: number;
}

interface SyncProgress {
  phase: 'pull' | 'push' | 'conflict';
  current: number;
  total: number;
}
```

### I. 开放 API 端

**`API_VERSION_MANAGER` 接口定义：**

```typescript
interface APIVersionManager extends BaseProvider {
  readonly id: string;
  readonly name: string;

  getActiveVersion(): string;
  listVersions(): APIVersion[];
  isEndpointAvailable(version: string, endpoint: string, method: string): boolean;
  getDeprecationInfo(version: string): DeprecationInfo | null;
  registerVersion(version: APIVersion): void;
  deprecate(version: string, deprecation: DeprecationInfo): void;
  migrateEndpoint(oldVersion: string, newVersion: string, endpoint: string): string | null;
  isBreakingChange(oldVersion: string, newVersion: string, endpoint: string): boolean;
}

interface APIVersion {
  version: string;
  status: 'active' | 'deprecated' | 'sunset';
  releaseDate: Date;
  deprecationDate?: Date;
  sunsetDate?: Date;
}

interface DeprecationInfo {
  sunsetDate: Date;
  migrationGuide: string;
  alternativeVersion?: string;
}
```

**`RATE_LIMIT_PROVIDER` 接口定义：**

```typescript
interface RateLimitProvider extends BaseProvider {
  readonly id: string;  // 'token-bucket' / 'sliding-window' / 'redis-counter'
  readonly name: string;

  checkLimit(identifier: string, scope: RateLimitScope): Promise<RateLimitCheckResult>;
  consume(identifier: string, scope: RateLimitScope, cost?: number): Promise<RateLimitResult>;
  resetLimit(identifier: string): Promise<void>;
  getQuota(identifier: string): Promise<RateLimitQuota>;
  setQuota(identifier: string, quota: RateLimitQuota): Promise<void>;
}

type RateLimitScope = 'user' | 'api_key' | 'ip' | 'global';

interface RateLimitCheckResult {
  allowed: boolean;
  limit: number;
  remaining: number;
  resetAt: Date;
  retryAfterMs?: number;
}

interface RateLimitResult {
  success: boolean;
  consumed: number;
  remaining: number;
}

interface RateLimitQuota {
  limit: number;
  windowSeconds: number;
  burst?: number;
}
```

**`OAUTH2_PROVIDER` 接口定义：**

```typescript
interface OAuth2Provider extends BaseProvider {
  readonly id: string;
  readonly name: string;

  getAuthorizationUrl(config: OAuth2Config): string;
  exchangeCode(config: OAuth2Config, code: string): Promise<OAuth2Token>;
  refreshToken(config: OAuth2Config, refreshToken: string): Promise<OAuth2Token>;
  revokeToken(token: string): Promise<void>;
  getUserInfo(accessToken: string): Promise<ExternalUser>;
  generateCodeChallenge(codeVerifier: string, method: 'S256' | 'plain'): string;
  generateCodeVerifier(): string;
  generateState(): string;
}

interface OAuth2Config {
  clientId: string;
  clientSecret: string;
  authorizationUrl: string;
  tokenUrl: string;
  redirectUri: string;
  scopes: string[];
  pkce: boolean;
}

interface OAuth2Token {
  accessToken: string;
  refreshToken?: string;
  expiresAt: Date;
  tokenType: string;
  scope: string;
}
```

### J. 跨端共享

**`DATA_SYNC_PROVIDER` 接口定义：**

```typescript
interface DataSyncProvider extends BaseProvider {
  readonly id: string;  // 'crdt' / 'last-write-wins' | 'operational-transform'
  readonly name: string;

  pull(since: SyncToken): Promise<ChangeBatch>;
  push(changes: LocalChange[]): Promise<PushResult[]>;
  detectConflicts(local: LocalChange[], remote: RemoteChange[]): Conflict[];
  resolveConflict(conflict: Conflict, strategy: ConflictStrategy): Resolution;
  merge(local: Document, remote: Document): Document;
}

type SyncToken = string;

interface ChangeBatch {
  token: SyncToken;
  changes: Array<{
    id: string;
    entityType: string;
    entityId: string;
    type: 'CREATED' | 'UPDATED' | 'DELETED';
    data: Record<string, unknown>;
    serverVersion: number;
    timestamp: number;
  }>;
}

interface LocalChange {
  id: string;
  entityType: string;
  entityId: string;
  localVersion: number;
  timestamp: number;
  data: Record<string, unknown>;
}

interface RemoteChange {
  id: string;
  entityType: string;
  entityId: string;
  serverVersion: number;
  timestamp: number;
  data: Record<string, unknown>;
}

interface Conflict {
  entityType: string;
  entityId: string;
  localVersion: number;
  serverVersion: number;
  localData: Record<string, unknown>;
  serverData: Record<string, unknown>;
}

type ConflictStrategy = 'server-wins' | 'client-wins' | 'merge';

interface Resolution {
  entityType: string;
  entityId: string;
  resolvedData: Record<string, unknown>;
  resolvedVersion: number;
}

interface Document {
  id: string;
  version: number;
  data: Record<string, unknown>;
}
```

### 共享错误类型定义

所有 Provider 方法的错误应使用以下标准错误类型：

```typescript
/** 标准 API 错误 */
class LestiError extends Error {
  constructor(
    public code: ErrorCode,
    message: string,
    public details?: unknown
  ) {
    super(message);
    this.name = 'LestiError';
  }
}

type ErrorCode =
  | 'INVALID_CONFIG'      // 配置错误
  | 'NETWORK_ERROR'       // 网络错误
  | 'AUTH_FAILED'        // 认证失败
  | 'RESOURCE_NOT_FOUND' // 资源不存在
  | 'RATE_LIMIT_EXCEEDED' // 限流
  | 'VALIDATION_ERROR'  // 参数校验失败
  | 'CONFLICT'          // 资源冲突
  | 'INTERNAL_ERROR';   // 内部错误
```

---

## 六、扩展点依赖图与初始化顺序

### 6.1 初始化 Phase

Core 按以下 Phase 顺序初始化扩展点：

```
Phase 1 — 核心基础设施（无依赖，并行初始化）
  ┌─────────────────────────────────────────────┐
  │  AUTH_PROVIDER                            │  ← 所有扩展点的根依赖
  │  STORAGE_PROVIDER                         │  ← 文件操作
  │  SEARCH_PROVIDER                          │  ← 全文搜索
  │  I18N_PROVIDER                            │  ← 翻译服务
  │  MAIL_PROVIDER                            │  ← 邮件发送
  │  AUDIT_HANDLER                            │  ← 审计日志
  └─────────────────────────────────────────────┘
           │
           ▼
Phase 2 — 认证与通知（依赖 Phase 1）
  ┌─────────────────────────────────────────────┐
  │  REALTIME_PROVIDER                        │  ← WebSocket 服务
  │  NOTIFICATION_CHANNEL_PROVIDER            │  ← IM 通知渠道
  │  PUSH_NOTIFICATION_PROVIDER               │  ← App 推送
  │  SMS_PROVIDER                             │  ← 短信
  │  IDENTITY_PROVIDER                        │  ← 第三方身份源
  │  MFA_PROVIDER                             │  ← 多因素认证
  │  CAPTCHA_PROVIDER                        │  ← 验证码
  │  WEBHOOK_HANDLER                          │  ← Webhook 接收
  │  OUTGOING_WEBHOOK                         │  ← Webhook 发送
  │  API_VERSION_MANAGER                      │  ← API 版本
  │  RATE_LIMIT_PROVIDER                     │  ← 限流
  │  OAUTH2_PROVIDER                         │  ← OAuth2
  │  AI_PROVIDER                             │  ← AI 服务
  │  PERMISSION_RULE_ENGINE                 │  ← 权限引擎
  │  FIELD_MASK_PROVIDER                      │  ← 字段屏蔽
  │  BIOMETRIC_PROVIDER                      │  ← 生物识别
  │  QR_SCAN_PROVIDER                       │  ← 扫码
  └─────────────────────────────────────────────┘
           │
           ▼
Phase 3 — 业务集成（依赖 Phase 1-2）
  ┌─────────────────────────────────────────────┐
  │  OFFLINE_STORAGE_PROVIDER                  │  ← 离线存储（Electron）
  │  OFFLINE_SYNC_PROVIDER                    │  ← 离线同步（React Native）
  │  DATA_SYNC_PROVIDER                       │  ← 冲突处理（最后初始化）
  │  SYSTEM_TRAY_PROVIDER                      │  ← 托盘
  │  GLOBAL_SHORTCUT_PROVIDER                 │  ← 快捷键
  │  CODE_HOST_INTEGRATION                     │  ← IDE 集成
  │  DIRECTORY_PROVIDER                        │  ← LDAP/AD
  │  CI_PROVIDER                              │  ← CI/CD
  │  CODE_HOSTING_PROVIDER                    │  ← 代码托管
  │  IM_PROVIDER                              │  ← IM 平台
  │  CALENDAR_PROVIDER                       │  ← 日历
  │  MEETING_PROVIDER                        │  ← 会议
  │  SCHEDULED_JOB                           │  ← 定时任务
  │  WORKLOG_PROVIDER                        │  ← 工时
  │  IMPORT_PROVIDER                         │  ← 导入
  │  EXPORT_PROVIDER                         │  ← 导出
  └─────────────────────────────────────────────┘
           │
           ▼
Phase 4 — 分析层（依赖 Phase 1-3）
  ┌─────────────────────────────────────────────┐
  │  METRICS_ENGINE                          │  ← 指标引擎
  │  REPORT_RENDERER                          │  ← 报表渲染
  │  AI_TASK_DECOMPOSER                      │  ← AI 任务拆解
  │  AI_RISK_DETECTOR                        │  ← AI 风险识别
  │  AI_FORECASTER                           │  ← AI 效能预测
  └─────────────────────────────────────────────┘
```

### 6.2 循环依赖解决方案

| 潜在循环 | 解决方案 |
|---------|---------|
| `REALTIME_PROVIDER` ↔ `NOTIFICATION_CHANNEL_PROVIDER` | `NOTIFICATION_CHANNEL_PROVIDER` 通过事件总线订阅，不直接调用 `REALTIME_PROVIDER` |
| `DATA_SYNC_PROVIDER` ↔ `REALTIME_PROVIDER` | `DATA_SYNC_PROVIDER` 订阅 `task.updated` 事件，不反向调用 |
| `OFFLINE_SYNC_PROVIDER` ↔ `STORAGE_PROVIDER` | `OFFLINE_SYNC_PROVIDER` 仅使用 `OFFLINE_STORAGE_PROVIDER`，不调用 `STORAGE_PROVIDER` |
| `PUSH_NOTIFICATION_PROVIDER` ↔ `REALTIME_PROVIDER` | `PUSH_NOTIFICATION_PROVIDER` 使用统一推送通道接口，不直接依赖 WebSocket |

### 6.3 Phase 内优先级

Phase 内按 `priority` 字段排序，数字越小越先初始化。默认 priority=100。

---

## 七、插件生命周期集成

每个插件可以实现以下生命周期钩子：

| 钩子 | 时机 | 返回类型 |
|------|------|---------|
| `onInstall` | 首次安装 | `Promise<InstallResult>` |
| `onUpgrade` | 版本升级 | `Promise<UpgradeResult>` |
| `onUninstall` | 卸载 | `Promise<CleanupResult>` |
| `onEnable` | 禁用→启用 | `Promise<void>` |
| `onDisable` | 启用→禁用 | `Promise<void>` |

详见：[V4.0 插件规划](../plugins/V4.0/README.md)

---

## 八、扩展点与版本映射

| 扩展点 | V1.0 | V2.0 | V3.0 | V4.0 | V5.0 | 任务文档 |
|--------|-------|-------|-------|-------|-------|---------|
| `AUTH_MECHANISM` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ Core V1.0 |
| `AUTH_PROVIDER` | — | ✅ | ✅ | ✅ | ✅ | ✅ Core V2.0 |
| `IDENTITY_PROVIDER` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ Core V1.0 |
| `DIRECTORY_PROVIDER` | — | — | — | — | ✅ | 🔴 待补充 |
| `MFA_PROVIDER` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ Core V1.0 |
| `CAPTCHA_PROVIDER` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ Core V1.0 |
| `AUDIT_HANDLER` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ Core V1.0 |
| `PERMISSION_RULE_ENGINE` | — | — | — | — | ✅ | 🔴 待补充 |
| `FIELD_MASK_PROVIDER` | — | — | — | — | ✅ | 🔴 待补充 |
| `MAIL_PROVIDER` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ Core V1.0 |
| `NOTIFICATION_CHANNEL_PROVIDER` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ Core V1.0 |
| `SMS_PROVIDER` | — | ✅ | ✅ | ✅ | ✅ | 🔴 待补充 |
| `REALTIME_PROVIDER` | — | ✅ | ✅ | ✅ | ✅ | ✅ [REALTIME_tasks.md](../../2-tasks/core/V2.0/REALTIME_PROVIDER_tasks.md) |
| `CI_PROVIDER` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ Plugins V1.0 |
| `CODE_HOSTING_PROVIDER` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ Plugins V1.0 |
| `CODE_HOST_INTEGRATION` | — | — | ✅ MVP | ✅ 完整 | ✅ | ✅ [桌面端_tasks.md](../../2-tasks/core/V3.0/桌面端_extensions_tasks.md) |
| `IM_PROVIDER` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ Plugins V1.0 |
| `WEBHOOK_HANDLER` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ Plugins V1.0 |
| `OUTGOING_WEBHOOK` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ Plugins V1.0 |
| `SCHEDULED_JOB` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ Core V1.0 |
| `MEETING_PROVIDER` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ Plugins V1.0 |
| `CALENDAR_PROVIDER` | — | — | ✅ | ✅ | ✅ | 🔴 待补充 |
| `IMPORT_PROVIDER` | — | ✅ | ✅ | ✅ | ✅ | ✅ Plugins V2.0 |
| `EXPORT_PROVIDER` | — | ✅ | ✅ | ✅ | ✅ | 🔴 待补充 |
| `WORKLOG_PROVIDER` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ Plugins V1.0 |
| `WAKATIME_PROVIDER` | — | ✅ | ✅ | ✅ | ✅ | 🔴 待补充 |
| `REPORT_RENDERER` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ Core V3.0 |
| `METRICS_ENGINE` | — | — | ✅ | ✅ | ✅ | 🔴 待补充 |
| `STORAGE_PROVIDER` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ Core V1.0 |
| `AI_PROVIDER` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ Core V4.0 |
| `SEARCH_PROVIDER` | ✅ | ✅ | ✅ | ✅ | ✅ | 🔴 待补充 |
| `I18N_PROVIDER` | ✅ | ✅ | ✅ | ✅ | ✅ | 🔴 待补充 |
| `CONTENT_I18N_PROVIDER` | — | — | — | ✅ | ✅ | 🔴 待补充 |
| `AUTOMATION_CUSTOM_ACTION` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ Core V3.0 |
| `OFFLINE_STORAGE_PROVIDER` | — | — | ✅ | ✅ | ✅ | ✅ [桌面端_tasks.md](../../2-tasks/core/V3.0/桌面端_extensions_tasks.md) |
| `SYSTEM_TRAY_PROVIDER` | — | — | ✅ | ✅ | ✅ | ✅ [桌面端_tasks.md](../../2-tasks/core/V3.0/桌面端_extensions_tasks.md) |
| `GLOBAL_SHORTCUT_PROVIDER` | — | — | ✅ | ✅ | ✅ | ✅ [桌面端_tasks.md](../../2-tasks/core/V3.0/桌面端_extensions_tasks.md) |
| `PUSH_NOTIFICATION_PROVIDER` | — | — | — | ✅ | ✅ | ✅ [V4.0 插件任务](../../2-tasks/plugins/V4.0/README.md) |
| `BIOMETRIC_PROVIDER` | — | — | — | ✅ | ✅ | ✅ [V4.0 插件任务](../../2-tasks/plugins/V4.0/README.md) |
| `QR_SCAN_PROVIDER` | — | — | — | ✅ | ✅ | ✅ [V4.0 插件任务](../../2-tasks/plugins/V4.0/README.md) |
| `OFFLINE_SYNC_PROVIDER` | — | — | — | ✅ | ✅ | ✅ [V4.0 插件任务](../../2-tasks/plugins/V4.0/README.md) |
| `API_VERSION_MANAGER` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ Plugins V1.0 |
| `RATE_LIMIT_PROVIDER` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ Plugins V1.0 |
| `OAUTH2_PROVIDER` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ Plugins V1.0 |
| `DATA_SYNC_PROVIDER` | — | — | ✅ | ✅ | ✅ | ✅ [桌面端_tasks.md](../../2-tasks/core/V3.0/桌面端_extensions_tasks.md) |

> **状态说明**：
> - ✅ = 已完成或有任务文档
> - 🔴 待补充 = 接口已定义但缺少任务分解文档
> - — = 该版本未交付

---

## 九、缺失任务文档清单

以下扩展点已定义接口，但缺少对应的任务分解文档：

| 扩展点 | 域 | 优先级 | 估计任务数 |
|--------|---|--------|----------|
| `SEARCH_PROVIDER` | F | 🟡 中等 | 15 |
| `SMS_PROVIDER` | C | 🟡 中等 | 10 |
| `I18N_PROVIDER` | F | 🟡 中等 | 10 |
| `CONTENT_I18N_PROVIDER` | F | 🟡 中等 | 10 |
| `CALENDAR_PROVIDER` | D | 🟡 中等 | 15 |
| `EXPORT_PROVIDER` | D | 🟡 中等 | 12 |
| `METRICS_ENGINE` | E | 🟡 中等 | 15 |
| `WAKATIME_PROVIDER` | E | 🟡 中等 | 12 |
| `PERMISSION_RULE_ENGINE` | B | 🟡 中等 | 20 |
| `FIELD_MASK_PROVIDER` | B | 🟡 中等 | 10 |
| `DIRECTORY_PROVIDER` | A | 🟠 高优 | 15 |
| `TASK_DETAIL_CONTEXT_MENU` | UI | 🟡 中等 | 8 |
| `COMMAND_PALETTE` | UI | 🟡 中等 | 15 |
| `GLOBAL_HEADER` | UI | 🟡 中等 | 8 |
| `MENU_ITEM` | UI | 🟡 中等 | 8 |
| `ISSUE_TYPE_ICON` | UI | 🟡 中等 | 8 |
| `FILTER_RENDERER` | UI | 🟡 中等 | 10 |

---

## 十、插件开发 Checklist

每个插件在 manifest.json 中声明使用的扩展点：

```json
{
  "id": "lest-im-dingtalk",
  "name": "钉钉集成",
  "version": "1.0.0",
  "targetEnds": ["web", "desktop"],
  "dependsOn": {
    "extensionPoints": ["NOTIFICATION_CHANNEL_PROVIDER", "WEBHOOK_HANDLER"]
  },
  "extensionPoints": [
    {
      "type": "NOTIFICATION_CHANNEL_PROVIDER",
      "id": "dingtalk-channel",
      "priority": 80
    },
    {
      "type": "WEBHOOK_HANDLER",
      "id": "dingtalk-webhook"
    }
  ],
  "lifecycle": {
    "onInstall": "lifecycle/onInstall.ts",
    "onEnable": "lifecycle/onEnable.ts"
  },
  "permissions": ["task:read", "notification:send"]
}
```

Core 在运行时通过 `ExtensionResolver` 按类型查询所有注册的扩展，按 `priority` 升序渲染。

---

## 十一、版本历史

| 版本 | 日期 | 修改内容 |
|------|------|---------|
| V1.0 | 2026-06-02 | 初始版本 |
| V1.1 | 2026-06-02 | TypeScript 接口名 `CodeRepositoryProvider` → `CodeHostingProvider`（保持与扩展点名一致） |
