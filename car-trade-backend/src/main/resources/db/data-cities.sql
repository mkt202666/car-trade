-- ============== 全国城市数据初始化 ==============
-- 包含34个省级行政区、333个地级行政区、部分县级行政区
-- 数据来源：国家统计局标准行政区划代码

-- ============== 北京市 ========================
INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '110000', '北京市', '110000', '北京', '110000', '北京市', NULL, NULL, 'PROVINCE', true, 1
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '110000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '110100', '北京市', '110000', '北京', '110100', '北京市', NULL, NULL, 'CITY', true, 1
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '110100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '110101', '东城区', '110000', '北京', '110100', '北京市', '110101', '东城区', 'DISTRICT', false, 1
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '110101');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '110102', '西城区', '110000', '北京', '110100', '北京市', '110102', '西城区', 'DISTRICT', false, 2
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '110102');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '110105', '朝阳区', '110000', '北京', '110100', '北京市', '110105', '朝阳区', 'DISTRICT', false, 3
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '110105');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '110106', '丰台区', '110000', '北京', '110100', '北京市', '110106', '丰台区', 'DISTRICT', false, 4
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '110106');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '110107', '石景山区', '110000', '北京', '110100', '北京市', '110107', '石景山区', 'DISTRICT', false, 5
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '110107');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '110108', '海淀区', '110000', '北京', '110100', '北京市', '110108', '海淀区', 'DISTRICT', false, 6
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '110108');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '110109', '门头沟区', '110000', '北京', '110100', '北京市', '110109', '门头沟区', 'DISTRICT', false, 7
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '110109');

-- ============== 天津市 ========================
INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '120000', '天津市', '120000', '天津', '120000', '天津市', NULL, NULL, 'PROVINCE', true, 2
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '120000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '120100', '天津市', '120000', '天津', '120100', '天津市', NULL, NULL, 'CITY', false, 2
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '120100');

-- ============== 河北省 ========================
INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '130000', '河北省', '130000', '河北', NULL, NULL, NULL, NULL, 'PROVINCE', false, 3
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '130000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '130100', '石家庄市', '130000', '河北', '130100', '石家庄市', NULL, NULL, 'CITY', false, 3
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '130100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '130200', '唐山市', '130000', '河北', '130200', '唐山市', NULL, NULL, 'CITY', false, 4
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '130200');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '130300', '秦皇岛市', '130000', '河北', '130300', '秦皇岛市', NULL, NULL, 'CITY', false, 5
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '130300');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '130400', '邯郸市', '130000', '河北', '130400', '邯郸市', NULL, NULL, 'CITY', false, 6
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '130400');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '130500', '邢台市', '130000', '河北', '130500', '邢台市', NULL, NULL, 'CITY', false, 7
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '130500');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '130600', '保定市', '130000', '河北', '130600', '保定市', NULL, NULL, 'CITY', false, 8
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '130600');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '130700', '张家口市', '130000', '河北', '130700', '张家口市', NULL, NULL, 'CITY', false, 9
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '130700');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '130800', '承德市', '130000', '河北', '130800', '承德市', NULL, NULL, 'CITY', false, 10
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '130800');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '130900', '沧州市', '130000', '河北', '130900', '沧州市', NULL, NULL, 'CITY', false, 11
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '130900');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '131000', '廊坊市', '130000', '河北', '131000', '廊坊市', NULL, NULL, 'CITY', false, 12
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '131000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '131100', '衡水市', '130000', '河北', '131100', '衡水市', NULL, NULL, 'CITY', false, 13
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '131100');

-- ============== 山西省 ========================
INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '140000', '山西省', '140000', '山西', NULL, NULL, NULL, NULL, 'PROVINCE', false, 4
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '140000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '140100', '太原市', '140000', '山西', '140100', '太原市', NULL, NULL, 'CITY', false, 14
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '140100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '140200', '大同市', '140000', '山西', '140200', '大同市', NULL, NULL, 'CITY', false, 15
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '140200');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '140300', '阳泉市', '140000', '山西', '140300', '阳泉市', NULL, NULL, 'CITY', false, 16
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '140300');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '140400', '长治市', '140000', '山西', '140400', '长治市', NULL, NULL, 'CITY', false, 17
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '140400');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '140500', '晋城市', '140000', '山西', '140500', '晋城市', NULL, NULL, 'CITY', false, 18
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '140500');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '140600', '朔州市', '140000', '山西', '140600', '朔州市', NULL, NULL, 'CITY', false, 19
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '140600');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '140700', '晋中市', '140000', '山西', '140700', '晋中市', NULL, NULL, 'CITY', false, 20
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '140700');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '140800', '运城市', '140000', '山西', '140800', '运城市', NULL, NULL, 'CITY', false, 21
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '140800');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '140900', '忻州市', '140000', '山西', '140900', '忻州市', NULL, NULL, 'CITY', false, 22
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '140900');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '141000', '临汾市', '140000', '山西', '141000', '临汾市', NULL, NULL, 'CITY', false, 23
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '141000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '141100', '吕梁市', '140000', '山西', '141100', '吕梁市', NULL, NULL, 'CITY', false, 24
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '141100');

-- ============== 内蒙古自治区 ========================
INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '150000', '内蒙古自治区', '150000', '内蒙古', NULL, NULL, NULL, NULL, 'PROVINCE', false, 5
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '150000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '150100', '呼和浩特市', '150000', '内蒙古', '150100', '呼和浩特市', NULL, NULL, 'CITY', false, 25
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '150100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '150200', '包头市', '150000', '内蒙古', '150200', '包头市', NULL, NULL, 'CITY', false, 26
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '150200');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '150300', '乌海市', '150000', '内蒙古', '150300', '乌海市', NULL, NULL, 'CITY', false, 27
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '150300');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '150400', '赤峰市', '150000', '内蒙古', '150400', '赤峰市', NULL, NULL, 'CITY', false, 28
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '150400');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '150500', '通辽市', '150000', '内蒙古', '150500', '通辽市', NULL, NULL, 'CITY', false, 29
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '150500');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '150600', '鄂尔多斯市', '150000', '内蒙古', '150600', '鄂尔多斯市', NULL, NULL, 'CITY', false, 30
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '150600');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '150700', '呼伦贝尔市', '150000', '内蒙古', '150700', '呼伦贝尔市', NULL, NULL, 'CITY', false, 31
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '150700');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '150800', '巴彦淖尔市', '150000', '内蒙古', '150800', '巴彦淖尔市', NULL, NULL, 'CITY', false, 32
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '150800');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '150900', '乌兰察布市', '150000', '内蒙古', '150900', '乌兰察布市', NULL, NULL, 'CITY', false, 33
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '150900');

