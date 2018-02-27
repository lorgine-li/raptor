package com.ppdai.framework.raptor.codegen.core.swagger;

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

        try {

            new ApiDefinitionGenerator(configuration)
                    .generate();

        } catch (Exception e) {
            logger.error("Error happens for DefaultSwaggerGen", e);
        }
    }
}
