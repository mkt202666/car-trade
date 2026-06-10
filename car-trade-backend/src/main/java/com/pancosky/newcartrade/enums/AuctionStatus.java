package com.pancosky.newcartrade.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuctionStatus {
    PENDING(0, "待开始"),
    BIDDING(1, "竞拍中"),
    ENDED(2, "已结束"),
    SETTLED(3, "已结算"),
    CANCELLED(4, "已取消"),
    FAILED(5, "流拍");

    @EnumValue
    private final Integer value;
    private final String description;
}