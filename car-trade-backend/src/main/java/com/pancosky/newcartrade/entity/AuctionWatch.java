package com.pancosky.newcartrade.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 拍卖关注/观看表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("auction_watches")
public class AuctionWatch {

    @Id
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("auction_id")
    private Long auctionId;

    @TableField("user_id")
    private Long userId;

    @TableField("created_at")
    private LocalDateTime createdAt;
}