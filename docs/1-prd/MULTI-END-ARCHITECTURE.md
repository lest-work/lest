# LEST Platform — 多端架构与版本覆盖

> **文档目的**：以产品经理视角，明确 LEST Platform 的**多端战略**，定义每个端的能力边界、功能分层，以及 5 个版本如何系统性地覆盖所有端的交付。
>
> **问题背景**：当前文档只在 `reference/ARCHITECTURE.md` 中定义了 3 个端（Web/H5/第三方），版本规划未系统性覆盖多端交付。本文档建立完整的多端体系。
>
> **最后更新**：2026-06-02

---

## 一、LEST 的端全景图

### 1.1 端清单

LEST 面向 6 类端，每类端有不同的用户和使用场景：

|| 端 | 技术栈 | 用户 | 场景 | 优先级 |
|--|---|------|------|------|--------|
| **A. Web 端** | Vue 3 + TypeScript | 团队成员（PC）| 核心工作区：看板/Backlog/任务详情 | P0 |
| **B. 桌面端** | Electron + Vue 3 | 开发者/PM | 离线可用、快捷键、通知托盘、GitHub 深度集成 | P1 |
| **C. 移动 H5 端** | Vue 3 响应式 | 移动办公者 | 审批/查看/评论，不需要创建 | P2 |
| **D. 原生 App 端** | React Native | 移动办公者 | 离线创建/扫码/H5 无法覆盖的场景 | P3 |
| **E. 开放 API 端** | REST API / GraphQL | 第三方系统 | CI/CD、BI 工具、Zapier、自动化 | P1 |
| **F. IM Bot 端** | 插件化（钉钉/企微/飞书）| IM 用户 | 通知推送、快速创建任务、审批 | P2 |

### 1.2 用户与端的关系

```
团队成员（80% 场景）
  └─ PC 工作 → Web 端（核心）
  └─ 离线/快捷键 → 桌面端
  └─ 移动审批 → 移动 H5 端 或 原生 App 端

开发者（15% 场景）
  └─ 代码 → 桌面端（VS Code 插件 + Electron）
  └─ CI/CD → 开放 API 端

管理者（5% 场景）
  └─ 仪表盘 → Web 端 / 移动 H5 端

第三方系统
  └─ 集成 → 开放 API 端

IM 用户
  └─ 通知/审批 → IM Bot 端（钉钉/企微/飞书内）
```

### 1.3 各端能力分层模型

```
                    Web 端    桌面端   移动 H5   原生 App   开放 API   IM Bot
                    ────      ────     ──────   ────────   ────────   ──────

核心工作流
  看板视图          ✅         ✅       ⚠️ 简化   ⚠️ 简化     ❌         ❌
  Backlog 管理      ✅         ✅       ❌        ⚠️ 简化     ❌         ❌
  任务创建          ✅         ✅       ⚠️ 简化   ✅          ✅         ⚠️ 快捷创建
  任务详情          ✅         ✅       ⚠️ 查看   ✅          ✅         ❌
  评论/活动流       ✅         ✅       ✅        ✅          ✅         ❌
  JQL 高级搜索      ✅         ✅       ❌        ❌          ✅         ❌
  自动化规则        ✅         ✅       ❌        ❌          ❌         ❌

管理功能
  项目设置          ✅         ✅       ❌        ❌          ✅         ❌
  工作流编辑器      ✅         ✅       ❌        ❌          ❌         ❌
  仪表盘 Gadget     ✅         ✅       ⚠️ 基础   ⚠️ 基础     ❌         ❌
  用户管理           ✅(Admin)  ✅(Admin) ❌        ❌          ✅         ❌

通知与协作
  站内消息          ✅         ✅       ✅        ✅          ❌         ❌
  邮件通知          ✅         ✅       ✅        ✅          ❌         ❌
  IM 通知           ✅         ✅       ✅        ✅          ❌         ✅
  ⌘K 命令面板      ✅         ✅       ❌        ❌          ❌         ❌
  键盘快捷键        ✅         ✅       ❌        ❌          ❌         ❌

离线能力
  离线读取          ❌         ✅       ❌        ✅          ❌         ❌
  离线创建          ❌         ✅       ❌        ✅          ❌         ❌
  变更同步          ❌         ✅       ❌        ✅          ❌         ❌

桌面特有
  系统通知托盘      ❌         ✅       ❌        ❌          ❌         ❌
  全局快捷键        ❌         ✅       ❌        ❌          ❌         ❌
  GitHub/GitLab 侧栏  ❌     ✅       ❌        ❌          ❌         ❌
  VS Code 插件       ❌         ✅       ❌        ❌          ❌         ❌

第三方集成
  Webhook 发送      ✅         ✅       ❌        ❌          ✅         ❌
  API Key 管理      ✅         ✅       ❌        ❌          ✅         ❌
  OAuth2 应用       ✅         ✅       ❌        ❌          ✅         ❌

图例：✅ 完整支持  ⚠️ 部分支持  ❌ 不支持
```

