| Attribute | Content |
| Version | V1.3 |
| Created Date | 2026-05-26 |
| Last Updated | 2026-05-31 |

---

## 1. Document Overview

This document provides the API interface definitions for all microservices in LEST Platform, including authentication services, task management, project management, code management, CI/CD, WakaTime, meeting management, notification messages, team performance, and AI services modules.

### Service Port Overview

> **Note**: API Gateway (`lest-gateway`, port 8080) serves as the unified entry point. All services are routed through the Gateway (StripPrefix=1). The following Base Path is the Gateway routing prefix.

| Service Name | Service ID | Default Port | Base Path |
|---------|---------|---------|-----------|
| API Gateway | lest-gateway | 8080 | `/` (Unified Entry) |
| Authentication Service | lest-auth | 8096 | `/auth` |
| System Management Service | lest-system | 8081 | `/system` |
| Project Management Service | lest-project | 8082 | `/project` |
| Task Management Service | lest-task | 8083 | `/task` |
| Release Management Service | lest-release | 8087 | `/release` |
| Scheduled Job Service | lest-job | 9203 | `/jobs` |
| File Service | lest-file | 8091 | `/file` |
| Monitoring Service | lest-monitor | 9100 | `/monitor` |

> Services not yet implemented (meeting/notification/ai/performance/open/plugin/wakapi) are currently disabled, and Gateway routing is disabled for these services.

---

## 2. General Specifications

### 2.1 Authentication Method

Unless otherwise specified, all API requests require an authentication token in the Header:

```
Authorization: Bearer {accessToken}
```

**Token Description**:
- `accessToken`: The access token obtained after user login, valid for 15 minutes
- `refreshToken`: The refresh token used to obtain a new accessToken, valid for 7 days

### 2.2 Internal Service Calls

Microservices communicate with each other using internal tokens:

```
X-Internal-Token: {Internal Call Token}
```

### 2.3 Webhook Authentication

Webhook callbacks use signature verification:

```
X-Hub-Signature-256: {signature}        # GitHub Webhook
X-Gitlab-Token: {signature}            # GitLab Webhook
X-Jenkins-Token: {signature}            # Jenkins Webhook
```

### 2.4 Response Format

All APIs use a unified JSON response format:

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

