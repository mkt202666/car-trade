package com.pancosky.cartradeadmin.controller;

import com.pancosky.cartradeadmin.common.ApiResponse;
import com.pancosky.cartradeadmin.common.PageResult;
import com.pancosky.cartradeadmin.dto.PurchaseQueryDTO;
import com.pancosky.cartradeadmin.service.AdminPurchaseRequestService;
import com.pancosky.cartradeadmin.vo.PurchaseRequestDetailVO;
import com.pancosky.cartradeadmin.vo.PurchaseRequestVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
}
