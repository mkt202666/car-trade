package com.pancosky.cartradeadmin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pancosky.cartradeadmin.common.BusinessException;
import com.pancosky.cartradeadmin.common.PageResult;
import com.pancosky.cartradeadmin.dto.ShopCreateDTO;
import com.pancosky.cartradeadmin.dto.ShopMemberAddDTO;
import com.pancosky.cartradeadmin.dto.ShopMemberRoleUpdateDTO;
import com.pancosky.cartradeadmin.dto.ShopQueryDTO;
import com.pancosky.cartradeadmin.entity.AppShopMember;
import com.pancosky.cartradeadmin.entity.AppUser;
import com.pancosky.cartradeadmin.mapper.AppShopMemberMapper;
import com.pancosky.cartradeadmin.mapper.AppUserMapper;
import com.pancosky.cartradeadmin.vo.ShopDetailVO;
import com.pancosky.cartradeadmin.vo.ShopMemberVO;
import com.pancosky.cartradeadmin.vo.ShopVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminShopService {

    @Autowired
    private AppUserMapper appUserMapper;

    @Autowired
    private AppShopMemberMapper appShopMemberMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Long createShop(ShopCreateDTO dto) {
        // 检查手机号是否已存在
        Long existCount = appUserMapper.selectCount(
                new LambdaQueryWrapper<AppUser>()
                        .eq(AppUser::getPhone, dto.getPhone())
                        .isNull(AppUser::getDeletedAt));
        if (existCount != null && existCount > 0) {
            throw new BusinessException(400, "该手机号已被注册");
        }

        AppUser user = new AppUser();
        user.setShopName(dto.getShopName());
        user.setShopLogo(dto.getShopLogo());
        user.setShopDescription(dto.getShopDescription());
        user.setPhone(dto.getPhone());
        user.setNickname(dto.getContactName() != null && !dto.getContactName().isEmpty()
                ? dto.getContactName() : dto.getShopName());
        user.setCreditCode(dto.getCreditCode());
        user.setProvince(dto.getProvince());
        user.setCity(dto.getCity());
        user.setAddress(dto.getAddress());
        user.setLicenseUrl(dto.getLicenseUrl());
        user.setIdCardNumber(dto.getIdCardNumber());
        user.setIdCardImageUrl(dto.getIdCardImageUrl());
        user.setStoreImageUrl(dto.getStoreImageUrl());
        user.setUserRole("SHOP");
        user.setCertificationStatus("PENDING");
        user.setStatus("ACTIVE");
        user.setCreditScore(0);
        user.setDealCount(0);
        user.setDepositBalance(0L);
        user.setPassword(passwordEncoder.encode("123456"));

        appUserMapper.insert(user);
        log.info("Created shop: id={}, shopName={}", user.getId(), user.getShopName());
        return user.getId();
    }

    public PageResult<ShopVO> getShopList(ShopQueryDTO query) {
        LambdaQueryWrapper<AppUser> wrapper = buildShopQueryWrapper(query);

        wrapper.orderByDesc(AppUser::getCreatedAt);

        Page<AppUser> page = appUserMapper.selectPage(new Page<>(query.getPage(), query.getSize()), wrapper);

        List<ShopVO> voList = page.getRecords().stream().map(user -> {
            ShopVO vo = populateShopVO(user);

            // 统计成员数
            Long memberCount = appShopMemberMapper.selectCount(
                    new LambdaQueryWrapper<AppShopMember>().eq(AppShopMember::getShopUserId, user.getId()));
            vo.setMemberCount(memberCount != null ? memberCount.intValue() : 0);

            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(voList, page.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }

    public List<ShopVO> getShopExportList(ShopQueryDTO query) {
        LambdaQueryWrapper<AppUser> wrapper = buildShopQueryWrapper(query);

        wrapper.orderByDesc(AppUser::getCreatedAt);

        List<AppUser> users = appUserMapper.selectList(wrapper);

        return users.stream().map(user -> {
            ShopVO vo = populateShopVO(user);

            Long memberCount = appShopMemberMapper.selectCount(
                    new LambdaQueryWrapper<AppShopMember>().eq(AppShopMember::getShopUserId, user.getId()));
            vo.setMemberCount(memberCount != null ? memberCount.intValue() : 0);

            return vo;
        }).collect(Collectors.toList());
    }

    private LambdaQueryWrapper<AppUser> buildShopQueryWrapper(ShopQueryDTO query) {
        LambdaQueryWrapper<AppUser> wrapper = new LambdaQueryWrapper<AppUser>()
                .isNull(AppUser::getDeletedAt)
                .isNotNull(AppUser::getShopName)
                .ne(AppUser::getShopName, "");

        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            wrapper.and(w -> w
                    .like(AppUser::getShopName, query.getKeyword())
                    .or().like(AppUser::getRealName, query.getKeyword())
                    .or().like(AppUser::getPhone, query.getKeyword())
                    .or().like(AppUser::getNickname, query.getKeyword()));
        }
        if (query.getStatus() != null && !query.getStatus().isEmpty()) {
            wrapper.eq(AppUser::getStatus, query.getStatus());
        }
        if (query.getProvince() != null && !query.getProvince().isEmpty()) {
            wrapper.eq(AppUser::getProvince, query.getProvince());
        }

        return wrapper;
    }

    private ShopVO populateShopVO(AppUser user) {
        ShopVO vo = new ShopVO();
        vo.setId(user.getId());
        vo.setShopName(user.getShopName());
        vo.setRealName(user.getRealName());
        vo.setPhone(maskPhone(user.getPhone()));
        vo.setNickname(user.getNickname());
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setCreditGrade(user.getCreditGrade());
        vo.setCreditScore(user.getCreditScore());
        vo.setDealCount(user.getDealCount());
        vo.setOnSaleCount(user.getOnSaleCount());
        vo.setStatus(user.getStatus());
        vo.setCertificationStatus(user.getCertificationStatus());
        vo.setCreatedAt(user.getCreatedAt());
        vo.setProvince(user.getProvince());
        vo.setCity(user.getCity());
        vo.setAddress(user.getAddress());
        vo.setCreditCode(user.getCreditCode());
        vo.setDepositBalance(user.getDepositBalance());
        vo.setLicenseUrl(user.getLicenseUrl());
        return vo;
    }

    public ShopDetailVO getShopDetail(Long id) {
        AppUser user = appUserMapper.selectById(id);
        if (user == null || user.getDeletedAt() != null) {
            throw new BusinessException(404, "车行不存在");
        }

        ShopDetailVO vo = new ShopDetailVO();
        vo.setId(user.getId());
        vo.setShopName(user.getShopName());
        vo.setShopLogo(user.getShopLogo());
        vo.setShopDescription(user.getShopDescription());
        vo.setRealName(user.getRealName());
        vo.setPhone(maskPhone(user.getPhone()));
        vo.setNickname(user.getNickname());
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setCreditGrade(user.getCreditGrade());
        vo.setCreditScore(user.getCreditScore());
        vo.setDealCount(user.getDealCount());
        vo.setOnSaleCount(user.getOnSaleCount());
        vo.setViewCount(user.getViewCount());
        vo.setFollowerCount(user.getFollowerCount());
        vo.setCertificationStatus(user.getCertificationStatus());
        vo.setStatus(user.getStatus());
        vo.setCreatedAt(user.getCreatedAt());
        vo.setProvince(user.getProvince());
        vo.setCity(user.getCity());
        vo.setAddress(user.getAddress());
        vo.setCreditCode(user.getCreditCode());
        vo.setDepositBalance(user.getDepositBalance());
        vo.setLicenseUrl(user.getLicenseUrl());

        // 查询成员列表
        List<ShopMemberVO> members = appShopMemberMapper.selectMembersByShopUserId(id);
        vo.setMembers(members);

        return vo;
    }

    public void updateShopStatus(Long id, String status) {
        AppUser user = appUserMapper.selectById(id);
        if (user == null || user.getDeletedAt() != null) {
            throw new BusinessException(404, "车行不存在");
        }
        user.setStatus(status);
        appUserMapper.updateById(user);
    }

    public List<ShopMemberVO> getShopMembers(Long shopId) {
        AppUser shop = appUserMapper.selectById(shopId);
        if (shop == null || shop.getDeletedAt() != null || shop.getShopName() == null) {
            throw new BusinessException(404, "车行不存在");
        }
        return appShopMemberMapper.selectMembersByShopUserId(shopId);
    }

    public void addShopMember(Long shopId, ShopMemberAddDTO dto) {
        // Validate shop exists
        AppUser shop = appUserMapper.selectById(shopId);
        if (shop == null || shop.getDeletedAt() != null || shop.getShopName() == null) {
            throw new BusinessException(404, "车行不存在");
        }

        // Find user by phone
        AppUser member = appUserMapper.selectOne(
                new LambdaQueryWrapper<AppUser>()
                        .eq(AppUser::getPhone, dto.getPhone())
                        .isNull(AppUser::getDeletedAt)
                        .last("LIMIT 1"));
        if (member == null) {
            throw new BusinessException(404, "未找到该手机号对应的用户");
        }

        // Check if already a member
        Long existCount = appShopMemberMapper.selectCount(
                new LambdaQueryWrapper<AppShopMember>()
                        .eq(AppShopMember::getShopUserId, shopId)
                        .eq(AppShopMember::getMemberUserId, member.getId()));
        if (existCount != null && existCount > 0) {
            throw new BusinessException(400, "该用户已是车行成员");
        }

        AppShopMember shopMember = new AppShopMember();
        shopMember.setShopUserId(shopId);
        shopMember.setMemberUserId(member.getId());
        shopMember.setRole(dto.getRole() != null && !dto.getRole().isEmpty() ? dto.getRole() : "MEMBER");
        shopMember.setStatus("ACTIVE");
        appShopMemberMapper.insert(shopMember);

        // Update nickname if provided
        if (dto.getNickname() != null && !dto.getNickname().isEmpty()) {
            member.setNickname(dto.getNickname());
            appUserMapper.updateById(member);
        }

        log.info("Added member to shop: shopId={}, memberUserId={}, phone={}", shopId, member.getId(), dto.getPhone());
    }

    public void updateMemberRole(Long shopId, Long memberId, ShopMemberRoleUpdateDTO dto) {
        AppShopMember member = appShopMemberMapper.selectOne(
                new LambdaQueryWrapper<AppShopMember>()
                        .eq(AppShopMember::getId, memberId)
                        .eq(AppShopMember::getShopUserId, shopId));
        if (member == null) {
            throw new BusinessException(404, "成员不存在");
        }
        member.setRole(dto.getRole());
        appShopMemberMapper.updateById(member);
        log.info("Updated member role: shopId={}, memberId={}, role={}", shopId, memberId, dto.getRole());
    }

    public void removeShopMember(Long shopId, Long memberId) {
        AppShopMember member = appShopMemberMapper.selectOne(
                new LambdaQueryWrapper<AppShopMember>()
                        .eq(AppShopMember::getId, memberId)
                        .eq(AppShopMember::getShopUserId, shopId));
        if (member == null) {
            throw new BusinessException(404, "成员不存在");
        }
        appShopMemberMapper.deleteById(memberId);
        log.info("Removed member from shop: shopId={}, memberId={}", shopId, memberId);
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
}
