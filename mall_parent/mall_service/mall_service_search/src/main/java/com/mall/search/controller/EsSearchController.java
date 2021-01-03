package com.mall.search.controller;

import com.mall.entity.Page;
import com.mall.search.service.EsSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
//@RestController
@Controller
@RequestMapping("/search")
public class EsSearchController {
    @Autowired
    private EsSearchService esSearchService;

    //返回String
    @GetMapping("/list")
    public String search(@RequestParam Map<String, String> searchMap, Model model){
        Map resultMap = esSearchService.search(searchMap);
        model.addAttribute("result", resultMap);
        model.addAttribute("searchMap", searchMap);

        //设置page对象，对应前端的页面
        Integer total = Integer.valueOf(String.valueOf(resultMap.get("total")));
        Integer pageNum = Integer.valueOf(String.valueOf(resultMap.get("pageNum")));
        Page page = new Page(total, pageNum, Page.pageSize);
        model.addAttribute("page", page);

        //拼接url，前端渲染需要url
        //http://localhost:9009/search/list?keywords=%E6%89%8B%E6%9C%BA
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("/search/list");

        //http://localhost:9009/search/list?keywords=%E6%89%8B%E6%9C%BA&brand=%E5%8D%8E%E4%B8%BA&sortRule=ASC&sortField=price
        if(searchMap != null && searchMap.size() > 0){
            int count = 0;
            for (String key : searchMap.keySet()) {
                if(!"sortRule".equals(key) && !"pageNum".equals(key) && !"sortField".equals(key)){
                    String value = searchMap.get(key);
                    if(count == 0){
                        urlBuilder.append("?" + key + "=" + value);
                    } else {
                        urlBuilder.append("&" + key + "=" + value);
                    }
                    count++;
                }
            }
        }
        model.addAttribute("url", urlBuilder.toString());

        return "search";
    }
}
