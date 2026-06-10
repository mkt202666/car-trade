package com.pancosky.newcartrade.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 创建订单请求 DTO
 * 描述：买家对一口价车源发起下单时的请求体。
 * 使用场景：POST /api/orders 接口的请求体。
 */
@Data
public class OrderCreateDTO {

    /** 目标车源ID（必填，关联 car_sources.id） */
    private Long carId;

    /** 保证金金额（单位：元；由前端展示、后端再次校验；与 car_sources.deposit 一致） */
    private BigDecimal depositAmount;

    /** 订单备注（买家填写的附加信息） */
    private String remark;
}
