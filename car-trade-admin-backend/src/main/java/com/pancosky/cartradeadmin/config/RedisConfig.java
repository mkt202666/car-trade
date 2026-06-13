package com.pancosky.cartradeadmin.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfig {

    /** 缓存区域 TTL 配置（秒） */
    private static final long DASHBOARD_KPI_TTL = 300;       // 5分钟
    private static final long DASHBOARD_TREND_TTL = 600;      // 10分钟
    private static final long DASHBOARD_DIST_TTL = 900;       // 15分钟
    private static final long DASHBOARD_QUEUE_TTL = 120;      // 2分钟
    private static final long CONFIG_TTL = 1800;              // 30分钟
    private static final long CAR_LIBRARY_TTL = 7200;         // 2小时（车辆字典数据变化低频）
    private static final long DEFAULT_TTL = 600;              // 10分钟

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // Use Jackson for value serialization (also supports LocalDateTime)
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL);

        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // 默认缓存配置
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(DEFAULT_TTL))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();

        // 按缓存名称设置不同 TTL
        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();
        cacheConfigs.put("dashboard:kpi", defaultConfig.entryTtl(Duration.ofSeconds(DASHBOARD_KPI_TTL)));
        cacheConfigs.put("dashboard:trend", defaultConfig.entryTtl(Duration.ofSeconds(DASHBOARD_TREND_TTL)));
        cacheConfigs.put("dashboard:distribution", defaultConfig.entryTtl(Duration.ofSeconds(DASHBOARD_DIST_TTL)));
        cacheConfigs.put("dashboard:coupon", defaultConfig.entryTtl(Duration.ofSeconds(DASHBOARD_DIST_TTL)));
        cacheConfigs.put("dashboard:approval", defaultConfig.entryTtl(Duration.ofSeconds(DASHBOARD_QUEUE_TTL)));
        cacheConfigs.put("dashboard:warnings", defaultConfig.entryTtl(Duration.ofSeconds(DASHBOARD_QUEUE_TTL)));
        cacheConfigs.put("config", defaultConfig.entryTtl(Duration.ofSeconds(CONFIG_TTL)));
        cacheConfigs.put("car-library", defaultConfig.entryTtl(Duration.ofSeconds(CAR_LIBRARY_TTL)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigs)
                .build();
    }
}
