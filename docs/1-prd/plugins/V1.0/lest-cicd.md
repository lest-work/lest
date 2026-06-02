# CI/CD 持续集成 PRD
> **📌 Jira 映射**：Jira CI/CD Integration（Jenkins / GitLab CI / GitHub Actions）
>
> **功能定位**：构建流水线、构建状态、部署管理


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

CI/CD 是 LEST Platform 实现自动化构建、测试和部署的核心模块。通过与代码仓库深度集成，平台支持多种 CI/CD 提供商，帮助团队实现从代码提交到生产部署的全流程自动化。

### 1.2 功能目标

- 支持 Jenkins、GitLab CI、GitHub Actions 多种 CI 提供商
- 提供构建流水线配置和管理
- 实时展示构建状态和日志
- 支持构建触发规则配置
- 支持构建结果通知
- 为 AI 提供构建数据分析

### 1.3 目标用户

- **开发人员**：触发构建、查看构建日志和状态
- **运维人员**：配置 CI/CD 流水线、管理部署
- **项目经理**：查看项目构建统计和健康度
- **平台管理员**：管理 CI 提供商配置

---

## 2. 用户故事

### 2.1 CI 提供商管理

#### US-001: 配置 CI 提供商
**作为** 平台管理员，**我希望** 配置 CI 提供商连接，**以便** 平台能够获取 CI 数据。

验收标准：
- [ ] 支持配置 Jenkins（URL + API Token）
- [ ] 支持配置 GitLab CI（通过 GitLab 仓库集成）
- [ ] 支持配置 GitHub Actions（通过 GitHub 仓库集成）
- [ ] 支持测试连接
- [ ] 支持多个相同类型提供商的配置

### 2.2 流水线管理

#### US-002: 查看构建列表
**作为** 开发人员，**我希望** 查看项目的构建历史，**以便** 了解构建状态。

验收标准：
- [ ] 显示构建编号、状态、耗时
- [ ] 显示触发者和触发原因
- [ ] 显示分支和提交信息
- [ ] 支持按状态筛选（成功/失败/运行中）
- [ ] 支持按分支筛选
- [ ] 支持按时间范围筛选
- [ ] 实时更新运行中的构建状态

#### US-003: 查看构建详情
**作为** 开发人员，**我希望** 查看构建的详细信息和日志，**以便** 排查构建问题。

验收标准：
- [ ] 显示构建基本信息（编号、状态、耗时、分支）
- [ ] 显示流水线阶段列表
- [ ] 支持查看每个阶段的详细日志
- [ ] 支持查看构建产物列表
- [ ] 支持查看测试报告
- [ ] 支持查看代码覆盖率报告

#### US-004: 触发构建
**作为** 开发人员，**我希望** 手动触发构建，**以便** 测试代码变更。

验收标准：
- [ ] 支持选择分支触发构建
- [ ] 支持传递构建参数
- [ ] 显示构建触发结果
- [ ] 支持取消运行中的构建

#### US-005: 配置构建触发规则
**作为** 运维人员，**我希望** 配置构建触发规则，**以便** 实现自动化构建。

验收标准：
- [ ] 支持配置 Git Webhook 触发
- [ ] 支持配置定时触发（Cron 表达式）
- [ ] 支持配置分支过滤（白名单/黑名单）
- [ ] 支持配置触发条件（PR 创建/合并/标签推送等）

### 2.3 部署管理

#### US-006: 部署配置
**作为** 运维人员，**我希望** 配置部署环境，**以便** 实现自动化部署。

验收标准：
- [ ] 支持配置多环境（开发/测试/预发/生产）
- [ ] 支持配置部署目标（K8S / 服务器）
- [ ] 支持配置部署顺序（蓝绿/金丝雀/滚动）
- [ ] 支持配置部署变量

#### US-007: 执行部署
**作为** 运维人员，**我希望** 在平台内执行部署，**以便** 管理应用发布。

验收标准：
- [ ] 支持选择构建版本部署
- [ ] 支持选择目标环境
- [ ] 显示部署进度
- [ ] 支持部署审批流程
- [ ] 支持部署回滚

### 2.4 构建统计

#### US-008: 查看构建统计
**作为** 项目经理，**我希望** 查看项目构建统计，**以便** 了解团队的开发效率。

验收标准：
- [ ] 显示构建成功率趋势图
- [ ] 显示平均构建时长
- [ ] 显示构建频率统计
- [ ] 显示构建排队时长
- [ ] 支持按时间段筛选
- [ ] 支持导出统计数据

---

## 3. 功能详细设计

### 3.1 功能清单

