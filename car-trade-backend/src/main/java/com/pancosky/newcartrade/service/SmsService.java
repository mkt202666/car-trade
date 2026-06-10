package com.pancosky.newcartrade.service;

/**
 * 短信服务接口
 * 当前实现：Mock（打印日志，验证码固定为 123456）
 * 生产对接：阿里云短信 / 腾讯云短信
 */
public interface SmsService {

    /**
     * 发送验证码
     * @param phone 手机号
     * @return 是否发送成功
     */
    boolean sendVerificationCode(String phone);

    /**
     * 验证验证码
     * @param phone 手机号
     * @param code 用户输入的验证码
     * @return 是否验证通过
     */
    boolean verifyCode(String phone, String code);

    /**
     * 发送通知短信
     * @param phone 手机号
     * @param templateCode 模板编码
     * @param params 模板参数
     * @return 是否发送成功
     */
    boolean sendNotification(String phone, String templateCode, String... params);
}
