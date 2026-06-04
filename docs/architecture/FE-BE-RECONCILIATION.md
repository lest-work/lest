# LEST Platform 0.3 前后端架构核对报告

> **文档目的**：逐项核对前端 `frontend-pc` 与后端 `backend` 在 V0.3 版本中的接口和功能一致性，识别缺失、路径不匹配、功能缺口。
>
> **核对日期**: 2026-06-03
> **版本**: 0.3.11（补全缺失 API 和前端页面）
>
> **0.3.9 重大变更**：2026-06-03 移除所有 `/api/v1` 前缀，统一为 Ruoyi 风格（无版本号前缀）。
>
> ### 0.3.10 修改摘要（插件矩阵架构完善）
> - **TokenController**：`@RequestMapping("/api/v1/auth")` → `@RequestMapping("/auth")`
> - **Gateway**：所有路由 Path 从 `/api/v1/...` 改为 `/...`
> - **前端 API**：所有 `src/api/*.ts` 文件去掉 `/api/v1` 前缀，路径完全匹配后端 Controller
> - **IterationController**：修复所有方法级路径（去掉冗余的 `/iteration/` 片段）
> - **MilestoneController**：修复所有方法级路径（去掉冗余的 `/milestone/` 片段）

> ### 0.3.11 修改摘要（补全缺失 API 和前端页面）
> - **新建前端 API 文件**：`dashboard.ts`、`ai.ts`、`file.ts`、`job.ts`、`webhook.ts`、`meeting.ts`、`openapi.ts`
> - **plugin.ts**：补全 `list()`、`install()`、`uninstall()`、`enable()`、`disable()` 接口
> - **新建前端页面**：`views/settings/AiSettings.vue`、`views/meeting/MeetingList.vue`、`views/settings/OpenApiSettings.vue`
> - **路由**：新增 `/settings/ai`、`/settings/openapi`、`/meetings` 路由，导航栏增加「会议」「AI 服务」入口
> - **后端权限**：移除 `SysJobController`/`SysJobLogController` 中的 `@RequiresPermissions`，改为 `SecurityUtils.isPlatformAdmin()` 校验
> - **Gateway**：补全 `/dashboard` → `lest-system` 和 `/upload` → `lest-file` 路由
> - **LabelController**：从 `@RequestMapping("/label")` 改为 `@RequestMapping("/project/{projectId}/label")`
> - **Gateway 路由**：添加 `/project/{projectId}/label/**` 路由到 lest-task (8083)，且放在 `/project/**` 之前
> - **Vite Proxy**：从单一 `/api` 改为每个服务独立路由到 gateway (8080)
> - **P2 Fix**：lest-notification、lest-meeting、lest-openapi 添加 `lest-common-security` 依赖
>
> ### P0 修复
> - `AppLayout.vue`: Sidebar 项目移除文字标签（仅保留图标），移除 Projects 分类标题，搜索按钮移除文字（仅图标），移除 TopBar + 新建按钮，移除项目子导航 Board/List/Settings
> - `Kanban.vue`: 卡片移除评论数、子任务进度、列内"添加任务"按钮、故事点条
> - `TaskDetailDrawer.vue`: 面板宽度改为 100%（移除 max-width:1100px），移除遮罩层背景
> - `TaskList.vue`: StatusChip 替换为纯色圆点，简化列布局（移除迭代/截止日期/箭头列），添加活跃筛选内联药丸芯片
> - `dashboard/index.vue`: 移除欢迎语和项目网格，改为 Jira 风格（统计条 + My Issues 主要区域 + Recently Updated 次要区域）
>
> ### P1 修复
> - `ProjectDetail.vue`: Tab 样式从药丸按钮改为下划线指示器
> - `axios.ts`: 修复响应拦截器 return type 错误（return response → return response.data 混用）
> - `main.ts`: 移除无法解析的 `element-plus/theme-chalk/dark/css-variable` 导入
>
> **0.3.5 重大变更**：2026-06-03 修复 API 双重 /api 前缀（baseURL 改为 ''）、project/task list 路径加 /list、修复 /settings/profile 路由在 AppLayout 外、ProjectList 新建项目改为内联弹窗、TaskList /my-tasks 自动筛选 assigneeId、Profile 页面添加 fetchUserInfo 初始化、统一 LoadingSpinner 组件（Gantt/MilestoneDetail）、修复硬编码 #6366F1 → CSS 变量。
>
> **0.3.4 重大变更**：2026-06-03 设计系统组件化。提取 8 个公共组件（StatusChip/TypeIcon/PriorityDot/EmptyState/LoadingSpinner/PageHeader/AssigneeAvatar + barrel export）、全局注册到 main.ts、Element Plus :root CSS 变量覆盖 light 主题、看板评论 emoji→SVG、全部视图重构使用新组件。
>
> **0.3.3 重大变更**：2026-06-03 修复前后端 Bug：TableDataInfo.rows → @JsonProperty("records")、ReleaseArtifact 字段映射、releaseApi.plan.update() 含 releasePlanId、iteration startDate 规范化、Activity Feed Math.random() key、CommandPalette 索引映射、TaskDetailDrawer 错误提示、Profile 保存按钮等。
>
> **0.3.5 重大变更（重复标记，忽略）**：前后端数据模型字段名对齐（`id`↔`taskId`/`projectId`/`iterationId`）、状态值对齐（`done`↔`completed`）、新增缺失 API（子任务/评论/工时/依赖/release）、迭代 API 路径修复。
>
> **0.3.7 重大变更**：2026-06-03 完成全面 UI 一致性修复。暗黑模式、组件统一、Loading、Activity Feed、focus-visible、5个页面暗黑模式修复、全部 el-tag/el-card/el-radio-group 等 admin 风格组件替换、自定义 loading pulse 统一、Activity Feed 合并评论+Commit+字段变更历史、focus-visible 状态补充、字体家族规范统一。
>
> **0.3.4 重大变更（重复标记，忽略）**：2026-06-03 完成 API 路径全面统一。

