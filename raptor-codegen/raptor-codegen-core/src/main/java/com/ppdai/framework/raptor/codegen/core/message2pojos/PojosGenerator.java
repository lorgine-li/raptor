package com.ppdai.framework.raptor.codegen.core.message2pojos;


import com.ppdai.framework.raptor.codegen.core.CodegenConfiguration;
import com.ppdai.framework.raptor.codegen.core.utils.protocjar.PlatformDetector;
import com.ppdai.framework.raptor.codegen.core.utils.protocjar.Protoc;
import com.ppdai.framework.raptor.codegen.core.utils.protocjar.ProtocVersion;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by zhongyi on 2018/1/4.
 */
public class PojosGenerator {

    public static final Logger LOGGER = LoggerFactory.getLogger(PojosGenerator.class);

    private static final String DEFAULT_INPUT_DIR = "/src/main/proto/".replace('/', File.separatorChar);
    private static final String PROJECT_SRC_PATH = "/src/main/java".replace('/', File.separatorChar);

    private CodegenConfiguration codegenConfiguration;

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

    private File tempRoot;

    public PojosGenerator codegenConfigure(CodegenConfiguration codegenConfiguration) {
        this.codegenConfiguration = codegenConfiguration;
        return this;
    }

    public void generate() throws Exception {
        setGeneratorProperties();
        performMessage2POJOs();
    }

    public void setGeneratorProperties() throws Exception{
        if (codegenConfiguration != null) {
            try{
                setProtocVersion(codegenConfiguration.getProtocVersion());
                setOutputDirectory(codegenConfiguration.getOutputDirectory());
                setInputDirectories(codegenConfiguration.getInputDirectories());
                setIncludeDirectories(codegenConfiguration.getIncludeDirectories());
                setIncludeStdTypes(codegenConfiguration.getIncludeStdTypes());
                setType(codegenConfiguration.getType());
                setExtension(codegenConfiguration.getExtension());
                setPluginPath(codegenConfiguration.getPluginPath());
                setPluginArtifact(codegenConfiguration.getPluginArtifact());
                setProtocCommand(codegenConfiguration.getProtocCommand());
                setProtocArtifact(codegenConfiguration.getProtocArtifact());
                setProject(codegenConfiguration.getProject());
                setLocalRepository(codegenConfiguration.getLocalRepository());
                setRemoteRepositories(codegenConfiguration.getRemoteRepositories());
                setArtifactFactory(codegenConfiguration.getArtifactFactory());
                setArtifactResolver(codegenConfiguration.getArtifactResolver());
                setProtocDependenciesPath(codegenConfiguration.getProtocDependenciesPath());
            }catch (Exception e){
                LOGGER.error(e.toString());
                throw new Exception("Exception happens at PojosGenerator->generate()->setGeneratorProperties()");
            }

        }
    }

