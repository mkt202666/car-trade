package com.pancosky.newcartrade.controller;
import com.pancosky.newcartrade.common.RequiresAuth;
import com.pancosky.newcartrade.common.AuthLevel;

import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.dto.ChangePasswordDTO;
import com.pancosky.newcartrade.dto.LoginDTO;
import com.pancosky.newcartrade.dto.RegisterDTO;
import com.pancosky.newcartrade.service.BrowsingHistoryService;
import com.pancosky.newcartrade.service.SmsService;
import com.pancosky.newcartrade.service.UserService;
import com.pancosky.newcartrade.service.cache.TokenBlacklistService;
import com.pancosky.newcartrade.util.SecurityUtils;
import com.pancosky.newcartrade.vo.BrowsingHistoryVO;
import com.pancosky.newcartrade.vo.LoginVO;
import com.pancosky.newcartrade.vo.UserPublicVO;
import com.pancosky.newcartrade.vo.UserStatsVO;
import com.pancosky.newcartrade.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiresAuth(AuthLevel.PROTECTED)
@Slf4j
public class UserController {

    private final UserService userService;
    private final BrowsingHistoryService browsingHistoryService;
    private final SmsService smsService;
    private final TokenBlacklistService tokenBlacklistService;

    /**
     * 用户登录
     * HTTP: POST /api/v1/users/login
     * 请求参数：LoginDTO（JSON body）—— phone、smsCode 或 password。
     * 响应：ApiResponse&lt;LoginVO&gt; —— 包含 token、用户基本信息、会员等级等。
     * 认证要求：公开。
     * 限流：同一手机号每分钟最多 5 次登录请求；连续失败 5 次锁定 10 分钟。
     */
    @PostMapping("/login")
    @RequiresAuth(AuthLevel.PUBLIC)
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
    @RequiresAuth(AuthLevel.PUBLIC)
    public ApiResponse<UserVO> register(@RequestBody RegisterDTO dto) {
        return ApiResponse.success(userService.register(dto));
    }

    /**
     * 刷新 token
     * HTTP: POST /api/v1/users/refresh
     * 请求体：{ refreshToken: "..." }
     * 行为：
     *   - 校验 refreshToken 有效 + 类型 = refresh
     *   - 旧 refreshToken 立即拉黑
     *   - 返回新的 accessToken + refreshToken（token 轮转）
     * 认证要求：公开（refreshToken 本身就是身份证明）。
     */
    @PostMapping("/refresh")
    @RequiresAuth(AuthLevel.PUBLIC)
    public ApiResponse<LoginVO> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new com.pancosky.newcartrade.exception.BusinessException(401, "refreshToken 不能为空");
        }
        return ApiResponse.success(userService.refresh(refreshToken));
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
    @RequiresAuth(AuthLevel.PUBLIC)
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

    @PostMapping("/sms/send")
    @RequiresAuth(AuthLevel.PUBLIC)
    public ApiResponse<Void> sendSms(@RequestBody Map<String, String> body) {
        String phone = body.get("phone");
        boolean sent = smsService.sendVerificationCode(phone);
        if (!sent) throw new com.pancosky.newcartrade.exception.BusinessException("验证码发送失败");
        return ApiResponse.success();
    }

    /**
     * 用户登出（注销当前 token）
     * HTTP: POST /api/v1/users/logout
     * 行为：
     *   - 从请求头读取当前 token
     *   - 将 token 加入 Redis 黑名单，TTL = token 剩余有效时间
     *   - 之后该 token 持有者的所有请求将被 AuthenticationInterceptor 拒绝（401）
     * 认证要求：必须登录。
     */
    @PostMapping("/logout")
    public ApiResponse<Map<String, Object>> logout(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        String rawToken = null;
        if (auth != null && auth.length() > 7 && auth.regionMatches(true, 0, "Bearer ", 0, 7)) {
            rawToken = auth.substring(7).trim();
        }
        Long userId = SecurityUtils.getCurrentUserId();
        boolean ok = rawToken != null && tokenBlacklistService.blacklist(rawToken);
        if (!ok) {
            // token 已过期 / 解析失败 也算登出成功 —— 反正拦截器下次也不会再让它进来
            log.info("[Logout] userId={} token invalid/expired, treat as logged out", userId);
        } else {
            log.info("[Logout] userId={} token blacklisted", userId);
        }
        Map<String, Object> data = new java.util.HashMap<>();
        data.put("userId", userId);
        data.put("loggedOut", true);
        return ApiResponse.success(data);
    }
}
