package com.mall.filter;

import com.mall.util.JwtUtil;
import io.netty.util.internal.StringUtil;
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
 * @date ：Created in 2020/12/26
 * @description ：
 * @version: 1.0
 */
@Component
public class AuthFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1 获取请求
        ServerHttpRequest request = exchange.getRequest();

        //2 获取响应
        ServerHttpResponse response = exchange.getResponse();

        //3 获取请求url是否是登录url，如果是就放行，如果不是就继续判断
        String path = request.getURI().getPath();
        if(path.contains("/admin/login")){
            return chain.filter(exchange);
        }

        //4 获取请求头中的token值
        String token = request.getHeaders().getFirst("token");

        //5 判断token值是否为空，如果为空就拒绝用户，如果不为空就继续判断
        if(StringUtil.isNullOrEmpty(token)){
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        //6 进行token值得解析操作，如果解析失败就拒绝用户
        try {
            JwtUtil.parseJWT(token);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        //7 如果解析成功，就放行
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
