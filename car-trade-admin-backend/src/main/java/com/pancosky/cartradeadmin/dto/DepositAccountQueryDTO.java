package com.pancosky.cartradeadmin.dto;

import lombok.Data;

@Data
public class DepositAccountQueryDTO {
    private String keyword;
    private int page = 1;
    private int size = 20;
}
