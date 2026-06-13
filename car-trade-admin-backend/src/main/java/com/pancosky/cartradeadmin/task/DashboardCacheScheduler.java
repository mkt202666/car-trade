package com.pancosky.cartradeadmin.task;

import com.pancosky.cartradeadmin.service.DashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Dashboard 缓存定时刷新任务
 * 定期清除缓存，下次请求时自动重建（Cache-Aside 模式）
 */
@Component
@Slf4j
public class DashboardCacheScheduler {

    @Autowired
    private DashboardService dashboardService;

    /**
     * 每 5 分钟刷新 KPI 缓存
     * cron: 每5分钟执行一次
     */
    @Scheduled(fixedRate = 300000, initialDelay = 60000)
    public void refreshKpiCache() {
        try {
            dashboardService.evictAllCache();
            log.debug("[Scheduler] KPI cache refreshed");
        } catch (Exception e) {
            log.warn("[Scheduler] Failed to refresh KPI cache: {}", e.getMessage());
        }
    }

    /**
     * 每 10 分钟刷新趋势数据缓存
     */
    @Scheduled(fixedRate = 600000, initialDelay = 120000)
    public void refreshTrendCache() {
        try {
            dashboardService.evictTrendCache();
            log.debug("[Scheduler] Trend cache refreshed");
        } catch (Exception e) {
            log.warn("[Scheduler] Failed to refresh trend cache: {}", e.getMessage());
        }
    }

    /**
     * 每天凌晨 2 点清除所有 Dashboard 缓存（新一天的数据）
     * cron: 0 0 2 * * ?
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void dailyCacheClear() {
        try {
            dashboardService.evictAllCache();
            dashboardService.evictTrendCache();
            log.info("[Scheduler] Daily cache clear completed");
        } catch (Exception e) {
            log.warn("[Scheduler] Daily cache clear failed: {}", e.getMessage());
        }
    }
}
