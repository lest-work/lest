---
description: API 设计规范与响应格式标准
---

# LEST API 设计规范

## 1. 请求规范

### 1.1 URL 路径设计
- **格式**：`/api/{service}/{resource}/{action}`
- **示例**：
  - `GET /api/project/list` - 项目列表
  - `GET /api/project/{id}` - 项目详情
  - `POST /api/project` - 新增项目
  - `PUT /api/project` - 修改项目
  - `DELETE /api/project/{id}` - 删除项目
  - `PUT /api/project/{id}/archive` - 归档项目（特殊操作）

### 1.2 HTTP 方法
- **GET**：查询、列表、详情、导出
- **POST**：新增、导入、批量操作、特殊创建操作
- **PUT**：修改、更新状态、特殊操作
- **DELETE**：删除、批量删除

### 1.3 查询参数
```typescript
// 分页参数（ProTable 标准）
pageNum?: number;      // 页码，从 1 开始
pageSize?: number;     // 每页数量，默认 10

// 排序参数
orderByColumn?: string; // 排序字段
isAsc?: string;        // 'asc' 或 'desc'

// 过滤参数（根据业务定义）
status?: number;
title?: string;
projectId?: number;
```

### 1.4 请求头
```
Content-Type: application/json
Authorization: Bearer {token}
X-Request-ID: {uuid}  // 可选，用于链路追踪
```

## 2. 响应规范

### 2.1 成功响应（200）

#### 列表响应（TableDataInfo）
```json
{
  "code": 200,
  "msg": "操作成功",
  "total": 100,
  "rows": [
    { "id": 1, "name": "项目1", ... },
    { "id": 2, "name": "项目2", ... }
  ]
}
```

**字段说明**：
- `code`: 200 表示成功
- `msg`: 操作提示信息
- `total`: 总记录数（分页时）
- `rows`: 数据数组

#### 详情/单条响应（AjaxResult）
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 1,
    "name": "项目1",
    "description": "项目描述",
    ...
  }
}
```

**字段说明**：
- `code`: 200 表示成功
- `msg`: 操作提示信息
- `data`: 响应数据对象

#### 操作响应（新增/修改/删除）
```json
{
  "code": 200,
  "msg": "新增成功"
}
```

### 2.2 错误响应

#### 400 - 请求参数错误
```json
{
  "code": 400,
  "msg": "参数验证失败：title 不能为空"
}
```

#### 401 - 未授权
```json
{
  "code": 401,
  "msg": "请先登录"
}
```

#### 403 - 无权限
```json
{
  "code": 403,
  "msg": "您没有权限执行此操作"
}
```

#### 404 - 资源不存在
```json
{
  "code": 404,
  "msg": "项目不存在"
}
```

#### 500 - 服务器错误
```json
{
  "code": 500,
  "msg": "系统内部错误，请稍后重试"
}
```

## 3. 前后端交互约定

### 3.1 API 文件结构（前端）
```typescript
// src/api/{service}/index.ts
import type { AjaxResult, TableDataInfo, PageParam } from '@/api';
import type { Project, ProjectParam } from './model';

// 列表接口
export function pageProjects(param: ProjectParam): Promise<TableDataInfo<Project>> {
  return request.get('/project/list', { params: param });
}

// 详情接口
export function getProject(id: number): Promise<Project> {
  return request.get(`/project/${id}`).then(res => res.data);
}

// 新增接口
export function addProject(data: Project): Promise<void> {
  return request.post('/project', data);
}

// 修改接口
export function updateProject(data: Project): Promise<void> {
  return request.put('/project', data);
}

// 删除接口
export function removeProject(id: number): Promise<void> {
  return request.delete(`/project/${id}`);
}
```

### 3.2 模型定义（前端）
```typescript
// src/api/{service}/model/index.ts
import type { PageParam } from '@/api';

export interface Project {
  id?: number;
  name?: string;
  description?: string;
  status?: number;
  ownerId?: number;
  ownerName?: string;
  startDate?: string;
  endDate?: string;
}

export interface ProjectParam extends PageParam {
  name?: string;
  status?: number;
  ownerId?: number;
}
```

### 3.3 后端 Controller 规范
```java
@RestController
@RequestMapping("")
public class ProjectController extends BaseController {
  
  @RequiresPermissions("project:project:list")
  @GetMapping("/list")
  public TableDataInfo list(Project project) {
    startPage();
    List<Project> list = projectService.selectProjectList(project);
    return getDataTable(list);
  }
  
  @RequiresPermissions("project:project:query")
  @GetMapping("/{id}")
  public AjaxResult getInfo(@PathVariable Long id) {
    return success(projectService.selectProjectById(id));
  }
  
  @RequiresPermissions("project:project:add")
  @Log(title = "项目管理", businessType = BusinessType.INSERT)
  @PostMapping
  public AjaxResult add(@RequestBody Project project) {
    return toAjax(projectService.insertProject(project));
  }
}
```

## 4. 常见错误处理

### 4.1 参数验证
```java
// 后端验证
if (StringUtils.isEmpty(project.getName())) {
  return error("项目名称不能为空");
}

// 前端验证
const rules = {
  name: [
    { required: true, message: '项目名称不能为空', trigger: 'blur' },
    { min: 2, max: 50, message: '项目名称长度 2-50 字符', trigger: 'blur' }
  ]
};
```

### 4.2 业务异常
```java
// 后端抛出异常
if (project == null) {
  throw new ServiceException("项目不存在");
}

// 前端捕获异常
.catch(error => {
  EleMessage.error({ message: error.message, plain: true });
});
```

## 5. API 版本管理

- 当前版本：**v1**（隐含在路径中）
- 版本升级时，在路径中显式标记：`/api/v2/project/list`
- 保持向后兼容，逐步废弃旧版本

## 6. API 文档

- 使用 **Swagger/OpenAPI** 自动生成 API 文档
- 访问地址：`http://localhost:8080/swagger-ui.html`
- 每个 Controller 方法必须添加 `@Operation` 和 `@Parameter` 注解
