package com.pancosky.cartradeadmin.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pancosky.cartradeadmin.annotation.RateLimit;
import com.pancosky.cartradeadmin.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

/**
 * 基于Redis ZSet滑动窗口的接口限流拦截器
 * 算法：每次请求先移除窗口外记录，统计窗口内数量，超过阈值则拒绝
 * 注意：当Redis不可用时，限流功能会自动降级（放行请求），不影响业务正常运行
 */
@Slf4j
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private static final String RATE_LIMIT_PREFIX = "ratelimit:";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RateLimit rateLimit = handlerMethod.getMethodAnnotation(RateLimit.class);
        if (rateLimit == null) {
            return true;
        }

        // 如果Redis不可用，直接放行（限流降级）
        try {
            return doRateLimit(request, response, rateLimit);
        } catch (RedisConnectionFailureException e) {
            log.warn("[RateLimit] Redis连接失败，限流功能降级，放行请求. URI: {}", request.getRequestURI(), e);
            return true; // 放行请求
        } catch (Exception e) {
            log.error("[RateLimit] 限流检查异常，放行请求. URI: {}", request.getRequestURI(), e);
            return true; // 发生异常时放行请求
        }
    }

    /**
     * 执行限流逻辑
     */
    private boolean doRateLimit(HttpServletRequest request, HttpServletResponse response, RateLimit rateLimit) throws Exception {
        String key = buildKey(request, rateLimit);
        long now = System.currentTimeMillis();
        long windowStart = now - rateLimit.window() * 1000L;

        // 1. 移除窗口外的过期记录
        redisTemplate.opsForZSet().removeRangeByScore(key, 0, windowStart);

        // 2. 统计窗口内当前请求数
        Long currentCount = redisTemplate.opsForZSet().count(key, windowStart, Long.MAX_VALUE);

        // 3. 超过阈值则拒绝
        if (currentCount != null && currentCount >= rateLimit.maxRequests()) {
            response.setStatus(429);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Retry-After", String.valueOf(rateLimit.window()));
            ApiResponse<Void> apiResponse = ApiResponse.error(429, rateLimit.message());
            objectMapper.writeValue(response.getWriter(), apiResponse);
            return false;
        }

        // 4. 允许通过，写入本次请求记录
        redisTemplate.opsForZSet().add(key, String.valueOf(now), now);

        // 5. 刷新过期时间
        redisTemplate.expire(key, rateLimit.window() + 10, TimeUnit.SECONDS);

        // 6. 写入限流响应头
        long remaining = Math.max(0, rateLimit.maxRequests() - (currentCount == null ? 0 : currentCount + 1));
        response.setHeader("X-RateLimit-Limit", String.valueOf(rateLimit.maxRequests()));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(remaining));
        response.setHeader("X-RateLimit-Window", String.valueOf(rateLimit.window()));

        return true;
    }

    private String buildKey(HttpServletRequest request, RateLimit rateLimit) {
        String dimension;
        if (rateLimit.limitType() == RateLimit.LimitType.USER) {
            Object adminId = request.getAttribute("ADMIN_ID");
            dimension = "user:" + (adminId != null ? adminId : "anonymous");
        } else {
            dimension = "ip:" + getClientIp(request);
        }
        String path = request.getRequestURI();
        return RATE_LIMIT_PREFIX + dimension + ":" + path;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip == null ? "0.0.0.0" : ip;
    }
}
