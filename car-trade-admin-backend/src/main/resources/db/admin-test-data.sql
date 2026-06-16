-- =====================================================================
-- 5D好车 Admin 完整测试数据
-- 密码统一: 123456 (BCrypt hash)
-- 使用 ON CONFLICT 防止重复插入
-- =====================================================================

-- 密码哈希常量 (123456)
-- $2a$10$JNgU6aIPmyglHMnFkcVsYedQBy6kjrViSfP7Rbfv22JX2e69ROGPC

-- =====================================================================
-- 1. Banner 数据
-- =====================================================================
INSERT INTO banners (title, image_url, type, link_url, sort_order, status, click_count)
VALUES
    ('5D好车春季大促首发', 'https://images.unsplash.com/photo-1549317661-bd32c8ce0db2?auto=format&fit=crop&q=80&w=800', 'BANNER', '/promotions/spring', 1, 'ACTIVE', 128),
    ('诚信商户招募计划开启', 'https://images.unsplash.com/photo-1600880292203-757bb62b4baf?auto=format&fit=crop&q=80&w=800', 'BANNER', '/merchant-apply', 2, 'ACTIVE', 89),
    ('五一特惠车源直降', 'https://images.unsplash.com/photo-1492144534655-ae79c964c9d7?auto=format&fit=crop&q=80&w=800', 'BANNER', '/promotions/may', 3, 'ACTIVE', 56),
    ('新车源上架提醒', 'https://images.unsplash.com/photo-1583121274602-3e2820c69888?auto=format&fit=crop&q=80&w=800', 'BANNER', '/cars/new', 4, 'ACTIVE', 34),
    ('新用户专享优惠', 'https://images.unsplash.com/photo-1556742049-0cfed4f6a45d?auto=format&fit=crop&q=80&w=800', 'POPUP', '/promotions/new-user', 1, 'ACTIVE', 45),
    ('下载APP领红包', 'https://images.unsplash.com/photo-1607082348824-0a96f2a4b9da?auto=format&fit=crop&q=80&w=800', 'POPUP', '/download', 2, 'ACTIVE', 78)
ON CONFLICT DO NOTHING;

-- =====================================================================
-- 2. Config 数据
-- =====================================================================
INSERT INTO configs (key, content)
VALUES
    ('trade-rules', '5D找车在线交易规范 v1.0\n\n第一条 总则\n为规范二手车在线交易行为，保障买卖双方合法权益，特制定本规范。\n\n第二条 适用范围\n本规范适用于通过5D找车平台进行的所有二手车交易活动。'),
    ('user-agreement', '5D找车用户协议 v1.0\n\n一、服务条款\n欢迎使用5D找车平台。本协议是您与5D好车之间关于使用平台服务的法律协议。'),
    ('privacy', '5D找车隐私条款 v1.0\n\n我们非常重视您的个人信息保护。本隐私政策说明我们如何收集、使用和保护您的个人信息。'),
    ('contract', '在线交易标准合同 v1.0\n\n甲方（卖方）：______\n乙方（买方）：______\n\n第一条 车辆信息\n车辆品牌：______\n车辆型号：______\n车架号：______')
ON CONFLICT (key) DO NOTHING;

