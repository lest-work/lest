# UI 插件化架构设计
> **定位**：本文档定义 LEST Platform 前端插件化架构，使 UI 层具备和 Jira / VS Code 同级别的可扩展性。
>
> **前置阅读**：
> - [插件系统 PRD](../plugins/V1.0/lest-plugin-sdk.md)

## 文档信息

| 属性 | 内容 |
|------|------|
| 版本 | V1.0 |
| 状态 | 设计中 |
| 创建日期 | 2026-06-02 |

---

## 1. 现状问题：前端是硬编码的

### 1.1 当前问题

现在的 `frontend-pc/` 是一个典型的前端单体应用：

```
src/views/
├── task/
│   └── index.vue          ← 任务列表，代码写死
├── project/
│   └── detail/
│       └── index.vue      ← 项目详情，代码写死
└── system/               ← 系统管理（传统OA思路）
    └── user/             ← ...
```

**问题**：
- 任务详情页的字段、Tab、按钮全都是写死的
- 如果要做一个「工时插件」在任务详情页加一个"工时"Tab，必须改 `task/index.vue` 源码
- 如果要做「CI/CD 插件」在任务详情页加一个"构建"Tab，也要改源码
- 每个插件都要改 Core 代码 → 违反了插件独立性的原则
- 而且改了就再也分不开了

### 1.2 对比：Jira 和 VS Code 是怎么做的

**Jira 的做法**：
- 任务详情页有一个 **App Hilton**（第三方应用面板）
- 插件开发者通过 Atlassian Forge 注册 Fragment（UI 片段）
- Fragment 可以注入到：任务详情页 Tab、侧边字段、表单字段、按钮、操作菜单
- Jira Core 代码完全不感知插件的存在

**VS Code 的做法**：
- 有一个 **Extension Host** 运行插件代码
- `package.json` 中声明 `contributes.viewsContainers`、`contributes.views` 等扩展点
- 插件可以注册：侧边栏 TreeItem、Webview 面板、编辑器装饰、命令、快捷键
- 核心完全不感知插件

### 1.3 LEST 应该怎么做

LEST 的前端应该：

1. **Core 本身是可空的** — 任务详情页不包含任何业务功能，只有一个"插件容器"
2. **所有业务功能通过插件注入** — 「工时」是 `lest-worklog` 插件注入的 Tab
3. **插件注册到扩展点** — 插件在 `manifest.json` 中声明要在哪个扩展点注入什么

---

## 2. 核心概念

### 2.1 扩展点（Extension Point）

扩展点是 LEST 前端预定义的可扩展位置。插件可以向扩展点注册 UI 片段（Fragment）。

### 2.2 扩展点清单（Task 详情页）

```
TaskDetailPage（任务详情页）
├── task-detail.header.actions       ← 头部操作按钮区
│   └── 例如：复制任务链接、导出任务
├── task-detail.field-sidebar.left   ← 左侧字段区（靠近指派人）
│   └── 例如：工时预估、工时实际（lest-worklog）
├── task-detail.field-sidebar.right  ← 右侧字段区（靠近优先级）
│   └── 例如：CI 构建状态（lest-cicd）
├── task-detail.tab                 ← Tab 区域
│   ├── task-detail.tab.comment     ← 评论（Core 内置）
│   ├── task-detail.tab.activity    ← 活动（Core 内置）
│   ├── task-detail.tab.worklog     ← 工时（lest-worklog 注入）
│   ├── task-detail.tab.cicd       ← 构建（lest-cicd 注入）
│   └── task-detail.tab.custom      ← 插件可注册任意 Tab
├── task-detail.toolbar             ← 详情页工具栏
│   └── 例如：任务关系图（lest-xxx）
└── task-detail.context-menu        ← 右键菜单
    └── 例如：添加到某个聚合视图
```

### 2.3 扩展点清单（Project 详情页）

```
ProjectDetailPage（项目详情页）
├── project-detail.tab              ← Tab 区域
│   ├── project-detail.tab.overview ← 概览（Core 内置）
│   ├── project-detail.tab.issues   ← 问题列表（Core 内置）
│   ├── project-detail.tab.board    ← 看板（Core 内置）
│   ├── project-detail.tab.reports  ← 报表（lest-report 注入）
│   └── project-detail.tab.meeting  ← 会议纪要（lest-meeting 注入）
├── project-detail.sidebar-widget   ← 侧边小组件
│   └── 例如：项目健康度（lest-ai）
└── project-detail.header-actions  ← 头部操作
    └── 例如：发布版本（lest-release）
```

### 2.4 扩展点清单（全局）

