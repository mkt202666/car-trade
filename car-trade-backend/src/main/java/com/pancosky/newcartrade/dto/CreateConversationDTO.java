package com.pancosky.newcartrade.dto;

import lombok.Data;

@Data
public class CreateConversationDTO {
    private Long targetUserId;
    private String orderId;
}
