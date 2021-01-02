package com.mall.search.controller;

import com.mall.search.service.EsSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author ：hodor007
 * @date ：Created in 2020/12/31
 * @description ：
 * @version: 1.0
 */
@RestController
@RequestMapping("/search")
public class EsSearchController {
    @Autowired
    private EsSearchService esSearchService;

    @GetMapping("/list")
    public Map search(@RequestParam Map<String, String> searchMap){
        Map resultMap = esSearchService.search(searchMap);
        return resultMap;
    }
}
