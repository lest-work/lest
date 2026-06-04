# Core V1.0 PRD

> **定位**：V1.0 是 Jira-like Core 基础版，只交付项目协作、Issue、敏捷看板、通知、附件、权限和扩展矩阵契约。
>
> **开发入口**：[V1.0 Core 六个小版本任务总表](../../../2-tasks/core/V1.0/V1.0-Core-6-minor-tasks.md)
>
> **总路线**：[DEVELOPMENT-EXECUTION-PLAN.zh-CN.md](../../../DEVELOPMENT-EXECUTION-PLAN.zh-CN.md)

## V1.0 PRD / TASK / 小版本矩阵

| 小版本 | 主题 | PRD | TASK |
|--------|------|-----|------|
| 1.1 | 稳定性收口与 Core E2E | [认证系统与平台管理](./认证系统与系统管理.md) | [总任务单](../../../2-tasks/core/V1.0/V1.0-Core-6-minor-tasks.md) |
| 1.2 | 项目与权限核心 | [项目管理](./项目管理.md) / [系统设置](./系统设置.md) / [安全增强](./安全增强.md) | [总任务单](../../../2-tasks/core/V1.0/V1.0-Core-6-minor-tasks.md) |
| 1.3 | Issue 详情与协作闭环 | [任务管理](./任务管理.md) | [任务管理_tasks.md](../../../2-tasks/core/V1.0/任务管理_tasks.md) |
| 1.4 | Backlog / Sprint / Board | [任务管理](./任务管理.md) / [项目管理](./项目管理.md) | [任务管理_tasks.md](../../../2-tasks/core/V1.0/任务管理_tasks.md) |
| 1.5 | 通知、附件、开放 API 基础 | [通知与消息](./通知与消息.md) / [任务管理](./任务管理.md) | [总任务单](../../../2-tasks/core/V1.0/V1.0-Core-6-minor-tasks.md) |
| 1.6 | 扩展矩阵契约与发布 | [EXTENSION-POINTS](../EXTENSION-POINTS.md) | [总任务单](../../../2-tasks/core/V1.0/V1.0-Core-6-minor-tasks.md) |

## V1.0 Core 模块

| 文档 | Jira 映射 | 说明 |
|------|-----------|------|
| [认证系统与平台管理](./认证系统与系统管理.md) | Atlassian Account + Jira Admin | 登录、Token、个人资料、平台管理员、项目邀请 |
| [项目管理](./项目管理.md) | Jira Project | 项目、成员、角色、迭代、里程碑、项目权限 |
| [任务管理](./任务管理.md) | Jira Issue + Board | Issue、评论、关注、标签、子任务、依赖、Backlog、Sprint、Board |
| [通知与消息](./通知与消息.md) | Jira Notifications | 站内信、WebSocket、SMTP 邮件、通知偏好 |
| [系统设置](./系统设置.md) | Jira System Administration | 邮件、安全、审计、附件存储基础 |
| [安全增强](./安全增强.md) | Security Settings | 密码策略、登录失败锁定、API Key、Webhook 签名 |
| [系统管理迁移方案](./系统管理迁移方案.md) | 历史迁移参考 | 从 OA 后台迁移到 Jira-like 项目协作模型 |

## V1.0 明确不做

| 不做项 | 归属 |
|--------|------|
| 自定义字段、工作流、屏幕、类型方案 | V2.0 Scheme 可配置版 |
| Worklog、报表、自动化、批量、回收站、导入导出 | V3.0 Jira Core 完整版 |
| 会议、飞书、钉钉、企业微信、WakaTime、CI/CD、代码仓库、AI | V4.0 插件生态 |
| SSO、LDAP、SCIM、多租户、Portfolio、OKR、SLA | V5.0 企业商业版 |
