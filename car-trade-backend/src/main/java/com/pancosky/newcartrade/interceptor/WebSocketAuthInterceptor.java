package com.pancosky.newcartrade.interceptor;

import com.pancosky.newcartrade.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;

/**
 * STOMP 通道拦截器 — 身份认证
 *
 * 关键点：
 *   - 仅在 STOMP CONNECT 帧做 token 校验（其他帧（SUBSCRIBE / SEND）通常不需要重复）
 *   - token 校验失败 → 拒绝连接（返回 null，Spring 会关闭 session）
 *   - token 类型必须为 access（防止拿 refresh token 接入 WS）
 *   - 校验通过后把 userId 写入 Principal，供业务层使用
 */
@Slf4j
@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("[WS Auth] missing/invalid Authorization header on CONNECT, reject");
                return null; // 拒绝连接
            }
            String token = authHeader.substring(7).trim();
            if (token.isEmpty()) {
                log.warn("[WS Auth] empty token, reject");
                return null;
            }
            // 1. 校验签名 / 过期
            if (!JwtUtil.validateToken(token)) {
                log.warn("[WS Auth] token invalid/expired, reject");
                return null;
            }
            // 2. 校验类型：必须是 access（不能用 refresh）
            String typ = JwtUtil.getTokenType(token);
            if (!"access".equals(typ)) {
                log.warn("[WS Auth] non-access token (typ={}), reject", typ);
                return null;
            }
            // 3. 写入 Principal
            Long userId = JwtUtil.getUserId(token);
            if (userId == null) {
                log.warn("[WS Auth] failed to extract userId, reject");
                return null;
            }
            final Long finalUserId = userId;
            accessor.setUser(new Principal() {
                @Override
                public String getName() {
                    return String.valueOf(finalUserId);
                }
            });
            log.info("[WS Auth] userId={} connected", finalUserId);
        }

        return message;
    }
}
