package com.pancosky.newcartrade.service.impl;

import com.pancosky.newcartrade.entity.Coupon;
import com.pancosky.newcartrade.entity.UserCoupon;
import com.pancosky.newcartrade.mapper.CouponMapper;
import com.pancosky.newcartrade.mapper.UserCouponMapper;
import com.pancosky.newcartrade.service.CouponService;
import com.pancosky.newcartrade.vo.CouponVO;
import com.pancosky.newcartrade.vo.UserCouponVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponMapper couponMapper;
    private final UserCouponMapper userCouponMapper;

    @Override
    public List<CouponVO> listAvailable() {
        List<Coupon> coupons = couponMapper.selectList(null);
        return coupons.stream().map(this::toCouponVO).collect(Collectors.toList());
    }

    @Override
    public List<UserCouponVO> listMyCoupons() {
        return null;
    }

    private CouponVO toCouponVO(Coupon coupon) {
        CouponVO vo = new CouponVO();
        vo.setId(coupon.getId());
        vo.setName(coupon.getName());
        vo.setType(coupon.getType());
        vo.setValue(coupon.getValue());
        vo.setMinAmount(coupon.getMinAmount());
        vo.setEndAt(coupon.getEndAt());
        return vo;
    }
}
