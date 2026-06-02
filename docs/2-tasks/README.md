# 开发任务单

> **目录结构调整**：任务单已按 **Core 核心** vs **Plugin 插件** 分离。

## 快速导航

|| 目录 | 说明 | 状态 |
||------|------|------|
|| [core/V1.0/](./core/V1.0/) | Core V1.0 开发任务单 | ✅ 已关联 Milestone |
|| [core/V2.0/](./core/V2.0/) | Core V2.0 开发任务单 | ✅ 已展开 |
|| [core/V3.0/](./core/V3.0/) | Core V3.0 开发任务单（Jira 100% 收口：Issue Link/附件/Time Tracking/Automation/看板增强等） | ✅ 已展开 |
|| [plugins/V1.0/](./plugins/V1.0/) | 插件 V1.0 开发任务单 | ✅ 已展开 |
|| [plugins/V2.0/](./plugins/V2.0/) | 插件 V2.0 开发任务单 | ✅ 已展开 |
|| [plugins/V3.0/](./plugins/V3.0/) | 插件 V3.0 开发任务单（OKR/Portfolio/审批流/LDAP/SSO/多租户） | ✅ 骨架完成 |

## 设计原则

- **Core 优先**：优先完成 Core V1.0 → V2.0 → V3.0，插件开发并行推进
- **可插拔验证**：插件开发完成后再独立发布，不影响 Core
- **里程碑驱动**：每个 Milestone 对应一个或多个功能模块的完整交付
- **SDK 先行**：每个版本先完善插件 SDK，再开发官方插件

---

## 小版本发布计划

详细的小版本迭代规划（含每周开发任务）请查看 [MILESTONES 目录](../MILESTONES/)：

|| 大版本 | 小版本文件 | 时间线 |
|--------|-----------|--------|
|| V1.0 | [MILESTONES/V1.0-小版本规划.md](../MILESTONES/V1.0-小版本规划.md) | 2026-06-08 ~ 2026-08-28 |
|| V2.0 | [MILESTONES/V2.0-小版本规划.md](../MILESTONES/V2.0-小版本规划.md) | 2026-09-07 ~ 2026-11-28 |
|| V3.0 | [MILESTONES/V3.0-小版本规划.md](../MILESTONES/V3.0-小版本规划.md) | 2026-12-01 ~ 2027-03-27 |
|| V4.0 | [MILESTONES/V4.0-小版本规划.md](../MILESTONES/V4.0-小版本规划.md) | 骨架 |
|| V5.0 | [MILESTONES/V5.0-小版本规划.md](../MILESTONES/V5.0-小版本规划.md) | 骨架 |

> **说明**：每个小版本采用 1 周开发 + 0.5 周测试 = 1.5 周发布的节奏，方便每周独立开发交付。详细的任务 ID、工作量估算和交付物在上述小版本规划文档中。
>
> **任务 ID 规则**：任务 ID 格式为 `{版本}-{模块}-{序号}`，例如 `V20-DDL-001`（V2.0 数据库设计）、`NF-BE-001`（v1.0 通知框架后端）。

---

## 开发任务单索引

### Core 核心

|| 版本 | 模块 | 任务 ID 前缀 | PRD | TASK 文件 | 状态 |
||------|------|-------------|------|---------|------|
|| V1.0 | 认证/项目/任务/通知/系统 | — | ✅ | 关联 Milestone | ✅ 已完成 |
|| V2.0 | EAV 自定义字段 | V20-* | ✅ | [任务管理_tasks.md](./core/V2.0/任务管理_tasks.md) | ✅ 已展开 |
|| V2.0 | Issue Type 体系 | V21-* | ✅ | [任务管理_tasks.md](./core/V2.0/任务管理_tasks.md) | ✅ 已展开 |
|| V2.0 | Screen / Scheme | V22-* | ✅ | [任务管理_tasks.md](./core/V2.0/任务管理_tasks.md) | ✅ 已展开 |
|| V2.0 | Field Config + 工作流 | V23-* | ✅ | [任务管理_tasks.md](./core/V2.0/任务管理_tasks.md) | ✅ 已展开 |
|| V2.0 | 项目模板 + 看板增强 | V24-* | ✅ | [项目管理_tasks.md](./core/V2.0/项目管理_tasks.md) | ✅ 已展开 |
|| V2.0 | UI 插件化深化 | V24-UIP-* | ✅ | [UI插件化_tasks.md](./core/V2.0/UI插件化_tasks.md) | ✅ 已展开 |
|| V3.0 | Issue Link + Attachment | V30-* | ✅ | [任务管理_tasks.md](./core/V3.0/任务管理_tasks.md) | ✅ 骨架 |
|| V3.0 | Time Tracking + 回收站 | V31-* | ✅ | [任务管理_tasks.md](./core/V3.0/任务管理_tasks.md) | ✅ 骨架 |
|| V3.0 | Automation 执行引擎 | V32-* | ✅ | [任务管理_tasks.md](./core/V3.0/任务管理_tasks.md) | ✅ 骨架 |
|| V3.0 | 高级看板 | V33-* | ✅ | [敏捷看板_tasks.md](./core/V3.0/敏捷看板_tasks.md) | ✅ 骨架 |

### Plugin 插件

|| 版本 | 插件 | 任务 ID 前缀 | PRD | TASK 文件 | 状态 |
||------|------|-------------|------|---------|------|
|| V1.0 | lest-meeting | MTG-* | ✅ | [plugins/V1.0/README.md](./plugins/V1.0/) | ✅ 已展开 |
|| V1.0 | lest-release | REL-* | ✅ | [plugins/V1.0/README.md](./plugins/V1.0/) | ✅ 已展开 |
|| V1.0 | lest-worklog/cicd/ai/IM/code 等 | WL-/CI-/AI-/IMFS-/CD-* | ✅ | [plugins/V1.0/README.md](./plugins/V1.0/) | ✅ 已展开 |
|| V2.0 | lest-ai/report/meeting/release/jira-sync/devops 等 | V25-*-* | ✅ | [plugins/V2.0/README.md](./plugins/V2.0/) | ✅ 已展开 |
|| V3.0 | lest-okr | V36-OKR-* | ✅ | [plugins/V3.0/lest-okr.md](./plugins/V3.0/lest-okr.md) | ✅ 骨架 |
|| V3.0 | lest-approval | V36-AP-* | ✅ | [plugins/V3.0/lest-approval.md](./plugins/V3.0/lest-approval.md) | ✅ 骨架 |
|| V3.0 | lest-portfolio | V36-PF-* | ✅ | [plugins/V3.0/lest-portfolio.md](./plugins/V3.0/lest-portfolio.md) | ✅ 骨架 |
|| V3.0 | lest-ldap/sso/multi-tenant | — | ✅ | 实施在 V5.0 | 📋 PRD 完成 |
