package com.pancosky.newcartrade.controller;

import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.entity.Brand;
import com.pancosky.newcartrade.entity.Model;
import com.pancosky.newcartrade.entity.Series;
import com.pancosky.newcartrade.mapper.BrandMapper;
import com.pancosky.newcartrade.mapper.ModelMapper;
import com.pancosky.newcartrade.mapper.SeriesMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandMapper brandMapper;
    private final SeriesMapper seriesMapper;
    private final ModelMapper modelMapper;

    @GetMapping
    public ApiResponse<List<Brand>> list() {
        return ApiResponse.success(brandMapper.selectList(
                new LambdaQueryWrapper<Brand>().eq(Brand::getStatus, "ACTIVE").orderByAsc(Brand::getSortOrder)));
    }

    @GetMapping("/{brandId}/series")
    public ApiResponse<List<Series>> series(@PathVariable Integer brandId) {
        return ApiResponse.success(seriesMapper.selectList(
                new LambdaQueryWrapper<Series>().eq(Series::getBrandId, brandId)
                        .eq(Series::getStatus, "ACTIVE").orderByAsc(Series::getSortOrder)));
    }

    @GetMapping("/series/{seriesId}/models")
    public ApiResponse<List<Model>> models(@PathVariable Integer seriesId) {
        return ApiResponse.success(modelMapper.selectList(
                new LambdaQueryWrapper<Model>().eq(Model::getSeriesId, seriesId)
                        .eq(Model::getStatus, "ACTIVE").orderByAsc(Model::getSortOrder)));
    }
}
