package com.pancosky.newcartrade.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 拍卖活动实体类
 * 描述：针对单车源发起的线上竞价活动，记录起止时间、价格、中标人等信息。
 *       与 CarSource 一一对应（若 car_sources.pricingType = AUCTION，则存在一条对应的 Auction）。
 * 关联：外键 carId → car_sources；sellerId/winnerId → users；
 *       关联 auction_bids（出价记录）、auction_watches（关注记录）。
 * 注意：使用 @Version 实现 MyBatis-Plus 乐观锁，避免并发出价导致的价格错乱。
 */
@Data
@TableName("auctions")
public class Auction {

    /** 拍卖ID（主键，使用 MyBatis-Plus 雪花算法自动生成） */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 关联车源ID（本次拍卖活动对应的车辆） */
    private Long carId;

    /** 卖家ID（发起拍卖的用户，关联 users.id） */
    private Long sellerId;

    /** 起拍价（单位：元，竞价起点价格） */
    private BigDecimal startPrice;

    /** 保留价/底价（卖家心理最低成交价，若最终出价未达此价则流拍） */
    private BigDecimal reservePrice;

    /** 当前最高价（实时刷新，展示在拍卖详情页） */
    private BigDecimal currentPrice;

    /** 加价幅度（每次出价的最小递增金额，单位：元） */
    private BigDecimal bidIncrement;

    /** 拍卖开始时间（计划开始时间） */
    private LocalDateTime startTime;

    /** 拍卖结束时间（计划结束时间，支持延时） */
    private LocalDateTime endTime;

    /** 实际结束时间（拍卖真实结束时间，含延时策略） */
    private LocalDateTime actualEndTime;

    /** 拍卖状态（PENDING=待开始；BIDDING=竞拍中；ENDED=已结束；
     *  SETTLED=已结算；CANCELLED=已取消；FAILED=流拍） */
    @TableField("status")
    private String status = "PENDING";

    /** 中标人ID（竞价结束后最高出价者，关联 users.id） */
    private Long winnerId;

    /** 中标价（最终成交价，单位：元） */
    private BigDecimal winningPrice;

    /** 总出价次数（该拍卖收到的有效出价数，用于数据展示） */
    private Integer totalBids = 0;

    /** 围观/浏览次数（用于热度展示） */
    private Long viewCount = 0L;

    /** 记录创建时间（由 MyBatis-Plus 自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 记录最后更新时间（由 MyBatis-Plus 自动填充） */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /** 乐观锁版本号（用于防止出价时的并发覆盖问题） */
    @Version
    private Integer version;
}
