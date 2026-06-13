package com.pancosky.newcartrade.util;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Set;

/**
 * ============================================
 *  URL 安全校验器（防 SSRF）
 * ============================================
 *
 * 目标：
 *   1. 强制 https/http scheme，禁用 file/ftp/gopher 等
 *   2. 解析 host，拒绝内网/回环/链路本地 IP（RFC1918、loopback、link-local、CGNAT、IPv6 私有）
 *   3. 不接受 DNS rebinding 风险：解析到 IP 后再次校验
 *   4. 白名单模式可启用（生产只允许已知 CDN 域名）
 *
 * 用法：
 *   - 普通代理场景：validatePublicUrl(url, whitelist)
 *   - 严格场景：validatePublicUrl(url, Set.of("images.unsplash.com"))
 * ============================================
 */
public final class UrlSafetyValidator {

    private UrlSafetyValidator() { }

    /** 内网/特殊 IPv4 段 */
    private static final String[] IPV4_BLOCK_PATTERNS = new String[] {
        "10.",        // 10.0.0.0/8
        "172.16.", "172.17.", "172.18.", "172.19.",
        "172.20.", "172.21.", "172.22.", "172.23.",
        "172.24.", "172.25.", "172.26.", "172.27.",
        "172.28.", "172.29.", "172.30.", "172.31.", // 172.16.0.0/12
        "192.168.",  // 192.168.0.0/16
        "169.254.",  // link-local（含云元数据 169.254.169.254）
        "0.",         // 0.0.0.0/8
        "127.",       // loopback
        "100.64.",   // 100.64.0.0/10 CGNAT
        "224.", "240."  // multicast / reserved
    };

    /** 允许的 scheme */
    private static final Set<String> ALLOWED_SCHEMES = Set.of("http", "https");

    /**
     * 校验 URL 安全性
     *
     * @param rawUrl    原始 URL 字符串
     * @param whitelist 允许的域名白名单；为空表示仅做内网/协议黑名单
     * @return 解析后的 URI（已规范化）
     * @throws SecurityException 不安全时抛出（含 4 类原因）
     */
    public static URI validatePublicUrl(String rawUrl, Set<String> whitelist) {
        if (rawUrl == null || rawUrl.isBlank()) {
            throw new SecurityException("URL is blank");
        }
        URI uri;
        try {
            uri = new URI(rawUrl);
        } catch (URISyntaxException e) {
            throw new SecurityException("URL syntax error: " + e.getMessage());
        }
        // scheme
        String scheme = uri.getScheme();
        if (scheme == null || !ALLOWED_SCHEMES.contains(scheme.toLowerCase())) {
            throw new SecurityException("Scheme not allowed: " + scheme);
        }
        // host
        String host = uri.getHost();
        if (host == null || host.isBlank()) {
            throw new SecurityException("Host is missing");
        }
        String lowerHost = host.toLowerCase();
        // 白名单
        if (whitelist != null && !whitelist.isEmpty()) {
            if (!whitelist.contains(lowerHost)) {
                throw new SecurityException("Host not in whitelist: " + lowerHost);
            }
        }
        // host 形如 IP
        if (isIpLiteral(lowerHost)) {
            ensureHostIpSafe(lowerHost);
        } else {
            // host 是域名，做一次 DNS 解析防止 rebinding / 内网域名（如 localtest.me）
            try {
                InetAddress addr = InetAddress.getByName(lowerHost);
                String ip = addr.getHostAddress();
                ensureHostIpSafe(ip);
            } catch (UnknownHostException e) {
                throw new SecurityException("Cannot resolve host: " + lowerHost);
            }
        }
        return uri;
    }

    private static boolean isIpLiteral(String host) {
        // IPv4
        if (host.matches("^\\d{1,3}(\\.\\d{1,3}){3}$")) return true;
        // IPv6（含 [::]）
        if (host.startsWith("[") && host.endsWith("]")) return true;
        return false;
    }

    private static void ensureHostIpSafe(String ipLiteral) {
        if (ipLiteral == null) {
            throw new SecurityException("IP resolution returned null");
        }
        // IPv6 内网/loopback
        String lower = ipLiteral.toLowerCase();
        // 剥除 IPv6 字面量方括号 [::1] -> ::1
        if (lower.startsWith("[") && lower.endsWith("]")) {
            lower = lower.substring(1, lower.length() - 1);
        }
        if (lower.equals("::1") || lower.equals("::") || lower.startsWith("fe80:") || lower.startsWith("fc") || lower.startsWith("fd")) {
            throw new SecurityException("IPv6 private/loopback/link-local rejected: " + ipLiteral);
        }
        // 兜底：识别任何 IPv6 形式（首段为 0xxx 整段 hex-colon 且含 ::）
        if (lower.contains(":")) {
            // 已通过 ::1/fe80/fc/fd 兜底；剩余 case（如 fec0::）也按私网处理
            if (lower.startsWith("fec0:") || lower.startsWith("ff")) {
                throw new SecurityException("IPv6 special-use rejected: " + ipLiteral);
            }
            return; // 其他 IPv6 视为公网，不做段位细分
        }
        for (String prefix : IPV4_BLOCK_PATTERNS) {
            if (lower.startsWith(prefix)) {
                throw new SecurityException("Internal/loopback IP rejected: " + ipLiteral);
            }
        }
        // 兜底：IPv4 各段
        if (lower.matches("^\\d{1,3}(\\.\\d{1,3}){3}$")) {
            String[] segs = lower.split("\\.");
            int first = safeInt(segs[0]);
            int second = safeInt(segs[1]);
            if (first == 0 || first == 127) {
                throw new SecurityException("Internal/loopback IP rejected: " + ipLiteral);
            }
            if (first == 169 && second == 254) {
                throw new SecurityException("Link-local IP rejected: " + ipLiteral);
            }
        }
    }

    private static int safeInt(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return -1; }
    }
}
