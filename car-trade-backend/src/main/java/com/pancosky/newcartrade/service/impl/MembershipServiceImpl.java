package com.pancosky.newcartrade.service.impl;

import com.pancosky.newcartrade.entity.MemberPlan;
import com.pancosky.newcartrade.entity.UserMembership;
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
        return null;
    }

    @Override
    @Transactional
    public void renew(Long planId) {
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
