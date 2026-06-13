package com.pancosky.cartradeadmin;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 基础集成测试类
 * 所有集成测试继承此基类，自动使用 test profile
 * 注意：需要Redis服务运行，或使用 @MockBean 替代 RedisTemplate
 */
@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {
    // 共享测试配置和工具方法可在此添加
}
