package com.mall.oauth.service.impl;

import com.mall.oauth.service.AuthService;
import com.mall.oauth.util.AuthToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author ：hodor007
 * @date ：Created in 2021/2/5
 * @description ：
 * @version: 1.0
 */
@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private LoadBalancerClient loadBalancerClient;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Value("${auth.ttl}")
    private Long ttl;


    @Override
    public AuthToken applyToken(String clientId, String clientSecret, String username, String password) {
        //1 获取以及拼接url 获取oauth的ip地址和端口
        String url = loadBalancerClient.choose("USER-AUTH").getUri() + "/oauth/token";
        //2 定义http请求头Authorization (http basic)
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.set("Authorization", getHttpBasic(clientId, clientSecret));
        //3 定义Oauth2密码模式下申请令牌的业务参数
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.set("grant_type", "password");
        body.set("username", username);
        body.set("password", password);
        //4 封装一个http请求实例
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
        //5 执行POST发送请求
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                //对于401和400不抛出异常
                if(response.getRawStatusCode()!=400 && response.getRawStatusCode()!=401) {
                    super.handleError(response);
                }
            }
        });
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        //6 请求完成得到结果
        Map resultMap = response.getBody();
        AuthToken authToken = new AuthToken();
        if(resultMap != null && resultMap.size() > 0) {
            for (Object key : resultMap.keySet()) {
                System.out.println(key + "<=======>" + resultMap.get(key));
            }
            authToken.setJti(String.valueOf(resultMap.get("jti")));
            authToken.setAccessToken(String.valueOf(resultMap.get("access_token")));
            authToken.setRefreshToken(String.valueOf(resultMap.get("refresh_token")));
        } else {
            throw new RuntimeException("申请令牌失败");
        }
        //写入redis中
        stringRedisTemplate.boundValueOps(String.valueOf(authToken.getJti()))
                .set(String.valueOf(authToken.getAccessToken()), ttl, TimeUnit.SECONDS);
        return authToken;
    }

    public String getHttpBasic(String clientId, String clientSecret) {
        String basic = clientId + ":" + clientSecret;
        basic = Base64Utils.encodeToString(basic.getBytes());
        return "Basic " + basic;
    }
}
