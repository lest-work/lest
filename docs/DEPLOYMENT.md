# LEST Platform 部署指南

## 文档信息

| 属性 | 内容 |
|------|------|
| 版本 | V1.0 |
| 状态 | 已完成 |
| 创建日期 | 2026-05-26 |
| 最后更新 | 2026-05-26 |

---

## 1. 环境要求

### 1.1 软件版本要求

| 软件 | 版本要求 | 说明 |
|------|---------|------|
| JDK | 25 | 后端 Java 运行环境 |
| Node.js | 18+ LTS | 前端开发/构建 |
| Docker | 24.0+ | 容器化支持 |
| Docker Compose | 2.20+ | 本地容器编排 |
| Kubernetes | 1.28+ | 容器编排（生产环境） |
| kubectl | 1.28+ | K8s 客户端工具 |
| Git | 2.30+ | 版本控制 |

### 1.2 硬件要求

| 环境 | CPU | 内存 | 磁盘 |
|------|------|------|------|
| 本地开发 | 4 核+ | 8GB+ | 20GB+ |
| Docker Compose | 8 核+ | 16GB+ | 50GB+ |
| Kubernetes 生产 | 16 核+ | 32GB+ | 100GB+ |

---

## 2. 中间件版本总览

LEST Platform 所有中间件均使用**成熟开源稳定版本**，不采用自研替代方案：

| 类别 | 组件 | 版本 | 说明 |
|------|------|------|------|
| **数据库** | MySQL | 8.4 | 主数据存储，每服务独立 Database |
| **缓存** | Redis | 8.6-alpine | 会话、缓存、分布式锁 |
| **消息队列** | Apache Kafka | 4.3.0 | KRaft 模式，无需 Zookeeper |
| **服务注册/配置** | Nacos | 3.2.1 | 服务发现 + 配置管理 + AI Registry |
| **对象存储** | pgsty/minio | RELEASE.2026-04-17 | S3 兼容，文件存储 |
| **Java 框架** | Spring Boot | 4.0.6 | 后端核心框架 |
| **Java 框架** | Spring Cloud | 2025.0.2 (Northfields) | 微服务生态 |
| **Java 框架** | Spring Cloud Alibaba | 2025.1.0.0 | Nacos/Sentinel/Seata 原生集成 |
| **ORM** | MyBatis-Plus | 3.5.16 | Java ORM 框架 |
| **熔断限流** | Resilience4j | 2.4.0 | 熔断、限流、重试 |
| **API 限流** | Bucket4j | 8.18.0 | 基于令牌桶的限流 |
| **JWT** | jjwt | 0.13.0 | JWT 编解码，支持 JWKS |
| **分布式锁** | Redisson | 4.3.1 | 基于 Redis 的分布式锁 |
| **分布式哈希** | zero-allocation-hashing | (按需引入) | xxHash 替代手写实现 |

---

## 3. Docker Compose 部署（本地/测试环境）

### 3.1 快速启动

```bash
# 克隆项目
git clone https://github.com/lest-work/lest.git
cd lest-platform

# 启动所有服务（基础设施 + 应用）
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f

# 停止所有服务
docker-compose down
```

### 3.2 服务端口映射

| 服务 | 端口 | 说明 |
|------|------|------|
| lest-gateway | 8080 | API 网关入口 |
| lest-auth | 8096 | 认证服务 |
| lest-system | 8081 | 系统管理服务 |
| lest-project | 8082 | 项目管理服务 |
| lest-task | 8083 | 任务管理服务 |
| lest-code | 8084 | 代码管理服务 |
| lest-meeting | 8085 | 会议管理服务 |
| lest-notification | 8086 | 消息通知服务 |
| lest-release | 8087 | 发布管理服务 |
| lest-performance | 8088 | 团队绩效服务 |
| lest-wakapi | 8089 | WakaTime 集成服务 |
| lest-ai | 8090 | AI 服务 |
| lest-file | 8091 | 文件服务 |
| lest-plugin | 8092 | 插件系统服务 |
| lest-open | 8094 | 开放平台服务 |

**基础设施端口：**

| 组件 | 端口 | 说明 |
|------|------|------|
| MySQL | 3306 | 数据库 |
| Redis | 6379 | 缓存 |
| Kafka | 9092 | 消息队列 |
| Nacos | 8848 | 服务注册与配置 |
| MinIO API | 9000 | 对象存储 API |
| MinIO Console | 9001 | 对象存储控制台 |

