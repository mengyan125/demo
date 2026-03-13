package com.feedback.config;

import com.feedback.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JWT 认证拦截器
 *
 * @author Claude
 * @since 2026-03-11
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求头中的 Token
        String authHeader = request.getHeader(jwtProperties.getHeader());

        // 如果没有 Token，放行（由具体接口决定是否需要认证）
        if (authHeader == null || authHeader.isEmpty()) {
            return true;
        }

        // 移除 Bearer 前缀
        String token = authHeader.replace(jwtProperties.getPrefix() + " ", "");

        // 验证 Token
        if (!jwtUtil.validateToken(token)) {
            log.warn("Token 验证失败");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"code\": 401, \"message\": \"Token 无效或已过期\"}");
            return false;
        }

        // 从 Token 中获取用户信息，存储到请求头中
        Long userId = jwtUtil.getUserIdFromToken(token);
        String username = jwtUtil.getUsernameFromToken(token);

        request.setAttribute("userId", userId);
        request.setAttribute("username", username);

        // 添加到请求头中，方便控制器获取
        request.setAttribute("X-User-Id", userId);
        request.setAttribute("X-Username", username);

        log.debug("Token 验证成功：用户ID={}, 用户名={}", userId, username);
        return true;
    }

}
