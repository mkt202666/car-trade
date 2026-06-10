package com.pancosky.newcartrade.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 拍卖查询DTO
 */
@Data
public class AuctionQueryDTO {

    private Integer page = 1;
    private Integer size = 10;
    private String keyword;
    private String status;
    private Long carId;
    private Long sellerId;
    private LocalDateTime startTimeBegin;
    private LocalDateTime startTimeEnd;
    private String sort = "startTime";
    private String order = "DESC";
}