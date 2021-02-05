package com.mall.service;

import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 * @author ：XXXX
 * @date ：Created in 2021/2/5
 * @description ：
 * @version: 1.0
 */
public interface AuthService {
    /**
     * 从cookie中获取jti短令牌
     * @return
     */
    String getJtiFromCookie(ServerHttpRequest request);

    /**
     * 根据jti从redis总获取长令牌
     * @return
     */
    String getTokenFromRedis(String jti);
}
