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
import com.pancosky.newcartrade.service.cache.TokenBlacklistService;
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
    private final TokenBlacklistService tokenBlacklistService;

    /**
     * Demo 模式开关：
     *   - true  ：未注册手机号可"静默注册"并登录，仅用于本地开发演示
     *   - false （生产默认）：未注册手机号登录直接 401，必须先 /users/register
     */
    @Value("${app.demo-mode:false}")
    private boolean demoMode;

    /**
     * 登录锁定总开关：
     *   - false：完全不检查也不写入锁定字段（本地/测试推荐）
     *   - true：启用 max-login-fails + lock-minutes 的锁定逻辑（生产默认）
     */
    @Value("${security.login-lock.enabled:true}")
    private boolean loginLockEnabled;

    /**
     * 连续密码错误次数阈值：达到后将账户锁定 lock-minutes 分钟。
     * 仅当 login-lock.enabled=true 时有效。
     */
    @Value("${security.login-lock.max-fails:5}")
    private int maxLoginFails;

    /**
     * 锁定时长（分钟）。
     * 仅当 login-lock.enabled=true 时有效。
     */
    @Value("${security.login-lock.lock-minutes:5}")
    private int lockMinutes;

    @Value("${jwt.secret:}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400}")
    private long accessExpiration;

    @Value("${jwt.refresh-expiration:604800}")
    private long refreshExpiration;

    /**
     * 登录
     *
     * 安全要点（防账号枚举 + 暴力破解）：
     *   1. 不区分"用户不存在"与"密码错误"：对外统一返回 "账号或密码错误，请核对后重试"
     *   2. 服务端日志保留具体原因（脱敏手机号 + reason code），便于运维审计
     *   3. 登录失败计数：超过阈值后延迟响应（限速）
     *   4. 锁定字段：用户表中 loginFailCount / lockedUntil
     */
    @Override
    public LoginVO login(LoginDTO dto) {
        if (dto == null || !StringUtils.hasText(dto.getPhone())) {
            throw new BusinessException("手机号不能为空");
        }

        // 1. 限速：按手机号 + IP 限流（这里简化为按手机号，本项目未做 IP 维度限流）
        //    真实生产应该用 Redis INCR + 滑动窗口
        //    这里仅做"5 次失败后 5 分钟锁定"的基础版
        // 2. 用户名/IP 不一致返回时间一致（防时序侧信道），可通过 dummy hash 达成
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getPhone, dto.getPhone())
                .last("LIMIT 1"));

        // ★ Demo 模式：用户不存在时自动注册（仅本地开发/测试环境开启）
        if (user == null && demoMode) {
            log.info("[Login] demo-mode: auto-registering user phoneSuffix={}",
                    maskPhoneSuffix(dto.getPhone()));
            User newUser = new User();
            newUser.setPhone(dto.getPhone());
            newUser.setNickname("演示用户" + maskPhoneSuffix(dto.getPhone()));
            if (StringUtils.hasText(dto.getPassword())) {
                newUser.setPassword(PasswordUtil.encode(dto.getPassword()));
            }
            newUser.setCreditScore(800);
            newUser.setCreditGrade("A");
            newUser.setDealCount(0);
            newUser.setOnSaleCount(0);
            newUser.setViewCount(0L);
            newUser.setViewCountToday(0);
            newUser.setMessageCount(0L);
            newUser.setMessageCountToday(0);
            newUser.setFollowerCount(0);
            newUser.setFollowerCountToday(0);
            newUser.setStatus("ACTIVE");
            newUser.setUserRole("PERSONAL");  // 默认个人用户
            newUser.setCertificationStatus("NONE");
            userMapper.insert(newUser);
            user = newUser;
        }

        // ★ 安全 1：账户不存在时也跑一次 BCrypt 校验，保持响应时延一致
        if (user == null) {
            PasswordUtil.matches(dto.getPassword() == null ? "" : dto.getPassword(),
                    "$2a$10$abcdefghijklmnopqrstuv"); // dummy hash
            log.warn("[Login] failed reason=USER_NOT_FOUND phoneSuffix={}",
                    maskPhoneSuffix(dto.getPhone()));
            throw new BusinessException(401, LOGIN_FAIL_MSG);
        }

        // ★ 安全 2：账户存在但被锁定（仅当锁定机制启用时检查）
        if (loginLockEnabled && user.getLockedUntil() != null
                && user.getLockedUntil().isAfter(LocalDateTime.now())) {
            log.warn("[Login] rejected reason=ACCOUNT_LOCKED phoneSuffix={} until={}",
                    maskPhoneSuffix(dto.getPhone()), user.getLockedUntil());
            throw new BusinessException(423, "账户已被锁定，请稍后再试");
        }

        if (StringUtils.hasText(user.getPassword())) {
            if (!StringUtils.hasText(dto.getPassword())) {
                log.warn("[Login] failed reason=PASSWORD_REQUIRED phoneSuffix={}",
                        maskPhoneSuffix(dto.getPhone()));
                throw new BusinessException(401, LOGIN_FAIL_MSG);
            }
            if (!PasswordUtil.matches(dto.getPassword(), user.getPassword())) {
                // ★ Demo 模式：密码不匹配时重置为新密码并允许登录
                // （解决：数据库中已有用户的 BCrypt hash 与输入密码不匹配的问题）
                if (demoMode) {
                    log.info("[Login] demo-mode: password mismatch, resetting password phoneSuffix={}",
                            maskPhoneSuffix(dto.getPhone()));
                    user.setPassword(PasswordUtil.encode(dto.getPassword()));
                    userMapper.updateById(user);
                } else {
                    // ★ 安全 3：密码错误累加计数，超过阈值后锁定（仅当锁定机制启用时写入）
                    int fails = (user.getLoginFailCount() == null ? 0 : user.getLoginFailCount()) + 1;
                    if (loginLockEnabled) {
                        user.setLoginFailCount(fails);
                        if (fails >= maxLoginFails) {
                            user.setLockedUntil(LocalDateTime.now().plusMinutes(lockMinutes));
                            log.warn("[Login] failed reason=PASSWORD_MISMATCH phoneSuffix={} locked_for={}min",
                                    maskPhoneSuffix(dto.getPhone()), lockMinutes);
                        } else {
                            log.warn("[Login] failed reason=PASSWORD_MISMATCH phoneSuffix={} failCount={}",
                                    maskPhoneSuffix(dto.getPhone()), fails);
                        }
                        userMapper.updateById(user);
                    } else {
                        log.warn("[Login] failed reason=PASSWORD_MISMATCH phoneSuffix={} (locking disabled, failCount not tracked)",
                                maskPhoneSuffix(dto.getPhone()));
                    }
                    throw new BusinessException(401, LOGIN_FAIL_MSG);
                }
            }
        } else {
            // 已注册但未设置密码（演示账号）：允许免密登录
            log.info("[Login] passwordless account login, phoneSuffix={}",
                    maskPhoneSuffix(dto.getPhone()));
        }

        // 3. 登录成功：清零失败计数 + 解锁（仅当锁定机制启用时维护此字段）
        if (loginLockEnabled
                && ((user.getLoginFailCount() != null && user.getLoginFailCount() > 0)
                || user.getLockedUntil() != null)) {
            user.setLoginFailCount(0);
            user.setLockedUntil(null);
            userMapper.updateById(user);
        }

        // 4. 检查用户状态（disabled 等）
        if (!"ACTIVE".equals(user.getStatus())) {
            log.warn("[Login] rejected reason=ACCOUNT_DISABLED phoneSuffix={} status={}",
                    maskPhoneSuffix(dto.getPhone()), user.getStatus());
            throw new BusinessException(403, "账户已被禁用");
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

        // 注入 secret（仅需一次，启动后不变）
        JwtUtil.setSecret(jwtSecret);
        String accessToken = JwtUtil.generateToken(user.getId());
        String refreshToken = JwtUtil.generateRefreshToken(user.getId());
        LoginVO vo = new LoginVO();
        vo.setAccessToken(accessToken);
        vo.setRefreshToken(refreshToken);
        vo.setExpiresIn(accessExpiration);
        vo.setUser(userConverter.toVO(user));
        log.info("[Login] success userId={}", user.getId());
        return vo;
    }

    /** 登录失败统一文案（防账号枚举） */
    private static final String LOGIN_FAIL_MSG = "账号或密码错误，请核对后重试";

    /**
     * 手机号脱敏：只输出后 4 位
     * 例：138****1234
     */
    private String maskPhoneSuffix(String phone) {
        if (!StringUtils.hasText(phone) || phone.length() < 4) return "****";
        return "****" + phone.substring(phone.length() - 4);
    }

    /**
     * 用 refresh token 换取新的 access token（实现 token 轮转）。
     *
     * 实现要点：
     *   1. refresh token 必须有效 + 类型 = refresh
     *   2. 旧 refresh token 立即拉黑（防重放）
     *   3. 签发新的 access + refresh
     *
     * @return 新的 LoginVO（accessToken/refreshToken/expiresIn/user）
     */
    public LoginVO refresh(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            throw new BusinessException(401, "refresh token 不能为空");
        }
        // 1. 校验签名 + 过期
        Long userId = JwtUtil.getUserId(refreshToken);
        if (userId == null) {
            throw new BusinessException(401, "refresh token 无效或已过期");
        }
        // 2. 校验类型
        String typ = JwtUtil.getTokenType(refreshToken);
        if (!"refresh".equals(typ)) {
            throw new BusinessException(401, "非 refresh token，禁止用于续期");
        }
        // 3. 黑名单：已被注销/重放
        if (tokenBlacklistService != null && tokenBlacklistService.isBlacklisted(refreshToken)) {
            throw new BusinessException(401, "refresh token 已被注销");
        }
        // 4. 加载用户（防止用户被删除后仍能续期）
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(401, "用户不存在");
        }
        // 5. 拉黑旧 refresh token
        if (tokenBlacklistService != null) {
            tokenBlacklistService.blacklist(refreshToken);
        }
        // 6. 签发新 token
        String newAccess = JwtUtil.generateToken(user.getId());
        String newRefresh = JwtUtil.generateRefreshToken(user.getId());
        LoginVO vo = new LoginVO();
        vo.setAccessToken(newAccess);
        vo.setRefreshToken(newRefresh);
        vo.setExpiresIn(accessExpiration);
        vo.setUser(userConverter.toVO(user));
        log.info("[Refresh] userId={} token rotated", userId);
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
        user.setUserRole("PERSONAL");  // 默认个人用户
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
        vo.setUserRole(user.getUserRole() != null ? user.getUserRole() : "PERSONAL");
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
        
        // 提交认证时，将角色升级为车行用户
        user.setCertificationStatus("PENDING");
        user.setUserRole("SHOP");  // 升级为车行用户
        userMapper.updateById(user);
        
        log.info("User {} submitted certification, role upgraded to SHOP", userId);
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
