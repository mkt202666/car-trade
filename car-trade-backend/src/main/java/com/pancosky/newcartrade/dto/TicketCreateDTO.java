package com.pancosky.newcartrade.dto;

import lombok.Data;

/**
 * 创建客服工单请求 DTO
 * 描述：用户在"联系客服"功能中发起工单时的请求体。
 * 使用场景：POST /api/cs/tickets 接口的请求体。
 */
@Data
public class TicketCreateDTO {

    /** 工单标题（简短描述问题） */
    private String title;

    /** 工单分类（ORDER=订单；PAYMENT=支付；CERTIFICATION=实名认证；AUCTION=拍卖；OTHER=其他） */
    private String category;

    /** 详细问题描述（用户填写的正文） */
    private String content;

    /** 优先级（LOW=低；NORMAL=普通；HIGH=高；URGENT=紧急） */
    private String priority;
}
