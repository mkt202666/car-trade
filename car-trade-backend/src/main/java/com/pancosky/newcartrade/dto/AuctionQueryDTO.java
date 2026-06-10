package com.pancosky.newcartrade.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 拍卖活动查询 DTO
 * 描述：用于拍卖列表页的搜索、筛选、排序与分页。
 * 使用场景：GET /api/auctions 接口的查询参数。
 */
@Data
public class AuctionQueryDTO {

    /** 当前页（从 1 开始，默认 1） */
    private Integer page = 1;

    /** 每页条数（默认 10，最大 100） */
    private Integer size = 10;

    /** 关键词（模糊匹配关联车源的标题/品牌/车系） */
    private String keyword;

    /** 拍卖状态（PENDING=待开始；RUNNING=进行中；ENDED=已结束；CANCELLED=已取消） */
    private String status;

    /** 按车源ID筛选（关联 car_sources.id） */
    private Long carId;

    /** 按卖家ID筛选（关联 users.id） */
    private Long sellerId;

    /** 拍卖开始时间区间起始（含） */
    private LocalDateTime startTimeBegin;

    /** 拍卖开始时间区间结束（含） */
    private LocalDateTime startTimeEnd;

    /** 排序字段（默认 startTime） */
    private String sort = "startTime";

    /** 排序方向（ASC=升序；DESC=降序，默认 DESC） */
    private String order = "DESC";
}
