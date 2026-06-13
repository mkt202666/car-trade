package com.pancosky.cartradeadmin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CarSeriesDTO {
    @NotNull(message = "品牌ID不能为空")
    private Long brandId;

    @NotBlank(message = "车系名称不能为空")
    private String name;
}
