package com.pancosky.newcartrade.service;

import com.pancosky.newcartrade.vo.ShopMemberVO;

import java.util.List;

public interface ShopMemberService {
    List<ShopMemberVO> listMembers();
    void invite(Long userId);
    void approve(Long id);
    void remove(Long id);
}
