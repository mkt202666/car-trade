package com.pancosky.newcartrade.service.impl;

import com.pancosky.newcartrade.entity.ShopMember;
import com.pancosky.newcartrade.exception.BusinessException;
import com.pancosky.newcartrade.mapper.ShopMemberMapper;
import com.pancosky.newcartrade.service.ShopMemberService;
import com.pancosky.newcartrade.util.SecurityUtils;
import com.pancosky.newcartrade.vo.ShopMemberVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopMemberServiceImpl implements ShopMemberService {

    private final ShopMemberMapper shopMemberMapper;

    @Override
    public List<ShopMemberVO> listMembers() {
        return null;
    }

    @Override
    @Transactional
    public void invite(Long userId) {
        Long shopUserId = SecurityUtils.getCurrentUserId();
        ShopMember member = new ShopMember();
        member.setShopUserId(shopUserId);
        member.setMemberUserId(userId);
        member.setStatus("PENDING");
        member.setAppliedAt(LocalDateTime.now());
        shopMemberMapper.insert(member);
    }

    @Override
    @Transactional
    public void approve(Long id) {
        ShopMember member = shopMemberMapper.selectById(id);
        if (member == null) throw new BusinessException("Shop member not found");
        member.setStatus("APPROVED");
        member.setApprovedAt(LocalDateTime.now());
        shopMemberMapper.updateById(member);
    }

    @Override
    @Transactional
    public void remove(Long id) {
        shopMemberMapper.deleteById(id);
    }
}
