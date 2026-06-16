/** 资源管理 Tab、表单规则与配置常量 */

import type { FormRules } from 'element-plus'
import type { TextTabKey } from './types'

/** 协议类文本 Tab 配置列表，name 对应 TextTabKey，label 为页面展示标题 */
export const TEXT_TABS: { name: TextTabKey; label: string }[] = [
  { name: 'trade-rules', label: '5D找车在线交易规范' },
  { name: 'user-agreement', label: '5D找车用户协议' },
  { name: 'privacy', label: '5D找车隐私条款' },
  { name: 'contract', label: '在线交易标准合同' },
]

/** 前端 TextTabKey 到后端 resource config key 的映射 */
export const CONFIG_KEY_MAP: Record<TextTabKey, string> = {
  'trade-rules': 'trade_rules',
  'user-agreement': 'user_agreement',
  privacy: 'privacy_policy',
  contract: 'contract_template',
}

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
