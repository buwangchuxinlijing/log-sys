package com.log.collect.tools.proxy.cglib;


import com.log.collect.tools.log.LogAutoConfig;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author lij
 * @description: 日志收集实现-Cglib 代理
 * @date 2023/3/8 11:16
 */
public class LogCollectProxyOfCglib<T> implements MethodInterceptor {

    private T target;

    private Function<T,String> callerPrintFun;
    private Function<Object[],String> paramsPrintFun;
    private Function<Object,String> resultPrintFun;

    private Set<String> filterMethods= Collections.EMPTY_SET;

    /**
     *
     * @param t 目标对象
     * @param callerPrintFun 调用者打印方法
     * @param paramsPrintFun 实参列表打印方法
     * @param resultPrintFun 结果打印方法
     * @param methods 代理方法名称
     */
    public LogCollectProxyOfCglib(T t,
            Function<T,String> callerPrintFun,
            Function<Object[],String> paramsPrintFun,
            Function<Object,String> resultPrintFun ,
            String ...methods
                                  ){
        this.target=t;
        this.callerPrintFun=callerPrintFun;
        this.paramsPrintFun=paramsPrintFun;
        this.resultPrintFun=resultPrintFun;
        if (methods!=null){
            filterMethods = Arrays.stream(methods).collect(Collectors.toSet());
        }

    }

    public T creatProxy() {
        return this.creatProxy(true);
    }

    public T creatProxy(boolean isSupperClass) {
        Enhancer enhancer = new Enhancer();
        if (isSupperClass){
            enhancer.setSuperclass(target.getClass().getSuperclass());
        }else {
            enhancer.setSuperclass(target.getClass());
        }
        enhancer.setCallback(this);
        return (T) enhancer.create();
    }


    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        if (filterMethods.isEmpty()){
            //拦截所有方法
            return invoke(method, objects, methodProxy);
        }else if (filterMethods.contains(method.getName())){
            //选定拦截
            return invoke(method, objects, methodProxy);
        }else {
            return methodProxy.invoke(target, objects);
        }

    }

    private Object invoke(Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        String uuid= UUID.randomUUID().toString();
        executeBefore(method, objects, uuid);
        Object result;
        try {
            result= methodProxy.invoke(target, objects);
            executeAfter(method, uuid, result);
        }catch (Throwable throwable){
            executeException(method, uuid, throwable);
            throw throwable;
        }


        return result;
    }



    private void executeBefore(Method method, Object[] objects, String uuid) {
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
            sj.add(uuid);
            LogAutoConfig.COLLECT_LOGGER.info(sj.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void executeException(Method method, String uuid, Throwable throwable) {
        try {
            StringJoiner sj=new StringJoiner(" - ");
            //调用对象打印
            sj.add(this.callerPrintFun.apply(target));
            //调用方法打印
            sj.add(method.toString());
            //错误信息
            sj.add(throwable.getMessage());
            //方向
            sj.add("exception");
            //uuid
            sj.add(uuid);
            LogAutoConfig.COLLECT_LOGGER.info(sj.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void executeAfter(Method method, String uuid, Object result) {
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
    }
}
