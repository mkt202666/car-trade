package com.pancosky.newcartrade.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;

public enum CsTicketStatus implements IEnum<Integer> {
    PENDING(0),
    PROCESSING(1),
    RESOLVED(2),
    CLOSED(3);

    @EnumValue
    private final Integer value;

    CsTicketStatus(Integer value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
