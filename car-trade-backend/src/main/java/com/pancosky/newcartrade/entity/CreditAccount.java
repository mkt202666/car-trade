package com.pancosky.newcartrade.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 信用额度账户实体类
 * 描述：平台给信誉良好用户的赊购额度，允许用户先使用部分额度作为保证金/货款。
 * 关联：外键 userId → users。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("credit_accounts")
public class CreditAccount {

    /** 信用账户ID（主键） */
    @TableId
    private Long id;

    /** 用户ID（关联 users.id） */
    private Long userId;

    /** 授信总额度（单位：元，由系统或人工审核设定） */
    private BigDecimal creditLimit;

    /** 已使用额度（当前占用的信用额度，如已下单未结清部分） */
    private BigDecimal usedAmount;

    /** 可用额度（= creditLimit - usedAmount，用于下单校验） */
    private BigDecimal availableAmount;

    /** 账户状态（ACTIVE=可用；SUSPENDED=已停用；OVERDUE=逾期） */
    private String status;

    /** 记录创建时间（由 MyBatis-Plus 自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 记录最后更新时间（由 MyBatis-Plus 自动填充） */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
