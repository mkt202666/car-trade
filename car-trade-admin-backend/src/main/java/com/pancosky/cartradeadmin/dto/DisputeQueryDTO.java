package com.pancosky.cartradeadmin.dto;

import lombok.Data;

@Data
public class DisputeQueryDTO {
    private String keyword;
    private String status;
    private int page = 1;
    private int size = 20;
}
