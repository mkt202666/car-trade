package com.pancosky.newcartrade.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pancosky.newcartrade.entity.Auction;
import com.pancosky.newcartrade.mapper.AuctionMapper;
import com.pancosky.newcartrade.service.AuctionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 拍卖定时任务
 * 每分钟检查已到结束时间的拍卖，自动结算
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionSettlementTask {

    private final AuctionMapper auctionMapper;
    private final AuctionService auctionService;

    @Scheduled(fixedRate = 60000) // 每60秒执行一次
    public void settleExpiredAuctions() {
        // 查找状态为 BIDDING 且已过结束时间的拍卖
        LambdaQueryWrapper<Auction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Auction::getStatus, "BIDDING")
               .le(Auction::getEndTime, LocalDateTime.now());
        List<Auction> expired = auctionMapper.selectList(wrapper);

        for (Auction auction : expired) {
            try {
                // 先标记为 ENDED
                auction.setStatus("ENDED");
                auctionMapper.updateById(auction);

                // 执行结算
                auctionService.settleAuction(auction.getId());
                log.info("Auto-settled auction {}", auction.getId());
            } catch (Exception e) {
                log.error("Failed to settle auction {}: {}", auction.getId(), e.getMessage());
            }
        }
    }

    @Scheduled(fixedRate = 60000)
    public void startPendingAuctions() {
        // 查找状态为 PENDING 且已到开始时间的拍卖
        LambdaQueryWrapper<Auction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Auction::getStatus, "PENDING")
               .le(Auction::getStartTime, LocalDateTime.now());
        List<Auction> ready = auctionMapper.selectList(wrapper);

        for (Auction auction : ready) {
            try {
                auction.setStatus("BIDDING");
                auctionMapper.updateById(auction);
                log.info("Auto-started auction {}", auction.getId());
            } catch (Exception e) {
                log.error("Failed to start auction {}: {}", auction.getId(), e.getMessage());
            }
        }
    }
}
