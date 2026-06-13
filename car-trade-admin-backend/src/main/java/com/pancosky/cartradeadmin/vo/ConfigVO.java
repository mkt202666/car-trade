package com.pancosky.cartradeadmin.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConfigVO {
    private String key;
    private String content;
    private LocalDateTime updatedAt;
}
