package com.mall.order.service;

/**
 * @author ：hodor007
 * @date ：Created in 2021/2/5
 * @description ：
 * @version: 1.0
 */
public interface CartService {
    /**
     * 添加商品到购物车中
     * @param username 用户
     * @param skuId 商品ID
     * @param num 商品数量
     */
    void add(String username, String skuId, Integer num);
}
