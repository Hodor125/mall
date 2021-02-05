package com.mall;

import io.jsonwebtoken.*;
import org.junit.Test;

import java.util.Date;
import java.util.UUID;

/**
 * JWT是一个非常轻巧的规范。这个规范允许我们使用JWT在用户和服务器之间传递安全可靠的信息
 * HS256是对称加密算法
 * @author ：hodor007
 * @date ：Created in 2020/12/26
 * @description ：
 * @version: 1.0
 */
public class JWTTest {
    //生成jwt
//    @Test
    public void createJWT(){
        String secret = "ahucom";
        //设置动态的载荷生成的内容才会有所不同
        //生成的jwt包含三部分 头部 载荷 头部和载荷的组合
        String jwt = Jwts.builder().signWith(SignatureAlgorithm.HS256, secret)    //设置头部和算法签名
                .setId(UUID.randomUUID().toString())    //设置jwt的唯一id标识，载荷的内容    动态载荷
                .setSubject("黑色五叶草")    //设置jwt的主题
                .setIssuedAt(new Date())    //设置jwt的系统时间    动态载荷
                .compact();
        System.out.println("jwt = " + jwt);
    }

    //设置过期事件
//    @Test
    public void createJWTExpire(){
        String secret = "ahucom";
        //设置动态的载荷生成的内容才会有所不同
        //生成的jwt包含三部分 头部 载荷 头部和载荷的组合
        String jwt = Jwts.builder().signWith(SignatureAlgorithm.HS256, secret)    //设置头部和算法签名
                .setId(UUID.randomUUID().toString())    //设置jwt的唯一id标识，载荷的内容    动态载荷
                .setSubject("黑色五叶草")    //设置jwt的主题
                .setIssuedAt(new Date())    //设置jwt的系统时间    动态载荷
                .setExpiration(new Date(System.currentTimeMillis() + 360000))    //设置过期时间5min
                .compact();
        System.out.println("jwt = " + jwt);
    }

    //设置自定义的属性claim
//    @Test
    public void createJWTClaim(){
        String secret = "ahucom";
        //设置动态的载荷生成的内容才会有所不同
        //生成的jwt包含三部分 头部 载荷 头部和载荷的组合
        String jwt = Jwts.builder().signWith(SignatureAlgorithm.HS256, secret)    //设置头部和算法签名
                .setId(UUID.randomUUID().toString())    //设置jwt的唯一id标识，载荷的内容    动态载荷    即jti
                .setSubject("黑色五叶草")    //设置jwt的主题
                .setIssuedAt(new Date())    //设置jwt的系统时间    动态载荷
                .claim("username","zhangsan")    //设置自定义属性 用户名
                .claim("age",23)    //设置自定义属性 用户年龄
                .compact();
        System.out.println("jwt = " + jwt);
    }


    //解析jwt，签名是加密后的结果，解析出来也是看不懂的
//    @Test
    public void parseJwt(){
        String secret = "ahucom";
        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJhNGRkZTI5Yy1jM2RmLTQ3ZjctOTQxOS1kYjI0ODliOGQ4MjUiLCJzdWIiOiLpu" +
                "5HoibLkupTlj7bojYkiLCJpYXQiOjE2MTIxOTIzOTQsInVzZXJuYW1lIjoiemhhbmdzYW4iLCJhZ2UiOjIzfQ.Jgb1h4_IFF8K" +
                "FTf1d2Pdb0NNx5lIzlnxV0t5RJIHmPg";
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secret).parseClaimsJws(jwt);
        JwsHeader header = claimsJws.getHeader();
        Claims body = claimsJws.getBody();
        String signature = claimsJws.getSignature();
        System.out.println("header = " + header);    //解析的头
        System.out.println("body = " + body);    //解析的载荷
        System.out.println("signature = " + signature);    //解析的签名
    }

    //设置自定义属性
}
