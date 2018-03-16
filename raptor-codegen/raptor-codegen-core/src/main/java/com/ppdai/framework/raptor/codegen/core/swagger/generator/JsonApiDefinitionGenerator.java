package com.ppdai.framework.raptor.codegen.core.swagger.generator;

import com.google.common.collect.Lists;
import com.ppdai.framework.raptor.codegen.core.swagger.SwaggerGenConfiguration;
import com.ppdai.framework.raptor.codegen.core.swagger.container.MetaContainer;
import com.ppdai.framework.raptor.codegen.core.swagger.tool.ContainerUtil;
import com.ppdai.framework.raptor.codegen.core.swagger.tool.Proto2SwaggerJson;
import com.ppdai.framework.raptor.codegen.core.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by zhangyicong on 18-2-27.
 */
public class JsonApiDefinitionGenerator {

    public static final Logger LOGGER = LoggerFactory.getLogger(JsonApiDefinitionGenerator.class);

    private File outputDirectory;
    private File[] inputDirectories;
    private File protocDependenciesPath;
    private String extension;
    private String apiVersion = "version not set";
    private String swaggerVersion = "2.0";

    private List<File> allProtoFile = Lists.newArrayList();

    public JsonApiDefinitionGenerator(SwaggerGenConfiguration configuration) {
        checkNotNull(configuration.getOutputDirectory());
        this.outputDirectory = configuration.getOutputDirectory();

        checkNotNull(configuration.getInputDirectories());
        this.inputDirectories = configuration.getInputDirectories();

        checkNotNull(configuration.getProtocDependenciesPath());
        this.protocDependenciesPath = configuration.getProtocDependenciesPath();

        checkNotNull(configuration.getExtension());
        this.extension = configuration.getExtension();

        if (configuration.getApiVersion() != null) {
            this.apiVersion = configuration.getApiVersion();
        }

        if (configuration.getSwaggerVersion() != null) {
            this.swaggerVersion = configuration.getSwaggerVersion();
        }
    }

    public void generate() throws Exception {
        LOGGER.info(">>>>>>>>>>>>>>>  Started performing swagger generate   ");
        // TODO: 2018/3/16  这里可以优化
        MetaContainer metaContainer = ContainerUtil.getMetaContainer(inputDirectories, protocDependenciesPath);

        for (int i = 0; i < inputDirectories.length; i++) {
            File inputDirectory = inputDirectories[i];
            Utils.collectSpecificFiles(inputDirectory, extension, allProtoFile);

            Proto2SwaggerJson proto2Swagger = Proto2SwaggerJson.forConfig(inputDirectory.getAbsolutePath(),
                    outputDirectory.getAbsolutePath(),
                    protocDependenciesPath,
                    swaggerVersion,
                    apiVersion,
                    metaContainer);

            List<File> sortedFile = allProtoFile.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());

            //生成 proto 文件对应的 swagger json 文件
            for (File file : sortedFile) {
                if (file.exists()) {
                    String protoFilePath = file.getPath();
                    proto2Swagger.generateFile(protoFilePath);
                } else {
                    LOGGER.warn(file.getName() + " does not exist.");
                }
            }
        }
    }
}
