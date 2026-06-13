package com.pancosky.cartradeadmin.service;

import com.pancosky.cartradeadmin.common.BusinessException;
import com.pancosky.cartradeadmin.entity.AdminUser;
import com.pancosky.cartradeadmin.mapper.AdminUserMapper;
import com.pancosky.cartradeadmin.dto.LoginDTO;
import com.pancosky.cartradeadmin.dto.PasswordChangeDTO;
import com.pancosky.cartradeadmin.dto.RefreshTokenDTO;
import com.pancosky.cartradeadmin.util.JwtUtil;
import com.pancosky.cartradeadmin.vo.AdminVO;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AdminAuthService {

    private static final String LOGIN_FAIL_KEY_PREFIX = "admin:login:fail:";
    private static final String TOKEN_BLACKLIST_PREFIX = "admin:token:blacklist:";
    private static final int MAX_LOGIN_FAILS = 5;
    private static final long LOCK_MINUTES = 5;

    @Autowired
    private AdminUserMapper adminUserMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    /**
     * 管理员登录
     */
    public Map<String, Object> login(LoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        String lockKey = LOGIN_FAIL_KEY_PREFIX + username;

        // 检查是否锁定
        String failCountStr = stringRedisTemplate.opsForValue().get(lockKey);
        if (failCountStr != null) {
            int failCount = Integer.parseInt(failCountStr);
            if (failCount >= MAX_LOGIN_FAILS) {
                throw new BusinessException(403, "登录失败次数过多，请" + LOCK_MINUTES + "分钟后再试");
            }
        }

        // 查询用户
        AdminUser admin = adminUserMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AdminUser>()
                        .eq(AdminUser::getUsername, username)
        );

        if (admin == null) {
            incrementFailCount(lockKey);
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), admin.getPassword())) {
            incrementFailCount(lockKey);
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 检查状态
        if (!"ACTIVE".equalsIgnoreCase(admin.getStatus())) {
            throw new BusinessException(403, "账号已被禁用，请联系管理员");
        }

        // 清除失败计数
        stringRedisTemplate.delete(lockKey);

        // 生成Token
        String accessToken = jwtUtil.generateToken(admin.getId(), admin.getUsername(), admin.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(admin.getId());

        // 更新最后登录时间
        admin.setLastLoginAt(LocalDateTime.now());
        adminUserMapper.updateById(admin);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("accessToken", accessToken);
        result.put("refreshToken", refreshToken);
        result.put("expiresIn", jwtExpiration);
        result.put("admin", convertToVO(admin));

        return result;
    }

    /**
     * 获取当前管理员信息
     */
    public AdminVO getMe(Long adminId) {
        AdminUser admin = adminUserMapper.selectById(adminId);
        if (admin == null) {
            throw new BusinessException(404, "管理员不存在");
        }
        return convertToVO(admin);
    }

    /**
     * 刷新Token
     */
    public Map<String, Object> refreshToken(RefreshTokenDTO refreshTokenDTO) {
        try {
            Claims claims = jwtUtil.parseToken(refreshTokenDTO.getRefreshToken());
            String type = jwtUtil.getTypeFromClaims(claims);
            if (!"REFRESH".equals(type)) {
                throw new BusinessException(401, "无效的刷新Token");
            }

            Long adminId = jwtUtil.getAdminIdFromClaims(claims);
            AdminUser admin = adminUserMapper.selectById(adminId);
            if (admin == null || !"ACTIVE".equalsIgnoreCase(admin.getStatus())) {
                throw new BusinessException(401, "管理员账号已失效");
            }

            String newAccessToken = jwtUtil.generateToken(admin.getId(), admin.getUsername(), admin.getRole());

            Map<String, Object> result = new HashMap<>();
            result.put("accessToken", newAccessToken);
            result.put("expiresIn", jwtExpiration);

            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(401, "刷新Token失败: " + e.getMessage());
        }
    }

    /**
     * 管理员登出 — 将 token 加入 Redis 黑名单
     */
    public void logout(String token, Long adminId) {
        try {
            Claims claims = jwtUtil.parseToken(token);
            // 计算剩余过期时间（秒）
            Date expiration = claims.getExpiration();
            long remainingSeconds = (expiration.getTime() - System.currentTimeMillis()) / 1000;
            if (remainingSeconds > 0) {
                String jti = claims.getId();
                String blacklistKey = TOKEN_BLACKLIST_PREFIX + jti;
                stringRedisTemplate.opsForValue().set(blacklistKey, String.valueOf(adminId), remainingSeconds, TimeUnit.SECONDS);
                log.info("[Auth] Token blacklisted for admin {}, jti={}, ttl={}s", adminId, jti, remainingSeconds);
            }
        } catch (Exception e) {
            log.warn("[Auth] Failed to blacklist token: {}", e.getMessage());
        }
    }

    /**
     * 检查 token 是否在黑名单中
     */
    public boolean isTokenBlacklisted(String jti) {
        String blacklistKey = TOKEN_BLACKLIST_PREFIX + jti;
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(blacklistKey));
    }

    /**
     * 修改密码
     */
    public void changePassword(Long adminId, PasswordChangeDTO passwordChangeDTO) {
        AdminUser admin = adminUserMapper.selectById(adminId);
        if (admin == null) {
            throw new BusinessException(404, "管理员不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(passwordChangeDTO.getOldPassword(), admin.getPassword())) {
            throw new BusinessException(400, "旧密码错误");
        }

        // 编码新密码
        String encodedNewPassword = passwordEncoder.encode(passwordChangeDTO.getNewPassword());
        admin.setPassword(encodedNewPassword);
        admin.setUpdatedAt(LocalDateTime.now());

        adminUserMapper.updateById(admin);
    }

    /**
     * 增加失败计数
     */
    private void incrementFailCount(String lockKey) {
        Long currentCount = stringRedisTemplate.opsForValue().increment(lockKey);
        // 每次失败都重置过期时间，确保5分钟内有5次失败才会锁定
        if (currentCount != null && currentCount == 1) {
            stringRedisTemplate.expire(lockKey, LOCK_MINUTES, TimeUnit.MINUTES);
        }
    }

    /**
     * 转换为VO
     */
    private AdminVO convertToVO(AdminUser admin) {
        AdminVO vo = new AdminVO();
        BeanUtils.copyProperties(admin, vo);
        return vo;
    }
}
