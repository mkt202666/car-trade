package com.pancosky.newcartrade.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;

public enum OrderStatus implements IEnum<Integer> {
    PENDING_CONFIRM(0),
    TRADING(1),
    DISPUTE(2),
    COMPLETED(3),
    CANCELLED(4);

    @EnumValue
    private final Integer value;

    OrderStatus(Integer value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