| 序号 | 功能点 | 功能描述 | 优先级 |
|------|--------|---------|--------|
| 1 | Jenkins 集成 | 连接 Jenkins 获取构建数据 | P0 |
| 2 | GitLab CI 集成 | 获取 GitLab CI 构建数据 | P0 |
| 3 | GitHub Actions 集成 | 获取 GitHub Actions 数据 | P0 |
| 4 | 构建列表 | 显示构建历史 | P0 |
| 5 | 构建详情 | 显示构建日志、产物 | P0 |
| 6 | 手动触发构建 | 手动触发 CI 构建 | P1 |
| 7 | 构建触发规则 | 配置自动触发规则 | P1 |
| 8 | 部署配置 | 配置多环境部署 | P1 |
| 9 | 部署执行 | 执行部署、回滚 | P1 |
| 10 | 构建统计 | 构建数据统计 | P2 |

### 3.2 CI Provider 接口设计

```java
public interface CIClient {
    // 提供商类型
    String getType();

    // 连接测试
    boolean testConnection();

    // 构建
    List<Build> listBuilds(String jobName, int page, int size);
    Build getBuild(String jobName, int buildNumber);
    String getBuildLog(String jobName, int buildNumber);
    Build triggerBuild(String jobName, Map<String, String> params);
    boolean cancelBuild(String jobName, int buildNumber);

    // 工作节点
    List<Node> listNodes();

    // Webhook
    void handleWebhook(String payload);
}
```

### 3.3 页面原型

#### 3.3.1 构建列表页

```
┌────────────────────────────────────────────────────────────────────────────┐
│ CI/CD                                                         [配置流水线]   │
├────────────────────────────────────────────────────────────────────────────┤
│ 环境: [全部 ▼]  状态: [全部 ▼]  分支: [筛选分支...]                        │
├────────────────────────────────────────────────────────────────────────────┤
│ #156  ✓ 成功   lest-platform / feature/login    张三  2分钟前    3分12秒 │
├────────────────────────────────────────────────────────────────────────────┤
│ #155  ✗ 失败   lest-platform / feature/login    张三  10分钟前   1分23秒 │
│          └─❌ 单元测试失败 (TestLoginService)                             │
├────────────────────────────────────────────────────────────────────────────┤
│ #154  ⏳ 运行中  lest-platform / main             李四  15分钟前  -         │
│          └─🔄 [编译] → [测试] → [构建镜像] → [部署]                       │
├────────────────────────────────────────────────────────────────────────────┤
│ #153  ✓ 成功   lest-platform / main             王五  30分钟前   2分45秒 │
└────────────────────────────────────────────────────────────────────────────┘
```

#### 3.3.2 构建详情页

```
┌────────────────────────────────────────────────────────────────────────────┐
│ ← 返回  Build #156                                           [重新构建]    │
├────────────────────────────────────────────────────────────────────────────┤
│ 信息摘要                                                                    │
│ ┌──────────────────────────────────────────────────────────────────────────┐│
│ │ 状态: ✓ 成功     耗时: 3分12秒    触发: 张三 (手动)                     ││
│ │ 分支: feature/login    提交: abc1234  队列等待: 0秒                    ││
│ └──────────────────────────────────────────────────────────────────────────┘│
├────────────────────────────────────────────────────────────────────────────┤
│ 流水线阶段                                                                    │
│ ┌──────────────────────────────────────────────────────────────────────────┐│
│ │ ✓ 编译    45秒   └─ [echo "Compiling..."]                              ││
│ │ ✓ 测试    1分20秒 └─ [mvn test] 覆盖率: 78.5%                           ││
│ │ ✓ 构建    32秒   └─ [docker build -t lest-platform:v1.0.0 .]            ││
│ │ ✓ 推送    25秒   └─ [docker push registry.example.com/lest-platform...]││
│ │ ✓ 部署    10秒   └─ [kubectl apply -f deployment.yaml]                 ││
│ └──────────────────────────────────────────────────────────────────────────┘│
├────────────────────────────────────────────────────────────────────────────┤
│ [控制台输出]  [构建产物]  [测试报告]  [变更记录]                           │
│ ────────────────────────────────────────────────────────────────────────── │
│ 14:00:00 [lest-platform] $ echo "Starting build..."                      │
│ 14:00:01 [lest-platform] $ mvn clean package -DskipTests=false           │
│ 14:00:45 [lest-platform] [INFO] BUILD SUCCESS                            │
│ ...                                                                         │
└────────────────────────────────────────────────────────────────────────────┘
```

---

## 4. 接口设计

### 4.1 构建接口

#### 4.1.1 获取构建列表
```
GET /ci/build?repositoryId=1&branch=main&status=success&page=1&size=20
Authorization: Bearer {accessToken}

响应:
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "buildNumber": 156,
        "status": "success",
        "provider": "jenkins",
        "jobName": "lest-platform",
        "branch": "feature/login",
        "commitSha": "abc1234",
        "commitMessage": "feat: 实现登录功能",
        "trigger": {
          "type": "manual",
          "userId": 1,
          "username": "张三"
        },
        "duration": 192,
        "queuedDuration": 0,
        "coverage": 78.5,
        "startedAt": "2026-05-25 14:00:00",
        "finishedAt": "2026-05-25 14:03:12"
      }
    ],
    "total": 156,
    "page": 1,
    "size": 20
  }
}
```

