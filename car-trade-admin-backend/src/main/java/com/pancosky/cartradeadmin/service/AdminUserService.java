package com.pancosky.cartradeadmin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pancosky.cartradeadmin.common.BusinessException;
import com.pancosky.cartradeadmin.common.PageResult;
import com.pancosky.cartradeadmin.dto.UserCreateDTO;
import com.pancosky.cartradeadmin.dto.UserProfileUpdateDTO;
import com.pancosky.cartradeadmin.dto.UserQueryDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.pancosky.cartradeadmin.entity.AppDepositAccount;
import com.pancosky.cartradeadmin.entity.AppUser;
import com.pancosky.cartradeadmin.event.MobileNotification;
import com.pancosky.cartradeadmin.mapper.AdminUserMapper;
import com.pancosky.cartradeadmin.mapper.AppDepositAccountMapper;
import com.pancosky.cartradeadmin.mapper.AppUserMapper;
import com.pancosky.cartradeadmin.vo.AdminUserDetailVO;
import com.pancosky.cartradeadmin.vo.AdminUserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminUserService {

    @Autowired
    private AppUserMapper appUserMapper;

    @Autowired
    private AdminUserMapper adminUserMapper;

    @Autowired
    private AppDepositAccountMapper appDepositAccountMapper;

    @Autowired
    private AdminNotificationService adminNotificationService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Long createUser(UserCreateDTO dto) {
        // 检查手机号是否已注册
        AppUser existing = appUserMapper.selectOne(
                new LambdaQueryWrapper<AppUser>().eq(AppUser::getPhone, dto.getPhone()));
        if (existing != null) {
            throw new BusinessException("该手机号已注册");
        }

        AppUser user = new AppUser();
        user.setPhone(dto.getPhone());
        user.setPassword(passwordEncoder.encode(
                dto.getPassword() != null ? dto.getPassword() : "123456"));
        user.setNickname(dto.getNickname() != null
                ? dto.getNickname()
                : "用户" + dto.getPhone().substring(dto.getPhone().length() - 4));
        user.setRealName(dto.getRealName());
        user.setUserRole(dto.getUserRole() != null ? dto.getUserRole() : "PERSONAL");
        user.setStatus("ACTIVE");
        user.setCertificationStatus("NONE");
        user.setCreditScore(0);
        user.setDealCount(0);

        if ("SHOP".equals(user.getUserRole()) && dto.getShopName() != null) {
            user.setShopName(dto.getShopName());
        }

        appUserMapper.insert(user);
        log.info("管理员创建用户成功, userId={}, phone={}", user.getId(), dto.getPhone());
        return user.getId();
    }

    public PageResult<AdminUserVO> listUsers(UserQueryDTO query) {
        String userType = query.getUserType();
        
        // 如果查询系统管理员，需要查询 admin_users 表
        if ("ADMIN_USER".equals(userType)) {
            return listAdminUsers(query);
        }
        
        // 默认查询移动端用户（users 表）
        LambdaQueryWrapper<AppUser> wrapper = new LambdaQueryWrapper<AppUser>();
        
        // 根据用户类型添加过滤条件（基于 user_role 字段）
        if ("SHOP_USER".equals(userType)) {
            // 车行用户：user_role = 'SHOP'
            wrapper.eq(AppUser::getUserRole, "SHOP");
        } else if ("PERSONAL_USER".equals(userType)) {
            // 个人用户：user_role = 'PERSONAL'（兼容历史数据中 user_role 为空的情况）
            wrapper.and(w -> w.eq(AppUser::getUserRole, "PERSONAL")
                            .or().isNull(AppUser::getUserRole)
                            .or().eq(AppUser::getUserRole, ""));
        } else if ("DELETED".equals(userType)) {
            // 已注销用户：deleted_at 非空
            wrapper.isNotNull(AppUser::getDeletedAt);
        } else {
            // ALL 或其他：只排除未删除的用户（除非明确查询 DELETED）
            wrapper.isNull(AppUser::getDeletedAt);
        }

        // 关键字搜索
        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            String kw = query.getKeyword();
            wrapper.and(w -> w.like(AppUser::getNickname, kw)
                    .or().like(AppUser::getRealName, kw)
                    .or().like(AppUser::getPhone, kw)
                    .or().like(AppUser::getShopName, kw));
        }
        
        // 状态过滤
        if (query.getStatus() != null && !query.getStatus().isEmpty()) {
            wrapper.eq(AppUser::getStatus, query.getStatus());
        }
        
        // 认证状态过滤
        if (query.getCertificationStatus() != null && !query.getCertificationStatus().isEmpty()) {
            wrapper.eq(AppUser::getCertificationStatus, query.getCertificationStatus());
        }

        wrapper.orderByDesc(AppUser::getCreatedAt);

        Page<AppUser> page = appUserMapper.selectPage(new Page<>(query.getPage(), query.getSize()), wrapper);

        List<AdminUserVO> voList = page.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        return new PageResult<>(voList, page.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }
    
    /**
     * 查询系统管理员列表（来自 admin_users 表）
     */
    private PageResult<AdminUserVO> listAdminUsers(UserQueryDTO query) {
        LambdaQueryWrapper<com.pancosky.cartradeadmin.entity.AdminUser> wrapper = 
            new LambdaQueryWrapper<>();
        
        // 关键字搜索（用户名/昵称/邮箱）
        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            String kw = query.getKeyword();
            wrapper.and(w -> w.like(com.pancosky.cartradeadmin.entity.AdminUser::getUsername, kw)
                    .or().like(com.pancosky.cartradeadmin.entity.AdminUser::getNickname, kw)
                    .or().like(com.pancosky.cartradeadmin.entity.AdminUser::getEmail, kw));
        }
        
        // 状态过滤
        if (query.getStatus() != null && !query.getStatus().isEmpty()) {
            wrapper.eq(com.pancosky.cartradeadmin.entity.AdminUser::getStatus, query.getStatus());
        }

        wrapper.orderByDesc(com.pancosky.cartradeadmin.entity.AdminUser::getCreatedAt);

        Page<com.pancosky.cartradeadmin.entity.AdminUser> page = adminUserMapper.selectPage(
                new Page<>(query.getPage(), query.getSize()), wrapper);

        List<AdminUserVO> voList = page.getRecords().stream().map(admin -> {
            AdminUserVO vo = new AdminUserVO();
            vo.setId(admin.getId());
            vo.setNickname(admin.getNickname() != null ? admin.getNickname() : admin.getUsername());
            vo.setRealName(null);  // 管理员无真实姓名
            vo.setPhone(null);     // 管理员无手机号
            vo.setAvatarUrl(null);
            vo.setShopName(null);  // 管理员无车行
            vo.setCreditGrade(null);
            vo.setCreditScore(null);
            vo.setCertificationStatus(null);  // 管理员无认证状态
            vo.setUserRole("ADMIN");           // 系统管理员角色
            vo.setStatus(admin.getStatus());
            vo.setDealCount(0);      // 管理员无交易
            vo.setOnSaleCount(0);    // 管理员无在售车源
            vo.setCreatedAt(admin.getCreatedAt());
            return vo;
        }).collect(Collectors.toList());
        
        return new PageResult<>(voList, page.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }

    public AdminUserDetailVO getUserDetail(Long id) {
        AppUser user = appUserMapper.selectById(id);
        if (user == null || user.getDeletedAt() != null) {
            throw new BusinessException(404, "用户不存在");
        }

        AdminUserDetailVO vo = new AdminUserDetailVO();
        vo.setId(user.getId());
        vo.setNickname(user.getNickname());
        vo.setRealName(user.getRealName());
        vo.setPhone(maskPhone(user.getPhone()));
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setShopName(user.getShopName());
        vo.setCreditGrade(user.getCreditGrade());
        vo.setCreditScore(user.getCreditScore());
        vo.setCertificationStatus(user.getCertificationStatus());
        vo.setStatus(user.getStatus());
        vo.setDealCount(user.getDealCount());
        vo.setOnSaleCount(user.getOnSaleCount());
        vo.setCreatedAt(user.getCreatedAt());
        vo.setViewCount(user.getViewCount());
        vo.setFollowerCount(user.getFollowerCount());
        vo.setUserRole(user.getUserRole() != null ? user.getUserRole() : "PERSONAL");
        vo.setShopDescription(user.getShopDescription());
        vo.setMemberExpireAt(user.getMemberExpireAt());
        vo.setNotificationSettings(user.getNotificationSettings());

        // 查询保证金余额
        AppDepositAccount account = appDepositAccountMapper.selectOne(
                new LambdaQueryWrapper<AppDepositAccount>().eq(AppDepositAccount::getUserId, id));
        vo.setDepositBalance(account != null ? account.getBalance() : BigDecimal.ZERO);

        return vo;
    }

    public void updateUserStatus(Long id, String status) {
        AppUser user = appUserMapper.selectById(id);
        if (user == null || user.getDeletedAt() != null) {
            throw new BusinessException(404, "用户不存在");
        }
        user.setStatus(status);
        appUserMapper.updateById(user);

        // 发送移动端通知（仅冻结时通知）
        if ("FROZEN".equals(status)) {
            adminNotificationService.notify(
                    MobileNotification.NotifyType.USER_STATUS_CHANGED, id,
                    "账号状态变更", "您的账号已被管理员冻结，如有疑问请联系客服。",
                    "user", String.valueOf(id));
        }
    }

    public Map<String, Object> getUserStatistics(Long id) {
        AppUser user = appUserMapper.selectById(id);
        if (user == null || user.getDeletedAt() != null) {
            throw new BusinessException(404, "用户不存在");
        }

        AppDepositAccount account = appDepositAccountMapper.selectOne(
                new LambdaQueryWrapper<AppDepositAccount>().eq(AppDepositAccount::getUserId, id));

        Map<String, Object> stats = new HashMap<>();
        stats.put("onSaleCount", user.getOnSaleCount());
        stats.put("dealCount", user.getDealCount());
        stats.put("viewCount", user.getViewCount());
        stats.put("followerCount", user.getFollowerCount() != null ? user.getFollowerCount() : 0);
        stats.put("depositBalance", account != null ? account.getBalance() : BigDecimal.ZERO);

        return stats;
    }

    public void updateUserProfile(Long id, UserProfileUpdateDTO dto) {
        AppUser user = appUserMapper.selectById(id);
        if (user == null || user.getDeletedAt() != null) {
            throw new BusinessException(404, "用户不存在");
        }

        if (dto.getNickname() != null) {
            user.setNickname(dto.getNickname());
        }
        if (dto.getRealName() != null) {
            user.setRealName(dto.getRealName());
        }
        if (dto.getAvatarUrl() != null) {
            user.setAvatarUrl(dto.getAvatarUrl());
        }
        if (dto.getShopName() != null) {
            user.setShopName(dto.getShopName());
        }
        if (dto.getShopLogo() != null) {
            user.setShopLogo(dto.getShopLogo());
        }
        if (dto.getShopDescription() != null) {
            user.setShopDescription(dto.getShopDescription());
        }
        if (dto.getCreditCode() != null) {
            user.setCreditCode(dto.getCreditCode());
        }
        if (dto.getProvince() != null) {
            user.setProvince(dto.getProvince());
        }
        if (dto.getCity() != null) {
            user.setCity(dto.getCity());
        }
        if (dto.getIdCardNumber() != null) {
            user.setIdCardNumber(dto.getIdCardNumber());
        }
        if (dto.getBusinessLicenseUrl() != null) {
            user.setBusinessLicenseUrl(dto.getBusinessLicenseUrl());
        }
        if (dto.getIdCardFrontUrl() != null) {
            user.setIdCardFrontUrl(dto.getIdCardFrontUrl());
        }
        if (dto.getIdCardBackUrl() != null) {
            user.setIdCardBackUrl(dto.getIdCardBackUrl());
        }

        appUserMapper.updateById(user);
        log.info("管理员更新用户资料成功, userId={}", id);
    }

    private AdminUserVO convertToVO(AppUser user) {
        AdminUserVO vo = new AdminUserVO();
        vo.setId(user.getId());
        vo.setNickname(user.getNickname());
        vo.setRealName(user.getRealName());
        vo.setPhone(maskPhone(user.getPhone()));
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setShopName(user.getShopName());
        vo.setCreditGrade(user.getCreditGrade());
        vo.setCreditScore(user.getCreditScore());
        vo.setCertificationStatus(user.getCertificationStatus());
        vo.setUserRole(user.getUserRole() != null ? user.getUserRole() : "PERSONAL");
        vo.setStatus(user.getStatus());
        vo.setDealCount(user.getDealCount());
        vo.setOnSaleCount(user.getOnSaleCount());
        vo.setCreatedAt(user.getCreatedAt());
        vo.setCreditCode(user.getCreditCode());
        vo.setProvince(user.getProvince());
        vo.setCity(user.getCity());
        vo.setIdCardNumber(user.getIdCardNumber());
        vo.setBusinessLicenseUrl(user.getBusinessLicenseUrl());
        vo.setIdCardFrontUrl(user.getIdCardFrontUrl());
        vo.setIdCardBackUrl(user.getIdCardBackUrl());
        
        // M1 新增字段
        vo.setMemberExpireAt(user.getMemberExpireAt());
        vo.setLoginFailCount(user.getLoginFailCount());
        vo.setLockedUntil(user.getLockedUntil());
        vo.setNotificationSettings(user.getNotificationSettings());
        
        return vo;
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
}
