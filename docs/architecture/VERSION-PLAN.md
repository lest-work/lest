# LEST Platform — 版本规划（0.1 ~ 0.3 / 1.0 ~ 5.0）

> **文档目的**：明确 0.1、0.2 和 0.3 三个预发布版本，以及 1.0 ~ 5.0 正式版本的开发范围，基于当前已实现的功能和新的架构要求，制定可执行的发版计划。
>
> **版本命名规范**：
> - `0.x` 为预发布版本（Pre-release），从 0.1 开始，遵循语义化版本（semver）惯例，0.1.0 为首个可测试版本
> - `1.0` 为正式版起点，发布节奏为每 1.5 周一个小版本
> - 各正式大版本（1.0 ~ 5.0）均从 **小版本 0.1** 开始发布（对应小版本规划中的 `1.1`/`2.1` 等命名（去掉 v 前缀））
>
> **当前状态**：
> - 前端：`frontend-pc` 已完成从 `ele-admin-plus-ts` 到纯 Element Plus 的迁移，采用 Linear 风格深色侧边栏布局
> - 后端：`backend` Core 业务功能已实现，OA 功能（部门/岗位管理）已删除，新增缺失的 Domain 类和 Mapper
> - 文档：采用 Core-first 路线，V1.0/V2.0/V3.0 聚焦 Jira Core，插件 PRD/TASK 只保留 V4.0 入口，V5.0 企业商业能力按 Core 维护
>
> **版本**: 文档版本 2.5 | **状态**: Core-first 路线同步 | **最后更新**: 2026-06-03

---

## 1. 当前现状分析

### 1.1 已实现的功能（无需重做）

#### 后端 — 已实现（基于 RuoYi 框架改造）

| 服务 | 模块 | 状态 | 说明 |
|------|------|------|------|
| `lest-auth` | 登录/登出/Token | ✅ 完整 | JWT + Redis，支持刷新 |
| `lest-project` | Project CRUD | ✅ 完整 | 含归档/成员/迭代/里程碑 |
| `lest-task` | Task CRUD | ✅ 完整 | 含状态流转/分配/看板/甘特 |
| `lest-task` | 看板拖拽 | ✅ 完整 | 列内排序 + 跨列拖拽 |
| `lest-task` | 子任务/依赖 | ✅ 完整 | 含循环依赖检测 |
| `lest-task` | 评论/工时/Watch | ✅ 完整 | 含投票/自动化规则（CRUD）+ Watch 关注 |
| `lest-release` | 发布计划 | ✅ 完整 | 状态机 + 制品管理 |
| `lest-file` | 文件上传 | ✅ 完整 | MinIO + 本地双实现 |
| `lest-system` | 用户管理 | ✅ 完整 | RuoYi 标准 CRUD |

#### 前端 — 已实现（纯 Element Plus）

| 页面 | 状态 | 说明 |
|------|------|------|
| 登录页 | ✅ 完整 | Linear 风格 |
| 项目管理列表 | ✅ 完整 | ProjectList.vue，含新建/归档 |
| 项目详情 | ✅ 完整 | ProjectDetail.vue，含任务/看板/甘特/发布计划 Tab |
| 看板视图 | ✅ 完整 | Kanban.vue，vuedraggable 拖拽 |
| 任务列表 | ✅ 完整 | TaskList.vue，含多维筛选 + "我负责的" |
| 任务详情抽屉 | ✅ 完整 | TaskDetailDrawer.vue，完整 Tab 化，含草稿自动保存 |
| 甘特图 | ✅ 完整 | Gantt.vue，ECharts 渲染 |
| AI 服务 | ✅ 完整 | AiSettings.vue，Provider 配置 + AI 对话 |
| 会议 | ✅ 完整 | MeetingList.vue，会议列表页 |
| 开放平台 | ✅ 完整 | OpenApiSettings.vue，API Key + Webhook |
| 仪表盘 | ✅ 完整 | dashboard/index.vue |
| iteration.ts | ✅ 完整 | 迭代 CRUD + 开始/完成 |
| label.ts | ✅ 完整 | 标签 CRUD |
| release.ts | ✅ 完整 | 发布计划/制品/关联问题 CRUD |
| task.ts | ✅ 完整 | 全部后端 API（含子任务/评论/工时/依赖/代码关联/Watch）|
| dashboard.ts | ✅ 完整 | Dashboard members 接口 |
| ai.ts | ✅ 完整 | AI Provider 管理 + chat 对话 |
| file.ts | ✅ 完整 | 文件上传/删除 |
| job.ts | ✅ 完整 | 定时任务 CRUD（仅平台管理员）|
| meeting.ts | ✅ 完整 | 会议 CRUD + 参与人 + 纪要 |
| openapi.ts | ✅ 完整 | API Key + Webhook 管理 |
| webhook.ts | ✅ 完整 | Webhook 测试端点 |

