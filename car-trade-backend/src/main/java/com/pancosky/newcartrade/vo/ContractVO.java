package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ContractVO {
    private Long id;
    private String contractNo;
    private String orderId;
    private String title;
    private String status;
    private Boolean buyerSigned;
    private Boolean sellerSigned;
    private LocalDateTime createdAt;
}
