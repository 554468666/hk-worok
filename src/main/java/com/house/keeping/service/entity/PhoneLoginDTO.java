package com.house.keeping.service.entity;

import lombok.Data;

@Data
public class PhoneLoginDTO {
    private String openid;
    private String sessionKey;   // 可选，后端可缓存
    private String phoneCode;    // getPhoneNumber 返回
    private String nickName;
    private String avatarUrl;
    private String encryptedData;
    private String iv;
}
