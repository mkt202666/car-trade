-- 数据库迁移：添加新字段

-- 用户表：确保 password 字段存在（用于密码登录、BCrypt 加密存储）
ALTER TABLE users ADD COLUMN IF NOT EXISTS password VARCHAR(100);

-- 用户表：通知订阅设置（JSON格式，存储各通知类型的开关状态）
ALTER TABLE users ADD COLUMN IF NOT EXISTS notification_settings JSONB DEFAULT '{"system":true,"auto_promotion":true,"order":true,"contract":true,"deposit":true,"shop":true}';

-- 消息表：确保 sender_id 字段存在（发送人ID，系统消息可为 NULL）
ALTER TABLE messages ADD COLUMN IF NOT EXISTS sender_id BIGINT;

-- 用户优惠券表：确保 received_at 字段存在（领取时间）
ALTER TABLE user_coupons ADD COLUMN IF NOT EXISTS received_at TIMESTAMP;

-- 用户会员表：确保 end_at 字段存在（结束时间/退款标记）
ALTER TABLE user_membership ADD COLUMN IF NOT EXISTS end_at TIMESTAMP;

-- 聊天消息表：确保整张表存在（实时聊天功能）
CREATE TABLE IF NOT EXISTS chat_messages (
    id              BIGSERIAL PRIMARY KEY,
    conversation_id BIGINT NOT NULL REFERENCES chat_conversations(id),
    sender_id       BIGINT,
    content         TEXT,
    message_type    VARCHAR(20),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_chat_messages_conv ON chat_messages(conversation_id);
CREATE INDEX IF NOT EXISTS idx_chat_messages_sender ON chat_messages(sender_id);
CREATE INDEX IF NOT EXISTS idx_chat_messages_created ON chat_messages(created_at DESC);

-- 车源表新字段
ALTER TABLE car_sources ADD COLUMN IF NOT EXISTS vin VARCHAR(50);
ALTER TABLE car_sources ADD COLUMN IF NOT EXISTS transmission VARCHAR(20);
ALTER TABLE car_sources ADD COLUMN IF NOT EXISTS pricing_type VARCHAR(20) DEFAULT 'FIXED';
ALTER TABLE car_sources ADD COLUMN IF NOT EXISTS starting_price DECIMAL(12,2);
ALTER TABLE car_sources ADD COLUMN IF NOT EXISTS ceiling_price DECIMAL(12,2);
ALTER TABLE car_sources ADD COLUMN IF NOT EXISTS bid_increment DECIMAL(10,2);
ALTER TABLE car_sources ADD COLUMN IF NOT EXISTS inspection_report_type VARCHAR(20);
ALTER TABLE car_sources ADD COLUMN IF NOT EXISTS inspection_report_url VARCHAR(500);
ALTER TABLE car_sources ADD COLUMN IF NOT EXISTS certificate_materials JSONB;
ALTER TABLE car_sources ADD COLUMN IF NOT EXISTS support_lock_negotiation BOOLEAN DEFAULT FALSE;
ALTER TABLE car_sources ADD COLUMN IF NOT EXISTS ai_auto_promote BOOLEAN DEFAULT FALSE;
ALTER TABLE car_sources ADD COLUMN IF NOT EXISTS is_draft BOOLEAN DEFAULT FALSE;
ALTER TABLE car_sources ADD COLUMN IF NOT EXISTS video_url VARCHAR(500);

-- 用户表新字段
ALTER TABLE users ADD COLUMN IF NOT EXISTS shop_logo VARCHAR(500);
ALTER TABLE users ADD COLUMN IF NOT EXISTS shop_description TEXT;

-- 订单表新字段
ALTER TABLE orders ADD COLUMN IF NOT EXISTS contract_content TEXT;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS contract_submitted BOOLEAN DEFAULT FALSE;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS contract_submitted_at TIMESTAMP;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS contract_confirmed BOOLEAN DEFAULT FALSE;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS contract_confirmed_at TIMESTAMP;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS terminate_count INTEGER DEFAULT 0;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS terminate_limit INTEGER DEFAULT 3;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS terminate_reason VARCHAR(200);
ALTER TABLE orders ADD COLUMN IF NOT EXISTS last_terminate_at TIMESTAMP;

-- 1. 用户表
CREATE TABLE IF NOT EXISTS users (
    id              BIGSERIAL PRIMARY KEY,
    phone           VARCHAR(20) NOT NULL UNIQUE,
    password        VARCHAR(100),
    nickname        VARCHAR(50),
    real_name       VARCHAR(50),
    avatar_url      VARCHAR(500),
    shop_name       VARCHAR(100),
    credit_grade    VARCHAR(10) DEFAULT 'C',
    credit_score    INTEGER DEFAULT 60,
    deal_count      INTEGER DEFAULT 0,
    on_sale_count   INTEGER DEFAULT 0,
    view_count      BIGINT DEFAULT 0,
    view_count_today INTEGER DEFAULT 0,
    message_count   BIGINT DEFAULT 0,
    message_count_today INTEGER DEFAULT 0,
    follower_count  INTEGER DEFAULT 0,
    follower_count_today INTEGER DEFAULT 0,
    member_expire_at TIMESTAMP,
    certification_status VARCHAR(20) DEFAULT 'UNCERTIFIED',
    status          VARCHAR(20) DEFAULT 'ACTIVE',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at      TIMESTAMP
);

-- 2. 汽车品牌表
CREATE TABLE IF NOT EXISTS brands (
    id              SERIAL PRIMARY KEY,
    name            VARCHAR(50) NOT NULL,
    logo_url        VARCHAR(500),
    first_letter    VARCHAR(1),
    sort_order      INTEGER DEFAULT 0,
    status          VARCHAR(20) DEFAULT 'ACTIVE',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. 车系表
CREATE TABLE IF NOT EXISTS series (
    id              SERIAL PRIMARY KEY,
    brand_id        INTEGER NOT NULL REFERENCES brands(id),
    name            VARCHAR(50) NOT NULL,
    sort_order      INTEGER DEFAULT 0,
    status          VARCHAR(20) DEFAULT 'ACTIVE',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. 车型表
CREATE TABLE IF NOT EXISTS models (
    id              SERIAL PRIMARY KEY,
    series_id       INTEGER NOT NULL REFERENCES series(id),
    name            VARCHAR(100) NOT NULL,
    year            INTEGER,
    guide_price     DECIMAL(12,2),
    sort_order      INTEGER DEFAULT 0,
    status          VARCHAR(20) DEFAULT 'ACTIVE',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. 车源表
CREATE TABLE IF NOT EXISTS car_sources (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id),
    brand_id        INTEGER NOT NULL,
    series_id       INTEGER NOT NULL,
    model_id        INTEGER NOT NULL,
    title           VARCHAR(200),
    vin             VARCHAR(50),  -- 车架号
    year            INTEGER,
    mileage         INTEGER,
    price           DECIMAL(12,2),
    pricing_type    VARCHAR(20) DEFAULT 'FIXED',  -- FIXED:一口价, AUCTION:拍卖
    starting_price  DECIMAL(12,2),  -- 起拍价
    ceiling_price   DECIMAL(12,2),  -- 封顶价
    bid_increment   DECIMAL(10,2),  -- 加价幅度
    deposit         DECIMAL(10,2),
    color           VARCHAR(20),
    city_code       VARCHAR(20),
    city_name       VARCHAR(50),
    energy_type     VARCHAR(20),
    transmission    VARCHAR(20),  -- 变速箱类型
    usage_type      VARCHAR(20),
    owner_type      VARCHAR(20),
    is_mortgaged    BOOLEAN DEFAULT FALSE,
    is_inherited    BOOLEAN DEFAULT FALSE,
    registration_date DATE,
    insurance_expiry DATE,
    inspection_expiry DATE,
    production_date VARCHAR(10),
    key_count       INTEGER,
    description     TEXT,
    inspection_report_type VARCHAR(20),  -- LINK:链接, FILE:文件
    inspection_report_url VARCHAR(500),  -- 检测报告链接/文件URL
    certificate_materials JSONB,  -- 证件材料JSON
    support_lock_negotiation BOOLEAN DEFAULT FALSE,  -- 支持锁车洽谈
    ai_auto_promote BOOLEAN DEFAULT FALSE,  -- AI自动推广
    is_draft        BOOLEAN DEFAULT FALSE,  -- 是否草稿
    auction_status  VARCHAR(20),
    auction_end_time TIMESTAMP,
    view_count      BIGINT DEFAULT 0,
    favorite_count  INTEGER DEFAULT 0,
    status          VARCHAR(20) DEFAULT 'ACTIVE',
    published_at    TIMESTAMP,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at      TIMESTAMP,
    export_countries VARCHAR(100)
);

-- 6. 车源图片表
CREATE TABLE IF NOT EXISTS car_images (
    id              BIGSERIAL PRIMARY KEY,
    car_id          BIGINT NOT NULL REFERENCES car_sources(id),
    image_url       VARCHAR(500) NOT NULL,
    image_type      VARCHAR(20),
    sort_order      INTEGER DEFAULT 0,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 7. 车源标签表
CREATE TABLE IF NOT EXISTS car_tags (
    id              BIGSERIAL PRIMARY KEY,
    car_id          BIGINT NOT NULL REFERENCES car_sources(id),
    tag_type        VARCHAR(20) NOT NULL,
    tag_value       VARCHAR(50),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 8. 车辆检测报告表
CREATE TABLE IF NOT EXISTS car_inspections (
    id              BIGSERIAL PRIMARY KEY,
    car_id          BIGINT NOT NULL REFERENCES car_sources(id),
    overall_condition VARCHAR(20),
    paint           VARCHAR(20),
    structure       VARCHAR(20),
    engine          VARCHAR(20),
    transmission    VARCHAR(20),
    transfer_count  INTEGER,
    mileage_type    VARCHAR(20),
    description     TEXT,
    abnormal_photos JSONB,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 9. 订单表
CREATE TABLE IF NOT EXISTS orders (
    id              VARCHAR(32) PRIMARY KEY,
    car_id          BIGINT NOT NULL REFERENCES car_sources(id),
    buyer_id        BIGINT NOT NULL REFERENCES users(id),
    seller_id       BIGINT NOT NULL REFERENCES users(id),
    total_price     DECIMAL(12,2) NOT NULL,
    deposit_amount  DECIMAL(10,2),
    buyer_deposit_paid BOOLEAN DEFAULT FALSE,
    buyer_deposit_paid_at TIMESTAMP,
    seller_deposit_paid BOOLEAN DEFAULT FALSE,
    seller_deposit_paid_at TIMESTAMP,
    status          VARCHAR(20) DEFAULT 'PENDING_CONFIRM',
    contract_no     VARCHAR(32),
    contract_content TEXT,  -- 合同内容
    contract_submitted BOOLEAN DEFAULT FALSE,  -- 合同是否已提交
    contract_submitted_at TIMESTAMP,  -- 合同提交时间
    contract_confirmed BOOLEAN DEFAULT FALSE,  -- 合同是否已确认
    contract_confirmed_at TIMESTAMP,  -- 合同确认时间
    terminate_count INTEGER DEFAULT 0,  -- 终止交易次数
    terminate_limit INTEGER DEFAULT 3,  -- 每日终止交易限制
    terminate_reason VARCHAR(200),  -- 终止交易原因
    last_terminate_at TIMESTAMP,  -- 最后终止交易时间
    remark          TEXT,
    cancel_reason   VARCHAR(200),
    completed_at    TIMESTAMP,
    cancelled_at    TIMESTAMP,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 10. 订单车况信息表
CREATE TABLE IF NOT EXISTS order_inspections (
    id              BIGSERIAL PRIMARY KEY,
    order_id        VARCHAR(32) NOT NULL REFERENCES orders(id),
    overall_condition VARCHAR(20),
    paint           VARCHAR(20),
    structure       VARCHAR(20),
    engine          VARCHAR(20),
    transmission    VARCHAR(20),
    transfer_count  INTEGER,
    mileage_type    VARCHAR(20),
    description     TEXT,
    abnormal_photos JSONB,
    materials       JSONB,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 11. 订单日志表
CREATE TABLE IF NOT EXISTS order_logs (
    id              BIGSERIAL PRIMARY KEY,
    order_id        VARCHAR(32) NOT NULL REFERENCES orders(id),
    action          VARCHAR(50) NOT NULL,
    operator_id     BIGINT,
    operator_name   VARCHAR(50),
    remark          TEXT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 12. 争议表
CREATE TABLE IF NOT EXISTS disputes (
    id              BIGSERIAL PRIMARY KEY,
    order_id        VARCHAR(32) NOT NULL REFERENCES orders(id),
    initiator_id    BIGINT NOT NULL REFERENCES users(id),
    reason          VARCHAR(500),
    description     TEXT,
    evidence        JSONB,
    status          VARCHAR(20) DEFAULT 'PENDING',
    result          VARCHAR(20),
    handler_id      BIGINT,
    handled_at      TIMESTAMP,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 13. 电子合同表
CREATE TABLE IF NOT EXISTS contracts (
    id              BIGSERIAL PRIMARY KEY,
    order_id        VARCHAR(32) NOT NULL REFERENCES orders(id),
    contract_no     VARCHAR(32) NOT NULL UNIQUE,
    template_id     VARCHAR(50),
    title           VARCHAR(200),
    content         TEXT,
    buyer_id        BIGINT NOT NULL REFERENCES users(id),
    seller_id       BIGINT NOT NULL REFERENCES users(id),
    buyer_signed    BOOLEAN DEFAULT FALSE,
    buyer_signed_at TIMESTAMP,
    seller_signed   BOOLEAN DEFAULT FALSE,
    seller_signed_at TIMESTAMP,
    status          VARCHAR(20) DEFAULT 'DRAFT',
    file_url        VARCHAR(500),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 14. 保证金账户表
CREATE TABLE IF NOT EXISTS deposit_accounts (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL UNIQUE REFERENCES users(id),
    balance         DECIMAL(10,2) DEFAULT 0,
    frozen_amount   DECIMAL(10,2) DEFAULT 0,
    total_deposit   DECIMAL(10,2) DEFAULT 0,
    status          VARCHAR(20) DEFAULT 'ACTIVE',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 15. 保证金流水表
CREATE TABLE IF NOT EXISTS deposit_records (
    id              BIGSERIAL PRIMARY KEY,
    account_id      BIGINT NOT NULL REFERENCES deposit_accounts(id),
    order_id        VARCHAR(32),
    type            VARCHAR(20) NOT NULL,
    amount          DECIMAL(10,2) NOT NULL,
    balance_after   DECIMAL(10,2),
    remark          VARCHAR(200),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 16. 信用账户表
CREATE TABLE IF NOT EXISTS credit_accounts (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL UNIQUE REFERENCES users(id),
    credit_limit    DECIMAL(12,2) DEFAULT 0,
    used_amount     DECIMAL(12,2) DEFAULT 0,
    available_amount DECIMAL(12,2) DEFAULT 0,
    status          VARCHAR(20) DEFAULT 'ACTIVE',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 17. 消息表
CREATE TABLE IF NOT EXISTS messages (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id),
    sender_id       BIGINT,
    type            VARCHAR(20) NOT NULL,
    title           VARCHAR(200),
    content         TEXT,
    is_read         BOOLEAN DEFAULT FALSE,
    related_id      VARCHAR(50),
    related_type    VARCHAR(20),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 18. 用户收藏表
CREATE TABLE IF NOT EXISTS user_favorites (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id),
    car_id          BIGINT NOT NULL REFERENCES car_sources(id),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, car_id)
);

-- 19. 用户关注表
CREATE TABLE IF NOT EXISTS user_follows (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id),
    follow_user_id  BIGINT NOT NULL REFERENCES users(id),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, follow_user_id)
);

-- 20. 浏览记录表
CREATE TABLE IF NOT EXISTS browsing_history (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id),
    car_id          BIGINT NOT NULL REFERENCES car_sources(id),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 21. 车行成员表
CREATE TABLE IF NOT EXISTS shop_members (
    id              BIGSERIAL PRIMARY KEY,
    shop_user_id    BIGINT NOT NULL REFERENCES users(id),
    member_user_id  BIGINT NOT NULL REFERENCES users(id),
    role            VARCHAR(20) DEFAULT 'MEMBER',
    status          VARCHAR(20) DEFAULT 'PENDING',
    applied_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    approved_at     TIMESTAMP,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(shop_user_id, member_user_id)
);

-- 22. 优惠券定义表
CREATE TABLE IF NOT EXISTS coupons (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    type            VARCHAR(20) NOT NULL,
    value           DECIMAL(10,2) NOT NULL,
    min_amount      DECIMAL(10,2) DEFAULT 0,
    total_count     INTEGER DEFAULT 0,
    remain_count    INTEGER DEFAULT 0,
    start_at        TIMESTAMP,
    end_at          TIMESTAMP,
    status          VARCHAR(20) DEFAULT 'ACTIVE',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 23. 用户优惠券表
CREATE TABLE IF NOT EXISTS user_coupons (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id),
    coupon_id       BIGINT NOT NULL REFERENCES coupons(id),
    used_at         TIMESTAMP,
    order_id        VARCHAR(32),
    status          VARCHAR(20) DEFAULT 'UNUSED',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 24. 会员方案表
CREATE TABLE IF NOT EXISTS member_plans (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(50) NOT NULL,
    level           VARCHAR(20) NOT NULL,
    price           DECIMAL(10,2) NOT NULL,
    duration_days   INTEGER NOT NULL,
    benefits        JSONB,
    sort_order      INTEGER DEFAULT 0,
    status          VARCHAR(20) DEFAULT 'ACTIVE',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 25. 用户会员表
CREATE TABLE IF NOT EXISTS user_membership (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id),
    plan_id         BIGINT NOT NULL REFERENCES member_plans(id),
    level           VARCHAR(20) NOT NULL,
    start_at        TIMESTAMP NOT NULL,
    expire_at       TIMESTAMP NOT NULL,
    status          VARCHAR(20) DEFAULT 'ACTIVE',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 26. 在线客服工单表
CREATE TABLE IF NOT EXISTS customer_service_tickets (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id),
    title           VARCHAR(200),
    category        VARCHAR(50),
    status          VARCHAR(20) DEFAULT 'PENDING',
    priority        VARCHAR(20) DEFAULT 'NORMAL',
    handler_id      BIGINT,
    handled_at      TIMESTAMP,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 27. 聊天会话表
CREATE TABLE IF NOT EXISTS chat_conversations (
    id              BIGSERIAL PRIMARY KEY,
    type            VARCHAR(20) DEFAULT 'SINGLE',
    related_order_id VARCHAR(32),
    last_message    TEXT,
    last_message_at TIMESTAMP,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 28. 聊天会话成员表
CREATE TABLE IF NOT EXISTS chat_conversation_members (
    id              BIGSERIAL PRIMARY KEY,
    conversation_id BIGINT NOT NULL REFERENCES chat_conversations(id),
    user_id         BIGINT NOT NULL REFERENCES users(id),
    unread_count    INTEGER DEFAULT 0,
    last_read_at    TIMESTAMP,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(conversation_id, user_id)
);

-- 确保已存在的数据库也能获取新列
ALTER TABLE car_sources ADD COLUMN IF NOT EXISTS export_countries VARCHAR(100);

-- 为已存在的车源补充出口国家数据（用于旧数据库兼容）
UPDATE car_sources SET export_countries = CASE
    WHEN id = 1 THEN 'RU,KZ,AE'
    WHEN id = 2 THEN 'RU,KZ'
    WHEN id = 3 THEN 'AE,AU,SA,SE'
    WHEN id = 4 THEN 'AE,AU'
    WHEN id = 5 THEN 'RU,KZ,AE,AU,SA,SE'
    WHEN id = 6 THEN 'AE,AU,SE'
    ELSE 'RU,KZ'
END WHERE export_countries IS NULL OR export_countries = '';

-- ============== 测试数据 ==============

-- 插入测试品牌数据（如果不存在）
INSERT INTO brands (id, name, logo_url, first_letter, sort_order, status)
SELECT 1, '大众', 'https://example.com/vw.png', 'D', 1, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM brands WHERE id = 1);

INSERT INTO brands (id, name, logo_url, first_letter, sort_order, status)
SELECT 2, '丰田', 'https://example.com/toyota.png', 'F', 2, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM brands WHERE id = 2);

INSERT INTO brands (id, name, logo_url, first_letter, sort_order, status)
SELECT 3, '奔驰', 'https://example.com/benz.png', 'B', 3, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM brands WHERE id = 3);

INSERT INTO brands (id, name, logo_url, first_letter, sort_order, status)
SELECT 4, '宝马', 'https://example.com/bmw.png', 'B', 4, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM brands WHERE id = 4);

INSERT INTO brands (id, name, logo_url, first_letter, sort_order, status)
SELECT 5, '特斯拉', 'https://example.com/tesla.png', 'T', 5, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM brands WHERE id = 5);

-- 插入测试车系
INSERT INTO series (id, brand_id, name, sort_order, status)
SELECT 1, 1, '帕萨特', 1, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM series WHERE id = 1);

INSERT INTO series (id, brand_id, name, sort_order, status)
SELECT 2, 1, '速腾', 2, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM series WHERE id = 2);

INSERT INTO series (id, brand_id, name, sort_order, status)
SELECT 3, 2, '凯美瑞', 1, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM series WHERE id = 3);

INSERT INTO series (id, brand_id, name, sort_order, status)
SELECT 4, 3, 'E级', 1, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM series WHERE id = 4);

INSERT INTO series (id, brand_id, name, sort_order, status)
SELECT 5, 4, '5系', 1, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM series WHERE id = 5);

INSERT INTO series (id, brand_id, name, sort_order, status)
SELECT 6, 5, 'Model Y', 1, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM series WHERE id = 6);

-- 插入测试车型
INSERT INTO models (id, series_id, name, year, guide_price, sort_order, status)
SELECT 1, 1, '330TSI 豪华版', 2024, 250000.00, 1, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM models WHERE id = 1);

INSERT INTO models (id, series_id, name, year, guide_price, sort_order, status)
SELECT 2, 2, '280TSI 舒适版', 2024, 180000.00, 1, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM models WHERE id = 2);

