# LEST Platform V0.1 & V0.2 真实功能完成度报告

> **文档目的**：揭示 PRD 文档中声称"已实现"的功能与代码实际实现之间的真实差距，明确哪些功能是骨架、哪些是完整实现、哪些根本没做。
>
> **核对日期**: 2026-06-03
> **版本**: V1.0

---

## 一、总体判断

**后端**：Controller 层骨架完整，核心 Service 有真实业务逻辑，但有多处编译级错误、数据库 Schema 不匹配、缺失的 Mapper XML，安全校验全无。前端 UI 和后端 API 的路径有大量不匹配（见 `FE-BE-RECONCILIATION.md`）。

**前端**：登录、看板、项目列表等核心页面有真实 UI 代码，但缺失关键 Store（`task.ts`），多个页面是骨架占位或硬编码假数据，Gateway 代理缺失导致任何 API 请求都会 404。

**根本问题**：代码和文档都在描述"目标状态"，但实际处于"迁移中"状态——旧系统（RuoYi/ele-admin-plus）还没删干净，新系统只搭了个空架子。

---

## 二、后端 Core 业务真实完成度

### 2.1 lest-task（任务管理）

#### Controller 层
| 端点 | 状态 | 说明 |
|------|------|------|
| 任务 CRUD | ✅ 存在 | 完整 |
| 看板 `GET /board` | ✅ 存在 | 存在 |
| 卡片移动 `PUT /{id}/move` | ✅ 存在 | 存在 |
| 子任务 CRUD | ✅ 存在 | 完整 |
| 评论 CRUD | ✅ 存在 | 完整 |
| 工时记录 CRUD | ✅ 存在 | 完整 |
| Git 提交/MR 关联 | ✅ 存在 | 完整 |
| 任务依赖 | ✅ 存在 | 完整（含循环依赖检测）|
| 任务领取 `POST /{id}/claim` | ✅ 存在 | 存在 |
| 估算工时/故事点 | ✅ 存在 | 存在 |
| Webhook 接收 | ✅ 存在 | CI + Git |
| 批次移动 | ✅ 存在 | 存在 |
| 甘特图数据 `GET /gantt` | ✅ 存在 | 存在 |

#### Service 层（真实实现情况）

| 方法 | 业务逻辑 | 说明 |
|------|---------|------|
| `insertTask` / `insertSubtask` | ✅ 有深度限制（最多 3 层）| 真实 |
| `hasCircularDependency` | ✅ BFS 循环检测 | 真实，但只检测单向 |
| `checkBlockers` | ✅ 完成前检查阻塞任务 | 真实 |
| 工时自动累加 | ✅ `actualHours` 自动汇总 | 真实 |
| 自动关注（创建者）| ✅ | 真实 |
| 软删除级联 | ✅ 软删除子任务 | 真实 |
| 硬删除清理 | ✅ 清理 8 个关联表 | 真实 |
| 大部分 select 方法 | ⚠️ 仅 CRUD 透传 | mapper 调用 |

#### 🔴 编译级错误（Service 层）

`TaskServiceImpl` 注入了 5 个 Mapper，但对应的 Domain 类或 Mapper XML **不存在**：

| 缺失项 | 影响 |
|--------|------|
| `IssueLink.java` | 任务关联功能无法编译 |
| `IssueLinkType.java` | 关联类型无法编译 |
| `Attachment.java` + `AttachmentMapper` | 附件上传无法编译 |
| `TaskVote.java` + `TaskVoteMapper` | 投票功能无法编译 |
| `AutomationRule.java` 等自动化相关 | 自动化规则无法编译 |

#### 🔴 Schema 不匹配

`task_commit` 表缺少 `type` 和 `source` 字段，但 `TaskServiceImpl` 中的代码会设置这两个字段（`commit.setType("commit")`），值会被静默丢弃。

#### 🟡 看板数据质量问题

`getBoard()` 方法：
- 按 status 分组返回 3 列（todo, in_progress, completed）
- 无 WIP 限制检查
- 无故事点汇总
- 无预估 vs 实际工时对比
- 硬编码 STATUS_FLOW，不灵活
- `assigneeName` 等字段在 Mapper XML 中未 JOIN，**永远为 null**

