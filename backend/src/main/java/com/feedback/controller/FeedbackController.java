package com.feedback.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.feedback.common.ApiResponse;
import com.feedback.dto.SubmitFeedbackRequest;
import com.feedback.entity.Feedback;
import com.feedback.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 反馈控制器
 *
 * @author Claude
 * @since 2026-03-11
 */
@Slf4j
@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    /**
     * 提交反馈
     */
    @PostMapping("/submit")
    public ApiResponse<Feedback> submitFeedback(
            @RequestHeader("X-User-Id") Long userId,
            @Validated @RequestBody SubmitFeedbackRequest request) {
        log.info("提交反馈：用户ID={}", userId);
        Feedback feedback = feedbackService.submitFeedback(userId, request);
        return ApiResponse.success("反馈提交成功", feedback);
    }

    /**
     * 获取我的反馈列表
     */
    @GetMapping("/my-list")
    public ApiResponse<IPage<Feedback>> getMyFeedbacks(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        log.info("获取反馈列表：用户ID={}", userId);
        IPage<Feedback> page = feedbackService.getStudentFeedbacks(userId, pageNum, pageSize);
        return ApiResponse.success(page);
    }

    /**
     * 获取反馈详情
     */
    @GetMapping("/{id}")
    public ApiResponse<Feedback> getFeedbackDetail(@PathVariable Long id) {
        log.info("获取反馈详情：反馈ID={}", id);
        Feedback feedback = feedbackService.getFeedbackDetail(id);
        return ApiResponse.success(feedback);
    }

}
