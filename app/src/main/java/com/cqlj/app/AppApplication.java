package com.cqlj.app;

import com.log.collect.tools.user.LogUserInfoObtain;
import com.log.collect.tools.user.UserModel;
import com.log.collect.tools.user.UserRoleInfo;
import com.log.collect.tools.log.LogAutoConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;

import javax.servlet.ServletRequest;

/**
 * @author lij
 * @description: TODO
 * @date 2023/3/2 16:17
 */
@SpringBootApplication
public class AppApplication {
    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class,args);
    }

    @Bean
    public ApplicationListener fun(){
        return new ApplicationListener<ApplicationStartedEvent>(){

            @Override
            public void onApplicationEvent(ApplicationStartedEvent event) {
                System.out.println(LogAutoConfig.COLLECT_LOGGER);
            }
        };
    }
    @Bean
    public LogUserInfoObtain LogUserInfoObtainBean(){
        return new LogUserInfoObtain<UserModel<UserRoleInfo>>(){

            @Override
            public UserModel<UserRoleInfo> obtain(ServletRequest request) {
                UserModel<UserRoleInfo> user=new UserModel<>();
                user.setUserName("test22");
                user.setNickname("测试用户");
                UserRoleInfo userRoleInfo=new UserRoleInfo();
                userRoleInfo.setCode("ADMIN");
                userRoleInfo.setName("管理员");
                user.setRoleModel(userRoleInfo);
                return user;
            }
        };
    }
}
