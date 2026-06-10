package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统通知VO
 * 描述：系统消息/活动通知/订单变更等消息列表项，用于消息中心展示。
 */
@Data
public class MessageVO {

    /** 消息ID */
    private Long id;

    /** 消息类型（SYSTEM 系统消息 / ORDER 订单消息 / AUCTION 拍卖消息 / PROMO 活动消息） */
    private String type;

    /** 消息标题 */
    private String title;

    /** 消息内容 */
    private String content;

    /** 是否已读（true 表示已读） */
    private Boolean isRead;

    /** 发送者ID（0 表示系统发送） */
    private Long senderId;

    /** 关联业务对象ID（如订单ID、拍卖ID） */
    private String relatedId;

    /** 关联业务类型（配合 relatedId 使用，如 ORDER / AUCTION / USER） */
    private String relatedType;

    /** 消息创建时间 */
    private LocalDateTime createdAt;
}
