package com.pancosky.newcartrade.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 拍卖出价记录实体类
 * 描述：记录某用户对某拍卖活动的单次出价，用于构建出价历史、判断中标、
 *       防机器人、防恶意竞价等。
 * 关联：外键 auctionId → auctions；bidderId → users。
 */
@Data
@TableName("tc_auction_bids")
public class AuctionBid {

    /** 出价记录ID（主键，使用 MyBatis-Plus 雪花算法） */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 拍卖ID（关联 auctions.id） */
    private Long auctionId;

    /** 出价人ID（关联 users.id，记录出价用户） */
    private Long bidderId;

    /** 出价金额（单位：元，本次出价金额） */
    private BigDecimal bidPrice;

    /** 出价时间（记录该次出价发生的时间戳） */
    private LocalDateTime bidTime;

    /** 是否为中标价（true=本次出价为拍卖最终成交价，默认 false） */
    private Boolean isWinning = false;

    /** 记录创建时间（由 MyBatis-Plus 自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
