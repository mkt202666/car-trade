-- ============================================================
-- 5D好车 B2B 二手车交易平台 - 完整数据库初始化脚本
-- 数据库: PostgreSQL 16
-- 编码: UTF-8
-- 日期: 2026-06-10
-- 说明: 包含建库、建表、索引、注释、测试数据
-- ============================================================

-- ===================== 1. 创建数据库 =====================
CREATE DATABASE new_car_trade WITH ENCODING 'UTF8' LC_COLLATE 'zh_CN.utf8' LC_CTYPE 'zh_CN.utf8' TEMPLATE template0;
ALTER DATABASE new_car_trade SET search_path TO public;

-- ===================== 2. 创建数据表 =====================

-- ----------------------------------------------------------
-- 表1: 用户表
-- ----------------------------------------------------------
CREATE TABLE users (
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

CREATE INDEX idx_users_phone ON users(phone);
CREATE INDEX idx_users_status ON users(status);
COMMENT ON TABLE users IS '用户表';
COMMENT ON COLUMN users.id IS '主键ID';
COMMENT ON COLUMN users.phone IS '手机号，唯一';
COMMENT ON COLUMN users.nickname IS '用户昵称';
COMMENT ON COLUMN users.real_name IS '真实姓名';
COMMENT ON COLUMN users.avatar_url IS '头像地址';
COMMENT ON COLUMN users.shop_name IS '车行名称';
COMMENT ON COLUMN users.credit_grade IS '信用等级: S-极佳, A-优秀, B-良好, C-一般, D-较差';
COMMENT ON COLUMN users.credit_score IS '信用分数';
COMMENT ON COLUMN users.deal_count IS '累计成交订单数';
COMMENT ON COLUMN users.on_sale_count IS '在售车源数量';
COMMENT ON COLUMN users.view_count IS '累计被访问次数';
COMMENT ON COLUMN users.view_count_today IS '今日被访问次数';
COMMENT ON COLUMN users.message_count IS '累计消息数';
COMMENT ON COLUMN users.message_count_today IS '今日消息数';
COMMENT ON COLUMN users.follower_count IS '累计粉丝数';
COMMENT ON COLUMN users.follower_count_today IS '今日新增粉丝数';
COMMENT ON COLUMN users.member_expire_at IS '会员到期时间';
COMMENT ON COLUMN users.certification_status IS '认证状态: UNCERTIFIED-未认证, PENDING-审核中, CERTIFIED-已认证, REJECTED-已拒绝';
COMMENT ON COLUMN users.status IS '账号状态: ACTIVE-正常, DISABLE-禁用';
COMMENT ON COLUMN users.created_at IS '创建时间';
COMMENT ON COLUMN users.updated_at IS '更新时间';
COMMENT ON COLUMN users.deleted_at IS '删除时间，软删除';

-- ----------------------------------------------------------
-- 表2: 汽车品牌表
-- ----------------------------------------------------------
CREATE TABLE brands (
    id              SERIAL PRIMARY KEY,
    name            VARCHAR(50) NOT NULL,
    logo_url        VARCHAR(500),
    first_letter    VARCHAR(1),
    sort_order      INTEGER DEFAULT 0,
    status          VARCHAR(20) DEFAULT 'ACTIVE',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_brands_first_letter ON brands(first_letter);
COMMENT ON TABLE brands IS '汽车品牌表';
COMMENT ON COLUMN brands.id IS '主键ID';
COMMENT ON COLUMN brands.name IS '品牌名称';
COMMENT ON COLUMN brands.logo_url IS '品牌logo地址';
COMMENT ON COLUMN brands.first_letter IS '品牌首字母';
COMMENT ON COLUMN brands.sort_order IS '排序权重';
COMMENT ON COLUMN brands.status IS '状态: ACTIVE-启用, DISABLE-禁用';
COMMENT ON COLUMN brands.created_at IS '创建时间';

-- ----------------------------------------------------------
-- 表3: 车系表
-- ----------------------------------------------------------
CREATE TABLE series (
    id              SERIAL PRIMARY KEY,
    brand_id        INTEGER NOT NULL REFERENCES brands(id),
    name            VARCHAR(50) NOT NULL,
    sort_order      INTEGER DEFAULT 0,
    status          VARCHAR(20) DEFAULT 'ACTIVE',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_series_brand_id ON series(brand_id);
COMMENT ON TABLE series IS '车系表';
COMMENT ON COLUMN series.id IS '主键ID';
COMMENT ON COLUMN series.brand_id IS '关联品牌ID';
COMMENT ON COLUMN series.name IS '车系名称';
COMMENT ON COLUMN series.sort_order IS '排序权重';
COMMENT ON COLUMN series.status IS '状态: ACTIVE-启用, DISABLE-禁用';
COMMENT ON COLUMN series.created_at IS '创建时间';

-- ----------------------------------------------------------
-- 表4: 车型表
-- ----------------------------------------------------------
CREATE TABLE models (
    id              SERIAL PRIMARY KEY,
    series_id       INTEGER NOT NULL REFERENCES series(id),
    name            VARCHAR(100) NOT NULL,
    year            INTEGER,
    guide_price     DECIMAL(12,2),
    sort_order      INTEGER DEFAULT 0,
    status          VARCHAR(20) DEFAULT 'ACTIVE',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_models_series_id ON models(series_id);
COMMENT ON TABLE models IS '车型表';
COMMENT ON COLUMN models.id IS '主键ID';
COMMENT ON COLUMN models.series_id IS '关联车系ID';
COMMENT ON COLUMN models.name IS '车型名称';
COMMENT ON COLUMN models.year IS '年款';
COMMENT ON COLUMN models.guide_price IS '官方指导价';
COMMENT ON COLUMN models.sort_order IS '排序权重';
COMMENT ON COLUMN models.status IS '状态: ACTIVE-启用, DISABLE-禁用';
COMMENT ON COLUMN models.created_at IS '创建时间';

-- ----------------------------------------------------------
-- 表5: 车源表
-- ----------------------------------------------------------
CREATE TABLE car_sources (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id),
    brand_id        INTEGER NOT NULL,
    series_id       INTEGER NOT NULL,
    model_id        INTEGER NOT NULL,
    title           VARCHAR(200),
    vin             VARCHAR(50),
    year            INTEGER,
    mileage         INTEGER,
    price           DECIMAL(12,2),
    pricing_type    VARCHAR(20) DEFAULT 'FIXED',
    starting_price  DECIMAL(12,2),
    ceiling_price   DECIMAL(12,2),
    bid_increment   DECIMAL(10,2),
    deposit         DECIMAL(10,2),
    color           VARCHAR(20),
    city_code       VARCHAR(20),
    city_name       VARCHAR(50),
    energy_type     VARCHAR(20),
    transmission    VARCHAR(20),
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
    inspection_report_type VARCHAR(20),
    inspection_report_url VARCHAR(500),
    certificate_materials JSONB,
    support_lock_negotiation BOOLEAN DEFAULT FALSE,
    ai_auto_promote BOOLEAN DEFAULT FALSE,
    is_draft        BOOLEAN DEFAULT FALSE,
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

CREATE INDEX idx_car_sources_user_id ON car_sources(user_id);
CREATE INDEX idx_car_sources_brand_series ON car_sources(brand_id, series_id);
CREATE INDEX idx_car_sources_city ON car_sources(city_code);
CREATE INDEX idx_car_sources_price ON car_sources(price);
CREATE INDEX idx_car_sources_status ON car_sources(status);
CREATE INDEX idx_car_sources_created_at ON car_sources(created_at DESC);
COMMENT ON TABLE car_sources IS '车源表';
COMMENT ON COLUMN car_sources.id IS '主键ID';
COMMENT ON COLUMN car_sources.user_id IS '发布用户ID';
COMMENT ON COLUMN car_sources.brand_id IS '品牌ID';
COMMENT ON COLUMN car_sources.series_id IS '车系ID';
COMMENT ON COLUMN car_sources.model_id IS '车型ID';
COMMENT ON COLUMN car_sources.title IS '车源标题';
COMMENT ON COLUMN car_sources.vin IS '车架号';
COMMENT ON COLUMN car_sources.year IS '车辆年款';
COMMENT ON COLUMN car_sources.mileage IS '行驶里程(公里)';
COMMENT ON COLUMN car_sources.price IS '售卖价格';
COMMENT ON COLUMN car_sources.pricing_type IS '定价方式: FIXED-一口价, AUCTION-拍卖';
COMMENT ON COLUMN car_sources.starting_price IS '起拍价';
COMMENT ON COLUMN car_sources.ceiling_price IS '封顶价';
COMMENT ON COLUMN car_sources.bid_increment IS '加价幅度';
COMMENT ON COLUMN car_sources.deposit IS '保证金金额';
COMMENT ON COLUMN car_sources.color IS '车身颜色';
COMMENT ON COLUMN car_sources.city_code IS '城市编码';
COMMENT ON COLUMN car_sources.city_name IS '城市名称';
COMMENT ON COLUMN car_sources.energy_type IS '能源类型: GASOLINE-燃油, PURE_ELECTRIC-纯电, HYBRID-混动';
COMMENT ON COLUMN car_sources.transmission IS '变速箱类型';
COMMENT ON COLUMN car_sources.usage_type IS '使用性质';
COMMENT ON COLUMN car_sources.owner_type IS '车主类型';
COMMENT ON COLUMN car_sources.is_mortgaged IS '是否抵押车';
COMMENT ON COLUMN car_sources.is_inherited IS '是否过户车';
COMMENT ON COLUMN car_sources.registration_date IS '上牌日期';
COMMENT ON COLUMN car_sources.insurance_expiry IS '保险到期日';
COMMENT ON COLUMN car_sources.inspection_expiry IS '年检到期日';
COMMENT ON COLUMN car_sources.production_date IS '出厂日期';
COMMENT ON COLUMN car_sources.key_count IS '车钥匙数量';
COMMENT ON COLUMN car_sources.description IS '车辆详情描述';
COMMENT ON COLUMN car_sources.inspection_report_type IS '检测报告类型: LINK-链接, FILE-文件';
COMMENT ON COLUMN car_sources.inspection_report_url IS '检测报告链接/文件URL';
COMMENT ON COLUMN car_sources.certificate_materials IS '证件材料JSON';
COMMENT ON COLUMN car_sources.support_lock_negotiation IS '支持锁车洽谈';
COMMENT ON COLUMN car_sources.ai_auto_promote IS 'AI自动推广';
COMMENT ON COLUMN car_sources.is_draft IS '是否草稿';
COMMENT ON COLUMN car_sources.auction_status IS '拍卖状态: NONE-未拍卖, BIDDING-拍卖中, BIDDED-已参拍';
COMMENT ON COLUMN car_sources.auction_end_time IS '拍卖结束时间';
COMMENT ON COLUMN car_sources.view_count IS '浏览次数';
COMMENT ON COLUMN car_sources.favorite_count IS '收藏数量';
COMMENT ON COLUMN car_sources.status IS '车源状态: ACTIVE-正常, OFFLINE-下架';
COMMENT ON COLUMN car_sources.published_at IS '发布时间';
COMMENT ON COLUMN car_sources.created_at IS '创建时间';
COMMENT ON COLUMN car_sources.updated_at IS '更新时间';
COMMENT ON COLUMN car_sources.deleted_at IS '删除时间，软删除';
COMMENT ON COLUMN car_sources.export_countries IS '可出口国家，逗号分隔';

-- ----------------------------------------------------------
-- 表6: 车源图片表
-- ----------------------------------------------------------
CREATE TABLE car_images (
    id              BIGSERIAL PRIMARY KEY,
    car_id          BIGINT NOT NULL REFERENCES car_sources(id),
    image_url       VARCHAR(500) NOT NULL,
    image_type      VARCHAR(20),
    sort_order      INTEGER DEFAULT 0,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_car_images_car_id ON car_images(car_id);
COMMENT ON TABLE car_images IS '车源图片表';
COMMENT ON COLUMN car_images.id IS '主键ID';
COMMENT ON COLUMN car_images.car_id IS '关联车源ID';
COMMENT ON COLUMN car_images.image_url IS '图片地址';
COMMENT ON COLUMN car_images.image_type IS '图片类型: EXTERIOR-外观, INTERIOR-内饰, DETAIL-细节, DEFECT-瑕疵';
COMMENT ON COLUMN car_images.sort_order IS '图片排序';
COMMENT ON COLUMN car_images.created_at IS '创建时间';

-- ----------------------------------------------------------
-- 表7: 车源标签表
-- ----------------------------------------------------------
CREATE TABLE car_tags (
    id              BIGSERIAL PRIMARY KEY,
    car_id          BIGINT NOT NULL REFERENCES car_sources(id),
    tag_type        VARCHAR(20) NOT NULL,
    tag_value       VARCHAR(50),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_car_tags_car_id ON car_tags(car_id);
CREATE INDEX idx_car_tags_type ON car_tags(tag_type);
COMMENT ON TABLE car_tags IS '车源标签表';
COMMENT ON COLUMN car_tags.id IS '主键ID';
COMMENT ON COLUMN car_tags.car_id IS '关联车源ID';
COMMENT ON COLUMN car_tags.tag_type IS '标签类型: DEPOSIT-保证金, EXPORT-出口, ENERGY-能源';
COMMENT ON COLUMN car_tags.tag_value IS '标签值';
COMMENT ON COLUMN car_tags.created_at IS '创建时间';

-- ----------------------------------------------------------
-- 表8: 车辆检测报告表
-- ----------------------------------------------------------
CREATE TABLE car_inspections (
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

CREATE INDEX idx_car_inspections_car_id ON car_inspections(car_id);
COMMENT ON TABLE car_inspections IS '车辆检测报告表';
COMMENT ON COLUMN car_inspections.id IS '主键ID';
COMMENT ON COLUMN car_inspections.car_id IS '关联车源ID';
COMMENT ON COLUMN car_inspections.overall_condition IS '整体车况: NORMAL-非事故车, ACCIDENT-事故车';
COMMENT ON COLUMN car_inspections.paint IS '漆面: ORIGINAL-原漆, SCRATCH-划痕剐蹭, PAINTED-喷漆';
COMMENT ON COLUMN car_inspections.structure IS '车身结构';
COMMENT ON COLUMN car_inspections.engine IS '发动机状态';
COMMENT ON COLUMN car_inspections.transmission IS '变速箱状态';
COMMENT ON COLUMN car_inspections.transfer_count IS '过户次数';
COMMENT ON COLUMN car_inspections.mileage_type IS '公里数类型: ACTUAL-实表, TAMPERED-调表, DISPLAY-表显';
COMMENT ON COLUMN car_inspections.description IS '检测备注说明';
COMMENT ON COLUMN car_inspections.abnormal_photos IS '异常图片集合，JSON格式';
COMMENT ON COLUMN car_inspections.created_at IS '创建时间';
COMMENT ON COLUMN car_inspections.updated_at IS '更新时间';

-- ----------------------------------------------------------
-- 表9: 订单表
-- ----------------------------------------------------------
CREATE TABLE orders (
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
    contract_content TEXT,
    contract_submitted BOOLEAN DEFAULT FALSE,
    contract_submitted_at TIMESTAMP,
    contract_confirmed BOOLEAN DEFAULT FALSE,
    contract_confirmed_at TIMESTAMP,
    terminate_count INTEGER DEFAULT 0,
    terminate_limit INTEGER DEFAULT 3,
    terminate_reason VARCHAR(200),
    last_terminate_at TIMESTAMP,
    remark          TEXT,
    cancel_reason   VARCHAR(200),
    completed_at    TIMESTAMP,
    cancelled_at    TIMESTAMP,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_orders_car_id ON orders(car_id);
CREATE INDEX idx_orders_buyer_id ON orders(buyer_id);
CREATE INDEX idx_orders_seller_id ON orders(seller_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_created_at ON orders(created_at DESC);
COMMENT ON TABLE orders IS '订单表';
COMMENT ON COLUMN orders.id IS '订单编号';
COMMENT ON COLUMN orders.car_id IS '关联车源ID';
COMMENT ON COLUMN orders.buyer_id IS '买家用户ID';
COMMENT ON COLUMN orders.seller_id IS '卖家用户ID';
COMMENT ON COLUMN orders.total_price IS '订单总金额';
COMMENT ON COLUMN orders.deposit_amount IS '保证金金额';
COMMENT ON COLUMN orders.buyer_deposit_paid IS '买家是否已交保证金';
COMMENT ON COLUMN orders.buyer_deposit_paid_at IS '买家保证金缴纳时间';
COMMENT ON COLUMN orders.seller_deposit_paid IS '卖家是否已交保证金';
COMMENT ON COLUMN orders.seller_deposit_paid_at IS '卖家保证金缴纳时间';
COMMENT ON COLUMN orders.status IS '订单状态: PENDING_CONFIRM-待确认, TRADING-交易中, DISPUTE-争议中, COMPLETED-已完成, CANCELLED-已终止';
COMMENT ON COLUMN orders.contract_no IS '合同编号';
COMMENT ON COLUMN orders.contract_content IS '合同内容';
COMMENT ON COLUMN orders.contract_submitted IS '合同是否已提交';
COMMENT ON COLUMN orders.contract_submitted_at IS '合同提交时间';
COMMENT ON COLUMN orders.contract_confirmed IS '合同是否已确认';
COMMENT ON COLUMN orders.contract_confirmed_at IS '合同确认时间';
COMMENT ON COLUMN orders.terminate_count IS '终止交易次数';
COMMENT ON COLUMN orders.terminate_limit IS '每日终止交易限制';
COMMENT ON COLUMN orders.terminate_reason IS '终止交易原因';
COMMENT ON COLUMN orders.last_terminate_at IS '最后终止交易时间';
COMMENT ON COLUMN orders.remark IS '订单备注';
COMMENT ON COLUMN orders.cancel_reason IS '取消原因';
COMMENT ON COLUMN orders.completed_at IS '交易完成时间';
COMMENT ON COLUMN orders.cancelled_at IS '订单取消时间';
COMMENT ON COLUMN orders.created_at IS '创建时间';
COMMENT ON COLUMN orders.updated_at IS '更新时间';

-- ----------------------------------------------------------
-- 表10: 订单车况信息表
-- ----------------------------------------------------------
CREATE TABLE order_inspections (
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

CREATE INDEX idx_order_inspections_order_id ON order_inspections(order_id);
COMMENT ON TABLE order_inspections IS '订单车况信息表';
COMMENT ON COLUMN order_inspections.id IS '主键ID';
COMMENT ON COLUMN order_inspections.order_id IS '关联订单编号';
COMMENT ON COLUMN order_inspections.overall_condition IS '整体车况';
COMMENT ON COLUMN order_inspections.paint IS '漆面情况';
COMMENT ON COLUMN order_inspections.structure IS '车身结构';
COMMENT ON COLUMN order_inspections.engine IS '发动机状态';
COMMENT ON COLUMN order_inspections.transmission IS '变速箱状态';
COMMENT ON COLUMN order_inspections.transfer_count IS '过户次数';
COMMENT ON COLUMN order_inspections.mileage_type IS '公里数类型';
COMMENT ON COLUMN order_inspections.description IS '车况描述';
COMMENT ON COLUMN order_inspections.abnormal_photos IS '异常照片集合，JSON格式';
COMMENT ON COLUMN order_inspections.materials IS '随车资料，JSON格式';
COMMENT ON COLUMN order_inspections.created_at IS '创建时间';
COMMENT ON COLUMN order_inspections.updated_at IS '更新时间';

-- ----------------------------------------------------------
-- 表11: 订单日志表
-- ----------------------------------------------------------
CREATE TABLE order_logs (
    id              BIGSERIAL PRIMARY KEY,
    order_id        VARCHAR(32) NOT NULL REFERENCES orders(id),
    action          VARCHAR(50) NOT NULL,
    operator_id     BIGINT,
    operator_name   VARCHAR(50),
    remark          TEXT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_order_logs_order_id ON order_logs(order_id);
COMMENT ON TABLE order_logs IS '订单日志表';
COMMENT ON COLUMN order_logs.id IS '主键ID';
COMMENT ON COLUMN order_logs.order_id IS '关联订单编号';
COMMENT ON COLUMN order_logs.action IS '操作行为';
COMMENT ON COLUMN order_logs.operator_id IS '操作人ID';
COMMENT ON COLUMN order_logs.operator_name IS '操作人名称';
COMMENT ON COLUMN order_logs.remark IS '日志备注';
COMMENT ON COLUMN order_logs.created_at IS '操作时间';

-- ----------------------------------------------------------
-- 表12: 争议表
-- ----------------------------------------------------------
CREATE TABLE disputes (
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

CREATE INDEX idx_disputes_order_id ON disputes(order_id);
CREATE INDEX idx_disputes_status ON disputes(status);
COMMENT ON TABLE disputes IS '争议表';
COMMENT ON COLUMN disputes.id IS '主键ID';
COMMENT ON COLUMN disputes.order_id IS '关联订单编号';
COMMENT ON COLUMN disputes.initiator_id IS '发起用户ID';
COMMENT ON COLUMN disputes.reason IS '争议原因';
COMMENT ON COLUMN disputes.description IS '争议详细描述';
COMMENT ON COLUMN disputes.evidence IS '举证材料，JSON格式';
COMMENT ON COLUMN disputes.status IS '状态: PENDING-待处理, PROCESSING-处理中, RESOLVED-已解决, REJECTED-已驳回';
COMMENT ON COLUMN disputes.result IS '处理结果';
COMMENT ON COLUMN disputes.handler_id IS '处理人ID';
COMMENT ON COLUMN disputes.handled_at IS '处理完成时间';
COMMENT ON COLUMN disputes.created_at IS '创建时间';
COMMENT ON COLUMN disputes.updated_at IS '更新时间';

-- ----------------------------------------------------------
-- 表13: 电子合同表
-- ----------------------------------------------------------
CREATE TABLE contracts (
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

CREATE INDEX idx_contracts_order ON contracts(order_id);
CREATE INDEX idx_contracts_no ON contracts(contract_no);
COMMENT ON TABLE contracts IS '电子合同表';
COMMENT ON COLUMN contracts.id IS '主键ID';
COMMENT ON COLUMN contracts.order_id IS '关联订单编号';
COMMENT ON COLUMN contracts.contract_no IS '合同编号，唯一';
COMMENT ON COLUMN contracts.template_id IS '合同模板ID';
COMMENT ON COLUMN contracts.title IS '合同标题';
COMMENT ON COLUMN contracts.content IS '合同正文内容';
COMMENT ON COLUMN contracts.buyer_id IS '买家ID';
COMMENT ON COLUMN contracts.seller_id IS '卖家ID';
COMMENT ON COLUMN contracts.buyer_signed IS '买家是否已签署';
COMMENT ON COLUMN contracts.buyer_signed_at IS '买家签署时间';
COMMENT ON COLUMN contracts.seller_signed IS '卖家是否已签署';
COMMENT ON COLUMN contracts.seller_signed_at IS '卖家签署时间';
COMMENT ON COLUMN contracts.status IS '状态: DRAFT-草稿, PENDING_SIGN-待签署, SIGNED-已签署, ARCHIVED-已归档';
COMMENT ON COLUMN contracts.file_url IS '合同文件地址';
COMMENT ON COLUMN contracts.created_at IS '创建时间';
COMMENT ON COLUMN contracts.updated_at IS '更新时间';

-- ----------------------------------------------------------
-- 表14: 保证金账户表
-- ----------------------------------------------------------
CREATE TABLE deposit_accounts (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL UNIQUE REFERENCES users(id),
    balance         DECIMAL(10,2) DEFAULT 0,
    frozen_amount   DECIMAL(10,2) DEFAULT 0,
    total_deposit   DECIMAL(10,2) DEFAULT 0,
    status          VARCHAR(20) DEFAULT 'ACTIVE',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_deposit_accounts_user_id ON deposit_accounts(user_id);
COMMENT ON TABLE deposit_accounts IS '保证金账户表';
COMMENT ON COLUMN deposit_accounts.id IS '主键ID';
COMMENT ON COLUMN deposit_accounts.user_id IS '关联用户ID，唯一';
COMMENT ON COLUMN deposit_accounts.balance IS '可用余额';
COMMENT ON COLUMN deposit_accounts.frozen_amount IS '冻结金额';
COMMENT ON COLUMN deposit_accounts.total_deposit IS '累计充值金额';
COMMENT ON COLUMN deposit_accounts.status IS '账户状态: ACTIVE-正常, DISABLE-禁用';
COMMENT ON COLUMN deposit_accounts.created_at IS '创建时间';
COMMENT ON COLUMN deposit_accounts.updated_at IS '更新时间';

-- ----------------------------------------------------------
-- 表15: 保证金流水表
-- ----------------------------------------------------------
CREATE TABLE deposit_records (
    id              BIGSERIAL PRIMARY KEY,
    account_id      BIGINT NOT NULL REFERENCES deposit_accounts(id),
    order_id        VARCHAR(32),
    type            VARCHAR(20) NOT NULL,
    amount          DECIMAL(10,2) NOT NULL,
    balance_after   DECIMAL(10,2),
    remark          VARCHAR(200),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_deposit_records_account_id ON deposit_records(account_id);
CREATE INDEX idx_deposit_records_created_at ON deposit_records(created_at DESC);
COMMENT ON TABLE deposit_records IS '保证金流水表';
COMMENT ON COLUMN deposit_records.id IS '主键ID';
COMMENT ON COLUMN deposit_records.account_id IS '关联保证金账户ID';
COMMENT ON COLUMN deposit_records.order_id IS '关联订单编号';
COMMENT ON COLUMN deposit_records.type IS '类型: RECHARGE-充值, WITHDRAW-提现, PAY-支付, REFUND-退款, FREEZE-冻结, UNFREEZE-解冻';
COMMENT ON COLUMN deposit_records.amount IS '变动金额';
COMMENT ON COLUMN deposit_records.balance_after IS '变动后余额';
COMMENT ON COLUMN deposit_records.remark IS '流水备注';
COMMENT ON COLUMN deposit_records.created_at IS '流水发生时间';

-- ----------------------------------------------------------
-- 表16: 信用账户表
-- ----------------------------------------------------------
CREATE TABLE credit_accounts (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL UNIQUE REFERENCES users(id),
    credit_limit    DECIMAL(12,2) DEFAULT 0,
    used_amount     DECIMAL(12,2) DEFAULT 0,
    available_amount DECIMAL(12,2) DEFAULT 0,
    status          VARCHAR(20) DEFAULT 'ACTIVE',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_credit_accounts_user_id ON credit_accounts(user_id);
COMMENT ON TABLE credit_accounts IS '信用账户表';
COMMENT ON COLUMN credit_accounts.id IS '主键ID';
COMMENT ON COLUMN credit_accounts.user_id IS '关联用户ID，唯一';
COMMENT ON COLUMN credit_accounts.credit_limit IS '信用额度';
COMMENT ON COLUMN credit_accounts.used_amount IS '已使用额度';
COMMENT ON COLUMN credit_accounts.available_amount IS '可用额度';
COMMENT ON COLUMN credit_accounts.status IS '账户状态: ACTIVE-正常, DISABLE-禁用';
COMMENT ON COLUMN credit_accounts.created_at IS '创建时间';
COMMENT ON COLUMN credit_accounts.updated_at IS '更新时间';

-- ----------------------------------------------------------
-- 表17: 消息表
-- ----------------------------------------------------------
CREATE TABLE messages (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id),
    type            VARCHAR(20) NOT NULL,
    title           VARCHAR(200),
    content         TEXT,
    is_read         BOOLEAN DEFAULT FALSE,
    related_id      VARCHAR(50),
    related_type    VARCHAR(20),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_messages_user_id ON messages(user_id);
CREATE INDEX idx_messages_type ON messages(type);
CREATE INDEX idx_messages_is_read ON messages(is_read);
CREATE INDEX idx_messages_created_at ON messages(created_at DESC);
COMMENT ON TABLE messages IS '消息表';
COMMENT ON COLUMN messages.id IS '主键ID';
COMMENT ON COLUMN messages.user_id IS '接收用户ID';
COMMENT ON COLUMN messages.type IS '消息类型: SYSTEM-系统消息, TRADE-交易通知, ACTIVITY-活动通知, AUTO_PROMOTION-自动推广, CHAT-聊天消息, TEAM_APPLICATION-车行成员申请, DEPOSIT_WARNING-保证金不足';
COMMENT ON COLUMN messages.title IS '消息标题';
COMMENT ON COLUMN messages.content IS '消息内容';
COMMENT ON COLUMN messages.is_read IS '是否已读: false-未读, true-已读';
COMMENT ON COLUMN messages.related_id IS '关联业务ID';
COMMENT ON COLUMN messages.related_type IS '关联业务类型';
COMMENT ON COLUMN messages.created_at IS '消息创建时间';

-- ----------------------------------------------------------
-- 表18: 用户收藏表
-- ----------------------------------------------------------
CREATE TABLE user_favorites (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id),
    car_id          BIGINT NOT NULL REFERENCES car_sources(id),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, car_id)
);

CREATE INDEX idx_user_favorites_user_id ON user_favorites(user_id);
CREATE INDEX idx_user_favorites_car_id ON user_favorites(car_id);
COMMENT ON TABLE user_favorites IS '用户收藏表';
COMMENT ON COLUMN user_favorites.id IS '主键ID';
COMMENT ON COLUMN user_favorites.user_id IS '用户ID';
COMMENT ON COLUMN user_favorites.car_id IS '收藏车源ID';
COMMENT ON COLUMN user_favorites.created_at IS '收藏时间';

-- ----------------------------------------------------------
-- 表19: 用户关注表
-- ----------------------------------------------------------
CREATE TABLE user_follows (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id),
    follow_user_id  BIGINT NOT NULL REFERENCES users(id),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, follow_user_id)
);

CREATE INDEX idx_user_follows_user ON user_follows(user_id);
CREATE INDEX idx_user_follows_follow ON user_follows(follow_user_id);
COMMENT ON TABLE user_follows IS '用户关注表（关注卖家）';
COMMENT ON COLUMN user_follows.id IS '主键ID';
COMMENT ON COLUMN user_follows.user_id IS '关注人ID';
COMMENT ON COLUMN user_follows.follow_user_id IS '被关注人ID';
COMMENT ON COLUMN user_follows.created_at IS '关注时间';

-- ----------------------------------------------------------
-- 表20: 浏览记录表
-- ----------------------------------------------------------
CREATE TABLE browsing_history (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id),
    car_id          BIGINT NOT NULL REFERENCES car_sources(id),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_browsing_history_user ON browsing_history(user_id, created_at DESC);
COMMENT ON TABLE browsing_history IS '浏览记录表';
COMMENT ON COLUMN browsing_history.id IS '主键ID';
COMMENT ON COLUMN browsing_history.user_id IS '浏览用户ID';
COMMENT ON COLUMN browsing_history.car_id IS '浏览车源ID';
COMMENT ON COLUMN browsing_history.created_at IS '浏览时间';

-- ----------------------------------------------------------
-- 表21: 车行成员表
-- ----------------------------------------------------------
CREATE TABLE shop_members (
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

CREATE INDEX idx_shop_members_shop ON shop_members(shop_user_id);
CREATE INDEX idx_shop_members_member ON shop_members(member_user_id);
COMMENT ON TABLE shop_members IS '车行成员表';
COMMENT ON COLUMN shop_members.id IS '主键ID';
COMMENT ON COLUMN shop_members.shop_user_id IS '车行主用户ID';
COMMENT ON COLUMN shop_members.member_user_id IS '成员用户ID';
COMMENT ON COLUMN shop_members.role IS '角色: OWNER-车行主, ADMIN-管理员, MEMBER-成员';
COMMENT ON COLUMN shop_members.status IS '状态: PENDING-待审批, ACTIVE-已加入, REJECTED-已拒绝';
COMMENT ON COLUMN shop_members.applied_at IS '申请时间';
COMMENT ON COLUMN shop_members.approved_at IS '审批通过时间';
COMMENT ON COLUMN shop_members.created_at IS '创建时间';
COMMENT ON COLUMN shop_members.updated_at IS '更新时间';

-- ----------------------------------------------------------
-- 表22: 优惠券定义表
-- ----------------------------------------------------------
CREATE TABLE coupons (
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

CREATE INDEX idx_coupons_status ON coupons(status);
COMMENT ON TABLE coupons IS '优惠券定义表';
COMMENT ON COLUMN coupons.id IS '主键ID';
COMMENT ON COLUMN coupons.name IS '优惠券名称';
COMMENT ON COLUMN coupons.type IS '券类型: CASH-现金券, DISCOUNT-折扣券, FREE_SHIPPING-免运费';
COMMENT ON COLUMN coupons.value IS '券面额/折扣值';
COMMENT ON COLUMN coupons.min_amount IS '使用最低金额门槛';
COMMENT ON COLUMN coupons.total_count IS '发行总数量';
COMMENT ON COLUMN coupons.remain_count IS '剩余可领取数量';
COMMENT ON COLUMN coupons.start_at IS '生效开始时间';
COMMENT ON COLUMN coupons.end_at IS '失效结束时间';
COMMENT ON COLUMN coupons.status IS '状态: ACTIVE-正常, DISABLE-下架';
COMMENT ON COLUMN coupons.created_at IS '创建时间';

-- ----------------------------------------------------------
-- 表23: 用户优惠券表
-- ----------------------------------------------------------
CREATE TABLE user_coupons (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id),
    coupon_id       BIGINT NOT NULL REFERENCES coupons(id),
    used_at         TIMESTAMP,
    order_id        VARCHAR(32),
    status          VARCHAR(20) DEFAULT 'UNUSED',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_coupons_user ON user_coupons(user_id);
COMMENT ON TABLE user_coupons IS '用户优惠券表';
COMMENT ON COLUMN user_coupons.id IS '主键ID';
COMMENT ON COLUMN user_coupons.user_id IS '用户ID';
COMMENT ON COLUMN user_coupons.coupon_id IS '关联优惠券ID';
COMMENT ON COLUMN user_coupons.used_at IS '使用时间';
COMMENT ON COLUMN user_coupons.order_id IS '使用关联订单编号';
COMMENT ON COLUMN user_coupons.status IS '状态: UNUSED-未使用, USED-已使用, EXPIRED-已过期';
COMMENT ON COLUMN user_coupons.created_at IS '领取时间';

-- ----------------------------------------------------------
-- 表24: 会员方案表
-- ----------------------------------------------------------
CREATE TABLE member_plans (
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

COMMENT ON TABLE member_plans IS '会员方案表';
COMMENT ON COLUMN member_plans.id IS '主键ID';
COMMENT ON COLUMN member_plans.name IS '套餐名称';
COMMENT ON COLUMN member_plans.level IS '等级: BRONZE-青铜, SILVER-白银, GOLD-黄金, DIAMOND-钻石';
COMMENT ON COLUMN member_plans.price IS '套餐价格';
COMMENT ON COLUMN member_plans.duration_days IS '有效时长(天)';
COMMENT ON COLUMN member_plans.benefits IS '权益JSON: {"free_publish": 50, "discount_rate": 0.9, "ai_analysis": true}';
COMMENT ON COLUMN member_plans.sort_order IS '排序权重';
COMMENT ON COLUMN member_plans.status IS '状态: ACTIVE-上架, DISABLE-下架';
COMMENT ON COLUMN member_plans.created_at IS '创建时间';

-- ----------------------------------------------------------
-- 表25: 用户会员表
-- ----------------------------------------------------------
CREATE TABLE user_membership (
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

CREATE INDEX idx_user_membership_user ON user_membership(user_id);
COMMENT ON TABLE user_membership IS '用户会员表';
COMMENT ON COLUMN user_membership.id IS '主键ID';
COMMENT ON COLUMN user_membership.user_id IS '用户ID';
COMMENT ON COLUMN user_membership.plan_id IS '购买会员套餐ID';
COMMENT ON COLUMN user_membership.level IS '会员等级';
COMMENT ON COLUMN user_membership.start_at IS '会员生效时间';
COMMENT ON COLUMN user_membership.expire_at IS '会员到期时间';
COMMENT ON COLUMN user_membership.status IS '状态: ACTIVE-有效, EXPIRED-已过期, CANCELLED-已取消';
COMMENT ON COLUMN user_membership.created_at IS '开通时间';
COMMENT ON COLUMN user_membership.updated_at IS '更新时间';

-- ----------------------------------------------------------
-- 表26: 在线客服工单表
-- ----------------------------------------------------------
CREATE TABLE customer_service_tickets (
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

CREATE INDEX idx_cs_tickets_user ON customer_service_tickets(user_id);
CREATE INDEX idx_cs_tickets_status ON customer_service_tickets(status);
COMMENT ON TABLE customer_service_tickets IS '在线客服工单表';
COMMENT ON COLUMN customer_service_tickets.id IS '主键ID';
COMMENT ON COLUMN customer_service_tickets.user_id IS '提交工单用户ID';
COMMENT ON COLUMN customer_service_tickets.title IS '工单标题';
COMMENT ON COLUMN customer_service_tickets.category IS '工单分类';
COMMENT ON COLUMN customer_service_tickets.status IS '状态: PENDING-待处理, PROCESSING-处理中, RESOLVED-已解决, CLOSED-已关闭';
COMMENT ON COLUMN customer_service_tickets.priority IS '优先级: LOW-低, NORMAL-普通, HIGH-高, URGENT-紧急';
COMMENT ON COLUMN customer_service_tickets.handler_id IS '处理客服ID';
COMMENT ON COLUMN customer_service_tickets.handled_at IS '处理完成时间';
COMMENT ON COLUMN customer_service_tickets.created_at IS '工单创建时间';
COMMENT ON COLUMN customer_service_tickets.updated_at IS '工单更新时间';

-- ----------------------------------------------------------
-- 表27: 聊天会话表
-- ----------------------------------------------------------
CREATE TABLE chat_conversations (
    id              BIGSERIAL PRIMARY KEY,
    type            VARCHAR(20) DEFAULT 'SINGLE',
    related_order_id VARCHAR(32),
    last_message    TEXT,
    last_message_at TIMESTAMP,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE chat_conversations IS '聊天会话表';
COMMENT ON COLUMN chat_conversations.id IS '主键ID';
COMMENT ON COLUMN chat_conversations.type IS '会话类型: SINGLE-单聊, ORDER-订单咨询, CS-客服';
COMMENT ON COLUMN chat_conversations.related_order_id IS '关联订单编号';
COMMENT ON COLUMN chat_conversations.last_message IS '最后一条消息内容';
COMMENT ON COLUMN chat_conversations.last_message_at IS '最后消息时间';
COMMENT ON COLUMN chat_conversations.created_at IS '会话创建时间';
COMMENT ON COLUMN chat_conversations.updated_at IS '会话更新时间';

-- ----------------------------------------------------------
-- 表28: 聊天会话成员表
-- ----------------------------------------------------------
CREATE TABLE chat_conversation_members (
    id              BIGSERIAL PRIMARY KEY,
    conversation_id BIGINT NOT NULL REFERENCES chat_conversations(id),
    user_id         BIGINT NOT NULL REFERENCES users(id),
    unread_count    INTEGER DEFAULT 0,
    last_read_at    TIMESTAMP,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(conversation_id, user_id)
);

CREATE INDEX idx_ccm_user ON chat_conversation_members(user_id);
COMMENT ON TABLE chat_conversation_members IS '聊天会话成员表';
COMMENT ON COLUMN chat_conversation_members.id IS '主键ID';
COMMENT ON COLUMN chat_conversation_members.conversation_id IS '关联会话ID';
COMMENT ON COLUMN chat_conversation_members.user_id IS '会话成员用户ID';
COMMENT ON COLUMN chat_conversation_members.unread_count IS '未读消息数';
COMMENT ON COLUMN chat_conversation_members.last_read_at IS '最后已读时间';
COMMENT ON COLUMN chat_conversation_members.created_at IS '加入会话时间';

-- ----------------------------------------------------------
-- 表29: 拍卖表
-- ----------------------------------------------------------
CREATE TABLE auctions (
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

CREATE INDEX idx_auctions_car_id ON auctions(car_id);
CREATE INDEX idx_auctions_seller_id ON auctions(seller_id);
CREATE INDEX idx_auctions_status ON auctions(status);
CREATE INDEX idx_auctions_start_time ON auctions(start_time);
CREATE INDEX idx_auctions_end_time ON auctions(end_time);
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

-- ----------------------------------------------------------
-- 表30: 拍卖出价记录表
-- ----------------------------------------------------------
CREATE TABLE auction_bids (
    id          BIGINT PRIMARY KEY,
    auction_id  BIGINT NOT NULL,
    bidder_id   BIGINT NOT NULL,
    bid_price   DECIMAL(12,2) NOT NULL,
    bid_time    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_winning  BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_auction_bids_auction_id ON auction_bids(auction_id);
CREATE INDEX idx_auction_bids_bidder_id ON auction_bids(bidder_id);
CREATE INDEX idx_auction_bids_bid_time ON auction_bids(bid_time);
COMMENT ON TABLE auction_bids IS '拍卖出价记录表';
COMMENT ON COLUMN auction_bids.id IS '主键ID';
COMMENT ON COLUMN auction_bids.auction_id IS '关联拍卖ID';
COMMENT ON COLUMN auction_bids.bidder_id IS '出价者用户ID';
COMMENT ON COLUMN auction_bids.bid_price IS '出价金额(元)';
COMMENT ON COLUMN auction_bids.bid_time IS '出价时间';
COMMENT ON COLUMN auction_bids.is_winning IS '是否为当前最高出价';
COMMENT ON COLUMN auction_bids.created_at IS '创建时间';

-- ----------------------------------------------------------
-- 表31: 拍卖关注表
-- ----------------------------------------------------------
CREATE TABLE auction_watches (
    id         BIGINT PRIMARY KEY,
    auction_id BIGINT NOT NULL,
    user_id    BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(auction_id, user_id)
);

CREATE INDEX idx_auction_watches_user_id ON auction_watches(user_id);
COMMENT ON TABLE auction_watches IS '拍卖关注表';
COMMENT ON COLUMN auction_watches.id IS '主键ID';
COMMENT ON COLUMN auction_watches.auction_id IS '关联拍卖ID';
COMMENT ON COLUMN auction_watches.user_id IS '关注用户ID';
COMMENT ON COLUMN auction_watches.created_at IS '关注时间';


-- ===================== 3. 测试数据 =====================

-- ----- 用户 -----
INSERT INTO users (id, phone, nickname, shop_name, credit_grade, credit_score, deal_count, on_sale_count, certification_status, status)
VALUES (1, '13800138001', '诚信车行', '诚信二手车', 'A', 850, 156, 23, 'CERTIFIED', 'ACTIVE');

INSERT INTO users (id, phone, nickname, shop_name, credit_grade, credit_score, deal_count, on_sale_count, certification_status, status)
VALUES (2, '13800138002', '车坊张老板', '精品车坊', 'S', 920, 328, 45, 'CERTIFIED', 'ACTIVE');

-- ----- 品牌 -----
INSERT INTO brands (id, name, logo_url, first_letter, sort_order, status) VALUES
(1, '大众', 'https://example.com/vw.png', 'D', 1, 'ACTIVE'),
(2, '丰田', 'https://example.com/toyota.png', 'F', 2, 'ACTIVE'),
(3, '奔驰', 'https://example.com/benz.png', 'B', 3, 'ACTIVE'),
(4, '宝马', 'https://example.com/bmw.png', 'B', 4, 'ACTIVE'),
(5, '特斯拉', 'https://example.com/tesla.png', 'T', 5, 'ACTIVE');

-- ----- 车系 -----
INSERT INTO series (id, brand_id, name, sort_order, status) VALUES
(1, 1, '帕萨特', 1, 'ACTIVE'),
(2, 1, '速腾', 2, 'ACTIVE'),
(3, 2, '凯美瑞', 1, 'ACTIVE'),
(4, 3, 'E级', 1, 'ACTIVE'),
(5, 4, '5系', 1, 'ACTIVE'),
(6, 5, 'Model Y', 1, 'ACTIVE');

-- ----- 车型 -----
INSERT INTO models (id, series_id, name, year, guide_price, sort_order, status) VALUES
(1, 1, '330TSI 豪华版', 2024, 250000.00, 1, 'ACTIVE'),
(2, 2, '280TSI 舒适版', 2024, 180000.00, 1, 'ACTIVE'),
(3, 3, '2.5G 豪华版', 2024, 220000.00, 1, 'ACTIVE'),
(4, 4, 'E300L 时尚版', 2024, 550000.00, 1, 'ACTIVE'),
(5, 5, '530Li 尊享版', 2024, 520000.00, 1, 'ACTIVE'),
(6, 6, '长续航全轮驱动版', 2024, 300000.00, 1, 'ACTIVE');

-- ----- 车源 -----
INSERT INTO car_sources (id, user_id, brand_id, series_id, model_id, title, year, mileage, price, deposit, color, city_code, city_name, energy_type, export_countries, view_count, favorite_count, status, published_at) VALUES
(1, 1, 1, 1, 1, '大众帕萨特 2024款 330TSI 豪华版', 2024, 15000, 188000.00, 5000.00, '黑色', '010', '北京', 'GASOLINE', 'RU,KZ,AE', 1256, 58, 'ACTIVE', CURRENT_TIMESTAMP),
(2, 1, 1, 2, 2, '大众速腾 2024款 280TSI 舒适版', 2024, 8000, 138000.00, 3000.00, '银色', '010', '北京', 'GASOLINE', 'RU,KZ', 892, 32, 'ACTIVE', CURRENT_TIMESTAMP),
(3, 2, 2, 3, 3, '丰田凯美瑞 2024款 2.5G 豪华版', 2024, 12000, 168000.00, 4000.00, '白色', '020', '上海', 'GASOLINE', 'AE,AU,SA,SE', 2341, 87, 'ACTIVE', CURRENT_TIMESTAMP),
(4, 2, 3, 4, 4, '奔驰E级 2024款 E300L 时尚版', 2024, 5000, 458000.00, 10000.00, '黑色', '020', '上海', 'GASOLINE', 'AE,AU', 3567, 124, 'ACTIVE', CURRENT_TIMESTAMP),
(5, 1, 4, 5, 5, '宝马5系 2024款 530Li 尊享版', 2024, 8000, 438000.00, 10000.00, '白色', '010', '北京', 'GASOLINE', 'RU,KZ,AE,AU,SA,SE', 2891, 98, 'ACTIVE', CURRENT_TIMESTAMP),
(6, 2, 5, 6, 6, '特斯拉Model Y 2024款 长续航全轮驱动版', 2024, 3000, 268000.00, 8000.00, '红色', '021', '广州', 'PURE_ELECTRIC', 'AE,AU,SE', 4567, 156, 'ACTIVE', CURRENT_TIMESTAMP);

-- ----- 车源图片 -----
INSERT INTO car_images (car_id, image_url, image_type, sort_order) VALUES
(1, 'https://images.unsplash.com/photo-1552519507-da3b142c6e3d?w=800', 'EXTERIOR', 1),
(2, 'https://images.unsplash.com/photo-1549317668-b6ada16ea8b8?w=800', 'EXTERIOR', 1),
(3, 'https://images.unsplash.com/photo-1621007933163-fec1a6d10b6d?w=800', 'EXTERIOR', 1),
(4, 'https://images.unsplash.com/photo-1617531653332-bd46c24f2000?w=800', 'EXTERIOR', 1),
(5, 'https://images.unsplash.com/photo-1555215695-3004980ad54e?w=800', 'EXTERIOR', 1),
(6, 'https://images.unsplash.com/photo-1617788615603-6ce36e9b65c8?w=800', 'EXTERIOR', 1);

-- ----- 会员套餐 -----
INSERT INTO member_plans (name, level, price, duration_days, benefits, sort_order, status, created_at) VALUES
('月度会员', 'BRONZE', 99.00, 30, '{"freePublish": 10, "discountRate": 0.95, "aiAnalysis": true}', 3, 'ACTIVE', CURRENT_TIMESTAMP),
('季度会员', 'SILVER', 249.00, 90, '{"freePublish": 30, "discountRate": 0.90, "aiAnalysis": true}', 2, 'ACTIVE', CURRENT_TIMESTAMP),
('年度会员', 'GOLD', 799.00, 365, '{"freePublish": 100, "discountRate": 0.85, "aiAnalysis": true}', 1, 'ACTIVE', CURRENT_TIMESTAMP);


-- ===================== 4. 重置自增序列 =====================
SELECT setval('brands_id_seq', (SELECT COALESCE(MAX(id), 0) FROM brands));
SELECT setval('series_id_seq', (SELECT COALESCE(MAX(id), 0) FROM series));
SELECT setval('models_id_seq', (SELECT COALESCE(MAX(id), 0) FROM models));
SELECT setval('users_id_seq', (SELECT COALESCE(MAX(id), 0) FROM users));
SELECT setval('car_sources_id_seq', (SELECT COALESCE(MAX(id), 0) FROM car_sources));
SELECT setval('car_images_id_seq', (SELECT COALESCE(MAX(id), 0) FROM car_images));
SELECT setval('member_plans_id_seq', (SELECT COALESCE(MAX(id), 0) FROM member_plans));
