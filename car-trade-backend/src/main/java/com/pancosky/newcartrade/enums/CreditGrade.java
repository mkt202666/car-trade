package com.pancosky.newcartrade.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

public enum CreditGrade implements IEnum<Integer> {
    S(5),
    A(4),
    B(3),
    C(2),
    D(1);

    private final Integer value;

    CreditGrade(Integer value) {
        this.value = value;
    }

    @Override
    @com.baomidou.mybatisplus.annotation.EnumValue
    public Integer getValue() {
        return value;
    }
}