-- ============== 辽宁省 ========================
INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '210000', '辽宁省', '210000', '辽宁', NULL, NULL, NULL, NULL, 'PROVINCE', false, 6
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '210000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '210100', '沈阳市', '210000', '辽宁', '210100', '沈阳市', NULL, NULL, 'CITY', true, 34
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '210100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '210200', '大连市', '210000', '辽宁', '210200', '大连市', NULL, NULL, 'CITY', true, 35
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '210200');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '210300', '鞍山市', '210000', '辽宁', '210300', '鞍山市', NULL, NULL, 'CITY', false, 36
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '210300');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '210400', '抚顺市', '210000', '辽宁', '210400', '抚顺市', NULL, NULL, 'CITY', false, 37
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '210400');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '210500', '本溪市', '210000', '辽宁', '210500', '本溪市', NULL, NULL, 'CITY', false, 38
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '210500');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '210600', '丹东市', '210000', '辽宁', '210600', '丹东市', NULL, NULL, 'CITY', false, 39
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '210600');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '210700', '锦州市', '210000', '辽宁', '210700', '锦州市', NULL, NULL, 'CITY', false, 40
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '210700');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '210800', '营口市', '210000', '辽宁', '210800', '营口市', NULL, NULL, 'CITY', false, 41
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '210800');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '210900', '阜新市', '210000', '辽宁', '210900', '阜新市', NULL, NULL, 'CITY', false, 42
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '210900');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '211000', '辽阳市', '210000', '辽宁', '211000', '辽阳市', NULL, NULL, 'CITY', false, 43
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '211000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '211100', '盘锦市', '210000', '辽宁', '211100', '盘锦市', NULL, NULL, 'CITY', false, 44
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '211100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '211200', '铁岭市', '210000', '辽宁', '211200', '铁岭市', NULL, NULL, 'CITY', false, 45
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '211200');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '211300', '朝阳市', '210000', '辽宁', '211300', '朝阳市', NULL, NULL, 'CITY', false, 46
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '211300');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '211400', '葫芦岛市', '210000', '辽宁', '211400', '葫芦岛市', NULL, NULL, 'CITY', false, 47
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '211400');

-- ============== 吉林省 ========================
INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '220000', '吉林省', '220000', '吉林', NULL, NULL, NULL, NULL, 'PROVINCE', false, 7
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '220000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '220100', '长春市', '220000', '吉林', '220100', '长春市', NULL, NULL, 'CITY', true, 48
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '220100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '220200', '吉林市', '220000', '吉林', '220200', '吉林市', NULL, NULL, 'CITY', false, 49
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '220200');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '220300', '四平市', '220000', '吉林', '220300', '四平市', NULL, NULL, 'CITY', false, 50
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '220300');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '220400', '辽源市', '220000', '吉林', '220400', '辽源市', NULL, NULL, 'CITY', false, 51
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '220400');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '220500', '通化市', '220000', '吉林', '220500', '通化市', NULL, NULL, 'CITY', false, 52
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '220500');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '220600', '白山市', '220000', '吉林', '220600', '白山市', NULL, NULL, 'CITY', false, 53
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '220600');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '220700', '松原市', '220000', '吉林', '220700', '松原市', NULL, NULL, 'CITY', false, 54
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '220700');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '220800', '白城市', '220000', '吉林', '220800', '白城市', NULL, NULL, 'CITY', false, 55
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '220800');

-- ============== 黑龙江省 ========================
INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '230000', '黑龙江省', '230000', '黑龙江', NULL, NULL, NULL, NULL, 'PROVINCE', false, 8
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '230000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '230100', '哈尔滨市', '230000', '黑龙江', '230100', '哈尔滨市', NULL, NULL, 'CITY', true, 56
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '230100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '230200', '齐齐哈尔市', '230000', '黑龙江', '230200', '齐齐哈尔市', NULL, NULL, 'CITY', false, 57
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '230200');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '230300', '鸡西市', '230000', '黑龙江', '230300', '鸡西市', NULL, NULL, 'CITY', false, 58
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '230300');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '230400', '鹤岗市', '230000', '黑龙江', '230400', '鹤岗市', NULL, NULL, 'CITY', false, 59
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '230400');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '230500', '双鸭山市', '230000', '黑龙江', '230500', '双鸭山市', NULL, NULL, 'CITY', false, 60
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '230500');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '230600', '大庆市', '230000', '黑龙江', '230600', '大庆市', NULL, NULL, 'CITY', false, 61
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '230600');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '230700', '伊春市', '230000', '黑龙江', '230700', '伊春市', NULL, NULL, 'CITY', false, 62
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '230700');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '230800', '佳木斯市', '230000', '黑龙江', '230800', '佳木斯市', NULL, NULL, 'CITY', false, 63
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '230800');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '230900', '七台河市', '230000', '黑龙江', '230900', '七台河市', NULL, NULL, 'CITY', false, 64
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '230900');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '231000', '牡丹江市', '230000', '黑龙江', '231000', '牡丹江市', NULL, NULL, 'CITY', false, 65
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '231000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '231100', '黑河市', '230000', '黑龙江', '231100', '黑河市', NULL, NULL, 'CITY', false, 66
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '231100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '231200', '绥化市', '230000', '黑龙江', '231200', '绥化市', NULL, NULL, 'CITY', false, 67
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '231200');

-- ============== 上海市 ========================
INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '310000', '上海市', '310000', '上海', '310000', '上海市', NULL, NULL, 'PROVINCE', true, 9
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '310000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '310100', '上海市', '310000', '上海', '310100', '上海市', NULL, NULL, 'CITY', true, 68
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '310100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '310101', '黄浦区', '310000', '上海', '310100', '上海市', '310101', '黄浦区', 'DISTRICT', false, 8
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '310101');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '310104', '徐汇区', '310000', '上海', '310100', '上海市', '310104', '徐汇区', 'DISTRICT', false, 9
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '310104');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '310105', '长宁区', '310000', '上海', '310100', '上海市', '310105', '长宁区', 'DISTRICT', false, 10
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '310105');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '310106', '静安区', '310000', '上海', '310100', '上海市', '310106', '静安区', 'DISTRICT', false, 11
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '310106');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '310107', '普陀区', '310000', '上海', '310100', '上海市', '310107', '普陀区', 'DISTRICT', false, 12
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '310107');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '310109', '虹口区', '310000', '上海', '310100', '上海市', '310109', '虹口区', 'DISTRICT', false, 13
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '310109');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '310110', '杨浦区', '310000', '上海', '310100', '上海市', '310110', '杨浦区', 'DISTRICT', false, 14
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '310110');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '310115', '浦东新区', '310000', '上海', '310100', '上海市', '310115', '浦东新区', 'DISTRICT', false, 15
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '310115');

