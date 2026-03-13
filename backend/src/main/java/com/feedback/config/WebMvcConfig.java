package com.feedback.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 * 注册拦截器
 *
 * @author Claude
 * @since 2026-03-11
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtAuthenticationInterceptor jwtAuthenticationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthenticationInterceptor)
                // 排除登录接口
                .excludePathPatterns("/auth/login", "/auth/logout")
                // 排除健康检查
                .excludePathPatterns("/health")
                // 排除获取类别列表
                .excludePathPatterns("/category/list")
                // 对所有其他接口进行拦截
                .addPathPatterns("/**");
    }

}
