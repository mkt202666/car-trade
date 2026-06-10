package com.pancosky.newcartrade.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 拍卖出价记录表
 */
@Data
@TableName("auction_bids")
public class AuctionBid {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long auctionId;
    private Long bidderId;
    private BigDecimal bidPrice;
    private LocalDateTime bidTime;
    private Boolean isWinning = false;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