INSERT INTO models (id, series_id, name, year, guide_price, sort_order, status)
SELECT 3, 3, '2.5G 豪华版', 2024, 220000.00, 1, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM models WHERE id = 3);

INSERT INTO models (id, series_id, name, year, guide_price, sort_order, status)
SELECT 4, 4, 'E300L 时尚版', 2024, 550000.00, 1, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM models WHERE id = 4);

INSERT INTO models (id, series_id, name, year, guide_price, sort_order, status)
SELECT 5, 5, '530Li 尊享版', 2024, 520000.00, 1, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM models WHERE id = 5);

INSERT INTO models (id, series_id, name, year, guide_price, sort_order, status)
SELECT 6, 6, '长续航全轮驱动版', 2024, 300000.00, 1, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM models WHERE id = 6);

-- 插入测试用户（发布者）
INSERT INTO users (id, phone, nickname, shop_name, credit_grade, credit_score, deal_count, on_sale_count, certification_status, status)
SELECT 1, '13800138001', '诚信车行', '诚信二手车', 'A', 850, 156, 23, 'CERTIFIED', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = 1);

INSERT INTO users (id, phone, nickname, shop_name, credit_grade, credit_score, deal_count, on_sale_count, certification_status, status)
SELECT 2, '13800138002', '车坊张老板', '精品车坊', 'S', 920, 328, 45, 'CERTIFIED', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = 2);

