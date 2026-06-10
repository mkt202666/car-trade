package com.pancosky.newcartrade.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 车源信息实体类
 * 描述：二手车交易的核心商品实体，包含车辆基本信息、交易方式（一口价/拍卖）、
 *       车源状态、认证材料等。用于列表展示、详情页、搜索与筛选。
 * 关联：外键 userId → users；brandId/seriesId/modelId → 品牌/车系/车型字典表；
 *       关联 car_images（图片）、car_inspections（检测报告）、car_tags（标签）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("car_sources")
public class CarSource {

    /** 车源ID（主键，与订单、拍卖、图片表等关联） */
    @TableId
    private Long id;

    /** 发布者用户ID（关联 users.id，指向卖家） */
    private Long userId;

    /** 品牌ID（关联 brands.id，如 1=宝马） */
    private Integer brandId;

    /** 车系ID（关联 series.id，如 "宝马 5系"） */
    private Integer seriesId;

    /** 车型ID（关联 models.id，如 "525Li 豪华套装 2023款"） */
    private Integer modelId;

    /** 车源标题（展示在列表/详情页顶部，如"宝马5系 2023款 525Li 豪华套装"） */
    private String title;

    /** 车架号（VIN 17位码，用于车辆身份唯一标识，敏感信息脱敏后展示） */
    private String vin;

    /** 上牌年份（如 2023，表示车辆首次上牌年份） */
    private Integer year;

    /** 行驶里程（单位：公里，用于车况评估与筛选） */
    private Integer mileage;

    /** 一口价价格（单位：元，当 pricingType=FIXED 时使用） */
    private BigDecimal price;

    /** 定价方式（FIXED=一口价；AUCTION=拍卖；决定交易流程路径） */
    private String pricingType;

    /** 起拍价（拍卖模式下的起始价格，单位：元） */
    private BigDecimal startingPrice;

    /** 封顶价（拍卖模式下的最高成交价格，防止恶性竞价） */
    private BigDecimal ceilingPrice;

    /** 加价幅度（每次出价的最小递增金额） */
    private BigDecimal bidIncrement;

    /** 保证金金额（买家在下单/出价前需缴纳的保证金数额，单位：元） */
    private BigDecimal deposit;

    /** 车身颜色（外饰颜色描述，如"矿石白"） */
    private String color;

    /** 车辆所在城市行政区划编码（如 "110100"=北京） */
    private String cityCode;

    /** 车辆所在城市名称（冗余字段，便于前端展示） */
    private String cityName;

    /** 能源类型（GASOLINE=燃油；HYBRID=混动；ELECTRIC=纯电；PLUG_IN=插混） */
    private String energyType;

    /** 变速箱类型（AUTO=自动；MANUAL=手动；CVT=无级变速；DCT=双离合） */
    private String transmission;

    /** 使用性质（PRIVATE=非营运；OPERATING=营运；RENTAL=租赁） */
    private String usageType;

    /** 过户次数（0=一手车；1=一次过户；>1=多手车） */
    private String ownerType;

    /** 是否抵押（true=存在抵押登记；false=无抵押，影响交易流程复杂度） */
    private Boolean isMortgaged;

    /** 是否继承车辆（true=继承过户；false=常规购买） */
    private Boolean isInherited;

    /** 首次上牌日期（用于计算车龄和年检周期） */
    private LocalDate registrationDate;

    /** 交强险到期日期（判断车辆是否合法上路） */
    private LocalDate insuranceExpiry;

    /** 年检到期日期（判断是否需要立即年检） */
    private LocalDate inspectionExpiry;

    /** 出厂日期（车辆制造日期字符串，格式 YYYY-MM） */
    private String productionDate;

    /** 钥匙数量（影响车辆估值与交付信息） */
    private Integer keyCount;

    /** 车源详细描述（富文本描述，可包含文字与图片） */
    private String description;

    /** 检测报告类型（LINK=外链报告；FILE=上传文件；NONE=未提供） */
    private String inspectionReportType;

    /** 检测报告URL（检测报告的访问地址，配合 inspectionReportType 使用） */
    private String inspectionReportUrl;

    /** 证件材料JSON（行驶证、登记证、发票等证件信息的JSON字符串，如{"license":"xxx","cert":"yyy"}） */
    private String certificateMaterials;

    /** 是否支持锁车洽谈（true=卖家支持买家缴纳保证金后锁车协商） */
    private Boolean supportLockNegotiation;

    /** 是否启用AI自动推广（true=系统自动通过AI渠道推广此车源） */
    private Boolean aiAutoPromote;

    /** 是否草稿（true=未发布草稿状态，不展示给买家） */
    private Boolean isDraft;

    /** 拍卖状态（仅当pricingType=AUCTION时有值，参考 enums.AuctionStatus） */
    private String auctionStatus;

    /** 拍卖结束时间（若为拍卖车源，记录拍卖截止时间） */
    private LocalDateTime auctionEndTime;

    /** 累计浏览次数（用于热度排序） */
    private Long viewCount;

    /** 被收藏次数（用于热度/推荐统计） */
    private Integer favoriteCount;

    /** 车源状态（ON_SALE=在售；SOLD=已售；OFFLINE=下架；DRAFT=草稿） */
    private String status;

    /** 发布时间（卖家点击"发布"的时间，用于排序） */
    private LocalDateTime publishedAt;

    /** 支持出口国家列表（逗号分隔的国家代码，如 "RU,AE"） */
    private String exportCountries;

    /** 非持久化字段：品牌名称（用于列表/详情展示，不入库） */
    @TableField(exist = false)
    private String brandName;

    /** 非持久化字段：车系名称（用于列表/详情展示，不入库） */
    @TableField(exist = false)
    private String seriesName;

    /** 非持久化字段：车型名称（用于列表/详情展示，不入库） */
    @TableField(exist = false)
    private String modelName;

    /** 非持久化字段：封面图片URL（从 car_images 取第一张作为主图） */
    @TableField(exist = false)
    private String coverImage;

    /** 记录创建时间（由 MyBatis-Plus 自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 记录最后更新时间（由 MyBatis-Plus 自动填充） */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /** 软删除时间（非空表示已逻辑删除） */
    private LocalDateTime deletedAt;
}
