package com.pancosky.newcartrade.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;

public enum MessageType implements IEnum<Integer> {
    SYSTEM(0),
    TRADE(1),
    ACTIVITY(2),
    AUTO_PROMOTION(3),
    CHAT(4),
    TEAM_APPLICATION(5),
    DEPOSIT_WARNING(6);

    @EnumValue
    private final Integer value;

    MessageType(Integer value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
