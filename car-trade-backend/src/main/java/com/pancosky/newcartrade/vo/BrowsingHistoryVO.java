package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 浏览历史VO
 * 描述：用户浏览过的车源记录，用于"我的浏览"页展示浏览过的车源摘要信息。
 * 用途：用于 /api/v1/users/me/browsing 接口返回。
 */
@Data
public class BrowsingHistoryVO {

    /** 浏览记录ID */
    private Long id;

    /** 车源ID（关联 car_sources.id，用于跳转至车源详情） */
    private Long carId;

    /** 车源名称（品牌+系列+年款+车型的组合名称） */
    private String carName;

    /** 车源封面图URL */
    private String carImage;

    /** 车源价格（元） */
    private BigDecimal price;

    /** 车源里程（公里） */
    private Integer mileage;

    /** 车源所在城市 */
    private String city;

    /** 浏览时间 */
    private LocalDateTime createdAt;
}
