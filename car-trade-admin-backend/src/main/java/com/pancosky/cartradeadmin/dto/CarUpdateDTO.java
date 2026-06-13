package com.pancosky.cartradeadmin.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 管理员编辑车源信息 DTO
 */
@Data
public class CarUpdateDTO {

    /** 标题 */
    @NotBlank(message = "标题不能为空")
    private String title;

    /** 品牌ID */
    @NotNull(message = "品牌ID不能为空")
    private Long brandId;

    /** 车系ID */
    @NotNull(message = "车系ID不能为空")
    private Long seriesId;

    /** 车型ID（可选） */
    private Long modelId;

    /** 城市名称 */
    @NotBlank(message = "城市不能为空")
    private String cityName;

    /** 能源类型 */
    @NotBlank(message = "能源类型不能为空")
    private String energyType;

    /** 价格（万元） */
    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    private BigDecimal price;

    /** 里程（公里） */
    @NotNull(message = "里程不能为空")
    @DecimalMin(value = "0", message = "里程不能为负数")
    private BigDecimal mileage;

    /** 年份 */
    @NotNull(message = "年份不能为空")
    private Integer year;

    /** 车源描述 */
    private String description;

    /** 状态（ON_SALE / OFFLINE） */
    private String status;
}