#### 🔴 安全问题：无项目级权限校验

`TaskServiceImpl` 完全没有 `ProjectMemberMapper`，任何通过 RBAC 认证的用户可以读写**任何项目**的任务。

---

### 2.2 lest-project（项目管理）

#### Controller 层
| 端点 | 状态 |
|------|------|
| 项目 CRUD + 归档/取消归档 | ✅ 完整 |
| 成员管理（增删改）| ✅ 完整 |
| 迭代 CRUD + 状态流转 | ✅ 完整 |
| 里程碑 CRUD + 关联迭代 | ✅ 完整 |

#### Service 层（真实实现情况）

| 功能 | 状态 | 说明 |
|------|------|------|
| 项目名唯一性校验 | ✅ 真实 | |
| 管理员保护（不能删除最后一个管理员）| ✅ 真实 | |
| 归档/取消归档状态守卫 | ✅ 真实 | |
| 软删除前检查（迭代数、成员数）| ✅ 真实 | |
| 创建者自动成为管理员 | ✅ 真实 | |
| 迭代日期冲突检测 | ✅ 真实 | V0.1 未对接 |
| 迭代状态机（只能 PLANNING→开始，IN_PROGRESS→完成）| ✅ 真实 | V0.1 未对接 |
| 里程碑重复关联检测 | ✅ 真实 | |

#### 🟡 缺失功能

| 功能 | 说明 |
|------|------|
| `updateMemberRole` 前端未定义 | 后端有，前端 API 缺失 |
| `ownerName` 从未填充 | Mapper 未 JOIN sys_user |
| 项目级权限校验 | 无，任何 RBAC 用户可操作任何项目 |

---

### 2.3 lest-auth（认证服务）

#### 🔴 架构不一致

整个后端只有 `lest-auth` **不使用** `IService + ServiceImpl` 模式，直接 `@Component` 注入。结构不一致。

#### ✅ 业务逻辑真实

`SysLoginService` 有实质性逻辑：
- 用户名/密码校验（长度、非空）
- Redis IP 黑名单检查
- Feign 调用远程用户服务
- 账户删除/停用检查
- 密码加密验证（强度、错误次数、锁定）
- 注册时字段校验

---

### 2.4 lest-file（文件服务）

#### ⚠️ 极薄包装

两个实现（Local 和 MinIO）都只是文件工具类的包装，没有：
- 文件类型校验（委托给 `FileUploadUtils`）
- 文件大小校验
- 病毒扫描
- 数据库元数据持久化（上传后无 DB 记录）

---

### 2.5 lest-system（系统管理）

#### 🔴 应清理但未清理

| Controller | 状态 | 说明 |
|-----------|------|------|
| `SysDeptController` | ❌ 仍在 | V0.1 应删除 |
| `SysPostController` | ❌ 仍在 | V0.1 应删除 |
| `SysMenuController` | ❌ 仍在 | V0.2 才删除 |
| `SysRoleController` | ❌ 仍在 | V0.2 才删除 |
| `SysJobController` | ⚠️ 定时任务管理 | V0.2 才删除 |
| `SysJobLogController` | ⚠️ 任务日志 | V0.2 才删除 |
| `SysOperlogController` | ⚠️ 操作日志 | V0.2 才删除 |
| `SysLogininforController` | ⚠️ 登录日志 | V0.2 才删除 |
| `SysUserOnlineController` | ⚠️ 在线用户 | V0.2 才删除 |

---

### 2.6 骨架模块（未实现）

| 模块 | 状态 | 说明 |
|------|------|------|
| `lest-notification` | ❌ 仅 SpringBootApplication | 无 Controller/Service |
| `lest-ai` | ❌ 仅 SpringBootApplication | 无任何代码 |
| `lest-meeting` | ❌ 仅 SpringBootApplication | 无任何代码 |
| `lest-release` | ✅ Controller 存在但未对接 | 前端无 API |
| `lest-open` | ❌ 仅 SpringBootApplication | 无任何代码 |
| `lest-plugin` | ❌ 仅 SpringBootApplication | 无任何代码 |
| `lest-performance` | ❌ 仅 SpringBootApplication | 无任何代码 |
| `lest-wakapi` | ❌ 仅 SpringBootApplication | 无任何代码 |

