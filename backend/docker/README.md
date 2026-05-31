# Lest Platform Docker 部署指南

## 概述

本指南用于在 Docker 中部署 Lest Platform 的所有微服务。

**架构特点**：
- **本地服务**：MySQL、Redis、Kafka、MongoDB 运行在本地（非 Docker）
- **Docker 服务**：Nacos、Sentinel、MinIO、所有微服务运行在 Docker
- **网络模式**：使用 `host` 模式，容器可直接访问本地服务

## 前置条件

### 1. 本地服务要求

确保以下服务已在本地运行：

```bash
# MySQL 8.0+
mysql --version
# 默认连接：localhost:3306, root/root

# Redis 6.0+
redis-cli ping
# 默认连接：localhost:6379

# Kafka 3.0+
# 默认连接：localhost:9092

# MongoDB 5.0+（可选）
# 默认连接：localhost:27017
```

### 2. Docker 环境

```bash
# Docker 20.10+
docker --version

# Docker Compose 2.0+
docker-compose --version
```

### 3. 构建 JAR 包

```bash
cd /Users/liuyue/code/lest-platform

# 编译所有模块
mvn clean compile -DskipTests

# 打包所有模块
mvn clean package -DskipTests -pl \
  backend/lest-gateway,\
  backend/lest-auth,\
  backend/lest-modules/lest-system,\
  backend/lest-modules/lest-project,\
  backend/lest-modules/lest-task,\
  backend/lest-modules/lest-release,\
  backend/lest-modules/lest-file,\
  backend/lest-modules/lest-job,\
  backend/lest-modules/lest-meeting,\
  backend/lest-modules/lest-notification,\
  backend/lest-modules/lest-ai,\
  backend/lest-modules/lest-performance,\
  backend/lest-modules/lest-open,\
  backend/lest-modules/lest-plugin,\
  backend/lest-modules/lest-wakapi,\
  backend/lest-visual/lest-visual-monitor
```

## 快速开始

### 1. 初始化数据库

```bash
# 使用 MySQL 客户端导入初始化脚本
mysql -u root -p lest_platform < backend/docker/mysql/db/01_lest_platform_init.sql

# 或使用 Docker 容器内的 MySQL
docker exec -i mysql mysql -u root -proot lest_platform < backend/docker/mysql/db/01_lest_platform_init.sql
```

### 2. 启动所有容器

```bash
cd backend/docker

# 使用启动脚本（推荐）
bash start.sh

# 或使用 docker-compose 直接启动
docker-compose -f docker-compose.yml up -d
```

### 3. 验证服务状态

```bash
# 使用健康检查脚本
bash health-check.sh

# 或查看容器状态
docker-compose -f docker-compose.yml ps
```

## 服务访问地址

| 服务 | 地址 | 说明 |
|------|------|------|
| Gateway | http://localhost:8080 | API 网关 |
| Nacos | http://localhost:18848 | 注册/配置中心 |
| Sentinel | http://localhost:8858 | 流量控制面板 |
| MinIO | http://localhost:9001 | 对象存储 |
| Auth | http://localhost:8096 | 认证服务 |
| System | http://localhost:8081 | 系统服务 |
| Project | http://localhost:8082 | 项目管理 |
| Task | http://localhost:8083 | 任务管理 |
| Meeting | http://localhost:8085 | 会议服务 |
| Notification | http://localhost:8086 | 通知服务 |
| Release | http://localhost:8087 | 发布管理 |
| Performance | http://localhost:8088 | 效能分析 |
| AI | http://localhost:8090 | AI 服务 |
| File | http://localhost:8091 | 文件服务 |
| Plugin | http://localhost:8092 | 插件服务 |
| Open | http://localhost:8094 | 开放平台 |
| Job | http://localhost:9203 | 定时任务 |

## 常用命令

### 启动/停止

```bash
cd backend/docker

# 启动所有容器
bash start.sh

# 停止所有容器
bash stop.sh

# 查看容器状态
docker-compose -f docker-compose.yml ps

# 查看容器日志
docker-compose -f docker-compose.yml logs -f lest-gateway
```

### 重启服务

```bash
cd backend/docker

# 重启单个服务
docker-compose -f docker-compose.yml restart lest-gateway

# 重启所有服务
docker-compose -f docker-compose.yml restart
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
```

## 故障排查

### 1. 容器无法启动

```bash
# 查看容器日志
docker-compose -f docker-compose.yml logs lest-gateway

# 检查 Docker 资源
docker stats

# 检查端口占用
lsof -i :8080
```

