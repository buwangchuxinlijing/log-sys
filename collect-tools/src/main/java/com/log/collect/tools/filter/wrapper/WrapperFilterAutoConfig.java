package com.log.collect.tools.filter.wrapper;

import com.log.collect.tools.configure.LogConfigProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import java.util.List;


/**
 * @author lij
 * @description: request包装自动配置
 * @date 2023/2/22 9:23
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "log.collect", name = "read-request", havingValue = "true")
public class WrapperFilterAutoConfig {
    @Bean
    public FilterRegistrationBean<HttpWrapperFilter> httpWrapperFilterFilterRegistrationBean(LogConfigProperties logConfigProperties){
        FilterRegistrationBean<HttpWrapperFilter> registrationBean=new FilterRegistrationBean<>();
        registrationBean.setName("custom-Name:HttpWrapperFilter");
        registrationBean.setOrder(Integer.MIN_VALUE);
        registrationBean.setFilter(new HttpWrapperFilter());
        final List<String> urls = logConfigProperties.getUrls();
        registrationBean.addUrlPatterns(urls.toArray(new String[urls.size()]));
        return registrationBean;
    }





}
