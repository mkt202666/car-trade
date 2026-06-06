package com.pancosky.newcartrade.service.impl;

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
        return null;
    }

    @Override
    public boolean checkFollowed(Long userId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return false;
    }
}
