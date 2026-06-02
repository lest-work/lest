# 插件 -- V2.0 开发任务单

> **关联 Milestone**：[MILESTONES/V2.0-小版本规划.md](../../MILESTONES/V2.0-小版本规划.md)
>
> **状态说明**：`🔴 待启动` = 未开始 | `🟡 开发中` = 进行中 | `✅ 已完成` = 完成

## V2.0 新增插件（9 个）

> V2.0 的 9 个新插件集中在 v2.5 小版本实现，均依赖 v2.4 的 Scheme 基础设施（EAV/IssueType/Screen/Workflow）。

|| 插件 ID | PRD 文档 | 小版本 | 任务 ID | 状态 |
||--------|---------|--------|--------|------|
|| lest-ai | [lest-ai.md](../../../1-prd/plugins/V2.0/lest-ai.md) | v2.5 | V25-AI-* | 🔴 待启动 |
|| lest-report | [lest-report.md](../../../1-prd/plugins/V2.0/lest-report.md) | v2.5 | V25-RPT-* | 🔴 待启动 |
|| lest-meeting | [lest-meeting.md](../../../1-prd/plugins/V2.0/lest-meeting.md) | v2.5 | V25-MTG-* | 🔴 待启动 |
|| lest-release | [lest-release.md](../../../1-prd/plugins/V2.0/lest-release.md) | v2.5 | V25-REL-* | 🔴 待启动 |
|| lest-jira-sync | [lest-jira-sync.md](../../../1-prd/plugins/V2.0/lest-jira-sync.md) | v2.5 | V25-JSY-* | 🔴 待启动 |
|| lest-devops | [lest-devops.md](../../../1-prd/plugins/V2.0/lest-devops.md) | v2.5 | V25-DVS-* | 🔴 待启动 |
|| lest-gitea | [lest-gitea.md](../../../1-prd/plugins/V2.0/lest-gitea.md) | v2.5 | V25-GIT-* | 🔴 待启动 |
|| lest-gogs | [lest-gogs.md](../../../1-prd/plugins/V2.0/lest-gogs.md) | v2.5 | V25-GOG-* | 🔴 待启动 |
|| lest-zentao-import | [lest-zentao-import.md](../../../1-prd/plugins/V2.0/lest-zentao-import.md) | v2.5 | V25-ZTI-* | 🔴 待启动 |

## V2.5 详细任务拆分

> 以下任务在 V2.5 小版本（2026-11-17 ~ 2026-11-28）集中实现。

### lest-ai AI 服务增强（V25-AI-*）

|| 任务 ID | 说明 | 工作量 | 状态 |
||---------|------|--------|------|
|| V25-AI-BE-001 | AI 网关 V2 架构升级（多模型编排 + RAG + Memory） | 2d | 🔴 |
|| V25-AI-BE-002 | 深度代码审查引擎（Claude/GPT-4 深度分析） | 2d | 🔴 |
|| V25-AI-BE-003 | 智能任务拆解（Epic → Story → Task 自动分解） | 2d | 🔴 |
|| V25-AI-BE-004 | 会议 AI 总结 V2（结构化摘要 + 关键行动项） | 1d | 🔴 |
|| V25-AI-FE-001 | AI 服务 V2 配置页面（模型选择 + RAG 配置） | 1d | 🔴 |
|| V25-AI-FE-002 | 代码审查结果面板 V2 | 1.5d | 🔴 |
|| V25-AI-FE-003 | 任务拆解对话界面 | 1.5d | 🔴 |

### lest-report 报表中心（V25-RPT-*）

|| 任务 ID | 说明 | 工作量 | 状态 |
||---------|------|--------|------|
|| V25-RPT-BE-001 | Burndown Chart 数据 API | 1d | 🔴 |
|| V25-RPT-BE-002 | CFD（累积流图）数据 API | 1d | 🔴 |
|| V25-RPT-BE-003 | Velocity Chart 数据 API | 1d | 🔴 |
|| V25-RPT-BE-004 | Control Chart 数据 API | 1d | 🔴 |
|| V25-RPT-BE-005 | 报表导出 API（PDF/Excel/CSV） | 1d | 🔴 |
|| V25-RPT-BE-006 | 报表订阅 API（定时邮件推送） | 1d | 🔴 |
|| V25-RPT-FE-001 | 报表设计器（拖拽字段 + 图表类型选择） | 2d | 🔴 |
|| V25-RPT-FE-002 | 报表仪表盘（多图表自由布局） | 1.5d | 🔴 |

