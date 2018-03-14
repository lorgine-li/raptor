package com.ppdai.framework.raptor.spring.autoconfig;

import com.ppdai.framework.raptor.annotation.RaptorInterface;
import com.ppdai.framework.raptor.exception.RaptorServiceException;
import com.ppdai.framework.raptor.rpc.URL;
import com.ppdai.framework.raptor.service.Endpoint;
import com.ppdai.framework.raptor.service.Provider;
import com.ppdai.framework.raptor.service.ProviderBuilder;
import com.ppdai.framework.raptor.spring.annotation.RaptorService;
import com.ppdai.framework.raptor.spring.utils.AopHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Configuration
@Slf4j
public class RaptorServiceProcessor implements BeanPostProcessor {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ProviderBuilder providerBuilder;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object springBean, String beanName) throws BeansException {
        Object bean = springBean;
        if (AopUtils.isAopProxy(bean)) {
            try {
                bean = AopHelper.getTarget(bean);
            } catch (Exception e) {
                throw new RaptorServiceException(String.format("Can not find proxy target for %s .", bean.getClass().getName()));
            }
        }
        Annotation annotation = AnnotationUtils.findAnnotation(bean.getClass(), RaptorService.class);
        if (annotation == null) {
            return springBean;
        }
        List<Class<?>> interfaceClasses = findAllInterfaces(bean);
        if (interfaceClasses.size() == 0) {
            throw new RaptorServiceException(String.format("Can not find %s serviceImpl's interface.", bean.getClass().getName()));
        }
        for (Class<?> interfaceClass : interfaceClasses) {
            registryRaptorService(interfaceClass, springBean);
        }
        return springBean;
    }


    private List<Class<?>> findAllInterfaces(Object bean) {
        List<Class<?>> raptorInterfaces = new ArrayList<>();
        Set<Class<?>> interfaceClasses = ClassUtils.getAllInterfacesAsSet(bean);
        for (Class<?> interfaceClass : interfaceClasses) {
            Annotation annotation = AnnotationUtils.findAnnotation(interfaceClass, RaptorInterface.class);
            if (annotation != null) {
                raptorInterfaces.add(interfaceClass);
            }
        }
        return raptorInterfaces;
    }

    @SuppressWarnings("unchecked")
    private void registryRaptorService(Class interfaceClass, Object bean) {
        Provider provider = providerBuilder.build(interfaceClass, bean);
        Map<String, Endpoint> endpointMap = applicationContext.getBeansOfType(Endpoint.class);
        for (Endpoint endpoint : endpointMap.values()) {
            URL url = endpoint.export(provider);
            log.info("register a raptor service '{}' to endpoint '{}'", interfaceClass.getName(), url.getUri());
        }
    }
}
