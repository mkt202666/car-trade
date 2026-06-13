package com.pancosky.newcartrade.controller;
import com.pancosky.newcartrade.common.RequiresAuth;
import com.pancosky.newcartrade.common.AuthLevel;

import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.service.CouponService;
import com.pancosky.newcartrade.vo.CouponVO;
import com.pancosky.newcartrade.vo.UserCouponVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 优惠券控制器
 * 描述：提供平台优惠券领取、使用及个人优惠券管理接口。
 * 基础路径：/api/v1/coupons
 * 认证要求：查看可领取优惠券列表公开；个人优惠券及领取/使用需登录。
 */
@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
@RequiresAuth(AuthLevel.PROTECTED)
public class CouponController {

    private final CouponService couponService;

    /**
     * 查看可领取的优惠券列表
     * HTTP: GET /api/v1/coupons
     * 响应：ApiResponse&lt;List&lt;CouponVO&gt;&gt; —— 当前可领取、未过期且剩余数量 > 0 的优惠券。
     * 认证要求：公开。
     */
    @GetMapping
    @RequiresAuth(AuthLevel.PUBLIC)
    public ApiResponse<List<CouponVO>> available() {
        return ApiResponse.success(couponService.listAvailable());
    }

    /**
     * 我的优惠券列表
     * HTTP: GET /api/v1/coupons/my
     * 响应：ApiResponse&lt;List&lt;UserCouponVO&gt;&gt; —— 当前用户已领取的优惠券，含状态（未使用/已使用/已过期）。
     * 认证要求：必须登录。
     */
    @GetMapping("/my")
    public ApiResponse<List<UserCouponVO>> myCoupons() {
        return ApiResponse.success(couponService.listMyCoupons());
    }

    /**
     * 领取优惠券
     * HTTP: POST /api/v1/coupons/{id}/claim
     * 请求参数：id（path，优惠券ID）
     * 响应：ApiResponse&lt;Void&gt;
     * 认证要求：必须登录；每个用户每张优惠券限领一张；优惠券需未达领取上限且未过期。
     */
    @PostMapping("/{id}/claim")
    public ApiResponse<Void> claim(@PathVariable Long id) {
        couponService.claim(id);
        return ApiResponse.success();
    }

    /**
     * 使用优惠券
     * HTTP: POST /api/v1/coupons/{id}/use
     * 请求参数：id（path，用户持有的优惠券ID）；body.orderId（目标订单ID）。
     * 响应：ApiResponse&lt;Void&gt;
     * 认证要求：必须登录；仅支持未使用、未过期的优惠券；订单金额需满足最低使用条件。
     */
    @PostMapping("/{id}/use")
    public ApiResponse<Void> use(@PathVariable Long id, @RequestBody Map<String, String> body) {
        couponService.use(id, body.get("orderId"));
        return ApiResponse.success();
    }
}
