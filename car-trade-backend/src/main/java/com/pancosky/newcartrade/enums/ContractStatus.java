package com.pancosky.newcartrade.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

public enum ContractStatus implements IEnum<Integer> {
    DRAFT(0),
    PENDING_SIGN(1),
    SIGNED(2),
    ARCHIVED(3);

    private final Integer value;

    ContractStatus(Integer value) {
        this.value = value;
    }

    @Override
    @com.baomidou.mybatisplus.annotation.EnumValue
    public Integer getValue() {
        return value;
    }
}