---

## 二、每端详细设计

### A. Web 端（Primary End）

**技术栈**：Vue 3 + TypeScript + Vite + Pinia + Vue Router

**定位**：LEST 的**主端**，所有功能在此交付。

```
用户入口：
  - PC 浏览器（Chrome/Firefox/Safari/Edge 最新版）
  - 平板横屏模式

核心场景：
  - 看板拖拽（Kanban）
  - Backlog 管理（Sprint Planning）
  - 任务详情编辑（Rich Editor）
  - JQL 高级搜索
  - 工作流可视化配置
  - 自动化规则配置
  - 仪表盘（多种 Gadget）
  - 项目/平台管理

不需要的功能：
  - 离线能力（交给桌面端）
  - 系统托盘通知（交给桌面端）
```

### B. 桌面端（Electron）

**技术栈**：Electron + Vue 3（复用 Web 端组件）+ electron-builder

**定位**：**开发者工作台**，面向需要高频操作和离线能力的用户。

```
用户入口：
  - macOS / Windows / Linux 安装包

与 Web 端的区别：
  1. 离线优先：Service Worker + IndexedDB，本地缓存任务数据
  2. 系统托盘：后台运行，托盘通知，不打扰工作流
  3. 全局快捷键：即使不在前台也能触发操作
  4. GitHub/GitLab 侧栏：IDE 风格，在代码页面看到关联任务
  5. VS Code 插件：在 VS Code 内查看任务、评论、状态变更
  6. 启动速度更快（Web 端需要打开浏览器）

技术架构：
  Web 端代码 → Electron 打包 → 桌面应用
              ↘ 共享同一套 Vue 组件库
  差异层：
    - electron/main.ts：原生 API（托盘/快捷键/通知）
    - electron/preload.ts：安全桥接（IPC）
    - electron/store.ts：离线数据（IndexedDB）
```

### C. 移动 H5 端（Responsive Web）

**技术栈**：Vue 3 + TypeScript + responsive CSS（复用 Web 端代码，响应式适配）

**定位**：**移动审批/查看**，不需要创建，只做快速查看和评论。

```
用户入口：
  - 手机浏览器（Safari/Chrome）
  - 微信/钉钉内置浏览器

功能策略：
  ✅ 收件箱查看（通知列表）
  ✅ 任务详情查看（只读为主）
  ✅ 评论添加
  ✅ 快速操作（状态变更/指派）
  ✅ 移动 H5 扫码绑定（V4.0 私有化部署场景）
  ❌ 看板拖拽（手势不友好，交给 App）
  ❌ Backlog 管理（交给 Web 端）
  ❌ JQL 搜索（交给 Web 端）
  ❌ 仪表盘编辑（交给 Web 端）

响应式断点：
  - < 768px：手机布局（单列卡片）
  - 768px - 1024px：平板布局（双栏）
  - > 1024px：降级到 Web 端提示
```

### D. 原生 App 端（React Native）

**技术栈**：React Native 0.76+ + TypeScript + Expo

**定位**：**移动全功能**，覆盖 H5 无法满足的场景。

```
用户入口：
  - iOS App Store
  - Android Play Store
  - 企业内测分发（企业内部 App）

功能策略：
  ✅ 完整任务管理（创建/编辑/查看）
  ✅ 扫码创建任务（关联 commit）
  ✅ 离线创建（队列同步）
  ✅ 推送通知（原生通道）
  ✅ 指纹/人脸识别登录
  ⚠️ 看板视图（简化版，竖向滚动）
  ❌ Backlog 管理（交给 Web 端）
  ❌ 工作流配置（交给 Web 端）
  ❌ JQL 搜索（交给 Web 端）

架构原则：
  - 与 Web 端共用后端 API（`lest-open` 服务）
  - 与 Web 端共用 REST 数据契约
  - 不共用前端代码（React Native ≠ Vue）
  - 通过 API 版本管理保持契约稳定
```

### E. 开放 API 端（lest-open）

**技术栈**：REST API（OpenAPI 3.0）+ GraphQL（可选）

**定位**：**第三方系统集成**，CI/CD、BI 工具、Zapier、N8N 等。

