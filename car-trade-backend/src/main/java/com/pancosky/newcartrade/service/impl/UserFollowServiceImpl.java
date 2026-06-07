package com.pancosky.newcartrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pancosky.newcartrade.entity.UserFollow;
import com.pancosky.newcartrade.exception.BusinessException;
import com.pancosky.newcartrade.mapper.UserFollowMapper;
import com.pancosky.newcartrade.service.UserFollowService;
import com.pancosky.newcartrade.util.SecurityUtils;
import com.pancosky.newcartrade.vo.UserPublicVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserFollowServiceImpl implements UserFollowService {

    private final UserFollowMapper userFollowMapper;

    @Override
    @Transactional
    public void follow(Long userId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId.equals(userId)) throw new BusinessException("Cannot follow yourself");
        UserFollow follow = new UserFollow();
        follow.setUserId(currentUserId);
        follow.setFollowUserId(userId);
        userFollowMapper.insert(follow);
    }

    @Override
    @Transactional
    public void unfollow(Long userId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        userFollowMapper.delete(null);
    }

    @Override
    public List<UserPublicVO> myFollows() {
        Long userId = SecurityUtils.getCurrentUserId();
        LambdaQueryWrapper<UserFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollow::getUserId, userId);
        List<UserFollow> follows = userFollowMapper.selectList(wrapper);
        return follows.stream().map(f -> {
            UserPublicVO vo = new UserPublicVO();
            vo.setId(f.getFollowUserId());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public boolean checkFollowed(Long userId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return false;
    }
}
