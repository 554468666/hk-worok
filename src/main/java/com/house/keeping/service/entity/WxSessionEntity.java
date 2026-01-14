package com.house.keeping.service.entity;

import lombok.Data;

@Data
public class WxSessionEntity {
    private String openid;
    private String session_key;
    private String unionid;
    private Integer errcode;
    private String errmsg;
}
