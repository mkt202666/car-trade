package com.pancosky.cartradeadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pancosky.cartradeadmin.entity.AppShopMember;
import com.pancosky.cartradeadmin.vo.ShopMemberVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AppShopMemberMapper extends BaseMapper<AppShopMember> {

    @Select("SELECT sm.id, sm.member_user_id, u.nickname, u.real_name, u.phone, u.avatar_url, sm.role, sm.status, sm.created_at AS joined_at " +
            "FROM shop_members sm " +
            "LEFT JOIN users u ON sm.member_user_id = u.id " +
            "WHERE sm.shop_user_id = #{shopUserId} " +
            "ORDER BY sm.created_at ASC")
    List<ShopMemberVO> selectMembersByShopUserId(@Param("shopUserId") Long shopUserId);
}
