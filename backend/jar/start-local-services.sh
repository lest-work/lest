#!/bin/bash
# start-local-services.sh
# 在本地启动 lest-platform 所有后端服务（不走 Docker）
# 前提：Redis、MySQL 已运行；Nacos/Sentinel/MinIO 已通过 docker-compose 启动

set -e

JAR_DIR="$(cd "$(dirname "$0")" && pwd)"
LOG_DIR="$JAR_DIR/logs"
mkdir -p "$LOG_DIR"

echo "=== lest-platform 本地启动脚本 ==="
echo "JAR 目录: $JAR_DIR"
echo ""

# 启动单个服务
start_service() {
  local name=$1
  local jar=$2
  local port=$3
  shift 3
  local extra_args=("$@")

  if lsof -i :$port 2>/dev/null | grep LISTEN | grep -q .; then
    echo "⚠️  :$port 已被占用，跳过 $name"
    return
  fi

  echo "🚀 启动 $name (:$port)..."
  nohup java -jar "$JAR_DIR/$jar" \
    --server.port=$port \
    "${extra_args[@]}" \
    > "$LOG_DIR/$name.log" 2>&1 &
  echo "   PID: $! → $LOG_DIR/$name.log"
}

# 0. Sentinel Dashboard（官方 JAR，无认证）
if lsof -i :8858 2>/dev/null | grep LISTEN | grep -q .; then
  echo "⚠️  :8858 已被占用，跳过 sentinel-dashboard"
else
  echo "🚀 启动 Sentinel Dashboard (:8858/:8719)..."
  nohup java -Dserver.port=8858 \
    -Dcsp.sentinel.dashboard.server=127.0.0.1:8858 \
    -Dcsp.sentinel.api.port=8719 \
    -Dsentinel.dashboard.auth.enabled=false \
    -jar "$JAR_DIR/sentinel-dashboard.jar" \
    > "$LOG_DIR/sentinel-dashboard.log" 2>&1 &
  echo "   PID: $! → $LOG_DIR/sentinel-dashboard.log"
fi

# 1. 认证服务（最先启动，其他服务依赖它）
start_service "lest-auth"     "lest-auth.jar"     8096

# 2. 核心系统服务
start_service "lest-system"  "lest-system.jar"   8081

# 3. 项目管理
start_service "lest-project" "lest-project.jar"  8082

# 4. 任务管理
start_service "lest-task"    "lest-task.jar"     8083

# 5. 文件服务
start_service "lest-file"    "lest-file.jar"     8091

# 6. 定时任务
start_service "lest-job"     "lest-job.jar"      9203

# 7. 发布管理
start_service "lest-release" "lest-release.jar"  8087

# 8. 监控服务
start_service "lest-monitor" "lest-monitor.jar"  9100

# 9. 网关（最后启动）
start_service "lest-gateway" "lest-gateway.jar"  8080 \
  "-Dspring.cloud.sentinel.transport.dashboard=127.0.0.1:8858"

echo ""
echo "=== 等待服务启动 (10s)... ==="
sleep 10

echo ""
echo "=== 端口状态 ==="
for port in 8858 8080 8081 8082 8083 8091 8096 9100 9203 8087; do
  if lsof -i :$port 2>/dev/null | grep LISTEN | grep -q .; then
    echo "  ✅ :$port — $(lsof -i :$port | grep LISTEN | awk '{print $3, $9}' | head -1)"
  else
    echo "  ❌ :$port — 未启动"
  fi
done

echo ""
echo "=== 启动完成 ==="
echo "  网关:        http://localhost:8080"
echo "  认证服务:    http://localhost:8096"
echo "  系统服务:    http://localhost:8081"
echo "  项目服务:    http://localhost:8082"
echo "  任务服务:    http://localhost:8083"
echo "  文件服务:    http://localhost:8091"
echo "  定时任务:    http://localhost:9203"
echo "  发布服务:    http://localhost:8087"
echo "  监控服务:    http://localhost:9100"
echo ""
echo "  Nacos:       http://localhost:18848/nacos"
echo "  Sentinel:     http://localhost:8858"
echo "  MinIO:       http://localhost:9001"
echo ""
echo "日志目录: $LOG_DIR"
