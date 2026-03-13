#!/bin/bash

# 学生反馈系统 - 快速启动脚本（前端）

echo "=========================================="
echo "学生反馈系统 - 前端启动"
echo "=========================================="
echo ""

# 检查 Node.js
echo "1. 检查 Node.js..."
if ! command -v node &> /dev/null; then
    echo "✗ Node.js 未安装"
    exit 1
fi
echo "✓ Node.js 已安装：$(node --version)"
echo ""

# 检查 pnpm
echo "2. 检查 pnpm..."
if ! command -v pnpm &> /dev/null; then
    echo "✗ pnpm 未安装"
    exit 1
fi
echo "✓ pnpm 已安装：$(pnpm --version)"
echo ""

# 安装依赖
echo "3. 安装依赖..."
pnpm install
if [ $? -ne 0 ]; then
    echo "✗ 依赖安装失败"
    exit 1
fi
echo "✓ 依赖安装成功"
echo ""

# 启动开发服务器
echo "4. 启动开发服务器..."
echo "前端应用将在 http://localhost:3001/ 启动"
echo ""
pnpm run dev
