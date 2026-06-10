package com.pancosky.newcartrade.service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 支付服务接口
 * 当前实现：Mock（直接操作余额）
 * 生产对接：微信支付 / 支付宝 / 银联
 */
public interface PaymentService {

    /**
     * 创建充值订单
     * @param userId 用户ID
     * @param amount 金额
     * @return 支付参数（如支付链接、二维码等）
     */
    Map<String, Object> createRechargeOrder(Long userId, BigDecimal amount);

    /**
     * 创建提现申请
     * @param userId 用户ID
     * @param amount 金额
     * @return 提现申请结果
     */
    Map<String, Object> createWithdrawOrder(Long userId, BigDecimal amount);

    /**
     * 查询支付订单状态
     * @param orderNo 订单号
     * @return 订单状态
     */
    String queryOrderStatus(String orderNo);

    /**
     * 支付回调处理（支付平台通知）
     * @param params 回调参数
     * @return 处理结果
     */
    String handleNotify(Map<String, String> params);
}
