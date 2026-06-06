package com.pancosky.newcartrade.controller;

import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.service.ShopMemberService;
import com.pancosky.newcartrade.vo.ShopMemberVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/shop")
@RequiredArgsConstructor
public class ShopMemberController {

    private final ShopMemberService shopMemberService;

    @GetMapping("/members")
    public ApiResponse<List<ShopMemberVO>> listMembers() {
        return ApiResponse.success(shopMemberService.listMembers());
    }

    @PostMapping("/members/invite")
    public ApiResponse<Void> invite(@RequestBody Map<String, Long> body) {
        shopMemberService.invite(body.get("userId"));
        return ApiResponse.success();
    }

    @PutMapping("/members/{id}/approve")
    public ApiResponse<Void> approve(@PathVariable Long id) {
        shopMemberService.approve(id);
        return ApiResponse.success();
    }

    @DeleteMapping("/members/{id}")
    public ApiResponse<Void> remove(@PathVariable Long id) {
        shopMemberService.remove(id);
        return ApiResponse.success();
    }
}
