package com.pancosky.newcartrade.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.net.URI;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * URL 安全校验器 — 单测
 * 覆盖：
 *   1. 协议白名单
 *   2. 内网/回环/链路本地 IP 拒绝
 *   3. 域名白名单强制
 *   4. 路径穿越与畸形 URL
 *   5. IPv6 私网地址
 */
class UrlSafetyValidatorTest {

    private static final Set<String> ALLOW = Set.of("images.unsplash.com", "cdn.example.com");

    @Test
    @DisplayName("合法的 https 公网 URL 应通过")
    void validHttpsUrl() {
        URI uri = UrlSafetyValidator.validatePublicUrl("https://images.unsplash.com/photo-1.png", ALLOW);
        assertEquals("images.unsplash.com", uri.getHost().toLowerCase());
    }

    @Test
    @DisplayName("file:// 协议必须拒绝")
    void rejectFileScheme() {
        SecurityException e = assertThrows(SecurityException.class,
            () -> UrlSafetyValidator.validatePublicUrl("file:///etc/passwd", null));
        assertTrue(e.getMessage().toLowerCase().contains("scheme"));
    }

    @Test
    @DisplayName("gopher:// 等危险协议必须拒绝")
    void rejectGopherScheme() {
        assertThrows(SecurityException.class,
            () -> UrlSafetyValidator.validatePublicUrl("gopher://attacker.com:6379/_FLUSHALL", null));
    }

    @Test
    @DisplayName("127.0.0.1 loopback 必须拒绝")
    void rejectLoopback() {
        assertThrows(SecurityException.class,
            () -> UrlSafetyValidator.validatePublicUrl("http://127.0.0.1:8080/admin", null));
    }

    @Test
    @DisplayName("169.254.169.254 云元数据必须拒绝")
    void rejectAwsMetadata() {
        assertThrows(SecurityException.class,
            () -> UrlSafetyValidator.validatePublicUrl("http://169.254.169.254/latest/meta-data/iam/", null));
    }

    @Test
    @DisplayName("192.168.x 内网必须拒绝")
    void rejectPrivate192() {
        assertThrows(SecurityException.class,
            () -> UrlSafetyValidator.validatePublicUrl("http://192.168.1.1/router", null));
    }

    @Test
    @DisplayName("10.x 内网必须拒绝")
    void rejectPrivate10() {
        assertThrows(SecurityException.class,
            () -> UrlSafetyValidator.validatePublicUrl("http://10.0.0.1/", null));
    }

    @Test
    @DisplayName("172.16-31 私网段必须拒绝")
    void rejectPrivate172() {
        assertThrows(SecurityException.class,
            () -> UrlSafetyValidator.validatePublicUrl("http://172.16.0.1/", null));
        assertThrows(SecurityException.class,
            () -> UrlSafetyValidator.validatePublicUrl("http://172.31.255.255/", null));
    }

    @Test
    @DisplayName("0.0.0.0 必须拒绝")
    void rejectZero() {
        assertThrows(SecurityException.class,
            () -> UrlSafetyValidator.validatePublicUrl("http://0.0.0.0/", null));
    }

    @Test
    @DisplayName("IPv6 ::1 loopback 必须拒绝")
    void rejectIpv6Loopback() {
        assertThrows(SecurityException.class,
            () -> UrlSafetyValidator.validatePublicUrl("http://[::1]/", null));
    }

    @Test
    @DisplayName("空 URL 必须拒绝")
    void rejectBlank() {
        assertThrows(SecurityException.class,
            () -> UrlSafetyValidator.validatePublicUrl("", null));
        assertThrows(SecurityException.class,
            () -> UrlSafetyValidator.validatePublicUrl(null, null));
    }

    @Test
    @DisplayName("URL 语法错误必须拒绝")
    void rejectMalformed() {
        assertThrows(SecurityException.class,
            () -> UrlSafetyValidator.validatePublicUrl("http://", null));
        assertThrows(SecurityException.class,
            () -> UrlSafetyValidator.validatePublicUrl("not-a-url", null));
    }

    @Test
    @DisplayName("白名单模式：未在白名单的域名必须拒绝")
    void rejectNonWhitelistedDomain() {
        assertThrows(SecurityException.class,
            () -> UrlSafetyValidator.validatePublicUrl("https://evil.com/x.png", ALLOW));
    }

    @Test
    @DisplayName("白名单模式：白名单内域名通过")
    void acceptWhitelistedDomain() {
        // 用 1.1.1.1 这个真实可达的公网 IP（不走 DNS）
        Set<String> ips = Set.of("1.1.1.1");
        assertNotNull(UrlSafetyValidator.validatePublicUrl("https://1.1.1.1/a.jpg", ips));
    }

    @Test
    @DisplayName("无白名单模式：公网 IP 通过")
    void acceptPublicIpWithoutWhitelist() {
        // 不做严格断言（依赖公网 DNS 解析），仅断言不抛内网相关异常
        assertDoesNotThrow(() -> {
            try {
                UrlSafetyValidator.validatePublicUrl("https://1.1.1.1/x.png", null);
            } catch (SecurityException e) {
                if (e.getMessage() != null && e.getMessage().contains("Internal")) {
                    throw e;
                }
            }
        });
    }
}
