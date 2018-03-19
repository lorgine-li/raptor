package com.ppdai.framework.raptor.codegen.core.utils.shell;

import java.io.*;

/**
 * Created by zhongyi on 2018/1/15.
 */

public class ShellCommand {

    private Runtime runtime;
    private Process process;
    private StreamGobbler responseStreamGobbler;
    private StreamGobbler errorStreamGobbler;

    public void runCommand(String command) {
        this.runCommand(command, null, false, false);
    }

    public void runCommand(String command, String workingDir) {
        this.runCommand(command, workingDir, false, false);
    }

    public void runCommand(String command, String workingDir, boolean runAsync) {
        this.runCommand(command, workingDir, runAsync, false);
    }

    public void runCommand(String command, String workingDir, boolean runAsync, boolean printOutputInConsole) {
        try {

            String OSName = System.getProperty("os.name");
            OSName = OSName.toLowerCase();

            if (OSName.contains("windows")) {
                command = "cmd /c " + command;
            }

            runtime = Runtime.getRuntime();

            if (workingDir != null && !"".equalsIgnoreCase(workingDir)) {

                File workDir = new File(workingDir);
                if (!workDir.exists()) {
                    System.out.println("The workingDir you specific does not exit.");
                    return;
                }

                if (!OSName.contains("windows")) {
                    process = runtime.exec(new String[]{"bash", "-c", command}, null, workDir);
                } else {
                    process = runtime.exec(command, null, workDir);
                }

            } else {
                System.out.println("---------CMD: "+command);
                if (!OSName.contains("windows")) {
                    process = runtime.exec(new String[]{"bash", "-c", command});
                } else {
                    process = runtime.exec(command);
                }
            }

            if (!printOutputInConsole) {
                responseStreamGobbler = new StreamGobbler(process.getInputStream(), "OUTPUT");
            } else {
                responseStreamGobbler = new StreamGobbler(process.getInputStream(), "OUTPUT", true);
            }

            if (!printOutputInConsole) {
                errorStreamGobbler = new StreamGobbler(process.getErrorStream(), "ERROR");
            } else {
                errorStreamGobbler = new StreamGobbler(process.getErrorStream(), "ERROR", true);
            }

            responseStreamGobbler.start();
            errorStreamGobbler.start();

            if (!runAsync) {
                process.waitFor();
            }

        } catch (IOException | InterruptedException e) {
            System.out.println("Error al llamar una instruccion en linea de comandos...");
            e.printStackTrace(System.out);
        }
    }

    //////////////////////////////////////////////
    //            Getters / Setters
    //////////////////////////////////////////////
    public Runtime getRuntime() {
        return runtime;
    }

    public Process getProcess() {
        return process;
    }

    public String getResponseString() {
        return responseStreamGobbler.getInputString();
    }

    public String getErrorString() {
        return errorStreamGobbler.getInputString();
    }

    public int getExitCode() {
        return process.exitValue();
    }

    public boolean errorOcurred() {
        if (hasProcessFinished()) {
            if (process.exitValue() == 0) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public boolean hasProcessFinished() {
        if (process != null) {
            try {
                process.exitValue();
                return true;
            } catch (Exception ex) {
                return false;
            }
        } else {
            return false;
        }
    }

    static class StreamGobbler extends Thread {

        InputStream inputStream;
        String type;
        OutputStream outputStream;

        boolean printStreamOnConsole;
        StringBuilder inputStringBuilder;

        public StreamGobbler(InputStream is, String type) {
            this(is, type, false, null);
        }

        public StreamGobbler(InputStream is, String type, boolean printStreamOnConsole) {
            this(is, type, printStreamOnConsole, null);
        }

        public StreamGobbler(InputStream is, String type, boolean printStreamOnConsole, OutputStream redirect) {
            this.inputStream = is;
            this.type = type;
            this.outputStream = redirect;
            this.printStreamOnConsole = printStreamOnConsole;
        }

        @Override
        public void run() {
            try {

                PrintWriter pw = null;
                if (outputStream != null) {
                    pw = new PrintWriter(outputStream, true);
                }

                InputStreamReader isr = new InputStreamReader(inputStream);
                BufferedReader br = new BufferedReader(isr);

                String line;
                inputStringBuilder = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    if (pw != null) {
                        pw.println(type + ">" + line);
                    }
                    if(printStreamOnConsole){
                        System.out.println(type + ">" + line);
                    }
                    inputStringBuilder.append(type).append("> ").append(line).append("\n");
                }

                if (pw != null) {
                    pw.flush();
                }

            } catch (Exception ioe) {
                ioe.printStackTrace(System.out);
            }
        }

        public String getInputString(){
            return inputStringBuilder.toString();
        }
    }
}



