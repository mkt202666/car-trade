package com.pancosky.cartradeadmin.controller;

import com.pancosky.cartradeadmin.annotation.AuditLog;
import com.pancosky.cartradeadmin.annotation.RequiresAdmin;
import com.pancosky.cartradeadmin.common.ApiResponse;
import com.pancosky.cartradeadmin.common.PageResult;
import com.pancosky.cartradeadmin.dto.OrderActionDTO;
import com.pancosky.cartradeadmin.dto.OrderQueryDTO;
import com.pancosky.cartradeadmin.service.AdminOrderService;
import com.pancosky.cartradeadmin.util.ExcelExportUtil;
import com.pancosky.cartradeadmin.vo.OrderDetailVO;
import com.pancosky.cartradeadmin.vo.OrderLogVO;
import com.pancosky.cartradeadmin.vo.OrderVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/orders")
@RequiresAdmin
public class AdminOrderController {

    @Autowired
    private AdminOrderService adminOrderService;

    @GetMapping
    public ApiResponse<PageResult<OrderVO>> listOrders(@ModelAttribute OrderQueryDTO query) {
        return ApiResponse.success(adminOrderService.getOrderList(query));
    }

    @GetMapping("/export")
    public void exportOrders(@ModelAttribute OrderQueryDTO query, HttpServletResponse response) {
        try {
            List<OrderVO> list = adminOrderService.getOrderExportList(query);

            String[] headers = {"订单号", "车源标题", "买家", "卖家", "总价", "保证金", "状态", "创建时间"};

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            List<String[]> dataRows = new ArrayList<>();

            for (OrderVO vo : list) {
                String[] row = {
                        vo.getId() != null ? vo.getId() : "",
                        vo.getCarTitle() != null ? vo.getCarTitle() : "",
                        vo.getBuyerName() != null ? vo.getBuyerName() : "",
                        vo.getSellerName() != null ? vo.getSellerName() : "",
                        vo.getTotalPrice() != null ? vo.getTotalPrice().toString() : "",
                        vo.getDepositAmount() != null ? vo.getDepositAmount().toString() : "",
                        convertOrderStatus(vo.getStatus()),
                        vo.getCreatedAt() != null ? vo.getCreatedAt().format(formatter) : ""
                };
                dataRows.add(row);
            }

            ExcelExportUtil.export(response, "交易订单列表", headers, dataRows);
        } catch (Exception e) {
            throw new RuntimeException("导出失败: " + e.getMessage(), e);
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderDetailVO> getOrderDetail(@PathVariable String id) {
        return ApiResponse.success(adminOrderService.getOrderDetail(id));
    }

    @GetMapping("/{id}/logs")
    public ApiResponse<List<OrderLogVO>> getOrderLogs(@PathVariable String id) {
        return ApiResponse.success(adminOrderService.getOrderLogs(id));
    }

    // ==================== 运营端订单操作 ====================

    /**
     * 管理员确认订单
     * PUT /api/v1/admin/orders/{id}/confirm
     * 将 PENDING_DEPOSIT 状态推进到 CONTRACT_DRAFT
     */
    @PutMapping("/{id}/confirm")
    @AuditLog(module = "order", action = "管理员确认订单", targetType = "order")
    public ApiResponse<Void> confirmOrder(@PathVariable String id) {
        adminOrderService.confirmOrder(id);
        return ApiResponse.success(null);
    }

    /**
     * 管理员强制取消订单
     * PUT /api/v1/admin/orders/{id}/force-cancel
     * 需要填写取消原因
     */
    @PutMapping("/{id}/force-cancel")
    @AuditLog(module = "order", action = "管理员强制取消订单", targetType = "order")
    public ApiResponse<Void> forceCancelOrder(@PathVariable String id, @RequestBody OrderActionDTO dto) {
        adminOrderService.forceCancelOrder(id, dto.getReason());
        return ApiResponse.success(null);
    }

    /**
     * 管理员纠纷裁决
     * PUT /api/v1/admin/orders/{id}/resolve-dispute
     * resolution: buyer（支持买家）/ seller（支持卖家）
     */
    @PutMapping("/{id}/resolve-dispute")
    @AuditLog(module = "order", action = "管理员纠纷裁决", targetType = "order")
    public ApiResponse<Void> resolveDispute(@PathVariable String id, @RequestBody OrderActionDTO dto) {
        adminOrderService.resolveDispute(id, dto.getResolution(), dto.getReason());
        return ApiResponse.success(null);
    }

    /**
     * 管理员退款
     * PUT /api/v1/admin/orders/{id}/refund
     * refundAmount 不传则全额退款
     */
    @PutMapping("/{id}/refund")
    @AuditLog(module = "order", action = "管理员退款", targetType = "order")
    public ApiResponse<Void> refundOrder(@PathVariable String id, @RequestBody OrderActionDTO dto) {
        adminOrderService.refundOrder(id, dto.getRefundAmount(), dto.getReason());
        return ApiResponse.success();
    }

    /**
     * 订单状态中文映射（用于导出）
     * 与移动端 Order 实体的实际状态值保持一致
     */
    private String convertOrderStatus(String status) {
        if (status == null) return "";
        return switch (status) {
            case "PENDING_DEPOSIT" -> "待付保证金";
            case "CONTRACT_DRAFT" -> "合同草拟";
            case "CONTRACT_SIGNED" -> "合同已签";
            case "IN_TRANSIT" -> "过户中";
            case "COMPLETED" -> "已完成";
            case "CANCELLED" -> "已取消";
            case "TERMINATED" -> "已终止";
            case "DISPUTE" -> "纠纷中";
            default -> status;
        };
    }
}