-- 插入测试车源（出口国家：RU=俄罗斯, KZ=哈萨克斯坦, AE=阿联酋, AU=澳大利亚, SA=南非, SE=东南亚）
INSERT INTO car_sources (id, user_id, brand_id, series_id, model_id, title, year, mileage, price, deposit, color, city_code, city_name, energy_type, export_countries, view_count, favorite_count, status, published_at)
SELECT 1, 1, 1, 1, 1, '大众帕萨特 2024款 330TSI 豪华版', 2024, 15000, 188000.00, 5000.00, '黑色', '010', '北京', 'GASOLINE', 'RU,KZ,AE', 1256, 58, 'ACTIVE', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM car_sources WHERE id = 1);

INSERT INTO car_sources (id, user_id, brand_id, series_id, model_id, title, year, mileage, price, deposit, color, city_code, city_name, energy_type, export_countries, view_count, favorite_count, status, published_at)
SELECT 2, 1, 2, 2, 2, '大众速腾 2024款 280TSI 舒适版', 2024, 8000, 138000.00, 3000.00, '银色', '010', '北京', 'GASOLINE', 'RU,KZ', 892, 32, 'ACTIVE', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM car_sources WHERE id = 2);

INSERT INTO car_sources (id, user_id, brand_id, series_id, model_id, title, year, mileage, price, deposit, color, city_code, city_name, energy_type, export_countries, view_count, favorite_count, status, published_at)
SELECT 3, 2, 3, 3, 3, '丰田凯美瑞 2024款 2.5G 豪华版', 2024, 12000, 168000.00, 4000.00, '白色', '020', '上海', 'GASOLINE', 'AE,AU,SA,SE', 2341, 87, 'ACTIVE', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM car_sources WHERE id = 3);

