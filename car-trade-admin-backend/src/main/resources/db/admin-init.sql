-- =====================================================================
-- 5D好车 - Admin Backend Init Script
-- Target DB: PostgreSQL (shares same schema with mobile backend)
-- Schema: public
-- =====================================================================

--SET client_min_messages = WARNING;
--SET search_path = public;

-- ---------------------------------------------------------------------
-- 1. tc_admin_users  -- 后台管理员账号
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS tc_admin_users CASCADE;
CREATE TABLE tc_admin_users (
    id            BIGSERIAL PRIMARY KEY,
    username      VARCHAR(100) NOT NULL UNIQUE,
    password      VARCHAR(200) NOT NULL,
    nickname      VARCHAR(50),
    email         VARCHAR(100),
    role          VARCHAR(30)  NOT NULL DEFAULT 'ADMIN',
    permissions   JSONB        NOT NULL DEFAULT '[]'::jsonb,
    status        VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    last_login_at TIMESTAMP,
    last_login_ip VARCHAR(50),
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_at    TIMESTAMP
);

COMMENT ON TABLE  tc_admin_users                  IS '后台管理员账号表';
COMMENT ON COLUMN tc_admin_users.username        IS '登录用户名(唯一)';
COMMENT ON COLUMN tc_admin_users.password        IS 'BCrypt 加密后的密码';
COMMENT ON COLUMN tc_admin_users.nickname        IS '昵称';
COMMENT ON COLUMN tc_admin_users.email           IS '邮箱';
COMMENT ON COLUMN tc_admin_users.role            IS '角色: SUPER_ADMIN / ADMIN / OPERATOR / VIEWER';
COMMENT ON COLUMN tc_admin_users.permissions     IS '权限列表 (JSONB 数组)';
COMMENT ON COLUMN tc_admin_users.status          IS '账号状态: ACTIVE / DISABLED';
COMMENT ON COLUMN tc_admin_users.last_login_at   IS '最后登录时间';
COMMENT ON COLUMN tc_admin_users.last_login_ip   IS '最后登录 IP';
COMMENT ON COLUMN tc_admin_users.deleted_at      IS '逻辑删除时间';

CREATE INDEX idx_admin_users_username ON tc_admin_users (username);
CREATE INDEX idx_admin_users_status   ON tc_admin_users (status);
CREATE INDEX idx_admin_users_role     ON tc_admin_users (role);
CREATE INDEX idx_admin_users_deleted  ON tc_admin_users (deleted_at);

