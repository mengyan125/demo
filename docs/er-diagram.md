# 学生反馈系统 - 数据库 ER 图

## 实体关系图（Entity-Relationship Diagram）

```
┌─────────────────┐
│   tb_class      │
│  (班级表)        │
├─────────────────┤
│ PK id           │
│    name         │
│    grade        │
│    class_no     │
│    status       │
└────────┬────────┘
         │
         │ 1:N
         │
┌────────▼────────┐         ┌─────────────────┐
│   tb_user       │         │  tb_category    │
│  (用户表)        │         │  (反馈类别表)    │
├─────────────────┤         ├─────────────────┤
│ PK id           │         │ PK id           │
│    username     │         │    name         │
│    password     │         │    description  │
│    real_name    │         │    require_     │
│    role         │         │    teacher      │
│ FK class_id     │         │    sort         │
│    class_name   │         │    status       │
└────┬────┬───┬───┘         └────────┬────────┘
     │    │   │                      │
     │    │   │                      │ 1:N
     │    │   │                      │
     │    │   │             ┌────────▼────────┐
     │    │   │             │  tb_feedback    │
     │    │   │             │  (反馈表)        │
     │    │   │             ├─────────────────┤
     │    │   │             │ PK id           │
     │    │   └─────────────┤ FK student_id   │
     │    │                 │    student_name │
     │    │                 │ FK class_id     │
     │    │                 │    class_name   │
     │    │                 │ FK category_id  │
     │    │                 │    category_    │
     │    │                 │    name         │
     │    │                 │    is_teacher_  │
     │    │                 │    related      │
     │    └─────────────────┤ FK teacher_id   │
     │                      │    teacher_name │
     │                      │    title        │
     │                      │    content      │
     │                      │    is_anonymous │
     │                      │    status       │
     │                      └────┬────┬───┬───┘
     │                           │    │   │
     │                           │    │   │ 1:N
     │                           │    │   │
     │                      ┌────▼────┴───▼───┐
     │                      │  tb_attachment  │
     │                      │  (附件表)        │
     │                      ├─────────────────┤
     │                      │ PK id           │
     │                      │ FK feedback_id  │
     │                      │    file_name    │
     │                      │    file_path    │
     │                      │    file_size    │
     │                      │    file_type    │
     │                      └─────────────────┘
     │
     │ 1:N                  ┌─────────────────┐
     │                      │   tb_reply      │
     │                      │  (回复表)        │
     │                      ├─────────────────┤
     │                      │ PK id           │
     │                      │ FK feedback_id  │
     │                      │ FK admin_id     │
     │                      │    admin_name   │
     │                      │    content      │
     │                      └─────────────────┘
     │
     │ 1:N                  ┌─────────────────┐
     │                      │   tb_remark     │
     │                      │  (备注表)        │
     │                      ├─────────────────┤
     │                      │ PK id           │
     │                      │ FK feedback_id  │
     │                      │ FK from_admin_  │
     │                      │    id           │
     │                      │    from_admin_  │
     │                      │    name         │
     │                      │ FK to_admin_id  │
     │                      │    to_admin_    │
     │                      │    name         │
     │                      │    content      │
     │                      │    priority     │
     │                      │    is_read      │
     │                      └─────────────────┘
     │
     │ N:M                  ┌─────────────────┐
     └──────────────────────┤ tb_permission   │
                            │  (权限配置表)    │
                            ├─────────────────┤
                            │ PK id           │
                            │ FK admin_id     │
                            │ FK category_id  │
                            └─────────────────┘

┌─────────────────┐
│ tb_student_     │
│ teacher         │
│ (学生-教师       │
│  关联表)         │
├─────────────────┤
│ PK id           │
│ FK student_id   │
│ FK teacher_id   │
│    semester     │
│    subject      │
└─────────────────┘
```

## 关系说明

### 一对多关系 (1:N)

1. **tb_class → tb_user**
   - 一个班级有多个学生
   - 外键：tb_user.class_id → tb_class.id

2. **tb_user (student) → tb_feedback**
   - 一个学生可以提交多条反馈
   - 外键：tb_feedback.student_id → tb_user.id

3. **tb_user (teacher) → tb_feedback**
   - 一个教师可以被多条反馈提及
   - 外键：tb_feedback.teacher_id → tb_user.id

4. **tb_category → tb_feedback**
   - 一个类别下有多条反馈
   - 外键：tb_feedback.category_id → tb_category.id

5. **tb_feedback → tb_attachment**
   - 一条反馈可以有多个附件
   - 外键：tb_attachment.feedback_id → tb_feedback.id

6. **tb_feedback → tb_reply**
   - 一条反馈可以有多条回复
   - 外键：tb_reply.feedback_id → tb_feedback.id

7. **tb_feedback → tb_remark**
   - 一条反馈可以有多条备注
   - 外键：tb_remark.feedback_id → tb_feedback.id

