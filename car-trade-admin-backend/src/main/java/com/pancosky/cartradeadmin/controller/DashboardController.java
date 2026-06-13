package com.pancosky.cartradeadmin.controller;

import com.pancosky.cartradeadmin.common.ApiResponse;
import com.pancosky.cartradeadmin.service.DashboardService;
import com.pancosky.cartradeadmin.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/kpi")
    public ApiResponse<DashboardKpiVO> getKpi() {
        return ApiResponse.success(dashboardService.getKpi());
    }

    /**
     * 数据全景概览 - 聚合 KPI + 预警摘要
     */
    @GetMapping("/stats/overview")
    public ApiResponse<Map<String, Object>> getStatsOverview() {
        Map<String, Object> overview = new HashMap<>();
        overview.put("kpi", dashboardService.getKpi());
        overview.put("warnings", dashboardService.getWarnings(null));
        return ApiResponse.success(overview);
    }

    @GetMapping("/trend")
    public ApiResponse<List<DashboardTrendVO>> getTrend(@RequestParam(defaultValue = "MONTH") String period) {
        return ApiResponse.success(dashboardService.getTrend(period));
    }

    @GetMapping("/car-distribution")
    public ApiResponse<List<DashboardCarDistVO>> getCarDistribution() {
        return ApiResponse.success(dashboardService.getCarDistribution());
    }

    @GetMapping("/coupon-stats")
    public ApiResponse<DashboardCouponVO> getCouponStats() {
        return ApiResponse.success(dashboardService.getCouponStats());
    }

    @GetMapping("/approval-queue")
    public ApiResponse<List<DashboardApprovalVO>> getApprovalQueue() {
        return ApiResponse.success(dashboardService.getApprovalQueue());
    }

    @GetMapping("/warnings")
    public ApiResponse<List<DashboardWarningVO>> getWarnings(@RequestParam(required = false) String status) {
        return ApiResponse.success(dashboardService.getWarnings(status));
    }
}
