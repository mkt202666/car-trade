package com.pancosky.newcartrade.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 拍卖关注/围观记录实体类
 * 描述：记录用户"关注/围观"某个拍卖活动的行为，用于消息推送、热度统计。
 * 关联：外键 auctionId → auctions；userId → users；(auctionId, userId) 唯一。
 */
@Data
@TableName("auction_watches")
public class AuctionWatch {

    /** 记录ID（主键，使用 MyBatis-Plus 雪花算法） */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 拍卖ID（关联 auctions.id） */
    private Long auctionId;

    /** 用户ID（关注该拍卖活动的用户） */
    private Long userId;

    /** 记录创建时间（由 MyBatis-Plus 自动填充；即关注时间） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
