# 开放平台 PRD
> **📌 Jira 映射**：Jira REST API + Webhooks + OAuth2 + Atlassian Connect
>
> **功能定位**：REST API、Webhook 事件订阅、OAuth2、API Key 管理


## 文档信息

| 属性 | 内容 |
|------|------|
| 版本 | V1.0 |
| 状态 | 已完成 |
| 创建日期 | 2026-05-25 |
| 最后更新 | 2026-05-25 |

---

## 1. 概述

### 1.1 功能背景

LEST Platform 作为开源的 AI-Native 敏捷管理平台，需要提供完善的开放能力，让外部系统和第三方开发者能够与平台进行深度集成。开放平台借鉴了 GitHub、GitLab、Slack 等成熟平台的 API 和 Webhook 设计，提供 REST API、Webhook 事件订阅、OAuth2 认证等能力。

**核心设计理念**：
- **标准化**：遵循 RESTful 规范，提供一致的 API 设计
- **安全**：基于 OAuth2 的认证授权，支持 API Key 和 JWT
- **可发现**：提供完整的 API 文档（OpenAPI 3.0）
- **可扩展**：通过 Webhook 事件订阅实现实时集成
- **可追踪**：完整的 API 调用日志和审计

### 1.2 功能目标

- 提供 RESTful API，覆盖平台所有业务功能
- 提供 Webhook 事件订阅，支持 50+ 业务事件
- 提供 OAuth2 应用管理，支持第三方应用授权
- 提供 API Key 管理，支持个人访问令牌
- 提供完整的 OpenAPI 3.0 文档
- 提供 API 调用统计和限流能力
- 提供 Webhook 重试和调试能力

### 1.3 与插件系统的关系

| 模块 | 职责 | 关系 |
|------|------|------|
| 开放平台 | 提供外部系统集成能力 | 平台 → 外部 |
| 插件系统 | 提供平台内部扩展能力 | 扩展 → 平台 |
| 两者互补 | 共同构成 LEST Platform 的开放生态 | 缺一不可 |

---

## 2. 用户故事

### 2.1 API 访问

#### US-001: 获取 API 文档
**作为** 开发者，**我希望** 查看完整的 API 文档，**以便** 了解如何使用 API。

验收标准：
- [ ] 提供 OpenAPI 3.0 格式的完整 API 文档
- [ ] 支持在线 Swagger UI 测试
- [ ] 支持按模块筛选 API
- [ ] 提供多种语言 SDK 示例代码

#### US-002: 生成个人访问令牌
**作为** 用户，**我希望** 生成个人访问令牌，**以便** 通过 API 访问平台。

验收标准：
- [ ] 支持生成多个访问令牌
- [ ] 支持设置令牌权限范围
- [ ] 支持设置令牌过期时间
- [ ] 支持随时撤销令牌

#### US-003: 调用 REST API
**作为** 开发者，**我希望** 通过 REST API 操作平台数据，**以便** 与我的系统集成。

验收标准：
- [ ] 支持 CRUD 操作所有业务实体
- [ ] 支持分页、排序、筛选
- [ ] 返回标准化的 JSON 响应
- [ ] 支持批量操作

### 2.2 OAuth2 应用

#### US-004: 创建 OAuth2 应用
**作为** 开发者，**我希望** 创建 OAuth2 应用，**以便** 实现第三方登录和授权。

验收标准：
- [ ] 支持创建多个 OAuth2 应用
- [ ] 支持配置回调地址
- [ ] 支持设置应用权限范围
- [ ] 提供 Client ID 和 Client Secret

#### US-005: 实现 OAuth2 授权流程
**作为** 开发者，**我希望** 实现 OAuth2 授权流程，**以便** 用户可以授权我的应用访问 LEST 数据。

验收标准：
- [ ] 支持标准 OAuth2 Authorization Code 流程
- [ ] 支持 PKCE 扩展
- [ ] 支持刷新令牌
- [ ] 提供多语言授权示例

### 2.3 Webhook 事件订阅

#### US-006: 创建 Webhook
**作为** 开发者，**我希望** 创建 Webhook 订阅，**以便** 接收平台事件通知。

验收标准：
- [ ] 支持创建多个 Webhook 端点
- [ ] 支持选择订阅的事件类型
- [ ] 支持配置请求头和认证方式
- [ ] 支持设置重试策略

#### US-007: 验证 Webhook 签名
**作为** 开发者，**我希望** 验证 Webhook 签名，**以便** 确保事件来源可信。

验收标准：
- [ ] 提供 HMAC-SHA256 签名验证
- [ ] 提供签名密钥管理
- [ ] 支持重放攻击防护

#### US-008: 调试 Webhook
**作为** 开发者，**我希望** 调试 Webhook，**以便** 排查集成问题。

验收标准：
- [ ] 支持手动触发 Webhook 测试
- [ ] 支持查看 Webhook 投递历史
- [ ] 支持查看详细的投递日志
- [ ] 支持手动重试投递

---

## 3. 功能详细设计

### 3.1 功能清单

