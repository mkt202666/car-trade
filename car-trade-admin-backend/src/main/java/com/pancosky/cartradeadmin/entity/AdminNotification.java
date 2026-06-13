package com.pancosky.cartradeadmin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("admin_notifications")
public class AdminNotification {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 通知类型 */
    private String type;

    /** 目标用户ID */
    private Long targetUserId;

    /** 通知标题 */
    private String title;

    /** 通知内容 */
    private String content;

    /** 关联实体类型 */
    private String targetType;

    /** 关联实体ID */
    private String targetId;

    /** 附加数据JSON */
    private String extraJson;

    /** 发送状态: SENT, FAILED */
    private String status;

    /** 发送时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
