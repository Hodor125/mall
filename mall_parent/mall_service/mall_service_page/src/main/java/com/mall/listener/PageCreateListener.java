package com.mall.listener;

import com.mall.service.PageService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author ：hodor007
 * @date ：Created in 2021/1/4
 * @description ：
 * @version: 1.0
 */
@Component
@RabbitListener(queues = "page_create_queue")
public class PageCreateListener {
    @Autowired
    private PageService pageService;

    @RabbitHandler
    public void msgHandler(String spuId){
        pageService.createPageHtml(spuId);
    }
}
