/** 车行管理相关类型定义 */

/** 车行金融授信额度 */
export interface DealerCredit {
  /** 已用授信，支持 W 后缀万单位 */
  used: string
  /** 授信总额 */
  total: string
}

/** 车行旗下成员/子账号 */
export interface DealerMember {
  /** 关联用户 ID，格式 USR-xxxx */
  userId: string
  /** 成员姓名 */
  name: string
  /** 成员联系电话 */
  phone: string
}

/** 车行列表行数据，含经营指标与成员列表 */
export interface Dealer {
  /** 原始数字 ID（来自 API，用于调保等需要 numeric userId 的场景） */
  numericId: number
  /** 车行唯一标识，格式 S000001 */
  id: string
  /** 车行工商名称 */
  name: string
  /** 入驻日期，格式 YYYY/M/D */
  joinedAt: string
  /** 当前车行管理员姓名 */
  adminName: string
  /** 管理员联系电话 */
  phone: string
  /** 驻点省份 */
  province: string
  /** 驻点城市 */
  city: string
  /** 实体店详细经营地址 */
  address: string
  /** 统一社会信用代码，未录入时为占位文案 */
  creditCode: string
  /** 保证金余额（元） */
  depositBalance: number
  /** 旗下成员数量 */
  members: number
  /** 成员详情列表，供详情抽屉渲染 */
  membersList: DealerMember[]
  /** 本月成交订单数 */
  monthlyOrders: number
  /** 累计成交订单数 */
  totalOrders: number
  /** 车源挂牌统计 */
  vehicles: {
    /** 当前在售车源数 */
    onSale: number
    /** 已下架车源数 */
    offShelf: number
  }
  /** 营业执照附件 URL，空字符串表示未上传 */
  licenseUrl: string
  /** 金融授信信息，无授信时为 null */
  credit: DealerCredit | null
  /** 准入状态：ACTIVE 正常开业 / INACTIVE 已停业 / SUSPENDED 封禁挂起 */
  status: 'active' | 'inactive' | 'suspended'
}

/** 新增合作车行弹窗表单数据 */
export interface DealerCreateForm {
  /** 车行名称 */
  name: string
  /** 初始车行管理员姓名 */
  adminName: string
  /** 联系手机电话，须为 11 位手机号 */
  phone: string
  /** 申请人身份证号 */
  idNumber: string
  /** 申请人身份证图片 URL */
  idImageUrl: string
  /** 车行实体门店图片 URL */
  storeImageUrl: string
  /** 统一社会信用代码 */
  creditCode: string
  /** 营业执照附件 URL */
  licenseUrl: string
  /** 驻点省份 */
  province: string
  /** 驻点城市 */
  city: string
  /** 实体店详细经营地址 */
  address: string
  /** 首期授信保证金金额（元） */
  initialCredit: number
}
