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

##### 3.1 如果需要用户信息请实现`LogUserInfoObtain`接口，并注入到spring容器中例如：

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

##### 3.2 日志框架已支持`Log4j2`、`Logback`，无需做配置即可根据项目环境适配

##### 3.3 如果需要收集某个方法的调用情况日志请使用代理类(LogCollectProxyOfCglib)

> 这里的必要配置是打开收集功能以及定义文件名称，其他不需要的配置可以删除
>
> log:
>   collect:
>     open: true
>     file-name: logs/interface-statistics.log

​	原理：使用代理模式，创建代理对象，在进行代理时使用定义的打印方法（函数式编程），将方法调用者、参数列表、方法返回进行打印。

​	例如我们这里代理CloseableHttpClient的execute()方法，从而实现对execute方法进行日志采集

1. 构造原始对象（目标对象）

   ```java
   CloseableHttpClient httpclient = HttpClients.custom().build();
   ```

2. 代理类创建

   * 将**目标对象**、**目标对象打印方法**、**参数列表打印方法**、**结果打印方法**，传入构造器

   ```java
   //将目标对象、目标对象打印方法、参数列表打印方法、结果打印方法传入构造器
   LogCollectProxyOfCglib<CloseableHttpClient> logCollectProxyOfCglib=new LogCollectProxyOfCglib<>(
           targetObj,
           callerFun,
           paramsFun,
           resultFun
   );
   ```

   * 三个打印方法

   ```java
   //1.指示日志打印工具如何打印调用者
   Function<CloseableHttpClient,String> callerFun=(call)->"CloseableHttpClient";
   //2.指示日志打印工具如何打印代理方法的实参列表
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
   //3.指示日志打印工具如何打印代理方法的结果
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
   ```

3. 创建代理对象,执行代理方法

   ```java
   CloseableHttpClient proxyObj = logCollectProxyOfCglib.creatProxy();
   proxyObj.execute(httpPost);
   ```



### 说明项

* 公司仓库配置（有两种配置方式）

  1. 在settings.xml中添加配置项：(xxxx为屏蔽信息)

     ```xml
        <!--公司私库信息添加在servers节点内-->
        <server>
               <id>releases</id>
               <username>xxxx</username>
               <password>xxxx</password>
        </server>
     ```

     ```xml
        <!--公司私库信息添加在mirrors节点内-->
        <mirror>  
           <id>releases</id>  
           <name>internal nexus repository</name>  
           <url>http://123.56.23.53:8081/nexus/content/repositories/releases/</url>
           <mirrorOf>*</mirrorOf>  
        </mirror>
     ```

  2. 在项目pom.xml中添加配置项：

     ```
     <repositories>
         <repository>
             <id>releases</id>
             <url>http://xxxx:xxxx@123.56.23.53:8081/nexus/content/repositories/releases/</url>
         </repository>
     </repositories>
     ```

* 项目地址：https://github.com/buwangchuxinlijing/log-sys