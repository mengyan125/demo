package com.feedback.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.feedback.common.ApiResponse;
import com.feedback.dto.FeedbackStatisticsDTO;
import com.feedback.dto.ReplyFeedbackRequest;
import com.feedback.entity.Feedback;
import com.feedback.entity.Reply;
import com.feedback.service.AdminFeedbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员反馈控制器
 *
 * @author Claude
 * @since 2026-03-11
 */
@Slf4j
@RestController
@RequestMapping("/admin/feedback")
@RequiredArgsConstructor
public class AdminFeedbackController {

    private final AdminFeedbackService adminFeedbackService;

    /**
     * 获取反馈列表（管理员）
     */
    @GetMapping("/list")
    public ApiResponse<IPage<Feedback>> getFeedbackList(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        log.info("获取反馈列表：状态={}, 类别ID={}", status, categoryId);
        IPage<Feedback> page = adminFeedbackService.getFeedbackList(status, categoryId, keyword, pageNum, pageSize);
        return ApiResponse.success(page);
    }

    /**
     * 获取教师的反馈列表
     */
    @GetMapping("/teacher/{teacherId}")
    public ApiResponse<IPage<Feedback>> getTeacherFeedbackList(
            @PathVariable Long teacherId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        log.info("获取教师反馈列表：教师ID={}, 页码={}, 页大小={}", teacherId, pageNum, pageSize);
        IPage<Feedback> page = adminFeedbackService.getTeacherFeedbackList(teacherId, pageNum, pageSize);
        return ApiResponse.success(page);
    }

    /**
     * 回复反馈
     */
    @PostMapping("/reply")
    public ApiResponse<Reply> replyFeedback(
            @RequestHeader("X-User-Id") Long adminId,
            @RequestHeader("X-Username") String adminName,
            @Validated @RequestBody ReplyFeedbackRequest request) {
        log.info("回复反馈：反馈ID={}, 管理员ID={}", request.getFeedbackId(), adminId);
        Reply reply = adminFeedbackService.replyFeedback(request.getFeedbackId(), adminId, adminName, request.getContent());
        return ApiResponse.success("回复成功", reply);
    }

    /**
     * 关闭反馈
     */
    @PostMapping("/{id}/close")
    public ApiResponse<Void> closeFeedback(@PathVariable Long id) {
        log.info("关闭反馈：反馈ID={}", id);
        adminFeedbackService.closeFeedback(id);
        return ApiResponse.success("反馈已关闭", null);
    }

    /**
     * 获取反馈统计
     */
    @GetMapping("/statistics")
    public ApiResponse<FeedbackStatisticsDTO> getStatistics() {
        log.info("获取反馈统计");
        FeedbackStatisticsDTO statistics = adminFeedbackService.getStatistics();
        return ApiResponse.success(statistics);
    }

}