```
用户入口：
  - 第三方开发者（API Key）
  - OAuth2 授权的用户应用
  - CI/CD 系统（Webhook）

API 分层：
  lest-open/
  ├── REST API v1        # 稳定，遵循 OpenAPI 3.0
  ├── GraphQL            # 可选，适合复杂查询
  ├── Webhook (outgoing) # LEST → 第三方
  └── OAuth2             # 第三方 → LEST

核心 API 资源：
  - /api/v1/projects     # 项目 CRUD
  - /api/v1/tasks        # 任务 CRUD + 搜索
  - /api/v1/members      # 成员管理
  - /api/v1/sprints      # 迭代管理
  - /api/v1/attachments  # 附件上传
  - /api/v1/webhooks     # Webhook 管理
  - /api/v1/reports      # 报表数据

API 版本策略：
  - URL 版本化：/api/v1/, /api/v2/
  - 废弃策略：1年预警 → 1年过渡 → 废弃
  - Breaking Changes 只在主版本号变更时引入
```

### F. IM Bot 端（钉钉/企微/飞书）

**技术栈**：各 IM 平台的机器人 SDK（通过 `lest-im-dingtalk`/`lest-im-wecom`/`lest-im-feishu` 插件实现）

**定位**：**嵌入式协作**，用户不需要离开 IM 工具就能处理任务。

```
使用场景：
  1. 收到任务通知（在群里看到任务卡片）
  2. 快速创建任务（在群里输入命令）
  3. 审批（在 IM 内完成审批操作）
  4. 评论（在 IM 内回复任务）

Bot 命令：
  @LEST 创建任务 [标题]        # 快速创建
  @LEST 搜索 [关键词]          # 搜索任务
  @LEST 我的任务               # 查看分配给我的任务
  @LEST 审批 [任务KEY]         # 打开审批

架构原则：
  - Bot 功能通过插件实现（NOTIFICATION_CHANNEL + IM_PROVIDER 扩展点）
  - Bot 本身是 IM 平台的原生功能，不是独立 App
  - LEST 提供 WebSocket 推送 → 插件转发到各 IM 平台
```

---

## 三、架构分层（Backend for Frontend）

### 3.1 为什么需要 BFF 层

```
客户端请求
     │
     ▼
┌─────────────┐
│  Gateway    │  ← 统一入口：认证、路由、限流
└─────────────┘
     │
     ├──────────────────────────────────────────┐
     ▼                                          ▼
┌─────────────┐                          ┌─────────────┐
│  BFF-Web    │                          │  BFF-Mobile  │
│  (for Web)  │                          │  (for H5/App)│
└─────────────┘                          └─────────────┘
     │                                          │
     ▼                                          ▼
┌─────────────┐                          ┌─────────────┐
│  lest-task  │                          │  lest-task  │
│  lest-proj  │                          │  (same APIs) │
│  lest-auth  │                          └─────────────┘
└─────────────┘                                    
     │
     ▼
┌─────────────┐
│  lest-open  │  ← 开放 API（第三方系统）
└─────────────┘
```

**BFF-Web** 和 **BFF-Mobile** 的区别：

| 维度 | BFF-Web | BFF-Mobile |
|------|---------|-----------|
| 响应结构 | 适合大屏，多字段 | 精简字段，减少传输 |
| 关联数据 | 一次性加载（网络快）| 懒加载+分页 |
| 模板渲染 | SSR 可选 | 只做 CSR |
| 离线支持 | 不需要 | 需要（带缓存协议）|
| 推送通道 | WebSocket | 极光/FCM |

### 3.2 服务间通信

```
同一进程（ monolith → 模块化）
  └── lest-platform/
      ├── lest-gateway        # Spring Cloud Gateway
      ├── lest-bff-web        # BFF for Web
      ├── lest-bff-mobile     # BFF for Mobile
      ├── lest-open          # Open API（独立部署）
      ├── lest-task          # 任务域
      ├── lest-project       # 项目域
      ├── lest-auth          # 认证域
      ├── lest-notification   # 通知域
      ├── lest-worklog       # 工时域（插件）
      └── lest-ai           # AI 域（插件）

服务间调用：
  - 同步：gRPC（高性能）/ REST（简单场景）
  - 异步：Kafka（跨域事件，如任务变更 → 通知）
```

---

## 四、版本覆盖矩阵

### 4.1 各端在 5 个版本中的交付状态

