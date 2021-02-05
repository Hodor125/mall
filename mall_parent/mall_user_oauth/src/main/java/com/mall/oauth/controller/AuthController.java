package com.mall.oauth.controller;

import com.mall.entity.Result;
import com.mall.entity.StatusCode;
import com.mall.oauth.service.AuthService;
import com.mall.oauth.util.AuthToken;
import com.mall.oauth.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;

/**
 * @author ：hodor007
 * @date ：Created in 2021/2/5
 * @description ：
 * @version: 1.0
 */
@Controller
@RequestMapping("/oauth")
public class AuthController {
    @Autowired
    private AuthService authService;
    @Value("${auth.clientId}")
    private String clientId;
    @Value("${auth.clientSecret}")
    private String clientSecret;
    @Value("${auth.cookieDomain}")
    private String cookieDomain;
    @Value("${auth.cookieMaxAge}")
    private Integer cookieMaxAge;

    @GetMapping("/toLogin")
    public String toLogin(@RequestParam(value = "ReturnUrl", required = false, defaultValue = "http://192.168.200.128/") String ReturnUrl, Model model) {
        model.addAttribute("ReturnUrl", ReturnUrl);
        return "login2";
    }

    /**
     * 登录
     * @param username
     * @param password
     * @return
     */
    @PostMapping("/login")
    public String login(@RequestParam("username") String username, @RequestParam("password") String password,
                        @RequestParam(value = "ReturnUrl", required = false, defaultValue = "http://192.168.200.128/") String ReturnUrl) {
        try {
            AuthToken authToken = authService.applyToken(clientId, clientSecret, username, password);
            saveTtiToCookie(authToken.getJti());
            return "redirect:" + ReturnUrl;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:" + ReturnUrl;
    }

    /**
     * 测试登录
     * @param username
     * @param password
     * @return
     */
    @PostMapping("/interface/login")
    @ResponseBody
    public Result interfaceLogin(@RequestParam("username") String username, @RequestParam("password") String password) {
        try {
            AuthToken authToken = authService.applyToken(clientId, clientSecret, username, password);
            saveTtiToCookie(authToken.getJti());
            return new Result(true, StatusCode.OK, "登录成功", authToken);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, StatusCode.LOGINERROR, "登录失败");
        }
    }

    private void saveTtiToCookie(String jti) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = servletRequestAttributes.getResponse();
        //存放在跟路径下，命名为uid
        CookieUtil.addCookie(response, cookieDomain, "/", "uid", jti, cookieMaxAge, false);
    }

}