-- ---------------------------------------------------------------------
-- 2. tc_audit_logs  -- 操作审计日志
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS tc_audit_logs CASCADE;
CREATE TABLE tc_audit_logs (
    id            BIGSERIAL PRIMARY KEY,
    admin_id      BIGINT       REFERENCES tc_admin_users (id),
    admin_name    VARCHAR(50),
    module        VARCHAR(50)  NOT NULL,
    action        VARCHAR(50)  NOT NULL,
    target_type   VARCHAR(50),
    target_id     VARCHAR(100),
    description   VARCHAR(500),
    request_params TEXT,
    ip_address    VARCHAR(50),
    user_agent    VARCHAR(500),
    duration_ms   INTEGER,
    result        VARCHAR(200) NOT NULL DEFAULT 'SUCCESS',
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE  tc_audit_logs                  IS '后台操作审计日志';
COMMENT ON COLUMN tc_audit_logs.admin_id         IS '操作人 ID (FK -> admin_users.id)';
COMMENT ON COLUMN tc_audit_logs.admin_name       IS '操作人姓名(冗余)';
COMMENT ON COLUMN tc_audit_logs.module           IS '业务模块: USER / DASHBOARD / BANNER / CONFIG 等';
COMMENT ON COLUMN tc_audit_logs.action           IS '动作: CREATE / UPDATE / DELETE / QUERY / EXPORT / LOGIN / LOGOUT';
COMMENT ON COLUMN tc_audit_logs.target_type      IS '操作对象类型';
COMMENT ON COLUMN tc_audit_logs.target_id        IS '操作对象 ID';
COMMENT ON COLUMN tc_audit_logs.description      IS '操作描述';
COMMENT ON COLUMN tc_audit_logs.request_params   IS '请求参数 (JSONB)';
COMMENT ON COLUMN tc_audit_logs.ip_address       IS '客户端 IP';
COMMENT ON COLUMN tc_audit_logs.user_agent       IS '浏览器 UA';
COMMENT ON COLUMN tc_audit_logs.duration_ms      IS '接口耗时 (ms)';
COMMENT ON COLUMN tc_audit_logs.result           IS '结果: SUCCESS / FAILURE';

CREATE INDEX idx_audit_logs_admin_id   ON tc_audit_logs (admin_id);
CREATE INDEX idx_audit_logs_module     ON tc_audit_logs (module);
CREATE INDEX idx_audit_logs_action     ON tc_audit_logs (action);
CREATE INDEX idx_audit_logs_created_at ON tc_audit_logs (created_at DESC);
CREATE INDEX idx_audit_logs_target     ON tc_audit_logs (target_type, target_id);

-- ---------------------------------------------------------------------
-- 3. tc_banners  -- 首页 / 活动 Banner
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS tc_banners CASCADE;
CREATE TABLE tc_banners (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(200) NOT NULL,
    image_url   VARCHAR(500) NOT NULL,
    type        VARCHAR(20)  NOT NULL DEFAULT 'BANNER',
    link_url    VARCHAR(500),
    sort_order  INTEGER      NOT NULL DEFAULT 0,
    status      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    click_count INTEGER      NOT NULL DEFAULT 0,
    start_at    TIMESTAMP,
    end_at      TIMESTAMP,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_at  TIMESTAMP
);

COMMENT ON TABLE  tc_banners                IS 'Banner / 广告位管理表';
COMMENT ON COLUMN tc_banners.title         IS 'Banner 标题';
COMMENT ON COLUMN tc_banners.image_url     IS '图片 URL';
COMMENT ON COLUMN tc_banners.type          IS '类型: BANNER / POPUP / NOTICE';
COMMENT ON COLUMN tc_banners.link_url      IS '点击跳转链接';
COMMENT ON COLUMN tc_banners.sort_order    IS '排序, 升序';
COMMENT ON COLUMN tc_banners.status        IS '状态: ACTIVE / DISABLED';
COMMENT ON COLUMN tc_banners.click_count   IS '点击次数';
COMMENT ON COLUMN tc_banners.start_at      IS '生效时间';
COMMENT ON COLUMN tc_banners.end_at        IS '失效时间';
COMMENT ON COLUMN tc_banners.deleted_at    IS '逻辑删除时间';

CREATE INDEX idx_banners_status     ON tc_banners (status);
CREATE INDEX idx_banners_type       ON tc_banners (type);
CREATE INDEX idx_banners_sort       ON tc_banners (sort_order);
CREATE INDEX idx_banners_deleted_at ON tc_banners (deleted_at);
CREATE INDEX idx_banners_time_range ON tc_banners (start_at, end_at);

-- ---------------------------------------------------------------------
-- 4. tc_export_templates  -- 导出模板
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS tc_export_templates CASCADE;
CREATE TABLE tc_export_templates (
    id             BIGSERIAL PRIMARY KEY,
    name           VARCHAR(100) NOT NULL,
    type           VARCHAR(50)  NOT NULL,
    format         VARCHAR(20)  NOT NULL DEFAULT 'EXCEL',
    field_mappings JSONB        NOT NULL,
    default_filters JSONB,
    created_by     BIGINT       REFERENCES tc_admin_users (id),
    created_at     TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_at     TIMESTAMP
);

COMMENT ON TABLE  tc_export_templates                 IS '数据导出模板表';
COMMENT ON COLUMN tc_export_templates.name            IS '模板名称';
COMMENT ON COLUMN tc_export_templates.type            IS '业务类型: USER / ORDER / VEHICLE / FINANCE 等';
COMMENT ON COLUMN tc_export_templates.format          IS '导出格式: EXCEL / CSV / PDF';
COMMENT ON COLUMN tc_export_templates.field_mappings  IS '字段映射 (JSONB)';
COMMENT ON COLUMN tc_export_templates.default_filters IS '默认筛选条件 (JSONB)';
COMMENT ON COLUMN tc_export_templates.created_by      IS '创建人 (FK -> admin_users.id)';
COMMENT ON COLUMN tc_export_templates.deleted_at      IS '逻辑删除时间';

CREATE INDEX idx_export_templates_type      ON tc_export_templates (type);
CREATE INDEX idx_export_templates_created_by ON tc_export_templates (created_by);
CREATE INDEX idx_export_templates_deleted   ON tc_export_templates (deleted_at);

-- ---------------------------------------------------------------------
-- 5. tc_configs  -- 系统配置 (KV)
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS tc_configs CASCADE;
CREATE TABLE tc_configs (
    key        VARCHAR(100) PRIMARY KEY,
    content    TEXT,
    updated_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE  tc_configs            IS '系统配置 KV 表';
COMMENT ON COLUMN tc_configs.key        IS '配置 key (主键)';
COMMENT ON COLUMN tc_configs.content    IS '配置内容 (TEXT/JSON)';
COMMENT ON COLUMN tc_configs.updated_at IS '更新时间';

-- =====================================================================
-- Seed Data
-- =====================================================================

-- 默认超级管理员账号
-- username : yuan2026@5d.com
-- password : admin123
-- BCrypt hash verified (cost = 10)
INSERT INTO tc_admin_users (username, password, nickname, email, role, permissions, status)
VALUES (
    'yuan2026@5d.com',
    '$2a$10$JNgU6aIPmyglHMnFkcVsYedQBy6kjrViSfP7Rbfv22JX2e69ROGPC',
    '管理员A',
    'yuan2026@5d.com',
    'SUPER_ADMIN',
    '["*"]'::jsonb,
    'ACTIVE'
)
ON CONFLICT (username) DO UPDATE
    SET password   = EXCLUDED.password,
        nickname   = EXCLUDED.nickname,
        role       = EXCLUDED.role,
        permissions = EXCLUDED.permissions,
        updated_at = NOW();

-- 默认配置项
INSERT INTO tc_configs (key, content) VALUES
    ('system.title',       '5D好车管理后台'),
    ('system.version',     '1.0.0'),
    ('system.copyright',   '© 2026 5D好车'),
    ('login.max-fails',    '5'),
    ('login.lock-minutes', '5')
ON CONFLICT (key) DO NOTHING;

-- =====================================================================
-- Shared users table: add shop-related columns (idempotent)
-- =====================================================================
ALTER TABLE tc_users ADD COLUMN IF NOT EXISTS province VARCHAR(50);
ALTER TABLE tc_users ADD COLUMN IF NOT EXISTS city VARCHAR(50);
ALTER TABLE tc_users ADD COLUMN IF NOT EXISTS address VARCHAR(300);
ALTER TABLE tc_users ADD COLUMN IF NOT EXISTS credit_code VARCHAR(50);
ALTER TABLE tc_users ADD COLUMN IF NOT EXISTS deposit_balance BIGINT DEFAULT 0;
ALTER TABLE tc_users ADD COLUMN IF NOT EXISTS license_url VARCHAR(500);
ALTER TABLE tc_users ADD COLUMN IF NOT EXISTS id_card_number VARCHAR(30);
ALTER TABLE tc_users ADD COLUMN IF NOT EXISTS id_card_image_url VARCHAR(500);
ALTER TABLE tc_users ADD COLUMN IF NOT EXISTS store_image_url VARCHAR(500);

COMMENT ON COLUMN tc_users.province IS '驻点省份';
COMMENT ON COLUMN tc_users.city IS '驻点城市';
COMMENT ON COLUMN tc_users.address IS '实体店详细经营地址';
COMMENT ON COLUMN tc_users.credit_code IS '统一社会信用代码';
COMMENT ON COLUMN tc_users.deposit_balance IS '保证金余额（元）';
COMMENT ON COLUMN tc_users.license_url IS '营业执照附件 URL';
COMMENT ON COLUMN tc_users.id_card_number IS '申请人身份证号';
COMMENT ON COLUMN tc_users.id_card_image_url IS '申请人身份证图片 URL';
COMMENT ON COLUMN tc_users.store_image_url IS '车行实体门店图片 URL';

CREATE INDEX IF NOT EXISTS idx_users_province ON tc_users (province);

-- =====================================================================
-- 6. tc_admin_notifications  -- 运营端通知推送记录
-- =====================================================================
CREATE TABLE IF NOT EXISTS tc_admin_notifications (
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

COMMENT ON TABLE  tc_admin_notifications              IS '运营端通知推送记录';
COMMENT ON COLUMN tc_admin_notifications.type          IS '通知类型: SHOP_AUDIT_RESULT / CAR_STATUS_CHANGED / DISPUTE_RESOLVED / DEPOSIT_CHANGED / USER_STATUS_CHANGED / SYSTEM_ANNOUNCEMENT / PURCHASE_MATCHED';
COMMENT ON COLUMN tc_admin_notifications.target_user_id IS '目标用户ID (FK -> users.id)';
COMMENT ON COLUMN tc_admin_notifications.title         IS '通知标题';
COMMENT ON COLUMN tc_admin_notifications.content       IS '通知内容';
COMMENT ON COLUMN tc_admin_notifications.target_type   IS '关联实体类型: order / car_source / dispute 等';
COMMENT ON COLUMN tc_admin_notifications.target_id     IS '关联实体ID';
COMMENT ON COLUMN tc_admin_notifications.extra_json    IS '附加数据 (JSONB)';
COMMENT ON COLUMN tc_admin_notifications.status        IS '发送状态: SENT / FAILED';

CREATE INDEX IF NOT EXISTS idx_admin_notifications_target_user ON tc_admin_notifications(target_user_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_admin_notifications_type          ON tc_admin_notifications(type);
CREATE INDEX IF NOT EXISTS idx_admin_notifications_status        ON tc_admin_notifications(status);
CREATE INDEX IF NOT EXISTS idx_admin_notifications_created       ON tc_admin_notifications(created_at DESC);

-- =====================================================================
-- 7. tc_export_regions  -- 出口地区配置
-- =====================================================================
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

COMMENT ON TABLE  tc_export_regions             IS '出口地区配置表';
COMMENT ON COLUMN tc_export_regions.code         IS '地区代码 (如 CN, US, EU)';
COMMENT ON COLUMN tc_export_regions.name         IS '地区名称';
COMMENT ON COLUMN tc_export_regions.flag         IS '国旗emoji';
COMMENT ON COLUMN tc_export_regions.region_group IS '地区分组名称';
COMMENT ON COLUMN tc_export_regions.group_key    IS '分组标识: asia / europe / americas / africa / oceania';
COMMENT ON COLUMN tc_export_regions.icon         IS '地区图标 (emoji 或图片URL)';
COMMENT ON COLUMN tc_export_regions.constraints  IS '参数约束条件 (JSONB数组)';
COMMENT ON COLUMN tc_export_regions.requirements IS '出口要求清单 (JSONB数组)';
COMMENT ON COLUMN tc_export_regions.status       IS '状态: ACTIVE / INACTIVE';

CREATE UNIQUE INDEX IF NOT EXISTS idx_export_regions_code ON tc_export_regions (code) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_export_regions_group_key ON tc_export_regions (group_key);
CREATE INDEX IF NOT EXISTS idx_export_regions_status ON tc_export_regions (status);

-- =====================================================================
-- 注意：共享表（tc_users / tc_deposit_records / tc_coupons / tc_export_regions）的完整字段定义
-- 已统一维护在 car-trade-backend 的 init.sql 中，此处不再重复 ALTER TABLE。
-- 新增字段请直接修改 init.sql 的 CREATE TABLE 语句。
-- =====================================================================

-- =====================================================================
-- End of script
-- =====================================================================
