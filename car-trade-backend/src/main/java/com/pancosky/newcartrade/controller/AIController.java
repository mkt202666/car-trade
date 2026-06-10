package com.pancosky.newcartrade.controller;

import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AIController {

    private final AIService aiService;

    /**
     * AI对话
     */
    @PostMapping("/chat")
    public ApiResponse<Map<String, Object>> chat(@RequestBody Map<String, Object> params) {
        return ApiResponse.success(aiService.chat(params));
    }

    /**
     * 智能找车
     */
    @PostMapping("/search")
    public ApiResponse<Map<String, Object>> search(@RequestBody Map<String, Object> params) {
        return ApiResponse.success(aiService.search(params));
    }

    /**
     * 行情分析
     */
    @PostMapping("/market-analysis")
    public ApiResponse<Map<String, Object>> marketAnalysis(@RequestBody Map<String, Object> params) {
        return ApiResponse.success(aiService.marketAnalysis(params));
    }

    /**
     * 获客文案生成
     */
    @PostMapping("/customer-generation")
    public ApiResponse<Map<String, Object>> generateCopywriting(@RequestBody Map<String, Object> params) {
        return ApiResponse.success(aiService.generateCopywriting(params));
    }

    /**
     * 自动外联
     */
    @PostMapping("/auto-outreach")
    public ApiResponse<Map<String, Object>> autoOutreach(@RequestBody Map<String, Object> params) {
        return ApiResponse.success(aiService.autoOutreach(params));
    }

    /**
     * 车源分配
     */
    @PostMapping("/distribute")
    public ApiResponse<Map<String, Object>> distributeCar(@RequestBody Map<String, Object> params) {
        return ApiResponse.success(aiService.distributeCar(params));
    }

    /**
     * 车源分析
     */
    @PostMapping("/car-analysis")
    public ApiResponse<Map<String, Object>> carAnalysis(@RequestBody Map<String, Object> params) {
        return ApiResponse.success(aiService.carAnalysis(params));
    }

    /**
     * 估价助手
     */
    @PostMapping("/price-estimate")
    public ApiResponse<Map<String, Object>> priceEstimate(@RequestBody Map<String, Object> params) {
        return ApiResponse.success(aiService.priceEstimate(params));
    }
}