package com.pancosky.newcartrade.controller;

import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.service.MembershipService;
import com.pancosky.newcartrade.vo.MemberPlanVO;
import com.pancosky.newcartrade.vo.UserMembershipVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 会员/订阅控制器
 * 描述：提供会员套餐列表、我的会员信息、会员续费/升级接口。
 * 基础路径：/api/v1/membership
 * 认证要求：套餐列表公开；/my 及后续需登录。
 */
@RestController
@RequestMapping("/api/v1/membership")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    /**
     * 会员套餐列表
     * HTTP: GET /api/v1/membership/plans
     * 响应：ApiResponse&lt;List&lt;MemberPlanVO&gt;&gt; —— 平台所有可订阅会员套餐（含价格、权益、时长）。
     * 认证要求：公开。
     */
    @GetMapping("/plans")
    public ApiResponse<List<MemberPlanVO>> listPlans() {
        return ApiResponse.success(membershipService.listPlans());
    }

    /**
     * 我的会员信息
     * HTTP: GET /api/v1/membership/my
     * 响应：ApiResponse&lt;UserMembershipVO&gt; —— 当前会员等级、到期时间、剩余权益等。
     * 认证要求：必须登录。
     */
    @GetMapping("/my")
    public ApiResponse<UserMembershipVO> myMembership() {
        return ApiResponse.success(membershipService.getMyMembership());
    }

    /**
     * 续费/开通会员
     * HTTP: POST /api/v1/membership/renew
     * 请求参数：body.planId（套餐ID）。
     * 响应：ApiResponse&lt;Void&gt; —— 成功后直接开通/延长会员。
     * 认证要求：必须登录；扣费前请确保支付渠道配置正确。
     */
    @PostMapping("/renew")
    public ApiResponse<Void> renew(@RequestBody Map<String, Long> body) {
        membershipService.renew(body.get("planId"));
        return ApiResponse.success();
    }
}
