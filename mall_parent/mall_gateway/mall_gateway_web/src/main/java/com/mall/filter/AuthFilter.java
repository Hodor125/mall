package com.mall.filter;

import com.mall.service.AuthService;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author ：hodor007
 * @date ：Created in 2021/2/5
 * @description ：
 * @version: 1.0
 */
@Component
public class AuthFilter implements GlobalFilter, Ordered {
    @Autowired
    private AuthService authService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取请求以及响应
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        //判断当前的url是否是登录url，如果是就放行
        String path = request.getURI().getPath();
        if(path.contains("/oauth/interface/login") || path.contains("/oauth/login") || path.contains("/oauth/toLogin")) {
            return chain.filter(exchange);
        }

        //判断请求的cookie中是否有名字为uid的，它的值就是jti，如果没有就报错
        String jti = authService.getJtiFromCookie(request);
        if(StringUtil.isNullOrEmpty(jti)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        //根据jti在redis中获取长令牌，如果没有就报错返回
        String token = authService.getTokenFromRedis(jti);
        if(StringUtil.isNullOrEmpty(token)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        //传递Authorization请求头到具体的微服务(资源服务器)
        request.mutate().header("Authorization", "Bearer " + token);
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
