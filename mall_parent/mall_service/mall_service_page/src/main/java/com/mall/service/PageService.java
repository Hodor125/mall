package com.mall.service;

import java.util.Map;

/**
 * @author ：XXXX
 * @date ：Created in 2021/1/3
 * @description ：
 * @version: 1.0
 */
public interface PageService {
    /**
     * 准备模板页面所需要的数据
     * @param spuId
     * @return
     */
    Map buildPageData(String spuId);

    /**
     * 基于模板页面生成静态页面
     * @param spuId
     */
    void createPageHtml(String spuId);
}
