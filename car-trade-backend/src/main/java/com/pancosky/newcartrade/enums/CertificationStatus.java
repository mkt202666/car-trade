package com.pancosky.newcartrade.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;

public enum CertificationStatus implements IEnum<Integer> {
    UNCERTIFIED(0),
    PENDING(1),
    CERTIFIED(2),
    REJECTED(3);

    @EnumValue
    private final Integer value;

    CertificationStatus(Integer value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
