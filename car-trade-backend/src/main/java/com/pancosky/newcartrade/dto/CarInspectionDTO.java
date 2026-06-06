package com.pancosky.newcartrade.dto;

import lombok.Data;

@Data
public class CarInspectionDTO {
    private String overallCondition;
    private String paint;
    private String structure;
    private String engine;
    private String transmission;
    private Integer transferCount;
    private String mileageType;
}
