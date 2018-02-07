package com.ppdai.framework.raptor.spring.annotation;

import com.ppdai.framework.raptor.spring.autoconfig.RaptorServiceConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@Import(RaptorServiceConfiguration.class)
public @interface RaptorService {
}
