package com.ppdai.framework.raptor.codegen.cli;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class ProjectGeneratorCliTest {

    private static final String DEST_PATH = "./target/generated/demo";

    @Before
    public void before() throws IOException {
        deleteGeneratedProject();
    }

    private void deleteGeneratedProject() throws IOException {
        File file = new File(DEST_PATH);
        if(file.exists()){
            FileUtils.deleteDirectory(file);
        }
    }

    @Test
    public void test() throws Exception {
        String[] args = "-i ./src/test/resources -o ./target/generated  -artifactId demo".split(" ");
        ProjectGeneratorCli.main(args);
        String destPomPath = "./target/generated/demo/pom.xml";
        Assert.assertTrue(new File(destPomPath).exists());

    }
}
