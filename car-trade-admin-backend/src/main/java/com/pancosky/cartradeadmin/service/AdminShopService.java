package com.pancosky.cartradeadmin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pancosky.cartradeadmin.common.BusinessException;
import com.pancosky.cartradeadmin.common.PageResult;
import com.pancosky.cartradeadmin.dto.ShopQueryDTO;
import com.pancosky.cartradeadmin.entity.AppShopMember;
import com.pancosky.cartradeadmin.entity.AppUser;
import com.pancosky.cartradeadmin.mapper.AppShopMemberMapper;
import com.pancosky.cartradeadmin.mapper.AppUserMapper;
import com.pancosky.cartradeadmin.vo.ShopDetailVO;
import com.pancosky.cartradeadmin.vo.ShopMemberVO;
import com.pancosky.cartradeadmin.vo.ShopVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminShopService {

    @Autowired
    private AppUserMapper appUserMapper;

    @Autowired
    private AppShopMemberMapper appShopMemberMapper;

    public PageResult<ShopVO> getShopList(ShopQueryDTO query) {
        LambdaQueryWrapper<AppUser> wrapper = new LambdaQueryWrapper<AppUser>()
                .isNull(AppUser::getDeletedAt)
                .isNotNull(AppUser::getShopName)
                .ne(AppUser::getShopName, "");

        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            wrapper.like(AppUser::getShopName, query.getKeyword());
        }
        if (query.getStatus() != null && !query.getStatus().isEmpty()) {
            wrapper.eq(AppUser::getStatus, query.getStatus());
        }

        wrapper.orderByDesc(AppUser::getCreatedAt);

        Page<AppUser> page = appUserMapper.selectPage(new Page<>(query.getPage(), query.getSize()), wrapper);

        List<ShopVO> voList = page.getRecords().stream().map(user -> {
            ShopVO vo = new ShopVO();
            vo.setId(user.getId());
            vo.setShopName(user.getShopName());
            vo.setRealName(user.getRealName());
            vo.setPhone(maskPhone(user.getPhone()));
            vo.setNickname(user.getNickname());
            vo.setAvatarUrl(user.getAvatarUrl());
            vo.setCreditGrade(user.getCreditGrade());
            vo.setCreditScore(user.getCreditScore());
            vo.setDealCount(user.getDealCount());
            vo.setOnSaleCount(user.getOnSaleCount());
            vo.setStatus(user.getStatus());
            vo.setCertificationStatus(user.getCertificationStatus());
            vo.setCreatedAt(user.getCreatedAt());

            // 统计成员数
            Long memberCount = appShopMemberMapper.selectCount(
                    new LambdaQueryWrapper<AppShopMember>().eq(AppShopMember::getShopUserId, user.getId()));
            vo.setMemberCount(memberCount != null ? memberCount.intValue() : 0);

            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(voList, page.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }

    public List<ShopVO> getShopExportList(ShopQueryDTO query) {
        LambdaQueryWrapper<AppUser> wrapper = new LambdaQueryWrapper<AppUser>()
                .isNull(AppUser::getDeletedAt)
                .isNotNull(AppUser::getShopName)
                .ne(AppUser::getShopName, "");

        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            wrapper.like(AppUser::getShopName, query.getKeyword());
        }
        if (query.getStatus() != null && !query.getStatus().isEmpty()) {
            wrapper.eq(AppUser::getStatus, query.getStatus());
        }

        wrapper.orderByDesc(AppUser::getCreatedAt);

        List<AppUser> users = appUserMapper.selectList(wrapper);

        return users.stream().map(user -> {
            ShopVO vo = new ShopVO();
            vo.setId(user.getId());
            vo.setShopName(user.getShopName());
            vo.setRealName(user.getRealName());
            vo.setPhone(maskPhone(user.getPhone()));
            vo.setNickname(user.getNickname());
            vo.setAvatarUrl(user.getAvatarUrl());
            vo.setCreditGrade(user.getCreditGrade());
            vo.setCreditScore(user.getCreditScore());
            vo.setDealCount(user.getDealCount());
            vo.setOnSaleCount(user.getOnSaleCount());
            vo.setStatus(user.getStatus());
            vo.setCertificationStatus(user.getCertificationStatus());
            vo.setCreatedAt(user.getCreatedAt());

            Long memberCount = appShopMemberMapper.selectCount(
                    new LambdaQueryWrapper<AppShopMember>().eq(AppShopMember::getShopUserId, user.getId()));
            vo.setMemberCount(memberCount != null ? memberCount.intValue() : 0);

            return vo;
        }).collect(Collectors.toList());
    }

    public ShopDetailVO getShopDetail(Long id) {
        AppUser user = appUserMapper.selectById(id);
        if (user == null || user.getDeletedAt() != null) {
            throw new BusinessException(404, "车行不存在");
        }

        ShopDetailVO vo = new ShopDetailVO();
        vo.setId(user.getId());
        vo.setShopName(user.getShopName());
        vo.setShopLogo(user.getShopLogo());
        vo.setShopDescription(user.getShopDescription());
        vo.setRealName(user.getRealName());
        vo.setPhone(maskPhone(user.getPhone()));
        vo.setNickname(user.getNickname());
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setCreditGrade(user.getCreditGrade());
        vo.setCreditScore(user.getCreditScore());
        vo.setDealCount(user.getDealCount());
        vo.setOnSaleCount(user.getOnSaleCount());
        vo.setViewCount(user.getViewCount());
        vo.setFollowerCount(user.getFollowerCount());
        vo.setCertificationStatus(user.getCertificationStatus());
        vo.setStatus(user.getStatus());
        vo.setCreatedAt(user.getCreatedAt());

        // 查询成员列表
        List<ShopMemberVO> members = appShopMemberMapper.selectMembersByShopUserId(id);
        vo.setMembers(members);

        return vo;
    }

    public void updateShopStatus(Long id, String status) {
        AppUser user = appUserMapper.selectById(id);
        if (user == null || user.getDeletedAt() != null) {
            throw new BusinessException(404, "车行不存在");
        }
        user.setStatus(status);
        appUserMapper.updateById(user);
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
}
