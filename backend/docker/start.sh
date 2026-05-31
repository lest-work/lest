#!/bin/bash

# ============================================================
# Lest Platform Docker 启动脚本
# 用途：启动所有 Docker 容器（基础设施 + 应用服务）
# 前置条件：MySQL、Redis、Kafka、MongoDB 已在本地运行
# ============================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查本地服务
check_local_services() {
    log_info "检查本地服务..."
    
    # 检查 MySQL
    if ! nc -z localhost 3306 2>/dev/null; then
        log_warn "MySQL (localhost:3306) 未运行"
    else
        log_info "✓ MySQL 已运行"
    fi
    
    # 检查 Redis
    if ! nc -z localhost 6379 2>/dev/null; then
        log_warn "Redis (localhost:6379) 未运行"
    else
        log_info "✓ Redis 已运行"
    fi
    
    # 检查 Kafka
    if ! nc -z localhost 9092 2>/dev/null; then
        log_warn "Kafka (localhost:9092) 未运行"
    else
        log_info "✓ Kafka 已运行"
    fi
}

# 启动 Docker 容器
start_containers() {
    log_info "启动 Docker 容器..."
    
    # 检查 docker-compose 文件
    if [ ! -f "docker-compose.yml" ]; then
        log_error "docker-compose.yml 不存在"
        exit 1
    fi
    
    # 启动容器
    docker-compose -f docker-compose.yml up -d
    
    if [ $? -eq 0 ]; then
        log_info "✓ Docker 容器启动成功"
    else
        log_error "Docker 容器启动失败"
        exit 1
    fi
}

# 等待服务就绪
wait_for_services() {
    log_info "等待服务就绪..."
    
    # 等待 Nacos
    log_info "等待 Nacos (8848)..."
    for i in {1..30}; do
        if curl -s http://localhost:8848/nacos/ > /dev/null 2>&1; then
            log_info "✓ Nacos 已就绪"
            break
        fi
        if [ $i -eq 30 ]; then
            log_warn "Nacos 启动超时"
        fi
        sleep 2
    done
    
    # 等待 Gateway
    log_info "等待 Gateway (8080)..."
    for i in {1..30}; do
        if curl -s http://localhost:8080/doc.html > /dev/null 2>&1; then
            log_info "✓ Gateway 已就绪"
            break
        fi
        if [ $i -eq 30 ]; then
            log_warn "Gateway 启动超时"
        fi
        sleep 2
    done
}

# 显示服务状态
show_status() {
    log_info "显示容器状态..."
    docker-compose -f docker-compose.yml ps
    
    echo ""
    log_info "服务访问地址："
    echo "  - Gateway:     http://localhost:8080"
    echo "  - Nacos:       http://localhost:18848"
    echo "  - Sentinel:    http://localhost:8858"
    echo "  - MinIO:       http://localhost:9001"
    echo ""
}

# 主流程
main() {
    log_info "=========================================="
    log_info "Lest Platform Docker 启动脚本"
    log_info "=========================================="
    echo ""
    
    check_local_services
    echo ""
    
    start_containers
    echo ""
    
    wait_for_services
    echo ""
    
    show_status
    
    log_info "=========================================="
    log_info "启动完成！"
    log_info "=========================================="
}

main "$@"
