/** 出口配置相关类型定义 */

/** 单条机读参数约束规则，用于车辆准入条件匹配 */
export interface Constraint {
  /** 车辆参数字段名，如「左舵/右舵」「环保标准」 */
  field: string
  /** 比较运算符，如 ==、包含、>= 等 */
  operator: string
  /** 比较目标值，如「左舵」「国VI」 */
  value: string
}

/** 出口地区完整配置，供列表展示与编辑弹窗读写 */
export interface ExportRegion {
  /** 唯一标识，新建时以时间戳生成 */
  id: string
  /** ISO 两位国家/地区代码，如 RU、KZ */
  code: string
  /** 地区中文名称，如「俄罗斯」 */
  name: string
  /** 所属分组中文名，如「独联体」「非洲」 */
  group: string
  /** 分组键，用于 Tag 配色与逻辑分组 */
  groupKey: 'cis' | 'africa' | 'asia' | 'europe'
  /** 展示图标，可为 Emoji 国旗、图片 URL 或 Base64 */
  icon: string
  /** 机读参数约束条件列表，组合规则判定车辆是否准入 */
  constraints: Constraint[]
  /** 面向用户的出口要求描述条目，列表有序展示 */
  requirements: string[]
  /** 地区配置启用状态，ACTIVE 在前端展示，INACTIVE 隐藏 */
  status: 'ACTIVE' | 'INACTIVE'
}

/** 地区编辑弹窗表单数据结构，requirementsText 为编号文本便于编辑 */
export interface RegionForm {
  /** 地区中文名称 */
  name: string
  /** ISO 两位地区代码 */
  code: string
  /** 所属分组中文名 */
  group: string
  /** 自定义图标，留空时回退为国旗 Emoji */
  icon: string
  /** 机读约束条件列表，至少保留一条 */
  constraints: Constraint[]
  /** 出口要求编号文本，如「1. 车龄需低于5年」每行一条 */
  requirementsText: string
  /** 表单内状态值，提交时映射为 ExportRegion.status */
  status: 'active' | 'inactive'
}
