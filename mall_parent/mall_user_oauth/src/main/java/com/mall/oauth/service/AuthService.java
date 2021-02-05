package com.mall.oauth.service;

import com.mall.oauth.util.AuthToken;

/**
 * @author ：XXXX
 * @date ：Created in 2021/2/5
 * @description ：
 * @version: 1.0
 */
public interface AuthService {
    AuthToken applyToken(String clientId, String clientSecret, String username, String password);
}
