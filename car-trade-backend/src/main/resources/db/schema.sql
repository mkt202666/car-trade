-- =====================================================================
-- schema.sql — 增量迁移与补充测试数据
-- 定位：在 init.sql 建表完成后按需执行一次
-- 特点：全部使用 WHERE NOT EXISTS / IF NOT EXISTS，可重复执行，不冲突
-- =====================================================================


-- ---------------------------------------------------------------------
-- 1. 为已存在的车源补充出口国家数据（用于旧数据库兼容）
--    仅在 export_countries 为空时写入，不覆盖已有数据
-- ---------------------------------------------------------------------
UPDATE tc_car_sources SET export_countries = CASE
    WHEN id = 1 THEN 'RU,KZ,AE'
    WHEN id = 2 THEN 'RU,KZ'
    WHEN id = 3 THEN 'AE,AU,SA,SE'
    WHEN id = 4 THEN 'AE,AU'
    WHEN id = 5 THEN 'RU,KZ,AE,AU,SA,SE'
    WHEN id = 6 THEN 'AE,AU,SE'
    ELSE 'RU,KZ'
END WHERE export_countries IS NULL OR export_countries = '';


-- =====================================================================
-- 2. 测试数据（默认密码：123456，BCrypt 已加密）
--    手机号         密码      角色
--    13800138001   123456  诚信车行（车行主）
--    13800138002   123456  车坊张老板
-- =====================================================================


-- ----- 品牌 -----
INSERT INTO tc_brands (id, name, logo_url, first_letter, sort_order, status)
SELECT 1, '大众',   'https://example.com/vw.png',     'D', 1, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM tc_brands WHERE id = 1);

INSERT INTO tc_brands (id, name, logo_url, first_letter, sort_order, status)
SELECT 2, '丰田',   'https://example.com/toyota.png', 'F', 2, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM tc_brands WHERE id = 2);

INSERT INTO tc_brands (id, name, logo_url, first_letter, sort_order, status)
SELECT 3, '奔驰',   'https://example.com/benz.png',   'B', 3, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM tc_brands WHERE id = 3);

INSERT INTO tc_brands (id, name, logo_url, first_letter, sort_order, status)
SELECT 4, '宝马',   'https://example.com/bmw.png',    'B', 4, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM tc_brands WHERE id = 4);

INSERT INTO tc_brands (id, name, logo_url, first_letter, sort_order, status)
SELECT 5, '特斯拉', 'https://example.com/tesla.png',  'T', 5, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM tc_brands WHERE id = 5);


-- ----- 车系 -----
INSERT INTO tc_series (id, brand_id, name, sort_order, status)
SELECT 1, 1, '帕萨特',  1, 'ACTIVE' WHERE NOT EXISTS (SELECT 1 FROM tc_series WHERE id = 1);

INSERT INTO tc_series (id, brand_id, name, sort_order, status)
SELECT 2, 1, '速腾',    2, 'ACTIVE' WHERE NOT EXISTS (SELECT 1 FROM tc_series WHERE id = 2);

INSERT INTO tc_series (id, brand_id, name, sort_order, status)
SELECT 3, 2, '凯美瑞',  1, 'ACTIVE' WHERE NOT EXISTS (SELECT 1 FROM tc_series WHERE id = 3);

INSERT INTO tc_series (id, brand_id, name, sort_order, status)
SELECT 4, 3, 'E级',    1, 'ACTIVE' WHERE NOT EXISTS (SELECT 1 FROM tc_series WHERE id = 4);

INSERT INTO tc_series (id, brand_id, name, sort_order, status)
SELECT 5, 4, '5系',    1, 'ACTIVE' WHERE NOT EXISTS (SELECT 1 FROM tc_series WHERE id = 5);

INSERT INTO tc_series (id, brand_id, name, sort_order, status)
SELECT 6, 5, 'Model Y', 1, 'ACTIVE' WHERE NOT EXISTS (SELECT 1 FROM tc_series WHERE id = 6);