> ### 0.3.10 修改摘要（本次）
> - **权限体系**：`@RequireProjectRoleAspect` 完整实现（成员检查 + 角色校验 + 平台管理员绕过）；新增 `SecurityUtils.isPlatformAdmin()`
> - **通知系统**：`TaskServiceImpl` 新增 Kafka 事件发布（assign/status/comment）；`lest-task/pom.xml` 新增 `lest-mq` 依赖
> - **前端 WebSocket**：`notificationWs.ts` 客户端；`AppLayout.vue` 集成连接和未读数实时更新
> - **前端插件基础设施**：`plugins/types.ts` + `stores/plugin.ts` + `api/plugin.ts` + `composables/usePlugin.ts`；`TaskDetailDrawer.vue` 集成 `task.detail.tab` 扩展点槽位
> - **Gateway 路由**：移除废弃 `lest-monitor`；新增 `lest-release`（8087）
> - **代码规范**：`RyTask/BeanUtils/LogAspect` System.out/printStackTrace → SLF4J
> - **死代码清理**：删除 `lest-common-datascope` 模块
> - **文档更新**：`backend/README.md` + `VERSION-PLAN.md` + `V1.0-小版本规划.md` 状态对齐

---

|---------|------|
| 前端 task.ts 缺少子任务/评论/工时/依赖 API | 新增 subtaskList / createSubtask / commentList / addComment / worklogList / addWorklog / dependencyList / addDependency / commitList / mergeRequestList 等 | ✅ 已修复 |
| 前端无 release.ts | 新增 `/api/release.ts`，覆盖发布计划/制品/关联问题 CRUD | ✅ 已修复 |
| boardApi 响应格式与后端不匹配 | board store 响应格式改为 `BoardColumn[]`（`column.column`=status），Kanban.vue 修复列 key | ✅ 已修复 |
| iteration.ts API 路径错误 | 修复为 `/api/v1/${projectId}/iteration/*` | ✅ 已修复 |
| Project 接口字段与后端不对齐 | Project 接口增加 `projectId`/`template` 字段，移除 `key`/`type` | ✅ 已修复 |
| Task 接口字段与后端不对齐 | Task 接口支持 `taskId`/`taskType`，normalizeTask 处理 | ✅ 已修复 |
| TaskDetailDrawer 状态选项 `review/done` | 修正为 `todo/in_progress/completed` | ✅ 已修复 |
| TaskList 状态筛选 `review/done` | 修正为 `todo/in_progress/completed` | ✅ 已修复 |
| Dashboard 统计 `done` 状态 | 修正为 `completed` | ✅ 已修复 |
| ProjectList `project.key` / `project.type` 不存在 | 改为 `project.template` 和项目名前缀 | ✅ 已修复 |
| AppLayout `userInfo.roles` 不存在 | UserInfo 接口增加 `roles` 和 `platformAdmin` | ✅ 已修复 |
| TaskList store 无字段规范化 | 新增 normalizeTask / normalizeProject / normalizeIteration | ✅ 已修复 |

---

## 一、整体架构总览

### 1.1 后端模块（已实现的 API 服务）

