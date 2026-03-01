package com.zhy.auraojbackend.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 密码加密工具类
 * 使用SHA-256 + 盐值进行密码加密和验证
 *
 * @author zhy
 * @date 2026/3/1
 */
public class PasswordEncoderUtil {

    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 对密码进行加密
     *
     * @param rawPassword 明文密码
     * @return 加密后的密码（包含盐值）
     */
    public static String encode(String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        
        // 生成随机盐值
        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);
        
        // 加密密码
        String hashedPassword = hashPassword(rawPassword, salt);
        
        // 将盐值和哈希值组合存储
        return Base64.getEncoder().encodeToString(salt) + "$" + hashedPassword;
    }

    /**
     * 验证明文密码与加密密码是否匹配
     *
     * @param rawPassword 明文密码
     * @param encodedPassword 加密密码
     * @return 是否匹配
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        
        try {
            // 分离盐值和哈希值
            String[] parts = encodedPassword.split("\\$");
            if (parts.length != 2) {
                return false;
            }
            
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            String storedHash = parts[1];
            
            // 使用相同盐值重新计算哈希
            String computedHash = hashPassword(rawPassword, salt);
            
            return computedHash.equals(storedHash);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查密码强度
     *
     * @param password 密码
     * @return 是否符合强度要求
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    /**
     * 使用SHA-256算法和盐值计算密码哈希
     *
     * @param password 密码
     * @param salt 盐值
     * @return 哈希值
     */
    private static String hashPassword(String password, byte[] salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            return bytesToHex(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("不支持的哈希算法: " + ALGORITHM, e);
        }
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}