### 3.3 环境变量配置

在 `.env` 文件中配置（或直接在 shell 中导出）：

```bash
# 数据库
MYSQL_PASSWORD=12345678

# Redis
REDIS_PASSWORD=redis123

# MinIO
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=minioadmin
```

### 3.4 构建应用镜像

```bash
# 构建所有应用镜像
docker-compose build

# 构建单个服务镜像
docker-compose build lest-gateway

# 仅启动基础设施
docker-compose up -d mysql redis kafka nacos minio
```

### 3.5 健康检查

```bash
# 检查所有容器健康状态
docker-compose ps

# 检查特定服务
curl http://localhost:8080/actuator/health
curl http://localhost:8848/nacos/
```

---

## 4. Kubernetes 部署（生产环境）

### 4.1 前置条件

```bash
# 确认 kubectl 已配置
kubectl cluster-info

# 创建 namespace
kubectl create namespace lest-platform
```

### 4.2 快速部署

```bash
# 方式一：使用 Kustomize（推荐）
kubectl apply -k k8s/

# 方式二：逐个应用
kubectl apply -f k8s/config/
kubectl apply -f k8s/infrastructure/
kubectl apply -f k8s/apps/
```

### 4.3 修改镜像地址

在 `k8s/apps/*.yaml` 中替换镜像地址为你的私有仓库：

```bash
# 替换所有镜像地址
sed -i 's|registry.example.com/lest/|your-registry.com/lest/|g' k8s/apps/*.yaml
```

### 4.4 修改 Secret

```bash
# 编辑 Secret
kubectl edit secret lest-secrets -n lest-platform

# 或使用 sealed-secrets 等方案管理生产环境密钥
```

### 4.5 验证部署

```bash
# 查看所有 Pod
kubectl get pods -n lest-platform

# 查看服务
kubectl get svc -n lest-platform

# 查看 HPA
kubectl get hpa -n lest-platform

# 查看 Pod 日志
kubectl logs -f deployment/lest-gateway -n lest-platform

# 进入 Pod 调试
kubectl exec -it deployment/lest-gateway -n lest-platform -- /bin/sh
```

### 4.6 扩容/缩容

```bash
# 手动扩容
kubectl scale deployment lest-task --replicas=5 -n lest-platform

# HPA 自动扩缩容（已配置，基于 CPU/内存）
kubectl autoscale deployment lest-task --min=2 --max=10 --cpu-percent=70 -n lest-platform
```

### 4.7 滚动更新

```bash
# 更新镜像版本
kubectl set image deployment/lest-gateway lest-gateway=your-registry.com/lest/lest-gateway:v1.1.0 -n lest-platform

# 查看滚动更新进度
kubectl rollout status deployment/lest-gateway -n lest-platform

# 回滚
kubectl rollout undo deployment/lest-gateway -n lest-platform
```

### 4.8 卸载

```bash
kubectl delete -k k8s/
kubectl delete namespace lest-platform
```

---

## 5. 构建产物

### 5.1 后端构建

```bash
cd backend

# 清理并构建所有服务
./gradlew clean build -x test

# 构建单个服务
./gradlew :lest-modules:lest-gateway:bootJar

# 构建 Docker 镜像
./gradlew bootJar
docker build -f Dockerfile -t lest-platform/lest-gateway:latest ./lest-modules/lest-gateway
```

### 5.2 前端构建

```bash
cd frontend-pc

# 安装依赖
npm install

# 开发模式
npm run dev

# 生产构建
npm run build
```

---

## 6. 配置说明

### 6.1 环境变量优先级

```
命令行参数 > 环境变量 > application-{profile}.yml > application.yml
```

### 6.2 应用配置（application.yml）

所有微服务的 `application.yml` 已配置环境变量支持，生产部署通过环境变量覆盖默认配置：

