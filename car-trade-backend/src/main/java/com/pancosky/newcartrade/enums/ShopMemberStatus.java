package com.pancosky.newcartrade.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;

public enum ShopMemberStatus implements IEnum<Integer> {
    PENDING(0),
    ACTIVE(1),
    REJECTED(2);

    @EnumValue
    private final Integer value;

    ShopMemberStatus(Integer value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
