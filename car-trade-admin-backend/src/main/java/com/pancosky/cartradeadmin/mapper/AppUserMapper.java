package com.pancosky.cartradeadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pancosky.cartradeadmin.entity.AppUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface AppUserMapper extends BaseMapper<AppUser> {

    @Select("SELECT DATE(created_at) as date, COUNT(*) as count " +
            "FROM tc_users WHERE created_at >= #{startDate}::timestamp AND deleted_at IS NULL " +
            "GROUP BY DATE(created_at) ORDER BY date")
    List<Map<String, Object>> selectDailyNewUsers(@Param("startDate") String startDate);

    @Select("SELECT u.*, (SELECT COUNT(*) FROM tc_shop_members sm WHERE sm.shop_user_id = u.id) AS memberCount " +
            "FROM tc_users u " +
            "WHERE u.deleted_at IS NULL " +
            "AND (#{keyword} IS NULL OR #{keyword} = '' OR u.shop_name LIKE CONCAT('%',#{keyword},'%')) " +
            "AND (#{status} IS NULL OR #{status} = '' OR u.status = #{status}) " +
            "AND u.shop_name IS NOT NULL AND u.shop_name != '' " +
            "ORDER BY u.created_at DESC " +
            "LIMIT #{offset}, #{size}")
    List<Map<String, Object>> selectShopList(@Param("keyword") String keyword,
                                              @Param("status") String status,
                                              @Param("offset") long offset,
                                              @Param("size") long size);

    @Select("SELECT COUNT(*) FROM tc_users u " +
            "WHERE u.deleted_at IS NULL " +
            "AND (#{keyword} IS NULL OR #{keyword} = '' OR u.shop_name LIKE CONCAT('%',#{keyword},'%')) " +
            "AND (#{status} IS NULL OR #{status} = '' OR u.status = #{status}) " +
            "AND u.shop_name IS NOT NULL AND u.shop_name != ''")
    long selectShopListCount(@Param("keyword") String keyword, @Param("status") String status);
}