---

## 2. 0.1 版本（预发布，2026-06-20）

> **目标**：完成前端架构迁移（纯 Element Plus），后端按 PRD 清理 OA 功能，核心业务功能稳定可用。

### 2.1 前端 0.1 任务

#### P0 — 必须完成（阻塞发版）

| 任务 | 工作内容 | 负责人 |
|------|---------|--------|
| 前端项目初始化 | 创建 `package.json`（无 ele-admin-plus）、`vite.config.ts`、`main.ts` | — |
| 新增 AppLayout | 侧边栏（项目列表）+ Header（搜索+通知+头像）| — |
| 新增登录页 | `views/login/index.vue`（el-form + JWT）| — |
| 新增看板视图 | `views/task/Kanban.vue`（vuedraggable + 列拖拽）| — |
| 新增任务列表 | `views/task/TaskList.vue`（el-table + 筛选）| — |
| 新增仪表盘 | `views/dashboard/index.vue`（统计卡片 + 项目列表）| — |
| API 层重构 | `api/auth.ts`、`api/task.ts`、`api/board.ts`、`api/project.ts` | — |
| 删除 OA 页面 | 删除 `views/system/`、`views/monitor/` 目录 | — |

#### P1 — 应当完成

| 任务 | 工作内容 |
|------|---------|
| 项目列表页 | `views/project/ProjectList.vue`（卡片布局）|
| 任务详情抽屉 | `views/task/TaskDetail.vue`（el-drawer + Tab）|
| 收件箱/通知 | `views/inbox/index.vue` |
| 个人设置页 | `views/settings/Profile.vue` |

### 2.2 后端 V0.1 任务

#### P0 — 必须完成

| 任务 | 工作内容 | 影响范围 | 状态 |
|------|---------|---------|------|
| 项目级权限校验 | lest-task/lest-project 所有 API 增加 `project_member` 表鉴权 | Core 业务 | ✅ 0.3 |
| 删除 OA Controller | 删除 `SysDeptController`、`SysPostController` | lest-system | ✅ 0.1 |
| OA 前端文件清理 | 删除 `views/system/`、`views/monitor/` 目录（已不存在）| 前端 | ✅ 0.1 |
| 后端编译错误清理 | 修复 gateway/task/system/file 模块的编译错误，补全缺失 Domain/Mapper | 后端 | ✅ 0.3 |

#### P1 — 应当完成

| 任务 | 工作内容 | 状态 |
|------|---------|------|
| @RequireProjectRole 注解 | 在 `lest-common-security` 中实现项目级角色 AOP 注解 | ✅ 0.3 |
| lest-notification 完善 | notification_db + Kafka 消费者 + WebSocket 推送 + Kafka 事件发布 | ✅ 0.3 |
| lest-ai 骨架 | ai_db + AI Provider 配置表 + 完整脚手架 | ✅ 0.3 |

---

## 3. 0.2 版本（预发布，2026-07-04）

> **目标**：后端 OA 功能全面清理，项目/任务/看板/详情/权限/通知的 Core 闭环补齐。插件服务不作为 0.2 交付目标。

### 3.1 后端 0.2 任务

#### P0 — 必须完成

