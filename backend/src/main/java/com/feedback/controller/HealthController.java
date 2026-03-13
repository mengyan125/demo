package com.feedback.controller;

import com.feedback.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 *
 * @author Claude
 * @since 2026-03-11
 */
@Slf4j
@RestController
@RequestMapping("/health")
public class HealthController {

    /**
     * 健康检查
     */
    @GetMapping
    public ApiResponse<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("application", "student-feedback-system");
        data.put("version", "1.0.0");
        data.put("timestamp", System.currentTimeMillis());

        log.info("健康检查：{}", data);
        return ApiResponse.success("系统运行正常", data);
    }

}
