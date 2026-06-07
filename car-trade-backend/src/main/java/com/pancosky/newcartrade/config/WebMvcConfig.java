package com.pancosky.newcartrade.config;

import com.pancosky.newcartrade.interceptor.AuthenticationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthenticationInterceptor authenticationInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        // 匿名接口（无需登录）
                        "/api/v1/users/login",
                        "/api/v1/users/register",
                        "/api/v1/cars",
                        "/api/v1/cars/*",
                        "/api/v1/cars/recommend",
                        "/api/v1/cars/export",
                        // 错误与静态资源
                        "/error",
                        "/favicon.ico"
                );
    }
}
