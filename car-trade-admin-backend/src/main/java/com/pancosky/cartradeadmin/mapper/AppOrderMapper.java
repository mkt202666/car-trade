package com.pancosky.cartradeadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pancosky.cartradeadmin.entity.AppOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface AppOrderMapper extends BaseMapper<AppOrder> {

    @Select("SELECT DATE(created_at) as date, COUNT(*) as order_count, " +
            "COALESCE(SUM(total_price), 0) as trade_amount " +
            "FROM orders WHERE created_at >= #{startDate}::timestamp " +
            "AND status = 'COMPLETED' " +
            "GROUP BY DATE(created_at) ORDER BY date")
    List<Map<String, Object>> selectDailyTrend(@Param("startDate") String startDate);
}
