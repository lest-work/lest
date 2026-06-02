#!/bin/bash
# stop-local-services.sh
# 停止 lest-platform 所有本地后端服务

echo "=== 停止 lest-platform 本地服务 ==="

for jar in sentinel-dashboard lest-gateway lest-auth lest-system lest-project lest-task lest-file lest-job lest-release lest-monitor; do
  pid=$(ps aux | grep "$jar.jar" | grep -v grep | awk '{print $2}')
  if [ -n "$pid" ]; then
    echo "  🛑 停止 $jar (PID: $pid)"
    kill $pid 2>/dev/null
  else
    echo "  ⏭️  $jar 未运行"
  fi
done

echo ""
echo "=== 端口状态 ==="
for port in 8080 8081 8082 8083 8091 8096 9100 9203 8087; do
  if lsof -i :$port 2>/dev/null | grep LISTEN | grep -q .; then
    echo "  ⚠️  :$port 仍被占用"
  else
    echo "  ✅ :$port 已释放"
  fi
done
