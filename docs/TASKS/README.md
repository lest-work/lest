# LEST Platform 敏捷开发任务看板主索引

> 本目录存放按 PRD 分解的精细化开发任务单，供 AI 辅助开发和人工开发追踪进度使用。
>
> **快速导航**：[完整里程碑规划](../MILESTONES.zh-CN.md) · [版本变更记录](../../CHANGELOG.zh-CN.md) · [PRD 需求文档](../PRD/)

---

## 目录结构

```
docs/TASKS/
├── README.md                    # 本文件 — 总索引
└── V1.0/                       # V1.0 迭代版本开发任务集
    ├── 认证系统与系统管理_tasks.md
    ├── 项目管理_tasks.md
    ├── 任务管理_tasks.md
    ├── 发布管理_tasks.md
    ├── 通知与消息_tasks.md
    ├── 敏捷会议_tasks.md
    ├── 系统设置_tasks.md
    ├── CI持续集成_tasks.md
    ├── WakaTime集成_tasks.md
    ├── 代码管理_tasks.md
    ├── 团队绩效_tasks.md
    ├── AI服务_tasks.md
    ├── 插件系统_tasks.md
    └── 开放平台_tasks.md
```

> **注**：V2.0 AI 服务任务单在 [docs/PRD/V2.0/AI服务.md](../PRD/V2.0/AI服务.md) 中管理，V2.0 TASKS 目录待创建。

---

## V1.0 开发任务清单

> 状态图例：`✅ 已完成` | `🟡 进行中` | `🔴 待启动` | `⏸️ 暂缓`

| 关联 PRD | 对应任务文件 | 微服务模块 | 完成度 | 状态 | v0.1/v0.2 发布情况 |
|:---|:---|:---|:---|:---|:---|
| 认证系统与系统管理 | [认证系统与系统管理_tasks.md](./V1.0/认证系统与系统管理_tasks.md) | `lest-auth` / `lest-system` | DDL✅ 后端✅ 前端✅ | ✅ 已完成 | v0.1.0 |
| 项目管理 | [项目管理_tasks.md](./V1.0/项目管理_tasks.md) | `lest-project` | DDL✅ 后端✅ 前端✅ | ✅ 已完成 | v0.1.0 + v0.2.0 |
| 任务管理 | [任务管理_tasks.md](./V1.0/任务管理_tasks.md) | `lest-task` | DDL✅ 后端✅ 前端✅ | ✅ 已完成 | v0.1.0 + v0.2.0 |
| 发布管理 | [发布管理_tasks.md](./V1.0/发布管理_tasks.md) | `lest-release` | DDL✅ 后端✅ 前端🟡 | 🟡 进行中 | v0.1.0 (后端)，前端待完善 |
| 通知与消息 | [通知与消息_tasks.md](./V1.0/通知与消息_tasks.md) | `lest-notification` | — | 🔴 待启动 | v0.4.0 |
| 敏捷会议 | [敏捷会议_tasks.md](./V1.0/敏捷会议_tasks.md) | `lest-meeting` | — | 🔴 待启动 | v0.4.0 |
| 系统设置 | [系统设置_tasks.md](./V1.0/系统设置_tasks.md) | `lest-system` | DDL✅ 后端✅ 前端✅ | ✅ 已完成 | v0.1.0 |
| CI 持续集成 | [CI持续集成_tasks.md](./V1.0/CI持续集成_tasks.md) | `lest-ci` | — | 🔴 待启动 | v0.5.0 |
| WakaTime 集成 | [WakaTime集成_tasks.md](./V1.0/WakaTime集成_tasks.md) | `lest-wakapi` | — | 🔴 待启动 | v0.5.0 |
| 代码管理 | [代码管理_tasks.md](./V1.0/代码管理_tasks.md) | `lest-code` | — | 🔴 待启动 | v0.6.0 |
| 团队绩效 | [团队绩效_tasks.md](./V1.0/团队绩效_tasks.md) | `lest-performance` | — | 🔴 待启动 | v0.5.0 |
| AI 服务 | [AI服务_tasks.md](./V1.0/AI服务_tasks.md) | `lest-ai` | — | 🔴 待启动 | v0.9.0 |
| 插件系统 | [插件系统_tasks.md](./V1.0/插件系统_tasks.md) | `lest-plugin` | — | 🔴 待启动 | v1.0.0 |
| 开放平台 | [开放平台_tasks.md](./V1.0/开放平台_tasks.md) | `lest-open` | — | 🔴 待启动 | v0.7.0 |

---

## 状态变更规范

AI 开发者（如 Cursor）或人工开发人员在开始、提测、完成任何功能时，**必须定位到具体的 `_tasks.md` 文件**中执行如下更新：

1. **领单启动** — 将任务 `状态` 从 `🔴 待启动` 修改为 `🟡 进行中`，在 `备注` 中追加承接人和日期
2. **提测运行** — 本地或 CI 测试通过后，将 `测试结果` 修改为 `PASS`
3. **完成合并** — 代码合并至 `develop` 后，将任务 `状态` 更新为 `✅ 已完成`，在 `备注` 中补充 commit hash 和 PR 编号

---

> 查看完整里程碑规划：[MILESTONES.zh-CN.md](../MILESTONES.zh-CN.md)
