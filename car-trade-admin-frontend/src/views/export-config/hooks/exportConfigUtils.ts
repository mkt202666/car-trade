/** 出口配置表单与展示层工具函数 */

import type { ExportRegion, RegionForm } from './types'

/**
 * 创建空白地区配置表单
 * @returns 含默认约束行与 active 状态的 RegionForm 对象
 */
export function createEmptyForm(): RegionForm {
  return {
    name: '',
    code: '',
    group: '',
    icon: '',
    constraints: [{ field: '左舵/右舵', operator: '==', value: '' }],
    requirementsText: '',
    status: 'active',
  }
}

/**
 * 根据 ISO 两位国家代码生成 emoji 国旗
 * @param code - 两位字母国家代码，如 RU、KZ
 * @returns 对应国旗 Emoji；无效时返回白旗 🏳️
 */
export function countryFlag(code: string) {
  if (!code || code.length !== 2) return '🏳️'
  return code
    .toUpperCase()
    .replace(/./g, (char) => String.fromCodePoint(127397 + char.charCodeAt(0)))
}

/**
 * 判断图标字符串是否为图片 URL 或 Base64
 * @param icon - 图标字段值
 * @returns 以 http 或 data:image 开头时返回 true
 */
export function isImageIcon(icon: string) {
  return icon.startsWith('http') || icon.startsWith('data:image')
}

/**
 * 获取地区在列表中的展示图标
 * @param row - 出口地区数据行
 * @returns 自定义 icon 或根据 code 生成的国旗 Emoji
 */
export function displayIcon(row: ExportRegion) {
  return row.icon || countryFlag(row.code)
}

/**
 * 地区分组对应的 Element Plus Tag 颜色类型
 * @param groupKey - 分组键
 * @returns Tag type 属性值
 */
export function groupTagType(groupKey: ExportRegion['groupKey']) {
  const map: Record<ExportRegion['groupKey'], 'primary' | 'success' | 'warning' | 'info'> = {
    cis: 'primary',
    africa: 'info',
    asia: 'success',
    europe: 'warning',
  }
  return map[groupKey]
}

/**
 * 将中文分组名映射为 groupKey
 * @param group - 分组中文名，如「独联体」「非洲」
 * @returns 对应 groupKey；未匹配时默认 cis
 */
export function resolveGroupKey(group: string): ExportRegion['groupKey'] {
  const map: Record<string, ExportRegion['groupKey']> = {
    独联体: 'cis',
    非洲: 'africa',
    亚洲: 'asia',
    欧洲: 'europe',
  }
  return map[group.trim()] ?? 'cis'
}

/**
 * 将条件描述数组格式化为编号文本
 * @param requirements - 出口要求条目数组
 * @returns 带行号的换行分隔文本，供 textarea 编辑
 */
export function formatRequirements(requirements: string[]) {
  return requirements.map((item, index) => `${index + 1}. ${item}`).join('\n')
}

/**
 * 从编号文本解析条件描述数组
 * @param text - 带行号的 textarea 内容
 * @returns 去除编号前缀后的要求条目数组
 */
export function parseRequirements(text: string) {
  return text
    .split('\n')
    .map((line) => line.trim())
    .filter(Boolean)
    .map((line) => line.replace(/^\d+\.\s*/, ''))
}
