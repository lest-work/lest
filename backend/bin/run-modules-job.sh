#!/bin/bash
echo ""
echo "[信息] 运行 lest-job 模块"
echo ""

cd "$(dirname "$0")/.."
java -jar lest-modules/lest-job/target/lest-job-*.jar
