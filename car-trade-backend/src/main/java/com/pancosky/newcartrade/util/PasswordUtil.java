package com.pancosky.newcartrade.util;

import cn.hutool.crypto.digest.BCrypt;

/**
 * 密码加密工具（基于 BCrypt）
 */
public class PasswordUtil {

    /**
     * 加密密码
     */
    public static String encode(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    /**
     * 验证密码
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) return false;
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }
}
