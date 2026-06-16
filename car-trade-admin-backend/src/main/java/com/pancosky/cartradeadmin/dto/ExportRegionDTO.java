package com.pancosky.cartradeadmin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ExportRegionDTO {

    @NotBlank(message = "国家代码不能为空")
    private String code;

    @NotBlank(message = "地区名称不能为空")
    private String name;

    private String flag;

    @NotBlank(message = "分组不能为空")
    private String group;

    @NotBlank(message = "分组标识不能为空")
    private String groupKey;

    private String icon;

    private String constraints;

    private String requirements;

    private String status;
}
