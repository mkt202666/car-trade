/** 仪表盘图表回退常量（当 API 返回为空时使用） */
import type { ChannelChartDatum } from './types'

/** 上架渠道占比饼图回退数据，当后端无数据时展示 */
export const CHANNEL_CHART_DATA: ChannelChartDatum[] = [
  { value: 17, name: '司法拍卖/竞标', itemStyle: { color: '#4c3aed' } },
  { value: 33, name: '车商寄售', itemStyle: { color: '#10b981' } },
  { value: 17, name: '旧车置换', itemStyle: { color: '#f59e0b' } },
  { value: 33, name: '卖家', itemStyle: { color: '#ec4899' } },
]
