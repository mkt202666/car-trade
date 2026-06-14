import psycopg2
import sys

conn = psycopg2.connect(
    host='47.117.41.219',
    port=5432,
    database='new_car_trade',
    user='postgres',
    password='sxk#2025@'
)
conn.autocommit = True
cur = conn.cursor()

# Set search_path to public
cur.execute("SET search_path = public")

# 1. Create admin_notifications table
print("=== Creating admin_notifications table ===")
sql_notifications = """
CREATE TABLE IF NOT EXISTS admin_notifications (
    id             BIGSERIAL PRIMARY KEY,
    type           VARCHAR(50)  NOT NULL,
    target_user_id BIGINT       NOT NULL,
    title          VARCHAR(200) NOT NULL,
    content        TEXT,
    target_type    VARCHAR(50),
    target_id      VARCHAR(100),
    extra_json     JSONB,
    status         VARCHAR(20)  NOT NULL DEFAULT 'SENT',
    created_at     TIMESTAMP    NOT NULL DEFAULT NOW()
);
"""
cur.execute(sql_notifications)
print("admin_notifications table created (or already exists)")

# Create indexes for admin_notifications
indexes_notif = [
    "CREATE INDEX IF NOT EXISTS idx_admin_notifications_target_user ON admin_notifications(target_user_id, created_at DESC)",
    "CREATE INDEX IF NOT EXISTS idx_admin_notifications_type ON admin_notifications(type)",
    "CREATE INDEX IF NOT EXISTS idx_admin_notifications_status ON admin_notifications(status)",
    "CREATE INDEX IF NOT EXISTS idx_admin_notifications_created ON admin_notifications(created_at DESC)",
]
for idx_sql in indexes_notif:
    cur.execute(idx_sql)
    print(f"  Index created: {idx_sql.split('idx_')[1].split(' ON')[0]}")

# 2. Performance indexes
print("\n=== Creating performance indexes ===")
indexes_perf = [
    # Priority 1: Core tables
    ("idx_users_del_status_created", "CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_users_del_status_created ON users(deleted_at, status, created_at DESC)"),
    ("idx_users_del_cert", "CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_users_del_cert ON users(deleted_at, certification_status)"),
    ("idx_users_del_shopname", "CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_users_del_shopname ON users(deleted_at, shop_name)"),
    ("idx_users_del_created", "CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_users_del_created ON users(deleted_at, created_at)"),
    ("idx_car_sources_status_created", "CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_car_sources_status_created ON car_sources(status, created_at DESC)"),
    ("idx_car_sources_user_id", "CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_car_sources_user_id ON car_sources(user_id)"),
    ("idx_car_sources_brand_id", "CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_car_sources_brand_id ON car_sources(brand_id)"),
    ("idx_car_sources_series_id", "CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_car_sources_series_id ON car_sources(series_id)"),
    ("idx_orders_status_created", "CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_orders_status_created ON orders(status, created_at DESC)"),
    ("idx_orders_created_status", "CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_orders_created_status ON orders(created_at, status)"),
    ("idx_orders_buyer_id", "CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_orders_buyer_id ON orders(buyer_id)"),
    ("idx_orders_seller_id", "CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_orders_seller_id ON orders(seller_id)"),
    ("idx_orders_car_id", "CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_orders_car_id ON orders(car_id)"),
    ("idx_deposit_accounts_user_id", "CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_deposit_accounts_user_id ON deposit_accounts(user_id)"),
    # Priority 2: Association tables
    ("idx_disputes_status_created", "CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_disputes_status_created ON disputes(status, created_at DESC)"),
    ("idx_order_logs_order_created", "CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_order_logs_order_created ON order_logs(order_id, created_at ASC)"),
    ("idx_shop_members_shop_user", "CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_shop_members_shop_user ON shop_members(shop_user_id)"),
    ("idx_series_brand_id", "CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_series_brand_id ON series(brand_id)"),
    ("idx_models_series_id", "CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_models_series_id ON models(series_id)"),
    ("idx_audit_logs_created", "CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_audit_logs_created ON audit_logs(created_at DESC)"),
    ("idx_audit_logs_module", "CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_audit_logs_module ON audit_logs(module)"),
    # Priority 3: Auxiliary tables
    ("idx_admin_notifications_target_user_perf", "CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_admin_notifications_target_user ON admin_notifications(target_user_id, created_at DESC)"),
    ("idx_deposit_records_account_created", "CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_deposit_records_account_created ON deposit_records(account_id, created_at DESC)"),
    ("idx_purchase_demands_status_created", "CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_purchase_demands_status_created ON purchase_demands(status, created_at DESC)"),
    ("idx_banners_status_sort", "CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_banners_status_sort ON banners(status, sort_order)"),
]

success = 0
failed = 0
for name, sql in indexes_perf:
    try:
        cur.execute(sql)
        print(f"  OK: {name}")
        success += 1
    except Exception as e:
        print(f"  SKIP: {name} - {e}")
        failed += 1

print(f"\n=== Summary ===")
print(f"Indexes created/verified: {success}")
print(f"Indexes skipped: {failed}")

cur.close()
conn.close()
print("\nDone!")
