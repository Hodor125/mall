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
//必须注入到容器里面
@Component
public class IPFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //得到ip地址
        ServerHttpRequest request = exchange.getRequest();
        String hostName = request.getRemoteAddress().getHostName();

        //TODO 将ip记录到数据库方便进行大数据分析
        System.out.println("过滤器记录用户的ip地址：" + hostName);

        return chain.filter(exchange);    //放行
    }

    @Override
    public int getOrder() {
        //数字越小优先级越高
        return 0;
    }
}
