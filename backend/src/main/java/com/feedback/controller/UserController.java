package com.feedback.controller;

import com.feedback.common.ApiResponse;
import com.feedback.entity.User;
import com.feedback.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户控制器
 *
 * @author Claude
 * @since 2026-03-11
 */
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public ApiResponse<User> getCurrentUser(@RequestHeader("X-User-Id") Long userId) {
        log.info("获取用户信息：用户ID={}", userId);
        User user = authService.getCurrentUser(userId);
        return ApiResponse.success(user);
    }

    /**
     * 获取教师列表
     */
    @GetMapping("/teachers")
    public ApiResponse<List<AuthService.TeacherDTO>> getTeachers() {
        log.info("获取教师列表");
        List<AuthService.TeacherDTO> teachers = authService.getTeacherList();
        return ApiResponse.success(teachers);
    }

    /**
     * 获取班级列表
     */
    @GetMapping("/classes")
    public ApiResponse<List<AuthService.ClassDTO>> getClasses() {
        log.info("获取班级列表");
        List<AuthService.ClassDTO> classes = authService.getClassList();
        return ApiResponse.success(classes);
    }

}
