# UI 插件化架构 — 开发任务单

> **📌 PRD**：[UI 插件化架构设计](../../../1-prd/core/V2.0/UI插件化架构设计.md)
>
> **整体状态**：🟡 设计完成，待启动

---

## Phase 1 — 插件运行时基础设施（V2.0）

### 1.1 PluginRegistry（插件注册表）

||| 任务 ID | 说明 | 优先级 | 状态 |
|||:---|:---|:---:|:---:|
|| FE-101 | 创建 `PluginRegistry` 类 — 管理已启用插件的 manifest 缓存 | P0 | 🔴 |
|| FE-102 | 实现 `loadAll()` — 从后端 `GET /plugin/enabled` 获取插件列表 | P0 | 🔴 |
|| FE-103 | 实现 `loadPlugin(pluginId)` — 动态加载单个插件 manifest | P0 | 🔴 |
|| FE-104 | 实现 `reload()` — 插件启用/禁用后触发重新加载 | P1 | 🔴 |
|| FE-105 | 实现 manifest schema 验证（JSON Schema）| P1 | 🔴 |

### 1.2 ExtensionResolver（扩展点解析器）

||| 任务 ID | 说明 | 优先级 | 状态 |
|||:---|:---|:---:|:---:|
|| FE-201 | 创建 `ExtensionResolver` 类 — 按扩展点类型查询插件注册内容 | P0 | 🔴 |
|| FE-202 | 实现 `resolve(page, context)` — 根据页面上下文返回匹配的扩展列表 | P0 | 🔴 |
|| FE-203 | 实现 `resolveById(id)` — 根据扩展 ID 精确查找单个扩展 | P0 | 🔴 |
|| FE-204 | 实现排序逻辑（按 `order` 字段升序）| P0 | 🔴 |
|| FE-205 | 实现权限过滤（检查用户是否有权限查看该扩展）| P1 | 🔴 |

### 1.3 ComponentRegistry（组件注册表）

||| 任务 ID | 说明 | 优先级 | 状态 |
|||:---|:---|:---:|:---:|
|| FE-301 | 创建 `ComponentRegistry` 类 — 动态注册和渲染 Vue 组件 | P0 | 🔴 |
|| FE-302 | 实现 `register(component)` — 注册插件的 Vue 组件（支持异步加载）| P0 | 🔴 |
|| FE-303 | 实现 `getComponent(id)` — 获取已注册的组件定义 | P0 | 🔴 |
|| FE-304 | 实现组件懒加载（`() => import()` 动态导入）| P0 | 🔴 |
|| FE-305 | 实现 `<PluginComponent>` Vue 组件 — 统一渲染接口 | P0 | 🔴 |

### 1.4 useExtensionResolver Composable

||| 任务 ID | 说明 | 优先级 | 状态 |
|||:---|:---|:---:|:---:|
|| FE-401 | 创建 `useExtensionResolver()` composable — 提供响应式扩展点查询 | P0 | 🔴 |
|| FE-402 | 实现 `usePluginTabs(page, context)` — 返回指定页面的 Tab 扩展 | P0 | 🔴 |
|| FE-403 | 实现 `usePluginFields(point, context)` — 返回指定字段区的扩展 | P0 | 🔴 |
|| FE-404 | 实现 `usePluginActions(page, context)` — 返回操作按钮扩展 | P1 | 🔴 |
|| FE-405 | 实现插件热更新（插件变更时自动刷新扩展列表）| P1 | 🔴 |

---

## Phase 2 — 任务详情页插件化（V2.0）

### 2.1 TaskDetailTabs 改造

||| 任务 ID | 说明 | 优先级 | 状态 |
|||:---|:---|:---:|:---:|
|| FE-501 | 重构 `TaskDetailTabs.vue` — 将插件 Tab 改为动态渲染 | P0 | 🔴 |
|| FE-502 | 迁移评论 Tab 到 Extension Point（`task-detail.tab.comment`）| P0 | 🔴 |
|| FE-503 | 迁移活动 Tab 到 Extension Point（`task-detail.tab.activity`）| P0 | 🔴 |
|| FE-504 | 实现插件 Tab 的 `<Suspense>` 异步加载和 Loading 状态 | P0 | 🔴 |
|| FE-505 | 实现插件 Tab 的错误边界（单插件报错不影响其他插件）| P1 | 🔴 |

