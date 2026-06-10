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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("orders")
public class Order {
    @TableId
    private String id;
    private Long carId;
    private Long buyerId;
    private Long sellerId;
    private BigDecimal totalPrice;
    private BigDecimal depositAmount;
    private Boolean buyerDepositPaid;
    private LocalDateTime buyerDepositPaidAt;
    private Boolean sellerDepositPaid;
    private LocalDateTime sellerDepositPaidAt;
    private String status;
    private String contractNo;
    private String contractContent;  // 合同内容
    private Boolean contractSubmitted;  // 合同是否已提交
    private LocalDateTime contractSubmittedAt;  // 合同提交时间
    private Boolean contractConfirmed;  // 合同是否已确认
    private LocalDateTime contractConfirmedAt;  // 合同确认时间
    private Integer terminateCount;  // 终止交易次数
    private Integer terminateLimit;  // 每日终止交易限制
    private String terminateReason;  // 终止交易原因
    private LocalDateTime lastTerminateAt;  // 最后终止交易时间
    private String remark;
    private String cancelReason;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