| 任务 | 工作内容 | 状态 |
|------|---------|------|
| lest-system 大拆分 | 将 `lest-system` 拆分为 `lest-auth`（user 表）+ 极简 `lest-system`（仅保留通知配置和审计日志） | ✅ 0.3 |
| 删除 sys_menu/sys_role | 菜单管理、角色管理 Controller/Service/Mapper 全部删除 | ✅ 0.3 |
| lest-common-datascope 删除 | 删除 `@DataScope` 注解链路，替换为项目级 Permission Scheme | ✅ 0.3 |

#### P1 — 应当完成

| 任务 | 工作内容 | 状态 |
|------|---------|------|
| 通知中心真实接入 | 站内信列表、未读数、标记已读、WebSocket 推送 | ✅ 0.3 |
| 附件基础链路 | 任务附件上传、下载、删除、文件元数据（lest-file 已实现，前端 job.ts 待接入） | ⚠️ 待完成（后端✅） |
| 开放 API 基础 | API Key、Webhook 出站、基础限流（lest-openapi 后端已实现，前端 OpenApiSettings.vue ✅） | ✅ 0.3.11 |

### 3.2 前端 0.2 任务

| 任务 | 工作内容 | 状态 |
|------|---------|------|
| 任务详情抽屉完善 | 富文本描述（Tiptap）、评论、附件、子任务、依赖、工时、代码关联 | ✅ 0.3.6 |
| 甘特图实现 | 使用 ECharts 渲染甘特图 | ✅ 0.3.6 |
| 全局搜索（Cmd+K）| 命令面板组件 | ✅ 0.8 |
| 暗黑模式 | Element Plus 暗黑模式适配 | ✅ 0.8 |
| UI 系统性重构 | Linear/Jira 规范对齐 | ✅ 0.9 |
| 国际化 | 中文 + 英文 | ⚠️ 待完成 |

---

## 4. 0.3 版本（预发布最终版，gap 分析补充）

> **目标**：解决 0.1/0.2 实现与 PRD 需求之间的差距，完善数据模型和接口规范。0.3 发布后进入 1.0 正式版。

### 4.1 后端 0.3 任务

#### 已完成

| 任务 | 工作内容 | 完成日期 |
|------|---------|---------|
| API 路径前缀规范 | 确认所有 Controller 路径规范（无强制 /api/v1 前缀）| 2026-06-03 |
| 分页接口补充 | Project/Iteration/Milestone/Task 添加 `/page` 分页端点 | 2026-06-03 |
| sys_user 新增字段 | `is_platform_admin` 平台管理员标识 + DDL | 2026-06-03 |
| task 新增字段 | `task_no` 任务编号（格式 TASK-{id}）+ DDL | 2026-06-03 |
| lest-common-plugin 模块 | 插件机制核心（Plugin 接口 + PluginManager + AutoConfiguration）| 2026-06-03 |
| lest-common-mq 模块 | Kafka 消息封装（LestMessage + Topic 常量 + KafkaMessageSender）| 2026-06-03 |
| @RequireProjectRole 注解 | 项目级角色 AOP 校验（注解 + 切面 + ProjectRoleChecker 接口）| 2026-06-03 |
| ProjectMemberService | 实现 `getUserRoles` / `isMember`，支持 `@RequireProjectRole` | 2026-06-03 |
| @RequireProjectRole 应用 | TaskController / ProjectController / IterationController / MilestoneController / ReleasePlanController | 2026-06-03 |
| Service 层权限校验 | ProjectAuthHelper + ProjectRoleChecker 注入，支持无 @PathVariable projectId 的端点 | 2026-06-03 |
| 平台管理员绕过 | SecurityUtils.isPlatformAdmin()，平台管理员跳过所有项目权限校验 | 2026-06-03 |
| Watch 关注功能 | TaskWatcherMapper 新增方法 + ITaskService/TaskServiceImpl 接口 + TaskController 端点 | 2026-06-03 |
| lest-notification 完善 | Kafka 消费者（NotificationEventConsumer）+ WebSocket 推送完整实现 | 2026-06-03 |
| lest-ai 骨架 | pom.xml + Application + Controller + Service + Domain + Mapper 完整脚手架 | 2026-06-03 |
| lest-common-plugin 扩展 | ManifestParser（manifest.json 解析器）+ ExtensionPoint + ExtensionRegistry + ExtensionPoints 常量 | 2026-06-03 |
| lest-common-mq 完善 | @LestKafkaListener 注解 + KafkaConsumerBase 基类 + ConsumerFactory + ConcurrentKafkaListenerContainerFactory | 2026-06-03 |
| 前端甘特图完善 | ECharts GanttChart 渲染（自定义 renderItem + 类型色条 + 暗黑模式）| 2026-06-03 |
| 前端 API 补全 | 新建 dashboard/ai/file/job/webhook/meeting/openapi.ts，共 7 个 API 文件 | 2026-06-03 |
| plugin.ts 补全 | 补全 list()/install()/uninstall()/enable()/disable() 接口 | 2026-06-03 |
| 新建前端页面 | AiSettings.vue（Provider 配置 + AI 对话）、MeetingList.vue（会议列表）、OpenApiSettings.vue（API Key + Webhook）| 2026-06-03 |
| 新建前端路由 | /settings/ai、/settings/openapi、/meetings，侧边栏增加「会议」「AI 服务」入口 | 2026-06-03 |
| 后端权限修复 | 移除 SysJobController/SysJobLogController @RequiresPermissions，改为 isPlatformAdmin() 校验 | 2026-06-03 |
| Gateway 路由补全 | 添加 /dashboard → lest-system 和 /upload → lest-file 路由 | 2026-06-03 |
| 前端 Inbox 接入真实 API | notification.ts API 层 + Inbox.vue 替换 mock 数据为真实接口调用 | 2026-06-03 |

