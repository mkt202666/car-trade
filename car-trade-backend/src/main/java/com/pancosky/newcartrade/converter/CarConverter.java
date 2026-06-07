package com.pancosky.newcartrade.converter;

import com.pancosky.newcartrade.entity.CarImage;
import com.pancosky.newcartrade.entity.CarInspection;
import com.pancosky.newcartrade.entity.CarSource;
import com.pancosky.newcartrade.entity.User;
import com.pancosky.newcartrade.vo.CarDetailVO;
import com.pancosky.newcartrade.vo.CarVO;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CarConverter {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("MM-dd HH:mm");

    public static CarVO toVO(CarSource source) {
        if (source == null) return null;
        CarVO vo = new CarVO();
        vo.setId(source.getId());
        vo.setTitle(source.getTitle());
        vo.setBrandName(source.getBrandName());
        vo.setSeriesName(source.getSeriesName());
        vo.setModelName(source.getModelName());
        vo.setYear(source.getYear());
        vo.setMileage(source.getMileage());
        vo.setPrice(source.getPrice());
        vo.setDeposit(source.getDeposit());
        vo.setHasDeposit(source.getDeposit() != null && source.getDeposit().compareTo(BigDecimal.ZERO) > 0);
        vo.setColor(source.getColor());
        vo.setCity(source.getCityName());
        vo.setCityCode(source.getCityCode());
        vo.setEnergyType(energyTypeToChinese(source.getEnergyType()));
        vo.setUsageType(source.getUsageType());
        vo.setIsMortgaged(source.getIsMortgaged());
        vo.setCoverImage(source.getCoverImage());
        vo.setAuctionStatus(source.getAuctionStatus());
        vo.setAuctionRemaining(calcAuctionRemaining(source.getAuctionEndTime()));
        vo.setHasParticipated(false);
        vo.setAuctionEndTime(source.getAuctionEndTime());
        vo.setCreatedAt(source.getCreatedAt());
        vo.setCreateTime(source.getCreatedAt() == null ? "" : source.getCreatedAt().format(TIME_FMT));
        vo.setViewCount(source.getViewCount());
        vo.setFavoriteCount(source.getFavoriteCount());
        vo.setExportCountries(parseExportCountries(source.getExportCountries()));
        vo.setIsGlobal(vo.getExportCountries() != null && vo.getExportCountries().size() >= 4);
        return vo;
    }

    public static CarVO toVO(CarSource source, List<CarImage> images) {
        CarVO vo = toVO(source);
        if (vo != null && images != null && !images.isEmpty()) {
            List<String> urls = images.stream()
                    .sorted((a, b) -> Integer.compare(a.getSortOrder() == null ? 0 : a.getSortOrder(),
                            b.getSortOrder() == null ? 0 : b.getSortOrder()))
                    .map(CarImage::getImageUrl)
                    .collect(Collectors.toList());
            vo.setImages(urls);
            if (vo.getCoverImage() == null && !urls.isEmpty()) vo.setCoverImage(urls.get(0));
        }
        return vo;
    }

    public static CarDetailVO toDetailVO(CarSource source, List<CarImage> images, CarInspection inspection, User seller,
                                          Boolean favorited, Boolean followed) {
        if (source == null) return null;
        CarDetailVO vo = new CarDetailVO();
        vo.setId(source.getId());
        vo.setTitle(source.getTitle());
        vo.setBrandName(source.getBrandName());
        vo.setSeriesName(source.getSeriesName());
        vo.setModelName(source.getModelName());
        vo.setYear(source.getYear());
        vo.setMileage(source.getMileage());
        vo.setPrice(source.getPrice());
        vo.setDeposit(source.getDeposit());
        vo.setColor(source.getColor());
        vo.setCity(source.getCityName());
        vo.setCityCode(source.getCityCode());
        vo.setEnergyType(energyTypeToChinese(source.getEnergyType()));
        vo.setUsageType(source.getUsageType());
        vo.setOwnerType(source.getOwnerType());
        vo.setIsMortgaged(source.getIsMortgaged());
        vo.setIsInherited(source.getIsInherited());
        vo.setRegistrationDate(source.getRegistrationDate());
        vo.setInsuranceExpiry(source.getInsuranceExpiry());
        vo.setInspectionExpiry(source.getInspectionExpiry());
        vo.setProductionDate(source.getProductionDate());
        vo.setKeyCount(source.getKeyCount());
        vo.setDescription(source.getDescription());
        vo.setCoverImage(source.getCoverImage());
        vo.setAuctionStatus(source.getAuctionStatus());
        vo.setAuctionEndTime(source.getAuctionEndTime());
        vo.setCreatedAt(source.getCreatedAt());
        vo.setViewCount(source.getViewCount());
        vo.setFavoriteCount(source.getFavoriteCount());

        if (images != null && !images.isEmpty()) {
            List<String> urls = images.stream()
                    .sorted((a, b) -> Integer.compare(a.getSortOrder() == null ? 0 : a.getSortOrder(),
                            b.getSortOrder() == null ? 0 : b.getSortOrder()))
                    .map(CarImage::getImageUrl)
                    .collect(Collectors.toList());
            vo.setImages(urls);
            if (vo.getCoverImage() == null) vo.setCoverImage(urls.get(0));
        } else {
            vo.setImages(new ArrayList<>());
        }

        if (inspection != null) {
            vo.setOverallCondition(inspection.getOverallCondition());
            vo.setPaint(inspection.getPaint());
            vo.setStructure(inspection.getStructure());
            vo.setEngine(inspection.getEngine());
            vo.setTransmission(inspection.getTransmission());
            vo.setTransferCount(inspection.getTransferCount());
            vo.setMileageType(inspection.getMileageType());
            vo.setAbnormalPhotos(parsePhotos(inspection.getAbnormalPhotos()));
        }

        if (seller != null) {
            vo.setSellerId(seller.getId());
            vo.setSellerName(seller.getNickname());
            vo.setSellerAvatar(seller.getAvatarUrl());
            vo.setSellerShopName(seller.getShopName());
            vo.setSellerCreditGrade(seller.getCreditGrade());
            vo.setSellerDealCount(seller.getDealCount());
            vo.setSellerFollowerCount(seller.getFollowerCount());
        }

        if (favorited != null) vo.setFavoritedByCurrentUser(favorited);
        if (followed != null) vo.setFollowedByCurrentUser(followed);
        return vo;
    }

    // ================= helpers =================

    /** 能源类型枚举 -> 中文 */
    private static String energyTypeToChinese(String raw) {
        if (raw == null) return "燃油";
        String upper = raw.trim().toUpperCase();
        switch (upper) {
            case "PURE_ELECTRIC":
            case "EV":
            case "ELECTRIC":
                return "纯电";
            case "HYBRID":
            case "PHEV":
                return "混动";
            case "GASOLINE":
            case "PETROL":
                return "燃油";
            default:
                return raw;
        }
    }

    /** 计算拍卖剩余时间 */
    private static String calcAuctionRemaining(LocalDateTime endTime) {
        if (endTime == null) return null;
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(endTime)) return "已结束";
        java.time.Duration d = java.time.Duration.between(now, endTime);
        long days = d.toDays();
        long hours = d.toHours() % 24;
        long minutes = d.toMinutes() % 60;
        if (days > 0) return days + "天" + hours + "小时后结束";
        if (hours > 0) return hours + "小时" + minutes + "分钟后结束";
        return minutes + "分钟后结束";
    }

    /** 解析出口国家代码（逗号分隔字符串 -> 列表） */
    private static List<String> parseExportCountries(String raw) {
        if (!StringUtils.hasText(raw)) return new ArrayList<>();
        return Arrays.stream(raw.trim().split("[,;，；]"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private static List<String> parsePhotos(String raw) {
        if (!StringUtils.hasText(raw)) return new ArrayList<>();
        String trimmed = raw.trim();
        if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
            try {
                String inner = trimmed.substring(1, trimmed.length() - 1).trim();
                if (inner.isEmpty()) return new ArrayList<>();
                return Arrays.stream(inner.split(","))
                        .map(s -> s.replaceAll("^[\"']|[\"']$", "").trim())
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
            } catch (Exception e) {
                return Collections.singletonList(raw);
            }
        }
        return Arrays.stream(raw.split("[,;，；]"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}
