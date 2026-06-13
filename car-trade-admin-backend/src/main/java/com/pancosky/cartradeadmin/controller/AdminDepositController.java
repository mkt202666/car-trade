package com.pancosky.cartradeadmin.controller;

import com.pancosky.cartradeadmin.annotation.AuditLog;
import com.pancosky.cartradeadmin.common.ApiResponse;
import com.pancosky.cartradeadmin.common.PageResult;
import com.pancosky.cartradeadmin.dto.DepositAccountQueryDTO;
import com.pancosky.cartradeadmin.dto.DepositManualDTO;
import com.pancosky.cartradeadmin.dto.DepositRecordQueryDTO;
import com.pancosky.cartradeadmin.service.AdminDepositService;
import com.pancosky.cartradeadmin.util.ExcelExportUtil;
import com.pancosky.cartradeadmin.vo.DepositAccountVO;
import com.pancosky.cartradeadmin.vo.DepositRecordVO;
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
@RequestMapping("${api.prefix}/deposits")
public class AdminDepositController {

    @Autowired
    private AdminDepositService adminDepositService;

    @GetMapping("/accounts")
    public ApiResponse<PageResult<DepositAccountVO>> listAccounts(@ModelAttribute DepositAccountQueryDTO query) {
        return ApiResponse.success(adminDepositService.getAccountList(query));
    }

    @GetMapping("/records")
    public ApiResponse<PageResult<DepositRecordVO>> listRecords(@ModelAttribute DepositRecordQueryDTO query) {
        return ApiResponse.success(adminDepositService.getRecordList(query));
    }

    @GetMapping("/records/export")
    public void exportDepositRecords(@ModelAttribute DepositRecordQueryDTO query, HttpServletResponse response) {
        try {
            List<DepositRecordVO> list = adminDepositService.getDepositExportList(query);

            String[] headers = {"ID", "用户ID", "用户名", "类型", "金额", "余额", "备注", "时间"};

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            List<String[]> dataRows = new ArrayList<>();

            for (DepositRecordVO vo : list) {
                String[] row = {
                        vo.getId() != null ? vo.getId().toString() : "",
                        vo.getUserId() != null ? vo.getUserId().toString() : "",
                        vo.getUserName() != null ? vo.getUserName() : "",
                        convertDepositType(vo.getType()),
                        vo.getAmount() != null ? vo.getAmount().toString() : "",
                        vo.getBalanceAfter() != null ? vo.getBalanceAfter().toString() : "",
                        vo.getRemark() != null ? vo.getRemark() : "",
                        vo.getCreatedAt() != null ? vo.getCreatedAt().format(formatter) : ""
                };
                dataRows.add(row);
            }

            ExcelExportUtil.export(response, "保证金记录列表", headers, dataRows);
        } catch (Exception e) {
            throw new RuntimeException("导出失败: " + e.getMessage(), e);
        }
    }

    private String convertDepositType(String type) {
        if (type == null) return "";
        return switch (type) {
            case "MANUAL" -> "手动调整";
            case "CHARGE" -> "充值";
            case "WITHDRAW" -> "提现";
            case "REFUND" -> "退款";
            case "FREEZE" -> "冻结";
            case "UNFREEZE" -> "解冻";
            case "DEDUCT" -> "扣除";
            default -> type;
        };
    }

    @AuditLog(module = "deposit", action = "手动调整保证金", targetType = "deposit")
    @PostMapping("/records/manual")
    public ApiResponse<Void> manualAdjust(@Valid @RequestBody DepositManualDTO dto,
                                          HttpServletRequest request) {
        Long operatorId = (Long) request.getAttribute("userId");
        adminDepositService.manualAdjust(dto, operatorId);
        return ApiResponse.success();
    }

    @GetMapping("/summary")
    public ApiResponse<Map<String, Object>> getSummary() {
        return ApiResponse.success(adminDepositService.getSummary());
    }
}
