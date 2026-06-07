package com.pancosky.newcartrade.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtil {

    /** 开发环境默认 key（至少 32 字节，符合 HS256 要求）；生产通过配置文件覆盖 */
    private static final String DEFAULT_SECRET = "5d-car-trade-default-secret-key-for-jwt-signing-please-change-in-prod";
    private static String secret = DEFAULT_SECRET;
    private static long expirationSeconds = 86400; // 默认 24h

    private static SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public static void setSecret(String s) {
        if (s != null && !s.isBlank()) JwtUtil.secret = s;
    }

    public static void setExpirationSeconds(long exp) {
        if (exp > 0) JwtUtil.expirationSeconds = exp;
    }

    public static String generateToken(Long userId) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expirationSeconds * 1000L);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(getSigningKey())
                .compact();
    }

    public static Long getUserId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Long.parseLong(claims.getSubject());
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
