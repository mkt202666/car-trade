package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 合同详情VO
 * 描述：合同详情页展示的完整合同信息，包括买卖双方、合同内容、文件等。
 */
@Data
public class ContractDetailVO {

    /** 合同ID */
    private Long id;

    /** 合同编号 */
    private String contractNo;

    /** 关联订单ID */
    private String orderId;

    /** 合同标题 */
    private String title;

    /** 合同状态 */
    private String status;

    /** 买家是否已签署 */
    private Boolean buyerSigned;

    /** 卖家是否已签署 */
    private Boolean sellerSigned;

    /** 合同创建时间 */
    private LocalDateTime createdAt;

    /** 买家用户ID */
    private Long buyerId;

    /** 买家名称 */
    private String buyerName;

    /** 卖家用户ID */
    private Long sellerId;

    /** 卖家名称 */
    private String sellerName;

    /** 合同正文内容（富文本或纯文本） */
    private String content;

    /** 合同文件URL（PDF/图片等） */
    private String fileUrl;
}
