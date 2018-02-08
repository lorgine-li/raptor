package com.ppdai.framework.raptor.codegen.core.service2interface;

import com.google.common.collect.Lists;
import com.ppdai.framework.raptor.codegen.core.CodegenConfiguration;
import com.ppdai.framework.raptor.codegen.core.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

/**
 * Created by zhongyi on 2018/1/4.
 */
public class InterfaceGenerator {

    public static final Logger LOGGER = LoggerFactory.getLogger(InterfaceGenerator.class);

    private CodegenConfiguration codegenConfiguration;

    private File outputDirectory;
    private File[] inputDirectories;
    private File protocDependenciesPath;
    private String extension;

    private List<File> allProtoFile = Lists.newArrayList();

    public InterfaceGenerator codegenConfigure(CodegenConfiguration codegenConfiguration){
        this.codegenConfiguration = codegenConfiguration;
        return this;
    }

    private void setGeneratorProperties(){
        //todo
        if(codegenConfiguration !=null){
            if(codegenConfiguration.getOutputDirectory()!=null)
                this.outputDirectory = codegenConfiguration.getOutputDirectory();
            if(codegenConfiguration.getInputDirectories()!=null)
                this.inputDirectories = codegenConfiguration.getInputDirectories();
            if(codegenConfiguration.getProtocDependenciesPath()!=null)
                this.protocDependenciesPath = codegenConfiguration.getProtocDependenciesPath();
            if(codegenConfiguration.getExtension()!=null)
                this.extension = codegenConfiguration.getExtension();
        }
    }
    public void generate() throws Exception {
        setGeneratorProperties();
        performService2BeamInterface();
    }
    private void performService2BeamInterface() {
        LOGGER.info(">>>>>>>>>>>>>>>  Started performing SERVICEs -> INTERFACEs   ");
        for (int i = 0; i < inputDirectories.length; i++) {
            File inputDirectory = inputDirectories[i];
            Utils.collectSpecificFiles(inputDirectory,extension,allProtoFile);
            CommonProto2Java proto2ServicePojo = CommonProto2Java.forConfig(inputDirectory.getAbsolutePath(), outputDirectory.getAbsolutePath(), protocDependenciesPath);
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
