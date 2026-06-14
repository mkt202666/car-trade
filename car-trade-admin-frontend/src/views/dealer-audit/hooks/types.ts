/** 车行审核相关类型定义 */

/** 审核单据状态枚举，驱动状态徽章、操作按钮与决议文案 */
export type AuditStatus = 'pending' | 'approved' | 'rejected'

/** 新车行创建申请中的自然人申请人信息，仅在 type 为新车行时展示 */
export interface AuditApplicant {
  /** 申请人真实姓名 */
  name: string
  /** 申请人身份证号码 */
  idNumber: string
  /** 身份证正面影像 URL，支持点击预览 */
  idCardImage: string
}

/** AI 智能辅助校验结果，展示在详情面板底部 Gemini 校验区 */
export interface AiCheck {
  /** 申报证照格式合规性结论 */
  format: string
  /** 风险等级归级（如低/中/高风险） */
  risk: string
  /** AI 推荐审核决断 */
  recommendation: string
  /** AI 综合评估意见全文 */
  comment: string
}

/** 单条车行/商户注册审核记录，贯穿列表行与右侧详情面板 */
export interface AuditItem {
  /** 审核单唯一编号，列表首行与详情标题展示 */
  id: string
  /** 申报主体名称（车行名或个人姓名） */
  name: string
  /** 申请资质类型（如新车行创建、区域代理、个人卖家等） */
  type: string
  /** 联系电话或座机 */
  contact: string
  /** 主申报证照号码（营业执照统一社会信用代码或身份证号） */
  certNumber: string
  /** 当前审核状态 */
  status: AuditStatus
  /** 提交日期，ISO 格式 YYYY-MM-DD */
  submittedAt: string
  /** 新车行申请时的自然人申请人信息（可选） */
  applicant?: AuditApplicant
  /** 营业执照统一社会信用代码（可选，与 certNumber 可能相同） */
  creditCode?: string
  /** 经营驻点/门店地址（可选） */
  address?: string
  /** 营业执照附件影像 URL（可选） */
  licenseImage?: string
  /** 车行实体门店照片 URL（可选） */
  storefrontImage?: string
  /** 人工审核备注或驳回原因，决议区下方展示 */
  auditNote?: string
  /** AI 预检结果（可选） */
  aiCheck?: AiCheck
}
