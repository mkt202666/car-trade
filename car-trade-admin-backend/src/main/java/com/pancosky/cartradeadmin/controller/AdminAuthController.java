package com.pancosky.cartradeadmin.controller;

import com.pancosky.cartradeadmin.annotation.AuditLog;
import com.pancosky.cartradeadmin.annotation.RateLimit;
import com.pancosky.cartradeadmin.common.ApiResponse;
import com.pancosky.cartradeadmin.dto.LoginDTO;
import com.pancosky.cartradeadmin.dto.PasswordChangeDTO;
import com.pancosky.cartradeadmin.dto.RefreshTokenDTO;
import com.pancosky.cartradeadmin.service.AdminAuthService;
import com.pancosky.cartradeadmin.vo.AdminVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/auth")
public class AdminAuthController {

    @Autowired
    private AdminAuthService adminAuthService;

    /**
     * 管理员登录
     */
    @AuditLog(module = "auth", action = "管理员登录", targetType = "admin")
    @RateLimit(window = 300, maxRequests = 10, message = "登录请求过于频繁，请5分钟后再试")
    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@Valid @RequestBody LoginDTO loginDTO) {
        Map<String, Object> result = adminAuthService.login(loginDTO);
        return ApiResponse.success(result);
    }

    /**
     * 管理员登出 — 将当前 token 加入黑名单
     */
    @AuditLog(module = "auth", action = "管理员登出", targetType = "admin")
    @RateLimit(window = 60, maxRequests = 5, limitType = RateLimit.LimitType.USER, message = "登出请求过于频繁")
    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        Long adminId = (Long) request.getAttribute("ADMIN_ID");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            adminAuthService.logout(token, adminId);
        }
        return ApiResponse.success();
    }

    /**
     * 刷新Token
     */
    @PostMapping("/refresh")
    public ApiResponse<Map<String, Object>> refreshToken(@Valid @RequestBody RefreshTokenDTO refreshTokenDTO) {
        Map<String, Object> result = adminAuthService.refreshToken(refreshTokenDTO);
        return ApiResponse.success(result);
    }

    /**
     * 获取当前管理员信息
     */
    @GetMapping("/me")
    public ApiResponse<AdminVO> getMe(HttpServletRequest request) {
        Long adminId = (Long) request.getAttribute("ADMIN_ID");
        if (adminId == null) {
            return ApiResponse.error(401, "未登录");
        }
        AdminVO adminVO = adminAuthService.getMe(adminId);
        return ApiResponse.success(adminVO);
    }

    /**
     * 修改密码
     */
    @AuditLog(module = "auth", action = "修改密码", targetType = "admin")
    @PutMapping("/password")
    public ApiResponse<Void> changePassword(
            HttpServletRequest request,
            @Valid @RequestBody PasswordChangeDTO passwordChangeDTO) {
        Long adminId = (Long) request.getAttribute("ADMIN_ID");
        if (adminId == null) {
            return ApiResponse.error(401, "未登录");
        }
        adminAuthService.changePassword(adminId, passwordChangeDTO);
        return ApiResponse.success();
    }
}
