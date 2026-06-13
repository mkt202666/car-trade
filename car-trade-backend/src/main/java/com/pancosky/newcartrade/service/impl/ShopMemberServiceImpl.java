package com.pancosky.newcartrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopMemberServiceImpl implements ShopMemberService {

    private final ShopMemberMapper shopMemberMapper;

    @Override
    public List<ShopMemberVO> listMembers() {
        Long userId = SecurityUtils.getCurrentUserId();
        LambdaQueryWrapper<ShopMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShopMember::getShopUserId, userId);
        List<ShopMember> members = shopMemberMapper.selectList(wrapper);
        return members.stream().map(m -> {
            ShopMemberVO vo = new ShopMemberVO();
            vo.setId(m.getId());
            vo.setShopUserId(m.getShopUserId());
            vo.setMemberUserId(m.getMemberUserId());
            vo.setRole(m.getRole());
            vo.setStatus(m.getStatus());
            vo.setAppliedAt(m.getAppliedAt());
            vo.setApprovedAt(m.getApprovedAt());
            return vo;
        }).collect(Collectors.toList());
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
    public void approve(Long id, boolean approve) {
        ShopMember member = shopMemberMapper.selectById(id);
        if (member == null) throw new BusinessException("Shop member not found");
        member.setStatus(approve ? "APPROVED" : "REJECTED");
        member.setApprovedAt(LocalDateTime.now());
        shopMemberMapper.updateById(member);
    }

    @Override
    @Transactional
    public void remove(Long id) {
        shopMemberMapper.deleteById(id);
    }
}
