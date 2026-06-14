

-- 34. 出口地区配置表
CREATE TABLE IF NOT EXISTS export_regions (
    id              SERIAL PRIMARY KEY,
    name            VARCHAR(50) NOT NULL,
    code            VARCHAR(10) NOT NULL UNIQUE,
    flag            VARCHAR(10),
    region_group    VARCHAR(50),
    constraints     TEXT,
    requirements    TEXT,
    status          VARCHAR(20) DEFAULT 'ACTIVE',
    sort_order      INTEGER DEFAULT 999,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at      TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_export_regions_code ON export_regions(code);
CREATE INDEX IF NOT EXISTS idx_export_regions_status ON export_regions(status);
CREATE INDEX IF NOT EXISTS idx_export_regions_sort_order ON export_regions(sort_order);
COMMENT ON TABLE export_regions IS '出口地区配置表';
COMMENT ON COLUMN export_regions.name IS '国家/地区名称';
COMMENT ON COLUMN export_regions.code IS '国家代码(ISO 3166-1 alpha-2)';
COMMENT ON COLUMN export_regions.flag IS '国旗emoji';
COMMENT ON COLUMN export_regions.region_group IS '所属地区分组';
COMMENT ON COLUMN export_regions.constraints IS '参数约束条件(JSON数组)';
COMMENT ON COLUMN export_regions.requirements IS '出口要求描述';
COMMENT ON COLUMN export_regions.status IS '状态: ACTIVE-启用, INACTIVE-禁用';
COMMENT ON COLUMN export_regions.sort_order IS '排序号';