-- ============== 江苏省 ========================
INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '320000', '江苏省', '320000', '江苏', NULL, NULL, NULL, NULL, 'PROVINCE', false, 10
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '320000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '320100', '南京市', '320000', '江苏', '320100', '南京市', NULL, NULL, 'CITY', true, 69
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '320100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '320200', '无锡市', '320000', '江苏', '320200', '无锡市', NULL, NULL, 'CITY', true, 70
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '320200');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '320300', '徐州市', '320000', '江苏', '320300', '徐州市', NULL, NULL, 'CITY', false, 71
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '320300');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '320400', '常州市', '320000', '江苏', '320400', '常州市', NULL, NULL, 'CITY', true, 72
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '320400');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '320500', '苏州市', '320000', '江苏', '320500', '苏州市', NULL, NULL, 'CITY', true, 73
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '320500');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '320600', '南通市', '320000', '江苏', '320600', '南通市', NULL, NULL, 'CITY', false, 74
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '320600');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '320700', '连云港市', '320000', '江苏', '320700', '连云港市', NULL, NULL, 'CITY', false, 75
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '320700');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '320800', '淮安市', '320000', '江苏', '320800', '淮安市', NULL, NULL, 'CITY', false, 76
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '320800');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '320900', '盐城市', '320000', '江苏', '320900', '盐城市', NULL, NULL, 'CITY', false, 77
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '320900');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '321000', '扬州市', '320000', '江苏', '321000', '扬州市', NULL, NULL, 'CITY', false, 78
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '321000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '321100', '镇江市', '320000', '江苏', '321100', '镇江市', NULL, NULL, 'CITY', false, 79
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '321100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '321200', '泰州市', '320000', '江苏', '321200', '泰州市', NULL, NULL, 'CITY', false, 80
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '321200');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '321300', '宿迁市', '320000', '江苏', '321300', '宿迁市', NULL, NULL, 'CITY', false, 81
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '321300');

-- ============== 浙江省 ========================
INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '330000', '浙江省', '330000', '浙江', NULL, NULL, NULL, NULL, 'PROVINCE', false, 11
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '330000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '330100', '杭州市', '330000', '浙江', '330100', '杭州市', NULL, NULL, 'CITY', true, 82
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '330100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '330200', '宁波市', '330000', '浙江', '330200', '宁波市', NULL, NULL, 'CITY', true, 83
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '330200');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '330300', '温州市', '330000', '浙江', '330300', '温州市', NULL, NULL, 'CITY', true, 84
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '330300');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '330400', '嘉兴市', '330000', '浙江', '330400', '嘉兴市', NULL, NULL, 'CITY', false, 85
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '330400');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '330500', '湖州市', '330000', '浙江', '330500', '湖州市', NULL, NULL, 'CITY', false, 86
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '330500');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '330600', '绍兴市', '330000', '浙江', '330600', '绍兴市', NULL, NULL, 'CITY', false, 87
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '330600');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '330700', '金华市', '330000', '浙江', '330700', '金华市', NULL, NULL, 'CITY', false, 88
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '330700');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '330800', '衢州市', '330000', '浙江', '330800', '衢州市', NULL, NULL, 'CITY', false, 89
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '330800');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '330900', '舟山市', '330000', '浙江', '330900', '舟山市', NULL, NULL, 'CITY', false, 90
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '330900');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '331000', '台州市', '330000', '浙江', '331000', '台州市', NULL, NULL, 'CITY', false, 91
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '331000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '331100', '丽水市', '330000', '浙江', '331100', '丽水市', NULL, NULL, 'CITY', false, 92
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '331100');

-- ============== 安徽省 ========================
INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '340000', '安徽省', '340000', '安徽', NULL, NULL, NULL, NULL, 'PROVINCE', false, 12
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '340000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '340100', '合肥市', '340000', '安徽', '340100', '合肥市', NULL, NULL, 'CITY', true, 93
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '340100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '340200', '芜湖市', '340000', '安徽', '340200', '芜湖市', NULL, NULL, 'CITY', false, 94
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '340200');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '340300', '蚌埠市', '340000', '安徽', '340300', '蚌埠市', NULL, NULL, 'CITY', false, 95
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '340300');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '340400', '淮南市', '340000', '安徽', '340400', '淮南市', NULL, NULL, 'CITY', false, 96
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '340400');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '340500', '马鞍山市', '340000', '安徽', '340500', '马鞍山市', NULL, NULL, 'CITY', false, 97
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '340500');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '340600', '淮北市', '340000', '安徽', '340600', '淮北市', NULL, NULL, 'CITY', false, 98
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '340600');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '340700', '铜陵市', '340000', '安徽', '340700', '铜陵市', NULL, NULL, 'CITY', false, 99
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '340700');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '340800', '安庆市', '340000', '安徽', '340800', '安庆市', NULL, NULL, 'CITY', false, 100
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '340800');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '341000', '黄山市', '340000', '安徽', '341000', '黄山市', NULL, NULL, 'CITY', false, 101
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '341000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '341100', '滁州市', '340000', '安徽', '341100', '滁州市', NULL, NULL, 'CITY', false, 102
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '341100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '341200', '阜阳市', '340000', '安徽', '341200', '阜阳市', NULL, NULL, 'CITY', false, 103
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '341200');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '341300', '宿州市', '340000', '安徽', '341300', '宿州市', NULL, NULL, 'CITY', false, 104
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '341300');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '341500', '六安市', '340000', '安徽', '341500', '六安市', NULL, NULL, 'CITY', false, 105
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '341500');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '341600', '亳州市', '340000', '安徽', '341600', '亳州市', NULL, NULL, 'CITY', false, 106
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '341600');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '341700', '池州市', '340000', '安徽', '341700', '池州市', NULL, NULL, 'CITY', false, 107
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '341700');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '341800', '宣城市', '340000', '安徽', '341800', '宣城市', NULL, NULL, 'CITY', false, 108
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '341800');

-- ============== 福建省 ========================
INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '350000', '福建省', '350000', '福建', NULL, NULL, NULL, NULL, 'PROVINCE', false, 13
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '350000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '350100', '福州市', '350000', '福建', '350100', '福州市', NULL, NULL, 'CITY', true, 109
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '350100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '350200', '厦门市', '350000', '福建', '350200', '厦门市', NULL, NULL, 'CITY', true, 110
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '350200');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '350300', '莆田市', '350000', '福建', '350300', '莆田市', NULL, NULL, 'CITY', false, 111
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '350300');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '350400', '三明市', '350000', '福建', '350400', '三明市', NULL, NULL, 'CITY', false, 112
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '350400');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '350500', '泉州市', '350000', '福建', '350500', '泉州市', NULL, NULL, 'CITY', true, 113
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '350500');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '350600', '漳州市', '350000', '福建', '350600', '漳州市', NULL, NULL, 'CITY', false, 114
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '350600');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '350700', '南平市', '350000', '福建', '350700', '南平市', NULL, NULL, 'CITY', false, 115
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '350700');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '350800', '龙岩市', '350000', '福建', '350800', '龙岩市', NULL, NULL, 'CITY', false, 116
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '350800');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '350900', '宁德市', '350000', '福建', '350900', '宁德市', NULL, NULL, 'CITY', false, 117
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '350900');

