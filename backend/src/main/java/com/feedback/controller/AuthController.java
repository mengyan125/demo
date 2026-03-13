package com.feedback.controller;

import com.feedback.common.ApiResponse;
import com.feedback.dto.LoginRequest;
import com.feedback.dto.LoginResponse;
import com.feedback.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 *
 * @author Claude
 * @since 2026-03-11
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Validated @RequestBody LoginRequest request) {
        log.info("用户登录请求：{}", request.getUsername());
        LoginResponse response = authService.login(request);
        return ApiResponse.success("登录成功", response);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        log.info("用户登出");
        // JWT 是无状态的，登出只需要前端删除 Token
        return ApiResponse.success("登出成功", null);
    }

}
