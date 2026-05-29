# LEST Platform

<p align="center">
  <img src="https://img.shields.io/badge/version-1.0.0-brightgreen.svg" alt="version">
  <img src="https://img.shields.io/badge/license-MIT-blue.svg" alt="license">
  <img src="https://img.shields.io/badge/JDK-25-blue.svg" alt="jdk">
</p>

基于 Spring Boot 4.x / Spring Cloud Alibaba / Maven 的企业级项目管理平台。

> 对标 RuoYi-Cloud 目录结构，采用 Maven 多模块扁平化架构。

## 目录结构

```
lest-platform/
├── bin/                          # 启动脚本
│   ├── package.sh                 # Maven 全量构建
│   ├── clean.sh                   # 清理构建产物
│   ├── start-all.sh               # 启动核心服务
│   ├── start-gateway.sh           # 启动网关
│   ├── start-auth.sh              # 启动认证服务
│   ├── start-system.sh            # 启动系统服务
│   └── stop.sh                    # 停止所有服务
├── docker/                        # Docker 部署
│   ├── docker-compose.yml          # 完整部署编排
│   ├── deploy.sh                   # 一键部署脚本
│   ├── copy.sh                    # 构建产物复制脚本
│   ├── mysql/                     # MySQL 配置
│   ├── nacos/                     # Nacos 配置
│   ├── redis/                     # Redis 配置
│   ├── nginx/                     # Nginx 配置
│   └── lest/                     # 微服务镜像目录
│       ├── gateway/
│       ├── auth/
│       └── modules/ (system, project, task, ...)
├── sql/                          # 数据库脚本
├── docs/                         # 文档
├── backend/                       # 后端源码
│   ├── pom.xml                    # 父 POM
│   ├── lest-common/             # 公共模块（8个子模块）
│   │   ├── lest-common-core/    # 核心工具（Result/Exception/Utils）
│   │   ├── lest-common-security/ # 安全认证（JWT/权限）
│   │   ├── lest-common-log/     # 日志记录（LogAspect）
│   │   ├── lest-common-redis/   # Redis 缓存
│   │   ├── lest-common-swagger/ # Swagger 文档
│   │   ├── lest-common-datascope/ # 数据权限
│   │   ├── lest-common-datasource/ # 多数据源
│   │   └── lest-common-sensitive/  # 数据脱敏
│   ├── lest-auth-entity/        # 认证实体层（共享实体/Mapper）
│   ├── lest-api/                 # Feign 接口层
│   │   └── lest-api-system/     # 系统模块 Feign 客户端
│   ├── lest-gateway/             # API 网关 [8080]
│   ├── lest-auth/                # 认证服务 [8096]
│   ├── lest-modules/             # 业务微服务
│   │   ├── lest-system/         # 系统服务 [8081]
│   │   ├── lest-project/        # 项目管理 [8082]
│   │   ├── lest-task/           # 任务管理 [8083]
│   │   ├── lest-release/        # 发布管理 [8087]
│   │   ├── lest-job/            # 定时任务 [8093]
│   │   ├── lest-file/          # 文件服务 [8091]
│   │   ├── lest-ai/             # AI 服务 [8090]
│   │   ├── lest-code/           # 代码管理 [8084]
│   │   ├── lest-meeting/        # 会议服务 [8085]
│   │   ├── lest-notification/   # 通知服务 [8086]
│   │   ├── lest-performance/    # 性能监控 [8088]
│   │   ├── lest-plugin/         # 插件服务 [8092]
│   │   ├── lest-open/           # 开放平台 [8094]
│   │   └── lest-wakapi/         # 时间追踪 [8089]
│   └── lest-visual/             # 可视化模块
│       └── lest-monitor/        # 监控中心 [9090]
├── frontend-pc/                  # PC 端管理后台
├── frontend-app/                 # 移动端
├── frontend-h5/                  # H5 端
└── Dockerfile                     # 多阶段 Docker 构建
```

## 技术栈

