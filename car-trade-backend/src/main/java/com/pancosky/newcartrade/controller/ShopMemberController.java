package com.pancosky.newcartrade.controller;

import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.service.ShopMemberService;
import com.pancosky.newcartrade.vo.ShopMemberVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 店铺成员控制器
 * 描述：提供店铺成员的列表、邀请、审核、移除接口。
 * 基础路径：/api/v1/shop
 * 认证要求：全部接口必须登录，且必须是店铺主账号或管理员。
 */
@RestController
@RequestMapping("/api/v1/shop")
@RequiredArgsConstructor
public class ShopMemberController {

    private final ShopMemberService shopMemberService;

    /**
     * 店铺成员列表
     * HTTP: GET /api/v1/shop/members
     * 响应：ApiResponse&lt;List&lt;ShopMemberVO&gt;&gt; —— 当前登录用户所属店铺的成员列表，含角色与加入时间。
     * 认证要求：必须登录；店铺主账号及管理员可见。
     */
    @GetMapping("/members")
    public ApiResponse<List<ShopMemberVO>> listMembers() {
        return ApiResponse.success(shopMemberService.listMembers());
    }

    /**
     * 邀请成员加入店铺
     * HTTP: POST /api/v1/shop/members/invite
     * 请求参数：body.userId（被邀请的用户ID）。
     * 响应：ApiResponse&lt;Void&gt;
     * 认证要求：必须登录；店铺主账号才能发起邀请。
     */
    @PostMapping("/members/invite")
    public ApiResponse<Void> invite(@RequestBody Map<String, Long> body) {
        shopMemberService.invite(body.get("userId"));
        return ApiResponse.success();
    }

    /**
     * 审核成员（同意/拒绝加入申请）
     * HTTP: PUT /api/v1/shop/members/{id}/approve
     * 请求参数：id（path，店铺成员邀请/申请ID）
     * 响应：ApiResponse&lt;Void&gt;
     * 认证要求：必须登录；店铺主账号才能审核。
     */
    @PutMapping("/members/{id}/approve")
    public ApiResponse<Void> approve(@PathVariable Long id) {
        shopMemberService.approve(id);
        return ApiResponse.success();
    }

    /**
     * 移除成员
     * HTTP: DELETE /api/v1/shop/members/{id}
     * 请求参数：id（path，店铺成员ID）
     * 响应：ApiResponse&lt;Void&gt;
     * 认证要求：必须登录；店铺主账号或管理员才能移除。
     */
    @DeleteMapping("/members/{id}")
    public ApiResponse<Void> remove(@PathVariable Long id) {
        shopMemberService.remove(id);
        return ApiResponse.success();
    }
}
