package com.ppdai.framework.raptor.spring.aop;

import java.lang.annotation.*;

/**
 * @author yinzuolong
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface AopAnnotation1 {
}
