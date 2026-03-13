#!/bin/bash

# ============================================
# 学生反馈系统 - 数据库连接测试脚本
# 用于测试 TiDB 连接是否正常
# 创建日期: 2026-03-11
# ============================================

echo "=========================================="
echo "学生反馈系统 - 数据库连接测试"
echo "=========================================="
echo ""

# 数据库连接信息
DB_HOST="xxxxx"
DB_PORT="xxxx"
DB_USER="xxxx"
DB_PASS="xxxxx"
DB_NAME="feedback_system"

# 检查 socks 代理是否运行
echo "1. 检查 socks 代理..."
if nc -z 127.0.0.1 51080 2>/dev/null; then
    echo "✓ socks 代理正在运行 (127.0.0.1:51080)"
else
    echo "✗ socks 代理未运行，请先启动代理"
    echo "  提示：确保 socks 代理运行在 127.0.0.1:51080"
    exit 1
fi
echo ""

# 测试数据库连接
echo "2. 测试数据库连接..."
mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASS -e "SELECT 1;" 2>/dev/null
if [ $? -eq 0 ]; then
    echo "✓ 数据库连接成功"
else
    echo "✗ 数据库连接失败"
    echo "  提示：请检查数据库连接信息和代理配置"
    exit 1
fi
echo ""

# 检查数据库是否存在
echo "3. 检查数据库..."
DB_EXISTS=$(mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASS -e "SHOW DATABASES LIKE '$DB_NAME';" 2>/dev/null | grep $DB_NAME)
if [ -n "$DB_EXISTS" ]; then
    echo "✓ 数据库 $DB_NAME 已存在"
else
    echo "✗ 数据库 $DB_NAME 不存在"
    echo "  提示：请先执行 init-db.sql 创建数据库"
    exit 1
fi
echo ""

# 检查表是否存在
echo "4. 检查数据表..."
TABLE_COUNT=$(mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASS -D $DB_NAME -e "SHOW TABLES;" 2>/dev/null | wc -l)
TABLE_COUNT=$((TABLE_COUNT - 1))  # 减去表头
if [ $TABLE_COUNT -gt 0 ]; then
    echo "✓ 共有 $TABLE_COUNT 张表"
    mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASS -D $DB_NAME -e "SHOW TABLES;" 2>/dev/null
else
    echo "✗ 数据库中没有表"
    echo "  提示：请先执行 init-db.sql 创建表"
    exit 1
fi
echo ""

# 检查数据
echo "5. 检查数据..."
USER_COUNT=$(mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASS -D $DB_NAME -e "SELECT COUNT(*) FROM tb_user;" 2>/dev/null | tail -n 1)
CATEGORY_COUNT=$(mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASS -D $DB_NAME -e "SELECT COUNT(*) FROM tb_category;" 2>/dev/null | tail -n 1)
FEEDBACK_COUNT=$(mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASS -D $DB_NAME -e "SELECT COUNT(*) FROM tb_feedback;" 2>/dev/null | tail -n 1)

echo "✓ 用户数: $USER_COUNT"
echo "✓ 类别数: $CATEGORY_COUNT"
echo "✓ 反馈数: $FEEDBACK_COUNT"
echo ""

# 测试完成
echo "=========================================="
echo "✓ 数据库连接测试通过！"
echo "=========================================="
echo ""
echo "测试账号信息："
echo "  学生账号: 20240101 / 123456"
echo "  管理员账号: admin1 / 123456"
echo "  系统管理员: sysadmin / 123456"
echo ""
