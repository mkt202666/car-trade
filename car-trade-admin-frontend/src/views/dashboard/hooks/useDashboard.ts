/** 仪表盘页面 composable */
import { computed, onMounted, ref } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart } from 'echarts/charts'
import {
  GridComponent,
  TooltipComponent,
  LegendComponent,
} from 'echarts/components'
import { ElMessage } from 'element-plus'
import { useTheme } from '../../../composables/useTheme'
import {
  getDashboardKPI,
  getDashboardTrend,
  getDashboardWarnings,
  getDashboardCarDistribution,
  getDashboardCouponStats,
  getDashboardApprovalQueue,
} from '../../../api/dashboard'
import type {
  DashboardKPI,
  DashboardTrend,
  DashboardWarning,
  DashboardCarDist,
  DashboardCouponStats,
  DashboardApproval,
} from '../../../api/dashboard'
import { buildChannelChartOption, buildTrendChartOption } from './chartUtils'
import type { DashboardStat, CouponItem, ApprovalQueueItem, WarningDisplayType } from './types'

export type { ApprovalQueueItem, CouponItem, DashboardStat, WarningDisplayType } from './types'
export { buildChannelChartOption, buildTrendChartOption } from './chartUtils'

/**
 * 将后端预警级别映射到 el-alert 组件的 type 属性。
 * 后端使用 HIGH / MEDIUM / LOW，前端 el-alert 需要 error / warning / info。
 */
function mapWarningLevel(level: string): WarningDisplayType {
  switch (level) {
    case 'HIGH': return 'error'
    case 'MEDIUM': return 'warning'
    case 'LOW': return 'info'
    default: return 'info'
  }
}

/**
 * 将后端 DashboardApproval 映射到前端 ApprovalQueueItem 展示格式。
 */
function mapApprovalItem(item: DashboardApproval): ApprovalQueueItem {
  const dt = item.createdAt ? new Date(item.createdAt) : new Date()
  return {
    title: item.title,
    date: `${dt.getMonth() + 1}/${dt.getDate()}/${dt.getFullYear()}`,
  }
}

/**
 * 聚合仪表盘页面所需数据与主题感知图表配置。
 */