INSERT INTO car_sources (id, user_id, brand_id, series_id, model_id, title, year, mileage, price, deposit, color, city_code, city_name, energy_type, export_countries, view_count, favorite_count, status, published_at)
SELECT 4, 2, 4, 4, 4, '奔驰E级 2024款 E300L 时尚版', 2024, 5000, 458000.00, 10000.00, '黑色', '020', '上海', 'GASOLINE', 'AE,AU', 3567, 124, 'ACTIVE', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM car_sources WHERE id = 4);

INSERT INTO car_sources (id, user_id, brand_id, series_id, model_id, title, year, mileage, price, deposit, color, city_code, city_name, energy_type, export_countries, view_count, favorite_count, status, published_at)
SELECT 5, 1, 5, 5, 5, '宝马5系 2024款 530Li 尊享版', 2024, 8000, 438000.00, 10000.00, '白色', '010', '北京', 'GASOLINE', 'RU,KZ,AE,AU,SA,SE', 2891, 98, 'ACTIVE', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM car_sources WHERE id = 5);

INSERT INTO car_sources (id, user_id, brand_id, series_id, model_id, title, year, mileage, price, deposit, color, city_code, city_name, energy_type, export_countries, view_count, favorite_count, status, published_at)
SELECT 6, 2, 5, 6, 6, '特斯拉Model Y 2024款 长续航全轮驱动版', 2024, 3000, 268000.00, 8000.00, '红色', '021', '广州', 'PURE_ELECTRIC', 'AE,AU,SE', 4567, 156, 'ACTIVE', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM car_sources WHERE id = 6);

