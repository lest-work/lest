# 发布管理 PRD
> **📌 Jira 映射**：Jira Releases + Version Management
>
> **功能定位**：版本规划、发布审批、变更记录、版本回滚


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

发布管理是 LEST Platform 实现规范化软件发布流程的核心模块。通过与 CI/CD 系统深度集成，平台支持版本规划、发布审批、变更记录、版本回滚等功能，确保软件发布安全可控。

### 1.2 功能目标

- 支持版本号规划和语义化版本管理
- 提供发布申请和审批流程
- 自动收集版本变更内容（任务、MR、构建产物）
- 支持多环境发布（测试/预发/生产）
- 支持发布变更记录和审计
- 支持版本回滚
- 提供发布统计和报表

### 1.3 目标用户

- **开发人员**：提交发布申请、查看发布状态
- **测试人员**：验证版本功能、执行预发验证
- **运维人员**：执行生产发布、进行版本回滚
- **项目经理**：审批发布申请、跟踪发布进度
- **发布经理**：管理发布计划、协调发布流程

---

## 2. 用户故事

### 2.1 版本规划

#### US-001: 创建版本
**作为** 发布经理，**我希望** 创建版本规划，**以便** 定义发布范围和目标。

验收标准：
- [ ] 填写版本号（支持语义化版本号）
- [ ] 选择所属项目
- [ ] 设置版本发布时间
- [ ] 选择目标环境
- [ ] 添加版本目标和说明
- [ ] 关联迭代或里程碑

#### US-002: 规划版本内容
**作为** 项目经理，**我希望** 规划版本包含的功能，**以便** 明确发布范围。

验收标准：
- [ ] 从迭代中选择要包含的任务
- [ ] 手动添加功能描述
- [ ] 设置功能优先级
- [ ] 显示功能完成状态
- [ ] 支持调整功能顺序

### 2.2 发布流程

#### US-003: 提交发布申请
**作为** 开发人员，**我希望** 提交发布申请，**以便** 申请将功能发布到目标环境。

验收标准：
- [ ] 选择目标版本
- [ ] 选择要发布的构建
- [ ] 填写发布说明
- [ ] 指定发布审批人
- [ ] 自动收集关联的 MR 和任务
- [ ] 显示变更摘要

#### US-004: 审批发布
**作为** 项目经理，**我希望** 审批发布申请，**以便** 控制发布风险。

验收标准：
- [ ] 查看变更内容
- [ ] 查看构建状态
- [ ] 查看测试报告（如果有）
- [ ] 支持批准/拒绝
- [ ] 支持添加审批意见
- [ ] 支持转交审批

#### US-005: 执行发布
**作为** 运维人员，**我希望** 执行版本发布，**以便** 将软件部署到目标环境。

验收标准：
- [ ] 审批通过后解锁发布
- [ ] 选择发布策略（滚动/蓝绿/金丝雀）
- [ ] 显示发布进度
- [ ] 显示部署状态和日志
- [ ] 支持中断发布
- [ ] 发布成功后自动更新版本状态

### 2.3 版本回滚

#### US-006: 回滚版本
**作为** 运维人员，**我希望** 回滚到之前的版本，**以便** 快速恢复生产环境。

验收标准：
- [ ] 显示可回滚的版本列表
- [ ] 支持选择回滚到的版本
- [ ] 显示回滚影响范围
- [ ] 执行回滚前需确认
- [ ] 显示回滚进度和结果
- [ ] 自动记录回滚原因

### 2.4 发布记录

#### US-007: 查看发布历史
**作为** 团队成员，**我希望** 查看发布历史，**以便** 了解版本发布情况。

验收标准：
- [ ] 显示所有发布记录
- [ ] 显示发布版本、环境、状态、时间
- [ ] 显示发布人
- [ ] 支持按环境/项目/时间筛选
- [ ] 支持查看发布详情

#### US-008: 查看发布详情
**作为** 团队成员，**我希望** 查看发布详情，**以便** 了解版本变更内容。

验收标准：
- [ ] 显示版本基本信息
- [ ] 显示变更内容列表（任务、MR）
- [ ] 显示构建信息
- [ ] 显示部署日志
- [ ] 显示审批记录
- [ ] 支持下载构建产物

### 2.5 发布统计

