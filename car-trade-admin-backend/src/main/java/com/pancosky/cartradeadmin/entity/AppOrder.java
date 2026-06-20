package com.pancosky.cartradeadmin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("tc_orders")
public class AppOrder {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private Long carId;

    private Long buyerId;

    private Long sellerId;

    private BigDecimal totalPrice;

    private BigDecimal depositAmount;

    /** 买家保证金是否已缴纳 */
    private Boolean buyerDepositPaid;

    /** 买家保证金缴纳时间 */
    private LocalDateTime buyerDepositPaidAt;

    /** 卖家保证金是否已缴纳 */
    private Boolean sellerDepositPaid;

    /** 卖家保证金缴纳时间 */
    private LocalDateTime sellerDepositPaidAt;

    /**
     * 订单状态
     * PENDING_DEPOSIT-待付保证金, CONTRACT_DRAFT-合同草拟, CONTRACT_SIGNED-合同已签,
     * IN_TRANSIT-过户中, COMPLETED-已完成, CANCELLED-已取消, TERMINATED-已终止, DISPUTE-纠纷中
     */
    private String status;

    /** 合同编号 */
    private String contractNo;

    /** 合同内容 */
    private String contractContent;

    /** 合同是否由卖家提交 */
    private Boolean contractSubmitted;

    /** 合同提交时间 */
    private LocalDateTime contractSubmittedAt;

    /** 合同是否双方确认签署 */
    private Boolean contractConfirmed;

    /** 合同确认签署时间 */
    private LocalDateTime contractConfirmedAt;

    /** 终止交易申请次数 */
    private Integer terminateCount;

    /** 终止交易限制次数 */
    private Integer terminateLimit;

    /** 终止交易原因 */
    private String terminateReason;

    /** 最后终止时间 */
    private LocalDateTime lastTerminateAt;

    /** 订单备注 */
    private String remark;

    /** 取消原因 */
    private String cancelReason;

    /** 订单完成时间 */
    private LocalDateTime completedAt;

    /** 订单取消时间 */
    private LocalDateTime cancelledAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
