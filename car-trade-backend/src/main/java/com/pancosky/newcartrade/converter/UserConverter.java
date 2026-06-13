package com.pancosky.newcartrade.converter;

import com.pancosky.newcartrade.entity.User;
import com.pancosky.newcartrade.vo.UserPublicVO;
import com.pancosky.newcartrade.vo.UserVO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class UserConverter {

    public UserVO toVO(User user) {
        if (user == null) return null;
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setNickname(user.getNickname());
        vo.setRealName(user.getRealName());
        vo.setPhone(maskPhone(user.getPhone()));
        vo.setAvatar(user.getAvatarUrl());
        vo.setShopName(user.getShopName());
        vo.setShopLogo(user.getShopLogo());
        vo.setShopDescription(user.getShopDescription());
        vo.setCreditGrade(user.getCreditGrade());
        vo.setCreditScore(user.getCreditScore());
        vo.setDealCount(user.getDealCount() == null ? 0 : user.getDealCount());
        vo.setMemberExpireAt(user.getMemberExpireAt());
        vo.setUserRole(user.getUserRole() != null ? user.getUserRole() : "PERSONAL");
        // 保证金账户信息（演示环境下设置默认值，实际应由财务账户表查询）
        vo.setDepositBalance(BigDecimal.ZERO);
        vo.setDepositTotal(BigDecimal.ZERO);
        vo.setOnSaleCount(user.getOnSaleCount() == null ? 0 : user.getOnSaleCount());
        vo.setViewCount(user.getViewCount() == null ? 0L : user.getViewCount());
        vo.setViewCountToday(user.getViewCountToday() == null ? 0 : user.getViewCountToday());
        vo.setMessageCount(user.getMessageCount() == null ? 0L : user.getMessageCount());
        vo.setMessageCountToday(user.getMessageCountToday() == null ? 0 : user.getMessageCountToday());
        vo.setFollowerCount(user.getFollowerCount() == null ? 0 : user.getFollowerCount());
        vo.setFollowerCountToday(user.getFollowerCountToday() == null ? 0 : user.getFollowerCountToday());
        vo.setCertificationStatus(user.getCertificationStatus());
        vo.setDescription(user.getDescription());
        vo.setCreatedAt(user.getCreatedAt());
        return vo;
    }

    public UserPublicVO toPublicVO(User user) {
        if (user == null) return null;
        UserPublicVO vo = new UserPublicVO();
        vo.setId(user.getId());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatarUrl());
        vo.setShopName(user.getShopName());
        vo.setShopLogo(user.getShopLogo());
        vo.setShopDescription(user.getShopDescription());
        vo.setCreditGrade(user.getCreditGrade());
        vo.setDealCount(user.getDealCount());
        vo.setOnSaleCount(user.getOnSaleCount());
        vo.setFollowerCount(user.getFollowerCount());
        vo.setLastOnlineAt(user.getUpdatedAt());
        return vo;
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) return phone;
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }
}
