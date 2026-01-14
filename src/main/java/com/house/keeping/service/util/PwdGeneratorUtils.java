package com.house.keeping.service.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class PwdGeneratorUtils {
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*_+=<>?/{}[]|";

    private static final String ALL = UPPER + LOWER + DIGITS;
    private static final SecureRandom RANDOM = new SecureRandom();

    public String generate(int length) {
        StringBuilder password = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(ALL.length());
            password.append(ALL.charAt(index));
        }

        return password.toString();
    }
}
