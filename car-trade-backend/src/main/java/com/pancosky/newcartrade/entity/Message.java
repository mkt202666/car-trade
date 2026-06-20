package com.pancosky.newcartrade.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 系统通知/消息实体类
 * 描述：平台向用户发送的非实时性站内消息（订单状态变化、活动提醒、系统公告等）。
 *       实时聊天请使用 chat_conversations / chat_messages 表。
 * 关联：外键 userId → users（接收人）；senderId → users（发送人，系统消息可为 null）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("tc_messages")
public class Message {

    /** 消息ID（主键，PostgreSQL BIGSERIAL 自增） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 接收人用户ID（消息送达的用户，关联 users.id） */
    private Long userId;

    /** 发送人ID（可为空，表示系统自动发送；否则为客服/管理员ID） */
    private Long senderId;

    /** 消息类型（ORDER=订单动态；AUCTION=拍卖提醒；SYSTEM=系统公告；
     *  ACTIVITY=活动；SECURITY=安全；MARKETING=营销消息） */
    private String type;

    /** 消息标题（短文本，用于消息列表展示） */
    private String title;

    /** 消息正文（可能包含占位符和链接，前端按配置渲染） */
    private String content;

    /** 是否已读标记（true=用户已阅读；false=未读，用于未读角标） */
    private Boolean isRead;

    /** 关联业务ID（如订单号 ORDxxx、拍卖ID、车源ID等，便于跳转详情） */
    private String relatedId;

    /** 关联业务类型（与 relatedId 配对，如 ORDER/CAR/AUCTION） */
    private String relatedType;

    /** 记录创建时间（由 MyBatis-Plus 自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
