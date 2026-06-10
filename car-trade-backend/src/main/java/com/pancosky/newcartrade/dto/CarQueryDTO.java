package com.pancosky.newcartrade.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 车源查询/搜索请求 DTO
 * 描述：承载车源列表的搜索、筛选、排序、分页参数，用于 GET /api/cars 等列表接口。
 * 使用场景：车源列表页、筛选页、卖家车源列表等。
 * 注意：本 DTO 同时提供两种命名约定的参数（priceMin/minPrice、ageMin/minAge、mileageMin/minMileage）以兼容前端。
 */
@Data
public class CarQueryDTO {

    /** 关键词（搜索车辆标题/品牌/车系/车型，模糊匹配） */
    private String keyword;

    /** 按品牌筛选（关联 brands.id） */
    private Integer brandId;

    /** 按车系筛选（关联 series.id） */
    private Integer seriesId;

    /** 最低价格（单位：元；标准命名） */
    private BigDecimal priceMin;

    /** 最高价格（单位：元；标准命名） */
    private BigDecimal priceMax;

    /** 最低价格（兼容前端参数） */
    private BigDecimal minPrice;

    /** 最高价格（兼容前端参数） */
    private BigDecimal maxPrice;

    /** 最低车龄（单位：年；标准命名） */
    private Integer ageMin;

    /** 最高车龄（单位：年；标准命名） */
    private Integer ageMax;

    /** 最低车龄（兼容前端参数） */
    private Integer minAge;

    /** 最高车龄（兼容前端参数） */
    private Integer maxAge;

    /** 最低里程（单位：公里；标准命名） */
    private BigDecimal mileageMin;

    /** 最高里程（单位：公里；标准命名） */
    private BigDecimal mileageMax;

    /** 最低里程（兼容前端参数） */
    private BigDecimal minMileage;

    /** 最高里程（兼容前端参数） */
    private BigDecimal maxMileage;

    /** 变速箱类型（AUTO=自动；MANUAL=手动；CVT；DCT） */
    private String transmission;

    /** 所在城市编码 */
    private String cityCode;

    /** 能源类型（GASOLINE/HYBRID/ELECTRIC/PLUG_IN） */
    private String energyType;

    /** 出口国家筛选（如 "RU"） */
    private String exportCountry;

    /** 地区/区域关键字（用于城市名/省份名的模糊匹配） */
    private String region;

    /** 是否支持保证金/锁车（true=仅展示支持保证金的车源） */
    private Boolean deposit;

    /** 排序方式（可选：price_asc / price_desc / mileage_asc / created_desc；默认 created_desc） */
    private String sort;

    /** 当前页（从 1 开始，默认 1） */
    private Integer page = 1;

    /** 每页条数（默认 10，最大 100） */
    private Integer size = 10;
}