-- ----- 车型 -----
INSERT INTO tc_models (id, series_id, name, year, guide_price, sort_order, status)
SELECT 1, 1, '330TSI 豪华版',    2024, 250000.00, 1, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM tc_models WHERE id = 1);

INSERT INTO tc_models (id, series_id, name, year, guide_price, sort_order, status)
SELECT 2, 2, '280TSI 舒适版',    2024, 180000.00, 1, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM tc_models WHERE id = 2);

INSERT INTO tc_models (id, series_id, name, year, guide_price, sort_order, status)
SELECT 3, 3, '2.5G 豪华版',      2024, 220000.00, 1, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM tc_models WHERE id = 3);

INSERT INTO tc_models (id, series_id, name, year, guide_price, sort_order, status)
SELECT 4, 4, 'E300L 时尚版',     2024, 550000.00, 1, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM tc_models WHERE id = 4);

INSERT INTO tc_models (id, series_id, name, year, guide_price, sort_order, status)
SELECT 5, 5, '530Li 尊享版',     2024, 520000.00, 1, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM tc_models WHERE id = 5);

INSERT INTO tc_models (id, series_id, name, year, guide_price, sort_order, status)
SELECT 6, 6, '长续航全轮驱动版', 2024, 300000.00, 1, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM tc_models WHERE id = 6);


-- ----- 用户（含 BCrypt 密码 123456，可直接登录）-----
INSERT INTO tc_users (id, phone, password, nickname, shop_name, credit_grade, credit_score, deal_count, on_sale_count, certification_status, status)
SELECT 1, '13800138001', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '诚信车行',   '诚信二手车', 'A', 850, 156, 23, 'CERTIFIED', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM tc_users WHERE id = 1);

INSERT INTO tc_users (id, phone, password, nickname, shop_name, credit_grade, credit_score, deal_count, on_sale_count, certification_status, status)
SELECT 2, '13800138002', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '车坊张老板', '精品车坊',   'S', 920, 328, 45, 'CERTIFIED', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM tc_users WHERE id = 2);


-- ----- 车源（出口国家：RU=俄  KZ=哈  AE=阿联酋  AU=澳  SA=南非  SE=东南亚）-----
INSERT INTO tc_car_sources (id, user_id, brand_id, series_id, model_id, title, year, mileage, price, deposit, color, city_code, city_name, energy_type, export_countries, view_count, favorite_count, status, published_at)
SELECT 1, 1, 1, 1, 1, '大众帕萨特 2024款 330TSI 豪华版',      2024, 15000, 188000.00,  5000.00, '黑色', '010', '北京', 'GASOLINE',      'RU,KZ,AE',          1256, 58, 'ACTIVE', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM tc_car_sources WHERE id = 1);

INSERT INTO tc_car_sources (id, user_id, brand_id, series_id, model_id, title, year, mileage, price, deposit, color, city_code, city_name, energy_type, export_countries, view_count, favorite_count, status, published_at)
SELECT 2, 1, 1, 2, 2, '大众速腾 2024款 280TSI 舒适版',         2024,  8000, 138000.00,  3000.00, '银色', '010', '北京', 'GASOLINE',      'RU,KZ',              892, 32, 'ACTIVE', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM tc_car_sources WHERE id = 2);

INSERT INTO tc_car_sources (id, user_id, brand_id, series_id, model_id, title, year, mileage, price, deposit, color, city_code, city_name, energy_type, export_countries, view_count, favorite_count, status, published_at)
SELECT 3, 2, 2, 3, 3, '丰田凯美瑞 2024款 2.5G 豪华版',         2024, 12000, 168000.00,  4000.00, '白色', '020', '上海', 'GASOLINE',      'AE,AU,SA,SE',      2341, 87, 'ACTIVE', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM tc_car_sources WHERE id = 3);

INSERT INTO tc_car_sources (id, user_id, brand_id, series_id, model_id, title, year, mileage, price, deposit, color, city_code, city_name, energy_type, export_countries, view_count, favorite_count, status, published_at)
SELECT 4, 2, 3, 4, 4, '奔驰E级 2024款 E300L 时尚版',           2024,  5000, 458000.00, 10000.00, '黑色', '020', '上海', 'GASOLINE',      'AE,AU',            3567, 124, 'ACTIVE', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM tc_car_sources WHERE id = 4);

