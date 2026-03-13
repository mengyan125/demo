package com.feedback.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.feedback.common.BusinessException;
import com.feedback.dto.FeedbackStatisticsDTO;
import com.feedback.entity.Feedback;
import com.feedback.entity.Reply;
import com.feedback.enums.FeedbackStatusEnum;
import com.feedback.mapper.FeedbackMapper;
import com.feedback.mapper.ReplyMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 管理员反馈服务
 *
 * @author Claude
 * @since 2026-03-11
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminFeedbackService {

    private final FeedbackMapper feedbackMapper;
    private final ReplyMapper replyMapper;

    /**
     * 获取反馈列表（管理员）
     */
    public IPage<Feedback> getFeedbackList(String status, Long categoryId, String keyword, int pageNum, int pageSize) {
        LambdaQueryWrapper<Feedback> wrapper = new LambdaQueryWrapper<>();

        // 按状态筛选
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Feedback::getStatus, status);
        }

        // 按类别筛选
        if (categoryId != null) {
            wrapper.eq(Feedback::getCategoryId, categoryId);
        }

        // 按关键词搜索（标题或内容）
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Feedback::getTitle, keyword).or().like(Feedback::getContent, keyword));
        }

        wrapper.orderByDesc(Feedback::getCreateTime);

        Page<Feedback> page = new Page<>(pageNum, pageSize);
        return feedbackMapper.selectPage(page, wrapper);
    }

    /**
     * 获取教师的反馈列表
     */
    public IPage<Feedback> getTeacherFeedbackList(Long teacherId, int pageNum, int pageSize) {
        LambdaQueryWrapper<Feedback> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Feedback::getTeacherId, teacherId);
        wrapper.orderByDesc(Feedback::getCreateTime);

        Page<Feedback> page = new Page<>(pageNum, pageSize);
        return feedbackMapper.selectPage(page, wrapper);
    }

    /**
     * 回复反馈
     */
    public Reply replyFeedback(Long feedbackId, Long adminId, String adminName, String content) {
        // 验证反馈是否存在
        Feedback feedback = feedbackMapper.selectById(feedbackId);
        if (feedback == null) {
            throw new BusinessException(404, "反馈不存在");
        }

        // 创建回复
        Reply reply = new Reply();
        reply.setFeedbackId(feedbackId);
        reply.setAdminId(adminId);
        reply.setAdminName(adminName);
        reply.setContent(content);
        reply.setCreateTime(LocalDateTime.now());

        replyMapper.insert(reply);

        // 更新反馈状态为已回复
        feedback.setStatus(FeedbackStatusEnum.REPLIED.getValue());
        feedback.setUpdateTime(LocalDateTime.now());
        feedbackMapper.updateById(feedback);

        log.info("反馈回复成功：反馈ID={}, 管理员ID={}", feedbackId, adminId);
        return reply;
    }

    /**
     * 关闭反馈
     */
    public void closeFeedback(Long feedbackId) {
        Feedback feedback = feedbackMapper.selectById(feedbackId);
        if (feedback == null) {
            throw new BusinessException(404, "反馈不存在");
        }

        feedback.setStatus(FeedbackStatusEnum.CLOSED.getValue());
        feedback.setUpdateTime(LocalDateTime.now());
        feedbackMapper.updateById(feedback);

        log.info("反馈已关闭：反馈ID={}", feedbackId);
    }

    /**
     * 获取反馈统计
     */
    public FeedbackStatisticsDTO getStatistics() {
        // 获取各状态的反馈数量
        long pendingCount = feedbackMapper.selectCount(
                new LambdaQueryWrapper<Feedback>().eq(Feedback::getStatus, FeedbackStatusEnum.PENDING.getValue())
        );
        long processingCount = feedbackMapper.selectCount(
                new LambdaQueryWrapper<Feedback>().eq(Feedback::getStatus, FeedbackStatusEnum.PROCESSING.getValue())
        );
        long repliedCount = feedbackMapper.selectCount(
                new LambdaQueryWrapper<Feedback>().eq(Feedback::getStatus, FeedbackStatusEnum.REPLIED.getValue())
        );
        long closedCount = feedbackMapper.selectCount(
                new LambdaQueryWrapper<Feedback>().eq(Feedback::getStatus, FeedbackStatusEnum.CLOSED.getValue())
        );
        long totalCount = feedbackMapper.selectCount(null);

        return new FeedbackStatisticsDTO(pendingCount, processingCount, repliedCount, closedCount, totalCount);
    }

}
