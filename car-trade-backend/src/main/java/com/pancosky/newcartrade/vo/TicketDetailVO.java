package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 客服工单详情VO
 * 描述：工单详情页展示的完整工单信息，包括工单内容、处理信息、消息记录等。
 */
@Data
public class TicketDetailVO {

    /** 工单ID */
    private Long id;

    /** 工单标题 */
    private String title;

    /** 工单分类 */
    private String category;

    /** 工单状态 */
    private String status;

    /** 工单优先级 */
    private String priority;

    /** 工单创建时间 */
    private LocalDateTime createdAt;

    /** 提交工单的用户ID */
    private Long userId;

    /** 处理工单的客服/管理员ID */
    private Long handlerId;

    /** 最后处理/回复时间 */
    private LocalDateTime handledAt;

    /** 工单消息记录（用户与客服的对话记录文本列表） */
    private List<String> messages;
}
