# PRD 产品需求文档

> LEST Platform 的产品需求文档全面对标 **Jira** 的产品结构，每个功能模块视为一个 Jira Project。

## 版本时间线（已与 MILESTONES 对齐）

|| 版本 | 发布时间 | 许可 | 开源内容 | 商业内容 |
||------|---------|------|---------|---------|
|| **V1.0** | 2026-08-28 | MIT | 所有功能 | — |
|| **V2.0** | 2026-11-28 | MIT | 所有功能 | — |
|| **V3.0** | 2027-03-27 | MIT + 商业 | 基础 IM 集成、OKR 基础 | 审批流高级版、Portfolio、国产数据库 |
|| **V4.0** | 2027-12-10 | MIT + 商业 | AI 基础功能 | AI 智能排期、AI 代码生成 |
|| **V5.0** | 2028-07-04 | MIT + 商业 | 基础多租户 | 多租户完整隔离、LDAP/SSO、审计合规 |

---

## 文档索引

### Core 核心层 — V1.0

|| 文档 | Jira 映射 | 说明 | 小版本 | 状态 |
||------|---------|------|--------|------|
|| [认证系统与系统管理](./core/V1.0/认证系统与系统管理.md) | Atlassian Access + Project Roles | JWT 认证、邀请制项目成员管理、平台级配置（邮件/安全/审计）、项目角色（Admin/Member/Viewer）| — | ✅ PRD 已完成 |
|| [项目管理](./core/V1.0/项目管理.md) | Jira Project | 项目、成员、迭代、里程碑（V2.0 将支持 Scheme 配置）| — | ✅ PRD 已完成 |
|| [任务管理](./core/V1.0/任务管理.md) | Jira Issue + Workflow | 任务、看板、甘特、工时、Webhook（V2.0 将支持完整定制）| — | ✅ PRD 已完成 |
|| [通知与消息](./core/V1.0/通知与消息.md) | Jira Notifications | 站内信、邮件、IM 通知渠道（钉钉/企微/飞书放插件）| — | ✅ PRD 已完成 |
|| [系统设置](./core/V1.0/系统设置.md) | Jira Administration | 精简配置：邮件、安全、审计日志（删除 OA 功能）| — | ✅ PRD 已完成 |
|| [系统管理迁移方案](./core/V1.0/系统管理迁移方案.md) | — | 系统管理模块迁移方案 | — | ✅ PRD 已完成 |

### Core 核心层 — V2.0

|| 文档 | Jira 映射 | 说明 | Jira 对齐率 | 小版本 | 状态 |
||------|---------|------|-----------|--------|------|
|| [任务管理](./core/V2.0/任务管理.md) | Jira Issue + Custom Field + Workflow | EAV 自定义字段（25 种类型） + Screen/Scheme + 工作流引擎（条件/校验/动作） + Issue Security + Issue Link + 批量操作 + Backlog 视图 | **100%** | v2.0~v2.3 | ✅ PRD 已完成 |
|| [项目管理](./core/V2.0/项目管理.md) | Jira Project Configuration | Project Scheme 体系（Issue Type / Workflow / Screen / Field Configuration / Permission / Notification / Priority / Board / 模板） + Issue Security Level | **100%** | v2.1~v2.4 | ✅ PRD 已完成 |
|| [插件架构与功能分层](./core/V2.0/插件架构与功能分层.md) | Atlassian Marketplace Apps | Core 核心层 vs 插件边界设计：官方插件矩阵（21 个插件） | — | — | ✅ PRD 已完成 |
|| [UI插件化架构设计](./core/V2.0/UI插件化架构设计.md) | Atlassian Forge Fragments | 前端插件运行时：Extension Point + 动态组件渲染 | — | v2.4 | ✅ PRD 已完成 |
|| [UI设计规范](./core/V2.0/UI设计规范.md) | Jira UI / Linear / Asana | 前端开发 UI 规范：页面原型 / Bug-Story-Task-Epic 表单字段 / 可定制工作流图 / Scheme 定制点 / 组件清单（50+ 组件） | — | — | ✅ PRD 已完成 |

### Core 核心层 — V3.0

