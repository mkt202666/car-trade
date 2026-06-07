package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CarVO {
    private Long id;
    private String title;
    private String brandName;
    private String seriesName;
    private String modelName;
    private Integer year;
    private Integer mileage;
    private BigDecimal price;
    private BigDecimal deposit;
    /** 是否需要保证金（前端判断用） */
    private Boolean hasDeposit;
    private String color;
    private String city;
    private String cityCode;
    /** 能源类型：燃油/纯电/混动 */
    private String energyType;
    private String usageType;
    private Boolean isMortgaged;
    /** 出口国家代码列表，如 ["RU","KZ"] */
    private List<String> exportCountries;
    /** 是否支持全球购 */
    private Boolean isGlobal;
    /** 封面图 URL */
    private String coverImage;
    private List<String> images;
    private List<String> tags;
    private String auctionStatus;
    /** 拍卖剩余时间（中文描述） */
    private String auctionRemaining;
    /** 是否已参拍（前端判断用） */
    private Boolean hasParticipated;
    private LocalDateTime auctionEndTime;
    private LocalDateTime createdAt;
    /** 创建时间格式化字符串 */
    private String createTime;
    private Long viewCount;
    private Integer favoriteCount;
}
