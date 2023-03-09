package com.log.collect.tools.filter.wrapper;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;


/**
 * @author lij
 * @description: request包装自动配置
 * @date 2023/2/22 9:23
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "log.collect", name = "read-request", havingValue = "true")
public class WrapperFilterAutoConfig {
    @Bean
    public FilterRegistrationBean<HttpWrapperFilter> httpWrapperFilterFilterRegistrationBean(){
        FilterRegistrationBean<HttpWrapperFilter> registrationBean=new FilterRegistrationBean<>();
        registrationBean.setName("custom-Name:HttpWrapperFilter");
        registrationBean.setOrder(Integer.MIN_VALUE);
        registrationBean.setFilter(new HttpWrapperFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }





}
