package com.pancosky.newcartrade.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

public enum EnergyType implements IEnum<Integer> {
    GASOLINE(0),
    PURE_ELECTRIC(1),
    HYBRID(2);

    private final Integer value;

    EnergyType(Integer value) {
        this.value = value;
    }

    @Override
    @com.baomidou.mybatisplus.annotation.EnumValue
    public Integer getValue() {
        return value;
    }
}