-- =====================================================================
-- 3. 车行用户 (users with shop_name)
-- =====================================================================
INSERT INTO users (phone, password, nickname, real_name, avatar_url, shop_name, shop_logo, shop_description, credit_grade, credit_score, deal_count, on_sale_count, certification_status, user_role, status)
SELECT * FROM (VALUES
    ('13800000001', '$2a$10$JNgU6aIPmyglHMnFkcVsYedQBy6kjrViSfP7Rbfv22JX2e69ROGPC', '诚信车行张总', '张伟', 'https://api.dicebear.com/7.x/avataaars/svg?seed=zhang', '诚信精品车行', 'https://images.unsplash.com/photo-1560472354-b33ff0c44a43?w=200', '专注精品二手车15年，诚信经营，品质保证。', 'A', 85, 42, 8, 'CERTIFIED', 'SHOP_OWNER', 'ACTIVE'),
    ('13800000002', '$2a$10$JNgU6aIPmyglHMnFkcVsYedQBy6kjrViSfP7Rbfv22JX2e69ROGPC', '优质车源王姐', '王芳', 'https://api.dicebear.com/7.x/avataaars/svg?seed=wang', '优质车源之家', 'https://images.unsplash.com/photo-1560472354-b33ff0c44a43?w=200', '主营BBA及豪华品牌，车况透明，支持检测。', 'A', 82, 35, 12, 'CERTIFIED', 'SHOP_OWNER', 'ACTIVE'),
    ('13800000003', '$2a$10$JNgU6aIPmyglHMnFkcVsYedQBy6kjrViSfP7Rbfv22JX2e69ROGPC', '速卖二手车李哥', '李强', 'https://api.dicebear.com/7.x/avataaars/svg?seed=li', '速卖二手车行', 'https://images.unsplash.com/photo-1560472354-b33ff0c44a43?w=200', '快速成交，价格公道，服务周到。', 'B', 75, 28, 6, 'CERTIFIED', 'SHOP_OWNER', 'ACTIVE'),
    ('13800000004', '$2a$10$JNgU6aIPmyglHMnFkcVsYedQBy6kjrViSfP7Rbfv22JX2e69ROGPC', '新能源专营赵哥', '赵磊', 'https://api.dicebear.com/7.x/avataaars/svg?seed=zhao', '新能源二手车专卖', 'https://images.unsplash.com/photo-1560472354-b33ff0c44a43?w=200', '专注新能源二手车，特斯拉/比亚迪/蔚来等。', 'A', 88, 20, 10, 'CERTIFIED', 'SHOP_OWNER', 'ACTIVE'),
    ('13800000005', '$2a$10$JNgU6aIPmyglHMnFkcVsYedQBy6kjrViSfP7Rbfv22JX2e69ROGPC', '待认证车行孙总', '孙磊', 'https://api.dicebear.com/7.x/avataaars/svg?seed=sun', '鑫达车行', 'https://images.unsplash.com/photo-1560472354-b33ff0c44a43?w=200', '新入驻车行，主营家用车型。', 'C', 60, 0, 3, 'PENDING', 'SHOP_OWNER', 'ACTIVE'),
    ('13800000006', '$2a$10$JNgU6aIPmyglHMnFkcVsYedQBy6kjrViSfP7Rbfv22JX2e69ROGPC', '已拒绝车行周姐', '周丽', 'https://api.dicebear.com/7.x/avataaars/svg?seed=zhou', '诚信车行', 'https://images.unsplash.com/photo-1560472354-b33ff0c44a43?w=200', '材料不齐全，认证被拒。', 'C', 60, 0, 0, 'REJECTED', 'SHOP_OWNER', 'ACTIVE')
) AS v(phone, password, nickname, real_name, avatar_url, shop_name, shop_logo, shop_description, credit_grade, credit_score, deal_count, on_sale_count, certification_status, user_role, status)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE phone = v.phone);

-- =====================================================================
-- 4. 个人用户 (买家)
-- =====================================================================
INSERT INTO users (phone, password, nickname, real_name, avatar_url, certification_status, user_role, status, credit_grade, credit_score)
SELECT * FROM (VALUES
    ('13900000001', '$2a$10$JNgU6aIPmyglHMnFkcVsYedQBy6kjrViSfP7Rbfv22JX2e69ROGPC', '购车达人小陈', '陈明', 'https://api.dicebear.com/7.x/avataaars/svg?seed=chen', 'CERTIFIED', 'PERSONAL', 'ACTIVE', 'A', 80),
    ('13900000002', '$2a$10$JNgU6aIPmyglHMnFkcVsYedQBy6kjrViSfP7Rbfv22JX2e69ROGPC', '新手买车刘哥', '刘洋', 'https://api.dicebear.com/7.x/avataaars/svg?seed=liuyang', 'CERTIFIED', 'PERSONAL', 'ACTIVE', 'B', 70),
    ('13900000003', '$2a$10$JNgU6aIPmyglHMnFkcVsYedQBy6kjrViSfP7Rbfv22JX2e69ROGPC', '换车一族小吴', '吴敏', 'https://api.dicebear.com/7.x/avataaars/svg?seed=wu', 'CERTIFIED', 'PERSONAL', 'ACTIVE', 'A', 85),
    ('13900000004', '$2a$10$JNgU6aIPmyglHMnFkcVsYedQBy6kjrViSfP7Rbfv22JX2e69ROGPC', '冻结用户测试', '冻结测试', 'https://api.dicebear.com/7.x/avataaars/svg?seed=frozen', 'UNCERTIFIED', 'PERSONAL', 'FROZEN', 'C', 60),
    ('13900000005', '$2a$10$JNgU6aIPmyglHMnFkcVsYedQBy6kjrViSfP7Rbfv22JX2e69ROGPC', '未认证新用户', '新用户', 'https://api.dicebear.com/7.x/avataaars/svg?seed=new', 'UNCERTIFIED', 'PERSONAL', 'ACTIVE', 'C', 60)
) AS v(phone, password, nickname, real_name, avatar_url, certification_status, user_role, status, credit_grade, credit_score)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE phone = v.phone);