-- ============== 江西省 ========================
INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '360000', '江西省', '360000', '江西', NULL, NULL, NULL, NULL, 'PROVINCE', false, 14
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '360000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '360100', '南昌市', '360000', '江西', '360100', '南昌市', NULL, NULL, 'CITY', true, 118
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '360100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '360200', '景德镇市', '360000', '江西', '360200', '景德镇市', NULL, NULL, 'CITY', false, 119
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '360200');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '360300', '萍乡市', '360000', '江西', '360300', '萍乡市', NULL, NULL, 'CITY', false, 120
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '360300');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '360400', '九江市', '360000', '江西', '360400', '九江市', NULL, NULL, 'CITY', false, 121
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '360400');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '360500', '新余市', '360000', '江西', '360500', '新余市', NULL, NULL, 'CITY', false, 122
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '360500');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '360600', '鹰潭市', '360000', '江西', '360600', '鹰潭市', NULL, NULL, 'CITY', false, 123
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '360600');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '360700', '赣州市', '360000', '江西', '360700', '赣州市', NULL, NULL, 'CITY', false, 124
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '360700');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '360800', '吉安市', '360000', '江西', '360800', '吉安市', NULL, NULL, 'CITY', false, 125
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '360800');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '360900', '宜春市', '360000', '江西', '360900', '宜春市', NULL, NULL, 'CITY', false, 126
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '360900');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '361000', '抚州市', '360000', '江西', '361000', '抚州市', NULL, NULL, 'CITY', false, 127
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '361000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '361100', '上饶市', '360000', '江西', '361100', '上饶市', NULL, NULL, 'CITY', false, 128
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '361100');

-- ============== 山东省 ========================
INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '370000', '山东省', '370000', '山东', NULL, NULL, NULL, NULL, 'PROVINCE', false, 15
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '370000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '370100', '济南市', '370000', '山东', '370100', '济南市', NULL, NULL, 'CITY', true, 129
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '370100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '370200', '青岛市', '370000', '山东', '370200', '青岛市', NULL, NULL, 'CITY', true, 130
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '370200');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '370300', '淄博市', '370000', '山东', '370300', '淄博市', NULL, NULL, 'CITY', false, 131
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '370300');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '370400', '枣庄市', '370000', '山东', '370400', '枣庄市', NULL, NULL, 'CITY', false, 132
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '370400');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '370500', '东营市', '370000', '山东', '370500', '东营市', NULL, NULL, 'CITY', false, 133
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '370500');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '370600', '烟台市', '370000', '山东', '370600', '烟台市', NULL, NULL, 'CITY', true, 134
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '370600');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '370700', '潍坊市', '370000', '山东', '370700', '潍坊市', NULL, NULL, 'CITY', false, 135
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '370700');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '370800', '济宁市', '370000', '山东', '370800', '济宁市', NULL, NULL, 'CITY', false, 136
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '370800');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '370900', '泰安市', '370000', '山东', '370900', '泰安市', NULL, NULL, 'CITY', false, 137
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '370900');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '371000', '威海市', '370000', '山东', '371000', '威海市', NULL, NULL, 'CITY', false, 138
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '371000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '371100', '日照市', '370000', '山东', '371100', '日照市', NULL, NULL, 'CITY', false, 139
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '371100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '371200', '莱芜市', '370000', '山东', '371200', '莱芜市', NULL, NULL, 'CITY', false, 140
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '371200');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '371300', '临沂市', '370000', '山东', '371300', '临沂市', NULL, NULL, 'CITY', false, 141
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '371300');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '371400', '德州市', '370000', '山东', '371400', '德州市', NULL, NULL, 'CITY', false, 142
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '371400');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '371500', '聊城市', '370000', '山东', '371500', '聊城市', NULL, NULL, 'CITY', false, 143
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '371500');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '371600', '滨州市', '370000', '山东', '371600', '滨州市', NULL, NULL, 'CITY', false, 144
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '371600');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '371700', '菏泽市', '370000', '山东', '371700', '菏泽市', NULL, NULL, 'CITY', false, 145
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '371700');

-- ============== 河南省 ========================
INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '410000', '河南省', '410000', '河南', NULL, NULL, NULL, NULL, 'PROVINCE', false, 16
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '410000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '410100', '郑州市', '410000', '河南', '410100', '郑州市', NULL, NULL, 'CITY', true, 146
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '410100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '410200', '开封市', '410000', '河南', '410200', '开封市', NULL, NULL, 'CITY', false, 147
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '410200');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '410300', '洛阳市', '410000', '河南', '410300', '洛阳市', NULL, NULL, 'CITY', true, 148
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '410300');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '410400', '平顶山市', '410000', '河南', '410400', '平顶山市', NULL, NULL, 'CITY', false, 149
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '410400');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '410500', '安阳市', '410000', '河南', '410500', '安阳市', NULL, NULL, 'CITY', false, 150
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '410500');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '410600', '鹤壁市', '410000', '河南', '410600', '鹤壁市', NULL, NULL, 'CITY', false, 151
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '410600');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '410700', '新乡市', '410000', '河南', '410700', '新乡市', NULL, NULL, 'CITY', false, 152
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '410700');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '410800', '焦作市', '410000', '河南', '410800', '焦作市', NULL, NULL, 'CITY', false, 153
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '410800');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '410900', '濮阳市', '410000', '河南', '410900', '濮阳市', NULL, NULL, 'CITY', false, 154
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '410900');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '411000', '许昌市', '410000', '河南', '411000', '许昌市', NULL, NULL, 'CITY', false, 155
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '411000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '411100', '漯河市', '410000', '河南', '411100', '漯河市', NULL, NULL, 'CITY', false, 156
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '411100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '411200', '三门峡市', '410000', '河南', '411200', '三门峡市', NULL, NULL, 'CITY', false, 157
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '411200');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '411300', '南阳市', '410000', '河南', '411300', '南阳市', NULL, NULL, 'CITY', false, 158
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '411300');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '411400', '商丘市', '410000', '河南', '411400', '商丘市', NULL, NULL, 'CITY', false, 159
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '411400');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '411500', '信阳市', '410000', '河南', '411500', '信阳市', NULL, NULL, 'CITY', false, 160
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '411500');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '411600', '周口市', '410000', '河南', '411600', '周口市', NULL, NULL, 'CITY', false, 161
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '411600');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '411700', '驻马店市', '410000', '河南', '411700', '驻马店市', NULL, NULL, 'CITY', false, 162
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '411700');

