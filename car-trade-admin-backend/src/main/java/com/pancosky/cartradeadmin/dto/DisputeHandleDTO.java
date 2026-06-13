package com.pancosky.cartradeadmin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DisputeHandleDTO {

    @NotBlank(message = "action不能为空")
    private String action;

    private String result;
}
