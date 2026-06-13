package com.pancosky.cartradeadmin.controller;

import com.pancosky.cartradeadmin.annotation.AuditLog;
import com.pancosky.cartradeadmin.common.ApiResponse;
import com.pancosky.cartradeadmin.common.PageResult;
import com.pancosky.cartradeadmin.dto.ShopQueryDTO;
import com.pancosky.cartradeadmin.dto.ShopStatusUpdateDTO;
import com.pancosky.cartradeadmin.service.AdminShopService;
import com.pancosky.cartradeadmin.util.ExcelExportUtil;
import com.pancosky.cartradeadmin.vo.ShopDetailVO;
import com.pancosky.cartradeadmin.vo.ShopVO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/shops")
public class AdminShopController {

    @Autowired
    private AdminShopService adminShopService;

    @GetMapping
    public ApiResponse<PageResult<ShopVO>> listShops(@ModelAttribute ShopQueryDTO query) {
        return ApiResponse.success(adminShopService.getShopList(query));
    }

    @GetMapping("/export")
    public void exportShops(@ModelAttribute ShopQueryDTO query, HttpServletResponse response) {
        try {
            List<ShopVO> list = adminShopService.getShopExportList(query);

            String[] headers = {"ID", "车行名称", "联系人", "手机号", "信用等级", "信用分", "成交数", "在售数", "状态", "认证状态", "创建时间"};

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            List<String[]> dataRows = new ArrayList<>();

            for (ShopVO vo : list) {
                String[] row = {
                        vo.getId() != null ? vo.getId().toString() : "",
                        vo.getShopName() != null ? vo.getShopName() : "",
                        vo.getRealName() != null ? vo.getRealName() : "",
                        vo.getPhone() != null ? vo.getPhone() : "",
                        vo.getCreditGrade() != null ? vo.getCreditGrade() : "",
                        vo.getCreditScore() != null ? vo.getCreditScore().toString() : "",
                        vo.getDealCount() != null ? vo.getDealCount().toString() : "0",
                        vo.getOnSaleCount() != null ? vo.getOnSaleCount().toString() : "0",
                        convertShopStatus(vo.getStatus()),
                        convertCertificationStatus(vo.getCertificationStatus()),
                        vo.getCreatedAt() != null ? vo.getCreatedAt().format(formatter) : ""
                };
                dataRows.add(row);
            }

            ExcelExportUtil.export(response, "车行列表", headers, dataRows);
        } catch (Exception e) {
            throw new RuntimeException("导出失败: " + e.getMessage(), e);
        }
    }

    private String convertShopStatus(String status) {
        if (status == null) return "";
        return switch (status) {
            case "ACTIVE" -> "正常";
            case "INACTIVE" -> "停用";
            default -> status;
        };
    }

    private String convertCertificationStatus(String status) {
        if (status == null) return "";
        return switch (status) {
            case "CERTIFIED" -> "已认证";
            case "UNCERTIFIED" -> "未认证";
            case "PENDING" -> "待审核";
            case "REJECTED" -> "已拒绝";
            default -> status;
        };
    }

    @GetMapping("/{id}")
    public ApiResponse<ShopDetailVO> getShopDetail(@PathVariable Long id) {
        return ApiResponse.success(adminShopService.getShopDetail(id));
    }

    @AuditLog(module = "shop", action = "启停车行", targetType = "shop")
    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateShopStatus(@PathVariable Long id, @Valid @RequestBody ShopStatusUpdateDTO dto) {
        adminShopService.updateShopStatus(id, dto.getStatus());
        return ApiResponse.success();
    }
}
