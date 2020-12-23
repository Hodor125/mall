package com.mall.goods.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mall.entity.Result;
import com.mall.entity.StatusCode;
import com.mall.goods.dao.AlbumMapper;
import com.mall.goods.dao.BrandMapper;
import com.mall.goods.service.AlbumService;
import com.mall.pojo.Album;
import com.netflix.discovery.converters.Auto;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author ：hodor007
 * @date ：Created in 2020/12/23
 * @description ：
 * @version: 1.0
 */
@Service
public class AlbumServiceImpl implements AlbumService {
    @Autowired
    private AlbumMapper albumMapper;


    @Override
    public List<Album> findAll() {
        return albumMapper.selectAll();
    }

    @Override
    public Album findById(Integer id) {
        return albumMapper.selectByPrimaryKey(id);
    }

    @Override
    public Integer add(Album album) {
        return albumMapper.insertSelective(album);
    }

    @Override
    public Integer delete(Integer id) {
        return albumMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Integer update(Album album) {
        return albumMapper.updateByPrimaryKey(album);
    }

    //泛型可以不写
    @Override
    public Page searchAlbum(String name, Integer pageNo, Integer pageSize) {
        //使用分页器进行分页
        PageHelper.startPage(pageNo, pageSize);

        //创建查询对象
        Example example = new Example(Album.class);
        Example.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(name)) {
            criteria.andLike("title", "%" + name + "%");
        }

        List<Album> albums = albumMapper.selectByExample(example);
        return (Page) albums;
    }


}
