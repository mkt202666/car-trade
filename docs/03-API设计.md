## 三、API 设计

### 3.1 API 规范

- **基础路径**: `/api/v1`
- **认证方式**: JWT Token (Bearer)
- **请求格式**: `application/json`
- **响应格式**: 统一 JSON 结构

### 3.2 统一响应结构

```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1717636800000,
  "traceId": "abc123"
}
```

### 3.3 API 接口清单

#### 3.3.1 车源模块 `/api/v1/cars`

| 方法 | 路径 | 描述 | 请求参数 | 响应数据 |
|------|------|------|----------|----------|
| GET | /cars | 车源列表 | keyword, brand, series, priceMin, priceMax, city, energyType, exportCountry, deposit, sort, page, size | PageResult<CarVO> |
| GET | /cars/{id} | 车源详情 | id | CarDetailVO |
| POST | /cars | 发布车源 | CarCreateDTO | CarVO |
| PUT | /cars/{id} | 修改车源 | id, CarUpdateDTO | CarVO |
| DELETE | /cars/{id} | 删除车源 | id | void |
| POST | /cars/{id}/favorite | 收藏车源 | id | void |
| DELETE | /cars/{id}/favorite | 取消收藏 | id | void |
| GET | /cars/recommend | 推荐车源 | page, size | PageResult<CarVO> |
| GET | /cars/export | 出口车源 | country, page, size | PageResult<CarVO> |

**CarCreateDTO**:
```json
{
  "brandId": 1,
  "seriesId": 1,
  "modelId": 1,
  "year": 2024,
  "mileage": 5000,
  "color": "黑色",
  "price": 250000,
  "deposit": 3000,
  "usageType": "NON_OPERATING",
  "ownerType": "PERSONAL",
  "isMortgaged": false,
  "isInherited": false,
  "cityCode": "120000",
  "images": ["url1", "url2"],
  "videos": ["url1"],
  "description": "车况描述...",
  "exportCountries": ["RU", "KZ"],
  "inspection": {
    "overallCondition": "NORMAL",
    "paint": "ORIGINAL",
    "structure": "ORIGINAL",
    "engine": "NORMAL",
    "transmission": "NORMAL",
    "transferCount": 0,
    "mileageType": "ACTUAL"
  }
}
```

**CarVO**:
```json
{
  "id": 87654321,
  "brandName": "奥迪",
  "seriesName": "RS7",
  "modelName": "2022款 RS 7 4.0T Sportback",
  "year": 2022,
  "mileage": 53000,
  "price": 1280000,
  "deposit": 3000,
  "city": "天津",
  "energyType": "GASOLINE",
  "images": ["url1"],
  "tags": ["DEPOSIT", "EXPORT_RU", "EXPORT_KZ"],
  "auctionStatus": "BIDDING",
  "auctionEndTime": "2026-06-06T15:30:00",
  "createdAt": "2026-06-05T10:00:00",
  "viewCount": 34010,
  "favoriteCount": 120
}
```

#### 3.3.2 订单模块 `/api/v1/orders`

| 方法 | 路径 | 描述 | 请求参数 | 响应数据 |
|------|------|------|----------|----------|
| GET | /orders | 订单列表 | type(sold/bought), status, page, size | PageResult<OrderVO> |
| GET | /orders/{id} | 订单详情 | id | OrderDetailVO |
| POST | /orders | 创建订单 | OrderCreateDTO | OrderVO |
| PUT | /orders/{id}/confirm | 确认订单 | id | OrderVO |
| PUT | /orders/{id}/cancel | 取消订单 | id, CancelReasonDTO | OrderVO |
| PUT | /orders/{id}/pay-deposit | 支付保证金 | id, PayDTO | PaymentVO |
| PUT | /orders/{id}/complete | 确认完成 | id | OrderVO |
| POST | /orders/{id}/dispute | 发起争议 | id, DisputeDTO | DisputeVO |
| GET | /orders/{id}/logs | 订单日志 | id | List<OrderLogVO> |

**OrderCreateDTO**:
```json
{
  "carId": 87654321,
  "depositAmount": 3000,
  "remark": "备注信息"
}
```