#### US-009: 查看发布统计
**作为** 发布经理，**我希望** 查看发布统计，**以便** 了解发布效率和稳定性。

验收标准：
- [ ] 显示发布频率统计
- [ ] 显示发布成功率
- [ ] 显示平均发布时长
- [ ] 显示回滚次数统计
- [ ] 支持按时间段筛选

---

## 3. 功能详细设计

### 3.1 功能清单

| 序号 | 功能点 | 功能描述 | 优先级 |
|------|--------|---------|--------|
| 1 | 版本规划 | 创建和管理版本 | P0 |
| 2 | 版本内容规划 | 定义版本包含的功能 | P0 |
| 3 | 发布申请 | 提交发布请求 | P0 |
| 4 | 发布审批 | 审批发布申请 | P0 |
| 5 | 发布执行 | 执行部署到目标环境 | P0 |
| 6 | 发布回滚 | 回滚到之前版本 | P0 |
| 7 | 发布历史 | 查看发布记录 | P0 |
| 8 | 发布详情 | 查看变更内容和日志 | P0 |
| 9 | 发布统计 | 发布效率统计 | P2 |
| 10 | 发布通知 | 发布状态变更通知 | P1 |
| 11 | 发布-任务集成 | 版本变更追踪任务关联 | P0 |
| 12 | 发布-CI集成 | CI Webhook 触发发布 | P1 |
| 13 | 发布-会议集成 | 发布审批会议通知 | P2 |

### 3.2 发布状态定义

```java
public enum ReleaseStatus {
    PLANNING,       // 规划中
    READY,          // 待发布
    PENDING_APPROVAL,  // 待审批
    APPROVED,       // 已审批
    DEPLOYING,      // 部署中
    DEPLOYED,       // 已部署
    ROLLING_BACK,   // 回滚中
    ROLLED_BACK,    // 已回滚
    REJECTED,       // 已拒绝
    CANCELLED       // 已取消
}

public enum ReleaseEnvironment {
    DEV("开发环境", 1),
    TEST("测试环境", 2),
    STAGING("预发环境", 3),
    PRODUCTION("生产环境", 4);
}

public enum DeploymentStrategy {
    ROLLING_UPDATE("滚动更新"),
    BLUE_GREEN("蓝绿部署"),
    CANARY("金丝雀发布");
}
```

### 3.3 页面原型

#### 3.3.1 发布列表页

```
┌────────────────────────────────────────────────────────────────────────────┐
│ 发布管理                                            [版本规划] [+ 创建发布] │
├────────────────────────────────────────────────────────────────────────────┤
│ 环境: [全部 ▼]  状态: [全部 ▼]  版本: [搜索...]                         │
├────────────────────────────────────────────────────────────────────────────┤
│ 版本号      │ 环境   │ 状态      │ 变更内容        │ 发布时间    │ 操作  │
│ ─────────────────────────────────────────────────────────────────────────── │
│ v1.2.0     │ 生产   │ ✓ 已部署  │ 5任务 3MR      │ 2026-05-20 │ [详情]│
│ v1.1.0     │ 生产   │ ✓ 已部署  │ 8任务 5MR      │ 2026-05-10 │ [详情]│
│ v1.0.0     │ 生产   │ ✓ 已部署  │ 12任务 8MR     │ 2026-05-01 │ [详情]│
├────────────────────────────────────────────────────────────────────────────┤
│ v1.3.0-beta│ 预发   │ ⏳ 部署中  │ 6任务 4MR      │ 2026-05-25 │ [详情]│
│ v1.3.0     │ 测试   │ ✓ 已部署  │ 6任务 4MR      │ 2026-05-24 │ [详情]│
└────────────────────────────────────────────────────────────────────────────┘
```

#### 3.3.2 发布详情页

