package com.ppdai.framework.raptor.codegen.maven;

import com.ppdai.framework.raptor.codegen.core.swagger.DefaultSwaggerGen;
import com.ppdai.framework.raptor.codegen.core.swagger.SwaggerGenConfiguration;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Created by zhangyicong on 18-2-27.
 */
@Mojo(name = "proto2swagger", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class Proto2Swagger extends AbstractMojo {

    private static final String DEFAULT_INPUT_DIR = "/src/main/proto/".replace('/', File.separatorChar);

    @Parameter(property = "protocVersion")
    private String protocVersion;

    /**
     * Input directories that have *.proto files (or the configured extension).
     * If none specified then <b>src/main/protobuf</b> is used.
     *
     * @parameter property="inputDirectories"
     */
    @Parameter(
            property = "inputDirectories"
    )
    private File[] inputDirectories;

    /**
     * This parameter lets you specify additional include paths to protoc.
     */
    @Parameter(property = "includeDirectories")
    private File[] includeDirectories;

    /**
     * If "true", extract the included google.protobuf standard types and add them to protoc import path.
     */
    @Parameter(
            property = "includeStdTypes",
            defaultValue = "false"
    )
    private Boolean includeStdTypes;

    /**
     * Output directory for the generated java files. Defaults to
     * "${project.build.directory}/generated-sources" or
     * "${project.build.directory}/generated-test-sources"
     * depending on the addSources parameter
     * <p>
     * Ignored when {@code <outputTargets>} is given
     *
     * @parameter property="outputDirectory"
     */
    @Parameter(
            property = "outputDirectory",
            defaultValue = "${project.build.directory}/generated-sources"
    )
    private File outputDirectory;

    /**
     * Default extension for protobuf files
     */
    @Parameter(
            property = "extension",
            defaultValue = ".proto"
    )
    private String extension;

    @Parameter(
            required = true,
            defaultValue = "${project.build.directory}/protoc-dependencies"
    )
    private File protocDependenciesPath;

    @Parameter(
            property = "project",
            required = true,
            readonly = true
    )
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        log("Generating a Swagger API definition for each *.proto file.");

        SwaggerGenConfiguration configuration = getConfiguration();

        try {
            new DefaultSwaggerGen()
                    .setCodegenConfiguration(configuration)
                    .generate();

            log("Succeed in generating a swagger for each *.proto file.");

        } catch (Exception e) {
            getLog().error(e);
            throw new MojoExecutionException(
                    "Swagger generation failed. See above for the full exception.");
        }
    }

    private SwaggerGenConfiguration getConfiguration() {
        SwaggerGenConfiguration configuration = new SwaggerGenConfiguration();

        if (isNotEmpty(protocVersion))
            configuration.setProtocVersion(protocVersion);

        if (outputDirectory != null)
            configuration.setOutputDirectory(outputDirectory);

        if (inputDirectories != null) {
            if (inputDirectories.length == 0) {
                File inputDir = new File(project.getBasedir().getAbsolutePath() + DEFAULT_INPUT_DIR);
                inputDirectories = new File[]{inputDir};
            }
            configuration.setInputDirectories(inputDirectories);
        }

        if (includeDirectories != null)
            configuration.setIncludeDirectories(includeDirectories);

        if (includeStdTypes != null)
            configuration.setIncludeStdTypes(includeStdTypes);

        if (isNotEmpty(extension))
            configuration.setExtension(extension);

        if (protocDependenciesPath != null)
            configuration.setProtocDependenciesPath(protocDependenciesPath);

        return configuration;
    }

    private void log(String log) {
        getLog().info("------------------------------------------------------------------------");
        getLog().info("    " + log + "  ");
        getLog().info("------------------------------------------------------------------------");
    }
}
