package com.pancosky.newcartrade.service.impl;

import com.pancosky.newcartrade.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 支付服务 - Mock 实现
 * 生产环境替换为真实支付实现时，只需新建 WxPayServiceImpl 或 AliPayServiceImpl 并加 @Primary 注解即可
 */
@Slf4j
@Service("mockPaymentService")
public class MockPaymentServiceImpl implements PaymentService {

    @Override
    public Map<String, Object> createRechargeOrder(Long userId, BigDecimal amount) {
        String orderNo = "PAY" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6);
        log.info("[Mock] Created recharge order: user={}, amount={}, orderNo={}", userId, amount, orderNo);

        Map<String, Object> result = new HashMap<>();
        result.put("orderNo", orderNo);
        result.put("amount", amount);
        result.put("status", "SUCCESS"); // Mock 直接成功
        result.put("payUrl", ""); // 真实对接时返回支付链接
        return result;
    }

    @Override
    public Map<String, Object> createWithdrawOrder(Long userId, BigDecimal amount) {
        String orderNo = "WD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6);
        log.info("[Mock] Created withdraw order: user={}, amount={}, orderNo={}", userId, amount, orderNo);

        Map<String, Object> result = new HashMap<>();
        result.put("orderNo", orderNo);
        result.put("amount", amount);
        result.put("status", "SUCCESS"); // Mock 直接成功
        return result;
    }

    @Override
    public String queryOrderStatus(String orderNo) {
        log.info("[Mock] Query order status: {}", orderNo);
        return "SUCCESS"; // Mock 始终返回成功
    }

    @Override
    public String handleNotify(Map<String, String> params) {
        log.info("[Mock] Payment notify received: {}", params);
        return "SUCCESS";
    }
}
