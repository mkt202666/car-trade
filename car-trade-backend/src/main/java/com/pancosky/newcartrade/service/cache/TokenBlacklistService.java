package com.pancosky.newcartrade.service.cache;

import com.pancosky.newcartrade.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Token 黑名单服务（Redis）
 *
 * 用途：
 *   - 用户主动 logout：将当前 token 的 jti / 主体信息加入黑名单，TTL = token 剩余有效期
 *   - 拦截器在每次请求时检查：若 jti（或 token 哈希）在黑名单内 → 拒绝（401）
 *
 * Key 设计：
 *   - blacklist:token:{jti-or-hash} -> "1"
 *   - TTL = token 剩余秒数（最多不超过 token 实际 exp）
 *
 * 为什么不直接存原始 token？
 *   - 原始 token 是 PII，且会出现在日志/审计中
 *   - 使用 jti / sha256(token) 作为 key，结构更紧凑且不暴露 token
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    /** 黑名单 key 前缀 */
    public static final String KEY_PREFIX = "blacklist:token:";

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 计算 token 的"指纹"用于黑名单存储。
     * 优先用 jti（如签发时设置），否则用 SHA-256(token) 头 32 字符。
     * 这里因为历史 token 没有 jti，fallback 到 token 哈希头 32 字符。
     */
    public String fingerprint(String token) {
        if (token == null || token.isBlank()) return null;
        // 1. 尝试解析 claims.jti
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(JwtUtil.getSigningKeyPublic())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            Object jti = claims.get("jti");
            if (jti != null) {
                return "jti:" + jti;
            }
        } catch (Exception ignore) {
            // 解析失败不影响，fallback 到 hash
        }
        // 2. fallback: token 哈希
        return "h:" + sha256Short(token);
    }

    /**
     * 将 token 加入黑名单，TTL = 剩余有效期（秒）。已过期的 token 不需要拉黑。
     *
     * @return true 成功拉黑；false token 已过期 / 解析失败
     */
    public boolean blacklist(String token) {
        if (token == null || token.isBlank()) return false;
        Long remainingSeconds = JwtUtil.getRemainingSeconds(token);
        if (remainingSeconds == null || remainingSeconds <= 0) {
            log.info("[Blacklist] skip expired or invalid token");
            return false;
        }
        String fp = fingerprint(token);
        if (fp == null) return false;
        String key = KEY_PREFIX + fp;
        try {
            stringRedisTemplate.opsForValue().set(key, "1", Duration.ofSeconds(remainingSeconds));
            log.info("[Blacklist] token blacklisted, ttl={}s, fp={}", remainingSeconds, fp);
            return true;
        } catch (Exception e) {
            // Redis 不可用时降级：放行请求，但记录告警
            // 业务上 fail-open / fail-closed 需根据风险等级决定。当前选择 fail-open
            // （避免 Redis 抖动导致所有已登录用户被踢出）
            log.error("[Blacklist] Redis set failed, fail-open: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 检查 token 是否在黑名单中。
     *
     * @return true=已被拉黑（应拒绝），false=未被拉黑（放行）
     */
    public boolean isBlacklisted(String token) {
        if (token == null || token.isBlank()) return false;
        String fp = fingerprint(token);
        if (fp == null) return false;
        String key = KEY_PREFIX + fp;
        try {
            Boolean has = stringRedisTemplate.hasKey(key);
            return Boolean.TRUE.equals(has);
        } catch (Exception e) {
            // Redis 不可用时 fail-open（与 blacklist() 保持一致）
            log.warn("[Blacklist] Redis check failed, fail-open: {}", e.getMessage());
            return false;
        }
    }

    private static String sha256Short(String input) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(64);
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return Integer.toHexString(input.hashCode());
        }
    }
}
