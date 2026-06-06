package com.pancosky.newcartrade.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

public enum OrderStatus implements IEnum<Integer> {
    PENDING_CONFIRM(0),
    TRADING(1),
    DISPUTE(2),
    COMPLETED(3),
    CANCELLED(4);

    private final Integer value;

    OrderStatus(Integer value) {
        this.value = value;
    }

    @Override
    @com.baomidou.mybatisplus.annotation.EnumValue
    public Integer getValue() {
        return value;
    }
}
