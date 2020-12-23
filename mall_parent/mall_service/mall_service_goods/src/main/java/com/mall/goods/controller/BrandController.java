package com.mall.goods.controller;

import com.github.pagehelper.Page;
import com.mall.entity.PageResult;
import com.mall.entity.Result;
import com.mall.entity.StatusCode;
import com.mall.goods.service.BrandService;
import com.mall.pojo.Album;
import com.mall.pojo.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    //添加品牌
    @PostMapping("/add")
    public Result add(@RequestBody Brand brand){    //json转为对象
        brandService.add(brand);
        return new Result(true, StatusCode.OK, "添加成功");
    }

    //更新品牌
    @PutMapping("/update")
    public Result update(@RequestBody Brand brand){
        brandService.update(brand);
        return new Result(true, StatusCode.OK, "修改品牌(单个)成功");
    }

    //删除品牌
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id){
        brandService.delete(id);
        return new Result(true, StatusCode.OK, "删除品牌(单个)成功");
    }

    @GetMapping("/searchPage/{pageNo}/{pageSize}")
    public Result searchPage(@RequestParam Map<String, String> searchMap,@PathVariable Integer pageNo, @PathVariable Integer pageSize){
        Page page = brandService.searchPage(searchMap, pageNo, pageSize);
        PageResult pageResult = new PageResult(page.getTotal(), page.getResult());
        return new Result(true, StatusCode.OK, "分页查询成功", pageResult);
    }

}
