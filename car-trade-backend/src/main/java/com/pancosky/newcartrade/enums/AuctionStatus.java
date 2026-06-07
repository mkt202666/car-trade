package com.pancosky.newcartrade.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.baomidou.mybatisplus.annotation.EnumValue;

public enum AuctionStatus implements IEnum<Integer> {
    NONE(0),
    BIDDING(1),
    BIDDED(2);

    @EnumValue
    private final Integer value;

    AuctionStatus(Integer value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
