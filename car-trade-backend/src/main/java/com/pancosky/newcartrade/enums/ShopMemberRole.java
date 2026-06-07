package com.pancosky.newcartrade.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;

public enum ShopMemberRole implements IEnum<Integer> {
    OWNER(0),
    ADMIN(1),
    MEMBER(2);

    @EnumValue
    private final Integer value;

    ShopMemberRole(Integer value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
