/** 资源管理 Tab、表单规则与 mock 数据 */

import type { FormRules } from 'element-plus'
import type { AdItem, TextTabKey } from './types'

/** 协议类文本 Tab 配置列表，name 对应 TextTabKey，label 为页面展示标题 */
export const TEXT_TABS: { name: TextTabKey; label: string }[] = [
  { name: 'trade-rules', label: '5D找车在线交易规范' },
  { name: 'user-agreement', label: '5D找车用户协议' },
  { name: 'privacy', label: '5D找车隐私条款' },
  { name: 'contract', label: '在线交易标准合同' },
]

/** Banner 广告位初始 mock 数据，页面加载时填充 banners 列表 */
export const SEED_BANNERS: AdItem[] = [
  {
    id: 'b1',
    title: '5D好车春季大促首发',
    link: '/promotions/spring',
    image: 'https://images.unsplash.com/photo-1549317661-bd32c8ce0db2?auto=format&fit=crop&q=80&w=800',
    active: true,
  },
  {
    id: 'b2',
    title: '诚信商户招募计划开启',
    link: '/merchant-apply',
    image: 'https://images.unsplash.com/photo-1600880292203-757bb62b4baf?auto=format&fit=crop&q=80&w=800',
    active: true,
  },
]

/** 各协议文本 Tab 的初始内容，key 为 TextTabKey，值为空字符串待编辑 */
export const INITIAL_TEXT_CONTENTS: Record<TextTabKey, string> = {
  'trade-rules': '',
  'user-agreement': '',
  privacy: '',
  contract: '',
}

/** Banner 编辑表单校验规则，绑定 bannerFormRef */
export const bannerFormRules: FormRules = {
  title: [{ required: true, message: '请填写活动标题', trigger: 'blur' }],
  link: [{ required: true, message: '请填写跳转路径', trigger: 'blur' }],
  image: [{ required: true, message: '请填写或上传图片', trigger: 'blur' }],
}

/** 弹窗广告编辑表单校验规则，绑定 popupFormRef */
export const popupFormRules: FormRules = {
  title: [{ required: true, message: '请填写弹窗标题', trigger: 'blur' }],
  link: [{ required: true, message: '请填写跳转路径', trigger: 'blur' }],
  image: [{ required: true, message: '请填写或上传图片', trigger: 'blur' }],
}

/**
 * 创建空白广告表单初始值
 * @returns 含默认 active=true 的广告表单对象，用于新建 Banner/弹窗
 */
export function createEmptyAdForm() {
  return { title: '', link: '', image: '', active: true }
}
