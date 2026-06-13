package com.pancosky.newcartrade.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.common.AuthLevel;
import com.pancosky.newcartrade.common.RequiresAuth;
import com.pancosky.newcartrade.entity.Banner;
import com.pancosky.newcartrade.mapper.BannerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Banner 广告位控制器（移动端只读）
 * 描述：提供首页轮播 Banner 和弹窗广告的查询接口
 * 基础路径：/api/v1/banners
 * 认证要求：公开（未登录可访问）
 */
@RestController
@RequestMapping("/api/v1/banners")
@RequiredArgsConstructor
@RequiresAuth(AuthLevel.PUBLIC)
public class BannerController {

    private final BannerMapper bannerMapper;

    /**
     * 获取生效中的轮播 Banner 列表
     * GET /api/v1/banners
     * 返回 type=BANNER 且 status=ACTIVE 的记录，按 sortOrder 升序排列
     * 自动过滤已过期（endAt < now）的 Banner
     */
    @GetMapping
    public ApiResponse<List<Banner>> listBanners() {
        LocalDateTime now = LocalDateTime.now();
        List<Banner> banners = bannerMapper.selectList(
                new LambdaQueryWrapper<Banner>()
                        .eq(Banner::getType, "BANNER")
                        .eq(Banner::getStatus, "ACTIVE")
                        .isNull(Banner::getDeletedAt)
                        .and(w -> w.isNull(Banner::getStartAt).or().le(Banner::getStartAt, now))
                        .and(w -> w.isNull(Banner::getEndAt).or().ge(Banner::getEndAt, now))
                        .orderByAsc(Banner::getSortOrder)
        );
        return ApiResponse.success(banners);
    }

    /**
     * 获取生效中的弹窗广告
     * GET /api/v1/banners/popup
     * 返回 type=POPUP 且 status=ACTIVE 的记录，取 sortOrder 最小的一条
     */
    @GetMapping("/popup")
    public ApiResponse<Banner> getPopup() {
        LocalDateTime now = LocalDateTime.now();
        Banner popup = bannerMapper.selectOne(
                new LambdaQueryWrapper<Banner>()
                        .eq(Banner::getType, "POPUP")
                        .eq(Banner::getStatus, "ACTIVE")
                        .isNull(Banner::getDeletedAt)
                        .and(w -> w.isNull(Banner::getStartAt).or().le(Banner::getStartAt, now))
                        .and(w -> w.isNull(Banner::getEndAt).or().ge(Banner::getEndAt, now))
                        .orderByAsc(Banner::getSortOrder)
                        .last("LIMIT 1")
        );
        return ApiResponse.success(popup);
    }
}
