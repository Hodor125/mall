package com.mall.goods.controller;

import com.mall.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ：hodor007
 * @date ：Created in 2020/12/27
 * @description ：
 * @version: 1.0
 */
@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private IdWorker idWorker;

    @GetMapping("/id")
    public long getId(){
        return idWorker.nextId();    }
}
