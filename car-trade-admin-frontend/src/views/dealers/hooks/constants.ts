/** 车行管理页常量与演示数据 */
import type { FormRules } from 'element-plus'
import type { Dealer, DealerCreateForm, DealerMember } from './types'

/** 省份筛选下拉选项，与种子数据中的驻点省份对应 */
export const provinces = ['天津市', '江苏省', '安徽省', '浙江省']

/** DLR-5001 旗下成员列表，作为种子数据子集复用 */
const dlr5001Members: DealerMember[] = [
  { userId: 'USR-0141779', name: '新成员0141779', phone: '13900001779' },
  { userId: 'USR-9291456', name: '新成员9291456', phone: '13900001456' },
  { userId: 'USR-5407197', name: '新成员5407197', phone: '13900007197' },
  { userId: 'USR-4924204', name: '新成员4924204', phone: '13900004204' },
  { userId: 'USR-3773658', name: '新成员3773658', phone: '13900003658' },
  { userId: 'USR-3006', name: '林建平', phone: '13911112222' },
  { userId: 'USR-3007', name: '赵子强', phone: '13911113333' },
]

/** 车行列表演示种子数据，初始化本地 dealers 列表 */
export const SEED_DEALERS: Dealer[] = [
  {
    id: 'DLR-5001',
    name: '天津5D好车',
    joinedAt: '2024/3/12',
    adminName: '苏宏达',
    phone: '13911112222',
    province: '天津市',
    city: '天津市',
    address: '天津自贸区保税路5号',
    creditCode: '未录入/老车行',
    depositBalance: 80000,
    members: 7,
    membersList: dlr5001Members,
    monthlyOrders: 32,
    totalOrders: 450,
    vehicles: { onSale: 1, offShelf: 15 },
    licenseUrl: 'license-1.jpg',
    credit: { used: '25.0W', total: '100.0W' },
    status: 'active',
  },
  {
    id: 'DLR-5002',
    name: '苏南宏大好车商行',
    joinedAt: '2025/8/20',
    adminName: '苏南宏大好车商行',
    phone: '13799990000',
    province: '江苏省',
    city: '苏州市',
    address: '苏州相城区蠡口段二手车交易中心',
    creditCode: '91320507MA1XXXXXX',
    depositBalance: 150000,
    members: 3,
    membersList: [
      { userId: 'USR-4001', name: '王明', phone: '13799990000' },
      { userId: 'USR-4002', name: '陈芳', phone: '13788881111' },
      { userId: 'USR-4003', name: '周磊', phone: '13777772222' },
    ],
    monthlyOrders: 45,
    totalOrders: 680,
    vehicles: { onSale: 0, offShelf: 20 },
    licenseUrl: 'license-2.jpg',
    credit: { used: '150.0W', total: '200.0W' },
    status: 'active',
  },
  {
    id: 'DLR-5003',
    name: '合肥五达二手车行',
    joinedAt: '2025/10/12',
    adminName: '张建国',
    phone: '13812345678',
    province: '安徽省',
    city: '合肥市',
    address: '合肥瑶海区三十埠汽配城',
    creditCode: '91340100MA2XXXXXX',
    depositBalance: 50000,
    members: 2,
    membersList: [
      { userId: 'USR-5001', name: '张建国', phone: '13812345678' },
      { userId: 'USR-5002', name: '李华', phone: '13823456789' },
    ],
    monthlyOrders: 15,
    totalOrders: 120,
    vehicles: { onSale: 1, offShelf: 8 },
    licenseUrl: 'license-3.jpg',
    credit: { used: '12.0W', total: '50.0W' },
    status: 'active',
  },
  {
    id: 'DLR-5004',
    name: '杭州法拍资产处置点',
    joinedAt: '2026/1/5',
    adminName: '李建波',
    phone: '13555558888',
    province: '浙江省',
    city: '杭州市',
    address: '杭州西湖区法拍及竞价整备园',
    creditCode: '91330100MA3XXXXXX',
    depositBalance: 30000,
    members: 2,
    membersList: [
      { userId: 'USR-6001', name: '李建波', phone: '13555558888' },
      { userId: 'USR-6002', name: '孙伟', phone: '13566669999' },
    ],
    monthlyOrders: 28,
    totalOrders: 230,
    vehicles: { onSale: 0, offShelf: 12 },
    licenseUrl: 'license-4.jpg',
    credit: { used: '0', total: '30.0W' },
    status: 'active',
  },
  {
    id: 'DLR-5005',
    name: '本地个人车主盟会',
    joinedAt: '2026/2/14',
    adminName: '刘强',
    phone: '18633334444',
    province: '全国',
    city: '全国',
    address: '全国个人直售自营服务站',
    creditCode: '未录入/老车行',
    depositBalance: 0,
    members: 1,
    membersList: [{ userId: 'USR-7001', name: '刘强', phone: '18633334444' }],
    monthlyOrders: 350,
    totalOrders: 4500,
    vehicles: { onSale: 0, offShelf: 300 },
    licenseUrl: '',
    credit: null,
    status: 'active',
  },
]

/** 新增车行弹窗 Element Plus 表单校验规则 */
export const createRules: FormRules = {
  name: [{ required: true, message: '请输入车行名称', trigger: 'blur' }],
  adminName: [{ required: true, message: '请输入初始车行管理员', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入联系手机电话', trigger: 'blur' },
    { pattern: /^1\d{10}$/, message: '请输入有效的 11 位手机号', trigger: 'blur' },
  ],
  creditCode: [{ required: true, message: '请输入统一社会信用代码', trigger: 'blur' }],
  licenseUrl: [{ required: true, message: '请输入营业执照附件 URL', trigger: 'blur' }],
}

/**
 * 创建空车行表单初始值。
 * 弹窗关闭时由 resetCreateForm 调用，恢复默认首期授信 50000 元。
 * @returns 各文本字段为空、initialCredit 为 50000 的表单对象
 */
export function createEmptyDealerForm(): DealerCreateForm {
  return {
    name: '',
    adminName: '',
    phone: '',
    idNumber: '',
    idImageUrl: '',
    storeImageUrl: '',
    creditCode: '',
    licenseUrl: '',
    address: '',
    initialCredit: 50000,
  }
}
