package com.pancosky.newcartrade.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.common.RequiresAuth;
import com.pancosky.newcartrade.common.AuthLevel;
import com.pancosky.newcartrade.entity.ExportRegion;
import com.pancosky.newcartrade.mapper.ExportRegionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 出口地区配置控制器
 * 描述：管理可出口的国家/地区及其约束条件和要求
 * 基础路径：/api/v1/export-regions
 * 认证要求：查询接口支持未登录访问；增删改需ADMIN权限
 */
@RestController
@RequestMapping("/api/v1/export-regions")
@RequiredArgsConstructor
@Slf4j
public class ExportRegionController {

    private final ExportRegionMapper exportRegionMapper;

    /**
     * 获取所有出口地区列表（按sortOrder排序）
     * GET /api/v1/export-regions
     */
    @GetMapping
    @RequiresAuth(AuthLevel.PUBLIC)
    public ApiResponse<List<ExportRegion>> list() {
        return ApiResponse.success(exportRegionMapper.selectList(
                new LambdaQueryWrapper<ExportRegion>()
                        .isNull(ExportRegion::getDeletedAt)
                        .orderByAsc(ExportRegion::getSortOrder)));
    }

    /**
     * 获取单个出口地区详情
     * GET /api/v1/export-regions/{id}
     */
    @GetMapping("/{id}")
    @RequiresAuth(AuthLevel.PUBLIC)
    public ApiResponse<ExportRegion> detail(@PathVariable Integer id) {
        ExportRegion region = exportRegionMapper.selectById(id);
        if (region == null || region.getDeletedAt() != null) {
            return ApiResponse.error(400, "出口地区不存在");
        }
        return ApiResponse.success(region);
    }

    /**
     * 创建出口地区配置
     * POST /api/v1/export-regions
     */
    @PostMapping
    @RequiresAuth(AuthLevel.ADMIN)
    public ApiResponse<ExportRegion> create(@RequestBody ExportRegion region) {
        // 验证国家代码唯一性
        Long count = exportRegionMapper.selectCount(
                new LambdaQueryWrapper<ExportRegion>()
                        .eq(ExportRegion::getCode, region.getCode())
                        .isNull(ExportRegion::getDeletedAt));
        if (count > 0) {
            return ApiResponse.error(400, "国家代码已存在");
        }

        // 设置默认值
        if (region.getStatus() == null) {
            region.setStatus("ACTIVE");
        }
        if (region.getSortOrder() == null) {
            region.setSortOrder(999);
        }

        exportRegionMapper.insert(region);
        return ApiResponse.success(region);
    }

    /**
     * 更新出口地区配置
     * PUT /api/v1/export-regions/{id}
     */
    @PutMapping("/{id}")
    @RequiresAuth(AuthLevel.ADMIN)
    public ApiResponse<ExportRegion> update(@PathVariable Integer id, @RequestBody ExportRegion region) {
        ExportRegion existing = exportRegionMapper.selectById(id);
        if (existing == null || existing.getDeletedAt() != null) {
            return ApiResponse.error(400, "出口地区不存在");
        }

        // 检查国家代码是否与其他记录冲突
        Long count = exportRegionMapper.selectCount(
                new LambdaQueryWrapper<ExportRegion>()
                        .eq(ExportRegion::getCode, region.getCode())
                        .ne(ExportRegion::getId, id)
                        .isNull(ExportRegion::getDeletedAt));
        if (count > 0) {
            return ApiResponse.error(400, "国家代码已被其他记录使用");
        }

        region.setId(id);
        exportRegionMapper.updateById(region);
        return ApiResponse.success(region);
    }

    /**
     * 删除出口地区配置（逻辑删除）
     * DELETE /api/v1/export-regions/{id}
     */
    @DeleteMapping("/{id}")
    @RequiresAuth(AuthLevel.ADMIN)
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        ExportRegion region = exportRegionMapper.selectById(id);
        if (region == null || region.getDeletedAt() != null) {
            return ApiResponse.error(400, "出口地区不存在");
        }

        region.setDeletedAt(java.time.LocalDateTime.now());
        exportRegionMapper.updateById(region);
        return ApiResponse.success(null);
    }

    /**
     * 切换出口地区状态（ACTIVE <-> INACTIVE）
     * PATCH /api/v1/export-regions/{id}/toggle-status
     */
    @PatchMapping("/{id}/toggle-status")
    @RequiresAuth(AuthLevel.ADMIN)
    public ApiResponse<ExportRegion> toggleStatus(@PathVariable Integer id) {
        ExportRegion region = exportRegionMapper.selectById(id);
        if (region == null || region.getDeletedAt() != null) {
            return ApiResponse.error(400, "出口地区不存在");
        }

        region.setStatus("ACTIVE".equals(region.getStatus()) ? "INACTIVE" : "ACTIVE");
        exportRegionMapper.updateById(region);
        return ApiResponse.success(region);
    }
}
