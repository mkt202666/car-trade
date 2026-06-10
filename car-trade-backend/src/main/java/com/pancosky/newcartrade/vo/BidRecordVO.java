package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 出价记录VO
 */
@Data
public class BidRecordVO {

    private Long id;
    private Long auctionId;
    private Long bidderId;
    private String bidderName;
    private String bidderAvatar;
    private BigDecimal bidPrice;
    private LocalDateTime bidTime;
    private Boolean isWinning;
}