```
┌────────────────────────────────────────────────────────────────────────────┐
│ ← 返回  Release v1.2.0                                           [回滚]    │
├────────────────────────────────────────────────────────────────────────────┤
│ 基本信息                                                                    │
│ ┌──────────────────────────────────────────────────────────────────────────┐│
│ │ 版本: v1.2.0    环境: 生产环境    状态: 已部署                         ││
│ │ 发布人: 张三    发布时间: 2026-05-20 14:00                            ││
│ │ 构建: #156  ✓ 成功    变更: 5任务 3MR                                  ││
│ └──────────────────────────────────────────────────────────────────────────┘│
├────────────────────────────────────────────────────────────────────────────┤
│ 变更内容                                                                    │
│ ┌──────────────────────────────────────────────────────────────────────────┐│
│ │ 任务:                                                                  ││
│ │ • [#123] 实现用户登录功能  ✓                                           ││
│ │ • [#124] 实现用户注册功能  ✓                                           ││
│ │ • [#125] 实现密码找回功能  ✓                                           ││
│ │                                                                          ││
│ │ MR:                                                                    ││
│ │ • MR #45 feat: 实现登录功能  ✓ 已合并                                 ││
│ │ • MR #46 feat: 实现注册功能  ✓ 已合并                                 ││
│ └──────────────────────────────────────────────────────────────────────────┘│
├────────────────────────────────────────────────────────────────────────────┤
│ 发布日志                                                                    │
│ ┌──────────────────────────────────────────────────────────────────────────┐│
│ │ 14:00:00 [张三] 开始部署                                               ││
│ │ 14:00:05 [系统] 拉取镜像 lest-platform:v1.2.0                         ││
│ │ 14:00:30 [系统] 更新 K8S Deployment                                   ││
│ │ 14:01:00 [系统] 滚动更新完成，共 3 个 Pod                              ││
│ │ 14:01:05 [系统] 健康检查通过                                          ││
│ │ 14:01:10 [系统] 部署成功                                              ││
│ └──────────────────────────────────────────────────────────────────────────┘│
└────────────────────────────────────────────────────────────────────────────┘
```

---

## 4. 接口设计

### 4.1 发布接口

#### 4.1.1 创建版本
```
POST /api/plugin/lest-release/version
Authorization: Bearer {accessToken}
Content-Type: application/json

请求:
{
  "projectId": 1,
  "version": "v1.3.0",
  "environment": "test",
  "releaseDate": "2026-05-30",
  "description": "用户认证模块正式发布"
}

响应:
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "version": "v1.3.0",
    "status": "planning"
  }
}
```

#### 4.1.2 获取发布列表
```
GET /release?projectId=1&environment=production&status=deployed&page=1&size=20
Authorization: Bearer {accessToken}

响应:
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "version": "v1.2.0",
        "projectId": 1,
        "projectName": "LEST Platform",
        "environment": "production",
        "status": "deployed",
        "buildId": 156,
        "buildNumber": 156,
        "taskCount": 5,
        "mrCount": 3,
        "releaseBy": {
          "userId": 1,
          "username": "张三"
        },
        "releasedAt": "2026-05-20 14:00:00",
        "createdAt": "2026-05-20 10:00:00"
      }
    ],
    "total": 20,
    "page": 1,
    "size": 20
  }
}
```

#### 4.1.3 获取发布详情
```
GET /release/{id}
Authorization: Bearer {accessToken}

响应:
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "version": "v1.2.0",
    "projectId": 1,
    "environment": "production",
    "status": "deployed",
    "description": "用户认证模块正式发布",
    "build": {
      "id": 156,
      "buildNumber": 156,
      "status": "success",
      "commitSha": "abc1234"
    },
    "changes": {
      "tasks": [
        {
          "id": 123,
          "title": "实现用户登录功能",
          "status": "completed"
        }
      ],
      "mergeRequests": [
        {
          "id": 1,
          "number": 45,
          "title": "feat: 实现登录功能",
          "status": "merged"
        }
      ]
    },
    "approval": {
      "approved": true,
      "approvedBy": {
        "userId": 2,
        "username": "李四"
      },
      "approvedAt": "2026-05-20 11:00:00",
      "comment": "功能验收通过"
    },
    "releaseBy": {
      "userId": 1,
      "username": "张三"
    },
    "deployment": {
      "strategy": "rolling",
      "startedAt": "2026-05-20 14:00:00",
      "finishedAt": "2026-05-20 14:01:10",
      "logs": ["部署日志..."]
    },
    "rollback": {
      "available": true,
      "previousVersion": "v1.1.0"
    },
    "releasedAt": "2026-05-20 14:00:00",
    "createdAt": "2026-05-20 10:00:00"
  }
}
```

