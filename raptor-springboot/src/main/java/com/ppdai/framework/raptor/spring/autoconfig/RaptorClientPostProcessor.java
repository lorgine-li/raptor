package com.ppdai.framework.raptor.spring.autoconfig;

import com.ppdai.framework.raptor.spring.annotation.RaptorClient;
import com.ppdai.framework.raptor.spring.utils.SpringResourceUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;

import java.lang.reflect.Field;

public class RaptorClientPostProcessor implements BeanPostProcessor, ResourceLoaderAware {
    private ResourceLoader resourceLoader;
    @Autowired
    private RaptorClientRegistry raptorClientRegistry;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            try {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                RaptorClient reference = field.getAnnotation(RaptorClient.class);
                if (reference != null) {
                    if (field.get(bean) != null) {
                        //字段已经初始化
                        continue;
                    }
                    Object value = getClientProxy(field.getType(), reference);
                    if (value != null) {
                        field.set(bean, value);
                    } else {
                        throw new BeanInitializationException(String.format("Can not find Object %s.", bean.getClass().getName()));
                    }
                }
            } catch (Exception e) {
                throw new BeanInitializationException(String.format("Failed to init remote service reference at filed %s in class %s",
                        field.getName(), bean.getClass().getName()), e);
            }
        }
        return bean;
    }

    private Object getClientProxy(Class<?> referenceClass, RaptorClient reference) {
        String url = SpringResourceUtils.resolve(resourceLoader, reference.url());
        return raptorClientRegistry.getOrCreateClientProxy(referenceClass.getName(), url);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