#### 4.1.2 获取构建详情
```
GET /ci/build/{id}
Authorization: Bearer {accessToken}

响应:
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "buildNumber": 156,
    "status": "success",
    "provider": "jenkins",
    "jobName": "lest-platform",
    "branch": "feature/login",
    "commitSha": "abc1234",
    "commitMessage": "feat: 实现登录功能",
    "trigger": {
      "type": "manual",
      "userId": 1,
      "username": "张三"
    },
    "duration": 192,
    "queuedDuration": 0,
    "coverage": 78.5,
    "stages": [
      {
        "name": "编译",
        "status": "success",
        "duration": 45,
        "steps": [
          {
            "name": "echo",
            "status": "success",
            "output": "Compiling..."
          }
        ]
      },
      {
        "name": "测试",
        "status": "success",
        "duration": 80,
        "coverage": 78.5
      }
    ],
    "artifacts": [
      {
        "name": "lest-platform.jar",
        "size": "25MB",
        "downloadUrl": "/api/ci/build/1/artifact/lest-platform.jar"
      }
    ],
    "startedAt": "2026-05-25 14:00:00",
    "finishedAt": "2026-05-25 14:03:12"
  }
}
```

#### 4.1.3 触发构建
```
POST /ci/build/trigger
Authorization: Bearer {accessToken}
Content-Type: application/json

请求:
{
  "repositoryId": 1,
  "branch": "feature/login",
  "parameters": {
    "SKIP_TEST": "false",
    "ENV": "test"
  }
}

响应:
{
  "code": 200,
  "message": "success",
  "data": {
    "buildNumber": 157,
    "queueId": 12345
  }
}
```

### 4.2 部署接口

#### 4.2.1 获取部署列表
```
GET /ci/deployment?projectId=1&environment=prod&page=1&size=20
Authorization: Bearer {accessToken}
```

#### 4.2.2 执行部署
```
POST /ci/deployment
Authorization: Bearer {accessToken}
Content-Type: application/json

请求:
{
  "projectId": 1,
  "buildId": 156,
  "environment": "test",
  "strategy": "rolling"
}

响应:
{
  "code": 200,
  "message": "success",
  "data": {
    "deploymentId": 1,
    "status": "in_progress"
  }
}
```

### 4.2 CI Webhook 接口

#### 4.2.1 Jenkins Webhook 回调
```
POST /webhook/ci/jenkins
X-Jenkins-Token: {签名}

Body:
{
  "name": "lest-platform",
  "build": {
    "number": 156,
    "phase": "COMPLETED",
    "status": "SUCCESS",
    "url": "https://jenkins.example.com/job/lest-platform/156/",
    "scm": {
      "commit": "abc1234"
    }
  }
}

响应:
{
  "code": 200,
  "message": "success"
}
```

#### 4.2.2 GitLab CI Webhook 回调
```
POST /webhook/ci/gitlab
X-Gitlab-Token: {签名}

Body:
{
  "object_kind": "pipeline",
  "pipeline": {
    "id": 12345,
    "status": "success",
    "ref": "main",
    "sha": "abc1234"
  },
  "project": {
    "id": 1,
    "path_with_namespace": "lest-work/lest"
  }
}
```

#### 4.2.3 向任务模块推送构建完成事件
```
内部调用: POST /webhook/ci/build
Content-Type: application/json

请求:
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

#### 4.2.4 向通知模块推送构建事件
```
内部调用: POST /notification/event
Content-Type: application/json

