package com.house.keeping.service.common;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.house.keeping.service.entity.SysConfigEntity;
import com.house.keeping.service.service.ConfigService;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.util.Set;

@Component
@Slf4j
public class SmmsApi {
    @Value("${hk.smms.user.name:554468666}")
    private String userName;

    @Value("${hk.smms.user.pwd:Aa123456}")
    private String userPwd;

    @Value("${hk.smms.url:https://sm.ms/api/v2}")
    private String smmsUrl;

    @Autowired
    private HttpApi httpApi;

    @Autowired
    private ConfigService configService;

    /**
     * 获取token
     * @return
     */
    public String getApiToken(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.putOnce("username",userName);
        jsonObject.putOnce("password",userPwd);
        String sResponse = httpApi.HttpPost(smmsUrl+"/token",jsonObject);
        return "";
    }
    public void putApiImage(File file){
        try {
        LambdaQueryWrapper<SysConfigEntity> wrapper = new LambdaQueryWrapper<SysConfigEntity>();
        wrapper.eq(SysConfigEntity::getConfigKey,"token");
        wrapper.eq(SysConfigEntity::getName,"smms");
        SysConfigEntity sysConfigEntity = configService.getOne(wrapper);

        String url = smmsUrl+"/token";

        HttpResponse<String> response = Unirest.post(url)
                .header("Authorization", sysConfigEntity.getConfigValue())
                .field("smfile", file)
//                .field("format", "png")
                .asString();

        System.out.println(response.getBody());
        } catch (RuntimeException e) {
            throw new RuntimeException("smms请求失败："+e);
        }
    }

    private static HttpRequest.BodyPublisher buildFormData(JSONObject data) {
        var builder = new StringBuilder();
        Set<String> keys = data.keySet();
        for (String key : keys) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(key).append("=").append(data.getStr(key));
        }
        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }
}
