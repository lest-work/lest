---
description: 部署、Docker 与环境管理规范
---

# LEST 部署规范

## 1. 环境配置

### 1.1 环境分类

| 环境 | 用途 | 访问 | 数据 |
|------|------|------|------|
| **Local** | 本地开发 | localhost | 本地 MySQL |
| **Dev** | 开发测试 | 内网 | 共享 MySQL |
| **Staging** | 预发布 | 内网/VPN | 预发布数据库 |
| **Production** | 生产环境 | 外网 | 生产数据库 |

### 1.2 配置文件管理

```
backend/
├── lest-gateway/
│   └── src/main/resources/
│       ├── application.yml           # 通用配置
│       ├── application-local.yml     # 本地开发
│       ├── application-dev.yml       # 开发环境
│       ├── application-staging.yml   # 预发布
│       └── application-prod.yml      # 生产环境
```

**激活配置**：
```yaml
# application.yml
spring:
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:local}
```

**启动时指定**：
```bash
java -jar app.jar --spring.profiles.active=dev
```

### 1.3 敏感信息管理

**禁止**：
- 硬编码数据库密码、API Key、JWT Secret
- 提交 `.env` 文件到 Git

**推荐**：
- 使用环境变量
- 使用 Docker Secrets（生产环境）
- 使用配置中心（如 Nacos）

**示例**：
```yaml
# application.yml
spring:
  datasource:
    url: ${DB_URL:jdbc:mysql://localhost:3306/lest_platform}
    username: ${DB_USER:root}
    password: ${DB_PASSWORD:root}
```

## 2. Docker 部署

### 2.1 Dockerfile 规范

```dockerfile
# 后端服务 Dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

# 复制构建产物
COPY target/lest-project-1.0.0-SNAPSHOT.jar app.jar

# 暴露端口
EXPOSE 8082

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8082/actuator/health || exit 1

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 2.2 Docker Compose 编排

```yaml
# docker-compose.yml
version: '3.8'

