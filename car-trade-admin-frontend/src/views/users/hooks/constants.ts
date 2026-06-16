/** 用户管理页常量与演示数据 */
import type { FormRules } from 'element-plus'
import type { User, UserProfile, UserRegisterForm } from './types'

/** 档案展开区证件图片字段配置，驱动 v-for 渲染上传/预览区 */
export const imageFields: { key: keyof UserProfile; label: string }[] = [
  { key: 'businessLicenseUrl', label: '营业执照' },
  { key: 'idFrontUrl', label: '身份证正面' },
  { key: 'idBackUrl', label: '身份证反面' },
]

/**
 * @deprecated 用户列表已从后端 API 加载，此种子数据仅保留作回退参考。
 * 用户列表演示种子数据，初始化本地 users 列表
 */
export const SEED_USERS: User[] = [
  {
    id: 'USR-3001',
    name: '张建国',
    registerAt: '2025/10/12',
    category: '车行用户',
    phone: '13800138000',
    wechat: 'zhangjg888',
    listing: { onSale: 8, offShelf: 3, wanted: 2 },
    dealership: { name: '南京腾达二手名车汇', vehicles: '5D 合作车辆' },
    deposit: { available: '5.0W', total: '5.0W' },
    transaction: { count: 12, total: '¥245w' },
    credit: { used: '12.0W', total: '50.0W' },
    profile: {
      dealershipName: '南京腾达二手名车汇有限公司',
      creditCode: '91320100MA1XXXXXX',
      provinceCity: '江苏省 南京市',
      idNumber: '320102198501011234',
      businessLicenseUrl: '',
      idFrontUrl: '',
      idBackUrl: '',
    },
  },
  {
    id: 'USR-3002',
    name: '李思琪',
    registerAt: '2025/11/05',
    category: '个人用户',
    phone: '13900139000',
    wechat: 'lsq_car',
    listing: { onSale: 0, offShelf: 1, wanted: 5 },
    dealership: { name: '—', vehicles: '—' },
    deposit: { available: '1.0W', total: '1.0W' },
    transaction: { count: 3, total: '¥68w' },
    credit: { used: '0', total: '10.0W' },
    profile: {
      dealershipName: '',
      creditCode: '',
      provinceCity: '浙江省 杭州市',
      idNumber: '330106199203151234',
      businessLicenseUrl: '',
      idFrontUrl: '',
      idBackUrl: '',
    },
  },
  {
    id: 'USR-3003',
    name: '王海涛',
    registerAt: '2025/12/20',
    category: 'IT开发',
    phone: '13600136000',
    wechat: 'wht_dev',
    listing: { onSale: 0, offShelf: 0, wanted: 0 },
    dealership: { name: '—', vehicles: '—' },
    deposit: { available: '0', total: '0' },
    transaction: { count: 0, total: '¥0' },
    credit: { used: '0', total: '0' },
    profile: {
      dealershipName: '',
      creditCode: '',
      provinceCity: '北京市',
      idNumber: '',
      businessLicenseUrl: '',
      idFrontUrl: '',
      idBackUrl: '',
    },
  },
  {
    id: 'USR-3004',
    name: '陈晓雯',
    registerAt: '2026/01/08',
    category: '系统管理员',
    phone: '13700137000',
    wechat: 'cxw_admin',
    listing: { onSale: 0, offShelf: 0, wanted: 0 },
    dealership: { name: '—', vehicles: '—' },
    deposit: { available: '0', total: '0' },
    transaction: { count: 0, total: '¥0' },
    credit: { used: '0', total: '0' },
    profile: {
      dealershipName: '',
      creditCode: '',
      provinceCity: '上海市',
      idNumber: '310115198806061234',
      businessLicenseUrl: '',
      idFrontUrl: '',
      idBackUrl: '',
    },
  },
  {
    id: 'USR-2999',
    name: '赵明',
    registerAt: '2024/08/15',
    category: '已注销',
    phone: '13500135000',
    wechat: 'zm_old',
    listing: { onSale: 0, offShelf: 0, wanted: 0 },
    dealership: { name: '—', vehicles: '—' },
    deposit: { available: '0', total: '0' },
    transaction: { count: 5, total: '¥32w' },
    credit: { used: '0', total: '0' },
    profile: {
      dealershipName: '',
      creditCode: '',
      provinceCity: '',
      idNumber: '',
      businessLicenseUrl: '',
      idFrontUrl: '',
      idBackUrl: '',
    },
  },
]

/** 建档弹窗 Element Plus 表单校验规则 */
export const registerRules: FormRules = {
  name: [{ required: true, message: '请输入注册姓名或车商名称', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1\d{10}$/, message: '请输入有效的 11 位手机号', trigger: 'blur' },
  ],
}

/**
 * 创建空注册表单初始值。
 * 弹窗关闭时由 resetRegisterForm 调用，恢复默认角色与零保证金。
 * @returns 各字段为空字符串、角色为「个人用户」、保证金为 0 的表单对象
 */
export function createEmptyRegisterForm(): UserRegisterForm {
  return {
    name: '',
    phone: '',
    wechat: '',
    category: '个人用户',
    deposit: 0,
  }
}
