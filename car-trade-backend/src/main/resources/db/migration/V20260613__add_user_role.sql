-- =====================================================
-- Migration: Add user_role field to tc_users table
-- Version: 2026-06-13
-- Description: 添加用户角色字段，替代仅通过 shop_name 判断用户类型的方式
-- =====================================================

-- 1. 添加 user_role 字段
ALTER TABLE tc_users 
ADD COLUMN IF NOT EXISTS user_role VARCHAR(20) DEFAULT 'PERSONAL';

-- 2. 添加注释
COMMENT ON COLUMN tc_users.user_role IS '用户角色：PERSONAL-个人用户, SHOP-车行用户, ADMIN-系统管理员, DEVELOPER-开发人员';

-- 3. 根据现有数据初始化 user_role
-- 有车行名称的用户标记为 SHOP
UPDATE tc_users 
SET user_role = 'SHOP' 
WHERE shop_name IS NOT NULL AND shop_name != '';

-- 无车行名称的用户保持 PERSONAL（默认值）

-- 4. 添加索引优化查询性能
CREATE INDEX IF NOT EXISTS idx_users_user_role ON tc_users(user_role);
CREATE INDEX IF NOT EXISTS idx_users_role_status ON tc_users(user_role, status);

-- 5. 验证数据一致性（可选，用于检查异常数据）
-- SELECT id, phone, nickname, shop_name, user_role, certification_status
-- FROM tc_users 
-- WHERE (shop_name IS NOT NULL AND shop_name != '' AND user_role != 'SHOP')
--    OR (shop_name IS NULL OR shop_name = '') AND user_role = 'SHOP';
