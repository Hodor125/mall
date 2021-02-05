package com.mall.oauth;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class CreateJwtTest {
//    @Test
    public void generate() {
        String pwd = new BCryptPasswordEncoder().encode("123456");
        System.out.println(pwd);
    }
}
