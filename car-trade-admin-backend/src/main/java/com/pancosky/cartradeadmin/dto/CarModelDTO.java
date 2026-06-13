package com.pancosky.cartradeadmin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CarModelDTO {
    @NotNull(message = "车系ID不能为空")
    private Long seriesId;

    @NotBlank(message = "车型名称不能为空")
    private String name;

    private Integer year;
    private BigDecimal price;
}