| 序号 | 功能点 | 功能描述 | 优先级 |
|------|--------|---------|--------|
| 1 | REST API | 覆盖所有业务功能的 RESTful API | P0 |
| 2 | Webhook 事件订阅 | 支持 50+ 业务事件的实时订阅 | P0 |
| 3 | OAuth2 应用 | 支持第三方应用授权 | P0 |
| 4 | API Key 管理 | 个人访问令牌管理 | P0 |
| 5 | OpenAPI 文档 | OpenAPI 3.0 格式完整文档 | P1 |
| 6 | API 限流 | 基于令牌桶的限流策略 | P1 |
| 7 | Webhook 重试 | 自动重试和手动重试 | P1 |
| 8 | API 调用日志 | 完整的调用审计日志 | P1 |
| 9 | SDK 生成 | 基于 OpenAPI 自动生成多语言 SDK | P2 |

### 3.2 API 设计规范

#### 3.2.1 基础规范

**Base URL**：
- 生产环境：`https://api.lest.dev/v1`
- 沙箱环境：`https://sandbox-api.lest.dev/v1`

**认证方式**：
- Bearer Token：`Authorization: Bearer {access_token}`
- API Key：`X-API-Key: {api_key}`

**请求格式**：
- Content-Type: `application/json`
- Accept: `application/json`

**响应格式**：
```json
{
  "code": 200,
  "message": "success",
  "data": { ... },
  "timestamp": "2026-05-25T10:00:00Z",
  "requestId": "req-xxx-xxx"
}
```

**错误响应**：
```json
{
  "code": 404,
  "message": "TASK_NOT_FOUND",
  "messageDetail": "任务不存在",
  "timestamp": "2026-05-25T10:00:00Z",
  "requestId": "req-xxx-xxx",
  "errors": [
    { "field": "taskId", "message": "任务 ID 格式不正确" }
  ]
}
```

#### 3.2.2 通用错误码

| HTTP 状态码 | 业务错误码前缀 | 说明 |
|-------------|---------------|------|
| 400 | 4xxx | 请求参数错误 |
| 401 | - | 未认证 |
| 403 | 9xxx | 无权限 |
| 404 | 1xxx | 资源不存在 |
| 409 | - | 资源冲突 |
| 429 | - | 请求过于频繁 |
| 500 | 9999 | 服务器内部错误 |

#### 3.2.3 分页规范

```
GET /api/v1/tasks?page=1&size=20&sort=createdAt,desc

响应:
{
  "code": 200,
  "data": {
    "content": [...],
    "page": 1,
    "size": 20,
    "totalElements": 100,
    "totalPages": 5,
    "first": true,
    "last": false
  }
}
```

### 3.3 API 模块划分

#### 3.3.1 认证与用户 API

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/v1/auth/login` | POST | 用户登录 |
| `/api/v1/auth/refresh` | POST | 刷新 Token |
| `/api/v1/auth/logout` | POST | 退出登录 |
| `/api/v1/users` | GET | 获取用户列表 |
| `/api/v1/users/{userId}` | GET | 获取用户详情 |
| `/api/v1/users/{userId}` | PUT | 更新用户信息 |

#### 3.3.2 项目 API

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/v1/projects` | GET | 获取项目列表 |
| `/api/v1/projects` | POST | 创建项目 |
| `/api/v1/projects/{projectId}` | GET | 获取项目详情 |
| `/api/v1/projects/{projectId}` | PUT | 更新项目 |
| `/api/v1/projects/{projectId}` | DELETE | 删除项目 |
| `/api/v1/projects/{projectId}/members` | GET | 获取项目成员 |
| `/api/v1/projects/{projectId}/members` | POST | 添加项目成员 |
| `/api/v1/projects/{projectId}/members/{userId}` | DELETE | 移除项目成员 |
| `/api/v1/projects/{projectId}/iterations` | GET | 获取迭代列表 |
| `/api/v1/projects/{projectId}/iterations` | POST | 创建迭代 |

#### 3.3.3 任务 API

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/v1/tasks` | GET | 获取任务列表 |
| `/api/v1/tasks` | POST | 创建任务 |
| `/api/v1/tasks/{taskId}` | GET | 获取任务详情 |
| `/api/v1/tasks/{taskId}` | PUT | 更新任务 |
| `/api/v1/tasks/{taskId}` | DELETE | 删除任务 |
| `/api/v1/tasks/{taskId}/comments` | GET | 获取任务评论 |
| `/api/v1/tasks/{taskId}/comments` | POST | 添加任务评论 |
| `/api/v1/tasks/{taskId}/worklog` | GET | 获取任务工时 |
| `/api/v1/tasks/{taskId}/worklog` | POST | 记录工时 |
| `/api/v1/tasks/{taskId}/commits` | GET | 获取关联提交 |
| `/api/v1/tasks/{taskId}/commits` | POST | 关联提交 |
| `/api/v1/tasks/{taskId}/merge-requests` | GET | 获取关联 MR |
| `/api/v1/tasks/{taskId}/merge-requests` | POST | 关联 MR |

#### 3.3.4 代码 API

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/v1/repositories` | GET | 获取仓库列表 |
| `/api/v1/repositories` | POST | 创建仓库 |
| `/api/v1/repositories/{repoId}` | GET | 获取仓库详情 |
| `/api/v1/repositories/{repoId}/merge-requests` | GET | 获取 MR 列表 |
| `/api/v1/repositories/{repoId}/commits` | GET | 获取提交列表 |
| `/api/v1/merge-requests/{mrId}` | GET | 获取 MR 详情 |