export function useDashboard() {
  use([CanvasRenderer, LineChart, PieChart, GridComponent, TooltipComponent, LegendComponent])

  const { theme } = useTheme()

  const kpi = ref<DashboardKPI>({
    userCount: 0,
    shopCount: 0,
    carCount: 0,
    orderCount: 0,
    tradeAmount: 0,
    pendingReviewCount: 0,
    pendingDisputeCount: 0,
    todayNewUsers: 0,
    todayNewCars: 0,
    todayOrders: 0,
    gmv: 0,
    gmvTrend: '',
    deposit: 0,
    depositActive: 0,
    pendingProcessed: 0,
  })

  const warnings = ref<DashboardWarning[]>([])
  const trendData = ref<DashboardTrend[]>([])
  const carDist = ref<DashboardCarDist[]>([])
  const couponStats = ref<DashboardCouponStats | null>(null)
  const approvalQueue = ref<ApprovalQueueItem[]>([])
  const loading = ref(false)

  async function fetchData() {
    loading.value = true
    try {
      const [kpiRes, warningsRes, trendRes, carDistRes, couponRes, approvalRes] = await Promise.allSettled([
        getDashboardKPI(),
        getDashboardWarnings(),
        getDashboardTrend('WEEK'),
        getDashboardCarDistribution(),
        getDashboardCouponStats(),
        getDashboardApprovalQueue(),
      ])

      // The API interceptor unwraps ApiResponse, returning inner data directly
      if (kpiRes.status === 'fulfilled' && kpiRes.value) {
        kpi.value = kpiRes.value as unknown as DashboardKPI
      } else if (kpiRes.status === 'rejected') {
        ElMessage.error('获取 KPI 数据失败')
      }

      if (warningsRes.status === 'fulfilled' && warningsRes.value) {
        warnings.value = warningsRes.value as unknown as DashboardWarning[]
      } else if (warningsRes.status === 'rejected') {
        ElMessage.error('获取预警数据失败')
      }

      if (trendRes.status === 'fulfilled' && trendRes.value) {
        trendData.value = trendRes.value as unknown as DashboardTrend[]
      } else if (trendRes.status === 'rejected') {
        ElMessage.error('获取趋势数据失败')
      }

      if (carDistRes.status === 'fulfilled' && carDistRes.value) {
        carDist.value = carDistRes.value as unknown as DashboardCarDist[]
      }

      if (couponRes.status === 'fulfilled' && couponRes.value) {
        couponStats.value = couponRes.value as unknown as DashboardCouponStats
      }

      if (approvalRes.status === 'fulfilled' && approvalRes.value) {
        const items = approvalRes.value as unknown as DashboardApproval[]
        approvalQueue.value = items.map(mapApprovalItem)
      }
    } catch (e) {
      console.error('Failed to fetch dashboard data:', e)
      ElMessage.error('仪表盘数据加载异常')
    } finally {
      loading.value = false
    }
  }

  onMounted(() => {
    fetchData()
  })

  const trendChartOption = computed(() =>
    buildTrendChartOption(theme.value === 'dark', trendData.value),
  )
  const channelChartOption = computed(() =>
    buildChannelChartOption(theme.value === 'dark', carDist.value),
  )

  const stats = computed<DashboardStat[]>(() => [
    {
      label: '用户总数',
      value: kpi.value.userCount.toLocaleString(),
      featured: true,
      icon: 'user-check',
      iconBg: 'glass',
      trend: kpi.value.gmvTrend || '今日新增',
      trendPrefix: kpi.value.todayNewUsers > 0 ? `+${kpi.value.todayNewUsers}` : undefined,
      trendVariant: 'up-arrow',
    },
    {
      label: '车源总数',
      value: kpi.value.carCount.toLocaleString(),
      icon: 'car',
      iconBg: 'indigo',
      trend: '今日新增',
      trendPrefix: kpi.value.todayNewCars > 0 ? `+${kpi.value.todayNewCars}` : undefined,
      trendVariant: 'split',
    },
    {
      label: '订单总数',
      value: kpi.value.orderCount.toLocaleString(),
      icon: 'shield-alert',
      iconBg: 'amber',
      trend: '今日新增',
      trendPrefix: kpi.value.todayOrders > 0 ? `+${kpi.value.todayOrders}` : undefined,
      trendVariant: 'pulse',
    },
    {
      label: '交易总额',
      value: `¥${(kpi.value.tradeAmount / 10000).toFixed(1)}万`,
      icon: 'dollar',
      iconBg: 'rose',
      trend: '累计 GMV',
      trendPrefix: undefined,
      trendVariant: 'emphasis',
    },
  ])

  /** 从优惠券统计 API 数据派生展示列表 */
  const coupons = computed<CouponItem[]>(() => {
    if (!couponStats.value) return []
    const cs = couponStats.value
    return [
      {
        code: 'TOTAL',
        name: '发放总量',
        used: cs.totalCount,
        status: '统计',
        statusType: 'active' as const,
      },
      {
        code: 'USED',
        name: '已核销使用',
        used: cs.usedCount,
        status: '已使用',
        statusType: 'active' as const,
      },
      {
        code: 'REMAIN',
        name: '剩余可用',
        used: cs.remainCount,
        status: cs.remainCount > 0 ? '可用' : '已耗尽',
        statusType: (cs.remainCount > 0 ? 'active' : 'expired') as const,
      },
      {
        code: 'RATE',
        name: '核销率',
        used: Math.round(cs.usageRate * 100),
        status: `${(cs.usageRate * 100).toFixed(1)}%`,
        statusType: 'active' as const,
      },
    ]
  })

  return {
    kpi,
    stats,
    trendChartOption,
    channelChartOption,
    coupons,
    approvalQueue,
    warnings,
    loading,
    mapWarningLevel,
  }
}
