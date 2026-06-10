package com.pancosky.newcartrade.vo;

import com.pancosky.newcartrade.enums.AuctionStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 拍卖列表VO
 * 描述：拍卖场列表卡片信息，用于拍卖列表页展示，包含车辆基本信息、价格、时间、卖家信息等。
 */
@Data
public class AuctionVO {

    /** 拍卖ID */
    private Long id;

    /** 关联车源ID */
    private Long carId;

    /** 车源名称 */
    private String carName;

    /** 车源封面图URL */
    private String carImage;

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

    /** 当前最高价 */
    private BigDecimal currentPrice;

    /** 加价幅度（每次出价必须为加价幅度的整数倍） */
    private BigDecimal bidIncrement;

    /** 拍卖开始时间 */
    private LocalDateTime startTime;

    /** 拍卖结束时间 */
    private LocalDateTime endTime;

    /** 拍卖状态（枚举：PENDING/ACTIVE/ENDED/CANCELLED等） */
    private AuctionStatus status;

    /** 卖家ID */
    private Long sellerId;

    /** 卖家名称 */
    private String sellerName;

    /** 卖家头像URL */
    private String sellerAvatar;

    /** 总出价次数 */
    private Integer totalBids;

    /** 累计浏览次数 */
    private Long viewCount;

    /** 当前用户是否已关注此拍卖 */
    private Boolean isWatching;

    /** 当前用户是否可出价（是否满足保证金/实名认证等条件） */
    private Boolean canBid;

    /** 拍卖创建时间 */
    private LocalDateTime createdAt;
}
