package com.ahu.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ：hodor007
 * @date ：Created in 2020/12/24
 * @description ：
 * @version: 1.0
 */
@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping
    public String hello(){
        return "hello world";
    }
}
