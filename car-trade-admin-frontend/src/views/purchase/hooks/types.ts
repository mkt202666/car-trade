/** 求购管理模块类型定义 */

/** 求购发布人类型：合作车商或个人用户 */
export type PurchasePublisherType = 'dealer' | 'individual'

/** 求购信息业务状态 */
export type PurchaseStatus = '推广中' | '待处理' | '已成交' | '已关闭'

/** 单条求购意向信息 */
export interface Purchase {
  /** 求购单 ID */
  id: string
  /** 品牌与车系展示文案，如「本田 / 雅阁」 */
  brand: string
  /** 具体年款/配置，可为空 */
  trim: string
  /** 发布人信息 */
  publisher: {
    /** 发布人姓名 */
    name: string
    /** 所属车行名称；个人用户时为「独立用户」等占位 */
    dealer: string
    /** 发布人类型：车商或个人 */
    type: PurchasePublisherType
  }
  /** 年限要求，如「2022年及以后」 */
  yearRequirement: string
  /** 里程要求，如「5万公里以内」 */
  mileageRequirement: string
  /** 颜色要求 */
  colorRequirement: string
  /** 求购价格区间或上限展示文案 */
  price: string
  /** 求购当前业务状态 */
  status: PurchaseStatus
}
