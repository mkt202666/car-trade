package com.pancosky.newcartrade.service;

import com.pancosky.newcartrade.entity.BrowsingHistory;
import com.pancosky.newcartrade.mapper.BrowsingHistoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrowsingHistoryService {

    private final BrowsingHistoryMapper browsingHistoryMapper;

    @Async
    public void record(Long userId, Long carId) {
        BrowsingHistory history = new BrowsingHistory();
        history.setUserId(userId);
        history.setCarId(carId);
        browsingHistoryMapper.insert(history);
    }

    public long count(Long userId) {
        return 0;
    }

    public void clear(Long userId) {
    }
}
