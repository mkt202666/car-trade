package com.pancosky.newcartrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pancosky.newcartrade.common.PageResult;
import com.pancosky.newcartrade.converter.CarConverter;
import com.pancosky.newcartrade.dto.CarCreateDTO;
import com.pancosky.newcartrade.dto.CarQueryDTO;
import com.pancosky.newcartrade.dto.CarUpdateDTO;
import com.pancosky.newcartrade.entity.*;
import com.pancosky.newcartrade.exception.BusinessException;
import com.pancosky.newcartrade.mapper.*;
import com.pancosky.newcartrade.service.BrowsingHistoryService;
import com.pancosky.newcartrade.service.CarService;
import com.pancosky.newcartrade.util.SecurityUtils;
import com.pancosky.newcartrade.vo.CarDetailVO;
import com.pancosky.newcartrade.vo.CarVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarMapper carMapper;
    private final CarImageMapper carImageMapper;
    private final CarInspectionMapper carInspectionMapper;
    private final BrandMapper brandMapper;
    private final SeriesMapper seriesMapper;
    private final ModelMapper modelMapper;
    private final UserMapper userMapper;
    private final UserFavoriteMapper userFavoriteMapper;
    private final UserFollowMapper userFollowMapper;
    private final BrowsingHistoryService browsingHistoryService;

    @Override
    public PageResult<CarVO> list(CarQueryDTO query) {
        LambdaQueryWrapper<CarSource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CarSource::getStatus, "ACTIVE");
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.and(w -> w.like(CarSource::getTitle, query.getKeyword())
                    .or().like(CarSource::getCityName, query.getKeyword()));
        }
        if (query.getBrandId() != null) wrapper.eq(CarSource::getBrandId, query.getBrandId());
        if (query.getSeriesId() != null) wrapper.eq(CarSource::getSeriesId, query.getSeriesId());
        
        // 处理价格筛选（兼容两种参数格式）
        BigDecimal priceMin = query.getPriceMin() != null ? query.getPriceMin() : query.getMinPrice();
        BigDecimal priceMax = query.getPriceMax() != null ? query.getPriceMax() : query.getMaxPrice();
        if (priceMin != null) wrapper.ge(CarSource::getPrice, priceMin.multiply(new BigDecimal("10000")));
        if (priceMax != null) wrapper.le(CarSource::getPrice, priceMax.multiply(new BigDecimal("10000")));
        
        // 处理车龄筛选（根据年份计算）
        Integer ageMin = query.getAgeMin() != null ? query.getAgeMin() : query.getMinAge();
        Integer ageMax = query.getAgeMax() != null ? query.getAgeMax() : query.getMaxAge();
        int currentYear = java.time.Year.now().getValue();
        if (ageMin != null) wrapper.le(CarSource::getYear, currentYear - ageMin);
        if (ageMax != null) wrapper.ge(CarSource::getYear, currentYear - ageMax);
        
        // 处理里程筛选
        BigDecimal mileageMin = query.getMileageMin() != null ? query.getMileageMin() : query.getMinMileage();
        BigDecimal mileageMax = query.getMileageMax() != null ? query.getMileageMax() : query.getMaxMileage();
        if (mileageMin != null) wrapper.ge(CarSource::getMileage, mileageMin.multiply(new BigDecimal("10000")).intValue());
        if (mileageMax != null) wrapper.le(CarSource::getMileage, mileageMax.multiply(new BigDecimal("10000")).intValue());
        
        // 处理变速箱筛选
        if (StringUtils.hasText(query.getTransmission())) wrapper.eq(CarSource::getTransmission, query.getTransmission());
        
        if (StringUtils.hasText(query.getCityCode())) wrapper.eq(CarSource::getCityCode, query.getCityCode());
        if (StringUtils.hasText(query.getEnergyType())) wrapper.eq(CarSource::getEnergyType, query.getEnergyType());
        if (query.getDeposit() != null && query.getDeposit()) wrapper.isNotNull(CarSource::getDeposit);

        if ("price_asc".equals(query.getSort())) {
            wrapper.orderByAsc(CarSource::getPrice);
        } else if ("price_desc".equals(query.getSort())) {
            wrapper.orderByDesc(CarSource::getPrice);
        } else if ("mileage_asc".equals(query.getSort())) {
            wrapper.orderByAsc(CarSource::getMileage);
        } else if ("mileage_desc".equals(query.getSort())) {
            wrapper.orderByDesc(CarSource::getMileage);
        } else {
            wrapper.orderByDesc(CarSource::getCreatedAt);
        }

        int page = query.getPage() == null || query.getPage() < 1 ? 1 : query.getPage();
        int size = query.getSize() == null || query.getSize() < 1 ? 10 : query.getSize();
        IPage<CarSource> pageInfo = carMapper.selectPage(new Page<>(page, size), wrapper);

        List<CarVO> vos = assembleCarVOs(pageInfo.getRecords());
        return PageResult.of(vos, pageInfo.getTotal(), page, size);
    }

    @Override
    public CarDetailVO detail(Long id) {
        CarSource source = carMapper.selectById(id);
        if (source == null) throw new BusinessException("Car not found");

        carMapper.incrementViewCount(id);
        source.setViewCount((source.getViewCount() == null ? 0L : source.getViewCount()) + 1);

        // 浏览历史记录
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId != null) {
            try { browsingHistoryService.record(currentUserId, id); } catch (Exception e) { log.warn("browse history failed", e); }
        }

        // 拼装品牌信息
        assembleBrandNames(Collections.singletonList(source));

        // 图片
        List<CarImage> images = carImageMapper.selectList(
                new LambdaQueryWrapper<CarImage>().eq(CarImage::getCarId, id));
        // 检测报告
        CarInspection inspection = carInspectionMapper.selectOne(
                new LambdaQueryWrapper<CarInspection>().eq(CarInspection::getCarId, id).last("LIMIT 1"));
        // 卖家
        User seller = source.getUserId() == null ? null : userMapper.selectById(source.getUserId());

        Boolean favorited = null;
        Boolean followed = null;
        if (currentUserId != null) {
            UserFavorite uf = userFavoriteMapper.selectOne(new LambdaQueryWrapper<UserFavorite>()
                    .eq(UserFavorite::getUserId, currentUserId).eq(UserFavorite::getCarId, id).last("LIMIT 1"));
            favorited = uf != null;
            if (seller != null) {
                UserFollow fl = userFollowMapper.selectOne(new LambdaQueryWrapper<UserFollow>()
                        .eq(UserFollow::getUserId, currentUserId).eq(UserFollow::getFollowUserId, seller.getId()).last("LIMIT 1"));
                followed = fl != null;
            }
        }

        if (!images.isEmpty()) {
            images.sort(Comparator.comparing(i -> i.getSortOrder() == null ? 0 : i.getSortOrder()));
            source.setCoverImage(images.get(0).getImageUrl());
        }

        return CarConverter.toDetailVO(source, images, inspection, seller, favorited, followed);
    }

    @Override
    @Transactional
    public CarVO create(CarCreateDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        CarSource entity = new CarSource();
        entity.setUserId(userId);
        entity.setBrandId(dto.getBrandId());
        entity.setSeriesId(dto.getSeriesId());
        entity.setModelId(dto.getModelId());
        entity.setTitle(dto.getTitle() != null ? dto.getTitle() : buildDefaultTitle(dto));
        entity.setVin(dto.getVin());  // 车架号
        entity.setYear(dto.getYear());
        entity.setMileage(dto.getMileage());
        entity.setPrice(dto.getPrice());
        entity.setPricingType(dto.getPricingType() != null ? dto.getPricingType() : "FIXED");  // 报价方式
        entity.setStartingPrice(dto.getStartingPrice());  // 起拍价
        entity.setCeilingPrice(dto.getCeilingPrice());  // 封顶价
        entity.setBidIncrement(dto.getBidIncrement());  // 加价幅度
        entity.setDeposit(dto.getDeposit());
        entity.setColor(dto.getColor());
        entity.setCityCode(dto.getCityCode());
        entity.setCityName(dto.getCityName());
        entity.setEnergyType(dto.getEnergyType());
        entity.setUsageType(dto.getUsageType());
        entity.setOwnerType(dto.getOwnerType());
        entity.setIsMortgaged(dto.getIsMortgaged());
        entity.setIsInherited(dto.getIsInherited());
        entity.setRegistrationDate(dto.getRegistrationDate());
        entity.setInsuranceExpiry(dto.getInsuranceExpiry());
        entity.setInspectionExpiry(dto.getInspectionExpiry());
        entity.setProductionDate(dto.getProductionDate());
        entity.setKeyCount(dto.getKeyCount());
        entity.setDescription(dto.getDescription());
        entity.setInspectionReportType(dto.getInspectionReportType());  // 检测报告类型
        entity.setInspectionReportUrl(dto.getInspectionReportUrl());  // 检测报告URL
        entity.setCertificateMaterials(dto.getCertificateMaterials());  // 证件材料
        entity.setSupportLockNegotiation(dto.getSupportLockNegotiation() != null ? dto.getSupportLockNegotiation() : false);  // 支持锁车洽谈
        entity.setAiAutoPromote(dto.getAiAutoPromote() != null ? dto.getAiAutoPromote() : false);  // AI自动推广
        entity.setIsDraft(dto.getIsDraft() != null ? dto.getIsDraft() : false);  // 是否草稿
        entity.setStatus("ACTIVE");
        entity.setPublishedAt(LocalDateTime.now());
        entity.setViewCount(0L);
        entity.setFavoriteCount(0);
        carMapper.insert(entity);

        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            int order = 0;
            for (String url : dto.getImages()) {
                CarImage img = new CarImage();
                img.setCarId(entity.getId());
                img.setImageUrl(url);
                img.setImageType("exterior");
                img.setSortOrder(order++);
                carImageMapper.insert(img);
            }
        }
        // 详情组装
        assembleBrandNames(Collections.singletonList(entity));
        if (dto.getImages() != null && !dto.getImages().isEmpty()) entity.setCoverImage(dto.getImages().get(0));
        return CarConverter.toVO(entity);
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
        if (dto.getCityName() != null) entity.setCityName(dto.getCityName());
        if (dto.getEnergyType() != null) entity.setEnergyType(dto.getEnergyType());
        if (dto.getUsageType() != null) entity.setUsageType(dto.getUsageType());
        if (dto.getOwnerType() != null) entity.setOwnerType(dto.getOwnerType());
        if (dto.getIsMortgaged() != null) entity.setIsMortgaged(dto.getIsMortgaged());
        if (dto.getIsInherited() != null) entity.setIsInherited(dto.getIsInherited());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getTitle() != null) entity.setTitle(dto.getTitle());
        carMapper.updateById(entity);
        assembleBrandNames(Collections.singletonList(entity));
        return CarConverter.toVO(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        CarSource entity = carMapper.selectById(id);
        if (entity == null) throw new BusinessException("Car not found");
        carMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void favorite(Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) throw new BusinessException("Please login first");
        CarSource car = carMapper.selectById(id);
        if (car == null) throw new BusinessException("Car not found");

        UserFavorite existing = userFavoriteMapper.selectOne(new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId).eq(UserFavorite::getCarId, id).last("LIMIT 1"));
        if (existing == null) {
            UserFavorite uf = new UserFavorite();
            uf.setUserId(userId);
            uf.setCarId(id);
            uf.setCreatedAt(LocalDateTime.now());
            userFavoriteMapper.insert(uf);
            carMapper.incrementFavoriteCount(id);
        }
    }

    @Override
    @Transactional
    public void unfavorite(Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) throw new BusinessException("Please login first");
        int deleted = userFavoriteMapper.delete(new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId).eq(UserFavorite::getCarId, id));
        if (deleted > 0) carMapper.decrementFavoriteCount(id);
    }

    @Override
    public List<CarVO> recommend() {
        LambdaQueryWrapper<CarSource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CarSource::getStatus, "ACTIVE");
        wrapper.orderByDesc(CarSource::getViewCount);
        wrapper.last("LIMIT 10");
        List<CarSource> sources = carMapper.selectList(wrapper);
        return assembleCarVOs(sources);
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

    // ================= helpers =================

    private List<CarVO> assembleCarVOs(List<CarSource> sources) {
        if (sources == null || sources.isEmpty()) return new ArrayList<>();
        assembleBrandNames(sources);

        Set<Long> ids = sources.stream().map(CarSource::getId).collect(Collectors.toSet());
        List<CarImage> allImages = carImageMapper.selectList(
                new LambdaQueryWrapper<CarImage>().in(CarImage::getCarId, ids));
        Map<Long, List<CarImage>> imageMap = allImages.stream()
                .collect(Collectors.groupingBy(CarImage::getCarId));

        return sources.stream().map(s -> {
            List<CarImage> imgs = imageMap.get(s.getId());
            if (imgs != null && !imgs.isEmpty()) {
                imgs.sort(Comparator.comparing(i -> i.getSortOrder() == null ? 0 : i.getSortOrder()));
                s.setCoverImage(imgs.get(0).getImageUrl());
            }
            return CarConverter.toVO(s, imgs);
        }).collect(Collectors.toList());
    }

    private void assembleBrandNames(List<CarSource> sources) {
        if (sources == null || sources.isEmpty()) return;
        Set<Integer> brandIds = new HashSet<>();
        Set<Integer> seriesIds = new HashSet<>();
        Set<Integer> modelIds = new HashSet<>();
        for (CarSource s : sources) {
            if (s.getBrandId() != null) brandIds.add(s.getBrandId());
            if (s.getSeriesId() != null) seriesIds.add(s.getSeriesId());
            if (s.getModelId() != null) modelIds.add(s.getModelId());
        }
        Map<Integer, String> brandMap = mapNames(brandMapper, brandIds, Brand::getId, Brand::getName);
        Map<Integer, String> seriesMap = mapNames(seriesMapper, seriesIds, Series::getId, Series::getName);
        Map<Integer, String> modelMap = mapNames(modelMapper, modelIds, Model::getId, Model::getName);
        for (CarSource s : sources) {
            s.setBrandName(brandMap.get(s.getBrandId()));
            s.setSeriesName(seriesMap.get(s.getSeriesId()));
            s.setModelName(modelMap.get(s.getModelId()));
            if (!StringUtils.hasText(s.getTitle())) {
                s.setTitle(buildDefaultTitle(s));
            }
        }
    }

    private <T, K extends java.io.Serializable> Map<K, String> mapNames(
            com.baomidou.mybatisplus.core.mapper.BaseMapper<T> mapper,
            Set<K> ids,
            java.util.function.Function<T, K> idFn,
            java.util.function.Function<T, String> nameFn) {
        if (ids == null || ids.isEmpty()) return Collections.emptyMap();
        List<T> rows = mapper.selectBatchIds(ids);
        Map<K, String> map = new HashMap<>();
        for (T r : rows) {
            K k = idFn.apply(r);
            String n = nameFn.apply(r);
            if (k != null && n != null) map.put(k, n);
        }
        return map;
    }

    private String buildDefaultTitle(CarSource s) {
        StringBuilder sb = new StringBuilder();
        if (s.getBrandName() != null) sb.append(s.getBrandName()).append(" ");
        if (s.getSeriesName() != null) sb.append(s.getSeriesName()).append(" ");
        if (s.getModelName() != null) sb.append(s.getModelName()).append(" ");
        if (s.getYear() != null) sb.append(s.getYear()).append("款 ");
        if (s.getCityName() != null) sb.append(" · ").append(s.getCityName());
        String title = sb.toString().trim();
        return title.isEmpty() ? ("车源 #" + s.getId()) : title;
    }

    private String buildDefaultTitle(CarCreateDTO dto) {
        StringBuilder sb = new StringBuilder();
        if (dto.getBrandId() != null) {
            Brand b = brandMapper.selectById(dto.getBrandId());
            if (b != null) sb.append(b.getName()).append(" ");
        }
        if (dto.getSeriesId() != null) {
            Series s = seriesMapper.selectById(dto.getSeriesId());
            if (s != null) sb.append(s.getName()).append(" ");
        }
        if (dto.getYear() != null) sb.append(dto.getYear()).append("款");
        String title = sb.toString().trim();
        return title.isEmpty() ? "新车源" : title;
    }
}
