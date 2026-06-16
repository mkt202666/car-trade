package com.pancosky.cartradeadmin.controller;

import com.pancosky.cartradeadmin.annotation.AuditLog;
import com.pancosky.cartradeadmin.common.ApiResponse;
import com.pancosky.cartradeadmin.common.PageResult;
import com.pancosky.cartradeadmin.dto.UserCreateDTO;
import com.pancosky.cartradeadmin.dto.UserProfileUpdateDTO;
import com.pancosky.cartradeadmin.dto.UserQueryDTO;
import com.pancosky.cartradeadmin.dto.UserStatusUpdateDTO;
import com.pancosky.cartradeadmin.service.AdminUserService;
import com.pancosky.cartradeadmin.vo.AdminUserDetailVO;
import com.pancosky.cartradeadmin.vo.AdminUserVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/users")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    @GetMapping
    public ApiResponse<PageResult<AdminUserVO>> listUsers(@ModelAttribute UserQueryDTO query) {
        return ApiResponse.success(adminUserService.listUsers(query));
    }

    @AuditLog(module = "user", action = "创建用户", targetType = "user")
    @PostMapping
    public ApiResponse<Map<String, Long>> createUser(@Valid @RequestBody UserCreateDTO dto) {
        Long id = adminUserService.createUser(dto);
        return ApiResponse.success(Map.of("id", id));
    }

    @GetMapping("/{id}")
    public ApiResponse<AdminUserDetailVO> getUserDetail(@PathVariable Long id) {
        return ApiResponse.success(adminUserService.getUserDetail(id));
    }

    @AuditLog(module = "user", action = "修改用户状态", targetType = "user")
    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateUserStatus(@PathVariable Long id, @Valid @RequestBody UserStatusUpdateDTO dto) {
        adminUserService.updateUserStatus(id, dto.getStatus());
        return ApiResponse.success();
    }

    @AuditLog(module = "user", action = "编辑用户资料", targetType = "user")
    @PutMapping("/{id}/profile")
    public ApiResponse<Void> updateUserProfile(@PathVariable Long id, @RequestBody UserProfileUpdateDTO dto) {
        adminUserService.updateUserProfile(id, dto);
        return ApiResponse.success();
    }

    @GetMapping("/{id}/statistics")
    public ApiResponse<Map<String, Object>> getUserStatistics(@PathVariable Long id) {
        return ApiResponse.success(adminUserService.getUserStatistics(id));
    }
}
