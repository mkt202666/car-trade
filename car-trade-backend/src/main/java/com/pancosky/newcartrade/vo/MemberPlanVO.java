package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MemberPlanVO {
    private Long id;
    private String name;
    private String level;
    private BigDecimal price;
    private Integer durationDays;
    private String benefits;
}
