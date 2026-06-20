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
 * 订单日志实体类
 * 描述：记录订单的每一次状态变更或操作日志，用于订单审计、追溯与争议处理。
 * 关联：通过 orderId 关联 orders 表，通过 operatorId 关联 users 表。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("tc_order_logs")
public class OrderLog {

    /** 主键ID（全局唯一，用于业务表关联） */
    @TableId
    private Long id;

    /** 订单ID（关联 orders.id，表示该日志所属的订单） */
    private String orderId;

    /** 操作类型（如 CREATE / CANCEL / PAY / SHIP / SIGN / COMPLETE / DISPUTE 等，描述本次日志的动作） */
    private String action;

    /** 操作人ID（关联 users.id，执行本次操作的用户/管理员/系统） */
    private Long operatorId;

    /** 操作人名称（冗余字段，直接展示操作人昵称/名称，避免关联查询） */
    private String operatorName;

    /** 备注信息（对本次操作的详细说明，如"买家申请退款，原因：车况不符"） */
    private String remark;

    /** 创建时间（操作发生时间，由系统在插入时自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