---

## 三、前端 UI/布局真实完成度

### 3.1 各页面实现质量

| 页面 | 实现质量 | 说明 |
|------|---------|------|
| `login/index.vue` | ✅ 真实完整 | 表单验证、登录逻辑、错误处理 |
| `dashboard/index.vue` | ⚠️ 部分真实 | 项目列表真实，但 3/4 统计数字硬编码（8, 3, 5）|
| `project/ProjectList.vue` | ✅ 真实完整 | 卡片布局、筛选、创建、跳转 |
| `project/ProjectDetail.vue` | ⚠️ 仅中转壳 | 无详情内容，永远重定向到任务列表 |
| `task/TaskList.vue` | ⚠️ 有 UI 但会崩溃 | UI 真实，但引用了不存在的 `stores/task.ts` |
| `task/Kanban.vue` | ✅ 真实完整 | vuedraggable 拖拽、列渲染、乐观更新、错误回滚 |
| `task/Gantt.vue` | ❌ 占位页 | 23 行，`el-empty` |
| `inbox/index.vue` | ❌ 假数据 | 3 条硬编码通知，无 API |
| `settings/Profile.vue` | ❌ 无实际功能 | Save 按钮只弹 success，不调 API |
| `components/layout/AppLayout.vue` | ✅ 真实完整 | 侧边栏 + Header + 路由视图 |

### 3.2 🔴 致命构建错误

| 问题 | 文件 | 影响 |
|------|------|------|
| **缺少 `stores/task.ts`** | `TaskList.vue` 引用但文件不存在 | 运行时崩溃 |
| **缺少 `src/styles/index.scss`** | `main.ts` 第 8 行 `import '@/styles/index.scss'` | **构建失败** |
| **API 路径全面错误** | 4 个 API 文件 | 所有请求 404（见 `FE-BE-RECONCILIATION.md`）|

### 3.3 ⚠️ 功能缺失或占位

| 缺失功能 | 现状 |
|---------|------|
| 甘特图 | 占位页，V0.2 才实现 |
| 任务详情抽屉（侧边详情）| 完全不存在 |
| 收件箱 API 对接 | 假数据 |
| 个人设置表单提交 | 假提交 |
| 迭代管理 UI | 不存在 |
| 里程碑管理 UI | 不存在 |
| 项目设置页 | 不存在 |
| 看板列"添加任务"按钮 | 无处理器（装饰性）|
| 全局搜索 Cmd+K | 无处理器（装饰性）|
| 通知未读数 | 硬编码 `3` |

### 3.4 🟡 遗留系统清理情况

| 清理项 | 状态 |
|--------|------|
| `views/system/`（38 文件）| ❌ 仍在 |
| `views/monitor/`（17 文件）| ❌ 仍在 |
| `src/layout/index.vue`（498 行 ele-admin）| ❌ 仍在（虽未被引用，但占用磁盘空间且含 ele-admin-plus 依赖引用）|
| `src/store/`（旧 Vuex）| ❌ 仍在 |
| `src/styles/transition.scss` 中的 `.ele-*` 死类 | ❌ 仍在 |

---

## 四、PRD vs 代码对照：V0.1/V0.2 真实完成度

### 4.1 项目管理（PRD 声称已实现）

| PRD 功能 | 后端 | 前端 | 说明 |
|---------|------|------|------|
| 项目 CRUD | ✅ Controller + Service | ⚠️ 列表页有，详情页是壳 | |
| 项目归档/取消归档 | ✅ 有 `archive` 和 `unarchive` | ⚠️ API 路径错误 | |
| 成员管理（增删查）| ✅ | ⚠️ 路径错误 | |
| 成员角色更新 | ✅ | ❌ `updateMemberRole` 前端未定义 | |
| 迭代 CRUD | ✅ 含状态机 | ❌ 前端无 API 对接，无 UI | |
| 里程碑 CRUD | ✅ 含迭代关联 | ❌ 前端无 API 对接，无 UI | |
| 软删除（含关联检查）| ✅ | — | 后端已实现 |
| 创建者自动成为管理员 | ✅ | — | 后端已实现 |
| 项目名唯一性 | ✅ | — | 后端已实现 |
| 项目权限校验 | ❌ **无任何校验** | — | 任何用户可操作任何项目 |

