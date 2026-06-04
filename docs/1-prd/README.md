# PRD 索引

> **开发口径**：V1.0/V2.0/V3.0 只做 Core；插件从 V4.0 开始；企业商业能力进入 V5.0。
>
> **开发执行入口**：[DEVELOPMENT-EXECUTION-PLAN.zh-CN.md](../DEVELOPMENT-EXECUTION-PLAN.zh-CN.md)

## Core PRD

| 版本 | 定位 | PRD 入口 | 小版本 |
|------|------|----------|--------|
| V1.0 | Jira Core 基础版 | [core/V1.0](./core/V1.0/README.md) | [V1.0 小版本](../MILESTONES/V1.0-小版本规划.md) |
| V2.0 | Scheme 可配置版 | [core/V2.0](./core/V2.0/README.md) | [V2.0 小版本](../MILESTONES/V2.0-小版本规划.md) |
| V3.0 | Jira Core 完整版 | [core/V3.0](./core/V3.0/README.md) | [V3.0 小版本](../MILESTONES/V3.0-小版本规划.md) |
| V4.0 | 无新增 Core，转入插件生态 | [core/V4.0](./core/V4.0/README.md) | [V4.0 小版本](../MILESTONES/V4.0-小版本规划.md) |
| V5.0 | 企业商业版 | [core/V5.0](./core/V5.0/README.md) | [V5.0 小版本](../MILESTONES/V5.0-小版本规划.md) |

## V1.0 Core PRD

| 模块 | PRD |
|------|-----|
| 认证与平台管理 | [认证系统与系统管理](./core/V1.0/认证系统与系统管理.md) |
| 项目管理 | [项目管理](./core/V1.0/项目管理.md) |
| 任务管理 | [任务管理](./core/V1.0/任务管理.md) |
| 通知与消息 | [通知与消息](./core/V1.0/通知与消息.md) |
| 系统设置 | [系统设置](./core/V1.0/系统设置.md) |
| 安全增强 | [安全增强](./core/V1.0/安全增强.md) |
| 扩展矩阵契约 | [EXTENSION-POINTS](./core/EXTENSION-POINTS.md) |

## Plugin PRD

| 版本 | 定位 | PRD |
|------|------|-----|
| V4.0 | 插件平台、官方插件池、AI 插件 | [plugins/V4.0](./plugins/V4.0/README.md) |
| V5.0 | 企业商业能力，按 Core 维护 | [core/V5.0](./core/V5.0/README.md) |
| 错误码 | 插件错误码 | [plugins/ERROR-CODES.md](./plugins/ERROR-CODES.md) |

## 规则

1. V1.0/V2.0/V3.0 目录下不放插件 PRD。
2. 会议、飞书、钉钉、企业微信、WakaTime、CI/CD、代码仓库、AI 归 V4.0 插件。
3. SSO、LDAP、SCIM、多租户、Portfolio、OKR、SLA 归 V5.0 企业商业 Core，不在插件目录重复维护。
