-- ============================================
-- 学生反馈系统 - 测试数据脚本
-- 用于开发和测试环境
-- 创建日期: 2026-03-11
-- ============================================

USE feedback_system;

-- ============================================
-- 插入更多测试反馈数据
-- ============================================

-- 反馈1：针对教师的教学质量反馈（实名）
INSERT INTO tb_feedback (
    student_id, student_name, class_id, class_name,
    category_id, category_name, is_teacher_related, teacher_id, teacher_name,
    title, content, is_anonymous, status
) VALUES (
    1, '张三', 1, '高一(1)班',
    1, '教学质量', 1, 4, '张老师',
    '希望增加课堂互动', '张老师的数学课讲得很好，但是希望能增加一些课堂互动环节，让同学们更积极参与。', 0, 'pending'
);

-- 反馈2：针对教师的教学质量反馈（匿名）
INSERT INTO tb_feedback (
    student_id, student_name, class_id, class_name,
    category_id, category_name, is_teacher_related, teacher_id, teacher_name,
    title, content, is_anonymous, status
) VALUES (
    2, '李四', 1, '高一(1)班',
    1, '教学质量', 1, 5, '李老师',
    '语文课进度有点快', '李老师讲课很认真，但是进度有点快，有些同学跟不上。希望能适当放慢一些。', 1, 'replied'
);

-- 反馈3：教学质量类别的一般性反馈（不针对具体教师）
INSERT INTO tb_feedback (
    student_id, student_name, class_id, class_name,
    category_id, category_name, is_teacher_related, teacher_id, teacher_name,
    title, content, is_anonymous, status
) VALUES (
    1, '张三', 1, '高一(1)班',
    1, '教学质量', 0, NULL, NULL,
    '建议增加实验课', '希望学校能增加一些实验课程，让我们有更多动手实践的机会。', 0, 'processing'
);

-- 反馈4：食堂服务反馈（该类别不关联教师）
INSERT INTO tb_feedback (
    student_id, student_name, class_id, class_name,
    category_id, category_name, is_teacher_related, teacher_id, teacher_name,
    title, content, is_anonymous, status
) VALUES (
    3, '王五', 2, '高一(2)班',
    4, '食堂服务', NULL, NULL, NULL,
    '食堂饭菜种类太少', '希望食堂能增加一些菜品种类，现在每天都是差不多的菜。', 0, 'pending'
);

-- 反馈5：宿舍管理反馈
INSERT INTO tb_feedback (
    student_id, student_name, class_id, class_name,
    category_id, category_name, is_teacher_related, teacher_id, teacher_name,
    title, content, is_anonymous, status
) VALUES (
    2, '李四', 1, '高一(1)班',
    5, '宿舍管理', NULL, NULL, NULL,
    '宿舍热水供应时间太短', '希望能延长热水供应时间，现在晚上10点就停了，很多同学洗不上热水澡。', 1, 'replied'
);

-- 反馈6：教学设施反馈
INSERT INTO tb_feedback (
    student_id, student_name, class_id, class_name,
    category_id, category_name, is_teacher_related, teacher_id, teacher_name,
    title, content, is_anonymous, status
) VALUES (
    1, '张三', 1, '高一(1)班',
    3, '教学设施', NULL, NULL, NULL,
    '教室投影仪老化', '我们班的投影仪已经很旧了，经常看不清楚，希望能更换新的。', 0, 'closed'
);

-- 反馈7：课堂纪律反馈
INSERT INTO tb_feedback (
    student_id, student_name, class_id, class_name,
    category_id, category_name, is_teacher_related, teacher_id, teacher_name,
    title, content, is_anonymous, status
) VALUES (
    3, '王五', 2, '高一(2)班',
    2, '课堂纪律', 1, 6, '王老师',
    '希望加强课堂纪律管理', '英语课上有些同学总是讲话，影响其他同学听课，希望老师能加强管理。', 1, 'pending'
);

-- ============================================
-- 插入回复数据
-- ============================================