#### 3.3.5 CI/CD API

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/v1/ci/pipelines` | GET | 获取流水线列表 |
| `/api/v1/ci/pipelines/{pipelineId}` | GET | 获取流水线详情 |
| `/api/v1/ci/pipelines/{pipelineId}/trigger` | POST | 触发流水线 |
| `/api/v1/ci/pipelines/{pipelineId}/cancel` | POST | 取消流水线 |
| `/api/v1/ci/jobs/{jobId}/logs` | GET | 获取构建日志 |

#### 3.3.6 会议 API

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/v1/meetings` | GET | 获取会议列表 |
| `/api/v1/meetings` | POST | 创建会议 |
| `/api/v1/meetings/{meetingId}` | GET | 获取会议详情 |
| `/api/v1/meetings/{meetingId}` | PUT | 更新会议 |
| `/api/v1/meetings/{meetingId}/cancel` | POST | 取消会议 |
| `/api/v1/meetings/{meetingId}/minutes` | GET | 获取会议纪要 |
| `/api/v1/meetings/{meetingId}/minutes` | PUT | 更新会议纪要 |

#### 3.3.7 通知 API

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/v1/notifications` | GET | 获取通知列表 |
| `/api/v1/notifications/{notificationId}` | GET | 获取通知详情 |
| `/api/v1/notifications/mark-read` | POST | 标记已读 |
| `/api/v1/notifications/settings` | GET | 获取通知设置 |
| `/api/v1/notifications/settings` | PUT | 更新通知设置 |

#### 3.3.8 开放平台管理 API

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/v1/open/applications` | GET | 获取应用列表 |
| `/api/v1/open/applications` | POST | 创建应用 |
| `/api/v1/open/applications/{appId}` | GET | 获取应用详情 |
| `/api/v1/open/applications/{appId}` | PUT | 更新应用 |
| `/api/v1/open/applications/{appId}/secrets` | POST | 重置密钥 |
| `/api/v1/open/webhooks` | GET | 获取 Webhook 列表 |
| `/api/v1/open/webhooks` | POST | 创建 Webhook |
| `/api/v1/open/webhooks/{webhookId}` | PUT | 更新 Webhook |
| `/api/v1/open/webhooks/{webhookId}` | DELETE | 删除 Webhook |
| `/api/v1/open/webhooks/{webhookId}/test` | POST | 测试 Webhook |
| `/api/v1/open/webhooks/{webhookId}/deliveries` | GET | 获取投递记录 |

### 3.4 Webhook 事件设计

#### 3.4.1 事件结构

所有 Webhook 事件统一使用以下结构：

```json
{
  "id": "evt-xxx-xxx-xxx",
  "type": "task.created",
  "timestamp": "2026-05-25T10:00:00Z",
  "version": "2026-05-25",
  "data": {
    // 事件具体数据
  }
}
```

**签名头**：
```
X-LEST-Signature: sha256=xxxxxx
X-LEST-Timestamp: 1716625200
X-LEST-Webhook-Id: webhook-xxx
X-LEST-Event-Type: task.created
```

**签名算法**（HMAC-SHA256）：
```
signature = HMAC-SHA256(secret, timestamp + "." + payload)
```

#### 3.4.2 事件类型清单