-- =====================================================================
-- 5. 车源数据 (car_sources)
-- =====================================================================
-- 需要先确认 brands/series/models 已有数据
-- 这里使用 brand_id=1 (假设为宝马), series_id=1 (3系), model_id=1
INSERT INTO car_sources (user_id, brand_id, series_id, model_id, title, vin, year, mileage, price, color, city_code, city_name, energy_type, transmission, recommended, status, view_count, favorite_count, created_at)
SELECT * FROM (VALUES
    -- 诚信精品车行的车源
    (1, 1, 1, 1, '2022款宝马325Li 豪华套装 准新车', 'WBAJB2109N1K00001', 2022, 23000, 238000.00, '白色', '420100', '武汉', 'GASOLINE', 'AUTOMATIC', true, 'ON_SALE', 1256, 89, NOW() - INTERVAL '5 days'),
    (1, 2, 3, 5, '2021款奔驰C200L 运动版 一手车', 'WDDZF4JB1MA000001', 2021, 35000, 268000.00, '黑色', '420100', '武汉', 'GASOLINE', 'AUTOMATIC', false, 'ON_SALE', 890, 56, NOW() - INTERVAL '3 days'),
    (1, 3, 5, 9, '2023款奥迪A4L 40TFSI 豪华版', 'WAUZZZ8V5PA000001', 2023, 12000, 258000.00, '灰色', '420100', '武汉', 'GASOLINE', 'AUTOMATIC', true, 'ON_SALE', 2100, 134, NOW() - INTERVAL '1 day'),
    -- 优质车源之家的车源
    (2, 1, 2, 3, '2020款宝马530Li xDrive 豪华型', 'WBAJC5C30LF000001', 2020, 48000, 328000.00, '蓝色', '440300', '深圳', 'GASOLINE', 'AUTOMATIC', true, 'ON_SALE', 567, 34, NOW() - INTERVAL '7 days'),
    (2, 4, 7, 13, '2022款特斯拉Model 3 后驱版', '5YJ3E1EA1NF000001', 2022, 18000, 198000.00, '白色', '440300', '深圳', 'PURE_ELECTRIC', 'AUTOMATIC', true, 'ON_SALE', 3400, 210, NOW() - INTERVAL '2 days'),
    (2, 5, 9, 17, '2021款比亚迪汉EV 旗舰型', 'LGXCE6CB1N0000001', 2021, 25000, 168000.00, '红色', '440300', '深圳', 'PURE_ELECTRIC', 'AUTOMATIC', false, 'ON_SALE', 1890, 98, NOW() - INTERVAL '4 days'),
    -- 速卖二手车行的车源
    (3, 6, 11, 21, '2020款丰田凯美瑞 2.5G 豪华版', '4T1B11HK5LU000001', 2020, 42000, 158000.00, '白色', '500100', '重庆', 'GASOLINE', 'AUTOMATIC', false, 'ON_SALE', 780, 45, NOW() - INTERVAL '6 days'),
    (3, 7, 13, 25, '2019款本田雅阁 260TURBO 旗舰版', '1HGCV2F34KA000001', 2019, 55000, 138000.00, '黑色', '500100', '重庆', 'GASOLINE', 'AUTOMATIC', false, 'SOLD', 1200, 67, NOW() - INTERVAL '30 days'),
    -- 新能源专营的车源
    (4, 4, 8, 15, '2023款特斯拉Model Y 长续航版', '7SAYGDE58PF000001', 2023, 8000, 268000.00, '白色', '510100', '成都', 'PURE_ELECTRIC', 'AUTOMATIC', true, 'ON_SALE', 4500, 320, NOW() - INTERVAL '1 day'),
    (4, 5, 10, 19, '2023款比亚迪海豹 650km 后驱版', 'LGXCE4CB5P0000001', 2023, 5000, 188000.00, '蓝色', '510100', '成都', 'PURE_ELECTRIC', 'AUTOMATIC', false, 'ON_SALE', 2800, 180, NOW() - INTERVAL '2 days'),
    (4, 8, 15, 29, '2022款蔚来ES6 75km 运动版', 'LGXNE4CB2N0000001', 2022, 15000, 298000.00, '灰色', '510100', '成都', 'PURE_ELECTRIC', 'AUTOMATIC', false, 'ON_SALE', 1500, 88, NOW() - INTERVAL '5 days'),
    -- 下架/草稿状态的车源
    (1, 1, 1, 2, '2021款宝马320Li 时尚型（已下架）', 'WBAJB2109N1K00002', 2021, 45000, 198000.00, '银色', '420100', '武汉', 'GASOLINE', 'AUTOMATIC', false, 'OFFLINE', 340, 12, NOW() - INTERVAL '15 days'),
    (2, 2, 4, 7, '2020款奔驰E300L 豪华型（草稿）', 'WDDZF4JB1MA000002', 2020, 38000, 358000.00, '黑色', '440300', '深圳', 'GASOLINE', 'AUTOMATIC', false, 'DRAFT', 0, 0, NOW() - INTERVAL '10 days')
) AS v(user_id, brand_id, series_id, model_id, title, vin, year, mileage, price, color, city_code, city_name, energy_type, transmission, status, view_count, favorite_count, created_at)
WHERE NOT EXISTS (SELECT 1 FROM car_sources WHERE vin = v.vin);

