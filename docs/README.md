# LEST Platform 文档中心

## 产品愿景

LEST Platform 是面向中国及全球企业的 **AI-Native 敏捷管理平台**，核心目标是打造一款**高度可定制、灵活扩展**的项目管理系统。

### 我们要做什么

不是又一个项目管理工具，而是：

- **对标 Jira 的灵活性**：采用 Jira 的 Scheme 架构体系，支持自定义字段、类型、状态、工作流、屏幕配置，让每个团队都能塑造最适合自己的工作方式
- **超越 Jira 的完整性**：集成 AI 智能（代码审查、任务拆解、效能分析）、IDE 活动追踪、团队绩效、办公 IM 集成，做 Jira 做不了的事
- **符合中国国情**：深度集成企业微信、钉钉、飞书，支持国产数据库和操作系统，提供本地化部署和私有化支持

### 开源 + 商业双版本策略

| 版本 | 定位 | 许可 | 定价 |
|------|------|------|------|
| **开源版（Community）** | 核心功能完整，开源免费 | MIT License | 免费 |
| **企业版（Enterprise）** | 高级功能 + 官方支持 | 商业许可 | 按席位/年 |

- **开源版永久免费**：支持个人、小团队、私有化部署
- **企业版增值功能**：多租户隔离、LDAP/SSO/OIDC、审计日志、 SLA 保障、高级报表、官方技术支持
- **核心路线一致**：开源版和企业版同代码分支，企业版在开源基础上叠加商业功能

---

## 文档体系说明

LEST Platform 文档全面对标 **Jira** 的产品结构，将每个功能模块视为一个 Jira Project，以 Jira 的概念体系作为设计语言。

### Jira 概念映射

| Jira 概念 | LEST 对应 | 说明 |
|-----------|---------|------|
| **Project** | 模块（PRD） | 每个功能模块 = 一个 Jira Project |
| **Issue Type** | 任务类型 | Epic / Story / Task / Bug 等，V2.0 支持自定义 |
| **Workflow** | 工作流 | 任务状态流转规则，V2.0 支持完整定制 |
| **Status** | 状态 | 待办 / 进行中 / 已完成等，V2.0 可新增 |
| **Custom Field** | 自定义字段 | 支持 25 种字段类型，V2.0 完整实现 |
| **Screen** | 表单 Screen | 定义字段在哪个 Tab 显示 |
| **Screen Scheme** | 屏幕方案 | Create / Edit / View 各操作对应哪个 Screen |
| **Field Configuration** | 字段行为 | 隐藏 / 必填 / 只读 / 标签 |
| **Board** | 看板 | 任务看板视图 |
| **Sprint** | 迭代 | 敏捷迭代 |
| **Component** | 子模块 | 项目的功能子分区 |
| **Version** | 版本/里程碑 | 发布版本规划 |

### 文档版本说明

| 版本 | 定位 | 说明 |
|------|------|------|
| **V1.0** | 已实现的基础功能 | 硬编码的任务类型/状态/工作流，快速启动 |
| **V2.0** | 高度定制化体系 | 完整 Jira 方案：EAV 自定义字段 + Scheme 映射 + 工作流引擎 |
| **V3.0** | **Jira 100% 收口 + 企业协作增强** | 补齐 Jira 全量能力并验收 100% + 中国特色：IM 集成、审批流、Portfolio、OKR |
| **V4.0** | AI 原生增强 | AI 深入每个业务流程 |
| **V5.0** | 企业旗舰版 | 多租户、审计合规、大规模部署 |

> **重要提示**：V1.0 是固定配置，V2.0 是可定制配置。随着各版本逐步实现，旧版本文档将标注"已废弃，由新版替代"。

---

## 产品路线图

### 5 大版本规划

```
V1.0 ──────────────► V2.0 ──────────────► V3.0 ──────────────► V4.0 ──────────────► V5.0
2026年8月发布           2026-11-28            2027-03-27            2027-12-10            2028-07-04
│                       │                      │                      │                      │
开源基础版               高度定制化              企业协作增强             AI原生增强             企业旗舰版
(Jira基础能力)          (Jira完整方案)          (中国特色)              (AI深度集成)           (大规模企业)
│                       │                      │                      │                      │
核心功能完整            EAV自定义字段            IM深度集成              AI自动拆解             多租户隔离
MIT开源+免费            Screen/Scheme           审批流                  AI预测分析             LDAP/SSO
私有化部署              工作流引擎              Portfolio/OKR           AI代码审查             审计合规
                        项目模板               国产化支持               智能排期               SLA保障
```

