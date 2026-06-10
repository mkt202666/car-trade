package com.pancosky.newcartrade.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.pancosky.newcartrade.enums.AuctionStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 拍卖表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("auctions")
public class Auction {

    @Id
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("car_id")
    private Long carId;

    @TableField("seller_id")
    private Long sellerId;

    @TableField("start_price")
    private BigDecimal startPrice;

    @TableField("reserve_price")
    private BigDecimal reservePrice;

    @TableField("current_price")
    private BigDecimal currentPrice;

    @TableField("bid_increment")
    private BigDecimal bidIncrement;

    @TableField("start_time")
    private LocalDateTime startTime;

    @TableField("end_time")
    private LocalDateTime endTime;

    @TableField("actual_end_time")
    private LocalDateTime actualEndTime;

    @Enumerated(EnumType.STRING)
    @TableField("status")
    private AuctionStatus status = AuctionStatus.PENDING;

    @TableField("winner_id")
    private Long winnerId;

    @TableField("winning_price")
    private BigDecimal winningPrice;

    @TableField("total_bids")
    private Integer totalBids = 0;

    @TableField("view_count")
    private Long viewCount = 0L;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    @Version
    @TableField("version")
    private Integer version;
}