```
GlobalExtensionPoints
├── global.sidebar                 ← 全局左侧栏（导航菜单下方）
│   └── 例如：WakaTime 热力图小组件（lest-wakatime）
├── global.notification-center    ← 通知中心
│   └── 允许插件注册自定义通知类型
├── global.command-palette         ← 命令面板（Ctrl+K）
│   └── 允许插件注册命令
└── global.dashboard              ← 首页仪表盘
    └── 允许插件注册小组件（lest-ai 注册"团队健康度"）
```

---

## 3. 插件前端 SDK

### 3.1 插件前端结构

```
my-lest-plugin/
├── manifest.json                  ← 插件清单（声明扩展点）
├── src/
│   ├── index.ts                  ← 插件入口（注册所有扩展）
│   ├── views/                    ← 插件的 Vue 组件
│   │   ├── WorklogTab.vue      ← 注入到 task-detail.tab
│   │   ├── WorklogField.vue     ← 注入到 task-detail.field-sidebar
│   │   └── WorklogPanel.vue     ← 独立页面
│   ├── components/               ← 共享组件
│   └── api/                      ← 调用后端 API
└── package.json
```

### 3.2 manifest.json 示例

```json
{
  "id": "lest-worklog",
  "version": "1.0.0",
  "name": "工时管理",
  "description": "任务工时记录、统计、报表",
  "author": "LEST Team",
  "frontend": {
    "entry": "./src/index.ts",
    "extensions": [
      {
        "point": "task-detail.tab",
        "id": "worklog-tab",
        "label": "工时",
        "icon": "Clock",
        "order": 10,
        "component": "./src/views/WorklogTab.vue"
      },
      {
        "point": "task-detail.field-sidebar.left",
        "id": "worklog-estimate-field",
        "label": "预估工时",
        "order": 20,
        "component": "./src/views/WorklogEstimateField.vue"
      },
      {
        "point": "project-detail.tab",
        "id": "worklog-report-tab",
        "label": "工时报表",
        "order": 30,
        "component": "./src/views/WorklogReportTab.vue"
      }
    ]
  }
}
```

### 3.3 插件入口示例

```typescript
// src/index.ts
import { definePlugin } from '@lest/plugin-sdk';

export default definePlugin({
  id: 'lest-worklog',
  onInit(runtime) {
    // 注册扩展点
    runtime.registerExtension({
      point: 'task-detail.tab',
      id: 'worklog-tab',
      label: '工时',
      component: () => import('./views/WorklogTab.vue'),
    });

    runtime.registerExtension({
      point: 'task-detail.field-sidebar.left',
      id: 'worklog-estimate',
      label: '预估工时',
      component: () => import('./views/WorklogEstimate.vue'),
    });

    // 注册 API 路由（后端插件已在 manifest.json 中声明）
    runtime.registerAPI({
      path: '/worklog',
      handlers: worklogHandlers,
    });
  },
});
```

---

## 4. 插件运行时（Plugin Runtime）

### 4.1 核心架构

```
┌─────────────────────────────────────────────────────────────┐
│                    Plugin Runtime（Core 提供）                  │
│                                                              │
│  ┌─────────────┐   ┌──────────────┐   ┌────────────────┐   │
│  │ Plugin      │   │ Extension    │   │ Component      │   │
│  │ Registry    │ → │ Resolver    │ → │ Registry       │   │
│  │ (注册所有插件) │   │ (按扩展点查询)   │ (按位置渲染)     │   │
│  └─────────────┘   └──────────────┘   └────────────────┘   │
│         │                 ↑                                    │
│         ↓                 │                                    │
│  ┌─────────────┐   ┌──────────────┐                          │
│  │ Manifest    │   │ Extension    │                          │
│  │ Loader      │   │ Context     │                          │
│  │ (加载插件)   │   │ (提供API)    │                          │
│  └─────────────┘   └──────────────┘                          │
└─────────────────────────────────────────────────────────────┘
                              ↑
┌─────────────────────────────────────────────────────────────┐
│                    已安装的插件                                │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ lest-worklog │  │ lest-cicd   │  │ lest-ai      │      │
│  │ 注册了3个扩展点│  │ 注册了2个扩展点│  │ 注册了5个扩展点│      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
```

### 4.2 PluginRegistry — 插件注册表

负责从后端获取已启用的插件列表，并加载插件 manifest。

```typescript
// 伪代码
class PluginRegistry {
  private plugins: Map<string, PluginManifest> = new Map();

  async loadAll(): Promise<void> {
    // 从后端获取已启用插件列表
    const enabledPlugins = await api.get('/plugin/enabled');
    for (const plugin of enabledPlugins) {
      await this.loadPlugin(plugin);
    }
  }

  async loadPlugin(pluginId: string): Promise<void> {
    const manifest = await api.get(`/plugin/${pluginId}/manifest`);
    this.plugins.set(pluginId, manifest);
    // 触发 ExtensionResolver 重新解析
    extensionResolver.rebuild();
  }

  getExtensions(point: ExtensionPoint): Extension[] {
    // 返回指定扩展点的所有插件扩展
  }
}
```