8. **tb_user (admin) → tb_reply**
   - 一个管理员可以回复多条反馈
   - 外键：tb_reply.admin_id → tb_user.id

9. **tb_user (admin) → tb_remark**
   - 一个管理员可以添加多条备注
   - 外键：tb_remark.from_admin_id → tb_user.id
   - 外键：tb_remark.to_admin_id → tb_user.id

### 多对多关系 (N:M)

1. **tb_user (admin) ↔ tb_category**
   - 一个管理员可以管理多个类别
   - 一个类别可以分配给多个管理员
   - 中间表：tb_permission
   - 外键：tb_permission.admin_id → tb_user.id
   - 外键：tb_permission.category_id → tb_category.id

2. **tb_user (student) ↔ tb_user (teacher)**
   - 一个学生有多个任课教师
   - 一个教师教多个学生
   - 中间表：tb_student_teacher
   - 外键：tb_student_teacher.student_id → tb_user.id
   - 外键：tb_student_teacher.teacher_id → tb_user.id

## 核心业务流程

### 1. 学生提交反馈流程

```
1. 学生登录 (tb_user, role='student')
2. 选择反馈类别 (tb_category)
3. 如果类别 require_teacher=1:
   3.1 查询本学期任课教师 (tb_student_teacher)
   3.2 选择是否与教师相关
   3.3 如果相关，选择具体教师
4. 填写反馈内容
5. 上传附件（可选）(tb_attachment)
6. 提交反馈 (tb_feedback)
```

### 2. 管理员查看反馈流程

```
1. 管理员登录 (tb_user, role='category_admin')
2. 查询权限配置 (tb_permission)
3. 根据权限过滤反馈列表 (tb_feedback)
4. 查看反馈详情
5. 添加回复 (tb_reply)
6. 添加备注 (tb_remark)
7. 更新反馈状态 (tb_feedback.status)
```

### 3. 权限控制流程

```
1. 管理员请求查看反馈
2. 查询 tb_permission 表
3. 检查 admin_id 和 category_id 是否匹配
4. 如果匹配，允许查看
5. 如果不匹配，拒绝访问
6. 系统管理员 (role='system_admin') 默认拥有所有权限
```

## 数据冗余设计

为了提升查询性能，以下字段采用冗余设计：

1. **tb_user.class_name**
   - 冗余自 tb_class.name
   - 避免频繁 JOIN 查询

2. **tb_feedback.student_name**
   - 冗余自 tb_user.real_name
   - 避免频繁 JOIN 查询

3. **tb_feedback.class_name**
   - 冗余自 tb_class.name
   - 避免频繁 JOIN 查询

4. **tb_feedback.category_name**
   - 冗余自 tb_category.name
   - 避免频繁 JOIN 查询

5. **tb_feedback.teacher_name**
   - 冗余自 tb_user.real_name
   - 避免频繁 JOIN 查询

6. **tb_reply.admin_name**
   - 冗余自 tb_user.real_name
   - 避免频繁 JOIN 查询

7. **tb_remark.from_admin_name / to_admin_name**
   - 冗余自 tb_user.real_name
   - 避免频繁 JOIN 查询

**注意**：冗余字段需要在更新源数据时同步更新，确保数据一致性。

## 索引设计

### 主键索引
所有表的 id 字段都是主键，自动创建聚簇索引。

### 唯一索引
- tb_user.username
- tb_class (grade, class_no)
- tb_permission (admin_id, category_id)
- tb_student_teacher (student_id, teacher_id, semester)

### 普通索引
- tb_user: role, class_id
- tb_category: sort, status
- tb_feedback: student_id, category_id, teacher_id, status, create_time
- tb_feedback: (category_id, status, create_time) - 复合索引
- tb_attachment: feedback_id
- tb_reply: feedback_id, admin_id
- tb_remark: feedback_id, from_admin_id, to_admin_id, is_read
- tb_permission: admin_id, category_id
- tb_student_teacher: student_id, teacher_id, semester

## 数据完整性约束

### 业务层约束（应用层实现）

1. **用户角色约束**
   - student: 必须有 class_id
   - teacher: class_id 必须为 NULL
   - admin: class_id 必须为 NULL

2. **反馈约束**
   - 如果 category.require_teacher=1，则 is_teacher_related 不能为 NULL
   - 如果 is_teacher_related=1，则 teacher_id 不能为 NULL
   - 如果 category.require_teacher=0，则 is_teacher_related 必须为 NULL

3. **附件约束**
   - 每个反馈最多 9 个附件
   - 单个文件最大 2MB

4. **权限约束**
   - 系统管理员默认拥有所有类别权限
   - 类别管理员只能查看被授权的类别

5. **学生-教师关联约束**
   - 学生只能对本学期任课教师提出反馈
   - 每学期初更新关联数据

---

**文档版本**: 1.0.0
**创建日期**: 2026-03-11
**最后更新**: 2026-03-11
