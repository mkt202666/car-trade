/** 仪表盘页面 composable */
import { computed } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart } from 'echarts/charts'
import {
  GridComponent,
  TooltipComponent,
  LegendComponent,
} from 'echarts/components'
import { useTheme } from '../../../composables/useTheme'
import { buildChannelChartOption, buildTrendChartOption } from './chartUtils'
import { APPROVAL_QUEUE, COUPONS, DASHBOARD_STATS } from './constants'

export type { ApprovalQueueItem, CouponItem, DashboardStat } from './types'
export { buildChannelChartOption, buildTrendChartOption } from './chartUtils'
export { APPROVAL_QUEUE, COUPONS, DASHBOARD_STATS } from './constants'

/**
 * 聚合仪表盘页面所需的 mock 数据与主题感知图表配置。
 * 在 index.vue 中解构后分别绑定顶部指标、双图表区与底部列表面板。
 * @returns 统计数据、图表 option 计算属性及列表数据
 */
export function useDashboard() {
  // 注册 ECharts 按需组件，供页面内 VChart 渲染折线图与饼图
  use([CanvasRenderer, LineChart, PieChart, GridComponent, TooltipComponent, LegendComponent])

  const { theme } = useTheme()

  /** 交易趋势折线图配置；theme 为 dark 时切换暗色 tooltip/坐标轴配色，绑定 charts-row 左侧 VChart */
  const trendChartOption = computed(() => buildTrendChartOption(theme.value === 'dark'))

  /** 上架渠道占比饼图配置；随主题切换 tooltip 样式，绑定 charts-row 右侧 VChart */
  const channelChartOption = computed(() => buildChannelChartOption(theme.value === 'dark'))

  return {
    stats: DASHBOARD_STATS,
    trendChartOption,
    channelChartOption,
    coupons: COUPONS,
    approvalQueue: APPROVAL_QUEUE,
  }
}
