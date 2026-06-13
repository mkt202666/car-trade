package com.pancosky.cartradeadmin.service;

import com.pancosky.cartradeadmin.entity.Config;
import com.pancosky.cartradeadmin.mapper.ConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统配置缓存服务
 * 缓存 configs 表数据到 Redis，减少数据库查询
 */
@Service
@Slf4j
public class ConfigCacheService {

    @Autowired
    private ConfigMapper configMapper;

    @Cacheable(value = "config", key = "'item:' + #key")
    public String getConfigValue(String key) {
        Config config = configMapper.selectById(key);
        return config != null ? config.getContent() : null;
    }

    @Cacheable(value = "config", key = "'all'")
    public List<Config> getAllConfigs() {
        return configMapper.selectList(null);
    }

    @CacheEvict(value = "config", key = "'item:' + #key")
    public void updateConfig(String key, String content) {
        Config config = configMapper.selectById(key);
        if (config == null) {
            config = new Config();
            config.setKey(key);
            config.setContent(content);
            configMapper.insert(config);
        } else {
            config.setContent(content);
            configMapper.updateById(config);
        }
        evictAllConfigCache();
    }

    @CacheEvict(value = "config", key = "'all'")
    public void evictAllConfigCache() {
        log.info("[ConfigCache] All config cache evicted");
    }

    @Cacheable(value = "config", key = "'kv_list'")
    public List<ConfigKeyValue> getConfigKeyValueList() {
        return configMapper.selectList(null).stream()
                .map(c -> new ConfigKeyValue(c.getKey(), c.getContent()))
                .collect(Collectors.toList());
    }

    public record ConfigKeyValue(String key, String value) {}
}
