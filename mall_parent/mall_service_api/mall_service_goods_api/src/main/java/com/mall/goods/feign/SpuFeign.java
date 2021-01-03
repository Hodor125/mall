package com.mall.goods.feign;

import com.mall.goods.pojo.Spu;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author ：XXXX
 * @date ：Created in 2021/1/3
 * @description ：
 * @version: 1.0
 */
@FeignClient("goods")
@RequestMapping("/spu")
public interface SpuFeign {
    /***
     * 根据ID查询数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Spu findById(@PathVariable String id);
}
