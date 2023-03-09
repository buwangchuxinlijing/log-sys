package com.cqlj.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.log.collect.tools.log.LogAutoConfig;
import com.log.collect.tools.proxy.cglib.LogCollectProxyOfCglib;
import com.log.collect.tools.proxy.cglib.print.JsonPrintOfJackson;
import lombok.SneakyThrows;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.xml.bind.DatatypeConverter;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 功能：
 *
 * @author lijing
 * @date 2020/5/13 14:55
 */
@Configuration
public class ConcurrentInterface {

    @Bean
    public ApplicationListener fun(){
        return new ApplicationListener<ApplicationStartedEvent>(){

            @Override
            public void onApplicationEvent(ApplicationStartedEvent event) {
                try {
                    ConcurrentInterface.send();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }


    //@PostConstruct
    public  static void send() throws Exception {
        String satUrl = "http://127.0.0.1:8095/api/test";
        HttpPost httpPost = new HttpPost(satUrl);

        String json="数据数据数据数据数据";
        StringEntity entity = new StringEntity(json, Consts.UTF_8);
        httpPost.setEntity(entity);
        CredentialsProvider credsProvider = new BasicCredentialsProvider();


        //创建目标对象
        CloseableHttpClient targetObj = HttpClients.custom().build();
        CloseableHttpResponse response = null;

        //指示日志打印工具如何打印调用者
        Function<CloseableHttpClient,String> callerFun=(call)->"CloseableHttpClient";
        //指示日志打印工具如何打印代理方法的实参列表
        Function<Object[],String> paramsFun=(parmas)->{
            HttpPost el = (HttpPost) parmas[0];
            //请求行
            String requestLine=el.getRequestLine().toString();
            //请求头
            Header [] headers=el.getAllHeaders();
            Map<String,String> headersMap=Stream.of(headers)
                    .collect(Collectors.toMap(Header::getName,Header::getValue, (u, v) -> u + ";" + v, HashMap::new));
            //请求体
            String body=null;
            try {
                body=EntityUtils.toString(el.getEntity());
            } catch (IOException e) {
                new RuntimeException(e);
            }
            Function<Object,String> objJson=JsonPrintOfJackson::print ;
            StringJoiner sj=new StringJoiner(" - ");
            sj.add(requestLine);
            sj.add( objJson.apply(headersMap));
            sj.add(body);
            return sj.toString();
        };
        //指示日志打印工具如何打印代理方法的结果
        Function<Object,String> resultFun=(result)->{
            CloseableHttpResponse el= (CloseableHttpResponse) result;
            //响应行
            String requestLine=el.getStatusLine().toString();
            //响应头
            Header [] headers=el.getAllHeaders();
            Map<String,String> headersMap=Stream.of(headers)
                    .collect(Collectors.toMap(Header::getName,Header::getValue, (u, v) -> u + ";" + v, HashMap::new));
            //响应体
            String body=null;
            try {
                body=EntityUtils.toString(el.getEntity(),Consts.UTF_8);
                //读取之后放回去否则二次读取有误
                el.setEntity(new ByteArrayEntity(body.getBytes(Consts.UTF_8)));
            } catch (IOException e) {
                new RuntimeException(e);
            }
            Function<Object,String> objJson=JsonPrintOfJackson::print ;
            StringJoiner sj=new StringJoiner(" - ");
            sj.add(requestLine);
            sj.add( objJson.apply(headersMap));
            sj.add(body);
            return sj.toString();
        };
        //将目标对象、目标对象打印方法、参数列表打印方法、结果打印方法传入构造器
        LogCollectProxyOfCglib<CloseableHttpClient> logCollectProxyOfCglib=new LogCollectProxyOfCglib<>(
                targetObj,
                callerFun,
                paramsFun,
                resultFun
        );
        CloseableHttpClient proxyObj = logCollectProxyOfCglib.creatProxy();
        response = proxyObj.execute(httpPost);
    }

    public static void paresResponse(HttpResponse response){
        String rs = "";
        try {
            System.out.println("响应行: \n" + response.getStatusLine().toString());
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("响应头: \n");
            Header[] headers=response.getAllHeaders();
            for (Header header:headers
            ) {
                System.out.println(header.getName()+':'+header.getValue());
            }
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            rs = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
            System.out.println("响应体：\n"+rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
