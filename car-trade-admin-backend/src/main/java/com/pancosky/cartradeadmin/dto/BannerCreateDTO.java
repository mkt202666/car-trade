package com.pancosky.cartradeadmin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BannerCreateDTO {
    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "图片链接不能为空")
    private String imageUrl;

    @NotBlank(message = "类型不能为空")
    private String type;

    private String linkUrl;
    private Integer sortOrder;
}
