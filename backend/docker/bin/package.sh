#!/bin/sh

# LEST Platform - 复制 Jar 包到 Docker 目录
# 使用说明: sh bin/package.sh

BASE_DIR=$(cd "$(dirname "$0")/.." && pwd)
BACKEND_DIR="$BASE_DIR/.."

echo "========================================="
echo " LEST Platform - 打包 Jar 到 Docker 目录"
echo "========================================="

# --- 应用服务 ---
echo ">>> 复制 lest-gateway ..."
cp "$BACKEND_DIR/lest-gateway/target/lest-gateway.jar" "$BASE_DIR/lest/gateway/jar/" 2>/dev/null || echo "  (lest-gateway.jar not found, skip)"

echo ">>> 复制 lest-auth ..."
cp "$BACKEND_DIR/lest-auth/target/lest-auth.jar" "$BASE_DIR/lest/auth/jar/" 2>/dev/null || echo "  (lest-auth.jar not found, skip)"

echo ">>> 复制 lest-system ..."
cp "$BACKEND_DIR/lest-modules/lest-system/target/lest-system.jar" "$BASE_DIR/lest/modules/system/jar/" 2>/dev/null || echo "  (lest-system.jar not found, skip)"

echo ">>> 复制 lest-job ..."
cp "$BACKEND_DIR/lest-modules/lest-job/target/lest-job.jar" "$BASE_DIR/lest/modules/job/jar/" 2>/dev/null || echo "  (lest-job.jar not found, skip)"

echo ">>> 复制 lest-file ..."
cp "$BACKEND_DIR/lest-modules/lest-file/target/lest-file.jar" "$BASE_DIR/lest/modules/file/jar/" 2>/dev/null || echo "  (lest-file.jar not found, skip)"

echo ">>> 复制 lest-project ..."
cp "$BACKEND_DIR/lest-modules/lest-project/target/lest-project.jar" "$BASE_DIR/lest/modules/project/jar/" 2>/dev/null || echo "  (lest-project.jar not found, skip)"

echo ">>> 复制 lest-task ..."
cp "$BACKEND_DIR/lest-modules/lest-task/target/lest-task.jar" "$BASE_DIR/lest/modules/task/jar/" 2>/dev/null || echo "  (lest-task.jar not found, skip)"

echo ">>> 复制 lest-release ..."
cp "$BACKEND_DIR/lest-modules/lest-release/target/lest-release.jar" "$BASE_DIR/lest/modules/release/jar/" 2>/dev/null || echo "  (lest-release.jar not found, skip)"

echo ">>> 复制 lest-monitor ..."
cp "$BACKEND_DIR/lest-visual/lest-monitor/target/lest-monitor.jar" "$BASE_DIR/lest/visual/monitor/jar/" 2>/dev/null || echo "  (lest-monitor.jar not found, skip)"

echo ">>> 复制 lest-meeting ..."
cp "$BACKEND_DIR/lest-modules/lest-meeting/target/lest-meeting.jar" "$BASE_DIR/lest/modules/meeting/jar/" 2>/dev/null || echo "  (lest-meeting.jar not found, skip)"

echo ">>> 复制 lest-notification ..."
cp "$BACKEND_DIR/lest-modules/lest-notification/target/lest-notification.jar" "$BASE_DIR/lest/modules/notification/jar/" 2>/dev/null || echo "  (lest-notification.jar not found, skip)"

echo ">>> 复制 lest-ai ..."
cp "$BACKEND_DIR/lest-modules/lest-ai/target/lest-ai.jar" "$BASE_DIR/lest/modules/ai/jar/" 2>/dev/null || echo "  (lest-ai.jar not found, skip)"

echo ">>> 复制 lest-performance ..."
cp "$BACKEND_DIR/lest-modules/lest-performance/target/lest-performance.jar" "$BASE_DIR/lest/modules/performance/jar/" 2>/dev/null || echo "  (lest-performance.jar not found, skip)"

echo ">>> 复制 lest-open ..."
cp "$BACKEND_DIR/lest-modules/lest-open/target/lest-open.jar" "$BASE_DIR/lest/modules/open/jar/" 2>/dev/null || echo "  (lest-open.jar not found, skip)"

echo ">>> 复制 lest-plugin ..."
cp "$BACKEND_DIR/lest-modules/lest-plugin/target/lest-plugin.jar" "$BASE_DIR/lest/modules/plugin/jar/" 2>/dev/null || echo "  (lest-plugin.jar not found, skip)"

echo ">>> 复制 lest-wakapi ..."
cp "$BACKEND_DIR/lest-modules/lest-wakapi/target/lest-wakapi.jar" "$BASE_DIR/lest/modules/wakapi/jar/" 2>/dev/null || echo "  (lest-wakapi.jar not found, skip)"

echo ""
echo "打包完成!"
