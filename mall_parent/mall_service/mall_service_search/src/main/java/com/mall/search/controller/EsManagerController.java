package com.mall.search.controller;

import com.mall.entity.Result;
import com.mall.entity.StatusCode;
import com.mall.search.service.EsManagerService;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ：hodor007
 * @date ：Created in 2020/12/30
 * @description ：
 * @version: 1.0
 */
@RestController
@RequestMapping("/manage")
public class EsManagerController {
    @Autowired
    private EsManagerService esManagerService;

    @PostMapping("/deleteIndexAndMapping")
    public Result deleteIndexAndMapping(){
        esManagerService.deleteIndexAndMapping();
        return new Result(true, StatusCode.OK, "删除索引库和映射关系成功");
    }

    @PostMapping("/createIndexAndMapping")
    public Result createIndexAndMapping(){
        esManagerService.createIndexAndMapping();
        return new Result(true, StatusCode.OK, "创建索引库和映射关系成功");
    }

    @PostMapping("/importAll")
    public Result importAll(){
        esManagerService.importAll();
        return new Result(true, StatusCode.OK, "导入全部数据成功");
    }
}