|| 端 | V1.0 | V2.0 | V3.0 | V4.0 | V5.0 |
|--|---|------|------|------|------|------|
| **Web 端** | ✅ 完整交付 | ✅ 增强 | ✅ 增强 | ✅ 增强 | ✅ 增强 |
| **桌面端** | — | 🟡 规划 | 🔴 MVP | ✅ 完整 | ✅ 增强 |
| **移动 H5 端** | — | 🟡 响应式适配 | ✅ 移动 H5 | ✅ 增强 | ✅ 增强 |
| **原生 App 端** | — | — | — | 🟡 规划 | ✅ MVP |
| **开放 API 端** | ✅ REST + Webhook | ✅ GraphQL | ✅ 增强 | ✅ 增强 | ✅ 完整 |
| **IM Bot 端** | ✅ 通知推送 | ✅ 快捷命令 | ✅ 审批流 | ✅ 深度集成 | ✅ 全功能 |

### 4.2 V1.0 端策略（2026-08-28）

```
交付范围：
  ✅ Web 端：完整交付（看板/Backlog/任务/迭代/认证/通知/自动化）
  ✅ 开放 API 端：REST API v1 + Webhook + OAuth2
  ✅ IM Bot 端：通知推送（钉钉/企微/飞书）

不交付：
  ❌ 桌面端（Electron）
  ❌ 移动 H5 端（响应式）
  ❌ 原生 App 端

架构预埋：
  - BFF-Mobile 接口契约（预留，不实现）
  - 移动 H5 响应式断点（CSS 预留）
  - Service Worker 注册（预留离线能力）
  - WebSocket 推送通道（IM Bot 使用）
```

### 4.3 V2.0 端策略（2026-11-28）

```
交付范围：
  ✅ Web 端：AI 基础（任务助手/代码审查）
  ✅ 移动 H5 端：响应式适配（复用 Web 端组件，移动优先 CSS）
  ✅ 开放 API 端：GraphQL（复杂查询场景）
  ✅ IM Bot 端：快捷命令（/搜索/创建/我的任务）

桌面端预埋：
  - Electron 脚手架搭建（lest-desktop）
  - 主进程/预加载脚本框架
  - 离线存储层设计（IndexedDB）
  - 全局快捷键注册机制

架构工作：
  - BFF 层拆分（BFF-Web / BFF-Mobile）
  - WebSocket 推送服务（统一推送通道）
```

### 4.4 V3.0 端策略（2027-03-27）

```
交付范围：
  ✅ Web 端：仪表盘 Gadget + 看板增强
  ✅ 桌面端：MVP（Electron + 离线缓存 + 托盘通知）
  ✅ 移动 H5 端：完整移动 H5（查看/评论/快速操作）
  ✅ IM Bot 端：审批流（任务审批/通过/拒绝）

桌面端 MVP 功能：
  - 离线任务读取（IndexedDB 缓存）
  - 离线任务创建（队列同步）
  - 系统托盘 + 原生通知
  - 全局快捷键（Cmd+Shift+L 打开 LEST）
  - GitHub 侧栏（浏览当前仓库的关联任务）

架构工作：
  - 变更推送协议（Web → Desktop → 增量同步）
  - 冲突处理策略（CRDT 或 Last-Write-Wins）
```

### 4.5 V4.0 端策略（2027-12-10）

```
交付范围：
  ✅ Web 端：AI 智能排期 + AI 代码生成
  ✅ 桌面端：完整版（VS Code 插件 + GitLab 侧栏）
  ✅ 移动 H5 端：扫码（私有化部署场景）
  ✅ 原生 App 端：MVP（React Native，iOS + Android）
  ✅ IM Bot 端：深度集成（审批 + 任务卡片交互）

桌面端完整版：
  - VS Code 插件：查看任务详情、评论、状态变更
  - GitHub PR 侧栏：显示关联任务状态
  - GitLab MR 侧栏：同上
  - 本地构建触发（点击任务卡片触发 CI）

原生 App MVP：
  - 任务列表 + 详情查看
  - 任务创建（离线队列）
  - 评论添加
  - 推送通知（极光/FCM）
  - 指纹/人脸登录

架构工作：
  - React Native 与 Web 端共用数据契约（OpenAPI schema）
  - 私有化部署 App 连接（Nacos 服务发现 + QR 码绑定）
```

### 4.6 V5.0 端策略（2028-07-04）

```
交付范围：
  ✅ Web 端：完整企业版（多租户 + LDAP/SSO）
  ✅ 桌面端：企业特性（IT 批量部署/组策略）
  ✅ 移动 H5 端：完整企业版
  ✅ 原生 App 端：完整版（所有核心功能）
  ✅ IM Bot 端：全功能（任务卡片内完全操作）
  ✅ 开放 API 端：完整版（API 版本 v2）

企业特性：
  - 多租户隔离（Schema per tenant）
  - LDAP 同步 + SSO
  - 等保合规审计
  - 敏感数据行级加密
```

