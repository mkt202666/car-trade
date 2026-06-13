package com.pancosky.newcartrade.util;

import cn.hutool.crypto.digest.BCrypt;

/**
 * 一次性工具：生成密码 "123456" 的 BCrypt 哈希。
 * 用法：cd target/classes && java -cp ".;../../../...hutool..." com.pancosky.newcartrade.util.PasswordHashGen
 */
public class PasswordHashGen {
    public static void main(String[] args) {
        String hash = BCrypt.hashpw("123456", BCrypt.gensalt(10));
        System.out.println("123456 BCrypt hash: " + hash);
        System.out.println("verify 123456: " + BCrypt.checkpw("123456", hash));
        // 同时也生成 password 的，备查
        String hash2 = BCrypt.hashpw("password", BCrypt.gensalt(10));
        System.out.println("password BCrypt hash: " + hash2);
        System.out.println("verify password: " + BCrypt.checkpw("password", hash2));
    }
}
