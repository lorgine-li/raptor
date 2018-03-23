package com.ppdai.framework.raptor.spring.utils;

import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.target.SingletonTargetSource;

import java.lang.reflect.Field;

public class AopHelper {
    public static Object getTarget(Object proxy) throws Exception {
        if (!AopUtils.isAopProxy(proxy)) {
            //不是代理对象
            return proxy;
        }

        Object target;

        if (AopUtils.isJdkDynamicProxy(proxy)) {
            target = getJdkDynamicProxyTargetObject(proxy);
        } else {
            //cglib
            target = getCglibProxyTargetObject(proxy);
        }

        if(target == null){
            //不是代理对象
            return proxy;
        }else{
            return getTarget(target);
        }
    }

    private static Object getCglibProxyTargetObject(Object proxy) throws Exception {
        Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
        h.setAccessible(true);
        Object dynamicAdvisedInterceptor = h.get(proxy);

        Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
        advised.setAccessible(true);
        TargetSource targetSource = ((AdvisedSupport) advised.get(dynamicAdvisedInterceptor)).getTargetSource();
        if (targetSource instanceof SingletonTargetSource) {
            return targetSource.getTarget();
        }
        return null;
    }


    private static Object getJdkDynamicProxyTargetObject(Object proxy) throws Exception {
        Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
        h.setAccessible(true);
        AopProxy aopProxy = (AopProxy) h.get(proxy);

        Field advised = aopProxy.getClass().getDeclaredField("advised");
        advised.setAccessible(true);

        TargetSource targetSource = ((AdvisedSupport) advised.get(aopProxy)).getTargetSource();

        if (targetSource instanceof SingletonTargetSource) {
            return targetSource.getTarget();
        }
        return null;
    }
}
