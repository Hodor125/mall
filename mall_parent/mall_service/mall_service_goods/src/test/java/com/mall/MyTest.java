package com.mall;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author ：hodor007
 * @date ：Created in 2020/12/24
 * @description ：
 * @version: 1.0
 */
public class MyTest {

//    @Test
    public void test(){
        try {
            String result = URLEncoder.encode("手机", "UTF-8");
            System.out.println(result);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
