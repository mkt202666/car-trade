-- ============================================================
-- 5D好车运营端 - 数据库索引优化脚本 (PostgreSQL)
-- 版本: 2.0 | 日期: 2026-06-13 | 表名已修正
--
-- 使用说明:
--   1. 在正式执行前，请先在测试环境验证
--   2. CONCURRENTLY 创建索引不锁表，推荐使用
--   3. 可通过 psql -d new_car_trade -f performance_indexes.sql 执行
--
-- 索引策略:
--   - 第一优先级: 高频查询核心表（tc_users, tc_car_sources, tc_orders, tc_deposit_accounts）
--   - 第二优先级: 关联表和审计表（tc_disputes, tc_order_logs, tc_shop_members, tc_audit_logs, tc_series, tc_models）
--   - 第三优先级: 辅助查询表（tc_admin_notifications, tc_deposit_records, tc_purchase_demands, tc_banners）
-- ============================================================

-- ==================== 第一优先级：核心高频表 ====================

-- tc_users 表（最高频，几乎所有服务都引用）
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_users_del_status_created
    ON tc_users(deleted_at, status, created_at DESC);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_users_del_cert
    ON tc_users(deleted_at, certification_status);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_users_del_shopname
    ON tc_users(deleted_at, shop_name);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_users_del_created
    ON tc_users(deleted_at, created_at);

-- tc_car_sources 表（车源管理核心表）
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_car_sources_status_created
    ON tc_car_sources(status, created_at DESC);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_car_sources_user_id
    ON tc_car_sources(user_id);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_car_sources_brand_id
    ON tc_car_sources(brand_id);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_car_sources_series_id
    ON tc_car_sources(series_id);

-- tc_orders 表（交易管理核心表）
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_orders_status_created
    ON tc_orders(status, created_at DESC);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_orders_created_status
    ON tc_orders(created_at, status);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_orders_buyer_id
    ON tc_orders(buyer_id);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_orders_seller_id
    ON tc_orders(seller_id);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_orders_car_id
    ON tc_orders(car_id);

-- tc_deposit_accounts 表
CREATE UNIQUE INDEX CONCURRENTLY IF NOT EXISTS idx_deposit_accounts_user_id
    ON tc_deposit_accounts(user_id);


-- ==================== 第二优先级：关联表和审计表 ====================

-- tc_disputes 表
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_disputes_status_created
    ON tc_disputes(status, created_at DESC);

-- tc_order_logs 表
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_order_logs_order_created
    ON tc_order_logs(order_id, created_at ASC);

-- tc_shop_members 表
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_shop_members_shop_user
    ON tc_shop_members(shop_user_id);

-- tc_series 表（品牌→车系查询，原 car_series 已修正）
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_series_brand_id
    ON tc_series(brand_id);

-- tc_models 表（车系→车型查询，原 car_models 已修正）
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_models_series_id
    ON tc_models(series_id);

-- tc_audit_logs 表
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_audit_logs_created
    ON tc_audit_logs(created_at DESC);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_audit_logs_module
    ON tc_audit_logs(module);


-- ==================== 第三优先级：辅助查询表 ====================

-- tc_admin_users 表（username 已有 UNIQUE 约束索引，无需额外创建）

-- tc_admin_notifications 表
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_admin_notifications_target_user
    ON tc_admin_notifications(target_user_id, created_at DESC);

-- tc_deposit_records 表（user_id 字段需要从 tc_deposit_accounts JOIN 获取）
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_deposit_records_account_created
    ON tc_deposit_records(account_id, created_at DESC);

-- tc_purchase_demands 表（原 purchase_requests 已修正）
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_purchase_demands_status_created
    ON tc_purchase_demands(status, created_at DESC);

-- tc_banners 表
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_banners_status_sort
    ON tc_banners(status, sort_order);


-- ==================== 第四优先级：可选优化 ====================

-- 全文搜索索引（LIKE '%keyword%' 性能提升）
-- CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_car_sources_title_gin
--     ON tc_car_sources USING gin(to_tsvector('simple', title));
-- CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_users_nickname_gin
--     ON tc_users USING gin(to_tsvector('simple', coalesce(nickname, '') || ' ' || coalesce(real_name, '') || ' ' || coalesce(phone, '')));

-- 保证金账户低余额预警（表达式索引）
-- CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_deposit_accounts_balance_sum
--     ON tc_deposit_accounts((balance + frozen_amount));