### 4.2 任务管理（PRD V1.0 基础功能）

| PRD 功能 | 后端 | 前端 | 说明 |
|---------|------|------|------|
| 任务 CRUD | ✅ 含 Service 逻辑 | ⚠️ API 路径错误，详情页不存在 | |
| 状态流转 | ✅ 含守卫（检查阻塞）| ⚠️ 路径错误 | |
| 任务分配 | ✅ | ⚠️ 路径错误 | |
| 看板拖拽 | ✅ `PUT /{id}/move` | ⚠️ board API 整体设计错误 | |
| 子任务（最多 3 层）| ✅ 含深度检测 | ❌ 前端无 UI/API | |
| 任务依赖 | ✅ 含循环检测 | ❌ 前端无 UI/API | |
| 评论 CRUD | ✅ | ❌ 前端无 UI/API | |
| 工时记录 | ✅ 自动累加 `actualHours` | ❌ 前端无 UI/API | |
| Git 提交关联 | ✅ 含手动和 Webhook | ❌ 前端无 UI/API | |
| MR 关联 | ✅ 含手动和 Webhook | ❌ 前端无 UI/API | |
| 领取任务 | ✅ | ❌ 前端无 UI/API | |
| 故事点 | ✅ | ❌ 前端无 UI/API | |
| 甘特图数据 | ✅ `GET /gantt` | ❌ 占位页 | |
| 批次移动 | ✅ | ❌ 前端无 UI | |
| 附件上传 | ⚠️ **编译失败**（缺类）| ❌ | 需先修编译错误 |
| 投票功能 | ⚠️ **编译失败**（缺类）| ❌ | 需先修编译错误 |
| 自动化规则 | ⚠️ **编译失败**（缺类）| ❌ | 需先修编译错误 |
| 任务权限校验 | ❌ **无任何校验** | — | 任何用户可操作任何任务 |

### 4.3 看板视图（PRD 核心功能）

| 规范要求 | 实现情况 |
|---------|---------|
| 列配置（拖拽排序、WIP 限制、超时高亮）| ⚠️ 有 WIP 显示，无 WIP 校验逻辑 |
| 看板数据按 projectId 获取 | ⚠️ API 路径错误 |
| 卡片着色（优先级/类型/截止日期）| ✅ `Kanban.vue` 有颜色映射 |
| 列内拖拽 | ✅ vuedraggable |
| 跨列拖拽 | ✅ `PUT /{id}/move` |
| 乐观更新 + 错误回滚 | ✅ `boardStore.moveCard()` 有回滚 |
| 看板列"添加任务" | ⚠️ UI 有，按钮无处理器 |
| 看板 API 独立 | ❌ 前端 `boardApi.moveCard` 指向不存在的后端路径 |

### 4.4 布局与导航（PRD 规范）

| 规范（FRONTEND-ARCHITECTURE.md）| 实现情况 |
|------------------------------|---------|
| 敏捷工作台风格（侧边项目列表 + 内容区）| ✅ `AppLayout.vue` 真实实现 |
| 侧边栏 240px，可折叠 | ✅ 有折叠逻辑 |
| Header 56px，含面包屑、搜索、通知、头像 | ⚠️ 搜索无处理器，通知数硬编码 |
| 仪表盘统计卡片 | ⚠️ 有卡片，但 3/4 数据硬编码 |
| 项目列表（卡片布局）| ✅ 真实实现 |
| 看板视图 | ✅ 真实实现（但 API 路径错）|
| 任务列表（el-table + 筛选）| ✅ 真实实现（但 Store 缺失）|
| 任务详情抽屉 | ❌ 完全不存在 |
| 甘特图 | ❌ 占位页 |
| 收件箱 | ❌ 假数据 |
| 个人设置 | ❌ 假提交 |
| 暗黑模式 | ❌ 未实现 |
| 国际化 | ❌ 未实现 |
| Cmd+K 命令面板 | ❌ 装饰性输入框 |

---

## 五、结论：真实进度评估

### 5.1 进度可视化