### 4.3 ExtensionResolver — 扩展点解析器

根据当前上下文（任务详情页），解析出所有需要渲染的扩展。

```typescript
// 伪代码
class ExtensionResolver {
  private registry: PluginRegistry;

  resolve(page: 'task-detail', context: { taskId: number }): ResolvedExtension[] {
    const extensions = this.registry.getExtensions('task-detail.tab');
    return extensions
      .filter(e => this.hasPermission(e))
      .sort((a, b) => a.order - b.order);
  }
}
```

### 4.4 ComponentRegistry — 组件注册表

动态渲染插件注册的 Vue 组件。

```vue
<!-- TaskDetailTabs.vue — Core 提供，插件感知不到的壳 -->
<template>
  <el-tabs v-model="activeTab">
    <!-- Core 内置 Tab -->
    <el-tab-pane label="评论" name="comment">
      <CommentList />
    </el-tab-pane>
    <el-tab-pane label="活动" name="activity">
      <ActivityTimeline />
    </el-tab-pane>

    <!-- 插件注入的 Tab（动态渲染）-->
    <el-tab-pane
      v-for="ext in pluginTabs"
      :key="ext.id"
      :label="ext.label"
      :name="ext.id"
    >
      <Suspense>
        <component :is="ext.component" v-bind="ext.props" />
      </Suspense>
    </el-tab-pane>
  </el-tabs>
</template>

<script setup lang="ts">
// Core 代码：只负责渲染插件注册的内容，完全不感知插件
const pluginTabs = useExtensionResolver().resolve('task-detail.tab', { taskId });
</script>
```

---

## 5. Task 详情页的插件化改造

### 5.1 改造前（硬编码）

```vue
<!-- 任务详情页 — 当前硬编码方式 -->
<template>
  <div class="task-detail">
    <!-- 写死的字段 -->
    <div class="fields">
      <span>指派人: {{ task.assignee }}</span>
      <span>优先级: {{ task.priority }}</span>
      <!-- ⚠️ 如果要加工时字段，必须改这里 -->
    </div>

    <!-- 写死的 Tab -->
    <el-tabs>
      <el-tab-pane label="评论">...</el-tab-pane>
      <el-tab-pane label="活动">...</el-tab-pane>
      <!-- ⚠️ 如果要加工时 Tab，必须改这里 -->
    </el-tabs>
  </div>
</template>
```

### 5.2 改造后（插件化）

```vue
<!-- 任务详情页 — 插件化后的壳 -->
<template>
  <div class="task-detail">
    <!-- 头部：Core 字段 + 插件字段 -->
    <div class="fields">
      <!-- Core 内置字段 -->
      <CoreField label="指派人">
        <AssigneeSelect v-model="task.assigneeId" />
      </CoreField>
      <CoreField label="优先级">
        <PrioritySelect v-model="task.priority" />
      </CoreField>

      <!-- 插件注入的左侧字段 -->
      <template v-for="field in leftFields" :key="field.id">
        <CoreField :label="field.label">
          <component :is="field.component" v-bind="field.props" />
        </CoreField>
      </template>
    </div>

    <!-- Tab 区：Core Tab + 插件 Tab -->
    <el-tabs v-model="activeTab">
      <!-- Core 内置 Tab（不可删除，因为是 Core 功能）-->
      <el-tab-pane label="评论" name="comment">
        <CommentList />
      </el-tab-pane>
      <el-tab-pane label="活动" name="activity">
        <ActivityTimeline />
      </el-tab-pane>

      <!-- 插件动态注入的 Tab -->
      <el-tab-pane
        v-for="tab in pluginTabs"
        :key="tab.id"
        :label="tab.label"
        :name="tab.id"
      >
        <Suspense>
          <component
            :is="tab.component"
            v-bind="tab.props"
          />
        </Suspense>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
// 只用一行代码获取插件注册的扩展
const pluginTabs = useExtensionResolver().resolve('task-detail.tab', { taskId });
const leftFields = useExtensionResolver().resolve('task-detail.field-sidebar.left', { taskId });
</script>
```

### 5.3 插件注入效果

