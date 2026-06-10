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
 * 客服工单实体类
 * 描述：用户向客服发起的咨询/投诉/申诉工单，记录工单状态、处理人、优先级等信息。
 * 关联：外键 userId → users（发起人）；handlerId → users（客服/管理员）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("customer_service_tickets")
public class CustomerServiceTicket {

    /** 工单ID（主键） */
    @TableId
    private Long id;

    /** 发起人用户ID（关联 users.id） */
    private Long userId;

    /** 工单标题（简短描述问题） */
    private String title;

    /** 工单分类（ORDER=订单问题；PAYMENT=支付问题；CERTIFICATION=实名认证；
     *  AUCTION=拍卖问题；OTHER=其他） */
    private String category;

    /** 工单状态（OPEN=待处理；IN_PROGRESS=处理中；PENDING_USER=待用户回复；
     *  RESOLVED=已解决；CLOSED=已关闭） */
    private String status;

    /** 优先级（LOW=低；NORMAL=普通；HIGH=高；URGENT=紧急） */
    private String priority;

    /** 处理人ID（当前指派的客服/管理员，关联 users.id） */
    private Long handlerId;

    /** 工单处理完成时间（标记为 RESOLVED/CLOSED 时记录） */
    private LocalDateTime handledAt;

    /** 记录创建时间（由 MyBatis-Plus 自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 记录最后更新时间（由 MyBatis-Plus 自动填充） */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
