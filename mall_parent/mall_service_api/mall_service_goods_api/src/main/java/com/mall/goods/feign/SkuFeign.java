package com.mall.goods.feign;

import com.mall.entity.Result;
import com.mall.goods.pojo.Sku;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 建立url和微服务名的关系
 */
@FeignClient(name = "goods")
@RequestMapping("/sku")
public interface SkuFeign {

    @GetMapping("findBySpuId/{spuId}")
    public List<Sku> findBySpuId(@PathVariable String spuId);

    @GetMapping
    public Result findAll();

    @GetMapping("/{id}")
    public Sku findById(@PathVariable("id") String id);
}
