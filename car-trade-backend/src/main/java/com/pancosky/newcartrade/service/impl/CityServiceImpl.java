package com.pancosky.newcartrade.service.impl;

import com.pancosky.newcartrade.service.CityService;
import com.pancosky.newcartrade.vo.CityVO;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 城市服务实现类
 */
@Service
public class CityServiceImpl implements CityService {

    /**
     * Mock城市数据
     */
    private static final List<CityVO> CITY_DATA = Arrays.asList(
            // 全国（特殊项）
            CityVO.builder().code("ALL").name("全国").province("").hot(true).sortOrder(0).build(),
            // 北京
            CityVO.builder().code("BEIJING").name("北京").province("北京").hot(true).sortOrder(1).build(),
            // 上海
            CityVO.builder().code("SHANGHAI").name("上海").province("上海").hot(true).sortOrder(2).build(),
            // 广州
            CityVO.builder().code("GUANGZHOU").name("广州").province("广东").hot(true).sortOrder(3).build(),
            // 深圳
            CityVO.builder().code("SHENZHEN").name("深圳").province("广东").hot(true).sortOrder(4).build(),
            // 杭州
            CityVO.builder().code("HANGZHOU").name("杭州").province("浙江").hot(true).sortOrder(5).build(),
            // 成都
            CityVO.builder().code("CHENGDU").name("成都").province("四川").hot(true).sortOrder(6).build(),
            // 武汉
            CityVO.builder().code("WUHAN").name("武汉").province("湖北").hot(true).sortOrder(7).build(),
            // 西安
            CityVO.builder().code("XIAN").name("西安").province("陕西").hot(true).sortOrder(8).build(),
            // 重庆
            CityVO.builder().code("CHONGQING").name("重庆").province("重庆").hot(true).sortOrder(9).build(),
            // 其他城市
            CityVO.builder().code("TIANJIN").name("天津").province("天津").hot(false).sortOrder(10).build(),
            CityVO.builder().code("NANJING").name("南京").province("江苏").hot(false).sortOrder(11).build(),
            CityVO.builder().code("SUZHOU").name("苏州").province("江苏").hot(false).sortOrder(12).build(),
            CityVO.builder().code("WUXI").name("无锡").province("江苏").hot(false).sortOrder(13).build(),
            CityVO.builder().code("CHANGZHOU").name("常州").province("江苏").hot(false).sortOrder(14).build(),
            CityVO.builder().code("NINGBO").name("宁波").province("浙江").hot(false).sortOrder(15).build(),
            CityVO.builder().code("WENZHOU").name("温州").province("浙江").hot(false).sortOrder(16).build(),
            CityVO.builder().code("ZHENGZHOU").name("郑州").province("河南").hot(false).sortOrder(17).build(),
            CityVO.builder().code("JINAN").name("济南").province("山东").hot(false).sortOrder(18).build(),
            CityVO.builder().code("QINGDAO").name("青岛").province("山东").hot(false).sortOrder(19).build(),
            CityVO.builder().code("SHENYANG").name("沈阳").province("辽宁").hot(false).sortOrder(20).build(),
            CityVO.builder().code("DALIAN").name("大连").province("辽宁").hot(false).sortOrder(21).build(),
            CityVO.builder().code("HARBIN").name("哈尔滨").province("黑龙江").hot(false).sortOrder(22).build(),
            CityVO.builder().code("CHANGCHUN").name("长春").province("吉林").hot(false).sortOrder(23).build(),
            CityVO.builder().code("XIAMEN").name("厦门").province("福建").hot(false).sortOrder(24).build(),
            CityVO.builder().code("FUZHOU").name("福州").province("福建").hot(false).sortOrder(25).build(),
            CityVO.builder().code("NANCHANG").name("南昌").province("江西").hot(false).sortOrder(26).build(),
            CityVO.builder().code("HEFEI").name("合肥").province("安徽").hot(false).sortOrder(27).build(),
            CityVO.builder().code("WUHAN").name("武汉").province("湖北").hot(false).sortOrder(28).build(),
            CityVO.builder().code("CHANGSHA").name("长沙").province("湖南").hot(false).sortOrder(29).build(),
            CityVO.builder().code("GUANGZHOU").name("广州").province("广东").hot(false).sortOrder(30).build(),
            CityVO.builder().code("SHENZHEN").name("深圳").province("广东").hot(false).sortOrder(31).build(),
            CityVO.builder().code("ZHUHai").name("珠海").province("广东").hot(false).sortOrder(32).build(),
            CityVO.builder().code("NANNING").name("南宁").province("广西").hot(false).sortOrder(33).build(),
            CityVO.builder().code("KUNMING").name("昆明").province("云南").hot(false).sortOrder(34).build(),
            CityVO.builder().code("CHENGDU").name("成都").province("四川").hot(false).sortOrder(35).build(),
            CityVO.builder().code("CHONGQING").name("重庆").province("重庆").hot(false).sortOrder(36).build(),
            CityVO.builder().code("XI'AN").name("西安").province("陕西").hot(false).sortOrder(37).build(),
            CityVO.builder().code("LANZHOU").name("兰州").province("甘肃").hot(false).sortOrder(38).build(),
            CityVO.builder().code("XINING").name("西宁").province("青海").hot(false).sortOrder(39).build(),
            CityVO.builder().code("YINCHUAN").name("银川").province("宁夏").hot(false).sortOrder(40).build(),
            CityVO.builder().code("URUMQI").name("乌鲁木齐").province("新疆").hot(false).sortOrder(41).build(),
            CityVO.builder().code("HAIKOU").name("海口").province("海南").hot(false).sortOrder(42).build(),
            CityVO.builder().code("SHIJIAZHUANG").name("石家庄").province("河北").hot(false).sortOrder(43).build(),
            CityVO.builder().code("TANGSHAN").name("唐山").province("河北").hot(false).sortOrder(44).build(),
            CityVO.builder().code("BAODING").name("保定").province("河北").hot(false).sortOrder(45).build(),
            CityVO.builder().code("HUHEHAOTE").name("呼和浩特").province("内蒙古").hot(false).sortOrder(46).build(),
            CityVO.builder().code("NANNING").name("南宁").province("广西").hot(false).sortOrder(47).build(),
            CityVO.builder().code("GUIYANG").name("贵阳").province("贵州").hot(false).sortOrder(48).build(),
            CityVO.builder().code("LASA").name("拉萨").province("西藏").hot(false).sortOrder(49).build()
    );

    @Override
    public List<CityVO> getAllCities() {
        return CITY_DATA.stream()
                .sorted((a, b) -> Integer.compare(a.getSortOrder(), b.getSortOrder()))
                .collect(Collectors.toList());
    }

    @Override
    public List<CityVO> getHotCities() {
        return CITY_DATA.stream()
                .filter(CityVO::getHot)
                .sorted((a, b) -> Integer.compare(a.getSortOrder(), b.getSortOrder()))
                .collect(Collectors.toList());
    }

    @Override
    public List<CityVO> getCitiesByProvince(String province) {
        return CITY_DATA.stream()
                .filter(city -> province.equals(city.getProvince()))
                .sorted((a, b) -> Integer.compare(a.getSortOrder(), b.getSortOrder()))
                .collect(Collectors.toList());
    }
}