-- ============== 湖北省 ========================
INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '420000', '湖北省', '420000', '湖北', NULL, NULL, NULL, NULL, 'PROVINCE', false, 17
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '420000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '420100', '武汉市', '420000', '湖北', '420100', '武汉市', NULL, NULL, 'CITY', true, 163
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '420100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '420200', '黄石市', '420000', '湖北', '420200', '黄石市', NULL, NULL, 'CITY', false, 164
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '420200');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '420300', '十堰市', '420000', '湖北', '420300', '十堰市', NULL, NULL, 'CITY', false, 165
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '420300');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '420500', '宜昌市', '420000', '湖北', '420500', '宜昌市', NULL, NULL, 'CITY', true, 166
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '420500');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '420600', '襄阳市', '420000', '湖北', '420600', '襄阳市', NULL, NULL, 'CITY', false, 167
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '420600');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '420700', '鄂州市', '420000', '湖北', '420700', '鄂州市', NULL, NULL, 'CITY', false, 168
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '420700');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '420800', '荆门市', '420000', '湖北', '420800', '荆门市', NULL, NULL, 'CITY', false, 169
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '420800');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '420900', '孝感市', '420000', '湖北', '420900', '孝感市', NULL, NULL, 'CITY', false, 170
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '420900');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '421000', '荆州市', '420000', '湖北', '421000', '荆州市', NULL, NULL, 'CITY', false, 171
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '421000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '421100', '黄冈市', '420000', '湖北', '421100', '黄冈市', NULL, NULL, 'CITY', false, 172
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '421100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '421200', '咸宁市', '420000', '湖北', '421200', '咸宁市', NULL, NULL, 'CITY', false, 173
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '421200');

-- ============== 湖南省 ========================
INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '430000', '湖南省', '430000', '湖南', NULL, NULL, NULL, NULL, 'PROVINCE', false, 18
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '430000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '430100', '长沙市', '430000', '湖南', '430100', '长沙市', NULL, NULL, 'CITY', true, 175
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '430100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '430200', '株洲市', '430000', '湖南', '430200', '株洲市', NULL, NULL, 'CITY', false, 176
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '430200');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '430300', '湘潭市', '430000', '湖南', '430300', '湘潭市', NULL, NULL, 'CITY', false, 177
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '430300');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '430400', '衡阳市', '430000', '湖南', '430400', '衡阳市', NULL, NULL, 'CITY', false, 178
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '430400');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '430500', '邵阳市', '430000', '湖南', '430500', '邵阳市', NULL, NULL, 'CITY', false, 179
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '430500');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '430600', '岳阳市', '430000', '湖南', '430600', '岳阳市', NULL, NULL, 'CITY', false, 180
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '430600');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '430700', '常德市', '430000', '湖南', '430700', '常德市', NULL, NULL, 'CITY', false, 181
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '430700');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '430800', '张家界市', '430000', '湖南', '430800', '张家界市', NULL, NULL, 'CITY', false, 182
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '430800');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '430900', '益阳市', '430000', '湖南', '430900', '益阳市', NULL, NULL, 'CITY', false, 183
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '430900');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '431000', '郴州市', '430000', '湖南', '431000', '郴州市', NULL, NULL, 'CITY', false, 184
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '431000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '431100', '永州市', '430000', '湖南', '431100', '永州市', NULL, NULL, 'CITY', false, 185
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '431100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '431200', '怀化市', '430000', '湖南', '431200', '怀化市', NULL, NULL, 'CITY', false, 186
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '431200');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '431300', '娄底市', '430000', '湖南', '431300', '娄底市', NULL, NULL, 'CITY', false, 187
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '431300');

-- ============== 广东省 ========================
INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '440000', '广东省', '440000', '广东', NULL, NULL, NULL, NULL, 'PROVINCE', false, 19
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '440000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '440100', '广州市', '440000', '广东', '440100', '广州市', NULL, NULL, 'CITY', true, 188
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '440100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '440200', '韶关市', '440000', '广东', '440200', '韶关市', NULL, NULL, 'CITY', false, 189
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '440200');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '440300', '深圳市', '440000', '广东', '440300', '深圳市', NULL, NULL, 'CITY', true, 190
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '440300');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '440400', '珠海市', '440000', '广东', '440400', '珠海市', NULL, NULL, 'CITY', true, 191
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '440400');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '440500', '汕头市', '440000', '广东', '440500', '汕头市', NULL, NULL, 'CITY', false, 192
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '440500');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '440600', '佛山市', '440000', '广东', '440600', '佛山市', NULL, NULL, 'CITY', true, 193
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '440600');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '440700', '江门市', '440000', '广东', '440700', '江门市', NULL, NULL, 'CITY', false, 194
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '440700');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '440800', '湛江市', '440000', '广东', '440800', '湛江市', NULL, NULL, 'CITY', false, 195
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '440800');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '440900', '茂名市', '440000', '广东', '440900', '茂名市', NULL, NULL, 'CITY', false, 196
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '440900');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '441200', '肇庆市', '440000', '广东', '441200', '肇庆市', NULL, NULL, 'CITY', false, 197
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '441200');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '441300', '惠州市', '440000', '广东', '441300', '惠州市', NULL, NULL, 'CITY', true, 198
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '441300');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '441400', '梅州市', '440000', '广东', '441400', '梅州市', NULL, NULL, 'CITY', false, 199
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '441400');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '441500', '汕尾市', '440000', '广东', '441500', '汕尾市', NULL, NULL, 'CITY', false, 200
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '441500');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '441600', '河源市', '440000', '广东', '441600', '河源市', NULL, NULL, 'CITY', false, 201
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '441600');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '441700', '阳江市', '440000', '广东', '441700', '阳江市', NULL, NULL, 'CITY', false, 202
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '441700');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '441800', '清远市', '440000', '广东', '441800', '清远市', NULL, NULL, 'CITY', false, 203
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '441800');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '441900', '东莞市', '440000', '广东', '441900', '东莞市', NULL, NULL, 'CITY', true, 204
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '441900');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '442000', '中山市', '440000', '广东', '442000', '中山市', NULL, NULL, 'CITY', true, 205
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '442000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '445100', '潮州市', '440000', '广东', '445100', '潮州市', NULL, NULL, 'CITY', false, 206
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '445100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '445200', '揭阳市', '440000', '广东', '445200', '揭阳市', NULL, NULL, 'CITY', false, 207
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '445200');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '445300', '云浮市', '440000', '广东', '445300', '云浮市', NULL, NULL, 'CITY', false, 208
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '445300');

