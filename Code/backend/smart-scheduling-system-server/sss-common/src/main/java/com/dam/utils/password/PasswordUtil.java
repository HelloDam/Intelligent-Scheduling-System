package com.dam.utils.password;

import java.util.Random;

public class PasswordUtil {
    private static final int MIN_LENGTH = 6;
    private static final int MAX_LENGTH = 12;
    private static final String DIGITS = "0123456789";
    private static final Random RANDOM = new Random();

    /**
     * 随机生成密码
     * @return
     */
    public static String generatePassword() {
        int length = RANDOM.nextInt(MAX_LENGTH - MIN_LENGTH + 1) + MIN_LENGTH;
        StringBuilder passwordBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char c = DIGITS.charAt(RANDOM.nextInt(DIGITS.length()));
            passwordBuilder.append(c);
        }
        return passwordBuilder.toString();
    }
}
