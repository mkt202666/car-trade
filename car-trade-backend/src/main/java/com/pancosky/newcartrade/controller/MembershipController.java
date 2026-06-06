package com.pancosky.newcartrade.controller;

import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.service.MembershipService;
import com.pancosky.newcartrade.vo.MemberPlanVO;
import com.pancosky.newcartrade.vo.UserMembershipVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/membership")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    @GetMapping("/plans")
    public ApiResponse<List<MemberPlanVO>> listPlans() {
        return ApiResponse.success(membershipService.listPlans());
    }

    @GetMapping("/my")
    public ApiResponse<UserMembershipVO> myMembership() {
        return ApiResponse.success(membershipService.getMyMembership());
    }

    @PostMapping("/renew")
    public ApiResponse<Void> renew(@RequestBody Map<String, Long> body) {
        membershipService.renew(body.get("planId"));
        return ApiResponse.success();
    }
}
