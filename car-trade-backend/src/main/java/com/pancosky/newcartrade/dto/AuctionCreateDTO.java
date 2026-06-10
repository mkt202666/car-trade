package com.pancosky.newcartrade.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 创建拍卖活动请求 DTO
 * 描述：卖家为某车源创建拍卖活动时使用，包含起止时间、价格设置等参数。
 * 使用场景：POST /api/auctions 接口的请求体。
 */
@Data
public class AuctionCreateDTO {

    /** 关联车源ID（必填，关联 car_sources.id） */
    @NotNull(message = "车源ID不能为空")
    private Long carId;

    /** 起拍价（必填，单位：元；必须大于 0） */
    @NotNull(message = "起拍价不能为空")
    @DecimalMin(value = "0.01", message = "起拍价必须大于0")
    private BigDecimal startPrice;

    /** 保留价/底价（可选，单位：元；若无则不启用保留价机制） */
    @DecimalMin(value = "0.01", message = "保留价必须大于0")
    private BigDecimal reservePrice;

    /** 加价幅度（必填，单位：元；每次出价的最小递增金额） */
    @NotNull(message = "加价幅度不能为空")
    @DecimalMin(value = "0.01", message = "加价幅度必须大于0")
    private BigDecimal bidIncrement;

    /** 拍卖开始时间（必须为未来时间，否则拒绝创建） */
    @NotNull(message = "开始时间不能为空")
    @Future(message = "开始时间必须是未来时间")
    private LocalDateTime startTime;

    /** 拍卖结束时间（必须为未来时间，且晚于 startTime） */
    @NotNull(message = "结束时间不能为空")
    @Future(message = "结束时间必须是未来时间")
    private LocalDateTime endTime;
}
