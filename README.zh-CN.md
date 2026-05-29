<p align="center">
  <img src="docs/assets/logo.png" alt="LEST Platform" width="120" />
</p>

<h1 align="center">LEST Platform</h1>

<p align="center">
  面向现代工程团队的开源云原生项目管理平台
</p>

<p align="center">
  <a href="https://github.com/yshan2028/Lest/releases"><img src="https://img.shields.io/badge/版本-v0.2.0-brightgreen.svg" alt="version"></a>
  <img src="https://img.shields.io/badge/许可证-MIT-blue.svg" alt="license">
  <img src="https://img.shields.io/badge/JDK-21+-blue.svg" alt="jdk">
  <img src="https://img.shields.io/badge/Spring%20Boot-4.0.3-green.svg" alt="spring boot">
  <img src="https://img.shields.io/badge/Vue-3.x-42b883.svg" alt="vue">
  <a href="README.md"><img src="https://img.shields.io/badge/Docs-English-blue.svg" alt="English"></a>
</p>

<p align="center">
  <a href="README.md">English</a> ·
  <a href="https://github.com/yshan2028/Lest/issues">反馈 Bug</a> ·
  <a href="https://github.com/yshan2028/Lest/issues">功能建议</a>
</p>

---

## 📖 项目简介

**LEST Platform** 是一款面向软件工程团队的全功能云原生项目与任务管理平台，提供项目全生命周期管理、迭代规划、任务跟踪、工时记录、发布管理和团队协作的一体化解决方案。

