package com.pancosky.newcartrade.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pancosky.newcartrade.entity.BrowsingHistory;
import com.pancosky.newcartrade.mapper.BrowsingHistoryMapper;
import com.pancosky.newcartrade.vo.BrowsingHistoryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        LambdaQueryWrapper<BrowsingHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BrowsingHistory::getUserId, userId);
        return browsingHistoryMapper.selectCount(wrapper);
    }

    public List<BrowsingHistoryVO> list(Long userId, int page, int size) {
        LambdaQueryWrapper<BrowsingHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BrowsingHistory::getUserId, userId)
               .orderByDesc(BrowsingHistory::getCreatedAt)
               .last("LIMIT " + size + " OFFSET " + (page - 1) * size);
        List<BrowsingHistory> records = browsingHistoryMapper.selectList(wrapper);
        if (records.isEmpty()) return Collections.emptyList();
        return records.stream().map(r -> {
            BrowsingHistoryVO vo = new BrowsingHistoryVO();
            vo.setId(r.getId());
            vo.setCarId(r.getCarId());
            vo.setCreatedAt(r.getCreatedAt());
            return vo;
        }).collect(Collectors.toList());
    }

    public void clear(Long userId) {
        LambdaQueryWrapper<BrowsingHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BrowsingHistory::getUserId, userId);
        browsingHistoryMapper.delete(wrapper);
    }
}
