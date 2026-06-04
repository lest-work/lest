# LEST Platform — 前端架构设计

> **文档目的**：以架构师视角，设计基于 **纯 Element Plus** 的 LEST Platform PC/H5 前端架构，替代原有的 `ele-admin-plus-ts` 模板。
>
> **参考文档**：
> - Element Plus 官网：https://element-plus.org/zh-CN/
> - Element Plus GitHub：https://github.com/element-plus/element-plus
> - [全栈架构设计](./BACKEND-FRONTEND-ARCHITECTURE.md)
> - [前端插件化架构设计](../1-prd/core/V2.0/UI插件化架构设计.md)
>
> **版本**: V1.0 | **状态**: 设计完成 | **最后更新**: 2026-06-03

---

## 1. 技术选型

### 1.1 核心依赖

|| 类别 | 技术 | 版本 | 说明 |
|------|------|------|------|
| **框架** | Vue | 3.5+ | Composition API + `<script setup>` + TypeScript |
| **UI 库** | Element Plus | 2.9+ | 纯 Element Plus（不再依赖 ele-admin-plus-ts）|
| **构建** | Vite | 6.0+ | 极速开发 + HMR |
| **路由** | Vue Router | 4.5+ | 前端路由 |
| **状态** | Pinia | 2.3+ | Vue 3 原生状态管理 |
| **HTTP** | Axios | 1.7+ | 请求拦截、响应拦截、错误处理 |
| **图表** | Apache ECharts | 5.5+ | 燃尽图、甘特图、报表 |
| **拖拽** | SortableJS + vuedraggable@next | — | 看板拖拽 |
| **国际化** | vue-i18n | 10+ | 多语言支持 |
| **样式** | Sass | 1.89+ | CSS 预处理器 |
| **类型** | TypeScript | 5.4+ | 严格模式 |

### 1.2 为什么放弃 ele-admin-plus-ts

| 维度 | ele-admin-plus-ts（原有）| 纯 Element Plus（目标）|
|------|------------------------|---------------------|
| **体积** | 包含大量 OA 管理组件（400KB+）| 按需引入，TreeSelect/DialogForm 按需加载 |
| **绑定后端** | 强依赖 RuoYi 后端菜单/权限接口 | API 完全自控，不依赖后端菜单结构 |
| **OA 风格** | 管理后台模板（左侧菜单+顶部导航）| 敏捷工作台风格（侧边项目列表+内容区）|
| **看板支持** | 无原生看板组件 | 需要手写看板或引入第三方 |
| **定制成本** | 改模板比从零难 | 干净代码，定制零成本 |
| **插件化** | 不支持 | 完整插件化 Extension Point |

### 1.3 为什么选 Element Plus 而非 Arco Design / Ant Design Vue

| 维度 | Element Plus | Arco Design Vue | Ant Design Vue |
|------|-------------|----------------|----------------|
| **活跃度** | GitHub 25k+ stars，最活跃 | GitHub 8k+ | GitHub 19k+ |
| **生态** | 插件化体系（Element Plus 官方）| 字节内部开源 | 社区维护，版本滞后 |
| **组件丰富度** | 覆盖 90% 需求 | 覆盖 80% | 覆盖 85% |
| **国际化** | 官方 i18n 插件 | 官方 i18n | 官方 i18n |
| **暗黑模式** | CSS 变量覆盖 | 官方暗黑模式 | 官方暗黑模式 |
| **学习成本** | 低（在国内最流行）| 中 | 中 |

---

## 2. 目录结构