| 事件类型 | 触发时机 | 数据内容 | 优先级 |
|---------|---------|---------|--------|
| **任务事件** | | | |
| `task.created` | 任务创建时 | task 对象 | P0 |
| `task.updated` | 任务更新时 | task 对象 + changedFields | P0 |
| `task.deleted` | 任务删除时 | taskId, task 对象 | P0 |
| `task.assigned` | 任务分配时 | task 对象 + assignee | P0 |
| `task.completed` | 任务完成时 | task 对象 | P0 |
| `task.commented` | 任务评论时 | taskId, comment 对象 | P1 |
| `task.status_changed` | 任务状态变更时 | taskId, oldStatus, newStatus | P0 |
| **项目事件** | | | |
| `project.created` | 项目创建时 | project 对象 | P0 |
| `project.updated` | 项目更新时 | project 对象 | P0 |
| `project.archived` | 项目归档时 | project 对象 | P1 |
| `project.member_added` | 成员加入时 | projectId, user 对象 | P1 |
| `project.member_removed` | 成员移除时 | projectId, userId | P1 |
| **迭代事件** | | | |
| `iteration.created` | 迭代创建时 | iteration 对象 | P1 |
| `iteration.started` | 迭代开始时 | iteration 对象 | P0 |
| `iteration.completed` | 迭代完成时 | iteration 对象 | P0 |
| **代码事件** | | | |
| `repository.linked` | 仓库关联时 | repository 对象 | P1 |
| `repository.unlinked` | 仓库取消关联时 | repositoryId | P1 |
| `merge_request.opened` | MR 创建时 | mergeRequest 对象 | P0 |
| `merge_request.merged` | MR 合并时 | mergeRequest 对象 | P0 |
| `merge_request.closed` | MR 关闭时 | mergeRequest 对象 | P1 |
| `merge_request.review_requested` | 请求评审时 | mergeRequest 对象 + reviewer | P1 |
| **CI 事件** | | | |
| `pipeline.started` | 流水线开始时 | pipeline 对象 | P0 |
| `pipeline.completed` | 流水线完成时 | pipeline 对象 | P0 |
| `pipeline.failed` | 流水线失败时 | pipeline 对象 + failedJobs | P0 |
| **会议事件** | | | |
| `meeting.scheduled` | 会议创建时 | meeting 对象 | P0 |
| `meeting.cancelled` | 会议取消时 | meeting 对象 | P1 |
| `meeting.started` | 会议开始时 | meeting 对象 | P1 |
| `meeting.ended` | 会议结束时 | meeting 对象 | P1 |
| `meeting.minutes_shared` | 纪要分享时 | meeting 对象 + minutes 对象 | P1 |
| **发布事件** | | | |
| `release.created` | 发布创建时 | release 对象 | P0 |
| `release.approved` | 发布审批通过时 | release 对象 | P0 |
| `release.rejected` | 发布审批拒绝时 | release 对象 | P0 |
| `release.deploy_started` | 发布部署开始时 | release 对象 | P0 |
| `release.deploy_completed` | 发布部署完成时 | release 对象 | P0 |
| `release.deploy_failed` | 发布部署失败时 | release 对象 + error | P0 |
| **绩效事件** | | | |
| `performance.goal_achieved` | 绩效目标达成时 | userId, metric 对象 | P1 |
| `performance.report_ready` | 绩效报告生成时 | report 对象 | P1 |
| **通知事件** | | | |
| `notification.sent` | 通知发送时 | notification 对象 | P2 |
| `notification.read` | 通知已读时 | notificationId | P2 |
| **认证事件** | | | |
| `user.created` | 用户创建时 | user 对象 | P0 |
| `user.updated` | 用户更新时 | user 对象 | P1 |
| `user.disabled` | 用户禁用时 | user 对象 | P0 |
| `user.login` | 用户登录时 | user 对象 + loginInfo | P1 |
| **系统事件** | | | |
| `system.backup_completed` | 备份完成时 | backup 对象 | P1 |
| `system.maintenance_scheduled` | 维护计划时 | maintenance 对象 | P0 |

#### 3.4.3 Webhook 配置

```json
{
  "id": "wh-xxx-xxx",
  "name": "我的 Jira 同步 Webhook",
  "url": "https://my-app.com/webhooks/lest",
  "secret": "whsec_xxx",
  "events": [
    "task.created",
    "task.updated",
    "task.status_changed"
  ],
  "active": true,
  "sslVerify": true,
  "headers": {
    "X-Custom-Header": "custom-value"
  },
  "retryPolicy": {
    "maxRetries": 3,
    "retryDelay": 60,
    "retryBackoff": "exponential"
  },
  "createdAt": "2026-05-25T10:00:00Z",
  "updatedAt": "2026-05-25T10:00:00Z"
}
```

### 3.5 OAuth2 设计

#### 3.5.1 授权流程

**Authorization Code 流程**：

```
1. 用户点击"授权"
   ↓
2. 跳转到授权页面
   GET https://lest.dev/oauth/authorize?
       client_id=xxx&
       redirect_uri=https://my-app.com/callback&
       response_type=code&
       scope=tasks:read tasks:write&
       state=random_state
   ↓
3. 用户确认授权
   ↓
4. 回调到应用
   https://my-app.com/callback?code=xxx&state=random_state
   ↓
5. 应用获取 Access Token
   POST https://lest.dev/oauth/token
   {
     "grant_type": "authorization_code",
     "client_id": "xxx",
     "client_secret": "xxx",
     "code": "xxx",
     "redirect_uri": "https://my-app.com/callback"
   }
   ↓
6. 返回 Token
   {
     "access_token": "xxx",
     "token_type": "Bearer",
     "expires_in": 3600,
     "refresh_token": "xxx",
     "scope": "tasks:read tasks:write"
   }
```

#### 3.5.2 权限范围

| 范围 | 说明 | 粒度 |
|------|------|------|
| `tasks:read` | 读取任务 | 粗粒度 |
| `tasks:write` | 创建、更新、删除任务 | 粗粒度 |
| `projects:read` | 读取项目 | 粗粒度 |
| `projects:write` | 创建、更新、删除项目 | 粗粒度 |
| `users:read` | 读取用户信息 | 粗粒度 |
| `meetings:read` | 读取会议 | 粗粒度 |
| `meetings:write` | 创建、更新会议 | 粗粒度 |
| `notifications:read` | 读取通知 | 粗粒度 |
| `notifications:write` | 更新通知设置 | 粗粒度 |
| `webhooks:manage` | 管理 Webhook | 粗粒度 |
| `*` | 所有权限 | 管理员 |

