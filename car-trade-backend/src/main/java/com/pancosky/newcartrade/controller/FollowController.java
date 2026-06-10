package com.pancosky.newcartrade.controller;

import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.service.UserFollowService;
import com.pancosky.newcartrade.vo.UserPublicVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户关注控制器
 * 描述：提供关注/取消关注用户、查看我的关注、检查关注状态接口。
 * 基础路径：/api/v1/follows
 * 认证要求：全部接口必须登录。
 */
@RestController
@RequestMapping("/api/v1/follows")
@RequiredArgsConstructor
public class FollowController {

    private final UserFollowService userFollowService;

    /**
     * 关注用户
     * HTTP: POST /api/v1/follows/{userId}
     * 请求参数：userId（path，被关注用户的ID）
     * 响应：ApiResponse&lt;Void&gt;
     * 认证要求：必须登录；不可关注自己；重复关注会幂等返回成功。
     */
    @PostMapping("/{userId}")
    public ApiResponse<Void> follow(@PathVariable Long userId) {
        userFollowService.follow(userId);
        return ApiResponse.success();
    }

    /**
     * 取消关注用户
     * HTTP: DELETE /api/v1/follows/{userId}
     * 请求参数：userId（path，被取消关注用户的ID）
     * 响应：ApiResponse&lt;Void&gt;
     * 认证要求：必须登录。
     */
    @DeleteMapping("/{userId}")
    public ApiResponse<Void> unfollow(@PathVariable Long userId) {
        userFollowService.unfollow(userId);
        return ApiResponse.success();
    }

    /**
     * 我的关注列表
     * HTTP: GET /api/v1/follows
     * 响应：ApiResponse&lt;List&lt;UserPublicVO&gt;&gt; —— 当前用户关注的用户列表（含其公开资料及店铺信息）。
     * 认证要求：必须登录。
     */
    @GetMapping
    public ApiResponse<List<UserPublicVO>> myFollows() {
        return ApiResponse.success(userFollowService.myFollows());
    }

    /**
     * 检查是否已关注某用户
     * HTTP: GET /api/v1/follows/{userId}/check
     * 请求参数：userId（path）
     * 响应：ApiResponse&lt;Boolean&gt; —— true 表示已关注，false 表示未关注。
     * 认证要求：必须登录。
     */
    @GetMapping("/{userId}/check")
    public ApiResponse<Boolean> checkFollowed(@PathVariable Long userId) {
        return ApiResponse.success(userFollowService.checkFollowed(userId));
    }
}
