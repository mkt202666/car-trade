package com.pancosky.newcartrade.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 城市信息VO
 * 描述：用于前端城市选择器展示的城市信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CityVO {

    /** 城市编码 */
    private String code;

    /** 城市名称 */
    private String name;

    /** 省份名称 */
    private String province;

    /** 是否热门城市 */
    private Boolean hot;

    /** 排序序号 */
    private Integer sortOrder;
}