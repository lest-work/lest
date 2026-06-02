# 开发任务单

> **目录结构调整**：任务单已按 **Core 核心** vs **Plugin 插件** 分离。

## 快速导航

| 目录 | 说明 |
|------|------|
| [core/V1.0/](./core/V1.0/) | Core V1.0 开发任务单 |
| [core/V2.0/](./core/V2.0/) | Core V2.0 开发任务单 |
| [core/V3.0/](./core/V3.0/) | Core V3.0 开发任务单（Issue Link/附件/Time Tracking/Automation/看板增强） |
| [plugins/V1.0/](./plugins/V1.0/) | 插件 V1.0 开发任务单 |
| [plugins/V2.0/](./plugins/V2.0/) | 插件 V2.0 开发任务单 |
| [plugins/V3.0/](./plugins/V3.0/) | 插件 V3.0 开发任务单（OKR/Portfolio/审批流/LDAP/SSO/多租户） |

## 设计原则

- **Core 优先**：优先完成 Core V1.0 → V2.0 → V3.0，插件开发并行推进
- **可插拔验证**：插件开发完成后再独立发布，不影响 Core
- **里程碑驱动**：每个 Milestone 对应一个或多个功能模块的完整交付
- **SDK 先行**：每个版本先完善插件 SDK，再开发官方插件
