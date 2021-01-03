package com.mall.controller;

import com.mall.entity.Result;
import com.mall.entity.StatusCode;
import com.mall.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ：hodor007
 * @date ：Created in 2021/1/3
 * @description ：
 * @version: 1.0
 */
@RestController
@RequestMapping("/page")
public class PageController {
    @Autowired
    private PageService pageService;

    @PostMapping("/createPage/{spuId}")
    public Result createPage(@PathVariable String spuId){
        pageService.createPageHtml(spuId);
        return new Result(true, StatusCode.OK, "创建静态页面成功");
    }
}
