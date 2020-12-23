package com.mall.goods.controller;

import com.mall.entity.Result;
import com.mall.entity.StatusCode;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author ：hodor007
 * @date ：Created in 2020/12/23
 * @description ：
 * @version: 1.0
 */
@ControllerAdvice
public class BaseExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Result exceptionHandler(Exception e){
        e.printStackTrace();

        return new Result(false, StatusCode.ERROR, "系统正忙");
    }
}
