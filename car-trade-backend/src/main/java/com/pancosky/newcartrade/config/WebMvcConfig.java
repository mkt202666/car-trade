package com.pancosky.newcartrade.config;

import com.pancosky.newcartrade.interceptor.AuthenticationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthenticationInterceptor authenticationInterceptor;

    /**
     * CORS 白名单（生产环境必须通过 env: CORS_ALLOWED_ORIGINS 配置，逗号分隔）
     * 本地开发默认值：localhost:5173（uni-app vite dev server）
     */
    @Value("${cors.allowed-origins:http://localhost:5173,http://127.0.0.1:5173,http://localhost:8080}")
    private String allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] origins = allowedOrigins.split(",");
        // 去除空白
        for (int i = 0; i < origins.length; i++) {
            origins[i] = origins[i].trim();
        }
        registry.addMapping("/**")
                .allowedOriginPatterns(origins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("Authorization", "Content-Type", "X-Auth-Token", "X-Requested-With")
                .exposedHeaders("Authorization", "X-Auth-Token")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        // 匿名接口（无需登录）
                        "/api/v1/users/login",
                        "/api/v1/users/register",

                        // 车源浏览（仅公开 GET 接口，排除需要登录的路径）
                        "/api/v1/cars",                     // GET 列表
                        "/api/v1/cars/{id:[0-9]+}",         // GET 详情
                        "/api/v1/cars/recommend",           // GET 推荐
                        "/api/v1/cars/export",              // GET 出口
                        "/api/v1/cars/images/proxy",        // GET 图片代理
                        "/api/v1/cars/{id:[0-9]+}/images/{imageId:[0-9]+}/download", // GET 下载原图

                        // 求购浏览（仅列表/详情 GET）
                        "/api/v1/purchases",
                        "/api/v1/purchases/{id:[0-9]+}",

                        // AI 开放接口
                        "/api/v1/ai/chat",
                        "/api/v1/ai/search",
                        "/api/v1/ai/market-analysis",

                        // 公开查询数据
                        "/api/v1/coupons",
                        "/api/v1/membership/plans",

                        // 关注状态查询
                        "/api/v1/follows/**/check",

                        // 拍卖浏览
                        "/api/v1/auctions",
                        "/api/v1/auctions/{id:[0-9]+}",

                        // 城市数据
                        "/api/v1/cities",
                        "/api/v1/cities/**",

                        // 静态资源
                        "/uploads/**",
                        // 错误
                        "/error",
                        "/favicon.ico"
                );
    }
}