```
frontend-pc/
├── src/
│   ├── main.ts                    # 入口
│   ├── App.vue                    # 根组件
│   │
│   ├── api/                       # API 请求层（按领域划分）
│   │   ├── axios.ts              # Axios 实例配置
│   │   ├── auth.ts               # 认证接口（登录/注册/Token）
│   │   ├── project.ts            # 项目接口
│   │   ├── task.ts               # 任务接口
│   │   ├── board.ts              # 看板接口
│   │   ├── notification.ts       # 通知接口
│   │   ├── file.ts               # 文件接口
│   │   └── types/                 # API 请求/响应类型定义
│   │       ├── auth.ts
│   │       ├── project.ts
│   │       ├── task.ts
│   │       └── index.ts
│   │
│   ├── components/                # 业务组件
│   │   ├── layout/               # 布局组件
│   │   │   ├── AppLayout.vue     # 主布局（侧边栏+内容区）
│   │   │   ├── Sidebar.vue       # 侧边栏（项目列表）
│   │   │   └── Header.vue        # 顶部栏（搜索+通知+头像）
│   │   │
│   │   ├── task/                 # 任务相关
│   │   │   ├── TaskCard.vue      # 任务卡片
│   │   │   ├── TaskForm.vue      # 任务创建/编辑表单
│   │   │   ├── TaskDetailDrawer.vue  # 任务详情抽屉
│   │   │   └── TaskFilters.vue   # 筛选工具栏
│   │   │
│   │   ├── board/                # 看板相关
│   │   │   ├── KanbanBoard.vue   # 看板主视图
│   │   │   ├── KanbanColumn.vue  # 单列
│   │   │   └── KanbanCard.vue    # 看板卡片（复用 TaskCard）
│   │   │
│   │   ├── project/              # 项目相关
│   │   │   ├── ProjectCard.vue   # 项目卡片
│   │   │   ├── ProjectForm.vue   # 项目表单
│   │   │   └── MemberList.vue    # 成员列表
│   │   │
│   │   ├── common/               # 通用业务组件
│   │   │   ├── UserAvatar.vue    # 用户头像
│   │   │   ├── StatusBadge.vue   # 状态徽章
│   │   │   ├── PriorityIcon.vue  # 优先级图标
│   │   │   ├── EmptyState.vue    # 空状态
│   │   │   └── ConfirmDialog.vue # 确认对话框
│   │   │
│   │   └── plugin-host/          # 🔌 插件运行时
│   │       ├── PluginEngine.ts   # 插件加载器
│   │       ├── ExtensionPoint.ts  # 扩展点定义
│   │       └── FragmentRenderer.vue  # Fragment 渲染器
│   │
│   ├── views/                    # 页面
│   │   ├── login/
│   │   │   └── index.vue         # 登录页
│   │   ├── dashboard/
│   │   │   └── index.vue         # 首页仪表盘
│   │   ├── project/
│   │   │   ├── ProjectList.vue   # 项目列表
│   │   │   ├── ProjectDetail.vue # 项目详情
│   │   │   └── ProjectSettings.vue  # 项目设置
│   │   ├── task/
│   │   │   ├── TaskList.vue      # 任务列表
│   │   │   ├── Kanban.vue        # 看板视图
│   │   │   └── Gantt.vue        # 甘特图视图
│   │   ├── inbox/
│   │   │   └── index.vue         # 收件箱/通知
│   │   └── settings/
│   │       └── Profile.vue       # 个人设置
│   │
│   ├── router/
│   │   ├── index.ts              # 路由实例
│   │   └── routes.ts             # 路由配置（静态）
│   │
│   ├── stores/                   # Pinia 状态
│   │   ├── user.ts              # 用户信息、认证状态
│   │   ├── project.ts           # 当前项目上下文
│   │   ├── task.ts              # 任务列表、筛选、分页
│   │   ├── board.ts             # 看板列、拖拽状态
│   │   ├── notification.ts      # 通知、未读数
│   │   └── plugin.ts            # 插件状态、Fragment 注册表
│   │
│   ├── plugin-host/              # 🔌 插件运行时核心
│   │   ├── PluginEngine.ts
│   │   ├── ExtensionPoint.ts
│   │   ├── FragmentRegistry.ts
│   │   └── FragmentRenderer.vue
│   │
│   ├── locales/                  # i18n
│   │   ├── zh-CN.ts
│   │   └── en.ts
│   │
│   ├── styles/
│   │   ├── variables.scss        # CSS 变量（主题色、暗黑模式）
│   │   ├── mixins.scss          # 混入
│   │   └── global.scss          # 全局样式
│   │
│   └── utils/                   # 工具函数
│       ├── storage.ts           # localStorage / sessionStorage
│       ├── date.ts              # 日期格式化
│       └── validation.ts        # 表单校验
│
├── public/
│   └── favicon.ico
│
├── index.html
├── package.json
├── vite.config.ts
├── tsconfig.json
├── tsconfig.app.json
└── tsconfig.node.json
```