#### 4.1.4 提交发布申请
```
POST /api/plugin/lest-release/apply
Authorization: Bearer {accessToken}
Content-Type: application/json

请求:
{
  "versionId": 1,
  "buildId": 156,
  "description": "用户认证模块正式发布",
  "approverId": 2
}

响应:
{
  "code": 200,
  "message": "success",
  "data": {
    "releaseId": 1,
    "status": "pending_approval"
  }
}
```

#### 4.1.5 审批发布
```
POST /api/plugin/lest-release/{id}/approve
Authorization: Bearer {accessToken}
Content-Type: application/json

请求:
{
  "approved": true,
  "comment": "功能验收通过，同意发布"
}

响应:
{
  "code": 200,
  "message": "success"
}
```

#### 4.1.6 执行发布
```
POST /api/plugin/lest-release/{id}/deploy
Authorization: Bearer {accessToken}
Content-Type: application/json

请求:
{
  "strategy": "rolling"
}

响应:
{
  "code": 200,
  "message": "success",
  "data": {
    "deploymentId": 1,
    "status": "deploying"
  }
}
```

#### 4.1.7 回滚发布
```
POST /api/plugin/lest-release/{id}/rollback
Authorization: Bearer {accessToken}
Content-Type: application/json

请求:
{
  "targetVersion": "v1.1.0",
  "reason": "生产环境出现严重问题"
}

响应:
{
  "code": 200,
  "message": "success",
  "data": {
    "rollbackId": 1,
    "status": "rolling_back"
  }
}
```

### 4.2 跨模块集成接口

#### 4.2.1 获取版本关联的任务列表
```
GET /release/{id}/tasks
Authorization: Bearer {accessToken}

响应:
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "taskId": 123,
        "taskTitle": "实现用户登录功能",
        "taskType": "task",
        "status": "completed",
        "assigneeId": 1,
        "assigneeName": "张三",
        "completedAt": "2026-05-20 10:00:00"
      }
    ],
    "total": 5
  }
}
```

#### 4.2.2 获取版本关联的 MR 列表
```
GET /release/{id}/merge-requests
Authorization: Bearer {accessToken}

响应:
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "mrId": 45,
        "number": 45,
        "title": "feat: 实现登录功能",
        "status": "merged",
        "authorUsername": "张三"
      }
    ],
    "total": 3
  }
}
```

#### 4.2.3 CI Webhook 触发发布更新
```
POST /webhook/release/ci
Content-Type: application/json

请求:
{
  "event": "pipeline.completed",
  "repositoryId": 1,
  "buildNumber": 156,
  "status": "success",
  "branch": "main",
  "commitSha": "abc1234"
}

说明: 当 CI 流水线完成时，通知发布模块更新版本关联的构建状态
```

#### 4.2.4 向通知模块推送发布事件
```
内部调用: POST /notification/event
Content-Type: application/json

请求:
{
  "event": "RELEASE_DEPLOY_STARTED",
  "data": {
    "releaseId": 1,
    "version": "v1.2.0",
    "environment": "production",
    "deployerId": 1
  }
}
```

### 4.3 发布模块通知事件定义

| 事件编码 | 事件名称 | 触发时机 | 通知对象 | 说明 |
|---------|---------|---------|---------|------|
| RELEASE_CREATED | 发布创建 | 创建版本规划时 | 项目管理员 | P1 |
| RELEASE_APPROVED | 发布审批通过 | 审批人批准发布时 | 发布申请人、运维人员 | P0 |
| RELEASE_REJECTED | 发布审批拒绝 | 审批人拒绝发布时 | 发布申请人 | P0 |
| RELEASE_DEPLOY_STARTED | 发布部署开始 | 开始执行部署时 | 相关运维人员 | P0 |
| RELEASE_DEPLOY_COMPLETED | 发布部署完成 | 部署成功完成时 | 所有项目成员 | P0 |
| RELEASE_DEPLOY_FAILED | 发布部署失败 | 部署失败时 | 运维人员、发布申请人 | P0 |
| RELEASE_ROLLBACK_STARTED | 发布回滚开始 | 开始执行回滚时 | 运维人员 | P0 |
| RELEASE_ROLLBACK_COMPLETED | 发布回滚完成 | 回滚成功完成时 | 运维人员、项目管理员 | P0 |

---

## 5. 数据库设计

### 5.1 表结构

