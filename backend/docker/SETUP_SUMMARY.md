# Docker 和 SQL 配置总结

## ✅ 已完成的工作

### 1. 启动脚本（5 个）

| 脚本 | 功能 | 耗时 |
|------|------|------|
| `build-and-deploy.sh` | 完整构建：编译 → 打包 → 构建镜像 → 启动 | 10-15 分钟 |
| `quick-start.sh` | 快速启动：构建镜像 → 启动（JAR 已存在） | 3-5 分钟 |
| `start.sh` | 简单启动：直接启动容器（镜像已构建） | 1-2 分钟 |
| `stop.sh` | 停止所有容器 | <1 分钟 |
| `health-check.sh` | 健康检查：验证所有服务状态 | <1 分钟 |

### 2. 文档（4 个）

| 文档 | 内容 |
|------|------|
| `README.md` | 完整部署指南（英文） |
| `DEPLOYMENT_GUIDE.md` | 详细部署指南（中文） |
| `QUICK_REFERENCE.md` | 快速参考卡片 |
| `SETUP_SUMMARY.md` | 本文件 |

### 3. 配置优化

- ✅ 添加 `version: '3.8'` 到 docker-compose.yml
- ✅ 为 Nacos 添加 healthcheck
- ✅ 所有脚本添加执行权限
- ✅ 创建 JAR 目录结构

## 🚀 快速开始

### 前置条件

确保以下服务已在本地运行：

```bash
# 检查 MySQL
mysql -u root -p -e "SELECT 1"

# 检查 Redis
redis-cli ping

# 检查 Kafka（可选）
nc -zv localhost 9092
```

### 初始化数据库（仅首次）

```bash
mysql -u root -p lest_platform < backend/docker/mysql/db/01_lest_platform_init.sql
```

### 部署方式选择

**选项 1：完整部署（首次或代码改变）**
```bash
cd backend/docker
bash build-and-deploy.sh
```

**选项 2：快速启动（JAR 已存在）**
```bash
cd backend/docker
bash quick-start.sh
```

**选项 3：直接启动（镜像已构建）**
```bash
cd backend/docker
bash start.sh
```

### 验证部署

```bash
cd backend/docker
bash health-check.sh
```

## 📋 架构说明

### 网络模式

- **host 模式**：容器直接使用宿主机网络
- **访问本地服务**：通过 `host.docker.internal` 访问 MySQL、Redis 等

### 服务分布

| 类型 | 服务 | 位置 |
|------|------|------|
| 基础设施 | MySQL、Redis、Kafka、MongoDB | 本地 |
| 中间件 | Nacos、Sentinel、MinIO | Docker |
| 应用 | Gateway、Auth、System、Project、Task 等 | Docker |

## 🌐 服务访问地址

| 服务 | 地址 | 用途 |
|------|------|------|
| Gateway | http://localhost:8080 | API 网关 |
| Nacos | http://localhost:18848 | 服务注册中心 |
| Sentinel | http://localhost:8858 | 流量控制 |
| MinIO | http://localhost:9001 | 文件存储 |
| Auth | http://localhost:8096 | 认证服务 |
| System | http://localhost:8081 | 系统服务 |
| Project | http://localhost:8082 | 项目管理 |
| Task | http://localhost:8083 | 任务管理 |

## 📚 文档导航

1. **快速参考** → `QUICK_REFERENCE.md`
   - 常用命令、服务地址、故障排查

2. **详细指南** → `DEPLOYMENT_GUIDE.md`
   - 三种部署方式详解
   - 常用命令详解
   - 故障排查详解
   - 性能优化建议

3. **完整指南** → `README.md`
   - 前置条件详解
   - 快速开始步骤
   - 常用命令
   - 故障排查
   - 生产部署建议

## ⚙️ 常用命令

```bash
cd backend/docker

# 启动
bash start.sh

# 停止
bash stop.sh

# 查看状态
docker-compose -f docker-compose.yml ps

# 查看日志
docker-compose -f docker-compose.yml logs -f lest-gateway

# 重启服务
docker-compose -f docker-compose.yml restart lest-gateway

# 健康检查
bash health-check.sh
```

## 🔧 故障排查

### 问题 1：容器无法启动

```bash
# 查看日志
docker-compose -f docker-compose.yml logs lest-gateway

# 检查端口占用
lsof -i :8080
```

### 问题 2：无法连接 MySQL

```bash
# 检查 MySQL 是否运行
mysql -u root -p -e "SELECT 1"

# 测试容器内连接
docker exec lest-gateway nc -zv host.docker.internal 3306
```

### 问题 3：Nacos 无法注册服务

```bash
# 检查 Nacos 状态
curl http://localhost:8848/nacos/

# 查看已注册服务
curl http://localhost:8848/nacos/v1/ns/service/list
```

## 📝 脚本说明

### build-and-deploy.sh

自动执行以下步骤：

1. 检查 Maven、Docker、Docker Compose
2. 编译 Java 代码（`mvn clean compile`）
3. 打包 JAR 文件（`mvn clean package`）
4. 复制 JAR 到 Docker 目录
5. 构建 Docker 镜像（`docker-compose build`）
6. 启动 Docker 容器（`docker-compose up -d`）
7. 等待服务就绪
8. 显示服务状态

### quick-start.sh

自动执行以下步骤：

1. 检查 JAR 文件是否存在
2. 构建 Docker 镜像
3. 启动 Docker 容器
4. 等待服务就绪
5. 显示服务状态

### start.sh

自动执行以下步骤：

1. 检查本地服务（MySQL、Redis、Kafka）
2. 启动 Docker 容器
3. 等待服务就绪
4. 显示服务状态

### health-check.sh

自动执行以下检查：

1. 检查本地服务状态
2. 检查 Docker 容器状态
3. 检查基础设施服务（Nacos、Sentinel、MinIO）
4. 检查应用服务健康状态

## 🎯 下一步

1. **运行完整部署**
   ```bash
   cd backend/docker
   bash build-and-deploy.sh
   ```

2. **等待部署完成**（10-15 分钟）

3. **验证服务**
   ```bash
   bash health-check.sh
   ```

4. **访问前端**
   - 打开浏览器访问 http://localhost:8080
   - 使用默认账号登录

## 📞 支持

如有问题，请参考：

1. 查看脚本输出日志
2. 查看容器日志：`docker-compose logs -f <service-name>`
3. 查看 Nacos 配置：http://localhost:18848
4. 查看 Sentinel 监控：http://localhost:8858

---

**最后更新**：2026-05-30 17:51
**配置方式**：参考若依 RuoYi-Cloud 3.6.8
**网络模式**：host（本地服务 + Docker 服务混合部署）
