package com.pancosky.newcartrade.controller;

import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.entity.User;
import com.pancosky.newcartrade.service.ContractService;
import com.pancosky.newcartrade.vo.ContractDetailVO;
import com.pancosky.newcartrade.vo.ContractVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

    @PostMapping
    public ApiResponse<ContractVO> create(@RequestBody Map<String, String> body) {
        return ApiResponse.success(contractService.create(body.get("orderId")));
    }

    @GetMapping("/{id}")
    public ApiResponse<ContractDetailVO> detail(@PathVariable Long id) {
        return ApiResponse.success(contractService.detail(id));
    }

    @PutMapping("/{id}/sign")
    public ApiResponse<Void> sign(
            @PathVariable Long id,
            @RequestParam String role,
            @RequestParam(required = false) Long userId) {
        contractService.sign(id, role, userId);
        return ApiResponse.success();
    }

    @GetMapping("/{id}/download")
    public ApiResponse<String> download(@PathVariable Long id) {
        return ApiResponse.success(contractService.download(id));
    }
}
