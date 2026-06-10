package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 出价记录VO
 * 描述：拍卖出价的历史记录，用于拍卖详情页出价列表展示。
 */
@Data
public class BidRecordVO {

    /** 出价记录ID */
    private Long id;

    /** 关联拍卖ID */
    private Long auctionId;

    /** 出价人ID */
    private Long bidderId;

    /** 出价人名称（昵称，可能脱敏展示） */
    private String bidderName;

    /** 出价人头像URL */
    private String bidderAvatar;

    /** 出价金额（元） */
    private BigDecimal bidPrice;

    /** 出价时间 */
    private LocalDateTime bidTime;

    /** 是否为当前最高价（用于标记领先出价） */
    private Boolean isWinning;
}
