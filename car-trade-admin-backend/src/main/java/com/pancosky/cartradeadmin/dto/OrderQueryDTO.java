package com.pancosky.cartradeadmin.dto;

import lombok.Data;

@Data
public class OrderQueryDTO {
    private String keyword;
    private String status;
    private String startDate;
    private String endDate;
    private int page = 1;
    private int size = 20;
}
