package com.pancosky.newcartrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pancosky.newcartrade.common.PageResult;
import com.pancosky.newcartrade.converter.CarConverter;
import com.pancosky.newcartrade.dto.CarCreateDTO;
import com.pancosky.newcartrade.dto.CarQueryDTO;
import com.pancosky.newcartrade.dto.CarUpdateDTO;
import com.pancosky.newcartrade.entity.CarSource;
import com.pancosky.newcartrade.entity.UserFavorite;
import com.pancosky.newcartrade.exception.BusinessException;
import com.pancosky.newcartrade.mapper.CarMapper;
import com.pancosky.newcartrade.mapper.UserFavoriteMapper;
import com.pancosky.newcartrade.service.BrowsingHistoryService;
import com.pancosky.newcartrade.service.CarService;
import com.pancosky.newcartrade.service.cache.CarCacheService;
import com.pancosky.newcartrade.util.SecurityUtils;
import com.pancosky.newcartrade.vo.CarDetailVO;
import com.pancosky.newcartrade.vo.CarVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarMapper carMapper;
    private final UserFavoriteMapper userFavoriteMapper;
    private final CarCacheService carCacheService;
    private final BrowsingHistoryService browsingHistoryService;
    private final CarConverter carConverter;

    @Override
    public PageResult<CarVO> list(CarQueryDTO query) {
        LambdaQueryWrapper<CarSource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CarSource::getStatus, "PUBLISHED");
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(CarSource::getTitle, query.getKeyword());
        }
        if (query.getBrandId() != null) {
            wrapper.eq(CarSource::getBrandId, query.getBrandId());
        }
        if (query.getSeriesId() != null) {
            wrapper.eq(CarSource::getSeriesId, query.getSeriesId());
        }
        if (query.getPriceMin() != null) {
            wrapper.ge(CarSource::getPrice, query.getPriceMin());
        }
        if (query.getPriceMax() != null) {
            wrapper.le(CarSource::getPrice, query.getPriceMax());
        }
        if (StringUtils.hasText(query.getCityCode())) {
            wrapper.eq(CarSource::getCityCode, query.getCityCode());
        }
        if (StringUtils.hasText(query.getEnergyType())) {
            wrapper.eq(CarSource::getEnergyType, query.getEnergyType());
        }
        if (query.getDeposit() != null && query.getDeposit()) {
            wrapper.isNotNull(CarSource::getDeposit);
        }
        if ("price_asc".equals(query.getSort())) {
            wrapper.orderByAsc(CarSource::getPrice);
        } else if ("price_desc".equals(query.getSort())) {
            wrapper.orderByDesc(CarSource::getPrice);
        } else if ("mileage_asc".equals(query.getSort())) {
            wrapper.orderByAsc(CarSource::getMileage);
        } else {
            wrapper.orderByDesc(CarSource::getCreatedAt);
        }
        IPage<CarSource> page = new Page<>(query.getPage(), query.getSize());
        IPage<CarSource> result = carMapper.selectPage(page, wrapper);
        List<CarVO> list = result.getRecords().stream()
                .map(carConverter::toVO)
                .collect(Collectors.toList());
        return PageResult.of(list, result.getTotal(), query.getPage(), query.getSize());
    }

    @Override
    public CarDetailVO detail(Long id) {
        CarSource source = carMapper.selectById(id);
        if (source == null) throw new BusinessException("Car not found");
        carMapper.incrementViewCount(id);
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId != null) browsingHistoryService.record(userId, id);
        return carConverter.toDetailVO(source);
    }

    @Override
    @Transactional
    public CarVO create(CarCreateDTO dto) {
        CarSource entity = new CarSource();
        entity.setUserId(SecurityUtils.getCurrentUserId());
        entity.setBrandId(dto.getBrandId());
        entity.setSeriesId(dto.getSeriesId());
        entity.setModelId(dto.getModelId());
        entity.setYear(dto.getYear());
        entity.setMileage(dto.getMileage());
        entity.setPrice(dto.getPrice());
        entity.setDeposit(dto.getDeposit());
        entity.setColor(dto.getColor());
        entity.setCityCode(dto.getCityCode());
        entity.setUsageType(dto.getUsageType());
        entity.setOwnerType(dto.getOwnerType());
        entity.setIsMortgaged(dto.getIsMortgaged());
        entity.setIsInherited(dto.getIsInherited());
        entity.setDescription(dto.getDescription());
        carMapper.insert(entity);
        carCacheService.clearListCache();
        return carConverter.toVO(entity);
    }

    @Override
    @Transactional
    public CarVO update(Long id, CarUpdateDTO dto) {
        CarSource entity = carMapper.selectById(id);
        if (entity == null) throw new BusinessException("Car not found");
        if (dto.getBrandId() != null) entity.setBrandId(dto.getBrandId());
        if (dto.getSeriesId() != null) entity.setSeriesId(dto.getSeriesId());
        if (dto.getModelId() != null) entity.setModelId(dto.getModelId());
        if (dto.getYear() != null) entity.setYear(dto.getYear());
        if (dto.getMileage() != null) entity.setMileage(dto.getMileage());
        if (dto.getPrice() != null) entity.setPrice(dto.getPrice());
        if (dto.getDeposit() != null) entity.setDeposit(dto.getDeposit());
        if (dto.getColor() != null) entity.setColor(dto.getColor());
        if (dto.getCityCode() != null) entity.setCityCode(dto.getCityCode());
        if (dto.getUsageType() != null) entity.setUsageType(dto.getUsageType());
        if (dto.getOwnerType() != null) entity.setOwnerType(dto.getOwnerType());
        if (dto.getIsMortgaged() != null) entity.setIsMortgaged(dto.getIsMortgaged());
        if (dto.getIsInherited() != null) entity.setIsInherited(dto.getIsInherited());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        carMapper.updateById(entity);
        carCacheService.clearDetailCache(id);
        carCacheService.clearListCache();
        return carConverter.toVO(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        CarSource entity = carMapper.selectById(id);
        if (entity == null) throw new BusinessException("Car not found");
        carMapper.deleteById(id);
        carCacheService.clearDetailCache(id);
        carCacheService.clearListCache();
    }

    @Override
    @Transactional
    public void favorite(Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        UserFavorite existing = userFavoriteMapper.selectOne(null);
        if (existing == null) {
            UserFavorite uf = new UserFavorite();
            uf.setUserId(userId);
            uf.setCarId(id);
            userFavoriteMapper.insert(uf);
            carMapper.incrementFavoriteCount(id);
        }
    }

    @Override
    @Transactional
    public void unfavorite(Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        userFavoriteMapper.delete(null);
        carMapper.decrementFavoriteCount(id);
    }

    @Override
    public List<CarVO> recommend() {
        LambdaQueryWrapper<CarSource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CarSource::getStatus, "PUBLISHED");
        wrapper.orderByDesc(CarSource::getViewCount);
        wrapper.last("LIMIT 10");
        List<CarSource> sources = carMapper.selectList(wrapper);
        return sources.stream().map(carConverter::toVO).collect(Collectors.toList());
    }

    @Override
    public void export(String country) {
        log.info("Export car data for country: {}", country);
    }

    @Override
    public void downloadImage(Long carId, Long imageId) {
        log.info("Download image {} for car: {}", imageId, carId);
    }

    @Override
    public void contactSeller(Long carId) {
        CarSource source = carMapper.selectById(carId);
        if (source == null) throw new BusinessException("Car not found");
        log.info("Contact seller {} for car: {}", source.getUserId(), carId);
    }
}
