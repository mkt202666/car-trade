package com.pancosky.newcartrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pancosky.newcartrade.converter.UserConverter;
import com.pancosky.newcartrade.dto.LoginDTO;
import com.pancosky.newcartrade.dto.RegisterDTO;
import com.pancosky.newcartrade.entity.User;
import com.pancosky.newcartrade.exception.BusinessException;
import com.pancosky.newcartrade.mapper.UserMapper;
import com.pancosky.newcartrade.service.UserService;
import com.pancosky.newcartrade.util.JwtUtil;
import com.pancosky.newcartrade.vo.LoginVO;
import com.pancosky.newcartrade.vo.UserStatsVO;
import com.pancosky.newcartrade.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserConverter userConverter;

    @Value("${jwt.secret:default-secret-key-for-dev-please-override-in-prod}")
    private String jwtSecret;

    @Override
    public LoginVO login(LoginDTO dto) {
        if (dto == null || !StringUtils.hasText(dto.getPhone())) {
            throw new BusinessException("手机号不能为空");
        }
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getPhone, dto.getPhone())
                .last("LIMIT 1"));
        if (user == null) {
            // 允许新用户直接登录（自动注册），简化演示流程
            user = new User();
            user.setPhone(dto.getPhone());
            user.setNickname("用户" + maskPhone4(dto.getPhone()));
            user.setCreditScore(600);
            user.setCreditGrade("B");
            user.setStatus("ACTIVE");
            user.setCertificationStatus("NONE");
            userMapper.insert(user);
        }
        // 注入 secret（仅需一次，启动后不变）
        JwtUtil.setSecret(jwtSecret);
        String token = JwtUtil.generateToken(user.getId());
        LoginVO vo = new LoginVO();
        vo.setAccessToken(token);
        vo.setExpiresIn(86400L);
        vo.setUser(userConverter.toVO(user));
        return vo;
    }

    @Override
    @Transactional
    public UserVO register(RegisterDTO dto) {
        if (dto == null || !StringUtils.hasText(dto.getPhone())) {
            throw new BusinessException("手机号不能为空");
        }
        User exists = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getPhone, dto.getPhone()).last("LIMIT 1"));
        if (exists != null) {
            throw new BusinessException("该手机号已注册");
        }
        User user = new User();
        user.setPhone(dto.getPhone());
        user.setNickname(StringUtils.hasText(dto.getNickname()) ? dto.getNickname() : "用户" + maskPhone4(dto.getPhone()));
        user.setCreditScore(600);
        user.setCreditGrade("B");
        user.setStatus("ACTIVE");
        user.setCertificationStatus("NONE");
        userMapper.insert(user);
        return userConverter.toVO(user);
    }

    @Override
    public UserVO getCurrentUser() {
        Long userId = com.pancosky.newcartrade.util.SecurityUtils.getCurrentUserId();
        if (userId == null) throw new BusinessException("请先登录");
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        return userConverter.toVO(user);
    }

    @Override
    @Transactional
    public UserVO updateProfile(UserVO vo) {
        Long userId = com.pancosky.newcartrade.util.SecurityUtils.getCurrentUserId();
        if (userId == null) throw new BusinessException("请先登录");
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        if (vo.getNickname() != null) user.setNickname(vo.getNickname());
        if (vo.getRealName() != null) user.setRealName(vo.getRealName());
        if (vo.getShopName() != null) user.setShopName(vo.getShopName());
        if (vo.getAvatar() != null) user.setAvatarUrl(vo.getAvatar());
        if (vo.getDescription() != null) user.setDescription(vo.getDescription());
        userMapper.updateById(user);
        return userConverter.toVO(user);
    }

    @Override
    public UserVO uploadAvatar(org.springframework.web.multipart.MultipartFile file) {
        Long userId = com.pancosky.newcartrade.util.SecurityUtils.getCurrentUserId();
        if (userId == null) throw new BusinessException("请先登录");
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        log.info("Upload avatar for user: {}, file: {}", userId, file == null ? null : file.getName());
        return userConverter.toVO(user);
    }

    @Override
    public UserStatsVO getStats() {
        Long userId = com.pancosky.newcartrade.util.SecurityUtils.getCurrentUserId();
        if (userId == null) throw new BusinessException("请先登录");
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        UserStatsVO vo = new UserStatsVO();
        vo.setOnSaleCount(user.getOnSaleCount());
        vo.setViewCount(user.getViewCount());
        vo.setViewCountToday(user.getViewCountToday());
        vo.setMessageCount(user.getMessageCount());
        vo.setMessageCountToday(user.getMessageCountToday());
        vo.setFollowerCount(user.getFollowerCount());
        vo.setFollowerCountToday(user.getFollowerCountToday());
        vo.setDealCount(user.getDealCount());
        vo.setCreditScore(user.getCreditScore());
        vo.setMemberExpireAt(user.getMemberExpireAt());
        vo.setCertificationStatus(user.getCertificationStatus());
        return vo;
    }

    @Override
    @Transactional
    public void certify() {
        Long userId = com.pancosky.newcartrade.util.SecurityUtils.getCurrentUserId();
        if (userId == null) throw new BusinessException("请先登录");
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        user.setCertificationStatus("PENDING");
        userMapper.updateById(user);
        log.info("User {} submitted certification", userId);
    }

    @Override
    public com.pancosky.newcartrade.vo.UserPublicVO getUserPublicInfo(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) throw new BusinessException("用户不存在");
        return userConverter.toPublicVO(user);
    }

    private static String maskPhone4(String phone) {
        if (phone == null || phone.length() < 7) return "0000";
        return phone.substring(phone.length() - 4);
    }
}
