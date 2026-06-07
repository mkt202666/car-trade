package com.pancosky.newcartrade.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;

public enum ChatConversationType implements IEnum<Integer> {
    SINGLE(0),
    ORDER(1),
    CS(2);

    @EnumValue
    private final Integer value;

    ChatConversationType(Integer value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
