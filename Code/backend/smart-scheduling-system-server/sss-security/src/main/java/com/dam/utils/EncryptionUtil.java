package com.dam.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 加密工具
 */
public class EncryptionUtil {
    private static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 盐值MD5加密
     *
     * @param strSrc
     * @return
     */
    public static String saltMd5Encrypt(String strSrc) {
        return passwordEncoder.encode(strSrc);
    }

    /**
     * 判断原密码和加密之后的密码是否相符
     *
     * @param originalPassword
     * @param encryptPassword
     * @return
     */
    public static boolean isSaltMd5Match(String originalPassword, String encryptPassword) {
        return passwordEncoder.matches(originalPassword, encryptPassword);
    }

    public static void main(String[] args) {
        System.out.println(EncryptionUtil.saltMd5Encrypt("123456"));
    }
}
