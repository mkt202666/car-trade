package com.pancosky.newcartrade.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

public enum MessageType implements IEnum<Integer> {
    SYSTEM(0),
    TRADE(1),
    ACTIVITY(2),
    AUTO_PROMOTION(3),
    CHAT(4),
    TEAM_APPLICATION(5),
    DEPOSIT_WARNING(6);

    private final Integer value;

    MessageType(Integer value) {
        this.value = value;
    }

    @Override
    @com.baomidou.mybatisplus.annotation.EnumValue
    public Integer getValue() {
        return value;
    }
}
