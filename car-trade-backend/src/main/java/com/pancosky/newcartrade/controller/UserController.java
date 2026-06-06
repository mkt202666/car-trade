package com.pancosky.newcartrade.controller;

import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.dto.LoginDTO;
import com.pancosky.newcartrade.dto.RegisterDTO;
import com.pancosky.newcartrade.service.BrowsingHistoryService;
import com.pancosky.newcartrade.service.UserService;
import com.pancosky.newcartrade.vo.LoginVO;
import com.pancosky.newcartrade.vo.UserPublicVO;
import com.pancosky.newcartrade.vo.UserStatsVO;
import com.pancosky.newcartrade.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ApiResponse<Void> browsing() {
        return ApiResponse.success();
    }

    @DeleteMapping("/me/browsing")
    public ApiResponse<Void> clearBrowsing() {
        return ApiResponse.success();
    }
}
