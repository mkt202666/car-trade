package com.pancosky.cartradeadmin.controller;

import com.pancosky.cartradeadmin.annotation.AuditLog;
import com.pancosky.cartradeadmin.common.ApiResponse;
import com.pancosky.cartradeadmin.common.PageResult;
import com.pancosky.cartradeadmin.dto.CarQueryDTO;
import com.pancosky.cartradeadmin.dto.CarStatusUpdateDTO;
import com.pancosky.cartradeadmin.dto.CarUpdateDTO;
import com.pancosky.cartradeadmin.dto.CarViolateDTO;
import com.pancosky.cartradeadmin.service.AdminCarService;
import com.pancosky.cartradeadmin.util.ExcelExportUtil;
import com.pancosky.cartradeadmin.vo.CarDetailVO;
import com.pancosky.cartradeadmin.vo.CarVO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/cars")
public class AdminCarController {

    @Autowired
    private AdminCarService adminCarService;

    @GetMapping
    public ApiResponse<PageResult<CarVO>> listCars(@ModelAttribute CarQueryDTO query) {
        return ApiResponse.success(adminCarService.getCarList(query));
    }

    @GetMapping("/export")
    public void exportCars(@ModelAttribute CarQueryDTO query, HttpServletResponse response) {
        try {
            List<CarVO> list = adminCarService.getCarExportList(query);

            String[] headers = {"ID", "标题", "城市", "能源类型", "价格", "里程", "年份", "卖家", "状态", "浏览量", "创建时间"};

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            List<String[]> dataRows = new ArrayList<>();

            for (CarVO vo : list) {
                String[] row = {
                        vo.getId() != null ? vo.getId().toString() : "",
                        vo.getTitle() != null ? vo.getTitle() : "",
                        vo.getCityName() != null ? vo.getCityName() : "",
                        vo.getEnergyType() != null ? vo.getEnergyType() : "",
                        vo.getPrice() != null ? vo.getPrice().toString() : "",
                        vo.getMileage() != null ? vo.getMileage().toString() : "",
                        vo.getYear() != null ? vo.getYear().toString() : "",
                        vo.getSellerName() != null ? vo.getSellerName() : "",
                        convertCarStatus(vo.getStatus()),
                        vo.getViewCount() != null ? vo.getViewCount().toString() : "0",
                        vo.getCreatedAt() != null ? vo.getCreatedAt().format(formatter) : ""
                };
                dataRows.add(row);
            }

            ExcelExportUtil.export(response, "车源列表", headers, dataRows);
        } catch (Exception e) {
            throw new RuntimeException("导出失败: " + e.getMessage(), e);
        }
    }

    private String convertCarStatus(String status) {
        if (status == null) return "";
        return switch (status) {
            case "ON_SALE" -> "在售";
            case "OFFLINE" -> "下架";
            case "SOLD" -> "已售";
            case "PENDING" -> "待审核";
            case "VIOLATE" -> "违规";
            default -> status;
        };
    }

    @GetMapping("/{id}")
    public ApiResponse<CarDetailVO> getCarDetail(@PathVariable Long id) {
        return ApiResponse.success(adminCarService.getCarDetail(id));
    }

    @AuditLog(module = "car", action = "车源上下架", targetType = "car")
    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateCarStatus(@PathVariable Long id, @Valid @RequestBody CarStatusUpdateDTO dto) {
        adminCarService.updateCarStatus(id, dto);
        return ApiResponse.success();
    }

    @AuditLog(module = "car", action = "标记违规", targetType = "car")
    @PostMapping("/{id}/violate")
    public ApiResponse<Void> violateCar(@PathVariable Long id, @Valid @RequestBody CarViolateDTO dto) {
        adminCarService.violateCar(id, dto);
        return ApiResponse.success();
    }

    @AuditLog(module = "car", action = "编辑车源", targetType = "car")
    @PutMapping("/{id}")
    public ApiResponse<Void> updateCar(@PathVariable Long id, @Valid @RequestBody CarUpdateDTO dto) {
        adminCarService.updateCar(id, dto);
        return ApiResponse.success();
    }

    @AuditLog(module = "car", action = "删除车源", targetType = "car")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCar(@PathVariable Long id) {
        adminCarService.deleteCar(id);
        return ApiResponse.success();
    }
}
