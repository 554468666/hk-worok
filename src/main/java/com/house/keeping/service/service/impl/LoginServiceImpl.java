package com.house.keeping.service.service.impl;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.house.keeping.service.entity.PhoneLoginDTO;
import com.house.keeping.service.entity.UserEntity;
import com.house.keeping.service.entity.WxSessionEntity;
import com.house.keeping.service.service.LoginService;
import com.house.keeping.service.service.RedisService;
import com.house.keeping.service.service.UserService;
import com.house.keeping.service.util.JwtUtils;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    @Value("${hk.wechat.appid}")
    private String appid;

    @Value("${hk.wechat.secret}")
    private String secret;

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public WxSessionEntity code2Session(String code) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.TEXT_PLAIN));

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(0, converter);
        String url = String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                appid, secret, code);
        WxSessionEntity response =restTemplate.getForObject(url, WxSessionEntity.class);
        redisService.setWithExpire("wx_session_key_"+response.getOpenid(),response.getSession_key(),7200);
        return response;
    }

    @Override
    public Map<String, Object> phoneLogin(PhoneLoginDTO dto, HttpSession session) {
        // 参数校验
        if (dto == null || StringUtils.isEmpty(dto.getOpenid()) ||
                StringUtils.isEmpty(dto.getEncryptedData()) || StringUtils.isEmpty(dto.getIv())) {
            throw new IllegalArgumentException("登录参数不能为空");
        }

        try {
            // 从缓存获取sessionKey
            String sessionKey = dto.getSessionKey();
            session.setAttribute("sessionKey",sessionKey);
            if(StringUtils.isEmpty(sessionKey)){
                getSessionKeyFromCache(dto.getOpenid());
            }
            if (sessionKey == null) {
                throw new RuntimeException("会话已过期，请重新登录");
            }

            // 解密手机号
            String phone = decryptPhoneNumber(dto.getEncryptedData(), sessionKey, dto.getIv());
            if (StringUtils.isEmpty(phone)) {
                throw new RuntimeException("获取手机号失败");
            }

            // 后续用户处理逻辑保持不变
            UserEntity user = userService.findByOpenId(dto.getOpenid());

            if (user == null) {
                user = new UserEntity();
                user.setCreatedAt(Date.from(Instant.now()));
            }

            user.setOpenId(dto.getOpenid());
            user.setPhone(phone);
            user.setName(dto.getNickName());
            user.setSessionKey(dto.getSessionKey());
            user.setImageUrl(dto.getAvatarUrl());
            // 更新用户信息...

            userService.saveOrUpdate(user);
            Map<String,Object> userMap = new HashMap<>();
            userMap.put("name",user.getName());
            userMap.put("phone",user.getPhone());
            userMap.put("openId",user.getOpenId());
            userMap.put("sessionKet",user.getSessionKey());
            redisService.saveMap("current_login_user_"+user.getSessionKey(),userMap);
            String token = jwtUtils.generate(user.getOpenId());
            return Map.of("token", token, "user", user);

        } catch (Exception e) {
            log.error("获取手机号过程中发生异常", e);
            throw new RuntimeException("获取手机号失败: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> checkUserPhone(Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();

        try {
            String openid = params.get("openid");

            // 查询用户是否存在
            UserEntity user = userService.findByOpenId(openid);

            if (user == null) {
                result.put("code", 404);
                result.put("msg", "用户不存在");
            } else {
                result.put("code", 200);
                result.put("msg", "查询成功");
                result.put("user", user);
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "服务器内部错误");
            log.error("checkUser error: ", e);
        }

        return result;
    }

    // 添加手机号解密方法
    private String decryptPhoneNumber(String encryptedData, String sessionKey, String iv) {
        try {
            // Base64解码
            byte[] encryptedDataBytes = Base64.getDecoder().decode(encryptedData);
            byte[] sessionKeyBytes = Base64.getDecoder().decode(sessionKey);
            byte[] ivBytes = Base64.getDecoder().decode(iv);

            // AES解密
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(sessionKeyBytes, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] decryptedBytes = cipher.doFinal(encryptedDataBytes);
            String decryptedData = new String(decryptedBytes, StandardCharsets.UTF_8);

            // 解析JSON获取手机号
            JSONObject jsonObject = new JSONObject(decryptedData);
            return jsonObject.getStr("phoneNumber");
        } catch (Exception e) {
            log.error("手机号解密失败", e);
            throw new RuntimeException("手机号解密失败");
        }
    }

    // 获取 access_token（可缓存 2 小时）
    private String getAccessToken() {
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="
                + appid + "&secret=" + secret;
        Map<?,?> res = new RestTemplate().getForObject(url, Map.class);
        return (String) res.get("access_token");
    }

    // 从缓存获取sessionKey
    private String getSessionKeyFromCache(String openid) {
        return redisService.get("wx_session_key_"+openid);
    }
}
