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
 * 交易纠纷/申诉实体类
 * 描述：订单交易过程中产生的纠纷或申诉记录，由平台客服介入处理。
 * 关联：外键 orderId → orders；initiatorId/handlerId → users。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("tc_disputes")
public class Dispute {

    /** 纠纷ID（主键） */
    @TableId
    private Long id;

    /** 关联订单号（关联 orders.id；标记属于哪个订单的纠纷） */
    private String orderId;

    /** 发起人ID（发起纠纷的买家/卖家用户ID；关联 users.id） */
    private Long initiatorId;

    /** 纠纷原因（DEPOSIT=保证金；VEHICLE_CONDITION=车况；CONTRACT=合同条款；PAYMENT=付款；OTHER=其他） */
    private String reason;

    /** 纠纷描述（用户对纠纷问题的文字描述） */
    private String description;

    /** 证据资料URL（逗号分隔的图片/文档等证据链接） */
    private String evidence;

    /** 纠纷状态（OPEN=待处理；IN_PROGRESS=处理中；RESOLVED=已解决；REJECTED=已驳回） */
    private String status;

    /** 处理结果（平台给出的仲裁/解决方案简述） */
    private String result;

    /** 处理人ID（客服/管理员用户ID；关联 users.id） */
    private Long handlerId;

    /** 处理完成时间（标记 status=RESOLVED/REJECTED 的时间点） */
    private LocalDateTime handledAt;

    /** 记录创建时间（由 MyBatis-Plus 自动填充；即纠纷提交时间） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 记录最后更新时间（由 MyBatis-Plus 自动填充） */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
