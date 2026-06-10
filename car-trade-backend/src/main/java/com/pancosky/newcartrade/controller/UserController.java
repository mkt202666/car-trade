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

/**
 * 用户账号控制器
 * 描述：提供注册、登录、个人信息管理、实名认证、浏览历史、账号安全（密码/手机号变更）等接口。
 * 基础路径：/api/v1/users
 * 认证要求：/login、/register 公开；其余接口必须登录，以当前登录用户的 ID 进行操作。
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final BrowsingHistoryService browsingHistoryService;

    /**
     * 用户登录
     * HTTP: POST /api/v1/users/login
     * 请求参数：LoginDTO（JSON body）—— phone、smsCode 或 password。
     * 响应：ApiResponse&lt;LoginVO&gt; —— 包含 token、用户基本信息、会员等级等。
     * 认证要求：公开。
     * 限流：同一手机号每分钟最多 5 次登录请求；连续失败 5 次锁定 10 分钟。
     */
    @PostMapping("/login")
    public ApiResponse<LoginVO> login(@RequestBody LoginDTO dto) {
        return ApiResponse.success(userService.login(dto));
    }

    /**
     * 用户注册
     * HTTP: POST /api/v1/users/register
     * 请求参数：RegisterDTO（JSON body）—— phone、smsCode、password、nickname。
     * 响应：ApiResponse&lt;UserVO&gt; —— 注册成功后返回新用户的基本信息。
     * 认证要求：公开。
     */
    @PostMapping("/register")
    public ApiResponse<UserVO> register(@RequestBody RegisterDTO dto) {
        return ApiResponse.success(userService.register(dto));
    }

    /**
     * 获取当前登录用户信息
     * HTTP: GET /api/v1/users/me
     * 响应：ApiResponse&lt;UserVO&gt; —— 当前登录用户的完整个人信息。
     * 认证要求：必须登录。
     */
    @GetMapping("/me")
    public ApiResponse<UserVO> me() {
        return ApiResponse.success(userService.getCurrentUser());
    }

    /**
     * 修改个人资料
     * HTTP: PUT /api/v1/users/me
     * 请求参数：UserVO（JSON body）—— 允许修改 nickname、avatar、shopName、简介等。
     * 响应：ApiResponse&lt;UserVO&gt; —— 返回更新后的用户信息。
     * 认证要求：必须登录。
     */
    @PutMapping("/me")
    public ApiResponse<UserVO> update(@RequestBody UserVO vo) {
        return ApiResponse.success(userService.updateProfile(vo));
    }

    /**
     * 上传/更新头像
     * HTTP: POST /api/v1/users/me/avatar（Content-Type: multipart/form-data）
     * 请求参数：file（表单文件字段），支持 jpg/png/webp，最大 5MB。
     * 响应：ApiResponse&lt;UserVO&gt; —— 返回用户信息，包含新的 avatar URL。
     * 认证要求：必须登录。
     */
    @PostMapping("/me/avatar")
    public ApiResponse<UserVO> uploadAvatar(@RequestParam MultipartFile file) {
        return ApiResponse.success(userService.uploadAvatar(file));
    }

    /**
     * 获取用户统计数据
     * HTTP: GET /api/v1/users/me/stats
     * 响应：ApiResponse&lt;UserStatsVO&gt; —— 发布车源数、收藏数、订单数、信用分等。
     * 认证要求：必须登录。
     */
    @GetMapping("/me/stats")
    public ApiResponse<UserStatsVO> stats() {
        return ApiResponse.success(userService.getStats());
    }

    /**
     * 提交实名认证（店铺认证）
     * HTTP: POST /api/v1/users/certification
     * 响应：ApiResponse&lt;Void&gt;
     * 认证要求：必须登录；具体认证资料由前端上传至文件服务后传入。
     */
    @PostMapping("/certification")
    public ApiResponse<Void> certify() {
        userService.certify();
        return ApiResponse.success();
    }

    /**
     * 查看任意用户的公开信息
     * HTTP: GET /api/v1/users/{id}
     * 请求参数：id（path，目标用户ID）
     * 响应：ApiResponse&lt;UserPublicVO&gt; —— 返回昵称、店铺名、信用等级、信用分、成交数等公开信息。
     * 认证要求：公开。
     */
    @GetMapping("/{id}")
    public ApiResponse<UserPublicVO> getUser(@PathVariable Long id) {
        return ApiResponse.success(userService.getUserPublicInfo(id));
    }

    /**
     * 浏览历史列表
     * HTTP: GET /api/v1/users/me/browsing?page=1&size=20
     * 请求参数：page（默认 1）、size（默认 20）
     * 响应：ApiResponse&lt;List&lt;BrowsingHistoryVO&gt;&gt; —— 按时间倒序的浏览记录。
     * 认证要求：必须登录。
     */
    @GetMapping("/me/browsing")
    public ApiResponse<List<BrowsingHistoryVO>> browsing(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(browsingHistoryService.list(userId, page, size));
    }

    /**
     * 清空浏览历史
     * HTTP: DELETE /api/v1/users/me/browsing
     * 响应：ApiResponse&lt;Void&gt;
     * 认证要求：必须登录。
     */
    @DeleteMapping("/me/browsing")
    public ApiResponse<Void> clearBrowsing() {
        Long userId = SecurityUtils.getCurrentUserId();
        browsingHistoryService.clear(userId);
        return ApiResponse.success();
    }

    /**
     * 修改登录密码
     * HTTP: PUT /api/v1/users/me/password
     * 请求参数：ChangePasswordDTO（JSON body）—— oldPassword、newPassword。
     * 响应：ApiResponse&lt;Void&gt;
     * 认证要求：必须登录；需正确提供原密码。
     */
    @PutMapping("/me/password")
    public ApiResponse<Void> changePassword(@RequestBody ChangePasswordDTO dto) {
        userService.changePassword(dto);
        return ApiResponse.success();
    }

    /**
     * 更换绑定手机号
     * HTTP: PUT /api/v1/users/me/phone
     * 请求参数：Map&lt;String,String&gt;（JSON body）—— phone（新手机号）、smsCode（新手机号的验证码）。
     * 响应：ApiResponse&lt;Void&gt;
     * 认证要求：必须登录；需要短信验证码。
     */
    @PutMapping("/me/phone")
    public ApiResponse<Void> updatePhone(@RequestBody Map<String, String> body) {
        userService.updatePhone(body.get("phone"), body.get("smsCode"));
        return ApiResponse.success();
    }
}
