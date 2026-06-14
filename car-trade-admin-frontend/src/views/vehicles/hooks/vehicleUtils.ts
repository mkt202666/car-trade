/** 车源展示与车况统计工具函数 */
import type { Vehicle, VehicleStatus } from './types'

/**
 * 将车源状态码映射为详情弹窗展示文案（完整描述）
 * @param status - 车源流转状态枚举值
 * @returns 中文状态文案，如「在售中」「锁车洽谈中」
 */
export function statusDetailLabel(status: VehicleStatus) {
  const map: Record<VehicleStatus, string> = {
    listed: '在售中',
    reserved: '锁车洽谈中',
    pending_review: '交易中',
    sold: '完成出售',
    disapproved: '已下架',
  }
  return map[status]
}

/**
 * 提取卖家姓名首字，用于详情弹窗头像占位
 * @param name - 卖家真实姓名
 * @returns 姓名第一个字符
 */
export function sellerInitial(name: string) {
  return name.charAt(0)
}

/**
 * 将车源状态码映射为列表 Badge 展示文案（简短版）
 * @param status - 车源流转状态枚举值
 * @returns 中文状态文案，如「在售中」「锁车洽谈」
 */
export function statusLabel(status: VehicleStatus) {
  const map: Record<VehicleStatus, string> = {
    listed: '在售中',
    reserved: '锁车洽谈',
    pending_review: '交易中',
    sold: '完成出售',
    disapproved: '下架',
  }
  return map[status]
}

/**
 * 将车源状态映射为 StatusBadge 组件的配色类型
 * @param status - 车源流转状态枚举值
 * @returns Badge 视觉变体：success / primary / warning / neutral / danger
 */
export function vehicleStatus(status: VehicleStatus): 'success' | 'primary' | 'warning' | 'neutral' | 'danger' {
  const map: Record<VehicleStatus, 'success' | 'primary' | 'warning' | 'neutral' | 'danger'> = {
    listed: 'success',
    reserved: 'primary',
    pending_review: 'warning',
    sold: 'neutral',
    disapproved: 'danger',
  }
  return map[status]
}

/**
 * 根据车源数据构建详情弹窗「车况描述」区域的统计网格项
 * @param vehicle - 当前选中的车源，为 null 时返回空数组
 * @returns 包含 label 与 value 的统计项数组，供 detail-stats-grid 渲染
 */
export function computeConditionStats(vehicle: Vehicle | null) {
  if (!vehicle) return []
  const d = vehicle.detail
  const v = vehicle
  return [
    { label: '上牌日期', value: v.registerDate },
    { label: '表显里程', value: d.mileageKm },
    { label: '排放标准', value: d.emissionStandard },
    { label: '排量', value: d.displacement },
    { label: '车身|内饰', value: d.colors },
    { label: '变速箱', value: d.transmission },
    { label: '主机厂', value: d.manufacturer },
    { label: '车辆类型', value: d.vehicleType },
    { label: '新车指导价', value: d.newCarPrice },
    { label: '使用性质', value: d.usageNature },
    { label: '交强险到期', value: d.insuranceExpiry },
    { label: '车辆位置', value: v.region },
  ]
}
