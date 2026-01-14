package com.house.keeping;

import cn.hutool.crypto.digest.BCrypt;

public class GeneratePassword {
    public static void main(String[] args) {
        String password = "admin123";
        String hashed = BCrypt.hashpw(password);
        System.out.println("Password: " + password);
        System.out.println("Hashed: " + hashed);

        // 验证
        boolean check = BCrypt.checkpw(password, hashed);
        System.out.println("Verification: " + check);
    }
}
