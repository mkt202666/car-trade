package com.pancosky.newcartrade.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;

public enum DepositType implements IEnum<Integer> {
    RECHARGE(0),
    WITHDRAW(1),
    PAY(2),
    REFUND(3),
    FREEZE(4),
    UNFREEZE(5);

    @EnumValue
    private final Integer value;

    DepositType(Integer value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
