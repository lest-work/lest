#!/bin/bash
echo ""
echo "[信息] 运行 lest-project 模块"
echo ""

cd "$(dirname "$0")/.."
java -jar lest-modules/lest-project/target/lest-project-*.jar
