package com.pancosky.newcartrade.controller;

import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.dto.OrderCreateDTO;
import com.pancosky.newcartrade.service.OrderService;
import com.pancosky.newcartrade.vo.OrderDetailVO;
import com.pancosky.newcartrade.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ApiResponse<List<OrderVO>> list(@RequestParam(required = false) String type,
                                           @RequestParam(required = false) String status) {
        return ApiResponse.success(orderService.list(type, status));
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderDetailVO> detail(@PathVariable String id) {
        return ApiResponse.success(orderService.detail(id));
    }

    @PostMapping
    public ApiResponse<OrderVO> create(@RequestBody OrderCreateDTO dto) {
        return ApiResponse.success(orderService.create(dto));
    }

    @PutMapping("/{id}/confirm")
    public ApiResponse<Void> confirm(@PathVariable String id) {
        orderService.confirm(id);
        return ApiResponse.success();
    }

    @PutMapping("/{id}/cancel")
    public ApiResponse<Void> cancel(@PathVariable String id) {
        orderService.cancel(id);
        return ApiResponse.success();
    }

    @PutMapping("/{id}/pay-deposit")
    public ApiResponse<Void> payDeposit(@PathVariable String id) {
        orderService.payDeposit(id);
        return ApiResponse.success();
    }

    @PutMapping("/{id}/complete")
    public ApiResponse<Void> complete(@PathVariable String id) {
        orderService.complete(id);
        return ApiResponse.success();
    }

    @PostMapping("/{id}/dispute")
    public ApiResponse<Void> dispute(@PathVariable String id) {
        orderService.dispute(id);
        return ApiResponse.success();
    }

    // 合同相关接口
    @PostMapping("/{id}/contract")
    public ApiResponse<Void> submitContract(@PathVariable String id, @RequestBody Map<String, String> body) {
        orderService.submitContract(id, body.get("content"));
        return ApiResponse.success();
    }

    @PutMapping("/{id}/contract")
    public ApiResponse<Void> updateContract(@PathVariable String id, @RequestBody Map<String, String> body) {
        orderService.updateContract(id, body.get("content"));
        return ApiResponse.success();
    }

    @PutMapping("/{id}/contract/confirm")
    public ApiResponse<Void> confirmContract(@PathVariable String id) {
        orderService.confirmContract(id);
        return ApiResponse.success();
    }

    @GetMapping("/{id}/contract")
    public ApiResponse<Map<String, Object>> getContract(@PathVariable String id) {
        return ApiResponse.success(orderService.getContract(id));
    }

    // 终止交易接口
    @PostMapping("/{id}/terminate")
    public ApiResponse<Void> terminate(@PathVariable String id, @RequestBody Map<String, String> body) {
        orderService.terminate(id, body.get("reason"));
        return ApiResponse.success();
    }

    @GetMapping("/{id}/terminate/count")
    public ApiResponse<Map<String, Integer>> getTerminateCount(@PathVariable String id) {
        return ApiResponse.success(orderService.getTerminateCount(id));
    }

    // 订单日志接口
    @GetMapping("/{id}/logs")
    public ApiResponse<List<Object>> logs(@PathVariable String id) {
        return ApiResponse.success(orderService.logs(id));
    }
}