后端架构深度参考了经过大规模生产验证的 [RuoYi-Cloud](https://ruoyi.vip) 微服务框架，前端管理界面基于 [EleAdmin Pro](https://eleadmin.com) 这一优秀的 Vue 3 组件库构建。

> **关于许可证** — 本项目所依赖的核心框架均提供官方商业授权。如果您在商业产品中使用 RuoYi，建议前往 [ruoyi.vip](https://ruoyi.vip) 购买官方授权或赞助作者。如果您在生产项目中使用 EleAdmin Pro，请前往 [eleadmin.com](https://eleadmin.com) 购买正版商业授权，这是对开发者劳动的尊重，也是对持续开发的直接支持。

---

## ✨ 功能特性

| 模块 | 功能亮点 |
|------|---------|
| **认证服务** | 图形验证码登录、JWT Token 签发与刷新、Redis Session 管理 |
| **系统管理** | 用户 / 角色 / 菜单 / 部门 / 岗位 / 字典 / 参数 / 公告 |
| **审计日志** | 操作日志、登录日志、在线用户管理、强制退出 |
| **定时任务** | 基于 Quartz 的任务调度，含执行历史记录 |
| **项目管理** | 项目 CRUD、归档、成员管理、模板支持（敏捷 / 看板 / 瀑布流） |
| **迭代管理** | 迭代规划、状态生命周期、里程碑时间线 |
| **任务管理** | 任务增删改查、优先级与类型标签、子任务、指派、截止日期 |
| **任务看板** | 三列看板（待办 / 进行中 / 已完成），按项目和迭代过滤 |
| **工时记录** | 任务粒度工时录入，支持预估工时 vs 实际工时对比 |
| **文件服务** | 文件上传/下载，基于 MinIO 对象存储 |
| **仪表盘** | 实时动态流、成员在线状态、项目进度卡片 |

---

## 🏗️ 架构设计

```
                ┌──────────────────────────────────┐
                │        Nginx / 前端               │
                │   Vue 3 + TypeScript + EleAdmin   │
                └────────────────┬─────────────────┘
                                 │ HTTP /api/*
                ┌────────────────▼─────────────────┐
                │       API 网关 [8080]              │
                │   Spring Cloud Gateway + JWT 鉴权  │
                └──┬────┬────┬────┬────┬────┬──────┘
                   │    │    │    │    │    │
        ┌──────────▼─┐ ┌▼──┐ ┌▼──┐ ┌▼──┐ ┌▼──┐  ┌──────┐
        │ lest-auth  │ │sys│ │prj│ │tsk│ │job│  │ ...  │
        │  [8096]    │ │81 │ │82 │ │83 │ │93 │  │      │
        └────────────┘ └───┘ └───┘ └───┘ └───┘  └──────┘
                   │    │    │    │    │
        ┌──────────▼────▼────▼────▼────▼──────────────┐
        │     Nacos（服务注册 + 配置中心）               │
        │     MySQL 8  ·  Redis 7  ·  Kafka            │
        │     MinIO  ·  Sentinel                       │
        └───────────────────────────────────────────────┘
```

---

## 🛠️ 技术栈

| 层级 | 技术选型 |
|------|---------|
| **后端框架** | Spring Boot `4.0.3` + Spring Cloud `2025.1.0` + Spring Cloud Alibaba `2025.1.0.0` |
| **数据库 ORM** | 原生 MyBatis + PageHelper（无 Lombok，手写 getter/setter） |
| **安全认证** | Spring Security + JWT（jjwt `0.12.7`） |
| **服务注册** | Nacos v2.x |
| **缓存** | Redis 7 + Spring Cache |
| **消息队列** | Apache Kafka |
| **对象存储** | MinIO |
| **定时调度** | Quartz |
| **前端** | Vue 3 + TypeScript + Element Plus + Vite |
| **UI 组件库** | [EleAdmin Pro](https://eleadmin.com) |
| **构建工具** | Maven 3.9+（多模块扁平化架构） |
| **容器化** | Docker + Docker Compose |

---

## 📦 模块结构

```
lest-platform/
├── backend/                    # Java 后端（Maven 多模块）
│   ├── lest-common/            # 公共库（core/security/log/redis/...）
│   ├── lest-auth/              # 认证服务                 [8096]
│   ├── lest-gateway/           # API 网关                 [8080]
│   ├── lest-api/               # Feign 客户端接口
│   └── lest-modules/
│       ├── lest-system/        # 系统管理                 [8081]
│       ├── lest-project/       # 项目管理                 [8082]
│       ├── lest-task/          # 任务管理                 [8083]
│       ├── lest-release/       # 发布管理                 [8087]
│       ├── lest-job/           # 定时任务                 [8093]
│       └── lest-file/          # 文件服务                 [8091]
├── frontend-pc/                # 管理端 Web（Vue 3 + TS）
├── frontend-h5/                # 移动端 H5
├── frontend-app/               # 原生移动 App
└── docs/                       # 架构、API、PRD、任务文档
```

---

## 🚀 快速启动

### 前提条件

| 依赖 | 最低版本 |
|------|---------|
| JDK | 21 |
| Maven | 3.9 |
| Docker | 24.x |
| Docker Compose | 2.x |
| Node.js | 18+ |

### 方式一 — Docker Compose（推荐）

```bash
# 克隆仓库
git clone https://github.com/yshan2028/Lest.git
cd lest-platform

# 启动所有基础设施 + 服务
docker compose -f docker-compose.dev.yml up -d

# 启动前端开发服务器（热更新）
cd frontend-pc
npm install
npm run dev
```

### 方式二 — 本地开发

```bash
# 1. 启动基础设施（MySQL、Redis、Nacos）
docker compose -f docker-compose.dev.yml up mysql redis nacos -d

# 2. 构建后端
cd backend
mvn clean install -DskipTests

# 3. 分别启动各服务（各开一个终端）
./bin/run-auth.sh
./bin/run-gateway.sh
./bin/run-system.sh

# 4. 启动前端
cd ../frontend-pc
npm run dev
```

---

## 🌐 访问地址

| 服务 | 地址 | 账号密码 |
|------|------|---------|
| **前端** | <http://localhost:5173> | admin / admin123 |
| **API 网关** | <http://localhost:8080> | — |
| **Swagger 文档** | <http://localhost:8080/doc.html> | — |
| **Nacos 控制台** | <http://localhost:8848/nacos> | nacos / nacos |
| **MinIO 控制台** | <http://localhost:9001> | minioadmin / minioadmin |

---

## 📋 版本规划

| 版本 | 主题 | 状态 |
|------|------|------|
| **v0.1.0** | 基础框架 — 认证、系统管理、网关、仪表盘 | ✅ 已发布 |
| **v0.2.0** | 项目与任务前端页面、DDL、API 补全 | ✅ 已发布 |
| **v0.3.0** | 燃尽图、任务工时/评论面板、看板拖拽 | 🔵 计划中 |
| **v0.4.0** | 发布管理 UI、Webhook 集成 | 🔵 计划中 |
| **v1.0.0** | 正式版、移动端、完整文档 | 🔵 计划中 |

完整更新记录见 [CHANGELOG.md](./CHANGELOG.md)，里程碑规划见 [docs/MILESTONES.md](./docs/MILESTONES.md)。

---

## 🤝 参与贡献

欢迎提交 Issue、功能建议和 Pull Request！

1. Fork 本仓库
2. 创建分支：`git checkout -b feat/your-feature`
3. 提交代码：`git commit -m 'feat: 添加某功能'`
4. 推送分支：`git push origin feat/your-feature`
5. 发起 Pull Request

代码规范和提交约定请参阅 [CONTRIBUTING.md](./CONTRIBUTING.md)。

---

## 🙏 致谢

本项目站在巨人的肩膀上，在此真诚感谢：

- **[RuoYi-Cloud](https://ruoyi.vip)** — LEST Platform 的后端微服务架构、安全框架、权限模型及代码生成规范深度参考了 RuoYi-Cloud。RuoYi 是中国开发者社区中最全面、最成熟的开源 Java 微服务脚手架之一，拥有庞大的用户群体和活跃的社区生态。**如果您在商业产品中使用了 RuoYi，请考虑前往 [ruoyi.vip](https://ruoyi.vip) 购买官方授权或向作者捐赠，支持优秀开源项目的持续发展。**

- **[EleAdmin Pro](https://eleadmin.com)** — LEST Platform 的前端管理界面基于 EleAdmin Pro 构建。EleAdmin Pro 是一款基于 Vue 3 + Element Plus 的高质量管理后台组件库，设计精良、体验出色、功能丰富。**如果您的项目中也采用了 EleAdmin Pro，我们强烈建议前往 [eleadmin.com](https://eleadmin.com) 购买官方商业授权，这是对作者原创设计和持续维护工作的认可与支持。**

- [Spring Boot](https://spring.io/projects/spring-boot) / [Spring Cloud Alibaba](https://github.com/alibaba/spring-cloud-alibaba) / [Vue 3](https://vuejs.org) / [Element Plus](https://element-plus.org) — 以及所有让本项目成为可能的开源依赖。

---

## 📄 许可证

本项目基于 **MIT 许可证** 开源，详见 [LICENSE](./LICENSE) 文件。

> MIT 许可证仅适用于 LEST Platform 本身的源代码。请遵守各第三方依赖的独立许可协议，特别是上述致谢中涉及商业授权的组件。
