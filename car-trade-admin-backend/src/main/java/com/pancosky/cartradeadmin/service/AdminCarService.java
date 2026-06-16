package com.pancosky.cartradeadmin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pancosky.cartradeadmin.common.BusinessException;
import com.pancosky.cartradeadmin.common.PageResult;
import com.pancosky.cartradeadmin.dto.CarQueryDTO;
import com.pancosky.cartradeadmin.dto.CarStatusUpdateDTO;
import com.pancosky.cartradeadmin.dto.CarUpdateDTO;
import com.pancosky.cartradeadmin.dto.CarViolateDTO;
import com.pancosky.cartradeadmin.entity.AppCarImage;
import com.pancosky.cartradeadmin.entity.AppCarInspection;
import com.pancosky.cartradeadmin.entity.AppCarSource;
import com.pancosky.cartradeadmin.entity.AppUser;
import com.pancosky.cartradeadmin.event.MobileNotification;
import com.pancosky.cartradeadmin.mapper.AppCarImageMapper;
import com.pancosky.cartradeadmin.mapper.AppCarInspectionMapper;
import com.pancosky.cartradeadmin.mapper.AppCarSourceMapper;
import com.pancosky.cartradeadmin.mapper.AppUserMapper;
import com.pancosky.cartradeadmin.vo.CarDetailVO;
import com.pancosky.cartradeadmin.vo.CarVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminCarService {

    @Autowired
    private AppCarSourceMapper appCarSourceMapper;

    @Autowired
    private AppUserMapper appUserMapper;

    @Autowired
    private AdminNotificationService adminNotificationService;

    @Autowired
    private AppCarImageMapper appCarImageMapper;

    @Autowired
    private AppCarInspectionMapper appCarInspectionMapper;

    public PageResult<CarVO> getCarList(CarQueryDTO query) {
        LambdaQueryWrapper<AppCarSource> wrapper = new LambdaQueryWrapper<AppCarSource>();

        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            wrapper.like(AppCarSource::getTitle, query.getKeyword());
        }
        if (query.getBrandId() != null) {
            wrapper.eq(AppCarSource::getBrandId, query.getBrandId());
        }
        if (query.getSeriesId() != null) {
            wrapper.eq(AppCarSource::getSeriesId, query.getSeriesId());
        }
        if (query.getStatus() != null && !query.getStatus().isEmpty()) {
            wrapper.eq(AppCarSource::getStatus, query.getStatus());
        }
        if (query.getEnergyType() != null && !query.getEnergyType().isEmpty()) {
            wrapper.eq(AppCarSource::getEnergyType, query.getEnergyType());
        }
        if (query.getCity() != null && !query.getCity().isEmpty()) {
            wrapper.like(AppCarSource::getCityName, query.getCity());
        }

        wrapper.orderByDesc(AppCarSource::getCreatedAt);

        Page<AppCarSource> page = appCarSourceMapper.selectPage(
                new Page<>(query.getPage(), query.getSize()), wrapper);

        // 批量查询卖家信息，避免 N+1
        List<Long> sellerIds = page.getRecords().stream()
                .map(AppCarSource::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, AppUser> sellerMap = sellerIds.isEmpty() ? Collections.emptyMap()
                : appUserMapper.selectBatchIds(sellerIds).stream()
                        .collect(Collectors.toMap(AppUser::getId, u -> u));

        List<CarVO> voList = page.getRecords().stream().map(car -> {
            CarVO vo = new CarVO();
            vo.setId(car.getId());
            vo.setTitle(car.getTitle());
            vo.setBrandId(car.getBrandId());
            vo.setSeriesId(car.getSeriesId());
            vo.setCityName(car.getCityName());
            vo.setEnergyType(car.getEnergyType());
            vo.setPrice(car.getPrice());
            vo.setMileage(car.getMileage());
            vo.setYear(car.getYear());
            vo.setStatus(car.getStatus());
            vo.setViewCount(car.getViewCount());
            vo.setCreatedAt(car.getCreatedAt());

            AppUser seller = sellerMap.get(car.getUserId());
            if (seller != null) {
                vo.setSellerName(seller.getNickname());
                vo.setSellerPhone(maskPhone(seller.getPhone()));
            }

            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(voList, page.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }

    public List<CarVO> getCarExportList(CarQueryDTO query) {
        LambdaQueryWrapper<AppCarSource> wrapper = new LambdaQueryWrapper<AppCarSource>();

        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            wrapper.like(AppCarSource::getTitle, query.getKeyword());
        }
        if (query.getBrandId() != null) {
            wrapper.eq(AppCarSource::getBrandId, query.getBrandId());
        }
        if (query.getSeriesId() != null) {
            wrapper.eq(AppCarSource::getSeriesId, query.getSeriesId());
        }
        if (query.getStatus() != null && !query.getStatus().isEmpty()) {
            wrapper.eq(AppCarSource::getStatus, query.getStatus());
        }
        if (query.getEnergyType() != null && !query.getEnergyType().isEmpty()) {
            wrapper.eq(AppCarSource::getEnergyType, query.getEnergyType());
        }
        if (query.getCity() != null && !query.getCity().isEmpty()) {
            wrapper.like(AppCarSource::getCityName, query.getCity());
        }

        wrapper.orderByDesc(AppCarSource::getCreatedAt);

        List<AppCarSource> cars = appCarSourceMapper.selectList(wrapper);

        // 批量查询卖家信息，避免 N+1
        List<Long> sellerIds = cars.stream()
                .map(AppCarSource::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, AppUser> sellerMap = sellerIds.isEmpty() ? Collections.emptyMap()
                : appUserMapper.selectBatchIds(sellerIds).stream()
                        .collect(Collectors.toMap(AppUser::getId, u -> u));

        return cars.stream().map(car -> {
            CarVO vo = new CarVO();
            vo.setId(car.getId());
            vo.setTitle(car.getTitle());
            vo.setBrandId(car.getBrandId());
            vo.setSeriesId(car.getSeriesId());
            vo.setCityName(car.getCityName());
            vo.setEnergyType(car.getEnergyType());
            vo.setPrice(car.getPrice());
            vo.setMileage(car.getMileage());
            vo.setYear(car.getYear());
            vo.setStatus(car.getStatus());
            vo.setViewCount(car.getViewCount());
            vo.setCreatedAt(car.getCreatedAt());

            AppUser seller = sellerMap.get(car.getUserId());
            if (seller != null) {
                vo.setSellerName(seller.getNickname());
                vo.setSellerPhone(maskPhone(seller.getPhone()));
            }

            return vo;
        }).collect(Collectors.toList());
    }

    public CarDetailVO getCarDetail(Long id) {
        AppCarSource car = appCarSourceMapper.selectById(id);
        if (car == null) {
            throw new BusinessException(404, "车源不存在");
        }

        CarDetailVO vo = new CarDetailVO();
        vo.setId(car.getId());
        vo.setTitle(car.getTitle());
        vo.setBrandId(car.getBrandId());
        vo.setSeriesId(car.getSeriesId());
        vo.setCityName(car.getCityName());
        vo.setEnergyType(car.getEnergyType());
        vo.setPrice(car.getPrice());
        vo.setMileage(car.getMileage());
        vo.setYear(car.getYear());
        vo.setStatus(car.getStatus());
        vo.setViewCount(car.getViewCount());
        vo.setCreatedAt(car.getCreatedAt());

        // 查询卖家信息
        if (car.getUserId() != null) {
            AppUser seller = appUserMapper.selectById(car.getUserId());
            if (seller != null) {
                vo.setSellerName(seller.getNickname());
                vo.setSellerPhone(maskPhone(seller.getPhone()));
                vo.setSellerAvatarUrl(seller.getAvatarUrl());
            }
        }

        // 从 car_images 表查询图片列表
        List<AppCarImage> images = appCarImageMapper.selectList(
                new LambdaQueryWrapper<AppCarImage>()
                        .eq(AppCarImage::getCarId, id)
                        .orderByAsc(AppCarImage::getSortOrder));
        vo.setImages(images);

        // 从 car_inspections 表查询检测报告
        AppCarInspection inspection = appCarInspectionMapper.selectOne(
                new LambdaQueryWrapper<AppCarInspection>()
                        .eq(AppCarInspection::getCarId, id));
        vo.setInspection(inspection);

        // 设置描述信息
        if (inspection != null) {
            vo.setDescription(inspection.getDescription());
        }

        return vo;
    }

    public void updateCarStatus(Long id, CarStatusUpdateDTO dto) {
        AppCarSource car = appCarSourceMapper.selectById(id);
        if (car == null) {
            throw new BusinessException(404, "车源不存在");
        }

        // 验证状态值
        String status = dto.getStatus();
        if (!"ON_SALE".equals(status) && !"OFFLINE".equals(status)) {
            throw new BusinessException(400, "无效的状态值");
        }

        car.setStatus(status);
        appCarSourceMapper.updateById(car);

        // 发送移动端通知给车主
        if (car.getUserId() != null) {
            String statusText = "ON_SALE".equals(status) ? "上架" : "下架";
            adminNotificationService.notify(
                    MobileNotification.NotifyType.CAR_STATUS_CHANGED, car.getUserId(),
                    "车源状态变更", "您的车源「" + car.getTitle() + "」已被管理员" + statusText + "。",
                    "car_source", String.valueOf(car.getId()));
        }
    }

    public void violateCar(Long id, CarViolateDTO dto) {
        AppCarSource car = appCarSourceMapper.selectById(id);
        if (car == null) {
            throw new BusinessException(404, "车源不存在");
        }

        // 标记违规，状态设为 OFFLINE
        car.setStatus(dto.getStatus());
        appCarSourceMapper.updateById(car);

        // TODO: 可在此处记录违规信息到单独的违规表，或存到 remark 字段
        log.info("车源 {} 被标记违规，原因：{}", id, dto.getReason());

        // 发送移动端通知给车主
        if (car.getUserId() != null) {
            adminNotificationService.notify(
                    MobileNotification.NotifyType.CAR_STATUS_CHANGED, car.getUserId(),
                    "车源违规下架", "您的车源「" + car.getTitle() + "」因违规被下架，原因：" + dto.getReason(),
                    "car_source", String.valueOf(car.getId()));
        }
    }

    /**
     * 管理员编辑车源信息
     */
    public void updateCar(Long id, CarUpdateDTO dto) {
        AppCarSource car = appCarSourceMapper.selectById(id);
        if (car == null) {
            throw new BusinessException(404, "车源不存在");
        }

        // 更新字段（注意类型转换）
        car.setTitle(dto.getTitle());
        car.setBrandId(dto.getBrandId().intValue());
        car.setSeriesId(dto.getSeriesId().intValue());
        car.setModelId(dto.getModelId() != null ? dto.getModelId().intValue() : null);
        car.setCityName(dto.getCityName());
        car.setEnergyType(dto.getEnergyType());
        car.setPrice(dto.getPrice());
        car.setMileage(dto.getMileage().intValue());
        car.setYear(dto.getYear());
        
        if (dto.getStatus() != null) {
            car.setStatus(dto.getStatus());
        }

        appCarSourceMapper.updateById(car);

        // 发送移动端通知给车主
        if (car.getUserId() != null) {
            adminNotificationService.notify(
                    MobileNotification.NotifyType.CAR_STATUS_CHANGED, car.getUserId(),
                    "车源信息更新", "您的车源「" + car.getTitle() + "」已被管理员更新。",
                    "car_source", String.valueOf(car.getId()));
        }

        log.info("Admin updated car source: id={}, title={}", id, car.getTitle());
    }

    /**
     * 管理员删除车源（软删除）
     */
    public void deleteCar(Long id) {
        AppCarSource car = appCarSourceMapper.selectById(id);
        if (car == null) {
            throw new BusinessException(404, "车源不存在");
        }

        // 软删除：设置 deletedAt 为当前时间
        car.setDeletedAt(java.time.LocalDateTime.now());
        appCarSourceMapper.updateById(car);

        // 发送移动端通知给车主
        if (car.getUserId() != null) {
            adminNotificationService.notify(
                    MobileNotification.NotifyType.CAR_STATUS_CHANGED, car.getUserId(),
                    "车源被删除", "您的车源「" + car.getTitle() + "」已被管理员删除。",
                    "car_source", String.valueOf(car.getId()));
        }

        log.info("Admin deleted car source: id={}, title={}", id, car.getTitle());
    }

    /**
     * 管理员设置/取消车源推荐
     */
    public void recommendCar(Long id, Boolean recommended) {
        AppCarSource car = appCarSourceMapper.selectById(id);
        if (car == null) {
            throw new BusinessException(404, "车源不存在");
        }

        car.setRecommended(recommended);
        appCarSourceMapper.updateById(car);

        // 如果设为推荐，发送通知给车主
        if (Boolean.TRUE.equals(recommended) && car.getUserId() != null) {
            adminNotificationService.notify(
                    MobileNotification.NotifyType.CAR_STATUS_CHANGED, car.getUserId(),
                    "车源被推荐", "您的车源「" + car.getTitle() + "」已被管理员设为推荐车源。",
                    "car_source", String.valueOf(car.getId()));
        }

        log.info("Admin set car recommend: id={}, recommended={}", id, recommended);
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
}
