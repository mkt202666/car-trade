package com.pancosky.cartradeadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("banners")
public class Banner {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    @TableField("image_url")
    private String imageUrl;

    private String type;

    @TableField("link_url")
    private String linkUrl;

    @TableField("sort_order")
    private Integer sortOrder;

    private String status;

    @TableField("click_count")
    private Integer clickCount;

    @TableField("start_at")
    private LocalDateTime startAt;

    @TableField("end_at")
    private LocalDateTime endAt;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    @JsonIgnore
    @TableLogic
    @TableField("deleted_at")
    private LocalDateTime deletedAt;
}
