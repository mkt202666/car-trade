package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ContractDetailVO {
    private Long id;
    private String contractNo;
    private String orderId;
    private String title;
    private String status;
    private Boolean buyerSigned;
    private Boolean sellerSigned;
    private LocalDateTime createdAt;
    private Long buyerId;
    private String buyerName;
    private Long sellerId;
    private String sellerName;
    private String content;
    private String fileUrl;
}
