package com.pancosky.newcartrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pancosky.newcartrade.common.PageResult;
import com.pancosky.newcartrade.dto.PurchaseDemandCreateDTO;
import com.pancosky.newcartrade.entity.PurchaseDemand;
import com.pancosky.newcartrade.entity.User;
import com.pancosky.newcartrade.exception.BusinessException;
import com.pancosky.newcartrade.mapper.PurchaseDemandMapper;
import com.pancosky.newcartrade.mapper.UserMapper;
import com.pancosky.newcartrade.service.PurchaseDemandService;
import com.pancosky.newcartrade.util.SecurityUtils;
import com.pancosky.newcartrade.vo.PurchaseDemandVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseDemandServiceImpl implements PurchaseDemandService {

    private final PurchaseDemandMapper demandMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public PurchaseDemandVO create(PurchaseDemandCreateDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) throw new BusinessException("请先登录");

        PurchaseDemand demand = new PurchaseDemand();
        demand.setUserId(userId);
        demand.setBrandName(dto.getBrandName());
        demand.setSeriesName(dto.getSeriesName());
        demand.setModelName(dto.getModelName());
        demand.setYearFrom(dto.getYearFrom());
        demand.setYearTo(dto.getYearTo());
        demand.setPriceMin(dto.getPriceMin());
        demand.setPriceMax(dto.getPriceMax());
        demand.setMileageMax(dto.getMileageMax());
        demand.setColor(dto.getColor());
        demand.setCityCode(dto.getCityCode());
        demand.setCityName(dto.getCityName());
        demand.setEnergyType(dto.getEnergyType());
        demand.setDescription(dto.getDescription());
        demand.setStatus("ACTIVE");
        demandMapper.insert(demand);

        log.info("User {} created purchase demand {}", userId, demand.getId());
        return toVO(demand);
    }

    @Override
    public PageResult<PurchaseDemandVO> list(Integer page, Integer size) {
        LambdaQueryWrapper<PurchaseDemand> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PurchaseDemand::getStatus, "ACTIVE")
               .orderByDesc(PurchaseDemand::getCreatedAt);

        Page<PurchaseDemand> pageResult = demandMapper.selectPage(new Page<>(page, size), wrapper);
        List<PurchaseDemandVO> vos = pageResult.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        return PageResult.of(vos, pageResult.getTotal(), page, size);
    }

    @Override
    public PageResult<PurchaseDemandVO> myDemands(Integer page, Integer size) {
        Long userId = SecurityUtils.getCurrentUserId();
        LambdaQueryWrapper<PurchaseDemand> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PurchaseDemand::getUserId, userId)
               .orderByDesc(PurchaseDemand::getCreatedAt);

        Page<PurchaseDemand> pageResult = demandMapper.selectPage(new Page<>(page, size), wrapper);
        List<PurchaseDemandVO> vos = pageResult.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        return PageResult.of(vos, pageResult.getTotal(), page, size);
    }

    @Override
    @Transactional
    public void cancel(Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        PurchaseDemand demand = demandMapper.selectById(id);
        if (demand == null) throw new BusinessException("求购不存在");
        if (!demand.getUserId().equals(userId)) throw new BusinessException("只能取消自己的求购");
        demand.setStatus("CANCELLED");
        demandMapper.updateById(demand);
    }

    private PurchaseDemandVO toVO(PurchaseDemand d) {
        PurchaseDemandVO vo = new PurchaseDemandVO();
        vo.setId(d.getId());
        vo.setUserId(d.getUserId());
        vo.setBrandName(d.getBrandName());
        vo.setSeriesName(d.getSeriesName());
        vo.setModelName(d.getModelName());
        vo.setYearFrom(d.getYearFrom());
        vo.setYearTo(d.getYearTo());
        vo.setPriceMin(d.getPriceMin());
        vo.setPriceMax(d.getPriceMax());
        vo.setMileageMax(d.getMileageMax());
        vo.setColor(d.getColor());
        vo.setCityName(d.getCityName());
        vo.setEnergyType(d.getEnergyType());
        vo.setDescription(d.getDescription());
        vo.setStatus(d.getStatus());
        vo.setCreatedAt(d.getCreatedAt());

        User user = userMapper.selectById(d.getUserId());
        if (user != null) {
            vo.setUserName(user.getNickname());
            vo.setUserAvatar(user.getAvatarUrl());
        }
        return vo;
    }

    @Override
    public void delete(Long id) {
        demandMapper.deleteById(id);
    }
}