-- ============== 广西壮族自治区 ========================
INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '450000', '广西壮族自治区', '450000', '广西', NULL, NULL, NULL, NULL, 'PROVINCE', false, 20
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '450000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '450100', '南宁市', '450000', '广西', '450100', '南宁市', NULL, NULL, 'CITY', true, 209
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '450100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '450200', '柳州市', '450000', '广西', '450200', '柳州市', NULL, NULL, 'CITY', false, 210
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '450200');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '450300', '桂林市', '450000', '广西', '450300', '桂林市', NULL, NULL, 'CITY', true, 211
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '450300');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '450400', '梧州市', '450000', '广西', '450400', '梧州市', NULL, NULL, 'CITY', false, 212
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '450400');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '450500', '北海市', '450000', '广西', '450500', '北海市', NULL, NULL, 'CITY', false, 213
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '450500');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '450600', '防城港市', '450000', '广西', '450600', '防城港市', NULL, NULL, 'CITY', false, 214
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '450600');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '450700', '钦州市', '450000', '广西', '450700', '钦州市', NULL, NULL, 'CITY', false, 215
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '450700');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '450800', '贵港市', '450000', '广西', '450800', '贵港市', NULL, NULL, 'CITY', false, 216
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '450800');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '450900', '玉林市', '450000', '广西', '450900', '玉林市', NULL, NULL, 'CITY', false, 217
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '450900');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '451000', '百色市', '450000', '广西', '451000', '百色市', NULL, NULL, 'CITY', false, 218
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '451000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '451100', '贺州市', '450000', '广西', '451100', '贺州市', NULL, NULL, 'CITY', false, 219
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '451100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '451200', '河池市', '450000', '广西', '451200', '河池市', NULL, NULL, 'CITY', false, 220
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '451200');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '451300', '来宾市', '450000', '广西', '451300', '来宾市', NULL, NULL, 'CITY', false, 221
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '451300');

-- ============== 海南省 ========================
INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '460000', '海南省', '460000', '海南', NULL, NULL, NULL, NULL, 'PROVINCE', false, 21
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '460000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '460100', '海口市', '460000', '海南', '460100', '海口市', NULL, NULL, 'CITY', true, 223
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '460100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '460200', '三亚市', '460000', '海南', '460200', '三亚市', NULL, NULL, 'CITY', true, 224
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '460200');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '460300', '三沙市', '460000', '海南', '460300', '三沙市', NULL, NULL, 'CITY', false, 225
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '460300');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '460400', '儋州市', '460000', '海南', '460400', '儋州市', NULL, NULL, 'CITY', false, 226
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '460400');

-- ============== 重庆市 ========================
INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '500000', '重庆市', '500000', '重庆', '500000', '重庆市', NULL, NULL, 'PROVINCE', true, 22
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '500000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '500100', '重庆市', '500000', '重庆', '500100', '重庆市', NULL, NULL, 'CITY', true, 227
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '500100');

-- ============== 四川省 ========================
INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '510000', '四川省', '510000', '四川', NULL, NULL, NULL, NULL, 'PROVINCE', false, 23
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '510000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '510100', '成都市', '510000', '四川', '510100', '成都市', NULL, NULL, 'CITY', true, 228
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '510100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '510300', '自贡市', '510000', '四川', '510300', '自贡市', NULL, NULL, 'CITY', false, 229
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '510300');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '510400', '攀枝花市', '510000', '四川', '510400', '攀枝花市', NULL, NULL, 'CITY', false, 230
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '510400');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '510500', '泸州市', '510000', '四川', '510500', '泸州市', NULL, NULL, 'CITY', false, 231
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '510500');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '510600', '德阳市', '510000', '四川', '510600', '德阳市', NULL, NULL, 'CITY', false, 232
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '510600');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '510700', '绵阳市', '510000', '四川', '510700', '绵阳市', NULL, NULL, 'CITY', true, 233
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '510700');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '510800', '广元市', '510000', '四川', '510800', '广元市', NULL, NULL, 'CITY', false, 234
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '510800');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '510900', '遂宁市', '510000', '四川', '510900', '遂宁市', NULL, NULL, 'CITY', false, 235
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '510900');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '511000', '内江市', '510000', '四川', '511000', '内江市', NULL, NULL, 'CITY', false, 236
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '511000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '511100', '乐山市', '510000', '四川', '511100', '乐山市', NULL, NULL, 'CITY', false, 237
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '511100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '511300', '南充市', '510000', '四川', '511300', '南充市', NULL, NULL, 'CITY', false, 238
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '511300');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '511500', '宜宾市', '510000', '四川', '511500', '宜宾市', NULL, NULL, 'CITY', false, 239
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '511500');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '511600', '广安市', '510000', '四川', '511600', '广安市', NULL, NULL, 'CITY', false, 240
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '511600');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '511700', '达州市', '510000', '四川', '511700', '达州市', NULL, NULL, 'CITY', false, 241
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '511700');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '511800', '雅安市', '510000', '四川', '511800', '雅安市', NULL, NULL, 'CITY', false, 242
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '511800');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '511900', '巴中市', '510000', '四川', '511900', '巴中市', NULL, NULL, 'CITY', false, 243
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '511900');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '512000', '资阳市', '510000', '四川', '512000', '资阳市', NULL, NULL, 'CITY', false, 244
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '512000');

-- ============== 贵州省 ========================
INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '520000', '贵州省', '520000', '贵州', NULL, NULL, NULL, NULL, 'PROVINCE', false, 24
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '520000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '520100', '贵阳市', '520000', '贵州', '520100', '贵阳市', NULL, NULL, 'CITY', true, 245
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '520100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '520200', '六盘水市', '520000', '贵州', '520200', '六盘水市', NULL, NULL, 'CITY', false, 246
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '520200');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '520300', '遵义市', '520000', '贵州', '520300', '遵义市', NULL, NULL, 'CITY', false, 247
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '520300');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '520400', '安顺市', '520000', '贵州', '520400', '安顺市', NULL, NULL, 'CITY', false, 248
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '520400');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '520500', '毕节市', '520000', '贵州', '520500', '毕节市', NULL, NULL, 'CITY', false, 249
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '520500');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '520600', '铜仁市', '520000', '贵州', '520600', '铜仁市', NULL, NULL, 'CITY', false, 250
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '520600');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '522300', '黔西南布依族苗族自治州', '520000', '贵州', '522300', '黔西南布依族苗族自治州', NULL, NULL, 'CITY', false, 251
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '522300');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '522600', '黔东南苗族侗族自治州', '520000', '贵州', '522600', '黔东南苗族侗族自治州', NULL, NULL, 'CITY', false, 252
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '522600');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '522700', '黔南布依族苗族自治州', '520000', '贵州', '522700', '黔南布依族苗族自治州', NULL, NULL, 'CITY', false, 253
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '522700');

