# 5D好车项目 - 长期记忆

## 项目结构
- monorepo: `car-trade-backend/`(移动端后端) + `car-trade-frontend/`(移动端前端uni-app) + `car-trade-admin-backend/`(运营端后端) + `car-trade-admin-frontend/`(运营端前端React)

## 关键技术决策 (2026-06-13)
- **运营端前端技术栈：React + Vite + Tailwind CSS + Ant Design 5.x**（非 uni-app）
  - 原因：原型 http://shengtaiprd.pancosky.com/5dadmin/ 为 React 技术栈，暗色主题管理后台
  - 端口分配：移动端后端8080 / 运营端后端8081 / 移动前端5173 / 运营前端5174
  - API前缀：运营端后端 `/api/v1/admin`

## 编译环境 (2026-06-13)
- **Java 21**: `C:\Program Files\Microsoft\jdk-21.0.11.10-hotspot`
- **Maven**: `D:\d_app\apache-maven-3.9.6\bin\mvn.cmd`
- **编译命令**: `JAVA_HOME="C:\Program Files\Microsoft\jdk-21.0.11.10-hotspot" "D:\d_app\apache-maven-3.9.6\bin\mvn.cmd" -B compile`
- **Lombok**: 必须在 maven-compiler-plugin 中显式配置 `annotationProcessorPaths`，否则 clean compile 不生效
- **依赖**: spring-security-crypto (BCrypt), hutool-all 5.8.26, jjwt 0.12.5, springdoc-openapi 2.6.0

## Phase 2 完成 (2026-06-13)
- **后端**：21个接口全部完成，clean compile 112文件 0错误
  - 车行管理+审核（8个）：`/api/v1/admin/shops`, `/api/v1/admin/shop-reviews`
  - 车源管理+交易管理（7个）：`/api/v1/admin/cars`, `/api/v1/admin/orders`
  - 争议处理+保证金管理（7个）：`/api/v1/admin/disputes`, `/api/v1/admin/deposits`
  - 求购管理（2个）：`/api/v1/admin/purchase-requests`
- **前端**：7个页面全部完成，vite build 1672 modules 0错误
  - 车行管理(`/shop-manage`)、车行审核(`/shop-audit`)
  - 车源管理(`/car-manage`)、交易管理(`/trade-manage`)
  - 争议处理(`/dispute-manage`)、保证金现金流(`/deposit-manage`)
  - 求购管理(`/purchase-manage`)
- **新增 Entity**：AppOrderLog, AppDepositRecord, AppPurchaseRequest
- **新增 Service**：AdminShopService, AdminShopReviewService, AdminCarService, AdminOrderService, AdminDisputeService, AdminDepositService, AdminPurchaseRequestService
- **新增 Controller**：AdminShopController, AdminShopReviewController, AdminCarController, AdminOrderController, AdminDisputeController, AdminDepositController, AdminPurchaseRequestController

## Phase 3 进度 (2026-06-13)
- **Day 13-14** ✅ 资源管理+车型库（22接口）
  - 新增 Entity: AdminBanner, AdminConfig, AdminCarBrand, AdminCarSeries, AdminCarModel
  - 新增 Controller: AdminResourceController(8接口), AdminCarLibraryController(14接口)
- **Day 15** ✅ 导出配置（5个Excel导出接口）
  - 新增工具类: `ExcelExportUtil`（Apache POI 5.3.0 XSSFWorkbook）
  - 导出接口: `/shops/export`, `/cars/export`, `/orders/export`, `/disputes/export`, `/deposits/records/export`
  - 各 Service 添加 `getXxxExportList()` 方法（不分页）
  - 前端5个列表页面均添加"导出"按钮（Download图标+window.open触发下载）
- **Day 16** ✅ 移动端配合接口（7个接口+6个Service改造）
  - 新增 Redis Pub/Sub 通道: `mobile:events`（运营端→移动端通知推送）
  - 新增 MobileNotification DTO（7种通知类型: SHOP_AUDIT_RESULT, CAR_STATUS_CHANGED, DISPUTE_RESOLVED, DEPOSIT_CHANGED, USER_STATUS_CHANGED, SYSTEM_ANNOUNCEMENT, PURCHASE_MATCHED）
  - 新增 MobileEventPublisher（发布到 mobile:events）
  - 新增 Entity: AdminNotification（通知记录持久化）
  - 新增 AdminNotificationService（通知发送+保存+广播公告）
  - 新增 AdminNotificationController: POST /notifications/announcement, POST /notifications/send, GET /notifications, GET /notifications/stats
  - 改造 Service: AdminShopReviewService, AdminCarService, AdminDisputeService, AdminDepositService, AdminUserService, AdminPurchaseRequestService 自动发送移动端通知
  - 后端 148 文件编译通过
