package com.pancosky.cartradeadmin.dto;

import lombok.Data;

/**
 * 运营端订单操作请求 DTO
 * 用于管理员对订单执行确认、强制取消、纠纷裁决等操作
 */
@Data
public class OrderActionDTO {

    /** 操作原因（必填：强制取消原因 / 纠纷裁决说明 / 退款说明） */
    private String reason;

    /** 纠纷裁决结果：buyer / seller（仅纠纷裁决时使用） */
    private String resolution;

    /** 退款金额（仅退款操作时使用，不传则全额退款） */
    private java.math.BigDecimal refundAmount;
}
