package com.mall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.mall.entity.Result;
import com.mall.goods.feign.SkuFeign;
import com.mall.goods.pojo.Sku;
import com.mall.search.dao.ESManagerMapper;
import com.mall.search.pojo.SkuInfo;
import com.mall.search.service.EsManagerService;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author ：hodor007
 * @date ：Created in 2020/12/30
 * @description ：
 * @version: 1.0
 */
@Service
public class EsManagerServiceImpl implements EsManagerService {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private SkuFeign skuFeign;
    @Autowired
    private ESManagerMapper esManagerMapper;

    @Override
    public void deleteIndexAndMapping() {
        elasticsearchTemplate.deleteIndex(SkuInfo.class);
    }

    @Override
    public void createIndexAndMapping() {
        elasticsearchTemplate.createIndex(SkuInfo.class);
        elasticsearchTemplate.putMapping(SkuInfo.class);
    }

    @Override
    public void importBySpuId(String spuId) {
        //1 根据spuId查询sku列表（使用feign接口调用服务，不要写冗余的代码）
        List<Sku> skuList = skuFeign.findBySpuId(spuId);
        //将skuList转为json字符串
        String skuListJson = JSON.toJSONString(skuList);
        //将json字符串转为List<SkuInfo>
        List<SkuInfo> skuInfoList = JSON.parseArray(skuListJson, SkuInfo.class);
        //将spec转为具体的specMap，将字符串spec转为对象
        if(skuInfoList != null && skuInfoList.size() > 0){
            for (SkuInfo skuInfo : skuInfoList) {
                Map map = JSON.parseObject(skuInfo.getSpec(), Map.class);
                skuInfo.setSpecMap(map);
            }
        }
        //2 将spu数据导入es中
        esManagerMapper.saveAll(skuInfoList);
    }

    @Override
    public void importAll() {
        //1 查找全部的数据，通过feign接口调用
        Result result = skuFeign.findAll();

        //2 数据转为SKuInfo
        String allJSON = JSON.toJSONString(result.getData());
        List<SkuInfo> skuInfoList = JSON.parseArray(allJSON, SkuInfo.class);
        //将spec转为specMap，spec是字符串
        if(skuInfoList != null && skuInfoList.size() > 0){
            for (SkuInfo skuInfo : skuInfoList) {
                Map map = JSON.parseObject(skuInfo.getSpec(), Map.class);
                skuInfo.setSpecMap(map);
            }
        }

        //3 将数据导入es
        esManagerMapper.saveAll(skuInfoList);
    }

    //删除对应spuId的es数据
    @Override
    public void removeBySpuId(String spuId) {
        //1 查询所有的sku
        List<Sku> skuList = skuFeign.findBySpuId(spuId);

        //2 删除es中的数据
        for (Sku sku : skuList) {
            Long id = Long.valueOf(sku.getId());
            esManagerMapper.deleteById(id);
        }
    }
}