---

## 3. 布局架构

### 3.1 主布局：敏捷工作台风格

LEST 不是管理后台，是工作台。布局参考 Linear + Jira：

```
┌─────────────────────────────────────────────────────────────────┐
│  LEST Logo   [🔍 搜索... Cmd+K]    [🔔 3] [头像 ▾] │ ← Header（56px）
├────────────┬────────────────────────────────────────────────────┤
│            │                                                    │
│  📥 收件箱  │   ┌─────────────────────────────────────────┐    │
│  ✅ 我的任务 │   │   视图切换 [列表] [看板] [甘特图]         │    │
│  📊 仪表盘  │   ├─────────────────────────────────────────┤    │
│            │   │                                          │    │
│  ─────────  │   │   核心内容区                              │    │
│            │   │   （TaskList / Kanban / Gantt / Detail）  │    │
│  📁 项目    │   │                                          │    │
│   ├ LEST-DEV│   │                                          │    │
│   ├ 移动端App│   │                                          │    │
│   └ 基础设施  │   │                                          │    │
│            │   │                                          │    │
│  ─────────  │   └─────────────────────────────────────────┘    │
│  [⚙️ 设置]  │                                                    │
│            │                                                    │
└────────────┴────────────────────────────────────────────────────┘
```

### 3.2 侧边栏（240px，可折叠）

- 顶部：LEST Logo + 折叠按钮
- 快速入口：收件箱、我的任务、仪表盘
- 项目列表：可展开，显示图标+名称
- 底部：个人设置、管理员入口（仅平台管理员）

### 3.3 Header（56px 固定高度）

- 左侧：面包屑（项目名 > 视图名）
- 中间：全局搜索（Cmd+K 命令面板）
- 右侧：通知铃铛（未读数红点）+ 用户头像下拉菜单

---

## 4. 核心页面设计

### 4.1 登录页（`/login`）

```
┌──────────────────────────────────────────────────────┐
│                                                      │
│              [LEST Logo]                             │
│           欢迎回来，请登录你的账号                      │
│                                                      │
│  ┌────────────────────────────────────────────────┐ │
│  │  用户名 / 邮箱                                   │ │
│  └────────────────────────────────────────────────┘ │
│  ┌────────────────────────────────────────────────┐ │
│  │  密码                               [👁️ 切换]   │ │
│  └────────────────────────────────────────────────┘ │
│                                                      │
│  ☐ 记住登录                                       │
│                                                      │
│  [              登录               ]                  │
│                                                      │
│  ─── 其他登录方式 ───                               │
│  [🔵 GitHub] [🐧 企业微信] [💬 飞书]                │
│                                                      │
│  还没有账号？[立即注册]                               │
└──────────────────────────────────────────────────────┘
```

**技术实现**：
- 使用 `el-form` + `el-input` + `el-button`
- 表单校验：`el-form` 的 `rules` prop
- 记住登录：JWT 存入 `localStorage` vs `sessionStorage`
- 第三方登录：OAuth2 授权跳转

### 4.2 任务列表页（`/project/:id/tasks`）

