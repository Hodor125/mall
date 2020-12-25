package com.mall.goods.dao;

import com.mall.pojo.Brand;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends Mapper<Brand> {

    //根据分类查询品牌
    @Select("SELECT * FROM tb_brand where id in (SELECT brand_id FROM tb_category_brand where category_id in (SELECT id FROM tb_category WHERE name=#{cateName}))")
    List<Brand> findBrandByCateName(@Param("cateName") String cateName);
}