| 服务 | 端口 | Gateway 路由 | Context Path | 说明 |
|------|------|-------------|-------------|------|
| `lest-gateway` | 8080 | — | — | 网关，直接转发 /api/v1/** |
| `lest-auth` | 8096 | `/api/v1/auth/**` | 无 | 认证/Token |
| `lest-system` | 8081 | `/api/v1/user/**`、`/api/v1/dashboard/**` | 无 | 用户/个人信息/仪表盘 |
| `lest-project` | 8082 | `/api/v1/project/**` | 无 | 项目/迭代/里程碑 |
| `lest-task` | 8083 | `/api/v1/task/**`、`/api/v1/webhook/**` | 无 | 任务 CRUD + 看板 + Webhook |
| `lest-file` | 8091 | `/api/v1/upload/**` | 无 | 文件上传 |
| `lest-release` | 8087 | `/api/v1/release/**` | 无 | 发布计划 |
| `lest-job` | 9203 | `/api/v1/job/**` | 无 | 定时任务 |

**骨架模块**（无 Controller，仅 SpringBootApplication）：
- `lest-notification`、`lest-ai`、`lest-meeting`、`lest-open`、`lest-plugin`、`lest-performance`、`lest-wakapi`

### 1.2 前端模块（`frontend-pc`，纯 Element Plus）

| 层级 | 文件 | 状态 |
|------|------|------|
| 入口 | `main.ts` | ✅ 纯 Element Plus + Pinia + Vue Router |
| 路由 | `router/index.ts` + `routes.ts` | ✅ 静态路由，JWT 鉴权 |
| API | `api/axios.ts` | ✅ baseURL: `/api` |
| API | `api/auth.ts` | ✅ 认证接口 |
| API | `api/project.ts` | ✅ 项目接口 |
| API | `api/task.ts` | ✅ 任务接口 |
| API | `api/board.ts` | ✅ 看板接口 |
| Store | `stores/user.ts` | ✅ |
| Store | `stores/project.ts` | ✅ |
| Store | `stores/board.ts` | ✅ |
| Store | `stores/task.ts` | ❌ **缺失** |
| View | `login/index.vue` | ✅ |
| View | `dashboard/index.vue` | ✅ |
| View | `inbox/index.vue` | ⚠️ 假数据，未对接 API |
| View | `project/ProjectList.vue` | ✅ |
| View | `project/ProjectDetail.vue` | ⚠️ 简单中转 |
| View | `task/TaskList.vue` | ✅ |
| View | `task/Kanban.vue` | ✅ |
| View | `task/Gantt.vue` | ⚠️ 占位页 |
| View | `settings/Profile.vue` | ⚠️ 表单未提交 |
| Component | `layout/AppLayout.vue` | ✅ |
| 旧系统 | `views/system/`（38 文件）| ❌ V0.1 需删除 |
| 旧系统 | `views/monitor/`（17 文件）| ❌ V0.1 需删除 |
| 旧系统 | `layout/index.vue` | ❌ V0.1 需删除 |

---

## 二、API 路径逐项核对

### 2.1 认证模块（`api/auth.ts` vs `lest-auth`）

> ✅ V1.4 已修复：`SysProfileController` 移至 `/api/v1/auth`，前端已对齐。

| 前端函数 | HTTP | 前端路径 | 后端路径 | 状态 |
|---------|------|---------|---------|------|
| `authApi.login()` | POST | `/api/v1/auth/login` | `/api/v1/auth/login` | ✅ |
| `authApi.logout()` | DELETE | `/api/v1/auth/logout` | `/api/v1/auth/logout` | ✅ |
| `authApi.refreshToken()` | POST | `/api/v1/auth/refresh` | `/api/v1/auth/refresh` | ✅ |
| `authApi.getUserInfo()` | GET | `/api/v1/auth/profile` | `/api/v1/auth/profile` | ✅ 已修复 |
| `authApi.updateProfile()` | PUT | `/api/v1/auth/profile` | `/api/v1/auth/profile` | ✅ 已修复 |
| `authApi.updatePassword()` | PUT | `/api/v1/auth/password` | `/api/v1/auth/password` | ✅ |

---

### 2.2 项目模块（`api/project.ts` vs `lest-project`）

> ✅ V1.4 已修复：ProjectController 移至 `/api/v1/project`，成员路径改为 `/members`。

| 前端函数 | HTTP | 前端路径 | 后端路径 | 状态 |
|---------|------|---------|---------|------|
| `projectApi.list()` | GET | `/api/v1/project` | `/api/v1/project/page` | ✅ |
| `projectApi.getById()` | GET | `/api/v1/project/${id}` | `/api/v1/project/${id}` | ✅ |
| `projectApi.members()` | GET | `/api/v1/project/${id}/members` | `/api/v1/project/${id}/members` | ✅ 已修复 |
| `projectApi.addMember()` | POST | `/api/v1/project/${id}/members` | `/api/v1/project/${id}/members` | ✅ 已修复 |
| `projectApi.removeMember()` | DELETE | `/api/v1/project/${id}/members/${userId}` | `/api/v1/project/${id}/members/${userId}` | ✅ 已修复 |

---
| `projectApi.getById()` | GET | `/v1/project/${id}` | `GET /{id}` | ⚠️ **路径不匹配** |
| `projectApi.create()` | POST | `/v1/project` | `POST /` | ⚠️ **路径不匹配** |
| `projectApi.update()` | PUT | `/v1/project/${id}` | `PUT /` | ⚠️ **路径不匹配** |
| `projectApi.delete()` | DELETE | `/v1/project/${id}` | `DELETE /{id}` | ✅ |
| `projectApi.archive()` | PUT | `/v1/project/${id}/archive` | `PUT /{id}/archive` | ✅ |
| `projectApi.members()` | GET | `/v1/project/${id}/members` | `GET /{id}/member/list` | ⚠️ **路径不匹配** |
| `projectApi.addMember()` | POST | `/v1/project/${id}/members` | `POST /{id}/member` | ⚠️ **路径不匹配** |
| `projectApi.removeMember()` | DELETE | `/v1/project/${id}/members/${userId}` | `DELETE /{id}/member/{userId}` | ✅ |

#### 问题 2：`project.ts` 所有 CRUD 路径均缺少 `/project` 前缀

后端 `ProjectController` 使用 `@RequestMapping("")`（空字符串），意味着所有路径直接拼接在 Gateway 的 `/project/**` 之后：

- 后端列表：`GET /project/list`（不是 `/project/`）
- 后端详情：`GET /project/{id}`
- 前端列表：`GET /v1/project`（期望 `GET /v1/project/list`）

#### 修正方案（修正 `api/project.ts`）：

```typescript
export const projectApi = {
  list(params?: { page?: number; size?: number; status?: string }) {
    return request.get('/v1/project/list', { params });  // 修正
  },
  getById(id: number) {
    return request.get(`/v1/project/${id}`);  // ✅ 正确
  },
  create(data: Partial<Project>) {
    return request.post('/v1/project', data);  // ✅ 正确
  },
  update(id: number, data: Partial<Project>) {
    return request.put('/v1/project', data);  // 修正（无 ID 在路径）
  },
  members(projectId: number) {
    return request.get(`/v1/project/${projectId}/member/list`);  // 修正
  },
  addMember(projectId: number, data: { userId: number; role: string }) {
    return request.post(`/v1/project/${projectId}/member`, data);  // 修正
  },
  // ...
};
```

**注意**：`update(id, data)` 中，后端 `PUT /` 路径没有 ID 参数，ID 在请求体中。前端应改为 `request.put('/v1/project', { ...data, id })`。

#### 问题 3：`archive` 和 `unarchive` 路径不匹配

- 前端：`PUT /v1/project/${id}/archive` → 正确
- 后端实际（见完整 Controller）：`PUT /{id}/archive` → ✅ 一致

但前端 `api/project.ts` **没有定义 `unarchive`**，而后端有这个接口。

---

### 2.3 任务模块（`api/task.ts` vs `lest-task`）

#### 前端定义（`api/task.ts`）

| 前端函数 | HTTP | 前端路径 | 后端实际路径 | 状态 |
|---------|------|---------|-------------|------|
| `taskApi.list()` | GET | `/v1/task` | `GET /list` | ⚠️ **路径不匹配** |
| `taskApi.getById()` | GET | `/v1/task/${id}` | `GET /{id}` | ⚠️ **路径不匹配** |
| `taskApi.create()` | POST | `/v1/task` | `POST /` | ⚠️ **路径不匹配** |
| `taskApi.update()` | PUT | `/v1/task/${id}` | `PUT /` | ⚠️ **路径不匹配** |
| `taskApi.delete()` | DELETE | `/v1/task/${id}` | `DELETE /{id}` | ✅ |
| `taskApi.updateStatus()` | PUT | `/v1/task/${id}/status` | `PUT /{id}/status` | ✅ |
| `taskApi.assign()` | PUT | `/v1/task/${id}/assign` | `PUT /{id}/assign` | ✅ |
| `taskApi.move()` | PUT | `/v1/task/${id}/move` | `PUT /{id}/move` | ✅ |
| `taskApi.batchMove()` | PUT | `/v1/task/batch/move-to-iteration` | `PUT /batch/move-to-iteration` | ✅ |

#### 问题 4：任务 CRUD 路径同样缺少 `/task` 前缀

修正方案（修正 `api/task.ts`）：

```typescript
export const taskApi = {
  list(params) {
    return request.get('/v1/task/list', { params });  // 修正
  },
  getById(id: number) {
    return request.get(`/v1/task/${id}`);  // ✅ 正确
  },
  create(data: Partial<Task>) {
    return request.post('/v1/task', data);  // ✅ 正确
  },
  update(id: number, data: Partial<Task>) {
    return request.put('/v1/task', { ...data, id });  // 修正（无 ID 在路径）
  },
  // ...
};
```

#### 问题 5：后端大量功能前端未暴露

后端 `TaskController` 有以下接口，但前端 `api/task.ts` **完全没有对接**：

| 后端路径 | 功能 | 前端状态 |
|---------|------|---------|
| `GET /{id}/subtask/list` | 子任务列表 | ❌ 未对接 |
| `POST /{id}/subtask` | 创建子任务 | ❌ 未对接 |
| `GET /{id}/comment/list` | 评论列表 | ❌ 未对接 |
| `POST /{id}/comment` | 添加评论 | ❌ 未对接 |
| `DELETE /{id}/comment/{commentId}` | 删除评论 | ❌ 未对接 |
| `GET /{id}/worklog/list` | 工时列表 | ❌ 未对接 |
| `POST /{id}/worklog` | 记录工时 | ❌ 未对接 |
| `GET /{id}/commit/list` | Git 提交列表 | ❌ 未对接 |
| `GET /{id}/merge-request/list` | MR 列表 | ❌ 未对接 |
| `PUT /{id}/estimate` | 估算工时 | ❌ 未对接 |
| `PUT /{id}/storypoints` | 故事点 | ❌ 未对接 |
| `GET /{id}/dependency/list` | 依赖列表 | ❌ 未对接 |
| `POST /{id}/dependency` | 添加依赖 | ❌ 未对接 |
| `POST /{id}/claim` | 领取任务 | ❌ 未对接 |
| `POST /{id}/commit` | 手动关联提交 | ❌ 未对接 |
| `POST /{id}/merge-request` | 手动关联 MR | ❌ 未对接 |

这些是 **V0.2 功能**，但后端已实现，前端应在 V0.2 完成对接。

#### 问题 6：迭代和里程碑接口完全缺失

后端有 `IterationController` 和 `MilestoneController`，但前端：

- **没有** `api/iteration.ts`
- **没有** `api/milestone.ts`
- 迭代和里程碑的功能完全未暴露

这影响任务列表中"迭代"列的筛选和甘特图的数据来源。

#### 问题 7：Label 接口缺失

后端 `LabelController` 有 `GET /project/{projectId}/label/list` 和 `POST /project/{projectId}/label`，前端完全没有。

---

### 2.4 看板模块（`api/board.ts` vs `lest-task`）

#### 前端定义（`api/board.ts`）

| 前端函数 | HTTP | 前端路径 | 后端实际路径 | 状态 |
|---------|------|---------|-------------|------|
| `boardApi.getBoard()` | GET | `/v1/board/${projectId}` | `GET /board`（query 参数 `projectId`）| ⚠️ **路径不匹配** |
| `boardApi.moveCard()` | PUT | `/v1/board/move` | `PUT /{id}/move`（路径参数）| ⚠️ **根本不对应** |

#### 问题 8：看板 API 设计与后端完全不符

- **后端**：`GET /board?projectId=xxx` → 返回按 status 分组的看板数据（列 + 卡片）
- **前端**：`GET /v1/board/${projectId}` → 期望 RESTful 风格路径参数

更严重的是：

- **后端移动卡片**：`PUT /{id}/move`（通过 task API 的 `/{id}/move`，不是独立的 board API）
- **前端移动卡片**：`PUT /v1/board/move`（独立的 board API，根本不存在）

#### 修正方案：

**方案 A**（推荐）：前端直接用 `taskApi.move()` 来移动卡片，`boardApi.getBoard()` 改路径：

```typescript
// api/board.ts
export const boardApi = {
  getBoard(projectId: number) {
    return request.get<any, { code: number; data: BoardData }>('/v1/task/board', {
      params: { projectId },
    });
  },
  // moveCard 不需要，taskApi.move() 已经处理了
};
```

**方案 B**：修改后端，增加独立的 board Controller。但 V0.1 不应大动后端，方案 A 更合适。

---

### 2.5 发布计划模块

| 后端路径 | 功能 | 前端状态 |
|---------|------|---------|
| `GET /release/plan/list` | 发布列表 | ❌ 未对接 |
| `GET /release/plan/{id}` | 发布详情 | ❌ 未对接 |
| `POST /release/plan` | 创建发布 | ❌ 未对接 |
| `POST /release/plan/{id}/publish` | 发布 | ❌ 未对接 |
| `POST /release/artifact` | 制品管理 | ❌ 未对接 |
| `POST /release/issue` | 关联任务 | ❌ 未对接 |

前端 `api/release/index.ts` **文件不存在**（探索时读取失败）。

这是 V0.2 功能，V0.1 优先级低，但需要确保 `api/release/index.ts` 骨架存在。

---

### 2.6 系统模块（通知/字典/配置）

| 后端路径 | 功能 | 前端状态 |
|---------|------|---------|
| `GET /notice/list` | 通知列表 | ❌ 未对接 |
| `POST /notice/markRead` | 标记已读 | ❌ 未对接 |
| `GET /dict/data/type/{dictType}` | 字典查询 | ❌ 未对接 |

`inbox/index.vue` 使用假数据，这些是 V0.2 功能，但骨架应提前建立。

---

## 三、功能完整性核对

### 3.1 V0.1 核心功能现状

| 功能 | 前端实现 | 后端实现 | 接口核对 | 状态 |
|------|---------|---------|---------|------|
| 登录 | ✅ `login/index.vue` | ✅ `TokenController` | ⚠️ 路径一致 | **可工作，但需验证** |
| 退出 | ✅ `userStore.logout()` | ✅ `DELETE /logout` | ✅ 一致 | 可工作 |
| 用户信息 | ✅ `userStore.fetchUserInfo()` | ⚠️ 后端在 lest-system | ⚠️ **路径需修正** | 需修复 |
| 项目列表 | ✅ `ProjectList.vue` | ✅ `GET /list` | ⚠️ **路径需修正** | 需修复 |
| 项目详情 | ✅ `ProjectDetail.vue` | ✅ `GET /{id}` | ⚠️ **路径需修正** | 需修复 |
| 项目归档 | ✅ `archive()` | ✅ `PUT /{id}/archive` | ✅ 一致 | 可工作 |
| 项目成员 | ⚠️ `members()` 路径错 | ✅ `GET /{id}/member/list` | ⚠️ **路径需修正** | 需修复 |
| 任务列表 | ✅ `TaskList.vue` | ✅ `GET /list` | ⚠️ **路径需修正** | 需修复 |
| 任务详情 | ✅ `TaskList.vue` 点击跳转 | ✅ `GET /{id}` | ⚠️ **路径需修正** | 需修复 |
| 任务创建 | ✅ `TaskList.vue` 创建按钮 | ✅ `POST /` | ⚠️ **路径需修正** | 需修复 |
| 任务状态更新 | ✅ `updateStatus()` | ✅ `PUT /{id}/status` | ✅ 一致 | 可工作 |
| 任务分配 | ✅ `assign()` | ✅ `PUT /{id}/assign` | ✅ 一致 | 可工作 |
| 看板数据 | ⚠️ 路径错 | ✅ `GET /board` | ⚠️ **路径需修正** | 需修复 |
| 看板拖拽 | ⚠️ API 错误 | ✅ `PUT /{id}/move` | ⚠️ **根本不对应** | 需修复 |
| 仪表盘 | ✅ `dashboard/index.vue` | ❌ 无专用 Dashboard API | — | 需实现（V0.2）|
| 甘特图 | ⚠️ 占位页 | ✅ `GET /gantt` | — | 需实现（V0.2）|
| 收件箱 | ⚠️ 假数据 | ✅ `SysNoticeController` | — | 需对接（V0.2）|
| 个人设置 | ⚠️ 表单未提交 | ✅ `PUT /user/profile` | ⚠️ **路径需修正** | 需修复 |

### 3.2 V0.1 技术债清理现状

| 清理项 | 状态 | 说明 |
|-------|------|------|
| 删除 `views/system/` | ❌ 未删除（38 文件仍在）| **P0，需删除** |
| 删除 `views/monitor/` | ❌ 未删除（17 文件仍在）| **P0，需删除** |
| 删除旧 `layout/index.vue` | ❌ 未删除 | **P0，需删除** |
| 删除 `src/store/`（Vuex 旧系统）| ❌ 未删除 | **P0，需删除** |
| 创建 `stores/task.ts` | ❌ 缺失 | **P0，需创建** |
| `ele-admin-plus` 移除 | ✅ `package.json` 已无 | 完成 |
| OA 表清理（sys_dept/sys_post）| ❌ 未清理 | V0.1 需添加 DEPRECATED 注释 |

### 3.3 后端 V0.1 技术债清理现状

| 清理项 | 状态 | 说明 |
|-------|------|------|
| `SysDeptController` 删除 | ❌ 仍在 | **P0，需删除** |
| `SysPostController` 删除 | ❌ 仍在 | **P0，需删除** |
| `SysMenuController` 删除 | ❌ 仍在 | V0.2 |
| `SysRoleController` 删除 | ❌ 仍在 | V0.2 |
| OA 表 DEPRECATED 注释 | ❌ 未添加 | V0.1 |
| `@RequireProjectRole` 实现 | ❌ 不存在 | V0.1 P1 |
| `@DataScope` 链路删除 | ❌ 仍在 | V0.2 |
| 项目级权限校验 | ❌ 无 | V0.1 P1 |

---

## 四、优先级排序与修复计划

### 🔴 P0 — V0.1 阻塞性问题（必须修复才能运行）

| # | 问题 | 修复位置 | 修复方案 |
|---|------|---------|---------|
| P0-1 | `stores/task.ts` 缺失 | 创建 `src/stores/task.ts` | 实现 `useTaskStore`，包含 `fetchTasks`、`updateTask`、`pagination` |
| P0-2 | `getUserInfo` 路径错误 | `api/auth.ts` | 改为 `/v1/system/user/profile`，或 lest-auth 新增 `/userinfo` |
| P0-3 | `updateProfile` 路径错误 | `api/auth.ts` | 改为 `/v1/system/user/profile` |
| P0-4 | `projectApi.list` 路径缺少 `/list` | `api/project.ts` | 改为 `'/v1/project/list'` |
| P0-5 | `projectApi.update` 路径错误 | `api/project.ts` | 改为 `PUT '/v1/project'` + body 带 id |
| P0-6 | `projectApi.members/addMember` 路径错误 | `api/project.ts` | 改为 `/v1/project/${id}/member/list` 和 `/member` |
| P0-7 | `taskApi.list` 路径缺少 `/list` | `api/task.ts` | 改为 `'/v1/task/list'` |
| P0-8 | `taskApi.update` 路径错误 | `api/task.ts` | 改为 `PUT '/v1/task'` + body 带 id |
| P0-9 | `boardApi.getBoard` 路径错误 | `api/board.ts` | 改为 `/v1/task/board?projectId=` |
| P0-10 | `boardApi.moveCard` API 不存在 | `api/board.ts` | 删除，直接使用 `taskApi.move()` |
| P0-11 | `views/system/` 未删除 | 文件系统 | 删除 38 个文件 |
| P0-12 | `views/monitor/` 未删除 | 文件系统 | 删除 17 个文件 |
| P0-13 | 旧 `layout/index.vue` 未删除 | 文件系统 | 删除旧布局 |
| P0-14 | 旧 `src/store/` 未删除 | 文件系统 | 删除旧 Vuex store |
| P0-15 | `SysDeptController` 未删除 | 后端 `lest-system` | V0.1 删除 |
| P0-16 | `SysPostController` 未删除 | 后端 `lest-system` | V0.1 删除 |

### 🟡 P1 — V0.1 重要但不阻塞

| # | 问题 | 建议 |
|---|------|------|
| P1-1 | `unarchive` API 未定义 | `projectApi` 增加 `unarchive()` |
| P1-2 | 迭代/里程碑 API 未对接 | V0.1 P1 完成 `api/iteration.ts` |
| P1-3 | Label API 未对接 | V0.1 P1 完成 `api/label.ts` |
| P1-4 | 项目详情页过于简单 | `ProjectDetail.vue` 需丰富 |
| P1-5 | `updateMemberRole` 未对接 | `projectApi` 增加 `updateMemberRole()` |
| P1-6 | 后端无项目级权限校验 | V0.1 P1 实现 `@RequireProjectRole` |

### 🟢 P2 — V0.2 功能（可延期）

| 功能 | 说明 |
|------|------|
| 甘特图实现 | `views/task/Gantt.vue` 当前为占位页 |
| 仪表盘专用 API | 后端需实现 `SysDashboardController` 或专用 Dashboard 服务 |
| 收件箱 API 对接 | `inbox/index.vue` 当前为假数据 |
| 个人设置表单提交 | `settings/Profile.vue` 表单未提交 |
| 子任务/评论/工时 API | 后端已实现，前端需对接 |
| 发布计划 API | `api/release/index.ts` 需创建 |
| 任务详情抽屉 | `TaskDetailDrawer.vue` 需实现 |
| 迭代管理 UI | 需新增迭代管理页面 |

---

## 五、后端功能 vs 前端能力矩阵

### 5.1 已实现但前端未用

这些后端功能已完成，前端 V0.2 需对接：

| 模块 | 功能 | 后端路径 | 前端状态 |
|------|------|---------|---------|
| Task | 子任务 CRUD | `/{id}/subtask` | ❌ 未对接 |
| Task | 评论 CRUD | `/{id}/comment` | ❌ 未对接 |
| Task | 工时记录 | `/{id}/worklog` | ❌ 未对接 |
| Task | 任务依赖 | `/{id}/dependency` | ❌ 未对接 |
| Task | Git 提交关联 | `/{id}/commit` | ❌ 未对接 |
| Task | MR 关联 | `/{id}/merge-request` | ❌ 未对接 |
| Task | 领取任务 | `/{id}/claim` | ❌ 未对接 |
| Task | 估算/故事点 | `/{id}/estimate`, `/{id}/storypoints` | ❌ 未对接 |
| Task | 甘特图数据 | `/gantt` | ❌ 未对接 |
| Task | 批次移动 | `/batch/move-to-iteration` | ❌ 未对接 |
| Task | Webhook | `/webhook/**` | ❌ 未对接 |
| Task | Label | `/project/{id}/label/*` | ❌ 未对接 |
| Project | 迭代 CRUD | `/{projectId}/iteration/*` | ❌ 未对接 |
| Project | 里程碑 CRUD | `/{projectId}/milestone/*` | ❌ 未对接 |
| System | 通知列表/已读 | `/notice/*` | ❌ 未对接 |
| System | 字典查询 | `/dict/data/type/{type}` | ❌ 未对接 |

### 5.2 前端有而后端无

| 前端功能 | 说明 |
|---------|------|
| 看板移动卡片 API（`/v1/board/move`）| 这个 API 在后端根本不存在，前端设计错误 |

---

## 六、数据库层核对

### 6.1 OA 表需清理（V0.1）

| 表 | 状态 | 建议 |
|----|------|------|
| `sys_dept` | 仍在 lest-system | 添加 `DEPRECATED` 注释，V0.2 前保留表但无 Controller |
| `sys_post` | 仍在 lest-system | 添加 `DEPRECATED` 注释，V0.2 前保留表但无 Controller |
| `sys_menu` | 仍在 lest-system | V0.2 才删除 |
| `sys_role` | 仍在 lest-system | V0.2 才删除 |
| `sys_user_role` | 仍在 lest-system | V0.2 才删除 |
| `sys_role_dept` | 仍在 lest-system | V0.2 才删除 |

### 6.2 Core 业务表

| 表 | 状态 |
|----|------|
| `project` | ✅ 正常 |
| `project_member` | ✅ 正常，但无权限校验 |
| `iteration` | ✅ 正常（v0.2 有 soft-delete migration）|
| `milestone` | ✅ 正常 |
| `task` | ✅ 正常 |
| `label` | ✅ 正常 |
| `task_label` | ✅ 正常 |
| `task_watcher` | ✅ 正常 |
| `task_worklog` | ✅ 正常 |
| `task_comment` | ✅ 正常 |
| `task_commit` | ✅ 正常 |
| `task_dependency` | ✅ 正常 |

---

## 七、网关与代理配置核对

### 7.1 前端 Vite 代理 vs 后端 Gateway 路径

| 前端代理配置 | 实际转发 | 后端实际路径 | 说明 |
|------------|---------|-------------|------|
| `/api` → `localhost:8080` | Gateway 入口 | — | ✅ 正确 |
| `/auth` → `localhost:8096` | 转发到 lest-auth | — | ⚠️ **前端未用此代理**，axios 用 `/api` 前缀 |
| `/ws` → `ws://localhost:8080` | WebSocket | — | 预留给通知功能 |

**关键问题**：前端 axios 的 `baseURL: '/api'`，所以所有请求都以 `/api` 开头。

但后端 Gateway 网关没有配置 `/api` → 各服务的路由。

当前 Gateway 配置的路径前缀：
- `/auth/**` → lest-auth（**不是** `/api/auth/**`）
- `/project/**` → lest-project
- `/task/**` → lest-task
- `/system/**` → lest-system

**这意味着前端的 `/api/v1/auth/login` 根本匹配不到任何网关路由！**

### 7.2 网关路由修复（关键）

Gateway 需要增加 `/api` 前缀路由：

```yaml
# lest-gateway/src/main/resources/application.yml
# 需要在 routes 前增加 /api 前缀映射
routes:
  - id: lest-auth-api
    uri: http://127.0.0.1:8096
    predicates:
      - Path=/api/auth/**
    filters:
      - StripPrefix=1
    # 这会转发到 lest-auth 的 /auth/... 但后端没有 /auth 前缀！

  - id: lest-project-api
    uri: http://127.0.0.1:8082
    predicates:
      - Path=/api/project/**
    filters:
      - StripPrefix=1
```

**实际存在的问题**：

假设前端请求 `GET /api/v1/project/list`：
1. Vite 代理到 `http://localhost:8080/api/v1/project/list`
2. Gateway 尝试匹配路由，找不到 `/api/**` 的路由（只有 `/auth/**`, `/project/**` 等）
3. 请求 404

**解决方案**：有两种

**方案 A**：前端去掉 `/api` 前缀，直接请求网关路径（修改 `api/axios.ts`）
```typescript
const service = axios.create({
  baseURL: '',  // 不加前缀，直接 /v1/xxx
  // ...
});
```
然后前端请求变成：
- `/v1/project/list` → 匹配 Gateway `/project/**` → lest-project `/list`
- `/v1/auth/login` → 匹配 Gateway `/auth/**` → lest-auth `/login`

**方案 B**：Gateway 增加 `/api` 前缀路由（推荐，更规范）
```yaml
routes:
  - id: lest-auth-api
    uri: http://127.0.0.1:8096
    predicates:
      - Path=/api/v1/auth/**
    filters:
      - StripPrefix=2  # 去掉 /api/v1
```

**建议采用方案 B**，因为 `/api` 前缀是 RESTful API 的标准做法。

---

## 八、汇总：需要修改的文件清单

### 前端需修改的文件

| 文件 | 操作 | 原因 |
|------|------|------|
| `src/api/auth.ts` | 修改 | `getUserInfo` 和 `updateProfile` 路径错误 |
| `src/api/project.ts` | 修改 | 多处路径错误（缺少 `/list`，成员路径错误）|
| `src/api/task.ts` | 修改 | 多处路径错误；增加迭代/里程碑/标签 API |
| `src/api/board.ts` | 修改 | 路径错误；`moveCard` API 不存在 |
| `src/api/release/index.ts` | 创建 | V0.2 骨架 |
| `src/api/iteration.ts` | 创建 | V0.1 P1 |
| `src/api/label.ts` | 创建 | V0.1 P1 |
| `src/api/notification.ts` | 创建 | V0.2 |
| `src/stores/task.ts` | **创建** | P0 缺失 |
| `src/stores/notification.ts` | 创建 | V0.2 |
| `src/views/task/Gantt.vue` | 完善 | V0.2 |
| `src/views/inbox/index.vue` | 完善 | V0.2 对接 API |
| `src/views/settings/Profile.vue` | 完善 | V0.2 表单提交 |
| `src/views/task/TaskDetail.vue` | 创建 | V0.2 任务详情抽屉 |
| `src/views/project/ProjectDetail.vue` | 完善 | V0.2 项目详情页 |
| `views/system/`（38 文件）| **删除** | V0.1 P0 |
| `views/monitor/`（17 文件）| **删除** | V0.1 P0 |
| `src/layout/index.vue` | 删除 | V0.1 P0 |
| `src/store/`（旧 Vuex）| 删除 | V0.1 P0 |

### 后端需修改的文件

| 文件 | 操作 | 原因 |
|------|------|------|
| `lest-system` 的 `SysDeptController.java` | **删除** | V0.1 P0 |
| `lest-system` 的 `SysPostController.java` | **删除** | V0.1 P0 |
| `lest-system` 的 `SysDeptMapper.java` | 标记废弃 | V0.1 |
| `lest-system` 的 `SysPostMapper.java` | 标记废弃 | V0.1 |
| `lest-gateway` `application.yml` | 修改 | 增加 `/api` 前缀路由 |
| `lest-auth` `TokenController.java` | 可能需增加 | `GET /userinfo`（或让前端走 lest-system）|
| `lest-common-security` | 新增 | 实现 `@RequireProjectRole` 注解（V0.1 P1）|
| `lest-task` 所有 Controller | 检查 | 增加项目级权限注解（V0.1 P1）|
| `lest-project` 所有 Controller | 检查 | 增加项目级权限注解（V0.1 P1）|
| `lest-release` 所有 Controller | 检查 | 增加权限注解（V0.1 P1）|

---

## 九、执行建议

### 阶段 1：修复核心路径问题（V0.1 P0）

1. **Gateway 增加 `/api` 前缀路由**（最重要，否则前端所有请求都 404）
2. **修正前端 API 路径**（`api/auth.ts`, `api/project.ts`, `api/task.ts`, `api/board.ts`）
3. **创建缺失的 `stores/task.ts`**
4. **删除 OA 页面**（`views/system/`, `views/monitor/`, `src/layout/index.vue`, `src/store/`）

### 阶段 2：完善功能（V0.1 P1）

5. 实现迭代/里程碑/Label API 前端对接
6. 实现后端项目级权限校验

### 阶段 3：V0.2 功能（2026-07-04）

7. 甘特图、任务详情抽屉、收件箱 API 对接
8. 后端 OA 功能全面清理

---

## 版本历史

| 版本 | 日期 | 修改内容 |
|------|------|---------|
| V1.0 | 2026-06-03 | 初始核对报告 |
| V1.1 | 2026-06-03 | API 路径统一修复 |
| V1.2 | 2026-06-03 | 前端 API 层全面对齐 |
| V1.5 | 2026-06-03 | @RequireProjectRole 全面应用，前后端数据模型对齐 |
| V1.6 | 2026-06-03 | 任务详情抽屉完整 Tab 化；迭代/里程碑/发布计划前端页面；milestone.ts API 路径对齐 targetDate 字段 |
| V1.9 | 2026-06-03 | UI 系统性重构：TaskDetailDrawer 去 Tab 化 + 双栏布局 + Kanban/TaskList Linear 风格 + AppLayout 侧边栏层级 |
| V2.0 | 2026-06-03 | 全面 UI 一致性修复：暗黑模式全面覆盖、自定义组件替换 admin 风格、Loading 统一、Activity Feed 合并、focus-visible 补充 |
| V2.1 | 2026-06-03 | Bug 修复：TableDataInfo rows→records、ReleaseArtifact 字段映射、releaseApi.update 含 ID、iteration startDate 规范化、Activity Feed key、CommandPalette 索引、错误提示等 |
