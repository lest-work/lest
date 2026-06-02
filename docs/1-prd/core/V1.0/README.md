# Core 核心 -- V1.0

> **Core 核心**是 LEST Platform 的核心层，包含平台必须的基础功能。Core 中的模块是不可或缺的，其他所有功能（插件）都依赖 Core 运行。

## Core V1.0 模块

| 文档 | Jira 映射 | 说明 | 状态 |
|------|---------|------|------|
| [认证系统与系统管理](./认证系统与系统管理.md) | Atlassian Access | 身份认证、用户、角色、菜单、部门、字典 | 已完成 |
| [项目管理](./项目管理.md) | Jira Project | 项目、成员、迭代、里程碑 | 已完成 |
| [任务管理](./任务管理.md) | Jira Issue + Workflow | 任务、看板、甘特、工时、Webhook | 已完成 |
| [通知与消息](./通知与消息.md) | Jira Notifications | 通知框架、事件总线（IM 渠道放插件） | 已完成 |
| [系统设置](./系统设置.md) | Jira Administration | 参数配置、日志审计、备份、监控 | 已完成 |

## Core 定位说明

Core 是 LEST Platform 的基础层，设计原则：

- **最小化**：只包含平台运行所必需的功能
- **稳定性**：Core 模块变更频率低，API 稳定
- **扩展性**：所有可插拔功能都通过插件系统扩展
- **开源性**：Core 全部开源（MIT License）

## V2.0 升级路径

Core V2.0 将对项目管理、任务管理进行 Scheme 化改造，详见 [core/V2.0/](../core/V2.0/)
