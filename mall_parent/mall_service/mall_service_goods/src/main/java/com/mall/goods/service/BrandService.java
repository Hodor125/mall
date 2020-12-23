package com.mall.goods.service;

import com.github.pagehelper.Page;
import com.mall.entity.Result;
import com.mall.pojo.Brand;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * @author ：XXXX
 * @date ：Created in 2020/12/22
 * @description ：
 * @version: 1.0
 */
public interface BrandService {
    List<Brand> findAll();

    Brand findById(Integer id);

    Integer add(Brand brand);

    Integer update(Brand brand);

    Integer delete(Integer id);

    Page searchPage(Map<String, String> searchMap, Integer pageNo, Integer pageSize);
}
