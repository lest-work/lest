#!/bin/bash

# ============================================================
# Lest Platform 快速启动脚本（假设 JAR 文件已存在）
# 用途：直接构建 Docker 镜像并启动容器
# 前置条件：JAR 文件已在 docker/lest/jar/ 目录中
# ============================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

log_section() {
    echo -e "${BLUE}=========================================="
    echo "$1"
    echo "==========================================${NC}"
}

# 检查 JAR 文件
check_jars() {
    log_section "检查 JAR 文件"
    
    JAR_DIR="$SCRIPT_DIR/lest/jar"
    
    if [ ! -d "$JAR_DIR" ]; then
        log_error "JAR 目录不存在：$JAR_DIR"
        exit 1
    fi
    
    local jar_count=$(ls -1 "$JAR_DIR"/*.jar 2>/dev/null | wc -l)
    
    if [ $jar_count -eq 0 ]; then
        log_error "JAR 文件不存在，请先运行 build-and-deploy.sh 或手动复制 JAR 文件"
        exit 1
    fi
    
    log_info "✓ 找到 $jar_count 个 JAR 文件"
    echo ""
}

# 构建 Docker 镜像
build_docker_images() {
    log_section "构建 Docker 镜像"
    
    log_info "运行 docker-compose build..."
    docker-compose -f docker-compose.yml build
    
    if [ $? -eq 0 ]; then
        log_info "✓ Docker 镜像构建成功"
    else
        log_error "Docker 镜像构建失败"
        exit 1
    fi
    
    echo ""
}

# 启动 Docker 容器
start_containers() {
    log_section "启动 Docker 容器"
    
    log_info "运行 docker-compose up..."
    docker-compose -f docker-compose.yml up -d
    
    if [ $? -eq 0 ]; then
        log_info "✓ Docker 容器启动成功"
    else
        log_error "Docker 容器启动失败"
        exit 1
    fi
    
    echo ""
}

# 等待服务就绪
wait_for_services() {
    log_section "等待服务就绪"
    
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
    
    echo ""
}

# 显示服务状态
show_status() {
    log_section "服务状态"
    
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
    log_section "Lest Platform 快速启动"
    echo ""
    
    check_jars
    build_docker_images
    start_containers
    wait_for_services
    show_status
    
    log_section "启动完成！"
}

main "$@"
