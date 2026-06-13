package com.pancosky.cartradeadmin.dto;

import lombok.Data;

@Data
public class CarQueryDTO {
    private String keyword;
    private Integer brandId;
    private Integer seriesId;
    private String status;
    private String energyType;
    private String city;
    private int page = 1;
    private int size = 20;
}
