package com.house.keeping.service.common;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.house.keeping.service.entity.SysConfigEntity;
import com.house.keeping.service.service.ConfigService;
import com.house.keeping.service.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;


@Slf4j
@Component
public class PicuiApi {

    private final static String picui_token = "picui_token";

    @Value("${hk.picui.url:https://picui.cn/api/v1}")
    private String picuiUrl;

    @Autowired
    private ConfigService configService;

    @Autowired
    private RedisService redisService;

    /**
     * 获取临时token /images/tokens
     * @return 响应报文
     */
    public String getToken(){
        //设置请求体参数
        JSONObject jsonObject = new JSONObject();
        jsonObject.putOnce("num", 1);
        jsonObject.putOnce("seconds", 7200);
        // 创建HttpClient实例
        HttpClient client = HttpClient.newHttpClient();

        // 创建HttpRequest实例，设置请求方法为POST，并设置请求体
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(picuiUrl+"/images/tokens"))
                .header("Authorization",getUserToken())
                .header("Content-Type","application/json")
                .header("Accept","application/json")
                .header("User-Agent","PostmanRuntime/7.43.4")
                .POST(HttpRequest.BodyPublishers.ofString(jsonObject.toString())) // 设置请求体
                .build();

        // 发送请求并获取响应
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 获取响应状态码
            int statusCode = response.statusCode();
            log.info("Status Code: " + statusCode);

            // 获取响应头
            response.headers().map().forEach((k, v) -> log.info(k + ":" + v));

            // 获取响应体
            JSONObject responseBody = new JSONObject(response.body());
            log.info("Response Body: " + responseBody);
            if(!responseBody.getBool("status")){
                log.info(responseBody.getStr("message"));
                throw new RuntimeException(responseBody.getStr("message"));
            }
            String token = "";
            JSONArray tokensArray = responseBody.getJSONObject("data").getJSONArray("tokens");
            for (int i = 0; i < tokensArray.size(); i++) {
                token = tokensArray.getJSONObject(i).getStr("token");
            }
            redisService.setWithExpire(picui_token,token,7000);
            return token;

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Http API ERROR:"+e);
        }
    }

    public String picuiPutFile(File file)  {

        if (!file.exists()) {
            log.info("文件不存在，请检查路径！");
            throw new RuntimeException("文件不存在，请检查路径！");
        }
        String picui_token1 = redisService.get(picui_token);
        if(picui_token1 == null){
            picui_token1 = getToken();
        }
        // 替换为你的PICUI接口URL
        String uploadUrl = picuiUrl+"/upload";
        // 替换为你的授权Token
        String token = getUserToken();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost uploadRequest = new HttpPost(uploadUrl);

            // 设置请求头
            uploadRequest.setHeader("Authorization", token);
            uploadRequest.setHeader("Accept", "application/json");

            // 构建请求体

            byte[] imageBytes = Files.readAllBytes(file.toPath());

            HttpEntity requestEntity = MultipartEntityBuilder.create()
                    .addBinaryBody("file", imageBytes, ContentType.APPLICATION_OCTET_STREAM, file.getName())
                    .addTextBody("token", picui_token1) // 替换为你的临时上传Token
                    .addTextBody("permission", "0") // 1=公开，0=私有
                    .build();

            uploadRequest.setEntity(requestEntity);

            // 发送请求并处理响应
            org.apache.http.HttpResponse response = httpClient.execute(uploadRequest);
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                String responseString = EntityUtils.toString(responseEntity);
                log.info("Response: " + responseString);
            }

            log.info(response.toString());
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uploadUrl;
    }



    public String getUserToken(){
        LambdaQueryWrapper<SysConfigEntity> wrapper = new LambdaQueryWrapper<SysConfigEntity>();
        wrapper.eq(SysConfigEntity::getConfigKey,"token");
        wrapper.eq(SysConfigEntity::getName,"picui");
        SysConfigEntity sysConfigEntity = configService.getOne(wrapper);
        return sysConfigEntity.getConfigValue();
    }



}
