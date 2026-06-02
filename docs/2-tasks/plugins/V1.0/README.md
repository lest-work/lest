# 插件 -- V1.0 开发任务单

> **关联 Milestone**：[MILESTONES/V1.0-小版本规划.md](../../MILESTONES/V1.0-小版本规划.md)
>
> **状态说明**：`🔴 待启动` = 未开始 | `🟡 开发中` = 进行中 | `✅ 已完成` = 完成 | `⚡ 后端已完成` = 后端已有实现，仅需前端接入

## V1.0 核心插件（7 个）

|| 插件 ID | PRD 文档 | 小版本 | 任务 ID（前缀） | 状态 |
||--------|---------|--------|-------------|------|
|| lest-worklog | [lest-worklog.md](../../../1-prd/plugins/V1.0/lest-worklog.md) | v1.3 | WL- | 🔴 待启动 |
|| lest-cicd | [lest-cicd.md](../../../1-prd/plugins/V1.0/lest-cicd.md) | v1.4 | CI- | 🔴 待启动 |
|| lest-wakatime | [lest-wakatime.md](../../../1-prd/plugins/V1.0/lest-wakatime.md) | v1.3 | WK- | 🔴 待启动 |
|| lest-ai | [lest-ai.md](../../../1-prd/plugins/V1.0/lest-ai.md) | v1.5 | AI- | 🔴 待启动 |
|| lest-im-feishu | [lest-im-feishu.md](../../../1-prd/plugins/V1.0/lest-im-feishu.md) | v1.5 | IMFS- | 🔴 待启动 |
|| lest-im-dingtalk | [lest-im-dingtalk.md](../../../1-prd/plugins/V1.0/lest-im-dingtalk.md) | v1.5 | IMDT- | 🔴 待启动 |
|| lest-im-wecom | [lest-im-wecom.md](../../../1-prd/plugins/V1.0/lest-im-wecom.md) | v1.5 | IMWX- | 🔴 待启动 |

## V1.0 附加插件

|| 插件 ID | PRD 文档 | 小版本 | 任务 ID（前缀） | 状态 |
||--------|---------|--------|-------------|------|
|| lest-code | [lest-code.md](../../../1-prd/plugins/V1.0/lest-code.md) | v1.5 | CD- | 🔴 待启动 |
|| lest-meeting | [lest-meeting.md](../../../1-prd/plugins/V1.0/lest-meeting.md) | v1.2 | MTG- | 🔴 待启动 |
|| lest-report | [lest-report.md](../../../1-prd/plugins/V1.0/lest-report.md) | v1.4 | RPT- | 🔴 待启动 |
|| lest-release | [lest-release.md](../../../1-prd/plugins/V1.0/lest-release.md) | v1.2 | REL- | 🔴 待启动 |
|| lest-openapi | [lest-openapi.md](../../../1-prd/plugins/V1.0/lest-openapi.md) | v1.0 | OP- | 🔴 待启动 |
|| lest-plugin-sdk | [lest-plugin-sdk.md](../../../1-prd/plugins/V1.0/lest-plugin-sdk.md) | v1.0 | PLG- | 🔴 待启动 |

## 核心基础设施（Core 依赖）

|| 模块 | PRD 文档 | 小版本 | 任务 ID | 状态 |
||------|---------|--------|--------|------|
|| 通知框架 | [通知与消息.md](../../../1-prd/core/V1.0/通知与消息.md) | v1.0 | NF- | 🔴 待启动 |
|| UI 插件化运行时 | [UI插件化架构设计.md](../../../1-prd/core/V2.0/UI插件化架构设计.md) | v1.1 | UIP- | 🔴 待启动 |
|| 系统管理精简 | [系统设置.md](../../../1-prd/core/V1.0/系统设置.md) | v1.1 | SYS- | 🔴 待启动 |

---

## 详细任务拆分索引

> 以下任务 ID 均来自 [MILESTONES/V1.0-小版本规划.md](../../MILESTONES/V1.0-小版本规划.md)，开发时请以该文档为准。

### v1.0 基础设施（2026-06-08 ~ 2026-06-20）

