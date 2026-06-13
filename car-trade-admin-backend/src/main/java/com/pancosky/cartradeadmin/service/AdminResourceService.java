package com.pancosky.cartradeadmin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pancosky.cartradeadmin.common.BusinessException;
import com.pancosky.cartradeadmin.dto.*;
import com.pancosky.cartradeadmin.entity.Banner;
import com.pancosky.cartradeadmin.entity.Config;
import com.pancosky.cartradeadmin.mapper.BannerMapper;
import com.pancosky.cartradeadmin.mapper.ConfigMapper;
import com.pancosky.cartradeadmin.vo.BannerVO;
import com.pancosky.cartradeadmin.vo.ConfigVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminResourceService {

    @Autowired
    private BannerMapper bannerMapper;

    @Autowired
    private ConfigMapper configMapper;

    public List<BannerVO> listBanners(String status) {
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            wrapper.eq(Banner::getStatus, status);
        }
        wrapper.orderByAsc(Banner::getSortOrder);
        List<Banner> banners = bannerMapper.selectList(wrapper);
        return banners.stream().map(this::toBannerVO).collect(Collectors.toList());
    }

    public List<ConfigVO> listConfigs() {
        List<Config> configs = configMapper.selectList(
                new LambdaQueryWrapper<Config>().orderByDesc(Config::getUpdatedAt));
        return configs.stream().map(c -> {
            ConfigVO vo = new ConfigVO();
            vo.setKey(c.getKey());
            vo.setContent(c.getContent());
            vo.setUpdatedAt(c.getUpdatedAt());
            return vo;
        }).collect(Collectors.toList());
    }

    public BannerVO createBanner(BannerCreateDTO dto) {
        Banner banner = new Banner();
        BeanUtils.copyProperties(dto, banner);
        banner.setStatus("ACTIVE");
        if (banner.getSortOrder() == null) {
            banner.setSortOrder(0);
        }
        bannerMapper.insert(banner);
        return toBannerVO(banner);
    }

    public BannerVO updateBanner(Long id, BannerUpdateDTO dto) {
        Banner banner = bannerMapper.selectById(id);
        if (banner == null) {
            throw new BusinessException(404, "Banner不存在");
        }
        if (dto.getTitle() != null) banner.setTitle(dto.getTitle());
        if (dto.getImageUrl() != null) banner.setImageUrl(dto.getImageUrl());
        if (dto.getType() != null) banner.setType(dto.getType());
        if (dto.getLinkUrl() != null) banner.setLinkUrl(dto.getLinkUrl());
        if (dto.getSortOrder() != null) banner.setSortOrder(dto.getSortOrder());
        if (dto.getStatus() != null) banner.setStatus(dto.getStatus());
        bannerMapper.updateById(banner);
        return toBannerVO(banner);
    }

    public void deleteBanner(Long id) {
        Banner banner = bannerMapper.selectById(id);
        if (banner == null) {
            throw new BusinessException(404, "Banner不存在");
        }
        bannerMapper.deleteById(id);
    }

    public void updateBannerStatus(Long id, String status) {
        Banner banner = bannerMapper.selectById(id);
        if (banner == null) {
            throw new BusinessException(404, "Banner不存在");
        }
        banner.setStatus(status);
        bannerMapper.updateById(banner);
    }

    @Transactional
    public void updateBannerSort(List<BannerSortItem> items) {
        for (BannerSortItem item : items) {
            Banner banner = bannerMapper.selectById(item.getId());
            if (banner != null) {
                banner.setSortOrder(item.getSortOrder());
                bannerMapper.updateById(banner);
            }
        }
    }

    public ConfigVO getConfig(String key) {
        Config config = configMapper.selectById(key);
        if (config == null) {
            throw new BusinessException(404, "配置不存在");
        }
        ConfigVO vo = new ConfigVO();
        vo.setKey(config.getKey());
        vo.setContent(config.getContent());
        vo.setUpdatedAt(config.getUpdatedAt());
        return vo;
    }

    public ConfigVO updateConfig(String key, ConfigUpdateDTO dto) {
        Config config = configMapper.selectById(key);
        if (config == null) {
            config = new Config();
            config.setKey(key);
            config.setContent(dto.getContent());
            config.setUpdatedAt(LocalDateTime.now());
            configMapper.insert(config);
        } else {
            config.setContent(dto.getContent());
            config.setUpdatedAt(LocalDateTime.now());
            configMapper.updateById(config);
        }
        ConfigVO vo = new ConfigVO();
        vo.setKey(config.getKey());
        vo.setContent(config.getContent());
        vo.setUpdatedAt(config.getUpdatedAt());
        return vo;
    }

    private BannerVO toBannerVO(Banner banner) {
        BannerVO vo = new BannerVO();
        BeanUtils.copyProperties(banner, vo);
        return vo;
    }
}
