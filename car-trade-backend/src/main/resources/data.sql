-- 会员套餐初始化数据
INSERT INTO member_plans (name, level, price, duration_days, benefits, sort_order, status, created_at)
VALUES 
('月度会员', 'BRONZE', 99.00, 30, '{"freePublish": 10, "discountRate": 0.95, "aiAnalysis": true}', 3, 'ACTIVE', CURRENT_TIMESTAMP),
('季度会员', 'SILVER', 249.00, 90, '{"freePublish": 30, "discountRate": 0.90, "aiAnalysis": true}', 2, 'ACTIVE', CURRENT_TIMESTAMP),
('年度会员', 'GOLD', 799.00, 365, '{"freePublish": 100, "discountRate": 0.85, "aiAnalysis": true}', 1, 'ACTIVE', CURRENT_TIMESTAMP);
