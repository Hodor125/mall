package com.mall.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author ：hodor007
 * @date ：Created in 2020/12/25
 * @description ：
 * @version: 1.0
 */
@Component
public class URLFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取url
        ServerHttpRequest request = exchange.getRequest();
        String url = request.getURI().getPath();

        //TODO将用户请求的url存到数据库
        System.out.println("记录用户请求的url");

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        //优先级在IPFilter之后
        return 1;
    }
}
