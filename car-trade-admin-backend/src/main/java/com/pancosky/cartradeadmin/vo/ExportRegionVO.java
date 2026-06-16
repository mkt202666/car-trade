package com.pancosky.cartradeadmin.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExportRegionVO {

    private Long id;

    private String code;

    private String name;

    private String flag;

    private String group;

    private String groupKey;

    private String icon;

    private String constraints;

    private String requirements;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
