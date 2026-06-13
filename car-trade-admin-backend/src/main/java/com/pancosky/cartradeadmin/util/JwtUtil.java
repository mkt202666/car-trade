package com.pancosky.cartradeadmin.util;

import com.pancosky.cartradeadmin.common.BusinessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类（HS256）- 运营端专用
 * 
 * 安全要求：
 *   1. 签名密钥必须 >= 32 字节
 *   2. 禁止使用公开默认密钥
 *   3. 启动时立即验证，失败则阻止应用启动
 */
@Slf4j
@Component
public class JwtUtil {

    /** 已被识别为"公开默认密钥"的字符串集合 —— 一旦配置命中，必须 fail-fast */
    private static final String[] FORBIDDEN_SECRETS = new String[] {
        "5d-car-trade-admin-default-secret-key-please-change-in-prod-env-2026",
        "secret",
        "changeme",
        "jwt-secret"
    };

    private static final int MIN_SECRET_BYTES = 32;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    @Value("${jwt.issuer}")
    private String issuer;

    /**
     * 启动时验证 JWT 密钥安全性
     */
    @PostConstruct
    public void validateSecret() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT secret is empty. Set jwt.secret via JWT_SECRET environment variable.");
        }
        
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < MIN_SECRET_BYTES) {
            throw new IllegalStateException(
                "JWT secret too short: " + bytes.length + " bytes, need >= " + MIN_SECRET_BYTES + 
                ". Please set a strong random value via JWT_SECRET env var.");
        }
        
        for (String forbidden : FORBIDDEN_SECRETS) {
            if (forbidden.equals(secret)) {
                throw new IllegalStateException(
                    "JWT secret equals a well-known default. Set a strong random value via JWT_SECRET env var.");
            }
        }
        
        log.info("JWT secret validated successfully ({} bytes)", bytes.length);
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成访问Token
     */
    public String generateToken(Long adminId, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("adminId", adminId);
        claims.put("username", username);
        claims.put("role", role);
        claims.put("type", "ACCESS");

        Instant now = Instant.now();
        return Jwts.builder()
                .claims(claims)
                .id(java.util.UUID.randomUUID().toString())
                .subject(username)
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expiration)))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 生成刷新Token
     */
    public String generateRefreshToken(Long adminId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("adminId", adminId);
        claims.put("type", "REFRESH");

        Instant now = Instant.now();
        return Jwts.builder()
                .claims(claims)
                .id(java.util.UUID.randomUUID().toString())
                .subject(adminId.toString())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(refreshExpiration)))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 解析Token，失败抛出BusinessException(401)
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            throw new BusinessException(401, "无效的Token: " + e.getMessage());
        }
    }

    /**
     * 从Claims中获取adminId
     */
    public Long getAdminIdFromClaims(Claims claims) {
        return claims.get("adminId", Long.class);
    }

    /**
     * 从Claims中获取username
     */
    public String getUsernameFromClaims(Claims claims) {
        return claims.get("username", String.class);
    }

    /**
     * 从Claims中获取role
     */
    public String getRoleFromClaims(Claims claims) {
        return claims.get("role", String.class);
    }

    /**
     * 从Claims中获取token类型
     */
    public String getTypeFromClaims(Claims claims) {
        return claims.get("type", String.class);
    }
}
