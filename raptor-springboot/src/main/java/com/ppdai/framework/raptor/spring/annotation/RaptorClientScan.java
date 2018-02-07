package com.ppdai.framework.raptor.spring.annotation;

import com.ppdai.framework.raptor.spring.autoconfig.RaptorClientConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({RaptorClientConfiguration.class})
public @interface RaptorClientScan {

    Class<?>[] basePackageClasses() default {};

    String[] basePackages() default {};
}
