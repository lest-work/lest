| 属性 | 内容 |
| 版本 | V1.2 |
| 创建日期 | 2026-05-26 |
| 最后更新 | 2026-05-27 |

---

## 1. 文档概述

本文档汇总了 LEST Platform 所有微服务的 API 接口定义，包括认证服务、任务管理、项目管理、代码管理、CI/CD、WakaTime、会议管理、通知消息、团队绩效和 AI 服务等模块。

### 服务端口概览

| 服务名称 | 服务标识 | 默认端口 | Base Path |
|---------|---------|---------|-----------|
| API 网关 | lest-gateway | 8080 | / |
| 认证服务 | lest-auth | 8096 | /auth |
| 系统管理服务 | lest-system | 8081 | /system |

> 其他微服务（任务、项目、代码等）待实现，端口规划见下方。

**规划端口**

| 服务名称 | 服务标识 | 规划端口 |
|---------|---------|----------|
| 任务服务 | lest-task | 8082 |
| 项目服务 | lest-project | 8083 |
| 代码服务 | lest-code | 8084 |
| CI 服务 | lest-ci | 8085 |
| 通知服务 | lest-notification | 8086 |
| AI 服务 | lest-ai | 8087 |

---

## 2. 通用说明

### 2.1 认证方式

除特殊说明外，所有 API 请求均需要在 Header 中携带认证 Token：

```
Authorization: Bearer {accessToken}
```

**Token 说明**：
- `accessToken`：用户登录后获取的访问令牌，有效期 15 分钟
- `refreshToken`：刷新令牌，用于获取新的 accessToken，有效期 7 天

### 2.2 内部服务调用

微服务之间通过内部 Token 进行调用：

```
X-Internal-Token: {内部调用Token}
```

### 2.3 Webhook 认证

Webhook 回调使用签名验证：

```
X-Hub-Signature-256: {签名}        # GitHub Webhook
X-Gitlab-Token: {签名}            # GitLab Webhook
X-Jenkins-Token: {签名}            # Jenkins Webhook
```

### 2.4 响应格式

所有 API 采用统一的 JSON 响应格式：

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

**code 说明**：
- `200`：成功
- 其他：业务错误码（见各模块错误码定义）

### 2.5 分页格式

列表查询接口采用统一分页格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [...],
    "total": 100,
    "page": 1,
    "size": 20
  }
}
```

**分页参数**：
| 参数 | 类型 | 说明 |
|------|------|------|
| page | int | 页码，从 1 开始 |
| size | int | 每页条数，默认 20 |

### 2.6 日期时间格式

- 日期格式：`YYYY-MM-DD`
- 时间格式：`YYYY-MM-DD HH:mm:ss`
- 时间戳：Unix 时间戳（秒）

### 2.7 通用错误码

| 错误码 | 说明 |
|--------|------|
| 9000 | VALIDATION_ERROR - 参数校验失败 |
| 9001 | PARAM_MISSING - 缺少必需参数 |
| 9002 | PARAM_TYPE_ERROR - 参数类型错误 |
| 9500 | PERMISSION_DENIED - 权限不足 |
| 9999 | SYSTEM_ERROR - 系统内部错误 |
| 9998 | SERVICE_UNAVAILABLE - 服务暂不可用 |

---

## 3. 认证服务 API (lest-auth)

**服务标识**：lest-auth
**默认端口**：8096
**Base Path**：/auth, /organization, /dictionary, /operation-log

### 3.1 认证接口

#### 3.1.1 获取验证码

获取图形验证码，用于登录防暴力破解。

```
GET /auth/captcha
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "uuid": "550e8400-e29b-41d4-a716-446655440000",
    "captcha": "data:image/png;base64,xxxxx"
  }
}
```

#### 3.1.2 用户登录

```
POST /auth/login
Content-Type: application/json
```

请求:

```json
{
  "username": "admin",
  "password": "admin123",
  "captcha": "1234",
  "uuid": "550e8400-e29b-41d4-a716-446655440000"
}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "expiresIn": 900
  }
}
```

#### 3.1.3 刷新 Token

```
POST /auth/refresh
Content-Type: application/json
```

请求:

```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "expiresIn": 900
  }
}
```

#### 3.1.4 获取当前用户信息

```
GET /auth/user
Authorization: Bearer {accessToken}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 1,
    "username": "admin",
    "nickname": "管理员",
    "email": "admin@example.com",
    "phone": "138****888",
    "avatar": "https://example.com/avatar.png",
    "roles": ["admin"],
    "permissions": ["user:list", "user:add", "user:edit"],
    "menus": [...]
  }
}
```

#### 3.1.5 修改密码

```
PUT /auth/password
Authorization: Bearer {accessToken}
Content-Type: application/json
```

请求:

```json
{
  "oldPassword": "old123456",
  "newPassword": "new123456"
}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 3.1.6 登出