-- 插入测试车源图片
INSERT INTO car_images (car_id, image_url, image_type, sort_order)
SELECT 1, 'https://images.unsplash.com/photo-1552519507-da3b142c6e3d?w=800', 'EXTERIOR', 1
WHERE NOT EXISTS (SELECT 1 FROM car_images WHERE car_id = 1);

INSERT INTO car_images (car_id, image_url, image_type, sort_order)
SELECT 2, 'https://images.unsplash.com/photo-1549317668-b6ada16ea8b8?w=800', 'EXTERIOR', 1
WHERE NOT EXISTS (SELECT 1 FROM car_images WHERE car_id = 2);

INSERT INTO car_images (car_id, image_url, image_type, sort_order)
SELECT 3, 'https://images.unsplash.com/photo-1621007933163-fec1a6d10b6d?w=800', 'EXTERIOR', 1
WHERE NOT EXISTS (SELECT 1 FROM car_images WHERE car_id = 3);

INSERT INTO car_images (car_id, image_url, image_type, sort_order)
SELECT 4, 'https://images.unsplash.com/photo-1617531653332-bd46c24f2000?w=800', 'EXTERIOR', 1
WHERE NOT EXISTS (SELECT 1 FROM car_images WHERE car_id = 4);

INSERT INTO car_images (car_id, image_url, image_type, sort_order)
SELECT 5, 'https://images.unsplash.com/photo-1555215695-3004980ad54e?w=800', 'EXTERIOR', 1
WHERE NOT EXISTS (SELECT 1 FROM car_images WHERE car_id = 5);

