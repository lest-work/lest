# Core V1.0 — Issue / Board / Attachment 任务单

> **关联 PRD**：[任务管理.md](../../../1-prd/core/V1.0/任务管理.md)
>
> **关联总任务单**：[V1.0-Core-6-minor-tasks.md](./V1.0-Core-6-minor-tasks.md)
>
> **范围说明**：本文只覆盖 V1.0 Core 内的 Issue、Backlog、Sprint、Board、标签、评论、关注、子任务、依赖、附件。Worklog、批量操作、回收站、导入导出、代码仓库、CI/CD 均不进入 V1.0。

## 1.3 Issue 详情与协作闭环

| 任务 ID | 任务 | 工作量 | 状态 |
|---------|------|--------|------|
| V13-ISS-BE-001 | Issue Key 生成器与项目内序列 | 1d | 🔴 |
| V13-ISS-BE-002 | Issue 创建/详情/更新/软删除 API | 2d | 🔴 |
| V13-ISS-BE-003 | Issue 列表分页与基础筛选 API | 1.5d | 🔴 |
| V13-ISS-BE-004 | 状态流转 API 与阻塞校验 | 1d | 🔴 |
| V13-ISS-BE-005 | 负责人、优先级、截止日期、预估字段更新 API | 1d | 🔴 |
| V13-ISS-BE-006 | 活动历史记录服务 | 1d | 🔴 |
| V13-LABEL-BE-001 | 项目标签 CRUD 与任务标签关联 API | 1.5d | 🔴 |
| V13-CMNT-BE-001 | 评论列表、新增、编辑、删除 API | 1.5d | 🔴 |
| V13-CMNT-BE-002 | @ 提及解析与通知事件触发 | 1d | 🔴 |
| V13-WATCH-BE-001 | Watch / Unwatch / Watcher 列表 API | 1d | 🔴 |
| V13-SUB-BE-001 | 子任务创建、列表、层级校验 API | 1.5d | 🔴 |
| V13-DEP-BE-001 | 依赖创建、删除、查询、循环检测 API | 2d | 🔴 |
| V13-ISS-FE-001 | Issue 列表页与筛选器 | 2d | 🔴 |
| V13-ISS-FE-002 | 新建/编辑 Issue 抽屉 | 1.5d | 🔴 |
| V13-ISS-FE-003 | Issue 详情抽屉或详情页 | 2d | 🔴 |
| V13-CMNT-FE-001 | 评论列表与 Markdown 编辑器 | 1.5d | 🔴 |
| V13-WATCH-FE-001 | 关注按钮与关注者列表 | 0.5d | 🔴 |
| V13-SUB-FE-001 | 子任务列表与创建入口 | 1d | 🔴 |
| V13-DEP-FE-001 | 依赖展示与添加/移除交互 | 1d | 🔴 |
| V13-DASH-FE-001 | 我的工作台基础视图 | 1.5d | 🔴 |

## 1.4 Backlog / Sprint / Board

| 任务 ID | 任务 | 工作量 | 状态 |
|---------|------|--------|------|
| V14-BLG-BE-001 | Backlog 查询、排序、移入 Sprint API | 1.5d | 🔴 |
| V14-SPR-BE-001 | Sprint 创建、开始、完成、任务迁移 API | 2d | 🔴 |
| V14-BRD-BE-001 | Board 数据 API（三列 + 卡片 + 列内排序） | 1.5d | 🔴 |
| V14-BRD-BE-002 | 拖拽更新状态与排序 API | 1d | 🔴 |
| V14-BLG-FE-001 | Backlog 视图 | 2d | 🔴 |
| V14-SPR-FE-001 | Sprint 计划、启动、完成交互 | 2d | 🔴 |
| V14-BRD-FE-001 | Board 三列视图与卡片组件 | 2d | 🔴 |
| V14-BRD-FE-002 | Board 拖拽、乐观更新、失败回滚 | 1.5d | 🔴 |
| V14-BRD-FE-003 | Board 基础筛选 | 0.5d | 🔴 |
| V14-TIME-FE-001 | 时间线基础视图 | 1.5d | 🔴 |

## 1.5 附件与任务通知事件

| 任务 ID | 任务 | 工作量 | 状态 |
|---------|------|--------|------|
| V15-ATT-BE-001 | 附件上传 API 与大小限制 | 1d | 🔴 |
| V15-ATT-BE-002 | 附件列表、下载、删除 API | 1d | 🔴 |
| V15-ATT-BE-003 | 附件元数据与权限校验 | 0.5d | 🔴 |
| V15-ATT-FE-001 | 附件上传组件与进度展示 | 1d | 🔴 |
| V15-ATT-FE-002 | 附件列表、下载、删除交互 | 1d | 🔴 |
| V15-NOT-BE-001 | task.created / assigned / commented / status_changed 事件 | 1d | 🔴 |
| V15-NOT-BE-002 | task.due_soon / overdue / dependency_unblocked 事件 | 1d | 🔴 |

## V1.0 明确不做

| 能力 | 后续版本 |
|------|----------|
| Worklog、剩余工时、工时报表 | V3.0 Core |
| 批量编辑、批量移动、批量删除 | V3.0 Core |
| 回收站与永久删除 | V3.0 Core |
| Excel/CSV 导入导出 | V3.0 Core |
| 代码提交、MR/PR、CI/CD 关联 | V4.0 插件 |
| WakaTime、会议、IM、AI | V4.0 插件 |
