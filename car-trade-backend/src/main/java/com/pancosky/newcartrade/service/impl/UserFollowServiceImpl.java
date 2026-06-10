package com.pancosky.newcartrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pancosky.newcartrade.entity.User;
import com.pancosky.newcartrade.entity.UserFollow;
import com.pancosky.newcartrade.exception.BusinessException;
import com.pancosky.newcartrade.mapper.UserFollowMapper;
import com.pancosky.newcartrade.mapper.UserMapper;
import com.pancosky.newcartrade.service.UserFollowService;
import com.pancosky.newcartrade.util.SecurityUtils;
import com.pancosky.newcartrade.vo.UserPublicVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserFollowServiceImpl implements UserFollowService {

    private final UserFollowMapper userFollowMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public void follow(Long userId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId.equals(userId)) throw new BusinessException("Cannot follow yourself");

        // 防重复关注
        LambdaQueryWrapper<UserFollow> existWrapper = new LambdaQueryWrapper<>();
        existWrapper.eq(UserFollow::getUserId, currentUserId)
                    .eq(UserFollow::getFollowUserId, userId);
        if (userFollowMapper.selectCount(existWrapper) > 0) {
            throw new BusinessException("Already followed");
        }

        UserFollow follow = new UserFollow();
        follow.setUserId(currentUserId);
        follow.setFollowUserId(userId);
        userFollowMapper.insert(follow);

        // 更新被关注者的粉丝数
        User targetUser = userMapper.selectById(userId);
        if (targetUser != null) {
            targetUser.setFollowerCount(targetUser.getFollowerCount() + 1);
            userMapper.updateById(targetUser);
        }
    }

    @Override
    @Transactional
    public void unfollow(Long userId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        LambdaQueryWrapper<UserFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollow::getUserId, currentUserId)
               .eq(UserFollow::getFollowUserId, userId);
        int deleted = userFollowMapper.delete(wrapper);
        if (deleted == 0) {
            throw new BusinessException("Not followed yet");
        }

        // 更新被关注者的粉丝数
        User targetUser = userMapper.selectById(userId);
        if (targetUser != null && targetUser.getFollowerCount() > 0) {
            targetUser.setFollowerCount(targetUser.getFollowerCount() - 1);
            userMapper.updateById(targetUser);
        }
    }

    @Override
    public List<UserPublicVO> myFollows() {
        Long userId = SecurityUtils.getCurrentUserId();
        LambdaQueryWrapper<UserFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollow::getUserId, userId)
               .orderByDesc(UserFollow::getCreatedAt);
        List<UserFollow> follows = userFollowMapper.selectList(wrapper);
        if (follows.isEmpty()) return Collections.emptyList();

        // 批量查询被关注用户信息
        List<Long> followUserIds = follows.stream()
                .map(UserFollow::getFollowUserId)
                .collect(Collectors.toList());
        Map<Long, User> userMap = userMapper.selectBatchIds(followUserIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        return follows.stream().map(f -> {
            UserPublicVO vo = new UserPublicVO();
            vo.setId(f.getFollowUserId());
            User user = userMap.get(f.getFollowUserId());
            if (user != null) {
                vo.setNickname(user.getNickname());
                vo.setAvatar(user.getAvatarUrl());
                vo.setShopName(user.getShopName());
                vo.setCreditGrade(user.getCreditGrade());
                vo.setDealCount(user.getDealCount());
                vo.setOnSaleCount(user.getOnSaleCount());
                vo.setFollowerCount(user.getFollowerCount());
            }
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public boolean checkFollowed(Long userId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        LambdaQueryWrapper<UserFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollow::getUserId, currentUserId)
               .eq(UserFollow::getFollowUserId, userId);
        return userFollowMapper.selectCount(wrapper) > 0;
    }
}