    private void performMessage2POJOs() throws Exception {
        LOGGER.info(">>>>>>>>>>>>>>>  Started performing MESSAGE -> POJOs   ");
        if (protocCommand != null) {
            try {
                Protoc.runProtoc(protocCommand, new String[]{"--version"});
            } catch (Exception e) {
                protocCommand = null;
            }
        }

        File stdTypeDir = null;
        if ((protocCommand == null && protocArtifact == null) || includeStdTypes) {
            if (protocVersion == null || protocVersion.length() < 1)
                protocVersion = ProtocVersion.PROTOC_VERSION.mVersion;
            LOGGER.info("Protoc version: " + protocVersion);

            try {
                // option (1) - extract embedded protoc
                if (protocCommand == null && protocArtifact == null) {
                    File protocFile = Protoc.extractProtoc(ProtocVersion.getVersion("-v" + protocVersion), includeStdTypes);
                    protocCommand = protocFile.getAbsolutePath();
                    try {
                        // some linuxes don't allow exec in /tmp, try one dummy execution, switch to user home if it fails
                        Protoc.runProtoc(protocCommand, new String[]{"--version"});
                    } catch (Exception e) {
                        tempRoot = new File(System.getProperty("user.home"));
                        protocFile = Protoc.extractProtoc(ProtocVersion.getVersion("-v" + protocVersion), includeStdTypes, tempRoot);
                        protocCommand = protocFile.getAbsolutePath();
                    }
                    stdTypeDir = new File(protocFile.getParentFile().getParentFile(), "include");
                } else if (includeStdTypes) {
                    File tmpDir = Protoc.extractStdTypes(ProtocVersion.getVersion("-v" + protocVersion), null);
                    stdTypeDir = new File(tmpDir, "include");
                }
            } catch (IOException e) {
                throw new Exception(
                        "Error extracting protoc for version " + protocVersion, e);
            }
        }

        // option (2) - resolve protoc maven artifact (download)
        if (protocCommand == null && protocArtifact != null) {
            protocCommand = resolveArtifact(protocArtifact, null).getAbsolutePath();
            try {
                // some linuxes don't allow exec in /tmp, try one dummy execution, switch to user home if it fails
                Protoc.runProtoc(protocCommand, new String[]{"--version"});
            } catch (Exception e) {
                tempRoot = new File(System.getProperty("user.home"));
                protocCommand = resolveArtifact(protocArtifact, tempRoot).getAbsolutePath();
            }
        }
        LOGGER.info("Protoc command: " + protocCommand);

        if (inputDirectories == null || inputDirectories.length == 0) {
            File inputDir = new File(project.getBasedir().getAbsolutePath() + DEFAULT_INPUT_DIR);
            inputDirectories = new File[]{inputDir};
        }
        LOGGER.info("Input directories:");
        for (File input : inputDirectories) LOGGER.info("    " + input);
        LOGGER.info("Output directories:");
        LOGGER.info("    " + outputDirectory);
        if (includeStdTypes) {

            if (includeDirectories != null && includeDirectories.length > 0) {
                List<File> includeDirList = new ArrayList<File>();
                includeDirList.add(stdTypeDir);
                includeDirList.addAll(Arrays.asList(includeDirectories));
                includeDirectories = includeDirList.toArray(new File[0]);
            } else {
                includeDirectories = new File[]{stdTypeDir};
            }
        }

        if (includeDirectories != null && includeDirectories.length > 0) {
            LOGGER.info("Include directories:");
            for (File include : includeDirectories)
                LOGGER.info("    " + include);
        }

        preprocessTarget();
        processTarget();
    }

    private void preprocessTarget() throws Exception {
        if (pluginArtifact != null && pluginArtifact.length() > 0) {
            pluginPath = resolveArtifact(pluginArtifact, tempRoot).getAbsolutePath();
        }

        File f = outputDirectory;
        if (!f.exists()) {
            LOGGER.info(f + " does not exist. Creating...");
            f.mkdirs();
        }
    }

    private void processTarget() throws Exception {
        boolean shaded = false;
        String targetType = type;
        if (targetType.equals("java-shaded") || targetType.equals("java_shaded")) {
            targetType = "java";
            shaded = true;
        }

        FileFilter fileFilter = new FileFilter(extension);
        for (File input : inputDirectories) {
            if (input == null) continue;

            if (input.exists() && input.isDirectory()) {
                Collection<File> protoFiles = FileUtils.listFiles(input, fileFilter, TrueFileFilter.INSTANCE);
                for (File protoFile : protoFiles) {
                    processFile(protoFile, protocVersion, targetType, pluginPath, outputDirectory, null);
                }
            } else {
                if (input.exists()) LOGGER.warn(input + " is not a directory");
                else LOGGER.warn(input + " does not exist");
            }
        }

        if (shaded) {
            try {
                LOGGER.info("    Shading (version " + protocVersion + "): " + outputDirectory);
                Protoc.doShading(outputDirectory, protocVersion.replace(".", ""));
            } catch (IOException e) {
                throw new Exception(
                        "Error occurred during shading", e);
            }
        }

        LOGGER.info("Adding generated classes to classpath");
        project.addCompileSourceRoot(outputDirectory.getAbsolutePath());
    }

