package com.house.keeping.service.common;

import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
@Slf4j
public class HttpApi {

    /**
     * httpPost请求
     * @param url 请求地址
     * @param argsBody 请求参数
     * @return 响应报文
     */
    public String HttpPost(String url,JSONObject argsBody){
        // 创建HttpClient实例
        HttpClient client = HttpClient.newHttpClient();

        // 创建HttpRequest实例，设置请求方法为POST，并设置请求体
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent","PostmanRuntime/7.43.4")
                .POST(HttpRequest.BodyPublishers.ofString(argsBody.toString())) // 设置请求体
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
            String responseBody = response.body();
            log.info("Response Body: " + responseBody);
            return responseBody;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Http API ERROR:"+e);
        }
    }

    /**
     * HttpGet请求
     * @param url 请求地址
     * @param args 请求参数
     * @return 响应报文
     */
    public String HttpGet(String url,JSONObject args){

        Set<String> keys = args.keySet();
        if (keys.size() > 0){
            url+="?";
        }
        for (String key : keys) {
            url+=key+":"+args.getStr(key);
        }
        // 创建HttpClient实例
        HttpClient client = HttpClient.newHttpClient();

        // 创建HttpRequest实例，设置请求方法为GET
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json") // 设置请求头（如果需要）
                .build();

        // 发送请求并获取响应
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 获取响应状态码
            int statusCode = response.statusCode();
            log.info("Status Code: " + statusCode);

            // 获取响应头
            response.headers().map().forEach((k, v) -> System.out.println(k + ":" + v));

            // 获取响应体
            String responseBody = response.body();
            log.info("Response Body: " + responseBody);
            return responseBody;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Http API ERROR:"+e);
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
