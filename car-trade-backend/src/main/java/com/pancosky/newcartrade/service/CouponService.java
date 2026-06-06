package com.pancosky.newcartrade.service;

import com.pancosky.newcartrade.vo.CouponVO;
import com.pancosky.newcartrade.vo.UserCouponVO;

import java.util.List;

public interface CouponService {
    List<CouponVO> listAvailable();
    List<UserCouponVO> listMyCoupons();
}
