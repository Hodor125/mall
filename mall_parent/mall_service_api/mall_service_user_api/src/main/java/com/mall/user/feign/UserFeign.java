package com.mall.user.feign;

import com.mall.user.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author ：XXXX
 * @date ：Created in 2021/2/5
 * @description ：
 * @version: 1.0
 */
@FeignClient("user")
@RequestMapping("/user")
public interface UserFeign {
    @GetMapping("/load/{username}")
    public User loadById(@PathVariable String username);
}
