package com.pancosky.newcartrade.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.pancosky.newcartrade.enums.AuctionStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 拍卖表
 */
@Data
@TableName("auctions")
public class Auction {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long carId;
    private Long sellerId;
    private BigDecimal startPrice;
    private BigDecimal reservePrice;
    private BigDecimal currentPrice;
    private BigDecimal bidIncrement;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime actualEndTime;

    @TableField("status")
    private AuctionStatus status = AuctionStatus.PENDING;

    private Long winnerId;
    private BigDecimal winningPrice;
    private Integer totalBids = 0;
    private Long viewCount = 0L;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @Version
    private Integer version;
}
