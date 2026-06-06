package com.pancosky.newcartrade.vo;

import lombok.Data;

@Data
public class UserStatsVO {
    private Integer onSaleCount;
    private Long viewCount;
    private Integer viewCountToday;
    private Long messageCount;
    private Integer messageCountToday;
    private Integer followerCount;
    private Integer followerCountToday;
}
