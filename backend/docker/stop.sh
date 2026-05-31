#!/bin/bash

# ============================================================
# Lest Platform Docker 停止脚本
# 用途：停止所有 Docker 容器
# ============================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

main() {
    log_info "=========================================="
    log_info "Lest Platform Docker 停止脚本"
    log_info "=========================================="
    echo ""
    
    if [ ! -f "docker-compose.yml" ]; then
        log_error "docker-compose.yml 不存在"
        exit 1
    fi
    
    log_info "停止 Docker 容器..."
    docker-compose -f docker-compose.yml down
    
    if [ $? -eq 0 ]; then
        log_info "✓ Docker 容器停止成功"
    else
        log_error "Docker 容器停止失败"
        exit 1
    fi
    
    echo ""
    log_info "=========================================="
    log_info "停止完成！"
    log_info "=========================================="
}

main "$@"