#### 通知框架（NF-*）

|| 任务 ID | 说明 | 工作量 | 状态 |
||---------|------|--------|------|
|| NF-BE-001 | lest-notification 服务初始化（Spring Boot、Redis、Kafka 连接） | 0.5d | 🔴 |
|| NF-BE-002 | `notification` 表 DDL | 0.5d | 🔴 |
|| NF-BE-003 | `notification_channel` 表 | 0.5d | 🔴 |
|| NF-BE-004 | NotificationController — 分页查询 + 标记已读 + 全部已读 API | 1d | 🔴 |
|| NF-BE-005 | Kafka 消费者：消费 task.assigned / task.commented / task.mentioned / task.status_changed 事件 | 1.5d | 🔴 |
|| NF-BE-006 | WebSocket 端点（STOMP） | 1d | 🔴 |
|| NF-BE-007 | NotificationService — 通知生成逻辑 | 1d | 🔴 |
|| NF-BE-008 | 通知发送渠道框架（站内通知接口，IM Webhook 作为扩展点） | 0.5d | 🔴 |
|| NF-BE-009 | lest-task 事件上报 Kafka | 1d | 🔴 |
|| NF-FE-001 | 通知铃铛组件（未读数 badge + 下拉列表） | 1d | 🔴 |
|| NF-FE-002 | 通知列表页面（分页 + 筛选 + 标记已读） | 1d | 🔴 |
|| NF-FE-003 | WebSocket 客户端（接入 stomp.js，实时推送通知） | 1d | 🔴 |
|| NF-FE-004 | 任务详情页评论 @ 提及功能 | 1d | 🔴 |

#### 插件系统框架（PLG-*）

|| 任务 ID | 说明 | 工作量 | 状态 |
||---------|------|--------|------|
|| PLG-BE-001 | `plugin` 表 DDL | 0.5d | 🔴 |
|| PLG-BE-002 | `plugin_config` 表 | 0.5d | 🔴 |
|| PLG-BE-003 | PluginController — CRUD + 启用/禁用/配置 API | 1d | 🔴 |
|| PLG-BE-004 | PluginLifecycleService — 安装/卸载/启用/禁用/热加载逻辑 | 1.5d | 🔴 |
|| PLG-BE-005 | PluginManifest 校验 | 1d | 🔴 |
|| PLG-BE-006 | 后端扩展点定义 | 1d | 🔴 |
|| PLG-BE-007 | 后端插件 SDK（Java 实现） | 1.5d | 🔴 |
|| PLG-BE-008 | 前端插件元数据 API | 0.5d | 🔴 |

#### 开放平台框架（OP-*）

|| 任务 ID | 说明 | 工作量 | 状态 |
||---------|------|--------|------|
|| OP-BE-001 | `api_key` 表 DDL | 0.5d | 🔴 |
|| OP-BE-002 | `api_key_usage_log` 表 | 0.5d | 🔴 |
|| OP-BE-003 | ApiKeyController — 创建/撤销/使用量统计 API | 1d | 🔴 |
|| OP-BE-004 | API 认证中间件（JWT Bearer Token + API Key） | 1d | 🔴 |
|| OP-BE-005 | Webhook 接收端点框架 | 1d | 🔴 |
|| OP-BE-006 | 速率限制（令牌桶） | 0.5d | 🔴 |
|| OP-FE-001 | API Key 管理页面 | 1d | 🔴 |
|| OP-FE-002 | Webhook 配置页面 | 1d | 🔴 |

### v1.1 UI 插件化 + 系统精简（2026-06-22 ~ 2026-07-04）

