-- =====================================================================
-- 5D好车 - Admin Backend Init Script
-- Target DB: PostgreSQL (shares same schema with mobile backend)
-- Schema: public
-- =====================================================================

SET client_min_messages = WARNING;
SET search_path = public;

-- ---------------------------------------------------------------------
-- 1. admin_users  -- 后台管理员账号
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS admin_users CASCADE;
CREATE TABLE admin_users (
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

COMMENT ON TABLE  admin_users                  IS '后台管理员账号表';
COMMENT ON COLUMN admin_users.username        IS '登录用户名(唯一)';
COMMENT ON COLUMN admin_users.password        IS 'BCrypt 加密后的密码';
COMMENT ON COLUMN admin_users.nickname        IS '昵称';
COMMENT ON COLUMN admin_users.email           IS '邮箱';
COMMENT ON COLUMN admin_users.role            IS '角色: SUPER_ADMIN / ADMIN / OPERATOR / VIEWER';
COMMENT ON COLUMN admin_users.permissions     IS '权限列表 (JSONB 数组)';
COMMENT ON COLUMN admin_users.status          IS '账号状态: ACTIVE / DISABLED';
COMMENT ON COLUMN admin_users.last_login_at   IS '最后登录时间';
COMMENT ON COLUMN admin_users.last_login_ip   IS '最后登录 IP';
COMMENT ON COLUMN admin_users.deleted_at      IS '逻辑删除时间';

CREATE INDEX idx_admin_users_username ON admin_users (username);
CREATE INDEX idx_admin_users_status   ON admin_users (status);
CREATE INDEX idx_admin_users_role     ON admin_users (role);
CREATE INDEX idx_admin_users_deleted  ON admin_users (deleted_at);

-- ---------------------------------------------------------------------
-- 2. audit_logs  -- 操作审计日志
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS audit_logs CASCADE;
CREATE TABLE audit_logs (
    id            BIGSERIAL PRIMARY KEY,
    admin_id      BIGINT       REFERENCES admin_users (id),
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

COMMENT ON TABLE  audit_logs                  IS '后台操作审计日志';
COMMENT ON COLUMN audit_logs.admin_id         IS '操作人 ID (FK -> admin_users.id)';
COMMENT ON COLUMN audit_logs.admin_name       IS '操作人姓名(冗余)';
COMMENT ON COLUMN audit_logs.module           IS '业务模块: USER / DASHBOARD / BANNER / CONFIG 等';
COMMENT ON COLUMN audit_logs.action           IS '动作: CREATE / UPDATE / DELETE / QUERY / EXPORT / LOGIN / LOGOUT';
COMMENT ON COLUMN audit_logs.target_type      IS '操作对象类型';
COMMENT ON COLUMN audit_logs.target_id        IS '操作对象 ID';
COMMENT ON COLUMN audit_logs.description      IS '操作描述';
COMMENT ON COLUMN audit_logs.request_params   IS '请求参数 (JSONB)';
COMMENT ON COLUMN audit_logs.ip_address       IS '客户端 IP';
COMMENT ON COLUMN audit_logs.user_agent       IS '浏览器 UA';
COMMENT ON COLUMN audit_logs.duration_ms      IS '接口耗时 (ms)';
COMMENT ON COLUMN audit_logs.result           IS '结果: SUCCESS / FAILURE';

CREATE INDEX idx_audit_logs_admin_id   ON audit_logs (admin_id);
CREATE INDEX idx_audit_logs_module     ON audit_logs (module);
CREATE INDEX idx_audit_logs_action     ON audit_logs (action);
CREATE INDEX idx_audit_logs_created_at ON audit_logs (created_at DESC);
CREATE INDEX idx_audit_logs_target     ON audit_logs (target_type, target_id);

-- ---------------------------------------------------------------------
-- 3. banners  -- 首页 / 活动 Banner
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS banners CASCADE;
CREATE TABLE banners (
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

COMMENT ON TABLE  banners                IS 'Banner / 广告位管理表';
COMMENT ON COLUMN banners.title         IS 'Banner 标题';
COMMENT ON COLUMN banners.image_url     IS '图片 URL';
COMMENT ON COLUMN banners.type          IS '类型: BANNER / POPUP / NOTICE';
COMMENT ON COLUMN banners.link_url      IS '点击跳转链接';
COMMENT ON COLUMN banners.sort_order    IS '排序, 升序';
COMMENT ON COLUMN banners.status        IS '状态: ACTIVE / DISABLED';
COMMENT ON COLUMN banners.click_count   IS '点击次数';
COMMENT ON COLUMN banners.start_at      IS '生效时间';
COMMENT ON COLUMN banners.end_at        IS '失效时间';
COMMENT ON COLUMN banners.deleted_at    IS '逻辑删除时间';

CREATE INDEX idx_banners_status     ON banners (status);
CREATE INDEX idx_banners_type       ON banners (type);
CREATE INDEX idx_banners_sort       ON banners (sort_order);
CREATE INDEX idx_banners_deleted_at ON banners (deleted_at);
CREATE INDEX idx_banners_time_range ON banners (start_at, end_at);

-- ---------------------------------------------------------------------
-- 4. export_templates  -- 导出模板
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS export_templates CASCADE;
CREATE TABLE export_templates (
    id             BIGSERIAL PRIMARY KEY,
    name           VARCHAR(100) NOT NULL,
    type           VARCHAR(50)  NOT NULL,
    format         VARCHAR(20)  NOT NULL DEFAULT 'EXCEL',
    field_mappings JSONB        NOT NULL,
    default_filters JSONB,
    created_by     BIGINT       REFERENCES admin_users (id),
    created_at     TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_at     TIMESTAMP
);

COMMENT ON TABLE  export_templates                 IS '数据导出模板表';
COMMENT ON COLUMN export_templates.name            IS '模板名称';
COMMENT ON COLUMN export_templates.type            IS '业务类型: USER / ORDER / VEHICLE / FINANCE 等';
COMMENT ON COLUMN export_templates.format          IS '导出格式: EXCEL / CSV / PDF';
COMMENT ON COLUMN export_templates.field_mappings  IS '字段映射 (JSONB)';
COMMENT ON COLUMN export_templates.default_filters IS '默认筛选条件 (JSONB)';
COMMENT ON COLUMN export_templates.created_by      IS '创建人 (FK -> admin_users.id)';
COMMENT ON COLUMN export_templates.deleted_at      IS '逻辑删除时间';

CREATE INDEX idx_export_templates_type      ON export_templates (type);
CREATE INDEX idx_export_templates_created_by ON export_templates (created_by);
CREATE INDEX idx_export_templates_deleted   ON export_templates (deleted_at);

-- ---------------------------------------------------------------------
-- 5. configs  -- 系统配置 (KV)
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS configs CASCADE;
CREATE TABLE configs (
    key        VARCHAR(100) PRIMARY KEY,
    content    TEXT,
    updated_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE  configs            IS '系统配置 KV 表';
COMMENT ON COLUMN configs.key        IS '配置 key (主键)';
COMMENT ON COLUMN configs.content    IS '配置内容 (TEXT/JSON)';
COMMENT ON COLUMN configs.updated_at IS '更新时间';

-- =====================================================================
-- Seed Data
-- =====================================================================

-- 默认超级管理员账号
-- username : yuan2026@5d.com
-- password : admin123
-- BCrypt hash verified (cost = 10)
INSERT INTO admin_users (username, password, nickname, email, role, permissions, status)
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
INSERT INTO configs (key, content) VALUES
    ('system.title',       '5D好车管理后台'),
    ('system.version',     '1.0.0'),
    ('system.copyright',   '© 2026 5D好车'),
    ('login.max-fails',    '5'),
    ('login.lock-minutes', '5')
ON CONFLICT (key) DO NOTHING;

-- =====================================================================
-- 6. admin_notifications  -- 运营端通知推送记录
-- =====================================================================
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

COMMENT ON TABLE  admin_notifications              IS '运营端通知推送记录';
COMMENT ON COLUMN admin_notifications.type          IS '通知类型: SHOP_AUDIT_RESULT / CAR_STATUS_CHANGED / DISPUTE_RESOLVED / DEPOSIT_CHANGED / USER_STATUS_CHANGED / SYSTEM_ANNOUNCEMENT / PURCHASE_MATCHED';
COMMENT ON COLUMN admin_notifications.target_user_id IS '目标用户ID (FK -> users.id)';
COMMENT ON COLUMN admin_notifications.title         IS '通知标题';
COMMENT ON COLUMN admin_notifications.content       IS '通知内容';
COMMENT ON COLUMN admin_notifications.target_type   IS '关联实体类型: order / car_source / dispute 等';
COMMENT ON COLUMN admin_notifications.target_id     IS '关联实体ID';
COMMENT ON COLUMN admin_notifications.extra_json    IS '附加数据 (JSONB)';
COMMENT ON COLUMN admin_notifications.status        IS '发送状态: SENT / FAILED';

CREATE INDEX IF NOT EXISTS idx_admin_notifications_target_user ON admin_notifications(target_user_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_admin_notifications_type          ON admin_notifications(type);
CREATE INDEX IF NOT EXISTS idx_admin_notifications_status        ON admin_notifications(status);
CREATE INDEX IF NOT EXISTS idx_admin_notifications_created       ON admin_notifications(created_at DESC);

-- =====================================================================
-- End of script
-- =====================================================================
