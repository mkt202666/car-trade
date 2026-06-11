package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 拍卖详情VO
 * 描述：拍卖详情页展示的完整拍卖信息，包括车辆信息、价格信息、卖家信息、出价记录等。
 */
@Data
public class AuctionDetailVO {

    /** 拍卖ID */
    private Long id;

    /** 关联车源ID */
    private Long carId;

    /** 车源名称 */
    private String carName;

    /** 车源图片URL列表（轮播使用） */
    private List<String> carImages;

    /** 车源品牌 */
    private String carBrand;

    /** 车源系列 */
    private String carSeries;

    /** 车源出厂年份 */
    private Integer carYear;

    /** 车源行驶里程 */
    private Integer carMileage;

    /** 车源所在城市 */
    private String city;

    /** 起拍价 */
    private BigDecimal startPrice;

    /** 保留价（卖家设置的最低成交价格，若未到达则流拍） */
    private BigDecimal reservePrice;

    /** 当前最高价 */
    private BigDecimal currentPrice;

    /** 加价幅度 */
    private BigDecimal bidIncrement;

    /** 拍卖开始时间 */
    private LocalDateTime startTime;

    /** 拍卖结束时间 */
    private LocalDateTime endTime;

    /** 拍卖状态（PENDING=待开始；BIDDING=竞拍中；ENDED=已结束；SETTLED=已结算；CANCELLED=已取消；FAILED=流拍） */
    private String status;

    /** 卖家ID */
    private Long sellerId;

    /** 卖家名称 */
    private String sellerName;

    /** 卖家头像URL */
    private String sellerAvatar;

    /** 卖家车行名称 */
    private String sellerShopName;

    /** 总出价次数 */
    private Integer totalBids;

    /** 累计浏览次数 */
    private Long viewCount;

    /** 最终买家ID（拍卖成交后填入） */
    private Long winnerId;

    /** 最终成交价格 */
    private BigDecimal winningPrice;

    /** 实际结束时间（提前成交或强制结束时间） */
    private LocalDateTime actualEndTime;

    /** 当前用户是否已关注此拍卖 */
    private Boolean isWatching;

    /** 当前用户是否可出价 */
    private Boolean canBid;

    /** 最近的出价记录（用于详情页滚动展示） */
    private List<BidRecordVO> recentBids;

    /** 拍卖创建时间 */
    private LocalDateTime createdAt;
}
