package com.pancosky.newcartrade.vo;

import com.pancosky.newcartrade.enums.AuctionStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 拍卖详情VO
 */
@Data
public class AuctionDetailVO {

    private Long id;
    private Long carId;
    private String carName;
    private List<String> carImages;
    private String carBrand;
    private String carSeries;
    private Integer carYear;
    private Integer carMileage;
    private String city;
    private BigDecimal startPrice;
    private BigDecimal reservePrice;
    private BigDecimal currentPrice;
    private BigDecimal bidIncrement;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AuctionStatus status;
    private Long sellerId;
    private String sellerName;
    private String sellerAvatar;
    private String sellerShopName;
    private Integer totalBids;
    private Long viewCount;
    private Long winnerId;
    private BigDecimal winningPrice;
    private LocalDateTime actualEndTime;
    private Boolean isWatching;
    private Boolean canBid;
    private List<BidRecordVO> recentBids;
    private LocalDateTime createdAt;
}