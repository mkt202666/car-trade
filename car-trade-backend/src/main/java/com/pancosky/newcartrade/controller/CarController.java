package com.pancosky.newcartrade.controller;

import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.common.PageResult;
import com.pancosky.newcartrade.dto.CarCreateDTO;
import com.pancosky.newcartrade.dto.CarQueryDTO;
import com.pancosky.newcartrade.dto.CarUpdateDTO;
import com.pancosky.newcartrade.service.CarService;
import com.pancosky.newcartrade.vo.CarDetailVO;
import com.pancosky.newcartrade.vo.CarVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 车源管理控制器
 * 描述：提供车源列表查询、详情查看、车源发布/修改/下架、收藏、推荐、出口、图片下载及联系卖家等接口。
 * 基础路径：/api/v1/cars
 * 认证要求：列表、详情、推荐、导出等 GET 接口支持未登录访问；发布、修改、删除、收藏、联系卖家需登录。
 * 权限控制：卖家对自己发布的车源拥有编辑/删除权限；买家对任意车源拥有查看/收藏权限。
 * 数据范围：默认仅返回已发布（非草稿）车源；/my（若存在）返回当前用户车源。
 */
@RestController
@RequestMapping("/api/v1/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    /**
     * 分页查询车源列表
     * HTTP: GET /api/v1/cars
     * 请求参数：CarQueryDTO（keyword、brandId、seriesId、priceMin/Max、ageMin/Max、mileageMin/Max、
     *         transmission、cityCode、energyType、exportCountry、region、deposit、sort、page、size）
     * 响应：ApiResponse&lt;PageResult&lt;CarVO&gt;&gt; —— 分页列表，包含总数、当前页数据、总页数等。
     * 认证要求：公开（未登录可访问）。
     * 限流：默认每个 IP 每分钟 60 次。
     */
    @GetMapping
    public ApiResponse<PageResult<CarVO>> list(CarQueryDTO params) {
        return ApiResponse.success(carService.list(params));
    }

    /**
     * 查看车源详情
     * HTTP: GET /api/v1/cars/{id}
     * 请求参数：id（path，车源ID）
     * 响应：ApiResponse&lt;CarDetailVO&gt; —— 车源完整信息，包含品牌/车系、图片、定价、检测报告、联系方式等。
     * 认证要求：公开；若用户已登录，将记录浏览历史。
     * 特殊处理：访问一次，浏览次数 +1。
     */
    @GetMapping("/{id}")
    public ApiResponse<CarDetailVO> detail(@PathVariable Long id) {
        return ApiResponse.success(carService.detail(id));
    }

    /**
     * 发布新车源
     * HTTP: POST /api/v1/cars
     * 请求参数：CarCreateDTO（JSON body），包含车辆基本信息、图片列表、检测信息、定价方式等。
     * 响应：ApiResponse&lt;CarVO&gt; —— 成功返回创建后的车源信息。
     * 认证要求：必须登录（Bearer Token）。
     * 特殊处理：isDraft=true 时保存为草稿，不进入列表展示。
     */
    @PostMapping
    public ApiResponse<CarVO> create(@RequestBody CarCreateDTO dto) {
        return ApiResponse.success(carService.create(dto));
    }

    /**
     * 修改车源信息
     * HTTP: PUT /api/v1/cars/{id}
     * 请求参数：id（path，车源ID）；CarUpdateDTO（JSON body）
     * 响应：ApiResponse&lt;CarVO&gt; —— 成功返回更新后的车源信息。
     * 认证要求：必须登录，且必须是车源的发布者或管理员。
     */
    @PutMapping("/{id}")
    public ApiResponse<CarVO> update(@PathVariable Long id, @RequestBody CarUpdateDTO dto) {
        return ApiResponse.success(carService.update(id, dto));
    }

    /**
     * 删除/下架车源
     * HTTP: DELETE /api/v1/cars/{id}
     * 请求参数：id（path，车源ID）
     * 响应：ApiResponse&lt;Void&gt; —— 仅返回状态码和消息。
     * 认证要求：必须登录，且必须是车源的发布者或管理员。
     * 注意：实际是否物理删除由业务层决定（通常为软删除或更新状态为 OFFLINE）。
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        carService.delete(id);
        return ApiResponse.success();
    }

    /**
     * 收藏车源
     * HTTP: POST /api/v1/cars/{id}/favorite
     * 请求参数：id（path，车源ID）
     * 响应：ApiResponse&lt;Void&gt;
     * 认证要求：必须登录。
     */
    @PostMapping("/{id}/favorite")
    public ApiResponse<Void> favorite(@PathVariable Long id) {
        carService.favorite(id);
        return ApiResponse.success();
    }

    /**
     * 取消收藏车源
     * HTTP: DELETE /api/v1/cars/{id}/favorite
     * 请求参数：id（path，车源ID）
     * 响应：ApiResponse&lt;Void&gt;
     * 认证要求：必须登录。
     */
    @DeleteMapping("/{id}/favorite")
    public ApiResponse<Void> unfavorite(@PathVariable Long id) {
        carService.unfavorite(id);
        return ApiResponse.success();
    }

    /**
     * 推荐车源
     * HTTP: GET /api/v1/cars/recommend
     * 响应：ApiResponse&lt;List&lt;CarVO&gt;&gt; —— 平台推荐的车源列表（如热门、新上架）。
     * 认证要求：公开。
     */
    @GetMapping("/recommend")
    public ApiResponse<List<CarVO>> recommend() {
        return ApiResponse.success(carService.recommend());
    }

    /**
     * 出口车源列表（按国家）
     * HTTP: GET /api/v1/cars/export?country=RU
     * 请求参数：country（query，国家代码，如 RU/AE/US）
     * 响应：ApiResponse&lt;List&lt;CarVO&gt;&gt; —— 支持出口到指定国家的车源列表。
     * 认证要求：公开。
     */
    @GetMapping("/export")
    public ApiResponse<List<CarVO>> export(@RequestParam String country) {
        return ApiResponse.success(carService.export(country));
    }

    /**
     * 下载车源图片
     * HTTP: GET /api/v1/cars/{id}/images/{imageId}/download
     * 请求参数：id（path，车源ID）；imageId（path，车源图片ID）
     * 响应：ApiResponse&lt;String&gt; —— 返回可访问的图片下载 URL（通常带签名、有时效）。
     * 认证要求：登录用户可下载原图；未登录可能返回缩略图。
     */
    @GetMapping("/{id}/images/{imageId}/download")
    public ApiResponse<String> downloadImage(@PathVariable Long id, @PathVariable Long imageId) {
        return ApiResponse.success(carService.downloadImage(id, imageId));
    }

    /**
     * 联系卖家（获取联系方式/发起会话）
     * HTTP: POST /api/v1/cars/{id}/contact
     * 请求参数：id（path，车源ID）
     * 响应：ApiResponse&lt;Map&lt;String, Object&gt;&gt; —— 包含卖家公开信息、电话、聊天会话入口等。
     * 认证要求：必须登录；需要绑定手机号才能查看卖家完整联系方式。
     */
    @PostMapping("/{id}/contact")
    public ApiResponse<Map<String, Object>> contactSeller(@PathVariable Long id) {
        return ApiResponse.success(carService.contactSeller(id));
    }
}
