package com.pancosky.cartradeadmin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pancosky.cartradeadmin.common.BusinessException;
import com.pancosky.cartradeadmin.common.PageResult;
import com.pancosky.cartradeadmin.dto.PurchaseQueryDTO;
import com.pancosky.cartradeadmin.entity.AppCarSource;
import com.pancosky.cartradeadmin.entity.AppPurchaseRequest;
import com.pancosky.cartradeadmin.entity.AppUser;
import com.pancosky.cartradeadmin.entity.AdminCarBrand;
import com.pancosky.cartradeadmin.mapper.AppCarSourceMapper;
import com.pancosky.cartradeadmin.mapper.AppPurchaseRequestMapper;
import com.pancosky.cartradeadmin.mapper.AppUserMapper;
import com.pancosky.cartradeadmin.mapper.AdminCarBrandMapper;
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

    @Autowired
    private AppCarSourceMapper appCarSourceMapper;

    @Autowired
    private AdminCarBrandMapper adminCarBrandMapper;

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

    public void closePurchaseRequest(Long id, String reason) {
        AppPurchaseRequest pr = appPurchaseRequestMapper.selectById(id);
        if (pr == null) {
            throw new BusinessException(404, "求购需求不存在");
        }

        if (!"ACTIVE".equals(pr.getStatus())) {
            throw new BusinessException("该求购已关闭或已完成");
        }

        pr.setStatus("CLOSED");
        appPurchaseRequestMapper.updateById(pr);

        log.info("[AdminPurchaseRequest] Purchase request {} closed by admin, reason: {}", id, reason);
    }

    public int matchPurchaseRequest(Long id) {
        AppPurchaseRequest pr = appPurchaseRequestMapper.selectById(id);
        if (pr == null) {
            throw new BusinessException(404, "求购需求不存在");
        }

        if (!"ACTIVE".equals(pr.getStatus())) {
            throw new BusinessException("该求购已关闭或已完成");
        }

        LambdaQueryWrapper<AppCarSource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AppCarSource::getStatus, "ON_SALE");

        if (pr.getBrandName() != null && !pr.getBrandName().isEmpty()) {
            LambdaQueryWrapper<AdminCarBrand> brandWrapper = new LambdaQueryWrapper<>();
            brandWrapper.eq(AdminCarBrand::getName, pr.getBrandName());
            List<AdminCarBrand> brands = adminCarBrandMapper.selectList(brandWrapper);
            if (!brands.isEmpty()) {
                List<Integer> brandIds = brands.stream().map(AdminCarBrand::getId).collect(Collectors.toList());
                wrapper.in(AppCarSource::getBrandId, brandIds);
            }
        }

        if (pr.getPriceMin() != null) {
            wrapper.ge(AppCarSource::getPrice, pr.getPriceMin());
        }
        if (pr.getPriceMax() != null) {
            wrapper.le(AppCarSource::getPrice, pr.getPriceMax());
        }

        wrapper.last("LIMIT 10");
        List<AppCarSource> matches = appCarSourceMapper.selectList(wrapper);
        int matchCount = matches.size();

        pr.setStatus("MATCHED");
        appPurchaseRequestMapper.updateById(pr);

        log.info("[AdminPurchaseRequest] Purchase request {} matched with {} car sources", id, matchCount);
        return matchCount;
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
}
