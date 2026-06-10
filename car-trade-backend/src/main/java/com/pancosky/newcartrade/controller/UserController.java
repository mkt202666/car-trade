package com.pancosky.newcartrade.controller;

import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.dto.ChangePasswordDTO;
import com.pancosky.newcartrade.dto.LoginDTO;
import com.pancosky.newcartrade.dto.RegisterDTO;
import com.pancosky.newcartrade.service.BrowsingHistoryService;
import com.pancosky.newcartrade.service.UserService;
import com.pancosky.newcartrade.util.SecurityUtils;
import com.pancosky.newcartrade.vo.BrowsingHistoryVO;
import com.pancosky.newcartrade.vo.LoginVO;
import com.pancosky.newcartrade.vo.UserPublicVO;
import com.pancosky.newcartrade.vo.UserStatsVO;
import com.pancosky.newcartrade.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final BrowsingHistoryService browsingHistoryService;

    @PostMapping("/login")
    public ApiResponse<LoginVO> login(@RequestBody LoginDTO dto) {
        return ApiResponse.success(userService.login(dto));
    }

    @PostMapping("/register")
    public ApiResponse<UserVO> register(@RequestBody RegisterDTO dto) {
        return ApiResponse.success(userService.register(dto));
    }

    @GetMapping("/me")
    public ApiResponse<UserVO> me() {
        return ApiResponse.success(userService.getCurrentUser());
    }

    @PutMapping("/me")
    public ApiResponse<UserVO> update(@RequestBody UserVO vo) {
        return ApiResponse.success(userService.updateProfile(vo));
    }

    @PostMapping("/me/avatar")
    public ApiResponse<UserVO> uploadAvatar(@RequestParam MultipartFile file) {
        return ApiResponse.success(userService.uploadAvatar(file));
    }

    @GetMapping("/me/stats")
    public ApiResponse<UserStatsVO> stats() {
        return ApiResponse.success(userService.getStats());
    }

    @PostMapping("/certification")
    public ApiResponse<Void> certify() {
        userService.certify();
        return ApiResponse.success();
    }

    @GetMapping("/{id}")
    public ApiResponse<UserPublicVO> getUser(@PathVariable Long id) {
        return ApiResponse.success(userService.getUserPublicInfo(id));
    }

    @GetMapping("/me/browsing")
    public ApiResponse<List<BrowsingHistoryVO>> browsing(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(browsingHistoryService.list(userId, page, size));
    }

    @DeleteMapping("/me/browsing")
    public ApiResponse<Void> clearBrowsing() {
        Long userId = SecurityUtils.getCurrentUserId();
        browsingHistoryService.clear(userId);
        return ApiResponse.success();
    }

    @PutMapping("/me/password")
    public ApiResponse<Void> changePassword(@RequestBody ChangePasswordDTO dto) {
        userService.changePassword(dto);
        return ApiResponse.success();
    }

    @PutMapping("/me/phone")
    public ApiResponse<Void> updatePhone(@RequestBody Map<String, String> body) {
        userService.updatePhone(body.get("phone"), body.get("smsCode"));
        return ApiResponse.success();
    }
}
