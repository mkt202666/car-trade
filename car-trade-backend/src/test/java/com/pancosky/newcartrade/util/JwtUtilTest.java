package com.pancosky.newcartrade.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JwtUtil 启动校验 / 解析失败日志 / 密钥强度 — 单测
 */
class JwtUtilTest {

    private static final String STRONG = "x9V7s5K2pQrU8jL1mN3bH6gF4dC0aZ9eR5tY7wI2oP3kM8qW1eR4tY6uI0oP5";

    @BeforeEach
    void reset() throws Exception {
        Field f = JwtUtil.class.getDeclaredField("secret");
        f.setAccessible(true);
        f.set(null, null);
    }

    @AfterEach
    void clear() throws Exception {
        Field f = JwtUtil.class.getDeclaredField("secret");
        f.setAccessible(true);
        f.set(null, null);
    }

    @Test
    @DisplayName("空密钥应抛 IllegalStateException")
    void emptySecret() {
        assertThrows(IllegalStateException.class, () -> JwtUtil.setSecret(""));
        assertThrows(IllegalStateException.class, () -> JwtUtil.setSecret(null));
    }

    @Test
    @DisplayName("过短密钥应抛 IllegalStateException")
    void tooShortSecret() {
        assertThrows(IllegalStateException.class, () -> JwtUtil.setSecret("short"));
    }

    @Test
    @DisplayName("知名默认密钥应抛 IllegalStateException")
    void forbiddenSecret() {
        assertThrows(IllegalStateException.class,
            () -> JwtUtil.setSecret("5d-car-trade-default-secret-key-for-jwt-signing-please-change-in-prod"));
        assertThrows(IllegalStateException.class, () -> JwtUtil.setSecret("secret"));
        assertThrows(IllegalStateException.class, () -> JwtUtil.setSecret("changeme"));
    }

    @Test
    @DisplayName("强密钥可注入并签发有效 token")
    void strongSecretWorks() {
        assertDoesNotThrow(() -> JwtUtil.setSecret(STRONG));
        String token = JwtUtil.generateToken(123L);
        assertNotNull(token);
        assertTrue(JwtUtil.validateToken(token));
        assertEquals(123L, JwtUtil.getUserId(token));
    }

    @Test
    @DisplayName("篡改后的 token 解析应返回 null")
    void tamperedTokenRejected() {
        JwtUtil.setSecret(STRONG);
        String token = JwtUtil.generateToken(100L);
        // 修改最后一字节
        String tampered = token.substring(0, token.length() - 1) + (token.charAt(token.length() - 1) == 'A' ? 'B' : 'A');
        assertNull(JwtUtil.getUserId(tampered));
        assertFalse(JwtUtil.validateToken(tampered));
    }

    @Test
    @DisplayName("空 token 解析应返回 null / false")
    void emptyToken() {
        JwtUtil.setSecret(STRONG);
        assertNull(JwtUtil.getUserId(null));
        assertNull(JwtUtil.getUserId(""));
        assertFalse(JwtUtil.validateToken(null));
        assertFalse(JwtUtil.validateToken(""));
    }

    @Test
    @DisplayName("assertSecretValid：与 setSecret 行为一致")
    void assertValid() {
        assertDoesNotThrow(() -> JwtUtil.assertSecretValid(STRONG));
        assertThrows(IllegalStateException.class, () -> JwtUtil.assertSecretValid(""));
        assertThrows(IllegalStateException.class, () -> JwtUtil.assertSecretValid(null));
        assertThrows(IllegalStateException.class, () -> JwtUtil.assertSecretValid("short"));
        assertThrows(IllegalStateException.class, () -> JwtUtil.assertSecretValid("secret"));
    }
}
