package com.example.demo.util;

import java.security.SecureRandom;

public class RandomStringUtil {

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom secureRandom = new SecureRandom();

    // 生成指定长度的随机字符串
    public static String generateRandomString(int length) {
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            stringBuilder.append(CHARACTERS.charAt(secureRandom.nextInt(CHARACTERS.length())));
        }
        return stringBuilder.toString();
    }
    public static String generateRandomString() {
        return generateRandomString(6);
    }
}
