package com.pancosky.newcartrade.service.impl;

import com.pancosky.newcartrade.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 短信服务 - Mock 实现
 * 生产环境替换为真实短信实现时，只需新建 AliyunSmsServiceImpl 并加 @Primary 注解即可
 *
 * Mock 规则：
 * - 验证码固定为 123456
 * - 所有发送操作直接返回成功
 */
@Slf4j
@Service("mockSmsService")
public class MockSmsServiceImpl implements SmsService {

    /** 临时验证码存储（生产环境应使用 Redis，设置 5 分钟过期） */
    private final Map<String, String> codeStore = new ConcurrentHashMap<>();

    @Override
    public boolean sendVerificationCode(String phone) {
        // Mock: 固定验证码 123456
        codeStore.put(phone, "123456");
        log.info("[Mock] SMS verification code sent to {}: 123456", phone);
        return true;
    }

    @Override
    public boolean verifyCode(String phone, String code) {
        String stored = codeStore.get(phone);
        boolean valid = "123456".equals(code) || "123456".equals(stored);
        log.info("[Mock] SMS code verify: phone={}, input={}, result={}", phone, code, valid);
        if (valid) codeStore.remove(phone);
        return valid;
    }

    @Override
    public boolean sendNotification(String phone, String templateCode, String... params) {
        log.info("[Mock] SMS notification sent to {}: template={}, params={}", phone, templateCode, params);
        return true;
    }
}
