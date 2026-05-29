#!/bin/bash
echo ""
echo "[信息] 运行 lest-release 模块"
echo ""

cd "$(dirname "$0")/.."
java -jar lest-modules/lest-release/target/lest-release-*.jar
