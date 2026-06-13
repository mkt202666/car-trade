package com.pancosky.cartradeadmin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConfigUpdateDTO {
    @NotBlank(message = "配置内容不能为空")
    private String content;
}
