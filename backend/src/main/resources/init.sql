-- 学生反馈系统 - 数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS feedback_system DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE feedback_system;

-- 用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密）',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '电话',
    real_name VARCHAR(50) COMMENT '真实姓名',
    role VARCHAR(20) NOT NULL DEFAULT 'student' COMMENT '角色：student/teacher/admin',
    status INT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX idx_username (username),
    INDEX idx_role (role),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 反馈分类表
CREATE TABLE IF NOT EXISTS category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    name VARCHAR(100) NOT NULL COMMENT '分类名称',
    description VARCHAR(500) COMMENT '分类描述',
    sort INT DEFAULT 0 COMMENT '排序',
    status INT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='反馈分类表';

-- 反馈表
CREATE TABLE IF NOT EXISTS feedback (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '反馈ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    student_name VARCHAR(50) COMMENT '学生姓名',
    teacher_id BIGINT COMMENT '教师ID',
    teacher_name VARCHAR(50) COMMENT '教师姓名',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    category_name VARCHAR(100) COMMENT '分类名称',
    title VARCHAR(200) NOT NULL COMMENT '反馈标题',
    content LONGTEXT NOT NULL COMMENT '反馈内容',
    grade VARCHAR(20) COMMENT '年级',
    class VARCHAR(50) COMMENT '班级',
    subject VARCHAR(50) COMMENT '反馈对象',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending/processing/replied/closed',
    priority INT DEFAULT 0 COMMENT '优先级：0-低，1-中，2-高',
    attachment_url VARCHAR(500) COMMENT '附件URL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_student_id (student_id),
    INDEX idx_teacher_id (teacher_id),
    INDEX idx_category_id (category_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='反馈表';

-- 回复表
CREATE TABLE IF NOT EXISTS reply (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '回复ID',
    feedback_id BIGINT NOT NULL COMMENT '反馈ID',
    admin_id BIGINT NOT NULL COMMENT '管理员ID',
    admin_name VARCHAR(50) COMMENT '管理员姓名',
    content LONGTEXT NOT NULL COMMENT '回复内容',
    attachment_url VARCHAR(500) COMMENT '附件URL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_feedback_id (feedback_id),
    INDEX idx_admin_id (admin_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='回复表';

-- 收藏表
CREATE TABLE IF NOT EXISTS collection (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '收藏ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    feedback_id BIGINT NOT NULL COMMENT '反馈ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_user_feedback (user_id, feedback_id),
    INDEX idx_user_id (user_id),
    INDEX idx_feedback_id (feedback_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';

-- 初始化数据
-- 插入管理员用户
INSERT INTO user (username, password, email, real_name, role, status) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/1Pq', 'admin@example.com', '管理员', 'admin', 1),
('teacher1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/1Pq', 'teacher1@example.com', '张老师', 'teacher', 1),
('student1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/1Pq', 'student1@example.com', '小明', 'student', 1);

-- 插入分类
INSERT INTO category (name, description, sort, status) VALUES
('教学质量', '关于教学质量的反馈', 1, 1),
('课程安排', '关于课程安排的反馈', 2, 1),
('教师态度', '关于教师态度的反馈', 3, 1),
('设施设备', '关于设施设备的反馈', 4, 1),
('其他', '其他反馈', 5, 1);

-- 插入示例反馈
INSERT INTO feedback (student_id, student_name, teacher_id, teacher_name, category_id, category_name, title, content, grade, class, subject, status, priority) VALUES
(3, '小明', 2, '张老师', 1, '教学质量', '数学课讲解不清楚', '老师讲解的内容太快，没有给学生充分的理解时间', '高一', '1班', '数学', 'pending', 1),
(3, '小明', 2, '张老师', 2, '课程安排', '希望增加实践课程', '建议增加更多的实践课程，让学生能够更好地应用所学知识', '高一', '1班', '数学', 'replied', 0);

-- 创建索引
CREATE INDEX idx_feedback_status_created ON feedback(status, created_at);
CREATE INDEX idx_reply_feedback_created ON reply(feedback_id, created_at);
