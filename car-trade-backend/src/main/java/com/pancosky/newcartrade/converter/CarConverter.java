package com.pancosky.newcartrade.converter;

import com.pancosky.newcartrade.entity.CarSource;
import com.pancosky.newcartrade.vo.CarDetailVO;
import com.pancosky.newcartrade.vo.CarVO;
import org.springframework.stereotype.Component;

@Component
public class CarConverter {

    public CarVO toVO(CarSource source) {
        if (source == null) return null;
        CarVO vo = new CarVO();
        vo.setId(source.getId());
        vo.setYear(source.getYear());
        vo.setMileage(source.getMileage());
        vo.setPrice(source.getPrice());
        vo.setDeposit(source.getDeposit());
        vo.setEnergyType(source.getEnergyType());
        vo.setAuctionStatus(source.getAuctionStatus());
        vo.setAuctionEndTime(source.getAuctionEndTime());
        vo.setCreatedAt(source.getCreatedAt());
        vo.setViewCount(source.getViewCount());
        vo.setFavoriteCount(source.getFavoriteCount());
        return vo;
    }

    public CarDetailVO toDetailVO(CarSource source) {
        if (source == null) return null;
        CarDetailVO vo = new CarDetailVO();
        vo.setId(source.getId());
        vo.setYear(source.getYear());
        vo.setMileage(source.getMileage());
        vo.setPrice(source.getPrice());
        vo.setDeposit(source.getDeposit());
        vo.setColor(source.getColor());
        vo.setCityCode(source.getCityCode());
        vo.setEnergyType(source.getEnergyType());
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
        vo.setAuctionStatus(source.getAuctionStatus());
        vo.setAuctionEndTime(source.getAuctionEndTime());
        vo.setCreatedAt(source.getCreatedAt());
        vo.setViewCount(source.getViewCount());
        vo.setFavoriteCount(source.getFavoriteCount());
        return vo;
    }
}
