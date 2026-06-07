// ==================== 首页-找车 Mock 数据 ====================
export const carList = [
  {
    id: 10001567,
    title: '大众 Polo 2023款 1.5L 自动全景乐享版',
    coverImage: '',
    year: 2023,
    mileage: 0.8,
    price: 65000,
    deposit: 3000,
    hasDeposit: true,
    color: '白色',
    energyType: '燃油',
    city: '天津',
    cityCode: '120000',
    createTime: '2分钟前',
    exportCountries: ['RU', 'KZ'],
    auctionStatus: null,
    auctionRemaining: null
  },
  {
    id: 10001568,
    title: '斯柯达 晶锐 2022款 1.5L 自动车顶',
    coverImage: '',
    year: 2022,
    mileage: 2.1,
    price: 52000,
    deposit: 0,
    hasDeposit: false,
    color: '蓝色',
    energyType: '燃油',
    city: '山东-青岛',
    cityCode: '370200',
    createTime: '15分钟前',
    exportCountries: ['KZ'],
    isGlobal: true,
    auctionStatus: null,
    auctionRemaining: null
  },
  {
    id: 10001569,
    title: '奔驰 CLE (进口) 2024款 CLE 260 2.0T',
    coverImage: '',
    year: 2024,
    mileage: 1.4,
    price: 268000,
    deposit: 5000,
    hasDeposit: true,
    color: '黑色',
    energyType: '燃油',
    city: '天津',
    cityCode: '120000',
    createTime: '1小时前',
    exportCountries: [],
    auctionStatus: '拍卖中',
    auctionRemaining: '剩1小时30分'
  },
  {
    id: 10001570,
    title: '宝马 X5 2023款 xDrive30Li 2.0T M运动套装',
    coverImage: '',
    year: 2023,
    mileage: 1.5,
    price: 345000,
    deposit: 8000,
    hasDeposit: true,
    color: '黑色',
    energyType: '燃油',
    city: '北京',
    cityCode: '110000',
    createTime: '2小时前',
    exportCountries: [],
    auctionStatus: '拍卖中',
    auctionRemaining: '剩2小时45分',
    hasParticipated: true
  },
  {
    id: 10001571,
    title: '奔驰 EQE 2022款 350 先锋版',
    coverImage: '',
    year: 2022,
    mileage: 4.7,
    price: 147000,
    deposit: 0,
    hasDeposit: false,
    color: '银色',
    energyType: '纯电',
    city: '广东-东莞',
    cityCode: '441900',
    createTime: '3小时前',
    exportCountries: [],
    auctionStatus: null,
    auctionRemaining: null
  },
  {
    id: 10001572,
    title: '奥迪 A6L 2024款 45 TFSI 臻选动感型',
    coverImage: '',
    year: 2024,
    mileage: 0.8,
    price: 326000,
    deposit: 5000,
    hasDeposit: true,
    color: '黑色',
    energyType: '燃油',
    city: '上海',
    cityCode: '310000',
    createTime: '4小时前',
    exportCountries: [],
    auctionStatus: null,
    auctionRemaining: null
  },
  {
    id: 10001573,
    title: '小米 SU7 2024款 后驱长续航智驾版',
    coverImage: '',
    year: 2024,
    mileage: 0.2,
    price: 215000,
    deposit: 0,
    hasDeposit: false,
    color: '海湾蓝',
    energyType: '纯电',
    city: '北京',
    cityCode: '110000',
    createTime: '5小时前',
    exportCountries: ['AE'],
    auctionStatus: '拍卖中',
    auctionRemaining: '剩3小时15分'
  },
  {
    id: 10001574,
    title: '蔚来 ET5T 2023款 75kWh',
    coverImage: '',
    year: 2023,
    mileage: 1.2,
    price: 228000,
    deposit: 3000,
    hasDeposit: true,
    color: '灰色',
    energyType: '纯电',
    city: '上海',
    cityCode: '310000',
    createTime: '6小时前',
    exportCountries: [],
    auctionStatus: null,
    auctionRemaining: null
  },
  {
    id: 10001575,
    title: '比亚迪 汉EV 2024款 荣耀版 715km旗舰型',
    coverImage: '',
    year: 2024,
    mileage: 0.5,
    price: 182000,
    deposit: 0,
    hasDeposit: false,
    color: '灰色',
    energyType: '纯电',
    city: '广东-广州',
    cityCode: '440100',
    createTime: '7小时前',
    exportCountries: ['AU'],
    isGlobal: true,
    auctionStatus: null,
    auctionRemaining: null
  },
  {
    id: 10001576,
    title: '沃尔沃 S90 2024款 T8 E驱混动 智雅豪华版',
    coverImage: '',
    year: 2024,
    mileage: 0.6,
    price: 368000,
    deposit: 5000,
    hasDeposit: true,
    color: '黑色',
    energyType: '混动',
    city: '浙江-杭州',
    cityCode: '330100',
    createTime: '8小时前',
    exportCountries: ['AU'],
    auctionStatus: null,
    auctionRemaining: null
  }
]

