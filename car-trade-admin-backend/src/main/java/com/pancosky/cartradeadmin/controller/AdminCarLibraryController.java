package com.pancosky.cartradeadmin.controller;

import com.pancosky.cartradeadmin.annotation.AuditLog;
import com.pancosky.cartradeadmin.common.ApiResponse;
import com.pancosky.cartradeadmin.dto.*;
import com.pancosky.cartradeadmin.service.AdminCarLibraryService;
import com.pancosky.cartradeadmin.vo.CarBrandVO;
import com.pancosky.cartradeadmin.vo.CarModelVO;
import com.pancosky.cartradeadmin.vo.CarSeriesVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/car-library")
public class AdminCarLibraryController {

    @Autowired
    private AdminCarLibraryService adminCarLibraryService;

    // ==================== 品牌 ====================

    @GetMapping("/brands")
    public ApiResponse<List<CarBrandVO>> listBrands(@RequestParam(required = false, defaultValue = "ALL") String status) {
        return ApiResponse.success(adminCarLibraryService.listBrands(status));
    }

    @AuditLog(module = "car-library", action = "添加品牌", targetType = "brand")
    @PostMapping("/brands")
    public ApiResponse<CarBrandVO> createBrand(@Valid @RequestBody CarBrandDTO dto) {
        return ApiResponse.success(adminCarLibraryService.createBrand(dto));
    }

    @AuditLog(module = "car-library", action = "编辑品牌", targetType = "brand")
    @PutMapping("/brands/{id}")
    public ApiResponse<CarBrandVO> updateBrand(@PathVariable Long id, @RequestBody CarBrandDTO dto) {
        return ApiResponse.success(adminCarLibraryService.updateBrand(id.intValue(), dto));
    }

    @AuditLog(module = "car-library", action = "删除品牌", targetType = "brand")
    @DeleteMapping("/brands/{id}")
    public ApiResponse<Void> deleteBrand(@PathVariable Long id) {
        adminCarLibraryService.deleteBrand(id.intValue());
        return ApiResponse.success();
    }

    // ==================== 车系 ====================

    @GetMapping("/series")
    public ApiResponse<List<CarSeriesVO>> listSeries(
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false, defaultValue = "ALL") String status) {
        return ApiResponse.success(adminCarLibraryService.listSeries(brandId, status));
    }

    @AuditLog(module = "car-library", action = "添加车系", targetType = "series")
    @PostMapping("/series")
    public ApiResponse<CarSeriesVO> createSeries(@Valid @RequestBody CarSeriesDTO dto) {
        return ApiResponse.success(adminCarLibraryService.createSeries(dto));
    }

    @AuditLog(module = "car-library", action = "编辑车系", targetType = "series")
    @PutMapping("/series/{id}")
    public ApiResponse<CarSeriesVO> updateSeries(@PathVariable Long id, @RequestBody CarSeriesDTO dto) {
        return ApiResponse.success(adminCarLibraryService.updateSeries(id, dto));
    }

    @AuditLog(module = "car-library", action = "删除车系", targetType = "series")
    @DeleteMapping("/series/{id}")
    public ApiResponse<Void> deleteSeries(@PathVariable Long id) {
        adminCarLibraryService.deleteSeries(id);
        return ApiResponse.success();
    }

    // ==================== 车型 ====================

    @GetMapping("/models")
    public ApiResponse<List<CarModelVO>> listModels(
            @RequestParam(required = false) Long seriesId,
            @RequestParam(required = false, defaultValue = "ALL") String status) {
        return ApiResponse.success(adminCarLibraryService.listModels(seriesId, status));
    }

    @AuditLog(module = "car-library", action = "添加车型", targetType = "model")
    @PostMapping("/models")
    public ApiResponse<CarModelVO> createModel(@Valid @RequestBody CarModelDTO dto) {
        return ApiResponse.success(adminCarLibraryService.createModel(dto));
    }

    @AuditLog(module = "car-library", action = "编辑车型", targetType = "model")
    @PutMapping("/models/{id}")
    public ApiResponse<CarModelVO> updateModel(@PathVariable Long id, @RequestBody CarModelDTO dto) {
        return ApiResponse.success(adminCarLibraryService.updateModel(id, dto));
    }

    @AuditLog(module = "car-library", action = "删除车型", targetType = "model")
    @DeleteMapping("/models/{id}")
    public ApiResponse<Void> deleteModel(@PathVariable Long id) {
        adminCarLibraryService.deleteModel(id);
        return ApiResponse.success();
    }

    // ==================== 批量导入 ====================

    @AuditLog(module = "car-library", action = "批量导入车型", targetType = "model")
    @PostMapping("/import")
    public ApiResponse<Map<String, Object>> importModels(@RequestParam("file") MultipartFile file) {
        int count = adminCarLibraryService.importModels(file);
        return ApiResponse.success(Map.of("imported", count));
    }

    @GetMapping("/export-template")
    public ResponseEntity<byte[]> exportTemplate() {
        byte[] data = adminCarLibraryService.exportTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "car_model_template.xlsx");
        return ResponseEntity.ok().headers(headers).body(data);
    }
}
