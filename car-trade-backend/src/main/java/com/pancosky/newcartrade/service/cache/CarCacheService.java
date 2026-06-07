package com.pancosky.newcartrade.service.cache;

import com.pancosky.newcartrade.vo.CarDetailVO;
import com.pancosky.newcartrade.vo.CarVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 车辆缓存服务 —— 当前为占位实现。
 * 生产环境可替换为 Redis 集群实现。
 */
@Slf4j
@Service
public class CarCacheService {

    public CarDetailVO getDetail(Long id) {
        return null;
    }

    public void setDetail(Long id, CarDetailVO vo) {
        // no-op
    }

    public List<CarVO> getList(String key) {
        return null;
    }

    public void setList(String key, List<CarVO> list) {
        // no-op
    }

    public void clearDetailCache(Long id) {
        log.debug("Clear detail cache for car: {}", id);
    }

    public void clearListCache() {
        log.debug("Clear list cache");
    }
}
