package com.pancosky.newcartrade.dto;

import lombok.Data;

@Data
public class TicketCreateDTO {
    private String title;
    private String category;
    private String content;
    private String priority;
}
