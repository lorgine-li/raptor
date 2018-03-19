package com.ppdai.framework.raptor.codegen.cli;

import com.ppdai.framework.raptor.codegen.core.CodegenConfiguration;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static com.ppdai.framework.raptor.codegen.core.utils.Utils.getCliJarPath;

/**
 *
 * Create the Options for cli;
 * Generate a appropriate CodegenConfiguration for Codegen.
 * <p>
 * Created by zhongyi on 2018/1/5.
 */
public class CliOptions {
    private static final Logger logger = LoggerFactory.getLogger(CliOptions.class);
    private static HelpFormatter helpFormatter = new HelpFormatter();
    private static final Options OPTIONS = new Options();
    private static BasicParser parser = new BasicParser();
    //todo Options need to be extended.
    public CliOptions() {
        OPTIONS.addOption("i", true, "the input directory where the proto files are");
        OPTIONS.addOption("o", true, "the output directory which is the output path");
        OPTIONS.addOption("c",true,"whether we use config.json or not");

        OPTIONS.addOption("s", false, "whether we will include the well-known google standard types");

        OPTIONS.addOption("groupId",true,"maven project related parameter");
        OPTIONS.addOption("artifactId",true,"maven project related parameter");
        OPTIONS.addOption("artifactVersion",true,"maven project related parameter");
    }
    /**
     * The priority of CodegenConfiguration:
     *   Default << Config.json << Cli Parameters
     *
     * @param args The cli parameters
     * */
    public CodegenConfiguration getCofig(String[] args) throws Exception {
        //default config
        CodegenConfiguration result = getDefaultConfiguration();

        try {
            //config from config.json
            try {
                File configFile = new File(getCliJarPath(this.getClass()) + File.separatorChar + "config.json");
                if(configFile.exists()) {
                    result.resolveConfigFile(CodegenConfiguration.fromFile(configFile));
                }
            }catch (Exception e){
                System.out.println(e.toString());
            }

            CommandLine cli = parser.parse(OPTIONS, args);
            if(cli.hasOption("c")){
                String confPath = cli.getOptionValue("c");
                try{
                    File configFile = new File(confPath);
                    if(configFile.exists()) {
                        result.resolveConfigFile(CodegenConfiguration.fromFile(configFile));
                    }
                }catch (Exception e){
                    System.out.println(e.toString());
                }
            }
            //config from cli parameters
            if (cli.hasOption("i")) {
                result.setInputDirectories(getDirectoriesFromPath(cli.getOptionValue("i")));
            }
            if (cli.hasOption("o")) {
                result.setOutputDirectory(getDirectoryFromPath(cli.getOptionValue("o")));
            }


            if (cli.hasOption("s")) {
                result.setIncludeStdTypes(true);
            }

            if (cli.hasOption("groupId")) {
                result.setGroupId(cli.getOptionValue("groupId"));
            }
            if (cli.hasOption("artifactVersion")) {
                result.setArtifactVersion(cli.getOptionValue("artifactVersion"));
            }
            if (cli.hasOption("artifactId")) {
                result.setArtifactId(cli.getOptionValue("artifactId"));
            }
            result.setOutputDirectory(new File(result.getOutputDirectory(),result.getArtifactId()));
        } catch (Exception e) {
            logger.error(e.toString());
            throw new Exception("Error occurs when getting CodegenConfiguration from cli.There might be sth wrong with your input.",e);
        }
        return result;
    }

    /**
     * The default configuration used when nothing has been specified.
     * todo Need further consideration for each config/option .
     */
    private CodegenConfiguration getDefaultConfiguration() {
        CodegenConfiguration defaultCon = new CodegenConfiguration();

        defaultCon.setGroupId("com.ppdai.framework.raptor");
        defaultCon.setArtifactId("demo-project");
        defaultCon.setArtifactVersion("0.0.1");
        defaultCon.setExtension(".proto");
        defaultCon.setType("java");
        defaultCon.setOutputDirectory(getDirectoryFromPath(getCliJarPath(this.getClass())));
        defaultCon.setInputDirectories(getDirectoriesFromPath(getCliJarPath(this.getClass())));
        defaultCon.setProtocDependenciesPath(getDirectoryFromPath(getCliJarPath(this.getClass())));
        defaultCon.setIncludeStdTypes(false);
        defaultCon.setProject(new MavenProject());
        defaultCon.setIncludeDirectories(getDirectoriesFromPath(getCliJarPath(this.getClass())));

        return defaultCon;
    }
    //todo The followings need consideration:
    //todo 1、linux or windows 2、reletive or absolute path
    private File getDirectoryFromPath(String path) {
        File file = new File(path);
        return file;
    }
    private File[] getDirectoriesFromPath(String path) {
        File file = new File(path);
        return new File[]{file};
    }

}