|| 任务 ID | 说明 | 工作量 | 状态 |
||---------|------|--------|------|
|| UIP-FE-001 | PluginRegistry — 插件注册表 | 1d | 🔴 |
|| UIP-FE-002 | ExtensionResolver — 扩展点解析器 | 1d | 🔴 |
|| UIP-FE-003 | ComponentRegistry — 组件注册表（动态注册 Vue 组件） | 1d | 🔴 |
|| UIP-FE-004 | useExtensionResolver composable | 1d | 🔴 |
|| UIP-FE-005 | `<PluginComponent>` Vue 组件 | 0.5d | 🔴 |
|| UIP-FE-006 | TaskDetailTabs 改造（动态渲染，支持插件注入） | 1.5d | 🔴 |
|| UIP-FE-007 | TaskDetailFields 改造（左侧/右侧字段区支持插件字段注入） | 1d | 🔴 |
|| UIP-FE-008 | ProjectDetailTabs 改造 | 1d | 🔴 |
|| UIP-FE-009 | 插件管理页面（安装/启用/禁用/配置） | 1.5d | 🔴 |
|| SYS-BE-001 | 系统管理精简（保留邮件/安全/审计，删除 OA 功能） | 1d | 🔴 |
|| SYS-FE-001 | 系统管理精简前端 UI | 1d | 🔴 |

### v1.2 会议 + 发布管理前端（2026-07-06 ~ 2026-07-18）

|| 任务 ID | 说明 | 工作量 | 状态 |
||---------|------|--------|------|
|| MTG-BE-001 | 会议 CRUD API | 1d | 🔴 |
|| MTG-BE-002 | 参与者管理 API | 0.5d | 🔴 |
|| MTG-BE-003 | 站会发言 API（每人提交昨日/今日/阻塞） | 1d | 🔴 |
|| MTG-BE-004 | 站会汇总 API（自动汇总所有参与者发言） | 0.5d | 🔴 |
|| MTG-BE-005 | 会议提醒定时任务（调用通知框架） | 0.5d | 🔴 |
|| MTG-BE-006 | 会议邀请逻辑（调用通知框架） | 1d | 🔴 |
|| MTG-BE-007 | 会议纪要 API（关联的行动项自动创建为任务） | 1d | 🔴 |
|| MTG-BE-008 | 会议计时器 API | 1d | 🔴 |
|| MTG-FE-001 | 会议列表页（卡片视图 + 日历视图切换） | 1d | 🔴 |
|| MTG-FE-002 | 新建会议弹窗（选择类型/参与者/日期/迭代） | 1d | 🔴 |
|| MTG-FE-003 | 会议详情页（议程 + 参与者状态 + 计时器 + 纪要编辑器） | 2d | 🔴 |
|| MTG-FE-004 | 会议日历视图（FullCalendar） | 1.5d | 🔴 |
|| MTG-FE-005 | 会议纪要编辑器（富文本 + 行动项一键转任务） | 1.5d | 🔴 |
|| MTG-FE-006 | 会议首页快捷入口（今日会议 + 即将开始提醒） | 0.5d | 🔴 |
|| REL-FE-001 | 发布管理 API 层（接入已实现的 lest-release 后端） | 1d | 🔴 |
|| REL-FE-002 | 发布计划列表页（卡片视图） | 1d | 🔴 |
|| REL-FE-003 | 发布计划详情页（甘特图 + 制品 + 变更记录） | 1.5d | 🔴 |
|| REL-FE-004 | 发布制品管理 UI | 1d | 🔴 |

### v1.3 工时管理 + WakaTime（2026-07-20 ~ 2026-08-01）

|| 任务 ID | 说明 | 工作量 | 状态 |
||---------|------|--------|------|
|| WL-BE-001 | 工时记录 CRUD API | 1d | 🔴 |
|| WL-BE-002 | 工时统计 API（按项目/成员/迭代聚合） | 1d | 🔴 |
|| WL-BE-003 | 工时报表 API（周报/月报数据） | 1d | 🔴 |
|| WL-BE-004 | 工时与任务关联（task_id → worklog 列表） | 1d | 🔴 |
|| WL-BE-005 | 工时审批流程 API（可选：提交→审批→通过） | 1d | 🔴 |
|| WL-FE-001 | 工时记录页面（今日/本周/本月/自定义） | 1.5d | 🔴 |
|| WL-FE-002 | 工时统计看板（柱状图/折线图/饼图） | 1.5d | 🔴 |
|| WL-FE-003 | 工时记录抽屉（快速记录当前任务工时） | 1d | 🔴 |
|| WK-BE-001 | WakaTime 心跳接收 API | 1d | 🔴 |
|| WK-BE-002 | 编码时间聚合 API（按天/周/项目/语言聚合） | 1d | 🔴 |
|| WK-BE-003 | 编码热力图数据 API | 1d | 🔴 |
|| WK-FE-001 | 编码热力图（GitHub 风格，按天着色） | 1.5d | 🔴 |
|| WK-FE-002 | 编码时间统计（语言/项目/编辑器的饼图/柱状图） | 1d | 🔴 |
|| WK-FE-003 | WakaTime 设置页面（API Key 配置 + 任务关联） | 0.5d | 🔴 |

