package com.ppdai.framework.raptor.codegen.core.swagger.tool;

import com.google.protobuf.DescriptorProtos;
import com.ppdai.framework.raptor.codegen.core.swagger.container.MetaContainer;
import com.ppdai.framework.raptor.codegen.core.swagger.template.Swagger2Template;
import com.ppdai.framework.raptor.codegen.core.swagger.template.Swagger3Template;
import com.ppdai.framework.raptor.codegen.core.swagger.template.SwaggerTemplate;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by zhangyicong on 18-2-27.
 */
public class Proto2SwaggerJson {

    private static final Logger logger = LoggerFactory.getLogger(Proto2SwaggerJson.class);

    private final String swaggerVersion;

    private final String apiVersion;

    private final String discoveryRoot;

    private final String generatePath;

    private final CommandProtoc commandProtoc;

    private final SwaggerTemplate swaggerTemplate;

    private final MetaContainer metaContainer = new MetaContainer();

    private Proto2SwaggerJson(String discoveryRoot,
                              String generatePath,
                              final File protocDependenciesPath,
                              String swaggerVersion,
                              String apiVersion) {

        this.discoveryRoot = discoveryRoot;
        this.generatePath = generatePath;
        this.commandProtoc =
                CommandProtoc.configProtoPath(discoveryRoot, protocDependenciesPath);
        this.swaggerVersion = swaggerVersion;
        this.apiVersion = apiVersion;

        if (this.swaggerVersion.startsWith("3")) {
            swaggerTemplate = new Swagger3Template();
        } else {
            swaggerTemplate = new Swagger2Template();
        }
    }

    public static Proto2SwaggerJson forConfig(String discoveryRoot,
                                              String generatePath,
                                              final File protocDependenciesPath,
                                              String swaggerVersion,
                                              String apiVersion) {
        return new Proto2SwaggerJson(discoveryRoot,
                generatePath, protocDependenciesPath, swaggerVersion, apiVersion);
    }

    public void generateFile(String protoPath) throws IOException {
        logger.info("    Processing : " + protoPath);

        if (!new File(protoPath).exists()) {
            logger.warn("protoPath:" + protoPath + " not exist, it may be in the third party jars.");
            return;
        }

        DescriptorProtos.FileDescriptorSet fileDescriptorSet = commandProtoc.invoke(protoPath);

        for (DescriptorProtos.FileDescriptorProto fdp : fileDescriptorSet.getFileList()) {
            //No service has been defined.
            if (fdp.getServiceCount() != 0) {
                try {
                    String json = swaggerTemplate.applyTemplate(fdp, metaContainer, apiVersion);
                    File apiFile = new File(generatePath + File.separatorChar + fdp.getName() + ".json");
                    if (apiFile.exists()) {
                        apiFile.delete();
                    }
                    FileUtils.writeStringToFile(apiFile, json);

                    //logger.info("Swagger API: {}", mapper.writeValueAsString(swaggerObject));
                    logger.info("Generate Swagger API file: {}", apiFile);
                } catch (Exception e) {
                    throw e;
                }
            }
        }
    }

    public void scanFile(String protoPath) {
        logger.info("    Scan File : " + protoPath);

        if (!new File(protoPath).exists()) {
            logger.warn("protoPath:" + protoPath + " not exist, it may be in the third party jars.");
            return;
        }

        DescriptorProtos.FileDescriptorSet fileDescriptorSet = commandProtoc.invoke(protoPath);

        for (DescriptorProtos.FileDescriptorProto fdp : fileDescriptorSet.getFileList()) {
            ContainerUtil.getEnums(fdp, metaContainer);
            ContainerUtil.getMessages(fdp, metaContainer);
        }


    }
}