```
POST /auth/logout
Authorization: Bearer {accessToken}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 3.2 用户管理接口

#### 3.2.1 分页查询用户

```
GET /auth/user/page?username=zhang&status=1&page=1&size=20
Authorization: Bearer {accessToken}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "username": "zhangsan",
        "nickname": "张三",
        "email": "zhangsan@example.com",
        "phone": "13812345678",
        "status": 1,
        "roles": ["developer"],
        "orgName": "研发部",
        "createdAt": "2026-05-25 10:00:00"
      }
    ],
    "total": 100,
    "page": 1,
    "size": 20
  }
}
```

#### 3.2.2 创建用户

```
POST /auth/user
Authorization: Bearer {accessToken}
Content-Type: application/json
```

请求:

```json
{
  "username": "zhangsan",
  "password": "lest123456",
  "nickname": "张三",
  "email": "zhangsan@example.com",
  "phone": "13812345678",
  "roleIds": [1, 2],
  "orgId": 1
}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 10,
    "username": "zhangsan"
  }
}
```

#### 3.2.3 更新用户

```
PUT /auth/user/{id}
Authorization: Bearer {accessToken}
Content-Type: application/json
```

请求:

```json
{
  "nickname": "张三新版",
  "email": "zhangsan@example.com",
  "phone": "13912345678",
  "roleIds": [1],
  "orgId": 2
}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 3.2.4 删除用户

```
DELETE /auth/user/{id}
Authorization: Bearer {accessToken}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 3.2.5 修改用户状态

```
PUT /auth/user/{id}/status
Authorization: Bearer {accessToken}
Content-Type: application/json
```

请求:

```json
{
  "status": 0
}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 3.2.6 重置密码

```
PUT /auth/user/{id}/password
Authorization: Bearer {accessToken}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 3.3 角色管理接口

#### 3.3.1 分页查询角色

```
GET /auth/role/page?roleName=开发&page=1&size=20
Authorization: Bearer {accessToken}
```

#### 3.3.2 创建角色

```
POST /auth/role
Authorization: Bearer {accessToken}
Content-Type: application/json
```

请求:

```json
{
  "roleCode": "developer",
  "roleName": "开发者",
  "description": "开发团队角色",
  "menuIds": [1, 2, 3, 4, 5]
}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 10,
    "roleCode": "developer"
  }
}
```

#### 3.3.3 更新角色

```
PUT /auth/role/{id}
Authorization: Bearer {accessToken}
Content-Type: application/json
```

请求:

```json
{
  "roleName": "开发者",
  "description": "开发团队角色(更新)",
  "menuIds": [1, 2, 3, 4, 5, 6]
}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 3.3.4 删除角色

