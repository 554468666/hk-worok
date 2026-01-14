package com.house.keeping.service.service;

import com.house.keeping.service.entity.PhoneLoginDTO;
import com.house.keeping.service.entity.WxSessionEntity;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

public interface LoginService {
    WxSessionEntity code2Session(String code);

    Map<String,Object> phoneLogin(PhoneLoginDTO dto, HttpSession session);

    Map<String, Object> checkUserPhone(Map<String, String> params);
}