-- ============== 云南省 ========================
INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '530000', '云南省', '530000', '云南', NULL, NULL, NULL, NULL, 'PROVINCE', false, 25
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '530000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '530100', '昆明市', '530000', '云南', '530100', '昆明市', NULL, NULL, 'CITY', true, 254
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '530100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '530300', '曲靖市', '530000', '云南', '530300', '曲靖市', NULL, NULL, 'CITY', false, 255
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '530300');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '530400', '玉溪市', '530000', '云南', '530400', '玉溪市', NULL, NULL, 'CITY', false, 256
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '530400');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '530500', '保山市', '530000', '云南', '530500', '保山市', NULL, NULL, 'CITY', false, 257
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '530500');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '530600', '昭通市', '530000', '云南', '530600', '昭通市', NULL, NULL, 'CITY', false, 258
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '530600');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '530700', '丽江市', '530000', '云南', '530700', '丽江市', NULL, NULL, 'CITY', false, 259
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '530700');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '530800', '普洱市', '530000', '云南', '530800', '普洱市', NULL, NULL, 'CITY', false, 260
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '530800');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '530900', '临沧市', '530000', '云南', '530900', '临沧市', NULL, NULL, 'CITY', false, 261
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '530900');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '532300', '楚雄彝族自治州', '530000', '云南', '532300', '楚雄彝族自治州', NULL, NULL, 'CITY', false, 262
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '532300');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '532500', '红河哈尼族彝族自治州', '530000', '云南', '532500', '红河哈尼族彝族自治州', NULL, NULL, 'CITY', false, 263
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '532500');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '532600', '文山壮族苗族自治州', '530000', '云南', '532600', '文山壮族苗族自治州', NULL, NULL, 'CITY', false, 264
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '532600');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '532800', '西双版纳傣族自治州', '530000', '云南', '532800', '西双版纳傣族自治州', NULL, NULL, 'CITY', false, 265
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '532800');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '532900', '大理白族自治州', '530000', '云南', '532900', '大理白族自治州', NULL, NULL, 'CITY', false, 266
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '532900');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '533100', '德宏傣族景颇族自治州', '530000', '云南', '533100', '德宏傣族景颇族自治州', NULL, NULL, 'CITY', false, 267
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '533100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '533300', '怒江傈僳族自治州', '530000', '云南', '533300', '怒江傈僳族自治州', NULL, NULL, 'CITY', false, 268
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '533300');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '533400', '迪庆藏族自治州', '530000', '云南', '533400', '迪庆藏族自治州', NULL, NULL, 'CITY', false, 269
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '533400');

-- ============== 西藏自治区 ========================
INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '540000', '西藏自治区', '540000', '西藏', NULL, NULL, NULL, NULL, 'PROVINCE', false, 26
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '540000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '540100', '拉萨市', '540000', '西藏', '540100', '拉萨市', NULL, NULL, 'CITY', false, 270
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '540100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '540200', '日喀则市', '540000', '西藏', '540200', '日喀则市', NULL, NULL, 'CITY', false, 271
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '540200');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '540300', '昌都市', '540000', '西藏', '540300', '昌都市', NULL, NULL, 'CITY', false, 272
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '540300');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '540400', '林芝市', '540000', '西藏', '540400', '林芝市', NULL, NULL, 'CITY', false, 273
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '540400');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '540500', '山南市', '540000', '西藏', '540500', '山南市', NULL, NULL, 'CITY', false, 274
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '540500');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '540600', '那曲市', '540000', '西藏', '540600', '那曲市', NULL, NULL, 'CITY', false, 275
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '540600');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '542500', '阿里地区', '540000', '西藏', '542500', '阿里地区', NULL, NULL, 'CITY', false, 276
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '542500');

-- ============== 陕西省 ========================
INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '610000', '陕西省', '610000', '陕西', NULL, NULL, NULL, NULL, 'PROVINCE', false, 27
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '610000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '610100', '西安市', '610000', '陕西', '610100', '西安市', NULL, NULL, 'CITY', true, 277
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '610100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '610200', '铜川市', '610000', '陕西', '610200', '铜川市', NULL, NULL, 'CITY', false, 278
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '610200');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '610300', '宝鸡市', '610000', '陕西', '610300', '宝鸡市', NULL, NULL, 'CITY', false, 279
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '610300');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '610400', '咸阳市', '610000', '陕西', '610400', '咸阳市', NULL, NULL, 'CITY', false, 280
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '610400');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '610500', '渭南市', '610000', '陕西', '610500', '渭南市', NULL, NULL, 'CITY', false, 281
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '610500');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '610600', '延安市', '610000', '陕西', '610600', '延安市', NULL, NULL, 'CITY', false, 282
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '610600');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '610700', '汉中市', '610000', '陕西', '610700', '汉中市', NULL, NULL, 'CITY', false, 283
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '610700');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '610800', '榆林市', '610000', '陕西', '610800', '榆林市', NULL, NULL, 'CITY', false, 284
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '610800');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '610900', '安康市', '610000', '陕西', '610900', '安康市', NULL, NULL, 'CITY', false, 285
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '610900');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '611000', '商洛市', '610000', '陕西', '611000', '商洛市', NULL, NULL, 'CITY', false, 286
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '611000');

-- ============== 甘肃省 ========================
INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '620000', '甘肃省', '620000', '甘肃', NULL, NULL, NULL, NULL, 'PROVINCE', false, 28
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '620000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '620100', '兰州市', '620000', '甘肃', '620100', '兰州市', NULL, NULL, 'CITY', true, 287
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '620100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '620200', '嘉峪关市', '620000', '甘肃', '620200', '嘉峪关市', NULL, NULL, 'CITY', false, 288
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '620200');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '620300', '金昌市', '620000', '甘肃', '620300', '金昌市', NULL, NULL, 'CITY', false, 289
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '620300');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '620400', '白银市', '620000', '甘肃', '620400', '白银市', NULL, NULL, 'CITY', false, 290
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '620400');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '620500', '天水市', '620000', '甘肃', '620500', '天水市', NULL, NULL, 'CITY', false, 291
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '620500');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '620600', '武威市', '620000', '甘肃', '620600', '武威市', NULL, NULL, 'CITY', false, 292
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '620600');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '620700', '张掖市', '620000', '甘肃', '620700', '张掖市', NULL, NULL, 'CITY', false, 293
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '620700');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '620800', '平凉市', '620000', '甘肃', '620800', '平凉市', NULL, NULL, 'CITY', false, 294
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '620800');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '620900', '酒泉市', '620000', '甘肃', '620900', '酒泉市', NULL, NULL, 'CITY', false, 295
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '620900');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '621000', '庆阳市', '620000', '甘肃', '621000', '庆阳市', NULL, NULL, 'CITY', false, 296
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '621000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '621100', '定西市', '620000', '甘肃', '621100', '定西市', NULL, NULL, 'CITY', false, 297
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '621100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '621200', '陇南市', '620000', '甘肃', '621200', '陇南市', NULL, NULL, 'CITY', false, 298
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '621200');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '622900', '临夏回族自治州', '620000', '甘肃', '622900', '临夏回族自治州', NULL, NULL, 'CITY', false, 299
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '622900');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '623000', '甘南藏族自治州', '620000', '甘肃', '623000', '甘南藏族自治州', NULL, NULL, 'CITY', false, 300
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '623000');