|| 文档 | Jira 映射 | 说明 | Jira 对齐率 | 小版本 | 状态 |
||------|---------|------|-----------|--------|------|
|| [任务管理 V3.0](./core/V3.0/任务管理.md) | Jira Issue + Automation | Jira 100% 收口：Issue Link（8 种）、附件、时间追踪、子任务增强、自动化执行引擎、批量操作、投票/分享、快捷键、回收站、规则模板库 | **100%** | v3.0~v3.2 | ✅ PRD 已完成 |
|| [项目管理 V3.0](./core/V3.0/项目管理.md) | Jira Project Settings | Issue Security Level 完善、跨项目标签、项目克隆、分组管理 | **100%** | v3.0 | ✅ PRD 已完成 |
|| [敏捷看板增强](./core/V3.0/敏捷看板增强.md) | Jira Board + Reports | 高级看板：泳道、WIP 限制、燃尽图、CFD、Velocity、Control Chart、团队容量、史诗进度 | **100%** | v3.3 | ✅ PRD 已完成 |
|| [仪表盘与日历视图](./core/V3.0/仪表盘与日历视图.md) | Jira Dashboard + Calendar | 个人仪表盘 + Gadget 目录（16 种）、日历视图、过滤器订阅、i18n、暗黑模式 | **100%** | v3.3~v3.4 | ✅ PRD 已完成 |
|| [看板快捷筛选增强](./core/V3.0/看板快捷筛选增强.md) | Jira Quick Filters | 快捷筛选按钮、卡片颜色、子任务折叠、列折叠、WIP 可视化、泳道路由 | **100%** | v3.4 | ✅ PRD 已完成 |

### Plugin 插件层 -- V1.0

> Plugin 插件是 LEST Platform 的功能扩展模块。所有非 Core 核心的功能都作为插件提供。
>
> **状态说明**：`✅ PRD 已完成` = PRD 文档已写完 | `🔧 开发中` = 开发任务进行中 | `📋 规划中` = 尚未开始

**核心插件（7 个）**：

|| 插件 ID | 名称 | Jira 映射 | 说明 | 小版本 | 状态 |
||--------|------|---------|------|--------|------|
|| [lest-worklog](./plugins/V1.0/lest-worklog.md) | 工时管理 | Jira Worklog | 工时记录、统计、报表 | v1.3 | ✅ PRD 已完成 |
|| [lest-cicd](./plugins/V1.0/lest-cicd.md) | CI/CD 集成 | Jira CI/CD | Jenkins/GitLab CI/GitHub Actions | v1.4 | ✅ PRD 已完成 |
|| [lest-wakatime](./plugins/V1.0/lest-wakatime.md) | WakaTime 集成 | IDE Activity Tracker | 编码心跳、任务关联、热力图 | v1.3 | ✅ PRD 已完成 |
|| [lest-ai](./plugins/V1.0/lest-ai.md) | AI 服务 | Atlassian Intelligence | AI 网关、代码审查、任务助手，会议总结 | v1.5 | ✅ PRD 已完成 |
|| [lest-im-feishu](./plugins/V1.0/lest-im-feishu.md) | 飞书集成 | — | 消息通知、审批集成 | v1.5 | ✅ PRD 已完成 |
|| [lest-im-dingtalk](./plugins/V1.0/lest-im-dingtalk.md) | 钉钉集成 | — | 消息通知、审批集成 | v1.5 | ✅ PRD 已完成 |
|| [lest-im-wecom](./plugins/V1.0/lest-im-wecom.md) | 企业微信集成 | — | 消息通知、组织架构同步 | v1.5 | ✅ PRD 已完成 |

**附加插件**：

