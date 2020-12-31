package com.mall.search.listener;

import com.mall.search.service.EsManagerService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 商品下架消费者监听器
 * @author ：hodor007
 * @date ：Created in 2020/12/30
 * @description ：
 * @version: 1.0
 */
@Component
@RabbitListener(queues = "search_del_queue")
public class GoodsDownListener {
    @Autowired
    private EsManagerService esManagerService;

    @RabbitHandler
    public void msgHandler(String spuId){
        esManagerService.removeBySpuId(spuId);
    }
}