**OrderVO**:
```json
{
  "id": "DJ202604181645538482",
  "carId": 87654321,
  "carName": "奥迪 RS7 2022款 RS 7 4.0T Sportback",
  "carImage": "url",
  "totalPrice": 1280000,
  "depositAmount": 3000,
  "status": "PENDING_CONFIRM",
  "buyerId": 1001,
  "buyerName": "华仔",
  "sellerId": 1002,
  "sellerName": "张三",
  "createdAt": "2026-04-18T16:45:53",
  "updatedAt": "2026-04-18T16:45:53"
}
```

#### 3.3.3 用户模块 `/api/v1/users`

| 方法 | 路径 | 描述 | 请求参数 | 响应数据 |
|------|------|------|----------|----------|
| POST | /users/login | 登录 | LoginDTO | LoginVO |
| POST | /users/register | 注册 | RegisterDTO | UserVO |
| GET | /users/me | 当前用户信息 | - | UserVO |
| PUT | /users/me | 更新个人信息 | UserUpdateDTO | UserVO |
| POST | /users/me/avatar | 上传头像 | MultipartFile | String(url) |
| GET | /users/me/stats | 用户统计 | - | UserStatsVO |
| POST | /users/certification | 实名认证 | CertificationDTO | CertificationVO |
| GET | /users/{id} | 查看用户信息 | id | UserPublicVO |

**LoginDTO**:
```json
{
  "phone": "13012348888",
  "smsCode": "123456"
}
```

