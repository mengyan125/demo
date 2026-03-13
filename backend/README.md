# 学生反馈系统 - 后端

## 技术栈

- Spring Boot 2.7.18
- JDK 1.8
- MyBatis Plus 3.5.3.1
- TiDB 8.5 (MySQL 兼容)
- JetCache 2.7.3 + Redis
- JWT 0.11.5
- Lombok

## 项目结构

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/feedback/
│   │   │   ├── FeedbackApplication.java    # 主应用类
│   │   │   ├── config/                     # 配置类
│   │   │   ├── controller/                 # 控制器
│   │   │   ├── service/                    # 服务层
│   │   │   ├── mapper/                     # 数据访问层
│   │   │   ├── entity/                     # 实体类
│   │   │   ├── dto/                        # 数据传输对象
│   │   │   ├── common/                     # 公共类
│   │   │   └── util/                       # 工具类
│   │   └── resources/
│   │       ├── application.yml             # 主配置
│   │       ├── application-dev.yml         # 开发环境
│   │       └── application-prod.yml        # 生产环境
│   └── test/                               # 测试代码
└── pom.xml                                 # Maven 配置
```

## 数据库连接

### 连接信息
- 主机: xxxxx
- 端口: xxxx
- 数据库: feedback_system
- 用户名: xxxx
- 密码: xxxxx

### 代理配置
数据库连接需要通过 socks 代理：
```bash
# 启动时添加 JVM 参数
-DsocksProxyHost=127.0.0.1 -DsocksProxyPort=51080
```

### 初始化数据库
```bash
# 1. 确保 socks 代理运行在 127.0.0.1:51080
# 2. 执行初始化脚本
mysql -h xxxxx -P xxxx -u xxxx -p < ../scripts/init-db.sql

# 3. 执行测试数据脚本（可选）
mysql -h xxxxx -P xxxx -u xxxx -p < ../scripts/test-data.sql
```

## 运行项目

### 开发环境
```bash
# 方式1：使用 Maven
mvn spring-boot:run -DsocksProxyHost=127.0.0.1 -DsocksProxyPort=51080

# 方式2：使用 IDE
# 在 Run Configuration 中添加 VM options:
# -DsocksProxyHost=127.0.0.1 -DsocksProxyPort=51080
```

### 生产环境
```bash
# 1. 打包
mvn clean package -DskipTests

# 2. 运行
java -DsocksProxyHost=127.0.0.1 -DsocksProxyPort=51080 -jar target/student-feedback-system-1.0.0.jar
```

## API 文档

启动后访问：http://localhost:8080/api

## 测试账号

### 学生账号
- 用户名: 20240101
- 密码: 123456

### 管理员账号
- 用户名: admin1
- 密码: 123456

### 系统管理员
- 用户名: sysadmin
- 密码: 123456

## 开发规范

1. 所有代码必须有中文注释
2. 遵循阿里巴巴 Java 开发手册
3. 使用 Lombok 简化代码
4. 统一异常处理
5. 统一响应格式

## 注意事项

1. 确保 socks 代理正常运行
2. 数据库连接超时设置为 30 秒
3. 文件上传限制：单个文件 2MB，总大小 20MB
4. JWT Token 有效期：24 小时
