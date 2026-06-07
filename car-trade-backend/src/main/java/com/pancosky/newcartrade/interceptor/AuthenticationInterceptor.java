package com.pancosky.newcartrade.interceptor;

import com.pancosky.newcartrade.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return writeUnauthorized(response, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        if (!JwtUtil.validateToken(token)) {
            return writeUnauthorized(response, "Invalid or expired token");
        }

        Long userId = JwtUtil.getUserId(token);
        request.setAttribute("currentUserId", userId);
        return true;
    }

    private boolean writeUnauthorized(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"message\":\"" + message + "\",\"data\":null}");
        return false;
    }
}
