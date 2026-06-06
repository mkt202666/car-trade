package com.pancosky.newcartrade.service;

import com.pancosky.newcartrade.vo.UserPublicVO;

import java.util.List;

public interface UserFollowService {
    void follow(Long userId);
    void unfollow(Long userId);
    List<UserPublicVO> myFollows();
    boolean checkFollowed(Long userId);
}
