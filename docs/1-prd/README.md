# PRD 产品需求文档

> LEST Platform 的产品需求文档全面对标 **Jira** 的产品结构，每个功能模块视为一个 Jira Project。

## 开源 + 商业双版本说明

| 版本 | 发布时间 | 许可 | 开源内容 | 商业内容 |
|------|---------|------|---------|---------|
| **V1.0** | 2026-08 | MIT | 所有功能 | — |
| **V2.0** | 2026年底 | MIT | 所有功能 | — |
| **V3.0** | 2027年中 | MIT + 商业 | 基础 IM 集成、OKR 基础 | 审批流高级版、Portfolio、国产数据库 |
| **V4.0** | 2027-2028 | MIT + 商业 | AI 基础功能 | AI 智能排期、AI 代码生成 |
| **V5.0** | 2028年 | MIT + 商业 | 基础多租户 | 多租户完整隔离、LDAP/SSO、审计合规 |

## 文档索引

### Core 核心层 — V1.0

> **Core 核心**是平台必须的基础层，模块之间存在强依赖关系。Core 中的模块是不可或缺的，其他所有功能（插件）都依赖 Core 运行。

| 文档 | Jira 映射 | 说明 | 状态 |
|------|---------|------|------|
| [认证系统与系统管理](./core/V1.0/认证系统与系统管理.md) | Atlassian Access | 身份认证、用户、角色、菜单、部门、字典 | 已完成 |
| [项目管理](./core/V1.0/项目管理.md) | Jira Project | 项目、成员、迭代、里程碑（V2.0 将支持 Scheme 配置） | 已完成 |
| [任务管理](./core/V1.0/任务管理.md) | Jira Issue + Workflow | 任务、看板、甘特、工时、Webhook（V2.0 将支持完整定制） | 已完成 |
| [通知与消息](./core/V1.0/通知与消息.md) | Jira Notifications | 站内信、邮件、IM 通知渠道（钉钉/企微/飞书放插件） | 已完成 |
| [系统设置](./core/V1.0/系统设置.md) | Jira Administration | 参数配置、日志审计、备份、监控 | 已完成 |

### Core 核心层 — V2.0

| 文档 | Jira 映射 | 说明 | Jira 对齐率 | 状态 |
|------|---------|------|-----------|------|
| [任务管理](./core/V2.0/任务管理.md) | Jira Issue + Custom Field + Workflow | **EAV 自定义字段**（25 种类型） + **Screen/Scheme** + **工作流引擎**（条件/校验/动作） + **Issue Security** + **Issue Link** + **批量操作** + **Backlog 视图** + **工作流增强**（全局转换/循环检测/草稿发布） + **Resolution** + **投票/分享** + **JQL** | **82%** | 规划中 |
| [项目管理](./core/V2.0/项目管理.md) | Jira Project Configuration | **Project Scheme 体系**（Issue Type / Workflow / Screen / Field Configuration / **Permission** / **Notification** / **Priority** / Board / 模板） | **97%** | 规划中 |
| [插件架构与功能分层](./core/V2.0/插件架构与功能分层.md) | Atlassian Marketplace Apps | **Core 核心层 vs 插件边界设计**：官方插件矩阵（21 个插件），工时/CI-CD/WakaTime/IM 集成/会议/发布/绩效等全部插件化 | — | 规划中 |

### Core 核心层 -- V3.0

| 文档 | Jira 映射 | 说明 | Jira 对齐率 | 状态 |
|------|---------|------|-----------|------|
| [任务管理 V3.0](./core/V3.0/任务管理.md) | Jira Issue + Automation | **补全 V2.0 缺失功能**：Issue Link、附件、时间追踪、子任务增强、自动化执行引擎、回收站 | **95%** | 规划中 |
| [敏捷看板增强](./core/V3.0/敏捷看板增强.md) | Jira Board + Reports | **高级看板**：泳道、WIP 限制、燃尽图、CFD、Velocity、Control Chart、团队容量、史诗进度 | **100%** | 规划中 |

### Plugin 插件层 -- V3.0

| 文档 | 说明 | 状态 |
|------|------|------|
| [V3.0 插件索引](./plugins/V3.0/README.md) | OKR、Portfolio、审批流、LDAP、SSO、多租户（6 个新插件） | 规划中 |

