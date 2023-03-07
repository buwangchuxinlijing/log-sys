package com.log.collect.tools.filter.log;

import com.log.collect.tools.user.LogUserInfoObtain;
import com.log.collect.tools.filter.wrapper.WrapperFilterAutoConfig;
import com.log.collect.tools.configure.LogConfigProperties;
import com.log.collect.tools.user.UserModel;
import com.log.collect.tools.user.UserRoleInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import javax.servlet.ServletRequest;
import java.util.List;


/**
 * @author lij
 * @description: TODO
 * @date 2023/3/2 9:07
 */
@AutoConfiguration(after = WrapperFilterAutoConfig.class)
@Import(LogConfigProperties.class)
@ConditionalOnProperty(prefix = "log.collect", name = "open", havingValue = "true")
public class LogFilterTypeAutoConfig {
  /*  @Autowired(required = false)
    LogUserInfoObtain logUserInfoObtain;*/

    @Bean
    @ConditionalOnMissingBean(LogUserInfoObtain.class)
    public LogUserInfoObtain logUserInfoObtain() {
        return new LogUserInfoObtain<UserModel<UserRoleInfo>>(){
            @Override
            public UserModel<UserRoleInfo> obtain(ServletRequest request) {
                return null;
            }
        };
    }


    @Bean
    @ConditionalOnProperty(prefix = "log.collect", name = "type", havingValue = "filter")
    public FilterRegistrationBean<LogFilter> logFilterFilterRegistrationBean(LogConfigProperties logConfigProperties,
                                                                             LogUserInfoObtain logUserInfoObtain){
        FilterRegistrationBean<LogFilter> registrationBean=new FilterRegistrationBean<>();
        registrationBean.setName("custom-Name:LogFilet");
        registrationBean.setOrder(Integer.MIN_VALUE+1);
        registrationBean.setFilter(new LogFilter(logUserInfoObtain));
        final List<String> urls = logConfigProperties.getUrls();
        registrationBean.addUrlPatterns(urls.toArray(new String[urls.size()]));
        return registrationBean;
    }
}