### 3.6 API 限流策略

#### 3.6.1 限流规则

| 级别 | 范围 | 默认限制 | 窗口 |
|------|------|---------|------|
| 未认证 | IP | 60 次/分钟 | 滑动窗口 |
| 个人令牌 | 用户 | 600 次/分钟 | 滑动窗口 |
| OAuth 应用 | 应用 | 1000 次/分钟 | 滑动窗口 |
| Webhook 投递 | 应用 | 100 次/分钟 | 滑动窗口 |

#### 3.6.2 限流响应头

```
X-RateLimit-Limit: 600
X-RateLimit-Remaining: 550
X-RateLimit-Reset: 1716625200
X-RateLimit-Window: 60
```

#### 3.6.3 限流错误响应

```json
{
  "code": 429,
  "message": "RATE_LIMIT_EXCEEDED",
  "messageDetail": "请求过于频繁，请稍后再试",
  "retryAfter": 30
}
```

### 3.7 页面原型

#### 3.7.1 开放平台概览页

```
┌────────────────────────────────────────────────────────────────────────────┐
│ 开放平台                                              [API文档] [开发者社区] │
├────────────────────────────────────────────────────────────────────────────┤
│ 概览                                                                       │
│ ┌──────────────────────┬──────────────────────┬──────────────────────┐   │
│ │ API 调用 (本月)       │ Webhook 投递 (本月)   │ 开发者应用           │   │
│ │     12.3M           │     456K            │      12             │   │
│ │  ↑ 15% vs 上月      │  ↑ 8% vs 上月       │                     │   │
│ └──────────────────────┴──────────────────────┴──────────────────────┘   │
├────────────────────────────────────────────────────────────────────────────┤
│ 快速开始                                                                    │
│ ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐                  │
│ │ 生成令牌  │  │ 创建应用  │  │ 配置Webhook│  │ 查看API文档│                  │
│ └──────────┘  └──────────┘  └──────────┘  └──────────┘                  │
├────────────────────────────────────────────────────────────────────────────┤
│ 最近活动                                                                    │
│ ┌────────────────────────────────────────────────────────────────────────┐│
│ │ API 调用趋势                                                             ││
│ │ ▁▂▃▅▆▇█▇▆▅▄▃▂▃▄▅▆▇█▇▆▅                                           ││
│ │ (折线图，显示最近30天调用量)                                            ││
│ └────────────────────────────────────────────────────────────────────────┘│
└────────────────────────────────────────────────────────────────────────────┘
```

#### 3.7.2 API Key 管理页

```
┌────────────────────────────────────────────────────────────────────────────┐
│ 个人访问令牌                                          [生成新令牌]           │
├────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│ ⚠️  注意：令牌只显示一次，请妥善保存。                                        │
│                                                                             │
│ ┌────────────────────────────────────────────────────────────────────────┐│
│ │ 令牌名称          │ 权限范围          │ 创建时间      │ 最后使用  │ 操作 ││
│ ├────────────────────────────────────────────────────────────────────────┤│
│ │ CI/CD 集成        │ tasks:read       │ 2026-05-20   │ 2026-05-25│[撤销]││
│ │                    │ projects:read    │               │ 10:00      │     ││
│ ├────────────────────────────────────────────────────────────────────────┤│
│ │ 数据同步脚本      │ tasks:read       │ 2026-05-18   │ 2026-05-25│[撤销]││
│ │                    │ tasks:write      │               │ 08:00      │     ││
│ │                    │ projects:read   │               │            │     ││
│ └────────────────────────────────────────────────────────────────────────┘│
└────────────────────────────────────────────────────────────────────────────┘

生成令牌弹窗:
┌────────────────────────────────────────────────────────────────────────────┐
│ 生成个人访问令牌                                                   [×]    │
├────────────────────────────────────────────────────────────────────────────┤
│ 令牌名称: [CI/CD 集成____________]                                        │
│                                                                             │
│ 权限范围:                                                                │
│ ☑ tasks:read    读取任务                                                 │
│ ☑ tasks:write   创建、更新、删除任务                                      │
│ ☑ projects:read 读取项目                                                 │
│ ☐ projects:write 创建、更新、删除项目                                      │
│ ☐ users:read    读取用户信息                                             │
│ ☐ webhooks:manage 管理 Webhook                                          │
│                                                                             │
│ 过期时间: [90天 ▼]                                                        │
│                                                                             │
│                              [取消]  [生成令牌]                            │
└────────────────────────────────────────────────────────────────────────────┘
```

#### 3.7.3 Webhook 管理页

