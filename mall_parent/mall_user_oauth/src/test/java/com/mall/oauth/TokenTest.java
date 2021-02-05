package com.mall.oauth;

import com.mall.OAuthApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

/**
 * @author ：hodor007
 * @date ：Created in 2021/2/5
 * @description ：
 * @version: 1.0
 */
@SpringBootTest(classes = OAuthApplication.class)
@RunWith(SpringRunner.class)
public class TokenTest {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private LoadBalancerClient loadBalancerClient;

    String clientId = "changgou";
    String clientSecret = "123456";
    String username = "heima";
    String password = "123456";

    /**
     * 测试密码模式获取Token
     */
//    @Test
    public void testApplyToken() {
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
        if(resultMap != null && resultMap.size() > 0) {
            for (Object key : resultMap.keySet()) {
                System.out.println(key + "<=======>" + resultMap.get(key));
            }
        }
    }

    public String getHttpBasic(String clientId, String clientSecret) {
        String basic = clientId + ":" + clientSecret;
        basic = Base64Utils.encodeToString(basic.getBytes());
        return "Basic " + basic;
    }
}
