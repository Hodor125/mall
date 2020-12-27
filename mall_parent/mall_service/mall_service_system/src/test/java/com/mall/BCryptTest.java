package com.mall;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * Spring Security自带了，不用下载源码
 * @author ：hodor007
 * @date ：Created in 2020/12/26
 * @description ：
 * @version: 1.0
 */
public class BCryptTest {
    /**
     * Bcrypt加密测试
     *
     * hashpw = $2a$10$J9FhRCpD/Yioj5lRvMfVKOnfCP15gYJjaLxHbio1l9D0Fb9YKkmKm
     * hashpw = $2a$10$Je6nBJoZ5.Aqgf6vmW6AX.1jdu3I/wFhzgvXNbhkqIXZbe1QkeLpi
     * hashpw = $2a$10$HqO0Tm3i.UVlemnqFZxFXexW55Yxrssw6PGc7k1xXOH32QR.A8c3e
     * hashpw = $2a$10$DGYXjbHTO1zkM2OtuE8n0OMDEYktBUIacfzVr3cnH.W/SlhN4kbFS
     * hashpw = $2a$10$51.D0XuFdhlApzazPXiJEuoVtMCXfzzCVFDPDd.p0TRjvpxMUtzxO
     * hashpw = $2a$10$AmRkubpfl2Yoj5X7.ZbHXuMs29ItSRnz2sxh3ZZ9rls7KsROnIKnu
     * hashpw = $2a$10$hQw1wMI1LizkLBfG5ZyoAespUJ/KuT/AbR0YRIIKlK/8pblTJWiRS
     * hashpw = $2a$10$28Wl8sdkEmSyErr4hh7D5O4Ifbh8LXV5jBgHSLy9P9l8J00PdKMi2
     * hashpw = $2a$10$66NyEluxCKRw5YVZGr/SfO9casDGCjAJBu0je6I9z0hX5wHr2aw3u
     * hashpw = $2a$10$BXs8fCQTrIp7YRrwr1AseennruRx/UGTT.P6u6DZ6EXC1WOn3Dt4i
     */
//    @Test
    public void testEncrypt(){
        for (int i = 0; i < 10; i++) {
            String hashpw = BCrypt.hashpw("ahucom", BCrypt.gensalt());
            System.out.println("hashpw = " + hashpw);
        }
    }

//    @Test
    public void check(){
        /**
         * 参数一：明文
         * 参数二：密文
         */
        boolean checkpw = BCrypt.checkpw("ahucom", "$2a$10$BXs8fCQTrIp7YRrwr1AseennruRx/UGTT.P6u6DZ6EXC1WOn3Dt4i");
        System.out.println(checkpw);
    }
}
