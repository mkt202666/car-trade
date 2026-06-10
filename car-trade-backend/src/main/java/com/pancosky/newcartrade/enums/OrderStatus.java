package com.pancosky.newcartrade.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单状态枚举
 */
@Getter
@AllArgsConstructor
public enum OrderStatus implements IEnum<Integer> {

    PENDING_CONFIRM(0, "待确认"),
    CONFIRMED(1, "已确认"),
    DEPOSIT_PAID(2, "已付保证金"),
    CONTRACT_SUBMITTED(3, "已提交合同"),
    CONTRACT_CONFIRMED(4, "已确认合同"),
    IN_TRANSACTION(5, "交易中"),
    DISPUTE(6, "争议中"),
    COMPLETED(7, "已完成"),
    CANCELLED(8, "已取消"),
    TERMINATED(9, "已终止");

    @EnumValue
    private final Integer value;
    private final String description;

    @Override
    public Integer getValue() {
        return value;
    }

    public static OrderStatus fromValue(Integer value) {
        for (OrderStatus status : values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown order status value: " + value);
    }
}
