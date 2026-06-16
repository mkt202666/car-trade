package com.pancosky.cartradeadmin.controller;

import com.pancosky.cartradeadmin.annotation.AuditLog;
import com.pancosky.cartradeadmin.common.ApiResponse;
import com.pancosky.cartradeadmin.common.PageResult;
import com.pancosky.cartradeadmin.dto.ShopCreateDTO;
import com.pancosky.cartradeadmin.dto.ShopMemberAddDTO;
import com.pancosky.cartradeadmin.dto.ShopMemberRoleUpdateDTO;
import com.pancosky.cartradeadmin.dto.ShopQueryDTO;
import com.pancosky.cartradeadmin.dto.ShopStatusUpdateDTO;
import com.pancosky.cartradeadmin.service.AdminShopService;
import com.pancosky.cartradeadmin.util.ExcelExportUtil;
import com.pancosky.cartradeadmin.vo.ShopDetailVO;
import com.pancosky.cartradeadmin.vo.ShopMemberVO;
import com.pancosky.cartradeadmin.vo.ShopVO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/shops")
public class AdminShopController {

    @Autowired
    private AdminShopService adminShopService;

    @GetMapping
    public ApiResponse<PageResult<ShopVO>> listShops(@ModelAttribute ShopQueryDTO query) {
        return ApiResponse.success(adminShopService.getShopList(query));
    }

    @AuditLog(module = "shop", action = "创建车行", targetType = "shop")
    @PostMapping
    public ApiResponse<Map<String, Long>> createShop(@Valid @RequestBody ShopCreateDTO dto) {
        Long id = adminShopService.createShop(dto);
        return ApiResponse.success(Map.of("id", id));
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

    @GetMapping("/{shopId}/members")
    public ApiResponse<List<ShopMemberVO>> listMembers(@PathVariable Long shopId) {
        return ApiResponse.success(adminShopService.getShopMembers(shopId));
    }

    @AuditLog(module = "shop", action = "添加车行成员", targetType = "shop_member")
    @PostMapping("/{shopId}/members")
    public ApiResponse<Void> addMember(@PathVariable Long shopId, @Valid @RequestBody ShopMemberAddDTO dto) {
        adminShopService.addShopMember(shopId, dto);
        return ApiResponse.success();
    }

    @AuditLog(module = "shop", action = "修改成员角色", targetType = "shop_member")
    @PutMapping("/{shopId}/members/{memberId}/role")
    public ApiResponse<Void> updateMemberRole(@PathVariable Long shopId,
                                               @PathVariable Long memberId,
                                               @Valid @RequestBody ShopMemberRoleUpdateDTO dto) {
        adminShopService.updateMemberRole(shopId, memberId, dto);
        return ApiResponse.success();
    }

    @AuditLog(module = "shop", action = "移除车行成员", targetType = "shop_member")
    @DeleteMapping("/{shopId}/members/{memberId}")
    public ApiResponse<Void> removeMember(@PathVariable Long shopId, @PathVariable Long memberId) {
        adminShopService.removeShopMember(shopId, memberId);
        return ApiResponse.success();
    }
}
