package com.pancosky.newcartrade.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 拍卖关注/观看表
 */
@Data
@TableName("auction_watches")
public class AuctionWatch {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long auctionId;
    private Long userId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
