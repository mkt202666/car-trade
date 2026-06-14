/** 仪表盘 mock 数据与图表常量 */
import type { ApprovalQueueItem, ChannelChartDatum, CouponItem, DashboardStat } from './types'

/** 顶部四格统计指标 mock 数据，绑定 stats-grid 区域的 DashboardStatCard 列表 */
export const DASHBOARD_STATS: DashboardStat[] = [
  {
    label: '实时履约总 GMV (CNY)',
    value: '￥928,500',
    featured: true,
    icon: 'dollar',
    iconBg: 'glass',
    trend: '较上周复合提升 18.2%',
    trendVariant: 'up-arrow',
  },
  {
    label: '在售/上架车源总数',
    value: '3 台',
    icon: 'car',
    iconBg: 'indigo',
    trendPrefix: '+1',
    trend: '本周新增提报',
    trendVariant: 'split',
  },
  {
    label: '冻结中投标保证金余额',
    value: '￥7,000',
    icon: 'shield-alert',
    iconBg: 'amber',
    trend: '当前 4 个竞拍协议活跃担保',
    trendVariant: 'pulse',
  },
  {
    label: '待审车商/合伙人资质',
    value: '3 件',
    icon: 'user-check',
    iconBg: 'rose',
    trend: '2 个三要素AI校正完毕',
    trendVariant: 'emphasis',
  },
]

/** 交易趋势折线图 X 轴日期标签（近 7 日） */
export const TREND_CHART_X_AXIS_DATA = ['05-28', '05-29', '05-30', '05-31', '06-01', '06-02', '06-03']

/** 交易趋势折线图 Y 轴每日成交金额序列（单位：CNY） */
export const TREND_CHART_SERIES_DATA = [120000, 185000, 95000, 210000, 168000, 245000, 928500]

/** 上架渠道占比饼图数据，绑定左侧 charts-row 渠道构成图 */
export const CHANNEL_CHART_DATA: ChannelChartDatum[] = [
  { value: 17, name: '司法拍卖/竞标', itemStyle: { color: '#4c3aed' } },
  { value: 33, name: '车商寄售', itemStyle: { color: '#10b981' } },
  { value: 17, name: '旧车置换', itemStyle: { color: '#f59e0b' } },
  { value: 33, name: '卖家', itemStyle: { color: '#ec4899' } },
]

/** 优惠券发放与核销 mock 列表，绑定 bottom-row 左侧优惠券面板 */
export const COUPONS: CouponItem[] = [
  { code: 'DEALER666', name: '车商端首充补贴券', used: 48, status: '发放中', statusType: 'active' },
  { code: 'AUTO80', name: '物流长途费8折通用券', used: 112, status: '发放中', statusType: 'active' },
  { code: 'STARFREE', name: '5D星级拍卖免除服务费券', used: 15, status: '发放中', statusType: 'active' },
  { code: 'EXPIRED5D', name: '过时备用券5D', used: 220, status: '暂停/到期', statusType: 'expired' },
]

/** 待处理资质注册队列 mock 列表，绑定 bottom-row 右侧审批队列面板 */
export const APPROVAL_QUEUE: ApprovalQueueItem[] = [
  { title: '南京腾达二手名车汇新车行创建申请', date: '6/4/2026' },
  { title: '成都捷诚二手车交易网区域代理商合伙人', date: '6/3/2026' },
  { title: '杭州星驰高端车行自营直销商户', date: '5/28/2026' },
]