services:
  # MySQL 数据库
  mysql:
    image: mysql:8.0
    container_name: lest-mysql
    environment:
      MYSQL_ROOT_PASSWORD: ${DB_PASSWORD:-root}
      MYSQL_DATABASE: lest_platform
      TZ: Asia/Shanghai
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./sql:/docker-entrypoint-initdb.d
    networks:
      - lest-network
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

  # Redis 缓存
  redis:
    image: redis:7-alpine
    container_name: lest-redis
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    networks:
      - lest-network
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5

  # Nacos 注册中心
  nacos:
    image: nacos/nacos-server:v2.2.0
    container_name: lest-nacos
    environment:
      MODE: standalone
      SPRING_DATASOURCE_PLATFORM: mysql
      MYSQL_SERVICE_HOST: mysql
      MYSQL_SERVICE_DB_NAME: nacos_config
      MYSQL_SERVICE_USER: root
      MYSQL_SERVICE_PASSWORD: ${DB_PASSWORD:-root}
    ports:
      - "8848:8848"
      - "9848:9848"
    depends_on:
      mysql:
        condition: service_healthy
    networks:
      - lest-network

  # API 网关
  gateway:
    build:
      context: ./backend/lest-gateway
      dockerfile: Dockerfile
    container_name: lest-gateway
    environment:
      SPRING_PROFILES_ACTIVE: ${SPRING_PROFILES_ACTIVE:-dev}
      NACOS_ENABLED: "true"
      NACOS_SERVER_ADDR: nacos:8848
    ports:
      - "8080:8080"
    depends_on:
      nacos:
        condition: service_started
    networks:
      - lest-network

  # 系统管理服务
  system:
    build:
      context: ./backend/lest-modules/lest-system
      dockerfile: Dockerfile
    container_name: lest-system
    environment:
      SPRING_PROFILES_ACTIVE: ${SPRING_PROFILES_ACTIVE:-dev}
      NACOS_ENABLED: "true"
      NACOS_SERVER_ADDR: nacos:8848
    depends_on:
      mysql:
        condition: service_healthy
      redis:
        condition: service_healthy
    networks:
      - lest-network

  # 项目管理服务
  project:
    build:
      context: ./backend/lest-modules/lest-project
      dockerfile: Dockerfile
    container_name: lest-project
    environment:
      SPRING_PROFILES_ACTIVE: ${SPRING_PROFILES_ACTIVE:-dev}
      NACOS_ENABLED: "true"
      NACOS_SERVER_ADDR: nacos:8848
    depends_on:
      mysql:
        condition: service_healthy
      redis:
        condition: service_healthy
    networks:
      - lest-network

  # 任务管理服务
  task:
    build:
      context: ./backend/lest-modules/lest-task
      dockerfile: Dockerfile
    container_name: lest-task
    environment:
      SPRING_PROFILES_ACTIVE: ${SPRING_PROFILES_ACTIVE:-dev}
      NACOS_ENABLED: "true"
      NACOS_SERVER_ADDR: nacos:8848
    depends_on:
      mysql:
        condition: service_healthy
      redis:
        condition: service_healthy
    networks:
      - lest-network

  # 前端应用
  frontend:
    build:
      context: ./frontend-pc
      dockerfile: Dockerfile
      args:
        VITE_API_BASE_URL: ${VITE_API_BASE_URL:-http://localhost:8080/api}
    container_name: lest-frontend
    ports:
      - "5173:5173"
    environment:
      GATEWAY_URL: http://gateway:8080
    depends_on:
      - gateway
    networks:
      - lest-network

volumes:
  mysql_data:
  redis_data:

networks:
  lest-network:
    driver: bridge
```

### 2.3 构建与发布

```bash
# 1. 构建后端服务
cd backend/lest-modules/lest-project
mvn clean package -DskipTests

# 2. 构建 Docker 镜像
docker build -t lest-project:1.0.0 .

# 3. 标记镜像
docker tag lest-project:1.0.0 registry.example.com/lest/project:1.0.0

# 4. 推送到镜像仓库
docker push registry.example.com/lest/project:1.0.0

# 5. 启动容器
docker-compose up -d

# 6. 查看日志
docker-compose logs -f project
```

## 3. 数据库初始化

### 3.1 初始化脚本

```
sql/
├── init.sql              # 基础表结构
├── system.sql            # 系统管理数据
├── project.sql           # 项目管理数据
├── task.sql              # 任务管理数据
└── mock-data.sql         # 测试数据
```

### 3.2 初始化步骤

```bash
# 1. 连接 MySQL
mysql -h localhost -u root -p

# 2. 创建数据库
CREATE DATABASE IF NOT EXISTS lest_platform 
  CHARACTER SET utf8mb4 
  COLLATE utf8mb4_unicode_ci;

# 3. 执行初始化脚本
USE lest_platform;
SOURCE sql/init.sql;
SOURCE sql/system.sql;
SOURCE sql/project.sql;
SOURCE sql/task.sql;

# 4. 验证
SHOW TABLES;
SELECT COUNT(*) FROM sys_user;
```

### 3.3 清理旧数据

```bash
# 1. 备份数据库
mysqldump -h localhost -u root -p lest_platform > backup_$(date +%Y%m%d_%H%M%S).sql

# 2. 删除所有表
mysql -h localhost -u root -p lest_platform < drop_all_tables.sql

# 3. 重新初始化
mysql -h localhost -u root -p lest_platform < sql/init.sql
```

## 4. 健康检查与监控

### 4.1 健康检查端点

```
GET /actuator/health
GET /actuator/health/liveness
GET /actuator/health/readiness
```

**响应示例**：
```json
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "redis": { "status": "UP" },
    "diskSpace": { "status": "UP" }
  }
}
```

### 4.2 Kubernetes 部署（可选）

```yaml
# k8s-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: lest-project
spec:
  replicas: 3
  selector:
    matchLabels:
      app: lest-project
  template:
    metadata:
      labels:
        app: lest-project
    spec:
      containers:
      - name: lest-project
        image: registry.example.com/lest/project:1.0.0
        ports:
        - containerPort: 8082
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: NACOS_SERVER_ADDR
          value: "nacos:8848"
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8082
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8082
          initialDelaySeconds: 10
          periodSeconds: 5
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
```

## 5. 发布清单

发布前检查：

- [ ] 所有测试通过（`mvn clean test`）
- [ ] 代码审查完成
- [ ] 版本号已更新（pom.xml, package.json）
- [ ] CHANGELOG 已更新
- [ ] 数据库迁移脚本已准备
- [ ] 环境变量已配置
- [ ] Docker 镜像已构建并测试
- [ ] 性能测试通过
- [ ] 安全扫描通过（OWASP, Snyk）
- [ ] 文档已更新

## 6. 回滚方案

```bash
# 1. 保存当前版本
docker tag lest-project:current lest-project:backup

# 2. 回滚到上一个版本
docker pull registry.example.com/lest/project:1.0.0
docker-compose up -d project

# 3. 验证服务状态
curl http://localhost:8080/actuator/health

# 4. 如果失败，恢复备份
docker-compose down
docker tag lest-project:backup lest-project:current
docker-compose up -d
```
