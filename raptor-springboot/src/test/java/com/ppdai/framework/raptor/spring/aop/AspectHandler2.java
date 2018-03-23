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
public class AspectHandler2 {

    @Pointcut("@annotation(com.ppdai.framework.raptor.spring.aop.AopAnnotation2)")
    public void aopMethod2() {
    }

    @Around("aopMethod2()")
    public Object raptorMethodWarp2(ProceedingJoinPoint pjp) throws Throwable {
        log.info("aopMethod2");
        return pjp.proceed();
    }

}
