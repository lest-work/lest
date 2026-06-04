# V10-STAB-006 · Core E2E 冒烟脚本

> **目的**：验证「登录 → 建项目 → 加成员 → 建任务 → 看板拖拽 → 评论 → 通知」主流程在本地环境可完整跑通。
>
> **交付物**：`docs/tests/core-e2e-smoke.http` HTTP Client 脚本 + 本文档的执行说明和期望结果。

---

## 1. 前置条件

| 项目 | 说明 |
| --- | --- |
| 数据库 | MySQL 8，执行 `backend/docker/mysql/db/01_lest_platform_init.sql`，可直接 `source` 完整脚本获得管理员、测试账户 + 示例项目。 |
| Redis | 默认连接 `127.0.0.1:6379`，用于 token/限流（可使用 docker redis）。 |
| 后端服务 | 至少启动 `gateway`（8080）、`auth`（8096）、`project`（8082）、`task`（8083）、`notification`（8084）。建议运行：`mvn spring-boot:run -pl gateway -am`，其会级联启动依赖模块。 |
| 前端 | 可选，仅用于肉眼确认。脚本只依赖后端接口。 |
| 账户 | `admin/123456`（平台管理员）、`user/123456`（示例成员）。两者来自初始化 SQL。 |

> ⚠️ **网络入口均走 Gateway**：所有接口访问 `http://localhost:8080`，由 Gateway 路由到后端子服务。

---

## 2. 脚本文件说明

- `docs/tests/core-e2e-smoke.http`
  - JetBrains HTTP Client / VS Code REST Client 均可直接运行。
  - 顶部声明了变量（host、账户、项目名称等）。`{{$timestamp}}` 确保多次执行不会命名冲突。
  - 步骤会自动把登录得到的 `access_token` 写入 `@adminToken/@memberToken`，以及把新建的 `projectId`、`taskId` 存入变量。
  - 若使用 `curl`，可参考脚本中的报文依次执行。

运行方式示例（IDEA/VS Code）：

1. 打开 `.http` 文件。
2. 确认上方 `host`/账号信息，如需要改成远程环境则只需修改 `@host`。
3. 逐个点击 `Run`（或 `▶`）执行 11 条请求，所有响应 `code` 应为 `200`。

---

## 3. 步骤与期望结果

| # | 操作 | 期望输出 |
| --- | --- | --- |
| 1 | 管理员登录 `/auth/login` | 返回 `data.access_token`，被脚本写入 `@adminToken`。 |
| 2 | `POST /project` 创建项目 | `code=200`，返回“操作成功”。 |
| 3 | `GET /project/list?name=...` | `records[0].projectId` 被写入 `@projectId`。 |
| 4 | `POST /project/{id}/members` | 新增 `userId=2` 成员，返回 `code=200`。若已存在会提示“已是成员”，视为可接受。 |
| 5 | `POST /task` | 在项目下创建任务，返回 `code=200`。 |
| 6 | `GET /task/list?projectId=...&title=...` | 找到刚建的任务，脚本写入 `@taskId`。 |
| 7 | `PUT /task/{id}/move` | 将任务列更新为 `in_progress`，返回 `code=200`。 |
| 8 | `POST /task/{id}/comment` | 新增评论，返回 `code=200`。 |
| 9 | 成员 `user` 登录 | 获得 `@memberToken`。 |
| 10 | `GET /task/{id}` | 成员可查看任务详情（含刚刚的状态/评论）。 |
| 11 | `GET /notification/unread/count` | 成员能访问通知接口，返回一个 `data` 数字（可能为 0，视订阅规则而定）。 |

跑完后，可在数据库中看到：

- `project` 表新增 `Core Smoke <timestamp>` 记录。
- `task` 表新增 Smoke Task。
- `task_comment` 表新增评论。

如需清理，可删除对应项目或重新导入 SQL。

---

## 4. 常见失败 & 排查

| 现象 | 处理方式 |
| --- | --- |
| 登录接口超时或返回 5xx | 检查 `gateway` 与 `auth` 服务是否启动，Redis 是否可用。 |
| `project/list` 不返回新项目 | 确认 `POST /project` 是否成功（`code=200`），并检查 `mysql` 权限/自增 ID。可以手动查询 `SELECT * FROM project WHERE name LIKE 'Core Smoke%';`。 |
| `task` 接口 401 | 通常是 `@adminToken` 未设置（先执行步骤 1），或 Redis 没有启动导致 token 校验失败。 |
| `PUT /task/{id}/move` 返回 403 | 需要确保 `RequireProjectRoleAspect` 能找到 projectId。若脚本变量 `projectId` 未写入，重新执行步骤 3。 |
| `notification` 接口 404 | 确认 `notification` 模块已启动并通过 Gateway 注册 `/notification/**` 路由。 |

---

## 5. 完成打勾

- [x] 记录了可复制运行的 HTTP 脚本
- [x] 覆盖登录、项目、成员、任务、看板、评论、通知的核心链路
- [x] 给出排查指南

> 后续如需接入 CI，可将 `.http` 转换为 `k6`、`newman` 或 `pytest`，并通过 `mvn/ npm` pipeline 自动化运行。
