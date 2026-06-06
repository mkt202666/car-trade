package com.pancosky.newcartrade.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

public enum AuctionStatus implements IEnum<Integer> {
    NONE(0),
    BIDDING(1),
    BIDDED(2);

    private final Integer value;

    AuctionStatus(Integer value) {
        this.value = value;
    }

    @Override
    @com.baomidou.mybatisplus.annotation.EnumValue
    public Integer getValue() {
        return value;
    }
}
