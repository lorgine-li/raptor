package com.ppdai.framework.raptor.codegen.core.service2interface;

import com.google.common.collect.Lists;
import com.ppdai.framework.raptor.codegen.core.CodegenConfiguration;
import com.ppdai.framework.raptor.codegen.core.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Objects;

/**
 * Created by zhongyi on 2018/1/4.
 */
public class InterfaceGenerator {

    public static final Logger LOGGER = LoggerFactory.getLogger(InterfaceGenerator.class);

    private File outputDirectory;
    private File[] inputDirectories;
    private File protocDependenciesPath;

    public InterfaceGenerator codegenConfigure(CodegenConfiguration codegenConfiguration){
        if(Objects.nonNull(codegenConfiguration)){
            this.outputDirectory = codegenConfiguration.getOutputDirectory();
            this.inputDirectories = codegenConfiguration.getInputDirectories();
            this.protocDependenciesPath = codegenConfiguration.getProtocDependenciesPath();
        }else{
            throw new NullPointerException("CodegenConfiguration is null");
        }
        return this;
    }

    public void generate() throws Exception {
        performService2RaptorInterface();
    }
    private void performService2RaptorInterface() {
        LOGGER.info(">>>>>>>>>>>>>>>  Started performing SERVICEs -> INTERFACEs   ");
        List<File> allProtoFile = Lists.newArrayList();
        for (File inputDirectory : inputDirectories) {
            Utils.collectSpecificFiles(inputDirectory, "proto", allProtoFile);
            CommonProto2Java proto2ServicePojo = CommonProto2Java.forConfig(inputDirectory.getAbsolutePath(),
                    outputDirectory.getAbsolutePath(), protocDependenciesPath);
            for (File file : allProtoFile) {
                if (file.exists()) {
                    String protoFilePath = file.getPath();
                    proto2ServicePojo.generateFile(protoFilePath);
                } else {
                    LOGGER.warn(file.getName() + " does not exist.");
                }
            }
        }
    }

}
