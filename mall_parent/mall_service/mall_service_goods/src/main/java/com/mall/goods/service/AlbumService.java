package com.mall.goods.service;

import com.github.pagehelper.Page;
import com.mall.pojo.Album;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author ：XXXX
 * @date ：Created in 2020/12/23
 * @description ：
 * @version: 1.0
 */
public interface AlbumService {
    List<Album> findAll();

    Album findById(Integer id);

    Integer add(Album album);

    Integer delete(Integer id);

    Integer update(Album album);

    Page searchAlbum(String name, Integer pageNo, Integer pageSize);
}
