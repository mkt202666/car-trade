package com.pancosky.newcartrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pancosky.newcartrade.entity.Coupon;
import com.pancosky.newcartrade.entity.UserCoupon;
import com.pancosky.newcartrade.exception.BusinessException;
import com.pancosky.newcartrade.mapper.CouponMapper;
import com.pancosky.newcartrade.mapper.UserCouponMapper;
import com.pancosky.newcartrade.service.CouponService;
import com.pancosky.newcartrade.util.SecurityUtils;
import com.pancosky.newcartrade.vo.CouponVO;
import com.pancosky.newcartrade.vo.UserCouponVO;
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
        Long userId = SecurityUtils.getCurrentUserId();
        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCoupon::getUserId, userId);
        List<UserCoupon> userCoupons = userCouponMapper.selectList(wrapper);
        return userCoupons.stream().map(uc -> {
            UserCouponVO vo = new UserCouponVO();
            vo.setId(uc.getId());
            vo.setCouponId(uc.getCouponId());
            vo.setUserId(uc.getUserId());
            vo.setStatus(uc.getStatus());
            vo.setReceivedAt(uc.getReceivedAt());
            vo.setUsedAt(uc.getUsedAt());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void claim(Long couponId) {
        Long userId = SecurityUtils.getCurrentUserId();
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) throw new BusinessException("优惠券不存在");
        if (!"ACTIVE".equals(coupon.getStatus())) throw new BusinessException("优惠券已下架");
        if (coupon.getEndAt() != null && coupon.getEndAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("优惠券已过期");
        }
        if (coupon.getRemainCount() != null && coupon.getRemainCount() <= 0) {
            throw new BusinessException("优惠券已领完");
        }

        // 检查是否已领取
        LambdaQueryWrapper<UserCoupon> existWrapper = new LambdaQueryWrapper<>();
        existWrapper.eq(UserCoupon::getUserId, userId).eq(UserCoupon::getCouponId, couponId);
        if (userCouponMapper.selectCount(existWrapper) > 0) {
            throw new BusinessException("已领取过该优惠券");
        }

        // 领取
        UserCoupon uc = new UserCoupon();
        uc.setUserId(userId);
        uc.setCouponId(couponId);
        uc.setStatus("UNUSED");
        uc.setReceivedAt(LocalDateTime.now());
        userCouponMapper.insert(uc);

        // 扣减库存
        coupon.setRemainCount(coupon.getRemainCount() - 1);
        couponMapper.updateById(coupon);
    }

    @Override
    @Transactional
    public void use(Long couponId, String orderId) {
        Long userId = SecurityUtils.getCurrentUserId();
        UserCoupon uc = userCouponMapper.selectOne(new LambdaQueryWrapper<UserCoupon>()
                .eq(UserCoupon::getUserId, userId)
                .eq(UserCoupon::getCouponId, couponId)
                .eq(UserCoupon::getStatus, "UNUSED")
                .last("LIMIT 1"));
        if (uc == null) throw new BusinessException("没有可用的优惠券");
        uc.setStatus("USED");
        uc.setUsedAt(LocalDateTime.now());
        uc.setOrderId(orderId);
        userCouponMapper.updateById(uc);
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
