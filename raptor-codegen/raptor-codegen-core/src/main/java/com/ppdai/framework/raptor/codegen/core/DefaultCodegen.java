package com.ppdai.framework.raptor.codegen.core;

import com.ppdai.framework.raptor.codegen.core.message2pojos.PojosGenerator;
import com.ppdai.framework.raptor.codegen.core.service2interface.InterfaceGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The default codegen used for maven-plugin.
 * Created by zhongyi on 2018/1/4.
 */
public class DefaultCodegen {
    public static final Logger logger = LoggerFactory.getLogger(DefaultCodegen.class);

    private CodegenConfiguration codegenConfiguration;

    public DefaultCodegen setCodegenConfiguration(CodegenConfiguration codegenConfiguration) {
        this.codegenConfiguration = codegenConfiguration;
        return this;
    }

    public void generate() throws Exception {
        logger.info(">>>>>>>>>>>>>>>  DefaultCodegen Starts to work.");
        try {

            //  Message -> Pojo
            new PojosGenerator()
                    .codegenConfigure(codegenConfiguration)
                    .generate();
            //  Service -> Interface
            new InterfaceGenerator()
                    .codegenConfigure(codegenConfiguration)
                    .generate();

        } catch (Exception e) {
            logger.error("Error happens for DefaultCodegen", e);
        }
    }

}
