package com.pancosky.newcartrade.vo;

import com.pancosky.newcartrade.enums.AuctionStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 拍卖列表VO
 */
@Data
public class AuctionVO {

    private Long id;
    private Long carId;
    private String carName;
    private String carImage;
    private String carBrand;
    private String carSeries;
    private Integer carYear;
    private Integer carMileage;
    private String city;
    private BigDecimal startPrice;
    private BigDecimal currentPrice;
    private BigDecimal bidIncrement;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AuctionStatus status;
    private Long sellerId;
    private String sellerName;
    private String sellerAvatar;
    private Integer totalBids;
    private Long viewCount;
    private Boolean isWatching;
    private Boolean canBid;
    private LocalDateTime createdAt;
}