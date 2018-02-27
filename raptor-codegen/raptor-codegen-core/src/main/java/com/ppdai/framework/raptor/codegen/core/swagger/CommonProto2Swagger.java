package com.ppdai.framework.raptor.codegen.core.swagger;

import com.google.common.collect.Maps;
import com.google.protobuf.DescriptorProtos;
import com.ppdai.framework.raptor.codegen.core.service2interface.CommandProtoc;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;

/**
 * Created by zhangyicong on 18-2-27.
 */
public class CommonProto2Swagger {

    private static final Logger logger = LoggerFactory.getLogger(CommonProto2Swagger.class);

    private final String discoveryRoot;

    private final String generatePath;

    private final CommandProtoc commandProtoc;

    private final SwaggerTemplate swaggerTemplate = new SwaggerTemplate();

    private CommonProto2Swagger(String discoveryRoot, String generatePath, final File protocDependenciesPath) {
        this.discoveryRoot = discoveryRoot;
        this.generatePath = generatePath;
        this.commandProtoc =
                CommandProtoc.configProtoPath(discoveryRoot, protocDependenciesPath);
    }

    public static CommonProto2Swagger forConfig(String discoveryRoot, String generatePath,
                                             final File protocDependenciesPath) {
        return new CommonProto2Swagger(discoveryRoot, generatePath, protocDependenciesPath);
    }

    public void generateFile(String protoPath) {
        logger.info("    Processing : " + protoPath);

        if (!new File(protoPath).exists()) {
            logger.warn("protoPath:" + protoPath + " not exist, it may be in the third party jars, so it can't be generate.java");
            return;
        }

        DescriptorProtos.FileDescriptorSet fileDescriptorSet = commandProtoc.invoke(protoPath);

        for (DescriptorProtos.FileDescriptorProto fdp : fileDescriptorSet.getFileList()) {
            //No service has been defined.
            if (fdp.getServiceCount() == 0) {
                logger.info(fdp.getName() + " seems to has no Service defined.");
                continue;
            }

            swaggerTemplate.applyTemplate(fdp);
        }
    }
}
