package com.pancosky.newcartrade.service;

import com.pancosky.newcartrade.vo.MemberPlanVO;
import com.pancosky.newcartrade.vo.UserMembershipVO;

import java.util.List;

public interface MembershipService {
    List<MemberPlanVO> listPlans();
    UserMembershipVO getMyMembership();
    void renew(Long planId);
}
