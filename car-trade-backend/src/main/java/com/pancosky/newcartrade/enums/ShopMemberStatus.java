package com.pancosky.newcartrade.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

public enum ShopMemberStatus implements IEnum<Integer> {
    PENDING(0),
    ACTIVE(1),
    REJECTED(2);

    private final Integer value;

    ShopMemberStatus(Integer value) {
        this.value = value;
    }

    @Override
    @com.baomidou.mybatisplus.annotation.EnumValue
    public Integer getValue() {
        return value;
    }
}