### v1.4 CI/CD + 绩效（2026-08-03 ~ 2026-08-15）

|| 任务 ID | 说明 | 工作量 | 状态 |
||---------|------|--------|------|
|| CI-BE-001 | CI/CD Webhook 接收端点（GitLab/GitHub/Jenkins） | 1.5d | 🔴 |
|| CI-BE-002 | 构建历史记录 API | 1d | 🔴 |
|| CI-BE-003 | 构建状态聚合 API（成功/失败/运行中统计） | 1d | 🔴 |
|| CI-BE-004 | 构建详情 API（构建日志 + 阶段 + 产物） | 1d | 🔴 |
|| CI-BE-005 | 构建 ↔ 任务关联 API | 1d | 🔴 |
|| CI-BE-006 | 构建趋势 API（成功率 + 周期时间趋势） | 1d | 🔴 |
|| CI-BE-007 | 发布 ↔ 构建关联 API | 0.5d | 🔴 |
|| CI-FE-001 | CI/CD 配置页面（仓库 + 分支 + 构建脚本配置） | 1.5d | 🔴 |
|| CI-FE-002 | 构建历史列表页 | 1d | 🔴 |
|| CI-FE-003 | 构建详情页（阶段/日志/产物/状态时间线） | 1.5d | 🔴 |
|| CI-FE-004 | 构建趋势图表 | 1d | 🔴 |
|| PRF-BE-001 | 个人绩效统计 API（工时/代码提交/会议参与/任务完成） | 1.5d | 🔴 |
|| PRF-BE-002 | 团队绩效聚合 API | 1d | 🔴 |
|| PRF-BE-003 | 绩效报表 API（个人 + 团队 + 项目维度） | 1d | 🔴 |
|| PRF-BE-004 | 会议参与统计 API | 0.5d | 🔴 |
|| PRF-FE-001 | 个人绩效仪表盘 | 1.5d | 🔴 |
|| PRF-FE-002 | 团队绩效排行榜 | 1d | 🔴 |
|| PRF-FE-003 | 绩效报表导出 | 0.5d | 🔴 |
|| RPT-BE-001 | KPI 数据聚合 API | 1d | 🔴 |
|| RPT-BE-002 | KPI 趋势数据 API | 1d | 🔴 |
|| RPT-FE-001 | KPI 看板页面 | 1.5d | 🔴 |
|| RPT-FE-002 | 导出功能（Excel） | 0.5d | 🔴 |

### v1.5 AI 服务 + IM 集成（2026-08-17 ~ 2026-08-28）