### 2. 服务无法连接本地 MySQL

```bash
# 检查 MySQL 是否运行
mysql -u root -p -e "SELECT 1"

# 检查容器网络配置
docker inspect lest-gateway | grep -A 10 "NetworkSettings"

# 测试容器内的连接
docker exec lest-gateway nc -zv host.docker.internal 3306
```

### 3. Nacos 无法注册服务

```bash
# 检查 Nacos 日志
docker-compose -f docker-compose.yml logs lest-nacos

# 检查 Nacos 健康状态
curl http://localhost:8848/nacos/

# 查看已注册的服务
curl http://localhost:8848/nacos/v1/ns/service/list
```

### 4. 重建镜像

```bash
cd backend/docker

# 删除旧镜像并重新构建
docker-compose -f docker-compose.yml build --no-cache

# 重新启动
docker-compose -f docker-compose.yml up -d
```

## 配置说明

### docker-compose.yml

- **network_mode: host**：容器使用宿主机网络，可直接访问本地服务
- **extra_hosts**：添加 `host.docker.internal` 映射，用于访问本地服务
- **environment**：配置 Spring 环境变量，如 Nacos 地址、Redis 地址等

### Dockerfile

每个服务都有对应的 Dockerfile，位置：

```
backend/docker/lest/
├── gateway/dockerfile
├── auth/dockerfile
└── modules/
    ├── system/dockerfile
    ├── project/dockerfile
    ├── task/dockerfile
    └── ...
```

## 数据库初始化

### 初始化脚本位置

```
backend/docker/mysql/db/
└── 01_lest_platform_init.sql
```

### 脚本内容

- 创建 `lest_platform` 数据库
- 创建所有系统表（sys_*）
- 创建所有业务表（project_*、task_*、release_*、file_*、job_*）
- 初始化基础数据（用户、角色、菜单、字典等）

### 手动初始化

```bash
# 方式 1：使用 MySQL 客户端
mysql -u root -p < backend/docker/mysql/db/01_lest_platform_init.sql

# 方式 2：使用 Docker
docker exec -i mysql mysql -u root -proot < backend/docker/mysql/db/01_lest_platform_init.sql

# 方式 3：在 MySQL 容器内执行
docker exec -it mysql mysql -u root -p
mysql> source /docker-entrypoint-initdb.d/01_lest_platform_init.sql;
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

编辑各服务的 `application.yml`，修改日志级别：

```yaml
logging:
  level:
    root: WARN
    com.lest: INFO
```

## 生产部署指南

本节介绍如何将 LEST Platform 部署到生产服务器（使用 `lest.work` 域名）。

### 前置条件

| 依赖 | 版本 | 说明 |
|------|------|------|
| Docker | 24.x+ | 容器化 |
| Docker Compose | 2.x+ | 服务编排 |
| 服务器 | 2核4G+ | 推荐 4核8G |

### 第一步：服务器准备

```bash
# 1. 安装 Docker
curl -fsSL https://get.docker.com | sh
systemctl enable docker
systemctl start docker

# 2. 安装 Docker Compose
curl -L "https://github.com/docker/compose/releases/download/v2.24.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose

# 3. 克隆项目
git clone https://github.com/lest-work/lest.git /opt/lest-platform
cd /opt/lest-platform/backend/docker
```

### 第二步：配置域名解析

在 DNS 服务商处添加以下 A 记录（指向服务器公网 IP）：

| 主机记录 | 记录类型 | 值 |
|---------|---------|-----|
| app | A | `<服务器IP>` |
| api | A | `<服务器IP>` |
| doc | A | `<服务器IP>` |
| nacos | A | `<服务器IP>` |
| minio | A | `<服务器IP>` |

### 第三步：配置 Nginx 反向代理

```bash
# 安装 Nginx
apt install -y nginx

