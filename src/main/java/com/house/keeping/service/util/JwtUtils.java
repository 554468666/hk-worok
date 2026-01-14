package com.house.keeping.service.util;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.house.keeping.service.service.RedisService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Slf4j
@Component
public class JwtUtils {
    private String secret;
    private long expireSeconds;

    @Autowired
    private RedisService redisService;

    @Autowired
    private PwdGeneratorUtils pwdGeneratorUtils;

    // 其他代码保持不变...

    public String generate(String openid) {
        // 从 Redis 获取密钥
        String secretKeyBase64 = redisService.get("jwt_secret");

        if (StringUtils.isEmpty(secretKeyBase64)) {
            // 生成一个安全的 HS256 密钥
            SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
            // 将密钥转换为 Base64 编码存储
            secretKeyBase64 = Encoders.BASE64.encode(secretKey.getEncoded());

            expireSeconds = 2592000L;
            redisService.setWithExpire("jwt_secret", secretKeyBase64, expireSeconds);
            log.info("Generated new JWT secret key");
        } else {
            // 清理可能的空格
            secretKeyBase64 = secretKeyBase64.trim();
            // 临时：清理旧密钥并重新生成
            if (secretKeyBase64.contains(" ") || !isValidBase64(secretKeyBase64)) {
                log.warn("Invalid JWT secret key found, regenerating...");
                redisService.del("jwt_secret");
                SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
                secretKeyBase64 = Encoders.BASE64.encode(secretKey.getEncoded());
                expireSeconds = 2592000L;
                redisService.setWithExpire("jwt_secret", secretKeyBase64, expireSeconds);
                log.info("Regenerated JWT secret key");
            }
            expireSeconds = redisService.getTimeOut("jwt_secret");
            log.info("Using existing JWT secret key");
        }

        // 保存 Base64 编码的密钥
        this.secret = secretKeyBase64;

        // 解码为字节数组用于签名
        byte[] secretKeyBytes = Decoders.BASE64.decode(secretKeyBase64);

        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(openid)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(expireSeconds)))
                .signWith(SignatureAlgorithm.HS256, secretKeyBytes)
                .compact();
    }

    private boolean isValidBase64(String str) {
        try {
            Decoders.BASE64.decode(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getOpenid(String token) {
        try {
            // 每次解析时都从 Redis 获取密钥，避免 secret 为 null
            String secretKeyBase64 = this.secret;
            if (StringUtils.isEmpty(secretKeyBase64)) {
                secretKeyBase64 = redisService.get("jwt_secret");
                if (StringUtils.isEmpty(secretKeyBase64)) {
                    log.error("JWT secret key not found in Redis");
                    return null;
                }
            }

            // 清理可能的空格
            secretKeyBase64 = secretKeyBase64.trim();
            log.debug("JWT secret key length: {}", secretKeyBase64.length());

            // 解析时需要先将 Base64 编码的密钥转回字节数组
            byte[] secretKeyBytes = Decoders.BASE64.decode(secretKeyBase64);
            return Jwts.parser()
                    .setSigningKey(secretKeyBytes)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            log.error("Failed to parse JWT: {}", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}