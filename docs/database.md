# 学生反馈系统 - 数据库设计文档

## 一、数据库概述

**数据库类型**: TiDB 8.5（MySQL 兼容）
**字符集**: utf8mb4
**排序规则**: utf8mb4_unicode_ci
**时区**: Asia/Shanghai

## 二、表结构设计

### 2.1 用户表 (tb_user)

存储所有用户信息（学生、教师、管理员）

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | - | NO | AUTO_INCREMENT | 主键 |
| username | VARCHAR | 50 | NO | - | 用户名（学号/工号），唯一 |
| password | VARCHAR | 255 | NO | - | 密码（BCrypt 加密） |
| real_name | VARCHAR | 50 | NO | - | 真实姓名 |
| role | VARCHAR | 20 | NO | - | 角色：student/teacher/category_admin/dept_leader/school_leader/system_admin |
| class_id | BIGINT | - | YES | NULL | 班级ID（学生专用） |
| class_name | VARCHAR | 50 | YES | NULL | 班级名称（冗余字段，提升查询性能） |
| status | TINYINT | - | NO | 1 | 状态：1=启用，0=禁用 |
| create_time | DATETIME | - | NO | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | - | NO | CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

**索引**:
- PRIMARY KEY (`id`)
- UNIQUE KEY `uk_username` (`username`)
- KEY `idx_role` (`role`)
- KEY `idx_class_id` (`class_id`)

**说明**:
- role 字段支持多种角色，便于扩展
- class_name 冗余存储，避免频繁关联查询
- 密码使用 BCrypt 加密，安全性高

---

### 2.2 班级表 (tb_class)

存储班级信息

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | - | NO | AUTO_INCREMENT | 主键 |
| name | VARCHAR | 50 | NO | - | 班级名称，如"高一(1)班" |
| grade | VARCHAR | 20 | NO | - | 年级，如"高一" |
| class_no | INT | - | NO | - | 班号，如 1 |
| status | TINYINT | - | NO | 1 | 状态：1=启用，0=禁用 |
| create_time | DATETIME | - | NO | CURRENT_TIMESTAMP | 创建时间 |

**索引**:
- PRIMARY KEY (`id`)
- UNIQUE KEY `uk_grade_class` (`grade`, `class_no`)

---

### 2.3 反馈类别表 (tb_category)

存储反馈类别配置

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | - | NO | AUTO_INCREMENT | 主键 |
| name | VARCHAR | 50 | NO | - | 类别名称 |
| description | VARCHAR | 200 | YES | NULL | 类别描述 |
| require_teacher | TINYINT | - | NO | 0 | 是否关联任课教师：1=是，0=否 |
| sort | INT | - | NO | 0 | 排序（数字越小越靠前） |
| status | TINYINT | - | NO | 1 | 状态：1=启用，0=禁用 |
| create_time | DATETIME | - | NO | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | - | NO | CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

**索引**:
- PRIMARY KEY (`id`)
- KEY `idx_sort` (`sort`)
- KEY `idx_status` (`status`)

**预置数据**:
```sql
INSERT INTO tb_category (name, description, require_teacher, sort) VALUES
('教学质量', '对教师教学方法、课堂管理等方面的反馈', 1, 1),
('课堂纪律', '对课堂纪律、学习氛围等方面的反馈', 1, 2),
('教学设施', '对教室设备、实验室等教学设施的反馈', 0, 3),
('食堂服务', '对食堂饭菜质量、服务态度等方面的反馈', 0, 4),
('宿舍管理', '对宿舍环境、管理制度等方面的反馈', 0, 5),
('其他建议', '其他方面的意见和建议', 0, 6);
```

---

### 2.4 反馈表 (tb_feedback)

存储学生提交的反馈信息

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | - | NO | AUTO_INCREMENT | 主键 |
| student_id | BIGINT | - | NO | - | 学生ID |
| student_name | VARCHAR | 50 | NO | - | 学生姓名（冗余） |
| class_id | BIGINT | - | NO | - | 班级ID |
| class_name | VARCHAR | 50 | NO | - | 班级名称（冗余） |
| category_id | BIGINT | - | NO | - | 类别ID |
| category_name | VARCHAR | 50 | NO | - | 类别名称（冗余） |
| is_teacher_related | TINYINT | - | YES | NULL | 是否与任课教师相关：1=是，0=否，NULL=该类别不关联教师 |
| teacher_id | BIGINT | - | YES | NULL | 教师ID（仅当is_teacher_related=1时有值） |
| teacher_name | VARCHAR | 50 | YES | NULL | 教师姓名（冗余） |
| title | VARCHAR | 100 | NO | - | 反馈主题 |
| content | TEXT | - | NO | - | 反馈内容 |
| is_anonymous | TINYINT | - | NO | 0 | 是否匿名：1=是，0=否 |
| status | VARCHAR | 20 | NO | 'pending' | 状态：pending=待处理，processing=处理中，replied=已回复，closed=已关闭 |
| create_time | DATETIME | - | NO | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | - | NO | CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