// ==================== 交易页 Mock 数据 ====================
export const orderStats = {
  allCount: 200,
  soldCount: 200,
  boughtCount: 90
}

export const soldOrders = [
  {
    id: 1,
    title: '奔驰 CLE（进口）2024款 CLE 260 Coupe 48V 2.0T',
    coverImage: '',
    carId: '12345678',
    price: 268000,
    deposit: 5000,
    status: 'IN_TRANSACTION',
    statusText: '等待双方确认交易完成',
    buyerName: '张学友',
    createTime: '2026-06-05 10:30:00',
    role: 'SELLER'
  },
  {
    id: 2,
    title: '奔驰 CLE（进口）2024款 CLE 260 Coupe 48V 2.0T',
    coverImage: '',
    carId: '12345678',
    price: 268000,
    deposit: 5000,
    status: 'PENDING',
    statusText: '买家已付保证金，请补充合同内容',
    buyerName: '张学友',
    createTime: '2026-06-05 09:00:00',
    role: 'SELLER'
  },
  {
    id: 3,
    title: '奔驰 CLE（进口）2024款 CLE 260 Coupe 48V 2.0T',
    coverImage: '',
    carId: '12345678',
    price: 268000,
    deposit: 5000,
    status: 'PENDING',
    statusText: '已提交合同，等待买家确认',
    buyerName: '张学友',
    createTime: '2026-06-04 15:00:00',
    role: 'SELLER'
  },
  {
    id: 4,
    title: '奔驰 CLE（进口）2024款 CLE 260 Coupe 48V 2.0T',
    coverImage: '',
    carId: '12345678',
    price: 268000,
    deposit: 5000,
    status: 'DISPUTE',
    statusText: '买家已发起争议，等待客服介入',
    buyerName: '张学友',
    createTime: '2026-06-04 11:00:00',
    role: 'SELLER'
  },
  {
    id: 5,
    title: '奔驰 CLE（进口）2024款 CLE 260 Coupe 48V 2.0T',
    coverImage: '',
    carId: '12345678',
    price: 268000,
    deposit: 5000,
    status: 'COMPLETED',
    statusText: '交易完成',
    buyerName: '张学友',
    createTime: '2026-06-03 16:00:00',
    role: 'SELLER'
  },
  {
    id: 6,
    title: '奔驰 CLE（进口）2024款 CLE 260 Coupe 48V 2.0T',
    coverImage: '',
    carId: '12345678',
    price: 268000,
    deposit: 5000,
    status: 'CANCELLED',
    statusText: '交易终止，已退回保证金',
    buyerName: '张学友',
    createTime: '2026-06-02 10:00:00',
    role: 'SELLER'
  }
]

