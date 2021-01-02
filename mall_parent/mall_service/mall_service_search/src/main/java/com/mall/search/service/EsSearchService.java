package com.mall.search.service;

import java.util.Map;

/**
 * @author ：XXXX
 * @date ：Created in 2020/12/31
 * @description ：
 * @version: 1.0
 */
public interface EsSearchService {

    Map search(Map<String, String> searchMap);
}
