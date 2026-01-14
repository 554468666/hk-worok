package com.house.keeping.service.entity;

import lombok.Data;

@Data
public class UserInfoRequestEntity {
    private String openid;
    private String encryptedData;
    private String iv;
}
