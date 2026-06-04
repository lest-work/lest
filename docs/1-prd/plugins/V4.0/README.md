# 官方插件 PRD — V4.0

> **定位**：V4.0 是插件生态启动版本。先实现插件运行时和 SDK，再交付官方插件池和 AI 插件。
>
> **关联小版本**：[V4.0 小版本规划](../../../MILESTONES/V4.0-小版本规划.md)

## V4.0 PRD 范围

| 小版本 | 能力 | 说明 |
|--------|------|------|
| 4.0.0-alpha | 插件运行时与 SDK | 生命周期、Manifest、权限、扩展点注册 |
| 4.0.1 | 插件管理 UI 与事件订阅 | 插件配置、启停、事件订阅、审计 |
| 4.0.2 | IM 通知插件 | 飞书、钉钉、企业微信通知插件 MVP |
| 4.0.3 | DevOps 插件 | 代码仓库、CI/CD 插件 MVP |
| 4.0.4 | 协作插件 | WakaTime、会议、报表、Jira Sync MVP |
| 4.0.5 | AI 插件 | AI Provider、AI 助手、AI 任务拆解、自然语言查询、风险识别、AI 报表 |
| 4.0.6 | 生态回归 | 安全扫描、兼容矩阵、开发文档、正式发布 |

## 已有专项 PRD

| 插件/平台 | 文档 | 说明 |
|-----------|------|------|
| 插件平台 | [plugin-platform.md](./plugin-platform.md) | 运行时、SDK、管理 UI、生态回归 |
| IM 通知插件 | [lest-im-notification.md](./lest-im-notification.md) | 飞书、钉钉、企业微信通知 MVP |
| DevOps 插件 | [lest-devops.md](./lest-devops.md) | 代码仓库、CI/CD 最小集成 |
| 协作插件 | [lest-collaboration.md](./lest-collaboration.md) | WakaTime、会议、报表、Jira Sync |
| AI Provider | [lest-ai-provider.md](./lest-ai-provider.md) | Provider、任务拆解、自然语言查询、风险识别 |
| AI 助手 | [lest-ai-assistant.md](./lest-ai-assistant.md) | 全局助手、上下文建议、操作确认 |
| AI 报表 | [lest-ai-report.md](./lest-ai-report.md) | AI 报表和效能分析 |

## V4.0 不做

| 功能 | 归属 |
|------|------|
| SSO、LDAP、SCIM、企业组织同步 | V5.0 商业 Core |
| 多租户、商业授权、租户控制台 | V5.0 商业 Core |
| Portfolio、OKR、资源容量、SLA | V5.0 商业 Core |
