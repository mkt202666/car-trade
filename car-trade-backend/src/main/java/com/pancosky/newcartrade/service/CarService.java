package com.pancosky.newcartrade.service;

import com.pancosky.newcartrade.common.PageResult;
import com.pancosky.newcartrade.dto.CarCreateDTO;
import com.pancosky.newcartrade.dto.CarQueryDTO;
import com.pancosky.newcartrade.dto.CarUpdateDTO;
import com.pancosky.newcartrade.vo.CarDetailVO;
import com.pancosky.newcartrade.vo.CarVO;

import java.util.List;

public interface CarService {
    PageResult<CarVO> list(CarQueryDTO query);
    CarDetailVO detail(Long id);
    CarVO create(CarCreateDTO dto);
    CarVO update(Long id, CarUpdateDTO dto);
    void delete(Long id);
    void favorite(Long id);
    void unfavorite(Long id);
    List<CarVO> recommend();
    void export(String country);
    void downloadImage(Long carId, Long imageId);
    void contactSeller(Long carId);
}
