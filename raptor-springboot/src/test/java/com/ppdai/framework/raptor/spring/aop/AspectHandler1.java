package com.ppdai.framework.raptor.spring.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @author yinzuolong
 */
@Aspect
@Slf4j
public class AspectHandler1 {

    @Pointcut("@annotation(com.ppdai.framework.raptor.spring.aop.AopAnnotation1)")
    public void aopMethod1() {
    }

    @Around("aopMethod1()")
    public Object raptorMethodWarp1(ProceedingJoinPoint pjp) throws Throwable {
        log.info("aopMethod1");
        return pjp.proceed();
    }

}
