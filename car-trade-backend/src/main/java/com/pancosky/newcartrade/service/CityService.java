package com.pancosky.newcartrade.service;

import com.pancosky.newcartrade.vo.CityVO;

import java.util.List;

/**
 * 城市服务接口
 */
public interface CityService {

    /**
     * 获取所有城市列表
     */
    List<CityVO> getAllCities();

    /**
     * 获取热门城市列表
     */
    List<CityVO> getHotCities();

    /**
     * 根据省份获取城市列表
     */
    List<CityVO> getCitiesByProvince(String province);
}