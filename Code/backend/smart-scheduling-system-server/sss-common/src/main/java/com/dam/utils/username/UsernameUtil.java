package com.dam.utils.username;

import java.util.Random;

public class UsernameUtil {
    /**
     * 用户名的最小位数
     */
    private static final int MIN_LENGTH = 6;
    /**
     * 用户名最大位数
     */
    private static final int MAX_LENGTH = 12;
    /**
     * 包含所有小写字母的常用
     */
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static final Random RANDOM = new Random();

    /**
     * 随机生成用户名
     * @return
     */
    public static String generateUsername() {
        int length = RANDOM.nextInt(MAX_LENGTH - MIN_LENGTH + 1) + MIN_LENGTH;
        StringBuilder usernameBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char c = ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length()));
            usernameBuilder.append(c);
        }
        return usernameBuilder.toString();
    }

}
