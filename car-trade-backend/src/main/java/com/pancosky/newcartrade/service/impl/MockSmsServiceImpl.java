package com.pancosky.newcartrade.service.impl;

import com.pancosky.newcartrade.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 短信服务 - Mock 实现
 *
 * 仅在 local / test profile 下激活。生产环境（prod profile）下：
 *   - 必须有一个真实短信实现（AliyunSmsServiceImpl / TencentSmsServiceImpl）
 *   - 如果没有真实实现且 prod 启动 → Spring 容器中无 SmsService bean，应用启动失败
 *
 * Mock 规则：
 * - 验证码固定为 123456
 * - 所有发送操作直接返回成功
 */
@Slf4j
@Service("mockSmsService")
@Profile({"local", "test"})
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
