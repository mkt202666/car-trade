-- ============================================================
-- 5D好车 B2B 二手车交易平台 - 完整数据库初始化脚本
-- 数据库: PostgreSQL 16
-- 编码: UTF-8
-- 日期: 2026-06-10
-- 说明: 包含建库、建表、索引、注释、测试数据
-- ============================================================

-- ===================== 1. 创建数据库 =====================
CREATE DATABASE sxk WITH ENCODING 'UTF8' LC_COLLATE 'zh_CN.utf8' LC_CTYPE 'zh_CN.utf8' TEMPLATE template0;
ALTER DATABASE sxk SET search_path TO sxk;

-- ===================== 2. 创建数据表 =====================

-- ----------------------------------------------------------
-- 表1: 用户表
-- ----------------------------------------------------------
CREATE TABLE tc_users (
    id              BIGSERIAL PRIMARY KEY,
    phone           VARCHAR(20) NOT NULL UNIQUE,
    password        VARCHAR(100),
    nickname        VARCHAR(50),
    real_name       VARCHAR(50),
    avatar_url      VARCHAR(500),
    shop_name       VARCHAR(100),
    shop_logo       VARCHAR(500),
    shop_description TEXT,
    credit_code     VARCHAR(50),
    province        VARCHAR(50),
    city            VARCHAR(50),
    address         VARCHAR(200),
    id_card_number  VARCHAR(30),
    business_license_url VARCHAR(500),
    license_url     VARCHAR(500),
    id_card_front_url VARCHAR(500),
    id_card_back_url VARCHAR(500),
    id_card_image_url VARCHAR(500),
    store_image_url VARCHAR(500),
    deposit_balance BIGINT DEFAULT 0,
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
    business_license VARCHAR(200),
    reject_reason   TEXT,
    reviewer_id     BIGINT,
    reviewed_at     TIMESTAMP,
    login_fail_count INTEGER DEFAULT 0,
    locked_until    TIMESTAMP,
    notification_settings JSONB DEFAULT '{"system":true,"auto_promotion":true,"order":true,"contract":true,"deposit":true,"shop":true}',
    user_role VARCHAR(20) DEFAULT 'PERSONAL',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at      TIMESTAMP
);

CREATE INDEX idx_users_phone ON tc_users(phone);
CREATE INDEX idx_users_status ON tc_users(status);
COMMENT ON TABLE tc_users IS '用户表';
COMMENT ON COLUMN tc_users.id IS '主键ID';
COMMENT ON COLUMN tc_users.phone IS '手机号，唯一';
COMMENT ON COLUMN tc_users.password IS '密码';
COMMENT ON COLUMN tc_users.nickname IS '用户昵称';
COMMENT ON COLUMN tc_users.real_name IS '真实姓名';
COMMENT ON COLUMN tc_users.avatar_url IS '头像地址';
COMMENT ON COLUMN tc_users.shop_name IS '车行名称';
COMMENT ON COLUMN tc_users.shop_logo IS '车行Logo地址';
COMMENT ON COLUMN tc_users.shop_description IS '车行简介';
COMMENT ON COLUMN tc_users.credit_code IS '统一社会信用代码';
COMMENT ON COLUMN tc_users.province IS '所在省';
COMMENT ON COLUMN tc_users.city IS '所在市';
COMMENT ON COLUMN tc_users.address IS '实体店详细经营地址';
COMMENT ON COLUMN tc_users.id_card_number IS '身份证号码';
COMMENT ON COLUMN tc_users.business_license_url IS '营业执照图片URL';
COMMENT ON COLUMN tc_users.license_url IS '营业执照附件URL';
COMMENT ON COLUMN tc_users.id_card_front_url IS '身份证正面图片URL';
COMMENT ON COLUMN tc_users.id_card_back_url IS '身份证反面图片URL';
COMMENT ON COLUMN tc_users.id_card_image_url IS '申请人身份证图片URL';
COMMENT ON COLUMN tc_users.store_image_url IS '车行实体门店图片URL';
COMMENT ON COLUMN tc_users.deposit_balance IS '保证金余额（元）';
COMMENT ON COLUMN tc_users.credit_grade IS '信用等级: S-极佳, A-优秀, B-良好, C-一般, D-较差';
COMMENT ON COLUMN tc_users.credit_score IS '信用分数';
COMMENT ON COLUMN tc_users.notification_settings IS '通知订阅设置 JSON: {"system":bool,"auto_promotion":bool,"order":bool,"contract":bool,"deposit":bool,"shop":bool}';
COMMENT ON COLUMN tc_users.deal_count IS '累计成交订单数';
COMMENT ON COLUMN tc_users.on_sale_count IS '在售车源数量';
COMMENT ON COLUMN tc_users.view_count IS '累计被访问次数';
COMMENT ON COLUMN tc_users.view_count_today IS '今日被访问次数';
COMMENT ON COLUMN tc_users.message_count IS '累计消息数';
COMMENT ON COLUMN tc_users.message_count_today IS '今日消息数';
COMMENT ON COLUMN tc_users.follower_count IS '累计粉丝数';
COMMENT ON COLUMN tc_users.follower_count_today IS '今日新增粉丝数';
COMMENT ON COLUMN tc_users.member_expire_at IS '会员到期时间';
COMMENT ON COLUMN tc_users.certification_status IS '认证状态: UNCERTIFIED-未认证, PENDING-审核中, CERTIFIED-已认证, REJECTED-已拒绝';
COMMENT ON COLUMN tc_users.status IS '账号状态: ACTIVE-正常, FROZEN-冻结, DELETED-注销, SUSPENDED-暂停, INACTIVE-停业';
COMMENT ON COLUMN tc_users.business_license IS '营业执照号';
COMMENT ON COLUMN tc_users.reject_reason IS '最近一次审核拒绝原因';
COMMENT ON COLUMN tc_users.reviewer_id IS '审核人ID (FK -> admin_users.id)';
COMMENT ON COLUMN tc_users.reviewed_at IS '最近审核时间';
COMMENT ON COLUMN tc_users.created_at IS '创建时间';
COMMENT ON COLUMN tc_users.updated_at IS '更新时间';
COMMENT ON COLUMN tc_users.deleted_at IS '删除时间，软删除';
CREATE INDEX IF NOT EXISTS idx_users_user_role ON tc_users(user_role);
CREATE INDEX IF NOT EXISTS idx_users_role_status ON tc_users(user_role, status);