-- =====================================================================
-- 6. 订单数据 (orders)
-- =====================================================================
INSERT INTO orders (id, car_id, buyer_id, seller_id, total_price, deposit_amount, buyer_deposit_paid, seller_deposit_paid, status, created_at)
SELECT * FROM (VALUES
    ('ORD20260601001', 8, 7, 3, 138000.00, 13800.00, true, true, 'COMPLETED', NOW() - INTERVAL '25 days'),
    ('ORD20260601002', 4, 8, 2, 328000.00, 32800.00, true, true, 'COMPLETED', NOW() - INTERVAL '20 days'),
    ('ORD20260610001', 1, 7, 1, 238000.00, 23800.00, true, true, 'IN_TRANSIT', NOW() - INTERVAL '5 days'),
    ('ORD20260612001', 5, 9, 2, 198000.00, 19800.00, true, true, 'CONTRACT_DRAFT', NOW() - INTERVAL '3 days'),
    ('ORD20260613001', 9, 8, 4, 268000.00, 26800.00, true, false, 'PENDING_DEPOSIT', NOW() - INTERVAL '2 days'),
    ('ORD20260614001', 6, 7, 2, 168000.00, 16800.00, false, false, 'PENDING_CONFIRM', NOW() - INTERVAL '1 day'),
    ('ORD20260614002', 3, 9, 1, 258000.00, 25800.00, true, true, 'DISPUTE', NOW() - INTERVAL '1 day')
) AS v(id, car_id, buyer_id, seller_id, total_price, deposit_amount, buyer_deposit_paid, seller_deposit_paid, status, created_at)
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE id = v.id);

-- =====================================================================
-- 7. 保证金账户 (deposit_accounts)
-- =====================================================================
INSERT INTO deposit_accounts (user_id, balance, frozen_amount, total_deposit, status)
SELECT * FROM (VALUES
    (1, 15000.00, 23800.00, 50000.00, 'ACTIVE'),
    (2, 8000.00, 19800.00, 30000.00, 'ACTIVE'),
    (3, 5000.00, 13800.00, 25000.00, 'ACTIVE'),
    (4, 20000.00, 26800.00, 60000.00, 'ACTIVE'),
    (7, 12000.00, 23800.00, 40000.00, 'ACTIVE'),
    (8, 3000.00, 32800.00, 45000.00, 'ACTIVE'),
    (9, 1500.00, 16800.00, 20000.00, 'ACTIVE')
) AS v(user_id, balance, frozen_amount, total_deposit, status)
WHERE NOT EXISTS (SELECT 1 FROM deposit_accounts WHERE user_id = v.user_id);

