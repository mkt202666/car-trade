/** 用户管理相关类型定义 */

/** 用户详细档案资料，在表格展开行内展示与编辑 */
export interface UserProfile {
  /** 完整车行工商注册名称 */
  dealershipName: string
  /** 统一社会信用代码 */
  creditCode: string
  /** 车行所在省市区，以空格分隔 */
  provinceCity: string
  /** 法人或负责人身份证号 */
  idNumber: string
  /** 营业执照图片外链 URL */
  businessLicenseUrl: string
  /** 身份证正面图片外链 URL */
  idFrontUrl: string
  /** 身份证反面图片外链 URL */
  idBackUrl: string
}

/** 用户列表行数据，含业务指标与档案摘要 */
export interface User {
  /** 用户唯一标识，格式 USR-xxxx */
  id: string
  /** 注册姓名或车商简称 */
  name: string
  /** 注册日期，格式 YYYY/M/D */
  registerAt: string
  /** 业务角色分类（个人用户、车行用户、IT开发、系统管理员、已注销等） */
  category: string
  /** 实名预留手机号 */
  phone: string
  /** 微信 Union ID，未填写时为「—」 */
  wechat: string
  /** 挂牌与求购统计 */
  listing: {
    /** 当前在售车源数 */
    onSale: number
    /** 已下架车源数 */
    offShelf: number
    /** 求购发布数 */
    wanted: number
  }
  /** 关联车行信息 */
  dealership: {
    /** 车行名称，非车行用户为「—」 */
    name: string
    /** 合作车辆说明，如「5D 合作车辆」 */
    vehicles: string
  }
  /** 保证金账户 */
  deposit: {
    /** 可用余额，支持 W 后缀万单位 */
    available: string
    /** 总保证金，与可用余额同步 */
    total: string
  }
  /** 历史交易汇总 */
  transaction: {
    /** 成交笔数 */
    count: number
    /** 成交总额，如「¥245w」 */
    total: string
  }
  /** 金融授信额度 */
  credit: {
    /** 已用授信 */
    used: string
    /** 授信总额 */
    total: string
  }
  /** 详细档案，展开行内展示与编辑 */
  profile: UserProfile
}

/** 手动建档弹窗表单数据 */
export interface UserRegisterForm {
  /** 注册姓名或车商名称 */
  name: string
  /** 联系电话，须为 11 位手机号 */
  phone: string
  /** 微信 Union ID，可选 */
  wechat: string
  /** 归置角色：个人用户或车行用户 */
  category: string
  /** 首充保证金金额（元） */
  deposit: number
}