```
V0.1 总工作量估算: 100%
──────────────────────────────────────────────────
代码骨架 (Controller层)      ████████████████░░░░  80%  ✅ 完成
后端Service业务逻辑          ████████████░░░░░░░░░  60%  ⚠️ 部分缺失/错误
前端页面UI                  ███████████░░░░░░░░░░░  55%  ⚠️ 多个占位
前端Store/状态管理          ████████░░░░░░░░░░░░░░  40%  ⚠️ task store缺失
前后端API对接               ██████░░░░░░░░░░░░░░░░  30%  🔴 大量路径错误
Gateway路由配置             ███░░░░░░░░░░░░░░░░░░░░  15%  🔴 /api路径缺失
技术债清理 (OA页面)         ██░░░░░░░░░░░░░░░░░░░░░  10%  🔴 完全未动
安全校验 (项目级权限)       █░░░░░░░░░░░░░░░░░░░░░░   5%  🔴 尚未实现
编译错误修复                ░░░░░░░░░░░░░░░░░░░░░░░   0%  🔴 有5个编译错误
Schema对齐                  ░░░░░░░░░░░░░░░░░░░░░░░   0%  🔴 task_commit缺字段
```

### 5.2 按优先级排序的待办

#### 🔴 立即修复（阻塞构建/运行）

| # | 问题 | 影响 | 修复位置 |
|---|------|------|---------|
| 1 | `src/styles/index.scss` 缺失 | **npm run dev 失败** | 需创建文件或移除 import |
| 2 | 5 个 Domain 类缺失（编译错误）| **mvn compile 失败** | `IssueLink`, `IssueLinkType`, `Attachment`, `TaskVote`, `AutomationRule` |
| 3 | `TaskLabelMapper.xml` 缺失 | **编译错误** | 需创建或确保 XML 存在 |
| 4 | `task_commit` 表缺字段 | **数据静默丢失** | DDL 添加 `type`, `source` 列 |
| 5 | `stores/task.ts` 缺失 | **TaskList 运行时崩溃** | 创建 `useTaskStore` |
| 6 | Gateway 无 `/api` 前缀路由 | **前端所有请求 404** | `lest-gateway/application.yml` 增加 `/api/v1/` 路由 |
| 7 | 前端 API 路径全面修正 | 4 个 API 文件 | 见 `FE-BE-RECONCILIATION.md` 第 8 节 |

#### 🟡 V0.1 核心功能补全

| # | 功能 | 说明 |
|---|------|------|
| 8 | 迭代/里程碑前端 API + UI | 后端已实现，前端无对接 |
| 9 | 看板 board API 重新设计 | `moveCard` 路径重新映射 |
| 10 | `ownerName` / `assigneeName` JOIN 修复 | Mapper XML 增加 JOIN |
| 11 | `updateMemberRole` 前端 API | `projectApi` 增加 |
| 12 | `api/iteration.ts` 创建 | |
| 13 | `api/label.ts` 创建 | |
| 14 | 看板 WIP 限制校验逻辑 | 后端无校验 |

#### 🟡 技术债清理

| # | 清理项 |
|---|--------|
| 15 | 删除 `views/system/`（38 文件）|
| 16 | 删除 `views/monitor/`（17 文件）|
| 17 | 删除 `src/layout/`（旧布局）|
| 18 | 删除 `src/store/`（旧 Vuex）|
| 19 | `SysDeptController` + `SysPostController` 后端删除 |
| 20 | OA 表添加 DEPRECATED 注释 |

#### 🟢 V0.2 功能

| 功能 | 说明 |
|------|------|
| 任务详情抽屉 | `TaskDetailDrawer.vue` |
| 甘特图 | ECharts 渲染 |
| 收件箱 API 对接 | `inbox/index.vue` |
| 个人设置真实提交 | `Profile.vue` |
| 项目详情页 | `ProjectDetail.vue` 丰富内容 |
| `@RequireProjectRole` 实现 | 项目级权限 AOP |
| 暗黑模式 | Element Plus 适配 |
| 国际化 | vue-i18n |

---

## 版本历史

| 版本 | 日期 | 修改内容 |
|------|------|---------|
| V1.0 | 2026-06-03 | 初始真实完成度报告 |