- **Day 17** ✅ 数据同步+缓存策略
  - 启用 Spring Cache + Redis：@EnableCaching + RedisCacheManager（7个缓存区域，不同TTL）
  - Dashboard 缓存：6个统计方法添加 @Cacheable（KPI 5min, 趋势 10min, 分布 15min, 审批 2min）
  - Token 黑名单：登出时 token 加入 Redis 黑名单（TTL=token剩余有效期），AdminAuthInterceptor 检查黑名单
  - 新增 POST /auth/logout 接口
  - ConfigCacheService：缓存 admin_configs 表配置（30min TTL）
  - DashboardCacheScheduler：定时任务（5min刷新KPI, 10min刷新趋势, 每天凌晨2点清除）
  - 缓存区域：dashboard:kpi, dashboard:trend, dashboard:distribution, dashboard:coupon, dashboard:approval, dashboard:warnings, config
  - 后端 150 文件编译通过
- **Day 18** ✅ 性能优化+集成测试+全部 Bug 修复
  - 限流、GZIP、索引、健康检查、单元测试、前端代码分割等已完成
  - 全部18个API端点测试通过 ✅
  - 新增端点：`/dashboard/stats/overview`、`/resources/configs`（list）
  - 数据库：创建 admin_notifications 表，执行25个性能索引
  - 后端150文件编译通过，前端1675 modules构建通过

## 联调修复的关键 Bug (2026-06-13)
- **Redis 配置属性名错误**: Spring Boot 3.x 需要用 `spring.data.redis.*` 而非 `spring.redis.*`（application.properties + application-local.properties + application-test.properties 全部修正）
- **@TableLogic + LocalDateTime 不兼容**: MyBatis-Plus 默认 `deleted_at = 0` 但字段是 timestamp → 全局配置 `logic-not-delete-value=null` + `logic-delete-value=NOW()`
- **JSONB 列与 String 实体不兼容**: `audit_logs.request_params` JSONB → 改为 TEXT；`admin_users.permissions` JSONB → 实体加 `@TableField(updateStrategy=NEVER)` 避免更新
- **状态大小写不一致**: DB 存 'ACTIVE'（大写）但 Java 用 "active"（小写）→ 改为 `equalsIgnoreCase`
- **audit_logs.admin_id NOT NULL 但登录接口无 adminId** → 改为 nullable
- **audit_logs.result VARCHAR(20) 太短** → 改为 VARCHAR(200)
- **登录账号**: `yuan2026@5d.com` / `admin123`（BCrypt hash）
- **StringRedisTemplate 类型转换**: `RedisTemplate<String, String>` 的 `increment()` 存储整数类型，`get()` 时 Integer 无法 cast 到 String → 改用 `StringRedisTemplate`
- **PostgreSQL 类型转换**: MyBatis `@Select` 中 String 参数与 TIMESTAMP 列比较需显式 `::timestamp` 转换（`selectDailyTrend` + `selectDailyNewUsers`）
- **coupons 表字段**: 无 `used_count` 列，有 `total_count` 和 `remain_count` → 用 `total_count - remain_count` 计算
- **前端 Dashboard 字段映射**: 前端期望 `gmv`/`carNew`/`deposit` 等字段，与后端 `DashboardKpiVO` 返回字段不一致 → 前端改映射 + 后端新增 `gmv`/`deposit`/`depositActive`/`pendingProcessed` 字段

## Entity 命名规范
- `App*` 前缀映射移动端表（AppUser→users, AppCarSource→car_sources 等）
- `Admin*` 前缀用于运营端独有表
- **车型库直接复用 mobile 端表**：AdminCarBrand→brands, AdminCarSeries→series, AdminCarModel→models（不复建 car_brands/car_series/car_models）
- **求购表复用 mobile 端**：AppPurchaseRequest→purchase_demands（不复建 purchase_requests）
- **已删除冗余实体**：AdminBanner（Banner更完整已覆盖）、AdminConfig（Config更完整已覆盖）
- @AuditLog 注解 + AOP 自动记录操作日志
- Redis Pub/Sub 事件通道：`admin:events`
- AdminAuthInterceptor 设置 request attribute: ADMIN_ID, ADMIN_USERNAME, ADMIN_ROLE

## 数据库表清单（最终版）
- **mobile 端建表（init.sql）**：34张（users/brands/series/models/car_sources/car_images/car_tags/car_inspections/orders/order_inspections/order_logs/deposit_accounts/deposit_records/credit_accounts/messages/user_favorites/user_follows/browsing_history/shop_members/coupons/user_coupons/member_plans/user_membership/customer_service_tickets/chat_conversations/chat_conversation_members/chat_messages/contracts/disputes/auctions/auction_bids/auction_watches/purchase_demands/cities/export_countries）
- **admin 端独有表（admin-init.sql）**：6张（admin_users/audit_logs/banners/export_templates/configs/admin_notifications）
- **两端共享的 App* 映射表**：users/car_sources/orders/order_logs/disputes/shop_members/deposit_accounts/deposit_records/coupons/purchase_demands + brands/series/models
- **已废弃的重复表名**：car_brands→brands, car_series→series, car_models→models, purchase_requests→purchase_demands
