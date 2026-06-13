package com.pancosky.cartradeadmin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ShopReviewRejectDTO {
    @NotBlank(message = "拒绝原因不能为空")
    private String reason;
}
