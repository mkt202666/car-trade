package com.pancosky.newcartrade.service.impl;

import com.pancosky.newcartrade.converter.UserConverter;
import com.pancosky.newcartrade.dto.LoginDTO;
import com.pancosky.newcartrade.dto.RegisterDTO;
import com.pancosky.newcartrade.entity.User;
import com.pancosky.newcartrade.exception.BusinessException;
import com.pancosky.newcartrade.mapper.UserMapper;
import com.pancosky.newcartrade.service.UserService;
import com.pancosky.newcartrade.util.SecurityUtils;
import com.pancosky.newcartrade.vo.LoginVO;
import com.pancosky.newcartrade.vo.UserPublicVO;
import com.pancosky.newcartrade.vo.UserStatsVO;
import com.pancosky.newcartrade.vo.UserVO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserConverter userConverter;

    @Value("${jwt.secret:defaultSecretKeyForDevelopmentOnly12345678}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400}")
    private Long jwtExpiration;

    @Override
    public LoginVO login(LoginDTO dto) {
        User user = userMapper.selectOne(null);
        if (user == null) throw new BusinessException("User not found");
        String token = generateToken(user.getId());
        LoginVO vo = new LoginVO();
        vo.setAccessToken(token);
        vo.setExpiresIn(jwtExpiration);
        vo.setUser(userConverter.toVO(user));
        return vo;
    }

    @Override
    @Transactional
    public UserVO register(RegisterDTO dto) {
        User user = new User();
        user.setPhone(dto.getPhone());
        user.setNickname(dto.getNickname());
        userMapper.insert(user);
        return userConverter.toVO(user);
    }

    @Override
    public UserVO getCurrentUser() {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("User not found");
        return userConverter.toVO(user);
    }

    @Override
    @Transactional
    public UserVO updateProfile(UserVO vo) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("User not found");
        if (vo.getNickname() != null) user.setNickname(vo.getNickname());
        if (vo.getRealName() != null) user.setRealName(vo.getRealName());
        if (vo.getShopName() != null) user.setShopName(vo.getShopName());
        userMapper.updateById(user);
        return userConverter.toVO(user);
    }

    @Override
    @Transactional
    public UserVO uploadAvatar(MultipartFile file) {
        return null;
    }

    @Override
    public UserStatsVO getStats() {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("User not found");
        UserStatsVO vo = new UserStatsVO();
        vo.setOnSaleCount(user.getOnSaleCount());
        vo.setViewCount(user.getViewCount());
        vo.setViewCountToday(user.getViewCountToday());
        vo.setMessageCount(user.getMessageCount());
        vo.setMessageCountToday(user.getMessageCountToday());
        vo.setFollowerCount(user.getFollowerCount());
        vo.setFollowerCountToday(user.getFollowerCountToday());
        return vo;
    }

    @Override
    @Transactional
    public void certify() {
    }

    @Override
    public UserPublicVO getUserPublicInfo(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) throw new BusinessException("User not found");
        return userConverter.toPublicVO(user);
    }

    private String generateToken(Long userId) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration * 1000))
                .signWith(key)
                .compact();
    }
}
