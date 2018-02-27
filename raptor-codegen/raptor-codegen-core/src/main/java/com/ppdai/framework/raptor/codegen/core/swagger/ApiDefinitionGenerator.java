package com.ppdai.framework.raptor.codegen.core.swagger;

import com.google.common.collect.Lists;
import com.ppdai.framework.raptor.codegen.core.service2interface.CommandProtoc;
import com.ppdai.framework.raptor.codegen.core.service2interface.InterfaceGenerator;
import com.ppdai.framework.raptor.codegen.core.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by zhangyicong on 18-2-27.
 */
public class ApiDefinitionGenerator {

    public static final Logger LOGGER = LoggerFactory.getLogger(ApiDefinitionGenerator.class);

    private File outputDirectory;
    private File[] inputDirectories;
    private File protocDependenciesPath;
    private String extension;

    private List<File> allProtoFile = Lists.newArrayList();

    public ApiDefinitionGenerator(SwaggerGenConfiguration configuration) {
        checkNotNull(configuration.getOutputDirectory());
        this.outputDirectory = configuration.getOutputDirectory();

        checkNotNull(configuration.getInputDirectories());
        this.inputDirectories = configuration.getInputDirectories();

        checkNotNull(configuration.getProtocDependenciesPath());
        this.protocDependenciesPath = configuration.getProtocDependenciesPath();

        checkNotNull(configuration.getExtension());
        this.extension = configuration.getExtension();

    }

    public void generate() throws Exception {
        LOGGER.info(">>>>>>>>>>>>>>>  Started performing swagger generate   ");
        for (int i = 0; i < inputDirectories.length; i++) {
            File inputDirectory = inputDirectories[i];
            Utils.collectSpecificFiles(inputDirectory,extension,allProtoFile);
            CommonProto2Swagger proto2Swagger = CommonProto2Swagger.forConfig(inputDirectory.getAbsolutePath(), outputDirectory.getAbsolutePath(), protocDependenciesPath);
            for (File file : allProtoFile) {
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