```
┌─────────────────────────────────────────────────────────────┐
│  LEST-DEV / 任务                    [列表] [看板] [甘特图]  │
│                                      [🔍 筛选 ▾] [+ 创建任务] │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  # │ 类型 │ 任务 │ 状态   │ 负责人  │ 迭代    │ 优先级 │   │
│  ├─────┼──────┼───────┼────────┼────────┼─────────┼───────┤   │
│  │ ☐  │ 🐛  │ 登录页 │ 🔵进行中│ @张三 │ Sprint1 │ 🔴高  │   │
│  │     │     │ 样式错 │        │        │         │       │   │
│  │ ☐  │ 📋  │ API   │ 🟡待办  │ —      │ Sprint1 │ 🟡中  │   │
│  │     │     │ 接口  │        │        │         │       │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  [◀ 上一页]                    第 1/5 页, 共 48 条  [下一页 ▶] │
└─────────────────────────────────────────────────────────────┘
```

**技术实现**：
- `el-table` + `el-table-column` 渲染列表
- 多选：`el-table` 的 `selection-change` 事件
- 无限滚动加载：`el-infinite-scroll`（看板模式下）
- 筛选工具栏：`el-select` 多选 + `el-date-picker` + 快捷筛选

### 4.3 看板页（`/project/:id/board`）

```
┌─────────────────────────────────────────────────────────────────────┐
│  LEST-DEV / 看板                    [列表] [看板✓] [甘特图]        │
│                                         [🔍 快速筛选 ▾]             │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐ │
│  │ 🟡 待办  5  │  │ 🔵 进行中 3 │  │ 🟢 测试  2 │  │ ✅ 已完成 8 │ │
│  │ [WIP: —]    │  │ [WIP: 5]   │  │ [WIP: 3]   │  │ [WIP: —]   │ │
│  ├─────────────┤  ├─────────────┤  ├─────────────┤  ├─────────────┤ │
│  │ ┌─────────┐ │  │ ┌─────────┐ │  │ ┌─────────┐ │  │ ┌─────────┐ │ │
│  │ │🔵 LEST-1│ │  │ │🔵 LEST-3│ │  │ │🔵 LEST-5│ │  │ │🔵 LEST-7│ │ │
│  │ │ 登录页  │ │  │ │ 首页    │ │  │ │ 搜索    │ │  │ │ 注册页  │ │ │
│  │ │ @张三   │ │  │ │ @李四   │ │  │ │ @王五   │ │  │ │ @张三   │ │ │
│  │ │ 🔴高    │ │  │ │ 🟡中    │ │  │ │ 🟢低    │ │  │ │ 🟡中    │ │ │
│  │ └─────────┘ │  │ └─────────┘ │  │ └─────────┘ │  │ └─────────┘ │ │
│  │ [+ 添加任务] │  │ [+ 添加任务] │  │ [+ 添加任务] │  │            │ │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────────────────┘
```

**技术实现**：
- `vuedraggable@next` 驱动列内拖拽排序
- 跨列拖拽：监听 `dragend`，调用 `PUT /api/v1/task/{id}/move`
- 列配置：WIP 限制、超时高亮、折叠列
- 卡片着色：基于优先级/类型/截止日期的 CSS 规则

### 4.4 任务详情抽屉（右侧抽屉，宽度 640px）

```
┌──────────────────────────────────────────────┐
│  LEST-123 · 登录页样式调整           [✕ 关闭] │
│  创建于 2 天前 · 报告人 @张三                 │
├──────────────────────────────────────────────┤
│                                              │
│  [🐛 Bug ▾]  [🔴 高 ▾]                       │
│                                              │
│  ┌────────────────────────────────────────┐ │
│  │ ## 登录页在 Safari 下样式错位           │ │
│  │                                        │ │
│  │ ## 问题描述                            │ │
│  │ Safari 16.4 下 flex 布局异常...        │ │
│  └────────────────────────────────────────┘ │
│                                              │
│  ── 详情 ── ── 活动 ── ── 工时 ── ── 附件 ── │
│                                              │
│  负责人     👤 @李四              [分配给我] │
│  报告人     👤 @张三                         │
│  迭代       📅 Sprint 2 (7月14日-7月28日)    │
│  截止日期   📅 2026-07-15                   │
│  预估工时   ⏱ 3 人天                        │
│  实际工时   ⏱ 2 人天                        │
│  故事点     ⭐ 2 SP                          │
│  标签       [后端] [样式]                    │
│  父子任务   📎 包含 3 个子任务               │
│  关联任务   🔗 阻塞 LEST-124                │
│                                              │
│  ── 评论区 ──                               │
│  👤 @李四 · 2 小时前                        │
│  已修复，PR 已合并到 dev 分支                │
│                                              │
│  ┌────────────────────────────────────────┐ │
│  │ 添加评论...                      [发送] │ │
│  └────────────────────────────────────────┘ │
│                                              │
├──────────────────────────────────────────────┤
│  [编辑]  [分配]  [更多 ▾]        🔖 3 人关注 │
└──────────────────────────────────────────────┘
```

