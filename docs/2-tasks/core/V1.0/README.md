# Core V1.0 开发任务单

> **开发口径**：V1.0 只交付 Jira-like Core 基础闭环，不开发会议、飞书、钉钉、企业微信、WakaTime、CI/CD、代码仓库、AI、绩效、SSO/LDAP、多租户、插件运行时、插件 SDK 或插件市场。
>
> **开发入口**：先看 [V1.0 Core 六个小版本任务总表](./V1.0-Core-6-minor-tasks.md)，再按需查看对应 PRD。小版本、PRD、TASK 必须以该总表为准。

## PRD / TASK / 小版本一致性矩阵

| Core 模块 | PRD | TASK | 小版本 |
|-----------|-----|------|--------|
| 稳定性与 E2E | [V1.0 小版本规划](../../../MILESTONES/V1.0-小版本规划.md) | [V1.0-Core-6-minor-tasks.md](./V1.0-Core-6-minor-tasks.md) | 1.1 |
| 认证与平台管理 | [认证系统与系统管理](../../../1-prd/core/V1.0/认证系统与系统管理.md) | [V1.0-Core-6-minor-tasks.md](./V1.0-Core-6-minor-tasks.md) | 1.1 / 1.2 |
| 项目与权限 | [项目管理](../../../1-prd/core/V1.0/项目管理.md) | [V1.0-Core-6-minor-tasks.md](./V1.0-Core-6-minor-tasks.md) | 1.2 |
| Issue 与协作 | [任务管理](../../../1-prd/core/V1.0/任务管理.md) | [任务管理_tasks.md](./任务管理_tasks.md) | 1.3 |
| Backlog / Sprint / Board | [任务管理](../../../1-prd/core/V1.0/任务管理.md) | [任务管理_tasks.md](./任务管理_tasks.md) | 1.4 |
| 通知、附件、开放 API | [通知与消息](../../../1-prd/core/V1.0/通知与消息.md) / [任务管理](../../../1-prd/core/V1.0/任务管理.md) | [V1.0-Core-6-minor-tasks.md](./V1.0-Core-6-minor-tasks.md) / [任务管理_tasks.md](./任务管理_tasks.md) | 1.5 |
| 扩展矩阵契约与发布 | [EXTENSION-POINTS](../../../1-prd/core/EXTENSION-POINTS.md) | [V1.0-Core-6-minor-tasks.md](./V1.0-Core-6-minor-tasks.md) | 1.6 |

## V1.0 后置能力归属

| 后置能力 | 不进入 V1.0 的原因 | 归属 |
|----------|--------------------|------|
| Worklog / Time Tracking | 需要工时报表与剩余工时模型 | V3.0 Core |
| 批量操作、回收站、导入导出 | 非最小 Core 闭环 | V3.0 Core |
| 代码仓库、CI/CD、会议、WakaTime、IM | 外部系统集成，必须插件化 | V4.0 插件 |
| AI 助手、AI 报表 | Provider 与推理能力可插拔 | V4.0 插件 |
| SSO、LDAP、SCIM、多租户 | 企业商业能力 | V5.0 商业版 |
