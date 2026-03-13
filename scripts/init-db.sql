-- ============================================
-- 学生反馈系统 - 数据库初始化脚本
-- 数据库: TiDB 8.5 (MySQL 兼容)
-- 字符集: utf8mb4
-- 创建日期: 2026-03-11
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS feedback_system
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE feedback_system;

-- ============================================
-- 1. 班级表
-- ============================================
DROP TABLE IF EXISTS tb_class;
CREATE TABLE tb_class (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    name VARCHAR(50) NOT NULL COMMENT '班级名称',
    grade VARCHAR(20) NOT NULL COMMENT '年级',
    class_no INT NOT NULL COMMENT '班号',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1=启用，0=禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_grade_class (grade, class_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='班级表';

-- ============================================
-- 2. 用户表
-- ============================================
DROP TABLE IF EXISTS tb_user;
CREATE TABLE tb_user (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    username VARCHAR(50) NOT NULL COMMENT '用户名（学号/工号）',
    password VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    role VARCHAR(20) NOT NULL COMMENT '角色：student/teacher/category_admin/dept_leader/school_leader/system_admin',
    class_id BIGINT DEFAULT NULL COMMENT '班级ID（学生专用）',
    class_name VARCHAR(50) DEFAULT NULL COMMENT '班级名称（冗余）',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1=启用，0=禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    KEY idx_role (role),
    KEY idx_class_id (class_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================
-- 3. 反馈类别表
-- ============================================
DROP TABLE IF EXISTS tb_category;
CREATE TABLE tb_category (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    name VARCHAR(50) NOT NULL COMMENT '类别名称',
    description VARCHAR(200) DEFAULT NULL COMMENT '类别描述',
    require_teacher TINYINT NOT NULL DEFAULT 0 COMMENT '是否关联任课教师：1=是，0=否',
    sort INT NOT NULL DEFAULT 0 COMMENT '排序（数字越小越靠前）',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1=启用，0=禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_sort (sort),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='反馈类别表';

-- ============================================
-- 4. 反馈表
-- ============================================
DROP TABLE IF EXISTS tb_feedback;
CREATE TABLE tb_feedback (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    student_name VARCHAR(50) NOT NULL COMMENT '学生姓名（冗余）',
    class_id BIGINT NOT NULL COMMENT '班级ID',
    class_name VARCHAR(50) NOT NULL COMMENT '班级名称（冗余）',
    category_id BIGINT NOT NULL COMMENT '类别ID',
    category_name VARCHAR(50) NOT NULL COMMENT '类别名称（冗余）',
    is_teacher_related TINYINT DEFAULT NULL COMMENT '是否与任课教师相关：1=是，0=否，NULL=该类别不关联教师',
    teacher_id BIGINT DEFAULT NULL COMMENT '教师ID（仅当is_teacher_related=1时有值）',
    teacher_name VARCHAR(50) DEFAULT NULL COMMENT '教师姓名（冗余）',
    title VARCHAR(100) NOT NULL COMMENT '反馈主题',
    content TEXT NOT NULL COMMENT '反馈内容',
    is_anonymous TINYINT NOT NULL DEFAULT 0 COMMENT '是否匿名：1=是，0=否',
    status VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态：pending=待处理，processing=处理中，replied=已回复，closed=已关闭',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_student_id (student_id),
    KEY idx_category_id (category_id),
    KEY idx_teacher_id (teacher_id),
    KEY idx_status (status),
    KEY idx_create_time (create_time),
    KEY idx_composite (category_id, status, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='反馈表';

-- ============================================
-- 5. 附件表
-- ============================================
DROP TABLE IF EXISTS tb_attachment;
CREATE TABLE tb_attachment (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    feedback_id BIGINT NOT NULL COMMENT '反馈ID',
    file_name VARCHAR(255) NOT NULL COMMENT '文件名',
    file_path VARCHAR(500) NOT NULL COMMENT '文件存储路径',
    file_size BIGINT NOT NULL COMMENT '文件大小（字节）',
    file_type VARCHAR(50) NOT NULL COMMENT '文件类型（MIME Type）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_feedback_id (feedback_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='附件表';

-- ============================================
-- 6. 回复表
-- ============================================
DROP TABLE IF EXISTS tb_reply;
CREATE TABLE tb_reply (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    feedback_id BIGINT NOT NULL COMMENT '反馈ID',
    admin_id BIGINT NOT NULL COMMENT '管理员ID',
    admin_name VARCHAR(50) NOT NULL COMMENT '管理员姓名（冗余）',
    content TEXT NOT NULL COMMENT '回复内容',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '回复时间',
    PRIMARY KEY (id),
    KEY idx_feedback_id (feedback_id),
    KEY idx_admin_id (admin_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='回复表';

-- ============================================
-- 7. 备注表
-- ============================================
DROP TABLE IF EXISTS tb_remark;
CREATE TABLE tb_remark (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    feedback_id BIGINT NOT NULL COMMENT '反馈ID',
    from_admin_id BIGINT NOT NULL COMMENT '发起人ID',
    from_admin_name VARCHAR(50) NOT NULL COMMENT '发起人姓名（冗余）',
    to_admin_id BIGINT DEFAULT NULL COMMENT '接收人ID（NULL表示所有管理员可见）',
    to_admin_name VARCHAR(50) DEFAULT NULL COMMENT '接收人姓名（冗余）',
    content TEXT NOT NULL COMMENT '备注内容',
    priority VARCHAR(20) NOT NULL DEFAULT 'normal' COMMENT '紧急程度：normal=普通，important=重要，urgent=紧急',
    is_read TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读：1=是，0=否',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_feedback_id (feedback_id),
    KEY idx_from_admin_id (from_admin_id),
    KEY idx_to_admin_id (to_admin_id),
    KEY idx_is_read (is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='备注表';

-- ============================================
-- 8. 权限配置表
-- ============================================
DROP TABLE IF EXISTS tb_permission;
CREATE TABLE tb_permission (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    admin_id BIGINT NOT NULL COMMENT '管理员ID',
    category_id BIGINT NOT NULL COMMENT '类别ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_admin_category (admin_id, category_id),
    KEY idx_admin_id (admin_id),
    KEY idx_category_id (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限配置表';

-- ============================================
-- 9. 学生-教师关联表
-- ============================================
DROP TABLE IF EXISTS tb_student_teacher;
CREATE TABLE tb_student_teacher (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    teacher_id BIGINT NOT NULL COMMENT '教师ID',
    semester VARCHAR(20) NOT NULL COMMENT '学期，如"2024-2025-1"',
    subject VARCHAR(50) NOT NULL COMMENT '科目',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_student_teacher_semester (student_id, teacher_id, semester),
    KEY idx_student_id (student_id),
    KEY idx_teacher_id (teacher_id),
    KEY idx_semester (semester)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生-教师关联表';

-- ============================================
-- 初始化数据
-- ============================================

-- 插入预置反馈类别
INSERT INTO tb_category (name, description, require_teacher, sort) VALUES
('教学质量', '对教师教学方法、课堂管理等方面的反馈', 1, 1),
('课堂纪律', '对课堂纪律、学习氛围等方面的反馈', 1, 2),
('教学设施', '对教室设备、实验室等教学设施的反馈', 0, 3),
('食堂服务', '对食堂饭菜质量、服务态度等方面的反馈', 0, 4),
('宿舍管理', '对宿舍环境、管理制度等方面的反馈', 0, 5),
('其他建议', '其他方面的意见和建议', 0, 6);

-- 插入测试班级
INSERT INTO tb_class (name, grade, class_no) VALUES
('高一(1)班', '高一', 1),
('高一(2)班', '高一', 2),
('高二(1)班', '高二', 1),
('高二(2)班', '高二', 2);

-- 插入测试用户（密码统一为 123456，BCrypt 加密后的值）
-- BCrypt 加密后的 "123456": $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi
INSERT INTO tb_user (username, password, real_name, role, class_id, class_name) VALUES
-- 学生
('20240101', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张三', 'student', 1, '高一(1)班'),
('20240102', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李四', 'student', 1, '高一(1)班'),
('20240103', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王五', 'student', 2, '高一(2)班'),
-- 教师
('T001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张老师', 'teacher', NULL, NULL),
('T002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李老师', 'teacher', NULL, NULL),
('T003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王老师', 'teacher', NULL, NULL),
-- 管理员
('admin1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '陈主任', 'category_admin', NULL, NULL),
('admin2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '刘主任', 'category_admin', NULL, NULL),
('sysadmin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 'system_admin', NULL, NULL);

-- 插入学生-教师关联关系（2024-2025学年第2学期）
INSERT INTO tb_student_teacher (student_id, teacher_id, semester, subject) VALUES
-- 张三的任课教师
(1, 4, '2024-2025-2', '数学'),
(1, 5, '2024-2025-2', '语文'),
(1, 6, '2024-2025-2', '英语'),
-- 李四的任课教师
(2, 4, '2024-2025-2', '数学'),
(2, 5, '2024-2025-2', '语文'),
(2, 6, '2024-2025-2', '英语'),
-- 王五的任课教师
(3, 4, '2024-2025-2', '数学'),
(3, 5, '2024-2025-2', '语文');

-- 插入权限配置（为类别管理员分配权限）
INSERT INTO tb_permission (admin_id, category_id) VALUES
-- 陈主任负责教学质量和课堂纪律
(7, 1),
(7, 2),
-- 刘主任负责教学设施、食堂服务、宿舍管理
(8, 3),
(8, 4),
(8, 5);

-- ============================================
-- 完成提示
-- ============================================
SELECT '数据库初始化完成！' AS message;
SELECT CONCAT('共创建 ', COUNT(*), ' 张表') AS table_count FROM information_schema.tables WHERE table_schema = 'feedback_system';
SELECT '预置数据插入完成！' AS message;