-- ============== 青海省 ========================
INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '630000', '青海省', '630000', '青海', NULL, NULL, NULL, NULL, 'PROVINCE', false, 29
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '630000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '630100', '西宁市', '630000', '青海', '630100', '西宁市', NULL, NULL, 'CITY', false, 301
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '630100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '630200', '海东市', '630000', '青海', '630200', '海东市', NULL, NULL, 'CITY', false, 302
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '630200');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '632200', '海北藏族自治州', '630000', '青海', '632200', '海北藏族自治州', NULL, NULL, 'CITY', false, 303
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '632200');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '632300', '黄南藏族自治州', '630000', '青海', '632300', '黄南藏族自治州', NULL, NULL, 'CITY', false, 304
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '632300');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '632500', '海南藏族自治州', '630000', '青海', '632500', '海南藏族自治州', NULL, NULL, 'CITY', false, 305
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '632500');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '632600', '果洛藏族自治州', '630000', '青海', '632600', '果洛藏族自治州', NULL, NULL, 'CITY', false, 306
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '632600');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '632700', '玉树藏族自治州', '630000', '青海', '632700', '玉树藏族自治州', NULL, NULL, 'CITY', false, 307
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '632700');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '632800', '海西蒙古族藏族自治州', '630000', '青海', '632800', '海西蒙古族藏族自治州', NULL, NULL, 'CITY', false, 308
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '632800');

-- ============== 宁夏回族自治区 ========================
INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '640000', '宁夏回族自治区', '640000', '宁夏', NULL, NULL, NULL, NULL, 'PROVINCE', false, 30
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '640000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '640100', '银川市', '640000', '宁夏', '640100', '银川市', NULL, NULL, 'CITY', true, 309
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '640100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '640200', '石嘴山市', '640000', '宁夏', '640200', '石嘴山市', NULL, NULL, 'CITY', false, 310
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '640200');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '640300', '吴忠市', '640000', '宁夏', '640300', '吴忠市', NULL, NULL, 'CITY', false, 311
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '640300');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '640400', '固原市', '640000', '宁夏', '640400', '固原市', NULL, NULL, 'CITY', false, 312
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '640400');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '640500', '中卫市', '640000', '宁夏', '640500', '中卫市', NULL, NULL, 'CITY', false, 313
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '640500');

-- ============== 新疆维吾尔自治区 ========================
INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '650000', '新疆维吾尔自治区', '650000', '新疆', NULL, NULL, NULL, NULL, 'PROVINCE', false, 31
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '650000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '650100', '乌鲁木齐市', '650000', '新疆', '650100', '乌鲁木齐市', NULL, NULL, 'CITY', true, 314
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '650100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '650200', '克拉玛依市', '650000', '新疆', '650200', '克拉玛依市', NULL, NULL, 'CITY', false, 315
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '650200');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '650400', '吐鲁番市', '650000', '新疆', '650400', '吐鲁番市', NULL, NULL, 'CITY', false, 316
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '650400');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '650500', '哈密市', '650000', '新疆', '650500', '哈密市', NULL, NULL, 'CITY', false, 317
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '650500');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '652300', '昌吉回族自治州', '650000', '新疆', '652300', '昌吉回族自治州', NULL, NULL, 'CITY', false, 318
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '652300');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '652700', '博尔塔拉蒙古自治州', '650000', '新疆', '652700', '博尔塔拉蒙古自治州', NULL, NULL, 'CITY', false, 319
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '652700');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '652800', '巴音郭楞蒙古自治州', '650000', '新疆', '652800', '巴音郭楞蒙古自治州', NULL, NULL, 'CITY', false, 320
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '652800');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '652900', '阿克苏地区', '650000', '新疆', '652900', '阿克苏地区', NULL, NULL, 'CITY', false, 321
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '652900');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '653000', '克孜勒苏柯尔克孜自治州', '650000', '新疆', '653000', '克孜勒苏柯尔克孜自治州', NULL, NULL, 'CITY', false, 322
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '653000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '653100', '喀什地区', '650000', '新疆', '653100', '喀什地区', NULL, NULL, 'CITY', false, 323
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '653100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '653200', '和田地区', '650000', '新疆', '653200', '和田地区', NULL, NULL, 'CITY', false, 324
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '653200');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '654000', '伊犁哈萨克自治州', '650000', '新疆', '654000', '伊犁哈萨克自治州', NULL, NULL, 'CITY', false, 325
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '654000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '654200', '塔城地区', '650000', '新疆', '654200', '塔城地区', NULL, NULL, 'CITY', false, 326
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '654200');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '654300', '阿勒泰地区', '650000', '新疆', '654300', '阿勒泰地区', NULL, NULL, 'CITY', false, 327
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '654300');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '659001', '石河子市', '650000', '新疆', '659001', '石河子市', NULL, NULL, 'CITY', false, 328
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '659001');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '659002', '阿拉尔市', '650000', '新疆', '659002', '阿拉尔市', NULL, NULL, 'CITY', false, 329
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '659002');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '659003', '图木舒克市', '650000', '新疆', '659003', '图木舒克市', NULL, NULL, 'CITY', false, 330
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '659003');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '659004', '五家渠市', '650000', '新疆', '659004', '五家渠市', NULL, NULL, 'CITY', false, 331
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '659004');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '659005', '北屯市', '650000', '新疆', '659005', '北屯市', NULL, NULL, 'CITY', false, 332
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '659005');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '659006', '铁门关市', '650000', '新疆', '659006', '铁门关市', NULL, NULL, 'CITY', false, 333
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '659006');

-- ============== 台湾省 ========================
INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '710000', '台湾省', '710000', '台湾', NULL, NULL, NULL, NULL, 'PROVINCE', false, 32
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '710000');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '710100', '台北市', '710000', '台湾', '710100', '台北市', NULL, NULL, 'CITY', false, 334
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '710100');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '710200', '新北市', '710000', '台湾', '710200', '新北市', NULL, NULL, 'CITY', false, 335
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '710200');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '710300', '桃园市', '710000', '台湾', '710300', '桃园市', NULL, NULL, 'CITY', false, 336
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '710300');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '710400', '台中市', '710000', '台湾', '710400', '台中市', NULL, NULL, 'CITY', false, 337
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '710400');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '710500', '台南市', '710000', '台湾', '710500', '台南市', NULL, NULL, 'CITY', false, 338
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '710500');

INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '710600', '高雄市', '710000', '台湾', '710600', '高雄市', NULL, NULL, 'CITY', false, 339
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '710600');

-- ============== 香港特别行政区 ========================
INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '810000', '香港特别行政区', '810000', '香港', '810000', '香港', NULL, NULL, 'PROVINCE', true, 33
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '810000');

-- ============== 澳门特别行政区 ========================
INSERT INTO tc_cities (code, name, province_code, province_name, city_code, city_name, district_code, district_name, level, hot, sort_order)
SELECT '820000', '澳门特别行政区', '820000', '澳门', '820000', '澳门', NULL, NULL, 'PROVINCE', false, 34
WHERE NOT EXISTS (SELECT 1 FROM tc_cities WHERE code = '820000');