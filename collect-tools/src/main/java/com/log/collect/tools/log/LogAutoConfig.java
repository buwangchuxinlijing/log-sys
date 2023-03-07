package com.log.collect.tools.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.log.collect.tools.configure.LogConfigProperties;
import com.log.collect.tools.filter.log.LogFilterTypeAutoConfig;
import com.log.collect.tools.log.log4j.Log4jConfiguration;
import com.log.collect.tools.log.logback.LogbakConfiguration;
import com.log.collect.tools.spring.SpringBeanContext;
import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;


/**
 * @author lij
 * @description: 如果一个被@Bean注解标记的方法的返回类型是void，它通常用于配置一些不需要返回对象实例的组件，例如：配置类中的初始化方法或者生命周期回调方法等。
 * @date 2023/3/2 15:22
 */
@AutoConfiguration(after = {LogFilterTypeAutoConfig.class})
@Import(value = {SpringBeanContext.class, LogConfigProperties.class})
@ConditionalOnProperty(prefix = "log.collect", name = "open", havingValue = "true")
public class LogAutoConfig {

    public static Logger COLLECT_LOGGER;
    @Bean
    @ConditionalOnClass(name = "org.apache.logging.log4j.core.LoggerContext")
    protected Object log4j(){
        COLLECT_LOGGER= Log4jConfiguration.Collect_LOGGER;
        return null;
    }

    @Bean
    @ConditionalOnClass(name = "ch.qos.logback.classic.LoggerContext")
    protected Object logback(){
        COLLECT_LOGGER= LogbakConfiguration.Collect_LOGGER;
        return null;
    }




}
