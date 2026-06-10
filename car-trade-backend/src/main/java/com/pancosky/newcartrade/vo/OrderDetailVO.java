package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单详情VO
 * 描述：订单详情页展示的完整订单信息，包括车源信息、买卖双方、验车报告、合同签署、保证金等。
 */
@Data
public class OrderDetailVO {

    /** 订单ID */
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

    /** 订单状态 */
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

    /** 车源品牌 */
    private String brandName;

    /** 车源系列 */
    private String seriesName;

    /** 车源车型 */
    private String modelName;

    /** 车源出厂年份 */
    private Integer year;

    /** 车源行驶里程 */
    private Integer mileage;

    /** 车源颜色 */
    private String color;

    /** 车源所在城市 */
    private String city;

    /** 验车综合车况评级 */
    private String overallCondition;

    /** 车漆状况 */
    private String paint;

    /** 车身结构状况 */
    private String structure;

    /** 发动机状况 */
    private String engine;

    /** 变速箱状况 */
    private String transmission;

    /** 历史过户次数 */
    private Integer transferCount;

    /** 里程表类型（真实/疑似调表/未知） */
    private String mileageType;

    /** 相关材料（如报告、文档URL） */
    private String materials;

    /** 合同编号 */
    private String contractNo;

    /** 合同状态（DRAFT / SIGNED / CANCELLED） */
    private String contractStatus;

    /** 买家是否已签署 */
    private Boolean buyerDepositPaid;

    /** 卖家是否已签署 */
    private Boolean sellerDepositPaid;

    /** 保证金状态（UNPAID / PAID / REFUNDED / PARTIAL_REFUNDED） */
    private String depositStatus;

    /** 取消原因（订单取消时填入） */
    private String cancelReason;

    /** 订单完成时间 */
    private LocalDateTime completedAt;

    /** 订单取消时间 */
    private LocalDateTime cancelledAt;
}
