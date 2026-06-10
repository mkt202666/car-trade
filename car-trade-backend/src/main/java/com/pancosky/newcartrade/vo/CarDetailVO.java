package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 车源详情VO
 * 描述：车源详情页展示的完整车源完整信息，包含基本参数、卖家信息、拍卖状态、以及验车信息。
 * 用途：用于 /api/v1/cars/{id} 接口返回。
 */
@Data
public class CarDetailVO {

    /** 车源ID */
    private Long id;

    /** 车源标题 */
    private String title;

    /** 品牌名称 */
    private String brandName;

    /** 系列名称 */
    private String seriesName;

    /** 车型名称 */
    private String modelName;

    /** 出厂年份 */
    private Integer year;

    /** 行驶里程（公里） */
    private Integer mileage;

    /** 售价（元） */
    private BigDecimal price;

    /** 保证金金额（元） */
    private BigDecimal deposit;

    /** 车身颜色 */
    private String color;

    /** 所在城市名称 */
    private String city;

    /** 所在城市代码 */
    private String cityCode;

    /** 能源类型：燃油/纯电/混动 */
    private String energyType;

    /** 车辆性质 */
    private String usageType;

    /** 车辆所有人性质（个人/商家） */
    private String ownerType;

    /** 是否抵押车 */
    private Boolean isMortgaged;

    /** 是否继承车（原车主继承得来） */
    private Boolean isInherited;

    /** 首次注册日期（上牌日期） */
    private LocalDate registrationDate;

    /** 保险到期日期 */
    private LocalDate insuranceExpiry;

    /** 年检到期日期 */
    private LocalDate inspectionExpiry;

    /** 生产日期（文本格式） */
    private String productionDate;

    /** 钥匙数量 */
    private Integer keyCount;

    /** 车源详细描述 */
    private String description;

    /** 封面图URL */
    private String coverImage;

    /** 车源图片URL列表 */
    private List<String> images;

    /** 车源标签列表 */
    private List<String> tags;

    /** 拍卖状态 */
    private String auctionStatus;

    /** 拍卖结束时间 */
    private LocalDateTime auctionEndTime;

    /** 发布时间 */
    private LocalDateTime createdAt;

    /** 累计浏览次数 */
    private Long viewCount;

    /** 累计收藏数 */
    private Integer favoriteCount;

    /** 卖家ID */
    private Long sellerId;

    /** 卖家昵称 */
    private String sellerName;

    /** 卖家头像URL */
    private String sellerAvatar;

    /** 卖家车行名称 */
    private String sellerShopName;

    /** 卖家信用等级 */
    private String sellerCreditGrade;

    /** 卖家累计成交订单数 */
    private Integer sellerDealCount;

    /** 卖家粉丝数 */
    private Integer sellerFollowerCount;

    /** 当前用户是否已关注卖家 */
    private boolean followedByCurrentUser;

    /** 当前用户是否已收藏此车源 */
    private boolean favoritedByCurrentUser;

    /** 验车综合车况评级 */
    private String overallCondition;

    /** 车漆状况描述 */
    private String paint;

    /** 车身结构状况描述 */
    private String structure;

    /** 发动机状况描述 */
    private String engine;

    /** 变速箱状况描述 */
    private String transmission;

    /** 历史过户次数 */
    private Integer transferCount;

    /** 里程表类型（REAL 真实 / TAMPERED 疑似调表 / UNKNOWN 未知） */
    private String mileageType;

    /** 异常照片URL列表 */
    private List<String> abnormalPhotos;
}