### Plugin 插件层 — V1.0

> **Plugin 插件**是 LEST Platform 的功能扩展模块。所有非 Core 核心的功能都作为插件提供，可按需安装、启用和卸载。

#### 核心插件（7 个）

| 插件 ID | 名称 | Jira 映射 | 说明 | 状态 |
|--------|------|---------|------|------|
| [lest-worklog](./plugins/V1.0/lest-worklog.md) | 工时管理 | Jira Worklog | 工时记录、统计、报表 | 规划中 |
| [lest-cicd](./plugins/V1.0/lest-cicd.md) | CI/CD 集成 | Jira CI/CD | Jenkins/GitLab CI/GitHub Actions | 已完成 |
| [lest-wakatime](./plugins/V1.0/lest-wakatime.md) | WakaTime 集成 | IDE Activity Tracker | 编码心跳、任务关联、热力图 | 已完成 |
| [lest-ai](./plugins/V1.0/lest-ai.md) | AI 服务 | Atlassian Intelligence | AI 网关、代码审查、任务助手、会议总结 | 已完成 |
| [lest-im-feishu](./plugins/V1.0/lest-im-feishu.md) | 飞书集成 | — | 消息通知、审批集成 | 规划中 |
| [lest-im-dingtalk](./plugins/V1.0/lest-im-dingtalk.md) | 钉钉集成 | — | 消息通知、审批集成 | 规划中 |
| [lest-im-wecom](./plugins/V1.0/lest-im-wecom.md) | 企业微信集成 | — | 消息通知、组织架构同步 | 规划中 |

#### 附加插件

| 文档 | Jira 映射 | 说明 | 状态 |
|------|---------|------|------|
| [lest-code](./plugins/V1.0/lest-code.md) | 代码管理 | Jira Git Integration | Git 仓库、MR/PR、提交追踪 | 已完成 |
| [lest-meeting](./plugins/V1.0/lest-meeting.md) | 敏捷会议 | Jira + Confluence | 站会、计划会、回顾会、评审会、纪要 | 已完成 |
| [lest-report](./plugins/V1.0/lest-report.md) | 团队绩效 | Jira Reports | KPI、工时、代码贡献、评审统计 | 已完成 |
| [lest-release](./plugins/V1.0/lest-release.md) | 发布管理 | Jira Releases | 版本规划、审批、变更记录、回滚 | 已完成 |
| [lest-openapi](./plugins/V1.0/lest-openapi.md) | 开放平台 | Jira REST API | REST API、Webhook、OAuth2、API Key | 已完成 |
| [lest-plugin-sdk](./plugins/V1.0/lest-plugin-sdk.md) | 插件系统 | Atlassian Marketplace | 插件 SDK、扩展点、市场 | 已完成 |

### Plugin 插件层 — V2.0

| 插件 ID | 名称 | Jira 映射 | 说明 | 状态 |
|--------|------|---------|------|------|
| [lest-report](./plugins/V2.0/lest-report.md) | 报表中心 | Jira Reports | Burndown、CFD、Velocity、Control Chart | 规划中 |
| [lest-meeting](./plugins/V2.0/lest-meeting.md) | 敏捷会议 | Jira + Confluence | 会议计时器、模板库、视频会议集成 | 规划中 |
| [lest-release](./plugins/V2.0/lest-release.md) | 发布管理 | Jira Releases | 发布计划、制品管理、变更记录 | 规划中 |
| [lest-jira-sync](./plugins/V2.0/lest-jira-sync.md) | Jira 双向同步 | — | 与 Jira Data Center/Cloud 双向同步 | 规划中 |
| [lest-devops](./plugins/V2.0/lest-devops.md) | DevOps 集成 | — | SonarQube、Harbor、Prometheus | 规划中 |
| [lest-gitea](./plugins/V2.0/lest-gitea.md) | Gitea 集成 | — | Gitea 代码仓库集成 | 规划中 |
| [lest-gogs](./plugins/V2.0/lest-gogs.md) | Gogs 集成 | — | Gogs 代码仓库集成 | 规划中 |
| [lest-zentao-import](./plugins/V2.0/lest-zentao-import.md) | 禅道导入 | — | 禅道数据一键迁移到 LEST | 规划中 |

