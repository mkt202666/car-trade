package com.pancosky.newcartrade.dto;

import lombok.Data;

@Data
public class CreateConversationDTO {
    private Long targetUserId;
    private Long sellerId;
    private String orderId;
}
