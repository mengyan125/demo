# 学生反馈系统 - 后端 API 文档

## 基本信息

- **基础 URL**: http://localhost:8080/api
- **认证方式**: JWT Token（Bearer）
- **请求头**: `Authorization: Bearer {token}`
- **响应格式**: JSON

## 统一响应格式

所有 API 响应都遵循以下格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1710158400000
}
```

## 错误码

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权（Token 无效或过期） |
| 403 | 禁止访问（账号被禁用） |
| 404 | 资源不存在 |
| 500 | 服务器错误 |

---

## API 端点

### 1. 认证相关

#### 1.1 用户登录

**请求**:
```
POST /auth/login
Content-Type: application/json

{
  "username": "20240101",
  "password": "123456"
}
```

**响应**:
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

#### 1.2 用户登出

**请求**:
```
POST /auth/logout
Authorization: Bearer {token}
```

**响应**:
```json
{
  "code": 200,
  "message": "登出成功",
  "data": null,
  "timestamp": 1710158400000
}
```

---

### 2. 用户相关

#### 2.1 获取当前用户信息

**请求**:
```
GET /user/info
Authorization: Bearer {token}
```

**响应**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "20240101",
    "realName": "张三",
    "role": "student",
    "classId": 1,
    "className": "高一(1)班",
    "status": 1,
    "createTime": "2024-01-01T00:00:00"
  },
  "timestamp": 1710158400000
}
```

---

### 3. 类别相关

#### 3.1 获取类别列表

**请求**:
```
GET /category/list
```

**响应**:
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

---

### 4. 反馈相关

#### 4.1 提交反馈

**请求**:
```
POST /feedback/submit
Authorization: Bearer {token}
Content-Type: application/json

{
  "categoryId": 1,
  "isTeacherRelated": 1,
  "teacherId": 4,
  "title": "希望增加课堂互动",
  "content": "张老师的数学课讲得很好，但是希望能增加一些课堂互动环节，让同学们更积极参与。",
  "isAnonymous": 0
}
```

**响应**:
```json
{
  "code": 200,
  "message": "反馈提交成功",
  "data": {
    "id": 1,
    "studentId": 1,
    "studentName": "张三",
    "classId": 1,
    "className": "高一(1)班",
    "categoryId": 1,
    "categoryName": "教学质量",
    "isTeacherRelated": 1,
    "teacherId": 4,
    "teacherName": "张老师",
    "title": "希望增加课堂互动",
    "content": "张老师的数学课讲得很好，但是希望能增加一些课堂互动环节，让同学们更积极参与。",
    "isAnonymous": 0,
    "status": "pending",
    "createTime": "2024-03-11T10:00:00"
  },
  "timestamp": 1710158400000
}
```

#### 4.2 获取我的反馈列表

**请求**:
```
GET /feedback/my-list?pageNum=1&pageSize=10
Authorization: Bearer {token}
```

**响应**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "studentId": 1,
        "studentName": "张三",
        "classId": 1,
        "className": "高一(1)班",
        "categoryId": 1,
        "categoryName": "教学质量",
        "isTeacherRelated": 1,
        "teacherId": 4,
        "teacherName": "张老师",
        "title": "希望增加课堂互动",
        "content": "张老师的数学课讲得很好...",
        "isAnonymous": 0,
        "status": "pending",
        "createTime": "2024-03-11T10:00:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  },
  "timestamp": 1710158400000
}
```

#### 4.3 获取反馈详情

**请求**:
```
GET /feedback/{id}
Authorization: Bearer {token}
```

**响应**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "studentId": 1,
    "studentName": "张三",
    "classId": 1,
    "className": "高一(1)班",
    "categoryId": 1,
    "categoryName": "教学质量",
    "isTeacherRelated": 1,
    "teacherId": 4,
    "teacherName": "张老师",
    "title": "希望增加课堂互动",
    "content": "张老师的数学课讲得很好，但是希望能增加一些课堂互动环节，让同学们更积极参与。",
    "isAnonymous": 0,
    "status": "pending",
    "createTime": "2024-03-11T10:00:00"
  },
  "timestamp": 1710158400000
}
```

---

## 测试账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| 20240101 | 123456 | 学生 |
| admin1 | 123456 | 类别管理员 |
| sysadmin | 123456 | 系统管理员 |

---

## 注意事项

1. Token 有效期为 24 小时
2. 所有时间字段使用 ISO 8601 格式
3. 分页参数：pageNum（页码，从1开始）、pageSize（每页数量）
4. 密码使用 BCrypt 加密存储
5. 匿名反馈中，学生名称显示为"匿名用户"
