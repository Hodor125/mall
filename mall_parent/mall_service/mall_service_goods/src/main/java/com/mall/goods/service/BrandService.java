package com.mall.goods.service;

import com.mall.pojo.Brand;

import java.util.List;

/**
 * @author ：XXXX
 * @date ：Created in 2020/12/22
 * @description ：
 * @version: 1.0
 */
public interface BrandService {
    List<Brand> findAll();

    Brand findById(Integer id);
}
