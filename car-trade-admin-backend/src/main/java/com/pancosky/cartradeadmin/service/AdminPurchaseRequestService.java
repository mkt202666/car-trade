package com.pancosky.cartradeadmin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pancosky.cartradeadmin.common.BusinessException;
import com.pancosky.cartradeadmin.common.PageResult;
import com.pancosky.cartradeadmin.dto.PurchaseQueryDTO;
import com.pancosky.cartradeadmin.entity.AppPurchaseRequest;
import com.pancosky.cartradeadmin.entity.AppUser;
import com.pancosky.cartradeadmin.mapper.AppPurchaseRequestMapper;
import com.pancosky.cartradeadmin.mapper.AppUserMapper;
import com.pancosky.cartradeadmin.vo.PurchaseRequestDetailVO;
import com.pancosky.cartradeadmin.vo.PurchaseRequestVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminPurchaseRequestService {

    @Autowired
    private AppPurchaseRequestMapper appPurchaseRequestMapper;

    @Autowired
    private AppUserMapper appUserMapper;

    public PageResult<PurchaseRequestVO> getPurchaseList(PurchaseQueryDTO query) {
        LambdaQueryWrapper<AppPurchaseRequest> wrapper = new LambdaQueryWrapper<>();

        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            wrapper.and(w -> w.like(AppPurchaseRequest::getBrandName, query.getKeyword())
                    .or().like(AppPurchaseRequest::getSeriesName, query.getKeyword())
                    .or().like(AppPurchaseRequest::getModelName, query.getKeyword()));
        }
        if (query.getStatus() != null && !query.getStatus().isEmpty() && !"ALL".equalsIgnoreCase(query.getStatus())) {
            wrapper.eq(AppPurchaseRequest::getStatus, query.getStatus());
        }

        wrapper.orderByDesc(AppPurchaseRequest::getCreatedAt);

        Page<AppPurchaseRequest> page = appPurchaseRequestMapper.selectPage(
                new Page<>(query.getPage(), query.getSize()), wrapper);

        List<PurchaseRequestVO> voList = page.getRecords().stream().map(req -> {
            PurchaseRequestVO vo = new PurchaseRequestVO();
            vo.setId(req.getId());
            vo.setUserId(req.getUserId());
            vo.setBrandName(req.getBrandName());
            vo.setSeriesName(req.getSeriesName());
            vo.setModelName(req.getModelName());
            vo.setMinPrice(req.getPriceMin());
            vo.setMaxPrice(req.getPriceMax());
            vo.setYearMin(req.getYearFrom());
            vo.setYearMax(req.getYearTo());
            vo.setCityName(req.getCityName());
            vo.setEnergyType(req.getEnergyType());
            vo.setStatus(req.getStatus());
            vo.setCreatedAt(req.getCreatedAt());

            if (req.getUserId() != null) {
                AppUser user = appUserMapper.selectById(req.getUserId());
                if (user != null) {
                    vo.setUserName(user.getNickname());
                    vo.setUserPhone(maskPhone(user.getPhone()));
                }
            }

            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(voList, page.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }

    public PurchaseRequestDetailVO getPurchaseDetail(Long id) {
        AppPurchaseRequest req = appPurchaseRequestMapper.selectById(id);
        if (req == null) {
            throw new BusinessException(404, "求购信息不存在");
        }

        PurchaseRequestDetailVO vo = new PurchaseRequestDetailVO();
        vo.setId(req.getId());
        vo.setUserId(req.getUserId());
        vo.setBrandName(req.getBrandName());
        vo.setSeriesName(req.getSeriesName());
        vo.setModelName(req.getModelName());
        vo.setMinPrice(req.getPriceMin());
        vo.setMaxPrice(req.getPriceMax());
        vo.setYearMin(req.getYearFrom());
        vo.setYearMax(req.getYearTo());
        vo.setMileageMax(req.getMileageMax());
        vo.setColor(req.getColor());
        vo.setCityName(req.getCityName());
        vo.setDescription(req.getDescription());
        vo.setStatus(req.getStatus());
        vo.setCreatedAt(req.getCreatedAt());

        if (req.getUserId() != null) {
            AppUser user = appUserMapper.selectById(req.getUserId());
            if (user != null) {
                vo.setUserName(user.getNickname());
                vo.setUserPhone(maskPhone(user.getPhone()));
            }
        }

        return vo;
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
}
