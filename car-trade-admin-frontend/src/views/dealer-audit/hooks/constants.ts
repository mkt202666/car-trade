/** 车行审核 mock 数据与默认配置 */
import type { AuditItem } from './types'

/** 新车行创建申请的类型标识，用于判断是否展示申请人详情区块 */
export const NEW_DEALER_TYPE = '新车行创建申请'

/** 页面初始选中的审核单 ID，右侧详情面板默认展示该记录 */
export const DEFAULT_SELECTED_AUDIT_ID = 'AUD-4009'

/** 审核列表种子数据，涵盖待审/已通过/已驳回等多种状态样例 */
export const SEED_AUDITS: AuditItem[] = [
  {
    id: 'AUD-4009',
    name: '南京腾达二手名车汇',
    type: NEW_DEALER_TYPE,
    contact: '13611112222',
    certNumber: '91320102MA1XXXXXXP',
    status: 'approved',
    submittedAt: '2026-06-04',
    applicant: {
      name: '周腾达',
      idNumber: '320102198001015678',
      idCardImage: 'https://images.unsplash.com/photo-1633332755192-727a05c4013d?auto=format&fit=crop&q=80&w=600&h=400',
    },
    creditCode: '91320102MA1XXXXXXP',
    address: '南京市玄武区红山路99号二手车市场',
    licenseImage: 'https://images.unsplash.com/photo-1554224155-8d04cb21cd6c?auto=format&fit=crop&q=80&w=600&h=800',
    storefrontImage: 'https://images.unsplash.com/photo-1558231264-77e8ee911dc2?auto=format&fit=crop&q=80&w=800&h=600',
    auditNote: '合规校验完成。统一社会信用代码结构合规。',
    aiCheck: {
      format: '结构合法',
      risk: '低风险',
      recommendation: '推荐通过',
      comment: '合规校验完成。统一社会信用代码结构合规。',
    },
  },
  {
    id: 'AUD-4001',
    name: '成都捷诚二手车交易网',
    type: '区域代理商合伙人',
    contact: '028-85552222',
    certNumber: '91510100MA6XXXX97Y',
    status: 'approved',
    submittedAt: '2026-06-03',
    creditCode: '91510100MA6XXXX97Y',
    address: '成都市武侯区红牌楼大道88号',
    licenseImage: 'https://images.unsplash.com/photo-1554224155-8d04cb21cd6c?auto=format&fit=crop&q=80&w=600&h=800',
    auditNote: '区域代理资质核验通过，合作协议已归档。',
    aiCheck: {
      format: '结构合法',
      risk: '低风险',
      recommendation: '推荐通过',
      comment: '代理合伙人证照格式合规，无异常关联风险。',
    },
  },
  {
    id: 'AUD-4002',
    name: '杭州星驰高端车行',
    type: '自营直销商户',
    contact: '13555558888',
    certNumber: '91330104MA2YYYY11B',
    status: 'approved',
    submittedAt: '2026-05-28',
    creditCode: '91330104MA2YYYY11B',
    address: '杭州市江干区艮山西路168号',
    licenseImage: 'https://images.unsplash.com/photo-1554224155-8d04cb21cd6c?auto=format&fit=crop&q=80&w=600&h=800',
    storefrontImage: 'https://images.unsplash.com/photo-1558231264-77e8ee911dc2?auto=format&fit=crop&q=80&w=800&h=600',
    auditNote: '自营直销资质审核通过。',
    aiCheck: {
      format: '结构合法',
      risk: '低风险',
      recommendation: '推荐通过',
      comment: '营业执照与经营类目匹配，风险可控。',
    },
  },
  {
    id: 'AUD-4003',
    name: '广州越华车务咨询公司',
    type: '认证大客户买家',
    contact: '18922227777',
    certNumber: '91440101MA3ZZZZ45X',
    status: 'pending',
    submittedAt: '2026-06-03',
    creditCode: '91440101MA3ZZZZ45X',
    address: '广州市天河区体育西路103号',
    licenseImage: 'https://images.unsplash.com/photo-1554224155-8d04cb21cd6c?auto=format&fit=crop&q=80&w=600&h=800',
    aiCheck: {
      format: '结构合法',
      risk: '中风险',
      recommendation: '待人工复核',
      comment: '证照格式合规，建议人工核实大客户采购资质。',
    },
  },
  {
    id: 'AUD-4004',
    name: '高启强',
    type: '个人认证卖家',
    contact: '13300001111',
    certNumber: '440103198001011234',
    status: 'rejected',
    submittedAt: '2026-05-20',
    auditNote: '身份证影像模糊，无法完成三要素比对，请重新提交清晰证件照。',
    aiCheck: {
      format: '格式异常',
      risk: '高风险',
      recommendation: '建议驳回',
      comment: '证件影像清晰度不足，三要素校验未通过。',
    },
  },
]
