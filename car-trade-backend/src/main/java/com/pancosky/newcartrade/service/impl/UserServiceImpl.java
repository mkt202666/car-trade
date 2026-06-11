package com.pancosky.newcartrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pancosky.newcartrade.converter.UserConverter;
import com.pancosky.newcartrade.dto.ChangePasswordDTO;
import com.pancosky.newcartrade.dto.LoginDTO;
import com.pancosky.newcartrade.dto.RegisterDTO;
import com.pancosky.newcartrade.entity.User;
import com.pancosky.newcartrade.exception.BusinessException;
import com.pancosky.newcartrade.mapper.UserMapper;
import com.pancosky.newcartrade.service.FileStorageService;
import com.pancosky.newcartrade.service.SmsService;
import com.pancosky.newcartrade.service.UserService;
import com.pancosky.newcartrade.util.JwtUtil;
import com.pancosky.newcartrade.util.PasswordUtil;
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
    private final FileStorageService fileStorageService;
    private final SmsService smsService;

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
            if (StringUtils.hasText(dto.getPassword())) {
                user.setPassword(PasswordUtil.encode(dto.getPassword()));
            }
            user.setCreditScore(600);
            user.setCreditGrade("B");
            user.setStatus("ACTIVE");
            user.setCertificationStatus("NONE");
            // 设置统计字段默认值，避免 null 导致序列化问题
            user.setDealCount(0);
            user.setOnSaleCount(0);
            user.setViewCount(0L);
            user.setViewCountToday(0);
            user.setMessageCount(0L);
            user.setMessageCountToday(0);
            user.setFollowerCount(0);
            user.setFollowerCountToday(0);
            userMapper.insert(user);
        } else {
            // 已注册用户：
            //   - 有密码：必须验证密码
            //   - 无密码：允许免密登录（演示账号 / 测试账号的典型场景）
            if (StringUtils.hasText(user.getPassword())) {
                if (!StringUtils.hasText(dto.getPassword())) {
                    throw new BusinessException("请输入密码");
                }
                if (!PasswordUtil.matches(dto.getPassword(), user.getPassword())) {
                    throw new BusinessException("密码不正确");
                }
            }
            // 确保统计字段非 null
            if (user.getDealCount() == null) user.setDealCount(0);
            if (user.getOnSaleCount() == null) user.setOnSaleCount(0);
            if (user.getViewCount() == null) user.setViewCount(0L);
            if (user.getViewCountToday() == null) user.setViewCountToday(0);
            if (user.getMessageCount() == null) user.setMessageCount(0L);
            if (user.getMessageCountToday() == null) user.setMessageCountToday(0);
            if (user.getFollowerCount() == null) user.setFollowerCount(0);
            if (user.getFollowerCountToday() == null) user.setFollowerCountToday(0);
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
        if (StringUtils.hasText(dto.getPassword())) {
            user.setPassword(PasswordUtil.encode(dto.getPassword()));
        }
        user.setCreditScore(600);
        user.setCreditGrade("B");
        user.setStatus("ACTIVE");
        user.setCertificationStatus("NONE");
        // 设置统计字段默认值，避免 null 导致序列化问题
        user.setDealCount(0);
        user.setOnSaleCount(0);
        user.setViewCount(0L);
        user.setViewCountToday(0);
        user.setMessageCount(0L);
        user.setMessageCountToday(0);
        user.setFollowerCount(0);
        user.setFollowerCountToday(0);
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
    @Transactional
    public UserVO uploadAvatar(org.springframework.web.multipart.MultipartFile file) {
        Long userId = com.pancosky.newcartrade.util.SecurityUtils.getCurrentUserId();
        if (userId == null) throw new BusinessException("请先登录");
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        if (file == null || file.isEmpty()) throw new BusinessException("请选择图片");

        String avatarUrl = fileStorageService.upload(file, "avatars");
        user.setAvatarUrl(avatarUrl);
        userMapper.updateById(user);
        log.info("User {} uploaded avatar: {}", userId, avatarUrl);
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

    @Override
    @Transactional
    public void changePassword(ChangePasswordDTO dto) {
        Long userId = com.pancosky.newcartrade.util.SecurityUtils.getCurrentUserId();
        if (userId == null) throw new BusinessException("请先登录");
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("用户不存在");

        // 如果已设置过密码，验证旧密码
        if (StringUtils.hasText(user.getPassword())) {
            if (!StringUtils.hasText(dto.getOldPassword())) {
                throw new BusinessException("请输入旧密码");
            }
            if (!PasswordUtil.matches(dto.getOldPassword(), user.getPassword())) {
                throw new BusinessException("旧密码不正确");
            }
        }
        if (!StringUtils.hasText(dto.getNewPassword()) || dto.getNewPassword().length() < 6) {
            throw new BusinessException("新密码长度不能少于6位");
        }
        user.setPassword(PasswordUtil.encode(dto.getNewPassword()));
        userMapper.updateById(user);
        log.info("User {} changed password", userId);
    }

    @Override
    @Transactional
    public void updatePhone(String newPhone, String smsCode) {
        Long userId = com.pancosky.newcartrade.util.SecurityUtils.getCurrentUserId();
        if (userId == null) throw new BusinessException("请先登录");
        if (!StringUtils.hasText(newPhone)) throw new BusinessException("请输入新手机号");

        // 检查新手机号是否已被使用
        User existing = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getPhone, newPhone).last("LIMIT 1"));
        if (existing != null && !existing.getId().equals(userId)) {
            throw new BusinessException("该手机号已被其他用户使用");
        }

        // 验证短信验证码
        if (!smsService.verifyCode(newPhone, smsCode)) {
            throw new BusinessException("验证码错误");
        }

        User user = userMapper.selectById(userId);
        user.setPhone(newPhone);
        userMapper.updateById(user);
        log.info("User {} updated phone to {}", userId, newPhone);
    }

    private static String maskPhone4(String phone) {
        if (phone == null || phone.length() < 7) return "0000";
        return phone.substring(phone.length() - 4);
    }
}