**索引**:
- PRIMARY KEY (`id`)
- KEY `idx_student_id` (`student_id`)
- KEY `idx_category_id` (`category_id`)
- KEY `idx_teacher_id` (`teacher_id`)
- KEY `idx_status` (`status`)
- KEY `idx_create_time` (`create_time`)
- KEY `idx_composite` (`category_id`, `status`, `create_time`)

**说明**:
- 冗余存储学生姓名、班级名称、类别名称、教师姓名，提升查询性能
- is_teacher_related 字段为 NULL 表示该类别不关联教师
- 复合索引 idx_composite 用于优化管理员查询反馈列表

---

### 2.5 附件表 (tb_attachment)

存储反馈附件信息

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | - | NO | AUTO_INCREMENT | 主键 |
| feedback_id | BIGINT | - | NO | - | 反馈ID |
| file_name | VARCHAR | 255 | NO | - | 文件名 |
| file_path | VARCHAR | 500 | NO | - | 文件存储路径 |
| file_size | BIGINT | - | NO | - | 文件大小（字节） |
| file_type | VARCHAR | 50 | NO | - | 文件类型（MIME Type） |
| create_time | DATETIME | - | NO | CURRENT_TIMESTAMP | 创建时间 |

**索引**:
- PRIMARY KEY (`id`)
- KEY `idx_feedback_id` (`feedback_id`)

**说明**:
- 每个反馈最多 9 个附件（业务层控制）
- 单个文件最大 2MB（业务层控制）
- 支持图片、文档等多种格式

---

### 2.6 回复表 (tb_reply)

存储管理员对反馈的回复

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | - | NO | AUTO_INCREMENT | 主键 |
| feedback_id | BIGINT | - | NO | - | 反馈ID |
| admin_id | BIGINT | - | NO | - | 管理员ID |
| admin_name | VARCHAR | 50 | NO | - | 管理员姓名（冗余） |
| content | TEXT | - | NO | - | 回复内容 |
| create_time | DATETIME | - | NO | CURRENT_TIMESTAMP | 回复时间 |

**索引**:
- PRIMARY KEY (`id`)
- KEY `idx_feedback_id` (`feedback_id`)
- KEY `idx_admin_id` (`admin_id`)

**说明**:
- 支持多次回复
- 回复后学生可见
- 冗余存储管理员姓名，避免频繁关联查询

---

### 2.7 备注表 (tb_remark)

存储管理员之间的内部备注（学生不可见）

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | - | NO | AUTO_INCREMENT | 主键 |
| feedback_id | BIGINT | - | NO | - | 反馈ID |
| from_admin_id | BIGINT | - | NO | - | 发起人ID |
| from_admin_name | VARCHAR | 50 | NO | - | 发起人姓名（冗余） |
| to_admin_id | BIGINT | - | YES | NULL | 接收人ID（NULL表示所有管理员可见） |
| to_admin_name | VARCHAR | 50 | YES | NULL | 接收人姓名（冗余） |
| content | TEXT | - | NO | - | 备注内容 |
| priority | VARCHAR | 20 | NO | 'normal' | 紧急程度：normal=普通，important=重要，urgent=紧急 |
| is_read | TINYINT | - | NO | 0 | 是否已读：1=是，0=否 |
| create_time | DATETIME | - | NO | CURRENT_TIMESTAMP | 创建时间 |

**索引**:
- PRIMARY KEY (`id`)
- KEY `idx_feedback_id` (`feedback_id`)
- KEY `idx_from_admin_id` (`from_admin_id`)
- KEY `idx_to_admin_id` (`to_admin_id`)
- KEY `idx_is_read` (`is_read`)

**说明**:
- 备注仅管理员可见，学生无法查看
- to_admin_id 为 NULL 表示所有管理员可见
- 支持标记已读/未读

---

### 2.8 权限配置表 (tb_permission)

存储管理员的类别权限配置

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | - | NO | AUTO_INCREMENT | 主键 |
| admin_id | BIGINT | - | NO | - | 管理员ID |
| category_id | BIGINT | - | NO | - | 类别ID |
| create_time | DATETIME | - | NO | CURRENT_TIMESTAMP | 创建时间 |

**索引**:
- PRIMARY KEY (`id`)
- UNIQUE KEY `uk_admin_category` (`admin_id`, `category_id`)
- KEY `idx_admin_id` (`admin_id`)
- KEY `idx_category_id` (`category_id`)

**说明**:
- 一个管理员可以管理多个类别
- 一个类别可以分配给多个管理员
- 系统管理员默认拥有所有类别权限（业务层控制）

---

