#!/bin/bash

# LEST Platform - Docker 部署脚本
# 使用说明: sh bin/deploy.sh [port|base|modules|app|stop|rm]

BASE_DIR=$(cd "$(dirname "$0")/.." && pwd)
cd "$BASE_DIR"

usage() {
    echo "========================================="
    echo " LEST Platform Docker 部署脚本"
    echo "========================================="
    echo "Usage: sh bin/deploy.sh [command]"
    echo ""
    echo "Commands:"
    echo "  port    - 开启所需端口 (需要 sudo)"
    echo "  base    - 启动基础环境 (Nacos + Sentinel)"
    echo "  app     - 启动应用服务 (Gateway + Auth + 所有模块)"
    echo "  stop    - 停止所有容器"
    echo "  rm      - 删除所有容器"
    echo "  clean   - 清理构建产物"
    echo ""
    exit 1
}

# 开启所需端口
port() {
    echo ">>> 开启所需端口..."
    for port in 8080 8848 8858 9848 9849; do
        if command -v firewall-cmd &>/dev/null; then
            firewall-cmd --add-port=${port}/tcp --permanent 2>/dev/null
        fi
    done
    if command -v firewall-cmd &>/dev/null; then
        firewall-cmd --reload 2>/dev/null
    fi
    echo "端口配置完成"
}

# 启动基础环境
base() {
    echo ">>> 启动基础环境 (Nacos + Sentinel)..."
    docker-compose up -d lest-nacos lest-sentinel
    echo "基础环境启动完成"
}

# 启动应用服务
app() {
    echo ">>> 构建并启动应用服务..."
    docker-compose up -d --build
    echo "应用服务启动完成"
}

# 停止所有容器
stop() {
    echo ">>> 停止所有容器..."
    docker-compose stop
    echo "所有容器已停止"
}

# 删除所有容器
rm() {
    echo ">>> 删除所有容器..."
    docker-compose rm
    echo "所有容器已删除"
}

# 清理构建产物
clean() {
    echo ">>> 清理构建产物..."
    # 清理各模块的 jar 包
    find "$BASE_DIR" -name "*.jar" -path "*/docker/*" -delete
    echo "构建产物已清理"
}

case "$1" in
"port")  port ;;
"base")  base ;;
"app")   app ;;
"stop")  stop ;;
"rm")    rm ;;
"clean") clean ;;
*)       usage ;;
esac
