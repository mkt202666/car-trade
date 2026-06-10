package com.pancosky.newcartrade.service.cache;

import com.pancosky.newcartrade.vo.CarDetailVO;
import com.pancosky.newcartrade.vo.CarVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String DETAIL_PREFIX = "car:detail:";
    private static final String LIST_PREFIX = "car:list:";
    private static final long DETAIL_TTL_MINUTES = 10;
    private static final long LIST_TTL_MINUTES = 5;

    public CarDetailVO getDetail(Long id) {
        try {
            Object cached = redisTemplate.opsForValue().get(DETAIL_PREFIX + id);
            if (cached instanceof CarDetailVO) {
                log.debug("Cache hit for car detail: {}", id);
                return (CarDetailVO) cached;
            }
        } catch (Exception e) {
            log.warn("Redis get detail failed for car {}: {}", id, e.getMessage());
        }
        return null;
    }

    public void setDetail(Long id, CarDetailVO vo) {
        try {
            redisTemplate.opsForValue().set(DETAIL_PREFIX + id, vo, DETAIL_TTL_MINUTES, TimeUnit.MINUTES);
            log.debug("Cached car detail: {}", id);
        } catch (Exception e) {
            log.warn("Redis set detail failed for car {}: {}", id, e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public List<CarVO> getList(String key) {
        try {
            Object cached = redisTemplate.opsForValue().get(LIST_PREFIX + key);
            if (cached instanceof List) {
                log.debug("Cache hit for car list: {}", key);
                return (List<CarVO>) cached;
            }
        } catch (Exception e) {
            log.warn("Redis get list failed for key {}: {}", key, e.getMessage());
        }
        return null;
    }

    public void setList(String key, List<CarVO> list) {
        try {
            redisTemplate.opsForValue().set(LIST_PREFIX + key, list, LIST_TTL_MINUTES, TimeUnit.MINUTES);
            log.debug("Cached car list: {}", key);
        } catch (Exception e) {
            log.warn("Redis set list failed for key {}: {}", key, e.getMessage());
        }
    }

    public void clearDetailCache(Long id) {
        try {
            redisTemplate.delete(DETAIL_PREFIX + id);
            log.debug("Cleared detail cache for car: {}", id);
        } catch (Exception e) {
            log.warn("Redis clear detail failed for car {}: {}", id, e.getMessage());
        }
    }

    public void clearListCache() {
        try {
            java.util.Set<String> keys = redisTemplate.keys(LIST_PREFIX + "*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.debug("Cleared {} list cache entries", keys.size());
            }
        } catch (Exception e) {
            log.warn("Redis clear list cache failed: {}", e.getMessage());
        }
    }
}
