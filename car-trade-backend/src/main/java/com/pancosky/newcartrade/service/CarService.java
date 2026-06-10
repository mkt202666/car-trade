package com.pancosky.newcartrade.service;

import com.pancosky.newcartrade.common.PageResult;
import com.pancosky.newcartrade.dto.CarCreateDTO;
import com.pancosky.newcartrade.dto.CarQueryDTO;
import com.pancosky.newcartrade.dto.CarUpdateDTO;
import com.pancosky.newcartrade.vo.CarDetailVO;
import com.pancosky.newcartrade.vo.CarVO;

import java.util.List;
import java.util.Map;

public interface CarService {
    PageResult<CarVO> list(CarQueryDTO query);
    CarDetailVO detail(Long id);
    CarVO create(CarCreateDTO dto);
    CarVO update(Long id, CarUpdateDTO dto);
    void delete(Long id);
    void favorite(Long id);
    void unfavorite(Long id);
    List<CarVO> recommend();
    List<CarVO> export(String country);
    String downloadImage(Long carId, Long imageId);
    Map<String, Object> contactSeller(Long carId);
}
