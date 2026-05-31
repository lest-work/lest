# Lest Platform Docker 部署指南

## 快速概览

本指南提供了三种部署方式，按推荐度排序：

| 方式 | 脚本 | 耗时 | 说明 |
|------|------|------|------|
| **完整部署** | `build-and-deploy.sh` | 10-15 分钟 | 编译 → 打包 → 构建镜像 → 启动容器（推荐首次使用） |
| **快速启动** | `quick-start.sh` | 3-5 分钟 | 直接构建镜像 → 启动容器（JAR 文件已存在） |
| **手动启动** | `start.sh` | 1-2 分钟 | 直接启动容器（镜像已构建） |

## 前置条件

### 1. 本地服务

确保以下服务已在本地运行：

```bash
# MySQL 8.0+
mysql --version
mysql -u root -p -e "SELECT 1"

# Redis 6.0+
redis-cli ping

# Kafka 3.0+（可选）
# 检查 localhost:9092 是否可连接

# MongoDB 5.0+（可选）
# 检查 localhost:27017 是否可连接
```

### 2. Docker 环境

```bash
docker --version      # Docker 20.10+
docker-compose --version  # Docker Compose 2.0+
```

### 3. 数据库初始化

```bash
# 使用 MySQL 客户端导入初始化脚本
mysql -u root -p lest_platform < backend/docker/mysql/db/01_lest_platform_init.sql

# 或使用 Docker（如果 MySQL 在容器中）
docker exec -i mysql mysql -u root -proot lest_platform < backend/docker/mysql/db/01_lest_platform_init.sql
```

## 部署方式

### 方式 1：完整部署（首次使用）

**适用场景**：首次部署或需要重新编译代码

```bash
cd backend/docker

# 执行完整部署脚本
bash build-and-deploy.sh
```

**脚本会自动执行以下步骤**：

1. ✓ 检查 Maven、Docker、Docker Compose
2. ✓ 编译 Java 代码（`mvn clean compile`）
3. ✓ 打包 JAR 文件（`mvn clean package`）
4. ✓ 复制 JAR 到 Docker 目录
5. ✓ 构建 Docker 镜像（`docker-compose build`）
6. ✓ 启动 Docker 容器（`docker-compose up -d`）
7. ✓ 等待服务就绪
8. ✓ 显示服务状态

**预期输出**：

```
==========================================
Lest Platform 完整构建和部署
==========================================

✓ Maven 已安装
✓ Docker 已安装
✓ Docker Compose 已安装

[编译过程...]
✓ 编译成功

[打包过程...]
✓ 打包成功

✓ 复制 lest-gateway.jar
✓ 复制 lest-auth.jar
...

[Docker 构建过程...]
✓ Docker 镜像构建成功

[Docker 启动过程...]
✓ Docker 容器启动成功

✓ Nacos 已就绪
✓ Gateway 已就绪

CONTAINER ID   IMAGE                    STATUS
abc123...      docker-lest-gateway      Up 2 minutes
def456...      docker-lest-auth         Up 2 minutes
...

服务访问地址：
  - Gateway:     http://localhost:8080
  - Nacos:       http://localhost:18848
  - Sentinel:    http://localhost:8858
  - MinIO:       http://localhost:9001

==========================================
构建和部署完成！
==========================================
```

### 方式 2：快速启动（JAR 文件已存在）

**适用场景**：代码未改变，只需重新构建镜像或重启容器

```bash
cd backend/docker

# 执行快速启动脚本
bash quick-start.sh
```

**脚本会自动执行以下步骤**：

1. ✓ 检查 JAR 文件是否存在
2. ✓ 构建 Docker 镜像
3. ✓ 启动 Docker 容器
4. ✓ 等待服务就绪
5. ✓ 显示服务状态

### 方式 3：手动启动（镜像已构建）

**适用场景**：容器已停止，只需重新启动

```bash
cd backend/docker

# 方式 3a：使用启动脚本
bash start.sh

# 方式 3b：使用 docker-compose 直接启动
docker-compose -f docker-compose.yml up -d
```

## 常用命令

### 查看容器状态

```bash
cd backend/docker

# 查看所有容器
docker-compose -f docker-compose.yml ps

# 查看特定容器日志
docker-compose -f docker-compose.yml logs -f lest-gateway

# 查看最后 100 行日志
docker-compose -f docker-compose.yml logs --tail=100 lest-gateway
```

### 停止和重启

```bash
cd backend/docker

# 停止所有容器
bash stop.sh

# 或使用 docker-compose
docker-compose -f docker-compose.yml down

# 重启特定服务
docker-compose -f docker-compose.yml restart lest-gateway

# 重启所有服务
docker-compose -f docker-compose.yml restart
```

### 健康检查

```bash
cd backend/docker

# 运行健康检查脚本
bash health-check.sh
```

### 清理资源

```bash
cd backend/docker

# 停止并删除容器
docker-compose -f docker-compose.yml down

# 删除所有容器和卷
docker-compose -f docker-compose.yml down -v

# 删除未使用的镜像
docker image prune -a

# 删除所有 Docker 缓存
docker system prune -a
```