-- =====================================================================
-- 8. 保证金流水 (deposit_records)
-- =====================================================================
INSERT INTO deposit_records (account_id, order_id, type, amount, balance_after, remark, created_at)
SELECT da.id, r.order_id, r.type, r.amount, r.balance_after, r.remark, r.created_at
FROM (VALUES
    (1, 'ORD20260601001', 'RECHARGE', 50000.00, 50000.00, '初始充值', NOW() - INTERVAL '30 days'),
    (1, 'ORD20260601001', 'FREEZE', -13800.00, 36200.00, '订单保证金冻结', NOW() - INTERVAL '25 days'),
    (1, 'ORD20260601001', 'UNFREEZE', 13800.00, 50000.00, '订单完成解冻', NOW() - INTERVAL '20 days'),
    (1, 'ORD20260610001', 'FREEZE', -23800.00, 26200.00, '订单保证金冻结', NOW() - INTERVAL '5 days'),
    (2, 'ORD20260601002', 'RECHARGE', 30000.00, 30000.00, '初始充值', NOW() - INTERVAL '25 days'),
    (2, 'ORD20260601002', 'FREEZE', -32800.00, -2800.00, '订单保证金冻结', NOW() - INTERVAL '20 days'),
    (2, 'ORD20260612001', 'FREEZE', -19800.00, -22600.00, '订单保证金冻结', NOW() - INTERVAL '3 days')
) AS r(account_id, order_id, type, amount, balance_after, remark, created_at)
CROSS JOIN deposit_accounts da WHERE da.user_id = r.account_id;

-- =====================================================================
-- 9. 求购意向 (purchase_demands)
-- =====================================================================
INSERT INTO purchase_demands (user_id, brand_name, series_name, model_name, year_from, year_to, price_min, price_max, mileage_max, city_code, city_name, energy_type, description, status)
SELECT * FROM (VALUES
    (7, '宝马', '3系', '325Li', 2020, 2023, 180000.00, 250000.00, 50000, '420100', '武汉', 'GASOLINE', '想买一台白色宝马325Li，最好2022款，里程3万公里以内。', 'ACTIVE'),
    (8, '特斯拉', 'Model 3', NULL, 2021, 2023, 150000.00, 220000.00, 30000, '440300', '深圳', 'PURE_ELECTRIC', '求购特斯拉Model 3，预算20万以内，希望车况好。', 'ACTIVE'),
    (9, '丰田', '凯美瑞', NULL, 2019, 2022, 100000.00, 160000.00, 60000, '500100', '重庆', 'GASOLINE', '家用代步，凯美瑞即可，价格实惠优先。', 'ACTIVE'),
    (7, '比亚迪', '汉', 'EV', 2021, 2023, 140000.00, 200000.00, 40000, '420100', '武汉', 'PURE_ELECTRIC', '新能源车也看看，比亚迪汉EV不错。', 'ACTIVE'),
    (8, '奔驰', 'C级', 'C200L', 2020, 2022, 200000.00, 280000.00, 40000, '440300', '深圳', 'GASOLINE', '奔驰C200L，黑色优先，车况好。', 'ACTIVE')
) AS v(user_id, brand_name, series_name, model_name, year_from, year_to, price_min, price_max, mileage_max, city_code, city_name, energy_type, description, status)
WHERE NOT EXISTS (SELECT 1 FROM purchase_demands WHERE user_id = v.user_id AND brand_name = v.brand_name);

-- =====================================================================
-- 10. 争议数据 (disputes)
-- =====================================================================
INSERT INTO disputes (order_id, initiator_id, reason, description, status, created_at)
SELECT * FROM (VALUES
    ('ORD20260614002', 9, '车况与描述不符', '车辆实际里程与卖家描述相差较大，要求退款。', 'OPEN', NOW() - INTERVAL '1 day'),
    ('ORD20260610001', 7, '交车延迟', '约定3天交车，已超过5天仍未交车。', 'IN_PROGRESS', NOW() - INTERVAL '3 days')
) AS v(order_id, initiator_id, reason, description, status, created_at)
WHERE NOT EXISTS (SELECT 1 FROM disputes WHERE order_id = v.order_id AND initiator_id = v.initiator_id);

