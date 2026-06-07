package com.pancosky.newcartrade.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;

public enum CouponType implements IEnum<Integer> {
    CASH(0),
    DISCOUNT(1);

    @EnumValue
    private final Integer value;

    CouponType(Integer value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