### 版本详情

| 版本 | 发布时间 | 核心特性 | 目标用户 | 开源 |
|------|---------|---------|---------|------|
| **V1.0** | 2026-08 | 项目/任务/看板、认证/用户/角色、通知、会议、发布、CI、WakaTime、绩效、AI基础 | 敏捷团队 | ✅ MIT |
| **V2.0** | 2026年底 | EAV自定义字段(25种)、Screen/Scheme多层映射、工作流引擎(条件/校验/动作)、项目模板、**插件系统上线**(工时/CI-CD/WakaTime/IM集成全部插件化) | 需要灵活配置的团队 | ✅ MIT |
| **V3.0** | 2027年中 | **Jira 功能 100% 收口**（Reports/Automation/附件/工时/批量等）+ 企业微信/钉钉/飞书深度集成、审批流、Portfolio多项目治理、OKR目标管理、国产数据库优化 | 中大型企业 | ✅ MIT + 🔒 |
| **V4.0** | 2027-2028 | AI自动生成任务/子任务、AI燃尽图预测、AI风险识别、AI智能排期、AI代码质量分析 | AI驱动的团队 | ✅ MIT |
| **V5.0** | 2028年 | 多租户隔离、LDAP/OIDC/SSO、审计日志、权限精细化、报表中心、SLA保障、官方支持 | 超大型企业 | ❌ 商业版 |

---

## 文档目录

```
docs/
├── README.md                    ← 你在这里
├── 1-prd/                      # 产品需求文档（PRDs）
│   ├── README.md                ← PRD 总索引 + 模块关联矩阵
│   ├── SCHEME-REFERENCE.md      ← Scheme 参考手册（Jira 方案完整对照）
│   ├── core/                    # Core 核心层（平台必须）
│   │   ├── V1.0/               # V1.0：硬编码基础版（5 个模块）
│   │   │   ├── 认证系统与系统管理.md
│   │   │   ├── 项目管理.md
│   │   │   ├── 任务管理.md
│   │   │   ├── 通知与消息.md
│   │   │   └── 系统设置.md
│   │   └── V2.0/               # V2.0：高度定制化版（3 个模块）
│   │       ├── 任务管理.md         ← Jira EAV 完整方案
│   │       ├── 项目管理.md         ← Jira Project Scheme 体系
│   │       └── 插件架构与功能分层.md  ← Core vs Plugin 边界定义
│   └── plugins/                 # 官方插件层（可插拔，21 个插件）
│       ├── V1.0/               # V1.0：7 个核心插件 + 6 个附加插件
│       │   ├── lest-worklog.md
│       │   ├── lest-cicd.md
│       │   ├── lest-wakatime.md
│       │   ├── lest-ai.md
│       │   ├── lest-code.md
│       │   ├── lest-meeting.md
│       │   ├── lest-report.md
│       │   ├── lest-release.md
│       │   ├── lest-openapi.md
│       │   ├── lest-plugin-sdk.md
│       │   ├── lest-im-feishu.md
│       │   ├── lest-im-dingtalk.md
│       │   └── lest-im-wecom.md
│       └── V2.0/               # V2.0：8 个新增插件
│           ├── lest-jira-sync.md
│           ├── lest-devops.md
│           ├── lest-gitea.md
│           ├── lest-gogs.md
│           └── lest-zentao-import.md
│
├── 2-tasks/                    # 开发任务单
│   ├── README.md
│   ├── core/                    # Core 开发任务单
│   │   ├── V1.0/               # V1.0 已完成
│   │   └── V2.0/               # V2.0 定制化任务
│   │       ├── 任务管理_tasks.md
│   │       └── 项目管理_tasks.md
│   └── plugins/                 # 插件开发任务单
│       ├── V1.0/
│       └── V2.0/
│
├── MILESTONES/                  # 🆕 小版本发布计划（V1.0-V5.0 各版本的详细小版本规划）
│   ├── V1.0-小版本规划.md     # v0.3 ~ v1.0.0（9个版本）
│   ├── V2.0-小版本规划.md     # v2.0.0-alpha ~ v2.0.6（7个版本）
│   ├── V3.0-小版本规划.md     # v3.0.0-alpha ~ v3.1.0（11个版本）
│   ├── V4.0-小版本规划.md     # v4.0.0-alpha ~ v4.0.6（7个版本）
│   └── V5.0-小版本规划.md     # v5.0.0-alpha ~ v5.0.7（8个版本）
│
├── guide/                       # 开发指南
│   ├── BRANCHING.md             # 分支管理规范
│   ├── DEPLOYMENT.md            # 部署指南
│   ├── DEVELOPMENT.md           # 开发环境搭建
│   └── DOMAIN_PLAN.md           # 领域模型规划
│
├── reference/                   # 技术参考
│   ├── ARCHITECTURE.md          # 架构设计文档
│   ├── DATABASE.md              # 数据库设计文档
│   └── api/                     # API 文档
│       ├── API.md               # API 文档（中文）
│       ├── API.en.md            # API 文档（英文）
│       └── API.zh-CN.md         # API 文档（简体中文）
│
└── assets/                      # 文档资源（图片、图标等）
```