-- =====================================================================
-- 11. 车源图片 (car_images) - 为主要车源添加图片
-- =====================================================================
INSERT INTO car_images (car_id, image_url, image_type, sort_order)
SELECT ci.car_id, ci.image_url, ci.image_type, ci.sort_order
FROM (VALUES
    (1, 'https://images.unsplash.com/photo-1555215695-3004980ad54e?w=800', 'EXTERIOR', 1),
    (1, 'https://images.unsplash.com/photo-1549317661-bd32c8ce0db2?w=800', 'INTERIOR', 2),
    (2, 'https://images.unsplash.com/photo-1618843479313-40f8afb4b4d8?w=800', 'EXTERIOR', 1),
    (3, 'https://images.unsplash.com/photo-1606664515524-ed2f786a0bd6?w=800', 'EXTERIOR', 1),
    (5, 'https://images.unsplash.com/photo-1560958089-b8a1929cea89?w=800', 'EXTERIOR', 1),
    (9, 'https://images.unsplash.com/photo-1593941707882-a5bba14938c7?w=800', 'EXTERIOR', 1),
    (10, 'https://images.unsplash.com/photo-1619767886558-efdc259cde1a?w=800', 'EXTERIOR', 1)
) AS ci(car_id, image_url, image_type, sort_order)
WHERE NOT EXISTS (SELECT 1 FROM car_images WHERE car_id = ci.car_id AND image_type = ci.image_type);

-- =====================================================================
-- 12. 操作审计日志 (audit_logs) - 最近的操作记录
-- =====================================================================
INSERT INTO audit_logs (admin_id, admin_name, module, action, target_type, target_id, description, result, created_at)
SELECT * FROM (VALUES
    (1, '管理员A', 'USER', 'QUERY', 'user', '13800000001', '查询用户详情', 'SUCCESS', NOW() - INTERVAL '1 hour'),
    (1, '管理员A', 'SHOP', 'APPROVE', 'shop', '5', '认证审核通过: 鑫达车行', 'SUCCESS', NOW() - INTERVAL '2 hours'),
    (1, '管理员A', 'CAR', 'UPDATE_STATUS', 'car_source', '1', '车源上架操作', 'SUCCESS', NOW() - INTERVAL '3 hours'),
    (1, '管理员A', 'ORDER', 'FORCE_CANCEL', 'order', 'ORD20260614002', '管理员强制取消订单', 'SUCCESS', NOW() - INTERVAL '5 hours'),
    (1, '管理员A', 'BANNER', 'CREATE', 'banner', '3', '创建Banner: 五一特惠车源直降', 'SUCCESS', NOW() - INTERVAL '1 day')
) AS v(admin_id, admin_name, module, action, target_type, target_id, description, result, created_at)
WHERE NOT EXISTS (SELECT 1 FROM audit_logs WHERE target_id = v.target_id AND action = v.action);

-- =====================================================================
-- 13. 消息通知 (messages) - 用户消息
-- =====================================================================
INSERT INTO messages (user_id, sender_id, type, title, content, is_read, related_id, related_type, created_at)
SELECT * FROM (VALUES
    (7, NULL, 'SYSTEM', '欢迎加入5D好车', '尊敬的用户，欢迎您注册5D好车平台！', false, NULL, NULL, NOW() - INTERVAL '30 days'),
    (1, NULL, 'TRADE', '订单状态更新', '您的订单 ORD20260610001 已进入运输阶段。', false, 'ORD20260610001', 'order', NOW() - INTERVAL '5 days'),
    (1, NULL, 'DEPOSIT_WARNING', '保证金余额提醒', '您的保证金账户余额较低，请及时充值。', false, NULL, NULL, NOW() - INTERVAL '2 days'),
    (9, NULL, 'TRADE', '纠纷通知', '您发起的订单 ORD20260614002 纠纷已受理，请耐心等待处理。', false, 'ORD20260614002', 'order', NOW() - INTERVAL '1 day'),
    (8, NULL, 'SYSTEM', '平台公告', '5D好车端午节活动即将开启，敬请期待！', true, NULL, NULL, NOW() - INTERVAL '3 days')
) AS v(user_id, sender_id, type, title, content, is_read, related_id, related_type, created_at)
WHERE NOT EXISTS (SELECT 1 FROM messages WHERE user_id = v.user_id AND title = v.title);

-- =====================================================================
-- End of script
-- =====================================================================