#### release_version 版本表

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| project_id | BIGINT | NOT NULL | 项目 ID |
| version | VARCHAR(32) | NOT NULL | 版本号 |
| environment | VARCHAR(16) | NOT NULL | 目标环境 |
| status | VARCHAR(16) | NOT NULL | 状态 |
| description | TEXT | | 版本描述 |
| release_date | DATE | | 计划发布日期 |
| target_build_id | BIGINT | | 目标构建 ID |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| **索引** | | | |
| idx_project_id | | | 项目 ID 索引 |
| uk_project_version | | UNIQUE | (project_id, version) 唯一索引 |

#### release_release 发布记录表

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| version_id | BIGINT | NOT NULL | 版本 ID |
| build_id | BIGINT | NOT NULL | 构建 ID |
| environment | VARCHAR(16) | NOT NULL | 环境 |
| status | VARCHAR(16) | NOT NULL | 发布状态 |
| description | TEXT | | 发布说明 |
| strategy | VARCHAR(16) | | 部署策略 |
| deploy_started_at | DATETIME | | 部署开始时间 |
| deploy_finished_at | DATETIME | | 部署完成时间 |
| deploy_logs | JSON | | 部署日志 |
| rollback_to_version | VARCHAR(32) | | 回滚目标版本 |
| rollback_reason | VARCHAR(512) | | 回滚原因 |
| released_by | BIGINT | NOT NULL | 发布人 |
| released_at | DATETIME | | 发布时间 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| **索引** | | | |
| idx_version_id | | | 版本 ID 索引 |
| idx_status | | | 状态索引 |

#### release_approval 发布审批表

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| release_id | BIGINT | NOT NULL | 发布记录 ID |
| approver_id | BIGINT | NOT NULL | 审批人 ID |
| status | VARCHAR(16) | NOT NULL | 审批状态 |
| comment | VARCHAR(512) | | 审批意见 |
| approved_at | DATETIME | | 审批时间 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

#### release_change 发布变更关联表

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| release_id | BIGINT | NOT NULL | 发布记录 ID |
| change_type | VARCHAR(16) | NOT NULL | 变更类型：task / mr / commit |
| change_id | BIGINT | NOT NULL | 变更 ID |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| **索引** | | | |
| idx_release_id | | | 发布记录 ID 索引 |

---

## 6. 验收标准

| 用例 | 验收标准 |
|------|---------|
| UC-001 | 可创建版本规划 |
| UC-002 | 可提交发布申请 |
| UC-003 | 审批人可批准/拒绝发布 |
| UC-004 | 发布执行显示部署进度 |
| UC-005 | 发布成功后更新版本状态 |
| UC-006 | 可回滚到之前版本 |
| UC-007 | 发布历史正确显示所有记录 |
| UC-008 | 发布详情显示完整变更内容 |

---

## 7. 错误码

### 7.1 发布模块错误码 (15000-15999)

| 错误码 | 枚举常量 | HTTP 状态码 | 说明 |
|---------|----------|-------------|------|
| 15000 | `VERSION_NOT_FOUND` | 404 | 版本不存在 |
| 15001 | `RELEASE_NOT_FOUND` | 404 | 发布记录不存在 |
| 15002 | `RELEASE_PENDING_APPROVAL` | 409 | 发布待审批 |
| 15003 | `RELEASE_APPROVAL_REJECTED` | 403 | 发布被拒绝 |
| 15004 | `DEPLOYMENT_IN_PROGRESS` | 409 | 部署进行中 |
| 15005 | `ROLLBACK_NOT_ALLOWED` | 403 | 不允许回滚 |
| 15006 | `BUILD_NOT_SUCCESS` | 400 | 构建不成功 |
| 15007 | `ENVIRONMENT_NOT_MATCH` | 400 | 环境不匹配 |
| 15008 | `RELEASE_VERSION_CONFLICT` | 409 | 版本号冲突 |
| 15009 | `RELEASE_APPROVER_NOT_FOUND` | 404 | 审批人不存在 |
| 15010 | `RELEASE_CI_BUILD_REQUIRED` | 400 | 缺少关联的 CI 构建记录 |

---

## 8. 版本历史

| 版本 | 日期 | 修改内容 | 作者 |
|------|------|---------|------|
| V1.0 | 2026-05-25 | 初始版本 | - |
| V1.1 | 2026-05-25 | 补充发布-任务关联接口、发布-CI集成接口、通知推送接口、完整发布通知事件定义、新增 4 个错误码 | - |
