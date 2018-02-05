package com.ppdai.framework.raptor.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RaptorClient {

    String urlKey() default "";
}
