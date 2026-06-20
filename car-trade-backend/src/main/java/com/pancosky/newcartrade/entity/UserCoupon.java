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
 * 用户领取优惠券记录实体类
 * 描述：用户与优惠券的多对多关系，记录领取时间、使用时间、关联订单等状态。
 * 关联：外键 userId → users；couponId → coupons；usedOrderId → orders（使用时关联）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("tc_user_coupons")
public class UserCoupon {

    /** 记录ID（主键） */
    @TableId
    private Long id;

    /** 用户ID（关联 users.id） */
    private Long userId;

    /** 优惠券ID（关联 coupons.id） */
    private Long couponId;

    /** 使用时间（若已使用则标记使用的时间点） */
    private LocalDateTime usedAt;

    /** 使用时的订单号（关联 orders.id，便于回溯） */
    private String orderId;

    /** 状态（UNUSED=未使用；USED=已使用；EXPIRED=已过期；LOCKED=锁定中） */
    private String status;

    /** 记录创建时间（由 MyBatis-Plus 自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 领取时间（与 createdAt 一致，便于业务字段单独处理） */
    private LocalDateTime receivedAt;
}
