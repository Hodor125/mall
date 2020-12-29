package com.mall.listener;

import okhttp3.*;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author ：hodor007
 * @date ：Created in 2020/12/29
 * @description ：
 * @version: 1.0
 */
@Component
@RabbitListener(queues = "ad_update_queue")
public class BussinessListener {

    @RabbitHandler
    public void msgHandler(String position){    //mq中读取的数据
        //1 拼接大广告预热更新的url
        String url = "http://192.168.200.128/ad_update?position=" + position;
        //2 构建请求对象
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        //3 执行请求
        Call call = okHttpClient.newCall(request);
        //4 处理请求结果
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("大广告预热更新失败" + position);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("大广告预热更新成功" + position);
            }
        });
    }
}
