
@echo off
REM 学生反馈系统 - 后端启动脚本（Windows）

echo ==========================================
echo 学生反馈系统 - 后端启动
echo ==========================================
echo.

REM 启动应用
echo 启动 Spring Boot 应用...
echo.

cd /d "%~dp0"

mvn spring-boot:run ^
    -DsocksProxyHost=127.0.0.1 ^
    -DsocksProxyPort=51080 ^
    -Dspring.profiles.active=dev

pause