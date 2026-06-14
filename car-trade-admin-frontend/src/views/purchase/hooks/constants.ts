/** 求购管理模块静态数据 */
import type { Purchase } from './types'

/** 求购列表 mock 种子数据，供 usePurchase 初始化列表 */
export const SEED_PURCHASES: Purchase[] = [
  {
    id: 'PR-001',
    brand: '本田 / 雅阁',
    trim: '2022款 1.5T 幻夜·尊贵版',
    publisher: { name: '张建国', dealer: '天天好车源行', type: 'dealer' },
    yearRequirement: '2022年及以后',
    mileageRequirement: '5万公里以内',
    colorRequirement: '黑色 或 白色',
    price: '12万 - 14万',
    status: '推广中',
  },
  {
    id: 'PR-002',
    brand: '特斯拉 / Model 3 2023 焕新版',
    trim: '',
    publisher: { name: '李思平', dealer: '独立用户', type: 'individual' },
    yearRequirement: '2023年及以后',
    mileageRequirement: '3万公里以内',
    colorRequirement: '灰色',
    price: '20万以内',
    status: '待处理',
  },
]