## 服务访问地址

| 服务 | 地址 | 用途 |
|------|------|------|
| Gateway | http://localhost:8080 | API 网关（前端访问入口） |
| Nacos | http://localhost:18848 | 服务注册/配置中心 |
| Sentinel | http://localhost:8858 | 流量控制面板 |
| MinIO | http://localhost:9001 | 对象存储（文件上传） |
| Auth | http://localhost:8096 | 认证服务 API |
| System | http://localhost:8081 | 系统服务 API |
| Project | http://localhost:8082 | 项目管理 API |
| Task | http://localhost:8083 | 任务管理 API |

## 故障排查

### 问题 1：容器无法启动

**症状**：`docker-compose up -d` 后容器立即退出

**解决方案**：

```bash
# 查看容器日志
docker-compose -f docker-compose.yml logs lest-gateway

# 常见原因：
# 1. JAR 文件不存在 → 运行 build-and-deploy.sh
# 2. 端口被占用 → 检查 lsof -i :8080
# 3. 内存不足 → 增加 Docker 内存配置
```

### 问题 2：无法连接本地 MySQL

**症状**：容器日志显示 `Connection refused` 或 `Cannot connect to MySQL`

**解决方案**：

```bash
# 1. 检查 MySQL 是否运行
mysql -u root -p -e "SELECT 1"

# 2. 检查容器网络配置
docker inspect lest-gateway | grep -A 10 "NetworkSettings"

# 3. 测试容器内的连接
docker exec lest-gateway nc -zv host.docker.internal 3306

# 4. 检查 docker-compose.yml 中的 extra_hosts 配置
# 应该包含：
#   extra_hosts:
#     - "host.docker.internal:host-gateway"
```

### 问题 3：Nacos 无法注册服务

**症状**：Nacos 控制台看不到服务，或服务无法相互调用

**解决方案**：

```bash
# 1. 检查 Nacos 是否运行
curl http://localhost:8848/nacos/

# 2. 查看 Nacos 日志
docker-compose -f docker-compose.yml logs lest-nacos

# 3. 查看已注册的服务
curl http://localhost:8848/nacos/v1/ns/service/list

# 4. 检查服务配置
# 确保 application.yml 中有：
#   spring:
#     cloud:
#       nacos:
#         discovery:
#           server-addr: 127.0.0.1:8848
#           enabled: true
```

### 问题 4：前端无法访问后端 API

**症状**：浏览器控制台显示 CORS 错误或 404

**解决方案**：

```bash
# 1. 检查 Gateway 是否运行
curl http://localhost:8080/doc.html

# 2. 检查 Gateway 日志
docker-compose -f docker-compose.yml logs lest-gateway

# 3. 检查路由配置
# 确保 gateway/application.yml 中有正确的路由配置

# 4. 测试 API 调用
curl http://localhost:8080/api/auth/login
```

### 问题 5：重建镜像

**症状**：修改了代码或配置，需要重新构建镜像

**解决方案**：

```bash
cd backend/docker

# 方式 1：完整重建（推荐）
bash build-and-deploy.sh

# 方式 2：仅重建镜像
docker-compose -f docker-compose.yml build --no-cache

# 方式 3：删除旧镜像并重建
docker-compose -f docker-compose.yml down
docker image prune -a
docker-compose -f docker-compose.yml build
docker-compose -f docker-compose.yml up -d
```

## 性能优化

### 1. 增加 JVM 内存

编辑 `docker-compose.yml`，修改环境变量：

```yaml
environment:
  - JAVA_OPTS=-Xms512m -Xmx1024m
```

### 2. 增加 Nacos 内存

```yaml
lest-nacos:
  environment:
    - JVM_XMS=1024m
    - JVM_XMX=1024m
```

### 3. 调整日志级别

编辑各服务的 `application.yml`：

```yaml
logging:
  level:
    root: WARN
    com.lest: INFO
```

## 生产部署建议

1. **使用 bridge 网络**：将 MySQL、Redis 等也部署在 Docker
2. **使用容器编排**：Docker Swarm 或 Kubernetes
3. **配置反向代理**：Nginx 或 Traefik
4. **持久化存储**：Docker volumes 或外部存储
5. **监控告警**：Prometheus + Grafana
6. **日志聚合**：ELK Stack 或 Loki
7. **备份策略**：定期备份数据库和文件

## 相关文档

- [README.md](./README.md) - Docker 部署详细指南
- [docker-compose.yml](./docker-compose.yml) - Docker Compose 配置
- [../mysql/db/01_lest_platform_init.sql](../mysql/db/01_lest_platform_init.sql) - 数据库初始化脚本

## 支持

如有问题，请参考：

1. 查看容器日志：`docker-compose logs -f <service-name>`
2. 检查 Nacos 配置：http://localhost:18848
3. 查看 Sentinel 监控：http://localhost:8858
4. 检查 MinIO 存储：http://localhost:9001
