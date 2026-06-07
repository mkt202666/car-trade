package com.pancosky.newcartrade.converter;

import com.pancosky.newcartrade.entity.User;
import com.pancosky.newcartrade.vo.UserPublicVO;
import com.pancosky.newcartrade.vo.UserVO;
import org.springframework.stereotype.Component;

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
        vo.setCreditGrade(user.getCreditGrade());
        vo.setCreditScore(user.getCreditScore());
        vo.setDealCount(user.getDealCount());
        vo.setMemberExpireAt(user.getMemberExpireAt());
        vo.setOnSaleCount(user.getOnSaleCount());
        vo.setViewCount(user.getViewCount());
        vo.setViewCountToday(user.getViewCountToday());
        vo.setMessageCount(user.getMessageCount());
        vo.setMessageCountToday(user.getMessageCountToday());
        vo.setFollowerCount(user.getFollowerCount());
        vo.setFollowerCountToday(user.getFollowerCountToday());
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
