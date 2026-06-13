package com.pancosky.cartradeadmin.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class PurchaseRequestDetailVO extends PurchaseRequestVO {
    private Integer mileageMax;
    private String color;
    private String cityCode;
    private String description;
}
