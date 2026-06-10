package com.pancosky.newcartrade.controller;

import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.service.CouponService;
import com.pancosky.newcartrade.vo.CouponVO;
import com.pancosky.newcartrade.vo.UserCouponVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @GetMapping
    public ApiResponse<List<CouponVO>> available() {
        return ApiResponse.success(couponService.listAvailable());
    }

    @GetMapping("/my")
    public ApiResponse<List<UserCouponVO>> myCoupons() {
        return ApiResponse.success(couponService.listMyCoupons());
    }

    @PostMapping("/{id}/claim")
    public ApiResponse<Void> claim(@PathVariable Long id) {
        couponService.claim(id);
        return ApiResponse.success();
    }

    @PostMapping("/{id}/use")
    public ApiResponse<Void> use(@PathVariable Long id, @RequestBody Map<String, String> body) {
        couponService.use(id, body.get("orderId"));
        return ApiResponse.success();
    }
}
