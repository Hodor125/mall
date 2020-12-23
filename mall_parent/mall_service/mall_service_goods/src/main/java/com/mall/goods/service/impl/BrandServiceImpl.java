package com.mall.goods.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mall.entity.Result;
import com.mall.goods.dao.BrandMapper;
import com.mall.goods.service.BrandService;
import com.mall.pojo.Brand;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

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

    /**
     * insertSelective：会判断brand的参数是否为空，为空的属性不参与拼接sql
     * 举例：加入brand的参数的name和image两个属性有值，letter和seq两个字段为空，那么拼接的结果为
     * insert into tb_brand (name,image) values (name的值,value的值)
     *
     * insert：不判断brand的参数是否未空，属性全部参与拼接sql
     * 举例：加入brand的参数的name和image两个属性有值，letter和seq两个字段为空，那么拼接的结果为
     * insert into tb_brand (name,image,letter,seq) values (name的值,value的值,letter的值,seq的值)
     *
     * @param brand
     * @return
     */
    @Override
    public Integer add(Brand brand) {
        return brandMapper.insertSelective(brand);
    }

    /**
     * insertSelective：会判断brand的参数是否为空，为空的属性不参与拼接sql
     * 举例：加入brand的参数的name和image两个属性有值，letter和seq两个字段为空，那么拼接的结果为
     * update tb_brand set id=id的值,name=name的值,image=image的值 where id=id的值
     *
     * insert：不判断brand的参数是否未空，属性全部参与拼接sql
     * 举例：加入brand的参数的name和image两个属性有值，letter和seq两个字段为空，那么拼接的结果为
     * update tb_brand set id=id的值,name=name的值,image=image的值,letter=letter的值,seq=seq的值 where id=id的值
     * @param brand
     * @return
     */
    @Override
    public Integer update(Brand brand) {
        return brandMapper.updateByPrimaryKeySelective(brand);
    }

    @Override
    public Integer delete(Integer id) {
        return brandMapper.deleteByPrimaryKey(id);
    }

    //分页和搜索
    @Override
    public Page searchPage(Map<String, String> searchMap, Integer pageNo, Integer pageSize) {

        //使用分页器进行分页
        PageHelper.startPage(pageNo, pageSize);

        //构建查询对象
        Example example = new Example(Brand.class);
        //必须配合selectCountByExample使用 相当于执行 select count(name) from tb_brand
//        example.setCountProperty("name");
        //相当于select * from tb_brand oeder by name desc
//        example.setOrderByClause(" name desc ");
        //相当于select distinct(name) from tb_brand
//        example.setDistinct(true);

        //查询条件封装到集合里面
        Example.Criteria criteria = example.createCriteria();

        if(searchMap != null && searchMap.size() > 0){
            if(!StringUtils.isEmpty(searchMap.get("name"))){
                criteria.andLike("name","%"+searchMap.get("name")+"%");
            }
            if(!StringUtils.isEmpty(searchMap.get("letter"))){
                criteria.andEqualTo("letter",searchMap.get("letter"));
            }
        }

        List<Brand> brands = brandMapper.selectByExample(example);

        return (Page) brands;
    }
}
