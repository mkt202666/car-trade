package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 合同列表VO
 * 描述：合同列表卡片，用于订单详情页或合同管理页展示合同简要信息。
 */
@Data
public class ContractVO {

    /** 合同ID */
    private Long id;

    /** 合同编号（系统自动生成，如 HT202507150001） */
    private String contractNo;

    /** 关联订单ID */
    private String orderId;

    /** 合同标题（如"二手车买卖合同"） */
    private String title;

    /** 合同状态（DRAFT 草稿 / BUYER_SIGNED 买家已签 / SELLER_SIGNED 卖家已签 / SIGNED 双方已签 / CANCELLED 已取消） */
    private String status;

    /** 买家是否已签署 */
    private Boolean buyerSigned;

    /** 卖家是否已签署 */
    private Boolean sellerSigned;

    /** 合同创建时间 */
    private LocalDateTime createdAt;
}
