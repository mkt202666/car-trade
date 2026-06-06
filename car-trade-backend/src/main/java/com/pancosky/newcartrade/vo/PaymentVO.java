package com.pancosky.newcartrade.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentVO {
    private String orderId;
    private BigDecimal amount;
    private String paymentUrl;
    private LocalDateTime expiresAt;
}
