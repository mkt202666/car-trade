package com.pancosky.cartradeadmin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pancosky.cartradeadmin.common.BusinessException;
import com.pancosky.cartradeadmin.common.PageResult;
import com.pancosky.cartradeadmin.dto.ShopQueryDTO;
import com.pancosky.cartradeadmin.entity.AppUser;
import com.pancosky.cartradeadmin.event.AdminEvent;
import com.pancosky.cartradeadmin.event.AdminEventPublisher;
import com.pancosky.cartradeadmin.event.MobileNotification;
import com.pancosky.cartradeadmin.entity.AdminUser;
import com.pancosky.cartradeadmin.mapper.AdminUserMapper;
import com.pancosky.cartradeadmin.mapper.AppUserMapper;
import com.pancosky.cartradeadmin.service.AdminNotificationService;
import com.pancosky.cartradeadmin.vo.ShopReviewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminShopReviewService {

    @Autowired
    private AppUserMapper appUserMapper;

    @Autowired
    private AdminUserMapper adminUserMapper;

    @Autowired
    private AdminEventPublisher adminEventPublisher;

    @Autowired
    private AdminNotificationService adminNotificationService;

    public PageResult<ShopReviewVO> getReviewList(ShopQueryDTO query) {
        LambdaQueryWrapper<AppUser> wrapper = new LambdaQueryWrapper<AppUser>()
                .isNull(AppUser::getDeletedAt)
                .isNotNull(AppUser::getShopName)
                .ne(AppUser::getShopName, "");

        // Filter by status: only apply filter when a specific status is given.
        // "ALL", null, or empty → return all statuses.
        String status = query.getStatus();
        if (status != null && !status.isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            wrapper.eq(AppUser::getCertificationStatus, status.toUpperCase());
        }

        // Keyword search
        String keyword = query.getKeyword();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w
                    .like(AppUser::getShopName, keyword)
                    .or().like(AppUser::getRealName, keyword)
                    .or().like(AppUser::getPhone, keyword));
        }

        wrapper.orderByAsc(AppUser::getCreatedAt);

        Page<AppUser> page = appUserMapper.selectPage(new Page<>(query.getPage(), query.getSize()), wrapper);

        List<ShopReviewVO> voList = page.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        return new PageResult<>(voList, page.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }

    public void approve(Long userId) {
        AppUser user = appUserMapper.selectById(userId);
        if (user == null || user.getDeletedAt() != null) {
            throw new BusinessException(404, "用户不存在");
        }

        user.setCertificationStatus("CERTIFIED");
        user.setReviewedAt(LocalDateTime.now());
        user.setRejectReason(null); // clear any previous rejection reason
        appUserMapper.updateById(user);

        // 发布 Redis 事件
        Map<String, Object> payload = new HashMap<>();
        payload.put("event", "user:certification");
        payload.put("userId", userId);
        payload.put("status", "CERTIFIED");
        adminEventPublisher.publish(AdminEvent.EventType.SHOP_AUDIT_APPROVED, null, null,
                "user", String.valueOf(userId), "审核通过");

        // 发送移动端通知
        adminNotificationService.notify(
                MobileNotification.NotifyType.SHOP_AUDIT_RESULT, userId,
                "车行认证通过", "恭喜！您的车行认证已通过审核，可以正常经营。",
                "shop", String.valueOf(userId));
    }

    public void reject(Long userId, String reason) {
        AppUser user = appUserMapper.selectById(userId);
        if (user == null || user.getDeletedAt() != null) {
            throw new BusinessException(404, "用户不存在");
        }

        user.setCertificationStatus("REJECTED");
        user.setRejectReason(reason);
        user.setReviewedAt(LocalDateTime.now());
        appUserMapper.updateById(user);

        // 发布 Redis 事件
        Map<String, Object> payload = new HashMap<>();
        payload.put("event", "user:certification");
        payload.put("userId", userId);
        payload.put("status", "REJECTED");
        payload.put("reason", reason);
        adminEventPublisher.publish(AdminEvent.EventType.SHOP_AUDIT_REJECTED, null, null,
                "user", String.valueOf(userId), "审核拒绝: " + reason);

        // 发送移动端通知
        adminNotificationService.notify(
                MobileNotification.NotifyType.SHOP_AUDIT_RESULT, userId,
                "车行认证未通过", "您的车行认证审核未通过，原因：" + reason + "。请修改后重新提交。",
                "shop", String.valueOf(userId));
    }

    @Transactional
    public void batchApprove(List<Long> userIds) {
        LambdaUpdateWrapper<AppUser> updateWrapper = new LambdaUpdateWrapper<AppUser>()
                .in(AppUser::getId, userIds)
                .isNull(AppUser::getDeletedAt)
                .set(AppUser::getCertificationStatus, "CERTIFIED");
        appUserMapper.update(null, updateWrapper);

        // 批量发布 Redis 事件
        for (Long uid : userIds) {
            adminEventPublisher.publish(AdminEvent.EventType.SHOP_AUDIT_APPROVED, null, null,
                    "user", String.valueOf(uid), "批量审核通过");
            // 发送移动端通知
            adminNotificationService.notify(
                    MobileNotification.NotifyType.SHOP_AUDIT_RESULT, uid,
                    "车行认证通过", "恭喜！您的车行认证已通过批量审核。",
                    "shop", String.valueOf(uid));
        }
    }

    public long getPendingCount() {
        return appUserMapper.selectCount(
                new LambdaQueryWrapper<AppUser>()
                        .isNull(AppUser::getDeletedAt)
                        .isNotNull(AppUser::getShopName)
                        .ne(AppUser::getShopName, "")
                        .eq(AppUser::getCertificationStatus, "PENDING"));
    }

    private ShopReviewVO convertToVO(AppUser user) {
        ShopReviewVO vo = new ShopReviewVO();
        vo.setId(user.getId());
        vo.setShopName(user.getShopName());
        vo.setRealName(user.getRealName());
        vo.setPhone(user.getPhone()); // admin sees full phone, no masking
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setCreatedAt(user.getCreatedAt());
        vo.setCertificationStatus(user.getCertificationStatus());

        // Aliased fields for frontend compatibility
        vo.setApplicantName(user.getRealName());
        vo.setApplicantPhone(user.getPhone());
        vo.setStatus(user.getCertificationStatus());

        // New audit-related fields
        vo.setBusinessLicense(user.getBusinessLicense());
        vo.setIdCardFront(user.getIdCardFrontUrl());
        vo.setIdCardBack(user.getIdCardBackUrl());
        vo.setLicenseImage(user.getBusinessLicenseUrl());
        vo.setIdCardNumber(user.getIdCardNumber());
        vo.setRejectReason(user.getRejectReason());
        vo.setReviewerId(user.getReviewerId());
        vo.setReviewedAt(user.getReviewedAt());

        // Look up reviewer name from admin_users
        if (user.getReviewerId() != null) {
            AdminUser reviewer = adminUserMapper.selectById(user.getReviewerId());
            if (reviewer != null) {
                vo.setReviewerName(reviewer.getNickname());
            }
        }

        return vo;
    }
}