-- 回复反馈2
INSERT INTO tb_reply (feedback_id, admin_id, admin_name, content) VALUES
(2, 7, '陈主任', '感谢你的反馈。我们已经和李老师沟通过，她会适当调整教学进度，确保每位同学都能跟上。');

-- 回复反馈5
INSERT INTO tb_reply (feedback_id, admin_id, admin_name, content) VALUES
(5, 8, '刘主任', '感谢你的建议。我们已经和后勤部门协调，从下周开始，热水供应时间将延长到晚上11点。');

-- 回复反馈6（已关闭）
INSERT INTO tb_reply (feedback_id, admin_id, admin_name, content) VALUES
(6, 8, '刘主任', '感谢你的反馈。我们已经为你们班更换了新的投影仪，请确认是否正常使用。');

INSERT INTO tb_reply (feedback_id, admin_id, admin_name, content) VALUES
(6, 1, '张三', '已经更换了，新投影仪很清晰，谢谢！');

-- ============================================
-- 插入备注数据
-- ============================================

-- 陈主任给刘主任的备注
INSERT INTO tb_remark (feedback_id, from_admin_id, from_admin_name, to_admin_id, to_admin_name, content, priority, is_read) VALUES
(1, 7, '陈主任', 8, '刘主任', '这条反馈涉及教学方法改进，建议和教务处一起讨论。', 'important', 0);

-- 刘主任给所有管理员的备注
INSERT INTO tb_remark (feedback_id, from_admin_id, from_admin_name, to_admin_id, to_admin_name, content, priority, is_read) VALUES
(4, 8, '刘主任', NULL, NULL, '食堂反馈较多，需要重点关注。', 'urgent', 0);

-- ============================================
-- 插入附件数据（模拟）
-- ============================================

INSERT INTO tb_attachment (feedback_id, file_name, file_path, file_size, file_type) VALUES
(1, '课堂照片.jpg', '/uploads/2024/03/11/abc123.jpg', 524288, 'image/jpeg'),
(4, '食堂菜单.pdf', '/uploads/2024/03/11/def456.pdf', 1048576, 'application/pdf');

-- ============================================
-- 统计信息
-- ============================================

SELECT '测试数据插入完成！' AS message;
SELECT CONCAT('共插入 ', COUNT(*), ' 条反馈') AS feedback_count FROM tb_feedback;
SELECT CONCAT('共插入 ', COUNT(*), ' 条回复') AS reply_count FROM tb_reply;
SELECT CONCAT('共插入 ', COUNT(*), ' 条备注') AS remark_count FROM tb_remark;
SELECT CONCAT('共插入 ', COUNT(*), ' 个附件') AS attachment_count FROM tb_attachment;

-- ============================================
-- 查询示例
-- ============================================

-- 查询所有反馈（按创建时间倒序）
SELECT
    f.id,
    f.title,
    f.student_name,
    f.class_name,
    f.category_name,
    f.teacher_name,
    f.status,
    f.create_time
FROM tb_feedback f
ORDER BY f.create_time DESC;

-- 查询待处理的反馈
SELECT
    f.id,
    f.title,
    f.category_name,
    f.student_name,
    f.create_time
FROM tb_feedback f
WHERE f.status = 'pending'
ORDER BY f.create_time DESC;

-- 查询某个管理员有权限查看的反馈
SELECT
    f.id,
    f.title,
    f.category_name,
    f.student_name,
    f.status,
    f.create_time
FROM tb_feedback f
INNER JOIN tb_permission p ON f.category_id = p.category_id
WHERE p.admin_id = 7  -- 陈主任
ORDER BY f.create_time DESC;

-- 查询某个教师被反馈的次数
SELECT
    f.teacher_name,
    COUNT(*) AS feedback_count
FROM tb_feedback f
WHERE f.teacher_id IS NOT NULL
GROUP BY f.teacher_id, f.teacher_name
ORDER BY feedback_count DESC;

-- 查询各类别的反馈数量
SELECT
    c.name AS category_name,
    COUNT(f.id) AS feedback_count
FROM tb_category c
LEFT JOIN tb_feedback f ON c.id = f.category_id
GROUP BY c.id, c.name
ORDER BY feedback_count DESC;
