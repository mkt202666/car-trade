package com.pancosky.cartradeadmin.controller;

import com.pancosky.cartradeadmin.annotation.AuditLog;
import com.pancosky.cartradeadmin.common.ApiResponse;
import com.pancosky.cartradeadmin.common.PageResult;
import com.pancosky.cartradeadmin.dto.PurchaseCloseDTO;
import com.pancosky.cartradeadmin.dto.PurchaseQueryDTO;
import com.pancosky.cartradeadmin.service.AdminPurchaseRequestService;
import com.pancosky.cartradeadmin.vo.PurchaseRequestDetailVO;
import com.pancosky.cartradeadmin.vo.PurchaseRequestVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/purchase-requests")
public class AdminPurchaseRequestController {

    @Autowired
    private AdminPurchaseRequestService adminPurchaseRequestService;

    @GetMapping
    public ApiResponse<PageResult<PurchaseRequestVO>> listPurchaseRequests(@ModelAttribute PurchaseQueryDTO query) {
        return ApiResponse.success(adminPurchaseRequestService.getPurchaseList(query));
    }

    @GetMapping("/{id}")
    public ApiResponse<PurchaseRequestDetailVO> getPurchaseRequestDetail(@PathVariable Long id) {
        return ApiResponse.success(adminPurchaseRequestService.getPurchaseDetail(id));
    }

    @AuditLog(module = "purchase-request", action = "关闭求购", targetType = "purchase-request")
    @PutMapping("/{id}/close")
    public ApiResponse<Void> closePurchaseRequest(@PathVariable Long id, @Valid @RequestBody PurchaseCloseDTO dto) {
        adminPurchaseRequestService.closePurchaseRequest(id, dto.getReason());
        return ApiResponse.success();
    }

    @AuditLog(module = "purchase-request", action = "智能匹配车源", targetType = "purchase-request")
    @PutMapping("/{id}/match")
    public ApiResponse<Map<String, Integer>> matchPurchaseRequest(@PathVariable Long id) {
        int count = adminPurchaseRequestService.matchPurchaseRequest(id);
        return ApiResponse.success(Map.of("matchCount", count));
    }
}
