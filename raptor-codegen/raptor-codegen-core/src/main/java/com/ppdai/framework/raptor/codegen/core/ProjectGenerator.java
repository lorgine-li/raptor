package com.ppdai.framework.raptor.codegen.core;

import com.google.common.collect.Lists;
import com.ppdai.framework.raptor.codegen.core.common.RaptorCodegenInfo;
import com.ppdai.framework.raptor.codegen.core.constant.ProtobufConstant;
import com.ppdai.framework.raptor.codegen.core.utils.mustache.MustacheProcessor;
import com.ppdai.framework.raptor.codegen.core.utils.Utils;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * Generate a standard maven project which we can use in IDE
 * Two main things should be done for this.
 * <p>
 * Created by zhongyi on 2018/1/17.
 */
public class ProjectGenerator {

    private static final String PROJECT_PROTO_PATH = "/src/main/proto".replace('/', File.separatorChar);
    private static final String PROJECT_SRC_PATH = "/src/main/java".replace('/', File.separatorChar);

    private CodegenConfiguration codegenConfiguration;
    private File[] inputDirectories;
    private File outputDirectory;
    private String groupId;
    private String artifactId;
    private String artifactVersion;

    List<File> allProtoFiles = Lists.newArrayList();

    public ProjectGenerator codegenConfigure(CodegenConfiguration codegenConfiguration) {
        this.codegenConfiguration = codegenConfiguration;
        return this;
    }

    public void generate() throws Exception {
        setGeneratorProperties();
        performProjectGenerator();
    }

    public void setGeneratorProperties() throws Exception {
        if (codegenConfiguration != null) {
            try {
                if (codegenConfiguration.getInputDirectories() != null) {
                    inputDirectories = codegenConfiguration.getInputDirectories();
                }
                if (codegenConfiguration.getOutputDirectory() != null) {
                    outputDirectory = codegenConfiguration.getOutputDirectory();
                }
                if (codegenConfiguration.getGroupId() != null) {
                    groupId = codegenConfiguration.getGroupId();
                }
                if (codegenConfiguration.getArtifactId() != null) {
                    artifactId = codegenConfiguration.getArtifactId();
                }
                if (codegenConfiguration.getArtifactVersion() != null) {
                    artifactVersion = codegenConfiguration.getArtifactVersion();
                }
            } catch (Exception e) {
                System.out.println(e.toString());
                throw new Exception("Exception happens at ProjectGenerator—setGeneratorProperties.");
            }
        }
    }

    private void performProjectGenerator() throws Exception {
        System.out.println(">>>>>>>>>>>>>>>  Adding Pom to the maven project generated.");

        HashMap<String, Object> mustacheParameters = new HashMap<>();
        mustacheParameters.put("groupId", groupId);
        mustacheParameters.put("artifactId", artifactId);
        mustacheParameters.put("artifactVersion", artifactVersion);
        mustacheParameters.put("raptorVersion", RaptorCodegenInfo.getInstance().getVersion());

        try {
            outputDirectory.mkdirs();
            MustacheProcessor.process("pom.mustache",
                    new File(outputDirectory, "pom.xml").getAbsolutePath(),
                    mustacheParameters);
        } catch (Exception e) {
            System.out.println(e.toString());
            throw new Exception("Exception happens at ProjectGenerator—ProjectGenerator.");
        }

        System.out.println(">>>>>>>>>>>>>>>  Copying proto files to the project");
        for (int i = 0; i < inputDirectories.length; i++) {
            Utils.collectSpecificFiles(inputDirectories[i], ProtobufConstant.PROTO_SUFFIX, allProtoFiles);
        }
        System.out.println("Number of protos :" + allProtoFiles.size());

        //Generate the src/main/java
        new File(outputDirectory, PROJECT_SRC_PATH).mkdirs();

        File desProtoPath = new File(outputDirectory, PROJECT_PROTO_PATH);
        desProtoPath.mkdirs();
        try {
            for (File tempProtoFile : allProtoFiles) {
                Utils.copyFile(tempProtoFile.getAbsolutePath()
                        , desProtoPath.getAbsolutePath() + File.separatorChar + tempProtoFile.getName());
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            throw new Exception("Exception at performProjectGenerator while copying protos to destination.", e);
        }

        System.out.println("--------------------------------------------------------");
        System.out.println("                  Generate Success                      ");
        System.out.println("--------------------------------------------------------");
    }


}
