package com.pancosky.newcartrade.controller;
import com.pancosky.newcartrade.common.RequiresAuth;
import com.pancosky.newcartrade.common.AuthLevel;

import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.service.CityService;
import com.pancosky.newcartrade.vo.CityVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 城市数据控制器
 * 描述：提供城市列表查询接口，用于前端城市选择器组件
 * 基础路径：/api/v1/cities
 * 认证要求：公开（未登录可访问）
 */
@RestController
@RequestMapping("/api/v1/cities")
@RequiredArgsConstructor
@RequiresAuth(AuthLevel.PUBLIC)
public class CityController {

    private final CityService cityService;

    /**
     * 获取所有城市列表
     * HTTP: GET /api/v1/cities
     * 响应：ApiResponse&lt;List&lt;CityVO&gt;&gt; —— 城市列表
     * 认证要求：公开
     */
    @GetMapping
    public ApiResponse<List<CityVO>> getAllCities() {
        return ApiResponse.success(cityService.getAllCities());
    }

    /**
     * 获取热门城市列表
     * HTTP: GET /api/v1/cities/hot
     * 响应：ApiResponse&lt;List&lt;CityVO&gt;&gt; —— 热门城市列表
     * 认证要求：公开
     */
    @GetMapping("/hot")
    public ApiResponse<List<CityVO>> getHotCities() {
        return ApiResponse.success(cityService.getHotCities());
    }

    /**
     * 根据省份获取城市列表
     * HTTP: GET /api/v1/cities/province/{province}
     * 请求参数：province（path，省份名称）
     * 响应：ApiResponse&lt;List&lt;CityVO&gt;&gt; —— 指定省份的城市列表
     * 认证要求：公开
     */
    @GetMapping("/province/{province}")
    public ApiResponse<List<CityVO>> getCitiesByProvince(@PathVariable String province) {
        return ApiResponse.success(cityService.getCitiesByProvince(province));
    }
}