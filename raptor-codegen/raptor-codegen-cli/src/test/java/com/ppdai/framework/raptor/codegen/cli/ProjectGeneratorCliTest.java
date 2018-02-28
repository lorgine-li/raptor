package com.ppdai.framework.raptor.codegen.cli;

import org.junit.Test;

public class ProjectGeneratorCliTest {

    @Test
    public void test() throws Exception {
        String[] args = "-i ./src/test/resources -o ./target/generated  -artifactId demo".split(" ");
        ProjectGeneratorCli.main(args);
    }
}