**技术实现**：
- `el-drawer` 右侧抽屉
- 富文本描述：`Monaco Editor` 或 `@tiptap/vue-3`
- Tab 切换：`el-tabs` + `el-tab-pane`
- 评论区：实时提交，WebSocket 推送新评论

---

## 5. 状态管理（Pinia）

### 5.1 Store 划分

```
stores/
├── user.ts          # 用户信息、Token、权限
├── project.ts       # 当前项目、项目列表、成员
├── task.ts         # 任务列表、筛选、分页
├── board.ts        # 看板列、列配置、拖拽状态
├── notification.ts # 通知列表、未读数
└── plugin.ts       # 已加载插件、Fragment 注册表
```

### 5.2 每个 Store 的职责

```typescript
// stores/task.ts — 示例
export const useTaskStore = defineStore('task', () => {
  // === 状态 ===
  const tasks = ref<Task[]>([]);
  const currentTask = ref<Task | null>(null);
  const loading = ref(false);
  const filter = reactive<TaskFilter>({
    projectId: '',
    type: [],
    status: [],
    assigneeId: [],
    iterationId: null,
    priority: [],
    keyword: '',
  });
  const pagination = reactive({ page: 1, size: 20, total: 0 });

  // === Action ===
  async function fetchTasks() {
    loading.value = true;
    try {
      const { records, total } = await taskApi.list({ ...filter, ...pagination });
      tasks.value = records;
      pagination.total = total;
    } finally {
      loading.value = false;
    }
  }

  // 乐观更新
  async function updateTask(id: number, patch: Partial<Task>) {
    const idx = tasks.value.findIndex(t => t.id === id);
    if (idx === -1) return;
    const old = { ...tasks.value[idx] };
    tasks.value[idx] = { ...tasks.value[idx], ...patch };
    try {
      await taskApi.update(id, patch);
    } catch {
      tasks.value[idx] = old; // 回滚
    }
  }

  return { tasks, currentTask, loading, filter, pagination, fetchTasks, updateTask };
});
```

---

## 6. 插件化架构

### 6.1 Extension Point 体系

```typescript
// plugin-host/ExtensionPoint.ts
export type ExtensionPointType =
  // 任务详情页
  | 'task-detail.header.actions'
  | 'task-detail.field.sidebar'
  | 'task-detail.tabs'
  | 'task-detail.quick.actions'
  // 任务列表
  | 'task-list.toolbar'
  | 'task-list.card.footer'
  // 项目
  | 'project-detail.tabs'
  // 全局
  | 'dashboard.gadget'
  | 'settings.panel';

export interface Fragment {
  id: string;
  pluginId: string;
  extensionPoint: ExtensionPointType;
  label: string;
  order: number;
  component: ComponentType;  // Vue 组件
}
```

### 6.2 插件加载流程

```
应用启动
    ↓
PluginEngine.init()
    ↓
加载 manifest.json（来自后端 lest-plugin 服务）
    ↓
验证签名（RSA 或 HMAC）
    ↓
注册所有 Fragment → FragmentRegistry
    ↓
前端路由匹配 → 渲染 FragmentRenderer
```

### 6.3 内置插件（编译时注入）

```
内置插件不经过动态加载，直接 import：
src/plugins/
├── lest-worklog/          # 工时插件（内置）
│   ├── fragments/
│   │   ├── WorklogTab.vue  # 注入到 task-detail.tabs
│   │   └── WorklogField.vue  # 注入到 task-detail.field.sidebar
│   └── api/
│       └── worklog.ts
├── lest-cicd/             # CI/CD 插件（内置）
└── lest-ai/              # AI 插件（内置）
```

