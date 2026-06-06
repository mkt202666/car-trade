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
@TableName("messages")
public class Message {
    @TableId
    private Long id;
    private Long userId;
    private Long senderId;
    private String type;
    private String title;
    private String content;
    private Boolean isRead;
    private String relatedId;
    private String relatedType;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
