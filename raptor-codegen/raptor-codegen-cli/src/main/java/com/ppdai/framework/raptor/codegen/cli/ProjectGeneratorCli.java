package com.ppdai.framework.raptor.codegen.cli;

import com.ppdai.framework.raptor.codegen.core.CodegenConfiguration;
import com.ppdai.framework.raptor.codegen.core.ProjectGenerator;


/**
 * This is the cli tool for generating a maven project.
 *
 * The ‘codegen’ can be executed by using the maven-plugin
 * "raptor-codegen:proto2java".
 *
 * Created by zhongyi on 2018/1/17.
 */
public class ProjectGeneratorCli {

    private static CliOptions codegenCliOptions = new CliOptions();

    public static void main(String[] args) {
        try {
            CodegenConfiguration codegenConfiguration = codegenCliOptions.getCofig(args);
            new ProjectGenerator()
                    .codegenConfigure(codegenConfiguration)
                    .generate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
