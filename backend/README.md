<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">Lest Platform</h1>
<h4 align="center">基于 Spring Boot / Spring Cloud & Alibaba 的研发项目管理微服务平台</h4>

## 平台简介

Lest Platform 是一套研发项目管理平台，基于 RuoYi-Cloud 微服务架构开发。

* 后端采用 Spring Boot 4.x、Spring Cloud 2025、Spring Cloud Alibaba 2025。
* 注册中心、配置中心选型 Nacos，权限认证使用 Redis。
* 流量控制框架选型 Sentinel。

## 系统模块

~~~
lest-platform
├── lest-gateway                          // 网关模块
├── lest-auth                             // 认证中心
├── lest-api                              // 接口模块
│   └── lest-api-system                   // 系统接口
├── lest-common                           // 通用模块
│   ├── lest-common-core                  // 核心模块
│   ├── lest-common-datasource            // 多数据源
│   ├── lest-common-log                   // 日志记录
│   ├── lest-common-redis                 // 缓存服务
│   ├── lest-common-security              // 安全模块
│   └── lest-common-swagger               // 系统接口
├── lest-modules                          // 业务模块
│   ├── lest-system                       // 系统模块  [9201]
│   ├── lest-job                          // 定时任务  [9203]
│   ├── lest-file                         // 文件服务  [9300]
│   ├── lest-project                      // 项目管理  [9400]
│   ├── lest-task                         // 任务管理  [9401]
│   └── lest-release                      // 发布管理  [9402]
├── lest-visual                           // 图形化管理
│   └── lest-monitor                      // 监控中心  [9100]
├── docker                                // docker 配置
├── bin                                   // 脚本目录
└── sql                                   // SQL 脚本
~~~

## 架构图

<img src="https://oscimg.oschina.net/oscnet/up-82e9722ecb846571c62a5530c085802d303.png"/>

## 内置功能

1. 用户管理：用户是系统操作者。
2. 菜单管理：配置系统菜单，操作权限，按钮权限标识等。
3. 角色管理：角色菜单权限分配。
4. 组织管理：配置系统组织机构（公司、部门、小组）。
5. 字典管理：对系统中经常使用的一些较为固定的数据进行维护。
6. 项目管理：管理研发项目、迭代、里程碑。
7. 任务管理：管理项目任务、看板、甘特图。
8. 发布管理：管理发布计划、制品、关联问题。
9. 定时任务：在线（添加、修改、删除）任务调度包含执行结果日志。
10. 服务监控：监视当前系统CPU、内存、磁盘、堆栈等相关信息。

## 在线体验

演示地址：待部署

## 演示图

待补充

## 使用说明

1. 安装 JDK 17+、Maven 3.9+、MySQL 8.0+、Redis 7+、Nacos 3.x。
2. 创建数据库 `lest_platform`，运行 `sql/` 目录下的 SQL 脚本。
3. 修改各模块 `application.yml` 中的数据库、Redis、Nacos 连接信息。
4. 执行 `bin/package.sh` 打包或直接在 IDE 中运行各模块 Application 类。
