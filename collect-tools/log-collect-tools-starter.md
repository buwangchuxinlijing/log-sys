### 1. 添加项目依赖

  在项目pom中引入如下依赖

 ```xml
<dependency>
  <groupId>com.log</groupId>
  <artifactId>collect-tools-starter</artifactId>
  <version>0.0.1</version>
</dependency>
 ```

   Maven仓库为公司私库，私库配置见说明项。

### 2.添加配置到application文件

```yaml
log:
  collect:
    open: true
    file-name: logs/interface-statistics.log
    type: filter
    read-request: true
    urls:
      - /api/*
      - /test/*
```

- ​	`open：`是否打开日志采集功能
- ​	`file-name`: 日志文件路径
- ​	`type:` 实现方式，目前仅支持filter
- ​	`read-request`: 是否读取请求，type=filter必为`true`
- ​	`urls:` 需要采集的`url`路径 ，支持*任意匹配

### 3. 扩展功能

 * 如果需要用户信息请实现`LogUserInfoObtain`接口，并注入到spring容器中例如：

   ```java
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
   ```

​       如果你需要更多的用户信息可以通过继承`UserModel`实现

* 日志框架已支持`Log4j2`、`Logback`，无需做配置即可根据项目环境适配



### 说明项

1. 公司仓库配置

   在settings.xml中添加配置项：

   ```xml
   <!--公司私库信息添加在servers节点内-->
   <server>
          <id>releases</id>
          <username>deploy</username>
          <password>deploy123</password>
   </server>
   ```

   ```xml
   <!--公司私库信息添加在mirrors节点内-->
   <mirror>  
      <id>releases</id>  
      <name>internal nexus repository</name>  
      <url>http://123.56.23.53:8081/nexus/content/groups/public/</url>
      <mirrorOf>*</mirrorOf>  
   </mirror>
   ```
   
   ---
   部署：mvn deploy:deploy-file -DgroupId=com.log -DartifactId=collect-tools-starter -Dversion=0.0.1 -Dpackaging=jar -Dfile=D:\workingspace\log-sys\collect-tools\build\libs\collect-tools-starter-0.0.1.jar -Durl=http://deploy:deploy123@123.56.23.53:8081/nexus/content/repositories/releases/ 