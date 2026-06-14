package com.pancosky.cartradeadmin.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis 配置类
 * 注意：当 Redis 不可用时，相关功能会自动降级，不影响应用启动和运行
 */
@Slf4j
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

    @Value("${spring.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.redis.port:6379}")
    private int redisPort;

    @Value("${spring.redis.password:}")
    private String redisPassword;

    @Value("${spring.redis.timeout:2000ms}")
    private Duration redisTimeout;

    /**
     * 创建支持 Java 8 时间类型的 ObjectMapper
     */
    private ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL);
        return objectMapper;
    }

    /**
     * 创建支持 Java 8 时间类型的 Redis JSON 序列化器
     */
    @Bean
    public GenericJackson2JsonRedisSerializer redisJsonSerializer() {
        return new GenericJackson2JsonRedisSerializer(createObjectMapper());
    }

    /**
     * 自定义 Lettuce 连接工厂，配置超时和容错
     */
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        log.info("[Redis] Configuring connection to {}: {}", redisHost, redisPort);
        
        // Standalone configuration
        RedisStandaloneConfiguration standaloneConfig = new RedisStandaloneConfiguration();
        standaloneConfig.setHostName(redisHost);
        standaloneConfig.setPort(redisPort);
        if (redisPassword != null && !redisPassword.isEmpty()) {
            standaloneConfig.setPassword(redisPassword);
        }
        
        // Client options with timeout and retry settings
        SocketOptions socketOptions = SocketOptions.builder()
                .connectTimeout(redisTimeout)
                .build();
        
        ClientOptions clientOptions = ClientOptions.builder()
                .socketOptions(socketOptions)
                .autoReconnect(true)
                .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                .build();
        
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .clientOptions(clientOptions)
                .commandTimeout(redisTimeout)
                .build();
        
        LettuceConnectionFactory factory = new LettuceConnectionFactory(standaloneConfig, clientConfig);
        factory.setValidateConnection(false); // 不验证连接，避免启动时阻塞
        return factory;
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());

        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // 使用支持 Java 8 时间类型的序列化器
        template.setValueSerializer(redisJsonSerializer());
        template.setHashValueSerializer(redisJsonSerializer());

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public CacheManager cacheManager() {
        // 默认缓存配置 - 使用支持 Java 8 时间类型的序列化器
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(DEFAULT_TTL))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisJsonSerializer()))
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

        return RedisCacheManager.builder(redisConnectionFactory())
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigs)
                .build();
    }
}
