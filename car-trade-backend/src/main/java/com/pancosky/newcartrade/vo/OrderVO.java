package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单列表VO
 * 描述：订单列表页卡片信息，用于买家/卖家订单列表展示。
 */
@Data
public class OrderVO {

    /** 订单ID（字符串，如 OD202507150001） */
    private String id;

    /** 车源ID */
    private Long carId;

    /** 车源名称 */
    private String carName;

    /** 车源封面图URL */
    private String carImage;

    /** 订单总金额（元） */
    private BigDecimal totalPrice;

    /** 保证金金额（元） */
    private BigDecimal depositAmount;

    /** 订单状态（如 CREATED / PAID / SHIPPED / SIGNED / COMPLETED / CANCELLED / DISPUTE） */
    private String status;

    /** 买家用户ID */
    private Long buyerId;

    /** 买家名称 */
    private String buyerName;

    /** 卖家用户ID */
    private Long sellerId;

    /** 卖家名称 */
    private String sellerName;

    /** 订单创建时间 */
    private LocalDateTime createdAt;

    /** 订单最近更新时间 */
    private LocalDateTime updatedAt;
}