INSERT INTO tc_car_sources (id, user_id, brand_id, series_id, model_id, title, year, mileage, price, deposit, color, city_code, city_name, energy_type, export_countries, view_count, favorite_count, status, published_at)
SELECT 5, 1, 4, 5, 5, '宝马5系 2024款 530Li 尊享版',           2024,  8000, 438000.00, 10000.00, '白色', '010', '北京', 'GASOLINE',      'RU,KZ,AE,AU,SA,SE',2891, 98, 'ACTIVE', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM tc_car_sources WHERE id = 5);

INSERT INTO tc_car_sources (id, user_id, brand_id, series_id, model_id, title, year, mileage, price, deposit, color, city_code, city_name, energy_type, export_countries, view_count, favorite_count, status, published_at)
SELECT 6, 2, 5, 6, 6, '特斯拉Model Y 2024款 长续航全轮驱动版', 2024,  3000, 268000.00,  8000.00, '红色', '021', '广州', 'PURE_ELECTRIC','AE,AU,SE',         4567, 156, 'ACTIVE', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM tc_car_sources WHERE id = 6);


-- ----- 车源图片 -----
INSERT INTO tc_car_images (car_id, image_url, image_type, sort_order)
SELECT 1, 'https://images.unsplash.com/photo-1552519507-da3b142c6e3d?w=800', 'EXTERIOR', 1
WHERE NOT EXISTS (SELECT 1 FROM tc_car_images WHERE car_id = 1);

INSERT INTO tc_car_images (car_id, image_url, image_type, sort_order)
SELECT 2, 'https://images.unsplash.com/photo-1549317668-b6ada16ea8b8?w=800', 'EXTERIOR', 1
WHERE NOT EXISTS (SELECT 1 FROM tc_car_images WHERE car_id = 2);

INSERT INTO tc_car_images (car_id, image_url, image_type, sort_order)
SELECT 3, 'https://images.unsplash.com/photo-1621007933163-fec1a6d10b6d?w=800', 'EXTERIOR', 1
WHERE NOT EXISTS (SELECT 1 FROM tc_car_images WHERE car_id = 3);

INSERT INTO tc_car_images (car_id, image_url, image_type, sort_order)
SELECT 4, 'https://images.unsplash.com/photo-1617531653332-bd46c24f2000?w=800', 'EXTERIOR', 1
WHERE NOT EXISTS (SELECT 1 FROM tc_car_images WHERE car_id = 4);

INSERT INTO tc_car_images (car_id, image_url, image_type, sort_order)
SELECT 5, 'https://images.unsplash.com/photo-1555215695-3004980ad54e?w=800', 'EXTERIOR', 1
WHERE NOT EXISTS (SELECT 1 FROM tc_car_images WHERE car_id = 5);

INSERT INTO tc_car_images (car_id, image_url, image_type, sort_order)
SELECT 6, 'https://images.unsplash.com/photo-1617788615603-6ce36e9b65c8?w=800', 'EXTERIOR', 1
WHERE NOT EXISTS (SELECT 1 FROM tc_car_images WHERE car_id = 6);


-- ----- 会员套餐 -----
INSERT INTO tc_member_plans (name, level, price, duration_days, benefits, sort_order, status, created_at)
SELECT '月度会员', 'BRONZE',  99.00,  30, '{"freePublish": 10,  "discountRate": 0.95, "aiAnalysis": true}', 3, 'ACTIVE', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM tc_member_plans WHERE name = '月度会员');

INSERT INTO tc_member_plans (name, level, price, duration_days, benefits, sort_order, status, created_at)
SELECT '季度会员', 'SILVER', 249.00,  90, '{"freePublish": 30,  "discountRate": 0.90, "aiAnalysis": true}', 2, 'ACTIVE', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM tc_member_plans WHERE name = '季度会员');

