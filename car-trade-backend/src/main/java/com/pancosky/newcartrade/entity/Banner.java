package com.pancosky.newcartrade.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * Banner 广告位实体（移动端只读）
 * 描述：首页轮播 Banner 和弹窗广告，由运营端管理，移动端读取展示。
 * 关联：banners 表，运营端 CRUD，移动端仅查询 ACTIVE 状态记录。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("banners")
public class Banner {

    @TableId
    private Long id;

    /** Banner 标题 */
    private String title;

    /** 图片URL */
    @TableField("image_url")
    private String imageUrl;

    /** 类型: BANNER-轮播, POPUP-弹窗 */
    private String type;

    /** 点击跳转链接 */
    @TableField("link_url")
    private String linkUrl;

    /** 排序权重（越小越靠前） */
    @TableField("sort_order")
    private Integer sortOrder;

    /** 状态: ACTIVE-生效, INACTIVE-停用 */
    private String status;

    /** 点击次数 */
    @TableField("click_count")
    private Integer clickCount;

    /** 生效开始时间 */
    @TableField("start_at")
    private LocalDateTime startAt;

    /** 生效结束时间 */
    @TableField("end_at")
    private LocalDateTime endAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /** 软删除 */
    private LocalDateTime deletedAt;
}
