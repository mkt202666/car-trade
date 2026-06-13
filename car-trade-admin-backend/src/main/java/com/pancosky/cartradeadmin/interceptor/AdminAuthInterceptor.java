package com.pancosky.cartradeadmin.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pancosky.cartradeadmin.common.ApiResponse;
import com.pancosky.cartradeadmin.service.AdminAuthService;
import com.pancosky.cartradeadmin.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AdminAuthService adminAuthService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 跳过OPTIONS请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            writeErrorResponse(response, 401, "缺少Authorization头");
            return false;
        }

        String token = authHeader.substring(7);
        try {
            Claims claims = jwtUtil.parseToken(token);
            String type = jwtUtil.getTypeFromClaims(claims);
            if (!"ACCESS".equals(type)) {
                writeErrorResponse(response, 401, "无效的访问Token");
                return false;
            }

            // 检查 token 是否在黑名单中（已登出）
            String jti = claims.getId();
            if (jti != null && adminAuthService.isTokenBlacklisted(jti)) {
                writeErrorResponse(response, 401, "Token已失效，请重新登录");
                return false;
            }

            Long adminId = jwtUtil.getAdminIdFromClaims(claims);
            String username = jwtUtil.getUsernameFromClaims(claims);
            String role = jwtUtil.getRoleFromClaims(claims);

            request.setAttribute("ADMIN_ID", adminId);
            request.setAttribute("ADMIN_USERNAME", username);
            request.setAttribute("ADMIN_ROLE", role);

            return true;
        } catch (Exception e) {
            writeErrorResponse(response, 401, e.getMessage());
            return false;
        }
    }

    private void writeErrorResponse(HttpServletResponse response, int code, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiResponse<Void> apiResponse = ApiResponse.error(code, message);
        objectMapper.writeValue(response.getWriter(), apiResponse);
    }
}
