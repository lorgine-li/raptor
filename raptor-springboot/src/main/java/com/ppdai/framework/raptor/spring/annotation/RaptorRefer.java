package com.ppdai.framework.raptor.spring.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RaptorRefer {

    String urlKey() default "";
}
