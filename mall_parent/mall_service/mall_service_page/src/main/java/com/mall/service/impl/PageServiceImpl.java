package com.mall.service.impl;

import com.alibaba.fastjson.JSON;
import com.mall.goods.feign.CategoryFeign;
import com.mall.goods.feign.SkuFeign;
import com.mall.goods.feign.SpuFeign;
import com.mall.goods.pojo.Category;
import com.mall.goods.pojo.Sku;
import com.mall.goods.pojo.Spu;
import com.mall.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查看文档，页面需要五种数据
 * @author ：hodor007
 * @date ：Created in 2021/1/3
 * @description ：
 * @version: 1.0
 */
@Service
public class PageServiceImpl implements PageService {
    @Autowired
    private SpuFeign spuFeign;
    @Autowired
    private SkuFeign skuFeign;
    @Autowired
    private CategoryFeign categoryFeign;
    @Autowired
    private TemplateEngine templateEngine;
    @Value("${pagepath}")
    private String pagePath;

    @Override
    public Map buildPageData(String spuId) {
        Map pageData = new HashMap();
        //获取spu
        Spu spu = spuFeign.findById(spuId);
        if(spu == null){
            throw new RuntimeException("数据不存在,spuId=" + spuId);
        }
        pageData.put("spu", spu);

        //获取skuList
        List<Sku> skuList = skuFeign.findBySpuId(spuId);
        pageData.put("skuList", skuList);

        //获取三级目录
        Integer category1Id = spu.getCategory1Id();
        Integer category2Id = spu.getCategory2Id();
        Integer category3Id = spu.getCategory3Id();
        Category category1 = categoryFeign.findById(category1Id);
        Category category2 = categoryFeign.findById(category2Id);
        Category category3 = categoryFeign.findById(category3Id);
        pageData.put("category1", category1);
        pageData.put("category2", category2);
        pageData.put("category3", category3);

        //从spu获取图片集合的链接
        List<Map> imgList = JSON.parseArray(spu.getImages(), Map.class);
        ArrayList<String> imageList = new ArrayList<>();
        if(imgList != null && imgList.size() > 0){
            for (Map map : imgList) {
                String url = String.valueOf(map.get("url"));
                imageList.add(url);
            }
        }
        pageData.put("imageList", imageList);

        //规格数据，从spu中获取，前端需要的是map，不需要进行转换
        String specItems = spu.getSpecItems();
        Map specificationList = JSON.parseObject(specItems, Map.class);
        pageData.put("specificationList", specificationList);
        return pageData;
    }

    @Override
    public void createPageHtml(String spuId) {
        FileWriter writer = null;
        try {
            Map pageData = buildPageData(spuId);
            Context context = new Context();
            context.setVariables(pageData);    //为模板引擎设置数据
            File file = new File(pagePath);
            if(!file.exists()){    //判断目录是否存在
                file.mkdirs();
            }
            writer = new FileWriter(pagePath + "/" + spuId + ".html");
            //利用模板引擎技术生成静态页面到本地磁盘    生产情况是设置在niginx服务器
            templateEngine.process("item", context, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(writer != null){
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