|| 任务 ID | 说明 | 工作量 | 状态 |
||---------|------|--------|------|
|| AI-BE-001 | AI 网关（多模型路由：OpenAI / Claude / Gemini / 本地模型） | 2d | 🔴 |
|| AI-BE-002 | 代码审查 API（接收 PR/MR → AI 分析 → 返回审查意见） | 1.5d | 🔴 |
|| AI-BE-003 | 任务助手 API（任务描述 → 子任务拆分建议） | 1.5d | 🔴 |
|| AI-BE-004 | 插件 SDK 接入 lest-ai（PlatformClient.ai()） | 1d | 🔴 |
|| AI-BE-005 | 会议总结 API | 1d | 🔴 |
|| AI-FE-001 | AI 服务配置页面（API Key + 模型选择 + 熔断配置） | 1d | 🔴 |
|| AI-FE-002 | 代码审查结果面板（MR/PR 详情页内嵌） | 1.5d | 🔴 |
|| AI-FE-003 | 会议纪要 AI 总结 | 1d | 🔴 |
|| AI-FE-004 | 任务助手对话界面（任务详情页内嵌） | 1.5d | 🔴 |
|| IMFS-BE-001 | 飞书 Webhook 消息推送 | 1d | 🔴 |
|| IMFS-BE-002 | 飞书消息模板（任务通知/会议提醒/发布通知） | 0.5d | 🔴 |
|| IMFS-BE-003 | 飞书 OAuth2 授权（扫码登录） | 1d | 🔴 |
|| IMFS-FE-001 | 飞书配置页面 | 0.5d | 🔴 |
|| IMDT-BE-001 | 钉钉 Webhook 消息推送 | 1d | 🔴 |
|| IMDT-BE-002 | 钉钉消息模板 | 0.5d | 🔴 |
|| IMDT-BE-003 | 钉钉 OAuth2 授权 | 1d | 🔴 |
|| IMDT-FE-001 | 钉钉配置页面 | 0.5d | 🔴 |
|| IMWX-BE-001 | 企业微信 Webhook 消息推送 | 1d | 🔴 |
|| IMWX-BE-002 | 企业微信消息模板 | 0.5d | 🔴 |
|| IMWX-BE-003 | 企业微信 OAuth2 授权 | 1d | 🔴 |
|| IMWX-FE-001 | 企业微信配置页面 | 0.5d | 🔴 |
|| CD-BE-001 | Git 仓库关联 API | 1d | 🔴 |
|| CD-BE-002 | MR/PR 列表 API（按仓库 + 分支筛选） | 1d | 🔴 |
|| CD-BE-003 | 提交记录 API（commit log + diff） | 1d | 🔴 |
|| CD-BE-004 | 提交 ↔ 任务关联 API | 0.5d | 🔴 |
|| CD-FE-001 | 仓库关联页面 | 1d | 🔴 |
|| CD-FE-002 | MR/PR 列表页 | 1d | 🔴 |
|| CD-FE-003 | 提交历史页（按任务筛选） | 1d | 🔴 |

---

## 状态追踪

> 下表供开发过程中快速勾选。任务 ID 旁的 `[ ]` 可填入负责人姓名缩写。

### v1.0（2026-06-08 ~ 2026-06-20）

- [ ] **NF-*** 通知框架（13 个任务，共约 12d）
- [ ] **PLG-*** 插件系统框架（8 个任务，共约 8d）
- [ ] **OP-*** 开放平台框架（8 个任务，共约 6.5d）
- [ ] **V10-TEST-*** 集成测试（3 个任务，共约 2d）

### v1.1（2026-06-22 ~ 2026-07-04）

- [ ] **UIP-*** UI 插件化运行时（9 个任务，共约 9d）
- [ ] **SYS-*** 系统管理精简（2 个任务，共约 2d）
- [ ] 集成测试

### v1.2（2026-07-06 ~ 2026-07-18）

- [ ] **MTG-*** 会议模块（14 个任务，共约 13.5d）
- [ ] **REL-FE-*** 发布管理前端接入（4 个任务，共约 4.5d）

### v1.3（2026-07-20 ~ 2026-08-01）

- [ ] **WL-*** 工时管理（7 个任务，共约 7d）
- [ ] **WK-*** WakaTime（6 个任务，共约 6d）

### v1.4（2026-08-03 ~ 2026-08-15）

- [ ] **CI-*** CI/CD 集成（11 个任务，共约 10d）
- [ ] **PRF-*** 团队绩效（7 个任务，共约 5.5d）
- [ ] **RPT-*** 绩效报表（4 个任务，共约 4d）

### v1.5（2026-08-17 ~ 2026-08-28）

- [ ] **AI-*** AI 服务（9 个任务，共约 9d）
- [ ] **IMFS-*** 飞书集成（4 个任务，共约 3d）
- [ ] **IMDT-*** 钉钉集成（4 个任务，共约 3d）
- [ ] **IMWX-*** 企业微信集成（4 个任务，共约 3d）
- [ ] **CD-*** Git 代码管理（6 个任务，共约 5.5d）
