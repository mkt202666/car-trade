package com.pancosky.cartradeadmin.controller;

import com.pancosky.cartradeadmin.annotation.AuditLog;
import com.pancosky.cartradeadmin.common.ApiResponse;
import com.pancosky.cartradeadmin.common.PageResult;
import com.pancosky.cartradeadmin.dto.DisputeHandleDTO;
import com.pancosky.cartradeadmin.dto.DisputeQueryDTO;
import com.pancosky.cartradeadmin.service.AdminDisputeService;
import com.pancosky.cartradeadmin.util.ExcelExportUtil;
import com.pancosky.cartradeadmin.vo.DisputeVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/disputes")
public class AdminDisputeController {

    @Autowired
    private AdminDisputeService adminDisputeService;

    @GetMapping
    public ApiResponse<PageResult<DisputeVO>> listDisputes(@ModelAttribute DisputeQueryDTO query) {
        return ApiResponse.success(adminDisputeService.getDisputeList(query));
    }

    @GetMapping("/export")
    public void exportDisputes(@ModelAttribute DisputeQueryDTO query, HttpServletResponse response) {
        try {
            List<DisputeVO> list = adminDisputeService.getDisputeExportList(query);

            String[] headers = {"ID", "订单号", "车源标题", "发起人", "发起人手机号", "原因", "状态", "创建时间"};

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            List<String[]> dataRows = new ArrayList<>();

            for (DisputeVO vo : list) {
                String[] row = {
                        vo.getId() != null ? vo.getId().toString() : "",
                        vo.getOrderId() != null ? vo.getOrderId() : "",
                        vo.getOrderTitle() != null ? vo.getOrderTitle() : "",
                        vo.getInitiatorName() != null ? vo.getInitiatorName() : "",
                        vo.getInitiatorPhone() != null ? vo.getInitiatorPhone() : "",
                        vo.getReason() != null ? vo.getReason() : "",
                        convertDisputeStatus(vo.getStatus()),
                        vo.getCreatedAt() != null ? vo.getCreatedAt().format(formatter) : ""
                };
                dataRows.add(row);
            }

            ExcelExportUtil.export(response, "争议处理列表", headers, dataRows);
        } catch (Exception e) {
            throw new RuntimeException("导出失败: " + e.getMessage(), e);
        }
    }

    private String convertDisputeStatus(String status) {
        if (status == null) return "";
        return switch (status) {
            case "OPEN" -> "待处理";
            case "IN_PROGRESS" -> "处理中";
            case "RESOLVED" -> "已解决";
            case "REJECTED" -> "已拒绝";
            default -> status;
        };
    }

    @AuditLog(module = "dispute", action = "处理争议", targetType = "dispute")
    @PutMapping("/{id}/handle")
    public ApiResponse<Void> handleDispute(@PathVariable Long id,
                                           @Valid @RequestBody DisputeHandleDTO dto,
                                           HttpServletRequest request) {
        Long operatorId = (Long) request.getAttribute("ADMIN_ID");
        adminDisputeService.handleDispute(id, dto, operatorId);
        return ApiResponse.success();
    }

    @GetMapping("/pending-count")
    public ApiResponse<Map<String, Object>> getPendingCount() {
        long count = adminDisputeService.getPendingCount();
        return ApiResponse.success(Map.of("count", count));
    }
}
