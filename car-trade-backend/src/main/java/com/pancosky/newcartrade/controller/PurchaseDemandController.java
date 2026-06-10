package com.pancosky.newcartrade.controller;

import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.common.PageResult;
import com.pancosky.newcartrade.dto.PurchaseDemandCreateDTO;
import com.pancosky.newcartrade.service.PurchaseDemandService;
import com.pancosky.newcartrade.vo.PurchaseDemandVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/purchases")
@RequiredArgsConstructor
public class PurchaseDemandController {

    private final PurchaseDemandService purchaseDemandService;

    @PostMapping
    public ApiResponse<PurchaseDemandVO> create(@Valid @RequestBody PurchaseDemandCreateDTO dto) {
        return ApiResponse.success(purchaseDemandService.create(dto));
    }

    @GetMapping
    public ApiResponse<PageResult<PurchaseDemandVO>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ApiResponse.success(purchaseDemandService.list(page, size));
    }

    @GetMapping("/my")
    public ApiResponse<PageResult<PurchaseDemandVO>> myDemands(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ApiResponse.success(purchaseDemandService.myDemands(page, size));
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<Void> cancel(@PathVariable Long id) {
        purchaseDemandService.cancel(id);
        return ApiResponse.success();
    }
}
