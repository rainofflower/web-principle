package com.yanghui.study.ratio;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.aop.SpringProxy;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yanghui
 * @date 2019-4-28
 */
@Component
public class RatioBeanPostProcessor implements InstantiationAwareBeanPostProcessor {

    private final AutowireCapableBeanFactory autowireBeanFactory;

    RatioBeanPostProcessor(AutowireCapableBeanFactory autowireBeanFactory){
        this.autowireBeanFactory = autowireBeanFactory;
    }

    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        boolean flag = false;
        Method[] methods = beanClass.getDeclaredMethods();
        for (Method method : methods) {
            if(method.isAnnotationPresent(EnableRatio.class)){
                flag = true;
                break;
            }
        }
        if(flag){
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(beanClass);
            enhancer.setInterfaces(new Class[]{SpringProxy.class});
            //设置增强
            enhancer.setCallback((MethodInterceptor) (target, method, args, methodProxy) -> {
                if (method.isAnnotationPresent(EnableRatio.class)) {
                    Object result1 = methodProxy.invokeSuper(target, args);
                    JSONObject param = JSON.parseObject(args[0].toString());
                    param.put("pageIndex",2);
                    Object result2 = methodProxy.invokeSuper(target, new Object[]{param});
                    return result1.toString() + result2;
                }
                return methodProxy.invokeSuper(target, args);
            });
            return this.postProcess(enhancer.create());
        }
        return null;
    }

    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        return true;
    }

    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {
        return pvs;
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public <T> T postProcess(T object) {
        if (object == null) {
            return null;
        }
        T result;
        try {
            //使用容器autowireBeanFactory标准依赖注入方法autowireBean()处理 object对象的依赖注入
            this.autowireBeanFactory.autowireBean(object);
            //使用容器autowireBeanFactory标准初始化方法initializeBean()初始化对象 object
            result = (T) this.autowireBeanFactory.initializeBean(object, object.toString());
        } catch (RuntimeException e) {
            throw new RuntimeException(
                    "初始化 " + object.getClass() + " 对象失败 ", e);
        }
        return result;
    }
}