---

## 五、多端共享资产

### 5.1 代码共享策略

```
┌─────────────────────────────────────────────┐
│            OpenAPI Schema (统一契约)           │  ← 所有端共用
│         lest-open/api-schema/               │
│         (OpenAPI 3.0 YAML)                  │
└────────────────┬────────────────────────────┘
                 │ 代码生成
       ┌─────────┼─────────┬──────────────┐
       ▼         ▼         ▼              ▼
  ┌────────┐ ┌────────┐ ┌──────────┐ ┌──────────┐
  │Vue 组件 │ │ RN 组件│ │  API SDK │ │Bot  SDK  │
  │(Web)   │ │(App)   │ │(所有端)   │ │(IM Bot) │
  └────────┘ └────────┘ └──────────┘ └──────────┘

共享层：
  - OpenAPI Schema → ts-axios 客户端（所有端共用）
  - Vue 组件 → 移动 H5 响应式适配（断点 CSS）
  - Electron → 复用 Vue 组件，仅加原生层
  - React Native → 不复用 Vue 代码，但共用 API 契约
```

### 5.2 数据一致性策略

```
多端数据流：
  Web 端 ←→lest-bff-web←→ lest-task（主数据源）
  桌面端 ←→lest-bff-web←→ lest-task（主数据源）  +  IndexedDB（本地缓存）
  移动端 ←→lest-bff-mobile←→ lest-task（主数据源）
  App 端 ←→lest-bff-mobile←→ lest-task（主数据源）  +  SQLite（本地缓存）

冲突处理：
  - 移动端离线创建 → 队列同步 → 服务端冲突检测 → Last-Write-Wins 或 Merge UI
  - 乐观更新：先更新 UI → 后台同步 → 失败回滚
  - 通知推送：服务端 Kafka → WebSocket（Web/桌面）→ 极光/FCM（App）
```

---

## 六、关键决策

### 决策 1：为什么不共用一套前端代码

| 方案 | 优点 | 缺点 | 结论 |
|------|------|------|------|
| 一套 Vue 代码（H5 响应式）| 维护成本低 | App 体验差（WebView 性能）| Web + H5 共用，App 独立 |
| React Native 共用 | 部分代码复用 | RN ≠ Vue，共享有限 | App 独立 |
| Electron 复用 Web 代码 | 最大化复用 | 桌面体验不如原生 | **采用**：Electron 复用 Web 代码 |

**结论**：
- Web + H5：**一套 Vue 代码**，响应式 CSS（`tailwindcss` 断点）
- 桌面端：**Electron + 同一套 Vue 代码** + 原生层
- App 端：**React Native 独立代码**，共用 OpenAPI Schema

### 决策 2：Web 端 vs 桌面端的边界

桌面端不是 Web 端的简单复制。桌面端的独特价值：

1. **离线工作**（开发者经常在地铁/飞机上）
2. **全局快捷键**（Cmd+Shift+L 随时打开，不打断工作流）
3. **IDE 集成**（VS Code 插件，代码即上下文）
4. **系统托盘**（后台运行，重要通知不漏接）

**结论**：桌面端是 Web 端的**能力增强**，不是替代。V3.0 开始交付。

### 决策 3：移动 App 的必要性

移动 H5 能覆盖 80% 的移动场景（查看/审批/评论），但以下场景需要原生 App：

- **扫码创建**（现场扫码关联 commit，H5 无法调用摄像头扫码）
- **指纹登录**（H5 WebAuthn 体验差）
- **推送通知**（原生通道到达率 > 95%，H5 推送依赖浏览器）
- **离线创建**（网络不稳定场景，如工厂/工地）

**结论**：V4.0 交付 React Native App MVP，覆盖 H5 无法满足的 20% 场景。

### 决策 4：开放 API 的优先级

开放 API（`lest-open`）是第三方集成的入口，但有 3 类消费者：

| 消费者 | 需求 | 交付版本 |
|--------|------|---------|
| CI/CD 系统 | 创建任务/读取状态/Webhook | V1.0 |
| BI/报表工具 | 查询统计/导出 | V1.0 |
| Zapier/N8N | 自动化集成 | V2.0 |
| 企业内部系统 | SSO + API | V3.0 |

**结论**：V1.0 交付 REST API v1，后续版本逐步完善。
