package com.pancosky.cartradeadmin.dto;

import lombok.Data;

@Data
public class ShopQueryDTO {
    private String keyword;
    private String status;
    private String province;
    private int page = 1;
    private int size = 20;
}
