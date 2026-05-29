# LEST Platform 敏捷开发任务看板主索引 (docs/TASKS/README.md)

## 📂 任务目录结构规范

为了彻底消除 AI 辅助开发在复杂业务落地时的盲目性，平台开发任务已完全对标 `docs/PRD/`，建立模块化、高精度的开发任务树：

```
docs/TASKS/
├── README.md                          # 本任务看板总索引
└── V1.0/                              # V1.0 迭代版本开发任务集
    ├── 认证系统与系统管理_tasks.md      # 关联 认证系统与系统管理 PRD
    ├── 项目管理_tasks.md                # 关联 项目管理 PRD
    ├── 任务管理_tasks.md                # 关联 任务管理 PRD
    ├── WakaTime集成_tasks.md            # 关联 WakaTime集成 PRD
    ├── AI服务_tasks.md                  # 关联 AI服务 PRD
    ├── 通知与消息_tasks.md              # 关联 通知与消息 PRD
    └── 插件系统_tasks.md                # 关联 插件系统 PRD
```

---

## 📅 V1.0 敏捷模块开发任务白皮书清单

点击下方链接直接进入对应模块的高精度开发任务单进行进度查看、领单及状态标记：

| 关联 PRD 需求文档 | 对应开发任务白皮书 (Markdown 格式) | 负责微服务模块 | 当前整体完成度 | 状态 |
| :--- | :--- | :--- | :--- | :--- |
| **认证系统与系统管理 PRD** | [认证系统与系统管理_tasks.md](./V1.0/认证系统与系统管理_tasks.md) | `lest-auth` / `lest-system` | DDL✅ 后端API✅ 前端✅ 仪表盘✅ | � 后端/数据库/前端基础已完成 |
| **项目管理 PRD** | [项目管理_tasks.md](./V1.0/项目管理_tasks.md) | `lest-project` | DDL⚠️ 后端API🟡 前端API✅ 仪表盘✅ | � 后端+API层完成，DDL待补，前端页面待开发 |
| **任务管理 PRD** | [任务管理_tasks.md](./V1.0/任务管理_tasks.md) | `lest-task` | DDL⚠️ 后端API🟡 前端API✅ 仪表盘✅ | 🟡 后端+API层完成，DDL待补，前端页面待开发 |
| **系统设置 PRD** | [系统设置_tasks.md](./V1.0/系统设置_tasks.md) | `lest-system` | DDL✅ 后端API✅ 前端✅ | 🟢 已通过 sys_config/sys_dict 实现 |
| **敏捷会议 PRD** | [敏捷会议_tasks.md](./V1.0/敏捷会议_tasks.md) | `lest-meeting` | — | 🔴 待启动 |
| **发布管理 PRD** | [发布管理_tasks.md](./V1.0/发布管理_tasks.md) | `lest-release` | — | 🔴 待启动 |
| **通知与消息 PRD** | [通知与消息_tasks.md](./V1.0/通知与消息_tasks.md) | `lest-notification` | — | 🔴 待启动 |
| **CI 持续集成 PRD** | [CI持续集成_tasks.md](./V1.0/CI持续集成_tasks.md) | `lest-ci` | — | 🔴 待启动 |
| **代码管理 PRD** | [代码管理_tasks.md](./V1.0/代码管理_tasks.md) | `lest-code` | — | 🔴 待启动 |
| **WakaTime 集成 PRD** | [WakaTime集成_tasks.md](./V1.0/WakaTime集成_tasks.md) | `lest-wakapi` | — | 🔴 待启动 |
| **团队绩效 PRD** | [团队绩效_tasks.md](./V1.0/团队绩效_tasks.md) | `lest-system` | — | 🔴 待启动 |
| **AI 服务 PRD** | [AI服务_tasks.md](./V1.0/AI服务_tasks.md) | `lest-ai` | — | 🔴 待启动 |
| **开放平台 PRD** | [开放平台_tasks.md](./V1.0/开放平台_tasks.md) | `lest-system` | — | 🔴 待启动 |
| **插件系统 PRD** | [插件系统_tasks.md](./V1.0/插件系统_tasks.md) | `lest-plugin` | — | 🔴 待启动 |

---

## 📊 任务领单与状态变更规范

当 AI 开发者（如 Cascade、Cursor 等）或人工开发人员在开始、提测、完成本项目的任何功能时，**必须定位到具体的 `_tasks.md` 文件**中执行如下更新：

1. **领单启动**：
   * 将任务的 `状态` 从 `🔴 待启动 (Pending)` 修改为 `🟡 进行中 (In Progress)`。
   * 在 `备注/关联提交` 中追加承接人标识，如：`Cascade 于 2026-05-26 领单启动`。
2. **提测运行**：
   * 本地或 CI 流水线测试跑通后，将 `测试结果` 从 `未测试` 修改为 `PASS (通过)`。
3. **完成合并**：
   * 代码合并至 `develop` 分支后，将任务 `状态` 更新为 `🟢 已完成 (Completed)`。
   * 必须在 `备注/关联提交` 中补充对应的 commit hash、PR 编号以及测试覆盖率（如 `Commit: 5a8e21c, PR #12, Coverage: 85%`）。
