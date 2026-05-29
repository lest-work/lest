#!/bin/bash
echo ""
echo "[信息] 运行 lest-auth 模块"
echo ""

cd "$(dirname "$0")/.."
java -jar lest-auth/target/lest-auth-*.jar
