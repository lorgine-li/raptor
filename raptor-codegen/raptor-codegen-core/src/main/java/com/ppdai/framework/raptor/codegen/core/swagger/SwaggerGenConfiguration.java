package com.ppdai.framework.raptor.codegen.core.swagger;

import java.io.File;

/**
 * Created by zhangyicong on 18-2-27.
 */
public class SwaggerGenConfiguration {

    private String swaggerVersion;
    private String protocVersion;
    private File[] inputDirectories;
    private File[] includeDirectories;
    private Boolean includeStdTypes;
    private String extension;
    private File protocDependenciesPath;
    private File outputDirectory;
    private String apiVersion;

    public String getSwaggerVersion() {
        return swaggerVersion;
    }

    public void setSwaggerVersion(String swaggerVersion) {
        this.swaggerVersion = swaggerVersion;
    }

    public String getProtocVersion() {
        return protocVersion;
    }

    public void setProtocVersion(String protocVersion) {
        this.protocVersion = protocVersion;
    }

    public File[] getInputDirectories() {
        return inputDirectories;
    }

    public void setInputDirectories(File[] inputDirectories) {
        this.inputDirectories = inputDirectories;
    }

    public File[] getIncludeDirectories() {
        return includeDirectories;
    }

    public void setIncludeDirectories(File[] includeDirectories) {
        this.includeDirectories = includeDirectories;
    }

    public Boolean getIncludeStdTypes() {
        return includeStdTypes;
    }

    public void setIncludeStdTypes(Boolean includeStdTypes) {
        this.includeStdTypes = includeStdTypes;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public File getProtocDependenciesPath() {
        return protocDependenciesPath;
    }

    public void setProtocDependenciesPath(File protocDependenciesPath) {
        this.protocDependenciesPath = protocDependenciesPath;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }
}