```
DELETE /auth/role/{id}
Authorization: Bearer {accessToken}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 3.4 菜单管理接口

#### 3.4.1 获取菜单树

```
GET /auth/menu/tree
Authorization: Bearer {accessToken}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "menuName": "系统管理",
      "parentId": 0,
      "menuType": 1,
      "path": "/system",
      "icon": "Setting",
      "sort": 1,
      "children": [...]
    }
  ]
}
```

#### 3.4.2 获取路由

```
GET /auth/menu/routes
Authorization: Bearer {accessToken}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "path": "/dashboard",
      "name": "Dashboard",
      "component": "/dashboard/index",
      "meta": {
        "title": "首页",
        "icon": "HomeFilled",
        "roles": ["admin"]
      }
    }
  ]
}
```

### 3.5 机构管理接口

#### 3.5.1 获取机构树

```
GET /organization/tree
Authorization: Bearer {accessToken}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "orgName": "总公司",
      "parentId": 0,
      "children": [
        {
          "id": 2,
          "orgName": "研发部",
          "parentId": 1
        }
      ]
    }
  ]
}
```

### 3.6 字典管理接口

#### 3.6.1 获取字典列表

```
GET /dictionary?dictCode=status
Authorization: Bearer {accessToken}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "dictCode": "status",
      "dictName": "状态",
      "dataKey": "1",
      "dataValue": "启用",
      "sort": 1
    }
  ]
}
```

### 3.7 操作日志接口

#### 3.7.1 分页查询日志

```
GET /operation-log/page?username=admin&module=用户管理&startTime=2026-05-01&endTime=2026-05-25&page=1&size=20
Authorization: Bearer {accessToken}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "username": "admin",
        "module": "用户管理",
        "operation": "创建用户",
        "method": "com.lest.auth.api.outer.AuthController.createUser",
        "url": "/auth/user",
        "params": "{\"username\":\"test\"}",
        "result": "success",
        "ip": "192.168.1.100",
        "executionTimeMs": 45,
        "createdAt": "2026-05-25 10:00:00"
      }
    ],
    "total": 100,
    "page": 1,
    "size": 20
  }
}
```

---

## 4. 项目管理 API (lest-project)

**服务标识**：lest-project
**默认端口**：8082
**Base Path**：/project, /iteration, /milestone

### 4.1 项目管理接口

#### 4.1.1 创建项目

```
POST /project
Authorization: Bearer {accessToken}
Content-Type: application/json
```

请求:

```json
{
  "name": "LEST Platform",
  "description": "敏捷团队管理平台",
  "startDate": "2026-05-01",
  "endDate": "2026-08-01",
  "template": "agile",
  "ownerId": 1
}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "LEST Platform"
  }
}
```

#### 4.1.2 分页查询项目

```
GET /project/page?name=lest&status=active&page=1&size=20
Authorization: Bearer {accessToken}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "name": "LEST Platform",
        "description": "敏捷团队管理平台",
        "status": "active",
        "ownerId": 1,
        "ownerName": "张三",
        "startDate": "2026-05-01",
        "endDate": "2026-08-01",
        "memberCount": 8,
        "iterationCount": 3,
        "taskCount": 120,
        "completedTaskCount": 86,
        "createdAt": "2026-05-01 10:00:00",
        "updatedAt": "2026-05-25 15:00:00"
      }
    ],
    "total": 10,
    "page": 1,
    "size": 20
  }
}
```

#### 4.1.3 获取项目详情

```
GET /project/{id}
Authorization: Bearer {accessToken}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "LEST Platform",
    "description": "敏捷团队管理平台",
    "status": "active",
    "template": "agile",
    "ownerId": 1,
    "ownerName": "张三",
    "startDate": "2026-05-01",
    "endDate": "2026-08-01",
    "stats": {
      "memberCount": 8,
      "iterationCount": 3,
      "activeIterationCount": 1,
      "taskCount": 120,
      "completedTaskCount": 86,
      "completionRate": 72
    },
    "members": [...],
    "iterations": [...],
    "milestones": [...],
    "createdAt": "2026-05-01 10:00:00",
    "updatedAt": "2026-05-25 15:00:00"
  }
}
```

#### 4.1.4 更新项目

```
PUT /project/{id}
Authorization: Bearer {accessToken}
Content-Type: application/json
```

请求:

```json
{
  "name": "LEST Platform v2.0",
  "description": "更新后的描述",
  "startDate": "2026-05-01",
  "endDate": "2026-09-01",
  "ownerId": 2
}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 4.1.5 删除项目

```
DELETE /project/{id}
Authorization: Bearer {accessToken}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 4.1.6 归档项目

```
PUT /project/{id}/archive
Authorization: Bearer {accessToken}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 4.1.7 恢复项目

```
PUT /project/{id}/unarchive
Authorization: Bearer {accessToken}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 4.2 项目成员接口

#### 4.2.1 获取项目成员列表

```
GET /project/{id}/member
Authorization: Bearer {accessToken}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "userId": 1,
      "username": "admin",
      "nickname": "张三",
      "avatar": "https://...",
      "role": "admin",
      "joinedAt": "2026-05-01 10:00:00"
    }
  ]
}
```

#### 4.2.2 添加项目成员

```
POST /project/{id}/member
Authorization: Bearer {accessToken}
Content-Type: application/json
```

请求:

```json
{
  "userId": 3,
  "role": "developer"
}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 3,
    "role": "developer"
  }
}
```

#### 4.2.3 移除项目成员

```
DELETE /project/{id}/member/{userId}
Authorization: Bearer {accessToken}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 4.2.4 修改成员角色

