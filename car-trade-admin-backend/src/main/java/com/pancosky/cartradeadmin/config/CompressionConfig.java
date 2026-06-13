package com.pancosky.cartradeadmin.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * HTTP缓存头过滤器
 * - API响应：no-store（防止浏览器缓存敏感数据）
 * - 静态资源：public + max-age（允许长期缓存）
 *
 * GZIP压缩由Spring Boot内置支持（server.compression.enabled=true）
 */
@Configuration
public class CompressionConfig {

    private static final Set<String> STATIC_PATH_PREFIXES = new HashSet<>(Arrays.asList(
            "/swagger-ui",
            "/webjars"
    ));

    @Bean
    public FilterRegistrationBean<OncePerRequestFilter> cacheHeaderFilter() {
        FilterRegistrationBean<OncePerRequestFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain filterChain) throws ServletException, IOException {
                filterChain.doFilter(request, response);

                String uri = request.getRequestURI();
                if (STATIC_PATH_PREFIXES.stream().anyMatch(uri::startsWith)) {
                    response.setHeader("Cache-Control", "public, max-age=86400");
                } else if (uri.startsWith("/api/")) {
                    response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
                    response.setHeader("Pragma", "no-cache");
                }
            }
        });
        registration.addUrlPatterns("/*");
        registration.setName("cacheHeaderFilter");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }
}
