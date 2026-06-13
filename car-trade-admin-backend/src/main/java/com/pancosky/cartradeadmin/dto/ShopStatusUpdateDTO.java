package com.pancosky.cartradeadmin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ShopStatusUpdateDTO {
    @NotBlank(message = "状态不能为空")
    private String status;
}