#### 待完成

| 任务 | 优先级 | 说明 |
|------|--------|------|
| 单元测试覆盖 | P2 | 从 Core API 开始写（后续迭代补充）|

---

## 5. 0.3 小版本记录（开发过程记录）

### 0.3.3（2026-06-03）

| 任务 | 说明 |
|------|------|
| Docker | docker-compose.yml 重写为 JAR 模式 + MySQL/Redis/Nacos/MinIO/Kafka 容器 |
| Docker | build-and-deploy.sh 重写为编译打包脚本 |
| Docker | bin/*.sh 所有脚本重写为 JAR 启动模式 |
| 后端编译 | lest-task Domain/Mapper 补全（IssueLink/Attachment/TaskVote/AutomationRule 等）|
| 后端编译 | lest-gateway RouterFunction 路由配置修复 |
| 后端编译 | lest-system StringUtils 冲突导入修复 |
| 后端编译 | lest-file SysFile 创建缺失类 |
| 后端编译 | AutoConfiguration.imports 清理已删除工厂引用 |
| OA 清理 | 数据表 DEPRECATED 注释（15 张 OA 表）|
| OA 清理 | DataScopeAspect ⚠️ DEPRECATED 注释 |

### 0.3.4（2026-06-03）

| 任务 | 说明 |
|------|------|
| API 路径统一 | 所有 Controller 统一为 /api/v1 前缀 + 资源名 |
| 前端 API 层对齐 | board.ts / auth.ts 修复 + 新增 iteration.ts / milestone.ts / label.ts |
| Gateway routes | 改为 /api/v1 直接转发，去除旧版 StripPrefix 模式 |

### 0.3.5（2026-06-03）

| 任务 | 说明 |
|------|------|
| @RequireProjectRole 全面应用 | TaskController / ProjectController / IterationController / MilestoneController / ReleasePlanController |
| ProjectRoleChecker 实现 | ProjectMemberServiceImpl 实现接口，消除切面注入 null 问题 |
| ProjectAuthHelper | Service 层权限校验辅助类，解决无 @PathVariable projectId 端点的校验 |
| SecurityUtils.isPlatformAdmin | 平台管理员跳过所有项目权限校验 |
| 前端看板 board store/API | 修复响应格式匹配后端 `List<BoardColumn>` 结构 |
| 前端看板 Kanban.vue | 修复列 key 匹配 `column.column`（status string） |
| 前端 iteration.ts API | 修复路径 `/api/v1/${projectId}/iteration/*` |
| 前端 label.ts API | 修复路径 `/api/v1/project/${projectId}/labels` |
| 前端甘特图 | 去除占位页，改为简单任务列表实现 |

### 0.3.6（2026-06-03）

| 任务 | 说明 |
|------|------|
| 任务详情抽屉完整 Tab 化 | TaskDetailDrawer.vue 完整 Tab 化（概览/子任务/评论/工时/依赖/代码关联），所有数据实时加载和操作 |
| 前端迭代详情页 | IterationDetail.vue，含迭代信息/状态/开始/完成操作 |
| 前端里程碑详情页 | MilestoneDetail.vue，含里程碑信息展示 |
| 前端发布计划页面 | ReleasePlan.vue，含发布计划 CRUD/发布/归档/构建/恢复 |
| 前端项目导航增强 | ProjectDetail.vue 新增发布计划 Tab |
| milestone.ts API 对齐 | 修复 API 路径匹配后端 `/{projectId}/milestone/*`，数据模型对齐 `milestoneId` / `targetDate` |

### 0.3.7（2026-06-03）

| 任务 | 说明 |
|------|------|
| Watch 关注功能后端 | TaskWatcherMapper 新增方法 + ITaskService/TaskServiceImpl 增删查接口 + TaskController 端点（POST/DELETE `/watch`、GET `/watched`）|
| Watch 关注功能前端 | taskApi.watch/unwatch/getWatched + TaskDetailDrawer 新增"关注"Tab，支持关注/取消关注 |
| Dashboard 状态值修复 | status badge 从 `done` 修正为 `completed`，匹配后端枚举 |
| My Tasks 按人筛选 | TaskFilter.assigneeId 改为 number 类型，TaskList.vue 新增"我负责的"按钮按当前用户筛选 |

### 0.3.8（2026-06-03）

| 任务 | 说明 |
|------|------|
| 任务自动保存草稿 | TaskDetailDrawer.vue 新增草稿机制：localStorage 按 taskId 存储草稿、500ms 防抖自动保存、点击标题/描述进入编辑模式、"草稿已保存" 指示器、任务保存后清除草稿 |
| 暗黑模式切换 | stores/theme.ts（useThemeStore）、styles/variables.css（CSS 变量）、AppLayout.vue 头部 Sun/Moon 按钮、main.ts/App.vue 初始化 |
| Cmd+K 全局搜索 | components/command/CommandPalette.vue（快速操作 + 任务搜索）、App.vue 全局快捷键监听、header 搜索框触发 |

### 0.3.9（2026-06-03）

| 任务 | 说明 |
|------|------|
| UI 系统性重构 | 全面对齐 Linear/Jira 规范，删除管理后台风格： |
| CSS Design System | styles/variables.css 重写：Inter/Noto Sans SC 字体、4px 间距系统、Linear 优先级/状态颜色、完整 light/dark CSS 变量 |
| AppLayout 重构 | 侧边栏层级可展开（点击项目展开 Issues/Board/Settings 子导航）、设置入口移至头像下拉菜单、TopBar 搜索按钮化、收件箱未读数角标 |
| TaskDetailDrawer 重构 | 去 Tab 化 → 双栏布局（左侧工作区 + 右侧 Notion 风格属性面板）、Teleport 弹层面板、全宽 88%、统一活动流（评论不独立 Tab）、关注按钮内联化 |
| Kanban 重构 | 卡片点击打开内联 TaskDetailDrawer（不再页面跳转）、Emoji 类型图标、Epic 色条、子任务/评论数、故事点进度条、悬停阴影规范 |
| TaskList 重构 | 去除 el-table → 自定义 Linear 风格列表行、筛选器改为单 `🔍 筛选` 下拉按钮 chip 化、去除提交按钮（响应式过滤）、空状态插画 |
| Dashboard 重构 | 统计数字加粗 + 柔和点状指示器、任务列表 Grid 化、项目卡片使用项目 color 变量、空状态 SVG 插画 |
| Login 重构 | 全屏深色极简风格、Radial 渐变背景、聚焦即开始、无验证码框、placeholder 为 you@company.com、Enter 快捷登录 |
| Project 接口 | Project 接口新增 color 字段（支持侧边栏项目图标多彩） |

---

## 6. 技术债务清理

### 6.1 前端技术债

| 债务 | 当前状态 | 清理方案 | 优先级 |
|------|---------|---------|--------|
| ele-admin-plus 依赖 | `package.json` 中 `ele-admin-plus` | 已移除 | ✅ 0.1 |
| 旧路由守卫 | 动态菜单加载 | 替换为静态路由 | ✅ 0.1 |
| Vuex store | `src/store/` | 已删除，使用 Pinia | ✅ 0.1 |
| 旧 permission 逻辑 | `utils/permission.ts` | 替换为 JWT 验证 | ✅ 0.1 |
| Tinymce 编辑器 | ele-admin 集成版 | 替换为 Tiptap | P1 |
| 暗黑模式 | 未实现 | Element Plus 暗黑模式 + CSS 变量 + 切换按钮 | ✅ 0.8 |
| Cmd+K 全局搜索 | 未实现 | CommandPalette.vue + 快速操作 + 任务搜索 | ✅ 0.8 |
| 任务自动保存草稿 | 未实现 | localStorage 草稿 + 防抖自动保存 | ✅ 0.8 |

### 6.2 后端技术债

| 债务 | 当前状态 | 清理方案 | 优先级 |
|------|---------|---------|--------|
| OA 表（sys_dept/sys_post）| 仍在 lest-system 中 | 删除 Controller + 添加 DEPRECATED 注释 | ✅ 0.1 |
| sys_menu/sys_role 权限 | 全局菜单权限 | 替换为 project_member 权限 | ⚠️ 待完成 |
| 数据权限 @DataScope | 基于部门的行级权限 | 删除（敏捷平台无部门）| ⚠️ 待完成 |
| Docker 部署脚本 | docker-compose.yml 引用不存在的目录 | 重写为 JAR 模式 | ✅ 0.3 |
| 无单元测试 | 0 个测试文件 | 从 Core API 开始写 | P2 |

---

## 7. 发版检查清单

### 0.1 发版前必须完成

```
前端:
  ☐ npm install -- 无 ele-admin-plus 依赖
  ☐ npm run dev -- 能启动
  ☐ 登录页 -- 可登录
  ☐ 看板视图 -- 拖拽正常
  ☐ 任务列表 -- 筛选/分页正常
  ☐ 项目列表 -- 能看到项目
  ☐ views/system/ -- 已删除
  ☐ views/monitor/ -- 已删除

后端:
  ☐ mvn clean package -- 编译通过
  ☐ lest-task -- 看板 API 正常
  ☐ lest-project -- 项目 CRUD 正常
  ☐ lest-auth -- 登录正常
  ☐ SysDeptController -- 已删除
  ☐ SysPostController -- 已删除
```

### 0.2 发版前必须完成

```
前端:
  ✅ 任务详情抽屉 -- 富文本编辑正常
  ✅ 甘特图 -- ECharts 渲染正常（0.3.17）
  ✅ Cmd+K 命令面板 -- 能搜索任务

后端:
  ✅ lest-system -- 仅保留通知+审计日志
  ✅ @RequireProjectRole -- 所有 Core API 已添加
  ✅ @DataScope 链路 -- 已删除
  ✅ lest-notification -- Kafka 消费者 + WebSocket 推送（0.3.17）
```

---

## 版本历史

| 版本 | 日期 | 修改内容 |
|------|------|---------|
| V1.0 | 2026-06-03 | 初始版本规划 |
| V1.1 | 2026-06-03 | 新增 V0.3 版本章节；补充 API 路径规范、分页接口、DDL Migration、新模块（plugin/mq）等已完成任务；更新 @RequireProjectRole 状态 |
| V1.2 | 2026-06-03 | 后端编译错误全面清理；OA 数据表添加 DEPRECATED 注释；前端 OA 页面清理确认 |
| V1.3 | 2026-06-03 | Docker 部署脚本全面修复；build-and-deploy.sh 重写；bin/*.sh 重写为 JAR 启动模式 |
| V1.4 | 2026-06-03 | API 路径全面统一；前端 API 全面对齐；Gateway routes 重写 |
| V1.5 | 2026-06-03 | @RequireProjectRole 全面应用；前后端数据模型对齐；缺失 API 补全；看板/甘特图修复 |
| V1.6 | 2026-06-03 | 任务详情抽屉完整 Tab 化；前端迭代/里程碑/发布计划详情页；ProjectDetail.vue 导航增强；milestone.ts API 对齐 |
| V1.7 | 2026-06-03 | Watch 关注功能完整实现；Dashboard 状态值修复；My Tasks 按人筛选 |
| V1.8 | 2026-06-03 | 任务自动保存草稿；暗黑模式切换；Cmd+K 全局搜索 |
| V1.9 | 2026-06-03 | UI 系统性重构：CSS Design System + AppLayout 侧边栏层级 + TaskDetailDrawer 双栏布局 + Kanban/TaskList 线性风格 + Dashboard 重构 + Login 极简风格 |
| V2.0 | 2026-06-03 | 全面暗黑模式修复（inbox/ProjectList/IterationDetail/ReleasePlan/MilestoneDetail/Gantt/Profile）；替换全部 admin 风格组件（el-card→自定义卡片、el-radio-group→pill tabs、el-tag→chip）；统一 Loading 状态（el-icon→自定义 pulse）；Activity Feed 合并字段变更历史+Commit；补充全局 focus-visible 状态；修复字体家族规范 |
| V2.1 | 2026-06-03 | Bug 修复：TableDataInfo rows→@JsonProperty("records")、ReleaseArtifact 字段映射（name→artifactName）、releaseApi.plan.update 含 releasePlanId、iteration startDate 规范化、Activity Feed 稳定 key、CommandPalette 索引映射、TaskDetailDrawer 全链路错误提示、Profile 保存按钮、dashboard 任务行点击打开抽屉 |
| V2.2 | 2026-06-03 | 若依遗产全面清理：删除 lest-common-datascope 模块（@DataScope 注解链路）、移除 dept_id 字段（DDL/sys_user 无此列）、删除 selectAllocatedList/selectUnallocatedList/selectUserRoleGroup/selectUserPostGroup/importUser 等死方法、更新 Maven 依赖清理、backend/README.md 更新 |
| V2.3 | 2026-06-03 | lest-system 架构拆分：ProfileController 从 lest-system 迁移至 lest-auth（消除 /auth 路径冲突）、删除 SysUserController 重复 /user/register 端点、RemoteUserService Feign 接口扩展（新增无 source header 重载方法）、@InnerAuth 注解应用于所有 Feign 端点、编译验证通过 |
| 0.3.11 | 2026-06-03 | 前端 API 补全（7 个新文件）；新建 AI/会议/开放平台前端页面；plugin.ts 补全；侧边栏导航；SysJobController 权限修复；Gateway 路由补全 |
| 0.3.17 | 2026-06-03 | lest-notification 完整实现（Kafka 消费者 + WebSocket）；lest-ai 完整脚手架；lest-common-plugin 扩展（ManifestParser + ExtensionPoint + ExtensionRegistry + ExtensionPoints）；lest-common-mq 完善（@LestKafkaListener + KafkaConsumerBase）；前端 ECharts 甘特图完善；前端 Inbox 接入真实 API |
| V2.4 | 2026-06-03 | 0.3 收尾：@RequireProjectRoleAspect 完整实现（成员检查 + 角色校验 + 平台管理员绕过）；TaskServiceImpl Kafka 事件发布（assign/status/comment）；前端 notificationWs.ts WebSocket 服务；AppLayout 集成 WebSocket；前端插件基础设施（types/store/API/composable/TaskDetailDrawer 扩展点）；Gateway lest-release 路由；删除 lest-common-datascope；backend/README.md 模块结构修正；代码规范修复（System.out→SLF4J log） |
