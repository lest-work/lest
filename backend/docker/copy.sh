#!/bin/sh

# 复制项目的文件到对应docker路径

BASE_DIR=$(cd "$(dirname "$0")" && pwd)
cd "$BASE_DIR"/.. || exit

# 复制SQL
cp sql/*.sql docker/mysql/db/

# 复制jar
cp lest-gateway/target/lest-gateway-*.jar docker/lest/gateway/jar/lest-gateway.jar
cp lest-auth/target/lest-auth-*.jar docker/lest/auth/jar/lest-auth.jar
cp lest-modules/lest-system/target/lest-system-*.jar docker/lest/modules/system/jar/lest-modules-system.jar
cp lest-modules/lest-job/target/lest-job-*.jar docker/lest/modules/job/jar/lest-modules-job.jar
cp lest-modules/lest-file/target/lest-file-*.jar docker/lest/modules/file/jar/lest-modules-file.jar
cp lest-modules/lest-project/target/lest-project-*.jar docker/lest/modules/project/jar/lest-modules-project.jar
cp lest-modules/lest-task/target/lest-task-*.jar docker/lest/modules/task/jar/lest-modules-task.jar
cp lest-modules/lest-release/target/lest-release-*.jar docker/lest/modules/release/jar/lest-modules-release.jar
cp lest-visual/lest-monitor/target/lest-monitor-*.jar docker/lest/visual/monitor/jar/lest-visual-monitor.jar
