package com.pancosky.newcartrade.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;

public enum ContractStatus implements IEnum<Integer> {
    DRAFT(0),
    PENDING_SIGN(1),
    SIGNED(2),
    ARCHIVED(3);

    @EnumValue
    private final Integer value;

    ContractStatus(Integer value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
