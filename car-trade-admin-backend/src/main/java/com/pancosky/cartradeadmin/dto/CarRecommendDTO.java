package com.pancosky.cartradeadmin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CarRecommendDTO {

    @NotNull(message = "推荐状态不能为空")
    private Boolean recommended;
}
