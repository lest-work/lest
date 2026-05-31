#!/bin/bash

# ============================================================
# Lest Platform 完整构建和部署脚本
# 用途：编译 Java 代码、构建 Docker 镜像、启动容器
# ============================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
cd "$BACKEND_ROOT"

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

# 检查前置条件
check_prerequisites() {
    log_section "检查前置条件"
    
    # 检查 Maven
    if ! command -v mvn &> /dev/null; then
        log_error "Maven 未安装"
        exit 1
    fi
    log_info "✓ Maven 已安装"
    
    # 检查 Docker
    if ! command -v docker &> /dev/null; then
        log_error "Docker 未安装"
        exit 1
    fi
    log_info "✓ Docker 已安装"
    
    # 检查 Docker Compose
    if ! command -v docker-compose &> /dev/null; then
        log_error "Docker Compose 未安装"
        exit 1
    fi
    log_info "✓ Docker Compose 已安装"
    
    echo ""
}

# 编译 Java 代码
build_java() {
    log_section "编译 Java 代码"
    
    log_info "运行 mvn clean compile..."
    mvn clean compile -DskipTests
    
    if [ $? -eq 0 ]; then
        log_info "✓ 编译成功"
    else
        log_error "编译失败"
        exit 1
    fi
    
    echo ""
}

# 打包 JAR 文件
package_jars() {
    log_section "打包 JAR 文件"
    
    log_info "运行 mvn clean package..."
    mvn clean package -DskipTests \
        -pl backend/lest-gateway,\
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
    
    if [ $? -eq 0 ]; then
        log_info "✓ 打包成功"
    else
        log_error "打包失败"
        exit 1
    fi
    
    echo ""
}

# 复制 JAR 文件到 Docker 目录
copy_jars() {
    log_section "复制 JAR 文件到 Docker 目录"
    
    JAR_DIR="$SCRIPT_DIR/lest/jar"
    mkdir -p "$JAR_DIR"
    
    # 定义 JAR 文件映射
    declare -A JAR_FILES=(
        ["lest-gateway"]="backend/lest-gateway/target/lest-gateway-1.0.0-SNAPSHOT.jar"
        ["lest-auth"]="backend/lest-auth/target/lest-auth-1.0.0-SNAPSHOT.jar"
        ["lest-system"]="backend/lest-modules/lest-system/target/lest-system-1.0.0-SNAPSHOT.jar"
        ["lest-project"]="backend/lest-modules/lest-project/target/lest-project-1.0.0-SNAPSHOT.jar"
        ["lest-task"]="backend/lest-modules/lest-task/target/lest-task-1.0.0-SNAPSHOT.jar"
        ["lest-release"]="backend/lest-modules/lest-release/target/lest-release-1.0.0-SNAPSHOT.jar"
        ["lest-file"]="backend/lest-modules/lest-file/target/lest-file-1.0.0-SNAPSHOT.jar"
        ["lest-job"]="backend/lest-modules/lest-job/target/lest-job-1.0.0-SNAPSHOT.jar"
        ["lest-meeting"]="backend/lest-modules/lest-meeting/target/lest-meeting-1.0.0-SNAPSHOT.jar"
        ["lest-notification"]="backend/lest-modules/lest-notification/target/lest-notification-1.0.0-SNAPSHOT.jar"
        ["lest-ai"]="backend/lest-modules/lest-ai/target/lest-ai-1.0.0-SNAPSHOT.jar"
        ["lest-performance"]="backend/lest-modules/lest-performance/target/lest-performance-1.0.0-SNAPSHOT.jar"
        ["lest-open"]="backend/lest-modules/lest-open/target/lest-open-1.0.0-SNAPSHOT.jar"
        ["lest-plugin"]="backend/lest-modules/lest-plugin/target/lest-plugin-1.0.0-SNAPSHOT.jar"
        ["lest-wakapi"]="backend/lest-modules/lest-wakapi/target/lest-wakapi-1.0.0-SNAPSHOT.jar"
        ["lest-visual-monitor"]="backend/lest-visual/lest-visual-monitor/target/lest-visual-monitor-1.0.0-SNAPSHOT.jar"
    )
    
    for jar_name in "${!JAR_FILES[@]}"; do
        jar_path="${JAR_FILES[$jar_name]}"
        if [ -f "$jar_path" ]; then
            cp "$jar_path" "$JAR_DIR/$jar_name.jar"
            log_info "✓ 复制 $jar_name.jar"
        else
            log_warn "⚠ 未找到 $jar_path"
        fi
    done
    
    echo ""
}

# 构建 Docker 镜像
build_docker_images() {
    log_section "构建 Docker 镜像"
    
    cd "$SCRIPT_DIR"
    
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
    
    cd "$SCRIPT_DIR"
    
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
    
    cd "$SCRIPT_DIR"
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
    log_section "Lest Platform 完整构建和部署"
    echo ""
    
    check_prerequisites
    build_java
    package_jars
    copy_jars
    build_docker_images
    start_containers
    wait_for_services
    show_status
    
    log_section "构建和部署完成！"
}

main "$@"