|| 插件 ID | 名称 | Jira 映射 | 说明 | 小版本 | 状态 |
||--------|------|---------|------|--------|------|
|| [lest-code](./plugins/V1.0/lest-code.md) | Jira Git Integration | Git 仓库、MR/PR、提交追踪 | v1.5 | ✅ PRD 已完成 |
|| [lest-meeting](./plugins/V1.0/lest-meeting.md) | Jira + Confluence | 站会、计划会、回顾会、评审会、纪要 | v1.2 | ✅ PRD 已完成 |
|| [lest-report](./plugins/V1.0/lest-report.md) | Jira Reports | KPI、工时、代码贡献、评审统计 | v1.4 | ✅ PRD 已完成 |
|| [lest-release](./plugins/V1.0/lest-release.md) | Jira Releases | 版本规划、审批、变更记录、回滚 | v1.2 | ✅ PRD 已完成 |
|| [lest-openapi](./plugins/V1.0/lest-openapi.md) | Jira REST API | REST API、Webhook、OAuth2、API Key | v1.0 | ✅ PRD 已完成 |
|| [lest-plugin-sdk](./plugins/V1.0/lest-plugin-sdk.md) | Atlassian Marketplace | 插件 SDK、前端插件运行时、扩展点、市场 | v1.0 | ✅ PRD 已完成 |

### Plugin 插件层 — V2.0

> V2.0 新增 8 个插件，均为 V2.0 milestone 期间开发。

|| 插件 ID | 名称 | Jira 映射 | 说明 | 小版本 | 状态 |
||--------|------|---------|------|--------|------|
|| [lest-ai](./plugins/V2.0/lest-ai.md) | AI 服务增强 | Atlassian Intelligence | AI 网关 V2、深度代码审查、智能任务拆解、会议 AI 总结 | v2.5 | ✅ PRD 已完成 |
|| [lest-report](./plugins/V2.0/lest-report.md) | 报表中心 | Jira Reports | Burndown、CFD、Velocity、Control Chart、报表订阅、导出 | v2.0~v2.5 | ✅ PRD 已完成 |
|| [lest-meeting](./plugins/V2.0/lest-meeting.md) | 敏捷会议 | Jira + Confluence | 会议计时器、模板库、视频会议集成 | v2.5 | ✅ PRD 已完成 |
|| [lest-release](./plugins/V2.0/lest-release.md) | 发布管理 | Jira Releases | 发布计划、制品管理、变更记录 | v2.5 | ✅ PRD 已完成 |
|| [lest-jira-sync](./plugins/V2.0/lest-jira-sync.md) | Jira 双向同步 | — | 与 Jira Data Center/Cloud 双向同步 | v2.5 | ✅ PRD 已完成 |
|| [lest-devops](./plugins/V2.0/lest-devops.md) | DevOps 集成 | — | SonarQube、Harbor、Prometheus | v2.5 | ✅ PRD 已完成 |
|| [lest-gitea](./plugins/V2.0/lest-gitea.md) | Gitea 集成 | — | Gitea 代码仓库集成 | v2.5 | ✅ PRD 已完成 |
|| [lest-gogs](./plugins/V2.0/lest-gogs.md) | Gogs 集成 | — | Gogs 代码仓库集成 | v2.5 | ✅ PRD 已完成 |
|| [lest-zentao-import](./plugins/V2.0/lest-zentao-import.md) | 禅道导入 | — | 禅道数据一键迁移到 LEST | v2.5 | ✅ PRD 已完成 |

> **说明**：`lest-jira-compat`（Jira 插件兼容架构设计）在 [插件架构与功能分层](../core/V2.0/插件架构与功能分层.md) 文档中统一设计，无独立文件。

### Plugin 插件层 -- V3.0

|| 插件 ID | 名称 | 说明 | 小版本 | 状态 |
||--------|------|------|--------|------|
|| [lest-okr](./plugins/V3.0/lest-okr.md) | OKR 目标管理 | OKR 目标制定、对齐、评分、AI 辅助 | v3.6 | ✅ PRD 已完成 |
|| [lest-portfolio](./plugins/V3.0/lest-portfolio.md) | Portfolio 多项目治理 | 跨项目视图、资源调度、甘特图 | v3.6 | ✅ PRD 已完成 |
|| [lest-approval](./plugins/V3.0/lest-approval.md) | 审批流引擎 | 自定义审批流程、多级审批、条件分支 | v3.6 | ✅ PRD 已完成 |
|| [lest-ldap](./plugins/V3.0/lest-ldap.md) | LDAP 集成 | Active Directory 同步、企业目录集成 | v3.6 | ✅ PRD 已完成 |
|| [lest-sso](./plugins/V3.0/lest-sso.md) | SSO 单点登录 | SAML/OIDC/OAuth2 支持 | v3.6 | ✅ PRD 已完成 |
|| [lest-multi-tenant](./plugins/V3.0/lest-multi-tenant.md) | 多租户隔离 | 租户数据隔离、租户管理，配额控制 | v3.6 | ✅ PRD 已完成 |

