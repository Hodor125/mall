package com.mall.goods.service.impl;

import com.mall.goods.dao.BrandMapper;
import com.mall.goods.service.BrandService;
import com.mall.pojo.Brand;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ：hodor007
 * @date ：Created in 2020/12/22
 * @description ：
 * @version: 1.0
 */
@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private BrandMapper brandMapper;

    @Override
    public List<Brand> findAll() {
        return brandMapper.selectAll();
    }

    @Override
    public Brand findById(Integer id) {
        return brandMapper.selectByPrimaryKey(id);
    }
}
