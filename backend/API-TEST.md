# 后端 API 测试文档

## 测试环境

- 后端地址：http://localhost:8080/api
- 数据库：TiDB（通过 socks 代理）

## 测试账号

### 学生账号
```
用户名：20240101
密码：123456
```

### 管理员账号
```
用户名：admin1
密码：123456
```

### 系统管理员
```
用户名：sysadmin
密码：123456
```

## API 测试

### 1. 健康检查

**请求**：
```bash
curl http://localhost:8080/api/health
```

**响应**：
```json
{
  "code": 200,
  "message": "系统运行正常",
  "data": {
    "status": "UP",
    "application": "student-feedback-system",
    "version": "1.0.0",
    "timestamp": 1710158400000
  },
  "timestamp": 1710158400000
}
```

### 2. 用户登录

**请求**：
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "20240101",
    "password": "123456"
  }'
```

**响应**：
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "id": 1,
      "username": "20240101",
      "realName": "张三",
      "role": "student",
      "classId": 1,
      "className": "高一(1)班"
    }
  },
  "timestamp": 1710158400000
}
```

### 3. 获取类别列表

**请求**：
```bash
curl http://localhost:8080/api/category/list
```

**响应**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "教学质量",
      "description": "对教师教学方法、课堂管理等方面的反馈",
      "requireTeacher": 1,
      "sort": 1,
      "status": 1,
      "createTime": "2024-01-01T00:00:00"
    },
    {
      "id": 2,
      "name": "课堂纪律",
      "description": "对课堂纪律、学习氛围等方面的反馈",
      "requireTeacher": 1,
      "sort": 2,
      "status": 1,
      "createTime": "2024-01-01T00:00:00"
    }
  ],
  "timestamp": 1710158400000
}
```

### 4. 用户登出

**请求**：
```bash
curl -X POST http://localhost:8080/api/auth/logout \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

**响应**：
```json
{
  "code": 200,
  "message": "登出成功",
  "data": null,
  "timestamp": 1710158400000
}
```

## 错误响应示例

### 参数校验失败
```json
{
  "code": 400,
  "message": "用户名不能为空",
  "data": null,
  "timestamp": 1710158400000
}
```

### 用户名或密码错误
```json
{
  "code": 401,
  "message": "用户名或密码错误",
  "data": null,
  "timestamp": 1710158400000
}
```

### 账号被禁用
```json
{
  "code": 403,
  "message": "账号已被禁用",
  "data": null,
  "timestamp": 1710158400000
}
```

### 系统异常
```json
{
  "code": 500,
  "message": "系统异常，请联系管理员",
  "data": null,
  "timestamp": 1710158400000
}
```

## 注意事项

1. 所有需要认证的接口都需要在请求头中添加 `Authorization: Bearer {token}`
2. Token 有效期为 24 小时
3. 密码使用 BCrypt 加密存储
4. 所有时间字段使用 ISO 8601 格式
5. 响应时间戳为毫秒级 Unix 时间戳
