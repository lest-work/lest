---
description: LEST Platform 前端 Vue 3 / TypeScript 开发规范
---

# 前端开发规范

## 1. API 层规范（最重要）

### 1.1 目录结构（强制）

每个业务模块的 API **必须**按如下结构分离：

```
src/api/{module}/
├── index.ts          # API 请求函数
└── model/
    └── index.ts      # TypeScript 类型定义
```

示例：`src/api/system/user/index.ts` + `src/api/system/user/model/index.ts`

### 1.2 通用类型（来自 src/api/index.ts）

```typescript
// ✅ 正确引用
import type { AjaxResult, TableDataInfo, PageParam } from '@/api';

// AjaxResult<T>: code(200=成功), msg(非message!), data
// TableDataInfo<T>: code, msg, rows(非records!), total
// PageParam: pageNum, pageSize
```

**关键字段名（与后端 RuoYi 格式对齐）：**

- 消息字段：`msg`（NOT `message`）
- 列表字段：`rows`（NOT `records` / `list`）
- 分页参数：`pageNum` / `pageSize`（NOT `page` / `size`）

### 1.3 model/index.ts 规范

```typescript
// src/api/project/model/index.ts
import type { PageParam } from '@/api';

/**
 * 项目
 */
export interface Project {
  /** 主键ID */
  id?: number;
  /** 项目名称 */
  name?: string;
  // 所有字段必须与后端 Domain 字段名完全一致（驼峰）
  // 非数据库字段需注释说明
  /** 进度（非数据库字段，前端计算） */
  progress?: number;
}

/**
 * 项目查询参数
 */
export interface ProjectParam extends PageParam {
  name?: string;
}
```

### 1.4 index.ts 规范

```typescript
// src/api/project/index.ts
import request from '@/utils/request';
import type { AjaxResult, TableDataInfo } from '@/api';
import type { Project, ProjectParam } from './model';

// 重新导出类型供组件使用
export type { Project, ProjectParam } from './model';

/**
 * 分页查询项目列表
 */
export async function pageProjects(params?: ProjectParam): Promise<TableDataInfo<Project>> {
  const res = await request.get<TableDataInfo<Project>>('/project/list', { params });
  if (res.data.code === 200) return res.data;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 新增项目
 */
export async function addProject(data: Project): Promise<string> {
  const res = await request.post<AjaxResult<unknown>>('/project', data);
  if (res.data.code === 200) return res.data.msg;
  return Promise.reject(new Error(res.data.msg));
}
```

### 1.5 URL 路径规范

```
前端调用:  /api/{service-prefix}/{resource}/{action}
Vite 代理: 去掉 /api → 转发到 gateway:8080
网关路由:  StripPrefix=1 → 去掉 {service-prefix} → 转发到对应微服务
```

示例：
- `request.get('/system/user/list')` → 最终到 lest-system 的 `/user/list`
- `request.get('/project/list')` → 最终到 lest-project 的 `/list`
- `request.get('/task/list')` → 最终到 lest-task 的 `/list`

## 2. 组件规范

### 2.1 视图组件位置

```
src/views/
├── index/                  # 工作台首页
│   ├── index.vue
│   └── components/
│       ├── activities-card.vue   # 最新动态
│       ├── task-card.vue         # 我的任务
│       ├── project-card.vue      # 项目进度
│       ├── user-list.vue         # 小组成员
│       └── goal-card.vue         # 本月目标
├── system/
│   ├── user/
│   │   ├── index.vue
│   │   └── components/
│   └── ...
└── project/
    └── index.vue
```

### 2.2 组件编写规范

```vue
<template>
  <ele-card :header="title" ...>
    <!-- 使用 el-empty 处理空数据状态 -->
    <el-empty v-if="!loading && !list.length" description="暂无数据" />
  </ele-card>
</template>

<script setup>
  import { ref, onMounted } from 'vue';
  // 从对应 API 模块导入
  import { pageProjects } from '@/api/project';

  // 数据加载统一模式
  const list = ref([]);
  const loading = ref(false);

  const loadData = async () => {
    loading.value = true;
    try {
      const res = await pageProjects({ pageNum: 1, pageSize: 10 });
      list.value = res.rows ?? [];
    } catch {
      list.value = [];
    } finally {
      loading.value = false;
    }
  };

  // handleCommand 处理刷新
  const handleCommand = (command) => {
    if (command === 'refresh') loadData();
    emit('command', command);
  };

  onMounted(loadData);
</script>
```

### 2.3 使用 useUserStore 获取当前用户

```typescript
import { useUserStore } from '@/store/modules/user';

const userStore = useUserStore();
const userId = userStore.info?.userId;      // number | undefined
const userName = userStore.info?.userName;  // string | undefined
```

## 3. 状态管理（Pinia）规范

Store 文件位置：`src/store/modules/{name}.ts`

引用方式：
```typescript
import { useUserStore } from '@/store/modules/user';
import { useThemeStore } from '@/store/modules/theme';
```

## 4. 路由规范

路由配置在 `src/router/` 下，菜单路由由后端 `sys_menu` 动态生成，不在前端硬编码。

## 5. UI 组件使用规范

优先使用顺序：
1. `EleAdminPlus` 组件（`ele-card`, `ele-pro-table`, `ele-text`, `ele-ellipsis`, `ele-page`）
2. `Element Plus` 组件（`el-button`, `el-dialog`, `el-form`, `el-table`）
3. 自定义组件（放在 `src/components/`）

## 6. 注释规范

- 所有 API 函数必须有 JSDoc 注释
- 接口字段必须有行注释，非数据库字段需特别说明
- Vue 组件内的 `ref`/`computed` 变量需简短注释

## 7. 错误处理规范

- API 层：统一 `return Promise.reject(new Error(res.data.msg))`
- 组件层：使用 try/catch，catch 块设置空数组/空值，不抛出
- 401 认证错误由 `src/utils/request.ts` 拦截统一处理，组件无需处理
