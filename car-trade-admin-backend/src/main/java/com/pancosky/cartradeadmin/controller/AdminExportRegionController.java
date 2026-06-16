package com.pancosky.cartradeadmin.controller;

import com.pancosky.cartradeadmin.annotation.AuditLog;
import com.pancosky.cartradeadmin.common.ApiResponse;
import com.pancosky.cartradeadmin.dto.ExportRegionDTO;
import com.pancosky.cartradeadmin.service.ExportRegionService;
import com.pancosky.cartradeadmin.vo.ExportRegionVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/export-regions")
public class AdminExportRegionController {

    @Autowired
    private ExportRegionService exportRegionService;

    @GetMapping
    public ApiResponse<List<ExportRegionVO>> listAll() {
        return ApiResponse.success(exportRegionService.listAll());
    }

    @AuditLog(module = "export-region", action = "创建导出地区", targetType = "export-region")
    @PostMapping
    public ApiResponse<Map<String, Long>> create(@Valid @RequestBody ExportRegionDTO dto) {
        Long id = exportRegionService.create(dto);
        return ApiResponse.success(Map.of("id", id));
    }

    @AuditLog(module = "export-region", action = "更新导出地区", targetType = "export-region")
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody ExportRegionDTO dto) {
        exportRegionService.update(id, dto);
        return ApiResponse.success();
    }

    @AuditLog(module = "export-region", action = "删除导出地区", targetType = "export-region")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        exportRegionService.delete(id);
        return ApiResponse.success();
    }
}