```
安装 lest-worklog 插件前：
┌────────────────────────────────────────────────────────────┐
│ 任务详情                    [编辑] [更多 ▾]                   │
├────────────────────────────────────────────────────────────┤
│ 字段： 指派人 | 优先级 | 截止日期 | 迭代                       │
├────────────────────────────────────────────────────────────┤
│ [评论] [活动]                                               │
└────────────────────────────────────────────────────────────┘

安装 lest-worklog 插件后（插件自动注入，无需改 Core）：
┌────────────────────────────────────────────────────────────┐
│ 任务详情                    [编辑] [更多 ▾]                   │
├────────────────────────────────────────────────────────────┤
│ 字段： 指派人 | 优先级 | 截止日期 | 迭代 | [预估工时: 8h]      │
├────────────────────────────────────────────────────────────┤
│ [评论] [活动] [工时]                                        │
│                                                             │
│  工时记录                                                    │
│  06-01  张三  2h  实现登录功能                              │
│  06-02  张三  3h  实现注册功能                              │
│  [+ 记录工时]                                               │
└────────────────────────────────────────────────────────────┘

再安装 lest-cicd 插件后：
┌────────────────────────────────────────────────────────────┐
│ 任务详情                    [编辑] [更多 ▾]                   │
├────────────────────────────────────────────────────────────┤
│ 字段： 指派人 | 优先级 | 截止日期 | 迭代 | 预估工时 | [CI: ✓]│
├────────────────────────────────────────────────────────────┤
│ [评论] [活动] [工时] [构建]                                  │
│                                                             │
│  构建历史                                                    │
│  #156 ✓ 成功   feature/login   3分12秒   张三  2分钟前      │
└────────────────────────────────────────────────────────────┘
```

---

## 6. Project 详情页的插件化改造

### 6.1 改造方案

```
ProjectDetailPage
├── 项目头部（名称 + 描述 + 操作按钮）
├── 项目导航栏（概览/任务/看板/迭代/... + 插件注入的 Tab）
│   ├── 概览（Core 内置）
│   ├── 任务列表（Core 内置）
│   ├── 看板（Core 内置）
│   ├── 迭代（Core 内置）
│   ├── [工时报表]（lest-worklog 注入）
│   ├── [发布管理]（lest-release 注入）
│   ├── [会议纪要]（lest-meeting 注入）
│   └── [报表中心]（lest-report 注入）
├── 侧边栏小组件（插件可注册）
└── 项目设置（插件可注册配置项）
```

---

## 7. 与后端插件的协同

### 7.1 前端插件 + 后端插件 = 完整功能

```
lest-worklog 插件（完整功能）
├── frontend/                    ← 前端插件包
│   ├── manifest.json           ← 声明 UI 扩展点
│   └── src/views/WorklogTab.vue
└── backend/                    ← 后端插件包
    ├── plugin.json             ← 声明 API 路由、数据库表
    └── src/WorklogService.java
```

### 7.2 前端独立插件

有些插件可以只有前端，不需要后端（如主题插件、UI 增强插件）。

```json
// 仅前端插件的 manifest.json
{
  "id": "lest-dark-theme",
  "name": "深色主题增强",
  "frontend": {
    "entry": "./src/index.ts",
    "extensions": [
      {
        "point": "global.theme",
        "id": "dark-theme-enhanced",
        "component": "./src/views/DarkTheme.vue"
      }
    ]
  },
  "backend": null  // 没有后端
}
```

---

## 8. 技术实现路径

### Phase 1：插件运行时基础设施（V2.0 Core）

1. 实现 `PluginRegistry`（插件注册表）
2. 实现 `ExtensionResolver`（扩展点解析器）
3. 实现 `ComponentRegistry`（组件注册表）
4. 实现任务详情页的插件化改造
5. 实现项目管理页的插件化改造

### Phase 2：SDK 完善（V2.0 SDK）

1. 提供 `definePlugin()` 工厂函数
2. 提供 `useExtensionResolver()` composable
3. 提供 TypeScript 类型定义（ExtensionPoint、Extension 等）
4. 提供 `usePluginAPI()` 调用后端插件 API

### Phase 3：更多扩展点（V3.0）

1. 首页仪表盘扩展点
2. 全局侧边栏扩展点
3. 任务批量操作扩展点
4. 看板列扩展点
5. 工作流配置 UI 扩展点

---

## 9. 与现有 lest-plugin-sdk.md 的关系

本文档补充了 `lest-plugin-sdk.md` 中缺失的 **前端插件运行时** 部分。

| 文档 | 关注点 |
|------|--------|
| lest-plugin-sdk.md | 后端插件架构（生命周期、API 路由、数据隔离）|
| **UI插件化架构设计.md** | **前端插件架构（Extension Point、动态渲染、运行时）** |

两者共同构成完整的 LEST 插件化体系。

---

## 10. 版本历史

| 版本 | 日期 | 修改内容 |
|------|------|---------|
| V1.0 | 2026-06-02 | 初始版本，定义 UI 扩展点体系、插件运行时架构、Task/Project 插件化改造方案 |