INSERT INTO car_images (car_id, image_url, image_type, sort_order)
SELECT 6, 'https://images.unsplash.com/photo-1617788615603-6ce36e9b65c8?w=800', 'EXTERIOR', 1
WHERE NOT EXISTS (SELECT 1 FROM car_images WHERE car_id = 6);

-- 重置自增序列，避免主键冲突
SELECT setval('brands_id_seq', (SELECT COALESCE(MAX(id), 0) FROM brands));
SELECT setval('series_id_seq', (SELECT COALESCE(MAX(id), 0) FROM series));
SELECT setval('models_id_seq', (SELECT COALESCE(MAX(id), 0) FROM models));
SELECT setval('users_id_seq', (SELECT COALESCE(MAX(id), 0) FROM users));
SELECT setval('car_sources_id_seq', (SELECT COALESCE(MAX(id), 0) FROM car_sources));
SELECT setval('car_images_id_seq', (SELECT COALESCE(MAX(id), 0) FROM car_images));

-- Test append

-- 拍卖表
CREATE TABLE IF NOT EXISTS auctions (
    id              BIGINT PRIMARY KEY,
    car_id          BIGINT NOT NULL,
    seller_id       BIGINT NOT NULL,
    start_price     DECIMAL(12,2) NOT NULL,
    reserve_price   DECIMAL(12,2),
    current_price   DECIMAL(12,2) NOT NULL,
    bid_increment   DECIMAL(10,2) NOT NULL DEFAULT 1000,
    start_time      TIMESTAMP NOT NULL,
    end_time        TIMESTAMP NOT NULL,
    actual_end_time TIMESTAMP,
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    winner_id       BIGINT,
    winning_price   DECIMAL(12,2),
    total_bids      INT NOT NULL DEFAULT 0,
    view_count      BIGINT NOT NULL DEFAULT 0,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version         INT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_auctions_car_id ON auctions(car_id);
CREATE INDEX IF NOT EXISTS idx_auctions_seller_id ON auctions(seller_id);
CREATE INDEX IF NOT EXISTS idx_auctions_status ON auctions(status);
CREATE INDEX IF NOT EXISTS idx_auctions_start_time ON auctions(start_time);
CREATE INDEX IF NOT EXISTS idx_auctions_end_time ON auctions(end_time);
COMMENT ON TABLE auctions IS '拍卖表';
COMMENT ON COLUMN auctions.status IS '状态: PENDING-待开始, BIDDING-竞拍中, ENDED-已结束, SETTLED-已结算, CANCELLED-已取消, FAILED-流拍';

-- 拍卖出价记录表
CREATE TABLE IF NOT EXISTS auction_bids (
    id          BIGINT PRIMARY KEY,
    auction_id  BIGINT NOT NULL,
    bidder_id   BIGINT NOT NULL,
    bid_price   DECIMAL(12,2) NOT NULL,
    bid_time    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_winning  BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_auction_bids_auction_id ON auction_bids(auction_id);
CREATE INDEX IF NOT EXISTS idx_auction_bids_bidder_id ON auction_bids(bidder_id);
CREATE INDEX IF NOT EXISTS idx_auction_bids_bid_time ON auction_bids(bid_time);
COMMENT ON TABLE auction_bids IS '拍卖出价记录表';

-- 拍卖关注/观看表
CREATE TABLE IF NOT EXISTS auction_watches (
    id         BIGINT PRIMARY KEY,
    auction_id BIGINT NOT NULL,
    user_id    BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(auction_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_auction_watches_user_id ON auction_watches(user_id);

COMMENT ON TABLE auctions IS '拍卖表';
COMMENT ON COLUMN auctions.id IS '主键ID';
COMMENT ON COLUMN auctions.car_id IS '关联车源ID';
COMMENT ON COLUMN auctions.seller_id IS '卖家用户ID';
COMMENT ON COLUMN auctions.start_price IS '起拍价(元)';
COMMENT ON COLUMN auctions.reserve_price IS '保留价/底价(元)';
COMMENT ON COLUMN auctions.current_price IS '当前最高出价(元)';
COMMENT ON COLUMN auctions.bid_increment IS '每次加价幅度(元)';
COMMENT ON COLUMN auctions.start_time IS '拍卖开始时间';
COMMENT ON COLUMN auctions.end_time IS '拍卖计划结束时间';
COMMENT ON COLUMN auctions.actual_end_time IS '拍卖实际结束时间';
COMMENT ON COLUMN auctions.status IS '状态: PENDING-待开始, BIDDING-竞拍中, ENDED-已结束, SETTLED-已结算, CANCELLED-已取消, FAILED-流拍';
COMMENT ON COLUMN auctions.winner_id IS '中标者用户ID';
COMMENT ON COLUMN auctions.winning_price IS '中标价格(元)';
COMMENT ON COLUMN auctions.total_bids IS '累计出价次数';
COMMENT ON COLUMN auctions.view_count IS '浏览次数';
COMMENT ON COLUMN auctions.created_at IS '创建时间';
COMMENT ON COLUMN auctions.updated_at IS '更新时间';
COMMENT ON COLUMN auctions.version IS '乐观锁版本号';

COMMENT ON TABLE auction_bids IS '拍卖出价记录表';
COMMENT ON COLUMN auction_bids.id IS '主键ID';
COMMENT ON COLUMN auction_bids.auction_id IS '关联拍卖ID';
COMMENT ON COLUMN auction_bids.bidder_id IS '出价者用户ID';
COMMENT ON COLUMN auction_bids.bid_price IS '出价金额(元)';
COMMENT ON COLUMN auction_bids.bid_time IS '出价时间';
COMMENT ON COLUMN auction_bids.is_winning IS '是否为当前最高出价';
COMMENT ON COLUMN auction_bids.created_at IS '创建时间';

COMMENT ON TABLE auction_watches IS '拍卖关注表';
COMMENT ON COLUMN auction_watches.id IS '主键ID';
COMMENT ON COLUMN auction_watches.auction_id IS '关联拍卖ID';
COMMENT ON COLUMN auction_watches.user_id IS '关注用户ID';
COMMENT ON COLUMN auction_watches.created_at IS '关注时间';
COMMENT ON TABLE auction_watches IS '拍卖关注表';

-- 32. 求购意向表
CREATE TABLE IF NOT EXISTS purchase_demands (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id),
    brand_name      VARCHAR(50),
    series_name     VARCHAR(50),
    model_name      VARCHAR(100),
    year_from       INTEGER,
    year_to         INTEGER,
    price_min       DECIMAL(12,2),
    price_max       DECIMAL(12,2),
    mileage_max     INTEGER,
    color           VARCHAR(20),
    city_code       VARCHAR(20),
    city_name       VARCHAR(50),
    energy_type     VARCHAR(20),
    description     TEXT,
    status          VARCHAR(20) DEFAULT 'ACTIVE',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_purchase_demands_user_id ON purchase_demands(user_id);
CREATE INDEX IF NOT EXISTS idx_purchase_demands_status ON purchase_demands(status);
CREATE INDEX IF NOT EXISTS idx_purchase_demands_created_at ON purchase_demands(created_at DESC);
COMMENT ON TABLE purchase_demands IS '求购意向表';
COMMENT ON COLUMN purchase_demands.user_id IS '求购用户ID';
COMMENT ON COLUMN purchase_demands.brand_name IS '意向品牌';
COMMENT ON COLUMN purchase_demands.price_min IS '最低预算';
COMMENT ON COLUMN purchase_demands.price_max IS '最高预算';
COMMENT ON COLUMN purchase_demands.status IS '状态: ACTIVE-有效, CANCELLED-已取消, FULFILLED-已成交';