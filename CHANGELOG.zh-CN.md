# 变更日志 — LEST Platform

> 版本发布遵循 **语义化版本 Semantic Versioning (SemVer)**，按周节奏迭代。

> 版本格式: `v{MAJOR}.{MINOR}.{PATCH}`
> - **MAJOR** — 不兼容的架构重大变更
> - **MINOR** — 每周新功能迭代
> - **PATCH** — 当周 Bug 修复补丁

> 发版周期：每周四发布 MINOR 版本

---

## [Unreleased]

### 基础设施

- **新增域名规划** — 注册 `lest.work`（产品平台）和 `lest.top`（官网），建立子域名架构。详见 [docs/guide/DOMAIN_PLAN.md](./docs/guide/DOMAIN_PLAN.md)
- **新增 `docs/guide/DOMAIN_PLAN.md`** — 域名规划文档，含生产环境架构、DNS 配置、Nginx 示例
- **更新 API 域名** — staging/production 环境 API 地址统一指向 `https://api.lest.work`
- **更新 CORS 白名单** — Gateway 允许 `app.lest.work` 和 `lest.top` 跨域请求

---

## [v0.2.0] — 2026-06-01 (W23)

### 本周主题
**项目与任务前端页面**

### 新功能

#### 数据库
- `project`/`project_member`/`iteration`/`milestone`/`milestone_iteration` 建表 SQL 加入 `01_lest_platform_init.sql`
- `task`/`label`/`task_label`/`task_watcher`/`task_worklog`/`task_comment`/`task_commit`/`task_dependency` 建表 SQL 加入统一初始化 SQL
- `sys_menu` 新增项目管理与任务管理菜单及按钮权限数据

#### 前端 API
- `api/project/index.ts` 补全：Iteration CRUD、Milestone CRUD、Member 添加/移除
- `api/project/model/index.ts` 补全：`Iteration`、`Milestone`、`IterationParam` 类型
- `api/task/index.ts` 补全：Worklog、Comment、Label、子任务、Gantt、看板（类型化）
- `api/task/model/index.ts` 补全：`TaskWorklog`、`TaskComment`、`Label`、`BoardColumn` 类型

#### 前端页面
- `views/project/index.vue` — 项目列表（卡片网格、新建/编辑/归档/删除）
- `views/project/detail/index.vue` — 项目详情（概况/成员/迭代/里程碑四 Tab）
- `views/task/index.vue` — 任务列表（搜索/分页/状态更改/详情抽屉）
- `views/task/board/index.vue` — 任务看板（三列看板、按项目/迭代过滤、快捷状态流转）
- `views/release/index.vue` — 发布列表页（卡片视图）
- `views/release/detail/index.vue` — 发布详情（产物/关联 Issue）
- `views/task/gantt/index.vue` — 甘特图视图（ECharts，支持新建任务）

#### 发布管理 (`lest-release`)
- 发布计划 CRUD（版本号/状态/目标日期/Git 信息）
- 发布制品管理（Artifact 上传/下载/元数据）
- 发布关联 Issue/Task（支持按 category 分类）
- `api/release/index.ts` + `api/release/model/index.ts` 前端 API

#### 后端
- 修复 `lest-monitor` 路由端口（8081→9100）
- 禁用 7 个空模块路由（meeting/notification/ai/performance/open/plugin/wakapi）
- 统一所有模块 `NACOS_ENABLED=false`
- 修复 `ReleasePlanServiceImpl` 状态名称返回中文

### Bug 修复
- **关键运行时崩溃修复**:
  - `ProjectMemberMapper`: 将软删除 (`SET deleted=1`) 改为硬删除 (`DELETE`)，因表无 `deleted` 列
  - `TaskCommentMapper`: 同上修复
- **后端逻辑修复**:
  - `IterationMapper`: 所有 SELECT 查询增加 `deleted=0` 过滤，`deleteById` 改为软删除，resultMap/SELECT 增加 `deleted` 列
  - `IterationMapper.countDateConflicts`: 排除已删除迭代的日期冲突检查
  - `IterationMapper.countByProjectId`: 仅统计未删除迭代
  - `TaskMapper.selectByParentId`: 增加 `deleted=0` 过滤
  - `ReleasePlanMapper`: 从 resultMap 移除重复的 `createdBy`/`updatedBy`（与 `BaseEntity` 字段冲突），resultMap/SELECT 增加 `deleted` 列
  - `ReleasePlan.java`: 移除重复的 `createdBy`/`updatedBy` 字段（类型冲突：`BaseEntity` 为 `String`，此处为 `Long`）
- **前后端 API 接线修复**:
  - `updateIteration`: 修复 `data.id` → `data.iterationId`（原值为 undefined）
  - `listWorklogs`: 修复 `/{id}/worklog` → `/{id}/worklog/list`
  - `addWorklog`: 修复 `/worklog` → `/{id}/worklog`（taskId 置于路径）
  - `listComments`: 修复 `/{id}/comment` → `/{id}/comment/list`
  - `addComment`/`removeComment`: 修复 URL 路径，补充 taskId 参数
  - `listLabels`/`addLabel`: 修复 `/label/*` → `/project/{id}/label/*`
- **新增后端接口**:
  - `GET /{id}/comment/list` — 查询任务评论
  - `POST /{id}/comment` — 添加任务评论
  - `DELETE /{id}/comment/{commentId}` — 删除任务评论
- **代码规范**:
  - `IterationServiceImpl`: 增加 `STATUS_PLANNING`/`IN_PROGRESS`/`COMPLETED` 状态常量
  - `ProjectServiceImpl`: 增加 `STATUS_ACTIVE`/`ARCHIVED` 状态常量