export const boughtOrders = [
  {
    id: 7,
    title: '奥迪 RS7 2022款 RS 7 4.0T Sportback',
    coverImage: '',
    carId: '87654321',
    price: 1280000,
    deposit: 30000,
    status: 'PENDING',
    statusText: '已支付保证金，等待卖家补充合同内容',
    sellerName: '张学友',
    createTime: '2026-06-05 14:00:00',
    role: 'BUYER'
  },
  {
    id: 8,
    title: '保时捷 911 2023款 Carrera S 3.0T',
    coverImage: '',
    carId: '56781234',
    price: 1168000,
    deposit: 30000,
    status: 'PENDING',
    statusText: '卖家已提交合同，等待您确认',
    sellerName: '郭富城',
    createTime: '2026-06-05 08:00:00',
    role: 'BUYER'
  },
  {
    id: 9,
    title: '宝马 M4 2022款 M4 双门轿跑车 雷霆版',
    coverImage: '',
    carId: '43218765',
    price: 856000,
    deposit: 20000,
    status: 'DISPUTE',
    statusText: '已发起争议，等待处理',
    sellerName: '黎明',
    createTime: '2026-06-04 12:00:00',
    role: 'BUYER'
  },
  {
    id: 10,
    title: '法拉利 Roma 2020款 3.9T V8',
    coverImage: '',
    carId: '99887766',
    price: 2300000,
    deposit: 50000,
    status: 'COMPLETED',
    statusText: '交易完成',
    sellerName: '张国荣',
    createTime: '2026-06-02 09:00:00',
    role: 'BUYER'
  },
  {
    id: 11,
    title: '阿斯顿马丁 DB11 2019款 4.0T V8 Coupe',
    coverImage: '',
    carId: '55443322',
    price: 2880000,
    deposit: 50000,
    status: 'CANCELLED',
    statusText: '交易已取消，定金退回',
    sellerName: '梁朝伟',
    createTime: '2026-05-28 16:00:00',
    role: 'BUYER'
  }
]

export const allOrders = [...soldOrders, ...boughtOrders]

// ==================== 个人中心 Mock 数据 ====================
export const userProfile = {
  id: 1,
  phone: '13066668888',
  nickname: '华仔',
  realName: '刘德华',
  avatar: '',
  shopName: '天津5D好车',
  role: '车商',
  creditGrade: 'S',
  creditScore: 98,
  creditLabel: '极佳',
  dealCount: 10,
  certificationStatus: 'CERTIFIED',
  memberExpireAt: '2027-06-30',
  memberLevel: '钻石',
  deposit: {
    balance: 3000,
    total: 4200
  },
  stats: {
    carCount: 4,
    viewCount: 34010,
    viewCountToday: 231,
    contactCount: 998,
    contactCountToday: 45,
    followerCount: 120,
    followerCountToday: 3
  }
}

// ==================== 车源详情 Mock 数据 ====================
export const carDetail = {
  id: 10001567,
  carId: '10001567',
  title: '大众 Polo 2023款 1.5L 自动全景乐享版',
  brand: '大众',
  series: 'Polo',
  model: '2023款 1.5L 自动全景乐享版',
  images: [
    { url: '' }
  ],
  price: 65000,
  originalPrice: 100900,
  deposit: 3000,
  hasDeposit: true,
  exportCountries: ['RU', 'KZ'],
  year: 2023,
  month: '02',
  mileage: 0.8,
  color: '黑色',
  interiorColor: '黑色',
  emissionStandard: '国六',
  displacement: '2.0L',
  gearbox: '自动',
  manufacturer: '大众',
  vehicleType: '中大型车',
  guidePrice: 100900,
  usageType: '非营运',
  insuranceExpire: '2027-01-30',
  location: '天津',
  cityCode: '120000',
  hasMortgage: false,
  hasInheritance: false,
  keyCount: 2,
  createTime: '2024-05-21',
  updateTime: '2024-05-21',
  description: '女车主一手准新，车况绝佳，一个面补漆，查博士S级',
  aiAnalysis: '车况优异，价格偏低0.5万元，该车系买家需求近期稳定。',
  // 检测报告
  inspectionReports: {
    chaboshi: { available: true, label: '查博士' },
    ningmengcha: { available: true, label: '柠檬查' },
    weibao: { available: true, label: '4S维保' }
  },
  // 卖家信息
  seller: {
    id: 1,
    name: '华**',
    nickname: '华仔',
    realName: '刘德华',
    avatar: '',
    shopName: '天津xxx汽车贸易公司',
    creditGrade: 'A',
    creditLabel: '优秀',
    lastOnline: '04-16 13:59',
    carCount: 10,
    dealCount: 10,
    followerCount: 100
  },
  favorited: false
}

