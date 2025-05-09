package com.dam.utils;


/**
 * 校验工具
 */
public class CheckUtil {
    /**
     * 密码正则表达式
     * 可以使用数字、字母、特殊字符，长度在6-30之间
     */
    public static final String PASSWORD_PATTERN = ".{6,30}$";

    /**
     * 密码校验
     *
     * @param password
     * @return
     */
    public static boolean passwordCheck(String password) {
        return password.matches(PASSWORD_PATTERN);
    }

    public static void main(String[] args) {
        System.out.println("passwordCheck:" + CheckUtil.passwordCheck("dasa"));
    }
}
