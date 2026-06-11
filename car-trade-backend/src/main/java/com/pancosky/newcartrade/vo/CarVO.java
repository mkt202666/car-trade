package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 车源列表VO
 * 描述：车源列表、搜索结果、店铺车源卡片的摘要信息，包含车源基本参数、价格、拍卖状态、封面图等。
 * 用途：用于 /api/v1/cars 列表接口返回。
 */
@Data
public class CarVO {

    /** 车源ID */
    private Long id;

    /** 车源标题（由品牌、系列、年款、车型等拼接而成） */
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

    /** 保证金金额（元，若有） */
    private BigDecimal deposit;

    /** 是否需要保证金（前端判断按钮展示"需要/普通车源按钮样式） */
    private Boolean hasDeposit;

    /** 车身颜色 */
    private String color;

    /** 车源所在城市名称 */
    private String city;

    /** 车源所在城市代码（用于筛选、排序等使用） */
    private String cityCode;

    /** 能源类型：燃油/纯电/混动 */
    private String energyType;

    /** 车辆性质（如 家用车 / 营运车等） */
    private String usageType;

    /** 是否抵押车（true 表示有抵押记录） */
    private Boolean isMortgaged;

    /** 出口国家代码列表，如 ["RU","KZ"] */
    private List<String> exportCountries;

    /** 是否支持全球购 */
    private Boolean isGlobal;

    /** 封面图URL */
    private String coverImage;

    /** 车源图片URL列表（轮播使用） */
    private List<String> images;

    /** 车源标签（如 "准新车"、"官方认证"等） */
    private List<String> tags;

    /** 车源视频URL */
    private String videoUrl;

    /** 拍卖状态（如 NONE / ACTIVE / ENDED / CANCELLED） */
    private String auctionStatus;

    /** 拍卖剩余时间（中文描述，如"剩余3天12小时） */
    private String auctionRemaining;

    /** 当前用户是否已参拍 */
    private Boolean hasParticipated;

    /** 拍卖结束时间 */
    private LocalDateTime auctionEndTime;

    /** 车源发布时间 */
    private LocalDateTime createdAt;

    /** 创建时间格式化字符串（yyyy-MM-dd 等格式） */
    private String createTime;

    /** 累计浏览次数 */
    private Long viewCount;

    /** 累计收藏数 */
    private Integer favoriteCount;
}