```
PUT /project/{id}/member/{userId}/role
Authorization: Bearer {accessToken}
Content-Type: application/json
```

请求:

```json
{
  "role": "admin"
}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 4.3 迭代管理接口

#### 4.3.1 创建迭代

```
POST /project/{id}/iteration
Authorization: Bearer {accessToken}
Content-Type: application/json
```

请求:

```json
{
  "name": "v1.0",
  "goal": "完成基础功能开发",
  "startDate": "2026-05-15",
  "endDate": "2026-05-31"
}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "v1.0"
  }
}
```

#### 4.3.2 分页查询迭代

```
GET /project/{id}/iteration/page?status=active&page=1&size=10
Authorization: Bearer {accessToken}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "name": "v1.0",
        "goal": "完成基础功能开发",
        "status": "active",
        "startDate": "2026-05-15",
        "endDate": "2026-05-31",
        "taskCount": 20,
        "completedTaskCount": 5,
        "createdAt": "2026-05-15 10:00:00"
      }
    ],
    "total": 3,
    "page": 1,
    "size": 10
  }
}
```

#### 4.3.3 获取迭代详情

```
GET /iteration/{id}
Authorization: Bearer {accessToken}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "projectId": 1,
    "name": "v1.0",
    "goal": "完成基础功能开发",
    "status": "active",
    "startDate": "2026-05-15",
    "endDate": "2026-05-31",
    "taskCount": 20,
    "completedTaskCount": 5,
    "completionRate": 25,
    "tasks": [...]
  }
}
```

#### 4.3.4 更新迭代

```
PUT /iteration/{id}
Authorization: Bearer {accessToken}
Content-Type: application/json
```

请求:

```json
{
  "name": "v1.0 更新版",
  "goal": "更新后的目标",
  "startDate": "2026-05-15",
  "endDate": "2026-06-05"
}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 4.3.5 删除迭代

```
DELETE /iteration/{id}
Authorization: Bearer {accessToken}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 4.3.6 启动迭代

```
PUT /iteration/{id}/start
Authorization: Bearer {accessToken}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 4.3.7 结束迭代

```
PUT /iteration/{id}/complete
Authorization: Bearer {accessToken}
Content-Type: application/json
```

请求:

```json
{
  "includeIncomplete": "next",
  "nextIterationId": 2
}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 4.3.8 获取迭代任务列表

```
GET /iteration/{id}/task?page=1&size=20
Authorization: Bearer {accessToken}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "title": "实现用户登录",
        "status": "completed",
        "priority": "high",
        "assigneeId": 1,
        "assigneeName": "张三",
        "estimatedHours": 8,
        "actualHours": 6
      }
    ],
    "total": 20,
    "page": 1,
    "size": 20
  }
}
```

### 4.4 里程碑接口

#### 4.4.1 创建里程碑

```
POST /project/{id}/milestone
Authorization: Bearer {accessToken}
Content-Type: application/json
```

请求:

```json
{
  "name": "Alpha 版本",
  "description": "完成核心功能",
  "targetDate": "2026-06-01"
}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "Alpha 版本"
  }
}
```

#### 4.4.2 分页查询里程碑

```
GET /project/{id}/milestone/page?page=1&size=10
Authorization: Bearer {accessToken}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "name": "Alpha 版本",
        "targetDate": "2026-06-01",
        "progress": 33,
        "iterationCount": 1,
        "completedIterationCount": 0,
        "taskCount": 30,
        "completedTaskCount": 10,
        "daysRemaining": 7
      }
    ],
    "total": 3,
    "page": 1,
    "size": 10
  }
}
```

#### 4.4.3 获取里程碑详情

```
GET /milestone/{id}
Authorization: Bearer {accessToken}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "projectId": 1,
    "name": "Alpha 版本",
    "description": "完成核心功能",
    "targetDate": "2026-06-01",
    "progress": 33,
    "iterationCount": 1,
    "completedIterationCount": 0,
    "taskCount": 30,
    "completedTaskCount": 10,
    "daysRemaining": 7,
    "iterations": [...]
  }
}
```

#### 4.4.4 更新里程碑

```
PUT /milestone/{id}
Authorization: Bearer {accessToken}
Content-Type: application/json
```

请求:

```json
{
  "name": "Alpha 版本更新",
  "description": "更新后的描述",
  "targetDate": "2026-06-05"
}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 4.4.5 删除里程碑

