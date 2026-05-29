---
description: LEST Platform 项目概览、技术栈和目录结构
---

# LEST Platform 项目规范 - 概览

## 1. 项目定位

LEST Platform 是一个**团队研发协作平台**，基于 Spring Cloud 微服务架构 + Vue 3 前端，类似 JIRA + GitLab + Confluence 的集成平台。

## 2. 技术栈

### 后端（Java）
- **Spring Boot 4.0.3** + **Spring Cloud 2025.1.0**
- **Spring Cloud Alibaba 2025.1.0.0**（Nacos 注册中心）
- **Spring Cloud Gateway**（网关，端口 8080）
- **MyBatis + PageHelper**（ORM，原生 MyBatis，NOT MyBatis-Plus）
- **MySQL 8.x**（主数据库）
- **Redis**（缓存、Session、在线用户）
- **Java 17+**，NO Lombok（手写 getter/setter）

### 前端（frontend-pc）
- **Vue 3 + TypeScript**
- **Element Plus**（UI 组件库）
- **EleAdminPlus**（管理后台框架）
- **Pinia**（状态管理）
- **Vite**（构建工具）

## 3. 微服务目录结构

```
backend/
├── lest-gateway/          # API 网关，端口 8080
├── lest-auth/             # 认证服务（登录/JWT）
├── lest-common/           # 公共模块
│   ├── lest-common-core/  # 核心工具（BaseController, AjaxResult, PageHelper）
│   ├── lest-common-security/
│   ├── lest-common-redis/
│   └── lest-common-log/
├── lest-api/
│   └── lest-api-system/   # 系统 API 模型（SysUser, SysOperLog, LoginUser）
└── lest-modules/
    ├── lest-system/        # 系统管理，端口 8081
    ├── lest-project/       # 项目管理，端口 8082
    ├── lest-task/          # 任务管理，端口 8083
    ├── lest-release/       # 发布管理
    ├── lest-meeting/       # 会议管理
    ├── lest-notification/  # 通知服务
    ├── lest-file/          # 文件服务
    └── lest-job/           # 定时任务
```

## 4. 前端目录结构

```
frontend-pc/src/
├── api/
│   ├── index.ts              # 通用类型：AjaxResult, TableDataInfo, PageParam
│   ├── system/               # 系统管理 API
│   │   └── user/
│   │       ├── index.ts      # API 函数
│   │       └── model/
│   │           └── index.ts  # TypeScript 类型定义
│   ├── project/              # 项目 API
│   ├── task/                 # 任务 API
│   └── dashboard/            # 仪表盘 API
├── views/                    # 页面组件
├── store/                    # Pinia 状态
├── router/                   # 路由
└── utils/
    └── request.ts            # Axios 封装
```

## 5. 网关路由规则（关键）

网关统一使用 **StripPrefix=1**，即剥离第一个路径段：

| 前端请求路径 | 网关路由 | 后端服务收到 |
|---|---|---|
| `/api/system/user/list` | lest-system | `/user/list` |
| `/api/project/list` | lest-project | `/list` |
| `/api/task/list` | lest-task | `/list` |
| `/api/monitor/operlog/list` | lest-system | `/operlog/list` |

**因此后端 Controller 的 `@RequestMapping` 不能包含路由前缀段：**
- lest-system: `@RequestMapping("/user")` ✅（前端调用 `/api/system/user/...`）
- lest-project: `@RequestMapping("")` ✅（前端调用 `/api/project/...`，strip 后 lest-project 收到 `/...`）
- lest-task: `@RequestMapping("")` ✅（前端调用 `/api/task/...`）