**Code Description**:
- `200`: Success
- Other: Business error codes (see each module's error code definitions)

### 2.5 Pagination Format

List query interfaces use a unified pagination format:

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

**Pagination Parameters**:
| Parameter | Type | Description |
|------|------|------|
| page | int | Page number, starting from 1 |
| size | int | Number of items per page, default 20 |

### 2.6 Date and Time Format

- Date format: `YYYY-MM-DD`
- Time format: `YYYY-MM-DD HH:mm:ss`
- Timestamp: Unix timestamp (seconds)

### 2.7 Common Error Codes

| Error Code | Description |
|--------|------|
| 9000 | VALIDATION_ERROR - Parameter validation failed |
| 9001 | PARAM_MISSING - Required parameter is missing |
| 9002 | PARAM_TYPE_ERROR - Parameter type error |
| 9500 | PERMISSION_DENIED - Insufficient permissions |
| 9999 | SYSTEM_ERROR - Internal system error |
| 9998 | SERVICE_UNAVAILABLE - Service temporarily unavailable |

---

## 3. Authentication Service API (lest-auth)

**Service ID**: lest-auth
**Default Port**: 8096
**Base Path**: /auth, /organization, /dictionary, /operation-log

### 3.1 Authentication Interfaces

#### 3.1.1 Get Captcha

Get a graphic captcha for login brute-force protection.

```
GET /auth/captcha
```

Response:

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

#### 3.1.2 User Login

```
POST /auth/login
Content-Type: application/json
```

Request:

```json
{
  "username": "admin",
  "password": "admin123",
  "captcha": "1234",
  "uuid": "550e8400-e29b-41d4-a716-446655440000"
}
```

Response:

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

#### 3.1.3 Refresh Token

```
POST /auth/refresh
Content-Type: application/json
```

Request:

```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

Response:

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

#### 3.1.4 Get Current User Info

```
GET /auth/user
Authorization: Bearer {accessToken}
```

Response:

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

#### 3.1.5 Change Password

```
PUT /auth/password
Authorization: Bearer {accessToken}
Content-Type: application/json
```

Request:

```json
{
  "oldPassword": "old123456",
  "newPassword": "new123456"
}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 3.1.6 Logout

```
POST /auth/logout
Authorization: Bearer {accessToken}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 3.2 User Management Interfaces

#### 3.2.1 Paginated Query Users

```
GET /auth/user/page?username=zhang&status=1&page=1&size=20
Authorization: Bearer {accessToken}
```

Response:

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

#### 3.2.2 Create User

```
POST /auth/user
Authorization: Bearer {accessToken}
Content-Type: application/json
```

Request:

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

Response:

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

#### 3.2.3 Update User

```
PUT /auth/user/{id}
Authorization: Bearer {accessToken}
Content-Type: application/json
```

Request:

```json
{
  "nickname": "张三新版",
  "email": "zhangsan@example.com",
  "phone": "13912345678",
  "roleIds": [1],
  "orgId": 2
}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 3.2.4 Delete User

```
DELETE /auth/user/{id}
Authorization: Bearer {accessToken}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 3.2.5 Change User Status

```
PUT /auth/user/{id}/status
Authorization: Bearer {accessToken}
Content-Type: application/json
```

Request:

```json
{
  "status": 0
}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 3.2.6 Reset Password

```
PUT /auth/user/{id}/password
Authorization: Bearer {accessToken}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 3.3 Role Management Interfaces

#### 3.3.1 Paginated Query Roles

```
GET /auth/role/page?roleName=开发&page=1&size=20
Authorization: Bearer {accessToken}
```

#### 3.3.2 Create Role

```
POST /auth/role
Authorization: Bearer {accessToken}
Content-Type: application/json
```

Request:

```json
{
  "roleCode": "developer",
  "roleName": "开发者",
  "description": "开发团队角色",
  "menuIds": [1, 2, 3, 4, 5]
}
```

Response:

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

#### 3.3.3 Update Role

```
PUT /auth/role/{id}
Authorization: Bearer {accessToken}
Content-Type: application/json
```

Request:

```json
{
  "roleName": "开发者",
  "description": "开发团队角色(更新)",
  "menuIds": [1, 2, 3, 4, 5, 6]
}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 3.3.4 Delete Role

```
DELETE /auth/role/{id}
Authorization: Bearer {accessToken}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 3.4 Menu Management Interfaces

#### 3.4.1 Get Menu Tree

```
GET /auth/menu/tree
Authorization: Bearer {accessToken}
```

Response:

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

#### 3.4.2 Get Routes

```
GET /auth/menu/routes
Authorization: Bearer {accessToken}
```

Response:

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

### 3.5 Organization Management Interfaces

#### 3.5.1 Get Organization Tree

```
GET /organization/tree
Authorization: Bearer {accessToken}
```

Response:

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

### 3.6 Dictionary Management Interfaces

#### 3.6.1 Get Dictionary List

```
GET /dictionary?dictCode=status
Authorization: Bearer {accessToken}
```

Response:

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

### 3.7 Operation Log Interfaces

#### 3.7.1 Paginated Query Logs

```
GET /operation-log/page?username=admin&module=用户管理&startTime=2026-05-01&endTime=2026-05-25&page=1&size=20
Authorization: Bearer {accessToken}
```

Response:

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

## 4. Project Management API (lest-project)

**Service ID**: lest-project
**Default Port**: 8082
**Base Path**: /project, /iteration, /milestone

### 4.1 Project Management Interfaces

#### 4.1.1 Create Project

```
POST /project
Authorization: Bearer {accessToken}
Content-Type: application/json
```

Request:

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

Response:

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

#### 4.1.2 Paginated Query Projects

```
GET /project/page?name=lest&status=active&page=1&size=20
Authorization: Bearer {accessToken}
```

Response:

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

#### 4.1.3 Get Project Details

```
GET /project/{id}
Authorization: Bearer {accessToken}
```

Response:

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

#### 4.1.4 Update Project

```
PUT /project/{id}
Authorization: Bearer {accessToken}
Content-Type: application/json
```

Request:

```json
{
  "name": "LEST Platform v2.0",
  "description": "更新后的描述",
  "startDate": "2026-05-01",
  "endDate": "2026-09-01",
  "ownerId": 2
}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 4.1.5 Delete Project

```
DELETE /project/{id}
Authorization: Bearer {accessToken}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 4.1.6 Archive Project

```
PUT /project/{id}/archive
Authorization: Bearer {accessToken}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 4.1.7 Restore Project

```
PUT /project/{id}/unarchive
Authorization: Bearer {accessToken}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 4.2 Project Member Interfaces

#### 4.2.1 Get Project Member List

```
GET /project/{id}/member
Authorization: Bearer {accessToken}
```

Response:

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

#### 4.2.2 Add Project Member

```
POST /project/{id}/member
Authorization: Bearer {accessToken}
Content-Type: application/json
```

Request:

```json
{
  "userId": 3,
  "role": "developer"
}
```

Response:

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

#### 4.2.3 Remove Project Member

```
DELETE /project/{id}/member/{userId}
Authorization: Bearer {accessToken}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 4.2.4 Update Member Role

```
PUT /project/{id}/member/{userId}/role
Authorization: Bearer {accessToken}
Content-Type: application/json
```

Request:

```json
{
  "role": "admin"
}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 4.3 Iteration Management Interfaces

#### 4.3.1 Create Iteration

```
POST /project/{id}/iteration
Authorization: Bearer {accessToken}
Content-Type: application/json
```

Request:

```json
{
  "name": "v1.0",
  "goal": "完成基础功能开发",
  "startDate": "2026-05-15",
  "endDate": "2026-05-31"
}
```

Response:

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

#### 4.3.2 Paginated Query Iterations

```
GET /project/{id}/iteration/page?status=active&page=1&size=10
Authorization: Bearer {accessToken}
```

Response:

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

#### 4.3.3 Get Iteration Details

```
GET /iteration/{id}
Authorization: Bearer {accessToken}
```

Response:

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

#### 4.3.4 Update Iteration

```
PUT /iteration/{id}
Authorization: Bearer {accessToken}
Content-Type: application/json
```

Request:

```json
{
  "name": "v1.0 更新版",
  "goal": "更新后的目标",
  "startDate": "2026-05-15",
  "endDate": "2026-06-05"
}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 4.3.5 Delete Iteration

```
DELETE /iteration/{id}
Authorization: Bearer {accessToken}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 4.3.6 Start Iteration

```
PUT /iteration/{id}/start
Authorization: Bearer {accessToken}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 4.3.7 Complete Iteration

```
PUT /iteration/{id}/complete
Authorization: Bearer {accessToken}
Content-Type: application/json
```

Request:

```json
{
  "includeIncomplete": "next",
  "nextIterationId": 2
}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 4.3.8 Get Iteration Task List

```
GET /iteration/{id}/task?page=1&size=20
Authorization: Bearer {accessToken}
```

Response:

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

### 4.4 Milestone Interfaces

#### 4.4.1 Create Milestone

```
POST /project/{id}/milestone
Authorization: Bearer {accessToken}
Content-Type: application/json
```

Request:

```json
{
  "name": "Alpha 版本",
  "description": "完成核心功能",
  "targetDate": "2026-06-01"
}
```

Response:

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

#### 4.4.2 Paginated Query Milestones

```
GET /project/{id}/milestone/page?page=1&size=10
Authorization: Bearer {accessToken}
```

Response:

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

#### 4.4.3 Get Milestone Details

```
GET /milestone/{id}
Authorization: Bearer {accessToken}
```

Response:

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

#### 4.4.4 Update Milestone

```
PUT /milestone/{id}
Authorization: Bearer {accessToken}
Content-Type: application/json
```

Request:

```json
{
  "name": "Alpha 版本更新",
  "description": "更新后的描述",
  "targetDate": "2026-06-05"
}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 4.4.5 Delete Milestone

```
DELETE /milestone/{id}
Authorization: Bearer {accessToken}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 4.4.6 Associate Iteration to Milestone

```
POST /milestone/{id}/iteration
Authorization: Bearer {accessToken}
Content-Type: application/json
```

Request:

```json
{
  "iterationId": 1
}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

## 5. Task Management API (lest-task)

**Service ID**: lest-task
**Default Port**: 8083
**Base Path**: /task

### 5.1 Task Management Interfaces

#### 5.1.1 Create Task

```
POST /task
Authorization: Bearer {accessToken}
Content-Type: application/json
```

Request:

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

Response:

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

#### 5.1.2 Paginated Query Tasks

```
GET /task/page?projectId=1&iterationId=1&assigneeId=1&status=todo&priority=p1&labels=后端&keyword=登录&page=1&size=20
Authorization: Bearer {accessToken}
```

Response:

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

#### 5.1.3 Get Task Details

```
GET /task/{id}
Authorization: Bearer {accessToken}
```

Response:

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

#### 5.1.4 Update Task

```
PUT /task/{id}
Authorization: Bearer {accessToken}
Content-Type: application/json
```

Request:

```json
{
  "title": "实现用户登录功能（优化版）",
  "description": "更新后的描述",
  "priority": "p0",
  "estimatedHours": 10
}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 5.1.5 Delete Task

```
DELETE /task/{id}
Authorization: Bearer {accessToken}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 5.1.6 Update Task Status

```
PUT /task/{id}/status
Authorization: Bearer {accessToken}
Content-Type: application/json
```

Request:

```json
{
  "status": "in_progress"
}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 5.1.7 Assign Task

```
PUT /task/{id}/assign
Authorization: Bearer {accessToken}
Content-Type: application/json
```

Request:

```json
{
  "assigneeId": 2,
  "watcherIds": [1, 3, 4]
}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 5.1.8 Claim Task

```
POST /task/{id}/claim
Authorization: Bearer {accessToken}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 5.2 Kanban Interfaces

#### 5.2.1 Get Kanban View

```
GET /task/board?projectId=1&iterationId=1
Authorization: Bearer {accessToken}
```

Response:

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

#### 5.2.2 Drag Task to Update Column

```
PUT /task/{id}/move
Authorization: Bearer {accessToken}
Content-Type: application/json
```

Request:

```json
{
  "targetColumn": "in_progress",
  "targetPosition": 0
}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 5.3 Gantt Chart Interfaces

#### 5.3.1 Get Gantt Chart Data

```
GET /task/gantt?projectId=1&iterationId=1&startDate=2026-05-01&endDate=2026-05-31
Authorization: Bearer {accessToken}
```

Response:

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

### 5.4 Parent-Child Task Interfaces

#### 5.4.1 Add Subtask

```
POST /task/{id}/subtask
Authorization: Bearer {accessToken}
Content-Type: application/json
```

Request:

```json
{
  "title": "前端对接登录接口",
  "taskType": "task",
  "priority": "p1",
  "assigneeId": 3,
  "estimatedHours": 4
}
```

Response:

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

#### 5.4.2 Get Subtask List

```
GET /task/{id}/subtask
Authorization: Bearer {accessToken}
```

Response:

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

### 5.5 Task Dependency Interfaces

#### 5.5.1 Add Blocker

```
POST /task/{id}/dependency
Authorization: Bearer {accessToken}
Content-Type: application/json
```

Request:

```json
{
  "type": "blocker",
  "dependencyTaskId": 10
}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

#### 5.5.2 Get Task Dependencies

```
GET /task/{id}/dependency
Authorization: Bearer {accessToken}
```

Response:

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

### 5.6 Worklog Interfaces

#### 5.6.1 Add Worklog

```
POST /task/{id}/worklog
Authorization: Bearer {accessToken}
Content-Type: application/json
```

Request:

```json
{
  "hours": 2,
  "description": "实现登录接口核心逻辑",
  "workDate": "2026-05-22"
}
```

Response:

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

#### 5.6.2 Get Worklogs

```
GET /task/{id}/worklog?page=1&size=20
Authorization: Bearer {accessToken}
```

Response:

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

### 5.7 Label Interfaces

#### 5.7.1 Get Project Labels

```
GET /project/{projectId}/labels
Authorization: Bearer {accessToken}
```

Response:

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

#### 5.7.2 Create Label

```
POST /project/{projectId}/label
Authorization: Bearer {accessToken}
Content-Type: application/json
```

Request:

```json
{
  "name": "后端",
  "color": "#1890ff"
}
```

Response:

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

### 5.8 Task-Code Association Interfaces

#### 5.8.1 Get Commits Associated with Task

```
GET /task/{id}/commits
Authorization: Bearer {accessToken}
```

Response:

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

#### 5.8.2 Get MR/PR Associated with Task

```
GET /task/{id}/merge-requests
Authorization: Bearer {accessToken}
```

Response:

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

#### 5.8.3 Manually Link Commit

```
POST /task/{id}/commit
Authorization: Bearer {accessToken}
Content-Type: application/json
```

Request:

```json
{
  "repositoryId": 1,
  "commitSha": "abc1234def5678"
}
```

Response:

```json
{
  "code": 200,
  "message": "success"
}
```

#### 5.8.4 Manually Link MR/PR

```
POST /task/{id}/merge-request
Authorization: Bearer {accessToken}
Content-Type: application/json
```

Request:

```json
{
  "mrId": 1
}
```

Response:

```json
{
  "code": 200,
  "message": "success"
}
```

### 5.9 Task-CI Association Interfaces

#### 5.9.1 Get Builds Associated with Task

```
GET /task/{id}/pipelines
Authorization: Bearer {accessToken}
```

Response:

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

### 5.10 Webhook Interfaces

#### 5.10.1 CI Build Completed Callback

```
POST /webhook/ci/build
Content-Type: application/json
X-Signature: {signature}
```

Request:

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

Response:

```json
{
  "code": 200,
  "message": "success"
}
```

#### 5.10.2 Git Commit Pushed Callback

```
POST /webhook/git/commit
Content-Type: application/json
X-Signature: {signature}
```

Request:

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

Response:

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

## 13. Error Code Summary

### 13.1 Authentication Module (1000-1999)

| Error Code | Description |
|--------|------|
| 1000 | AUTH_USERNAME_PASSWORD_ERROR - Incorrect username or password |
| 1001 | AUTH_CAPTCHA_EXPIRED - Captcha has expired |
| 1002 | AUTH_CAPTCHA_ERROR - Incorrect captcha |

---