### lest-meeting 敏捷会议增强（V25-MTG-*）

|| 任务 ID | 说明 | 工作量 | 状态 |
||---------|------|--------|------|
|| V25-MTG-BE-001 | 会议计时器增强（议题计时 + 演讲模式） | 1d | 🔴 |
|| V25-MTG-BE-002 | 会议模板库 API | 1d | 🔴 |
|| V25-MTG-BE-003 | 视频会议集成 API（腾讯会议/钉钉/飞书/Zoom） | 2d | 🔴 |
|| V25-MTG-FE-001 | 会议计时器 UI | 1d | 🔴 |
|| V25-MTG-FE-002 | 会议模板库页面 | 1d | 🔴 |

### lest-release 发布管理增强（V25-REL-*）

|| 任务 ID | 说明 | 工作量 | 状态 |
||---------|------|--------|------|
|| V25-REL-BE-001 | 发布计划甘特图 API | 1d | 🔴 |
|| V25-REL-BE-002 | 制品管理 API（版本化文件存储） | 1.5d | 🔴 |
|| V25-REL-BE-003 | 变更记录 API（完整的变更历史） | 1d | 🔴 |
|| V25-REL-FE-001 | 发布甘特图页面 | 1.5d | 🔴 |
|| V25-REL-FE-002 | 制品管理 UI | 1d | 🔴 |

### lest-jira-sync Jira 双向同步（V25-JSY-*）

|| 任务 ID | 说明 | 工作量 | 状态 |
||---------|------|--------|------|
|| V25-JSY-BE-001 | Jira 连接器 API（OAuth2 授权） | 1.5d | 🔴 |
|| V25-JSY-BE-002 | Issue 双向同步引擎（映射规则配置） | 2d | 🔴 |
|| V25-JSY-BE-003 | 同步冲突检测与解决策略 | 1d | 🔴 |
|| V25-JSY-BE-004 | 同步历史记录 API | 1d | 🔴 |
|| V25-JSY-FE-001 | Jira 连接配置页面 | 1d | 🔴 |
|| V25-JSY-FE-002 | 映射规则配置 UI | 1.5d | 🔴 |

### lest-devops DevOps 集成（V25-DVS-*）

|| 任务 ID | 说明 | 工作量 | 状态 |
||---------|------|--------|------|
|| V25-DVS-BE-001 | SonarQube API 包装（质量门禁数据） | 1d | 🔴 |
|| V25-DVS-BE-002 | Harbor API 包装（镜像仓库数据） | 1d | 🔴 |
|| V25-DVS-BE-003 | Prometheus API 包装（应用指标数据） | 1d | 🔴 |
|| V25-DVS-BE-004 | DevOps 仪表盘聚合 API | 1.5d | 🔴 |
|| V25-DVS-FE-001 | DevOps 仪表盘页面 | 2d | 🔴 |

### lest-gitea / lest-gogs Gitea/Gogs 集成（V25-GIT-* / V25-GOG-*）

|| 任务 ID | 说明 | 工作量 | 状态 |
||---------|------|--------|------|
|| V25-GIT-BE-001 | Gitea API 包装（仓库 + MR + 提交） | 1.5d | 🔴 |
|| V25-GIT-FE-001 | Gitea 连接配置 + 仓库列表 | 1.5d | 🔴 |
|| V25-GOG-BE-001 | Gogs API 包装（仓库 + MR + 提交） | 1.5d | 🔴 |
|| V25-GOG-FE-001 | Gogs 连接配置 + 仓库列表 | 1.5d | 🔴 |

### lest-zentao-import 禅道导入（V25-ZTI-*）

|| 任务 ID | 说明 | 工作量 | 状态 |
||---------|------|--------|------|
|| V25-ZTI-BE-001 | 禅道 API 连接器 | 1d | 🔴 |
|| V25-ZTI-BE-002 | 数据映射规则引擎（禅道项目/任务/用户 → LEST） | 1.5d | 🔴 |
|| V25-ZTI-BE-003 | 批量导入执行引擎（异步 + 进度跟踪） | 1.5d | 🔴 |
|| V25-ZTI-BE-004 | 导入报告生成 | 1d | 🔴 |
|| V25-ZTI-FE-001 | 禅道导入向导（连接 → 映射 → 预览 → 执行） | 2.5d | 🔴 |