---

## 官方插件矩阵

|| 插件 ID | V1.0 | V2.0 | V3.0 | 小版本归属 | 说明 |
||--------|------|------|------|-----------|------|
|| lest-worklog | ✅ | — | — | v1.3 | 工时管理 |
|| lest-cicd | ✅ | — | — | v1.4 | CI/CD 集成 |
|| lest-wakatime | ✅ | — | — | v1.3 | WakaTime 集成 |
|| lest-ai | ✅ | ✅ | — | v1.5 / v2.5 | AI 服务（V1.0 基础 + V2.0 增强） |
|| lest-im-feishu | ✅ | — | — | v1.5 | 飞书集成 |
|| lest-im-dingtalk | ✅ | — | — | v1.5 | 钉钉集成 |
|| lest-im-wecom | ✅ | — | — | v1.5 | 企业微信集成 |
|| lest-code | ✅ | — | — | v1.5 | 代码管理 |
|| lest-meeting | ✅ | ✅ | — | v1.2 / v2.5 | 敏捷会议（V1.0 基础 + V2.0 增强） |
|| lest-report | ✅ | ✅ | — | v1.4 / v2.5 | 报表中心（V1.0 基础 + V2.0 增强） |
|| lest-release | ✅ | ✅ | — | v1.2 / v2.5 | 发布管理（V1.0 基础 + V2.0 增强） |
|| lest-openapi | ✅ | — | — | v1.0 | 开放平台 |
|| lest-plugin-sdk | ✅ | — | — | v1.0 | 插件系统 |
|| lest-jira-sync | — | ✅ | — | v2.5 | Jira 双向同步 |
|| lest-devops | — | ✅ | — | v2.5 | DevOps 集成 |
|| lest-gitea | — | ✅ | — | v2.5 | Gitea 集成 |
|| lest-gogs | — | ✅ | — | v2.5 | Gogs 集成 |
|| lest-zentao-import | — | ✅ | — | v2.5 | 禅道导入 |
|| lest-okr | — | — | ✅ | v3.6 | OKR 目标管理 |
|| lest-portfolio | — | — | ✅ | v3.6 | Portfolio 管理 |
|| lest-approval | — | — | ✅ | v3.6 | 审批流引擎 |
|| lest-ldap | — | — | ✅ | v3.6 | LDAP 集成 |
|| lest-sso | — | — | ✅ | v3.6 | SSO 单点登录 |
|| lest-multi-tenant | — | — | ✅ | v3.6 | 多租户隔离 |

> **小版本归属说明**：每个插件按其首次实现的小版本号标记。部分插件（lest-ai/meeting/report/release）在多个版本中持续迭代，增强版本标注在 `/` 之后。
>
> **矩阵状态说明**：`✅` = PRD 文档已完成，不等于开发已完成。开发状态见 [2-tasks](../2-tasks/) 目录和 [MILESTONES](../MILESTONES/) 小版本规划。

**总计：21 个功能插件，21 个 PRD 全部完成 ✅**

---

## 小版本发布计划

详细的小版本迭代规划（每个版本拆分为 1.5 周一个小版本）：

- [V1.0 小版本规划](../MILESTONES/V1.0-小版本规划.md)
- [V2.0 小版本规划](../MILESTONES/V2.0-小版本规划.md)
- [V3.0 小版本规划](../MILESTONES/V3.0-小版本规划.md)
- [V4.0 小版本规划](../MILESTONES/V4.0-小版本规划.md)
- [V5.0 小版本规划](../MILESTONES/V5.0-小版本规划.md)

## Scheme 参考手册

完整的 Scheme 设计文档（V2.0 核心）见：

- [SCHEME-REFERENCE.md](./SCHEME-REFERENCE.md) — 涵盖 Issue Type / Workflow / Screen / Field Configuration / Board / 模板的完整设计