```bash
# 数据库配置
MYSQL_HOST=mysql
MYSQL_DB=xxx_db
MYSQL_USERNAME=root
MYSQL_PASSWORD=xxx

# Redis 配置
REDIS_HOST=redis
REDIS_PORT=6379
REDIS_PASSWORD=xxx

# Kafka 配置
KAFKA_BOOTSTRAP_SERVERS=kafka:9092

# Nacos 配置
NACOS_SERVER=nacos:8848
NACOS_USERNAME=nacos
NACOS_PASSWORD=xxx

# MinIO 配置
STORAGE_TYPE=minio
MINIO_ENDPOINT=http://minio:9000
MINIO_ACCESS_KEY=xxx
MINIO_SECRET_KEY=xxx
MINIO_BUCKET=lest-files
```

### 6.3 Kubernetes 配置

- **ConfigMap** (`k8s/config/configmap.yaml`): 服务端口、数据库名、Kafka 地址等非敏感配置
- **Secret** (`k8s/config/secret.yaml`): 密码、JWT 密钥、API Key 等敏感配置

---

## 7. 监控与可观测性

每个服务均暴露以下端点：

| 端点 | 说明 |
|------|------|
| `/actuator/health` | 健康检查 |
| `/actuator/info` | 应用信息 |
| `/actuator/metrics` | Prometheus 指标 |
| `/actuator/prometheus` | Prometheus 格式指标 |

### 7.1 Prometheus 抓取配置

```yaml
scrape_configs:
  - job_name: 'lest-platform'
    kubernetes_sd_configs:
      - role: pod
    relabel_configs:
      - source_labels: [__meta_kubernetes_pod_annotation_prometheus_io_scrape]
        action: keep
        regex: true
      - source_labels: [__meta_kubernetes_pod_annotation_prometheus_io_port]
        action: keep
        regex: (.+)
        target_label: __param_target
      - source_labels: [__meta_kubernetes_pod_name]
        target_label: pod
```

---

## 8. 安全配置

### 8.1 生产环境必做事项

1. **修改所有默认密码**：MySQL、Redis、Nacos、MinIO、JWT Secret
2. **启用 TLS**：所有服务间通信使用 HTTPS
3. **网络隔离**：使用 Kubernetes NetworkPolicy 限制服务间访问
4. **镜像安全扫描**：使用 Trivy 或 Snyk 扫描镜像漏洞
5. **Secret 管理**：使用 Sealed Secrets、Vault 或云厂商 KMS 管理密钥
6. **审计日志**：开启数据库审计日志，记录所有 DDL 和敏感操作

### 8.2 防火墙/网络策略

```yaml
# 禁止所有 Pod 间流量
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: default-deny-all
  namespace: lest-platform
spec:
  podSelector: {}
  policyTypes:
    - Ingress
    - Egress
```

---

## 9. 常见问题

### 9.1 容器启动失败

```bash
# 查看详细日志
docker-compose logs <service-name>

# 进入容器调试
docker exec -it <container-name> /bin/sh
```

### 9.2 数据库连接失败

```
检查清单：
1. MySQL 容器是否健康: docker-compose ps mysql
2. 数据库是否已初始化: 检查 16 个 database 是否存在
3. 用户名密码是否正确: 检查 docker-compose.yml 中的 MYSQL_PASSWORD
4. 网络是否互通: docker network inspect lest-platform_default
```

### 9.3 Kafka 连接失败

```bash
# 检查 Kafka 是否就绪
docker-compose exec kafka kafka-topics.sh --bootstrap-server localhost:9092 --list

# 查看 Kafka 日志
docker-compose logs kafka
```

### 9.4 Pod 无法调度

```bash
# 查看 Pod 详情
kubectl describe pod <pod-name> -n lest-platform

# 常见原因：资源不足、镜像拉取失败、依赖服务未就绪
```

---

## 10. 参考链接

| 资源 | 链接 |
|------|------|
| Spring Boot 文档 | https://spring.io/projects/spring-boot |
| Spring Cloud 文档 | https://spring.io/projects/spring-cloud |
| Nacos 文档 | https://nacos.io/docs/latest/ |
| Kafka 文档 | https://kafka.apache.org/documentation/ |
| Redis 文档 | https://redis.io/docs/ |
| MinIO 文档 | https://min.io/docs/ |
| Kubernetes 文档 | https://kubernetes.io/zh/docs/ |
| Docker 文档 | https://docs.docker.com/ |

---

## 版本历史

| 版本 | 日期 | 修改内容 | 作者 |
|------|------|---------|------|
| V1.0 | 2026-05-26 | 初始版本 | LEST Team |
