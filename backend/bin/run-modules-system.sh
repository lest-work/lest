#!/bin/bash
echo ""
echo "[信息] 运行 lest-system 模块"
echo ""

cd "$(dirname "$0")/.."
java -jar lest-modules/lest-system/target/lest-system-*.jar
