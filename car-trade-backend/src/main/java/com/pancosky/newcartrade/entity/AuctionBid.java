package com.pancosky.newcartrade.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 拍卖出价记录表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("auction_bids")
public class AuctionBid {

    @Id
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("auction_id")
    private Long auctionId;

    @TableField("bidder_id")
    private Long bidderId;

    @TableField("bid_price")
    private BigDecimal bidPrice;

    @TableField("bid_time")
    private LocalDateTime bidTime;

    @TableField("is_winning")
    private Boolean isWinning = false;

    @TableField("created_at")
    private LocalDateTime createdAt;
}