| 分类 | 技术 |
|------|------|
| 后端框架 | Spring Boot 3.4.5 / Spring Cloud 2025.0 / Spring Cloud Alibaba |
| 构建工具 | Maven 3.9+ (JDK 25) |
| 数据库 | MySQL 9.x + MyBatis-Plus 4.0 |
| 缓存 | Redis 8.0 + Redisson |
| 注册中心 | Nacos v3.1.0 |
| 流量控制 | Sentinel 1.9.0 |
| 消息队列 | Apache Kafka |
| 对象存储 | MinIO |
| 前端 | Vue 3 + Element Plus + Vite |

## 内置功能

1. **用户管理** - 系统用户配置与状态管理
2. **角色管理** - 角色权限分配与数据范围控制
3. **菜单管理** - 前端路由与按钮权限配置
4. **机构管理** - 组织架构树形管理
5. **字典管理** - 系统通用字典数据维护
6. **参数管理** - 动态系统参数配置
7. **岗位管理** - 用户所属职务配置
8. **通知公告** - 系统通知公告发布与查看
9. **登录日志** - 登录成功/失败记录与查询
10. **操作日志** - 业务操作审计与追踪
11. **项目管理** - 项目全生命周期管理（敏捷/看板/瀑布流）
12. **迭代管理** - 敏捷迭代规划与追踪
13. **任务管理** - 任务创建、分配、追踪、工时记录
14. **发布管理** - 发布计划、制品、变更管理
15. **定时任务** - 定时任务调度与执行日志
16. **文件管理** - 文件上传、存储、下载

## 环境要求

- JDK 25+
- Maven 3.9+
- Docker & Docker Compose
- MySQL 9.x
- Redis 8.0+

## 快速开始

### 方式一：Maven 构建

```bash
# 全量构建（跳过测试）
cd backend
mvn clean install -DskipTests

# 启动核心服务
cd ..
./bin/start-all.sh
```

### 方式二：Docker 部署

```bash
# 1. 构建后端
cd backend
mvn clean package -DskipTests

# 2. 复制产物到 Docker 目录
cd ..
./docker/copy.sh

# 3. 启动基础设施
cd docker
./deploy.sh base

# 4. 启动微服务
./deploy.sh modules
```

### 方式三：完整 Docker Compose

```bash
docker compose -f docker-compose.yml up -d
```

## 访问地址

| 服务 | 地址 | 说明 |
|------|------|------|
| 前端 | http://localhost:5173 | Vite 开发服务器 |
| 网关 API | http://localhost:8080 | 后端统一入口 |
| Swagger | http://localhost:8080/doc.html | API 文档 |
| Nacos | http://localhost:8848/nacos | 注册中心（nacos/nacos） |
| Sentinel | http://localhost:9090 | 流量控制台 |
| MinIO | http://localhost:9001 | 对象存储控制台 |

## 默认账号

- 用户名: `admin`
- 密码: `Lest@123456`

## Maven 模块说明

```
lest-platform (pom.xml - 父项目)
├── lest-common (pom.xml - 公共模块父)
│   ├── lest-common-core        # 核心工具层
│   ├── lest-common-log         # 日志
│   ├── lest-common-redis       # 缓存
│   ├── lest-common-security    # 安全
│   ├── lest-common-swagger     # 文档
│   ├── lest-common-datascope   # 数据权限
│   ├── lest-common-datasource  # 多数据源
│   └── lest-common-sensitive   # 数据脱敏
├── lest-auth-entity           # 认证实体（共享）
├── lest-api (pom.xml - API 父)
│   └── lest-api-system         # Feign 客户端
├── lest-gateway               # 网关（独立）
├── lest-auth                  # 认证服务（独立）
├── lest-modules (pom.xml - 业务模块父)
│   └── lest-xxx (14个)        # 业务微服务
└── lest-visual (pom.xml - 可视化父)
    └── lest-monitor           # 监控中心
```

## 相关文档

- [RuoYi-Cloud 差异报告](docs/RUOYI_DIFF_STATUS.md)

## 许可证

MIT