-- ----------------------------------------------------------
-- 表2: 汽车品牌表
-- ----------------------------------------------------------
CREATE TABLE tc_brands (
    id              SERIAL PRIMARY KEY,
    name            VARCHAR(50) NOT NULL,
    logo_url        VARCHAR(500),
    first_letter    VARCHAR(1),
    sort_order      INTEGER DEFAULT 0,
    status          VARCHAR(20) DEFAULT 'ACTIVE',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_brands_first_letter ON tc_brands(first_letter);
COMMENT ON TABLE tc_brands IS '汽车品牌表';
COMMENT ON COLUMN tc_brands.id IS '主键ID';
COMMENT ON COLUMN tc_brands.name IS '品牌名称';
COMMENT ON COLUMN tc_brands.logo_url IS '品牌logo地址';
COMMENT ON COLUMN tc_brands.first_letter IS '品牌首字母';
COMMENT ON COLUMN tc_brands.sort_order IS '排序权重';
COMMENT ON COLUMN tc_brands.status IS '状态: ACTIVE-启用, DISABLE-禁用';
COMMENT ON COLUMN tc_brands.created_at IS '创建时间';

-- ----------------------------------------------------------
-- 表3: 车系表
-- ----------------------------------------------------------
CREATE TABLE tc_series (
    id              SERIAL PRIMARY KEY,
    brand_id        INTEGER NOT NULL REFERENCES tc_brands(id),
    name            VARCHAR(50) NOT NULL,
    sort_order      INTEGER DEFAULT 0,
    status          VARCHAR(20) DEFAULT 'ACTIVE',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_series_brand_id ON tc_series(brand_id);
COMMENT ON TABLE tc_series IS '车系表';
COMMENT ON COLUMN tc_series.id IS '主键ID';
COMMENT ON COLUMN tc_series.brand_id IS '关联品牌ID';
COMMENT ON COLUMN tc_series.name IS '车系名称';
COMMENT ON COLUMN tc_series.sort_order IS '排序权重';
COMMENT ON COLUMN tc_series.status IS '状态: ACTIVE-启用, DISABLE-禁用';
COMMENT ON COLUMN tc_series.created_at IS '创建时间';

-- ----------------------------------------------------------
-- 表4: 车型表
-- ----------------------------------------------------------
CREATE TABLE tc_models (
    id              SERIAL PRIMARY KEY,
    series_id       INTEGER NOT NULL REFERENCES tc_series(id),
    name            VARCHAR(100) NOT NULL,
    year            INTEGER,
    guide_price     DECIMAL(12,2),
    sort_order      INTEGER DEFAULT 0,
    status          VARCHAR(20) DEFAULT 'ACTIVE',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_models_series_id ON tc_models(series_id);
COMMENT ON TABLE tc_models IS '车型表';
COMMENT ON COLUMN tc_models.id IS '主键ID';
COMMENT ON COLUMN tc_models.series_id IS '关联车系ID';
COMMENT ON COLUMN tc_models.name IS '车型名称';
COMMENT ON COLUMN tc_models.year IS '年款';
COMMENT ON COLUMN tc_models.guide_price IS '官方指导价';
COMMENT ON COLUMN tc_models.sort_order IS '排序权重';
COMMENT ON COLUMN tc_models.status IS '状态: ACTIVE-启用, DISABLE-禁用';
COMMENT ON COLUMN tc_models.created_at IS '创建时间';

-- ----------------------------------------------------------
-- 表5: 车源表
-- ----------------------------------------------------------
CREATE TABLE tc_car_sources (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES tc_users(id),
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
    video_url       VARCHAR(500),
    auction_status  VARCHAR(20),
    auction_end_time TIMESTAMP,
    view_count      BIGINT DEFAULT 0,
    favorite_count  INTEGER DEFAULT 0,
    recommended     BOOLEAN DEFAULT FALSE,
    status          VARCHAR(20) DEFAULT 'ACTIVE',
    published_at    TIMESTAMP,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at      TIMESTAMP,
    export_countries VARCHAR(100)
);

CREATE INDEX idx_car_sources_user_id ON tc_car_sources(user_id);
CREATE INDEX idx_car_sources_brand_series ON tc_car_sources(brand_id, series_id);
CREATE INDEX idx_car_sources_city ON tc_car_sources(city_code);
CREATE INDEX idx_car_sources_price ON tc_car_sources(price);
CREATE INDEX idx_car_sources_status ON tc_car_sources(status);
CREATE INDEX idx_car_sources_created_at ON tc_car_sources(created_at DESC);
COMMENT ON TABLE tc_car_sources IS '车源表';
COMMENT ON COLUMN tc_car_sources.id IS '主键ID';
COMMENT ON COLUMN tc_car_sources.user_id IS '发布用户ID';
COMMENT ON COLUMN tc_car_sources.brand_id IS '品牌ID';
COMMENT ON COLUMN tc_car_sources.series_id IS '车系ID';
COMMENT ON COLUMN tc_car_sources.model_id IS '车型ID';
COMMENT ON COLUMN tc_car_sources.title IS '车源标题';
COMMENT ON COLUMN tc_car_sources.vin IS '车架号';
COMMENT ON COLUMN tc_car_sources.year IS '车辆年款';
COMMENT ON COLUMN tc_car_sources.mileage IS '行驶里程(公里)';
COMMENT ON COLUMN tc_car_sources.price IS '售卖价格';
COMMENT ON COLUMN tc_car_sources.pricing_type IS '定价方式: FIXED-一口价, AUCTION-拍卖';
COMMENT ON COLUMN tc_car_sources.starting_price IS '起拍价';
COMMENT ON COLUMN tc_car_sources.ceiling_price IS '封顶价';
COMMENT ON COLUMN tc_car_sources.bid_increment IS '加价幅度';
COMMENT ON COLUMN tc_car_sources.deposit IS '保证金金额';
COMMENT ON COLUMN tc_car_sources.color IS '车身颜色';
COMMENT ON COLUMN tc_car_sources.city_code IS '城市编码';
COMMENT ON COLUMN tc_car_sources.city_name IS '城市名称';
COMMENT ON COLUMN tc_car_sources.energy_type IS '能源类型: GASOLINE-燃油, PURE_ELECTRIC-纯电, HYBRID-混动';
COMMENT ON COLUMN tc_car_sources.transmission IS '变速箱类型';
COMMENT ON COLUMN tc_car_sources.usage_type IS '使用性质';
COMMENT ON COLUMN tc_car_sources.owner_type IS '车主类型';
COMMENT ON COLUMN tc_car_sources.is_mortgaged IS '是否抵押车';
COMMENT ON COLUMN tc_car_sources.is_inherited IS '是否过户车';
COMMENT ON COLUMN tc_car_sources.registration_date IS '上牌日期';
COMMENT ON COLUMN tc_car_sources.insurance_expiry IS '保险到期日';
COMMENT ON COLUMN tc_car_sources.inspection_expiry IS '年检到期日';
COMMENT ON COLUMN tc_car_sources.production_date IS '出厂日期';
COMMENT ON COLUMN tc_car_sources.key_count IS '车钥匙数量';
COMMENT ON COLUMN tc_car_sources.description IS '车辆详情描述';
COMMENT ON COLUMN tc_car_sources.inspection_report_type IS '检测报告类型: LINK-链接, FILE-文件';
COMMENT ON COLUMN tc_car_sources.inspection_report_url IS '检测报告链接/文件URL';
COMMENT ON COLUMN tc_car_sources.certificate_materials IS '证件材料JSON';
COMMENT ON COLUMN tc_car_sources.support_lock_negotiation IS '支持锁车洽谈';
COMMENT ON COLUMN tc_car_sources.ai_auto_promote IS 'AI自动推广';
COMMENT ON COLUMN tc_car_sources.is_draft IS '是否草稿';
COMMENT ON COLUMN tc_car_sources.video_url IS '车源视频URL';
COMMENT ON COLUMN tc_car_sources.auction_status IS '拍卖状态: NONE-未拍卖, BIDDING-拍卖中, BIDDED-已参拍';
COMMENT ON COLUMN tc_car_sources.auction_end_time IS '拍卖结束时间';
COMMENT ON COLUMN tc_car_sources.view_count IS '浏览次数';
COMMENT ON COLUMN tc_car_sources.favorite_count IS '收藏数量';
COMMENT ON COLUMN tc_car_sources.status IS '车源状态: ACTIVE-正常, OFFLINE-下架';
COMMENT ON COLUMN tc_car_sources.published_at IS '发布时间';
COMMENT ON COLUMN tc_car_sources.created_at IS '创建时间';
COMMENT ON COLUMN tc_car_sources.updated_at IS '更新时间';
COMMENT ON COLUMN tc_car_sources.deleted_at IS '删除时间，软删除';
COMMENT ON COLUMN tc_car_sources.export_countries IS '可出口国家，逗号分隔';

-- ----------------------------------------------------------
-- 表6: 车源图片表
-- ----------------------------------------------------------
CREATE TABLE tc_car_images (
    id              BIGSERIAL PRIMARY KEY,
    car_id          BIGINT NOT NULL REFERENCES tc_car_sources(id),
    image_url       VARCHAR(500) NOT NULL,
    image_type      VARCHAR(20),
    sort_order      INTEGER DEFAULT 0,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_car_images_car_id ON tc_car_images(car_id);
COMMENT ON TABLE tc_car_images IS '车源图片表';
COMMENT ON COLUMN tc_car_images.id IS '主键ID';
COMMENT ON COLUMN tc_car_images.car_id IS '关联车源ID';
COMMENT ON COLUMN tc_car_images.image_url IS '图片地址';
COMMENT ON COLUMN tc_car_images.image_type IS '图片类型: EXTERIOR-外观, INTERIOR-内饰, DETAIL-细节, DEFECT-瑕疵';
COMMENT ON COLUMN tc_car_images.sort_order IS '图片排序';
COMMENT ON COLUMN tc_car_images.created_at IS '创建时间';

-- ----------------------------------------------------------
-- 表7: 车源标签表
-- ----------------------------------------------------------
CREATE TABLE tc_car_tags (
    id              BIGSERIAL PRIMARY KEY,
    car_id          BIGINT NOT NULL REFERENCES tc_car_sources(id),
    tag_type        VARCHAR(20) NOT NULL,
    tag_value       VARCHAR(50),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_car_tags_car_id ON tc_car_tags(car_id);
CREATE INDEX idx_car_tags_type ON tc_car_tags(tag_type);
COMMENT ON TABLE tc_car_tags IS '车源标签表';
COMMENT ON COLUMN tc_car_tags.id IS '主键ID';
COMMENT ON COLUMN tc_car_tags.car_id IS '关联车源ID';
COMMENT ON COLUMN tc_car_tags.tag_type IS '标签类型: DEPOSIT-保证金, EXPORT-出口, ENERGY-能源';
COMMENT ON COLUMN tc_car_tags.tag_value IS '标签值';
COMMENT ON COLUMN tc_car_tags.created_at IS '创建时间';

-- ----------------------------------------------------------
-- 表8: 车辆检测报告表
-- ----------------------------------------------------------
CREATE TABLE tc_car_inspections (
    id              BIGSERIAL PRIMARY KEY,
    car_id          BIGINT NOT NULL REFERENCES tc_car_sources(id),
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

CREATE INDEX idx_car_inspections_car_id ON tc_car_inspections(car_id);
COMMENT ON TABLE tc_car_inspections IS '车辆检测报告表';
COMMENT ON COLUMN tc_car_inspections.id IS '主键ID';
COMMENT ON COLUMN tc_car_inspections.car_id IS '关联车源ID';
COMMENT ON COLUMN tc_car_inspections.overall_condition IS '整体车况: NORMAL-非事故车, ACCIDENT-事故车';
COMMENT ON COLUMN tc_car_inspections.paint IS '漆面: ORIGINAL-原漆, SCRATCH-划痕剐蹭, PAINTED-喷漆';
COMMENT ON COLUMN tc_car_inspections.structure IS '车身结构';
COMMENT ON COLUMN tc_car_inspections.engine IS '发动机状态';
COMMENT ON COLUMN tc_car_inspections.transmission IS '变速箱状态';
COMMENT ON COLUMN tc_car_inspections.transfer_count IS '过户次数';
COMMENT ON COLUMN tc_car_inspections.mileage_type IS '公里数类型: ACTUAL-实表, TAMPERED-调表, DISPLAY-表显';
COMMENT ON COLUMN tc_car_inspections.description IS '检测备注说明';
COMMENT ON COLUMN tc_car_inspections.abnormal_photos IS '异常图片集合，JSON格式';
COMMENT ON COLUMN tc_car_inspections.created_at IS '创建时间';
COMMENT ON COLUMN tc_car_inspections.updated_at IS '更新时间';

-- ----------------------------------------------------------
-- 表9: 订单表
-- ----------------------------------------------------------
CREATE TABLE tc_orders (
    id              VARCHAR(32) PRIMARY KEY,
    car_id          BIGINT NOT NULL REFERENCES tc_car_sources(id),
    buyer_id        BIGINT NOT NULL REFERENCES tc_users(id),
    seller_id       BIGINT NOT NULL REFERENCES tc_users(id),
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

CREATE INDEX idx_orders_car_id ON tc_orders(car_id);
CREATE INDEX idx_orders_buyer_id ON tc_orders(buyer_id);
CREATE INDEX idx_orders_seller_id ON tc_orders(seller_id);
CREATE INDEX idx_orders_status ON tc_orders(status);
CREATE INDEX idx_orders_created_at ON tc_orders(created_at DESC);
COMMENT ON TABLE tc_orders IS '订单表';
COMMENT ON COLUMN tc_orders.id IS '订单编号';
COMMENT ON COLUMN tc_orders.car_id IS '关联车源ID';
COMMENT ON COLUMN tc_orders.buyer_id IS '买家用户ID';
COMMENT ON COLUMN tc_orders.seller_id IS '卖家用户ID';
COMMENT ON COLUMN tc_orders.total_price IS '订单总金额';
COMMENT ON COLUMN tc_orders.deposit_amount IS '保证金金额';
COMMENT ON COLUMN tc_orders.buyer_deposit_paid IS '买家是否已交保证金';
COMMENT ON COLUMN tc_orders.buyer_deposit_paid_at IS '买家保证金缴纳时间';
COMMENT ON COLUMN tc_orders.seller_deposit_paid IS '卖家是否已交保证金';
COMMENT ON COLUMN tc_orders.seller_deposit_paid_at IS '卖家保证金缴纳时间';
COMMENT ON COLUMN tc_orders.status IS '订单状态: PENDING_CONFIRM-待确认, TRADING-交易中, DISPUTE-争议中, COMPLETED-已完成, CANCELLED-已终止';
COMMENT ON COLUMN tc_orders.contract_no IS '合同编号';
COMMENT ON COLUMN tc_orders.contract_content IS '合同内容';
COMMENT ON COLUMN tc_orders.contract_submitted IS '合同是否已提交';
COMMENT ON COLUMN tc_orders.contract_submitted_at IS '合同提交时间';
COMMENT ON COLUMN tc_orders.contract_confirmed IS '合同是否已确认';
COMMENT ON COLUMN tc_orders.contract_confirmed_at IS '合同确认时间';
COMMENT ON COLUMN tc_orders.terminate_count IS '终止交易次数';
COMMENT ON COLUMN tc_orders.terminate_limit IS '每日终止交易限制';
COMMENT ON COLUMN tc_orders.terminate_reason IS '终止交易原因';
COMMENT ON COLUMN tc_orders.last_terminate_at IS '最后终止交易时间';
COMMENT ON COLUMN tc_orders.remark IS '订单备注';
COMMENT ON COLUMN tc_orders.cancel_reason IS '取消原因';
COMMENT ON COLUMN tc_orders.completed_at IS '交易完成时间';
COMMENT ON COLUMN tc_orders.cancelled_at IS '订单取消时间';
COMMENT ON COLUMN tc_orders.created_at IS '创建时间';
COMMENT ON COLUMN tc_orders.updated_at IS '更新时间';

-- ----------------------------------------------------------
-- 表10: 订单车况信息表
-- ----------------------------------------------------------
CREATE TABLE tc_order_inspections (
    id              BIGSERIAL PRIMARY KEY,
    order_id        VARCHAR(32) NOT NULL REFERENCES tc_orders(id),
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

CREATE INDEX idx_order_inspections_order_id ON tc_order_inspections(order_id);
COMMENT ON TABLE tc_order_inspections IS '订单车况信息表';
COMMENT ON COLUMN tc_order_inspections.id IS '主键ID';
COMMENT ON COLUMN tc_order_inspections.order_id IS '关联订单编号';
COMMENT ON COLUMN tc_order_inspections.overall_condition IS '整体车况';
COMMENT ON COLUMN tc_order_inspections.paint IS '漆面情况';
COMMENT ON COLUMN tc_order_inspections.structure IS '车身结构';
COMMENT ON COLUMN tc_order_inspections.engine IS '发动机状态';
COMMENT ON COLUMN tc_order_inspections.transmission IS '变速箱状态';
COMMENT ON COLUMN tc_order_inspections.transfer_count IS '过户次数';
COMMENT ON COLUMN tc_order_inspections.mileage_type IS '公里数类型';
COMMENT ON COLUMN tc_order_inspections.description IS '车况描述';
COMMENT ON COLUMN tc_order_inspections.abnormal_photos IS '异常照片集合，JSON格式';
COMMENT ON COLUMN tc_order_inspections.materials IS '随车资料，JSON格式';
COMMENT ON COLUMN tc_order_inspections.created_at IS '创建时间';
COMMENT ON COLUMN tc_order_inspections.updated_at IS '更新时间';

-- ----------------------------------------------------------
-- 表11: 订单日志表
-- ----------------------------------------------------------
CREATE TABLE tc_order_logs (
    id              BIGSERIAL PRIMARY KEY,
    order_id        VARCHAR(32) NOT NULL REFERENCES tc_orders(id),
    action          VARCHAR(50) NOT NULL,
    operator_id     BIGINT,
    operator_name   VARCHAR(50),
    remark          TEXT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_order_logs_order_id ON tc_order_logs(order_id);
COMMENT ON TABLE tc_order_logs IS '订单日志表';
COMMENT ON COLUMN tc_order_logs.id IS '主键ID';
COMMENT ON COLUMN tc_order_logs.order_id IS '关联订单编号';
COMMENT ON COLUMN tc_order_logs.action IS '操作行为';
COMMENT ON COLUMN tc_order_logs.operator_id IS '操作人ID';
COMMENT ON COLUMN tc_order_logs.operator_name IS '操作人名称';
COMMENT ON COLUMN tc_order_logs.remark IS '日志备注';
COMMENT ON COLUMN tc_order_logs.created_at IS '操作时间';

-- ----------------------------------------------------------
-- 表12: 争议表
-- ----------------------------------------------------------
CREATE TABLE tc_disputes (
    id              BIGSERIAL PRIMARY KEY,
    order_id        VARCHAR(32) NOT NULL REFERENCES tc_orders(id),
    initiator_id    BIGINT NOT NULL REFERENCES tc_users(id),
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

CREATE INDEX idx_disputes_order_id ON tc_disputes(order_id);
CREATE INDEX idx_disputes_status ON tc_disputes(status);
COMMENT ON TABLE tc_disputes IS '争议表';
COMMENT ON COLUMN tc_disputes.id IS '主键ID';
COMMENT ON COLUMN tc_disputes.order_id IS '关联订单编号';
COMMENT ON COLUMN tc_disputes.initiator_id IS '发起用户ID';
COMMENT ON COLUMN tc_disputes.reason IS '争议原因';
COMMENT ON COLUMN tc_disputes.description IS '争议详细描述';
COMMENT ON COLUMN tc_disputes.evidence IS '举证材料，JSON格式';
COMMENT ON COLUMN tc_disputes.status IS '状态: PENDING-待处理, PROCESSING-处理中, RESOLVED-已解决, REJECTED-已驳回';
COMMENT ON COLUMN tc_disputes.result IS '处理结果';
COMMENT ON COLUMN tc_disputes.handler_id IS '处理人ID';
COMMENT ON COLUMN tc_disputes.handled_at IS '处理完成时间';
COMMENT ON COLUMN tc_disputes.created_at IS '创建时间';
COMMENT ON COLUMN tc_disputes.updated_at IS '更新时间';

-- ----------------------------------------------------------
-- 表13: 电子合同表
-- ----------------------------------------------------------
CREATE TABLE tc_contracts (
    id              BIGSERIAL PRIMARY KEY,
    order_id        VARCHAR(32) NOT NULL REFERENCES tc_orders(id),
    contract_no     VARCHAR(32) NOT NULL UNIQUE,
    template_id     VARCHAR(50),
    title           VARCHAR(200),
    content         TEXT,
    buyer_id        BIGINT NOT NULL REFERENCES tc_users(id),
    seller_id       BIGINT NOT NULL REFERENCES tc_users(id),
    buyer_signed    BOOLEAN DEFAULT FALSE,
    buyer_signed_at TIMESTAMP,
    seller_signed   BOOLEAN DEFAULT FALSE,
    seller_signed_at TIMESTAMP,
    buyer_signature_url VARCHAR(500),
    seller_signature_url VARCHAR(500),
    status          VARCHAR(20) DEFAULT 'DRAFT',
    file_url        VARCHAR(500),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_contracts_order ON tc_contracts(order_id);
CREATE INDEX idx_contracts_no ON tc_contracts(contract_no);
COMMENT ON TABLE tc_contracts IS '电子合同表';
COMMENT ON COLUMN tc_contracts.id IS '主键ID';
COMMENT ON COLUMN tc_contracts.order_id IS '关联订单编号';
COMMENT ON COLUMN tc_contracts.contract_no IS '合同编号，唯一';
COMMENT ON COLUMN tc_contracts.template_id IS '合同模板ID';
COMMENT ON COLUMN tc_contracts.title IS '合同标题';
COMMENT ON COLUMN tc_contracts.content IS '合同正文内容';
COMMENT ON COLUMN tc_contracts.buyer_id IS '买家ID';
COMMENT ON COLUMN tc_contracts.seller_id IS '卖家ID';
COMMENT ON COLUMN tc_contracts.buyer_signed IS '买家是否已签署';
COMMENT ON COLUMN tc_contracts.buyer_signed_at IS '买家签署时间';
COMMENT ON COLUMN tc_contracts.seller_signed IS '卖家是否已签署';
COMMENT ON COLUMN tc_contracts.seller_signed_at IS '卖家签署时间';
COMMENT ON COLUMN tc_contracts.buyer_signature_url IS '买家手写签名图片URL';
COMMENT ON COLUMN tc_contracts.seller_signature_url IS '卖家手写签名图片URL';
COMMENT ON COLUMN tc_contracts.status IS '状态: DRAFT-草稿, PENDING_SIGN-待签署, SIGNED-已签署, ARCHIVED-已归档';
COMMENT ON COLUMN tc_contracts.file_url IS '合同文件地址';
COMMENT ON COLUMN tc_contracts.created_at IS '创建时间';
COMMENT ON COLUMN tc_contracts.updated_at IS '更新时间';

-- ----------------------------------------------------------
-- 表14: 保证金账户表
-- ----------------------------------------------------------
CREATE TABLE tc_deposit_accounts (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL UNIQUE REFERENCES tc_users(id),
    balance         DECIMAL(10,2) DEFAULT 0,
    frozen_amount   DECIMAL(10,2) DEFAULT 0,
    total_deposit   DECIMAL(10,2) DEFAULT 0,
    status          VARCHAR(20) DEFAULT 'ACTIVE',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_deposit_accounts_user_id ON tc_deposit_accounts(user_id);
COMMENT ON TABLE tc_deposit_accounts IS '保证金账户表';
COMMENT ON COLUMN tc_deposit_accounts.id IS '主键ID';
COMMENT ON COLUMN tc_deposit_accounts.user_id IS '关联用户ID，唯一';
COMMENT ON COLUMN tc_deposit_accounts.balance IS '可用余额';
COMMENT ON COLUMN tc_deposit_accounts.frozen_amount IS '冻结金额';
COMMENT ON COLUMN tc_deposit_accounts.total_deposit IS '累计充值金额';
COMMENT ON COLUMN tc_deposit_accounts.status IS '账户状态: ACTIVE-正常, DISABLE-禁用';
COMMENT ON COLUMN tc_deposit_accounts.created_at IS '创建时间';
COMMENT ON COLUMN tc_deposit_accounts.updated_at IS '更新时间';

-- ----------------------------------------------------------
-- 表15: 保证金流水表
-- ----------------------------------------------------------
CREATE TABLE tc_deposit_records (
    id              BIGSERIAL PRIMARY KEY,
    account_id      BIGINT NOT NULL REFERENCES tc_deposit_accounts(id),
    user_id         BIGINT,
    order_id        VARCHAR(32),
    type            VARCHAR(20) NOT NULL,
    amount          DECIMAL(10,2) NOT NULL,
    balance_after   DECIMAL(10,2),
    remark          VARCHAR(200),
    operator_id     BIGINT,
    status          VARCHAR(20) DEFAULT 'SUCCESS',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_deposit_records_account_id ON tc_deposit_records(account_id);
CREATE INDEX idx_deposit_records_user_id ON tc_deposit_records(user_id);
CREATE INDEX idx_deposit_records_operator_id ON tc_deposit_records(operator_id);
CREATE INDEX idx_deposit_records_created_at ON tc_deposit_records(created_at DESC);
COMMENT ON TABLE tc_deposit_records IS '保证金流水表';
COMMENT ON COLUMN tc_deposit_records.id IS '主键ID';
COMMENT ON COLUMN tc_deposit_records.user_id IS '关联用户ID (FK -> tc_users.id)';
COMMENT ON COLUMN tc_deposit_records.operator_id IS '操作人ID (FK -> admin_users.id)';
COMMENT ON COLUMN tc_deposit_records.status IS '流水状态: PENDING / SUCCESS / FAILED';
COMMENT ON COLUMN tc_deposit_records.account_id IS '关联保证金账户ID';
COMMENT ON COLUMN tc_deposit_records.order_id IS '关联订单编号';
COMMENT ON COLUMN tc_deposit_records.type IS '类型: RECHARGE-充值, WITHDRAW-提现, PAY-支付, REFUND-退款, FREEZE-冻结, UNFREEZE-解冻';
COMMENT ON COLUMN tc_deposit_records.amount IS '变动金额';
COMMENT ON COLUMN tc_deposit_records.balance_after IS '变动后余额';
COMMENT ON COLUMN tc_deposit_records.remark IS '流水备注';
COMMENT ON COLUMN tc_deposit_records.created_at IS '流水发生时间';

-- ----------------------------------------------------------
-- 表16: 信用账户表
-- ----------------------------------------------------------
CREATE TABLE tc_credit_accounts (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL UNIQUE REFERENCES tc_users(id),
    credit_limit    DECIMAL(12,2) DEFAULT 0,
    used_amount     DECIMAL(12,2) DEFAULT 0,
    available_amount DECIMAL(12,2) DEFAULT 0,
    status          VARCHAR(20) DEFAULT 'ACTIVE',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_credit_accounts_user_id ON tc_credit_accounts(user_id);
COMMENT ON TABLE tc_credit_accounts IS '信用账户表';
COMMENT ON COLUMN tc_credit_accounts.id IS '主键ID';
COMMENT ON COLUMN tc_credit_accounts.user_id IS '关联用户ID，唯一';
COMMENT ON COLUMN tc_credit_accounts.credit_limit IS '信用额度';
COMMENT ON COLUMN tc_credit_accounts.used_amount IS '已使用额度';
COMMENT ON COLUMN tc_credit_accounts.available_amount IS '可用额度';
COMMENT ON COLUMN tc_credit_accounts.status IS '账户状态: ACTIVE-正常, DISABLE-禁用';
COMMENT ON COLUMN tc_credit_accounts.created_at IS '创建时间';
COMMENT ON COLUMN tc_credit_accounts.updated_at IS '更新时间';

-- ----------------------------------------------------------
-- 表17: 消息表
-- ----------------------------------------------------------
CREATE TABLE tc_messages (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES tc_users(id),
    sender_id       BIGINT,
    type            VARCHAR(20) NOT NULL,
    title           VARCHAR(200),
    content         TEXT,
    is_read         BOOLEAN DEFAULT FALSE,
    related_id      VARCHAR(50),
    related_type    VARCHAR(20),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_messages_user_id ON tc_messages(user_id);
CREATE INDEX idx_messages_sender_id ON tc_messages(sender_id);
CREATE INDEX idx_messages_type ON tc_messages(type);
CREATE INDEX idx_messages_is_read ON tc_messages(is_read);
CREATE INDEX idx_messages_created_at ON tc_messages(created_at DESC);
COMMENT ON TABLE tc_messages IS '消息表';
COMMENT ON COLUMN tc_messages.id IS '主键ID';
COMMENT ON COLUMN tc_messages.user_id IS '接收用户ID';
COMMENT ON COLUMN tc_messages.sender_id IS '发送人ID(系统消息可为NULL)';
COMMENT ON COLUMN tc_messages.type IS '消息类型: SYSTEM-系统消息, TRADE-交易通知, ACTIVITY-活动通知, AUTO_PROMOTION-自动推广, CHAT-聊天消息, TEAM_APPLICATION-车行成员申请, DEPOSIT_WARNING-保证金不足';
COMMENT ON COLUMN tc_messages.title IS '消息标题';
COMMENT ON COLUMN tc_messages.content IS '消息内容';
COMMENT ON COLUMN tc_messages.is_read IS '是否已读: false-未读, true-已读';
COMMENT ON COLUMN tc_messages.related_id IS '关联业务ID';
COMMENT ON COLUMN tc_messages.related_type IS '关联业务类型';
COMMENT ON COLUMN tc_messages.created_at IS '消息创建时间';

-- ----------------------------------------------------------
-- 表18: 用户收藏表
-- ----------------------------------------------------------
CREATE TABLE tc_user_favorites (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES tc_users(id),
    car_id          BIGINT NOT NULL REFERENCES tc_car_sources(id),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, car_id)
);

CREATE INDEX idx_user_favorites_user_id ON tc_user_favorites(user_id);
CREATE INDEX idx_user_favorites_car_id ON tc_user_favorites(car_id);
COMMENT ON TABLE tc_user_favorites IS '用户收藏表';
COMMENT ON COLUMN tc_user_favorites.id IS '主键ID';
COMMENT ON COLUMN tc_user_favorites.user_id IS '用户ID';
COMMENT ON COLUMN tc_user_favorites.car_id IS '收藏车源ID';
COMMENT ON COLUMN tc_user_favorites.created_at IS '收藏时间';

-- ----------------------------------------------------------
-- 表19: 用户关注表
-- ----------------------------------------------------------
CREATE TABLE tc_user_follows (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES tc_users(id),
    follow_user_id  BIGINT NOT NULL REFERENCES tc_users(id),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, follow_user_id)
);

CREATE INDEX idx_user_follows_user ON tc_user_follows(user_id);
CREATE INDEX idx_user_follows_follow ON tc_user_follows(follow_user_id);
COMMENT ON TABLE tc_user_follows IS '用户关注表（关注卖家）';
COMMENT ON COLUMN tc_user_follows.id IS '主键ID';
COMMENT ON COLUMN tc_user_follows.user_id IS '关注人ID';
COMMENT ON COLUMN tc_user_follows.follow_user_id IS '被关注人ID';
COMMENT ON COLUMN tc_user_follows.created_at IS '关注时间';

-- ----------------------------------------------------------
-- 表20: 浏览记录表
-- ----------------------------------------------------------
CREATE TABLE tc_browsing_history (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES tc_users(id),
    car_id          BIGINT NOT NULL REFERENCES tc_car_sources(id),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_browsing_history_user ON tc_browsing_history(user_id, created_at DESC);
COMMENT ON TABLE tc_browsing_history IS '浏览记录表';
COMMENT ON COLUMN tc_browsing_history.id IS '主键ID';
COMMENT ON COLUMN tc_browsing_history.user_id IS '浏览用户ID';
COMMENT ON COLUMN tc_browsing_history.car_id IS '浏览车源ID';
COMMENT ON COLUMN tc_browsing_history.created_at IS '浏览时间';

-- ----------------------------------------------------------
-- 表21: 车行成员表
-- ----------------------------------------------------------
CREATE TABLE tc_shop_members (
    id              BIGSERIAL PRIMARY KEY,
    shop_user_id    BIGINT NOT NULL REFERENCES tc_users(id),
    member_user_id  BIGINT NOT NULL REFERENCES tc_users(id),
    role            VARCHAR(20) DEFAULT 'MEMBER',
    status          VARCHAR(20) DEFAULT 'PENDING',
    applied_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    approved_at     TIMESTAMP,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(shop_user_id, member_user_id)
);

CREATE INDEX idx_shop_members_shop ON tc_shop_members(shop_user_id);
CREATE INDEX idx_shop_members_member ON tc_shop_members(member_user_id);
COMMENT ON TABLE tc_shop_members IS '车行成员表';
COMMENT ON COLUMN tc_shop_members.id IS '主键ID';
COMMENT ON COLUMN tc_shop_members.shop_user_id IS '车行主用户ID';
COMMENT ON COLUMN tc_shop_members.member_user_id IS '成员用户ID';
COMMENT ON COLUMN tc_shop_members.role IS '角色: OWNER-车行主, ADMIN-管理员, MEMBER-成员';
COMMENT ON COLUMN tc_shop_members.status IS '状态: PENDING-待审批, ACTIVE-已加入, REJECTED-已拒绝';
COMMENT ON COLUMN tc_shop_members.applied_at IS '申请时间';
COMMENT ON COLUMN tc_shop_members.approved_at IS '审批通过时间';
COMMENT ON COLUMN tc_shop_members.created_at IS '创建时间';
COMMENT ON COLUMN tc_shop_members.updated_at IS '更新时间';

-- ----------------------------------------------------------
-- 表22: 优惠券定义表
-- ----------------------------------------------------------
CREATE TABLE tc_coupons (
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
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_coupons_status ON tc_coupons(status);
COMMENT ON TABLE tc_coupons IS '优惠券定义表';
COMMENT ON COLUMN tc_coupons.id IS '主键ID';
COMMENT ON COLUMN tc_coupons.name IS '优惠券名称';
COMMENT ON COLUMN tc_coupons.type IS '券类型: CASH-现金券, DISCOUNT-折扣券, FREE_SHIPPING-免运费';
COMMENT ON COLUMN tc_coupons.value IS '券面额/折扣值';
COMMENT ON COLUMN tc_coupons.min_amount IS '使用最低金额门槛';
COMMENT ON COLUMN tc_coupons.total_count IS '发行总数量';
COMMENT ON COLUMN tc_coupons.remain_count IS '剩余可领取数量';
COMMENT ON COLUMN tc_coupons.start_at IS '生效开始时间';
COMMENT ON COLUMN tc_coupons.end_at IS '失效结束时间';
COMMENT ON COLUMN tc_coupons.status IS '状态: ACTIVE-正常, DISABLE-下架';
COMMENT ON COLUMN tc_coupons.created_at IS '创建时间';
COMMENT ON COLUMN tc_coupons.updated_at IS '更新时间';

-- ----------------------------------------------------------
-- 表23: 用户优惠券表
-- ----------------------------------------------------------
CREATE TABLE tc_user_coupons (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES tc_users(id),
    coupon_id       BIGINT NOT NULL REFERENCES tc_coupons(id),
    used_at         TIMESTAMP,
    order_id        VARCHAR(32),
    status          VARCHAR(20) DEFAULT 'UNUSED',
    received_at     TIMESTAMP,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_coupons_user ON tc_user_coupons(user_id);
COMMENT ON TABLE tc_user_coupons IS '用户优惠券表';
COMMENT ON COLUMN tc_user_coupons.id IS '主键ID';
COMMENT ON COLUMN tc_user_coupons.user_id IS '用户ID';
COMMENT ON COLUMN tc_user_coupons.coupon_id IS '关联优惠券ID';
COMMENT ON COLUMN tc_user_coupons.used_at IS '使用时间';
COMMENT ON COLUMN tc_user_coupons.order_id IS '使用关联订单编号';
COMMENT ON COLUMN tc_user_coupons.status IS '状态: UNUSED-未使用, USED-已使用, EXPIRED-已过期';
COMMENT ON COLUMN tc_user_coupons.received_at IS '领取时间(与created_at一致,便于业务字段单独处理)';
COMMENT ON COLUMN tc_user_coupons.created_at IS '创建时间';

-- ----------------------------------------------------------
-- 表24: 会员方案表
-- ----------------------------------------------------------
CREATE TABLE tc_member_plans (
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

COMMENT ON TABLE tc_member_plans IS '会员方案表';
COMMENT ON COLUMN tc_member_plans.id IS '主键ID';
COMMENT ON COLUMN tc_member_plans.name IS '套餐名称';
COMMENT ON COLUMN tc_member_plans.level IS '等级: BRONZE-青铜, SILVER-白银, GOLD-黄金, DIAMOND-钻石';
COMMENT ON COLUMN tc_member_plans.price IS '套餐价格';
COMMENT ON COLUMN tc_member_plans.duration_days IS '有效时长(天)';
COMMENT ON COLUMN tc_member_plans.benefits IS '权益JSON: {"free_publish": 50, "discount_rate": 0.9, "ai_analysis": true}';
COMMENT ON COLUMN tc_member_plans.sort_order IS '排序权重';
COMMENT ON COLUMN tc_member_plans.status IS '状态: ACTIVE-上架, DISABLE-下架';
COMMENT ON COLUMN tc_member_plans.created_at IS '创建时间';

-- ----------------------------------------------------------
-- 表25: 用户会员表
-- ----------------------------------------------------------
CREATE TABLE tc_user_membership (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES tc_users(id),
    plan_id         BIGINT NOT NULL REFERENCES tc_member_plans(id),
    level           VARCHAR(20) NOT NULL,
    start_at        TIMESTAMP NOT NULL,
    expire_at       TIMESTAMP NOT NULL,
    end_at          TIMESTAMP,
    status          VARCHAR(20) DEFAULT 'ACTIVE',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_membership_user ON tc_user_membership(user_id);
COMMENT ON TABLE tc_user_membership IS '用户会员表';
COMMENT ON COLUMN tc_user_membership.id IS '主键ID';
COMMENT ON COLUMN tc_user_membership.user_id IS '用户ID';
COMMENT ON COLUMN tc_user_membership.plan_id IS '购买会员套餐ID';
COMMENT ON COLUMN tc_user_membership.level IS '会员等级';
COMMENT ON COLUMN tc_user_membership.start_at IS '会员生效时间';
COMMENT ON COLUMN tc_user_membership.expire_at IS '会员到期时间';
COMMENT ON COLUMN tc_user_membership.end_at IS '结束时间(用于历史归档或退款标记)';
COMMENT ON COLUMN tc_user_membership.status IS '状态: ACTIVE-有效, EXPIRED-已过期, CANCELLED-已取消';
COMMENT ON COLUMN tc_user_membership.created_at IS '开通时间';
COMMENT ON COLUMN tc_user_membership.updated_at IS '更新时间';

-- ----------------------------------------------------------
-- 表26: 在线客服工单表
-- ----------------------------------------------------------
CREATE TABLE tc_customer_service_tickets (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES tc_users(id),
    title           VARCHAR(200),
    category        VARCHAR(50),
    status          VARCHAR(20) DEFAULT 'PENDING',
    priority        VARCHAR(20) DEFAULT 'NORMAL',
    handler_id      BIGINT,
    handled_at      TIMESTAMP,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_cs_tickets_user ON tc_customer_service_tickets(user_id);
CREATE INDEX idx_cs_tickets_status ON tc_customer_service_tickets(status);
COMMENT ON TABLE tc_customer_service_tickets IS '在线客服工单表';
COMMENT ON COLUMN tc_customer_service_tickets.id IS '主键ID';
COMMENT ON COLUMN tc_customer_service_tickets.user_id IS '提交工单用户ID';
COMMENT ON COLUMN tc_customer_service_tickets.title IS '工单标题';
COMMENT ON COLUMN tc_customer_service_tickets.category IS '工单分类';
COMMENT ON COLUMN tc_customer_service_tickets.status IS '状态: PENDING-待处理, PROCESSING-处理中, RESOLVED-已解决, CLOSED-已关闭';
COMMENT ON COLUMN tc_customer_service_tickets.priority IS '优先级: LOW-低, NORMAL-普通, HIGH-高, URGENT-紧急';
COMMENT ON COLUMN tc_customer_service_tickets.handler_id IS '处理客服ID';
COMMENT ON COLUMN tc_customer_service_tickets.handled_at IS '处理完成时间';
COMMENT ON COLUMN tc_customer_service_tickets.created_at IS '工单创建时间';
COMMENT ON COLUMN tc_customer_service_tickets.updated_at IS '工单更新时间';

-- ----------------------------------------------------------
-- 表27: 聊天会话表
-- ----------------------------------------------------------
CREATE TABLE tc_chat_conversations (
    id              BIGSERIAL PRIMARY KEY,
    type            VARCHAR(20) DEFAULT 'SINGLE',
    related_order_id VARCHAR(32),
    last_message    TEXT,
    last_message_at TIMESTAMP,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE tc_chat_conversations IS '聊天会话表';
COMMENT ON COLUMN tc_chat_conversations.id IS '主键ID';
COMMENT ON COLUMN tc_chat_conversations.type IS '会话类型: SINGLE-单聊, ORDER-订单咨询, CS-客服';
COMMENT ON COLUMN tc_chat_conversations.related_order_id IS '关联订单编号';
COMMENT ON COLUMN tc_chat_conversations.last_message IS '最后一条消息内容';
COMMENT ON COLUMN tc_chat_conversations.last_message_at IS '最后消息时间';
COMMENT ON COLUMN tc_chat_conversations.created_at IS '会话创建时间';
COMMENT ON COLUMN tc_chat_conversations.updated_at IS '会话更新时间';

-- ----------------------------------------------------------
-- 表28: 聊天会话成员表
-- ----------------------------------------------------------
CREATE TABLE tc_chat_conversation_members (
    id              BIGSERIAL PRIMARY KEY,
    conversation_id BIGINT NOT NULL REFERENCES tc_chat_conversations(id),
    user_id         BIGINT NOT NULL REFERENCES tc_users(id),
    unread_count    INTEGER DEFAULT 0,
    last_read_at    TIMESTAMP,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(conversation_id, user_id)
);

CREATE INDEX idx_ccm_user ON tc_chat_conversation_members(user_id);
COMMENT ON TABLE tc_chat_conversation_members IS '聊天会话成员表';
COMMENT ON COLUMN tc_chat_conversation_members.id IS '主键ID';
COMMENT ON COLUMN tc_chat_conversation_members.conversation_id IS '关联会话ID';
COMMENT ON COLUMN tc_chat_conversation_members.user_id IS '会话成员用户ID';
COMMENT ON COLUMN tc_chat_conversation_members.unread_count IS '未读消息数';
COMMENT ON COLUMN tc_chat_conversation_members.last_read_at IS '最后已读时间';
COMMENT ON COLUMN tc_chat_conversation_members.created_at IS '加入会话时间';

-- ----------------------------------------------------------
-- 表29: 聊天消息表
-- ----------------------------------------------------------
CREATE TABLE tc_chat_messages (
    id              BIGSERIAL PRIMARY KEY,
    conversation_id BIGINT NOT NULL REFERENCES tc_chat_conversations(id),
    sender_id       BIGINT,
    content         TEXT,
    message_type    VARCHAR(20),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_chat_messages_conv ON tc_chat_messages(conversation_id);
CREATE INDEX idx_chat_messages_sender ON tc_chat_messages(sender_id);
CREATE INDEX idx_chat_messages_created ON tc_chat_messages(created_at DESC);
COMMENT ON TABLE tc_chat_messages IS '聊天消息表';
COMMENT ON COLUMN tc_chat_messages.id IS '主键ID';
COMMENT ON COLUMN tc_chat_messages.conversation_id IS '所属会话ID';
COMMENT ON COLUMN tc_chat_messages.sender_id IS '发送人ID(系统消息可为NULL)';
COMMENT ON COLUMN tc_chat_messages.content IS '消息正文(文本/图片URL/卡片JSON等,按message_type解析)';
COMMENT ON COLUMN tc_chat_messages.message_type IS '消息类型: TEXT-文本, IMAGE-图片, FILE-文件, SYSTEM-系统提示, CARD-卡片消息';
COMMENT ON COLUMN tc_chat_messages.created_at IS '消息创建时间';

-- ----------------------------------------------------------
-- 表30: 拍卖表
-- ----------------------------------------------------------
CREATE TABLE tc_auctions (
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

CREATE INDEX idx_auctions_car_id ON tc_auctions(car_id);
CREATE INDEX idx_auctions_seller_id ON tc_auctions(seller_id);
CREATE INDEX idx_auctions_status ON tc_auctions(status);
CREATE INDEX idx_auctions_start_time ON tc_auctions(start_time);
CREATE INDEX idx_auctions_end_time ON tc_auctions(end_time);
COMMENT ON TABLE tc_auctions IS '拍卖表';
COMMENT ON COLUMN tc_auctions.id IS '主键ID';
COMMENT ON COLUMN tc_auctions.car_id IS '关联车源ID';
COMMENT ON COLUMN tc_auctions.seller_id IS '卖家用户ID';
COMMENT ON COLUMN tc_auctions.start_price IS '起拍价(元)';
COMMENT ON COLUMN tc_auctions.reserve_price IS '保留价/底价(元)';
COMMENT ON COLUMN tc_auctions.current_price IS '当前最高出价(元)';
COMMENT ON COLUMN tc_auctions.bid_increment IS '每次加价幅度(元)';
COMMENT ON COLUMN tc_auctions.start_time IS '拍卖开始时间';
COMMENT ON COLUMN tc_auctions.end_time IS '拍卖计划结束时间';
COMMENT ON COLUMN tc_auctions.actual_end_time IS '拍卖实际结束时间';
COMMENT ON COLUMN tc_auctions.status IS '状态: PENDING-待开始, BIDDING-竞拍中, ENDED-已结束, SETTLED-已结算, CANCELLED-已取消, FAILED-流拍';
COMMENT ON COLUMN tc_auctions.winner_id IS '中标者用户ID';
COMMENT ON COLUMN tc_auctions.winning_price IS '中标价格(元)';
COMMENT ON COLUMN tc_auctions.total_bids IS '累计出价次数';
COMMENT ON COLUMN tc_auctions.view_count IS '浏览次数';
COMMENT ON COLUMN tc_auctions.created_at IS '创建时间';
COMMENT ON COLUMN tc_auctions.updated_at IS '更新时间';
COMMENT ON COLUMN tc_auctions.version IS '乐观锁版本号';

-- ----------------------------------------------------------
-- 表30: 拍卖出价记录表
-- ----------------------------------------------------------
CREATE TABLE tc_auction_bids (
    id          BIGINT PRIMARY KEY,
    auction_id  BIGINT NOT NULL,
    bidder_id   BIGINT NOT NULL,
    bid_price   DECIMAL(12,2) NOT NULL,
    bid_time    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_winning  BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_auction_bids_auction_id ON tc_auction_bids(auction_id);
CREATE INDEX idx_auction_bids_bidder_id ON tc_auction_bids(bidder_id);
CREATE INDEX idx_auction_bids_bid_time ON tc_auction_bids(bid_time);
COMMENT ON TABLE tc_auction_bids IS '拍卖出价记录表';
COMMENT ON COLUMN tc_auction_bids.id IS '主键ID';
COMMENT ON COLUMN tc_auction_bids.auction_id IS '关联拍卖ID';
COMMENT ON COLUMN tc_auction_bids.bidder_id IS '出价者用户ID';
COMMENT ON COLUMN tc_auction_bids.bid_price IS '出价金额(元)';
COMMENT ON COLUMN tc_auction_bids.bid_time IS '出价时间';
COMMENT ON COLUMN tc_auction_bids.is_winning IS '是否为当前最高出价';
COMMENT ON COLUMN tc_auction_bids.created_at IS '创建时间';

-- ----------------------------------------------------------
-- 表31: 拍卖关注表
-- ----------------------------------------------------------
CREATE TABLE tc_auction_watches (
    id         BIGINT PRIMARY KEY,
    auction_id BIGINT NOT NULL,
    user_id    BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(auction_id, user_id)
);

CREATE INDEX idx_auction_watches_user_id ON tc_auction_watches(user_id);
COMMENT ON TABLE tc_auction_watches IS '拍卖关注表';
COMMENT ON COLUMN tc_auction_watches.id IS '主键ID';
COMMENT ON COLUMN tc_auction_watches.auction_id IS '关联拍卖ID';
COMMENT ON COLUMN tc_auction_watches.user_id IS '关注用户ID';
COMMENT ON COLUMN tc_auction_watches.created_at IS '关注时间';

-- 32. 求购意向表
CREATE TABLE IF NOT EXISTS tc_purchase_demands (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES tc_users(id),
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

CREATE INDEX idx_purchase_demands_user_id ON tc_purchase_demands(user_id);
CREATE INDEX idx_purchase_demands_status ON tc_purchase_demands(status);
CREATE INDEX idx_purchase_demands_created_at ON tc_purchase_demands(created_at DESC);
COMMENT ON TABLE tc_purchase_demands IS '求购意向表';
COMMENT ON COLUMN tc_purchase_demands.user_id IS '求购用户ID';
COMMENT ON COLUMN tc_purchase_demands.brand_name IS '意向品牌';
COMMENT ON COLUMN tc_purchase_demands.price_min IS '最低预算';
COMMENT ON COLUMN tc_purchase_demands.price_max IS '最高预算';
COMMENT ON COLUMN tc_purchase_demands.status IS '状态: ACTIVE-有效, CANCELLED-已取消, FULFILLED-已成交';

-- 33. 城市表
CREATE TABLE IF NOT EXISTS tc_cities (
    id              SERIAL PRIMARY KEY,
    code            VARCHAR(20) NOT NULL UNIQUE,
    name            VARCHAR(50) NOT NULL,
    province_code   VARCHAR(20),
    province_name   VARCHAR(50),
    city_code       VARCHAR(20),
    city_name       VARCHAR(50),
    district_code   VARCHAR(20),
    district_name   VARCHAR(50),
    level           VARCHAR(10) NOT NULL,  -- PROVINCE/CITY/DISTRICT
    hot             BOOLEAN DEFAULT FALSE,
    sort_order      INTEGER DEFAULT 0,
    status          VARCHAR(20) DEFAULT 'ACTIVE',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_cities_code ON tc_cities(code);
CREATE INDEX IF NOT EXISTS idx_cities_level ON tc_cities(level);
CREATE INDEX IF NOT EXISTS idx_cities_province_code ON tc_cities(province_code);
CREATE INDEX IF NOT EXISTS idx_cities_city_code ON tc_cities(city_code);
CREATE INDEX IF NOT EXISTS idx_cities_hot ON tc_cities(hot);
COMMENT ON TABLE tc_cities IS '城市表';
COMMENT ON COLUMN tc_cities.level IS '层级: PROVINCE-省份, CITY-城市, DISTRICT-区县';


-- 34. 出口地区配置表
CREATE TABLE IF NOT EXISTS tc_export_regions (
    id           BIGSERIAL PRIMARY KEY,
    code         VARCHAR(10)  NOT NULL,
    name         VARCHAR(100) NOT NULL,
    flag         VARCHAR(10),
    region_group VARCHAR(50)  NOT NULL,
    group_key    VARCHAR(20)  NOT NULL,
    icon         TEXT,
    constraints  JSONB        NOT NULL DEFAULT '[]'::jsonb,
    requirements JSONB        NOT NULL DEFAULT '[]'::jsonb,
    status       VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    sort_order      INTEGER DEFAULT 999,
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_at   TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_export_regions_code ON tc_export_regions(code);
CREATE INDEX IF NOT EXISTS idx_export_regions_status ON tc_export_regions(status);
CREATE INDEX IF NOT EXISTS idx_export_regions_sort_order ON tc_export_regions(sort_order);
COMMENT ON TABLE tc_export_regions IS '出口地区配置表';
COMMENT ON COLUMN tc_export_regions.name IS '国家/地区名称';
COMMENT ON COLUMN tc_export_regions.code IS '国家代码(ISO 3166-1 alpha-2)';
COMMENT ON COLUMN tc_export_regions.flag IS '国旗emoji';
COMMENT ON COLUMN tc_export_regions.region_group IS '所属地区分组';
COMMENT ON COLUMN tc_export_regions.constraints IS '参数约束条件(JSON数组)';
COMMENT ON COLUMN tc_export_regions.requirements IS '出口要求描述';
COMMENT ON COLUMN tc_export_regions.status IS '状态: ACTIVE-启用, INACTIVE-禁用';
COMMENT ON COLUMN tc_export_regions.sort_order IS '排序号';
COMMENT ON COLUMN tc_export_regions.group_key    IS '分组标识: asia / europe / americas / africa / oceania';
COMMENT ON COLUMN tc_export_regions.icon         IS '地区图标 (emoji 或图片URL)';


