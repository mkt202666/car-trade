package com.pancosky.newcartrade.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;

public enum MemberLevel implements IEnum<Integer> {
    BRONZE(0),
    SILVER(1),
    GOLD(2),
    DIAMOND(3);

    @EnumValue
    private final Integer value;

    MemberLevel(Integer value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
