#!/bin/bash

# 学生反馈系统 - 后端启动脚本

echo "=========================================="
echo "学生反馈系统 - 后端启动"
echo "=========================================="
echo ""

# 检查 socks 代理
echo "1. 检查 socks 代理..."
if nc -z 127.0.0.1 51080 2>/dev/null; then
    echo "✓ socks 代理正在运行 (127.0.0.1:51080)"
else
    echo "✗ socks 代理未运行，请先启动代理"
    echo "  提示：确保 socks 代理运行在 127.0.0.1:51080"
    exit 1
fi
echo ""

# 启动应用
echo "2. 启动 Spring Boot 应用..."
echo ""

mvn spring-boot:run \
    -DsocksProxyHost=127.0.0.1 \
    -DsocksProxyPort=51080 \
    -Dspring.profiles.active=dev
