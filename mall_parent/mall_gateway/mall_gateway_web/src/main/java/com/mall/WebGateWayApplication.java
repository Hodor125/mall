package com.mall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author ：hodor007
 * @date ：Created in 2021/2/5
 * @description ：
 * @version: 1.0
 */
@SpringBootApplication
@EnableDiscoveryClient
public class WebGateWayApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebGateWayApplication.class,args);
    }
}
