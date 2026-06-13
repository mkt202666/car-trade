package com.pancosky.cartradeadmin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pancosky.cartradeadmin.entity.*;
import com.pancosky.cartradeadmin.mapper.*;
import com.pancosky.cartradeadmin.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DashboardService {

    @Autowired
    private AppUserMapper appUserMapper;

    @Autowired
    private AppCarSourceMapper appCarSourceMapper;

    @Autowired
    private AppOrderMapper appOrderMapper;

    @Autowired
    private AppDepositAccountMapper appDepositAccountMapper;

    @Autowired
    private AppDisputeMapper appDisputeMapper;

    @Autowired
    private AppCouponMapper appCouponMapper;

    @Cacheable(value = "dashboard:kpi", key = "'all'")
    public DashboardKpiVO getKpi() {
        DashboardKpiVO kpi = new DashboardKpiVO();

        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();

        // 用户总数（未删除）
        kpi.setUserCount(appUserMapper.selectCount(
                new LambdaQueryWrapper<AppUser>().isNull(AppUser::getDeletedAt)));

        // 店铺数（有店铺名且已认证）
        kpi.setShopCount(appUserMapper.selectCount(
                new LambdaQueryWrapper<AppUser>()
                        .isNotNull(AppUser::getShopName)
                        .eq(AppUser::getCertificationStatus, "CERTIFIED")
                        .isNull(AppUser::getDeletedAt)));

        // 车源数
        kpi.setCarCount(appCarSourceMapper.selectCount(
                new LambdaQueryWrapper<AppCarSource>().isNull(AppCarSource::getDeletedAt)));

        // 本月订单数
        kpi.setOrderCount(appOrderMapper.selectCount(
                new LambdaQueryWrapper<AppOrder>().ge(AppOrder::getCreatedAt, monthStart)));

        // 本月交易额（已完成订单）
        QueryWrapper<AppOrder> tradeWrapper = new QueryWrapper<>();
        tradeWrapper.select("COALESCE(SUM(total_price), 0) as total_price")
                .ge("created_at", monthStart)
                .eq("status", "COMPLETED");
        List<Map<String, Object>> tradeResult = appOrderMapper.selectMaps(tradeWrapper);
        BigDecimal tradeAmount = BigDecimal.ZERO;
        if (tradeResult != null && !tradeResult.isEmpty() && tradeResult.get(0).get("total_price") != null) {
            tradeAmount = new BigDecimal(tradeResult.get(0).get("total_price").toString());
        }
        kpi.setTradeAmount(tradeAmount);

        // 待审核数
        kpi.setPendingReviewCount(appUserMapper.selectCount(
                new LambdaQueryWrapper<AppUser>()
                        .eq(AppUser::getCertificationStatus, "PENDING")
                        .isNull(AppUser::getDeletedAt)));

        // 待处理争议数
        kpi.setPendingDisputeCount(appDisputeMapper.selectCount(
                new LambdaQueryWrapper<AppDispute>()
                        .in(AppDispute::getStatus, "OPEN", "IN_PROGRESS")));

        // 今日新增用户
        kpi.setTodayNewUsers(appUserMapper.selectCount(
                new LambdaQueryWrapper<AppUser>()
                        .ge(AppUser::getCreatedAt, todayStart)
                        .isNull(AppUser::getDeletedAt)));

        // 今日新增车源
        kpi.setTodayNewCars(appCarSourceMapper.selectCount(
                new LambdaQueryWrapper<AppCarSource>()
                        .ge(AppCarSource::getCreatedAt, todayStart)
                        .isNull(AppCarSource::getDeletedAt)));

        // 今日订单数
        kpi.setTodayOrders(appOrderMapper.selectCount(
                new LambdaQueryWrapper<AppOrder>().ge(AppOrder::getCreatedAt, todayStart)));

        // 累计GMV（所有已完成订单交易额）
        QueryWrapper<AppOrder> gmvWrapper = new QueryWrapper<>();
        gmvWrapper.select("COALESCE(SUM(total_price), 0) as total_price")
                .eq("status", "COMPLETED");
        List<Map<String, Object>> gmvResult = appOrderMapper.selectMaps(gmvWrapper);
        BigDecimal gmv = BigDecimal.ZERO;
        if (gmvResult != null && !gmvResult.isEmpty() && gmvResult.get(0).get("total_price") != null) {
            gmv = new BigDecimal(gmvResult.get(0).get("total_price").toString());
        }
        kpi.setGmv(gmv);

        // GMV环比趋势：本月 vs 上月
        LocalDateTime lastMonthStart = monthStart.minusMonths(1);
        LocalDateTime lastMonthEnd = monthStart;
        QueryWrapper<AppOrder> lastMonthWrapper = new QueryWrapper<>();
        lastMonthWrapper.select("COALESCE(SUM(total_price), 0) as total_price")
                .ge("created_at", lastMonthStart)
                .lt("created_at", lastMonthEnd)
                .eq("status", "COMPLETED");
        List<Map<String, Object>> lastMonthResult = appOrderMapper.selectMaps(lastMonthWrapper);
        BigDecimal lastMonthAmount = BigDecimal.ZERO;
        if (lastMonthResult != null && !lastMonthResult.isEmpty() && lastMonthResult.get(0).get("total_price") != null) {
            lastMonthAmount = new BigDecimal(lastMonthResult.get(0).get("total_price").toString());
        }
        if (lastMonthAmount.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal change = tradeAmount.subtract(lastMonthAmount)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(lastMonthAmount, 1, RoundingMode.HALF_UP);
            kpi.setGmvTrend((change.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "") + change + "%");
        } else if (tradeAmount.compareTo(BigDecimal.ZERO) > 0) {
            kpi.setGmvTrend("+新增");
        } else {
            kpi.setGmvTrend("0%");
        }

        // 冻结中保证金总额
        QueryWrapper<AppDepositAccount> depositWrapper = new QueryWrapper<>();
        depositWrapper.select("COALESCE(SUM(frozen_amount), 0) as frozen_amount");
        List<Map<String, Object>> depositResult = appDepositAccountMapper.selectMaps(depositWrapper);
        if (depositResult != null && !depositResult.isEmpty() && depositResult.get(0).get("frozen_amount") != null) {
            kpi.setDeposit(new BigDecimal(depositResult.get(0).get("frozen_amount").toString()).longValue());
        }

        // 活跃保证金账户数
        kpi.setDepositActive(appDepositAccountMapper.selectCount(
                new LambdaQueryWrapper<AppDepositAccount>()
                        .gt(AppDepositAccount::getBalance, BigDecimal.ZERO)));

        // 已处理认证数（CERTIFIED + REJECTED）
        kpi.setPendingProcessed(appUserMapper.selectCount(
                new LambdaQueryWrapper<AppUser>()
                        .in(AppUser::getCertificationStatus, "CERTIFIED", "REJECTED")
                        .isNull(AppUser::getDeletedAt)));

        return kpi;
    }

    @Cacheable(value = "dashboard:trend", key = "#period")
    public List<DashboardTrendVO> getTrend(String period) {
        int days = "WEEK".equals(period) ? 7 : 30;
        String startDate = LocalDate.now().minusDays(days - 1).toString();

        List<Map<String, Object>> orderTrend = appOrderMapper.selectDailyTrend(startDate);
        List<Map<String, Object>> userTrend = appUserMapper.selectDailyNewUsers(startDate);

        // 转为 Map 便于查找
        Map<String, int[]> dataMap = new LinkedHashMap<>();
        for (int i = 0; i < days; i++) {
            String date = LocalDate.now().minusDays(days - 1 - i).toString();
            dataMap.put(date, new int[]{0, 0, 0}); // orderCount, newUsers; tradeAmount handled separately
        }

        Map<String, BigDecimal> tradeAmountMap = new HashMap<>();
        for (Map<String, Object> row : orderTrend) {
            String date = row.get("date").toString();
            int orderCount = ((Number) row.get("order_count")).intValue();
            BigDecimal amount = new BigDecimal(row.get("trade_amount").toString());
            if (dataMap.containsKey(date)) {
                dataMap.get(date)[0] = orderCount;
            }
            tradeAmountMap.put(date, amount);
        }

        for (Map<String, Object> row : userTrend) {
            String date = row.get("date").toString();
            int count = ((Number) row.get("count")).intValue();
            if (dataMap.containsKey(date)) {
                dataMap.get(date)[1] = count;
            }
        }

        List<DashboardTrendVO> result = new ArrayList<>();
        for (Map.Entry<String, int[]> entry : dataMap.entrySet()) {
            DashboardTrendVO vo = new DashboardTrendVO();
            vo.setDate(entry.getKey());
            vo.setOrderCount(entry.getValue()[0]);
            vo.setNewUsers(entry.getValue()[1]);
            vo.setTradeAmount(tradeAmountMap.getOrDefault(entry.getKey(), BigDecimal.ZERO));
            result.add(vo);
        }

        return result;
    }

    @Cacheable(value = "dashboard:distribution", key = "'all'")
    public List<DashboardCarDistVO> getCarDistribution() {
        QueryWrapper<AppCarSource> wrapper = new QueryWrapper<>();
        wrapper.select("status, COUNT(*) as count").groupBy("status").isNull("deleted_at");
        List<Map<String, Object>> results = appCarSourceMapper.selectMaps(wrapper);

        Map<String, String> channelMap = new HashMap<>();
        channelMap.put("ON_SALE", "在售");
        channelMap.put("SOLD", "已售");
        channelMap.put("OFFLINE", "下架");
        channelMap.put("DRAFT", "草稿");

        long total = 0;
        List<DashboardCarDistVO> list = new ArrayList<>();
        for (Map<String, Object> row : results) {
            DashboardCarDistVO vo = new DashboardCarDistVO();
            String status = row.get("status").toString();
            vo.setChannel(channelMap.getOrDefault(status, status));
            long count = ((Number) row.get("count")).longValue();
            vo.setCount(count);
            total += count;
            list.add(vo);
        }

        for (DashboardCarDistVO vo : list) {
            if (total > 0) {
                vo.setPercentage(BigDecimal.valueOf(vo.getCount())
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP));
            } else {
                vo.setPercentage(BigDecimal.ZERO);
            }
        }

        return list;
    }

    @Cacheable(value = "dashboard:coupon", key = "'all'")
    public DashboardCouponVO getCouponStats() {
        QueryWrapper<AppCoupon> wrapper = new QueryWrapper<>();
        wrapper.select("COALESCE(SUM(total_count), 0) as total_count",
                "COALESCE(SUM(total_count - remain_count), 0) as used_count");
        List<Map<String, Object>> results = appCouponMapper.selectMaps(wrapper);

        DashboardCouponVO vo = new DashboardCouponVO();
        if (results != null && !results.isEmpty()) {
            Map<String, Object> row = results.get(0);
            int totalCount = ((Number) row.get("total_count")).intValue();
            int usedCount = ((Number) row.get("used_count")).intValue();
            vo.setTotalCount(totalCount);
            vo.setUsedCount(usedCount);
            vo.setRemainCount(totalCount - usedCount);
            if (totalCount > 0) {
                vo.setUsageRate(BigDecimal.valueOf(usedCount)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(totalCount), 2, RoundingMode.HALF_UP));
            } else {
                vo.setUsageRate(BigDecimal.ZERO);
            }
        } else {
            vo.setTotalCount(0);
            vo.setUsedCount(0);
            vo.setRemainCount(0);
            vo.setUsageRate(BigDecimal.ZERO);
        }

        return vo;
    }

    @Cacheable(value = "dashboard:approval", key = "'all'")
    public List<DashboardApprovalVO> getApprovalQueue() {
        List<DashboardApprovalVO> result = new ArrayList<>();

        // 待认证用户
        List<AppUser> pendingUsers = appUserMapper.selectList(
                new LambdaQueryWrapper<AppUser>()
                        .eq(AppUser::getCertificationStatus, "PENDING")
                        .isNull(AppUser::getDeletedAt)
                        .orderByAsc(AppUser::getCreatedAt)
                        .last("LIMIT 10"));
        for (AppUser user : pendingUsers) {
            DashboardApprovalVO vo = new DashboardApprovalVO();
            vo.setType("SHOP_REVIEW");
            vo.setId(user.getId());
            vo.setTitle(user.getNickname() + " 申请店铺认证");
            vo.setCreatedAt(user.getCreatedAt());
            result.add(vo);
        }

        // 待处理争议
        List<AppDispute> disputes = appDisputeMapper.selectList(
                new LambdaQueryWrapper<AppDispute>()
                        .in(AppDispute::getStatus, "OPEN", "IN_PROGRESS")
                        .orderByAsc(AppDispute::getCreatedAt)
                        .last("LIMIT 5"));
        for (AppDispute dispute : disputes) {
            DashboardApprovalVO vo = new DashboardApprovalVO();
            vo.setType("DISPUTE");
            vo.setId(dispute.getId());
            vo.setTitle("争议 " + dispute.getOrderId() + ": " + dispute.getReason());
            vo.setCreatedAt(dispute.getCreatedAt());
            result.add(vo);
        }

        return result;
    }

    @Cacheable(value = "dashboard:warnings", key = "#status ?: 'all'")
    public List<DashboardWarningVO> getWarnings(String status) {
        List<DashboardWarningVO> result = new ArrayList<>();

        // 冻结用户
        List<AppUser> frozenUsers = appUserMapper.selectList(
                new LambdaQueryWrapper<AppUser>()
                        .eq(AppUser::getStatus, "FROZEN")
                        .isNull(AppUser::getDeletedAt)
                        .orderByDesc(AppUser::getUpdatedAt)
                        .last("LIMIT 5"));
        for (AppUser user : frozenUsers) {
            DashboardWarningVO vo = new DashboardWarningVO();
            vo.setId(user.getId());
            vo.setType("USER_FROZEN");
            vo.setLevel("HIGH");
            vo.setMessage("用户" + user.getNickname() + "账户已冻结");
            vo.setCreatedAt(user.getUpdatedAt());
            result.add(vo);
        }

        // 保证金余额不足
        List<AppDepositAccount> lowDeposit = appDepositAccountMapper.selectList(
                new QueryWrapper<AppDepositAccount>()
                        .apply("balance + frozen_amount < 1000")
                        .orderByAsc("balance + frozen_amount")
                        .last("LIMIT 10"));
        for (AppDepositAccount account : lowDeposit) {
            DashboardWarningVO vo = new DashboardWarningVO();
            vo.setId(account.getId());
            vo.setType("LOW_DEPOSIT");
            vo.setLevel("MEDIUM");
            vo.setMessage("用户" + account.getUserId() + "保证金余额不足");
            vo.setCreatedAt(account.getUpdatedAt());
            result.add(vo);
        }

        return result;
    }

    /**
     * 清除所有 Dashboard 缓存（数据变更时调用）
     */
    @CacheEvict(value = {"dashboard:kpi", "dashboard:distribution", "dashboard:coupon",
            "dashboard:approval", "dashboard:warnings"}, allEntries = true)
    public void evictAllCache() {
        log.info("[DashboardCache] All dashboard cache evicted");
    }

    /**
     * 清除趋势数据缓存
     */
    @CacheEvict(value = "dashboard:trend", allEntries = true)
    public void evictTrendCache() {
        log.info("[DashboardCache] Trend cache evicted");
    }
}
