package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付跳转VO
 * 描述：订单支付接口返回的支付信息，包含第三方支付URL或订单信息。
 */
@Data
public class PaymentVO {

    /** 关联订单ID */
    private String orderId;

    /** 待支付金额（元） */
    private BigDecimal amount;

    /** 第三方支付页面跳转URL（若为 H5 支付） */
    private String paymentUrl;

    /** 支付链接有效期（超时需重新发起支付） */
    private LocalDateTime expiresAt;
}
