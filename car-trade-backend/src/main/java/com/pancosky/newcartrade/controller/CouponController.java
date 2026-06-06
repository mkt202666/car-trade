package com.pancosky.newcartrade.controller;

import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.service.CouponService;
import com.pancosky.newcartrade.vo.CouponVO;
import com.pancosky.newcartrade.vo.UserCouponVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