> **目录结构说明**：从 V2.0 开始，文档按 **Core 核心** vs **Plugin 插件** 分离。Core 是平台必须的基础层；Plugin 是可插拔的功能扩展。
>
> **维护追踪**：所有待办事项和文档完成度见 [TODO.md](./TODO.md)。

---

## 快速导航

### 产品需求文档（PRD）

#### Core 核心模块

| 文档 | 版本 | Jira 映射 | 状态 |
|------|------|---------|------|
| [认证系统与系统管理](./1-prd/core/V1.0/认证系统与系统管理.md) | V1.0 | Atlassian Access | 已完成 |
| [项目管理](./1-prd/core/V1.0/项目管理.md) | V1.0 + **V2.0** | Jira Project | ✅ PRD 已完成 |
| [任务管理](./1-prd/core/V1.0/任务管理.md) | V1.0 + **V2.0** | Jira Issue + Workflow | ✅ PRD 已完成 |
| [通知与消息](./1-prd/core/V1.0/通知与消息.md) | V1.0 | Jira Notifications | 已完成 |
| [系统设置](./1-prd/core/V1.0/系统设置.md) | V1.0 | Jira Administration | 已完成 |

#### Plugin 插件模块

| 文档 | 版本 | Jira 映射 | 状态 |
|------|------|---------|------|
| [代码管理](./1-prd/plugins/V1.0/lest-code.md) | V1.0 | Jira Git integration | 已完成 |
| [CI持续集成](./1-prd/plugins/V1.0/lest-cicd.md) | V1.0 → **插件** | Jira CI/CD | ✅ PRD 已完成 |
| [敏捷会议](./1-prd/plugins/V1.0/lest-meeting.md) | V1.0 → **插件** | Jira Meetings | ✅ PRD 已完成 |
| [团队绩效](./1-prd/plugins/V1.0/lest-report.md) | V1.0 → **插件** | Jira Reports | ✅ PRD 已完成 |
| [发布管理](./1-prd/plugins/V1.0/lest-release.md) | V1.0 → **插件** | Jira Releases | ✅ PRD 已完成 |
| [AI服务](./1-prd/plugins/V1.0/lest-ai.md) | V1.0 → **插件** | Jira AI | ✅ PRD 已完成 |
| [WakaTime集成](./1-prd/plugins/V1.0/lest-wakatime.md) | V1.0 → **插件** | IDE Activity Tracker | ✅ PRD 已完成 |
| [插件系统](./1-prd/plugins/V1.0/lest-plugin-sdk.md) | V1.0 | Atlassian Marketplace | 已完成 |
| [开放平台](./1-prd/plugins/V1.0/lest-openapi.md) | V1.0 | Jira REST API | 已完成 |

#### Core 核心层 -- V3.0

