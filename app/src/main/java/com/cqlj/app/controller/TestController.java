package com.cqlj.app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author lij
 * @description: TODO
 * @date 2023/2/9 15:56
 */
@RestController
public class TestController {

    @PostMapping("/api/test")
    protected String test(String code, HttpServletRequest request){
        System.out.println("执行方法test");
        return "TEST hello world";
    }

//    @PostMapping("/api/test3")
//    public String test3(@Validated @RequestBody TestModel testModel){
//        if(true)
//            throw new RuntimeException("xx");
//        System.out.println("test3");
//        return "TEST hello world";
//    }


//http://localhost:8085/oauth2/authorize?client_id=messaging-client&response_type=code&scope=openid&redirect_uri=http://127.0.0.1:8085/authorized/okta
    @GetMapping("/authorized/okta")
    protected String okta(String code, HttpServletRequest request){
        return "hello world";
    }



}
