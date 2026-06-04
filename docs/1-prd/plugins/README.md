# 插件 PRD

> **规划口径**：插件从 V4.0 开始正式开发。V1.0/V2.0/V3.0 不保留插件 PRD 目录，避免把会议、飞书、钉钉、WakaTime、CI/CD、AI 等误读为 Core 功能。

## 插件版本归属

| 版本 | 定位 | PRD |
|------|------|-----|
| V4.0 | 插件平台、官方插件池、AI 插件 | [V4.0](./V4.0/README.md) |
| V5.0 | 企业商业能力，不在插件目录重复维护 | [core/V5.0](../core/V5.0/README.md) |
| 错误码 | 插件错误码约定 | [ERROR-CODES.md](./ERROR-CODES.md) |

## Core vs Plugin 划分原则

| 放 Core | 放 Plugin |
|---------|------------|
| 没有它系统无法完成项目、Issue、Sprint、Board 基础闭环 | 没有它 Core 仍可完整运行 |
| 其他能力依赖的数据基础 | 第三方系统、外部 SDK、可替换实现 |
| V1~V3 的 Jira Core 主链路 | 会议、IM、WakaTime、CI/CD、代码仓库、AI |
| V1~V3 的扩展契约 | V4+ 的插件运行时、SDK、市场、官方插件 |

扩展点契约见：[core/EXTENSION-POINTS.md](../core/EXTENSION-POINTS.md)。

> **V5.0 说明**：SSO、LDAP、SCIM、多租户、审计合规、SLA 等能力影响认证链路、租户路由和数据隔离，统一归入 V5.0 商业 Core，不再维护独立的 V5 插件目录。