**LoginVO**:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 7200,
  "user": {
    "id": 1001,
    "nickname": "华仔",
    "phone": "130****8888",
    "avatar": "url",
    "shopName": "天津5D好车",
    "creditGrade": "S"
  }
}
```

**UserVO**:
```json
{
  "id": 1001,
  "nickname": "华仔",
  "realName": "刘德华",
  "phone": "130****8888",
  "avatar": "url",
  "shopName": "天津5D好车",
  "creditGrade": "S",
  "creditScore": 95,
  "dealCount": 10,
  "memberExpireAt": "2027-06-30",
  "depositBalance": 3000,
  "depositTotal": 4200,
  "onSaleCount": 4,
  "viewCount": 34010,
  "viewCountToday": 231,
  "messageCount": 998,
  "messageCountToday": 45,
  "followerCount": 120,
  "followerCountToday": 3,
  "certificationStatus": "CERTIFIED",
  "createdAt": "2025-01-01"
}
```

#### 3.3.4 消息模块 `/api/v1/messages`

| 方法 | 路径 | 描述 | 请求参数 | 响应数据 |
|------|------|------|----------|----------|
| GET | /messages | 消息列表 | type, isRead, page, size | PageResult<MessageVO> |
| GET | /messages/unread-count | 未读数量 | - | UnreadCountVO |
| PUT | /messages/{id}/read | 标记已读 | id | void |
| PUT | /messages/read-all | 全部已读 | - | void |
| DELETE | /messages/{id} | 删除消息 | id | void |

**MessageVO**:
```json
{
  "id": 10001,
  "type": "TRADE",
  "title": "订单状态变更",
  "content": "您的订单 DJ202604181645538482 已进入交易中状态",
  "isRead": false,
  "relatedId": "DJ202604181645538482",
  "relatedType": "ORDER",
  "createdAt": "2026-06-06T10:00:00"
}
```

#### 3.3.5 AI 助理模块 `/api/v1/ai`

| 方法 | 路径 | 描述 | 请求参数 | 响应数据 |
|------|------|------|----------|----------|
| POST | /ai/market-analysis | 行情分析 | MarketAnalysisDTO | AnalysisVO |
| POST | /ai/search | 全网找车 | AISearchDTO | PageResult<CarVO> |
| POST | /ai/customer-generation | 获客文案 | CopywritingDTO | CopywritingVO |
| POST | /ai/auto-outreach | 自动拓客 | OutreachDTO | TaskVO |
| POST | /ai/distribute | AI分发 | DistributeDTO | TaskVO |

#### 3.3.6 金融模块 `/api/v1/finance`

| 方法 | 路径 | 描述 | 请求参数 | 响应数据 |
|------|------|------|----------|----------|
| GET | /finance/deposit | 保证金账户 | - | DepositVO |
| POST | /finance/deposit/recharge | 充值保证金 | RechargeDTO | PaymentVO |
| POST | /finance/deposit/withdraw | 提现保证金 | WithdrawDTO | WithdrawVO |
| GET | /finance/deposit/records | 保证金流水 | page, size | PageResult<DepositRecordVO> |
| GET | /finance/credit | 信用额度 | - | CreditVO |
| POST | /finance/credit/apply | 申请额度 | CreditApplyDTO | CreditApplyVO |

#### 3.3.7 关注模块 `/api/v1/follows`

| 方法 | 路径 | 描述 | 请求参数 | 响应数据 |
|------|------|------|----------|----------|
| POST | /follows/{userId} | 关注卖家 | userId | void |
| DELETE | /follows/{userId} | 取消关注 | userId | void |
| GET | /follows | 我的关注列表 | page, size | PageResult<UserPublicVO> |
| GET | /follows/{userId}/check | 是否已关注 | userId | {followed: true/false} |

#### 3.3.8 车行管理模块 `/api/v1/shop`

| 方法 | 路径 | 描述 | 请求参数 | 响应数据 |
|------|------|------|----------|----------|
| GET | /shop/members | 车行成员列表 | page, size | PageResult<ShopMemberVO> |
| POST | /shop/members/invite | 邀请成员 | userId | void |
| PUT | /shop/members/{id}/approve | 审批加入 | id, approve(true/false) | void |
| PUT | /shop/members/{id}/role | 修改角色 | id, role | void |
| DELETE | /shop/members/{id} | 移除成员 | id | void |

#### 3.3.9 在线客服模块 `/api/v1/cs`

| 方法 | 路径 | 描述 | 请求参数 | 响应数据 |
|------|------|------|----------|----------|
| POST | /cs/tickets | 创建工单 | TicketCreateDTO | TicketVO |
| GET | /cs/tickets | 我的工单列表 | status, page, size | PageResult<TicketVO> |
| GET | /cs/tickets/{id} | 工单详情 | id | TicketDetailVO |
| POST | /cs/tickets/{id}/messages | 发送消息 | content, image? | TicketMessageVO |
| GET | /cs/tickets/{id}/messages | 工单消息列表 | page, size | PageResult<TicketMessageVO> |

#### 3.3.10 会员模块 `/api/v1/membership`

| 方法 | 路径 | 描述 | 请求参数 | 响应数据 |
|------|------|------|----------|----------|
| GET | /membership/plans | 会员方案列表 | - | List<MemberPlanVO> |
| GET | /membership/my | 我的会员 | - | UserMembershipVO |
| POST | /membership/renew | 开通/续费 | planId, paymentMethod | PaymentVO |

#### 3.3.11 电子合同模块 `/api/v1/contracts`

| 方法 | 路径 | 描述 | 请求参数 | 响应数据 |
|------|------|------|----------|----------|
| POST | /contracts | 生成合同 | orderId | ContractVO |
| GET | /contracts/{id} | 合同详情 | id | ContractDetailVO |
| PUT | /contracts/{id}/sign | 签署合同 | id | void |
| GET | /contracts/{id}/download | 下载合同 | id | File |

#### 3.3.12 聊天模块 `/api/v1/chat`

| 方法 | 路径 | 描述 | 请求参数 | 响应数据 |
|------|------|------|----------|----------|
| GET | /chat/conversations | 会话列表 | - | List<ConversationVO> |
| POST | /chat/conversations | 创建会话 | targetUserId, orderId? | ConversationVO |
| GET | /chat/conversations/{id}/messages | 消息列表 | page, size | PageResult<ChatMessageVO> |
| POST | /chat/conversations/{id}/read | 标记已读 | - | void |

#### 3.3.13 浏览记录 `/api/v1/users/me/browsing`

| 方法 | 路径 | 描述 | 请求参数 | 响应数据 |
|------|------|------|----------|----------|
| GET | /users/me/browsing | 浏览记录 | page, size | PageResult<BrowsingHistoryVO> |
| DELETE | /users/me/browsing | 清空记录 | - | void |

#### 3.3.14 优惠券模块 `/api/v1/coupons`

| 方法 | 路径 | 描述 | 请求参数 | 响应数据 |
|------|------|------|----------|----------|
| GET | /coupons | 可用优惠券列表 | - | List<CouponVO> |
| GET | /coupons/my | 我的优惠券 | status, page, size | PageResult<UserCouponVO> |

#### 3.3.15 车源模块补充

| 方法 | 路径 | 描述 | 请求参数 | 响应数据 |
|------|------|------|----------|----------|
| GET | /cars/{id}/images/{imageId}/download | 下载图片 | id, imageId | File |
| POST | /cars/{id}/contact | 联系卖家 | id | void |
