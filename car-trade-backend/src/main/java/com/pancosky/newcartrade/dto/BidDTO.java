package com.pancosky.newcartrade.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 出价DTO
 */
@Data
public class BidDTO {

    @NotNull(message = "拍卖ID不能为空")
    private Long auctionId;

    @NotNull(message = "出价金额不能为空")
    @DecimalMin(value = "0.01", message = "出价金额必须大于0")
    private BigDecimal bidPrice;
}