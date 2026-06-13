package com.pancosky.cartradeadmin.dto;

import lombok.Data;

@Data
public class DepositRecordQueryDTO {
    private String type;
    private String status;
    private Long userId;
    private int page = 1;
    private int size = 20;
}
