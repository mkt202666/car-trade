package com.pancosky.cartradeadmin.config;

import com.pancosky.cartradeadmin.interceptor.AdminAuthInterceptor;
import com.pancosky.cartradeadmin.interceptor.RateLimitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AdminAuthInterceptor adminAuthInterceptor;

    @Autowired
    private RateLimitInterceptor rateLimitInterceptor;

    @Value("${api.prefix}")
    private String apiPrefix;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 限流拦截器 — 在鉴权之前，优先检查频率
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns(apiPrefix + "/**")
                .excludePathPatterns(
                        "/swagger-ui/**",
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/**"
                )
                .order(1);

        // 鉴权拦截器
        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns(apiPrefix + "/**")
                .excludePathPatterns(
                        apiPrefix + "/auth/login",
                        apiPrefix + "/auth/refresh",
                        apiPrefix + "/health",
                        "/swagger-ui/**",
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/**"
                )
                .order(2);
    }
}
