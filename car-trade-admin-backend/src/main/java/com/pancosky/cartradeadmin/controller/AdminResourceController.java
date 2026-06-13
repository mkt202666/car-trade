package com.pancosky.cartradeadmin.controller;

import com.pancosky.cartradeadmin.annotation.AuditLog;
import com.pancosky.cartradeadmin.common.ApiResponse;
import com.pancosky.cartradeadmin.dto.*;
import com.pancosky.cartradeadmin.service.AdminResourceService;
import com.pancosky.cartradeadmin.vo.BannerVO;
import com.pancosky.cartradeadmin.vo.ConfigVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/resources")
public class AdminResourceController {

    @Autowired
    private AdminResourceService adminResourceService;

    // ==================== Banner ====================

    @GetMapping("/banners")
    public ApiResponse<List<BannerVO>> listBanners(@RequestParam(required = false, defaultValue = "ALL") String status) {
        return ApiResponse.success(adminResourceService.listBanners(status));
    }

    @AuditLog(module = "resource", action = "添加Banner", targetType = "banner")
    @PostMapping("/banners")
    public ApiResponse<BannerVO> createBanner(@Valid @RequestBody BannerCreateDTO dto) {
        return ApiResponse.success(adminResourceService.createBanner(dto));
    }

    @AuditLog(module = "resource", action = "编辑Banner", targetType = "banner")
    @PutMapping("/banners/{id}")
    public ApiResponse<BannerVO> updateBanner(@PathVariable Long id, @RequestBody BannerUpdateDTO dto) {
        return ApiResponse.success(adminResourceService.updateBanner(id, dto));
    }

    @AuditLog(module = "resource", action = "删除Banner", targetType = "banner")
    @DeleteMapping("/banners/{id}")
    public ApiResponse<Void> deleteBanner(@PathVariable Long id) {
        adminResourceService.deleteBanner(id);
        return ApiResponse.success();
    }

    @AuditLog(module = "resource", action = "启停Banner", targetType = "banner")
    @PutMapping("/banners/{id}/status")
    public ApiResponse<Void> updateBannerStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        adminResourceService.updateBannerStatus(id, body.get("status"));
        return ApiResponse.success();
    }

    @AuditLog(module = "resource", action = "排序Banner", targetType = "banner")
    @PutMapping("/banners/sort")
    public ApiResponse<Void> updateBannerSort(@RequestBody List<BannerSortItem> items) {
        adminResourceService.updateBannerSort(items);
        return ApiResponse.success();
    }

    // ==================== Config ====================

    @GetMapping("/configs")
    public ApiResponse<List<ConfigVO>> listConfigs() {
        return ApiResponse.success(adminResourceService.listConfigs());
    }

    @GetMapping("/configs/{key}")
    public ApiResponse<ConfigVO> getConfig(@PathVariable String key) {
        return ApiResponse.success(adminResourceService.getConfig(key));
    }

    @AuditLog(module = "resource", action = "更新配置", targetType = "config")
    @PutMapping("/configs/{key}")
    public ApiResponse<ConfigVO> updateConfig(@PathVariable String key, @Valid @RequestBody ConfigUpdateDTO dto) {
        return ApiResponse.success(adminResourceService.updateConfig(key, dto));
    }
}
