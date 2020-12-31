package com.mall.search.service;

/**
 * @author ：hodor007
 * @date ：Created in 2020/12/30
 * @description ：
 * @version: 1.0
 */
public interface EsManagerService {

    /**
     * 删除索引库和映射关系
     */
    void deleteIndexAndMapping();


    /**
     * 创建索引库和映射
     */
    void createIndexAndMapping();


    /**
     * 导入单条document
     */
    void importBySpuId(String spuId);

    /**
     * 导入全部document
     */
    void importAll();


    void removeBySpuId(String spuId);
}
