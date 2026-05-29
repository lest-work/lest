#!/bin/bash
echo ""
echo "[信息] 运行 lest-monitor 模块"
echo ""

cd "$(dirname "$0")/.."
java -jar lest-visual/lest-monitor/target/lest-monitor-*.jar
