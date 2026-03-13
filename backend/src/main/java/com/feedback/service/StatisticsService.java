package com.feedback.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.feedback.entity.Feedback;
import com.feedback.mapper.FeedbackMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 统计服务
 *
 * @author Claude
 * @since 2026-03-12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final FeedbackMapper feedbackMapper;

    /**
     * 获取教师详情
     */
    public TeacherDetail getTeacherDetail(Long teacherId) {
        // 获取该教师的反馈总数
        long feedbackCount = feedbackMapper.selectCount(
                new LambdaQueryWrapper<Feedback>().eq(Feedback::getTeacherId, teacherId)
        );

        TeacherDetail detail = new TeacherDetail();
        detail.setTeacherId(teacherId);
        detail.setName("教师 " + teacherId);
        detail.setFeedbackCount(feedbackCount);
        return detail;
    }

    /**
     * 教师详情
     */
    public static class TeacherDetail {
        public Long teacherId;
        public String name;
        public long feedbackCount;

        public Long getTeacherId() {
            return teacherId;
        }

        public void setTeacherId(Long teacherId) {
            this.teacherId = teacherId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getFeedbackCount() {
            return feedbackCount;
        }

        public void setFeedbackCount(long feedbackCount) {
            this.feedbackCount = feedbackCount;
        }
    }
}