```
DELETE /milestone/{id}
Authorization: Bearer {accessToken}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 4.4.6 关联迭代到里程碑

```
POST /milestone/{id}/iteration
Authorization: Bearer {accessToken}
Content-Type: application/json
```

请求:

```json
{
  "iterationId": 1
}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

## 5. 任务管理 API (lest-task)

**服务标识**：lest-task
**默认端口**：8083
**Base Path**：/task

### 5.1 任务管理接口

#### 5.1.1 创建任务

```
POST /task
Authorization: Bearer {accessToken}
Content-Type: application/json
```

请求:

```json
{
  "title": "实现用户登录功能",
  "description": "实现用户的登录功能",
  "projectId": 1,
  "iterationId": 1,
  "taskType": "task",
  "priority": "p1",
  "assigneeId": 1,
  "estimatedHours": 8,
  "dueDate": "2026-05-30",
  "labels": ["后端", "登录"],
  "parentTaskId": null
}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "title": "实现用户登录功能"
  }
}
```

#### 5.1.2 分页查询任务

```
GET /task/page?projectId=1&iterationId=1&assigneeId=1&status=todo&priority=p1&labels=后端&keyword=登录&page=1&size=20
Authorization: Bearer {accessToken}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "title": "实现用户登录功能",
        "description": "...",
        "projectId": 1,
        "projectName": "LEST Platform",
        "iterationId": 1,
        "iterationName": "v1.0",
        "taskType": "task",
        "priority": "p1",
        "status": "todo",
        "assigneeId": 1,
        "assigneeName": "张三",
        "labels": ["后端", "登录"],
        "estimatedHours": 8,
        "actualHours": 0,
        "dueDate": "2026-05-30",
        "subTaskCount": 5,
        "completedSubTaskCount": 3,
        "createdAt": "2026-05-20 10:00:00",
        "updatedAt": "2026-05-25 15:00:00"
      }
    ],
    "total": 100,
    "page": 1,
    "size": 20
  }
}
```

#### 5.1.3 获取任务详情

```
GET /task/{id}
Authorization: Bearer {accessToken}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "title": "实现用户登录功能",
    "description": "...",
    "projectId": 1,
    "projectName": "LEST Platform",
    "iterationId": 1,
    "iterationName": "v1.0",
    "taskType": "task",
    "priority": "p1",
    "status": "in_progress",
    "assigneeId": 1,
    "assigneeName": "张三",
    "assigneeAvatar": "https://...",
    "watcherIds": [2, 3],
    "labels": ["后端", "登录"],
    "estimatedHours": 8,
    "actualHours": 5,
    "progress": 62,
    "dueDate": "2026-05-30",
    "startTime": "2026-05-22 10:00:00",
    "completedAt": null,
    "parentTaskId": null,
    "subTasks": [...],
    "dependencies": [...],
    "worklogs": [...],
    "comments": [...],
    "activities": [...],
    "createdAt": "2026-05-20 10:00:00",
    "updatedAt": "2026-05-25 15:00:00"
  }
}
```

#### 5.1.4 更新任务

```
PUT /task/{id}
Authorization: Bearer {accessToken}
Content-Type: application/json
```

请求:

```json
{
  "title": "实现用户登录功能（优化版）",
  "description": "更新后的描述",
  "priority": "p0",
  "estimatedHours": 10
}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 5.1.5 删除任务

```
DELETE /task/{id}
Authorization: Bearer {accessToken}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 5.1.6 更新任务状态

```
PUT /task/{id}/status
Authorization: Bearer {accessToken}
Content-Type: application/json
```

请求:

```json
{
  "status": "in_progress"
}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 5.1.7 分配任务

```
PUT /task/{id}/assign
Authorization: Bearer {accessToken}
Content-Type: application/json
```

请求:

```json
{
  "assigneeId": 2,
  "watcherIds": [1, 3, 4]
}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 5.1.8 认领任务

```
POST /task/{id}/claim
Authorization: Bearer {accessToken}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 5.2 看板接口

#### 5.2.1 获取看板视图

```
GET /task/board?projectId=1&iterationId=1
Authorization: Bearer {accessToken}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "columns": [
      {
        "id": 1,
        "name": "待办",
        "status": "todo",
        "taskCount": 10,
        "tasks": [
          {
            "id": 1,
            "title": "实现用户登录功能",
            "priority": "p1",
            "assigneeId": 1,
            "assigneeName": "张三",
            "assigneeAvatar": "https://...",
            "dueDate": "2026-05-30",
            "subTaskCount": 5,
            "completedSubTaskCount": 3,
            "labels": ["后端"]
          }
        ]
      }
    ]
  }
}
```

#### 5.2.2 拖拽任务更新列

```
PUT /task/{id}/move
Authorization: Bearer {accessToken}
Content-Type: application/json
```

请求:

```json
{
  "targetColumn": "in_progress",
  "targetPosition": 0
}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 5.3 甘特图接口

#### 5.3.1 获取甘特图数据

```
GET /task/gantt?projectId=1&iterationId=1&startDate=2026-05-01&endDate=2026-05-31
Authorization: Bearer {accessToken}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "tasks": [
      {
        "id": 1,
        "title": "实现用户登录功能",
        "startDate": "2026-05-22",
        "endDate": "2026-05-30",
        "progress": 62,
        "assigneeId": 1,
        "assigneeName": "张三",
        "status": "in_progress",
        "parentTaskId": null,
        "dependencies": [10]
      }
    ],
    "milestones": [
      {
        "id": 1,
        "name": "Alpha 版本",
        "date": "2026-06-01"
      }
    ]
  }
}
```

### 5.4 父子任务接口

#### 5.4.1 添加子任务

```
POST /task/{id}/subtask
Authorization: Bearer {accessToken}
Content-Type: application/json
```

请求:

```json
{
  "title": "前端对接登录接口",
  "taskType": "task",
  "priority": "p1",
  "assigneeId": 3,
  "estimatedHours": 4
}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 10,
    "title": "前端对接登录接口"
  }
}
```

#### 5.4.2 获取子任务列表

```
GET /task/{id}/subtask
Authorization: Bearer {accessToken}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 2,
      "title": "设计登录页面 UI",
      "status": "completed",
      "assigneeId": 1,
      "assigneeName": "张三"
    }
  ]
}
```

### 5.5 任务依赖接口

#### 5.5.1 添加前置任务

```
POST /task/{id}/dependency
Authorization: Bearer {accessToken}
Content-Type: application/json
```

请求:

```json
{
  "type": "blocker",
  "dependencyTaskId": 10
}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 5.5.2 获取任务依赖

```
GET /task/{id}/dependency
Authorization: Bearer {accessToken}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "blockers": [
      {
        "taskId": 10,
        "taskTitle": "实现 JWT Token 功能",
        "status": "completed"
      }
    ],
    "blockedBys": []
  }
}
```

### 5.6 工时接口

#### 5.6.1 添加工时记录

```
POST /task/{id}/worklog
Authorization: Bearer {accessToken}
Content-Type: application/json
```

请求:

```json
{
  "hours": 2,
  "description": "实现登录接口核心逻辑",
  "workDate": "2026-05-22"
}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "totalHours": 5
  }
}
```

#### 5.6.2 获取工时记录

```
GET /task/{id}/worklog?page=1&size=20
Authorization: Bearer {accessToken}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "userId": 1,
        "userName": "张三",
        "hours": 2,
        "description": "实现登录接口核心逻辑",
        "workDate": "2026-05-22",
        "createdAt": "2026-05-22 18:00:00"
      }
    ],
    "total": 5,
    "estimatedHours": 8,
    "page": 1,
    "size": 20
  }
}
```

### 5.7 标签接口

#### 5.7.1 获取项目标签

```
GET /project/{projectId}/labels
Authorization: Bearer {accessToken}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "后端",
      "color": "#1890ff"
    },
    {
      "id": 2,
      "name": "前端",
      "color": "#52c41a"
    }
  ]
}
```

#### 5.7.2 创建标签

```
POST /project/{projectId}/label
Authorization: Bearer {accessToken}
Content-Type: application/json
```

请求:

```json
{
  "name": "后端",
  "color": "#1890ff"
}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "后端",
    "color": "#1890ff"
  }
}
```

### 5.8 任务-代码关联接口

#### 5.8.1 获取任务关联的提交

```
GET /task/{id}/commits
Authorization: Bearer {accessToken}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "commitSha": "abc1234def5678",
        "shortSha": "abc1234",
        "commitMessage": "feat: 实现登录接口\n\nCloses #1",
        "authorUsername": "zhangsan",
        "authorAvatar": "https://...",
        "committedAt": "2026-05-25 10:00:00",
        "repositoryName": "lest-work/lest",
        "linkType": "auto"
      }
    ],
    "total": 10
  }
}
```

#### 5.8.2 获取任务关联的 MR/PR

```
GET /task/{id}/merge-requests
Authorization: Bearer {accessToken}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "number": 45,
        "title": "feat: 实现登录功能",
        "status": "open",
        "sourceBranch": "feature/login",
        "targetBranch": "main",
        "authorUsername": "zhangsan",
        "authorAvatar": "https://...",
        "url": "https://github.com/lest-work/lest/pull/45",
        "createdAt": "2026-05-25 10:00:00"
      }
    ],
    "total": 2
  }
}
```

#### 5.8.3 手动关联提交

```
POST /task/{id}/commit
Authorization: Bearer {accessToken}
Content-Type: application/json
```

请求:

```json
{
  "repositoryId": 1,
  "commitSha": "abc1234def5678"
}
```

响应:

```json
{
  "code": 200,
  "message": "success"
}
```

#### 5.8.4 手动关联 MR/PR

```
POST /task/{id}/merge-request
Authorization: Bearer {accessToken}
Content-Type: application/json
```

请求:

```json
{
  "mrId": 1
}
```

响应:

```json
{
  "code": 200,
  "message": "success"
}
```

### 5.9 任务-CI 关联接口

#### 5.9.1 获取任务关联的构建

```
GET /task/{id}/pipelines
Authorization: Bearer {accessToken}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "buildId": 156,
        "buildNumber": 156,
        "jobName": "lest-platform",
        "status": "success",
        "provider": "jenkins",
        "branch": "feature/login",
        "commitSha": "abc1234",
        "duration": 192,
        "triggerType": "commit",
        "startedAt": "2026-05-25 14:00:00",
        "url": "https://jenkins.example.com/job/lest-platform/156"
      }
    ],
    "total": 5
  }
}
```

### 5.10 Webhook 接口

#### 5.10.1 CI 构建完成回调

```
POST /webhook/ci/build
Content-Type: application/json
X-Signature: {签名}
```

请求:

```json
{
  "event": "build.completed",
  "buildId": 156,
  "jobName": "lest-platform",
  "status": "success",
  "commitSha": "abc1234",
  "repositoryId": 1,
  "triggerType": "commit",
  "triggeredBy": "zhangsan"
}
```

响应:

```json
{
  "code": 200,
  "message": "success"
}
```

#### 5.10.2 Git 提交推送回调

```
POST /webhook/git/commit
Content-Type: application/json
X-Signature: {签名}
```

请求:

```json
{
  "event": "commit.pushed",
  "repositoryId": 1,
  "commitSha": "abc1234",
  "commitMessage": "feat: 实现登录功能\n\nCloses #1",
  "authorUsername": "zhangsan",
  "authorEmail": "zhangsan@example.com",
  "timestamp": "2026-05-25T10:00:00Z"
}
```

响应:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "autoLinkedTaskIds": [1]
  }
}
```

---


---

## 13. 错误码汇总

### 13.1 认证模块 (1000-1999)

| 错误码 | 说明 |
|--------|------|
| 1000 | AUTH_USERNAME_PASSWORD_ERROR - 用户名或密码错误 |
| 1001 | AUTH_CAPTCHA_EXPIRED - 验证码已过期 |
| 1002 | AUTH_CAPTCHA_ERROR - 验证码错误 |

---