请求:
{
  "event": "BUILD_FAILED",
  "data": {
    "buildId": 156,
    "jobName": "lest-platform",
    "branch": "feature/login",
    "triggeredBy": 1,
    "commitSha": "abc1234"
  }
}
```

### 4.3 CI 模块通知事件定义

| 事件编码 | 事件名称 | 触发时机 | 通知对象 | 说明 |
|---------|---------|---------|---------|------|
| BUILD_STARTED | 构建开始 | CI 构建启动时 | 构建触发者 | P1 |
| BUILD_SUCCESS | 构建成功 | CI 构建成功完成时 | 构建触发者、关注者 | P0 |
| BUILD_FAILED | 构建失败 | CI 构建失败时 | 构建触发者 | P0 |
| BUILD_CANCELLED | 构建取消 | 手动取消构建时 | 构建触发者 | P2 |
| DEPLOYMENT_SUCCESS | 部署成功 | 部署成功完成时 | 部署执行者 | P0 |
| DEPLOYMENT_FAILED | 部署失败 | 部署失败时 | 部署执行者 | P0 |

---

## 5. 数据库设计

### 5.1 表结构

#### ci_provider CI 提供商配置表

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| name | VARCHAR(64) | NOT NULL | 提供商名称 |
| type | VARCHAR(16) | NOT NULL | 类型：jenkins / gitlab / github |
| config | JSON | NOT NULL | 配置信息（URL、Token 等） |
| enabled | TINYINT | DEFAULT 1 | 启用状态 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

#### ci_build 构建记录表

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| repository_id | BIGINT | NOT NULL | 仓库 ID |
| provider_id | BIGINT | NOT NULL | CI 提供商 ID |
| job_name | VARCHAR(128) | NOT NULL | 任务名称 |
| build_number | INT | NOT NULL | 构建编号 |
| status | VARCHAR(16) | NOT NULL | 状态 |
| branch | VARCHAR(128) | | 分支 |
| commit_sha | VARCHAR(64) | | 提交 SHA |
| commit_message | VARCHAR(512) | | 提交消息 |
| trigger_type | VARCHAR(16) | | 触发类型 |
| trigger_user_id | BIGINT | | 触发用户 ID |
| duration | INT | | 耗时（秒） |
| queued_duration | INT | | 排队耗时（秒） |
| coverage | DECIMAL(5,2) | | 覆盖率 |
| queued_at | DATETIME | | 排队时间 |
| started_at | DATETIME | | 开始时间 |
| finished_at | DATETIME | | 结束时间 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| **索引** | | | |
| idx_repository_id | | | 仓库 ID 索引 |
| idx_status | | | 状态索引 |
| idx_branch | | | 分支索引 |

#### ci_deployment 部署记录表

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| project_id | BIGINT | NOT NULL | 项目 ID |
| build_id | BIGINT | NOT NULL | 构建 ID |
| environment | VARCHAR(16) | NOT NULL | 环境 |
| strategy | VARCHAR(16) | NOT NULL | 部署策略 |
| status | VARCHAR(16) | NOT NULL | 状态 |
| deployer_id | BIGINT | | 部署人 ID |
| started_at | DATETIME | | 开始时间 |
| finished_at | DATETIME | | 结束时间 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

---

## 6. 验收标准

| 用例 | 验收标准 |
|------|---------|
| UC-001 | 可配置 Jenkins 连接并测试成功 |
| UC-002 | 可获取并显示构建列表 |
| UC-003 | 可查看构建日志 |
| UC-004 | 可手动触发构建 |
| UC-005 | 运行中构建实时更新状态 |
| UC-006 | 可配置部署环境 |
| UC-007 | 可执行部署和回滚 |
| UC-008 | 可查看构建统计图表 |

---

## 7. 错误码

### 7.1 CI 模块错误码 (11000-11999)

| 错误码 | 枚举常量 | HTTP 状态码 | 说明 |
|---------|----------|-------------|------|
| 11000 | `PROVIDER_NOT_FOUND` | 404 | CI 提供商不存在 |
| 11001 | `PROVIDER_AUTH_FAILED` | 401 | CI 提供商授权失败 |
| 11002 | `BUILD_NOT_FOUND` | 404 | 构建不存在 |
| 11003 | `BUILD_TRIGGER_FAILED` | 500 | 触发构建失败 |
| 11004 | `BUILD_CANCEL_FAILED` | 500 | 取消构建失败 |
| 11005 | `DEPLOYMENT_NOT_FOUND` | 404 | 部署记录不存在 |
| 11006 | `CI_DEPLOYMENT_IN_PROGRESS` | 409 | CI 通道存在进行中的部署 |
| 11007 | `CI_ROLLBACK_FAILED` | 500 | CI 回滚失败 |
| 11008 | `CI_WEBHOOK_SIGNATURE_INVALID` | 401 | CI Webhook 签名验证失败 |
| 11009 | `BUILD_STATUS_UPDATE_FAILED` | 500 | 构建状态更新失败 |
| 11010 | `TASK_AUTO_CREATE_FAILED` | 500 | 构建失败自动创建 Bug 任务失败 |

---

## 8. 版本历史

| 版本 | 日期 | 修改内容 | 作者 |
|------|------|---------|------|
| V1.0 | 2026-05-25 | 初始版本 | - |
| V1.1 | 2026-05-25 | 修正错误码 11006 命名（CI_DEPLOYMENT_IN_PROGRESS）、补充 Jenkins/GitLab CI Webhook 接口、向任务/通知模块推送事件接口、补充完整构建通知事件定义、新增错误码、修正 WEBHOOK_SIGNATURE_INVALID 命名（CI_WEBHOOK_SIGNATURE_INVALID） | - |