```
┌────────────────────────────────────────────────────────────────────────────┐
│ Webhook 管理                                        [创建 Webhook]          │
├────────────────────────────────────────────────────────────────────────────┤
│ [全部] [启用] [禁用]                                                       │
├────────────────────────────────────────────────────────────────────────────┤
│ ┌────────────────────────────────────────────────────────────────────────┐│
│ │ 我的 Jira 同步 Webhook                                    [已启用]    ││
│ │ URL: https://my-app.com/webhooks/lest                                  ││
│ │ 事件: task.created, task.updated, task.status_changed                   ││
│ │ 投递: 成功率 99.5% │ 最后投递: 2026-05-25 10:00                      ││
│ │ ────────────────────────────────────────────────────────────────────  ││
│ │ [测试] [编辑] [查看日志] [禁用] [删除]                                ││
│ └────────────────────────────────────────────────────────────────────────┘│
│ ┌────────────────────────────────────────────────────────────────────────┐│
│ │ GitHub 同步 Webhook                                        [已启用]    ││
│ │ URL: https://my-app.com/webhooks/github                                ││
│ │ 事件: merge_request.opened, merge_request.merged                      ││
│ │ 投递: 成功率 100% │ 最后投递: 2026-05-25 09:30                        ││
│ │ ────────────────────────────────────────────────────────────────────  ││
│ │ [测试] [编辑] [查看日志] [禁用] [删除]                                ││
│ └────────────────────────────────────────────────────────────────────────┘│
└────────────────────────────────────────────────────────────────────────────┘
```

---



## 4. 扩展功能（V2.0/V3.0）

### 4.X.1 Bulk API 批量操作（V2.0）


支持大数据量操作，减少 API 调用次数：

| 接口 | 说明 |
|------|------|
| POST /bulk/issues | 批量创建 Issue（最多 100 条/次）|
| PUT /bulk/issues | 批量更新 Issue 字段 |
| POST /bulk/transitions | 批量变更 Issue 状态 |
| GET /bulk/issues/search | 批量查询 Issue（返回更多数据）|

**批量响应：** 返回每个操作的独立结果（部分成功时不影响其他操作）。

### 4.X.2 数据导入/导出 API（V3.0）


| 接口 | 说明 |
|------|------|
| POST /import/jira | 从 Jira 导入数据 |
| POST /import/csv | 从 CSV 导入 Issue |
| GET /export/issues | 导出 Issue 为 CSV/JSON/Excel |
| GET /export/worklog | 导出工时数据 |

**Jira 导入映射：**
- Project → Project
- Issue → Issue
- Custom Fields → Custom Fields（按类型匹配）
- Workflow → Workflow（通过 V2.0 API 创建）

### 4.X.3 App Marketplace API（V4.0）


为插件市场预留 API：

| 接口 | 说明 |
|------|------|
| GET /apps | 获取应用列表 |
| GET /apps/{id} | 获取应用详情 |
| POST /apps/install | 安装应用 |
| DELETE /apps/{id} | 卸载应用 |
| GET /apps/{id}/config | 获取应用配置 |

### 4.X.4 Audit Event API（V3.0）


| 接口 | 说明 |
|------|------|
| GET /audit/events | 查询审计事件（支持时间范围、用户、类型过滤）|
| GET /audit/events/{id} | 获取单个事件详情 |
| GET /audit/export | 导出审计日志（CSV/JSON）|

**审计事件类型：** 登录/登出、创建/更新/删除、权限变更、设置变更、API 调用。

### 4.X.5 Webhook 增强（V2.0）


| 功能 | 说明 |
|------|------|
| Webhook 重试 | 失败时自动重试（最多 5 次，指数退避）|
| Webhook 签名 | 每个请求带 HMAC 签名，验证来源 |
| Webhook 测试 | 发送测试事件验证 Webhook 可达性 |
| Webhook 日志 | 记录每个 Webhook 的请求和响应历史 |

## 5. 接口设计

### 5.1 API Key 接口

#### 5.1.1 生成个人访问令牌
```
POST /open/tokens
Authorization: Bearer {accessToken}
Content-Type: application/json

请求:
{
  "name": "CI/CD 集成",
  "scopes": ["tasks:read", "projects:read"],
  "expiresIn": 90
}

响应:
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "tok-xxx",
    "name": "CI/CD 集成",
    "token": "lest_tok_xxxxxxxxxxxxx",  // 只在创建时返回
    "scopes": ["tasks:read", "projects:read"],
    "expiresAt": "2026-08-23T10:00:00Z",
    "createdAt": "2026-05-25T10:00:00Z"
  }
}
```

#### 5.1.2 撤销访问令牌
```
DELETE /open/tokens/{tokenId}
Authorization: Bearer {accessToken}

响应:
{
  "code": 200,
  "message": "success"
}
```

### 5.2 OAuth2 接口

#### 5.2.1 获取授权 URL
```
GET /oauth/authorize?client_id=xxx&redirect_uri=xxx&scope=xxx&state=xxx

响应: 302 重定向到授权页面
```

#### 5.2.2 获取 Access Token
```
POST /oauth/token
Content-Type: application/json

请求:
{
  "grant_type": "authorization_code",
  "client_id": "xxx",
  "client_secret": "xxx",
  "code": "xxx",
  "redirect_uri": "xxx"
}

响应:
{
  "access_token": "xxx",
  "token_type": "Bearer",
  "expires_in": 3600,
  "refresh_token": "xxx",
  "scope": "tasks:read tasks:write"
}
```

### 5.3 Webhook 接口

