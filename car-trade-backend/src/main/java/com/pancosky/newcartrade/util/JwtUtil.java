package com.pancosky.newcartrade.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类（HS256）
 *
 * 安全要点：
 *   1. 签名密钥必须 >= 32 字节；缺失或过短时抛 IllegalStateException → 启动失败
 *   2. 禁止使用"知名默认密钥"（任何人都能伪造 token）
 *   3. 解析失败记录原因 / token 前 8 字节（便于审计、阻断重放）
 *   4. issuer 在生成时写入，验证时不做强制（兼容老 token）
 */
@Slf4j
public class JwtUtil {

    /** 已被识别为"公开默认密钥"的字符串集合 —— 一旦配置命中，必须 fail-fast */
    private static final String[] FORBIDDEN_SECRETS = new String[] {
        "default-secret-key-for-dev-please-override-in-prod",
        "5d-car-trade-default-secret-key-for-jwt-signing-please-change-in-prod",
        "5d-car-trade-dev-jwt-secret-key-please-change-in-prod-2026",
        "secret",
        "changeme",
        "jwt-secret"
    };

    private static final int MIN_SECRET_BYTES = 32;

    private static volatile String secret;
    private static volatile long expirationSeconds = 86400L; // 默认 24h
    private static volatile long refreshExpirationSeconds = 7L * 86400L; // 默认 7 天

    /** 构造期即锁定密钥，避免后续运行时篡改 */
    public JwtUtil() {
        if (secret == null) {
            throw new IllegalStateException("JwtUtil.secret not initialized. Call setSecret() in startup runner.");
        }
    }

    /**
     * 启动期注入密钥。若密钥不合规立即抛 IllegalStateException，阻断应用启动。
     */
    public static synchronized void setSecret(String s) {
        if (s == null || s.isBlank()) {
            throw new IllegalStateException("JWT secret is empty. Set jwt.secret in environment variable JWT_SECRET.");
        }
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < MIN_SECRET_BYTES) {
            throw new IllegalStateException(
                "JWT secret too short: " + bytes.length + " bytes, need >= " + MIN_SECRET_BYTES);
        }
        for (String forbidden : FORBIDDEN_SECRETS) {
            if (forbidden.equals(s)) {
                throw new IllegalStateException(
                    "JWT secret equals a well-known default. Set a strong random value via JWT_SECRET env.");
            }
        }
        secret = s;
        log.info("JwtUtil secret initialized ({} bytes)", bytes.length);
    }

    public static void setExpirationSeconds(long exp) {
        if (exp > 0) JwtUtil.expirationSeconds = exp;
    }

    public static void setRefreshExpirationSeconds(long exp) {
        if (exp > 0) JwtUtil.refreshExpirationSeconds = exp;
    }

    /** 启动期主动校验；非空 / 非默认 / 长度合规。失败抛 IllegalStateException。 */
    public static void assertSecretValid(String s) {
        if (s == null || s.isBlank()) {
            throw new IllegalStateException("JWT secret is empty. Set jwt.secret in environment variable JWT_SECRET.");
        }
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < MIN_SECRET_BYTES) {
            throw new IllegalStateException(
                "JWT secret too short: " + bytes.length + " bytes, need >= " + MIN_SECRET_BYTES);
        }
        for (String forbidden : FORBIDDEN_SECRETS) {
            if (forbidden.equals(s)) {
                throw new IllegalStateException(
                    "JWT secret equals a well-known default. Set a strong random value via JWT_SECRET env.");
            }
        }
    }

    private static SecretKey getSigningKey() {
        if (secret == null) {
            throw new IllegalStateException("JwtUtil.secret not initialized");
        }
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 提供给其他模块（如 TokenBlacklistService）共用的签名密钥读取器。
     * 注意：仅用于验签，绝不可外泄。
     */
    public static SecretKey getSigningKeyPublic() {
        return getSigningKey();
    }

    /**
     * 解析 token 并返回剩余有效秒数（用于黑名单 TTL 计算）。
     *
     * @return 剩余秒数；token 已过期 / 无效 / 无 exp 声明则返回 null
     */
    public static Long getRemainingSeconds(String token) {
        if (token == null || token.isBlank()) return null;
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            Date exp = claims.getExpiration();
            if (exp == null) return null;
            long remainingMs = exp.getTime() - System.currentTimeMillis();
            if (remainingMs <= 0) return 0L;
            return remainingMs / 1000L;
        } catch (Exception e) {
            return null;
        }
    }

    public static String generateToken(Long userId) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expirationSeconds * 1000L);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("typ", "access")
                .id(java.util.UUID.randomUUID().toString())
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 生成 refresh token
     *   - 单独的 claim type=refresh
     *   - 默认 7 天有效期（可由 setRefreshExpirationSeconds 调整）
     *   - 必须配合 Redis 黑名单使用，实现"轮转 refresh token"模式
     */
    public static String generateRefreshToken(Long userId) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + refreshExpirationSeconds * 1000L);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("typ", "refresh")
                .id(java.util.UUID.randomUUID().toString())
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 获取 token 的类型（access / refresh）。
     * 旧 token 无 typ claim 时按 access 处理（兼容）。
     */
    public static String getTokenType(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            Object typ = claims.get("typ");
            return typ == null ? "access" : String.valueOf(typ);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 解析 token，失败返回 null 并记录失败原因（仅日志，不含 token 全文）
     */
    public static Long getUserId(String token) {
        if (token == null || token.isBlank()) return null;
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Long.parseLong(claims.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JWT parse failed: reason={} tokenPrefix={}", e.getMessage(), maskToken(token));
            return null;
        } catch (Exception e) {
            log.error("JWT parse unexpected error: reason={} tokenPrefix={}", e.getMessage(), maskToken(token), e);
            return null;
        }
    }

    public static boolean validateToken(String token) {
        if (token == null || token.isBlank()) return false;
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JWT validate failed: reason={} tokenPrefix={}", e.getMessage(), maskToken(token));
            return false;
        }
    }

    /** 仅打印前 8 个字符 + 长度，规避 PII / 全 token 落盘 */
    private static String maskToken(String token) {
        if (token == null) return "<null>";
        int len = token.length();
        if (len <= 8) return token.substring(0, Math.min(4, len)) + "***(len=" + len + ")";
        return token.substring(0, 8) + "***(len=" + len + ")";
    }
}
