package com.pancosky.newcartrade.controller;

import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.service.UserFollowService;
import com.pancosky.newcartrade.vo.UserPublicVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/follows")
@RequiredArgsConstructor
public class FollowController {

    private final UserFollowService userFollowService;

    @PostMapping("/{userId}")
    public ApiResponse<Void> follow(@PathVariable Long userId) {
        userFollowService.follow(userId);
        return ApiResponse.success();
    }

    @DeleteMapping("/{userId}")
    public ApiResponse<Void> unfollow(@PathVariable Long userId) {
        userFollowService.unfollow(userId);
        return ApiResponse.success();
    }

    @GetMapping
    public ApiResponse<List<UserPublicVO>> myFollows() {
        return ApiResponse.success(userFollowService.myFollows());
    }

    @GetMapping("/{userId}/check")
    public ApiResponse<Boolean> checkFollowed(@PathVariable Long userId) {
        return ApiResponse.success(userFollowService.checkFollowed(userId));
    }
}