#### 5.3.1 创建 Webhook
```
POST /open/webhooks
Authorization: Bearer {accessToken}
Content-Type: application/json

请求:
{
  "name": "我的 Webhook",
  "url": "https://my-app.com/webhooks/lest",
  "events": ["task.created", "task.updated", "task.status_changed"],
  "secret": "whsec_xxx",
  "active": true,
  "headers": {
    "X-Custom-Header": "custom-value"
  }
}

响应:
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "wh-xxx",
    "name": "我的 Webhook",
    "url": "https://my-app.com/webhooks/lest",
    "events": ["task.created", "task.updated", "task.status_changed"],
    "secret": "whsec_xxx",
    "active": true,
    "createdAt": "2026-05-25T10:00:00Z"
  }
}
```

#### 5.3.2 测试 Webhook
```
POST /open/webhooks/{webhookId}/test
Authorization: Bearer {accessToken}

请求:
{
  "event": "task.created"
}

响应:
{
  "code": 200,
  "message": "success",
  "data": {
    "deliveryId": "dlv-xxx",
    "status": "success",
    "statusCode": 200,
    "responseTime": 150,
    "responseBody": "OK"
  }
}
```

#### 5.3.3 获取 Webhook 投递记录
```
GET /open/webhooks/{webhookId}/deliveries?page=1&size=20

响应:
{
  "code": 200,
  "data": {
    "content": [
      {
        "id": "dlv-xxx",
        "event": "task.created",
        "status": "success",
        "statusCode": 200,
        "request": {
          "url": "https://my-app.com/webhooks/lest",
          "headers": { ... },
          "body": { ... }
        },
        "response": {
          "statusCode": 200,
          "body": "OK",
          "time": 150
        },
        "attempt": 1,
        "createdAt": "2026-05-25T10:00:00Z"
      }
    ]
  }
}
```

---

## 6. 数据库设计

### 6.1 表结构

#### open_api_token API 令牌表

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | VARCHAR(32) | PK | 令牌 ID |
| user_id | BIGINT | NOT NULL | 用户 ID |
| name | VARCHAR(128) | NOT NULL | 令牌名称 |
| token | VARCHAR(64) | NOT NULL, UNIQUE | 令牌值（hash 存储） |
| scopes | JSON | | 权限范围 |
| expires_at | DATETIME | | 过期时间 |
| last_used_at | DATETIME | | 最后使用时间 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| **索引** | | | |
| uk_token | | UNIQUE | 令牌唯一索引 |
| idx_user_id | | | 用户 ID 索引 |

#### open_oauth_application OAuth 应用表

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | VARCHAR(32) | PK | 应用 ID |
| user_id | BIGINT | NOT NULL | 创建者用户 ID |
| name | VARCHAR(128) | NOT NULL | 应用名称 |
| description | VARCHAR(512) | | 应用描述 |
| client_id | VARCHAR(64) | NOT NULL, UNIQUE | Client ID |
| client_secret | VARCHAR(256) | NOT NULL | Client Secret（加密存储） |
| redirect_uris | JSON | | 允许的回调地址 |
| scopes | JSON | | 默认权限范围 |
| homepage | VARCHAR(256) | | 应用主页 |
| logo_url | VARCHAR(256) | | Logo URL |
| status | VARCHAR(16) | DEFAULT 'active' | 状态：active/disabled |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| **索引** | | | |
| uk_client_id | | UNIQUE | Client ID 唯一索引 |
| idx_user_id | | | 创建者 ID 索引 |

#### open_oauth_token OAuth 访问令牌表

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | VARCHAR(32) | PK | Token ID |
| application_id | VARCHAR(32) | NOT NULL | 应用 ID |
| user_id | BIGINT | NOT NULL | 授权用户 ID |
| access_token | VARCHAR(256) | NOT NULL, UNIQUE | Access Token（hash 存储） |
| refresh_token | VARCHAR(256) | UNIQUE | Refresh Token（hash 存储） |
| scopes | JSON | | 权限范围 |
| expires_at | DATETIME | NOT NULL | Access Token 过期时间 |
| refresh_expires_at | DATETIME | | Refresh Token 过期时间 |
| revoked | TINYINT | DEFAULT 0 | 是否已撤销 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| **索引** | | | |
| uk_access_token | | UNIQUE | Access Token 唯一索引 |
| idx_application_id | | | 应用 ID 索引 |
| idx_user_id | | | 用户 ID 索引 |

#### open_webhook Webhook 表

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | VARCHAR(32) | PK | Webhook ID |
| user_id | BIGINT | NOT NULL | 创建者用户 ID |
| application_id | VARCHAR(32) | | OAuth 应用 ID（可选） |
| name | VARCHAR(128) | NOT NULL | Webhook 名称 |
| url | VARCHAR(512) | NOT NULL | 回调 URL |
| secret | VARCHAR(128) | NOT NULL | 签名密钥 |
| events | JSON | NOT NULL | 订阅的事件类型 |
| headers | JSON | | 自定义请求头 |
| ssl_verify | TINYINT | DEFAULT 1 | 是否验证 SSL |
| active | TINYINT | DEFAULT 1 | 是否启用 |
| retry_policy | JSON | | 重试策略 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| **索引** | | | |
| idx_user_id | | | 创建者 ID 索引 |
| idx_active | | | 启用状态索引 |