| 文档 | Jira 映射 | 说明 | Jira 对齐率 | 状态 |
|------|---------|------|-----------|------|
| [任务管理 V3.0](./1-prd/core/V3.0/任务管理.md) | Jira Issue + Automation | 补全 Issue Link/附件/时间追踪/子任务/自动化执行引擎/批量操作/投票分享/快捷键/回收站 | **100%** | ✅ PRD 已完成 |
| [敏捷看板增强](./1-prd/core/V3.0/敏捷看板增强.md) | Jira Board + Reports | 泳道/WIP/燃尽图/CFD/Velocity/Control Chart/容量/史诗进度 | **100%** | ✅ PRD 已完成 |
| [仪表盘与日历视图](./1-prd/core/V3.0/仪表盘与日历视图.md) | Jira Dashboard + Calendar | 个人仪表盘+Gadget 目录、日历视图、过滤器订阅、i18n、暗黑模式 | **100%** | ✅ PRD 已完成 |
| [看板快捷筛选增强](./1-prd/core/V3.0/看板快捷筛选增强.md) | Jira Quick Filters | 快捷筛选按钮、卡片着色、子任务折叠、列折叠、WIP 可视化 | **100%** | ✅ PRD 已完成 |

#### Plugin 插件层 -- V3.0

| 文档 | 说明 | 状态 |
|------|------|------|
| [V3.0 插件总览](./1-prd/plugins/V3.0/README.md) | OKR、Portfolio、审批流、LDAP、SSO、多租户（6 个新插件） | ✅ PRD 已完成 |

#### Scheme 与架构文档

| 文档 | 说明 |
|------|------|
| [SCHEME-REFERENCE.md](./1-prd/SCHEME-REFERENCE.md) | 完整 Scheme 体系参考（Jira 方案核心设计文档） |
| [插件架构与功能分层](./1-prd/core/V2.0/插件架构与功能分层.md) | Core 核心层 vs 插件边界、官方插件矩阵（21 个插件） |

### 开发任务单（Tasks）

| 目录 | 说明 |
|------|------|
| [2-tasks/core/V1.0/](./2-tasks/core/V1.0/) | Core V1.0 开发任务单（已完成） |
| [2-tasks/core/V2.0/](./2-tasks/core/V2.0/) | Core V2.0 开发任务单（规划中） |
| [2-tasks/core/V3.0/](./2-tasks/core/V3.0/) | Core V3.0 开发任务单（Jira 100% 功能对齐收口） |
| [2-tasks/plugins/V1.0/](./2-tasks/plugins/V1.0/) | 插件 V1.0 开发任务单（规划中） |
| [2-tasks/plugins/V2.0/](./2-tasks/plugins/V2.0/) | 插件 V2.0 开发任务单（规划中） |
| [2-tasks/plugins/V3.0/](./2-tasks/plugins/V3.0/) | 插件 V3.0 开发任务单（企业协作增强：OKR/Portfolio/审批流等） |

---

## 模块关联矩阵

| LEST 模块 | Jira 对应 | 依赖模块 | 被依赖模块 |
|-----------|---------|---------|----------|
| 任务管理 | Jira Issues | 代码管理、通知、敏捷会议 | 代码管理、CI、发布、会议、绩效 |
| 项目管理 | Jira Projects | 任务管理 | 任务、CI、发布、会议 |
| 代码管理 | Jira Git integration | 任务管理 | 任务、CI、通知 |
| CI持续集成 | Jira CI/CD | 代码管理 | 发布、任务 |
| 发布管理 | Jira Releases | CI、任务 | 通知 |
| 通知与消息 | Jira Notifications | 所有模块 | 所有模块 |
| 敏捷会议 | Jira + Confluence | 任务、绩效 | 任务、绩效 |
| 团队绩效 | Jira Reports | 任务、代码、WakaTime、会议 | 通知 |
| WakaTime集成 | IDE Tracker | 任务 | 绩效、通知 |
| AI服务 | Atlassian Intelligence | 所有模块 | 通知 |
| 认证系统 | Atlassian Access | - | 所有模块 |
| 系统设置 | Jira Administration | 认证 | - |
| 插件系统 | Atlassian Apps | - | AI服务 |
| 开放平台 | Jira REST API | 认证 | 通知 |

---

## 错误码规范

