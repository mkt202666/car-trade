package com.pancosky.newcartrade.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Arrays;
import java.util.Map;

/**
 * WebSocket 握手拦截器 — Origin 校验
 *
 * 目的：阻止跨站 WebSocket 劫持（CSWSH）。浏览器在 WS 握手时会携带 Origin 头，
 * 攻击者构造恶意页面诱骗用户连接 ws://victim/ws 并携带用户的 cookie / token。
 * 白名单校验 Origin 后，攻击者站点无法建立 WS 连接。
 *
 * 注意：
 *   - 仅校验"声称的"Origin（浏览器发送的），无法防止 native HTTP 客户端伪造 Origin
 *   - 真正的身份认证由 WebSocketAuthInterceptor 在 STOMP CONNECT 时完成
 */
@Slf4j
@Component
public class WebSocketOriginInterceptor implements HandshakeInterceptor {

    /**
     * 允许的 Origin 白名单。生产环境通过 env: CORS_ALLOWED_ORIGINS 注入（与 WebMvcConfig 共享）
     */
    @Value("${cors.allowed-origins:http://localhost:5173,http://127.0.0.1:5173,http://localhost:8080}")
    private String allowedOriginsConfig;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String origin = request.getHeaders().getOrigin();
        // 1. 缺少 Origin 头：可能是 native 客户端、测试工具、postman —— 这里不强制要求
        //    真实安全由 STOMP CONNECT 的 token 校验把关
        if (origin == null || origin.isBlank()) {
            log.debug("[WS Handshake] no Origin header from {}, allow (token auth will guard)", request.getRemoteAddress());
            return true;
        }
        // 2. 解析白名单
        String[] allowed = allowedOriginsConfig.split(",");
        boolean ok = Arrays.stream(allowed)
                .map(String::trim)
                .anyMatch(o -> o.equalsIgnoreCase(origin));
        if (!ok) {
            log.warn("[WS Handshake] reject due to Origin not in whitelist: origin={} remote={}", origin, request.getRemoteAddress());
            response.setStatusCode(org.springframework.http.HttpStatus.FORBIDDEN);
            return false;
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        if (exception != null) {
            log.warn("[WS Handshake] failed: {}", exception.getMessage());
        }
    }
}
