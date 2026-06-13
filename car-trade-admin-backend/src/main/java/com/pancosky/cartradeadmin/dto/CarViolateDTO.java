package com.pancosky.cartradeadmin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CarViolateDTO {
    @NotBlank(message = "违规原因不能为空")
    private String reason;

    @NotBlank(message = "状态不能为空")
    private String status;
}
