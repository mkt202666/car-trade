/** 仪表盘相关类型定义 */

/** 统计卡片图标标识，对应 DashboardStatCard 内联 SVG */
export type DashboardIcon = 'dollar' | 'car' | 'shield-alert' | 'user-check'

/** 图标容器背景色主题，控制 stat-icon-wrap 的配色类名 */
export type DashboardIconBg = 'glass' | 'indigo' | 'amber' | 'rose'

/** 趋势文案展示样式变体，决定趋势行图标与配色 */
export type DashboardTrendVariant = 'up-arrow' | 'split' | 'pulse' | 'emphasis'

/** 顶部统计指标卡片数据结构，供 stats-grid 区域渲染 */
export interface DashboardStat {
  /** 指标名称，显示在卡片左上角 */
  label: string
  /** 指标主数值，大号等宽字体展示 */
  value: string
  /** 是否为高亮主指标卡片（渐变背景 + 光晕效果） */
  featured?: boolean
  /** 右上角图标类型 */
  icon: DashboardIcon
  /** 图标容器背景色主题 */
  iconBg: DashboardIconBg
  /** 趋势说明文案，显示在数值下方 */
  trend: string
  /** 趋势前缀数字/符号，仅 trendVariant 为 split 时与 trend 分开展示 */
  trendPrefix?: string
  /** 趋势行视觉样式变体 */
  trendVariant: DashboardTrendVariant
}

/** 优惠券列表项，供底部「优惠券效能与发放统计」面板渲染 */
export interface CouponItem {
  /** 优惠券编码，等宽字体高亮展示 */
  code: string
  /** 优惠券名称/用途说明 */
  name: string
  /** 已核销使用次数 */
  used: number
  /** 发放状态文案（如「发放中」「暂停/到期」） */
  status: string
  /** 状态标签类型，映射 el-tag 的 success/info 配色 */
  statusType: 'active' | 'expired'
}

/** 资质审批队列项，供底部「资质注册处理队列」面板渲染 */
export interface ApprovalQueueItem {
  /** 申请主体名称（车行或合伙人） */
  title: string
  /** 提交日期，格式为 M/D/YYYY */
  date: string
}

/** 上架渠道饼图单条数据，直接传入 ECharts pie series */
export interface ChannelChartDatum {
  /** 该渠道车源数量或占比权重 */
  value: number
  /** 渠道名称，用于图例与 tooltip */
  name: string
  /** 扇区颜色配置 */
  itemStyle: { color: string }
}

/** 预警级别映射到 el-alert 组件的 type 属性 */
export type WarningDisplayType = 'success' | 'info' | 'warning' | 'error'