| 模块 | 错误码范围 |
|------|-----------|
| 认证系统 | 1000-1999 |
| 用户管理 | 2000-2999 |
| 角色管理 | 3000-3999 |
| 菜单管理 | 4000-4999 | ← 已废弃（V2.0 删除菜单管理模块）|
| 机构管理 | 5000-5999 | ← 已废弃（V2.0 删除机构管理模块）|
| 字典管理 | 6000-6999 | ← 已废弃（V2.0 用 EAV 自定义字段代替）|
| 项目管理 | 7000-7999 |
| 任务管理 | 8000-8999 |
| 工时管理 | 8100-8199 |
| 代码管理 | 10000-10999 |
| CI/CD | 11000-11999 |
| 消息通知 | 12000-12999 |
| 会议管理 | 13000-13999 |
| 团队绩效 | 14000-14999 |
| 发布管理 | 15000-15999 |
| AI 服务 | 16000-16999 |
| 插件系统 | 17000-17999 |
| 开放平台 | 18000-18999 |
| WakaTime 集成 | 19000-19999 |
| 系统设置 | 9500-9599 |
| **AI 绩效洞察** | **16500-16599** |

---

## 更新日志

- **2026-06-01（第八轮）**：全面 Jira 化改造——建立 `1-prd/` / `2-tasks/` / `3-guide/` / `4-reference/` 目录结构；新增 `SCHEME-REFERENCE.md`（Jira Scheme 完整参考手册）；更新 `README.md` 总索引添加 Jira 概念映射表 + 产品愿景 + 5 版本路线图 + 开源/商业双版本策略；各 V1.0 PRD 模块添加 Jira 概念映射头部；任务管理 V2.0 升级为 Jira EAV 完整方案（自定义字段 + Screen/Scheme + 工作流引擎）；项目管理 V2.0 升级为 Jira Project Scheme 体系；新增 V2.0 开发任务单（`2-tasks/V2.0/`）
- **2026-06-02（第九轮）**：完成 V1.0/V2.0/V3.0 所有插件 PRD（共 21 个插件），补全缺失的 Vote/Share/批量操作/键盘快捷键/自动化规则模板到 V3.0 任务管理 PRD，创建 V3.0 项目管理 PRD，修复 V2.0 项目管理.md 章节编号错误（1-15 顺序正确），新增 `docs/TODO.md` 全局追踪文档，小版本发布计划（V1.0-V5.0 共 5 个 MILESTONES 文件）全面完善
- **2026-06-02（第十一轮）**：MILESTONES 与 PRD/TASK 对齐修复——修正 V2.0 发布时间为 2026-11-28（从 2026-12-26 前移）；V3.0 所有子版本日期前移约 2 周，正式发布调整为 2027-03-27，消除了 V2.0/V3.0 时间线矛盾；补全 PRD 插件矩阵遗漏的 lest-code 插件（v1.5）；删除 V2.0 插件列表重复条目（lest-jira-sync/devops/gitea/gogs 各重复一次）；PRD 所有 Core/Plugin 表格新增「小版本」列，标注功能首次实现的子版本号；PRD 新增「状态说明」，区分「PRD 文档完成」vs「开发完成」；V1.0 小版本规划新增「插件归属汇总」表；V2.0 小版本汇总表新增插件列，标注 v2.5 的 9 个插件增强版；V3.0 小版本汇总表添加说明，解释 lest-ldap/sso/multi-tenant「PRD 设计在 V3.0，实施在 V5.0」策略；补全 V1.0 插件 TASK 文件（NF/PLG/OP/UIP/SYS/MTG/REL/WL/WK/CI/PRF/RPT/AI/IM/CD 共 120+ 任务 ID）；补全 V2.0 插件 TASK 文件（9 个插件，V25-*-* 任务 ID 齐全）；全面更新 docs/README.md、1-prd/README.md、2-tasks/README.md、TODO.md 中的版本时间线
- **2026-06-02（第十轮）**：修复版本命名不一致问题——重写 `MILESTONES.md`（英文版）使其与 `MILESTONES.zh-CN.md` 结构完全对齐（V1.0 含 M1-M10 子里程碑 + V2.0/V3.0/V4.0/V5.0），修复 TODO.md 版本计划表状态描述（V1.0 改为"进行中"、V2.0/V3.0 改为"规划完成待启动"），全面对齐所有文档的版本命名体系；修复 V2.0 Core README 缺失 `UI设计规范` 模块、修复 V3.0 Core README 缺失 `仪表盘与日历视图` 和 `看板快捷筛选增强` 模块；修复 TODO.md 统计数字（V2.0 Core 6→5，V2.0 插件 10→9，任务单 13→22，总计 53→50）；补充 V2.0 插件 README 和 1-prd/README.md 缺失的 `lest-ai.md` 和 `lest-jira-compat.md` 条目；全面对齐各 README 的模块数量与实际文件
