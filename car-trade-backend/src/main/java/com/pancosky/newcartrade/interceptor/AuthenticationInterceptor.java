package com.pancosky.newcartrade.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.common.AuthLevel;
import com.pancosky.newcartrade.common.RequiresAuth;
import com.pancosky.newcartrade.entity.User;
import com.pancosky.newcartrade.mapper.UserMapper;
import com.pancosky.newcartrade.service.cache.TokenBlacklistService;
import com.pancosky.newcartrade.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;

/**
 * 统一认证拦截器
 *
 * 处理流程：
 *   1. 放行 OPTIONS 预检请求
 *   2. 读取 @RequiresAuth 注解（方法级优先于类级）
 *   3. PUBLIC → 放行，同时尝试解析 token 以便在接口内部获取当前用户信息
 *   4. PROTECTED → 校验 token + Redis 黑名单，有效后放行
 *   5. CERTIFIED → 校验 token + 认证状态（certificationStatus = "CERTIFIED"）
 *   6. 校验失败 → 返回统一的 401/403 JSON 响应
 *
 * HTTP Header:
 *   Authorization: Bearer <token>
 *   （不再支持 ?token= 入参，避免 token 出现在 URL/Referer/访问日志中）
 */
@Component
@Slf4j
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 0. CORS 预检请求直接放行（与 WebMvcConfig 的 OPTIONS 保持一致）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        // 放行非方法请求（例如静态资源）
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod method = (HandlerMethod) handler;

        // 1. 读取 @RequiresAuth 注解：方法级 > 类级
        RequiresAuth methodAnn = method.getMethodAnnotation(RequiresAuth.class);
        RequiresAuth classAnn = method.getBeanType().getAnnotation(RequiresAuth.class);

        // 2. 权限等级
        AuthLevel level;
        if (methodAnn != null) {
            level = methodAnn.value();
        } else if (classAnn != null) {
            level = classAnn.value();
        } else {
            // 没有注解则按严格原则处理：需要登录
            level = AuthLevel.PROTECTED;
        }

        // 3. 读取 token：Authorization: Bearer <token>（已禁用 ?token= 入参）
        String rawToken = extractToken(request);
        Long currentUserId = null;
        String currentUserRole = null;

        if (rawToken != null && !rawToken.isBlank()) {
            try {
                currentUserId = JwtUtil.getUserId(rawToken);
            } catch (Exception e) {
                log.warn("[Auth] token 解析失败: {}", e.getMessage());
                currentUserId = null;
            }
            // 4. 黑名单检查：解析成功但已被拉黑 → 视为未登录
            if (currentUserId != null && currentUserId > 0) {
                try {
                    if (tokenBlacklistService.isBlacklisted(rawToken)) {
                        log.info("[Auth] blacklisted token rejected, userId={}", currentUserId);
                        write401(response, "登录已失效，请重新登录");
                        return false;
                    }
                } catch (Exception e) {
                    // 黑名单检查异常不影响登录校验（fail-open 已在 service 内处理）
                    log.warn("[Auth] blacklist check error: {}", e.getMessage());
                }
                // 轻量读取用户状态
                try {
                    User u = userMapper.selectById(currentUserId);
                    if (u != null) {
                        currentUserRole = u.getCertificationStatus();
                    }
                } catch (Exception e) {
                    log.warn("[Auth] 查询用户信息失败: {}", e.getMessage());
                }
            }
        }

        // 将当前用户信息放入 request attribute，供 Service 层使用
        if (currentUserId != null) {
            request.setAttribute("currentUserId", currentUserId);
            if (currentUserRole != null) {
                request.setAttribute("currentUserCertStatus", currentUserRole);
            }
        }

        // 5. 按等级判断
        switch (level) {
            case PUBLIC:
                // 公开接口：无论是否登录都放行
                return true;
            case PROTECTED:
                // 需要登录
                if (currentUserId == null || currentUserId <= 0) {
                    write401(response, "请先登录");
                    return false;
                }
                return true;
            case CERTIFIED:
                // 需要商家认证
                if (currentUserId == null || currentUserId <= 0) {
                    write401(response, "请先登录");
                    return false;
                }
                if (!"CERTIFIED".equals(currentUserRole)) {
                    write403(response, "请先完成商家认证");
                    return false;
                }
                return true;
            case ADMIN:
                // 管理员接口：预留
                if (currentUserId == null || currentUserId <= 0) {
                    write401(response, "请先登录");
                    return false;
                }
                // TODO: 预留管理员校验，当前按 PROTECTED 处理
                return true;
            default:
                return true;
        }
    }

    /**
     * 从请求头读取 token
     * 优先顺序: Authorization (Bearer xxx) > X-Auth-Token
     *
     * 已禁用：query 参数 ?token=xxx —— 会在 URL/Referer/访问日志中泄露 token
     */
    private String extractToken(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.length() > 7 && auth.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return auth.substring(7).trim();
        }
        String xtoken = request.getHeader("X-Auth-Token");
        if (xtoken != null && !xtoken.isBlank()) return xtoken.trim();
        return null;
    }

    private void write401(HttpServletResponse response, String msg) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        writeJson(response, ApiResponse.error(401, msg));
    }

    private void write403(HttpServletResponse response, String msg) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        writeJson(response, ApiResponse.error(403, msg));
    }

    private void writeJson(HttpServletResponse response, ApiResponse<?> body) {
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.print(objectMapper.writeValueAsString(body));
            out.flush();
        } catch (Exception e) {
            log.error("[Auth] 写入响应失败", e);
        }
    }
}
