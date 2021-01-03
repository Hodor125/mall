package com.mall.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author ：hodor007
 * @date ：Created in 2021/1/3
 * @description ：
 * @version: 1.0
 */
@Controller
@RequestMapping("/page")
public class PageDetailController {
    //访问http://localhost:9011/items/1345756097339719680.html
    @GetMapping("/pageDetail/{spuId}")
    public String pageDetail(@PathVariable String spuId){
        return "items/" + spuId;
    }
}
