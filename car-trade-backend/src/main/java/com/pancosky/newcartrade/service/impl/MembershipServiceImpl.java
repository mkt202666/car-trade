package com.pancosky.newcartrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pancosky.newcartrade.entity.MemberPlan;
import com.pancosky.newcartrade.entity.UserMembership;
import com.pancosky.newcartrade.exception.BusinessException;
import com.pancosky.newcartrade.mapper.MemberPlanMapper;
import com.pancosky.newcartrade.mapper.UserMembershipMapper;
import com.pancosky.newcartrade.service.MembershipService;
import com.pancosky.newcartrade.util.SecurityUtils;
import com.pancosky.newcartrade.vo.MemberPlanVO;
import com.pancosky.newcartrade.vo.UserMembershipVO;
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
public class MembershipServiceImpl implements MembershipService {

    private final MemberPlanMapper memberPlanMapper;
    private final UserMembershipMapper userMembershipMapper;

    @Override
    public List<MemberPlanVO> listPlans() {
        List<MemberPlan> plans = memberPlanMapper.selectList(null);
        return plans.stream().map(this::toPlanVO).collect(Collectors.toList());
    }

    @Override
    public UserMembershipVO getMyMembership() {
        Long userId = SecurityUtils.getCurrentUserId();
        LambdaQueryWrapper<UserMembership> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserMembership::getUserId, userId);
        wrapper.orderByDesc(UserMembership::getCreatedAt);
        wrapper.last("LIMIT 1");
        UserMembership membership = userMembershipMapper.selectOne(wrapper);
        if (membership == null) return null;
        UserMembershipVO vo = new UserMembershipVO();
        vo.setId(membership.getId());
        vo.setPlanId(membership.getPlanId());
        vo.setUserId(membership.getUserId());
        vo.setLevel(membership.getLevel());
        vo.setStartAt(membership.getStartAt());
        vo.setEndAt(membership.getEndAt());
        vo.setStatus(membership.getStatus());
        return vo;
    }

    @Override
    @Transactional
    public void renew(Long planId) {
        Long userId = SecurityUtils.getCurrentUserId();
        MemberPlan plan = memberPlanMapper.selectById(planId);
        if (plan == null) throw new BusinessException("Member plan not found");

        // 查找当前有效的会员记录
        LambdaQueryWrapper<UserMembership> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserMembership::getUserId, userId)
               .eq(UserMembership::getStatus, "ACTIVE")
               .orderByDesc(UserMembership::getEndAt)
               .last("LIMIT 1");
        UserMembership existing = userMembershipMapper.selectOne(wrapper);

        if (existing != null && existing.getEndAt().isAfter(LocalDateTime.now())) {
            // 未过期：在到期时间基础上顺延
            existing.setEndAt(existing.getEndAt().plusDays(plan.getDurationDays()));
            existing.setPlanId(planId);
            existing.setLevel(plan.getLevel());
            userMembershipMapper.updateById(existing);
            log.info("User {} extended membership to {}", userId, existing.getEndAt());
        } else {
            // 已过期或无记录：创建新会员
            UserMembership membership = new UserMembership();
            membership.setUserId(userId);
            membership.setPlanId(planId);
            membership.setLevel(plan.getLevel());
            membership.setStartAt(LocalDateTime.now());
            membership.setEndAt(LocalDateTime.now().plusDays(plan.getDurationDays()));
            membership.setStatus("ACTIVE");
            userMembershipMapper.insert(membership);
            log.info("User {} created new membership plan {}", userId, planId);
        }
    }

    private MemberPlanVO toPlanVO(MemberPlan plan) {
        MemberPlanVO vo = new MemberPlanVO();
        vo.setId(plan.getId());
        vo.setName(plan.getName());
        vo.setLevel(plan.getLevel());
        vo.setPrice(plan.getPrice());
        vo.setDurationDays(plan.getDurationDays());
        vo.setBenefits(plan.getBenefits());
        return vo;
    }
}
