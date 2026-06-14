<template>

  <div class="dashboard">
    <header class="dashboard-header">
      <div class="header-text">
        <h1 class="page-title">5D Auto 数据全景透视</h1>
        <p class="page-subtitle">业务指标实时对齐与合规操作舱</p>
      </div>
      <el-tag class="sync-badge" effect="plain" round>
        <el-icon class="sync-icon"><Clock /></el-icon>
        <span>最新同步: 2026-06-04 11:33</span>
      </el-tag>
    </header>

    <section class="stats-grid">
      <DashboardStatCard
        v-for="stat in stats"
        :key="stat.label"
        :label="stat.label"
        :value="stat.value"
        :featured="stat.featured"
        :icon="stat.icon"
        :icon-bg="stat.iconBg"
        :trend="stat.trend"
        :trend-prefix="stat.trendPrefix"
        :trend-variant="stat.trendVariant"
      />
    </section>

    <section class="charts-row">
      <el-card class="panel chart-panel" shadow="never">
        <div class="panel-header">
          <h2 class="panel-title">平台每日交易及成交趋势 (CNY)</h2>
          <p class="panel-desc">每日实际锁定定金及最终合同签发金额</p>
        </div>
        <VChart class="chart" :option="trendChartOption" autoresize />
      </el-card>

      <el-card class="panel chart-panel" shadow="never">
        <div class="panel-header">
          <h2 class="panel-title">上架车源获取渠道构成</h2>
          <p class="panel-desc">多维度货源占比与平台转化效能分析</p>
        </div>
        <div class="channel-summary">货源总计 <strong>6 台</strong></div>
        <VChart class="chart chart-pie" :option="channelChartOption" autoresize />
      </el-card>
    </section>

    <section class="bottom-row">
      <el-card class="panel" shadow="never">
        <div class="panel-header">
          <h2 class="panel-title">优惠券效能与发放统计</h2>
          <p class="panel-desc">平台补贴池及车商消耗核销概览</p>
        </div>
        <ul class="coupon-list">
          <li v-for="coupon in coupons" :key="coupon.code" class="coupon-item">
            <div class="coupon-info">
              <span class="coupon-code">{{ coupon.code }}</span>
              <span class="coupon-name">{{ coupon.name }}</span>
            </div>
            <div class="coupon-meta">
              <span class="coupon-used">{{ coupon.used }} 次已用</span>
              <el-tag :type="coupon.statusType === 'active' ? 'success' : 'info'" size="small" round>
                {{ coupon.status }}
              </el-tag>
            </div>
          </li>
        </ul>
      </el-card>

      <el-card class="panel" shadow="never">
        <div class="panel-header">
          <h2 class="panel-title">资质注册处理队列 (最新)</h2>
          <p class="panel-desc">车商与代理合伙人准入审批流</p>
        </div>
        <ul class="queue-list">
          <li v-for="item in approvalQueue" :key="item.title" class="queue-item">
            <div class="queue-info">
              <span class="queue-title">{{ item.title }}</span>
              <span class="queue-date">| {{ item.date }}</span>
            </div>
            <el-button plain size="small">前往处理</el-button>
          </li>
        </ul>
      </el-card>
    </section>

    <el-card class="panel alert-panel" shadow="never">
      <div class="panel-header">
        <h2 class="panel-title">异常交易与资金预警系统</h2>
        <p class="panel-desc">自动拦截高风险、超时未成交订单定金</p>
      </div>
      <el-alert
        type="success"
        :closable="false"
        show-icon
        title="平台当前共有 0 笔处于超期未流转的锁定保证金。资金链路总体健康。"
      />
      <p class="alert-note">
        当任何保证金锁定超过 45 个法定期限，系统将自动发起人工接听核实流转。
      </p>
    </el-card>
  </div>

</template>

<script setup lang="ts">
/**
 * 5D Auto 数据全景透视主页（Dashboard）。
 * 展示平台核心业务指标、交易趋势图、渠道占比图、优惠券统计、
 * 资质审批队列及资金预警信息；数据与图表配置由 useDashboard 提供。
 */
import { Clock } from '@element-plus/icons-vue'
import VChart from 'vue-echarts'
import DashboardStatCard from './components/DashboardStatCard.vue'
import { useDashboard } from './hooks/useDashboard'

