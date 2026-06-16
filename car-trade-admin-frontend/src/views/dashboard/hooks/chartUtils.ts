/** 仪表盘图表 ECharts 配置构建 */
import {
  CHANNEL_CHART_DATA,
  TREND_CHART_SERIES_DATA,
  TREND_CHART_X_AXIS_DATA,
} from './constants'
import type { DashboardTrend } from '../../../api/dashboard'
import type { ChannelChartDatum } from './types'

/**
 * 将后端 DashboardTrend 数据映射为折线图所需的 X 轴标签与 Y 轴金额序列。
 * 若后端返回数据为空则回退到本地常量。
 */
function mapTrendData(trend?: DashboardTrend) {
  if (trend && trend.dates && trend.dates.length > 0) {
    return {
      xAxis: trend.dates,
      series: trend.revenue ?? trend.dates.map(() => 0),
    }
  }
  return { xAxis: TREND_CHART_X_AXIS_DATA, series: TREND_CHART_SERIES_DATA }
}

/**
 * 构建「平台每日交易及成交趋势」折线图 ECharts option。
 * 根据明暗主题切换线条、坐标轴与 tooltip 配色。
 * @param isDark - 当前是否为暗色主题，来自 useTheme().theme
 * @param trend  - 后端返回的趋势数据（可选，缺失时回退本地常量）
 * @returns 可直接绑定 VChart :option 的完整配置对象
 */
export function buildTrendChartOption(isDark: boolean, trend?: DashboardTrend) {
  const lineColor = isDark ? '#8b7cf6' : '#4c3aed' // 折线及数据点主色
  const axisColor = isDark ? '#334155' : '#f1f5f9' // 坐标轴线与网格分割线色
  const labelColor = '#94a3b8' // 轴标签与 Y 轴名称文字色（明暗主题通用）

  const { xAxis, series } = mapTrendData(trend)

  return {
    tooltip: {
      trigger: 'axis',
      backgroundColor: isDark ? '#1e293b' : '#fff',
      borderColor: isDark ? '#334155' : '#e5e7eb',
      textStyle: { color: isDark ? '#e2e8f0' : '#374151' },
    },
    grid: { left: 48, right: 24, top: 32, bottom: 32 },
    xAxis: {
      type: 'category',
      data: xAxis,
      axisLine: { lineStyle: { color: axisColor } },
      axisLabel: { color: labelColor },
    },
    yAxis: {
      type: 'value',
      name: '交易金额 (CNY)',
      nameTextStyle: { color: labelColor, fontSize: 11 },
      axisLine: { show: false },
      splitLine: { lineStyle: { color: axisColor } },
      axisLabel: { color: labelColor },
    },
    series: [
      {
        name: '交易金额',
        type: 'line',
        smooth: true,
        data: series,
        lineStyle: { color: lineColor, width: isDark ? 2 : 3.5 },
        itemStyle: { color: lineColor },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: isDark ? 'rgba(139, 124, 246, 0.3)' : 'rgba(76, 58, 237, 0.2)' },
              { offset: 1, color: 'rgba(76, 58, 237, 0)' }, // 渐变底部透明，形成面积填充
            ],
          },
        },
      },
    ],
  }
}

/**
 * 构建「上架车源获取渠道构成」环形饼图 ECharts option。
 * 图例垂直居右，扇区标签隐藏由图例承载。
 * @param isDark - 当前是否为暗色主题，控制 tooltip 背景与边框
 * @param channelData - 后端返回的渠道数据（可选，缺失时回退本地常量）
 * @returns 可直接绑定 VChart :option 的完整配置对象
 */
export function buildChannelChartOption(isDark: boolean, channelData?: ChannelChartDatum[]) {
  return {
    tooltip: {
      trigger: 'item',
      backgroundColor: isDark ? '#1e293b' : '#fff',
      borderColor: isDark ? '#334155' : '#e5e7eb',
      textStyle: { color: isDark ? '#e2e8f0' : '#374151' },
      formatter: '{b}: {d}%', // 展示渠道名与百分比
    },
    legend: {
      orient: 'vertical',
      right: 16,
      top: 'center',
      textStyle: { color: '#94a3b8', fontSize: 12 },
    },
    series: [
      {
        type: 'pie',
        radius: ['45%', '70%'], // 内外半径构成环形图
        center: ['35%', '50%'], // 左移为右侧图例留出空间
        avoidLabelOverlap: false,
        label: { show: false },
        data: channelData && channelData.length > 0 ? channelData : CHANNEL_CHART_DATA,
      },
    ],
  }
}
