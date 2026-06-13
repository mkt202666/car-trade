package com.pancosky.cartradeadmin.dto;

import lombok.Data;

@Data
public class BannerUpdateDTO {
    private String title;
    private String imageUrl;
    private String type;
    private String linkUrl;
    private Integer sortOrder;
    private String status;
}
