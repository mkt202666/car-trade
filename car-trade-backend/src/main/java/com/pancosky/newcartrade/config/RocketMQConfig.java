package com.pancosky.newcartrade.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * RocketMQ 条件配置
 * 仅当 rocketmq.name-server 配置存在且非空时才启用 RocketMQ
 * 未配置时应用正常启动，消息功能降级为仅写数据库
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "rocketmq", name = "name-server")
public class RocketMQConfig {

    public RocketMQConfig() {
        log.info("RocketMQ auto-configuration enabled");
    }
}