const {
  stats,
  trendChartOption,
  channelChartOption,
  coupons,
  approvalQueue,
} = useDashboard()

</script>

<style lang="scss" scoped>

.dashboard {
  padding: 28px 32px 40px;
  max-width: 1400px;
}

.dashboard-header {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  gap: 16px;
  padding: 16px;
  border-radius: 16px;
  border: 1px solid var(--border-color);
  background: rgba(255, 255, 255, 0.4);
  backdrop-filter: blur(12px);
}

[data-theme='dark'] .dashboard-header {
  background: var(--bg-card);
  backdrop-filter: none;
}

@media (min-width: 640px) {
  .dashboard-header {
    flex-direction: row;
    align-items: center;
  }
}

.page-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 4px;
  letter-spacing: -0.025em;
}

.page-subtitle {
  font-size: 12px;
  color: var(--text-secondary);
  margin: 0;
}

.sync-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  font-family: ui-monospace, SFMono-Regular, Menlo, monospace;
  white-space: nowrap;
  align-self: flex-start;

  .el-icon {
    font-size: 14px;
  }
}

@media (min-width: 640px) {
  .sync-badge {
    align-self: auto;
  }
}

@keyframes pulse {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.4;
  }
}

.stats-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 16px;
  margin-bottom: 24px;
}

@media (min-width: 640px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (min-width: 1024px) {
  .stats-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

.charts-row {
  display: grid;
  grid-template-columns: 1.4fr 1fr;
  gap: 16px;
  margin-bottom: 24px;
}

.bottom-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 24px;
}

.panel,
.panel.el-card {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  padding: 20px 24px;
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);
}

.panel.el-card {
  --el-card-padding: 20px 24px;
}

[data-theme='dark'] .panel,
[data-theme='dark'] .panel.el-card {
  box-shadow: none;
}

.panel-header {
  margin-bottom: 16px;
}

.panel-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 4px;
}

.panel-desc {
  font-size: 12px;
  color: var(--text-muted);
  margin: 0;
}

.chart {
  height: 260px;
  width: 100%;
}

.chart-pie {
  height: 220px;
}

.channel-summary {
  font-size: 13px;
  color: var(--text-secondary);
  margin-bottom: 8px;
}

.channel-summary strong {
  color: var(--text-primary);
  font-size: 18px;
}

.coupon-list,
.queue-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.coupon-item,
.queue-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 14px;
  background: var(--bg-elevated);
  border-radius: 8px;
  gap: 12px;
}

.coupon-info,
.queue-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.coupon-code {
  font-size: 12px;
  font-weight: 700;
  color: var(--color-primary);
  font-family: monospace;
}

.coupon-name {
  font-size: 13px;
  color: var(--text-primary);
}

.coupon-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
  flex-shrink: 0;
}

.coupon-used {
  font-size: 12px;
  color: var(--text-muted);
}

.alert-panel {
  margin-bottom: 0;

  .alert-note {
    font-size: 12px;
    color: var(--text-muted);
    margin: 12px 0 0;
  }
}

.queue-title {
  font-size: 13px;
  color: var(--text-primary);
}

.queue-date {
  font-size: 12px;
  color: var(--text-muted);
}

@media (max-width: 1200px) {
  .charts-row,
  .bottom-row {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 749px) {
  .dashboard {
    padding: 16px 14px 32px;
  }

  .dashboard-header {
    flex-direction: column;
    align-items: stretch;
    margin-bottom: 16px;
    gap: 10px;
  }

  .page-title {
    font-size: 18px;
  }

  .page-subtitle {
    font-size: 12px;
  }

  .sync-badge {
    align-self: flex-start;
    font-size: 11px;
    padding: 6px 12px;
  }

  .stats-grid {
    gap: 10px;
    margin-bottom: 16px;
  }

  .panel {
    padding: 16px;
    border-radius: 10px;
  }

  .panel-title {
    font-size: 14px;
  }

  .chart {
    height: 220px;
  }

  .chart-pie {
    height: 200px;
  }

  .coupon-item,
  .queue-item {
    flex-direction: column;
    align-items: flex-start;
  }

  .coupon-meta {
    align-items: flex-start;
  }

  .alert-note {
    padding-left: 0;
  }
}

</style>
