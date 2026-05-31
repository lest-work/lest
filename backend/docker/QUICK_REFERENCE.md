# Docker 部署快速参考

## 三步启动

### 步骤 1：初始化数据库（仅首次）

```bash
mysql -u root -p lest_platform < backend/docker/mysql/db/01_lest_platform_init.sql
```

### 步骤 2：选择部署方式

**方式 A：完整部署（首次或代码改变）**
```bash
cd backend/docker
bash build-and-deploy.sh
```
耗时：10-15 分钟

**方式 B：快速启动（JAR 已存在）**
```bash
cd backend/docker
bash quick-start.sh
```
耗时：3-5 分钟

**方式 C：直接启动（镜像已构建）**
```bash
cd backend/docker
bash start.sh
```
耗时：1-2 分钟

### 步骤 3：验证服务

```bash
cd backend/docker
bash health-check.sh
```

## 常用命令速查

| 命令 | 说明 |
|------|------|
| `bash start.sh` | 启动所有容器 |
| `bash stop.sh` | 停止所有容器 |
| `bash health-check.sh` | 健康检查 |
| `docker-compose -f docker-compose.yml ps` | 查看容器状态 |
| `docker-compose -f docker-compose.yml logs -f lest-gateway` | 查看网关日志 |
| `docker-compose -f docker-compose.yml restart lest-gateway` | 重启网关 |
| `docker-compose -f docker-compose.yml down` | 停止并删除容器 |

## 服务地址速查

| 服务 | 地址 |
|------|------|
| 网关 | http://localhost:8080 |
| Nacos | http://localhost:18848 |
| Sentinel | http://localhost:8858 |
| MinIO | http://localhost:9001 |

## 前置条件检查

```bash
# MySQL
mysql -u root -p -e "SELECT 1"

# Redis
redis-cli ping

# Kafka
nc -zv localhost 9092

# Docker
docker --version
docker-compose --version
```

## 故障排查速查

| 问题 | 解决方案 |
|------|---------|
| 容器无法启动 | `docker-compose logs lest-gateway` |
| 无法连接 MySQL | `docker exec lest-gateway nc -zv host.docker.internal 3306` |
| Nacos 无法注册 | `curl http://localhost:8848/nacos/` |
| 前端无法访问 API | `curl http://localhost:8080/doc.html` |
| 重建镜像 | `bash build-and-deploy.sh` |

## 详细文档

- **README.md** - 完整部署指南
- **DEPLOYMENT_GUIDE.md** - 中文部署指南
- **docker-compose.yml** - Docker Compose 配置
- **01_lest_platform_init.sql** - 数据库初始化脚本