#### open_webhook_delivery Webhook 投递记录表

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | VARCHAR(32) | PK | 投递记录 ID |
| webhook_id | VARCHAR(32) | NOT NULL | Webhook ID |
| event_id | VARCHAR(64) | NOT NULL | 事件 ID |
| event_type | VARCHAR(64) | NOT NULL | 事件类型 |
| payload | JSON | NOT NULL | 投递内容 |
| status | VARCHAR(16) | NOT NULL | 状态：pending/success/failed |
| status_code | INT | | HTTP 响应码 |
| response_body | TEXT | | 响应内容 |
| response_time | INT | | 响应时间（毫秒） |
| attempt | INT | DEFAULT 1 | 尝试次数 |
| error_message | VARCHAR(512) | | 错误信息 |
| next_retry_at | DATETIME | | 下次重试时间 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| completed_at | DATETIME | | 完成时间 |
| **索引** | | | |
| idx_webhook_id | | | Webhook ID 索引 |
| idx_status | | | 状态索引 |
| idx_created_at | | | 创建时间索引 |

#### open_api_log API 调用日志表

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| request_id | VARCHAR(64) | NOT NULL, UNIQUE | 请求 ID |
| user_id | BIGINT | | 用户 ID |
| application_id | VARCHAR(32) | | OAuth 应用 ID |
| token_id | VARCHAR(32) | | API Token ID |
| method | VARCHAR(8) | NOT NULL | HTTP 方法 |
| path | VARCHAR(256) | NOT NULL | 请求路径 |
| query_params | JSON | | 查询参数 |
| request_body | TEXT | | 请求体 |
| response_status | INT | | 响应状态码 |
| response_time | INT | | 响应时间（毫秒） |
| ip_address | VARCHAR(45) | | IP 地址 |
| user_agent | VARCHAR(256) | | User Agent |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| **索引** | | | |
| uk_request_id | | UNIQUE | 请求 ID 唯一索引 |
| idx_user_id | | | 用户 ID 索引 |
| idx_created_at | | | 创建时间索引 |

---

## 7. 验收标准

| 用例 | 验收标准 |
|------|---------|
| UC-001 | 可生成个人访问令牌并调用 API |
| UC-002 | 可创建 OAuth2 应用并实现授权流程 |
| UC-003 | 可创建 Webhook 订阅并接收事件通知 |
| UC-004 | Webhook 支持 HMAC-SHA256 签名验证 |
| UC-005 | 可手动触发 Webhook 测试并查看投递记录 |
| UC-006 | API 支持限流并返回正确的限流响应头 |
| UC-007 | 完整的 OpenAPI 3.0 文档可在线查看 |
| UC-008 | 所有 API 调用记录审计日志 |
| UC-009 | Webhook 投递失败自动重试 |

---

## 8. 错误码

### 8.1 开放平台模块错误码 (18000-18999)

| 错误码 | 枚举常量 | HTTP 状态码 | 说明 |
|---------|----------|-------------|------|
| 18000 | `TOKEN_NOT_FOUND` | 404 | 访问令牌不存在 |
| 18001 | `TOKEN_EXPIRED` | 401 | 访问令牌已过期 |
| 18002 | `TOKEN_REVOKED` | 401 | 访问令牌已撤销 |
| 18003 | `TOKEN_SCOPE_INVALID` | 403 | 访问令牌权限不足 |
| 18004 | `APPLICATION_NOT_FOUND` | 404 | OAuth 应用不存在 |
| 18005 | `APPLICATION_DISABLED` | 403 | OAuth 应用已被禁用 |
| 18006 | `CLIENT_SECRET_INVALID` | 401 | Client Secret 错误 |
| 18007 | `REDIRECT_URI_MISMATCH` | 400 | 回调地址不匹配 |
| 18008 | `AUTHORIZATION_PENDING` | 400 | 授权待确认 |
| 18009 | `AUTHORIZATION_EXPIRED` | 400 | 授权码已过期 |
| 18010 | `WEBHOOK_NOT_FOUND` | 404 | Webhook 不存在 |
| 18011 | `WEBHOOK_DELIVERY_FAILED` | 500 | Webhook 投递失败 |
| 18012 | `WEBHOOK_URL_INVALID` | 400 | Webhook URL 无效 |
| 18013 | `WEBHOOK_SIGNATURE_INVALID` | 401 | Webhook 签名验证失败 |
| 18014 | `RATE_LIMIT_EXCEEDED` | 429 | API 调用频率超限 |
| 18015 | `API_KEY_REQUIRED` | 401 | 缺少 API Key |
| 18016 | `SCOPE_NOT_GRANTED` | 403 | 权限范围未授权 |

---

## 9. 版本历史

| 版本 | 日期 | 修改内容 | 作者 |
|------|------|---------|------|
| V1.0 | 2026-05-25 | 初始版本 | - |
| V1.1 | 2026-05-25 | 补充 open_oauth_application 表（OAuth应用）、open_oauth_token 表（OAuth访问令牌） | - |
