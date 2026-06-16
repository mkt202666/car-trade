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
import { getDashboardKPI, getDashboardTrend, getDashboardWarnings } from '../../../api/dashboard'
import type { DashboardKPI, DashboardTrend, DashboardWarning } from '../../../api/dashboard'
import { buildChannelChartOption, buildTrendChartOption } from './chartUtils'
import type { DashboardStat, CouponItem, ApprovalQueueItem } from './types'

export type { ApprovalQueueItem, CouponItem, DashboardStat } from './types'
export { buildChannelChartOption, buildTrendChartOption } from './chartUtils'

/**
 * 聚合仪表盘页面所需数据与主题感知图表配置。
 */
export function useDashboard() {
  use([CanvasRenderer, LineChart, PieChart, GridComponent, TooltipComponent, LegendComponent])

  const { theme } = useTheme()

  const kpi = ref<DashboardKPI>({
    totalUsers: 0,
    userGrowth: 0,
    totalCars: 0,
    carGrowth: 0,
    totalOrders: 0,
    orderGrowth: 0,
    totalRevenue: 0,
    revenueGrowth: 0,
    pendingAudits: 0,
    pendingDisputes: 0,
    activeShops: 0,
    todayVisits: 0,
  })

  const warnings = ref<DashboardWarning[]>([])
  const trendData = ref<DashboardTrend | undefined>(undefined)
  const loading = ref(false)

  async function fetchData() {
    loading.value = true
    try {
      const [kpiRes, warningsRes, trendRes] = await Promise.allSettled([
        getDashboardKPI(),
        getDashboardWarnings(),
        getDashboardTrend(7),
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
        trendData.value = trendRes.value as unknown as DashboardTrend
      } else if (trendRes.status === 'rejected') {
        ElMessage.error('获取趋势数据失败')
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
    buildChannelChartOption(theme.value === 'dark'),
  )

  const stats = computed<DashboardStat[]>(() => [
    {
      label: '注册用户',
      value: kpi.value.totalUsers.toLocaleString(),
      featured: true,
      icon: 'user-check',
      iconBg: 'glass',
      trend: '较上周同期',
      trendPrefix: `+${kpi.value.userGrowth}%`,
      trendVariant: 'up-arrow',
    },
    {
      label: '上架车源',
      value: kpi.value.totalCars.toLocaleString(),
      icon: 'car',
      iconBg: 'indigo',
      trend: '较上周同期',
      trendPrefix: `+${kpi.value.carGrowth}%`,
      trendVariant: 'split',
    },
    {
      label: '成交订单',
      value: kpi.value.totalOrders.toLocaleString(),
      icon: 'shield-alert',
      iconBg: 'amber',
      trend: '较上周同期',
      trendPrefix: `+${kpi.value.orderGrowth}%`,
      trendVariant: 'pulse',
    },
    {
      label: '交易流水',
      value: `¥${(kpi.value.totalRevenue / 10000).toFixed(1)}万`,
      icon: 'dollar',
      iconBg: 'rose',
      trend: '较上周同期',
      trendPrefix: `+${kpi.value.revenueGrowth}%`,
      trendVariant: 'emphasis',
    },
  ])

  const coupons = ref<CouponItem[]>([])

  /** 从 KPI 数据派生审批队列：待审核车行 + 待处理纠纷 */
  const approvalQueue = computed<ApprovalQueueItem[]>(() => {
    const items: ApprovalQueueItem[] = []
    const today = new Date().toLocaleDateString()
    if (kpi.value.pendingAudits > 0) {
      items.push({ title: `待审核车行 (${kpi.value.pendingAudits})`, date: today })
    }
    if (kpi.value.pendingDisputes > 0) {
      items.push({ title: `待处理纠纷 (${kpi.value.pendingDisputes})`, date: today })
    }
    // Always show at least placeholder items so the panel is never empty
    if (items.length === 0) {
      items.push(
        { title: '待审核车行', date: today },
        { title: '待处理纠纷', date: today },
      )
    }
    return items
  })

  return {
    stats,
    trendChartOption,
    channelChartOption,
    coupons,
    approvalQueue,
    warnings,
    loading,
  }
}
