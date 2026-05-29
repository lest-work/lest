#!/bin/bash
echo ""
echo "[信息] 运行 lest-gateway 模块"
echo ""

cd "$(dirname "$0")/.."
java -jar lest-gateway/target/lest-gateway-*.jar
