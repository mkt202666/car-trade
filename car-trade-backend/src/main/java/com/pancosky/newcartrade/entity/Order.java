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
 * 订单实体类
 * 描述：二手车交易订单，记录买卖双方、车源、价格、保证金缴纳情况、合同签署状态等。
 *       支持一口价/拍卖成交后的订单流程。
 * 关联：外键 carId → car_sources；buyerId/sellerId → users；
 *       关联 contracts（合同）、disputes（纠纷）、order_logs（操作日志）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("tc_orders")
public class Order {

    /** 订单ID（主键，业务订单号，格式如 ORD20250101123456789） */
    @TableId
    private String id;

    /** 关联车源ID（对应 car_sources.id，指向本次交易的车辆） */
    private Long carId;

    /** 买家用户ID（关联 users.id） */
    private Long buyerId;

    /** 卖家用户ID（关联 users.id） */
    private Long sellerId;

    /** 订单总价（单位：元，按车源成交价/一口价记录） */
    private BigDecimal totalPrice;

    /** 保证金金额（买家下单时需缴纳的保证金，单位：元） */
    private BigDecimal depositAmount;

    /** 买家保证金是否已缴纳（true=已冻结/已支付；false=待支付） */
    private Boolean buyerDepositPaid;

    /** 买家保证金缴纳时间 */
    private LocalDateTime buyerDepositPaidAt;

    /** 卖家保证金是否已缴纳（true=卖家已缴纳履约保证金） */
    private Boolean sellerDepositPaid;

    /** 卖家保证金缴纳时间 */
    private LocalDateTime sellerDepositPaidAt;

    /** 订单状态（PENDING_DEPOSIT=待付保证金；CONTRACT_DRAFT=合同草拟；
     *  CONTRACT_SIGNED=合同已签；IN_TRANSIT=车辆过户中；COMPLETED=已完成；
     *  CANCELLED=已取消；TERMINATED=已终止；DISPUTE=纠纷中） */
    private String status;

    /** 合同编号（关联 contracts.contract_no，合同唯一标识符） */
    private String contractNo;

    /** 合同内容（保存合同条款的全文/JSON，便于留痕） */
    private String contractContent;

    /** 合同是否已由卖家提交（标志合同条款已就绪等待买家签署） */
    private Boolean contractSubmitted;

    /** 合同提交时间 */
    private LocalDateTime contractSubmittedAt;

    /** 合同是否已由双方确认签署（true=双方均已电子签署） */
    private Boolean contractConfirmed;

    /** 合同确认签署时间 */
    private LocalDateTime contractConfirmedAt;

    /** 当前订单终止交易申请次数（防滥用机制） */
    private Integer terminateCount;

    /** 每日终止交易限制次数（超过此值后当日不能再发起终止申请） */
    private Integer terminateLimit;

    /** 终止交易原因（记录买家/卖家选择的终止原因及说明） */
    private String terminateReason;

    /** 最后一次终止交易的时间（用于每日限制计数的重置判断） */
    private LocalDateTime lastTerminateAt;

    /** 订单备注（买家或卖家在下单时填写的备注信息） */
    private String remark;

    /** 订单取消原因（记录订单被取消时的描述，便于客服回溯） */
    private String cancelReason;

    /** 订单完成时间（车辆过户完成、款项结算后的时间点） */
    private LocalDateTime completedAt;

    /** 订单取消时间（标记订单被撤销的时间） */
    private LocalDateTime cancelledAt;

    /** 记录创建时间（由 MyBatis-Plus 自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 记录最后更新时间（由 MyBatis-Plus 自动填充） */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
