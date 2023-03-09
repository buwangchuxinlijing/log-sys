package com.log.collect.tools.proxy.cglib;


import com.log.collect.tools.log.LogAutoConfig;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.function.Function;

/**
 * @author lij
 * @description: TODO
 * @date 2023/3/8 11:16
 */
public class LogCollectProxyOfCglib<T> implements MethodInterceptor {

    private T target;

    private Function<T,String> callerPrintFun;
    private Function<Object[],String> paramsPrintFun;
    private Function<Object,String> resultPrintFun;


    public LogCollectProxyOfCglib(T t,
            Function<T,String> callerPrintFun,
            Function<Object[],String> paramsPrintFun,
            Function<Object,String> resultPrintFun ){
        this.target=t;
        this.callerPrintFun=callerPrintFun;

        this.paramsPrintFun=paramsPrintFun;
        this.resultPrintFun=resultPrintFun;
    }

    public T creatProxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(target.getClass().getSuperclass());
        enhancer.setCallback(this);
        return (T) enhancer.create();
    }


    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        String uuid= null;
        try {
            StringJoiner sj=new StringJoiner(" - ");
            //调用对象打印
            sj.add(this.callerPrintFun.apply(target));
            //调用方法打印
            sj.add(method.toString());
            //调用参数打印：
            sj.add(this.paramsPrintFun.apply(objects));
            //方向
            sj.add("before");
            //uuid
            uuid= UUID.randomUUID().toString();
            sj.add(uuid);
            LogAutoConfig.COLLECT_LOGGER.info(sj.toString());
        }catch (Exception e){
            e.printStackTrace();
        }

        Object result= methodProxy.invoke(target, objects);

        try {

            StringJoiner sj=new StringJoiner(" - ");
            //调用对象打印
            sj.add(this.callerPrintFun.apply(target));
            //调用方法打印
            sj.add(method.toString());
            if (!"void".equals(method.getReturnType().getName())){
                //返回对象打印
                sj.add(this.resultPrintFun.apply(result));
            }else {
                sj.add("method return type is void");
            }
            //方向
            sj.add("after");
            //uuid
            sj.add(uuid);
            LogAutoConfig.COLLECT_LOGGER.info(sj.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
