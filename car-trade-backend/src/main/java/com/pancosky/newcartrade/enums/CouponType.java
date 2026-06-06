package com.pancosky.newcartrade.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

public enum CouponType implements IEnum<Integer> {
    CASH(0),
    DISCOUNT(1);

    private final Integer value;

    CouponType(Integer value) {
        this.value = value;
    }

    @Override
    @com.baomidou.mybatisplus.annotation.EnumValue
    public Integer getValue() {
        return value;
    }
}
