#!/bin/bash
echo ""
echo "[信息] 运行 lest-task 模块"
echo ""

cd "$(dirname "$0")/.."
java -jar lest-modules/lest-task/target/lest-task-*.jar
