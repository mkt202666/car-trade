package com.pancosky.newcartrade.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 新建车源请求 DTO
 * 描述：卖家在前台发布新车源时的请求体，包含车辆基本信息、交易方式、认证资料等。
 * 使用场景：POST /api/cars 接口的请求体。
 */
@Data
public class CarCreateDTO {

    /** 车辆品牌ID（关联 brands.id） */
    private Integer brandId;

    /** 车辆车系ID（关联 series.id） */
    private Integer seriesId;

    /** 车辆车型ID（关联 models.id） */
    private Integer modelId;

    /** 车架号VIN（17位码，敏感信息） */
    private String vin;

    /** 上牌年份（如 2023） */
    private Integer year;

    /** 行驶里程（单位：公里） */
    private Integer mileage;

    /** 一口价价格（单位：元；pricingType=FIXED 时使用） */
    private BigDecimal price;

    /** 定价方式（FIXED=一口价；AUCTION=拍卖） */
    private String pricingType;

    /** 起拍价（单位：元；pricingType=AUCTION 时使用） */
    private BigDecimal startingPrice;

    /** 封顶价（单位：元；拍卖时的最高价格，超过后自动成交） */
    private BigDecimal ceilingPrice;

    /** 加价幅度（每次出价的最小递增金额，单位：元） */
    private BigDecimal bidIncrement;

    /** 保证金金额（买家下单/出价前需缴纳保证金，单位：元） */
    private BigDecimal deposit;

    /** 车身颜色（外饰颜色描述） */
    private String color;

    /** 车辆所在城市行政区划编码 */
    private String cityCode;

    /** 车辆所在城市名称（冗余字段，便于前端展示） */
    private String cityName;

    /** 能源类型（GASOLINE=燃油；HYBRID=混动；ELECTRIC=纯电；PLUG_IN=插混） */
    private String energyType;

    /** 使用性质（PRIVATE=非营运；OPERATING=营运；RENTAL=租赁） */
    private String usageType;

    /** 过户次数描述（"0"/"1"/..."多手"等） */
    private String ownerType;

    /** 是否存在抵押登记 */
    private Boolean isMortgaged;

    /** 是否为继承车辆 */
    private Boolean isInherited;

    /** 首次上牌日期（用于计算车龄） */
    private LocalDate registrationDate;

    /** 交强险到期日期 */
    private LocalDate insuranceExpiry;

    /** 年检到期日期 */
    private LocalDate inspectionExpiry;

    /** 出厂日期字符串（格式 YYYY-MM） */
    private String productionDate;

    /** 钥匙数量 */
    private Integer keyCount;

    /** 车源描述（富文本） */
    private String description;

    /** 车源标题（列表/详情页顶部展示） */
    private String title;

    /** 检测报告类型（LINK=外链；FILE=上传文件；NONE=无） */
    private String inspectionReportType;

    /** 检测报告URL（配合 inspectionReportType 使用） */
    private String inspectionReportUrl;

    /** 证件材料信息（JSON 字符串，包含行驶证、登记证等） */
    private String certificateMaterials;

    /** 是否支持锁车洽谈功能 */
    private Boolean supportLockNegotiation;

    /** 是否启用 AI 自动推广（由系统决定渠道与方式） */
    private Boolean aiAutoPromote;

    /** 是否仅保存为草稿（true=草稿，不展示给买家） */
    private Boolean isDraft;

    /** 车源视频URL */
    private String videoUrl;

    /** 车源图片URL列表（主图、外观、内饰等顺序由前端排序后传入） */
    private List<String> images;

    /** 车辆检测报告信息（嵌套对象） */
    private CarInspectionDTO inspection;

    /** 支持的出口国家（代码列表，如 ["RU","AE"]） */
    private List<String> exportCountries;
}
