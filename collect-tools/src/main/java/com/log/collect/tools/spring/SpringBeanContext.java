package com.log.collect.tools.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * @author lij
 * @description: TODO
 * @date 2023/3/2 15:53
 */

public class SpringBeanContext implements BeanFactoryAware {

    private static BeanFactory BEAN_FACTORY;

    public static <T> T getBean(Class<T> tClass){
        return BEAN_FACTORY.getBean(tClass);
    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        BEAN_FACTORY=beanFactory;
    }
}
