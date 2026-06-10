package com.pancosky.newcartrade.controller;

import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.common.PageResult;
import com.pancosky.newcartrade.service.DepositService;
import com.pancosky.newcartrade.vo.DepositRecordVO;
import com.pancosky.newcartrade.vo.DepositVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/finance")
@RequiredArgsConstructor
public class FinanceController {

    private final DepositService depositService;

    @GetMapping("/deposit")
    public ApiResponse<DepositVO> getDeposit() {
        return ApiResponse.success(depositService.getDepositInfo());
    }

    @PostMapping("/deposit/recharge")
    public ApiResponse<Void> recharge(@RequestBody Map<String, Object> body) {
        Double amount = ((Number) body.get("amount")).doubleValue();
        depositService.recharge(amount);
        return ApiResponse.success();
    }

    @PostMapping("/deposit/withdraw")
    public ApiResponse<Void> withdraw(@RequestBody Map<String, Object> body) {
        Double amount = ((Number) body.get("amount")).doubleValue();
        depositService.withdraw(amount);
        return ApiResponse.success();
    }

    @GetMapping("/deposit/records")
    public ApiResponse<PageResult<DepositRecordVO>> getRecords(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        return ApiResponse.success(depositService.getRecords(page, size));
    }
}