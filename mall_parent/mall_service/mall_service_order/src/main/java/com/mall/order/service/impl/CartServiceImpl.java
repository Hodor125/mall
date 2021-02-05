package com.mall.order.service.impl;

import com.mall.goods.feign.SkuFeign;
import com.mall.goods.feign.SpuFeign;
import com.mall.goods.pojo.Sku;
import com.mall.goods.pojo.Spu;
import com.mall.order.pojo.OrderItem;
import com.mall.order.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * key 用户名
 * value
 *   key skuId
 *   value 商品条目
 * @author ：hodor007
 * @date ：Created in 2021/2/5
 * @description ：
 * @version: 1.0
 */
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SkuFeign skuFeign;
    @Autowired
    private SpuFeign spuFeign;

    //使用redis中的map类型，value是map
    @Override
    public void add(String username, String skuId, Integer num) {
        //从redis缓存中查找当前用户的购物车的该商品
        OrderItem orderItem = (OrderItem) redisTemplate.boundHashOps("cart_" + username).get(skuId);
        if(orderItem != null) {
            orderItem.setNum(orderItem.getNum() + num);
            if(orderItem.getNum() < 0){  //计算购物车中商品的数量，前端传入的商品数量为负数
                redisTemplate.boundHashOps("cart_" + username).delete(skuId);
                return;
            }
            orderItem.setMoney(orderItem.getPrice() * orderItem.getNum());
            orderItem.setPayMoney(orderItem.getPrice() * orderItem.getNum());
        }

        //如果缓存中不存在该商品则添加该商品
        OrderItem orderItem1 = buildOrderItem(skuId, num);
        //将数据更新到redis缓存中
        redisTemplate.boundHashOps("cart_" + username).put(skuId, orderItem1);
    }

    private OrderItem buildOrderItem(String skuId, Integer num) {
        OrderItem orderItem = new OrderItem();
        Sku sku = skuFeign.findById(skuId);
        if(sku != null) {
            Spu spu = spuFeign.findById(sku.getSpuId());
            if(spu != null) {
                orderItem.setName(sku.getName());
                orderItem.setSkuId(skuId);
                orderItem.setSpuId(sku.getSpuId());
                orderItem.setNum(num);
                orderItem.setPrice(sku.getPrice());
                orderItem.setImage(sku.getImage());
                orderItem.setMoney(sku.getPrice() * num);
                orderItem.setPayMoney(sku.getPrice() * num);
                orderItem.setWeight(sku.getWeight());

                orderItem.setCategoryId1(spu.getCategory1Id());
                orderItem.setCategoryId2(spu.getCategory2Id());
                orderItem.setCategoryId3(spu.getCategory3Id());
            }
        }
        return orderItem;
    }
}