## 官方插件矩阵

| 插件 ID | V1.0 | V2.0 | V3.0 | 说明 |
|--------|-------|-------|-------|------|
| lest-worklog | OK | OK | OK | 工时管理 |
| lest-cicd | OK | OK | OK | CI/CD 集成 |
| lest-wakatime | OK | OK | OK | WakaTime 集成 |
| lest-ai | OK | OK | OK | AI 服务 |
| lest-im-feishu | OK | OK | OK | 飞书集成 |
| lest-im-dingtalk | OK | OK | OK | 钉钉集成 |
| lest-im-wecom | OK | OK | OK | 企业微信集成 |
| lest-meeting | 内置 | OK | OK | 敏捷会议增强 |
| lest-report | 内置 | OK | OK | 报表中心 |
| lest-release | 内置 | OK | OK | 发布管理增强 |
| lest-code | OK | OK | OK | 代码管理 |
| lest-openapi | OK | OK | OK | 开放平台 |
| lest-plugin-sdk | OK | OK | OK | 插件系统 |
| lest-jira-sync | — | OK | OK | Jira 双向同步 |
| lest-devops | — | OK | OK | DevOps 集成 |
| lest-gitea | — | OK | OK | Gitea 集成 |
| lest-gogs | — | OK | OK | Gogs 集成 |
| lest-zentao-import | — | OK | OK | 禅道导入 |
| lest-okr | — | — | OK | OKR 目标管理 |
| lest-portfolio | — | — | OK | Portfolio 管理 |
| lest-approval | — | — | OK | 审批流引擎 |
| lest-ldap | — | — | OK | LDAP 集成 |
| lest-sso | — | — | OK | SSO 单点登录 |

OK = 独立插件 | 内置 = 随 Core 内置 | -- = 暂无

## V1.0 vs V2.0 vs Jira 功能对比

| 功能维度 | V1.0（硬编码） | V2.0（Jira 方案） | Jira 对齐率 |
|---------|--------------|-----------------|-----------|
| 任务类型 | 全局固定 4 种 | 项目可自定义任意数量 | **90%** |
| 状态 | 全局固定 3 种 | 项目可自定义任意数量 | **90%** |
| 工作流 | 无（仅状态变更） | V2.0 完整工作流引擎 | **100%** |
| 自定义字段 | 无 | V2.0 支持 25 种字段类型 | **100%** |
| 表单配置 | 固定表单 | V2.0 支持 Screen/Scheme | **100%** |
| 字段行为 | 固定 | V2.0 支持隐藏/必填/只读 | **100%** |
| 看板列 | 固定 3 列 | V2.0 可自定义 + WIP 限制 | **100%** |
| 项目模板 | 无 | V2.0 完整复制 Scheme 配置 | **100%** |
| Issue Security | 无 | V2.0 安全级别控制 | **100%** |
| Issue Resolution | 无 | V2.0 解决方案字段 | **100%** |
| Vote / Share | 无 | V2.0 投票/分享 | **100%** |
| 快捷键 | 无 | V2.0 快捷键支持 | **100%** |
| JQL 查询 | 基础筛选 | V2.0 JQL 高级查询 | **100%** |
| Automation Rules | 无 | V2.0 数据结构，V3.0 执行引擎 | **100%** |
| Permission Scheme | 无 | V2.0 完整权限方案 | **100%** |
| Notification Scheme | 有基础 | V2.0 增强为条件通知 | **100%** |
| Priority Scheme | 无 | V2.0 优先级方案 | **100%** |
| Burndown/CFD 等报表 | 无 | V3.0 实现 | **0%** |

> **V2.0 整体 Jira 功能对齐率**：任务管理 90%，项目管理 97%，综合约 **90%**。
> V3.0 将补齐 Reports 相关功能（Burndown Chart、Control Chart、Cumulative Flow Diagram 等），目标达到 **95%+**。

## Scheme 参考手册

完整的 Scheme 设计文档（V2.0 核心）见：

- [SCHEME-REFERENCE.md](./SCHEME-REFERENCE.md) — 涵盖 Issue Type / Workflow / Screen / Field Configuration / Board / 模板的完整设计
