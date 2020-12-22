package com.mall.goods.controller;

import com.mall.entity.Result;
import com.mall.entity.StatusCode;
import com.mall.goods.service.BrandService;
import com.mall.pojo.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author ：hodor007
 * @date ：Created in 2020/12/22
 * @description ：
 * @version: 1.0
 */
@RestController
@RequestMapping("/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    //Restful风格的接口不能允许所有的类型
//    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    @GetMapping("/findAll")    //和上面的效果相同
    public Result findAll(){
        List<Brand> brandList = brandService.findAll();
        return new Result(true, StatusCode.OK, "查询品牌成功", brandList);
    }

    @GetMapping("/{id}")
    public Result findById(@PathVariable Integer id){
        Brand brand = brandService.findById(id);
        return new Result(true, StatusCode.OK, "查询品牌成功", brand);
    }
}