### 2.2 TaskDetailFields 改造

||| 任务 ID | 说明 | 优先级 | 状态 |
|||:---|:---|:---:|:---:|
|| FE-601 | 重构 `TaskDetailFields.vue` — 左右两侧字段区支持插件注入 | P0 | 🔴 |
|| FE-602 | 定义 `task-detail.field-sidebar.left` 扩展点 | P0 | 🔴 |
|| FE-603 | 定义 `task-detail.field-sidebar.right` 扩展点 | P0 | 🔴 |
|| FE-604 | 实现字段扩展的 `<CoreField>` 包裹组件（统一布局）| P0 | 🔴 |
|| FE-605 | 实现字段扩展的权限过滤（字段级权限控制）| P1 | 🔴 |

### 2.3 TaskDetailActions 改造

||| 任务 ID | 说明 | 优先级 | 状态 |
|||:---|:---|:---:|:---:|
|| FE-701 | 重构任务详情页头部操作按钮区 | P0 | 🔴 |
|| FE-702 | 定义 `task-detail.header.actions` 扩展点 | P0 | 🔴 |
|| FE-703 | 实现操作按钮的顺序控制和分组 | P1 | 🔴 |

---

## Phase 3 — 项目详情页插件化（V2.0）

||| 任务 ID | 说明 | 优先级 | 状态 |
|||:---|:---|:---:|:---:|
|| FE-801 | 重构 `ProjectDetailTabs.vue` — 项目 Tab 区支持插件注入 | P0 | 🔴 |
|| FE-802 | 定义 `project-detail.tab` 扩展点 | P0 | 🔴 |
|| FE-803 | 迁移概览/任务/看板 Tab 到 Extension Point | P0 | 🔴 |
|| FE-804 | 重构项目侧边栏 — 支持 `project-detail.sidebar-widget` 扩展点 | P1 | 🔴 |
|| FE-805 | 重构项目头部操作区 — 支持 `project-detail.header-actions` 扩展点 | P1 | 🔴 |

---

## Phase 4 — SDK 完善（V2.0）

||| 任务 ID | 说明 | 优先级 | 状态 |
|||:---|:---|:---:|:---:|
|| FE-901 | 提供 `@lest/plugin-sdk` 的 `definePlugin()` 工厂函数 | P0 | 🔴 |
|| FE-902 | 提供 TypeScript 类型定义（`ExtensionPoint`, `Extension`, `PluginManifest`）| P0 | 🔴 |
|| FE-903 | 提供 `usePluginAPI()` composable — 调用后端插件 API | P0 | 🔴 |
|| FE-904 | 编写插件模板 CLI（`lest plugin init --template=vue`）| P1 | 🔴 |
|| FE-905 | 提供 `usePluginContext()` — 获取当前插件运行时上下文 | P1 | 🔴 |

---

## Phase 5 — 更多扩展点（V3.0）

||| 任务 ID | 说明 | 优先级 | 状态 |
|||:---|:---|:---:|:---:|
|| FE-1001 | 首页仪表盘扩展点 `dashboard.widgets` | P1 | 🔴 |
|| FE-1002 | 全局侧边栏扩展点 `global.sidebar` | P1 | 🔴 |
|| FE-1003 | 任务批量操作扩展点 `task.batch-action` | P2 | 🔴 |
|| FE-1004 | 看板列扩展点 `board.column.header` | P2 | 🔴 |
|| FE-1005 | 工作流配置 UI 扩展点 `workflow.editor` | P2 | 🔴 |

---

## 前置依赖

- [FE-101] ~ [FE-305] 必须先完成，才能进行 Phase 2 及之后的工作
- Phase 4（SDK 完善）可与 Phase 2/3 并行开发

## 验收标准

- [ ] 任务详情页评论 Tab 由 Core 注册，其他 Tab 由插件动态注入
- [ ] 插件注册后，无需修改 Core 代码即可在任务详情页显示
- [ ] 插件卸载后，UI 自动消失，不残留痕迹
- [ ] 单插件报错不影响其他插件和 Core 功能
- [ ] 单元测试覆盖 PluginRegistry、ExtensionResolver、ComponentRegistry
