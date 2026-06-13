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
--   - 第一优先级: 高频查询核心表（users, car_sources, orders, deposit_accounts）
--   - 第二优先级: 关联表和审计表（disputes, order_logs, shop_members, audit_logs, series, models）
--   - 第三优先级: 辅助查询表（admin_notifications, deposit_records, purchase_demands, banners）
-- ============================================================

-- ==================== 第一优先级：核心高频表 ====================

-- users 表（最高频，几乎所有服务都引用）
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_users_del_status_created
    ON users(deleted_at, status, created_at DESC);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_users_del_cert
    ON users(deleted_at, certification_status);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_users_del_shopname
    ON users(deleted_at, shop_name);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_users_del_created
    ON users(deleted_at, created_at);

-- car_sources 表（车源管理核心表）
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_car_sources_status_created
    ON car_sources(status, created_at DESC);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_car_sources_user_id
    ON car_sources(user_id);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_car_sources_brand_id
    ON car_sources(brand_id);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_car_sources_series_id
    ON car_sources(series_id);

-- orders 表（交易管理核心表）
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_orders_status_created
    ON orders(status, created_at DESC);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_orders_created_status
    ON orders(created_at, status);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_orders_buyer_id
    ON orders(buyer_id);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_orders_seller_id
    ON orders(seller_id);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_orders_car_id
    ON orders(car_id);

-- deposit_accounts 表
CREATE UNIQUE INDEX CONCURRENTLY IF NOT EXISTS idx_deposit_accounts_user_id
    ON deposit_accounts(user_id);


-- ==================== 第二优先级：关联表和审计表 ====================

-- disputes 表
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_disputes_status_created
    ON disputes(status, created_at DESC);

-- order_logs 表
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_order_logs_order_created
    ON order_logs(order_id, created_at ASC);

-- shop_members 表
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_shop_members_shop_user
    ON shop_members(shop_user_id);

-- series 表（品牌→车系查询，原 car_series 已修正）
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_series_brand_id
    ON series(brand_id);

-- models 表（车系→车型查询，原 car_models 已修正）
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_models_series_id
    ON models(series_id);

-- audit_logs 表
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_audit_logs_created
    ON audit_logs(created_at DESC);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_audit_logs_module
    ON audit_logs(module);


-- ==================== 第三优先级：辅助查询表 ====================

-- admin_users 表（username 已有 UNIQUE 约束索引，无需额外创建）

-- admin_notifications 表
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_admin_notifications_target_user
    ON admin_notifications(target_user_id, created_at DESC);

-- deposit_records 表（user_id 字段需要从 deposit_accounts JOIN 获取）
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_deposit_records_account_created
    ON deposit_records(account_id, created_at DESC);

-- purchase_demands 表（原 purchase_requests 已修正）
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_purchase_demands_status_created
    ON purchase_demands(status, created_at DESC);

-- banners 表
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_banners_status_sort
    ON banners(status, sort_order);


-- ==================== 第四优先级：可选优化 ====================

-- 全文搜索索引（LIKE '%keyword%' 性能提升）
-- CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_car_sources_title_gin
--     ON car_sources USING gin(to_tsvector('simple', title));
-- CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_users_nickname_gin
--     ON users USING gin(to_tsvector('simple', coalesce(nickname, '') || ' ' || coalesce(real_name, '') || ' ' || coalesce(phone, '')));

-- 保证金账户低余额预警（表达式索引）
-- CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_deposit_accounts_balance_sum
--     ON deposit_accounts((balance + frozen_amount));
