package com.mall;

import com.mall.util.IdWorker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author ：hodor007
 * @date ：Created in 2020/12/22
 * @description ：
 * @version: 1.0
 */
@SpringBootApplication
@EnableEurekaClient
@MapperScan(basePackages = "com.mall.goods.dao")
public class GoodsApplication {
    @Value("${workerId}")
    private long workerId;
    @Value("${datacenterId}")
    private long datacenterId;

    public static void main(String[] args) {
        SpringApplication.run(GoodsApplication.class, args);
    }

    @Bean
    public IdWorker idWorker(){
        return new IdWorker(workerId,datacenterId);
    }
}
