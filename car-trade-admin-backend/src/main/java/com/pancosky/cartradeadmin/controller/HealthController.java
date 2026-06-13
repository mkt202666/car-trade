package com.pancosky.cartradeadmin.controller;

import com.pancosky.cartradeadmin.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 健康检查端点
 * 检查数据库连接、Redis连接和应用状态
 * 不需要鉴权（已在WebMvcConfig中排除）
 */
@RestController
public class HealthController {

    @Autowired(required = false)
    private DataSource dataSource;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${server.port}")
    private String serverPort;

    @GetMapping("/api/v1/admin/health")
    public Map<String, Object> health() {
        Map<String, Object> health = new LinkedHashMap<>();
        health.put("status", "UP");
        health.put("application", applicationName);
        health.put("port", serverPort);
        health.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        // 检查数据库连接
        Map<String, Object> dbCheck = checkDatabase();
        health.put("database", dbCheck);

        // 检查Redis连接
        Map<String, Object> redisCheck = checkRedis();
        health.put("redis", redisCheck);

        // 综合状态
        boolean allUp = "UP".equals(dbCheck.get("status")) && "UP".equals(redisCheck.get("status"));
        health.put("status", allUp ? "UP" : "DEGRADED");

        return health;
    }

    private Map<String, Object> checkDatabase() {
        Map<String, Object> db = new LinkedHashMap<>();
        if (dataSource == null) {
            db.put("status", "DOWN");
            db.put("error", "DataSource未配置");
            return db;
        }
        try (Connection conn = dataSource.getConnection()) {
            if (conn.isValid(3)) {
                db.put("status", "UP");
            } else {
                db.put("status", "DOWN");
            }
        } catch (Exception e) {
            db.put("status", "DOWN");
            db.put("error", e.getMessage());
        }
        return db;
    }

    private Map<String, Object> checkRedis() {
        Map<String, Object> redis = new LinkedHashMap<>();
        try {
            String result = redisTemplate.getConnectionFactory().getConnection().ping();
            redis.put("status", "PONG".equals(result) ? "UP" : "DOWN");
        } catch (Exception e) {
            redis.put("status", "DOWN");
            redis.put("error", e.getMessage());
        }
        return redis;
    }
}
