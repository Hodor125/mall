package com.mall.search.listener;

import com.mall.search.service.EsManagerService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 商品上架消费者监听器
 * @author ：hodor007
 * @date ：Created in 2020/12/30
 * @description ：
 * @version: 1.0
 */
@Component
@RabbitListener(queues = "search_add_queue")
public class GoodsUpListener {
    @Autowired
    private EsManagerService esManagerService;

    @RabbitHandler
    public void msgHandler(String spuId){
        esManagerService.importBySpuId(spuId);
    }
}
