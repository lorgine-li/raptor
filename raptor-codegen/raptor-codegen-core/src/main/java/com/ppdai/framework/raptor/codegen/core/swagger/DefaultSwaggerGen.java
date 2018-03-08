package com.ppdai.framework.raptor.codegen.core.swagger;

import com.ppdai.framework.raptor.codegen.core.swagger.generator.JsonApiDefinitionGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangyicong on 18-2-27.
 */
public class DefaultSwaggerGen {

    public static final Logger logger = LoggerFactory.getLogger(DefaultSwaggerGen.class);

    private SwaggerGenConfiguration configuration;

    public DefaultSwaggerGen setCodegenConfiguration(SwaggerGenConfiguration configuration) {
        this.configuration = configuration;
        return this;
    }

    public void generate() throws Exception {
        logger.info(">>>>>>>>>>>>>>>  DefaultSwaggerGen Starts to work.");

        new JsonApiDefinitionGenerator(configuration)
                .generate();
    }
}
