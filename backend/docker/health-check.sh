#!/bin/bash

# ============================================================
# Lest Platform 健康检查脚本
# 用途：检查所有服务的健康状态
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
    echo -e "${GREEN}[✓]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[✗]${NC} $1"
}

log_error() {
    echo -e "${RED}[✗]${NC} $1"
}

log_section() {
    echo -e "${BLUE}=== $1 ===${NC}"
}

# 检查本地服务
check_local_services() {
    log_section "本地服务检查"
    
    local mysql_ok=0
    local redis_ok=0
    local kafka_ok=0
    
    if nc -z localhost 3306 2>/dev/null; then
        log_info "MySQL (localhost:3306)"
        mysql_ok=1
    else
        log_warn "MySQL (localhost:3306) 未运行"
    fi
    
    if nc -z localhost 6379 2>/dev/null; then
        log_info "Redis (localhost:6379)"
        redis_ok=1
    else
        log_warn "Redis (localhost:6379) 未运行"
    fi
    
    if nc -z localhost 9092 2>/dev/null; then
        log_info "Kafka (localhost:9092)"
        kafka_ok=1
    else
        log_warn "Kafka (localhost:9092) 未运行"
    fi
    
    echo ""
    return $((mysql_ok && redis_ok && kafka_ok ? 0 : 1))
}

# 检查 Docker 容器
check_docker_containers() {
    log_section "Docker 容器检查"
    
    if ! command -v docker-compose &> /dev/null; then
        log_error "docker-compose 未安装"
        return 1
    fi
    
    if [ ! -f "docker-compose.yml" ]; then
        log_error "docker-compose.yml 不存在"
        return 1
    fi
    
    docker-compose -f docker-compose.yml ps
    echo ""
}

# 检查基础设施服务
check_infrastructure() {
    log_section "基础设施服务检查"
    
    # Nacos
    if curl -s http://localhost:8848/nacos/ > /dev/null 2>&1; then
        log_info "Nacos (http://localhost:8848)"
    else
        log_warn "Nacos (http://localhost:8848) 无响应"
    fi
    
    # Sentinel
    if curl -s http://localhost:8858/ > /dev/null 2>&1; then
        log_info "Sentinel (http://localhost:8858)"
    else
        log_warn "Sentinel (http://localhost:8858) 无响应"
    fi
    
    # MinIO
    if curl -s http://localhost:9001/ > /dev/null 2>&1; then
        log_info "MinIO (http://localhost:9001)"
    else
        log_warn "MinIO (http://localhost:9001) 无响应"
    fi
    
    echo ""
}

# 检查应用服务
check_application_services() {
    log_section "应用服务检查"
    
    local services=(
        "Gateway:8080:/doc.html"
        "Auth:8096:/swagger-ui.html"
        "System:8081:/swagger-ui.html"
        "Project:8082:/swagger-ui.html"
        "Task:8083:/swagger-ui.html"
        "Meeting:8085:/swagger-ui.html"
        "Notification:8086:/swagger-ui.html"
        "Release:8087:/swagger-ui.html"
        "Performance:8088:/swagger-ui.html"
        "AI:8090:/swagger-ui.html"
        "File:8091:/swagger-ui.html"
        "Plugin:8092:/swagger-ui.html"
        "Open:8094:/swagger-ui.html"
        "Job:9203:/swagger-ui.html"
    )
    
    for service in "${services[@]}"; do
        IFS=':' read -r name port path <<< "$service"
        if curl -s "http://localhost:${port}${path}" > /dev/null 2>&1; then
            log_info "$name (http://localhost:${port})"
        else
            log_warn "$name (http://localhost:${port}) 无响应"
        fi
    done
    
    echo ""
}

# 主流程
main() {
    echo -e "${BLUE}=========================================="
    echo "Lest Platform 健康检查"
    echo "==========================================${NC}"
    echo ""
    
    check_local_services
    check_docker_containers
    check_infrastructure
    check_application_services
    
    echo -e "${BLUE}=========================================="
    echo "健康检查完成"
    echo "==========================================${NC}"
}

main "$@"