INSERT INTO tc_member_plans (name, level, price, duration_days, benefits, sort_order, status, created_at)
SELECT '年度会员', 'GOLD',   799.00, 365, '{"freePublish": 100, "discountRate": 0.85, "aiAnalysis": true}', 1, 'ACTIVE', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM tc_member_plans WHERE name = '年度会员');


-- ----- 保证金账户（为已有测试用户创建）-----
UPDATE tc_users SET user_role = 'SHOP' WHERE id IN (1, 2) AND (user_role IS NULL OR user_role = 'PERSONAL');

INSERT INTO tc_deposit_accounts (user_id, balance, frozen_amount, total_deposit, status)
SELECT 1, 50000.00, 23800.00, 73800.00, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM tc_deposit_accounts WHERE user_id = 1);

INSERT INTO tc_deposit_accounts (user_id, balance, frozen_amount, total_deposit, status)
SELECT 2, 30000.00, 19800.00, 49800.00, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM tc_deposit_accounts WHERE user_id = 2);


-- ----- 保证金流水 -----
INSERT INTO tc_deposit_records (account_id, user_id, type, amount, balance_after, remark, status, created_at)
SELECT da.id, da.user_id, 'CHARGE', 50000.00, 50000.00, '初始充值', 'SUCCESS', NOW() - INTERVAL '30 days'
FROM tc_deposit_accounts da WHERE da.user_id = 1
AND NOT EXISTS (SELECT 1 FROM tc_deposit_records WHERE account_id = da.id AND type = 'CHARGE');

INSERT INTO tc_deposit_records (account_id, user_id, type, amount, balance_after, remark, status, created_at)
SELECT da.id, da.user_id, 'FREEZE', -23800.00, 26200.00, '订单保证金冻结', 'SUCCESS', NOW() - INTERVAL '5 days'
FROM tc_deposit_accounts da WHERE da.user_id = 1
AND NOT EXISTS (SELECT 1 FROM tc_deposit_records WHERE account_id = da.id AND type = 'FREEZE');

INSERT INTO tc_deposit_records (account_id, user_id, type, amount, balance_after, remark, status, created_at)
SELECT da.id, da.user_id, 'CHARGE', 30000.00, 30000.00, '初始充值', 'SUCCESS', NOW() - INTERVAL '25 days'
FROM tc_deposit_accounts da WHERE da.user_id = 2
AND NOT EXISTS (SELECT 1 FROM tc_deposit_records WHERE account_id = da.id AND type = 'CHARGE');

INSERT INTO tc_deposit_records (account_id, user_id, type, amount, balance_after, remark, status, created_at)
SELECT da.id, da.user_id, 'FREEZE', -19800.00, 10200.00, '订单保证金冻结', 'SUCCESS', NOW() - INTERVAL '3 days'
FROM tc_deposit_accounts da WHERE da.user_id = 2
AND NOT EXISTS (SELECT 1 FROM tc_deposit_records WHERE account_id = da.id AND type = 'FREEZE');


-- =====================================================================
-- 3. 重置自增序列，避免手动插入后自增冲突（PostgreSQL 专用）
--    setval(seq, value, false) → 当前值设为 value，下次 nextval 返回 value + 1
-- =====================================================================
SELECT setval('brands_id_seq',       (SELECT COALESCE(MAX(id), 0) FROM tc_brands),       false);
SELECT setval('series_id_seq',       (SELECT COALESCE(MAX(id), 0) FROM tc_series),       false);
SELECT setval('models_id_seq',       (SELECT COALESCE(MAX(id), 0) FROM tc_models),       false);
SELECT setval('users_id_seq',        (SELECT COALESCE(MAX(id), 0) FROM tc_users),        false);
SELECT setval('car_sources_id_seq',  (SELECT COALESCE(MAX(id), 0) FROM tc_car_sources),  false);
SELECT setval('car_images_id_seq',   (SELECT COALESCE(MAX(id), 0) FROM tc_car_images),   false);
SELECT setval('member_plans_id_seq', (SELECT COALESCE(MAX(id), 0) FROM tc_member_plans), false);
SELECT setval('cities_id_seq',       (SELECT COALESCE(MAX(id), 0) FROM tc_cities),       false);
