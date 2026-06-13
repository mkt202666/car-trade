package com.pancosky.cartradeadmin.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CarModelVO {
    private Long id;
    private Long seriesId;
    private String name;
    private Integer year;
    private BigDecimal price;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
