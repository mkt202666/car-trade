package com.pancosky.cartradeadmin.controller;

import com.pancosky.cartradeadmin.annotation.AuditLog;
import com.pancosky.cartradeadmin.common.ApiResponse;
import com.pancosky.cartradeadmin.common.PageResult;
import com.pancosky.cartradeadmin.dto.ShopQueryDTO;
import com.pancosky.cartradeadmin.dto.ShopReviewRejectDTO;
import com.pancosky.cartradeadmin.service.AdminShopReviewService;
import com.pancosky.cartradeadmin.vo.ShopReviewVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/shop-reviews")
public class AdminShopReviewController {

    @Autowired
    private AdminShopReviewService adminShopReviewService;

    @GetMapping
    public ApiResponse<PageResult<ShopReviewVO>> listReviews(@ModelAttribute ShopQueryDTO query) {
        return ApiResponse.success(adminShopReviewService.getReviewList(query));
    }

    @AuditLog(module = "shop-review", action = "审核通过", targetType = "user")
    @PutMapping("/{id}/approve")
    public ApiResponse<Void> approve(@PathVariable Long id) {
        adminShopReviewService.approve(id);
        return ApiResponse.success();
    }

    @AuditLog(module = "shop-review", action = "审核拒绝", targetType = "user")
    @PutMapping("/{id}/reject")
    public ApiResponse<Void> reject(@PathVariable Long id, @Valid @RequestBody ShopReviewRejectDTO dto) {
        adminShopReviewService.reject(id, dto.getReason());
        return ApiResponse.success();
    }

    @AuditLog(module = "shop-review", action = "批量审核通过", targetType = "user")
    @PutMapping("/batch-approve")
    public ApiResponse<Void> batchApprove(@RequestBody List<Long> userIds) {
        adminShopReviewService.batchApprove(userIds);
        return ApiResponse.success();
    }

    @GetMapping("/pending-count")
    public ApiResponse<Map<String, Object>> getPendingCount() {
        long count = adminShopReviewService.getPendingCount();
        return ApiResponse.success(Map.of("count", count));
    }
}
