package com.pancosky.newcartrade.controller;

import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.common.PageResult;
import com.pancosky.newcartrade.dto.CarCreateDTO;
import com.pancosky.newcartrade.dto.CarQueryDTO;
import com.pancosky.newcartrade.dto.CarUpdateDTO;
import com.pancosky.newcartrade.service.CarService;
import com.pancosky.newcartrade.vo.CarDetailVO;
import com.pancosky.newcartrade.vo.CarVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @GetMapping
    public ApiResponse<PageResult<CarVO>> list(CarQueryDTO params) {
        return ApiResponse.success(carService.list(params));
    }

    @GetMapping("/{id}")
    public ApiResponse<CarDetailVO> detail(@PathVariable Long id) {
        return ApiResponse.success(carService.detail(id));
    }

    @PostMapping
    public ApiResponse<CarVO> create(@RequestBody CarCreateDTO dto) {
        return ApiResponse.success(carService.create(dto));
    }

    @PutMapping("/{id}")
    public ApiResponse<CarVO> update(@PathVariable Long id, @RequestBody CarUpdateDTO dto) {
        return ApiResponse.success(carService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        carService.delete(id);
        return ApiResponse.success();
    }

    @PostMapping("/{id}/favorite")
    public ApiResponse<Void> favorite(@PathVariable Long id) {
        carService.favorite(id);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}/favorite")
    public ApiResponse<Void> unfavorite(@PathVariable Long id) {
        carService.unfavorite(id);
        return ApiResponse.success();
    }

    @GetMapping("/recommend")
    public ApiResponse<List<CarVO>> recommend() {
        return ApiResponse.success(carService.recommend());
    }

    @GetMapping("/export")
    public ApiResponse<List<CarVO>> export(@RequestParam String country) {
        return ApiResponse.success(carService.export(country));
    }

    @GetMapping("/{id}/images/{imageId}/download")
    public ApiResponse<String> downloadImage(@PathVariable Long id, @PathVariable Long imageId) {
        return ApiResponse.success(carService.downloadImage(id, imageId));
    }

    @PostMapping("/{id}/contact")
    public ApiResponse<Map<String, Object>> contactSeller(@PathVariable Long id) {
        return ApiResponse.success(carService.contactSeller(id));
    }
}
