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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("contracts")
public class Contract {
    @TableId
    private Long id;
    private String orderId;
    private String contractNo;
    private String templateId;
    private String title;
    private String content;
    private Long buyerId;
    private Long sellerId;
    private Boolean buyerSigned;
    private LocalDateTime buyerSignedAt;
    private Boolean sellerSigned;
    private LocalDateTime sellerSignedAt;
    private String status;
    private String fileUrl;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
