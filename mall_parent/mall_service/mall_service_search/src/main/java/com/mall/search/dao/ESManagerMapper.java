package com.mall.search.dao;

import com.mall.search.pojo.SkuInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Long 指的是es索引库的id
 */
public interface ESManagerMapper extends ElasticsearchRepository<SkuInfo,Long> {

}
