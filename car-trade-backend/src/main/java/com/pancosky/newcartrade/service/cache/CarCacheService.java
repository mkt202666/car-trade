package com.pancosky.newcartrade.service.cache;

import com.pancosky.newcartrade.vo.CarDetailVO;
import com.pancosky.newcartrade.vo.CarVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CarCacheService {

    private final StringRedisTemplate redisTemplate;

    public CarDetailVO getDetail(Long id) {
        return null;
    }

    public void setDetail(Long id, CarDetailVO vo) {
        redisTemplate.opsForValue().set("car:detail:" + id, "", 30, TimeUnit.MINUTES);
    }

    public void clearDetailCache(Long id) {
        redisTemplate.delete("car:detail:" + id);
    }

    public void setList(String key, Object result) {
        redisTemplate.opsForValue().set("car:list:" + key.hashCode(), "", 5, TimeUnit.MINUTES);
    }

    public void clearListCache() {
    }
}
