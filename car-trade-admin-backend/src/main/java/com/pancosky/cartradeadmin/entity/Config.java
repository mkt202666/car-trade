package com.pancosky.cartradeadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("tc_configs")
public class Config {

    /**
     * Configs use the natural key (VARCHAR) as primary key,
     * not an auto-increment id, so use INPUT strategy.
     */
    @TableId(type = IdType.INPUT)
    private String key;

    private String content;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
