package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 客服工单列表VO
 * 描述：客服工单列表卡片，用于"我的客服"工单列表展示。
 */
@Data
public class TicketVO {

    /** 工单ID */
    private Long id;

    /** 工单标题（由用户填写的简要问题描述） */
    private String title;

    /** 工单分类（如 ORDER_DISPUTE 订单纠纷 / PAYMENT 支付问题 / ACCOUNT 账号问题等） */
    private String category;

    /** 工单状态（OPEN 处理中 / REPLIED 客服已回复 / CLOSED 已关闭） */
    private String status;

    /** 工单优先级（LOW 低 / NORMAL 普通 / HIGH 高 / URGENT 紧急） */
    private String priority;

    /** 工单创建时间 */
    private LocalDateTime createdAt;
}
