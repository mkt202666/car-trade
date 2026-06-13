package com.pancosky.cartradeadmin.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.jsonwebtoken.Claims;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JwtUtil 单元测试
 * 使用反射注入测试专用密钥，不依赖外部配置
 */
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() throws Exception {
        jwtUtil = new JwtUtil();
        setField(jwtUtil, "secret", "test-secret-key-for-unit-testing-must-be-at-least-32-chars");
        setField(jwtUtil, "expiration", 3600L);
        setField(jwtUtil, "refreshExpiration", 86400L);
        setField(jwtUtil, "issuer", "test-issuer");
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void testGenerateAndParseAccessToken() {
        String token = jwtUtil.generateToken(1L, "admin", "SUPER_ADMIN");
        Claims claims = jwtUtil.parseToken(token);

        assertNotNull(claims.getId()); // jti (UUID)
        assertEquals("admin", jwtUtil.getUsernameFromClaims(claims));
        assertEquals(1L, jwtUtil.getAdminIdFromClaims(claims));
        assertEquals("SUPER_ADMIN", jwtUtil.getRoleFromClaims(claims));
        assertEquals("ACCESS", jwtUtil.getTypeFromClaims(claims));
    }

    @Test
    void testGenerateAndParseRefreshToken() {
        String token = jwtUtil.generateRefreshToken(1L);
        Claims claims = jwtUtil.parseToken(token);

        assertEquals("REFRESH", jwtUtil.getTypeFromClaims(claims));
        assertEquals(1L, jwtUtil.getAdminIdFromClaims(claims));
    }

    @Test
    void testInvalidTokenThrowsException() {
        assertThrows(Exception.class, () -> jwtUtil.parseToken("invalid.jwt.token"));
    }

    @Test
    void testExpiredTokenThrowsException() throws Exception {
        setField(jwtUtil, "expiration", -1L); // 已过期
        String token = jwtUtil.generateToken(1L, "admin", "SUPER_ADMIN");
        assertThrows(Exception.class, () -> jwtUtil.parseToken(token));
    }

    @Test
    void testDifferentUsersHaveDifferentJti() {
        String token1 = jwtUtil.generateToken(1L, "admin1", "SUPER_ADMIN");
        String token2 = jwtUtil.generateToken(2L, "admin2", "OPERATOR");

        Claims claims1 = jwtUtil.parseToken(token1);
        Claims claims2 = jwtUtil.parseToken(token2);

        assertNotEquals(claims1.getId(), claims2.getId());
        assertEquals("admin1", jwtUtil.getUsernameFromClaims(claims1));
        assertEquals("admin2", jwtUtil.getUsernameFromClaims(claims2));
    }
}
