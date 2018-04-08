package com.ppdai.framework.raptor.codegen.maven;

import junitx.framework.FileAssert;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Test;

import java.io.File;

/**
 * Created by zhangyicong on 18-2-27.
 */
public class Proto2Swagger2Test extends AbstractMojoTestCase {

    protected void setUp() throws Exception {
        // required for mojo lookups to work
        super.setUp();

    }

    @Test
    public void testProto2Swagger() throws Exception {
        //File testPom = new File( getBasedir(), "src/test/resources/Proto2Swagger-config.xml" );
        //Proto2Swagger mojo = (Proto2Swagger) lookupMojo ("proto2swagger", testPom );
        //assertNotNull( mojo );
        Proto2Swagger mojo = new Proto2Swagger();
        setVariableValueToObject(mojo, "swaggerVersion", "2.0");
        setVariableValueToObject(mojo, "inputDirectories", new File[] {new File("src/test/protobuf")} );
        setVariableValueToObject(mojo, "outputDirectory", new File( getBasedir(), "target/generated-sources" ));
        setVariableValueToObject(mojo, "includeStdTypes", false);
        setVariableValueToObject(mojo, "extension", ".proto");
        setVariableValueToObject(mojo, "protocDependenciesPath", new File( getBasedir(), "target/protoc-dependencies" ));
        mojo.execute();

        File hellowordSwaggerGen = new File(getBasedir(), "target/generated-sources/helloworld.proto.json");
        assertTrue(hellowordSwaggerGen.exists());

        File simpleSwaggerGen = new File(getBasedir(), "target/generated-sources/simple.proto.json");
        assertTrue(simpleSwaggerGen.exists());

        File hellowordSwaggerCorrect = new File(getBasedir(), "src/test/resources/helloworld.proto.json");
        assertTrue(hellowordSwaggerCorrect.exists());

        FileAssert.assertEquals(hellowordSwaggerCorrect, hellowordSwaggerGen);

        File simpleSwaggerCorrect = new File(getBasedir(), "src/test/resources/simple.proto.json");
        assertTrue(simpleSwaggerCorrect.exists());

        FileAssert.assertEquals(simpleSwaggerCorrect, simpleSwaggerGen);
    }

    @Test
    public void testImport() throws Exception{
        Proto2Swagger mojo = new Proto2Swagger();
        setVariableValueToObject(mojo, "swaggerVersion", "2.0");
        setVariableValueToObject(mojo, "inputDirectories", new File[] {new File("src/test/protobuf")} );
        setVariableValueToObject(mojo, "outputDirectory", new File( getBasedir(), "target/generated-sources" ));
        setVariableValueToObject(mojo, "includeStdTypes", false);
        setVariableValueToObject(mojo, "extension", ".proto");
        setVariableValueToObject(mojo, "protocDependenciesPath", new File( getBasedir(), "target/protoc-dependencies" ));
        mojo.execute();

        File importSwaggerGen = new File(getBasedir(), "target/generated-sources/Import.proto.json");
        assertTrue(importSwaggerGen.exists());

        File importedSwaggerGen = new File(getBasedir(), "target/generated-sources/Imported.proto.json");
        assertTrue(importedSwaggerGen.exists());

        File importSwaggerCorrect = new File(getBasedir(), "src/test/resources/Import.proto.json");
        assertTrue(importSwaggerCorrect.exists());

        FileAssert.assertEquals(importSwaggerCorrect, importSwaggerGen);

        File importedSwaggerCorrect = new File(getBasedir(), "src/test/resources/Imported.proto.json");
        assertTrue(importedSwaggerCorrect.exists());

        FileAssert.assertEquals(importedSwaggerCorrect, importedSwaggerGen);
    }

    @Test
    public void testNestMessage() throws Exception{
        Proto2Swagger mojo = new Proto2Swagger();
        setVariableValueToObject(mojo, "swaggerVersion", "2.0");
        setVariableValueToObject(mojo, "inputDirectories", new File[] {new File("src/test/protobuf")} );
        setVariableValueToObject(mojo, "outputDirectory", new File( getBasedir(), "target/generated-sources" ));
        setVariableValueToObject(mojo, "includeStdTypes", false);
        setVariableValueToObject(mojo, "extension", ".proto");
        setVariableValueToObject(mojo, "protocDependenciesPath", new File( getBasedir(), "target/protoc-dependencies" ));
        mojo.execute();

        File nestSwaggerGen = new File(getBasedir(), "target/generated-sources/AccountApi.proto.json");
        assertTrue(nestSwaggerGen.exists());

        File correctSwaggerCorrect = new File(getBasedir(), "src/test/resources/AccountApi.proto.json");
        assertTrue(correctSwaggerCorrect.exists());

        FileAssert.assertEquals(correctSwaggerCorrect, nestSwaggerGen);


    }
}