### 2.9 学生-教师关联表 (tb_student_teacher)

存储学生与任课教师的关联关系

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | - | NO | AUTO_INCREMENT | 主键 |
| student_id | BIGINT | - | NO | - | 学生ID |
| teacher_id | BIGINT | - | NO | - | 教师ID |
| semester | VARCHAR | 20 | NO | - | 学期，如"2024-2025-1" |
| subject | VARCHAR | 50 | NO | - | 科目 |
| create_time | DATETIME | - | NO | CURRENT_TIMESTAMP | 创建时间 |

**索引**:
- PRIMARY KEY (`id`)
- UNIQUE KEY `uk_student_teacher_semester` (`student_id`, `teacher_id`, `semester`)
- KEY `idx_student_id` (`student_id`)
- KEY `idx_teacher_id` (`teacher_id`)
- KEY `idx_semester` (`semester`)

**说明**:
- 每学期初从教务系统同步数据
- 用于验证学生是否可以对某教师提出反馈
- 支持一个学生多个教师，一个教师多个学生

---

## 三、外键关系

由于 TiDB 对外键支持有限，建议不使用物理外键，改用应用层控制数据一致性。

**逻辑外键关系**:
- tb_feedback.student_id → tb_user.id
- tb_feedback.category_id → tb_category.id
- tb_feedback.teacher_id → tb_user.id (role='teacher')
- tb_feedback.class_id → tb_class.id
- tb_attachment.feedback_id → tb_feedback.id
- tb_reply.feedback_id → tb_feedback.id
- tb_reply.admin_id → tb_user.id
- tb_remark.feedback_id → tb_feedback.id
- tb_remark.from_admin_id → tb_user.id
- tb_remark.to_admin_id → tb_user.id
- tb_permission.admin_id → tb_user.id
- tb_permission.category_id → tb_category.id
- tb_student_teacher.student_id → tb_user.id
- tb_student_teacher.teacher_id → tb_user.id

---

## 四、数据库连接配置

### 4.1 连接信息
- **主机**: xxxxx
- **端口**: xxxx
- **用户名**: xxxx
- **密码**: xxxxx
- **数据库**: feedback_system

### 4.2 代理配置
由于 TiDB 需要通过 socks 代理连接，Spring Boot 配置如下：

```yaml
spring:
  datasource:
    url: jdbc:mysql://xxxxx:xxxx/feedback_system?useSSL=true&serverTimezone=Asia/Shanghai&characterEncoding=utf8mb4
    username: xxxx
    password: xxxxx
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

**代理设置**:
在 JVM 启动参数中添加：
```
-DsocksProxyHost=127.0.0.1 -DsocksProxyPort=51080
```

---

## 五、性能优化建议

### 5.1 索引优化
- 为高频查询字段添加索引
- 使用复合索引优化多条件查询
- 定期分析慢查询日志，优化索引

### 5.2 分区策略
对于数据量大的表（如 tb_feedback），可以考虑按时间分区：
```sql
ALTER TABLE tb_feedback PARTITION BY RANGE (YEAR(create_time)) (
    PARTITION p2024 VALUES LESS THAN (2025),
    PARTITION p2025 VALUES LESS THAN (2026),
    PARTITION p2026 VALUES LESS THAN (2027),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);
```

### 5.3 缓存策略
- 使用 Redis 缓存热点数据（类别列表、权限配置）
- 使用 JetCache 缓存查询结果
- 缓存过期时间：5-10 分钟

### 5.4 查询优化
- 避免 SELECT *，只查询需要的字段
- 使用冗余字段减少 JOIN 操作
- 分页查询使用 LIMIT + OFFSET
- 统计查询使用定时任务预计算

---

## 六、数据安全

### 6.1 敏感数据加密
- 密码使用 BCrypt 加密（强度 10）
- 敏感字段（如反馈内容）可考虑加密存储

### 6.2 数据备份
- 每天凌晨 2:00 自动备份
- 保留最近 30 天的备份
- 定期测试备份恢复

### 6.3 审计日志
- 记录所有数据修改操作
- 记录管理员查看敏感数据的操作
- 日志保留 1 年

---

## 七、数据迁移

### 7.1 初始化脚本
参见 `scripts/init-db.sql`

### 7.2 测试数据
参见 `scripts/test-data.sql`

### 7.3 迁移步骤
1. 连接 TiDB 数据库（通过 socks 代理）
2. 创建数据库：`CREATE DATABASE feedback_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;`
3. 执行初始化脚本：`source scripts/init-db.sql;`
4. 执行测试数据脚本（可选）：`source scripts/test-data.sql;`
5. 验证表结构：`SHOW TABLES;`
6. 验证数据：`SELECT COUNT(*) FROM tb_user;`

---

**文档版本**: 1.0.0
**创建日期**: 2026-03-11
**最后更新**: 2026-03-11
