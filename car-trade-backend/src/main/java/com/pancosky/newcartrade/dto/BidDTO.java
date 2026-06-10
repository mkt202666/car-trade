package com.pancosky.newcartrade.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 拍卖出价请求 DTO
 * 描述：用户在拍卖活动中提交出价时的请求体，由后端校验金额与规则。
 * 使用场景：POST /api/auctions/{auctionId}/bids 接口的请求体。
 * 注意：业务层会校验出价必须高于当前最高价一个出价幅度、用户保证金冻结状态等。
 */
@Data
public class BidDTO {

    /** 拍卖活动ID（必填，关联 auctions.id） */
    @NotNull(message = "拍卖ID不能为空")
    private Long auctionId;

    /** 出价金额（单位：元；必须大于 0；业务校验时判断是否高于当前最高价 + bidIncrement） */
    @NotNull(message = "出价金额不能为空")
    @DecimalMin(value = "0.01", message = "出价金额必须大于0")
    private BigDecimal bidPrice;
}