# 编辑配置
cat > /etc/nginx/sites-available/lest.work.conf << 'EOF'
server {
    listen 443 ssl;
    server_name app.lest.work;

    ssl_certificate /etc/nginx/ssl/lest.work.crt;
    ssl_certificate_key /etc/nginx/ssl/lest.work.key;

    client_max_body_size 100m;

    location / {
        proxy_pass http://127.0.0.1:5173;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}

server {
    listen 443 ssl;
    server_name api.lest.work;

    ssl_certificate /etc/nginx/ssl/lest.work.crt;
    ssl_certificate_key /etc/nginx/ssl/lest.work.key;

    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}

server {
    listen 443 ssl;
    server_name doc.lest.work;

    ssl_certificate /etc/nginx/ssl/lest.work.crt;
    ssl_certificate_key /etc/nginx/ssl/lest.work.key;

    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
    }
}

server {
    listen 443 ssl;
    server_name nacos.lest.work;

    ssl_certificate /etc/nginx/ssl/lest.work.crt;
    ssl_certificate_key /etc/nginx/ssl/lest.work.key;

    location / {
        proxy_pass http://127.0.0.1:8848;
        proxy_set_header Host $host;
    }
}

server {
    listen 443 ssl;
    server_name minio.lest.work;

    ssl_certificate /etc/nginx/ssl/lest.work.crt;
    ssl_certificate_key /etc/nginx/ssl/lest.work.key;

    location / {
        proxy_pass http://127.0.0.1:9001;
        proxy_set_header Host $host;
    }
}

# HTTP 重定向到 HTTPS
server {
    listen 80;
    server_name app.lest.work api.lest.work doc.lest.work nacos.lest.work minio.lest.work;
    return 301 https://$host$request_uri;
}
EOF

# 启用配置
ln -s /etc/nginx/sites-available/lest.work.conf /etc/nginx/sites-enabled/
nginx -t
systemctl reload nginx
```

### 第四步：构建并启动后端

```bash
cd /opt/lest-platform/backend

# 构建所有微服务 JAR 包
mvn clean package -DskipTests

# 构建 Docker 镜像
cd docker
docker-compose -f docker-compose.yml build --no-cache

# 启动所有服务
docker-compose -f docker-compose.yml up -d

# 查看服务状态
docker-compose -f docker-compose.yml ps
```

### 第五步：构建并启动前端

```bash
cd /opt/lest-platform/frontend-pc

# 安装依赖
npm install

# 构建生产版本
npm run build

# 使用 Nginx serve dist 目录
# 将 dist 内容复制到 /var/www/lest-work
cp -r dist/* /var/www/lest-work/
```

### 第六步：验证部署

| 检查项 | 验证命令 |
|--------|---------|
| 前端访问 | 访问 https://app.lest.work |
| API 健康检查 | `curl https://api.lest.work/actuator/health` |
| API 文档 | 访问 https://doc.lest.work/doc.html |
| Nacos 控制台 | 访问 https://nacos.lest.work/nacos |
| MinIO 控制台 | 访问 https://minio.lest.work |

默认账号：`admin` / `admin123`

### 一键部署脚本

项目已提供自动化部署脚本：

```bash
cd /opt/lest-platform/backend/docker

# 完整部署（构建 + 启动）
bash build-and-deploy.sh

# 查看服务状态
bash health-check.sh

# 停止所有服务
bash stop.sh
```

### 更新部署

```bash
cd /opt/lest-platform
git pull origin main

# 重新构建并启动
cd backend/docker
bash build-and-deploy.sh
```

### TLS 证书（Let's Encrypt）

```bash
# 安装 certbot
apt install -y certbot python3-certbot-nginx

# 申请泛域名证书
certbot certonly --manual --preferred-challenges dns \
  -d "lest.work" -d "*.lest.work" \
  --server https://acme-v02.api.letsencrypt.org/directory

# 复制证书
cp /etc/letsencrypt/live/lest.work/fullchain.pem /etc/nginx/ssl/lest.work.crt
cp /etc/letsencrypt/live/lest.work/privkey.pem /etc/nginx/ssl/lest.work.key

# 续期
certbot renew
```

### 完整架构拓扑

```
Internet
   │
   ▼
Nginx (lest.work HTTPS)
   │
   ├── app.lest.work ────► Vue 3 SPA (Nginx :5173)
   ├── api.lest.work ────► Gateway (localhost:8080)
   ├── doc.lest.work ────► Swagger UI
   ├── nacos.lest.work ──► Nacos (:8848)
   └── minio.lest.work ──► MinIO (:9001)

lest.work 生态 ─────────► lest.top (官网, 待开发)
```

详细域名架构见 [docs/guide/DOMAIN_PLAN.md](../docs/guide/DOMAIN_PLAN.md)。

## 相关文档

- [Lest Platform 项目概览](./../docs/README.md)
- [后端开发规范](./../.windsurf/rules/02-backend-conventions.md)
- [部署规范](./../.windsurf/rules/08-deployment-conventions.md)

## 支持

如有问题，请参考：

1. 查看容器日志：`docker-compose logs -f <service-name>`
2. 检查 Nacos 配置：http://localhost:18848
3. 查看 Sentinel 监控：http://localhost:8858
4. 检查 MinIO 存储：http://localhost:9001
