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
 * 用户会员购买记录实体类
 * 描述：记录用户购买/升级/续费会员套餐的实际情况（套餐、有效期、状态等）。
 * 关联：外键 userId → users；planId → member_plans。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("user_membership")
public class UserMembership {

    /** 记录ID（主键） */
    @TableId
    private Long id;

    /** 用户ID（关联 users.id） */
    private Long userId;

    /** 套餐ID（关联 member_plans.id） */
    private Long planId;

    /** 当前会员等级（冗余字段；BASIC/GOLD/PLATINUM/DIAMOND 等） */
    private String level;

    /** 会员开始时间（本次购买的有效期起点） */
    private LocalDateTime startAt;

    /** 会员到期时间（超出此时间则视为已过期） */
    private LocalDateTime expireAt;

    /** 状态（ACTIVE=生效中；EXPIRED=已过期；CANCELLED=已取消；REFUNDED=已退款） */
    private String status;

    /** 记录创建时间（由 MyBatis-Plus 自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 记录最后更新时间（由 MyBatis-Plus 自动填充） */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /** 结束时间（用于历史归档或退款标记） */
    private LocalDateTime endAt;
}
