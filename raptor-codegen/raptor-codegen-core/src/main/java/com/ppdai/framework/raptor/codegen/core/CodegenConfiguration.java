package com.ppdai.framework.raptor.codegen.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by zhongyi on 2018/1/4.
 */

public class CodegenConfiguration {
    public static final Logger LOGGER = LoggerFactory.getLogger(CodegenConfiguration.class);
    private static final ObjectMapper mapper = new ObjectMapper();


    private String groupId;
    private String artifactId;
    private String artifactVersion;
    //TODO StringPath or File? Standardize.
    private String protocVersion;
    private File outputDirectory;
    private File[] inputDirectories;
    private File[] includeDirectories;
    private Boolean includeStdTypes;
    private String type;
    private String extension;
    private String pluginPath;
    private String pluginArtifact;
    private String protocCommand;
    private String protocArtifact;
    private MavenProject project;
    private ArtifactRepository localRepository;
    private List<ArtifactRepository> remoteRepositories;
    private ArtifactFactory artifactFactory;
    private ArtifactResolver artifactResolver;
    private File protocDependenciesPath;

    public static CodegenConfiguration fromFile(File configFile) {

        if (configFile.exists()) {
            try {
                return mapper.readValue(configFile, CodegenConfiguration.class);
            } catch (IOException e) {
                System.out.println("【WARN】The config.json will have to be neglected for being unable to recognize the parameters from it.");
            }
        }
        return null;
    }

    public void resolveConfigFile(CodegenConfiguration codegenConfiguration) {
        if(codegenConfiguration == null)
            return;

        if (codegenConfiguration.getInputDirectories() != null)
            this.inputDirectories = codegenConfiguration.getInputDirectories();
        if (codegenConfiguration.getOutputDirectory() != null)
            this.outputDirectory = codegenConfiguration.getOutputDirectory();
        if (codegenConfiguration.getGroupId() != null)
            this.groupId = codegenConfiguration.getGroupId();
        if (codegenConfiguration.getArtifactId() != null)
            this.artifactId = codegenConfiguration.getArtifactId();
        if (codegenConfiguration.getArtifactVersion() != null)
            this.artifactVersion = codegenConfiguration.getArtifactVersion();
    }
    //getter & setter

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getArtifactVersion() {
        return artifactVersion;
    }

    public void setArtifactVersion(String artifactVersion) {
        this.artifactVersion = artifactVersion;
    }

    public String getProtocVersion() {
        return protocVersion;
    }

    public void setProtocVersion(String protocVersion) {
        this.protocVersion = protocVersion;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getPluginPath() {
        return pluginPath;
    }

    public void setPluginPath(String pluginPath) {
        this.pluginPath = pluginPath;
    }

    public String getPluginArtifact() {
        return pluginArtifact;
    }

    public void setPluginArtifact(String pluginArtifact) {
        this.pluginArtifact = pluginArtifact;
    }

    public String getProtocCommand() {
        return protocCommand;
    }

    public void setProtocCommand(String protocCommand) {
        this.protocCommand = protocCommand;
    }

    public String getProtocArtifact() {
        return protocArtifact;
    }

    public void setProtocArtifact(String protocArtifact) {
        this.protocArtifact = protocArtifact;
    }

    public MavenProject getProject() {
        return project;
    }

    public void setProject(MavenProject project) {
        this.project = project;
    }

    public ArtifactRepository getLocalRepository() {
        return localRepository;
    }

    public void setLocalRepository(ArtifactRepository localRepository) {
        this.localRepository = localRepository;
    }

    public List<ArtifactRepository> getRemoteRepositories() {
        return remoteRepositories;
    }

    public void setRemoteRepositories(List<ArtifactRepository> remoteRepositories) {
        this.remoteRepositories = remoteRepositories;
    }

    public ArtifactFactory getArtifactFactory() {
        return artifactFactory;
    }

    public void setArtifactFactory(ArtifactFactory artifactFactory) {
        this.artifactFactory = artifactFactory;
    }

    public ArtifactResolver getArtifactResolver() {
        return artifactResolver;
    }

    public void setArtifactResolver(ArtifactResolver artifactResolver) {
        this.artifactResolver = artifactResolver;
    }

    public File getProtocDependenciesPath() {
        return protocDependenciesPath;
    }

    public void setProtocDependenciesPath(File protocDependenciesPath) {
        this.protocDependenciesPath = protocDependenciesPath;
    }

}
