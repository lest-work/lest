#!/bin/bash
echo ""
echo "[信息] 打包Web工程，生成jar包文件。"
echo ""

cd "$(dirname "$0")/.."
mvn clean package -Dmaven.test.skip=true