- **数据库迁移**: `migration_002_iteration_soft_delete.sql` — 为 `iteration` 表增加 `deleted` 列
- 修复 `task/gantt/index.vue` 模板语法错误

### CI/CD 与发布

#### 多仓库自动化 Docker 镜像发布
每次发版自动构建并推送 **16 个 Docker 镜像**到三个镜像仓库：

| 镜像仓库 | 地址格式 | 示例 |
|----------|---------|------|
| **GitHub Container Registry (GHCR)** | `ghcr.io/lest-work/lest-platform/<service>:<tag>` | `ghcr.io/lest-work/lest-platform/gateway:0.2.0` |
| **Docker Hub** | `yshan2028/lest-platform-<service>:<tag>` | `yshan2028/lest-platform-gateway:0.2.0` |
| **阿里云 ACR** | `crpi-qxx4stoxzuctl22y.cn-hangzhou.personal.cr.aliyuncs.com/lest-platform/<service>:<tag>` | `crpi-qxx4stoxzuctl22y.cn-hangzhou.personal.cr.aliyuncs.com/lest-platform/gateway:0.2.0` |

16 个服务全部发布：`gateway`、`auth`、`modules-system`、`modules-project`、`modules-task`、`modules-release`、`modules-job`、`modules-file`、`modules-meeting`、`modules-notification`、`modules-ai`、`modules-open`、`modules-performance`、`modules-plugin`、`modules-wakapi`、`visual-monitor`。

每个镜像均包含三种 tag：`x.y.z`（语义化版本）、`<sha>-<short-sha>`（Commit SHA）、`latest`。

拉取镜像示例：

```bash
# GitHub Container Registry (推荐)
docker pull ghcr.io/lest-work/lest-platform/gateway:0.2.0
docker pull ghcr.io/lest-work/lest-platform/auth:0.2.0
docker pull ghcr.io/lest-work/lest-platform/modules-system:0.2.0

# 拉取所有模块镜像
for svc in gateway auth modules-system modules-project modules-task modules-release modules-job modules-file modules-meeting modules-notification modules-ai modules-open modules-performance modules-plugin modules-wakapi visual-monitor; do
  docker pull ghcr.io/lest-work/lest-platform/${svc}:0.2.0
done
```

详细部署说明见 [docs/guide/DEPLOYMENT.md](./docs/guide/DEPLOYMENT.md)。

### 已知待完成
- 项目燃尽图（ECharts）
- 任务工时/评论详情面板
- 看板拖拽排序


---

## [v0.1.0] — 2026-05-29 (W22)

### 本周主题
**基础框架搭建**

### 新功能

#### 后端
- **认证服务** (`lest-auth`, port: 8096)
  - 图形验证码登录 (`POST /auth/login`)
  - JWT Token 签发与刷新
  - 登出清除 Redis Session
  - 获取当前用户信息+菜单权限
- **系统管理服务** (`lest-system`, port: 8081)
  - 用户/角色/菜单/部门/岗位 CRUD
  - 字典类型与字典数据管理
  - 系统参数配置
  - 操作日志与登录日志
  - 在线用户管理与强退
  - 定时任务管理（Quartz）
  - 公告管理
  - 仪表盘 API：最新动态 + 小组成员在线状态
- **项目管理服务** (`lest-project`, port: 8082)
  - 项目 CRUD + 归档
  - 项目成员管理
  - 迭代管理
  - 里程碑管理
- **任务管理服务** (`lest-task`, port: 8083)
  - 任务 CRUD + 状态流转
  - 看板视图
  - 甘特图数据
  - 子任务
  - 工时记录
  - 关联代码提交
  - GitLab/GitHub Webhook 接入
  - 标签管理
- **网关** (`lest-gateway`, port: 8080)
  - 统一路由 StripPrefix=1
  - 验证码过滤器（WebFlux）
  - Token 鉴权过滤器
  - 接口白名单配置

#### 前端 (`frontend-pc`)
- Vue 3 + TypeScript + Element Plus + Vite 完整脚手架
- 登录页（验证码）
- 首页仪表盘：最新动态/小组成员/项目进度/我的任务 卡片
- 系统管理全套页面：用户/角色/菜单/部门/岗位/字典/参数/公告
- 监控中心：操作日志/登录日志/在线用户/定时任务
- API 层类型化（TypeScript model/index.ts）
- 响应格式对齐 RuoYi

#### 数据库
- 完整初始化 SQL（含初始数据）
- 系统表：`sys_user/role/menu/dept/post/dict/config/log/notice/job` + 11张 Quartz 表

#### 基础设施
- Docker Compose 本地开发环境（14 个容器）
  - MySQL 8.0, Redis 7, Nacos 2.x, Kafka, MinIO, Nginx Frontend
  - Gateway, Auth, System, Project, Task, Release, File, Job 服务

### 技术栈
- Spring Boot `4.0.3` + Spring Cloud `2025.1.0` + Spring Cloud Alibaba `2025.1.0.0`
- 原生 MyBatis + PageHelper（非 MyBatis-Plus）
- 无 Lombok，手写 getter/setter
- Vue 3 + TypeScript + Element Plus + EleAdmin Pro

### 已知待完成
- 项目燃尽图（ECharts）尚未开发
- 任务详情页工时/评论功能待完善


---

> 📌 查看里程碑规划: [docs/MILESTONES.zh-CN.md](./docs/MILESTONES.zh-CN.md)