// ==================== 订单详情 Mock 数据 ====================
export const orderDetail = {
  id: 1,
  orderNo: 'DJ202604181645538482',
  carId: '87654321',
  carTitle: '奥迪 RS7 2022款 RS 7 4.0T Sportback',
  coverImage: '',
  price: 1280000,
  depositAmount: 3000,
  buyerDeposit: 3000,
  buyerDepositStatus: '已支付',
  sellerDeposit: 3000,
  sellerDepositStatus: '待支付',
  status: 'PENDING',
  statusText: '待确认',
  statusDesc: '已支付保证金，等待卖家补充合同内容',
  buyer: {
    name: '刘德华',
    city: '南京',
    phone: '135****1991',
    idCard: '320102********3816'
  },
  seller: {
    name: '张学友',
    city: '郑州',
    phone: '155****3620',
    idCard: '411423********051X'
  },
  contractId: 'DJ202604181645538482',
  createTime: '2026-04-18 16:45:53',
  // 车辆详情
  vin: 'LHGR*********6492',
  brandModel: '奥迪 RS7 2022款 RS 7 4.0T Sportback',
  registerDate: '2017-08-04',
  mileage: 5.3,
  color: '黑色',
  annualInspection: '2027-08-31',
  insuranceExpire: '2026-08-04',
  manufactureDate: '',
  keyCount: 2,
  usageType: '非营运',
  ownerType: '个人',
  hasMortgage: false,
  hasInheritance: false,
  // 车况信息
  condition: {
    overall: '非事故车',
    paint: '原漆',
    structure: '原版',
    engine: '正常',
    gearbox: '正常',
    ownershipTransfer: '一手车',
    mileageType: '实表'
  },
  conditionDesc: '',
  conditionPhotos: []
}

// ==================== 消息 Mock 数据 ====================
export const systemMessages = [
  {
    id: 1,
    type: 'SYSTEM',
    subtype: 'auto_promotion',
    title: '自动推广',
    content: '您有 20 个车源正在自动拓客',
    createTime: '13:00:00',
    date: '今天'
  },
  {
    id: 2,
    type: 'SYSTEM',
    subtype: 'order_update',
    title: '订单状态更新',
    content: '您的订单状态已更新，请点击查看详情',
    createTime: '12:00:00',
    date: '今天'
  },
  {
    id: 3,
    type: 'SYSTEM',
    subtype: 'contract',
    title: '新的订单合同',
    content: '您有一份新的订单合同待签署或确认',
    createTime: '11:00:00',
    date: '今天'
  },
  {
    id: 4,
    type: 'SYSTEM',
    subtype: 'deposit_warning',
    title: '可用保证金不足',
    content: '您的保证金不足3000，建议及时充值',
    createTime: '11:00:00',
    date: '今天'
  },
  {
    id: 5,
    type: 'SYSTEM',
    subtype: 'shop_apply',
    title: '新成员申请加入您的车行',
    content: '有新的员工申请加入，请点此审核',
    createTime: '11:00:00',
    date: '今天'
  }
]

export const chatConversations = [
  {
    id: 1,
    name: '学友',
    nickname: '张学友',
    avatar: '',
    lastMessage: '查博士过了吗?',
    lastMessageTime: '10:00:00',
    unreadCount: 0
  },
  {
    id: 2,
    name: '华仔',
    nickname: '华仔',
    avatar: '',
    lastMessage: '老板，奔驰那台客户约了下午看车',
    lastMessageTime: '4-15',
    unreadCount: 0
  }
]

// ==================== AI 快捷入口 Mock 数据 ====================
export const aiFunctions = [
  { id: 'market', name: 'AI行情分析', icon: 'trending-up', desc: '输入车型获取市场分析', color: '#3c9cff' },
  { id: 'search', name: '全网找车', icon: 'search', desc: '描述需求 AI 匹配车源', color: '#5ac725' },
  { id: 'outreach', name: '自动拓客', icon: 'share', desc: '设置条件自动推广车源', color: '#f56c6c' },
  { id: 'copywrite', name: '获客文案', icon: 'edit-pen', desc: '生成朋友圈/社群推广文案', color: '#f9ae3d' },
  { id: 'distribute', name: 'AI分发车源', icon: 'red-packet', desc: '智能匹配买家', color: '#a855f7' }
]

export const aiHistory = [
  { id: 1, title: '宝马X5行情分析', time: '今天 10:30' },
  { id: 2, title: '寻找10万以内SUV', time: '昨天 15:00' },
  { id: 3, title: '生成奥迪A6L获客文案', time: '前天 09:00' }
]