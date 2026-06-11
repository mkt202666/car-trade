package com.pancosky.newcartrade.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 更新车源请求 DTO
 * 描述：卖家修改已发布/草稿车源信息时使用，请求体字段与 CarCreateDTO 类似但不含创建时必填字段。
 * 使用场景：PUT /api/cars/{id} 接口的请求体。
 * 注意：未传的字段将保持原值，不会被清空。
 */
@Data
public class CarUpdateDTO {

    /** 车辆品牌ID */
    private Integer brandId;

    /** 车辆车系ID */
    private Integer seriesId;

    /** 车辆车型ID */
    private Integer modelId;

    /** 上牌年份 */
    private Integer year;

    /** 行驶里程（单位：公里） */
    private Integer mileage;

    /** 一口价价格（单位：元） */
    private BigDecimal price;

    /** 保证金金额（单位：元） */
    private BigDecimal deposit;

    /** 车身颜色 */
    private String color;

    /** 所在城市行政区划编码 */
    private String cityCode;

    /** 所在城市名称 */
    private String cityName;

    /** 能源类型 */
    private String energyType;

    /** 使用性质 */
    private String usageType;

    /** 过户次数描述 */
    private String ownerType;

    /** 是否存在抵押登记 */
    private Boolean isMortgaged;

    /** 是否为继承车辆 */
    private Boolean isInherited;

    /** 首次上牌日期 */
    private LocalDate registrationDate;

    /** 交强险到期日期 */
    private LocalDate insuranceExpiry;

    /** 年检到期日期 */
    private LocalDate inspectionExpiry;

    /** 出厂日期字符串 */
    private String productionDate;

    /** 钥匙数量 */
    private Integer keyCount;

    /** 车源描述（富文本） */
    private String description;

    /** 车源视频URL */
    private String videoUrl;

    /** 车源标题 */
    private String title;

    /** 车源图片URL列表（全量替换，请传入完整列表） */
    private List<String> images;

    /** 车辆检测报告信息（嵌套对象） */
    private CarInspectionDTO inspection;

    /** 支持的出口国家列表 */
    private List<String> exportCountries;
}
