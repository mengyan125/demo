package com.feedback.controller;

import com.feedback.common.ApiResponse;
import com.feedback.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 统计控制器
 *
 * @author Claude
 * @since 2026-03-12
 */
@Slf4j
@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * 获取教师详情
     */
    @GetMapping("/teacher/{teacherId}")
    public ApiResponse<StatisticsService.TeacherDetail> getTeacherDetail(@PathVariable Long teacherId) {
        log.info("获取教师详情：教师ID={}", teacherId);
        StatisticsService.TeacherDetail detail = statisticsService.getTeacherDetail(teacherId);
        return ApiResponse.success(detail);
    }
}