---

## 7. API 请求设计

### 7.1 Axios 实例配置

```typescript
// api/axios.ts
const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 30000,
});

// 请求拦截器：注入 Token
service.interceptors.request.use((config) => {
  const token = localStorage.getItem('access_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 响应拦截器：统一错误处理
service.interceptors.response.use(
  (response) => response.data,
  (error) => {
    if (error.response?.status === 401) {
      // Token 过期，跳转登录
      router.push('/login');
    }
    return Promise.reject(error);
  }
);
```

### 7.2 API 模块划分

| 模块 | 文件 | 主要接口 |
|------|------|---------|
| 认证 | `api/auth.ts` | login/logout/register/refreshToken/profile |
| 项目 | `api/project.ts` | list/detail/create/update/archive/members |
| 任务 | `api/task.ts` | list/detail/create/update/delete/move/batch |
| 看板 | `api/board.ts` | boardData/moveCard |
| 迭代 | `api/iteration.ts` | list/create/update/start/complete |
| 通知 | `api/notification.ts` | list/markRead/markAllRead |

---

## 8. Element Plus 按需引入策略

### 8.1 必须全量引入的组件

全局注册（`main.ts` 中 `app.use(ElementPlus)`）用于布局和通用场景：
- `ElButton`、`ElInput`、`ElForm` 等基础组件
- `ElTable`、`ElPagination` 等数据展示
- `ElDialog`、`ElDrawer`、`ElMessage` 等反馈

### 8.2 按需引入的组件

```typescript
// vite.config.ts — 自动导入（推荐）
import AutoImport from 'unplugin-auto-import/vite';
import Components from 'unplugin-vue-components/vite';
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers';

export default {
  plugins: [
    AutoImport({
      resolvers: [ElementPlusResolver()],
    }),
    Components({
      resolvers: [ElementPlusResolver()],
    }),
  ],
};
```

---

## 9. 与后端的接口契约

### 9.1 统一响应格式

```typescript
// 成功
interface ApiResponse<T> {
  code: number;      // 200 = 成功
  message: string;   // 描述信息
  data: T;           // 泛型数据
  timestamp: number;
}

// 分页
interface PageResult<T> {
  records: T[];
  total: number;
  page: number;
  size: number;
}

// 错误
interface ApiError {
  code: number;      // 4xx / 5xx
  message: string;
  traceId?: string;  // 请求追踪 ID
}
```

### 9.2 认证流程

```
登录 POST /api/v1/auth/login
  → { access_token, refresh_token, user }
  → 存入 localStorage + Pinia userStore

请求 Authorization: Bearer <token>

Token 过期 → 401 → 自动 refresh
  → POST /api/v1/auth/refresh
  → 更新 localStorage

登出 → DELETE /api/v1/auth/logout
  → 清除 localStorage → 跳转 /login
```

---

## 10. H5 响应式适配

### 10.1 断点策略

| 断点 | 宽度 | 布局变化 |
|------|------|---------|
| `xs` | < 768px | 侧边栏收起为图标模式，汉堡菜单 |
| `md` | 768px - 1200px | 侧边栏收窄（200px→60px）|
| `lg` | 1200px - 1920px | 完整侧边栏（240px）|
| `xl` | > 1920px | 宽屏优化（内容区最大宽度 1400px）|

### 10.2 H5 特殊处理

- 看板：移动端使用单列滑动模式（而非多列并排）
- 任务详情：移动端使用全屏页面（而非抽屉）
- 表格：移动端使用卡片列表替代
- 拖拽：移动端使用长按激活

---

## 版本历史

| 版本 | 日期 | 修改内容 | 作者 |
|------|------|---------|------|
| V1.0 | 2026-06-03 | 初始版本：从 ele-admin-plus-ts 迁移到纯 Element Plus | 架构师 |
