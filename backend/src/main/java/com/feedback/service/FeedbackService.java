package com.feedback.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.feedback.common.BusinessException;
import com.feedback.dto.SubmitFeedbackRequest;
import com.feedback.entity.Feedback;
import com.feedback.entity.User;
import com.feedback.mapper.FeedbackMapper;
import com.feedback.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 反馈服务
 *
 * @author Claude
 * @since 2026-03-11
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackMapper feedbackMapper;
    private final UserMapper userMapper;
    private final CategoryService categoryService;

    /**
     * 提交反馈
     */
    public Feedback submitFeedback(Long studentId, SubmitFeedbackRequest request) {
        // 获取学生信息
        User student = userMapper.selectById(studentId);
        if (student == null) {
            throw new BusinessException(404, "学生不存在");
        }

        // 验证类别是否存在
        if (categoryService.getCategoryById(request.getCategoryId()) == null) {
            throw new BusinessException(404, "反馈类别不存在");
        }

        // 如果选择了教师，验证教师是否存在
        if (request.getIsTeacherRelated() != null && request.getIsTeacherRelated() == 1) {
            if (request.getTeacherId() == null) {
                throw new BusinessException(400, "请选择任课教师");
            }
            User teacher = userMapper.selectById(request.getTeacherId());
            if (teacher == null) {
                throw new BusinessException(404, "教师不存在");
            }
        }

        // 创建反馈
        Feedback feedback = new Feedback();
        feedback.setStudentId(studentId);
        feedback.setStudentName(student.getRealName());
        feedback.setClassId(student.getClassId());
        feedback.setClassName(student.getClassName());
        feedback.setCategoryId(request.getCategoryId());
        feedback.setIsTeacherRelated(request.getIsTeacherRelated());
        feedback.setTeacherId(request.getTeacherId());
        feedback.setTitle(request.getTitle());
        feedback.setContent(request.getContent());
        feedback.setIsAnonymous(request.getIsAnonymous());
        feedback.setStatus("pending");
        feedback.setCreateTime(LocalDateTime.now());

        feedbackMapper.insert(feedback);
        log.info("反馈提交成功：学生ID={}, 反馈ID={}", studentId, feedback.getId());
        return feedback;
    }

    /**
     * 获取学生的反馈列表（分页）
     */
    public IPage<Feedback> getStudentFeedbacks(Long studentId, int pageNum, int pageSize) {
        LambdaQueryWrapper<Feedback> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Feedback::getStudentId, studentId)
                .orderByDesc(Feedback::getCreateTime);

        Page<Feedback> page = new Page<>(pageNum, pageSize);
        return feedbackMapper.selectPage(page, wrapper);
    }

    /**
     * 获取反馈详情
     */
    public Feedback getFeedbackDetail(Long feedbackId) {
        Feedback feedback = feedbackMapper.selectById(feedbackId);
        if (feedback == null) {
            throw new BusinessException(404, "反馈不存在");
        }
        return feedback;
    }

}