    private void processFile(File file, String version, String type, String pluginPath, File outputDir, String outputOptions) throws Exception {
        LOGGER.info("    Processing (" + type + "): " + file.getName());
        Collection<String> cmd = buildCommand(file, version, type, pluginPath, outputDir, outputOptions);
        try {
            int ret = 0;
            if (protocCommand == null) ret = Protoc.runProtoc(cmd.toArray(new String[0]));
            else ret = Protoc.runProtoc(protocCommand, cmd.toArray(new String[0]));
            if (ret != 0) throw new Exception(
                    "protoc-jar failed for " + file + ". Exit code " + ret);
        } catch (InterruptedException e) {
            throw new Exception(
                    "Interrupted", e);
        } catch (IOException e) {
            throw new Exception(
                    "Unable to execute protoc-jar for " + file, e);
        }
    }

    private Collection<String> buildCommand(File file, String version, String type, String pluginPath, File outputDir, String outputOptions) throws Exception {
        Collection<String> cmd = new ArrayList<String>();
        populateIncludes(cmd);
        cmd.add("-I" + file.getParentFile().getAbsolutePath());
        if ("descriptor".equals(type)) {
            File outFile = new File(outputDir, file.getName());
            cmd.add("--descriptor_set_out=" + FilenameUtils.removeExtension(outFile.toString()) + ".desc");
            cmd.add("--include_imports");
            if (outputOptions != null) {
                for (String arg : outputOptions.split("\\s+")) cmd.add(arg);
            }
        } else {
            if (outputOptions != null) {
                cmd.add("--" + type + "_out=" + outputOptions + ":" + outputDir.getAbsolutePath());
            } else {
                cmd.add("--" + type + "_out=" + outputDir.getAbsolutePath());
            }

            if (pluginPath != null) {
                LOGGER.info("    Plugin path: " + pluginPath);
                cmd.add("--plugin=protoc-gen-" + type + "=" + pluginPath);
            }
        }
        cmd.add(file.getAbsolutePath());
        if (version != null) cmd.add("-v" + version);
        return cmd;
    }

    private void populateIncludes(Collection<String> args) throws Exception {
        for (File include : includeDirectories) {
            if (!include.exists()) throw new Exception(
                    "Include path '" + include.getPath() + "' does not exist");
            if (!include.isDirectory()) throw new Exception(
                    "Include path '" + include.getPath() + "' is not a directory");
            args.add("-I" + include.getPath());
        }
    }

    private File resolveArtifact(String artifactSpec, File dir) throws Exception {
        try {
            Properties detectorProps = new Properties();
            new PlatformDetector().detect(detectorProps, null);
            String platform = detectorProps.getProperty("os.detected.classifier");

            LOGGER.info("Resolving artifact: " + artifactSpec + ", platform: " + platform);
            String[] as = artifactSpec.split(":");
            Artifact artifact = artifactFactory.createDependencyArtifact(as[0], as[1], VersionRange.createFromVersionSpec(as[2]), "exe", platform, Artifact.SCOPE_RUNTIME);
            artifactResolver.resolve(artifact, remoteRepositories, localRepository);

            File tempFile = File.createTempFile(as[1], ".exe", dir);
            copyFile(artifact.getFile(), tempFile);
            tempFile.setExecutable(true);
            tempFile.deleteOnExit();
            return tempFile;
        } catch (Exception e) {
            throw new Exception(
                    "Error resolving artifact: " + artifactSpec, e);
        }
    }

    static File copyFile(File srcFile, File destFile) throws IOException {
        FileInputStream is = null;
        FileOutputStream os = null;
        try {
            is = new FileInputStream(srcFile);
            os = new FileOutputStream(destFile);
            int read = 0;
            byte[] buf = new byte[4096];
            while ((read = is.read(buf)) > 0) os.write(buf, 0, read);
        } finally {
            if (is != null) is.close();
            if (os != null) os.close();
        }
        return destFile;
    }

    static class FileFilter implements IOFileFilter {
        String extension;

        public FileFilter(String extension) {
            this.extension = extension;
        }

        public boolean accept(File dir, String name) {
            return name.endsWith(extension);
        }

        public boolean accept(File file) {
            return file.getName().endsWith(extension);
        }
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
