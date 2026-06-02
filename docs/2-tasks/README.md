# 开发任务单

> **目录结构调整**：任务单已按 **Core 核心** vs **Plugin 插件** 分离。

## 快速导航

| 目录 | 说明 |
|------|------|
| [core/V1.0/](./core/V1.0/) | Core V1.0 开发任务单 |
| [core/V2.0/](./core/V2.0/) | Core V2.0 开发任务单 |
| [core/V3.0/](./core/V3.0/) | Core V3.0 开发任务单（Jira 100% 收口：Issue Link/附件/Time Tracking/Automation/看板增强等） |
| [plugins/V1.0/](./plugins/V1.0/) | 插件 V1.0 开发任务单 |
| [plugins/V2.0/](./plugins/V2.0/) | 插件 V2.0 开发任务单 |
| [plugins/V3.0/](./plugins/V3.0/) | 插件 V3.0 开发任务单（OKR/Portfolio/审批流/LDAP/SSO/多租户） |

## 设计原则

- **Core 优先**：优先完成 Core V1.0 → V2.0 → V3.0，插件开发并行推进
- **可插拔验证**：插件开发完成后再独立发布，不影响 Core
- **里程碑驱动**：每个 Milestone 对应一个或多个功能模块的完整交付
- **SDK 先行**：每个版本先完善插件 SDK，再开发官方插件

---

## 小版本发布计划

详细的小版本迭代规划（含每周开发任务）请查看 [MILESTONES 目录](../MILESTONES/)：

| 大版本 | 小版本文件 |
|--------|-----------|
| V1.0 | [MILESTONES/V1.0-小版本规划.md](../MILESTONES/V1.0-小版本规划.md) |
| V2.0 | [MILESTONES/V2.0-小版本规划.md](../MILESTONES/V2.0-小版本规划.md) |
| V3.0 | [MILESTONES/V3.0-小版本规划.md](../MILESTONES/V3.0-小版本规划.md) |
| V4.0 | [MILESTONES/V4.0-小版本规划.md](../MILESTONES/V4.0-小版本规划.md) |
| V5.0 | [MILESTONES/V5.0-小版本规划.md](../MILESTONES/V5.0-小版本规划.md) |

> **说明**：每个小版本采用 1 周开发 + 0.5 周测试 = 1.5 周发布的节奏，方便每周独立开发交付。详细的任务 ID、工作量估算和交付物在上述小版本规划文档中。
