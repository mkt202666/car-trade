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
 * 电子合同实体类
 * 描述：订单对应的买卖双方车辆转让合同，记录合同内容、签署状态、电子文件等。
 * 关联：外键 orderId → orders；buyerId/sellerId → users。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("contracts")
public class Contract {

    /** 合同ID（主键） */
    @TableId
    private Long id;

    /** 关联订单号（关联 orders.id，一个订单对应一份主合同） */
    private String orderId;

    /** 合同编号（对外展示的业务编号，如 HT2025-000001） */
    private String contractNo;

    /** 合同模板ID（来自合同模板库，便于复用条款） */
    private String templateId;

    /** 合同标题（如"二手车买卖协议 - 宝马5系 2023款"） */
    private String title;

    /** 合同正文（富文本/HTML/Markdown，包含完整条款） */
    private String content;

    /** 买家用户ID（关联 users.id） */
    private Long buyerId;

    /** 卖家用户ID（关联 users.id） */
    private Long sellerId;

    /** 买家是否已签署（true=买家已完成电子签署） */
    private Boolean buyerSigned;

    /** 买家签署时间 */
    private LocalDateTime buyerSignedAt;

    /** 卖家是否已签署（true=卖家已完成电子签署） */
    private Boolean sellerSigned;

    /** 卖家签署时间 */
    private LocalDateTime sellerSignedAt;

    /** 买家手写签名图片URL */
    private String buyerSignatureUrl;

    /** 卖家手写签名图片URL */
    private String sellerSignatureUrl;

    /** 合同状态（DRAFT=草稿；PENDING_SIGN=待签署；SIGNED=已签署；
     *  VOID=已作废；ARCHIVED=归档） */
    private String status;

    /** 合同PDF/图片文件URL（可下载/预览的合同文件） */
    private String fileUrl;

    /** 记录创建时间（由 MyBatis-Plus 自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 记录最后更新时间（由 MyBatis-Plus 自动填充） */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
